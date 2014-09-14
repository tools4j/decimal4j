package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.ScaleMetrics;
import ch.javasoft.decimal.ScaleMetrics.Scale0f;
import ch.javasoft.decimal.ScaleMetrics.Scale18f;

/**
 * The special case for longs with {@link Scale0f} and rounding.
 */
public class UncheckedLongRoundingArithmetics extends AbstractUncheckedArithmetics {

	private final DecimalRounding rounding;

	public UncheckedLongRoundingArithmetics(RoundingMode roundingMode) {
		this(DecimalRounding.valueOf(roundingMode));
	}

	public UncheckedLongRoundingArithmetics(DecimalRounding rounding) {
		this.rounding = rounding;
	}

	@Override
	public final RoundingMode getRoundingMode() {
		return rounding.getRoundingMode();
	}

	@Override
	public ScaleMetrics getScaleMetrics() {
		return Scale0f.INSTANCE;
	}

	@Override
	public int getScale() {
		return 0;
	}

	@Override
	public long one() {
		return 1;
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		return uDecimal1 * uDecimal2;
	}

	@Override
	public long divideByLong(long uDecimalDividend, long lDivisor) {
		return divideByLong(rounding, uDecimalDividend, lDivisor);
	}
	static long divideByLong(DecimalRounding rounding, long uDecimalDividend, long lDivisor) {
		final long quotient = uDecimalDividend / lDivisor;
		final long remainder = uDecimalDividend - quotient * lDivisor;
		return quotient + rounding.calculateRoundingIncrementForDivision(quotient, remainder, lDivisor);
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return divideByLong(uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public long invert(long uDecimal) {
		//special cases first
		if (uDecimal == 0) {
			SpecialDivisionResult.DIVISOR_IS_ZERO.divide(this, 1, uDecimal);
		}
		if (uDecimal == 1) {
			return 1;
		}
		if (uDecimal == -1) {
			return -1;
		}
		return rounding.calculateRoundingIncrementForDivision(0, 1, uDecimal);
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		return shiftLeft(rounding, uDecimal, positions);
	}

	static long shiftLeft(DecimalRounding rounding, long uDecimal, int positions) {
		if (uDecimal == 0 | positions == 0) {
			return uDecimal;
		}
		if (positions < 0) {
			if (positions > -63) {
				return shiftRight(rounding, uDecimal, -positions);
			}
			return rounding.calculateRoundingIncrement(0, uDecimal < 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
		return uDecimal << positions;
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		return shiftRight(rounding, uDecimal, positions);
	}

	static long shiftRight(DecimalRounding rounding, long uDecimal, int positions) {
		if (uDecimal == 0 | positions == 0) {
			return uDecimal;
		}
		if (positions > 0) {
			//rounding may be necessary
			if (positions < 63) {
				final long truncated = uDecimal >> positions;
				final long remainder = uDecimal - (truncated << positions);
				final TruncatedPart truncatedPart = TruncatedPart.valueOf(Math.abs(remainder), 1L << positions);
				return truncated + rounding.calculateRoundingIncrement(truncated, uDecimal < 0, truncatedPart);
			}
			return rounding.calculateRoundingIncrement(0, uDecimal < 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
		//shift left, no rounding
		return uDecimal >> positions;
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int positions) {
		return divideByPowerOf10(rounding, uDecimal, positions);
	}

	static long divideByPowerOf10(DecimalRounding rounding, long uDecimal, int positions) {
		if (uDecimal == 0 | positions == 0) {
			return uDecimal;
		}
		if (positions > 0) {
			if (positions <= 18) {
				final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(positions);
				return scaleMetrics.divideByScaleFactor(uDecimal);
			}
			//truncated part is always larger 0 (see first if) 
			//and less than 0.5 because abs(Long.MIN_VALUE) / 10^19 < 0.5
			return rounding.calculateRoundingIncrement(0, uDecimal < 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		} else {
			int pos = positions;
			long result = uDecimal;
			//NOTE: this is not very efficient for positions << -18
			//      but how else do we get the correct truncated value?
			while (pos < -18) {
				result = Scale18f.INSTANCE.multiplyByScaleFactor(result);
				pos += 18;
			}
			final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(-pos);
			return scaleMetrics.multiplyByScaleFactor(result);
		}
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int positions) {
		return multiplyByPowerOf10(rounding, uDecimal, positions);
	}

	static long multiplyByPowerOf10(DecimalRounding rounding, long uDecimal, int positions) {
		if (uDecimal == 0 | positions == 0) {
			return uDecimal;
		}
		if (positions > 0) {
			int pos = positions;
			long result = uDecimal;
			//NOTE: this is not very efficient for positions >> 18
			//      but how else do we get the correct truncated value?
			while (pos > 18) {
				result = Scale18f.INSTANCE.multiplyByScaleFactor(result);
				pos -= 18;
			}
			final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(pos);
			return scaleMetrics.multiplyByScaleFactor(result);
		} else {
			if (positions >= -18) {
				final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(-positions);
				return scaleMetrics.divideByScaleFactor(uDecimal);
			}
			//truncated part is always larger 0 (see first if) 
			//and less than 0.5 because abs(Long.MIN_VALUE) / 10^19 < 0.5
			return rounding.calculateRoundingIncrement(0, uDecimal < 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
	}

	@Override
	public long pow(long uDecimal, int exponent) {
		//FIXME implement with rounding (not only on multiplications!)
		return super.pow(uDecimal, exponent);
	}

	@Override
	public long toLong(long uDecimal) {
		return uDecimal;
	}

	@Override
	public double toDouble(long uDecimal) {
		return (double) uDecimal;
	}

	@Override
	public float toFloat(long uDecimal) {
		return (float) uDecimal;
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal) {
		return BigDecimal.valueOf(uDecimal);
	}

	@Override
	public long fromLong(long value) {
		return value;
	}

	@Override
	public long fromDouble(double value) {
//		final double floor = Math.rou
//		final long truncated = (long)value;
//		final double delta = Math.abs(value - truncated);
//		
//		return truncated + rounding.calculateRoundingIncrement(truncated, value < 0, truncatedPart);
		return (long) value;
	}

	@Override
	public long fromBigInteger(BigInteger value) {
		return value.longValue();
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		return value.setScale(0, getRoundingMode()).longValue();
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		if (scale == 0) {
			return fromLong(unscaledValue);
		}
		long result = unscaledValue;
		if (scale < 0) {
			for (int i = scale; i < 0; i++) {
				result *= 10;
			}
		} else if (scale > 0) {
			int lastDigit = 0;
			boolean zeroAfterLastDigit = true;
			for (int i = 0; i < scale; i++) {
				zeroAfterLastDigit &= (lastDigit == 0);
				lastDigit = (int) Math.abs(result % 10);
				result /= 10;
			}
			//rounding
			result += rounding.calculateRoundingIncrement(result, unscaledValue < 0, lastDigit, zeroAfterLastDigit);
		}
		return result;
	}

	@Override
	public long parse(String value) {
		return Long.parseLong(value);
	}

	@Override
	public String toString(long uDecimal) {
		return Long.toString(uDecimal);
	}
}
