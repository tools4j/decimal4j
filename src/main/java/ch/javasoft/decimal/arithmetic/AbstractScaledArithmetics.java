package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.ScaleMetrics;

/**
 * Base class for arithmetic implementations implementing those functions where
 * rounding is no issue. Overflow is not checked, that is,
 * {@link #getOverflowMode()} returns {@link OverflowMode#SILENT SILENT}.
 */
abstract public class AbstractScaledArithmetics extends AbstractArithmetics {

	private final int scale;
	private final long one;//10^scale
	
	private transient BigInteger oneBigInteger;
	private transient BigDecimal oneBigDecimal;
	
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
	
	protected BigInteger oneBigInteger() {
		if (oneBigInteger == null) {
			oneBigInteger = BigInteger.valueOf(one());
		}
		return oneBigInteger;
	}
	protected BigDecimal oneBigDecimal() {
		if (oneBigDecimal == null) {
			oneBigDecimal = BigDecimal.valueOf(one());
		}
		return oneBigDecimal;
	}

	@Override
	public long fromBigInteger(BigInteger value) {
		return value.multiply(oneBigInteger()).longValue();
	}

}
