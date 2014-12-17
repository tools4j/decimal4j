package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

import ch.javasoft.decimal.truncate.DecimalRounding;

/**
 * Arithmetics implementation for the special case {@code scale=0}, that is, for
 * long values. The implementation throws an exception if an operation leads to on
 * overflow. Decimals after the last scale digit are truncated without rounding.
 */
public class CheckedScale0fTruncatingArithmetics extends AbstractCheckedScale0fArithmetics {

	/**
	 * The singleton instance.
	 */
	public static final CheckedScale0fTruncatingArithmetics INSTANCE = new CheckedScale0fTruncatingArithmetics();

	@Override
	public RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public long invert(long uDecimal) {
		if (uDecimal == 0) {
			throw new ArithmeticException("Division by zero: " + uDecimal + "^-1");
		}
		if (uDecimal == 1) {
			return 1;
		}
		if (uDecimal == -1) {
			return -1;
		}
		return 0;
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
		return DoubleConversion.unscaledToDouble(this, uDecimal);
	}

}
