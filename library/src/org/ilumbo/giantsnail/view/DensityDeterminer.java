package org.ilumbo.giantsnail.view;

import android.util.DisplayMetrics;

/**
 * Graphical elements often have to appear physically the same size on different screens. A button that is two centimetres wide
 * on one device should probably not appear one centimetre wide on another, just because the latter device happens to have a
 * screen with a higher density. It is common practice in Adroid to multiply a density-independent size by the density of the
 * display, and using the result as the size in pixels.
 *
 * Android provides {@link DisplayMetrics#density}. This class is similar in use to that property, but gives more control over
 * how the density is determined.
 */
public final class DensityDeterminer {
	/**
	 * 1 divided by the (natural) log of 2.
	 */
	private static final float INVERTED_LOG_2 = 1.4427f;
	private static final int PRECISION_SHIFT = 0;
	/**
	 * Mask the configuration by this value to obtain the precision part of it.
	 */
	private static final int PRECISION_MASK = 0xF << PRECISION_SHIFT;
	/**
	 * Determine the density with extra precision. Getting an accurate density has priority over getting a round one.
	 */
	public static final int PRECISION_EXTRA_PRECISE = 7 << PRECISION_SHIFT;
	/**
	 * Determine the density similar to the way {@link DisplayMetrics#density} is determined. Getting a round density has
	 * priority over getting an accurate one.
	 */
	public static final int PRECISION_NORMAL = 1 << PRECISION_SHIFT;
	/**
	 * Determine the density with more precision than {@link DensityDeterminer#PRECISION_NORMAL}.
	 */
	public static final int PRECISION_PRECISE = 3 << PRECISION_SHIFT;
	private static final int SOURCE_SHIFT = 4;
	/**
	 * Mask the configuration by this value to obtain the source part of it.
	 */
	private static final int SOURCE_MASK = 0xF << SOURCE_SHIFT;
	/**
	 * Base the result on the density along the x-axis.
	 */
	public static final int SOURCE_X = 3 << SOURCE_SHIFT;
	/**
	 * Base the result on the average of the density along the x-axis and the density along the y-axis.
	 */
	public static final int SOURCE_X_Y_AVERAGE = 0 << SOURCE_SHIFT;
	/**
	 * Base the result on the density along the y-axis.
	 */
	public static final int SOURCE_Y = 4 << SOURCE_SHIFT;
	/**
	 * Calculates and returns a somewhat rounded density of the display. This is a scaling factor for the density independent
	 * pixel unit (dp).
	 *
	 * This method returns 1 for a display that has 160 pixels per inch.
	 */
	public static final float determineDensity(DisplayMetrics displayMetrics, int configuration) {
		// Grab the unrounded density.
		final float unroundedDensity;
		switch (configuration & SOURCE_MASK) {
		case SOURCE_X:
			unroundedDensity = displayMetrics.xdpi;
			break;
		case SOURCE_X_Y_AVERAGE:
			unroundedDensity = (displayMetrics.xdpi + displayMetrics.ydpi) / 2;
			break;
		case SOURCE_Y:
			unroundedDensity = displayMetrics.ydpi;
			break;
		default:
			throw new IllegalArgumentException();
		}
		// If the density is smaller than or equal to 80, return the expected result for 80. This allows all of the code that
		// follows to assume it is greater than 80.
		if (unroundedDensity <= 80) {
			return .5f;
		}
		// Get the number of steps it takes to get from 80 to 160 which is also the number of steps it takes to get from 160 to
		// 320, and so on.
		final int stepCount;
		if (0 == (configuration & PRECISION_MASK)) {
			stepCount = 2;
		} else {
			stepCount = 1 + (configuration & PRECISION_MASK);
		}
		// Round the density. First, create an array large enough to hold the density that is the smallest number in the
		// sequence described by 80 * 2 ^ n that is greater than or equal to the unrounded one.
		final int[] candidates = new int[1 + ((int) Math.ceil(INVERTED_LOG_2 * Math.log(unroundedDensity / 80))) * stepCount];
		// Calculate all of the density candidates.
		candidates[0] = 80;
		for (int stepMultiplier = 1; stepCount != stepMultiplier; stepMultiplier++) {
			candidates[stepMultiplier] = 80 + stepMultiplier * 80 / stepCount;
		}
		for (int index = stepCount; candidates.length != index; index++) {
			candidates[index] = candidates[index - stepCount] << 1;
		}
		// Find the density candidate closest to the unrounded density. Linearly searching from the back is probably faster
		// than binary searching, as the closest candidate is very likely to be one of the last.
		int index = candidates.length - 1;
		do {
			--index;
		} while (candidates[index] > unroundedDensity);
		final float roundedResult;
		if (candidates[index + 1] - unroundedDensity < unroundedDensity - candidates[index]) {
			roundedResult = candidates[index + 1];
		} else {
			roundedResult = candidates[index];
		}
		// Return the appropriate result.
		return roundedResult / 160;
	}
}