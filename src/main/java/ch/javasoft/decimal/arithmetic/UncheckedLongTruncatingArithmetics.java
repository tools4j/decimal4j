package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.scale.Scale0f;
import ch.javasoft.decimal.scale.Scale18f;
import ch.javasoft.decimal.scale.Scales;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * The special case for longs with {@link Scale0f} and no rounding.
 */
public class UncheckedLongTruncatingArithmetics extends AbstractUncheckedArithmetics {
	
	/**
	 * The singleton instance.
	 */
	public static final UncheckedLongTruncatingArithmetics INSTANCE = new UncheckedLongTruncatingArithmetics();
	
	@Override
	public ScaleMetrics getScaleMetrics() {
		return Scale0f.INSTANCE;
	}

	@Override
	public int getScale() {
		return 0;
	}

	@Override
	public RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public long one() {
		return 1L;
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		return uDecimal1 * uDecimal2;
	}
	
	@Override
	public long sqrt(long uDecimal) {
		return sqrt(this, uDecimal);
	}
	static long sqrt(DecimalArithmetics arith, long uDecimal) {
		if (uDecimal < 0) {
			throw new ArithmeticException("square root of a negative value: " + arith.toString(uDecimal));
		}
		return _sqrt(uDecimal);
	}
	static long _sqrt(long uDecimal) {
		//http://www.codecodex.com/wiki/Calculate_an_integer_square_root
		if ((uDecimal & 0xfff0000000000000L) == 0) {
			return (long)StrictMath.sqrt(uDecimal);  
		}
	    final long result = (long) StrictMath.sqrt(2.0d*(uDecimal >>> 1));  
	    return result*result - uDecimal > 0L ? result - 1 : result;
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
		return _multiplyByPowerOf10(uDecimal, positions);
	}
	static long _multiplyByPowerOf10(long uDecimal, int positions) {
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
			final ScaleMetrics scaleMetrics = Scales.valueOf(pos);
			return scaleMetrics.multiplyByScaleFactor(result);
		} else {
			if (positions >= -18) {
				final ScaleMetrics scaleMetrics = Scales.valueOf(-positions);
				return scaleMetrics.divideByScaleFactor(uDecimal);
			}
			//truncated result is 0
			return 0;
		}
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int positions) {
		return _divideByPowerOf10(uDecimal, positions);
	}
	static long _divideByPowerOf10(long uDecimal, int positions) {
		if (uDecimal == 0 | positions == 0) {
			return uDecimal;
		}
		if (positions > 0) {
			if (positions <= 18) {
				final ScaleMetrics scaleMetrics = Scales.valueOf(positions);
				return scaleMetrics.divideByScaleFactor(uDecimal);
			}
			//truncated result is 0
			return 0;
		} else {
			int pos = positions;
			long result = uDecimal;
			//NOTE: this is not very efficient for positions << -18
			//      but how else do we get the correct truncated value?
			while (pos < -18) {
				result = Scale18f.INSTANCE.multiplyByScaleFactor(result);
				pos += 18;
			}
			final ScaleMetrics scaleMetrics = Scales.valueOf(-pos);
			return scaleMetrics.multiplyByScaleFactor(result);
		}
	}
	
	@Override
	public long average(long a, long b) {
		return _average(a, b);
	}
	static long _average(long a, long b) {
		final long xor = a ^ b;
		final long floor = (a & b) + (xor >> 1);
		return floor + ((floor >>> 63) & xor);
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
		return _divideByPowerOf10(unscaledValue, scale);
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
