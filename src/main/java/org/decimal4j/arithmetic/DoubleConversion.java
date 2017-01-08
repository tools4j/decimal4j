/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 decimal4j (tools4j), Marco Terzer
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
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;

/**
 * Contains methods to convert from and to double.
 */
final class DoubleConversion {

	private static final long LONG_MASK = 0xffffffffL;

	// The mask for the significand, according to the {@link Double#doubleToRawLongBits(double)} spec.
	private static final long SIGNIFICAND_MASK = 0x000fffffffffffffL;

	// The mask for the exponent, according to the {@link Double#doubleToRawLongBits(double)} spec.
	@SuppressWarnings("unused")
	private static final long EXPONENT_MASK = 0x7ff0000000000000L;

	// The mask for the sign, according to the {@link Double#doubleToRawLongBits(double)} spec.
	private static final long SIGN_MASK = 0x8000000000000000L;

	private static final int SIGNIFICAND_BITS = 52;

	private static final int EXPONENT_BIAS = 1023;

	/**
	 * The implicit 1 bit that is omitted in significands of normal doubles.
	 */
	private static final long IMPLICIT_BIT = SIGNIFICAND_MASK + 1;

	private static final double MIN_LONG_AS_DOUBLE = -0x1p63;
	/*
	 * We cannot store Long.MAX_VALUE as a double without losing precision. Instead, we store Long.MAX_VALUE + 1 ==
	 * -Long.MIN_VALUE, and then offset all comparisons by 1.
	 */
	private static final double MAX_LONG_AS_DOUBLE_PLUS_ONE = 0x1p63;

	/**
	 * Converts the specified double value to a long truncating the fractional part if any is present. If the value is
	 * NaN, infinite or outside of the valid long range, an exception is thrown.
	 * 
	 * @param value
	 *            the value to convert
	 * @return <tt>round<sub>DOWN</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large for the double to be represented
	 *             as a {@code long}
	 */
	public static final long doubleToLong(double value) {
		if (Double.isNaN(value)) {
			throw new IllegalArgumentException("Cannot convert double to long: " + value);
		}
		if (isInLongRange(value)) {
			return (long) value;
		}
		throw new IllegalArgumentException("Overflow for conversion from double to long: " + value);
	}

	/**
	 * Converts the specified double value to a long rounding the fractional part if necessary using the given
	 * {@code rounding} mode. If the value is NaN, infinite or outside of the valid long range, an exception is thrown.
	 * 
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param value
	 *            the value to convert
	 * @return <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large for the double to be represented
	 *             as a {@code long}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is necessary
	 */
	public static final long doubleToLong(DecimalRounding rounding, double value) {
		if (Double.isNaN(value)) {
			throw new IllegalArgumentException("Cannot convert double to long: " + value);
		}
		if (isInLongRange(value)) {
			return (long) roundIntermediate(value, rounding);
		}
		throw new IllegalArgumentException("Overflow for conversion from double to long: " + value);
	}

	/*
	 * Copied from guava. This method returns a value y such that rounding y DOWN (towards zero) gives the same result
	 * as rounding x according to the specified mode. PRECONDITION: isFinite(x)
	 */
	private static final double roundIntermediate(double x, DecimalRounding mode) {
		switch (mode) {
		case UNNECESSARY:
			if (!isMathematicalInteger(x)) {
				throw new ArithmeticException("Rounding necessary to convert to an integer value: " + x);
			}
			return x;
		case FLOOR:
			if (x >= 0.0 || isMathematicalInteger(x)) {
				return x;
			} else {
				return (long)x - 1L;
			}
		case CEILING:
			if (x <= 0.0 || isMathematicalInteger(x)) {
				return x;
			} else {
				return (long)x + 1L;
			}
		case DOWN:
			return x;
		case UP:
			if (isMathematicalInteger(x)) {
				return x;
			} else {
				return (long)x + (x > 0 ? 1L : -1L);
			}
		case HALF_EVEN:
			return Math.rint(x);
		case HALF_UP: {
			double z = Math.rint(x);
			if (Math.abs(x - z) == 0.5) {
				return x + Math.copySign(0.5, x);
			} else {
				return z;
			}
		}
		case HALF_DOWN: {
			double z = Math.rint(x);
			if (Math.abs(x - z) == 0.5) {
				return x;
			} else {
				return z;
			}
		}
		default:
			throw new IllegalArgumentException("Unsupported rounding mode: " + mode);
		}
	}

	/**
	 * Converts the specified double value to an unscaled decimal truncating extra fractional digits if necessary. If
	 * the value is NaN, infinite or outside of the valid Decimal range, an exception is thrown.
	 * 
	 * @param arith
	 *            the arithmetic associated with the result value
	 * @param value
	 *            the value to convert
	 * @return <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large for the double to be represented
	 *             as a Decimal of the arithmetic's scale
	 */
	public static final long doubleToUnscaled(DecimalArithmetic arith, double value) {
		return doubleToUnscaled(arith, DecimalRounding.DOWN, value);
	}

	/**
	 * Converts the specified double value to an unscaled decimal. The specified {@code rounding} mode is used if
	 * rounding is necessary. If the value is NaN, infinite or outside of the valid Decimal range, an exception is
	 * thrown.
	 * 
	 * @param arith
	 *            the arithmetic associated with the result value
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param value
	 *            the value to convert
	 * @return <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large for the double to be represented
	 *             as a Decimal of the arithmetic's scale
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is necessary
	 */
	public static final long doubleToUnscaled(DecimalArithmetic arith, DecimalRounding rounding, double value) {
		if (value == 0) {
			return 0;
		}
		final int exp = Math.getExponent(value);
		if (exp >= Long.SIZE) {
			throw newOverflowException(arith, value);
		}

		// multiply significand by scale factor into a 128bit integer
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final long significand = getSignificand(value);

		// HD + Knuth's Algorithm M from [Knu2] section 4.3.1.
		final int lFactor = (int) (significand & LONG_MASK);
		final int hFactor = (int) (significand >>> 32);
		final long w1, w2, w3;
		long k, t;

		t = scaleMetrics.mulloByScaleFactor(lFactor);
		w3 = t & LONG_MASK;
		k = t >>> 32;

		t = scaleMetrics.mulloByScaleFactor(hFactor) + k;
		w2 = t & LONG_MASK;
		w1 = t >>> 32;

		t = scaleMetrics.mulhiByScaleFactor(lFactor) + w2;
		k = t >>> 32;

		final long hScaled = scaleMetrics.mulhiByScaleFactor(hFactor) + w1 + k;
		final long lScaled = (t << 32) + w3;

		// now multiply or divide by powers of two as instructed by the double
		// exponent
		final int shift = exp - SIGNIFICAND_BITS;
		return doubleToUnscaledShift(arith, rounding, value, hScaled, lScaled, shift);
	}

	private static final long doubleToUnscaledShift(DecimalArithmetic arith, DecimalRounding rounding, double value, long hScaled, long lScaled, int shift) {
		if (shift > 0) {
			// multiply: shift left
			if (hScaled != 0) {
				throw newOverflowException(arith, value);
			}
			final int zeros = Long.numberOfLeadingZeros(lScaled);
			if (shift >= zeros) {
				throw newOverflowException(arith, value);
			}
			final long absResult = lScaled << shift;
			return value >= 0 ? absResult : -absResult;
		} else if (shift == 0) {
			if (hScaled != 0 | lScaled < 0) {
				throw newOverflowException(arith, value);
			}
			return value >= 0 ? lScaled : -lScaled;
		} else {// shift < 0
			// divide: shift right
			if (rounding == DecimalRounding.DOWN) {
				return doubleToUnscaledShiftRight(arith, value, hScaled, lScaled, -shift);
			}
			return doubleToUnscaledShiftRight(arith, rounding, value, hScaled, lScaled, -shift);
		}
	}

	private static final long doubleToUnscaledShiftRight(DecimalArithmetic arith, double value, long hScaled, long lScaled, int shift) {
		final long absResult;
		if (shift < Long.SIZE) {
			if ((hScaled >>> shift) != 0) {
				throw newOverflowException(arith, value);
			}
			absResult = (hScaled << (Long.SIZE - shift)) | (lScaled >>> shift);
		} else if (shift < 2 * Long.SIZE) {
			absResult = (hScaled >>> (shift - Long.SIZE));
		} else {
			return 0;// rounded down
		}
		if (absResult < 0) {
			throw newOverflowException(arith, value);
		}
		return value >= 0 ? absResult : -absResult;
	}

	private static final long doubleToUnscaledShiftRight(DecimalArithmetic arith, DecimalRounding rounding, double value, long hScaled, long lScaled, int shift) {
		final long absResult;
		final TruncatedPart truncatedPart;
		if (shift < Long.SIZE) {
			if ((hScaled >>> shift) != 0) {
				throw newOverflowException(arith, value);
			}
			absResult = (hScaled << (Long.SIZE - shift)) | (lScaled >>> shift);
			final long rem = modPow2(lScaled, shift);
			truncatedPart = Rounding.truncatedPartFor2powN(rem, shift);
		} else if (shift < 2 * Long.SIZE) {
			absResult = (hScaled >>> (shift - Long.SIZE));
			final long rem = modPow2(hScaled, shift - Long.SIZE);
			truncatedPart = Rounding.truncatedPartFor2powN(rem, lScaled, shift);
		} else {
			absResult = 0;// rounded down
			truncatedPart = Rounding.truncatedPartFor2powN(hScaled, lScaled, shift);
		}
		final int inc = absResult < 0 ? 0
				: rounding.calculateRoundingIncrement(value >= 0 ? 1 : -1, absResult, truncatedPart);
		if (absResult < 0 | (value >= 0 & absResult == Long.MAX_VALUE & inc == 1)) {
			throw newOverflowException(arith, value);
		}
		return (value >= 0 ? absResult : -absResult) + inc;
	}

	/**
	 * Converts the specified long value to a double truncating extra mantissa digits if necessary.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param value
	 *            the long value
	 * @return <tt>round<sub>DOWN</sub>(value)</tt>
	 */
	public static final double longToDouble(DecimalArithmetic arith, long value) {
		return unscaledToDouble(arith, DecimalRounding.DOWN, value);
	}

	/**
	 * Converts the specified long value to a double rounding extra mantissa digits if necessary.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param value
	 *            the long value
	 * @return <tt>round(value)</tt>
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is necessary
	 */
	public static final double longToDouble(DecimalArithmetic arith, DecimalRounding rounding, long value) {
		if (rounding == DecimalRounding.HALF_EVEN) {
			return value;
		}
		return unscaledToDouble(arith, rounding, value);
	}

	/**
	 * Converts the specified unscaled decimal value to a double truncating extra precision digits if necessary.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param unscaled
	 *            the unscaled decimal value
	 * @return <tt>round<sub>DOWN</sub>(value)</tt>
	 */
	public static final double unscaledToDouble(DecimalArithmetic arith, long unscaled) {
		return unscaledToDouble(arith, DecimalRounding.DOWN, unscaled);
	}

	/**
	 * Converts the specified unscaled decimal value to a double rounding extra precision digits if necessary.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param unscaled
	 *            the unscaled decimal value
	 * @return <tt>round(value)</tt>
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is necessary
	 */
	public static final double unscaledToDouble(DecimalArithmetic arith, DecimalRounding rounding, long unscaled) {
		if (unscaled == 0) {
			return 0;
		}
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		
		final long absUnscaled = Math.abs(unscaled);
        if (absUnscaled < (1L << SIGNIFICAND_BITS) & rounding == DecimalRounding.HALF_EVEN) {
            // Don't have too guard against Math.abs(MIN_VALUE)
            // because MIN_VALUE also works without loss of precision
            return (double)unscaled / scaleMetrics.getScaleFactor();
        }
		
		// eliminate sign and trailing power-of-2 zero bits
		final int pow2 = Long.numberOfTrailingZeros(absUnscaled);
		final long absVal = absUnscaled >>> pow2;
		final int nlzAbsVal = Long.numberOfLeadingZeros(absVal);

		/*
		 * NOTE: a) If absVal has no more than 53 bits it can be represented as a double value without loss of precision
		 * (52 mantissa bits plus the implicit leading 1 bit) b) The scale factor has never more than 53 bits if shifted
		 * right by the trailing power-of-2 zero bits ==> For HALF_EVEN rounding mode we can therefore apply the scale
		 * factor via double division without losing information
		 */
		if (Long.SIZE - nlzAbsVal <= SIGNIFICAND_BITS + 1 & rounding == DecimalRounding.HALF_EVEN) {
			return unscaledToDoubleWithDoubleDivisionRoundHalfEven(scaleMetrics, unscaled, pow2, absVal);
		}

		/*
		 * 1) we align absVal and factor such that: 2*factor > absVal >= factor 
		 *    then the division absVal/factor == 1.xxxxx, i.e. it is normalized 
		 * 2) because we omit the 1 in the mantissa, we calculate 
		 *    valModFactor = absVal - floor(absVal/factor)*factor = absVal - 1*factor 
		 * 3) we shift valModFactor such that the 1 from the division would be on bit 53 
		 * 4) we perform the division
		 */

		// (1) + (2)
		final int exp;
		final int mantissaShift;
		final long valModFactor;
		final int alignShift = nlzAbsVal - scaleMetrics.getScaleFactorNumberOfLeadingZeros();
		if (alignShift >= 0) {
			final long scaledAbsVal = absVal << alignShift;
			final long diff = scaledAbsVal - scaleMetrics.getScaleFactor();
			exp = -alignShift + (int) (diff >> 63);
			// if scaledAbsVal < factor we shift left by 1, i.e. we add the absVal
			valModFactor = diff + ((diff >> 63) & scaledAbsVal);
			mantissaShift = SIGNIFICAND_BITS;
		} else {
			final long scaledFactor = scaleMetrics.getScaleFactor() << -alignShift;
			if (Unsigned.isLess(absVal, scaledFactor)) {
				exp = -alignShift - 1;
				// if absVal < scaledFactor we shift left by 1 (right shift of scaledFactor to avoid overflow)
				valModFactor = absVal - (scaledFactor >>> 1);
				mantissaShift = SIGNIFICAND_BITS + alignShift + 1;
			} else {
				exp = -alignShift;
				valModFactor = absVal - scaledFactor;
				mantissaShift = SIGNIFICAND_BITS + alignShift;
			}
		}
		if (rounding == DecimalRounding.DOWN) {
			return unscaledToDoubleShiftAndDivideByScaleFactor(scaleMetrics, unscaled, exp + pow2, mantissaShift,
					valModFactor);
		}
		// (3) + (4)
		return unscaledToDoubleShiftAndDivideByScaleFactor(scaleMetrics, rounding, unscaled, exp + pow2, mantissaShift,
				valModFactor);
	}

	private static final double unscaledToDoubleWithDoubleDivisionRoundHalfEven(ScaleMetrics scaleMetrics, long unscaled, int pow2, long absVal) {
		final int scale = scaleMetrics.getScale();
		final double dividend = absVal;
		final double divisor = scaleMetrics.getScaleFactor() >> scale;
		final double quotient = dividend / divisor;
		final int exponent = Math.getExponent(quotient) + pow2 - scale;
		final long significand = Double.doubleToRawLongBits(quotient) & SIGNIFICAND_MASK;
		final long raw = (unscaled & SIGN_MASK) | (((long) (exponent + EXPONENT_BIAS)) << SIGNIFICAND_BITS)
				| significand;
		return Double.longBitsToDouble(raw);
	}

	private static final double unscaledToDoubleShiftAndDivideByScaleFactor(ScaleMetrics scaleMetrics, long unscaled, int exp, int mantissaShift, long valModFactor) {
		final long quot;
		if (mantissaShift >= 0) {
			final long hValModFactor = (valModFactor >>> (Long.SIZE - mantissaShift)) & (-mantissaShift >> 63);
			final long lValModFactor = valModFactor << mantissaShift;
			if (hValModFactor == 0) {
				quot = scaleMetrics.divideUnsignedByScaleFactor(lValModFactor);
			} else {
				quot = Math.abs(Div.div128by64(DecimalRounding.DOWN, unscaled < 0, hValModFactor, lValModFactor,
						scaleMetrics.getScaleFactor()));
			}
		} else {
			quot = scaleMetrics.divideByScaleFactor(valModFactor >>> -mantissaShift);
		}
		final long raw = (unscaled & SIGN_MASK) | (((long) (exp + EXPONENT_BIAS)) << SIGNIFICAND_BITS)
				| (quot & SIGNIFICAND_MASK);
		return Double.longBitsToDouble(raw);
	}

	private static final double unscaledToDoubleShiftAndDivideByScaleFactor(ScaleMetrics scaleMetrics, DecimalRounding rounding, long unscaled, int exp, int mantissaShift, long valModFactor) {
		final long quotient;
		final long scaleFactor = scaleMetrics.getScaleFactor();
		if (mantissaShift >= 0) {
			final long hValModFactor = (valModFactor >>> (Long.SIZE - mantissaShift)) & (-mantissaShift >> 63);
			final long lValModFactor = valModFactor << mantissaShift;
			if (hValModFactor == 0) {
				final long truncated = scaleMetrics.divideUnsignedByScaleFactor(lValModFactor);
				final long remainder = applySign(unscaled, lValModFactor - scaleMetrics.multiplyByScaleFactor(truncated));
				quotient = truncated + Math.abs(Rounding.calculateRoundingIncrementForDivision(rounding, truncated, remainder, scaleFactor));
			} else {
				quotient = Math.abs(Div.div128by64(rounding, unscaled < 0, hValModFactor, lValModFactor, scaleFactor));
				// rounding already done by div128by64
			}
		} else {
			final long scaledVal = valModFactor >>> -mantissaShift;
			final long truncated = scaleMetrics.divideByScaleFactor(scaledVal);
			final long remainder = applySign(unscaled, ((scaledVal - scaleMetrics.multiplyByScaleFactor(truncated)) << -mantissaShift)
					| (valModFactor & (-1L >>> (Long.SIZE + mantissaShift))));
			// this cannot overflow as min(mantissaShift)=-10 for scale=1, -9 for scale=10, ..., -1 for scale=10^9
			final long shiftedScaleFactor = scaleFactor << -mantissaShift;
			quotient = truncated + Math.abs(Rounding.calculateRoundingIncrementForDivision(rounding, truncated, remainder,
					shiftedScaleFactor));
		}
		final long raw;
		if (quotient <= SIGNIFICAND_MASK) {
			raw = (unscaled & SIGN_MASK) | (((long) (exp + EXPONENT_BIAS)) << SIGNIFICAND_BITS)
					| (quotient & SIGNIFICAND_MASK);
		} else {
			// rounding made our value to be 1 instead of smaller than one. 1 +
			// 1 == 2 i.e. our mantissa is zero due to the implicit 1 and our
			// exponent increments by 1
			raw = (unscaled & SIGN_MASK) | (((long) ((exp + 1) + EXPONENT_BIAS)) << SIGNIFICAND_BITS);
		}
		return Double.longBitsToDouble(raw);
	}

	// @return value % (2^n)
	private static final long modPow2(long value, int n) {
		// return value & ((1L << n) - 1);
		return value & (-1L >>> (Long.SIZE - n)) & (-n >> 31);// last bracket is for case n=0
	}
	
	private static final long applySign(final long signed, final long value) {
		return signed >= 0 ? value : -value;
	}

	private static final boolean isInLongRange(double value) {
		return MIN_LONG_AS_DOUBLE - value < 1.0 & value < MAX_LONG_AS_DOUBLE_PLUS_ONE;
	}

	private static final boolean isMathematicalInteger(double x) {
		return isFinite(x) && (x == 0.0
				|| SIGNIFICAND_BITS - Long.numberOfTrailingZeros(getSignificand(x)) <= Math.getExponent(x));
	}

	private static final boolean isFinite(double d) {
		return Math.abs(d) <= Double.MAX_VALUE;
	}

	// PRECONDITION: isFinite(d)
	private static final long getSignificand(double d) {
		int exponent = Math.getExponent(d);
		long bits = Double.doubleToRawLongBits(d);
		bits &= SIGNIFICAND_MASK;
		return (exponent == Double.MIN_EXPONENT - 1) ? bits << 1 : bits | IMPLICIT_BIT;
	}

	private static final IllegalArgumentException newOverflowException(final DecimalArithmetic arith, double value) {
		return new IllegalArgumentException(
				"Overflow for conversion from double to decimal with scale " + arith.getScale() + ": " + value);
	}

	// no instances
	private DoubleConversion() {
		super();
	}
}
