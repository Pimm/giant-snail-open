package org.ilumbo.giantsnail.collections;

/**
 * Builds an array of integers (not {@link Integer}s; use an {@link java.util.ArrayList} for that instead).
 */
public class IntegerArrayBuilder {
	/**
	 * The index of the next integer that will be added. Also the number of integers that have already been added.
	 */
	protected int pointer;
	/**
	 * The array that is being built.
	 */
	protected int[] result;
	public IntegerArrayBuilder(int length) {
		result = new int[length];
	}
	/**
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public void add(int value) {
		result[pointer++] = value;
	}
	/**
	 * Returns an array that contains the values passed to the add method. The returned array might not be a copy; it might
	 * actually be part of the internal state of the builder. After calling this method, all methods (including this one) will
	 * have undefined behaviour.
	 *
	 * @throws IllegalStateException
	 */
	public int[] build() {
		// Ensure the resulting array has been filled entirely. 
		if (pointer != result.length) {
			throw new IllegalStateException();
		}
		return result;
	}
}