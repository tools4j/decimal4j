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
 * Contains methods to convert from and to float.
 */
final class FloatConversion {

	private static final long LONG_MASK = 0xffffffffL;

	// The mask for the significand, according to the {@link
	// Float#floatToRawIntBits(float)} spec.
	private static final int SIGNIFICAND_MASK = 0x007fffff;

	// The mask for the exponent, according to the {@link Float#floatToRawIntBits(float)} spec.
	@SuppressWarnings("unused")
	private static final int EXPONENT_MASK = 0x7f800000;

	// The mask for the sign, according to the {@link Float#floatToRawIntBits(float)} spec.
	private static final int SIGN_MASK = 0x80000000;

	private static final int SIGNIFICAND_BITS = 23;

	private static final int EXPONENT_BIAS = 127;

	/**
	 * The implicit 1 bit that is omitted in significands of normal floats.
	 */
	private static final int IMPLICIT_BIT = SIGNIFICAND_MASK + 1;

	private static final float MIN_LONG_AS_FLOAT = -0x1p63f;
	/*
	 * We cannot store Long.MAX_VALUE as a float without losing precision. Instead, we store Long.MAX_VALUE + 1 ==
	 * -Long.MIN_VALUE, and then offset all comparisons by 1.
	 */
	private static final float MAX_LONG_AS_FLOAT_PLUS_ONE = 0x1p63f;

	/**
	 * Converts the specified float value to a long truncating the fractional part if any is present. If the value is
	 * NaN, infinite or outside of the valid long range, an exception is thrown.
	 * 
	 * @param value
	 *            the value to convert
	 * @return <tt>round<sub>DOWN</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large for the float to be represented
	 *             as a {@code long}
	 */
	public static final long floatToLong(float value) {
		if (Float.isNaN(value)) {
			throw new IllegalArgumentException("Cannot convert float to long: " + value);
		}
		if (isInLongRange(value)) {
			return (long) value;
		}
		throw new IllegalArgumentException("Overflow for conversion from float to long: " + value);
	}

	/**
	 * Converts the specified float value to a long rounding the fractional part if necessary using the given
	 * {@code rounding} mode. If the value is NaN, infinite or outside of the valid long range, an exception is thrown.
	 * 
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param value
	 *            the value to convert
	 * @return <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large for the float to be represented
	 *             as a {@code long}
	 */
	public static final long floatToLong(DecimalRounding rounding, float value) {
		if (Float.isNaN(value)) {
			throw new IllegalArgumentException("Cannot convert float to long: " + value);
		}
		if (isInLongRange(value)) {
			return (long) roundIntermediate(value, rounding);
		}
		throw new IllegalArgumentException("Overflow for conversion from float to long: " + value);
	}

	/*
	 * Copied from guava. This method returns a value y such that rounding y DOWN (towards zero) gives the same result
	 * as rounding x according to the specified mode. PRECONDITION: isFinite(x)
	 */
	private static final float roundIntermediate(float x, DecimalRounding mode) {
		switch (mode) {
		case UNNECESSARY:
			if (!isMathematicalInteger(x)) {
				throw new ArithmeticException("Rounding necessary to convert to an integer value: " + x);
			}
			return x;
		case FLOOR:
			if (x >= 0.0f || isMathematicalInteger(x)) {
				return x;
			} else {
				return (long)x - 1L;
			}
		case CEILING:
			if (x <= 0.0f || isMathematicalInteger(x)) {
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
			return rint(x);
		case HALF_UP: {
			final float z = rint(x);
			if (Math.abs(x - z) == 0.5f) {
				return x + Math.copySign(0.5f, x);
			} else {
				return z;
			}
		}
		case HALF_DOWN: {
			final float z = rint(x);
			if (Math.abs(x - z) == 0.5f) {
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
	 * Converts the specified float value to an unscaled decimal truncating extra fractional digits if necessary. If the
	 * value is NaN, infinite or outside of the valid Decimal range, an exception is thrown.
	 * 
	 * @param arith
	 *            the arithmetic associated with the result value
	 * @param value
	 *            the value to convert
	 * @return <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large for the float to be represented
	 *             as a Decimal of the arithmetic's scale
	 */
	public static final long floatToUnscaled(DecimalArithmetic arith, float value) {
		return floatToUnscaled(arith, DecimalRounding.DOWN, value);
	}

	/**
	 * Converts the specified float value to an unscaled decimal. The specified {@code rounding} mode is used if
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
	 *             if {@code value} is NaN or infinite or if the magnitude is too large for the float to be represented
	 *             as a Decimal of the arithmetic's scale
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is necessary
	 */
	public static final long floatToUnscaled(DecimalArithmetic arith, DecimalRounding rounding, float value) {
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

		// now multiply or divide by powers of two as instructed by the float exponent
		final int shift = exp - SIGNIFICAND_BITS;
		return floatToUnscaledShift(arith, rounding, value, hScaled, lScaled, shift);
	}

	private static final long floatToUnscaledShift(DecimalArithmetic arith, DecimalRounding rounding, float value, long hScaled, long lScaled, int shift) {
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
				return floatToUnscaledShiftRight(arith, value, hScaled, lScaled, -shift);
			}
			return floatToUnscaledShiftRight(arith, rounding, value, hScaled, lScaled, -shift);
		}
	}

	private static final long floatToUnscaledShiftRight(DecimalArithmetic arith, float value, long hScaled, long lScaled, int shift) {
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

	private static final long floatToUnscaledShiftRight(DecimalArithmetic arith, DecimalRounding rounding, float value, long hScaled, long lScaled, int shift) {
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
	 * Converts the specified long value to a float truncating extra mantissa digits if necessary.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param value
	 *            the long value
	 * @return <tt>round<sub>DOWN</sub>(value)</tt>
	 */
	public static final float longToFloat(DecimalArithmetic arith, long value) {
		return unscaledToFloat(arith, DecimalRounding.DOWN, value);
	}

	/**
	 * Converts the specified long value to a float rounding extra mantissa digits if necessary.
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
	public static final float longToFloat(DecimalArithmetic arith, DecimalRounding rounding, long value) {
		if (rounding == DecimalRounding.HALF_EVEN) {
			return value;
		}
		return unscaledToFloat(arith, rounding, value);
	}

	/**
	 * Converts the specified unscaled decimal value to a float truncating extra precision digits if necessary.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param unscaled
	 *            the unscaled decimal value
	 * @return <tt>round<sub>DOWN</sub>(value)</tt>
	 */
	public static final float unscaledToFloat(DecimalArithmetic arith, long unscaled) {
		return unscaledToFloat(arith, DecimalRounding.DOWN, unscaled);
	}

	/**
	 * Converts the specified unscaled decimal value to a float rounding extra precision digits if necessary.
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
	public static final float unscaledToFloat(DecimalArithmetic arith, DecimalRounding rounding, long unscaled) {
		if (unscaled == 0) {
			return 0;
		}
		if (rounding == DecimalRounding.HALF_EVEN) {
			return (float)DoubleConversion.unscaledToDouble(arith, rounding, unscaled);
		}

		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final long absUnscaled = Math.abs(unscaled);
		
		// eliminate sign and trailing power-of-2 zero bits
		final int pow2 = Long.numberOfTrailingZeros(absUnscaled);
		final long absVal = absUnscaled >>> pow2;
		final int nlzAbsVal = Long.numberOfLeadingZeros(absVal);

		/*
		 * 1) we align absVal and factor such that: 2*factor > absVal >= factor then the division 
		 *    absVal/factor == 1.xxxxx, i.e. it is normalized 
		 * 2) because we omit the 1 in the mantissa, we calculate 
		 *    valModFactor = absVal - floor(absVal/factor)*factor = absVal - 1*factor 
		 * 3) we shift valModFactor such that the 1 from the division would be on bit 24 
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
				// if absVal < scaledFactor we shift by 1 (right shift of scaledFactor to avoid overflow)
				valModFactor = absVal - (scaledFactor >>> 1);
				mantissaShift = SIGNIFICAND_BITS + alignShift + 1;
			} else {
				exp = -alignShift;
				valModFactor = absVal - scaledFactor;
				mantissaShift = SIGNIFICAND_BITS + alignShift;
			}
		}
		if (rounding == DecimalRounding.DOWN) {
			return unscaledToFloatShiftAndDivideByScaleFactor(scaleMetrics, unscaled, exp + pow2, mantissaShift,
					valModFactor);
		}
		// (3) + (4)
		return unscaledToFloatShiftAndDivideByScaleFactor(scaleMetrics, rounding, unscaled, exp + pow2, mantissaShift,
				valModFactor);
	}

	private static final float unscaledToFloatShiftAndDivideByScaleFactor(ScaleMetrics scaleMetrics, long unscaled, int exp, int mantissaShift, long valModFactor) {
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
		final int signBit = (int) ((unscaled >>> 32) & SIGN_MASK);
		final int raw = signBit | ((exp + EXPONENT_BIAS) << SIGNIFICAND_BITS) | (int) (quot & SIGNIFICAND_MASK);
		return Float.intBitsToFloat(raw);
	}

	private static final float unscaledToFloatShiftAndDivideByScaleFactor(ScaleMetrics scaleMetrics, DecimalRounding rounding, long unscaled, int exp, int mantissaShift, long valModFactor) {
		final long quotient;
		final long scaleFactor = scaleMetrics.getScaleFactor();
		if (mantissaShift >= 0) {
			final long hValModFactor = (valModFactor >>> (Long.SIZE - mantissaShift)) & (-mantissaShift >> 63);
			final long lValModFactor = valModFactor << mantissaShift;
			if (hValModFactor == 0) {
				final long truncated = scaleMetrics.divideUnsignedByScaleFactor(lValModFactor);
				final long remainder = applySign(unscaled, lValModFactor - scaleMetrics.multiplyByScaleFactor(truncated));
				quotient = truncated
						+ Math.abs(Rounding.calculateRoundingIncrementForDivision(rounding, truncated, remainder, scaleFactor));
			} else {
				quotient = Math.abs(Div.div128by64(rounding, unscaled < 0, hValModFactor, lValModFactor, scaleFactor));
				// rounding already done by div128by64
			}
		} else {
			final long scaledVal = valModFactor >>> -mantissaShift;
			final long truncated = scaleMetrics.divideByScaleFactor(scaledVal);
			final long remainder = applySign(unscaled, ((scaledVal - scaleMetrics.multiplyByScaleFactor(truncated)) << -mantissaShift)
					| (valModFactor & (-1L >>> (Long.SIZE + mantissaShift))));
			// NOTE: below shift can overflow as min(mantissaShift)=-39 for scale=1, -38 for scale=10, ..., -21 for scale=10^18
			//		 hence we use MAX_VALUE in this case, should always be more than 2x remainder (which is good enough for HALF_UP etc)
			final long shiftedScaleFactor = -mantissaShift >= scaleMetrics.getScaleFactorNumberOfLeadingZeros() ? Long.MAX_VALUE : scaleFactor << -mantissaShift;
			quotient = truncated + Math.abs(Rounding.calculateRoundingIncrementForDivision(rounding, truncated, remainder,
					shiftedScaleFactor));
		}
		final int raw;
		final int signBit = (int) ((unscaled >>> 32) & SIGN_MASK);
		if (quotient <= SIGNIFICAND_MASK) {
			raw = signBit | ((exp + EXPONENT_BIAS) << SIGNIFICAND_BITS) | (int) (quotient & SIGNIFICAND_MASK);
		} else {
			// rounding made our value to be 1 instead of smaller than one. 1 + 1 == 2 i.e. our mantissa is zero due to
			// the implicit 1 and our exponent increments by 1
			raw = signBit | ((exp + 1 + EXPONENT_BIAS) << SIGNIFICAND_BITS);
		}
		return Float.intBitsToFloat(raw);
	}

	// @return value % (2^n)
	private static final long modPow2(long value, int n) {
		// return value & ((1L << n) - 1);
		return value & (-1L >>> (Long.SIZE - n)) & (-n >> 31);// last bracket is for case n=0
	}

	private static final long applySign(final long signed, final long value) {
		return signed >= 0 ? value : -value;
	}

	private static final boolean isInLongRange(float value) {
		return MIN_LONG_AS_FLOAT - value < 1.0f & value < MAX_LONG_AS_FLOAT_PLUS_ONE;
	}

	private static final boolean isMathematicalInteger(float x) {
		return isFinite(x) && (x == 0.0f
				|| SIGNIFICAND_BITS - Long.numberOfTrailingZeros(getSignificand(x)) <= Math.getExponent(x));
	}

	private static final boolean isFinite(float f) {
		return Math.abs(f) <= Float.MAX_VALUE;
	}

	// PRECONDITION: isFinite(d)
	private static final int getSignificand(float f) {
		final int exponent = Math.getExponent(f);
		int bits = Float.floatToRawIntBits(f);
		bits &= SIGNIFICAND_MASK;
		return (exponent == Float.MIN_EXPONENT - 1) ? bits << 1 : bits | IMPLICIT_BIT;
	}

	/**
	 * Returns the {@code float} value that is closest in value to the argument and is equal to a mathematical integer.
	 * If two {@code float} values that are mathematical integers are equally close to the value of the argument, the
	 * result is the integer value that is even. Special cases:
	 * <ul>
	 * <li>If the argument value is already equal to a mathematical integer, then the result is the same as the
	 * argument.
	 * <li>If the argument is NaN or an infinity or positive zero or negative zero, then the result is the same as the
	 * argument.
	 * </ul>
	 *
	 * @param a
	 *            a value.
	 * @return the closest floating-point value to {@code a} that is equal to a mathematical integer.
	 * @author Joseph D. Darcy
	 */
	private static final float rint(float a) {
		/*
		 * If the absolute value of a is not less than 2^23, it is either a finite integer (the float format does not
		 * have enough significand bits for a number that large to have any fractional portion), an infinity, or a NaN.
		 * In any of these cases, rint of the argument is the argument.
		 *
		 * Otherwise, the sum (twoToThe23 + a ) will properly round away any fractional portion of a since
		 * ulp(twoToThe23) == 1.0; subtracting out twoToThe23 from this sum will then be exact and leave the rounded
		 * integer portion of a.
		 *
		 * This method does *not* need to be declared strictfp to get fully reproducible results. Whether or not a
		 * method is declared strictfp can only make a difference in the returned result if some operation would
		 * overflow or underflow with strictfp semantics. The operation (twoToThe23 + a ) cannot overflow since large
		 * values of a are screened out; the add cannot underflow since twoToThe23 is too large. The subtraction
		 * ((twoToThe23 + a ) - twoToThe23) will be exact as discussed above and thus cannot overflow or meaningfully
		 * underflow. Finally, the last multiply in the return statement is by plus or minus 1.0, which is exact too.
		 */
		float twoToThe23 = (float) (1L << 23); // 2^23
		float sign = Math.copySign(1.0f, a); // preserve sign info
		a = Math.abs(a);

		if (a < twoToThe23) { // E_min <= ilogb(a) <= 51
			a = ((twoToThe23 + a) - twoToThe23);
		}

		return sign * a; // restore original sign
	}

	private static final IllegalArgumentException newOverflowException(final DecimalArithmetic arith, double value) {
		return new IllegalArgumentException(
				"Overflow for conversion from float to decimal with scale " + arith.getScale() + ": " + value);
	}

	// no instances
	private FloatConversion() {
		super();
	}
}
