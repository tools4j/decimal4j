package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.scale.Scale0f;
import ch.javasoft.decimal.truncate.DecimalRounding;

/**
 * The special case for longs with {@link Scale0f} and no rounding.
 */
public class UncheckedScale0fTruncatingArithmetics extends AbstractUncheckedScale0fArithmetics {
	
	/**
	 * The singleton instance.
	 */
	public static final UncheckedScale0fTruncatingArithmetics INSTANCE = new UncheckedScale0fTruncatingArithmetics();
	
	@Override
	public RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public long sqrt(long uDecimal) {
		return Sqrt.sqrtLong(uDecimal);
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return uDecimalDividend / uDecimalDivisor;
	}

	@Override
	public long divideByLong(long uDecimalDividend, long lDivisor) {
		return uDecimalDividend / lDivisor;
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int positions) {
		return Pow10.multiplyByPowerOf10(uDecimal, positions);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int positions) {
		return Pow10.divideByPowerOf10(uDecimal, positions);
	}
	
	@Override
	public long pow(long uDecimal, int exponent) {
		return Pow.powLong(this, DecimalRounding.DOWN, uDecimal, exponent);
	}

	@Override
	public long avg(long a, long b) {
		return Avg.avg(a, b);
	}

	@Override
	public long fromLong(long value) {
		return value;
	}

	@Override
	public long fromDouble(double value) {
		return (long)value;
	}

	@Override
	public long fromBigInteger(BigInteger value) {
		return value.longValue();
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		return value.longValue();
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		if (scale == 0 | unscaledValue == 0) {
			return unscaledValue;
		}
		return Pow10.divideByPowerOf10(unscaledValue, scale);
	}

	@Override
	public long parse(String value) {
		return Long.parseLong(value);
	}

	@Override
	public long toLong(long uDecimal) {
		return uDecimal;
	}

	@Override
	public double toDouble(long uDecimal) {
		return (double)uDecimal;
	}
	
	@Override
	public float toFloat(long uDecimal) {
		return (float)uDecimal;
	}

	@Override
	public String toString(long uDecimal) {
		return Long.toString(uDecimal);
	}

}
