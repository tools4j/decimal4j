package org.decimal4j.arithmetic;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.decimal4j.scale.Scale0f;
import org.decimal4j.truncate.DecimalRounding;

/**
 * The special case for longs with {@link Scale0f} and no rounding.
 */
public final class UncheckedScale0fTruncatingArithmetics extends AbstractUncheckedScale0fArithmetics {
	
	/**
	 * The singleton instance.
	 */
	public static final UncheckedScale0fTruncatingArithmetics INSTANCE = new UncheckedScale0fTruncatingArithmetics();
	
	@Override
	public final RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public final long sqrt(long uDecimal) {
		return Sqrt.sqrtLong(uDecimal);
	}

	@Override
	public final long divide(long uDecimalDividend, long uDecimalDivisor) {
		return uDecimalDividend / uDecimalDivisor;
	}

	@Override
	public final long divideByLong(long uDecimalDividend, long lDivisor) {
		return uDecimalDividend / lDivisor;
	}

	@Override
	public final long multiplyByPowerOf10(long uDecimal, int positions) {
		return Pow10.multiplyByPowerOf10(uDecimal, positions);
	}

	@Override
	public final long divideByPowerOf10(long uDecimal, int positions) {
		return Pow10.divideByPowerOf10(uDecimal, positions);
	}
	
	@Override
	public long invert(long uDecimal) {
		return Invert.invertLong(uDecimal);
	}
	
	@Override
	public final long pow(long uDecimal, int exponent) {
		return Pow.powLong(this, DecimalRounding.DOWN, uDecimal, exponent);
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		return Shift.shiftLeft(DecimalRounding.DOWN, uDecimal, positions);
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		return Shift.shiftRight(DecimalRounding.DOWN, uDecimal, positions);
	}

	@Override
	public final long avg(long a, long b) {
		return Avg.avg(a, b);
	}

	@Override
	public final long round(long uDecimal, int precision) {
		return Round.round(this, uDecimal, precision);
	}

	@Override
	public final long fromUnscaled(long unscaledValue, int scale) {
		return Pow10.divideByPowerOf10(unscaledValue, scale);
	}

	@Override
	public final long fromDouble(double value) {
		return DoubleConversion.doubleToLong(value);
	}

	@Override
	public final long fromBigDecimal(BigDecimal value) {
		return value.longValue();
	}

	@Override
	public final double toDouble(long uDecimal) {
		return DoubleConversion.longToDouble(this, uDecimal);
	}
	
	@Override
	public final float toFloat(long uDecimal) {
		return (float)uDecimal;
	}
}
