/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.truncate;

import static org.junit.Assert.assertEquals;

import org.decimal4j.truncate.TruncatedPart;
import org.junit.Test;

/**
 * Unit test for {@link TruncatedPart}.
 */
public class TruncatedPartTest {
	
	@Test
	public void testFirstZeroRestZero() {
		assertEquals(TruncatedPart.ZERO, TruncatedPart.valueOf(0, true));
	}
	@Test
	public void testFirstOneRestZero() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(1, true));
	}
	@Test
	public void testFirstFiveRestZero() {
		assertEquals(TruncatedPart.EQUAL_TO_HALF, TruncatedPart.valueOf(5, true));
	}
	@Test
	public void testFirstSixRestZero() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(6, true));
	}
	@Test
	public void testFirstNineRestZero() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(9, false));
	}
	@Test
	public void testFirstZeroRestNonZero() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(0, false));
	}
	@Test
	public void testFirstOneRestNonZero() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(1, false));
	}
	@Test
	public void testFirstFiveRestNonZero() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(5, false));
	}
	@Test
	public void testFirstSixRestNonZero() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(6, false));
	}
	@Test
	public void testFirstNineRestNonZero() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(9, false));
	}
}