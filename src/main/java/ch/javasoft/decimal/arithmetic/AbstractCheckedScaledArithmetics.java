package ch.javasoft.decimal.arithmetic;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Base class for arithmetic implementations implementing those functions where
 * rounding is no issue. Overflow is checked, that is,
 * {@link #getOverflowMode()} returns {@link OverflowMode#CHECKED}.
 */
abstract public class AbstractCheckedScaledArithmetics extends AbstractCheckedArithmetics {

	private final ScaleMetrics scaleMetrics;
	protected final DecimalArithmetics unchecked;

	public AbstractCheckedScaledArithmetics(ScaleMetrics scaleMetrics) {
		this.scaleMetrics = scaleMetrics;
		this.unchecked = scaleMetrics.getArithmetics(getRoundingMode());
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
	public long pow(long uDecimalBase, int exponent) {
		//NOTE: we assume that multiply with this arithmetics does the correct rounding
		return CheckedLongTruncatingArithmetics.pow(this, uDecimalBase, exponent);
	}

	@Override
	public long fromLong(long value) {
		return getScaleMetrics().multiplyByScaleFactorExact(value);
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
