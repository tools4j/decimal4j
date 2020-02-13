/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 decimal4j (tools4j), Marco Terzer
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

import static org.decimal4j.truncate.DecimalRounding.CEILING;
import static org.decimal4j.truncate.DecimalRounding.DOWN;
import static org.decimal4j.truncate.DecimalRounding.FLOOR;
import static org.decimal4j.truncate.DecimalRounding.HALF_DOWN;
import static org.decimal4j.truncate.DecimalRounding.HALF_EVEN;
import static org.decimal4j.truncate.DecimalRounding.HALF_UP;
import static org.decimal4j.truncate.DecimalRounding.UNNECESSARY;
import static org.decimal4j.truncate.DecimalRounding.UP;
import static org.decimal4j.truncate.DecimalRounding.VALUES;
import static org.decimal4j.truncate.DecimalRounding.valueOf;
import static org.decimal4j.truncate.DecimalRounding.values;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.math.RoundingMode;

import org.junit.Test;

/**
 * Unit test for {@link DecimalRounding}.
 */
public class DecimalRoundingTest {

	@Test
	public void shouldNotRoundWithZeroPart() {
		for (final DecimalRounding rounding : VALUES) {
			for (final int sgn : new int[] {-1, 0, 1}) {
				for (int i = -10; i < 10; i++) {
					assertEquals("rounding increment should be zero", 0, rounding.calculateRoundingIncrement(sgn, i, TruncatedPart.ZERO));
				}
				assertEquals("rounding increment should be zero", 0, rounding.calculateRoundingIncrement(sgn, Long.MIN_VALUE, TruncatedPart.ZERO));
				assertEquals("rounding increment should be zero", 0, rounding.calculateRoundingIncrement(sgn, Long.MAX_VALUE, TruncatedPart.ZERO));
			}
		}
	}

	@Test
	public void shouldNotRoundWithUndefinedSign() {
		for (final DecimalRounding rounding : VALUES) {
			for (int i = -10; i < 10; i++) {
				assertEquals("rounding increment should be zero", 0, rounding.calculateRoundingIncrement(0, i, TruncatedPart.ZERO));
			}
			assertEquals("rounding increment should be zero", 0, rounding.calculateRoundingIncrement(0, Long.MIN_VALUE, TruncatedPart.ZERO));
			assertEquals("rounding increment should be zero", 0, rounding.calculateRoundingIncrement(0, Long.MAX_VALUE, TruncatedPart.ZERO));
		}
	}

	@Test
	public void shouldRoundUp() {
		//pos
		assertEquals("increment should be 1", 1, UP.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 1", 1, UP.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 1", 1, UP.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be 1", 1, UP.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 1", 1, UP.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 1", 1, UP.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF));
		//neg
		assertEquals("increment should be -1", -1, UP.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be -1", -1, UP.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be -1", -1, UP.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be -1", -1, UP.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be -1", -1, UP.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be -1", -1, UP.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF));
	}

	@Test
	public void shouldRoundDown() {
		//pos
		assertEquals("increment should be 0", 0, DOWN.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, DOWN.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 0", 0, DOWN.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be 0", 0, DOWN.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, DOWN.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 0", 0, DOWN.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF));
		//neg
		assertEquals("increment should be 0", 0, DOWN.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, DOWN.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 0", 0, DOWN.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be 0", 0, DOWN.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, DOWN.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 0", 0, DOWN.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF));
	}

	@Test
	public void shouldRoundCeiling() {
		//pos
		assertEquals("increment should be 1", 1, CEILING.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 1", 1, CEILING.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 1", 1, CEILING.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be 1", 1, CEILING.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 1", 1, CEILING.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 1", 1, CEILING.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF));
		//neg
		assertEquals("increment should be 0", 0, CEILING.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, CEILING.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 0", 0, CEILING.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be 0", 0, CEILING.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, CEILING.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 0", 0, CEILING.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF));
	}

	@Test
	public void shouldRoundFloor() {
		//pos
		assertEquals("increment should be 0", 0, FLOOR.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, FLOOR.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 0", 0, FLOOR.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be 0", 0, FLOOR.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, FLOOR.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 0", 0, FLOOR.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF));
		//neg
		assertEquals("increment should be -1", -1, FLOOR.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be -1", -1, FLOOR.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be -1", -1, FLOOR.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be -1", -1, FLOOR.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be -1", -1, FLOOR.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be -1", -1, FLOOR.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF));
	}

	@Test
	public void shouldRoundHalfUp() {
		//pos
		assertEquals("increment should be 0", 0, HALF_UP.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 1", 1, HALF_UP.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 1", 1, HALF_UP.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be 0", 0, HALF_UP.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 1", 1, HALF_UP.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 1", 1, HALF_UP.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF));
		//neg
		assertEquals("increment should be 0", 0, HALF_UP.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be -1", -1, HALF_UP.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be -1", -1, HALF_UP.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be 0", 0, HALF_UP.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be -1", -1, HALF_UP.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be -1", -1, HALF_UP.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF));
	}

	@Test
	public void shouldRoundHalfDown() {
		//pos
		assertEquals("increment should be 0", 0, HALF_DOWN.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, HALF_DOWN.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 1", 1, HALF_DOWN.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be 0", 0, HALF_DOWN.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, HALF_DOWN.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 1", 1, HALF_DOWN.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF));
		//neg
		assertEquals("increment should be 0", 0, HALF_DOWN.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, HALF_DOWN.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be -1", -1, HALF_DOWN.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be 0", 0, HALF_DOWN.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, HALF_DOWN.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be -1", -1, HALF_DOWN.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF));
	}

	@Test
	public void shouldRoundHalfEven() {
		//pos
		assertEquals("increment should be 0", 0, HALF_EVEN.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, HALF_EVEN.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 1", 1, HALF_EVEN.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be 0", 0, HALF_EVEN.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 1", 1, HALF_EVEN.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be 1", 1, HALF_EVEN.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF));
		//neg
		assertEquals("increment should be 0", 0, HALF_EVEN.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be 0", 0, HALF_EVEN.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be -1", -1, HALF_EVEN.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF));
		assertEquals("increment should be 0", 0, HALF_EVEN.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO));
		assertEquals("increment should be -1", -1, HALF_EVEN.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF));
		assertEquals("increment should be -1", -1, HALF_EVEN.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF));
	}

	@Test(expected = ArithmeticException.class)
	public void shouldRoundUnneccessaryWithHalfButNotZero() {
		UNNECESSARY.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
	}

	@Test(expected = ArithmeticException.class)
	public void shouldRoundUnneccessaryWithEqualToHalf() {
		UNNECESSARY.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF);
	}

	@Test(expected = ArithmeticException.class)
	public void shouldRoundUnneccessaryWithGreaterThanHalf() {
		UNNECESSARY.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF);
	}

	@Test
	public void valuesListShouldBeSortedByOrdinal() {
		assertEquals("VALUES size not as expected", values().length, VALUES.size());
		int ordinal = 0;
		for (final DecimalRounding decimalRounding : VALUES) {
			assertEquals("should have ordinal " + ordinal, ordinal, decimalRounding.ordinal());
			ordinal++;
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void valuesListShouldBeImmutable() {
		VALUES.clear();
	}

	@Test
	public void compareWithRoundingMode() {
		assertEquals("RoundingMode and DecimalRounding should have same number of constants", RoundingMode.values().length, values().length);
		for (final RoundingMode roundingMode : RoundingMode.values()) {
			//when
			final DecimalRounding decimalRounding = valueOf(roundingMode);
			//then
			assertSame("should be same rounding mode", roundingMode, decimalRounding.getRoundingMode());
			assertSame("should have same name", roundingMode.name(), decimalRounding.name());
		}
	}

	@Test
	public void testValueOf() {
		//test to achieve 100% coverage 
		for (final DecimalRounding rounding : values()) {
			assertSame("should be same instance", rounding, valueOf(rounding.name()));
		}
	}
}
