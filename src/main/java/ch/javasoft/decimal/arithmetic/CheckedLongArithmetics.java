package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.ScaleMetrics;
import ch.javasoft.decimal.ScaleMetrics.Scale0f;

/**
 * Throws an exception on overflows as indicated by {@link #getOverflowMode()}
 * returning {@link OverflowMode#CHECKED EXCEPTION}.
 */
public class CheckedLongArithmetics implements DecimalArithmetics {

	private static final long FLOOR_SQRT_MAX_LONG = 3037000499L;
	
	public static final CheckedLongArithmetics INSTANCE = new CheckedLongArithmetics();

	@Override
	public Scale0f getScaleMetrics() {
		return Scale0f.INSTANCE;
	}

	@Override
	public int getScale() {
		return 0;
	}

	@Override
	public RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public OverflowMode getOverflowMode() {
		return OverflowMode.CHECKED;
	}

	@Override
	public long one() {
		return 1;
	}

	@Override
	public int signum(long uDecimal) {
		return Long.signum(uDecimal);
	}

	@Override
	public int compare(long uDecimal1, long uDecimal2) {
		return Long.compare(uDecimal1, uDecimal2);
	}

	@Override
	public long add(long uDecimal1, long uDecimal2) {
		return add(this, uDecimal1, uDecimal2);
	}
	static long add(DecimalArithmetics arith, long uDecimal1, long uDecimal2) {
		final long result = uDecimal1 + uDecimal2;
		if ((uDecimal1 ^ uDecimal2) >= 0 & (uDecimal1 ^ result) < 0) {
			throw new ArithmeticException("overflow: " + arith.toString(uDecimal1) + " + " + arith.toString(uDecimal2) + " = " + arith.toString(result));
		}
		return result;
	}

	@Override
	public long subtract(long uDecimalMinuend, long uDecimalSubtrahend) {
		return subtract(this, uDecimalMinuend, uDecimalSubtrahend);
	}
	static long subtract(DecimalArithmetics arith, long uDecimalMinuend, long uDecimalSubtrahend) {
		final long result = uDecimalMinuend - uDecimalSubtrahend;
		if ((uDecimalMinuend ^ uDecimalSubtrahend) < 0 & (uDecimalMinuend ^ result) < 0) {
			throw new ArithmeticException("overflow: " + arith.toString(uDecimalMinuend) + " - " + arith.toString(uDecimalSubtrahend) + " = " + arith.toString(result));
		}
		return result;
	}

	@Override
	public long multiplyByLong(long uDecimal, long lValue) {
		return multiply(this, uDecimal, lValue);
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		return multiply(this, uDecimal1, uDecimal2);
	}

	static long multiply(DecimalArithmetics arith, long a, long b) {
		// Hacker's Delight, Section 2-12
		final int leadingZeros = Long.numberOfLeadingZeros(a) + Long.numberOfLeadingZeros(~a) + Long.numberOfLeadingZeros(b) + Long.numberOfLeadingZeros(~b);
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
		final long result = a * b;
		if (leadingZeros > Long.SIZE + 1) {
			return result;
		}
		if (leadingZeros < Long.SIZE | (a < 0 & b == Long.MIN_VALUE) | (a != 0 && result / a != b)) {
			throw new ArithmeticException("overflow: " + arith.toString(a) + " * " + arith.toString(b) + " = " + arith.toString(result));
		}
		return result;
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return divide(this, uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public long divideByLong(long uDecimalDividend, long lDivisor) {
		return divide(this, uDecimalDividend, lDivisor);
	}

	static long divide(DecimalArithmetics arith, long uDecimalDividend, long lDivisor) {
		if (lDivisor == 0) {
			throw new ArithmeticException("division by zero: " + arith.toString(uDecimalDividend) + " / " + arith.toString(lDivisor));
		}
		if (lDivisor == -1 & uDecimalDividend == Long.MIN_VALUE) {
			throw new ArithmeticException("overflow: " + arith.toString(uDecimalDividend) + " / " + arith.toString(lDivisor) + " = " + arith.toString(Long.MIN_VALUE));
		}
		return uDecimalDividend / lDivisor;
	}

	@Override
	public long abs(long uDecimal) {
		return abs(this, uDecimal);
	}
	static long abs(DecimalArithmetics arith, long uDecimal) {
		final long abs = Math.abs(uDecimal);
		if (abs < 0) {
			throw new ArithmeticException("overflow: abs(" + arith.toString(uDecimal) + ") = " + arith.toString(abs));
		}
		return abs;
	}

	@Override
	public long negate(long uDecimal) {
		return negate(this, uDecimal);
	}
	static long negate(DecimalArithmetics arith, long uDecimal) {
		final long neg = -uDecimal;
		if (neg != 0 && Long.signum(uDecimal) == Long.signum(neg)) {
			throw new ArithmeticException("overflow: -" + arith.toString(uDecimal) + " = " + arith.toString(neg));
		}
		return neg;
	}

	@Override
	public long invert(long uDecimal) {
		return divide(one(), uDecimal);
	}

	@Override
	public long pow(long uDecimalBase, int exponent) {
		return pow(this, uDecimalBase, exponent);
	}
	static long pow(DecimalArithmetics arith, long uDecimalBase, int exponent) {
		if (exponent == 0) {
			return 1;
		}
		if (exponent < 0) {
			if (uDecimalBase == 1 | uDecimalBase == -1) {
				return uDecimalBase;
			}
			if (uDecimalBase != 0) {
				return 0;
			}
			throw new ArithmeticException("division by zero: " + arith.toString(uDecimalBase) + "^" + exponent);
		}
		if (uDecimalBase >= -2 & uDecimalBase <= 2) {
			switch ((int) uDecimalBase) {
			case 0:
				return (exponent == 0) ? 1 : 0;
			case 1:
				return 1;
			case (-1):
				return ((exponent & 1) == 0) ? 1 : -1;
			case 2:
				if (exponent >= Long.SIZE - 1) {
					throw new ArithmeticException("overflow: " + arith.toString(uDecimalBase) + "^" + exponent);
				}
				return 1L << exponent;
			case (-2):
				if (exponent >= Long.SIZE) {
					throw new ArithmeticException("overflow: " + arith.toString(uDecimalBase) + "^" + exponent);
				}
				return ((exponent & 1) == 0) ? (1L << exponent) : (-1L << exponent);
			default:
				throw new AssertionError();
			}
		}
		long accum = 1;
		while (true) {
			switch (exponent) {
			case 0:
				return accum;
			case 1:
				return multiply(arith, accum, uDecimalBase);
			default:
				if ((exponent & 1) != 0) {
					accum = multiply(arith, accum, uDecimalBase);
				}
				exponent >>= 1;
				if (exponent > 0) {
					if (uDecimalBase > FLOOR_SQRT_MAX_LONG) {
						throw new ArithmeticException("overflow: " + arith.toString(uDecimalBase) + "^" + exponent);
					}
					uDecimalBase *= uDecimalBase;
				}
			}
		}
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		return shiftLeft(this, uDecimal, positions);
	}
	static long shiftLeft(DecimalArithmetics arith, long uDecimal, int positions) {
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
		throw new ArithmeticException("overflow: " + arith.toString(uDecimal) + " << " + positions + " = " + arith.toString(uDecimal << positions));
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		return shiftRight(this, uDecimal, positions);
	}
	static long shiftRight(DecimalArithmetics arith, long uDecimal, int positions) {
		if (uDecimal == 0) {
			return 0;
		}
		if (positions >= 0) {
			return uDecimal >> positions;
		}
		if (positions > -Long.SIZE) {
			return shiftLeft(arith, uDecimal, -positions);
		}
		throw new ArithmeticException("overflow: " + arith.toString(uDecimal) + " >> " + positions + " = " + arith.toString(uDecimal >> positions));
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int n) {
		return divideByPowerOf10(this, uDecimal, n);
	}
	static long divideByPowerOf10(DecimalArithmetics arith, long uDecimal, int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		if (n < 0) {
			if (n >= -18) {
				final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(-n);
				return scaleMetrics.multiplyByScaleFactorExact(uDecimal);
			}
			throw new ArithmeticException("overflow: " + arith.toString(uDecimal) + " / 10^" + n);
		}
		if (n <= 18) {
			final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(n);
			return scaleMetrics.divideByScaleFactor(uDecimal);
		}
		return 0;
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int n) {
		return multiplyByPowerOf10(this, uDecimal, n);
	}
	static long multiplyByPowerOf10(DecimalArithmetics arith, long uDecimal, int n) {
		if (uDecimal == 0 | n == 0) {
			return uDecimal;
		}
		if (n < 0) {
			if (n >= -18) {
				final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(-n);
				return scaleMetrics.divideByScaleFactor(uDecimal);
			}
			return 0;
		}
		if (n <= 18) {
			final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(n);
			return scaleMetrics.multiplyByScaleFactorExact(uDecimal);
		}
		throw new ArithmeticException("overflow: " + arith.toString(uDecimal) + " * 10^" + n);
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
		return value.longValueExact();
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		final BigInteger scaled = value.setScale(0, getRoundingMode()).toBigInteger();
		return scaled.longValueExact();
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		if (scale == 0 || unscaledValue == 0) {
			return unscaledValue;
		}
		if (scale > 0) {
			if (scale <= 18) {
				final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(scale);
				return scaleMetrics.multiplyByScaleFactorExact(unscaledValue);
			}
			throw new ArithmeticException("overflow: " + unscaledValue + " * 10^" + scale);
		} else {
			if (scale >= -18) {
				final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(-scale);
				return scaleMetrics.divideByScaleFactor(unscaledValue);
			}
			return 0;
		}
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
		return (float)uDecimal;
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
	public BigDecimal toBigDecimal(long uDecimal, int scale) {
		return BigDecimal.valueOf(uDecimal).setScale(scale);
	}

	@Override
	public String toString(long uDecimal) {
		return Long.toString(uDecimal);
	}

}
