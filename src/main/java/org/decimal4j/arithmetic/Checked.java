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
import org.decimal4j.scale.ScaleMetrics;

/**
 * Helper class for arithmetic operations with overflow checks.
 */
final class Checked {

	private static final boolean isAddOverflow(long long1, long long2, long result) {
		return (long1 ^ long2) >= 0 & (long1 ^ result) < 0;
	}

	private static final boolean isSubtractOverflow(long minuend, long subtrahend, long result) {
		return (minuend ^ subtrahend) < 0 & (minuend ^ result) < 0;
	}

	public static final long addLong(long long1, long long2) {
		final long result = long1 + long2;
		if (isAddOverflow(long1, long2, result)) {
			throw new ArithmeticException("Overflow: " + long1 + " + " + long2 + " = " + result);
		}
		return result;
	}

	public static final long addDecimalAndLong(DecimalArithmetic arith, long uDecimal, long lValue) {
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final long minInt = scaleMetrics.getMinIntegerValue();
		final long maxInt = scaleMetrics.getMaxIntegerValue();
		if (minInt <= lValue & lValue <= maxInt) {
			return add(arith, uDecimal, scaleMetrics.multiplyByScaleFactor(lValue));
		} else {
			if ((uDecimal ^ lValue) >= 0) {
				throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " + " + lValue);
			}
			// else: different sign, we must be careful,
			// scaling could overflow but this could be offset by other operand
		}
		final long ival = scaleMetrics.divideByScaleFactor(uDecimal);
		final long fval = uDecimal - scaleMetrics.multiplyByScaleFactor(ival);
		final long ires = ival + lValue;// cannot overflow with different sign
		final long scaled = scaleMetrics.multiplyByScaleFactor(ires);
		final long result = scaled + fval;
		if (ires < minInt | ires > maxInt) {
			if ((ires + 1 != minInt & ires - 1 != maxInt) | !isAddOverflow(scaled, fval, result)) {
				throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " + " + lValue + " = " + result);
			}
			// else: adding fval brought value back into the valid range:
			// ires was only off by one and adding fval is overflow, hence back
			// into valid range
		} else if (isAddOverflow(scaled, fval, result)) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " + " + lValue + " = " + result);
		}
		return result;
	}

	public static final long add(DecimalArithmetic arith, long uDecimal1, long uDecimal2) {
		final long result = uDecimal1 + uDecimal2;
		if ((uDecimal1 ^ uDecimal2) >= 0 & (uDecimal1 ^ result) < 0) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimal1) + " + " + arith.toString(uDecimal2)
					+ " = " + arith.toString(result));
		}
		return result;
	}

	public static final long subtract(DecimalArithmetic arith, long uDecimalMinuend, long uDecimalSubtrahend) {
		final long result = uDecimalMinuend - uDecimalSubtrahend;
		if (isSubtractOverflow(uDecimalMinuend, uDecimalSubtrahend, result)) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimalMinuend) + " - "
					+ arith.toString(uDecimalSubtrahend) + " = " + arith.toString(result));
		}
		return result;
	}

	public static final long subtractLongFromDecimal(DecimalArithmetic arith, long uDecimal, long lValue) {
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final long minInt = scaleMetrics.getMinIntegerValue();
		final long maxInt = scaleMetrics.getMaxIntegerValue();
		if (minInt <= lValue & lValue <= maxInt) {
			return subtract(arith, uDecimal, scaleMetrics.multiplyByScaleFactor(lValue));
		} else {
			if ((uDecimal ^ lValue) < 0) {
				throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " - " + lValue);
			}
			// else: same sign, we must be careful,
			// scaling could overflow but this could be offset by other operand
		}
		final long ival = scaleMetrics.divideByScaleFactor(uDecimal);
		final long fval = uDecimal - scaleMetrics.multiplyByScaleFactor(ival);
		final long ires = ival - lValue;// cannot overflow with same sign
		final long scaled = scaleMetrics.multiplyByScaleFactor(ires);
		final long result = scaled + fval;
		if (ires < minInt | ires > maxInt) {
			if ((ires + 1 != minInt & ires - 1 != maxInt) | !isAddOverflow(scaled, fval, result)) {
				throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " - " + lValue + " = " + result);
			}
			// else: adding fval brought value back into the valid range:
			// ires was only off by one and adding fval is overflow, hence back
			// into valid range
		} else if (isAddOverflow(scaled, fval, result)) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " - " + lValue + " = " + result);
		}
		return result;
	}

	public static final long multiplyLong(long lValue1, long lValue2) {
		// Hacker's Delight, Section 2-12
		final int leadingZeros = Long.numberOfLeadingZeros(lValue1) + Long.numberOfLeadingZeros(~lValue1)
				+ Long.numberOfLeadingZeros(lValue2) + Long.numberOfLeadingZeros(~lValue2);
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
		if (leadingZeros < Long.SIZE || (lValue1 < 0 & lValue2 == Long.MIN_VALUE)
				|| (lValue1 != 0 && (result / lValue1) != lValue2)) {
			throw new ArithmeticException("Overflow: " + lValue1 + " * " + lValue2 + " = " + result);
		}
		return result;
	}

	public static final long multiplyByLong(DecimalArithmetic arith, long uDecimal, long lValue) {
		// Hacker's Delight, Section 2-12
		final int leadingZeros = Long.numberOfLeadingZeros(uDecimal) + Long.numberOfLeadingZeros(~uDecimal)
				+ Long.numberOfLeadingZeros(lValue) + Long.numberOfLeadingZeros(~lValue);
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
		if (leadingZeros < Long.SIZE || (uDecimal < 0 & lValue == Long.MIN_VALUE)
				|| (uDecimal != 0 && (result / uDecimal) != lValue)) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " * " + lValue + " = "
					+ arith.toString(result));
		}
		return result;
	}

	public static final long divideByLong(DecimalArithmetic arith, long uDecimalDividend, long lDivisor) {
		if (lDivisor == 0) {
			throw new ArithmeticException("Division by zero: " + arith.toString(uDecimalDividend) + " / " + lDivisor);
		}
		if (lDivisor == -1 & uDecimalDividend == Long.MIN_VALUE) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimalDividend) + " / " + lDivisor + " = "
					+ arith.toString(Long.MIN_VALUE));
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
