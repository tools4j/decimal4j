package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.ScaleMetrics;

/**
 * Base class for arithmetic implementations implementing those functions where
 * rounding is no issue. Overflow is not checked, that is,
 * {@link #getOverflowMode()} returns {@link OverflowMode#SILENT SILENT}.
 */
abstract public class AbstractArithmetics implements DecimalArithmetics {
	
	private final ScaleMetrics scaleMetrics;

	public AbstractArithmetics(ScaleMetrics scaleMetrics) {
		this.scaleMetrics = scaleMetrics;
	}
	
	@Override
	public ScaleMetrics getScaleMetrics() {
		return scaleMetrics;
	}
	@Override
	public int getScale() {
		return scaleMetrics.getScale();
	}
	@Override
	public OverflowMode getOverflowMode() {
		return OverflowMode.SILENT;
	}

	@Override
	public int signum(long uDecimal) {
		return uDecimal > 0 ? 1 : uDecimal == 0 ? 0 : -1;
	}

	@Override
	public int compare(long uDecimal1, long uDecimal2) {
		return (uDecimal1 < uDecimal2) ? -1 : ((uDecimal1 == uDecimal2) ? 0 : 1);
	}

	@Override
	public long abs(long uDecimal) {
		return Math.abs(uDecimal);
	}

	@Override
	public long negate(long uDecimal) {
		return -uDecimal;
	}

	@Override
	public long invert(long uDecimal) {
		return divide(one(), uDecimal);
	}
	
	@Override
	public long add(long uDecimal1, long uDecimal2) {
		return uDecimal1 + uDecimal2;
	}
	@Override
	public long subtract(long uDecimalMinuend, long uDecimalSubtrahend) {
		return uDecimalMinuend - uDecimalSubtrahend;
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		return uDecimal << positions;
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		return uDecimal >> positions;
	}

	@Override
	public long movePointLeft(long uDecimal, int positions) {
		if (positions >= 0) {
			long result = uDecimal;
			//NOTE: this is not very efficient
			for (int i = 0; i < positions && result != 0; i++) {
				result /= 10;
			}
			return result;
		} else {
			if (positions < Integer.MIN_VALUE) {
				return movePointRight(uDecimal, -positions);
			}
			long halfResult = movePointRight(uDecimal, -(positions / 2));
			return movePointRight(halfResult, -(positions / 2));
		}
	}

	@Override
	public long movePointRight(long uDecimal, int positions) {
		if (positions >= 0) {
			long result = uDecimal;
			//NOTE: this is not very efficient
			for (int i = 0; i < positions && result != 0; i++) {
				result *= 10;
			}
			return result;
		} else {
			return movePointLeft(uDecimal, -positions);
		}
	}

	@Override
	public long pow(long uDecimal, int exponent) {
		return pow(this, uDecimal, exponent);
	}

	static long pow(DecimalArithmetics arithmetics, long uDecimal, int exponent) {
		if (exponent == 0) {
			return arithmetics.one();
		}
		long base;
		int exp;
		if (exponent > 0) {
			base = uDecimal;
			exp = exponent;
		} else {/* exponent < 0 */
			base = arithmetics.invert(uDecimal);
			exp = -exponent;
		}
		long result = base;
		//TODO eliminate repeated truncation with multiplications in loop
		while (exp != 1 && result != 0) {
			if (exp % 2 == 0) {
				//even
				result = arithmetics.multiply(result, result);
				exp >>>= 1;
			} else {
				//odd
				result = arithmetics.multiply(result, base);
				exp--;
			}
		}
		return result;
	}

	@Override
	public double toDouble(long uDecimal) {
		return Double.valueOf(toString(uDecimal));
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal) {
		return BigDecimal.valueOf(uDecimal, getScale());
	}

	@Override
	public String toString(long uDecimal) {
		return toString(uDecimal, getScale());
	}

	public static String toString(long uDecimal, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("scale cannot be negative: " + scale);
		}
		final int negativeOffset = uDecimal < 0 ? 1 : 0;
		final StringBuilder sb = new StringBuilder(scale + 2 + negativeOffset);
		sb.append(uDecimal);
		final int len = sb.length();
		if (len <= scale + negativeOffset) {
			//Long.MAX_VALUE = 9,223,372,036,854,775,807
			sb.insert(negativeOffset, "0.00000000000000000000", 0, 2 + scale - len + negativeOffset);
		} else {
			sb.insert(len - scale, '.');
		}
		return sb.toString();
	}

	@Override
	public long fromLong(long value) {
		return getScaleMetrics().multiplyByScaleFactor(value);
	}

	@Override
	public long fromDouble(double value) {
		if (Double.isNaN(value) || Double.isInfinite(value)) {
			throw new ArithmeticException("cannot convert double to decimal: " + value);
		}
		return fromBigDecimal(BigDecimal.valueOf(value));
	}

	@Override
	public long toLong(long uDecimal) {
		return getScaleMetrics().divideByScaleFactor(uDecimal);
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal, int scale) {
		return toBigDecimal(uDecimal).round(new MathContext(scale));
	}
}
