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

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.OverflowMode;

/**
 * Helper class used by multiplication methods to handle special cases.
 */
enum SpecialMultiplicationResult {
	/**
	 * {@code a*b} with {@code a==0} or {@code b==0} leading to {@code 0}
	 */
	FACTOR_IS_ZERO {
		@Override
		final long multiply(DecimalArithmetic arithmetic, long uDecimal1, long uDecimal2) {
			return 0;
		}
	},
	/**
	 * {@code a*b} with {@code a==1} leading to {@code b}
	 */
	FACTOR_1_IS_ONE {
		@Override
		final long multiply(DecimalArithmetic arithmetic, long uDecimal1, long uDecimal2) {
			return uDecimal2;
		}
	},
	/**
	 * {@code a*b} with {@code b==1} leading to {@code a}
	 */
	FACTOR_2_IS_ONE {
		@Override
		final long multiply(DecimalArithmetic arithmetic, long uDecimal1, long uDecimal2) {
			return uDecimal1;
		}
	},
	/**
	 * {@code a*b} with {@code a==-1} leading to {@code -b}
	 */
	FACTOR_1_IS_MINUS_ONE {
		@Override
		final long multiply(DecimalArithmetic arithmetic, long uDecimal1, long uDecimal2) {
			return arithmetic.negate(uDecimal2);// we must go through arithmetic
												// because overflow is possible
		}
	},
	/**
	 * {@code a*b} with {@code b==-1} leading to {@code -a}
	 */
	FACTOR_2_IS_MINUS_ONE {
		@Override
		final long multiply(DecimalArithmetic arithmetic, long uDecimal1, long uDecimal2) {
			return arithmetic.negate(uDecimal1);// we must go through arithmetic
												// because overflow is possible
		}
	},
	/**
	 * {@code a*b} with {@code a==b} leading to {@code a^2}
	 */
	FACTORS_ARE_EQUAL {
		@Override
		final long multiply(DecimalArithmetic arithmetic, long uDecimal1, long uDecimal2) {
			return arithmetic.square(uDecimal1);
		}
	};

	/**
	 * Performs the multiplication for this special multiplication result. The
	 * arithmetics overflow mode is considered.
	 * 
	 * @param arithmetic
	 *            the arithmetic associated with the values
	 * @param uDecimal1
	 *            the first factor
	 * @param uDecimal2
	 *            the second factor
	 * @return <tt>uDecimal1 * uDecimal2</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and the arithmetic's
	 *             {@link OverflowMode} is set to throw an exception
	 */
	abstract long multiply(DecimalArithmetic arithmetic, long uDecimal1, long uDecimal2);

	/**
	 * Returns the special multiplication case if it is one and null otherwise.
	 * 
	 * @param arithmetic
	 *            the arithmetic object
	 * @param uDecimal1
	 *            the first factor
	 * @param uDecimal2
	 *            the second factor
	 * @return special case if found one and null otherwise
	 */
	static final SpecialMultiplicationResult getFor(DecimalArithmetic arithmetic, long uDecimal1, long uDecimal2) {
		if (uDecimal1 == 0 | uDecimal2 == 0) {
			return FACTOR_IS_ZERO;
		}
		final long one = arithmetic.one();
		if (uDecimal1 == one) {
			return FACTOR_1_IS_ONE;
		}
		if (uDecimal2 == one) {
			return FACTOR_2_IS_ONE;
		}
		if (uDecimal1 == -one) {
			return FACTOR_1_IS_MINUS_ONE;
		}
		if (uDecimal2 == -one) {
			return FACTOR_2_IS_MINUS_ONE;
		}
		if (uDecimal1 == uDecimal2) {
			return FACTORS_ARE_EQUAL;
		}
		return null;
	}
}