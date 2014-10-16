package ch.javasoft.decimal.arithmetic;

import java.math.BigInteger;

import ch.javasoft.decimal.scale.Scale0f;

/**
 * Base class for arithmetics implementations for the special case
 * {@code scale=0}, that is, for long values. The implementation throws an
 * exception if an operation leads to on overflow.
 */
abstract public class AbstractCheckedScale0fArithmetics extends
		AbstractCheckedArithmetics {

	@Override
	public Scale0f getScaleMetrics() {
		return Scale0f.INSTANCE;
	}

	@Override
	public int getScale() {
		return 0;
	}

	@Override
	public long one() {
		return 1;
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		return multiplyByLong(uDecimal1, uDecimal2);
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return divideByLong(uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public long pow(long uDecimalBase, int exponent) {
		return Pow.powChecked(this, uDecimalBase, exponent);
	}

	@Override
	public long avg(long a, long b) {
		return Avg.avg(a, b);
	}

	@Override
	public long fromLong(long value) {
		return value;
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		if (scale == 0 | unscaledValue == 0) {
			return unscaledValue;
		}
		return multiplyByPowerOf10(unscaledValue, scale);
	}

	@Override
	public long parse(String value) {
		if (value.length() < 18) {
			return Long.parseLong(value);
		}
		return new BigInteger(value).longValueExact();
	}

	@Override
	public long toLong(long uDecimal) {
		return uDecimal;
	}

	@Override
	public float toFloat(long uDecimal) {
		return (float) uDecimal;
	}

	@Override
	public double toDouble(long uDecimal) {
		return (double) uDecimal;
	}

	@Override
	public String toString(long uDecimal) {
		return Long.toString(uDecimal);
	}

}
