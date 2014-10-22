package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
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
	public long shiftLeft(long uDecimal, int positions) {
		return uDecimal << positions;
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		return uDecimal >> positions;
	}

	@Override
	public long pow(long uDecimal, int exponent) {
		return Pow.pow(this, uDecimal, exponent);
	}

	@Override
	public long fromLong(long value) {
		return getScaleMetrics().multiplyByScaleFactor(value);
	}

	@Override
	public long fromBigInteger(BigInteger value) {
		return value.multiply(getScaleMetrics().getScaleFactorAsBigInteger()).longValue();
	}

	@Override
	public long fromDouble(double value) {
		if (Double.isNaN(value) || Double.isInfinite(value)) {
			throw new ArithmeticException("cannot convert double to decimal: " + value);
		}
		return fromBigDecimal(BigDecimal.valueOf(value));
	}

}
