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
package org.decimal4j.arithmetic;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;

/**
 * Provides static methods to invert a Decimal number, that is, to calculate
 * {@code 1/x}.
 */
final class Invert {

	/**
	 * Inverts the specified long value truncating the result if necessary.
	 * 
	 * @param lValue
	 *            the long value to invert
	 * @return <tt>round<sub>DOWN</sub>(1/lValue)</tt>
	 * @throws ArithmeticException
	 *             if {@code lValue == 0}
	 */
	public static final long invertLong(long lValue) {
		if (lValue == 0) {
			throw new ArithmeticException("Division by zero: " + lValue + "^-1");
		}
		if (lValue == 1) {
			return 1;
		}
		if (lValue == -1) {
			return -1;
		}
		return 0;
	}

	/**
	 * Inverts the specified long value rounding the result if necessary.
	 * 
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param lValue
	 *            the long value to invert
	 * @return <tt>round(1/lValue)</tt>
	 * @throws ArithmeticException
	 *             if {@code lValue == 0} or if
	 *             {@code roundingMode==UNNECESSARY} and rounding is necessary
	 */
	public static final long invertLong(DecimalRounding rounding, long lValue) {
		// special cases first
		if (lValue == 0) {
			throw new ArithmeticException("Division by zero: " + lValue + "^-1");
		}
		if (lValue == 1) {
			return 1;
		}
		if (lValue == -1) {
			return -1;
		}
		return Rounding.calculateRoundingIncrementForDivision(rounding, 0, 1, lValue);
	}

	/**
	 * Inverts the specified unscaled decimal value truncating the result if
	 * necessary.
	 * 
	 * @param arith
	 *            the arithmetic associated with the given value
	 * @param uDecimal
	 *            the unscaled decimal value to invert
	 * @return <tt>round<sub>DOWN</sub>(1/uDecimal)</tt>
	 * @throws ArithmeticException
	 *             if {@code uDecimalDividend} is zero or if an overflow occurs
	 *             and the arithmetics {@link OverflowMode} is set to throw an
	 *             exception
	 * @see DecimalArithmetic#divide(long, long)
	 */
	public static final long invert(DecimalArithmetic arith, long uDecimal) {
		// special cases are handled by divide
		return arith.divide(arith.one(), uDecimal);
	}

	/**
	 * Inverts the specified unscaled decimal value rounding the result if
	 * necessary.
	 * 
	 * @param arith
	 *            the arithmetic associated with the given value
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param uDecimal
	 *            the unscaled decimal value to invert
	 * @return <tt>round(1/uDecimal)</tt>
	 * @throws ArithmeticException
	 *             if {@code uDecimalDividend} is zero, if {@code roundingMode}
	 *             is UNNECESSARY and rounding is necessary or if an overflow
	 *             occurs and the arithmetics {@link OverflowMode} is set to
	 *             throw an exception
	 * @see DecimalArithmetic#divide(long, long)
	 */
	public static final long invert(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal) {
		// special cases are handled by divide
		return arith.divide(arith.one(), uDecimal);
	}

	// no instances
	private Invert() {
		super();
	}
}
