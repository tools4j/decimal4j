package org.decimal4j.arithmetic;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;

/**
 * Arithmetic implementation with rounding for scales other than zero. If an
 * operation leads to an overflow the result is silently truncated.
 */
public class UncheckedScaleNfRoundingArithmetic extends
		AbstractUncheckedScaleNfArithmetic {

	private final DecimalRounding rounding;

	/**
	 * Constructor for decimal arithmetic with given scale, rounding mode and
	 * {@link OverflowMode#UNCHECKED SILENT} overflow mode.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics for this decimal arithmetic
	 * @param roundingMode
	 *            the rounding mode to use for all decimal arithmetic
	 */
	public UncheckedScaleNfRoundingArithmetic(ScaleMetrics scaleMetrics, RoundingMode roundingMode) {
		this(scaleMetrics, DecimalRounding.valueOf(roundingMode));
	}

	/**
	 * Constructor for decimal arithmetic with given scale, rounding mode and
	 * {@link OverflowMode#UNCHECKED SILENT} overflow mode.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics for this decimal arithmetic
	 * @param rounding
	 *            the rounding mode to use for all decimal arithmetic
	 */
	public UncheckedScaleNfRoundingArithmetic(ScaleMetrics scaleMetrics, DecimalRounding rounding) {
		super(scaleMetrics);
		this.rounding = rounding;
	}

	public DecimalRounding getDecimalRounding() {
		return rounding;
	}

	@Override
	public final RoundingMode getRoundingMode() {
		return getDecimalRounding().getRoundingMode();
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		return Mul.multiply(this, rounding, uDecimal1, uDecimal2);
	}

	@Override
	public long square(long uDecimal) {
		return Mul.square(getScaleMetrics(), rounding, uDecimal);
	}

	@Override
	public long sqrt(long uDecimal) {
		return Sqrt.sqrt(this, rounding, uDecimal);
	}

	@Override
	public long divideByLong(long uDecimalDividend, long lDivisor) {
		return Div.divideByLong(rounding, uDecimalDividend, lDivisor);
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Div.divide(this, rounding, uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public long invert(long uDecimal) {
		return Invert.invert(this, rounding, uDecimal);
	}

	@Override
	public long pow(long uDecimal, int exponent) {
		return Pow.pow(this, rounding, uDecimal, exponent);
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		return Shift.shiftLeft(rounding, uDecimal, positions);
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		return Shift.shiftRight(rounding, uDecimal, positions);
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int n) {
		return Pow10.multiplyByPowerOf10(rounding, uDecimal, n);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int n) {
		return Pow10.divideByPowerOf10(rounding, uDecimal, n);
	}

	@Override
	public long round(long uDecimal, int precision) {
		return Round.round(this, rounding, uDecimal, precision);
	}

	@Override
	public long toLong(long uDecimal) {
		final ScaleMetrics scaleMetrics = getScaleMetrics();
		final long truncated = scaleMetrics.divideByScaleFactor(uDecimal);
		final long reminder = scaleMetrics.moduloByScaleFactor(uDecimal);
		return truncated + RoundingUtil.calculateRoundingIncrement(rounding, truncated, reminder, one());
	}

	@Override
	public float toFloat(long uDecimal) {
		return FloatConversion.unscaledToFloat(this, rounding, uDecimal);
	}

	@Override
	public double toDouble(long uDecimal) {
		return DoubleConversion.unscaledToDouble(this, rounding, uDecimal);
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		return value.multiply(getScaleMetrics().getScaleFactorAsBigDecimal()).setScale(0, getRoundingMode()).longValue();//FIXME make garbage free
	}

	@Override
	public long fromFloat(float value) {
		return FloatConversion.floatToUnscaled(this, rounding, value);
	}

	@Override
	public long fromDouble(double value) {
		return DoubleConversion.doubleToUnscaled(this, rounding, value);
	}

	@Override
	public long parse(String value) {
		return Parse.parseUnscaledDecimal(this, rounding, value);
	}
}
