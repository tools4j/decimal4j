package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

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
			return SpecialDivisionResult.DIVIDEND_IS_ZERO.divide(this, 1, uDecimal);
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
	public long fromDouble(double value) {
		if (value <= Long.MAX_VALUE & value >= Long.MIN_VALUE) { 
			return (long)value;
		}
		throw new ArithmeticException("overflow for conversion from double: " + value);
	}

}
