package org.ilumbo.giantsnail.view;

import java.util.Arrays;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.MotionEvent;

/**
 * Counts the number of pointers that are down on a certain window or view (depending on how it is used). 
 */
public abstract class PointerCounter {
	/**
	 * Two pointers are down.
	 */
	public static final int COUNT_DUAL = 2;
	/**
	 * Jazz hand: three pointers are down.
	 */
	public static final int COUNT_JAZZ_HAND = 3;
	/**
	 * Large jazz hand: four pointers are down.
	 */
	public static final int COUNT_JAZZ_HAND_LARGE = 4;
	/**
	 * Royal jazz hand: five pointers are down.
	 */
	public static final int COUNT_JAZZ_HAND_ROYAL = 5;
	/**
	 * Mask the value returned by {@link PointerCounter#handleTouchEvent(MotionEvent)} by this constant to obtain the number of
	 * pointers thate are down.
	 */
	public static final int COUNT_MASK = 0xFFFFFF;
	/**
	 * No pointers are down.
	 */
	public static final int COUNT_NONE = 0;
	/**
	 * A single pointer is down.
	 */
	public static final int COUNT_SINGLE = 1;
	/**
	 * The pointer count has decreased, because a pointer went up.
	 */
	public static final int DIRECTION_DECREASING = 2 << 24;
	/**
	 * The pointer count has increased, because a pointer went down.
	 */
	public static final int DIRECTION_INCREASING = 1 << 24;
	/**
	 * Mask the value returned by {@link PointerCounter#handleTouchEvent(MotionEvent)} by this constant to obtain the direction
	 * of the pointer count.
	 */
	public static final int DIRECTION_MASK = 3 << 24;
	/**
	 * The pointer count has not changed.
	 */
	public static final int DIRECTION_NONE = 0 << 24;
	/**
	 * The number of pointers that are down at this moment.
	 */
	protected int count;
	/**
	 * The identifiers of the pointers that are down at this moment. Every identifier has an index smaller than the count, and
	 * every value in the array with an index that equals the count or greater is garbage. This array is not sorted.
	 */
	protected int[] identifiers;
	protected PointerCounter() {
		identifiers = new int[4];
	}
	/**
	 * Creates a new pointer counter.
	 */
	public static final PointerCounter createPointerCounter() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return new GingerbreadPointerCounter();
		} else /* if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) */ {
			return new FroyoPointerCounter();
		}
	}
	/**
	 * Returns the number of pointers that are down at this moment.
	 */
	protected int getCount() {
		return count;
	}
	/**
	 * Alters the internal state based on the passed event, and returns the number of pointers that are currently down. (The
	 * returned value has been updated according to the passed event.)
	 */
	public int handleTouchEvent(MotionEvent event) {
		final int direction;
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
		{
			direction = DIRECTION_INCREASING;
			// In the unlikely event that the array is not big enough to hold the new identifier, increase the size of the
			// array.
			if (identifiers.length == count) {
				prepareForAdditionalIdentifiers();
			}
			// Save the identifier of the pointer that went down.
			identifiers[count++] = event.getPointerId(event.getActionIndex());
			break;
		}
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:
		{
			direction = DIRECTION_DECREASING;
			// Grab the identifier of the pointer that went up.
			final int identifier = event.getPointerId(event.getActionIndex());
			switch (count) {
			case 1:
				// If only one pointer is down, ensure that is the one going up and set the count to zero.
				if (identifier == identifiers[0]) {
					count = 0;
					break;
				}
			case 0:
				count = handleUnknownPointer();
				break;
			default:
			{
				// Find the index of the identifier of the pointer that went up.
				int identifierIndex = Integer.MIN_VALUE;
				for (int index = 0; count != index; index++) {
					if (identifier == identifiers[index]) {
						identifierIndex = index;
						break;
					}
				}
				if (Integer.MIN_VALUE == identifierIndex) {
					count = handleUnknownPointer();
					break;
				}
				// Remove the identifier of the pointer that went up, by overwriting it by the last identifier in the array.
				// Said identifier could be the last identifier itself, in which case it is overwritten by itself. That is OK:
				// it has the desired effect.
				identifiers[identifierIndex] = identifiers[--count];
				break;
			}
			}
			break;
		}
		default:
			direction = DIRECTION_NONE;
			break;
		}
		return count | direction;
	}
	/**
	 * Called when a pointer goes up that was not known to be down by this counter. Returns the new number of pointers that are
	 * down.
	 */
	protected int handleUnknownPointer() {
		throw new IllegalStateException("A pointer went up that was not down according to this counter");
	}
	protected abstract void prepareForAdditionalIdentifiers();
	public static class FroyoPointerCounter extends PointerCounter {
		@Override
		protected final void prepareForAdditionalIdentifiers() {
			final int[] newIdentifiers = new int[count + 4];
			System.arraycopy(identifiers, 0, newIdentifiers, 0, count);
			identifiers = newIdentifiers;
		}
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static class GingerbreadPointerCounter extends PointerCounter {
		@Override
		protected final void prepareForAdditionalIdentifiers() {
			identifiers = Arrays.copyOf(identifiers, count + 4);
		}
	}
}