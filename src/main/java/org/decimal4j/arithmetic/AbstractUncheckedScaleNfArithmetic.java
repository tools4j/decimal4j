package org.decimal4j.arithmetic;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.OverflowMode;

/**
 * Base class for arithmetic implementations implementing those functions where
 * rounding is no issue. Overflow is not checked, that is,
 * {@link #getOverflowMode()} returns {@link OverflowMode#UNCHECKED}.
 */
abstract public class AbstractUncheckedScaleNfArithmetic extends
		AbstractUncheckedArithmetic {

	private final ScaleMetrics scaleMetrics;
	private final int scale;
	private final long one;//10^scale

	public AbstractUncheckedScaleNfArithmetic(ScaleMetrics scaleMetrics) {
		this.scaleMetrics = scaleMetrics;
		this.scale = scaleMetrics.getScale();
		this.one = scaleMetrics.getScaleFactor();
	}

	@Override
	public ScaleMetrics getScaleMetrics() {
		return scaleMetrics;
	}

	@Override
	public int getScale() {
		return scale;
	}

	@Override
	public long one() {
		return one;
	}

	@Override
	public final long fromUnscaled(long unscaledValue, int scale) {
		return Scale.rescale(this, unscaledValue, scale, getScale());
	}

	@Override
	public String toString(long uDecimal) {
		final int scale = getScale();
		final int negativeOffset = uDecimal < 0 ? 1 : 0;
		final StringBuilder sb = new StringBuilder(scale + 2 + negativeOffset);
		sb.append(uDecimal);
		final int len = sb.length();
		if (len <= scale + negativeOffset) {
			//Long.MAX_VALUE = 9,223,372,036,854,775,807
			sb.insert(negativeOffset, "0.00000000000000000000", 0, 2 + scale - len + negativeOffset);
		} else {
			sb.insert(len - scale, '.');
		}
		return sb.toString();
	}
}
