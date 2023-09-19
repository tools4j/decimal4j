/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.arithmetic;

import static org.junit.Assert.assertEquals;

import org.decimal4j.truncate.TruncatedPart;
import org.junit.Test;

/**
 * Unit test for {@link Rounding}
 */
public class RoundingUtilTest {
	@Test
	public void testRemainingZeroOfOne() {
		assertEquals(TruncatedPart.ZERO, Rounding.truncatedPartFor(0, 1));
	}
	@Test
	public void testRemainingOneOfTwo() {
		assertEquals(TruncatedPart.EQUAL_TO_HALF, Rounding.truncatedPartFor(1, 2));
	}
	@Test
	public void testRemainingOneOfThree() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, Rounding.truncatedPartFor(1, 3));
	}
	@Test
	public void testRemainingTwoOfThree() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, Rounding.truncatedPartFor(2, 3));
	}
	@Test
	public void testRemainingLongMaxHalfOfLongMax() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, Rounding.truncatedPartFor(Long.MAX_VALUE/2, Long.MAX_VALUE));
	}
	@Test
	public void testRemainingLongMaxHalfMinusOneOfLongMax() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, Rounding.truncatedPartFor(Long.MAX_VALUE/2-1, Long.MAX_VALUE));
	}
	@Test
	public void testRemainingLongMaxHalfPlusOneOfLongMax() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, Rounding.truncatedPartFor(Long.MAX_VALUE/2+1, Long.MAX_VALUE));
	}
	@Test
	public void testRemainingLongMaxHalfOfLongMaxMinusOne() {
		assertEquals(TruncatedPart.EQUAL_TO_HALF, Rounding.truncatedPartFor(Long.MAX_VALUE/2, Long.MAX_VALUE - 1));
	}
	@Test
	public void testRemainingLongMaxHalfMinusOneOfLongMaxMinusOne() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, Rounding.truncatedPartFor(Long.MAX_VALUE/2-1, Long.MAX_VALUE - 1));
	}
	@Test
	public void testRemainingLongMaxHalfPlusOneOfLongMaxMinusOne() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, Rounding.truncatedPartFor(Long.MAX_VALUE/2+1, Long.MAX_VALUE - 1));
	}
	@Test
	public void testRemainingLongMinHalfOfLongMin() {
		assertEquals(TruncatedPart.EQUAL_TO_HALF, Rounding.truncatedPartFor(Math.abs(Long.MIN_VALUE/2), Long.MIN_VALUE));
	}
	@Test
	public void testRemainingLongMinHalfMinusOneOfLongMin() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, Rounding.truncatedPartFor(Math.abs(Long.MIN_VALUE/2)-1, Long.MIN_VALUE));
	}
	@Test
	public void testRemainingLongMinHalfPlusOneOfLongMin() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, Rounding.truncatedPartFor(Math.abs(Long.MIN_VALUE/2)+1, Long.MIN_VALUE));
	}

}
