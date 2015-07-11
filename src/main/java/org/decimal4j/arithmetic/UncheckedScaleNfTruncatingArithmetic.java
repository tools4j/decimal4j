/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
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

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;

/**
 * Arithmetic implementation without rounding for scales other than zero. If an
 * operation leads to an overflow the result is silently truncated.
 */
public final class UncheckedScaleNfTruncatingArithmetic extends AbstractUncheckedScaleNfArithmetic implements
		DecimalArithmetic {

	/**
	 * Constructor for silent decimal arithmetic with given scale, truncating
	 * {@link RoundingMode#DOWN DOWN} rounding mode and
	 * {@link OverflowMode#UNCHECKED SILENT} overflow mode.
	 * 
	 * @param scaleMetrics
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative or uneven
	 */
	public UncheckedScaleNfTruncatingArithmetic(ScaleMetrics scaleMetrics) {
		super(scaleMetrics);
	}

	@Override
	public final RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public final long addUnscaled(long uDecimal, long unscaled, int scale) {
		return Add.addUnscaledUnscaled(getScaleMetrics(), uDecimal, unscaled, scale);
	}

	@Override
	public final long subtractUnscaled(long uDecimal, long unscaled, int scale) {
		return Sub.subtractUnscaledUnscaled(getScaleMetrics(), uDecimal, unscaled, scale);
	}

	@Override
	public final long multiply(long uDecimal1, long uDecimal2) {
		return Mul.multiply(this, uDecimal1, uDecimal2);
	}

	@Override
	public long multiplyByUnscaled(long uDecimal, long unscaled, int scale) {
		return Mul.multiplyByUnscaled(uDecimal, unscaled, scale);
	}

	@Override
	public final long square(long uDecimal) {
		return Square.square(getScaleMetrics(), uDecimal);
	}

	@Override
	public final long sqrt(long uDecimal) {
		return Sqrt.sqrt(this, uDecimal);
	}

	@Override
	public final long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Div.divide(this, uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public final long divideByLong(long uDecimalDividend, long lDivisor) {
		return uDecimalDividend / lDivisor;
	}

	@Override
	public long divideByUnscaled(long uDecimal, long unscaled, int scale) {
		return Div.divideByUnscaled(uDecimal, unscaled, scale);
	}

	@Override
	public final long invert(long uDecimal) {
		return Invert.invert(this, uDecimal);
	}

	@Override
	public final long avg(long a, long b) {
		return Avg.avg(a, b);
	}

	@Override
	public final long multiplyByPowerOf10(long uDecimal, int positions) {
		return Pow10.multiplyByPowerOf10(uDecimal, positions);
	}

	@Override
	public final long divideByPowerOf10(long uDecimal, int positions) {
		return Pow10.divideByPowerOf10(uDecimal, positions);
	}

	@Override
	public final long pow(long uDecimal, int exponent) {
		return Pow.pow(this, DecimalRounding.DOWN, uDecimal, exponent);
	}

	@Override
	public final long shiftLeft(long uDecimal, int positions) {
		return Shift.shiftLeft(DecimalRounding.DOWN, uDecimal, positions);
	}

	@Override
	public final long shiftRight(long uDecimal, int positions) {
		return Shift.shiftRight(DecimalRounding.DOWN, uDecimal, positions);
	}

	@Override
	public final long round(long uDecimal, int precision) {
		return Round.round(this, uDecimal, precision);
	}

	@Override
	public final long fromLong(long value) {
		return LongConversion.longToUnscaled(getScaleMetrics(), value);
	}

	@Override
	public final long fromUnscaled(long unscaledValue, int scale) {
		return UnscaledConversion.unscaledToUnscaled(this, unscaledValue, scale);
	}

	@Override
	public final long fromBigDecimal(BigDecimal value) {
		return BigDecimalConversion.bigDecimalToUnscaled(getScaleMetrics(), RoundingMode.DOWN, value);
	}

	@Override
	public final long fromFloat(float value) {
		return FloatConversion.floatToUnscaled(this, value);
	}

	@Override
	public final long fromDouble(double value) {
		return DoubleConversion.doubleToUnscaled(this, value);
	}

	@Override
	public final long toLong(long uDecimal) {
		return getScaleMetrics().divideByScaleFactor(uDecimal);
	}

	@Override
	public long toUnscaled(long uDecimal, int scale) {
		return UnscaledConversion.unscaledToUnscaled(scale, this, uDecimal);
	}

	@Override
	public final float toFloat(long uDecimal) {
		return FloatConversion.unscaledToFloat(this, uDecimal);
	}

	@Override
	public final double toDouble(long uDecimal) {
		return DoubleConversion.unscaledToDouble(this, uDecimal);
	}

	@Override
	public final long parse(String value) {
		return StringConversion.parseUnscaledDecimal(this, DecimalRounding.DOWN, value);
	}
}
