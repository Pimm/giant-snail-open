package org.ilumbo.giantsnail.test.mathematics;

import junit.framework.TestCase;

import org.ilumbo.giantsnail.mathematics.POTMath;

public final class POTMathTest extends TestCase {
	public final void testCeil() {
		assertEquals(1, POTMath.ceil(1));
		assertEquals(2, POTMath.ceil(2));
		assertEquals(4, POTMath.ceil(3));
		assertEquals(4, POTMath.ceil(4));
		assertEquals(8, POTMath.ceil(5));
		assertEquals(128, POTMath.ceil(127));
		assertEquals(128, POTMath.ceil(128));
		assertEquals(256, POTMath.ceil(129));
	}
	public final void testFloor() {
		assertEquals(1, POTMath.floor(1));
		assertEquals(2, POTMath.floor(2));
		assertEquals(2, POTMath.floor(3));
		assertEquals(4, POTMath.floor(4));
		assertEquals(4, POTMath.floor(5));
		assertEquals(64, POTMath.floor(127));
		assertEquals(128, POTMath.floor(128));
		assertEquals(128, POTMath.floor(129));
	}
}
