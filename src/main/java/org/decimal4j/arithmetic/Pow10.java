/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 decimal4j (tools4j), Marco Terzer
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
import org.decimal4j.scale.Scale18f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;

/**
 * Contains methods for multiplications and divisions with powers of ten.
 */
final class Pow10 {

	/**
	 * Calculates the multiple by a power of 10 truncating the result if
	 * necessary for negative {@code n}. Overflows are silently truncated.
	 * 
	 * @param uDecimal
	 *            the value to multiply
	 * @param n
	 *            the power-ten exponent
	 * @return <tt>round<sub>DOWN</sub>(uDecimal * 10<sup>n</sup>)</tt>
	 */
	public static final long multiplyByPowerOf10(final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		if (n > 0) {
			int pos = n;
			long result = uDecimal;
			// NOTE: result will be 0 after at most 1+64/18 rounds
			// because 10^64 contains 2^64 which is a shift left by 64
			while (pos > 18) {
				result = Scale18f.INSTANCE.multiplyByScaleFactor(result);
				if (result == 0) {
					return 0;
				}
				pos -= 18;
			}
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(pos);
			return scaleMetrics.multiplyByScaleFactor(result);
		} else {
			if (n >= -18) {
				final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-n);
				return scaleMetrics.divideByScaleFactor(uDecimal);
			}
			// truncated result is 0
			return 0;
		}
	}

	/**
	 * Calculates the multiple by a power of 10 rounding the result if necessary
	 * for negative {@code n}. Overflows are silently truncated.
	 * 
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param uDecimal
	 *            the value to multiply
	 * @param n
	 *            the power-ten exponent
	 * @return <tt>round(uDecimal * 10<sup>n</sup>)</tt>
	 */
	public static final long multiplyByPowerOf10(final DecimalRounding rounding, final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		if (n > 0) {
			int pos = n;
			long result = uDecimal;
			// NOTE: result will be 0 after at most 1+64/18 rounds
			// because 10^64 contains 2^64 which is a shift left by 64
			while (pos > 18) {
				result = Scale18f.INSTANCE.multiplyByScaleFactor(result);
				if (result == 0) {
					return 0;
				}
				pos -= 18;
			}
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(pos);
			return scaleMetrics.multiplyByScaleFactor(result);
		} else {
			if (n >= -18) {
				final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-n);
				final long truncated = scaleMetrics.divideByScaleFactor(uDecimal);
				final long rem = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated);
				final long inc = Rounding.calculateRoundingIncrement(rounding, truncated, rem,
						scaleMetrics.getScaleFactor());
				return truncated + inc;
			} else if (n == -19) {
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0,
						Rounding.truncatedPartForScale19(uDecimal));
			}
			// truncated part is always larger 0 (see first if)
			// and less than 0.5 because abs(Long.MIN_VALUE) / 10^20 < 0.5
			return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0,
					TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
	}

	/**
	 * Calculates the multiple by a power of 10 truncating the result if
	 * necessary for negative {@code n}. An exception is thrown if an overflow
	 * occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param uDecimal
	 *            the value to multiply
	 * @param n
	 *            the power-ten exponent
	 * @return <tt>round<sub>DOWN</sub>(uDecimal * 10<sup>n</sup>)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and the arithmetic's
	 *             {@code OverflowMode} is set to throw an exception
	 */
	public static final long multiplyByPowerOf10Checked(final DecimalArithmetic arith, final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}

		if (n > 0) {
			if (n > 18) {
				throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " * 10^" + n);
			}

			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(n);
			return scaleMetrics.multiplyByScaleFactorExact(uDecimal);
		} else {
			if (n >= -18) {
				final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-n);
				return scaleMetrics.divideByScaleFactor(uDecimal);
			}
			return 0;
		}
	}

	/**
	 * Calculates the multiple by a power of 10 rounding the result if necessary
	 * for negative {@code n}. An exception is thrown if an overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param uDecimal
	 *            the value to multiply
	 * @param n
	 *            the power-ten exponent
	 * @return <tt>round(uDecimal * 10<sup>n</sup>)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and the arithmetic's
	 *             {@code OverflowMode} is set to throw an exception
	 */
	public static final long multiplyByPowerOf10Checked(final DecimalArithmetic arith, final DecimalRounding rounding, final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}

		if (rounding == DecimalRounding.DOWN) {
			return multiplyByPowerOf10Checked(arith, uDecimal, Math.abs(n));
		}

		if (n > 0) {
			if (n > 18) {
				throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " * 10^" + n);
			}

			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(n);
			return scaleMetrics.multiplyByScaleFactorExact(uDecimal);
		} else {
			if (n >= -18) {
				final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-n);
				final long truncated = scaleMetrics.divideByScaleFactor(uDecimal);
				final long rem = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated);
				final long inc = Rounding.calculateRoundingIncrement(rounding, truncated, rem,
						scaleMetrics.getScaleFactor());
				return truncated + inc;
			} else if (n == -19) {
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0,
						Rounding.truncatedPartForScale19(uDecimal));
			}
			// truncated part is always larger 0 (see first if)
			// and less than 0.5 because abs(Long.MIN_VALUE) / 10^20 < 0.5
			return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0,
					TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
	}

	/**
	 * Divides the given value by a power of 10 truncating the result if
	 * necessary. Overflows are silently truncated.
	 * 
	 * @param uDecimal
	 *            the value to divide
	 * @param n
	 *            the power-ten exponent
	 * @return <tt>round<sub>DOWN</sub>(uDecimal / 10<sup>n</sup>)</tt>
	 */
	public static final long divideByPowerOf10(final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}

		if (n > 0) {
			if (n > 18) {
				return 0; // truncated result is 0
			}

			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(n);
			return scaleMetrics.divideByScaleFactor(uDecimal);
		} else {
			int pos = n;
			long result = uDecimal;
			// NOTE: result will be 0 after at most 1+64/18 rounds
			// because 10^64 contains 2^64 which is a shift left by 64
			while (pos < -18) {
				result = Scale18f.INSTANCE.multiplyByScaleFactor(result);
				if (result == 0) {
					return 0;
				}
				pos += 18;
			}
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-pos);
			return scaleMetrics.multiplyByScaleFactor(result);
		}
	}

	/**
	 * Divides the given value by a power of 10 rounding the result if
	 * necessary. Overflows are silently truncated.
	 * 
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param uDecimal
	 *            the value to divide
	 * @param n
	 *            the power-ten exponent
	 * @return <tt>round(uDecimal / 10<sup>n</sup>)</tt>
	 */
	public static final long divideByPowerOf10(final DecimalRounding rounding, final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}

		if (rounding == DecimalRounding.DOWN) {
			return divideByPowerOf10(uDecimal, n);
		}

		if (n > 0) {
			if (n > 19) {
				// truncated part is always larger 0 (see first if)
				// and less than 0.5 because abs(Long.MIN_VALUE) / 10^20 < 0.5
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0,
						TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
			} else if (n == 19) {
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0,
						Rounding.truncatedPartForScale19(uDecimal));
			}

			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(n);
			final long truncated = scaleMetrics.divideByScaleFactor(uDecimal);
			final long rem = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated);
			final long inc = Rounding.calculateRoundingIncrement(rounding, truncated, rem,
					scaleMetrics.getScaleFactor());
			return truncated + inc;
		} else {
			int pos = n;
			long result = uDecimal;
			// NOTE: result will be 0 after at most 1+64/18 rounds
			// because 10^64 contains 2^64 which is a shift left by 64
			while (pos < -18) {
				result = Scale18f.INSTANCE.multiplyByScaleFactor(result);
				if (result == 0) {
					return 0;
				}
				pos += 18;
			}
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-pos);
			return scaleMetrics.multiplyByScaleFactor(result);
		}
	}

	/**
	 * Divides the given value by a power of 10 truncating the result if
	 * necessary. An exception is thrown if an overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param uDecimal
	 *            the value to divide
	 * @param n
	 *            the power-ten exponent
	 * @return <tt>round<sub>DOWN</sub>(uDecimal / 10<sup>n</sup>)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and the arithmetic's
	 *             {@code OverflowMode} is set to throw an exception
	 */
	public static final long divideByPowerOf10Checked(final DecimalArithmetic arith, final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}

		if (n > 0) {
			if (n > 18) {
				return 0;
			}

			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(n);
			return scaleMetrics.divideByScaleFactor(uDecimal);
		} else {
			if (n >= -18) {
				final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-n);
				return scaleMetrics.multiplyByScaleFactorExact(uDecimal);
			}
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " / 10^" + n);
		}
	}

	/**
	 * Divides the given value by a power of 10 rounding the result if
	 * necessary. An exception is thrown if an overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param uDecimal
	 *            the value to divide
	 * @param n
	 *            the power-ten exponent
	 * @return <tt>round(uDecimal / 10<sup>n</sup>)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and the arithmetic's
	 *             {@code OverflowMode} is set to throw an exception
	 */
	public static final long divideByPowerOf10Checked(final DecimalArithmetic arith, final DecimalRounding rounding, final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}

		if (rounding == DecimalRounding.DOWN) {
			return divideByPowerOf10Checked(arith, uDecimal, Math.abs(n));
		}

		if (n > 0) {
			if (n > 19) {
				// truncated part is always larger 0 (see first if)
				// and less than 0.5 because abs(Long.MIN_VALUE) / 10^20 < 0.5
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0,
						TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
			} else if (n == 19) {
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0,
						Rounding.truncatedPartForScale19(uDecimal));
			}

			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(n);
			final long truncated = scaleMetrics.divideByScaleFactor(uDecimal);
			final long rem = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated);
			final long inc = Rounding.calculateRoundingIncrement(rounding, truncated, rem,
					scaleMetrics.getScaleFactor());
			return truncated + inc;
		} else {
			if (n < -18) {
				throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " / 10^" + n);
			}

			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-n);
			return scaleMetrics.multiplyByScaleFactorExact(uDecimal);
		}
	}

	/**
	 * Divides the specified dividend by a power of ten truncating the result if
	 * necessary. Overflows are silently truncated.
	 * 
	 * @param uDecimalDividend
	 *            the dividend to divide
	 * @param dividendMetrics
	 *            the arithmetics associated with the dividend
	 * @param pow10divisorIsPositive
	 *            true if the divisor is positive
	 * @param pow10divisorMetrics
	 *            the metrics reflecting the power-ten-division
	 * @return <tt>round<sub>DOWN</sub>(uDecimalDividend / 10<sup>(scale - scale10)</sup>)</tt>
	 *         where scale is the {@code scale} of the dividend and
	 *         {@code scale10} is the exponent of the power-ten divisor (negated
	 *         if {@code pow10divisorIsPositive==false})
	 */
	static final long divideByPowerOf10(final long uDecimalDividend, final ScaleMetrics dividendMetrics, final boolean pow10divisorIsPositive, final ScaleMetrics pow10divisorMetrics) {
		final int scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale();
		final long quot;
		if (scaleDiff <= 0) {
			// divide
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-scaleDiff);
			quot = scaleMetrics.divideByScaleFactor(uDecimalDividend);

		} else {
			// multiply
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(scaleDiff);
			quot = scaleMetrics.multiplyByScaleFactor(uDecimalDividend);
		}
		return pow10divisorIsPositive ? quot : -quot;
	}

	/**
	 * Divides the specified dividend by a power of ten rounding the result if
	 * necessary. Overflows are silently truncated.
	 * 
	 * @param rounding
	 *            the rounding to apply if rounding is necessary
	 * @param uDecimalDividend
	 *            the dividend to divide
	 * @param dividendMetrics
	 *            the arithmetics associated with the dividend
	 * @param pow10divisorIsPositive
	 *            true if the divisor is positive
	 * @param pow10divisorMetrics
	 *            the metrics reflecting the power-ten-division
	 * @return <tt>round(uDecimalDividend / 10<sup>(scale - scale10)</sup>)</tt>
	 *         where scale is the {@code scale} of the dividend and
	 *         {@code scale10} is the exponent of the power-ten divisor (negated
	 *         if {@code pow10divisorIsPositive==false})
	 */
	static final long divideByPowerOf10(final DecimalRounding rounding, final long uDecimalDividend, final ScaleMetrics dividendMetrics, final boolean pow10divisorIsPositive, final ScaleMetrics pow10divisorMetrics) {
		final int scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale();
		if (scaleDiff <= 0) {
			// divide
			final ScaleMetrics scaler = Scales.getScaleMetrics(-scaleDiff);
			final long truncatedValue = scaler.divideByScaleFactor(uDecimalDividend);
			final long truncatedDigits = uDecimalDividend - scaler.multiplyByScaleFactor(truncatedValue);
			if (pow10divisorIsPositive) {
				return truncatedValue + Rounding.calculateRoundingIncrementForDivision(rounding, truncatedValue,
						truncatedDigits, scaler.getScaleFactor());
			}
			return -truncatedValue + Rounding.calculateRoundingIncrementForDivision(rounding, -truncatedValue,
					-truncatedDigits, scaler.getScaleFactor());
		} else {
			// multiply
			final ScaleMetrics scaler = Scales.getScaleMetrics(scaleDiff);
			final long quot = scaler.multiplyByScaleFactor(uDecimalDividend);
			return pow10divisorIsPositive ? quot : -quot;
		}
	}

	/**
	 * Divides the specified dividend by a power of ten truncating the result if
	 * necessary. An exception is thrown if an overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the dividend value
	 * @param uDecimalDividend
	 *            the dividend to divide
	 * @param dividendMetrics
	 *            the arithmetics associated with the dividend
	 * @param pow10divisorIsPositive
	 *            true if the divisor is positive
	 * @param pow10divisorMetrics
	 *            the metrics reflecting the power-ten-division
	 * @return <tt>round<sub>DOWN</sub>(uDecimalDividend / 10<sup>(scale - scale10)</sup>)</tt>
	 *         where scale is the {@code scale} of the dividend and
	 *         {@code scale10} is the exponent of the power-ten divisor (negated
	 *         if {@code pow10divisorIsPositive==false})
	 * @throws ArithmeticException
	 *             if an overflow occurs and the arithmetic's
	 *             {@code OverflowMode} is set to throw an exception
	 */
	static final long divideByPowerOf10Checked(final DecimalArithmetic arith, final long uDecimalDividend, final ScaleMetrics dividendMetrics, final boolean pow10divisorIsPositive, final ScaleMetrics pow10divisorMetrics) {
		final int scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale();
		final long quot;
		if (scaleDiff <= 0) {
			// divide
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-scaleDiff);
			quot = scaleMetrics.divideByScaleFactor(uDecimalDividend);
		} else {
			// multiply
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(scaleDiff);
			quot = scaleMetrics.multiplyByScaleFactorExact(uDecimalDividend);
		}
		return pow10divisorIsPositive ? quot : arith.negate(quot);
	}

	/**
	 * Divides the specified dividend by a power of ten rounding the result if
	 * necessary. An exception is thrown if an overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the dividend value
	 * @param rounding
	 *            the rounding to apply if rounding is necessary
	 * @param uDecimalDividend
	 *            the dividend to divide
	 * @param dividendMetrics
	 *            the arithmetics associated with the dividend
	 * @param pow10divisorIsPositive
	 *            true if the divisor is positive
	 * @param pow10divisorMetrics
	 *            the metrics reflecting the power-ten-division
	 * @return <tt>round(uDecimalDividend / 10<sup>(scale - scale10)</sup>)</tt>
	 *         where scale is the {@code scale} of the dividend and
	 *         {@code scale10} is the exponent of the power-ten divisor (negated
	 *         if {@code pow10divisorIsPositive==false})
	 * @throws ArithmeticException
	 *             if an overflow occurs and the arithmetic's
	 *             {@code OverflowMode} is set to throw an exception
	 */
	static final long divideByPowerOf10Checked(final DecimalArithmetic arith, final DecimalRounding rounding, final long uDecimalDividend, final ScaleMetrics dividendMetrics, final boolean pow10divisorIsPositive, final ScaleMetrics pow10divisorMetrics) {
		final int scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale();
		final long quot;
		if (scaleDiff <= 0) {
			// divide
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-scaleDiff);
			quot = scaleMetrics.divideByScaleFactor(uDecimalDividend);

			final long truncatedDigits = uDecimalDividend - scaleMetrics.multiplyByScaleFactor(quot);
			if (pow10divisorIsPositive) {
				return quot + Rounding.calculateRoundingIncrementForDivision(rounding, quot, truncatedDigits,
						scaleMetrics.getScaleFactor());
			}
			return -quot + Rounding.calculateRoundingIncrementForDivision(rounding, -quot, -truncatedDigits,
					scaleMetrics.getScaleFactor());
		} else {
			// multiply
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(scaleDiff);
			quot = scaleMetrics.multiplyByScaleFactorExact(uDecimalDividend);
		}
		return pow10divisorIsPositive ? quot : arith.negate(quot);
	}

	// no instances
	private Pow10() {
	}

}
