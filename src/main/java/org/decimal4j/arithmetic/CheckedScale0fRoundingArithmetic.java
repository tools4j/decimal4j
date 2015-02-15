package org.decimal4j.arithmetic;

import java.math.RoundingMode;

import org.decimal4j.truncate.DecimalRounding;

public class CheckedScale0fRoundingArithmetic extends
		AbstractCheckedScale0fArithmetic {

	private final DecimalRounding rounding;

	public CheckedScale0fRoundingArithmetic(RoundingMode roundingMode) {
		this(DecimalRounding.valueOf(roundingMode));
	}

	public CheckedScale0fRoundingArithmetic(DecimalRounding rounding) {
		this.rounding = rounding;
	}

	@Override
	public RoundingMode getRoundingMode() {
		return rounding.getRoundingMode();
	}

	@Override
	public long invert(long uDecimal) {
		return Invert.invertLong(rounding, uDecimal);
	}

	@Override
	public final long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Div.divideChecked(this, rounding, uDecimalDividend, uDecimalDivisor);
	}
	
	@Override
	public long multiplyByPowerOf10(long uDecimal, int n) {
		return Pow10.multiplyByPowerOf10Checked(this, rounding, uDecimal, n);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int n) {
		return Pow10.divideByPowerOf10Checked(this, rounding, uDecimal, n);
	}

	@Override
	public long sqrt(long uDecimal) {
		return Sqrt.sqrtLong(rounding, uDecimal);
	}

	@Override
	public long pow(long uDecimalBase, int exponent) {
		return Pow.powLongChecked(this, rounding, uDecimalBase, exponent);
	}

	@Override
	public long round(long uDecimal, int precision) {
		return Round.round(this, rounding, uDecimal, precision);
	}

	@Override
	public long shiftLeft(long uDecimal, int n) {
		return Shift.shiftLeftChecked(this, rounding, uDecimal, n);
	}

	@Override
	public long shiftRight(long uDecimal, int n) {
		return Shift.shiftRightChecked(this, rounding, uDecimal, n);
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
	public long fromFloat(float value) {
		return FloatConversion.floatToLong(rounding, value);
	}

	@Override
	public long fromDouble(double value) {
		return DoubleConversion.doubleToLong(rounding, value);
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		return Scale.rescale(this, unscaledValue, scale, getScale());
	}
	
	@Override
	public long parse(String value) {
		return Parse.parseLong(this, rounding, value);
	}

}
