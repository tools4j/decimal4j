package org.decimal4j.arithmetic;

import java.math.RoundingMode;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;

public class CheckedScaleNfRoundingArithmetic extends AbstractCheckedScaleNfArithmetic {

	private final DecimalRounding rounding;

	/**
	 * Constructor for decimal arithmetic with given scale, rounding mode and
	 * {@link OverflowMode#CHECKED} overflow mode.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics for this decimal arithmetic
	 * @param roundingMode
	 *            the rounding mode to use for all decimal arithmetic
	 */
	public CheckedScaleNfRoundingArithmetic(ScaleMetrics scaleMetrics, RoundingMode roundingMode) {
		this(scaleMetrics, DecimalRounding.valueOf(roundingMode));
	}

	/**
	 * Constructor for decimal arithmetic with given scale, rounding mode and
	 * {@link OverflowMode#CHECKED} overflow mode.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics for this decimal arithmetic
	 * @param rounding
	 *            the rounding mode to use for all decimal arithmetic
	 */
	public CheckedScaleNfRoundingArithmetic(ScaleMetrics scaleMetrics, DecimalRounding rounding) {
		super(scaleMetrics, rounding);
		this.rounding = rounding;
	}
	
	@Override
	public RoundingMode getRoundingMode() {
		return rounding.getRoundingMode();
	}

	@Override
	public long invert(long uDecimal) {
		return Invert.invert(this, rounding, uDecimal);
	}
	
	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		return Mul.multiplyChecked(this, rounding, uDecimal1, uDecimal2);
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int n) {
		return Pow10.multiplyByPowerOf10Checked(this, rounding, uDecimal, n);
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Div.divideChecked(this, rounding, uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int n) {
		return Pow10.divideByPowerOf10Checked(this, rounding, uDecimal, n);
	}

	@Override
	public long square(long uDecimal) {
		return Mul.squareChecked(this, rounding, uDecimal);
	}

	@Override
	public long sqrt(long uDecimal) {
		return Sqrt.sqrt(this, rounding, uDecimal);
	}

	@Override
	public long pow(long uDecimalBase, int exponent) {
		return Pow.pow(this, rounding, uDecimalBase, exponent);
	}

	@Override
	public long shiftLeft(long uDecimal, int n) {
		return Shift.shiftLeftChecked(this, rounding, uDecimal, n);
	}

	@Override
	public long shiftRight(long uDecimal, int n) {
		return Shift.shiftRightChecked(this, rounding, uDecimal, n);
	}

	@Override
	public long round(long uDecimal, int precision) {
		return Round.round(this, rounding, uDecimal, precision);
	}

	@Override
	public long fromFloat(float value) {
		return FloatConversion.floatToUnscaled(this, rounding, value);
	}

	@Override
	public long fromDouble(double value) {
		return DoubleConversion.doubleToUnscaled(this, rounding, value);
	}
}
