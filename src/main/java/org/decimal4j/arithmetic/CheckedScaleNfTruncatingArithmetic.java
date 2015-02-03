package org.decimal4j.arithmetic;

import java.math.RoundingMode;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.DecimalRounding;

/**
 * Arithmetic implementation throwing an exception if an operation leads to on
 * overflow. Decimals after the last scale digit are truncated without rounding.
 */
public class CheckedScaleNfTruncatingArithmetic extends AbstractCheckedScaleNfArithmetic {

	public CheckedScaleNfTruncatingArithmetic(ScaleMetrics scaleMetrics) {
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
	public long square(long uDecimal) {
		return Mul.squareChecked(this, uDecimal);
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Div.divideChecked(this, uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public long pow(long uDecimal, int exponent) {
		return Pow.pow(this, DecimalRounding.DOWN, uDecimal, exponent);
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
	public long invert(long uDecimal) {
		return Invert.invert(this, uDecimal);
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int positions) {
		return Pow10.multiplyByPowerOf10Checked(this, uDecimal, positions);
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		return Shift.shiftLeftChecked(this, DecimalRounding.DOWN, uDecimal, positions);
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		return Shift.shiftRightChecked(this, DecimalRounding.DOWN, uDecimal, positions);
	}

	@Override
	public long round(long uDecimal, int precision) {
		return Round.round(this, uDecimal, precision);
	}

	@Override
	public long fromDouble(double value) {
		return DoubleConversion.doubleToUnscaled(this, DecimalRounding.DOWN, value);
	}

}
