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

	private static final long FLOOR_SQRT_MAX_LONG = 3037000499L;

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
				return arith.multiplyByLong(accum, uDecimalBase);
			default:
				if ((exponent & 1) != 0) {
					accum = arith.multiplyByLong(accum, uDecimalBase);
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
	public long avg(long a, long b) {
		return UncheckedScale0fTruncatingArithmetics._avg(a, b);
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
