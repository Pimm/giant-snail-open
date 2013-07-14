package org.ilumbo.giantsnail.test.mathematics;

import java.util.Random;

import junit.framework.TestCase;

import org.ilumbo.giantsnail.mathematics.BitArray;

public final class BitArrayTest extends TestCase {
	public final void testSanity() throws Exception {
		final Random random = new Random(0);
		{
			int bitArray = BitArray.ShortGenerator.generateFalseArray();
			for (int iterationIdentifier = 0; 0x1000 != iterationIdentifier; iterationIdentifier++) {
				final int position = random.nextInt(32);
				final boolean value = 1 == (iterationIdentifier & 1);
				bitArray = BitArray.setBit(bitArray, position, value);
				assertEquals(BitArray.getBit(bitArray, position), value);
			}
		}
		{
			long bitArray = BitArray.LongGenerator.generateFalseArray();
			for (int iterationIdentifier = 0; 0x1000 != iterationIdentifier; iterationIdentifier++) {
				final int position = random.nextInt(64);
				final boolean value = 1 == (iterationIdentifier & 1);
				bitArray = BitArray.setBit(bitArray, position, value);
				assertEquals(BitArray.getBit(bitArray, position), value);
			}
		}
	}
	public final void testAndSanity() throws Exception {
		final Random random = new Random(0);
		{
			int bitArray = BitArray.ShortGenerator.generateFalseArray();
			for (int iterationIdentifier = 0; 0x1000 != iterationIdentifier; iterationIdentifier++) {
				final int position = random.nextInt(30);
				final boolean value = 1 == (iterationIdentifier & 1);
				bitArray = BitArray.setBit(bitArray, position, value);
				bitArray = BitArray.setBit(bitArray, position + 1, value);
				bitArray = BitArray.setBit(bitArray, position + 2, value);
				assertEquals(BitArray.getBitsAnd(bitArray, position, 3), value);
			}
		}
		{
			long bitArray = BitArray.LongGenerator.generateFalseArray();
			for (int iterationIdentifier = 0; 0x1000 != iterationIdentifier; iterationIdentifier++) {
				final int position = random.nextInt(62);
				final boolean value = 1 == (iterationIdentifier & 1);
				bitArray = BitArray.setBit(bitArray, position, value);
				bitArray = BitArray.setBit(bitArray, position + 1, value);
				bitArray = BitArray.setBit(bitArray, position + 2, value);
				assertEquals(BitArray.getBitsAnd(bitArray, position, 3), value);
			}
		}
	}
	public final void testGenerateTrueArray() throws Exception {
		assertEquals(0x7F, BitArray.ShortGenerator.generateTrueArray(7));
		assertEquals(0x7FFF, BitArray.ShortGenerator.generateTrueArray(15));
		assertEquals(0x7FFFFF, BitArray.ShortGenerator.generateTrueArray(23));
		assertEquals(0x7FFFFFFF, BitArray.ShortGenerator.generateTrueArray(31));
		assertEquals(0x7FFFFFFFFFl, BitArray.LongGenerator.generateTrueArray(39));
		assertEquals(0x7FFFFFFFFFFFl, BitArray.LongGenerator.generateTrueArray(47));
		assertEquals(0x7FFFFFFFFFFFFFl, BitArray.LongGenerator.generateTrueArray(55));
		assertEquals(0x7FFFFFFFFFFFFFFFl, BitArray.LongGenerator.generateTrueArray(63));
	}
	public final void testGetRandomTrueBit() throws Exception {
		// No true bits.
		int bitArray = BitArray.ShortGenerator.generateFalseArray();
		assertEquals(BitArray.NONE, BitArray.getRandomTrueBit(bitArray));
		// One true bit.
		bitArray = BitArray.ShortGenerator.generateFalseArray();
		bitArray = BitArray.setBit(bitArray, 2, true);
		assertEquals(2, BitArray.getRandomTrueBit(bitArray));
		// Two true bits.
		bitArray = BitArray.ShortGenerator.generateFalseArray();
		bitArray = BitArray.setBit(bitArray, 2, true);
		bitArray = BitArray.setBit(bitArray, 5, true);
		int randomBit = BitArray.getRandomTrueBit(bitArray);
		assertTrue(2 == randomBit || 5 == randomBit);
		// Three true bits.
		bitArray = BitArray.ShortGenerator.generateFalseArray();
		bitArray = BitArray.setBit(bitArray, 2, true);
		bitArray = BitArray.setBit(bitArray, 5, true);
		bitArray = BitArray.setBit(bitArray, 7, true);
		randomBit = BitArray.getRandomTrueBit(bitArray);
		assertTrue(2 == randomBit || 5 == randomBit || 7 == randomBit);
		// Four true bits.
		bitArray = BitArray.ShortGenerator.generateFalseArray();
		bitArray = BitArray.setBit(bitArray, 2, true);
		bitArray = BitArray.setBit(bitArray, 5, true);
		bitArray = BitArray.setBit(bitArray, 7, true);
		bitArray = BitArray.setBit(bitArray, 19, true);
		randomBit = BitArray.getRandomTrueBit(bitArray);
		assertTrue(2 == randomBit || 5 == randomBit || 7 == randomBit || 19 == randomBit);
	}
	public final void testGetRandomTrueBitsAnd() throws Exception {
		// No true bits.
		int bitArray = BitArray.ShortGenerator.generateFalseArray();
		assertEquals(BitArray.NONE, BitArray.getRandomTrueBitsAnd(bitArray, 2));
		// One true bit.
		bitArray = BitArray.ShortGenerator.generateFalseArray();
		bitArray = BitArray.setBit(bitArray, 2, true);
		assertEquals(BitArray.NONE, BitArray.getRandomTrueBitsAnd(bitArray, 2));
		// One possibility for 2.
		bitArray = BitArray.ShortGenerator.generateFalseArray();
		bitArray = BitArray.setBit(bitArray, 2, true);
		bitArray = BitArray.setBit(bitArray, 3, true);
		assertEquals(2, BitArray.getRandomTrueBitsAnd(bitArray, 2));
		assertEquals(BitArray.NONE, BitArray.getRandomTrueBitsAnd(bitArray, 3));
		assertEquals(BitArray.NONE, BitArray.getRandomTrueBitsAnd(bitArray, 4));
		// Extra garbage bit, still one possibility for 2.
		bitArray = BitArray.ShortGenerator.generateFalseArray();
		bitArray = BitArray.setBit(bitArray, 2, true);
		bitArray = BitArray.setBit(bitArray, 3, true);
		bitArray = BitArray.setBit(bitArray, 7, true);
		assertEquals(2, BitArray.getRandomTrueBitsAnd(bitArray, 2));
		assertEquals(BitArray.NONE, BitArray.getRandomTrueBitsAnd(bitArray, 3));
		assertEquals(BitArray.NONE, BitArray.getRandomTrueBitsAnd(bitArray, 4));
		// Two possibilities for 2.
		bitArray = BitArray.ShortGenerator.generateFalseArray();
		bitArray = BitArray.setBit(bitArray, 2, true);
		bitArray = BitArray.setBit(bitArray, 3, true);
		bitArray = BitArray.setBit(bitArray, 7, true);
		bitArray = BitArray.setBit(bitArray, 8, true);
		int randomBit = BitArray.getRandomTrueBitsAnd(bitArray, 2);
		assertTrue(2 == randomBit || 7 == randomBit);
		assertEquals(BitArray.NONE, BitArray.getRandomTrueBitsAnd(bitArray, 3));
		assertEquals(BitArray.NONE, BitArray.getRandomTrueBitsAnd(bitArray, 4));
		// Added garbage, still two possibilities for 2.
		bitArray = BitArray.ShortGenerator.generateFalseArray();
		bitArray = BitArray.setBit(bitArray, 2, true);
		bitArray = BitArray.setBit(bitArray, 3, true);
		bitArray = BitArray.setBit(bitArray, 5, true);
		bitArray = BitArray.setBit(bitArray, 7, true);
		bitArray = BitArray.setBit(bitArray, 8, true);
		bitArray = BitArray.setBit(bitArray, 10, true);
		randomBit = BitArray.getRandomTrueBitsAnd(bitArray, 2);
		assertTrue(2 == randomBit || 7 == randomBit);
		assertEquals(BitArray.NONE, BitArray.getRandomTrueBitsAnd(bitArray, 3));
		assertEquals(BitArray.NONE, BitArray.getRandomTrueBitsAnd(bitArray, 4));
		// Plenty of possibilities: 4 for 2; 2 for 3 and 1 for 4.
		bitArray = BitArray.ShortGenerator.generateFalseArray();
		bitArray = BitArray.setBit(bitArray, 2, true);
		bitArray = BitArray.setBit(bitArray, 3, true);
		bitArray = BitArray.setBit(bitArray, 5, true);
		bitArray = BitArray.setBit(bitArray, 6, true);
		bitArray = BitArray.setBit(bitArray, 7, true);
		bitArray = BitArray.setBit(bitArray, 8, true);
		bitArray = BitArray.setBit(bitArray, 10, true);
		randomBit = BitArray.getRandomTrueBitsAnd(bitArray, 2);
		assertTrue(2 == randomBit || 5 == randomBit || 6 == randomBit || 7 == randomBit);
		randomBit = BitArray.getRandomTrueBitsAnd(bitArray, 3);
		assertTrue(5 == randomBit || 6 == randomBit);
		assertEquals(5, BitArray.getRandomTrueBitsAnd(bitArray, 4));
		// Plenty of possibilities: 6 for 2; 4 for 3 and 3 for 4.
		bitArray = BitArray.ShortGenerator.generateFalseArray();
		bitArray = BitArray.setBit(bitArray, 2, true);
		bitArray = BitArray.setBit(bitArray, 3, true);
		bitArray = BitArray.setBit(bitArray, 5, true);
		bitArray = BitArray.setBit(bitArray, 6, true);
		bitArray = BitArray.setBit(bitArray, 7, true);
		bitArray = BitArray.setBit(bitArray, 8, true);
		bitArray = BitArray.setBit(bitArray, 9, true);
		bitArray = BitArray.setBit(bitArray, 10, true);
		randomBit = BitArray.getRandomTrueBitsAnd(bitArray, 2);
		assertTrue(2 == randomBit || 5 == randomBit || 6 == randomBit || 7 == randomBit || 8 == randomBit || 9 == randomBit);
		randomBit = BitArray.getRandomTrueBitsAnd(bitArray, 3);
		assertTrue(5 == randomBit || 6 == randomBit || 7 == randomBit || 8 == randomBit);
		randomBit = BitArray.getRandomTrueBitsAnd(bitArray, 4);
		assertTrue(5 == randomBit || 6 == randomBit || 7 == randomBit);
	}
	public final void testGetRandomTrueBitProbability() throws Exception {
		// This test always uses the same seed, and therefore will consistently pass or fail. If it fails, the implementation
		// might still be correct: the chosen seed can simply be one that fails. Pun incoming: it could be a bad seed. Try a
		// different seed before wasting time checking other code.
		final Random random = new Random(0);
		{
			int[] resultCounts = new int[32];
			int bitArray = BitArray.ShortGenerator.generateFalseArray();
			bitArray = BitArray.setBit(bitArray, 2, true);
			bitArray = BitArray.setBit(bitArray, 5, true);
			bitArray = BitArray.setBit(bitArray, 7, true);
			bitArray = BitArray.setBit(bitArray, 19, true);
			for (int iterationIdentifier = 0; 0x10000 != iterationIdentifier; iterationIdentifier++) {
				resultCounts[BitArray.getRandomTrueBit(bitArray, random)]++;
			}
			for (int result = 0; 32 != result; result++) {
				// Every correct result must have rougly as many occurrences.
				if (BitArray.getBit(bitArray, result)) {
					assertTrue(Math.abs(resultCounts[result] - 0x4000) < 0x100);
				// Every incorrect result must have no occurences.
				} else {
					assertEquals(resultCounts[result], 0);
				}
			}
		}
		{
			int[] resultCounts = new int[64];
			long bitArray = BitArray.LongGenerator.generateFalseArray();
			bitArray = BitArray.setBit(bitArray, 4, true);
			bitArray = BitArray.setBit(bitArray, 10, true);
			bitArray = BitArray.setBit(bitArray, 14, true);
			bitArray = BitArray.setBit(bitArray, 38, true);
			for (int iterationIdentifier = 0; 0x10000 != iterationIdentifier; iterationIdentifier++) {
				resultCounts[BitArray.getRandomTrueBit(bitArray, random)]++;
			}
			for (int result = 0; 64 != result; result++) {
				// Every correct result must have rougly as many occurrences.
				if (BitArray.getBit(bitArray, result)) {
					assertTrue(Math.abs(resultCounts[result] - 0x4000) < 0x100);
				// Every incorrect result must have no occurences.
				} else {
					assertEquals(resultCounts[result], 0);
				}
			}
		}
	}
}