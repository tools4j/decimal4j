package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;

import ch.javasoft.decimal.ScaleMetrics;
import ch.javasoft.decimal.ScaleMetrics.Scale0f;
import ch.javasoft.decimal.ScaleMetrics.Scale18f;

/**
 * The special case for longs with {@link Scale0f} and no rounding.
 */
public class LongArithmetics extends TruncatingArithmetics {
	
	public LongArithmetics(Scale0f scale0f) {
		super(scale0f);
	}

	@Override
	public int getScale() {
		return 0;
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
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return uDecimalDividend / uDecimalDivisor;
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
		if (scale == 0) {
			return fromLong(unscaledValue);
		}
		if (scale > 0) {
			long value = unscaledValue;
			while (scale > 18) {
				//not very efficient for large scale, but how do we otherwise
				//get the correct truncated value?
				value = Scale18f.INSTANCE.multiplyByScaleFactor(value);
				scale -= 18;
			}
			final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(scale);
			return scaleMetrics.multiplyByScaleFactor(value);
		} else {
			if (scale >= -18) {
				final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(-scale);
				return scaleMetrics.divideByScaleFactor(unscaledValue);
			}
			//truncating division leads to zero
			return 0;
		}
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
	public BigDecimal toBigDecimal(long uDecimal) {
		return BigDecimal.valueOf(uDecimal);
	}

	@Override
	public String toString(long uDecimal) {
		return Long.toString(uDecimal);
	}

}
