package org.decimal4j.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.truncate.OverflowMode;

/**
 * Base class for checked arithmetic implementations throwing an exception if
 * an operation leads to on overflow. The {@link #getOverflowMode()} method
 * returns {@link OverflowMode#CHECKED EXCEPTION}. Only operations common to all
 * checked arithmetic implementations are implemented here.
 */
abstract public class AbstractCheckedArithmetic extends AbstractArithmetic {

	@Override
	public OverflowMode getOverflowMode() {
		return OverflowMode.CHECKED;
	}

	@Override
	public long add(long uDecimal1, long uDecimal2) {
		return Checked.add(this, uDecimal1, uDecimal2);
	}

	@Override
	public long subtract(long uDecimalMinuend, long uDecimalSubtrahend) {
		return Checked.subtract(this, uDecimalMinuend, uDecimalSubtrahend);
	}

	@Override
	public long multiplyByLong(long uDecimal, long lValue) {
		return Checked.multiplyByLong(this, uDecimal, lValue);
	}

	@Override
	public long divideByLong(long uDecimalDividend, long lDivisor) {
		return Checked.divideByLong(this, uDecimalDividend, lDivisor);
	}

	@Override
	public long abs(long uDecimal) {
		return Checked.abs(this, uDecimal);
	}

	@Override
	public long negate(long uDecimal) {
		return Checked.negate(this, uDecimal);
	}

	@Override
	public long fromLong(long value) {
		return getScaleMetrics().multiplyByScaleFactorExact(value);
	}

	@Override
	public long fromBigInteger(BigInteger value) {
		//FIXME make garbage free
		return JDKSupport.bigIntegerToLongValueExact(value.multiply(getScaleMetrics().getScaleFactorAsBigInteger()));
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		//FIXME make garbage free
		final BigDecimal scaled = value.multiply(getScaleMetrics().getScaleFactorAsBigDecimal()).setScale(0, getRoundingMode());
		return JDKSupport.bigIntegerToLongValueExact(scaled.toBigInteger());
	}
}