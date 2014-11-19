package ch.javasoft.decimal.arithmetic;

import java.math.BigInteger;

import ch.javasoft.decimal.scale.Scale0f;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Base class for arithmetic special case for longs with {@link Scale0f}.
 */
abstract public class AbstractUncheckedScale0fArithmetics extends AbstractUncheckedArithmetics {
	
	@Override
	public final ScaleMetrics getScaleMetrics() {
		return Scale0f.INSTANCE;
	}

	@Override
	public final int getScale() {
		return 0;
	}

	@Override
	public final long one() {
		return 1L;
	}

	@Override
	public final long multiply(long uDecimal1, long uDecimal2) {
		return uDecimal1 * uDecimal2;
	}
	
	@Override
	public final long square(long uDecimal) {
		return uDecimal * uDecimal;
	}

	@Override
	public final long fromLong(long value) {
		return value;
	}

	@Override
	public final long fromBigInteger(BigInteger value) {
		return value.longValue();
	}

	@Override
	public final long parse(String value) {
		return Long.parseLong(value);
	}

	@Override
	public final long toLong(long uDecimal) {
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
	public final String toString(long uDecimal) {
		return Long.toString(uDecimal);
	}

}
