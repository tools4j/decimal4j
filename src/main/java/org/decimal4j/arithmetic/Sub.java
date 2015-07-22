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
import org.decimal4j.scale.Scale0f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;

/**
 * Provides static methods to calculate subtractions.
 */
final class Sub {

	/**
	 * Calculates unchecked unrounded subtraction of a long value and an unscaled
	 * value with the given scale.
	 * 
	 * @param lValue
	 *            the long value
	 * @param unscaled
	 *            the unscaled value
	 * @param scale
	 *            the scale of the second value
	 * @return the subtraction result without rounding and without overflow checks
	 */
	public static final long subtractLongUnscaled(long lValue, long unscaled, int scale) {
		return subtractUnscaledUnscaled(Scale0f.INSTANCE, lValue, unscaled, scale);
	}

	/**
	 * Calculates unchecked rounded subtraction of a long value and an unscaled
	 * value with the given scale.
	 * 
	 * @param rounding
	 *            the rounding to apply
	 * @param lValue
	 *            the long value
	 * @param unscaled
	 *            the unscaled value
	 * @param scale
	 *            the scale of the second value
	 * @return the subtraction result with rounding but without overflow checks
	 */
	public static final long subtractLongUnscaled(DecimalRounding rounding, long lValue, long unscaled, int scale) {
		return subtractUnscaledUnscaled(Scale0f.INSTANCE, rounding, lValue, unscaled, scale);
	}

	/**
	 * Calculates unchecked subtraction of an unscaled value and a long value.
	 * 
	 * @param arith
	 *            the arithmetic associated with the first value
	 * @param uDecimal
	 *            the unscaled value
	 * @param lValue
	 *            the long value
	 * @return the subtraction result without overflow checks
	 */
	public static final long subtractUnscaledLong(DecimalArithmetic arith, long uDecimal, long lValue) {
		return uDecimal - Pow10.multiplyByPowerOf10(lValue, arith.getScale());
	}

	/**
	 * Calculates checked subtraction of an unscaled value and a long value.
	 * 
	 * @param arith
	 *            the arithmetic associated with the first value
	 * @param uDecimal
	 *            the unscaled value
	 * @param lValue
	 *            the long value
	 * @return the subtraction result performed with overflow checks
	 */
	public static final long subtractUnscaledLongChecked(DecimalArithmetic arith, long uDecimal, long lValue) {
		final int scale = arith.getScale();
		if (lValue == 0 | scale == 0) {
			return arith.subtract(uDecimal, lValue);
		}
		try {
			return subtractForNegativeScaleDiff(arith, uDecimal, lValue, -scale);
		} catch (ArithmeticException e) {
			throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + arith.toString(uDecimal) + " - " + lValue, e);
		}
	}

	/**
	 * Calculates unchecked unrounded subtraction of an unscaled value and another
	 * unscaled value with the given {@code scaleMetrics} and {@code scale},
	 * respectively.
	 * 
	 * @param scaleMetrics
	 *            the scaleMetrics associated with the first value
	 * @param uDecimal
	 *            the first unscaled value
	 * @param unscaled
	 *            the second unscaled value
	 * @param scale
	 *            the scale of the second value
	 * @return the subtraction result without rounding and without overflow checks
	 */
	public static final long subtractUnscaledUnscaled(ScaleMetrics scaleMetrics, long uDecimal, long unscaled, int scale) {
		if (scale > Scales.MAX_SCALE) {
			throw new IllegalArgumentException("Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale);
		}
		final int scaleDiff = scale - scaleMetrics.getScale();
		if (unscaled == 0 | scaleDiff == 0) {
			return uDecimal - unscaled;
		} else if (scaleDiff < 0) {
			return uDecimal - Pow10.divideByPowerOf10(unscaled, scaleDiff);//multiplication
		}
		return subtractForPositiveScaleDiff(uDecimal, unscaled, scaleDiff);
	}

	/**
	 * Calculates unchecked rounded subtraction of an unscaled value and another
	 * unscaled value with the given {@code scaleMetrics} and {@code scale},
	 * respectively.
	 * 
	 * @param scaleMetrics
	 *            the scaleMetrics associated with the first value
	 * @param rounding
	 *            the rounding to apply
	 * @param uDecimal
	 *            the first unscaled value
	 * @param unscaled
	 *            the second unscaled value
	 * @param scale
	 *            the scale of the second value
	 * @return the subtraction result with rounding but without overflow checks
	 */
	public static final long subtractUnscaledUnscaled(ScaleMetrics scaleMetrics, DecimalRounding rounding, long uDecimal, long unscaled, int scale) {
		if (scale > Scales.MAX_SCALE) {
			throw new IllegalArgumentException("Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale);
		}
		final int scaleDiff = scale - scaleMetrics.getScale();
		if (unscaled == 0 | scaleDiff == 0) {
			return uDecimal - unscaled;
		} else if (scaleDiff < 0) {
			return uDecimal - Pow10.divideByPowerOf10(unscaled, scaleDiff);//multiplication
		}
		//scale > 0
		return subtractForPositiveScaleDiff(rounding, uDecimal, unscaled, scaleDiff);
	}

	/**
	 * Calculates checked unrounded subtraction of an unscaled value and another
	 * unscaled value with the given {@code scaleMetrics} and {@code scale},
	 * respectively.
	 * 
	 * @param arith
	 *            the arithmetic associated with the first value
	 * @param uDecimal
	 *            the first unscaled value
	 * @param unscaled
	 *            the second unscaled value
	 * @param scale
	 *            the scale of the second value
	 * @return the subtraction result without rounding but with overflow checks
	 */
	public static final long subtractUnscaledUnscaledChecked(DecimalArithmetic arith, long uDecimal, long unscaled, int scale) {
		if (scale > Scales.MAX_SCALE) {
			throw new IllegalArgumentException("Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale);
		}
		final int scaleDiff = scale - arith.getScale();
		if (unscaled == 0 | scaleDiff == 0) {
			return arith.subtract(uDecimal, unscaled);
		} else if (scaleDiff < 0) {
			try {
				return subtractForNegativeScaleDiff(arith, uDecimal, unscaled, scaleDiff);
			} catch (ArithmeticException e) {
				throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + arith.toString(uDecimal) + " - " + unscaled + "*10^" + (-scale), e);
			}
		}
		final long diff = subtractForPositiveScaleDiff(uDecimal, unscaled, scaleDiff);
		if (!Checked.isSubtractOverflow(uDecimal, unscaled, diff)) {
			return diff;
		}
		throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " - " + unscaled + "*10^" + (-scale) + "=" + diff);
	}
	
	/**
	 * Calculates checked rounded subtraction of an unscaled value and another
	 * unscaled value with the given {@code arith} and {@code scale},
	 * respectively.
	 * 
	 * @param arith
	 *            the arithmetic associated with the first value
	 * @param rounding
	 *            the rounding to apply
	 * @param uDecimal
	 *            the first unscaled value
	 * @param unscaled
	 *            the second unscaled value
	 * @param scale
	 *            the scale of the second value
	 * @return the subtraction result with rounding and overflow checks
	 */
	public static final long subtractUnscaledUnscaledChecked(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal, long unscaled, int scale) {
		if (scale > Scales.MAX_SCALE) {
			throw new IllegalArgumentException("Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale);
		}
		final int scaleDiff = scale - arith.getScale();
		if (unscaled == 0 | scaleDiff == 0) {
			return arith.subtract(uDecimal, unscaled);
		} else if (scaleDiff < 0) {
			try {
				return subtractForNegativeScaleDiff(arith, uDecimal, unscaled, scaleDiff);
			} catch (ArithmeticException e) {
				throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + arith.toString(uDecimal) + " - " + unscaled + "*10^" + (-scale), e);
			}
		}
		final long diff = subtractForPositiveScaleDiff(rounding, uDecimal, unscaled, scaleDiff);
		if (!Checked.isSubtractOverflow(uDecimal, unscaled, diff)) {
			return diff;
		}
		throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " - " + unscaled + "*10^" + (-scale) + "=" + diff);
	}

	/**
	 * Calculates unchecked unrounded subtraction of an unscaled value and another
	 * unscaled value with the given {@code scaleDiff=scale2-scale1 > 0}.
	 * 
	 * @param uDecimal
	 *            the first unscaled value
	 * @param unscaled
	 *            the second unscaled value
	 * @param scaleDiff
	 *            scale2 - scale1, must be positive
	 * @return the subtraction result without rounding and without overflow checks
	 */
	//PRECONDITION: scaleDiff > 0
	private static final long subtractForPositiveScaleDiff(long uDecimal, long unscaled, int scaleDiff) {
		//scaleDiff > 0
		final ScaleMetrics diffMetrics = Scales.getScaleMetrics(scaleDiff);
		final long trunc = diffMetrics.divideByScaleFactor(unscaled);
		final long diff = uDecimal - trunc;
		if (uDecimal == 0 | diff == 0 | (uDecimal ^ unscaled) < 0 | (diff ^ unscaled) < 0) { 
			return diff;
		}
		final long remainder = unscaled - diffMetrics.multiplyByScaleFactor(trunc);
		return diff - Long.signum(remainder);
	}

	/**
	 * Calculates unchecked rounded subtraction of an unscaled value and another
	 * unscaled value with the given {@code scaleDiff=scale2-scale1 > 0}.
	 * 
	 * @param rounding
	 *            the rounding to apply
	 * @param uDecimal
	 *            the first unscaled value
	 * @param unscaled
	 *            the second unscaled value
	 * @param scaleDiff
	 *            scale2 - scale1, must be positive
	 * @return the subtraction result with rounding but without overflow checks
	 */
	//PRECONDITION: scaleDiff > 0
	private static final long subtractForPositiveScaleDiff(DecimalRounding rounding, long uDecimal, long unscaled, int scaleDiff) {
		//scaleDiff > 0
		final ScaleMetrics diffMetrics = Scales.getScaleMetrics(scaleDiff);
		final long trunc = diffMetrics.divideByScaleFactor(unscaled);
		final long remainder = unscaled - diffMetrics.multiplyByScaleFactor(trunc);
		final long diff = uDecimal - trunc;
		if (uDecimal == 0 | diff == 0 | (uDecimal ^ unscaled) < 0 | (diff ^ unscaled) < 0) { 
			return diff + Rounding.calculateRoundingIncrement(rounding, diff, -remainder, diffMetrics.getScaleFactor());
		}
		return diff + Rounding.calculateRoundingIncrement(RoundingInverse.ADDITIVE_REVERSION.invert(rounding), diff, -remainder, diffMetrics.getScaleFactor());
	}

	/**
	 * Calculates checked subtraction of an unscaled value and another
	 * unscaled value with the given {@code scaleDiff = scal2 - scale1 > 0} which must be negative, such that the subtracted
	 * value can be rescaled through multiplication.
	 * 
	 * @param arith
	 *            the arithmetic associated with the first value
	 * @param uDecimal
	 *            the first unscaled value
	 * @param unscaled
	 *            the second unscaled value
	 * @param scaleDiff
	 *            the scale of the second value
	 * @return the subtraction result with overflow checks
	 */
	//PRECONDITION: scaleDiff < 0
	private static final long subtractForNegativeScaleDiff(DecimalArithmetic arith, long uDecimal, long unscaled, int scaleDiff) {
		//NOTE: multiplication by power of 10 may lead to an overflow but the result may still be valid if signs are same
		//		--> therefore we multiply only half of the value with pow10 and subtract it twice
		//		--> then we subtract the remainder 1 (x pow10) if the value was odd (again in halves to avoid overflow)
		final long half = Pow10.divideByPowerOf10Checked(arith, unscaled / 2, scaleDiff);//multiplication;
		final long halfReminder = ((unscaled & 0x1) == 0) ? 0 : Pow10.divideByPowerOf10Checked(arith, unscaled > 0 ? 5 : -5, scaleDiff + 1);
		long result = uDecimal;
		result = arith.subtract(result, half);
		result = arith.subtract(result, half);
		result = arith.subtract(result, halfReminder);
		result = arith.subtract(result, halfReminder);
		return result;
	}

	// no instances
	private Sub() {
		super();
	}
}
