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

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.DecimalRounding;

/**
 * Arithmetic implementation without rounding but with overflow check for scales
 * other than zero. An exception is thrown if an operation leads to an overflow.
 */
public final class CheckedScaleNfTruncatingArithmetic extends AbstractCheckedScaleNfArithmetic {

	/**
	 * Constructor with scale metrics for this arithmetic.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics
	 */
	public CheckedScaleNfTruncatingArithmetic(ScaleMetrics scaleMetrics) {
		super(scaleMetrics);
	}

	@Override
	public final RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public final CheckedRounding getTruncationPolicy() {
		return CheckedRounding.DOWN;
	}

	@Override
	public final long addUnscaled(long uDecimal, long unscaled, int scale) {
		return Add.addUnscaledUnscaledChecked(this, uDecimal, unscaled, scale);
	}

	@Override
	public final long subtractUnscaled(long uDecimal, long unscaled, int scale) {
		return Sub.subtractUnscaledUnscaledChecked(this, uDecimal, unscaled, scale);
	}

	@Override
	public final long multiplyByUnscaled(long uDecimal, long unscaled, int scale) {
		return Mul.multiplyByUnscaledChecked(this, uDecimal, unscaled, scale);
	}

	@Override
	public final long divideByUnscaled(long uDecimal, long unscaled, int scale) {
		return Div.divideByUnscaledChecked(this, uDecimal, unscaled, scale);
	}

	@Override
	public final long multiply(long uDecimal1, long uDecimal2) {
		return Mul.multiplyChecked(this, uDecimal1, uDecimal2);
	}

	@Override
	public final long square(long uDecimal) {
		return Square.squareChecked(this, uDecimal);
	}

	@Override
	public final long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Div.divideChecked(this, uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public final long pow(long uDecimal, int exponent) {
		return Pow.pow(this, DecimalRounding.DOWN, uDecimal, exponent);
	}

	@Override
	public final long avg(long a, long b) {
		return Avg.avg(a, b);
	}

	@Override
	public final long sqrt(long uDecimal) {
		return Sqrt.sqrt(this, uDecimal);
	}

	@Override
	public final long divideByLong(long uDecimalDividend, long lDivisor) {
		return Checked.divideByLong(this, uDecimalDividend, lDivisor);
	}

	@Override
	public final long divideByPowerOf10(long uDecimal, int positions) {
		return Pow10.divideByPowerOf10Checked(this, uDecimal, positions);
	}

	@Override
	public final long invert(long uDecimal) {
		return Invert.invert(this, uDecimal);
	}

	@Override
	public final long multiplyByPowerOf10(long uDecimal, int positions) {
		return Pow10.multiplyByPowerOf10Checked(this, uDecimal, positions);
	}

	@Override
	public final long shiftLeft(long uDecimal, int positions) {
		return Shift.shiftLeftChecked(this, DecimalRounding.DOWN, uDecimal, positions);
	}

	@Override
	public final long shiftRight(long uDecimal, int positions) {
		return Shift.shiftRightChecked(this, DecimalRounding.DOWN, uDecimal, positions);
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
	public final long fromFloat(float value) {
		return FloatConversion.floatToUnscaled(this, DecimalRounding.DOWN, value);
	}

	@Override
	public final long fromDouble(double value) {
		return DoubleConversion.doubleToUnscaled(this, DecimalRounding.DOWN, value);
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
	public final long toLong(long uDecimal) {
		return LongConversion.unscaledToLong(getScaleMetrics(), uDecimal);
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
	public final long toUnscaled(long uDecimal, int scale) {
		return UnscaledConversion.unscaledToUnscaled(scale, this, uDecimal);
	}
	@Override
	public final long parse(String value) {
		return StringConversion.parseUnscaledDecimal(this, DecimalRounding.DOWN, value);
	}

}
