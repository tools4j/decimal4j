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


/**
 * Helper class for arithmetic operations with overflow checks.
 */
final class Checked {

	public static final long addLong(long long1, long long2) {
		final long result = long1 + long2;
		if ((long1 ^ long2) >= 0 & (long1 ^ result) < 0) {
			throw new ArithmeticException("Overflow: " + long1 + " + " + long2 + " = " + result);
		}
		return result;
	}
	public static final long add(DecimalArithmetic arith, long uDecimal1, long uDecimal2) {
		final long result = uDecimal1 + uDecimal2;
		if ((uDecimal1 ^ uDecimal2) >= 0 & (uDecimal1 ^ result) < 0) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimal1) + " + " + arith.toString(uDecimal2) + " = " + arith.toString(result));
		}
		return result;
	}

	public static final long subtract(DecimalArithmetic arith, long uDecimalMinuend, long uDecimalSubtrahend) {
		final long result = uDecimalMinuend - uDecimalSubtrahend;
		if ((uDecimalMinuend ^ uDecimalSubtrahend) < 0 & (uDecimalMinuend ^ result) < 0) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimalMinuend) + " - " + arith.toString(uDecimalSubtrahend) + " = " + arith.toString(result));
		}
		return result;
	}

	public static final long multiplyLong(long lValue1, long lValue2) {
		// Hacker's Delight, Section 2-12
		final int leadingZeros = Long.numberOfLeadingZeros(lValue1) + Long.numberOfLeadingZeros(~lValue1) + Long.numberOfLeadingZeros(lValue2) + Long.numberOfLeadingZeros(~lValue2);
		/*
		 * If leadingZeros > Long.SIZE + 1 it's definitely fine, if it's <
		 * Long.SIZE it's definitely bad. We do the leadingZeros check to avoid
		 * the division below if at all possible.
		 * 
		 * Otherwise, if b == Long.MIN_VALUE, then the only allowed values of a
		 * are 0 and 1. We take care of all a < 0 with their own check, because
		 * in particular, the case a == -1 will incorrectly pass the division
		 * check below.
		 * 
		 * In all other cases, we check that either a is 0 or the result is
		 * consistent with division.
		 */
		final long result = lValue1 * lValue2;
		if (leadingZeros > Long.SIZE + 1) {
			return result;
		}
		if (leadingZeros < Long.SIZE || (lValue1 < 0 & lValue2 == Long.MIN_VALUE) || (lValue1 != 0 && (result / lValue1) != lValue2)) {
			throw new ArithmeticException("Overflow: " + lValue1 + " * " + lValue2 + " = " + result);
		}
		return result;
	}

	public static final long multiplyByLong(DecimalArithmetic arith, long uDecimal, long lValue) {
		// Hacker's Delight, Section 2-12
		final int leadingZeros = Long.numberOfLeadingZeros(uDecimal) + Long.numberOfLeadingZeros(~uDecimal) + Long.numberOfLeadingZeros(lValue) + Long.numberOfLeadingZeros(~lValue);
		/*
		 * If leadingZeros > Long.SIZE + 1 it's definitely fine, if it's <
		 * Long.SIZE it's definitely bad. We do the leadingZeros check to avoid
		 * the division below if at all possible.
		 * 
		 * Otherwise, if b == Long.MIN_VALUE, then the only allowed values of a
		 * are 0 and 1. We take care of all a < 0 with their own check, because
		 * in particular, the case a == -1 will incorrectly pass the division
		 * check below.
		 * 
		 * In all other cases, we check that either a is 0 or the result is
		 * consistent with division.
		 */
		final long result = uDecimal * lValue;
		if (leadingZeros > Long.SIZE + 1) {
			return result;
		}
		if (leadingZeros < Long.SIZE || (uDecimal < 0 & lValue == Long.MIN_VALUE) || (uDecimal != 0 && (result / uDecimal) != lValue)) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " * " + lValue + " = " + arith.toString(result));
		}
		return result;
	}

	public static final long divideByLong(DecimalArithmetic arith, long uDecimalDividend, long lDivisor) {
		if (lDivisor == 0) {
			throw new ArithmeticException("Division by zero: " + arith.toString(uDecimalDividend) + " / " + lDivisor);
		}
		if (lDivisor == -1 & uDecimalDividend == Long.MIN_VALUE) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimalDividend) + " / " + lDivisor + " = " + arith.toString(Long.MIN_VALUE));
		} 
		return uDecimalDividend / lDivisor;
	}

	public static final long abs(DecimalArithmetic arith, long value) {
		final long abs = Math.abs(value);
		if (abs < 0) {
			throw new ArithmeticException("Overflow: abs(" + arith.toString(value) + ") = " + abs);
		}
		return abs;
	}

	public static final long negate(DecimalArithmetic arith, long value) {
		final long neg = -value;
		if (value != 0 & (value ^ neg) >= 0) {
			throw new ArithmeticException("Overflow: -" + arith.toString(value) + " = " + neg);
		}
		return neg;
	}

	// no instances
	private Checked() {
	}

}
