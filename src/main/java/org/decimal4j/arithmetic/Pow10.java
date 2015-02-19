package org.decimal4j.arithmetic;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;
import org.decimal4j.scale.Scale18f;

/**
 * Contains methods for multiplications and divisions with powers of ten.
 */
final class Pow10 {
	
	public static long multiplyByPowerOf10(final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		if (n > 0) {
			int pos = n;
			long result = uDecimal;
			//NOTE: result will be 0 after at most 1+64/18 rounds
			//      because 10^64 contains 2^64 which is a shift left by 64
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
			//truncated result is 0
			return 0;
		}
	}

	public static long multiplyByPowerOf10(final DecimalRounding rounding, final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		if (n > 0) {
			int pos = n;
			long result = uDecimal;
			//NOTE: result will be 0 after at most 1+64/18 rounds
			//      because 10^64 contains 2^64 which is a shift left by 64
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
				final long inc = RoundingUtil.calculateRoundingIncrement(rounding, truncated, rem, scaleMetrics.getScaleFactor());
				return truncated + inc;
			} else if (n == -19) {
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, RoundingUtil.truncatedPartForScale19(uDecimal));
			}
			//truncated part is always larger 0 (see first if) 
			//and less than 0.5 because abs(Long.MIN_VALUE) / 10^20 < 0.5
			return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
	}
	
	public static long multiplyByPowerOf10Checked(final DecimalArithmetic arith, final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		
		if (n > 0) {
			if (n > 18) {
				throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " * 10^" + n);
			}

			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(n);
			return scaleMetrics.multiplyByScaleFactorExact(uDecimal);
		}
		else {
			if (n >= -18) {
				final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-n);
				return scaleMetrics.divideByScaleFactor(uDecimal);
			}
			return 0;
		}
	}

	public static long multiplyByPowerOf10Checked(final DecimalArithmetic arith, final DecimalRounding rounding, final long uDecimal, final int n) {
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
				final long inc = RoundingUtil.calculateRoundingIncrement(rounding, truncated, rem, scaleMetrics.getScaleFactor());
				return truncated + inc;
			} else if (n == -19) {
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, RoundingUtil.truncatedPartForScale19(uDecimal));
			}
			//truncated part is always larger 0 (see first if) 
			//and less than 0.5 because abs(Long.MIN_VALUE) / 10^20 < 0.5
			return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
	}
	
	public static long divideByPowerOf10(final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		
		if (n > 0) {
			if (n > 18) {
				return 0; //truncated result is 0
			}
			
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(n);
			return scaleMetrics.divideByScaleFactor(uDecimal);
		} else {
			int pos = n;
			long result = uDecimal;
			//NOTE: result will be 0 after at most 1+64/18 rounds
			//      because 10^64 contains 2^64 which is a shift left by 64
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
	
	public static long divideByPowerOf10(final DecimalRounding rounding, final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		
		if (rounding == DecimalRounding.DOWN) {
			return divideByPowerOf10(uDecimal, n);
		}
		
		if (n > 0) {
			if (n > 19) {
				//truncated part is always larger 0 (see first if) 
				//and less than 0.5 because abs(Long.MIN_VALUE) / 10^20 < 0.5
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
			} else if (n == 19) {
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, RoundingUtil.truncatedPartForScale19(uDecimal));
			} 
			
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(n);
			final long truncated = scaleMetrics.divideByScaleFactor(uDecimal);
			final long rem = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated);
			final long inc = RoundingUtil.calculateRoundingIncrement(rounding, truncated, rem, scaleMetrics.getScaleFactor());
			return truncated + inc;
		} else {
			int pos = n;
			long result = uDecimal;
			//NOTE: result will be 0 after at most 1+64/18 rounds
			//      because 10^64 contains 2^64 which is a shift left by 64
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
	
	public static long divideByPowerOf10Checked(final DecimalArithmetic arith, final long uDecimal, final int n) {
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

	public static long divideByPowerOf10Checked(final DecimalArithmetic arith, final DecimalRounding rounding, final long uDecimal, final int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}

		if (rounding == DecimalRounding.DOWN) {
			return divideByPowerOf10Checked(arith, uDecimal, Math.abs(n));
		}
		
		if (n > 0) {
			if (n > 19) {
				//truncated part is always larger 0 (see first if) 
				//and less than 0.5 because abs(Long.MIN_VALUE) / 10^20 < 0.5
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
			} else if (n == 19) {
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, RoundingUtil.truncatedPartForScale19(uDecimal));
			}
			
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(n);
			final long truncated = scaleMetrics.divideByScaleFactor(uDecimal);
			final long rem = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated);
			final long inc = RoundingUtil.calculateRoundingIncrement(rounding, truncated, rem, scaleMetrics.getScaleFactor());
			return truncated + inc;
		} else {
			if (n < -18) {
				throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " / 10^" + n);
			}
			
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-n);
			return scaleMetrics.multiplyByScaleFactorExact(uDecimal);
		}
	}
	
	static long divideByPowerOf10(final long uDecimalDividend, final ScaleMetrics dividendMetrics, final boolean pow10divisorIsPositive, final ScaleMetrics pow10divisorMetrics) {
		final int scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale();
		final long quot;
		if (scaleDiff <= 0) {
			//divide
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-scaleDiff);
			quot = scaleMetrics.divideByScaleFactor(uDecimalDividend);

		} else {
			//multiply
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(scaleDiff);
			quot = scaleMetrics.multiplyByScaleFactor(uDecimalDividend);
		}
		return pow10divisorIsPositive ? quot : -quot;
	}

	static long divideByPowerOf10(final DecimalRounding rounding, final long uDecimalDividend, final ScaleMetrics dividendMetrics, final boolean pow10divisorIsPositive, final ScaleMetrics pow10divisorMetrics) {
		final int scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale();
		if (scaleDiff <= 0) {
			//divide
			final ScaleMetrics scaler = Scales.getScaleMetrics(-scaleDiff);
			final long truncatedValue = scaler.divideByScaleFactor(uDecimalDividend);
			final long truncatedDigits = uDecimalDividend - scaler.multiplyByScaleFactor(truncatedValue);
			if (pow10divisorIsPositive) {
				return truncatedValue + RoundingUtil.calculateRoundingIncrementForDivision(rounding, truncatedValue, truncatedDigits, scaler.getScaleFactor());
			}
			return -truncatedValue + RoundingUtil.calculateRoundingIncrementForDivision(rounding, -truncatedValue, -truncatedDigits, scaler.getScaleFactor());
		} else {
			//multiply
			final ScaleMetrics scaler = Scales.getScaleMetrics(scaleDiff);
			final long quot = scaler.multiplyByScaleFactor(uDecimalDividend);
			return pow10divisorIsPositive ? quot : -quot;
		}
	}
	static long divideByPowerOf10Checked(final DecimalArithmetic arith, final long uDecimalDividend, final ScaleMetrics dividendMetrics, final boolean pow10divisorIsPositive, final ScaleMetrics pow10divisorMetrics) {
		final int scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale();
		final long quot;
		if (scaleDiff <= 0) {
			//divide
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-scaleDiff);
			quot = scaleMetrics.divideByScaleFactor(uDecimalDividend);
		} else {
			//multiply
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(scaleDiff);
			quot = scaleMetrics.multiplyByScaleFactorExact(uDecimalDividend);
		}
		return pow10divisorIsPositive ? quot : arith.negate(quot);
	}
	
	static long divideByPowerOf10Checked(final DecimalArithmetic arith, final DecimalRounding rounding, final long uDecimalDividend, final ScaleMetrics dividendMetrics, final boolean pow10divisorIsPositive, final ScaleMetrics pow10divisorMetrics) {
		final int scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale();
		final long quot;
		if (scaleDiff <= 0) {
			//divide
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(-scaleDiff);
			quot = scaleMetrics.divideByScaleFactor(uDecimalDividend);

			final long truncatedDigits = uDecimalDividend - scaleMetrics.multiplyByScaleFactor(quot);
			if (pow10divisorIsPositive) {
				return quot + RoundingUtil.calculateRoundingIncrementForDivision(rounding, quot, truncatedDigits, scaleMetrics.getScaleFactor());
			}
			return -quot + RoundingUtil.calculateRoundingIncrementForDivision(rounding, -quot, -truncatedDigits, scaleMetrics.getScaleFactor());
		} else {
			//multiply
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(scaleDiff);
			quot = scaleMetrics.multiplyByScaleFactorExact(uDecimalDividend);
		}
		return pow10divisorIsPositive ? quot : arith.negate(quot);
	}

	// no instances
	private Pow10() {
	}
	
}
