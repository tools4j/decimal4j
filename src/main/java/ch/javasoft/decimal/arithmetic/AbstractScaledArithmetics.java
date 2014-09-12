package ch.javasoft.decimal.arithmetic;

import java.math.BigInteger;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.ScaleMetrics;

/**
 * Base class for arithmetic implementations implementing those functions where
 * rounding is no issue. Overflow is not checked, that is,
 * {@link #getOverflowMode()} returns {@link OverflowMode#STANDARD SILENT}.
 */
abstract public class AbstractScaledArithmetics extends AbstractArithmetics {

	private final int scale;
	private final long one;//10^scale
	
	public AbstractScaledArithmetics(ScaleMetrics scaleMetrics) {
		super(scaleMetrics);
		this.scale = scaleMetrics.getScale();
		this.one = scaleMetrics.getScaleFactor();
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
	public long fromBigInteger(BigInteger value) {
		return value.multiply(getScaleMetrics().getScaleFactorAsBigInteger()).longValue();
	}

}
