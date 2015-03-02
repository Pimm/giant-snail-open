package org.ilumbo.giantsnail.collections;

import java.util.Arrays;

import android.annotation.TargetApi;
import android.os.Build;

/**
 * Builds an array of integers (not {@link Integer}s; use an {@link java.util.ArrayList} for that instead).
 */
public abstract class DynamicIntegerArrayBuilder extends IntegerArrayBuilder {
	/**
	 * The capacity that was passed to the constructor.
	 */
	protected final int initialCapacity;
	protected DynamicIntegerArrayBuilder(int capacity) {
		super(capacity);
		initialCapacity = capacity;
	}
	@Override
	public void add(int value) {
		// If the resulting array is not big enough to hold the additional value, increase the size of the array.
		if (pointer == result.length) {
			prepareForAdditionalValues();
		}
		super.add(value);
	}
	@Override
	public int[] build() {
		// If the resulting array has been filled entirely, return it directly.
		if (pointer == result.length) {
			return result;
		// If the resulting array has not been filled entirely, return an array with the same content and the correct length.
		} else {
			return buildTruncated();
		}
	}
	protected abstract int[] buildTruncated();
	public static final DynamicIntegerArrayBuilder createDynamicIntegerArrayBuilder(int capacity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return new GingerbreadDynamicIntegerArrayBuilder(capacity);
		} else /* if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) */ {
			return new FroyoDynamicIntegerArrayBuilder(capacity);
		}
	}
	protected abstract void prepareForAdditionalValues();
	public static class FroyoDynamicIntegerArrayBuilder extends DynamicIntegerArrayBuilder {
		public FroyoDynamicIntegerArrayBuilder(int capacity) {
			super(capacity);
		}
		@Override
		protected int[] buildTruncated() {
			final int[] truncatedResult = new int[pointer];
			System.arraycopy(result, 0, truncatedResult, 0, pointer);
			return truncatedResult;
		}
		@Override
		protected void prepareForAdditionalValues() {
			final int[] newResult = new int[pointer + initialCapacity];
			System.arraycopy(result, 0, newResult, 0, pointer);
			result = newResult;
		}
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static class GingerbreadDynamicIntegerArrayBuilder extends DynamicIntegerArrayBuilder {
		public GingerbreadDynamicIntegerArrayBuilder(int capacity) {
			super(capacity);
		}
		@Override
		protected int[] buildTruncated() {
			return Arrays.copyOf(result, pointer);
		}
		@Override
		protected void prepareForAdditionalValues() {
			result = Arrays.copyOf(result, pointer + initialCapacity);
		}
	}
}