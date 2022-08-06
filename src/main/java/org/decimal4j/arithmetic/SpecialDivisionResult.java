/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 decimal4j (tools4j), Marco Terzer
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

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.OverflowMode;

/**
 * Helper class used by division and inversion methods to handle special cases.
 */
enum SpecialDivisionResult {
	/**
	 * {@code a/b} with {@code a==0, b!=0} leading to {@code 0/b=0}
	 */
	DIVIDEND_IS_ZERO {
		@Override
		final long divide(DecimalArithmetic arithmetic, long uDecimalDividend, long uDecimalDivisor) {
			return 0;
		}
	},
	/**
	 * {@code a/b} with {@code b==0} leading to an arithmetic exception
	 */
	DIVISOR_IS_ZERO {
		@Override
		final long divide(DecimalArithmetic arithmetic, long uDecimalDividend, long uDecimalDivisor) {
			throw new ArithmeticException("Division by zero: " + arithmetic.toString(uDecimalDividend) + " / "
					+ arithmetic.toString(uDecimalDivisor));
		}
	},
	/**
	 * {@code a/b} with {@code b==1} leading to {@code a/1=a}
	 */
	DIVISOR_IS_ONE {
		@Override
		final long divide(DecimalArithmetic arithmetic, long uDecimalDividend, long uDecimalDivisor) {
			return uDecimalDividend;
		}
	},
	/**
	 * {@code a/b} with {@code b==-1} resulting in {@code a/-1=-a}
	 */
	DIVISOR_IS_MINUS_ONE {
		@Override
		final long divide(DecimalArithmetic arithmetic, long uDecimalDividend, long uDecimalDivisor) {
			return arithmetic.negate(uDecimalDividend);// we must go through
														// arithmetic because
														// overflow is possible
		}
	},
	/**
	 * {@code a/b} with {@code a==b} resulting in {@code a/a=b/b=1}
	 */
	DIVISOR_EQUALS_DIVIDEND {
		@Override
		final long divide(DecimalArithmetic arithmetic, long uDecimalDividend, long uDecimalDivisor) {
			return arithmetic.one();
		}
	},
	/**
	 * {@code a/b} with {@code a==-b} resulting in {@code a/-a=-b/b=-1}
	 */
	DIVISOR_EQUALS_MINUS_DIVIDEND {
		@Override
		final long divide(DecimalArithmetic arithmetic, long uDecimalDividend, long uDecimalDivisor) {
			return -arithmetic.one();
		}
	};

	/**
	 * Performs the division for this special division result. The arithmetics
	 * overflow mode is considered.
	 * 
	 * @param arithmetic
	 *            the arithmetic associated with the values
	 * @param uDecimalDividend
	 *            the dividend
	 * @param uDecimalDivisor
	 *            the divisor
	 * @return <code>uDecimalDividend / uDecimalDivisor</code>
	 * @throws ArithmeticException
	 *             if {@code this==DIVISOR_IS_ZERO} or if an overflow occurs and
	 *             the arithmetic's {@link OverflowMode} is set to throw an
	 *             exception
	 */
	abstract long divide(DecimalArithmetic arithmetic, long uDecimalDividend, long uDecimalDivisor);

	/**
	 * Returns the special division case if it is one and null otherwise.
	 * 
	 * @param arithmetic
	 *            the arithmetic object
	 * @param uDecimalDividend
	 *            the dividend
	 * @param uDecimalDivisor
	 *            the divisor
	 * @return the special case if it is one and null otherwise
	 */
	static final SpecialDivisionResult getFor(DecimalArithmetic arithmetic, long uDecimalDividend, long uDecimalDivisor) {
		// NOTE: this must be the first case because 0/0 must also throw an
		// exception!
		if (uDecimalDivisor == 0) {
			return DIVISOR_IS_ZERO;
		}
		if (uDecimalDividend == 0) {
			return DIVIDEND_IS_ZERO;
		}
		final long one = arithmetic.one();
		if (uDecimalDivisor == one) {
			return DIVISOR_IS_ONE;
		}
		if (uDecimalDivisor == -one) {
			return DIVISOR_IS_MINUS_ONE;
		}
		if (uDecimalDividend == uDecimalDivisor) {
			return DIVISOR_EQUALS_DIVIDEND;
		}
		if (uDecimalDividend == -uDecimalDivisor) {
			return DIVISOR_EQUALS_MINUS_DIVIDEND;
		}
		return null;
	}
}