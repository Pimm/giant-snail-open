package org.ilumbo.giantsnail.cache;

import java.util.Arrays;

import org.ilumbo.giantsnail.mathematics.POTMath;

import android.annotation.TargetApi;
import android.os.Build;

/**
 * Knows about the elements that exist in the cache, and can be used to coordinate access to those elements. You should
 * probably make sure only one thread is using instances of this class at the same time.
 */
public class CacheSupervisor {
	/**
	 * You must create the element and write it to the cache. Call {@link #finish()} after writing it.
	 */
	public static final int OBTAIN_OPERATION_CREATE_AND_WRITE = 0;
	/**
	 * You should read the element directly from cache.
	 */
	public static final int OBTAIN_OPERATION_READ = 2;
	/**
	 * The element is being created and written to cache. You can wait and re-call {@link #determineObtainOperation(int)},
	 * which will return {@link #OBTAIN_OPERATION_READ} at some point. You can also simply create the element and bypass the
	 * cache.
	 */
	public static final int OBTAIN_OPERATION_WAIT_OR_CREATE = 1;
	/**
	 * The number of elements that exist in the cache, or are in the process of being added to the cache.
	 */
	protected int elementCount;
	/**
	 * The identifiers of the elements that exist in the cache, or are in the process of being added to the cache. This aray is
	 * sorted.
	 */
	protected int[] identifiers;
	/**
	 * The statusses of the elements that exist in the cache, or are in the process of being added to the cache where true
	 * means that the element exists in the cache and false means that the element is in the process of being added to the
	 * cache. <pre>statusses[index]</pre> is the status of the element with identifier <pre>identifiers[index]</pre>.
	 */
	protected boolean[] statusses;
	public CacheSupervisor(int[] initialCachedElementsIdentifiers) {
		elementCount = initialCachedElementsIdentifiers.length;
		// Determine the initial capacity.
		final int initialCapacity = POTMath.ceil(elementCount + 5);
		// Copy and sort the identifiers of the elements.
		System.arraycopy(initialCachedElementsIdentifiers, 0,
				identifiers = new int[initialCapacity], 0,
				elementCount);
		Arrays.sort(identifiers, 0, elementCount);
		// Add a true-status for every element.
		Arrays.fill(statusses = new boolean[identifiers.length], true);
	}
	/**
	 * Replacement for {@link Arrays#binarySearch(int[], int, int, int)}.
	 */
	protected static int binarySearch(int[] array, int length, int value) {
		int high = length, low = -1, guess;
		while (high - low > 1) {
			guess = (high + low) / 2;
			if (array[guess] < value) {
				low = guess;
			} else /* if (array[guess] >= value) */ {
				high = guess;
			}
	    }
		if (high == length) {
			return ~length;
		} else if (array[high] == value) {
			return high;
		} else /* if (array[high] != value) */ {
			return ~high;
		}
	}
	/**
	 * Determines and returns how an element with the passed identifier must be obtained. Once you call this method, you must
	 * obey whatever is returned. If this is too much of a commitment, call {@link #peekObtainOperation(int)}.
	 */
	public int determineObtainOperation(int identifier) {
		int index = getIndexForIdentifier(identifier);
		// No information could be available at all, in which case the element should be created and written.
		if (index < 0) {
			// Set the status, so calling this method again with the same identifier will return OBTAIN_OPERATION_WAIT. Start
			// by increasing the capacity to hold the new status (and identifier).
			if (elementCount == identifiers.length) {
				increaseCapacity(elementCount << 1);
			}
			// Now move the identifiers and statusses, to make room for the new status (and identifier).
			index = ~index;
			if (index != elementCount) {
				System.arraycopy(identifiers, index, identifiers, index + 1, elementCount - index);
				System.arraycopy(statusses, index, statusses, index + 1, elementCount - index);
			}
			// Put the new status (and identifier).
			identifiers[index] = identifier;
			statusses[index] = false;
			elementCount++;
			// Finally, return the operation.
			return OBTAIN_OPERATION_CREATE_AND_WRITE;
		// The element could be available in cache, in which case it can simply be read.
		} else if (statusses[index]) {
			return OBTAIN_OPERATION_READ;
		// The element could be in the process of being added, in which case it should be waited for.
		} else /* if (false == statusses[index]) */ {
			return OBTAIN_OPERATION_WAIT_OR_CREATE;
		}
	}
	/**
	 * Informs the supervisor that the element with the passed identifier is now available in the cache. This method must be
	 * called after an element is created and written to the cache.
	 */
	public void finish(int identifier) {
		final int index = getIndexForIdentifier(identifier);
		// Check sanity.
		if (index < 0) {
			throw new IllegalStateException("No element with the passed identifier is known to this cache supervisor");
		}
		// Raise the status.
		statusses[index] = true;
	}
	/**
	 * Binary searches for the identifier in the identifiers array, returning the index. You know how binary searching works.
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	protected int getIndexForIdentifier(int identifier) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return Arrays.binarySearch(identifiers, 0, elementCount, identifier);			
		} else /* if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) */ {
			return binarySearch(identifiers, elementCount, identifier);
		}
	}
	/**
	 * Increases the capacity of the {@link #identifiers} and {@link #statusses} arrays.
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	protected void increaseCapacity(int newCapacity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			identifiers = Arrays.copyOf(identifiers, newCapacity);
			statusses = Arrays.copyOf(statusses, newCapacity);
		} else /* if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) */ {
			final int[] newIdentifiers = new int[newCapacity];
			System.arraycopy(identifiers, 0, newIdentifiers, 0, elementCount);
			identifiers = newIdentifiers;
			final boolean[] newStatusses = new boolean[newCapacity];
			System.arraycopy(statusses, 0, newStatusses, 0, elementCount);
			statusses = newStatusses;
		}
	}
	/**
	 * Determines and returns how an element with the passed identifier should be obtained if it was to be obtained. If this
	 * method returns {@link #OBTAIN_OPERATION_READ}, {@link #determineObtainOperation(int)} will also return
	 * {@link #OBTAIN_OPERATION_READ} for that identifier.
	 */
	public int peekObtainOperation(int identifier) {
		final int index = getIndexForIdentifier(identifier);
		// No information could be available at all, in which case the element should be created and written.
		if (index < 0) {
			return OBTAIN_OPERATION_CREATE_AND_WRITE;
		// The element could be available in cache, in which case it can simply be read.
		} else if (statusses[index]) {
			return OBTAIN_OPERATION_READ;
		// The element could be in the process of being added, in which case it should be waited for.
		} else /* if (false == statusses[index]) */ {
			return OBTAIN_OPERATION_WAIT_OR_CREATE;
		}
	}
	/**
	 * Informs the supervisor that the element with the passed identifier is broken, meaning
	 * {@link #determineObtainOperation(int)} or {@link #peekObtainOperation(int)} returned {@link #OBTAIN_OPERATION_READ} but
	 * the element was not available in the cache. This method returns how the element should be obtained. Once you call this
	 * method, you must obey whatever is returned
	 */
	public int refreshAndDetermineObtainOperation(int identifier) {
		final int index = getIndexForIdentifier(identifier);
		// Check sanity.
		if (index < 0) {
			throw new IllegalStateException("No element with the passed identifier is known to this cache supervisor");
		// Check the current status. It is possible that another thread also noticed that the element is broken, and already
		// called this method.
		} else if (false == statusses[index]) {
			return OBTAIN_OPERATION_WAIT_OR_CREATE;
		// If this supervisor actually considered this element to be available in cache, the status is lowered and the element
		// is recreated and rewritten.
		} else /* if (statusses[index]) */ {
			return OBTAIN_OPERATION_CREATE_AND_WRITE;
		}
	}
}