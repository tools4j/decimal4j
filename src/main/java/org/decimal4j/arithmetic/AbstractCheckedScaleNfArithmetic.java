package org.decimal4j.arithmetic;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;

/**
 * Base class for arithmetic implementations implementing those functions where
 * rounding is no issue. Overflow is checked, that is,
 * {@link #getOverflowMode()} returns {@link OverflowMode#CHECKED}.
 */
abstract public class AbstractCheckedScaleNfArithmetic extends AbstractCheckedArithmetic {

	private final ScaleMetrics scaleMetrics;
	
	// FIXME why is it called unchecked?
	// FIXME field should not be protected
	protected final DecimalArithmetic unchecked;

	public AbstractCheckedScaleNfArithmetic(ScaleMetrics scaleMetrics) {
		this.scaleMetrics = scaleMetrics;
		this.unchecked = scaleMetrics.getArithmetic(getRoundingMode());
	}
	
	public AbstractCheckedScaleNfArithmetic(ScaleMetrics scaleMetrics, DecimalRounding rounding) {
		this.scaleMetrics = scaleMetrics;
		this.unchecked = scaleMetrics.getArithmetic(rounding.getRoundingMode());
	}

	@Override
	public ScaleMetrics getScaleMetrics() {
		return scaleMetrics;
	}

	@Override
	public OverflowMode getOverflowMode() {
		return OverflowMode.CHECKED;
	}

	@Override
	public long fromLong(long value) {
		return getScaleMetrics().multiplyByScaleFactorExact(value);
	}

	@Override
	public final long fromUnscaled(long unscaledValue, int scale) {
		return Scale.rescale(this, unscaledValue, scale, getScale());
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
	public String toString(long uDecimal) {
		return unchecked.toString(uDecimal);
	}

}
