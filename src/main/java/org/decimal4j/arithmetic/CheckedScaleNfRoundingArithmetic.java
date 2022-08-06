/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.arithmetic;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;

/**
 * Arithmetic implementation with rounding and overflow check for scales other
 * than zero. An exception is thrown if an operation leads to an overflow.
 */
public final class CheckedScaleNfRoundingArithmetic extends AbstractCheckedScaleNfArithmetic {

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
		super(scaleMetrics);
		this.rounding = rounding;
	}

	@Override
	public final RoundingMode getRoundingMode() {
		return rounding.getRoundingMode();
	}

	@Override
	public final CheckedRounding getTruncationPolicy() {
		return CheckedRounding.valueOf(getRoundingMode());
	}

	@Override
	public final long addUnscaled(long uDecimal, long unscaled, int scale) {
		return Add.addUnscaledUnscaledChecked(this, rounding, uDecimal, unscaled, scale);
	}

	@Override
	public final long subtractUnscaled(long uDecimal, long unscaled, int scale) {
		return Sub.subtractUnscaledUnscaledChecked(this, rounding, uDecimal, unscaled, scale);
	}

	@Override
	public final long multiplyByUnscaled(long uDecimal, long unscaled, int scale) {
		return Mul.multiplyByUnscaledChecked(this, rounding, uDecimal, unscaled, scale);
	}

	@Override
	public final long divideByUnscaled(long uDecimal, long unscaled, int scale) {
		return Div.divideByUnscaledChecked(this, rounding, uDecimal, unscaled, scale);
	}

	@Override
	public final long avg(long uDecimal1, long uDecimal2) {
		return Avg.avg(this, rounding, uDecimal1, uDecimal2);
	}

	@Override
	public final long invert(long uDecimal) {
		return Invert.invert(this, rounding, uDecimal);
	}

	@Override
	public final long multiply(long uDecimal1, long uDecimal2) {
		return Mul.multiplyChecked(this, rounding, uDecimal1, uDecimal2);
	}

	@Override
	public final long multiplyByPowerOf10(long uDecimal, int n) {
		return Pow10.multiplyByPowerOf10Checked(this, rounding, uDecimal, n);
	}

	@Override
	public final long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Div.divideChecked(this, rounding, uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public final long divideByLong(long uDecimalDividend, long lDivisor) {
		return Div.divideByLongChecked(this, rounding, uDecimalDividend, lDivisor);
	}

	@Override
	public final long divideByPowerOf10(long uDecimal, int n) {
		return Pow10.divideByPowerOf10Checked(this, rounding, uDecimal, n);
	}

	@Override
	public final long square(long uDecimal) {
		return Square.squareChecked(this, rounding, uDecimal);
	}

	@Override
	public final long sqrt(long uDecimal) {
		return Sqrt.sqrt(this, rounding, uDecimal);
	}

	@Override
	public final long pow(long uDecimalBase, int exponent) {
		return Pow.pow(this, rounding, uDecimalBase, exponent);
	}

	@Override
	public final long shiftLeft(long uDecimal, int n) {
		return Shift.shiftLeftChecked(this, rounding, uDecimal, n);
	}

	@Override
	public final long shiftRight(long uDecimal, int n) {
		return Shift.shiftRightChecked(this, rounding, uDecimal, n);
	}

	@Override
	public final long round(long uDecimal, int precision) {
		return Round.round(this, rounding, uDecimal, precision);
	}

	@Override
	public final long fromLong(long value) {
		return LongConversion.longToUnscaled(getScaleMetrics(), value);
	}

	@Override
	public final long fromFloat(float value) {
		return FloatConversion.floatToUnscaled(this, rounding, value);
	}

	@Override
	public final long fromDouble(double value) {
		return DoubleConversion.doubleToUnscaled(this, rounding, value);
	}

	@Override
	public final long fromUnscaled(long unscaledValue, int scale) {
		return UnscaledConversion.unscaledToUnscaled(this, rounding, unscaledValue, scale);
	}

	@Override
	public final long fromBigDecimal(BigDecimal value) {
		return BigDecimalConversion.bigDecimalToUnscaled(getScaleMetrics(), getRoundingMode(), value);
	}

	@Override
	public final long toLong(long uDecimal) {
		return LongConversion.unscaledToLong(getScaleMetrics(), rounding, uDecimal);
	}

	@Override
	public final float toFloat(long uDecimal) {
		return FloatConversion.unscaledToFloat(this, rounding, uDecimal);
	}

	@Override
	public final double toDouble(long uDecimal) {
		return DoubleConversion.unscaledToDouble(this, rounding, uDecimal);
	}

	@Override
	public final long toUnscaled(long uDecimal, int scale) {
		return UnscaledConversion.unscaledToUnscaled(rounding, scale, this, uDecimal);
	}

	@Override
	public final long parse(String value) {
		return StringConversion.parseUnscaledDecimal(this, rounding, value, 0, value.length());
	}
	
	@Override
	public final long parse(CharSequence value, int start, int end) {
		return StringConversion.parseUnscaledDecimal(this, rounding, value, start, end);
	}
}
