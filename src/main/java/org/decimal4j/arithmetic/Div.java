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
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;

/**
 * Provides static methods to calculate division results.
 */
final class Div {

	private static final long LONG_MASK = 0xffffffffL;

	/**
	 * Calculates unchecked division by a long value with rounding.
	 * 
	 * @param rounding
	 *            the decimal rounding to apply if rounding is necessary
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param lDivisor
	 *            the long divisor
	 * @return the division result with rounding and no overflow checks
	 */
	public static final long divideByLong(DecimalRounding rounding, long uDecimalDividend, long lDivisor) {
		final long quotient = uDecimalDividend / lDivisor;
		final long remainder = uDecimalDividend - quotient * lDivisor;
		return quotient + Rounding.calculateRoundingIncrementForDivision(rounding, quotient, remainder, lDivisor);
	}

	/**
	 * Calculates checked division by a long value with rounding.
	 * 
	 * @param arith
	 *            the arithmetic used to format numbers when throwing exceptions
	 * @param rounding
	 *            the decimal rounding to apply if rounding is necessary
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param lDivisor
	 *            the long divisor
	 * @return the division result with rounding and overflow checks
	 */
	public static final long divideByLongChecked(DecimalArithmetic arith, DecimalRounding rounding, long uDecimalDividend, long lDivisor) {
		if (lDivisor == 0) {
			throw new ArithmeticException("Division by zero: " + arith.toString(uDecimalDividend) + " / " + lDivisor);
		}
		try {
			final long quotient = Checked.divideByLong(arith, uDecimalDividend, lDivisor);
			final long remainder = uDecimalDividend - quotient * lDivisor;
			final long inc = Rounding.calculateRoundingIncrementForDivision(rounding, quotient, remainder, lDivisor);
			return Checked.add(arith, quotient, inc);
		} catch (ArithmeticException e) {
			Exceptions.rethrowIfRoundingNecessary(e);
			throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + arith.toString(uDecimalDividend) + " / "
					+ lDivisor, e);
		}
	}

	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * without rounding and overflow checks.
	 * 
	 * @param arith
	 *            the arithmetic with scale metrics and overflow mode
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * @return the division result without rounding and without overflow checks.
	 */
	public static final long divide(DecimalArithmetic arith, long uDecimalDividend, long uDecimalDivisor) {
		// special cases first
		final SpecialDivisionResult special = SpecialDivisionResult.getFor(arith, uDecimalDividend, uDecimalDivisor);
		if (special != null) {
			return special.divide(arith, uDecimalDividend, uDecimalDivisor);
		}
		// div by power of 10
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final ScaleMetrics pow10 = Scales.findByScaleFactor(Math.abs(uDecimalDivisor));
		if (pow10 != null) {
			return Pow10.divideByPowerOf10(uDecimalDividend, scaleMetrics, uDecimalDivisor > 0, pow10);
		}
		return divide(uDecimalDividend, scaleMetrics, uDecimalDivisor);
	}

	/**
	 * Calculates unchecked division by an unscaled value with the given scale
	 * without rounding and overflow checks.
	 * 
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param unscaledDivisor
	 *            the long divisor
	 * @param scale
	 *            the scale of the divisor
	 * @return the division result without rounding and without overflow checks
	 */
	public static final long divideByUnscaled(long uDecimalDividend, long unscaledDivisor, int scale) {
		if (scale > Scales.MAX_SCALE) {
			throw new IllegalArgumentException("Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale);
		}
		if (unscaledDivisor == 0 | scale == 0) {
			return uDecimalDividend / unscaledDivisor;
		} else if (scale < 0) {
			if (Checked.isDivideOverflow(uDecimalDividend, unscaledDivisor)) {
				return -Pow10.multiplyByPowerOf10(uDecimalDividend, scale);
			}
			return Pow10.multiplyByPowerOf10(uDecimalDividend / unscaledDivisor, scale);
		}
		final ScaleMetrics divisorMetrics = Scales.getScaleMetrics(scale);
		return divide(uDecimalDividend, divisorMetrics, unscaledDivisor);
	}

	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * without rounding and overflow checks.
	 * 
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param divisorMetrics
	 *            the metrics associated with the divisor
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * @return the division result without rounding and without overflow checks.
	 */
	private static final long divide(long uDecimalDividend, ScaleMetrics divisorMetrics, long uDecimalDivisor) {
		// WE WANT: uDecimalDividend * 10^scale / unscaledDivisor
		if (divisorMetrics.isValidIntegerValue(uDecimalDividend)) {
			// just do it, multiplication result fits in long
			return divisorMetrics.multiplyByScaleFactor(uDecimalDividend) / uDecimalDivisor;
		}
		if (divisorMetrics.isValidIntegerValue(uDecimalDivisor)) {
			// perform component wise division (reminder fits in long after scaling)
			final long integralPart = uDecimalDividend / uDecimalDivisor;
			final long remainder = uDecimalDividend - integralPart * uDecimalDivisor;
			final long fractionalPart = divisorMetrics.multiplyByScaleFactor(remainder) / uDecimalDivisor;
			return divisorMetrics.multiplyByScaleFactor(integralPart) + fractionalPart;
		}
		return scaleTo128divBy64(divisorMetrics, DecimalRounding.DOWN, uDecimalDividend, uDecimalDivisor);
	}

	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * with rounding and without overflow checks.
	 * 
	 * @param arith
	 *            the arithmetic with scale metrics and overflow mode
	 * @param rounding
	 *            the decimal rounding to apply if rounding is necessary
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * @return the division result with rounding and without overflow checks
	 */
	public static final long divide(DecimalArithmetic arith, DecimalRounding rounding, long uDecimalDividend, long uDecimalDivisor) {
		// special cases first
		final SpecialDivisionResult special = SpecialDivisionResult.getFor(arith, uDecimalDividend, uDecimalDivisor);
		if (special != null) {
			return special.divide(arith, uDecimalDividend, uDecimalDivisor);
		}
		// div by power of 10
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final ScaleMetrics pow10 = Scales.findByScaleFactor(Math.abs(uDecimalDivisor));
		if (pow10 != null) {
			return Pow10.divideByPowerOf10(rounding, uDecimalDividend, scaleMetrics, uDecimalDivisor > 0, pow10);
		}
		return divide(rounding, uDecimalDividend, scaleMetrics, uDecimalDivisor);
	}

	/**
	 * Calculates unchecked division by an unscaled value with the given scale
	 * without rounding.
	 * 
	 * @param rounding
	 *            the rounding to apply
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param unscaledDivisor
	 *            the long divisor
	 * @param scale
	 *            the scale of the divisor
	 * @return the division result without rounding and without overflow checks
	 */
	public static final long divideByUnscaled(DecimalRounding rounding, long uDecimalDividend, long unscaledDivisor, int scale) {
		if (scale > Scales.MAX_SCALE) {
			throw new IllegalArgumentException("Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale);
		}
		if (unscaledDivisor == 0 | scale == 0) {
			return divideByLong(rounding, uDecimalDividend, unscaledDivisor);
		} else if (scale < 0) {
			if (Checked.isDivideOverflow(uDecimalDividend, unscaledDivisor)) {
				return -Pow10.multiplyByPowerOf10(RoundingInverse.SIGN_REVERSION.invert(rounding), uDecimalDividend, scale);
			}
			//NOTE: rounding twice could be a problem here, e.g. consider HALF_UP with 10.51 and 10.49
			final long quot;
			switch (rounding) {
			case HALF_UP:
				quot = uDecimalDividend / unscaledDivisor;//DOWN
				break;
			case HALF_DOWN:
				quot = divideByLong(DecimalRounding.UP, uDecimalDividend, unscaledDivisor);
				break;
			case HALF_EVEN: {
				//try HALF_UP first
				final long quotD = uDecimalDividend / unscaledDivisor;//DOWN
				final long powHU = Pow10.multiplyByPowerOf10(DecimalRounding.HALF_UP, quotD, scale);
				if (0 == (powHU & 0x1)) {
					//even, we're done
					return powHU;
				}
				//odd, HALF_DOWN may be even in which case it should win
				final long quotU = divideByLong(DecimalRounding.UP, uDecimalDividend, unscaledDivisor);
				final long powHD = Pow10.multiplyByPowerOf10(DecimalRounding.HALF_DOWN, quotU, scale);
				return powHD;//either even or the same as powHU
			}
			default:
				quot = divideByLong(rounding, uDecimalDividend, unscaledDivisor);
				break;
			}
			return Pow10.multiplyByPowerOf10(rounding, quot, scale);
		}
		final ScaleMetrics divisorMetrics = Scales.getScaleMetrics(scale);
		return divide(rounding, uDecimalDividend, divisorMetrics, unscaledDivisor);
	}

	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * with rounding and without overflow checks.
	 * 
	 * @param rounding
	 *            the decimal rounding to apply if rounding is necessary
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param divisorMetrics
	 *            the metrics associated with the divisor
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * @return the division result with rounding and without overflow checks
	 */
	private static final long divide(DecimalRounding rounding, long uDecimalDividend, ScaleMetrics divisorMetrics, long uDecimalDivisor) {
		if (divisorMetrics.isValidIntegerValue(uDecimalDividend)) {
			// just do it, multiplication result fits in long
			final long scaledDividend = divisorMetrics.multiplyByScaleFactor(uDecimalDividend);
			final long quot = scaledDividend / uDecimalDivisor;
			final long rem = scaledDividend - quot * uDecimalDivisor;
			return quot + Rounding.calculateRoundingIncrementForDivision(rounding, quot, rem, uDecimalDivisor);
		}
		if (divisorMetrics.isValidIntegerValue(uDecimalDivisor)) {
			// perform component wise division (reminder fits in long after
			// scaling)
			final long integralPart = uDecimalDividend / uDecimalDivisor;
			final long remainder = uDecimalDividend - integralPart * uDecimalDivisor;
			final long scaledReminder = divisorMetrics.multiplyByScaleFactor(remainder);
			final long fractionalPart = scaledReminder / uDecimalDivisor;
			final long subFractionalPart = scaledReminder - fractionalPart * uDecimalDivisor;
			final long truncated = divisorMetrics.multiplyByScaleFactor(integralPart) + fractionalPart;
			return truncated + Rounding.calculateRoundingIncrementForDivision(rounding, truncated, subFractionalPart, uDecimalDivisor);
		}
		return Div.scaleTo128divBy64(divisorMetrics, rounding, uDecimalDividend, uDecimalDivisor);
	}

	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * without rounding and with overflow checks.
	 * 
	 * @param arith
	 *            the arithmetic with scale metrics and overflow mode
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * @return the division result without rounding and with overflow checks
	 */
	public static final long divideChecked(DecimalArithmetic arith, long uDecimalDividend, long uDecimalDivisor) {
		// special cases first
		final SpecialDivisionResult special = SpecialDivisionResult.getFor(arith, uDecimalDividend, uDecimalDivisor);
		if (special != null) {
			return special.divide(arith, uDecimalDividend, uDecimalDivisor);
		}
		// div by power of 10
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final ScaleMetrics pow10 = Scales.findByScaleFactor(Math.abs(uDecimalDivisor));
		if (pow10 != null) {
			return Pow10.divideByPowerOf10Checked(arith, uDecimalDividend, scaleMetrics, uDecimalDivisor > 0, pow10);
		}
		return divideChecked(scaleMetrics, uDecimalDividend, scaleMetrics, uDecimalDivisor);
	}

	/**
	 * Calculates unchecked division by an unscaled value with the given scale
	 * without rounding and with overflow checks.
	 * 
	 * @param arith
	 *            the arithmetic associated with the dividend
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param unscaledDivisor
	 *            the long divisor
	 * @param scale
	 *            the scale of the divisor
	 * @return the division result without rounding and with overflow checks
	 */
	public static final long divideByUnscaledChecked(DecimalArithmetic arith, long uDecimalDividend, long unscaledDivisor, int scale) {
		if (scale > Scales.MAX_SCALE) {
			throw new IllegalArgumentException("Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale);
		}
		if (uDecimalDividend == 0 & unscaledDivisor != 0) {
			return 0;
		} else if (scale == 0) {
			return Checked.divideByLong(arith, uDecimalDividend, unscaledDivisor);
		} else if (scale < 0) {
			if (Checked.isDivideOverflow(uDecimalDividend, unscaledDivisor)) {
				return -Pow10.multiplyByPowerOf10Checked(arith, uDecimalDividend, scale);
			}
			return Pow10.multiplyByPowerOf10Checked(arith, uDecimalDividend / unscaledDivisor, scale);
		}
		final ScaleMetrics divisorMetrics = Scales.getScaleMetrics(scale);
		return divideChecked(arith.getScaleMetrics(), uDecimalDividend, divisorMetrics, unscaledDivisor);
	}

	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * without rounding and with overflow checks.
	 * 
	 * @param dividendMetrics
	 *            the metrics associated with the dividend
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param divisorMetrics
	 *            the scale metrics associated with the divisor
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * @return the division result without rounding and with overflow checks
	 */
	private static final long divideChecked(ScaleMetrics dividendMetrics, long uDecimalDividend, ScaleMetrics divisorMetrics, long uDecimalDivisor) {
		try {
			// WE WANT: uDecimalDividend * 10^divisorScale / unscaledDivisor
			if (divisorMetrics.isValidIntegerValue(uDecimalDividend)) {
				// just do it, multiplication result fits in long (division can only overflow for scale=1)
				return divisorMetrics.multiplyByScaleFactor(uDecimalDividend) / uDecimalDivisor;
			}
			// perform component wise division
			final long integralPart = Checked.divideLong(uDecimalDividend, uDecimalDivisor);
			final long remainder = uDecimalDividend - integralPart * uDecimalDivisor;
			final long fractionalPart;
			if (divisorMetrics.isValidIntegerValue(remainder)) {
				// scaling and result can't overflow because of the above condition
				fractionalPart = divisorMetrics.multiplyByScaleFactor(remainder) / uDecimalDivisor;
			} else {
				// result can't overflow because reminder is smaller than
				// divisor, i.e. -1 < result < 1
				fractionalPart = scaleTo128divBy64(divisorMetrics, DecimalRounding.DOWN, remainder, uDecimalDivisor);
			}
			return Checked.addLong(divisorMetrics.multiplyByScaleFactorExact(integralPart), fractionalPart);
		} catch (ArithmeticException e) {
			throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + dividendMetrics.toString(uDecimalDividend) + " / "
					+ divisorMetrics.toString(uDecimalDivisor), e);
		}
	}

	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * with rounding and with overflow checks.
	 * 
	 * @param arith
	 *            the arithmetic with scale metrics and overflow mode
	 * @param rounding
	 *            the decimal rounding to apply if rounding is necessary
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * @return the division result with rounding and with overflow checks
	 */
	public static final long divideChecked(DecimalArithmetic arith, DecimalRounding rounding, long uDecimalDividend, long uDecimalDivisor) {
		// special cases first
		final SpecialDivisionResult special = SpecialDivisionResult.getFor(arith, uDecimalDividend, uDecimalDivisor);
		if (special != null) {
			return special.divide(arith, uDecimalDividend, uDecimalDivisor);
		}
		// div by power of 10
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final ScaleMetrics pow10 = Scales.findByScaleFactor(Math.abs(uDecimalDivisor));
		if (pow10 != null) {
			return Pow10.divideByPowerOf10Checked(arith, rounding, uDecimalDividend, scaleMetrics, uDecimalDivisor > 0, pow10);
		}
		return divideChecked(rounding, scaleMetrics, uDecimalDividend, scaleMetrics, uDecimalDivisor);
	}

	/**
	 * Calculates unchecked division by an unscaled value with the given scale
	 * without rounding and with overflow checks.
	 * 
	 * @param arith
	 *            the arithmetic associated with the dividend
	 * @param rounding
	 *            the ronuding to apply
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param unscaledDivisor
	 *            the long divisor
	 * @param scale
	 *            the scale of the divisor
	 * @return the division result without rounding and with overflow checks
	 */
	public static final long divideByUnscaledChecked(DecimalArithmetic arith, DecimalRounding rounding, long uDecimalDividend, long unscaledDivisor, int scale) {
		if (scale > Scales.MAX_SCALE) {
			throw new IllegalArgumentException("Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale);
		}
		if (uDecimalDividend == 0 & unscaledDivisor != 0) {
			return 0;
		} else if (scale == 0) {
			return divideByLongChecked(arith, rounding, uDecimalDividend, unscaledDivisor);
		} else if (scale < 0) {
			if (Checked.isDivideOverflow(uDecimalDividend, unscaledDivisor)) {
				return -Pow10.multiplyByPowerOf10Checked(arith, RoundingInverse.SIGN_REVERSION.invert(rounding), uDecimalDividend, scale);
			}
			//NOTE: rounding twice could be a problem here, e.g. consider HALF_UP with 10.51 and 10.49
			final long quot;
			switch (rounding) {
			case HALF_UP:
				quot = divideByLongChecked(arith, DecimalRounding.DOWN, uDecimalDividend, unscaledDivisor);
				break;
			case HALF_DOWN:
				quot = divideByLongChecked(arith, DecimalRounding.UP, uDecimalDividend, unscaledDivisor);
				break;
			case HALF_EVEN: {
				//try HALF_UP first
				final long quotD = divideByLongChecked(arith, DecimalRounding.DOWN, uDecimalDividend, unscaledDivisor);
				final long powHU = Pow10.multiplyByPowerOf10Checked(arith, DecimalRounding.HALF_UP, quotD, scale);
				if (0 == (powHU & 0x1)) {
					//even, we're done
					return powHU;
				}
				//odd, HALF_DOWN may be even in which case it should win
				final long quotU = divideByLongChecked(arith, DecimalRounding.UP, uDecimalDividend, unscaledDivisor);
				final long powHD = Pow10.multiplyByPowerOf10Checked(arith, DecimalRounding.HALF_DOWN, quotU, scale);
				return powHD;//either even or the same as powHU
			}
			default:
				quot = divideByLongChecked(arith, rounding, uDecimalDividend, unscaledDivisor);
				break;
			}
			return Pow10.multiplyByPowerOf10Checked(arith, rounding, quot, scale);
		}
		final ScaleMetrics divisorMetrics = Scales.getScaleMetrics(scale);
		return divideChecked(rounding, arith.getScaleMetrics(), uDecimalDividend, divisorMetrics, unscaledDivisor);
	}

	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * with rounding and with overflow checks.
	 * 
	 * @param rounding
	 *            the ronuding to apply
	 * @param dividendMetrics
	 *            the matrics associated with the dividend
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param divisorMetrics
	 *            the scale metrics associated with the divisor
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * @return the division result with rounding and with overflow checks
	 */
	private static final long divideChecked(DecimalRounding rounding, ScaleMetrics dividendMetrics, long uDecimalDividend, ScaleMetrics divisorMetrics, long uDecimalDivisor) {
		try {
			// WE WANT: uDecimalDividend * 10^divisorScale / unscaledDivisor
			if (divisorMetrics.isValidIntegerValue(uDecimalDividend)) {
				// just do it, multiplication result fits in long
				final long scaledDividend = divisorMetrics.multiplyByScaleFactor(uDecimalDividend);
				final long quot = scaledDividend / uDecimalDivisor;//cannot overflow for scale>1
				final long rem = scaledDividend - quot * uDecimalDivisor;

				//cannot overflow for scale > 1 because of quot
				return quot + Rounding.calculateRoundingIncrementForDivision(rounding, quot, rem, uDecimalDivisor);
			}

			// perform component wise division
			final long integralPart = Checked.divideLong(uDecimalDividend, uDecimalDivisor);
			final long remainder = uDecimalDividend - integralPart * uDecimalDivisor;

			if (divisorMetrics.isValidIntegerValue(remainder)) {
				final long scaledReminder = divisorMetrics.multiplyByScaleFactor(remainder);
				final long fractionalPart = scaledReminder / uDecimalDivisor;//cannot overflow for scale>1
				final long subFractionalPart = scaledReminder - fractionalPart * uDecimalDivisor;

				final long result = Checked.addLong(divisorMetrics.multiplyByScaleFactorExact(integralPart), fractionalPart);
				final long inc = Rounding.calculateRoundingIncrementForDivision(rounding, result, subFractionalPart, uDecimalDivisor);
				return Checked.addLong(result, inc);
			} else {
				final long fractionalPart = Div.scaleTo128divBy64(divisorMetrics, rounding, remainder, uDecimalDivisor);
				return Checked.addLong(divisorMetrics.multiplyByScaleFactorExact(integralPart), fractionalPart);
			}
		} catch (ArithmeticException e) {
			Exceptions.rethrowIfRoundingNecessary(e);
			throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + dividendMetrics.toString(uDecimalDividend) + " / " + divisorMetrics.toString(uDecimalDivisor), e);
		}
	}

	/**
	 * Calculates {@code uDecimalDividend * scaleFactor / uDecimalDivisor} performing the multiplication into a 128 bit product
	 * and then performing a 128 by 64 bit division.
	 * 
	 * @param scaleMetrics		the metrics with scale factor to apply when scaling the dividend
	 * @param rounding			the rounding to apply if necessary
	 * @param uDecimalDividend	the dividend
	 * @param uDecimalDivisor	the divisor
	 * @return the unscaled decimal result of the division, rounded if necessary and overflow checked if 
	 */
	private static final long scaleTo128divBy64(ScaleMetrics scaleMetrics, DecimalRounding rounding, long uDecimalDividend, long uDecimalDivisor) {
		final boolean negative = (uDecimalDividend ^ uDecimalDivisor) < 0;
		final long absDividend = Math.abs(uDecimalDividend);
		final long absDivisor = Math.abs(uDecimalDivisor);

		// multiply by scale factor into a 128bit integer
		// HD + Knuth's Algorithm M from [Knu2] section 4.3.1.
		final int lFactor = (int) (absDividend & LONG_MASK);
		final int hFactor = (int) (absDividend >>> 32);
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

		// divide 128 bit product by 64 bit divisor
		final long hQuotient, lQuotient;
		if (Unsigned.isLess(hScaled, absDivisor)) {
			hQuotient = 0;
			lQuotient = div128by64(rounding, negative, hScaled, lScaled, absDivisor);
		} else {
			hQuotient = Unsigned.divide(hScaled, absDivisor);
			final long rem = hScaled - hQuotient * absDivisor;
			lQuotient = div128by64(rounding, negative, rem, lScaled, absDivisor);
		}
		return lQuotient;
	}

	/**
	 * PRECONDITION: Unsigned.isLess(u1, v0)
	 * <p>
	 * Divides a 128 bit divisor by a 64 bit dividend and returns the signed 64
	 * bit result. Rounding is applied if {@code rounding != null}, otherwise
	 * the value is truncated.
	 * <p>
	 * From <a
	 * href="http://www.codeproject.com/Tips/785014/UInt-Division-Modulus"
	 * >www.codeproject.com</a>.
	 * 
	 * @param neg
	 *            true if result is negative
	 * @param u1
	 *            high order 64 bits of dividend
	 * @param u0
	 *            low order 64 bits of dividend
	 * @param v0
	 *            64 bit divisor
	 * @param rounding
	 *            rounding to apply, or null to truncate result
	 * @return the signed quotient, rounded if {@code rounding != null}
	 */
	static final long div128by64(final DecimalRounding rounding, final boolean neg, final long u1, final long u0, final long v0) {
		final long q, r;

		final long un1, un0, vn1, vn0, un32, un21, un10;
		long q1, q0;

		final int s = Long.numberOfLeadingZeros(v0);

		final long v = v0 << s;
		vn1 = v >>> 32;
		vn0 = v & LONG_MASK;

		un32 = (u1 << s) | (u0 >>> (64 - s)) & (-s >> 63);
		un10 = u0 << s;

		un1 = un10 >>> 32;
		un0 = un10 & LONG_MASK;

		q1 = div128by64part(un32, un1, vn1, vn0);
		un21 = (un32 << 32) + (un1 - (q1 * v));
		q0 = div128by64part(un21, un0, vn1, vn0);
		q = (q1 << 32) | q0;

		// apply sign and rounding
		if (rounding == DecimalRounding.DOWN) {
			return neg ? -q : q;
		}

		r = ((un21 << 32) + un0 - q0 * v) >>> s;
		final TruncatedPart truncatedPart = Rounding.truncatedPartFor(Math.abs(r), v0);
		final int inc = rounding.calculateRoundingIncrement(neg ? -1 : 1, q, truncatedPart);
		return (neg ? -q : q) + inc;
	}

	private static final long div128by64part(final long unCB, final long unA, final long vn1, final long vn0) {
		// quotient and reminder, first guess
		long q = unsignedDiv64by32(unCB, vn1);
		long rhat = unCB - q * vn1;

		// correct, first attempt
		while (q > LONG_MASK) {
			q--;
			rhat += vn1;
			if (rhat > LONG_MASK) {
				return q;
			}
		}
		// correct, second attempt
		long left = q * vn0;
		long right = (rhat << 32) | unA;
		while (Unsigned.isGreater(left, right)) {
			q--;
			rhat += vn1;
			if (rhat > LONG_MASK) {
				return q;
			}
			left -= vn0;
			right = (rhat << 32) | unA;
		}
		return q;
	}

	/**
	 * Returns dividend / divisor, where the dividend and divisor are treated as
	 * unsigned 64-bit quantities.
	 * <p>
	 * From Guava's <a href=
	 * "http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/primitives/UnsignedLongs.html"
	 * >UnsignedLongs</a>.
	 *
	 * @param dividend
	 *            the dividend (numerator)
	 * @param divisor
	 *            the divisor (denominator)
	 * @return the unsigned quotient {@code dividend / divisor}
	 * @throws ArithmeticException
	 *             if divisor is 0
	 */
	private static final long unsignedDiv64by32(long dividend, long divisor) {
		// Optimization - use signed division if dividend < 2^63
		if (dividend >= 0) {
			return dividend / divisor;
		}
		// Optimization if divisor is even
		if (0 == (divisor & 0x1)) {
			return (dividend >>> 1) / (divisor >>> 1);
		}

		/*
		 * Otherwise, approximate the quotient, check, and correct if necessary.
		 * Our approximation is guaranteed to be either exact or one less than
		 * the correct value. This follows from fact that floor(floor(x)/i) ==
		 * floor(x/i) for any real x and integer i != 0. The proof is not quite
		 * trivial.
		 */
		final long quotient = ((dividend >>> 1) / divisor) << 1;
		final long rem = dividend - quotient * divisor;
		return quotient + (((rem >= divisor) | (rem < 0)) ? 1 : 0);
	}

	// no instances
	private Div() {
		super();
	}

}
