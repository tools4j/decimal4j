package org.decimal4j.arithmetic;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;

/**
 * Base class for arithmetic implementations with overflow check for scales
 * other than zero.
 */
abstract public class AbstractCheckedScaleNfArithmetic extends
		AbstractCheckedArithmetic {

	private final ScaleMetrics scaleMetrics;

	/* The unchecked version of arithmetic with the same rounding mode */
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
