package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Arithmetics implementation throwing an exception if an operation leads to on
 * overflow. Decimals after the last scale digit are truncated without rounding.
 */
public class CheckedScaleNfTruncatingArithmetics extends AbstractCheckedScaleNfArithmetics {

	//lazy init
	private Double minDouble;
	private Double maxDouble;

	public CheckedScaleNfTruncatingArithmetics(ScaleMetrics scaleMetrics) {
		super(scaleMetrics);
	}

	@Override
	public RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		return Mul.multiplyChecked(this, uDecimal1, uDecimal2);
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Div.divide(this, uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public long avg(long a, long b) {
		return Avg.avg(a, b);
	}

	@Override
	public long sqrt(long uDecimal) {
		return Sqrt.sqrt(this, uDecimal);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int positions) {
		return Pow10.divideByPowerOf10Checked(this, uDecimal, positions);
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int positions) {
		return Pow10.multiplyByPowerOf10Checked(this, uDecimal, positions);
	}

	@Override
	public long fromDouble(double value) {
		initDoubleMinMax();
		if (value <= maxDouble & value >= minDouble) { 
			return unchecked.fromDouble(value);
		}
		throw new ArithmeticException("overflow for conversion from double: " + value);
	}

	private void initDoubleMinMax() {
		if (minDouble == null) {
			minDouble = toDouble(Long.MIN_VALUE);
		}
		if (maxDouble == null) {
			maxDouble = toDouble(Long.MAX_VALUE);
		}
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		if (scale == 0) {
			return fromLong(unscaledValue);
		}
		return Pow10.multiplyByPowerOf10Checked(this, unscaledValue, getScale() - scale);
	}

}
