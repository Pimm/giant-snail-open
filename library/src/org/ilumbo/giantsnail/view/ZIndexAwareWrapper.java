package org.ilumbo.giantsnail.view;

import java.util.Arrays;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

/**
 * Alters the list of children of a view, respecting z-indexes. Such z-indexes may be "sparse": you may add a view with a
 * z-index of 0, and then one with a z-index of 8. If you do, and later add a view with a z-index of 1, it will appear above
 * the first added view and below the second one.
 *
 * Once this wrapper is constructed, you should only alter the list of children of the passed view group through this wrapper.
 */
public final class ZIndexAwareWrapper {
	/**
	 * Represents the relation between a view group and a child of said view group.
	 */
	public static final class Kinship {
		/**
		 * The view that is a chid of the target. null if the kinship has already been destroyed.
		 */
		private View child;
		/**
		 * The z-index of the child in the target.
		 */
		private int zIndex;
		/**
		 * The wrapper, used to destroy this kinship. null if the kinship has already been destroyed.
		 */
		private ZIndexAwareWrapper zIndexAwareWrapper;
		private Kinship(View child, int zIndex, ZIndexAwareWrapper zIndexAwareWrapper) {
			this.child = child;
			this.zIndex = zIndex;
			this.zIndexAwareWrapper = zIndexAwareWrapper;
		}
		/**
		 * Removes the internal view from the internal view group. Calling this method more than once will have the same effect
		 * as calling it the first time only.
		 */
		public final void destroy() {
			if (null != child) {
				// Remove the child from the target, through the wrapper.
				zIndexAwareWrapper.remove(child, zIndex);
				// null out the references, to ensure nothing happens when this method is called twice as well as to avoid
				// memory leaks.
				child = null;
				zIndexAwareWrapper = null;
			}
		}
	}
	/**
	 * The number of children added to the target, via this wrapper.
	 */
	private int childCount;
	/**
	 * The number of children the target had when this wrapper was constructed.
	 */
	private final int preexistingChildCount;
	/**
	 * The view group whose list of children will be altered.
	 */
	private final ViewGroup target;
	/**
	 * A list of z-indexes of the children added to the target, via this wrapper.
	 */
	private int[] zIndexes;
	public ZIndexAwareWrapper(ViewGroup target) {
		preexistingChildCount = (this.target = target)
				.getChildCount();
		zIndexes = new int[4];
		childCount = 0;
	}
	/**
	 * Adds a view to the view group passed to the constructor of this wrapper. The passed view will appear in front of every
	 * view added with a smaller z-index, and behind every view with a greater z-index.
	 *
	 * Returns a kinship object which can be used to reverse the effects of this call.
	 *
	 * An exception is thrown if the passed z-index has already been used, as z-indexes must be unique. If such exception is
	 * thrown, the state of the wrapper and that of the view group are not altered. Therefore it is safe to catch it and
	 * proceed normally.
	 */
	public final Kinship add(View child, int zIndex) throws IllegalStateException {
		// Add the view to the target.
		target.addView(child, determineChildIndex(zIndex));
		// Add the z-index to the list of z-indexes.
		addZIndex(zIndex);
		// Return the kinship object.
		return new Kinship(child, zIndex, this);
	}
	/**
	 * Adds a view to the view group passed to the constructor of this wrapper. The passed view will appear in front of every
	 * view added with a smaller z-index, and behind every view with a greater z-index.
	 *
	 * Returns a kinship object which can be used to reverse the effects of this call.
	 *
	 * An exception is thrown if the passed z-index has already been used, as z-indexes must be unique. If such exception is
	 * thrown, the state of the wrapper and that of the view group are not altered. Therefore it is safe to catch it and
	 * proceed normally.
	 */
	public final Kinship add(View child, int zIndex, LayoutParams layoutParameters) throws IllegalStateException {
		// Add the view to the target.
		target.addView(child, determineChildIndex(zIndex), layoutParameters);
		// Add the z-index to the list of z-indexes.
		addZIndex(zIndex);
		// Return the kinship object.
		return new Kinship(child, zIndex, this);
	}
	/**
	 * Adds a view to the view group passed to the constructor of this wrapper. The passed view will appear in front of every
	 * view added with a smaller z-index, and behind every view with a greater z-index.
	 *
	 * No kinship object is returned (or created in the first place), therefore the effect of this call can not be reversed.
	 *
	 * An exception is thrown if the passed z-index has already been used, as z-indexes must be unique. If such exception is
	 * thrown, the state of the wrapper and that of the view group are not altered. Therefore it is safe to catch it and
	 * proceed normally.
	 */
	public final void addPermanently(View child, int zIndex) throws IllegalStateException {
		// Add the view to the target.
		target.addView(child, determineChildIndex(zIndex));
		// Add the z-index to the list of z-indexes.
		addZIndex(zIndex);
	}
	/**
	 * Adds a view to the view group passed to the constructor of this wrapper. The passed view will appear in front of every
	 * view added with a smaller z-index, and behind every view with a greater z-index.
	 *
	 * No kinship object is returned (or created in the first place), therefore the effect of this call can not be reversed.
	 *
	 * An exception is thrown if the passed z-index has already been used, as z-indexes must be unique. If such exception is
	 * thrown, the state of the wrapper and that of the view group are not altered. Therefore it is safe to catch it and
	 * proceed normally.
	 */
	public final void addPermanently(View child, int zIndex, LayoutParams layoutParameters) throws IllegalStateException {
		// Add the view to the target.
		target.addView(child, determineChildIndex(zIndex), layoutParameters);
		// Add the z-index to the list of z-indexes.
		addZIndex(zIndex);
	}
	/**
	 * Adds the passed z-index to the list of z-indexes. Increases the length of the array if required.
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private final void addZIndex(int value) {
		if (zIndexes.length == childCount) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				zIndexes = Arrays.copyOf(zIndexes, childCount + 4);
			} else {
				final int[] newZIndexes = new int[childCount + 4];
				System.arraycopy(zIndexes, 0, newZIndexes, 0, childCount);
				zIndexes = newZIndexes;
			}
		}
		zIndexes[childCount++] = value;
	}
	/**
	 * Returns the child index a child with the passed z-index should have.
	 */
	private final int determineChildIndex(int zIndex) throws IllegalStateException {
		int childIndex = 0;
		for (int index = 0; childCount != index; index++) {
			if (zIndexes[index] < zIndex) {
				childIndex++;
			} else if (zIndexes[index] == zIndex) {
				throw new IllegalStateException(new StringBuilder(80)
						.append("A view has already been added with a z-index of ")
						.append(zIndex)
						.append(", z-indexes must be unique")
						.toString());
			}
		}
		return preexistingChildCount + childIndex;
	}
	/**
	 * Removes a view from the view group passed to the constructor of this wrapper. The view must have been added earlier with
	 * the passed z-index.
	 *
	 * An exception is thrown if the view-z-index combination is unexpected, in which case the wrapper is corrupt and must no
	 * longer be used.
	 */
	/* package */ final void remove(View child, int zIndex) throws IllegalStateException {
		// Remove the z-index from the list of z-indexes.
		final int childIndex = removeZIndex(zIndex);
		// Ensure the child with this child index is indeed the passed view.
		if (target.getChildAt(childIndex) != child) {
			throw new IllegalStateException(new StringBuilder(160)
					.append("The passed view does not have the expected z-index of ")
					.append(zIndex)
					.append(", possibly because the list of children was altered directly instead of through this wrapper")
					.toString());
		}
		// Remove the view from the target.
		target.removeView(child);
	}
	/**
	 * Removes a z-index from the list of z-indexes. Returns the child index of a view with such a z-index.
	 */
	private final int removeZIndex(int value) throws IllegalStateException {
		// Find the position of the z-index in the array, as well as the child index.
		int childIndex = 0, zIndexIndex = Integer.MIN_VALUE;
		for (int index = 0; childCount != index; index++) {
			if (zIndexes[index] < value) {
				childIndex++;
			} else if (zIndexes[index] == value) {
				zIndexIndex = index;
			}
		}
		// Remove the z-index by moving all values that come after it one index to the "left".
		System.arraycopy(zIndexes, zIndexIndex + 1, zIndexes, zIndexIndex, childCount-- - (zIndexIndex + 1));
		// Return the found child index.
		return preexistingChildCount + childIndex;
	}
}