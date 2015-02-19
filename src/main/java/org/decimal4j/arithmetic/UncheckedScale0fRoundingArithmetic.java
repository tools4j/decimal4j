package org.decimal4j.arithmetic;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.decimal4j.scale.Scale0f;
import org.decimal4j.truncate.DecimalRounding;

/**
 * Arithmetic implementation with rounding for the special case with
 * {@link Scale0f}, that is, for longs. If an operation leads to an overflow the
 * result is silently truncated.
 */
public class UncheckedScale0fRoundingArithmetic extends
		AbstractUncheckedScale0fArithmetic {

	private final DecimalRounding rounding;

	public UncheckedScale0fRoundingArithmetic(RoundingMode roundingMode) {
		this(DecimalRounding.valueOf(roundingMode));
	}

	public UncheckedScale0fRoundingArithmetic(DecimalRounding rounding) {
		this.rounding = rounding;
	}

	@Override
	public final RoundingMode getRoundingMode() {
		return rounding.getRoundingMode();
	}

	@Override
	public long divideByLong(long uDecimalDividend, long lDivisor) {
		return Div.divideByLong(rounding, uDecimalDividend, lDivisor);
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Div.divideByLong(rounding, uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public long invert(long uDecimal) {
		return Invert.invertLong(rounding, uDecimal);
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		return Shift.shiftLeft(rounding, uDecimal, positions);
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		return Shift.shiftRight(rounding, uDecimal, positions);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int n) {
		return Pow10.divideByPowerOf10(rounding, uDecimal, n);
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int positions) {
		return Pow10.multiplyByPowerOf10(rounding, uDecimal, positions);
	}

	@Override
	public long sqrt(long uDecimal) {
		return Sqrt.sqrtLong(rounding, uDecimal);
	}

	@Override
	public long pow(long uDecimal, int exponent) {
		return Pow.powLong(this, rounding, uDecimal, exponent);
	}

	@Override
	public long round(long uDecimal, int precision) {
		return Round.round(this, rounding, uDecimal, precision);
	}

	@Override
	public float toFloat(long uDecimal) {
		return FloatConversion.longToFloat(this, rounding, uDecimal);
	}

	@Override
	public double toDouble(long uDecimal) {
		return DoubleConversion.longToDouble(this, rounding, uDecimal);
	}

	@Override
	public final long fromUnscaled(long unscaledValue, int scale) {
		return Pow10.divideByPowerOf10(rounding, unscaledValue, scale);
	}

	@Override
	public long fromFloat(float value) {
		return FloatConversion.floatToLong(rounding, value);
	}

	@Override
	public long fromDouble(double value) {
		return DoubleConversion.doubleToLong(rounding, value);
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		return value.setScale(0, getRoundingMode()).longValueExact();//FIXME make garbage free
	}

	@Override
	public long parse(String value) {
		return Parse.parseLong(this, rounding, value);
	}
}
