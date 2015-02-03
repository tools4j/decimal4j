package org.decimal4j.arithmetic;

import java.math.BigInteger;

import org.decimal4j.scale.Scale0f;
import org.decimal4j.scale.ScaleMetrics;

/**
 * Base class for arithmetic special case for longs with {@link Scale0f}.
 */
abstract public class AbstractUncheckedScale0fArithmetic extends AbstractUncheckedArithmetic {
	
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
	public float toFloat(long uDecimal) {
		return (float)uDecimal;//TODO impl proper rounding if mantissa cannot take all the bits
	}

	@Override
	public final String toString(long uDecimal) {
		return Long.toString(uDecimal);
	}

}
