package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;

/**
 * Throws an exception on overflows as indicated by {@link #getOverflowMode()}
 * returning {@link OverflowMode#EXCEPTION EXCEPTION}. Delegates the real
 * arithmetic operations to another arithmetics instance which also defines the
 * {@link #getRoundingMode() rounding mode} and the {@link #getScale() scale}.
 */
public class ExceptionOnOverflowArithmetics implements DecimalArithmetics {

	private final DecimalArithmetics delegate;

	public ExceptionOnOverflowArithmetics(DecimalArithmetics delegate) {
		this.delegate = delegate;
	}

	@Override
	public int getScale() {
		return delegate.getScale();
	}

	@Override
	public RoundingMode getRoundingMode() {
		return delegate.getRoundingMode();
	}

	@Override
	public OverflowMode getOverflowMode() {
		return OverflowMode.EXCEPTION;
	}

	@Override
	public DecimalArithmetics derive(int scale) {
		final DecimalArithmetics derivedDelegate = delegate.derive(scale);
		return delegate == derivedDelegate ? this : new ExceptionOnOverflowArithmetics(derivedDelegate);
	}

	@Override
	public DecimalArithmetics derive(RoundingMode roundingMode) {
		final DecimalArithmetics derivedDelegate = delegate.derive(roundingMode);
		return delegate == derivedDelegate ? this : new ExceptionOnOverflowArithmetics(derivedDelegate);
	}
	
	@Override
	public DecimalArithmetics derive(OverflowMode overflowMode) {
		final DecimalArithmetics derivedDelegate = delegate.derive(overflowMode);
		return delegate == derivedDelegate ? this : new ExceptionOnOverflowArithmetics(derivedDelegate);
	}

	@Override
	public long one() {
		return delegate.one();
	}

	@Override
	public int signum(long uDecimal) {
		return delegate.signum(uDecimal);
	}

	@Override
	public int compare(long uDecimal1, long uDecimal2) {
		return delegate.compare(uDecimal1, uDecimal2);
	}

	@Override
	public long add(long uDecimal1, long uDecimal2) {
		final long result = delegate.add(uDecimal1, uDecimal2);
		final int sgn1 = Long.signum(uDecimal1);
		final int sgn2 = Long.signum(uDecimal2);
		if (sgn1 == sgn2) {
			if (Long.signum(result) != sgn1) {
				throw new ArithmeticException("overflow: " + uDecimal1 + " + " + uDecimal2 + " = " + result);
			}
		}
		return result;
	}

	@Override
	public long subtract(long uDecimalMinuend, long uDecimalSubtrahend) {
		final long result = delegate.subtract(uDecimalMinuend, uDecimalSubtrahend);
		final int sgn1 = Long.signum(uDecimalMinuend);
		final int sgn2 = Long.signum(uDecimalSubtrahend);
		if (sgn1 != sgn2) {
			if (Long.signum(result) != sgn1) {
				throw new ArithmeticException("overflow: " + uDecimalMinuend + " - " + uDecimalSubtrahend + " = " + result);
			}
		}
		return result;
	}

	private static long checkedMultiplication(long a, long b) {
		final long max = Long.signum(a) == Long.signum(b) ? Long.MAX_VALUE : Long.MIN_VALUE;
		if (a != 0 && (b > 0 && b > max / a || b < 0 && b < max / a)) {
			throw new ArithmeticException("overflow: " + a + " * " + b + " = " + (a * b));
		}
		return a*b;
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		//FIXME checkedMultiplication(..) doesn't work here due to scaling
		return delegate.multiply(uDecimal1, uDecimal2);
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		//FIXME this can overflow e.g. division by very small number
		return delegate.divide(uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public long abs(long uDecimal) {
		final long abs = delegate.abs(uDecimal);
		if (abs < 0) {
			throw new ArithmeticException("overflow: abs(" + uDecimal + ") = " + abs);
		}
		return abs;
	}

	@Override
	public long negate(long uDecimal) {
		final long neg = delegate.negate(uDecimal);
		if (neg != 0 && Long.signum(uDecimal) == Long.signum(neg)) {
			throw new ArithmeticException("overflow: -" + uDecimal + " = " + neg);
		}
		return neg;
	}

	@Override
	public long invert(long uDecimal) {
		return divide(one(), uDecimal);
	}

	@Override
	public long pow(long uDecimalBase, int exponent) {
		//detect overflow through multiplications
		return TruncatingArithmetics.pow(this, uDecimalBase, exponent);
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		if (uDecimal == 0) return 0;
		if (positions < 0) {
			if (positions == Integer.MIN_VALUE) {
				final long result = delegate.shiftRight(uDecimal, -(positions/2));
				return delegate.shiftRight(result, -(positions/2));
			}
			return delegate.shiftRight(uDecimal, positions);
		}
		if (positions < 63) {
			final long pow2 = 1L << positions;
			return checkedMultiplication(uDecimal, pow2);
		}
		throw new ArithmeticException("overflow: " + uDecimal + " << " + positions + " = " + (uDecimal << positions));
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		if (uDecimal == 0) return 0;
		if (positions < 0) {
			if (positions != Integer.MIN_VALUE) {
				return shiftLeft(uDecimal, -positions);
			}
			throw new ArithmeticException("overflow: " + uDecimal + " << " + positions + " = " + (uDecimal << positions));
		}
		//no overflow possible
		return delegate.shiftRight(uDecimal, positions);
	}

	@Override
	public long movePointLeft(long uDecimal, int positions) {
		//TODO impl
		return delegate.movePointLeft(uDecimal, positions);
	}

	@Override
	public long movePointRight(long uDecimal, int positions) {
		//TODO impl
		return delegate.movePointRight(uDecimal, positions);
	}

	@Override
	public long fromLong(long value) {
		return checkedMultiplication(value, one());
	}

	@Override
	public long fromDouble(double value) {
		//TODO impl
		return delegate.fromDouble(value);
	}

	private static long check(BigInteger scaled) {
		if (scaled.bitLength() > 63) {
			throw new ArithmeticException("overflow: " + scaled);
		}
		return scaled.longValue();
	}
	@Override
	public long fromBigInteger(BigInteger value) {
		final BigInteger scaled = value.multiply(BigInteger.valueOf(one()));
		return check(scaled);
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		final BigInteger scaled = value.multiply(BigDecimal.valueOf(one())).setScale(0, getRoundingMode()).toBigInteger();
		return check(scaled);
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		final int targetScale = getScale();
		if (scale == 0) {
			return fromLong(unscaledValue);
		} else if (scale > targetScale) {
			//no overflow possible
			return delegate.fromUnscaled(unscaledValue, targetScale);
		}
		long result = unscaledValue;
		if (scale < targetScale) {
			for (int i = scale; i < targetScale; i++) {
				result = checkedMultiplication(result, 10);
			}
		}
		return result;
	}

	@Override
	public long parse(String value) {
		//TODO impl with exception
		return delegate.parse(value);
	}

	@Override
	public long toLong(long uDecimal) {
		return delegate.toLong(uDecimal);
	}

	@Override
	public double toDouble(long uDecimal) {
		return delegate.toDouble(uDecimal);
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal) {
		return delegate.toBigDecimal(uDecimal);
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal, int scale) {
		return delegate.toBigDecimal(uDecimal, scale);
	}

	@Override
	public String toString(long uDecimal) {
		return delegate.toString(uDecimal);
	}

}
