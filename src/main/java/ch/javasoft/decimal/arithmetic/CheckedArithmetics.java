package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.ScaleMetrics;

/**
 * Throws an exception on overflows as indicated by {@link #getOverflowMode()}
 * returning {@link OverflowMode#CHECKED EXCEPTION}. Delegates the real
 * arithmetic operations to another arithmetics instance which also defines the
 * {@link #getRoundingMode() rounding mode} and the {@link #getScale() scale}.
 */
public class CheckedArithmetics implements DecimalArithmetics {

	private final ScaleMetrics scaleMetrics;
	private final DecimalArithmetics unchecked;
	
	public CheckedArithmetics(ScaleMetrics scaleMetrics) {
		this.scaleMetrics = scaleMetrics;
		this.unchecked = scaleMetrics.getTruncatingArithmetics();
	}

	@Override
	public ScaleMetrics getScaleMetrics() {
		return scaleMetrics;
	}

	@Override
	public int getScale() {
		return scaleMetrics.getScale();
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
		return scaleMetrics.getScaleFactor();
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
		return CheckedLongArithmetics.add(this, uDecimal1, uDecimal2);
	}

	@Override
	public long subtract(long uDecimalMinuend, long uDecimalSubtrahend) {
		return CheckedLongArithmetics.subtract(this, uDecimalMinuend, uDecimalSubtrahend);
	}

	@Override
	public long multiplyByLong(long uDecimal, long lValue) {
		return CheckedLongArithmetics.multiply(this, uDecimal, lValue);
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		final ScaleMetrics scaleMetrics = getScaleMetrics();
		final int scale = scaleMetrics.getScale();
		final long i1 = scaleMetrics.divideByScaleFactor(uDecimal1);
		final long i2 = scaleMetrics.divideByScaleFactor(uDecimal2);
		final long f1 = uDecimal1 - scaleMetrics.multiplyByScaleFactor(i1);
		final long f2 = uDecimal2 - scaleMetrics.multiplyByScaleFactor(i2);
		final long i1xi2 = CheckedLongArithmetics.multiply(this, i1, i2);
		final long i1xf2 = CheckedLongArithmetics.multiply(this, i1, f2);
		final long i2xf1 = CheckedLongArithmetics.multiply(this, i2, f1);
		final long f1xf2;
		if (scale <= 9) {
			//product fits, hence unchecked
			f1xf2 = scaleMetrics.divideByScaleFactor(f1 * f2);
		} else {
			//product does not fit in long, divide first to fit, then remainder
			final ScaleMetrics m1 = ScaleMetrics.valueOf(scale - 9);
			final ScaleMetrics m2 = ScaleMetrics.valueOf(18 - scale);
			final long tmp = m1.divideByScaleFactor(f1) * m1.divideByScaleFactor(f2);
			f1xf2 = m2.divideByScaleFactor(tmp);
		}
		//add it all up now, every addition checked
		long result = scaleMetrics.multiplyByScaleFactorExact(i1xi2);
		result = CheckedLongArithmetics.add(this, result, i1xf2);
		result = CheckedLongArithmetics.add(this, result, i2xf1);
		result = CheckedLongArithmetics.add(this, result, f1xf2);
		return result;
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		//FIXME this can overflow e.g. division by very small number
		return unchecked.divide(uDecimalDividend, uDecimalDivisor);
	}
	
	@Override
	public long divideByLong(long uDecimalDividend, long lDivisor) {
		return CheckedLongArithmetics.divide(this, uDecimalDividend, lDivisor);
	}

	@Override
	public long abs(long uDecimal) {
		return CheckedLongArithmetics.abs(this, uDecimal);
	}

	@Override
	public long negate(long uDecimal) {
		return CheckedLongArithmetics.negate(this, uDecimal);
	}

	@Override
	public long invert(long uDecimal) {
		return divide(one(), uDecimal);
	}

	@Override
	public long pow(long uDecimalBase, int exponent) {
		return CheckedLongArithmetics.pow(this, uDecimalBase, exponent);
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		return CheckedLongArithmetics.shiftLeft(this, uDecimal, positions);
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		return CheckedLongArithmetics.shiftRight(this, uDecimal, positions);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int positions) {
		return CheckedLongArithmetics.divideByPowerOf10(this, uDecimal, positions);
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int positions) {
		return CheckedLongArithmetics.multiplyByPowerOf10(this, uDecimal, positions);
	}

	@Override
	public long fromLong(long value) {
		return getScaleMetrics().multiplyByScaleFactorExact(value);
	}

	@Override
	public long fromDouble(double value) {
		//TODO impl
		return unchecked.fromDouble(value);
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
		if (scale == 0 || unscaledValue == 0) {
			return unscaledValue;
		}
		final int scaleTarget = getScale();
		if (scale >= scaleTarget) {
			final int scaleDiff = scale - scaleTarget;
			if (scaleDiff <= 18) {
				final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(scaleDiff);
				return scaleMetrics.multiplyByScaleFactorExact(unscaledValue);
			}
			throw new ArithmeticException("overflow: " + unscaledValue + " * 10^" + scale);
		} else {
			final int scaleDiff = scaleTarget - scale;
			if (scaleDiff <= 18) {
				final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(scaleDiff);
				return scaleMetrics.divideByScaleFactor(unscaledValue);
			}
			return 0;
		}
	}

	@Override
	public long parse(String value) {
		//TODO impl with exception
		return unchecked.parse(value);
	}

	@Override
	public long toLong(long uDecimal) {
		return unchecked.toLong(uDecimal);
	}

	@Override
	public float toFloat(long uDecimal) {
		return unchecked.toFloat(uDecimal);
	}
	@Override
	public double toDouble(long uDecimal) {
		return unchecked.toDouble(uDecimal);
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal) {
		return unchecked.toBigDecimal(uDecimal);
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal, int scale) {
		return unchecked.toBigDecimal(uDecimal, scale);
	}

	@Override
	public String toString(long uDecimal) {
		return unchecked.toString(uDecimal);
	}

}
