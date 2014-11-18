package ch.javasoft.decimal.arithmetic;

import ch.javasoft.decimal.scale.Scale18f;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.scale.Scales;
import ch.javasoft.decimal.truncate.DecimalRounding;
import ch.javasoft.decimal.truncate.TruncatedPart;

/**
 * Calculates multiplications and divisions with powers of 10.
 */
final class Pow10 {
	
	public static long multiplyByPowerOf10(long uDecimal, int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		if (n > 0) {
			int pos = n;
			long result = uDecimal;
			//NOTE: this is not very efficient for n >> 18
			//      but how else do we get the correct truncated value?
			while (pos > 18) {
				result = Scale18f.INSTANCE.multiplyByScaleFactor(result);
				pos -= 18;
			}
			final ScaleMetrics scaleMetrics = Scales.valueOf(pos);
			return scaleMetrics.multiplyByScaleFactor(result);
		} else {
			if (n >= -18) {
				final ScaleMetrics scaleMetrics = Scales.valueOf(-n);
				return scaleMetrics.divideByScaleFactor(uDecimal);
			}
			//truncated result is 0
			return 0;
		}
	}
	
	public static long divideByPowerOf10(long uDecimal, int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		if (n > 0) {
			if (n <= 18) {
				final ScaleMetrics scaleMetrics = Scales.valueOf(n);
				return scaleMetrics.divideByScaleFactor(uDecimal);
			}
			//truncated result is 0
			return 0;
		} else {
			int pos = n;
			long result = uDecimal;
			//NOTE: this is not very efficient for n << -18
			//      but how else do we get the correct truncated value?
			while (pos < -18) {
				result = Scale18f.INSTANCE.multiplyByScaleFactor(result);
				pos += 18;
			}
			final ScaleMetrics scaleMetrics = Scales.valueOf(-pos);
			return scaleMetrics.multiplyByScaleFactor(result);
		}
	}
	
	public static long divideByPowerOf10Checked(DecimalArithmetics arith, long uDecimal, int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		if (n < 0) {
			if (n >= -18) {
				final ScaleMetrics scaleMetrics = Scales.valueOf(-n);
				return scaleMetrics.multiplyByScaleFactorExact(uDecimal);
			}
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " / 10^" + n);
		}
		if (n <= 18) {
			final ScaleMetrics scaleMetrics = Scales.valueOf(n);
			return scaleMetrics.divideByScaleFactor(uDecimal);
		}
		return 0;
	}

	public static long multiplyByPowerOf10Checked(DecimalArithmetics arith, long uDecimal, int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		if (n < 0) {
			if (n >= -18) {
				final ScaleMetrics scaleMetrics = Scales.valueOf(-n);
				return scaleMetrics.divideByScaleFactor(uDecimal);
			}
			return 0;
		}
		if (n <= 18) {
			final ScaleMetrics scaleMetrics = Scales.valueOf(n);
			return scaleMetrics.multiplyByScaleFactorExact(uDecimal);
		}
		throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " * 10^" + n);
	}

	public static long multiplyByPowerOf10(DecimalRounding rounding, long uDecimal, int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		if (n > 0) {
			int pos = n;
			long result = uDecimal;
			//NOTE: this is not very efficient for n >> 18
			//      but how else do we get the correct truncated value?
			while (pos > 18) {
				result = Scale18f.INSTANCE.multiplyByScaleFactor(result);
				pos -= 18;
			}
			final ScaleMetrics scaleMetrics = Scales.valueOf(pos);
			return scaleMetrics.multiplyByScaleFactor(result);
		} else {
			if (n >= -18) {
				final ScaleMetrics scaleMetrics = Scales.valueOf(-n);
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

	public static long divideByPowerOf10(DecimalRounding rounding, long uDecimal, int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		if (n > 0) {
			if (rounding == DecimalRounding.DOWN) {
				return divideByPowerOf10(uDecimal, n);
			}
			if (n <= 18) {
				final ScaleMetrics scaleMetrics = Scales.valueOf(n);
				final long truncated = scaleMetrics.divideByScaleFactor(uDecimal);
				final long rem = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated);
				final long inc = RoundingUtil.calculateRoundingIncrement(rounding, truncated, rem, scaleMetrics.getScaleFactor());
				return truncated + inc;
			} else if (n == 19) {
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, RoundingUtil.truncatedPartForScale19(uDecimal));
			}
			//truncated part is always larger 0 (see first if) 
			//and less than 0.5 because abs(Long.MIN_VALUE) / 10^20 < 0.5
			return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		} else {
			int pos = n;
			long result = uDecimal;
			//NOTE: this is not very efficient for n << -18
			//      but how else do we get the correct truncated value?
			while (pos < -18) {
				result = Scale18f.INSTANCE.multiplyByScaleFactor(result);
				pos += 18;
			}
			final ScaleMetrics scaleMetrics = Scales.valueOf(-pos);
			return scaleMetrics.multiplyByScaleFactor(result);
		}
	}

	static long divideByPowerOf10(long uDecimalDividend, ScaleMetrics dividendMetrics, boolean pow10divisorIsPositive, ScaleMetrics pow10divisorMetrics) {
		final int scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale();
		final long quot;
		if (scaleDiff <= 0) {
			//divide
			final ScaleMetrics scaleMetrics = Scales.valueOf(-scaleDiff);
			quot = scaleMetrics.divideByScaleFactor(uDecimalDividend);

		} else {
			//multiply
			final ScaleMetrics scaleMetrics = Scales.valueOf(scaleDiff);
			quot = scaleMetrics.multiplyByScaleFactor(uDecimalDividend);
		}
		return pow10divisorIsPositive ? quot : -quot;
	}

	static long divideByPowerOf10(DecimalRounding rounding, long uDecimalDividend, ScaleMetrics dividendMetrics, boolean pow10divisorIsPositive, ScaleMetrics pow10divisorMetrics) {
		final int scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale();
		if (scaleDiff <= 0) {
			//divide
			final ScaleMetrics scaler = Scales.valueOf(-scaleDiff);
			final long truncatedValue = scaler.divideByScaleFactor(uDecimalDividend);
			final long truncatedDigits = uDecimalDividend - scaler.multiplyByScaleFactor(truncatedValue);
			if (pow10divisorIsPositive) {
				return truncatedValue + RoundingUtil.calculateRoundingIncrementForDivision(rounding, truncatedValue, truncatedDigits, scaler.getScaleFactor());
			}
			return -truncatedValue + RoundingUtil.calculateRoundingIncrementForDivision(rounding, -truncatedValue, -truncatedDigits, scaler.getScaleFactor());

		} else {
			//multiply
			final ScaleMetrics scaler = Scales.valueOf(scaleDiff);
			final long quot = scaler.multiplyByScaleFactor(uDecimalDividend);
			return pow10divisorIsPositive ? quot : -quot;
		}
	}
	static long divideByPowerOf10Checked(DecimalArithmetics arith, long uDecimalDividend, ScaleMetrics dividendMetrics, boolean pow10divisorIsPositive, ScaleMetrics pow10divisorMetrics) {
		final int scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale();
		final long quot;
		if (scaleDiff <= 0) {
			//divide
			final ScaleMetrics scaleMetrics = Scales.valueOf(-scaleDiff);
			quot = scaleMetrics.divideByScaleFactor(uDecimalDividend);

		} else {
			//multiply
			final ScaleMetrics scaleMetrics = Scales.valueOf(scaleDiff);
			quot = scaleMetrics.multiplyByScaleFactorExact(uDecimalDividend);
		}
		return pow10divisorIsPositive ? quot : arith.negate(quot);
	}

	// no instances
	private Pow10() {
		super();
	}
}
