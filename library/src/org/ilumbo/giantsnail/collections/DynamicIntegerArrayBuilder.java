package org.ilumbo.giantsnail.collections;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.os.Build;

/**
 * Builds an array of integers (not {@link Integer}s; use an {@link java.util.ArrayList} for that instead).
 */
public class DynamicIntegerArrayBuilder extends IntegerArrayBuilder {
	/**
	 * The capacity that was passed to the constructor.
	 */
	private final int initialCapacity;
	public DynamicIntegerArrayBuilder(int capacity) {
		super(capacity);
		initialCapacity = capacity;
	}
	@Override
	public void add(int value) {
		// If the resulting array is not big enough to hold the additional integer, increase the size of the array.
		if (pointer == result.length) {
			prepareForAdditionalIntegers();
		}
		super.add(value);
	}
	@SuppressLint("NewApi")
	@Override
	public int[] build() {
		// If the resulting array has been filled entirely, return it directly.
		if (pointer == result.length) {
			return result;
		// If the resulting array has not been filled entirely, return an array with the same content and the correct length.
		} else {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				return Arrays.copyOf(result, pointer);
			} else /* if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) */ {
				final int[] truncatedResult = new int[pointer];
				System.arraycopy(result, 0, truncatedResult, 0, pointer);
				return truncatedResult;
			}
		}
	}
	@SuppressLint("NewApi")
	protected void prepareForAdditionalIntegers() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			result = Arrays.copyOf(result, pointer + initialCapacity);
		} else /* if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) */ {
			final int[] newResult = new int[pointer + initialCapacity];
			System.arraycopy(result, 0, newResult, 0, pointer);
			result = newResult;
		}
	}
}