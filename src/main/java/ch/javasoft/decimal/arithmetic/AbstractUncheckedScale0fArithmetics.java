package ch.javasoft.decimal.arithmetic;

import java.math.BigInteger;

import ch.javasoft.decimal.scale.Scale0f;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Base class for arithmetic special case for longs with {@link Scale0f}.
 */
abstract public class AbstractUncheckedScale0fArithmetics extends AbstractUncheckedArithmetics {
	
	@Override
	public ScaleMetrics getScaleMetrics() {
		return Scale0f.INSTANCE;
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
	public long fromLong(long value) {
		return value;
	}

	@Override
	public long fromBigInteger(BigInteger value) {
		return value.longValue();
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
