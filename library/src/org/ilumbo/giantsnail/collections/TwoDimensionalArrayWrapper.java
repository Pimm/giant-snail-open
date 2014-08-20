package org.ilumbo.giantsnail.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Wraps around a two-dimensional array, and is iterable.
 */
public final class TwoDimensionalArrayWrapper<Type> implements Iterable<Type> {
	/**
	 * Iterates over a two-dimensional array.
	 */
	public static final class TwoDimensionalArrayIterator<Type> implements Iterator<Type> {
		/**
		 * The current inner array. See the outerIndex property.
		 */
		private Type[] innerArray;
		/**
		 * The current index in the inner array. Treat this property as read-only!
		 */
		public int innerIndex;
		/**
		 * The outer array.
		 */
		private final Type[][] outerArray;
		/**
		 * The current index in the outer array. See the innerArray property. Treat this property as read-only!
		 */
		public int outerIndex;
		public TwoDimensionalArrayIterator(Type[][] outerArray) {
			innerArray = (this.outerArray = outerArray)[outerIndex = 0];
			innerIndex = -1;
		}
		@Override
		public final boolean hasNext() {
			return outerIndex != outerArray.length - 1 ||
					(/* outerIndex == outerArray.length - 1 && */ innerIndex != innerArray.length - 1);
		}
		@Override
		public final Type next() {
			// Advance to the next element in the inner array. If the inner array had already been depleted, advance to the
			// next inner array.
			if (++innerIndex == innerArray.length) {
				// If the outer array had already been depleted as well, throw an exception.
				if (++outerIndex == outerArray.length) {
					// (Reset the state of the iterator to what it was before this method was called. This causes this method
					// to throw the same exception when called again.)
					outerIndex = outerArray.length - 1;
					innerIndex = innerArray.length - 1;
					throw new NoSuchElementException();
				}
				innerIndex = 0;
				innerArray = outerArray[outerIndex];
			}
			// Return the current element.
			return innerArray[innerIndex];
		}
		@Override
		public final void remove() {
			throw new UnsupportedOperationException();
		}
		/**
		 * Replaces the element most recently returned by {@link TwoDimensionalArrayIterator#next}.
		 */
		public final void replace(Type newValue) {
			try {
				innerArray[innerIndex] = newValue;
			} catch (ArrayIndexOutOfBoundsException exception) {
				// If next has never been called the above will throw an exception. Throw an exception for an illegal state in
				// that case, which is more descriptive than the one for the array index being out of bounds.
				throw new IllegalStateException();
			}
		}
		/**
		 * Resets the iterator, causing {@link TwoDimensionalArrayIterator#next} to return the first element again.
		 */
		public final void reset() {
			innerArray = outerArray[outerIndex = 0];
			innerIndex = -1;
		}
	}
	/**
	 * The two-dimensional array that is wrapped arround.
	 */
	private final Type[][] wrappee;
	public TwoDimensionalArrayWrapper(Type[][] wrappee) {
		this.wrappee = wrappee;
	}
	@Override
	public final TwoDimensionalArrayIterator<Type> iterator() {
		return new TwoDimensionalArrayIterator<Type>(wrappee);
	}
}