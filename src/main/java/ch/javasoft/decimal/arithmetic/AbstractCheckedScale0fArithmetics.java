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
	public final Scale0f getScaleMetrics() {
		return Scale0f.INSTANCE;
	}

	@Override
	public final int getScale() {
		return 0;
	}

	@Override
	public final long one() {
		return 1;
	}

	@Override
	public final long multiply(long uDecimal1, long uDecimal2) {
		return Checked.multiplyByLong(this, uDecimal1, uDecimal2);
	}
	
	@Override
	public final long square(long uDecimal) {
		return Checked.multiplyByLong(this, uDecimal, uDecimal);
	}
	
	@Override
	public final long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Checked.divideByLong(this, uDecimalDividend, uDecimalDivisor);
	}
	
	@Override
	public final long avg(long a, long b) {
		return Avg.avg(a, b);
	}

	@Override
	public final long fromLong(long value) {
		return value;
	}

	@Override
	public long parse(String value) {
		if (value.length() < 18) {
			return Long.parseLong(value);
		}
		return JDKSupport.bigIntegerToLongValueExact(new BigInteger(value));
	}

	@Override
	public final long toLong(long uDecimal) {
		return uDecimal;
	}

	@Override
	public float toFloat(long uDecimal) {
		return (float) uDecimal;//TODO impl proper rounding if mantissa cannot take all the bits
	}

	@Override
	public final String toString(long uDecimal) {
		return Long.toString(uDecimal);
	}

}
