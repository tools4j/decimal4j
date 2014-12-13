package ch.javasoft.decimal.arithmetic;

import java.math.BigInteger;

import ch.javasoft.decimal.truncate.OverflowMode;

/**
 * Base class for arithmetic implementations without overflow checking, that is,
 * for arithmetics whose {@link #getOverflowMode()} method returns
 * {@link OverflowMode#UNCHECKED}.
 */
abstract public class AbstractUncheckedArithmetics extends AbstractArithmetics {

	@Override
	public OverflowMode getOverflowMode() {
		return OverflowMode.UNCHECKED;
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
	public long add(long uDecimal1, long uDecimal2) {
		return uDecimal1 + uDecimal2;
	}

	@Override
	public long subtract(long uDecimalMinuend, long uDecimalSubtrahend) {
		return uDecimalMinuend - uDecimalSubtrahend;
	}

	@Override
	public long multiplyByLong(long uDecimal, long lValue) {
		return uDecimal * lValue;
	}

	@Override
	public long fromLong(long value) {
		return getScaleMetrics().multiplyByScaleFactor(value);
	}

	@Override
	public long fromBigInteger(BigInteger value) {
		return value.multiply(getScaleMetrics().getScaleFactorAsBigInteger()).longValue();
	}
}
