/**
 * Copyright 2013 Pimm Hogeling
 *
 * Giant Snail open is free software. Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Alternatively, the Software may be used under the terms of either the GNU General Public License Version 3 or later (the
 * "GPL"), or the GNU Lesser General Public License Version 3 or later (the "LGPL"), in which case the provisions of the GPL or
 * the LGPL are applicable instead of those above.
 */

package org.ilumbo.giantsnail.mathematics;

import java.util.Random;

/**
 * An array of bits. The actual data is represented by a primitive int or long. If a wrapper for the primitives is desired,
 * {@link java.lang.Integer} and {@link java.lang.Long} can be used.
 *
 * This class can be useful in game development. Say you are creating a dungeon that consists of puzzle rooms and combat rooms.
 * You want the dungeon to have 8 rooms in total, exactly 4 of which are compat rooms. Then you can use
 * {@link ShortGenerator#generateRandomArray(int, int)}, passing 8 and 4, to generate a bit array that determines which rooms
 * are combat and which are puzzle. Use {@link #getBit(int, int)}, passing the generated bit array and the identifier of the
 * room, to determine the type of a room.
 */
public final class BitArray {
	/**
	 * Generates short bit arrays. These bit arrays are backed by a long, and should be no longer than 63 bits. 64 bits long
	 * bit arrays might work as well, but has not been tested.
	 */
	public static final class LongGenerator {
		/**
		 * Generates a bit array with all false bits
		 */
		public static final long generateFalseArray() {
			return 0;
		}
		/**
		 * Generates a random bit array that has the passed number of true bits. All true bits will have a position that is
		 * smaller than the passed length.
		 *
		 * If one passes a length of 3 and a true bit count of 2, the result can be 011, 101 or 110.
		 *
		 * Note: this method will result in an endless loop if the trueBitCount argument is greater than the length argument.
		 */
		public static final long generateRandomArray(final int length, final int trueBitCount) {
			return generateRandomArray(length, trueBitCount, new Random());
		}
		/**
		 * Generates a random bit array that has the passed number of true bits. All true bits will have a position that is
		 * smaller than the passed length.
		 *
		 * If one passes a length of 3 and a true bit count of 2, the result can be 011, 101 or 110.
		 *
		 * Note: this method will result in an endless loop if the trueBitCount argument is greater than the length argument.
		 */
		public static final long generateRandomArray(final int length, final int trueBitCount, final Random random) {
			if (length == trueBitCount) {
				return (1l << length) - 1;
			}
			long result = getNextLong(1l << length, random);
			while (trueBitCount != Long.bitCount(result)) {
				result = getNextLong(1l << length, random);
			}
			return result;
		}
		/**
		 * Generates a bit array that has the passed number of true bits, filling all of the positions smaller than the passed
		 * length.
		 */
		public static final long generateTrueArray(final int length) {
			return (1l << length) - 1;
		}
		/**
		 * Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the specified value (exclusive),
		 * drawn from this random number generator's sequence. All n possible long values are produced with (approximately)
		 * equal probability.
		 */
		private static final long getNextLong(final long n, final Random random) {
			long bits, result;
			do {
				bits = random.nextLong() << 1 >>> 1;
				result = bits % n;
			} while (bits - result + n - 1 < 0);
			return result;
		}
	}
	/**
	 * Generates short bit arrays. These bit arrays are backed by an integer, and should be no longer than 31 bits. 32 bits
	 * long bit arrays might work as well, but has not been tested.
	 */
	public static final class ShortGenerator {
		/**
		 * Generates a bit array with all false bits
		 */
		public static final int generateFalseArray() {
			return 0;
		}
		/**
		 * Generates a random bit array that has the passed number of true bits. All true bits will have a position that is
		 * smaller than the passed length.
		 *
		 * If one passes a length of 3 and a true bit count of 2, the result can be 011, 101 or 110.
		 *
		 * Note: this method will result in an endless loop if the trueBitCount argument is greater than the length argument.
		 */
		public static final int generateRandomArray(final int length, final int trueBitCount) {
			return generateRandomArray(length, trueBitCount, new Random());
		}
		/**
		 * Generates a random bit array that has the passed number of true bits. All true bits will have a position that is
		 * smaller than the passed length.
		 *
		 * If one passes a length of 3 and a true bit count of 2, the result can be 011, 101 or 110.
		 *
		 * Note: this method will result in an endless loop if the trueBitCount argument is greater than the length argument.
		 */
		public static final int generateRandomArray(final int length, final int trueBitCount, final Random random) {
			if (length == trueBitCount) {
				return (1 << length) - 1;
			}
			int result = random.nextInt(1 << length);
			while (trueBitCount != Integer.bitCount(result)) {
				result = random.nextInt(1 << length);
			}
			return result;
		}
		/**
		 * Generates a bit array that has the passed number of true bits, filling all of the positions smaller than the passed
		 * length.
		 */
		public static final int generateTrueArray(final int length) {
			return (1 << length) - 1;
		}
	}
	/**
	 * The position of a bit that does not exist. Returned by getRandomBit if there are no true bits, for example.
	 */
	public static final int NONE = -1;
	private static final char ZERO = '0';
	/**
	 * Returns the bit with the passed position.
	 */
	public static final boolean getBit(final int input, final int position) {
		return 0 != (input & 1 << position);
	}
	/**
	 * Returns the bit with the passed position.
	 */
	public static final boolean getBit(final long input, final int position) {
		return 0 != (input & 1l << position);
	}
	/**
	 * Returns whether the bit with the passed first position is the first bit in a string of the passed number of true bits.
	 */
	public static final boolean getBitsAnd(final int input, final int firstPosition, final int count) {
		final int mask = (1 << count) - 1 << firstPosition;
		return mask == (input & mask);
	}
	/**
	 * Returns whether the bit with the passed first position is the first bit in a string of the passed number of true bits.
	 */
	public static final boolean getBitsAnd(final long input, final int firstPosition, final int count) {
		final long mask = (1l << count) - 1 << firstPosition;
		return mask == (input & mask);
	}
	/**
	 * Returns whether the string of the passed number of bits starting at the passed position contains at least one true bit.
	 */
	public static final boolean getBitsOr(final int input, final int firstPosition, final int count) {
		return 0 != (input & (1 << count) - 1 << firstPosition);
	}
	/**
	 * Returns whether the string of the passed number of bits starting at the passed position contains at least one true bit.
	 */
	public static final boolean getBitsOr(final long input, final int firstPosition, final int count) {
		return 0 != (input & (1l << count) - 1 << firstPosition);
	}
	/**
	 * Returns the lowest from the set of positions of true bits. Returns NONE if there are no true bits.
	 */
	public static final int getFirstTrueBit(final int input) {
		return 0 == input ? NONE : Integer.numberOfTrailingZeros(input);
	}
	/**
	 * Returns the lowest from the set of positions of true bits. Returns NONE if there are no true bits.
	 */
	public static final int getFirstTrueBit(final long input) {
		return 0 == input ? NONE : Long.numberOfTrailingZeros(input);
	}
	/**
	 * Returns the position of a random true bit. Returns NONE if there are no true bits.
	 */
	public static final int getRandomTrueBit(final int input) {
		return getRandomTrueBit(input, new Random());
	}
	/**
	 * Returns the position of a random true bit. Returns NONE if there are no true bits.
	 */
	public static final int getRandomTrueBit(int input, final Random random) {
		final int trueBitCount = Integer.bitCount(input);
		switch (trueBitCount) {
		case 0:
			return NONE;
		// For two bits, coin flip whether the lowest true bit is flipped (therefore ignored). Then return the position of the
		// lowest true bit as if the true bit count were 1.
		case 2:
			if (random.nextBoolean()) {
				input &= input - 1;
			}
		// Return the position of the lowest true bit, which is the only true bit.
		case 1:
			return Integer.numberOfTrailingZeros(input);
		}
		// Choose the number of true bits that should be skipped before the selected true bit is reached.
		int skipCount = random.nextInt(trueBitCount);
		// Flip the number of true bits determined above.
		while (0 != skipCount--) {
			input &= input - 1;
		}
		return Integer.numberOfTrailingZeros(input);
	}
	/**
	 * Returns the position of a random true bit. Returns NONE if there are no true bits.
	 */
	public static final int getRandomTrueBit(final long input) {
		return getRandomTrueBit(input, new Random());
	}
	/**
	 * Returns the position of a random true bit. Returns NONE if there are no true bits.
	 */
	public static final int getRandomTrueBit(long input, final Random random) {
		final int trueBitCount = Long.bitCount(input);
		switch (trueBitCount) {
		case 0:
			return NONE;
		// For two bits, coin flip whether the lowest true bit is flipped (therefore ignored). Then return the position of the
		// lowest true bit as if the true bit count were 1.
		case 2:
			if (random.nextBoolean()) {
				input &= input - 1;
			}
		// Return the position of the lowest true bit, which is the only true bit.
		case 1:
			return Long.numberOfTrailingZeros(input);
		}
		// Choose the number of true bits that should be skipped before the selected true bit is reached.
		int skipCount = random.nextInt(trueBitCount);
		// Flip the number of true bits determined above.
		while (0 != skipCount--) {
			input &= input - 1;
		}
		return Long.numberOfTrailingZeros(input);
	}
	/**
	 * Finds a string of the passed number of true bits, and returns the position of the first (true) bit. If multiple
	 * positions would be correct, returns one of those positions at random where every position has (approximately) equal
	 * probability of being returned. Returns NONE if no such string exists.
	 */
	public static final int getRandomTrueBitsAnd(final int input, final int count) {
		return getRandomTrueBitsAnd(input, count, new Random());
	}
	/**
	 * Finds a string of the passed number of true bits, and returns the position of the first (true) bit. If multiple
	 * positions would be correct, returns one of those positions at random where every position has (approximately) equal
	 * probability of being returned. Returns NONE if no such string exists.
	 */
	public static final int getRandomTrueBitsAnd(final int input, final int count, final Random random) {
		// Find the candidates, positions which point to a true bit followed by the passed number of other true bits.
		int canditates = 0;
		// (Integer.SIZE - count) - Integer.numberOfLeadingZeros(input) skips the (count - 1) true bits with the highest
		// positions. That's fine: those bits aren't followed by the required other true bits, and therefore don't quality.
		int position = Integer.SIZE - count - Integer.numberOfLeadingZeros(input);
		// Check whether there are no true bits at all.
		if (-count == position) {
			return NONE;
		}
		final int mask = (1 << count) - 1;
		do {
			if (mask == (input & mask << position) >> position) {
				canditates |= 1 << position;
			}
		} while (0 != position--);
		// Use the getRandomTrueBit implementation to select a random candidate.
		return getRandomTrueBit(canditates, random);
	}
	/**
	 * Finds a string of the passed number of true bits, and returns the position of the first (true) bit. If multiple
	 * positions would be correct, returns one of those positions at random where every position has (approximately) equal
	 * probability of being returned. Returns NONE if no such string exists.
	 */
	public static final int getRandomTrueBitsAnd(final long input, final int count) {
		return getRandomTrueBitsAnd(input, count, new Random());
	}
	/**
	 * Finds a string of the passed number of true bits, and returns the position of the first (true) bit. If multiple
	 * positions would be correct, returns one of those positions at random where every position has (approximately) equal
	 * probability of being returned. Returns NONE if no such string exists.
	 */
	public static final int getRandomTrueBitsAnd(final long input, final int count, final Random random) {
		// Find the candidates, positions which point to a true bit followed by the passed number of other true bits.
		long canditates = 0;
		// (Long.SIZE - count) - Long.numberOfLeadingZeros(input) skips the (count - 1) true bits with the highest positions.
		// That's fine: those bits aren't followed by the required other true bits, and therefore don't quality.
		int position = Long.SIZE - count - Long.numberOfLeadingZeros(input);
		// Check whether there are no true bits at all.
		if (-count == position) {
			return NONE;
		}
		final long mask = (1l << count) - 1;
		do {
			if (mask == (input & mask << position) >> position) {
				canditates |= 1l << position;
			}
		} while (0 != position--);
		// Use the getRandomTrueBit implementation to select a random candidate.
		return getRandomTrueBit(canditates, random);
	}
	/**
	 * Returns a bit array with the same bits but in the reversed order.
	 */
	public static final int reverse(final int input, final int length) {
		return Integer.reverse(input) >> (Integer.SIZE - length);
	}
	/**
	 * Returns a bit array with the same bits but in the reversed order.
	 */
	public static final long reverse(final long input, final int length) {
		return Long.reverse(input) >> (Long.SIZE - length);
	}
	/**
	 * Sets the value of the bit at the passed position.
	 */
	public static final int setBit(final int input, final int position, final boolean value) {
		return value ? input | 1 << position: input & ~(1 << position);
	}
	/**
	 * Sets the value of the bit at the passed position.
	 */
	public static final long setBit(final long input, final int position, final boolean value) {
		return value ? input | 1l << position: input & ~(1l << position);
	}
	public final static String toString(final int input, final int size) {
		final String partialResult = Integer.toBinaryString(input);
		int resultSize = partialResult.length();
		// Start with a reversed binary representation of the input. Reversed, so that the bit with position 0 appears in the
		// resulting string form first.  This is consistent with the string forms of regular Java arrays, as well as arrays in
		// every language I know of.
		final StringBuilder resultBuilder = new StringBuilder(partialResult)
				.reverse();
		// Add a zero for every bit that was not included in the binary representation created above.
		while (resultSize < size) {
			resultBuilder.append(ZERO);
			resultSize++;
		}
		return resultBuilder.toString();
	}
	public final static String toString(final long input, final int size) {
		final String partialResult = Long.toBinaryString(input);
		int resultSize = partialResult.length();
		// Start with a reversed binary representation of the input. Reversed, so that the bit with position 0 appears in the
		// resulting string form first.  This is consistent with the string forms of regular Java arrays, as well as arrays in
		// every language I know of.
		final StringBuilder resultBuilder = new StringBuilder(partialResult)
				.reverse();
		// Add a zero for every bit that was not included in the binary representation created above.
		while (resultSize < size) {
			resultBuilder.append(ZERO);
			resultSize++;
		}
		return resultBuilder.toString();
	}
}