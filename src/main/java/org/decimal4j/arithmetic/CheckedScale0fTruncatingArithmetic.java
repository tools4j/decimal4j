package org.decimal4j.arithmetic;

import java.math.RoundingMode;

import org.decimal4j.truncate.DecimalRounding;

/**
 * Arithmetic implementation for the special case {@code scale=0}, that is, for
 * long values. The implementation throws an exception if an operation leads to an
 * overflow. Decimals after the last scale digit are truncated without rounding.
 */
public class CheckedScale0fTruncatingArithmetic extends AbstractCheckedScale0fArithmetic {

	/**
	 * The singleton instance.
	 */
	public static final CheckedScale0fTruncatingArithmetic INSTANCE = new CheckedScale0fTruncatingArithmetic();

	@Override
	public RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public long invert(long uDecimal) {
		return Invert.invertLong(uDecimal);
	}
	
	@Override
	public long pow(long uDecimalBase, int exponent) {
		return Pow.powLongChecked(this, DecimalRounding.DOWN, uDecimalBase, exponent);
	}

	@Override
	public long sqrt(long uDecimal) {
		return Sqrt.sqrtLong(uDecimal);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int n) {
		return Pow10.divideByPowerOf10Checked(this, uDecimal, n);
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int n) {
		return Pow10.multiplyByPowerOf10Checked(this, uDecimal, n);
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		return Shift.shiftLeftChecked(this, DecimalRounding.DOWN, uDecimal, positions);
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		return Shift.shiftRightChecked(this, DecimalRounding.DOWN, uDecimal, positions);
	}

	@Override
	public long round(long uDecimal, int precision) {
		return Round.round(this, uDecimal, precision);
	}

	@Override
	public final long fromUnscaled(long unscaledValue, int scale) {
		return Pow10.divideByPowerOf10Checked(this, unscaledValue, scale);
	}

	@Override
	public long fromDouble(double value) {
		return DoubleConversion.doubleToLong(value);
	}

	@Override
	public double toDouble(long uDecimal) {
		return DoubleConversion.longToDouble(this, uDecimal);
	}

}
