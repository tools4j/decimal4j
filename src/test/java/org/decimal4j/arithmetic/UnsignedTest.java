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
package org.decimal4j.arithmetic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for {@link Unsigned}.
 */
@RunWith(JUnitParamsRunner.class)
public class UnsignedTest {

	private static final long FIRST_LARGE_VALUE = Long.MAX_VALUE + 1;
	private static final long LARGE_VALUE = Long.MAX_VALUE + 10;

	private static final long LAST_LARGE_VALUE = 2 * FIRST_LARGE_VALUE - 1;

	// Division

	@Test
	@Parameters(source = DivisionArgumentProvider.class)
	@TestCaseName("{0} / {1} = {2}")
	public void unsignedDivision(final long dividend, final long divisor,
			final long expectedQuotient) {
		// when
		final long result = Unsigned.divide(dividend, divisor);

		// then
		assertEquals(expectedQuotient, result);
	}

	// Special cases for division

	@Test(expected = ArithmeticException.class)
	public void divideByZero() {
		// given
		final long dividend = 1;
		final long divisor = 0;

		// when
		Unsigned.divide(dividend, divisor);
	}

	// Comparison tests

	@Test
	@Parameters(source = LowerThanArgumentProvider.class)
	@TestCaseName("{0} < {1}")
	public void unsignedLowerThan(final long first, final long second) {
		assertLess(first, second);
	}

	@Test
	@Parameters(source = LowerThanArgumentProvider.class)
	@TestCaseName("{1} > {0}")
	public void unsignedGreaterThan(final long first, final long second) {
		assertGreater(second, first);
	}

	/**
	 * Provides arguments for division tests where dividend and divisor are
	 * unsigned long values.
	 * 
	 * <pre>
	 * Arguments: { dividend (a), divisor (b), expected quotient(c) }
	 * 
	 * Notes:
	 * - the expression 'large value' is used for long values greater than
	 * {@link Long#MAX_VALUE} (i.e. >= 2^63)
	 */
	public static class DivisionArgumentProvider {

		public static Object[] provideInput() {
			return new Object[][] { //
					//
					/**
					 * Cases for large divisor values (i.e. b >= 2^63)
					 */
					// b > 2^63, a < b
					{ LARGE_VALUE - 1, LARGE_VALUE, 0 }, //
					{ 0, LARGE_VALUE, 0 }, //
					{ 1, LARGE_VALUE, 0 }, //
					{ 100, LARGE_VALUE, 0 }, //

					// b > 2^63, a = b
					{ FIRST_LARGE_VALUE, FIRST_LARGE_VALUE, 1 },
					{ LARGE_VALUE, LARGE_VALUE, 1 }, //

					// b = 2^63, a < b
					{ FIRST_LARGE_VALUE - 1, FIRST_LARGE_VALUE, 0 }, //
					{ 0, FIRST_LARGE_VALUE, 0 }, //
					{ 1, FIRST_LARGE_VALUE, 0 }, //
					{ 100, FIRST_LARGE_VALUE, 0 }, //

					// b = 2^63, a = b
					{ FIRST_LARGE_VALUE, FIRST_LARGE_VALUE, 1 }, //

					// b > 2^63, a > b
					{ LAST_LARGE_VALUE, LARGE_VALUE, 1 },
					{ LARGE_VALUE + 1, LARGE_VALUE, 1 },

					/**
					 * Cases for 'normal' values (i.e. a, b < 2^63)
					 */
					{ 0, 1, 0 }, //
					{ 1, 1, 1 }, //
					{ 100, 2, 50 }, //
					{ Long.MAX_VALUE, Long.MAX_VALUE, 1 }, //
					{ Long.MAX_VALUE, 2, Long.MAX_VALUE / 2 }, //
					{ Long.MAX_VALUE, Long.MAX_VALUE - 1, 1 }, //
					{ Long.MAX_VALUE - 1, Long.MAX_VALUE, 0 }, //

					/**
					 * Cases for 'normal' divisor & 'large' dividend (i.e. b <
					 * 2^63, a >= 2^63)
					 */
					// a = 2^63, b < a
					{ FIRST_LARGE_VALUE, 1, FIRST_LARGE_VALUE }, //
					{ FIRST_LARGE_VALUE, 100, 92233720368547758L }, //
					{ FIRST_LARGE_VALUE, FIRST_LARGE_VALUE - 1, 1 }, //

					// a > 2^63, b < a
					{ LARGE_VALUE, 1, -9223372036854775799L }, //
					{ LARGE_VALUE, 100, 92233720368547758L }, //
					{ LARGE_VALUE, Long.MAX_VALUE, 1 } //
			};
		}
	}

	/**
	 * Provides arguments for testing {@link Unsigned#isLess(long, long)}.
	 * 
	 * <pre>
	 * Arguments: { first (a), second (b) }
	 */
	public static class LowerThanArgumentProvider {

		public static Object[] provideInput() {
			return new Object[][] { //
			// a, b < 2^63
					{ 0, 1 }, //
					{ 0, 100 }, //
					{ 1, 100 }, //

					{ Long.MAX_VALUE / 2, Long.MAX_VALUE }, //
					{ 100, Long.MAX_VALUE }, //

					// a < 2^63, b = 2^63
					{ 0, FIRST_LARGE_VALUE }, //
					{ 1, FIRST_LARGE_VALUE }, //
					{ 100, FIRST_LARGE_VALUE }, //
					{ Long.MAX_VALUE / 2, FIRST_LARGE_VALUE }, //
					{ Long.MAX_VALUE, FIRST_LARGE_VALUE }, //

					// a < 2^63, b > 2^63
					{ 0, LARGE_VALUE }, //
					{ 1, LARGE_VALUE }, //
					{ 100, LARGE_VALUE }, //
					{ Long.MAX_VALUE / 2, LARGE_VALUE }, //
					{ Long.MAX_VALUE, LARGE_VALUE }, //

					{ 0, LAST_LARGE_VALUE }, //
					{ Long.MAX_VALUE / 2, LAST_LARGE_VALUE }, //
					{ Long.MAX_VALUE, LAST_LARGE_VALUE } //
			};
		}
	}

	private static void assertLess(final long first, final long second) {
		assertTrue(Unsigned.compare(first, second) < 0);
		assertTrue(Unsigned.isLess(first, second));
		assertTrue(Unsigned.isLessOrEqual(first, second));
		assertFalse(Unsigned.isGreater(first, second));
	}

	private static void assertGreater(final long first, final long second) {
		assertTrue(Unsigned.compare(first, second) > 0);
		assertTrue(Unsigned.isGreater(first, second));
		assertFalse(Unsigned.isLess(first, second));
		assertFalse(Unsigned.isLessOrEqual(first, second));
	}

}
