package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.scale.Scales;

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
		return UncheckedScale0fTruncatingArithmetics._sqrt(uDecimal);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int n) {
		return divideByPowerOf10(this, uDecimal, n);
	}
	static long divideByPowerOf10(DecimalArithmetics arith, long uDecimal, int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		if (n < 0) {
			if (n >= -18) {
				final ScaleMetrics scaleMetrics = Scales.valueOf(-n);
				return scaleMetrics.multiplyByScaleFactorExact(uDecimal);
			}
			throw new ArithmeticException("overflow: " + arith.toString(uDecimal) + " / 10^" + n);
		}
		if (n <= 18) {
			final ScaleMetrics scaleMetrics = Scales.valueOf(n);
			return scaleMetrics.divideByScaleFactor(uDecimal);
		}
		return 0;
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int n) {
		return multiplyByPowerOf10(this, uDecimal, n);
	}
	static long multiplyByPowerOf10(DecimalArithmetics arith, long uDecimal, int n) {
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
		throw new ArithmeticException("overflow: " + arith.toString(uDecimal) + " * 10^" + n);
	}

	@Override
	public long fromDouble(double value) {
		if (value <= Long.MAX_VALUE & value >= Long.MIN_VALUE) { 
			return (long)value;
		}
		throw new ArithmeticException("overflow for conversion from double: " + value);
	}

}
