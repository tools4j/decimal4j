package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;

import ch.javasoft.decimal.OverflowMode;

/**
 * Base class for checked arithmetics implementations throwing an exception if
 * an operation leads to on overflow. The {@link #getOverflowMode()} method
 * returns {@link OverflowMode#CHECKED EXCEPTION}. Only operations common to all
 * checked arithmetics implementations are implemented here.
 */
abstract public class AbstractCheckedArithmetics extends AbstractArithmetics {

	@Override
	public OverflowMode getOverflowMode() {
		return OverflowMode.CHECKED;
	}

	@Override
	public long add(long uDecimal1, long uDecimal2) {
		final long result = uDecimal1 + uDecimal2;
		if ((uDecimal1 ^ uDecimal2) >= 0 & (uDecimal1 ^ result) < 0) {
			throw new ArithmeticException("overflow: " + toString(uDecimal1) + " + " + toString(uDecimal2) + " = " + toString(result));
		}
		return result;
	}

	@Override
	public long subtract(long uDecimalMinuend, long uDecimalSubtrahend) {
		final long result = uDecimalMinuend - uDecimalSubtrahend;
		if ((uDecimalMinuend ^ uDecimalSubtrahend) < 0 & (uDecimalMinuend ^ result) < 0) {
			throw new ArithmeticException("overflow: " + toString(uDecimalMinuend) + " - " + toString(uDecimalSubtrahend) + " = " + toString(result));
		}
		return result;
	}

	@Override
	public long multiplyByLong(long uDecimal, long lValue) {
		// Hacker's Delight, Section 2-12
		final int leadingZeros = Long.numberOfLeadingZeros(uDecimal) + Long.numberOfLeadingZeros(~uDecimal) + Long.numberOfLeadingZeros(lValue) + Long.numberOfLeadingZeros(~lValue);
		/*
		 * If leadingZeros > Long.SIZE + 1 it's definitely fine, if it's <
		 * Long.SIZE it's definitely bad. We do the leadingZeros check to avoid
		 * the division below if at all possible.
		 * 
		 * Otherwise, if b == Long.MIN_VALUE, then the only allowed values of a
		 * are 0 and 1. We take care of all a < 0 with their own check, because
		 * in particular, the case a == -1 will incorrectly pass the division
		 * check below.
		 * 
		 * In all other cases, we check that either a is 0 or the result is
		 * consistent with division.
		 */
		final long result = uDecimal * lValue;
		if (leadingZeros > Long.SIZE + 1) {
			return result;
		}
		if (leadingZeros < Long.SIZE | (uDecimal < 0 & lValue == Long.MIN_VALUE) | (uDecimal != 0 && result / uDecimal != lValue)) {
			throw new ArithmeticException("overflow: " + toString(uDecimal) + " * " + toString(lValue) + " = " + toString(result));
		}
		return result;
	}

	@Override
	public long divideByLong(long uDecimalDividend, long lDivisor) {
		if (lDivisor == 0) {
			throw new ArithmeticException("division by zero: " + toString(uDecimalDividend) + " / " + toString(lDivisor));
		}
		if (lDivisor == -1 & uDecimalDividend == Long.MIN_VALUE) {
			throw new ArithmeticException("overflow: " + toString(uDecimalDividend) + " / " + toString(lDivisor) + " = " + toString(Long.MIN_VALUE));
		}
		return uDecimalDividend / lDivisor;
	}

	@Override
	public long abs(long uDecimal) {
		final long abs = Math.abs(uDecimal);
		if (abs < 0) {
			throw new ArithmeticException("overflow: abs(" + toString(uDecimal) + ") = " + toString(abs));
		}
		return abs;
	}

	@Override
	public long negate(long uDecimal) {
		final long neg = -uDecimal;
		if (neg != 0 && Long.signum(uDecimal) == Long.signum(neg)) {
			throw new ArithmeticException("overflow: -" + toString(uDecimal) + " = " + toString(neg));
		}
		return neg;
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		if (uDecimal == 0) {
			return 0;
		}
		if (positions <= 0) {
			if (positions > -64) {
				return uDecimal >> -positions;
			}
			return 0;
		}
		if (positions < Long.SIZE) {
			if (uDecimal > 0) {
				if (positions < Long.SIZE - 1) {
					final int leadingZeros = Long.numberOfLeadingZeros(uDecimal);
					if (leadingZeros > positions) {
						return uDecimal << positions;
					}
				}
			} else if (uDecimal > Long.MIN_VALUE) {
				final int leadingZeros = Long.numberOfLeadingZeros(~uDecimal);
				if (leadingZeros > positions) {
					return uDecimal << positions;
				}
			}
		}
		throw new ArithmeticException("overflow: " + toString(uDecimal) + " << " + positions + " = " + toString(uDecimal << positions));
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		if (uDecimal == 0) {
			return 0;
		}
		if (positions >= 0) {
			return uDecimal >> positions;
		}
		if (positions > -Long.SIZE) {
			return shiftLeft(uDecimal, -positions);
		}
		throw new ArithmeticException("overflow: " + toString(uDecimal) + " >> " + positions + " = " + toString(uDecimal >> positions));
	}

	@Override
	public long fromLong(long value) {
		return getScaleMetrics().multiplyByScaleFactorExact(value);
	}

	@Override
	public long fromBigInteger(BigInteger value) {
		return value.multiply(getScaleMetrics().getScaleFactorAsBigInteger()).longValueExact();
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		final BigDecimal scaled = value.multiply(getScaleMetrics().getScaleFactorAsBigDecimal()).setScale(0, getRoundingMode());
		return scaled.toBigInteger().longValueExact();
	}


	@Override
	public long parse(String value) {
		//TODO not very efficient
		return fromBigDecimal(new BigDecimal(value));
	}
}
