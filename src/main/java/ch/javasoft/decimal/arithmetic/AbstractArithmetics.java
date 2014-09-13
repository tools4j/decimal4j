package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Base class for all arithmetic implementations. Only operations are
 * implemented that are common irrespective of {@link #getScale() scale},
 * {@link RoundingMode rounding mode} and {@link #getOverflowMode() overflow
 * mode}.
 */
abstract public class AbstractArithmetics implements DecimalArithmetics {

	@Override
	public int getScale() {
		return getScaleMetrics().getScale();
	}
	
	@Override
	public long one() {
		return getScaleMetrics().getScaleFactor();
	}

	@Override
	public int signum(long uDecimal) {
		return (int) ((uDecimal >> 63) | (-uDecimal >>> 63));
	}

	@Override
	public int compare(long uDecimal1, long uDecimal2) {
		return (uDecimal1 < uDecimal2) ? -1 : ((uDecimal1 == uDecimal2) ? 0 : 1);
	}

	@Override
	public long invert(long uDecimal) {
		return divide(one(), uDecimal);
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal) {
		return BigDecimal.valueOf(uDecimal, getScale());
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal, int scale) {
		final BigDecimal bd = toBigDecimal(uDecimal);
		return (scale == getScale()) ? bd : bd.round(new MathContext(scale, getRoundingMode()));
	}
}
