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
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;

/**
 * Arithmetic implementation with rounding for scales other than zero. If an
 * operation leads to an overflow the result is silently truncated.
 */
public final class UncheckedScaleNfRoundingArithmetic extends AbstractUncheckedScaleNfArithmetic {

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

	public final DecimalRounding getDecimalRounding() {
		return rounding;
	}

	@Override
	public final RoundingMode getRoundingMode() {
		return getDecimalRounding().getRoundingMode();
	}

	@Override
	public final long addUnscaled(long uDecimal, long unscaled, int scale) {
		return Add.addUnscaledUnscaled(getScaleMetrics(), rounding, uDecimal, unscaled, scale);
	}

	@Override
	public final long subtractUnscaled(long uDecimal, long unscaled, int scale) {
		return Sub.subtractUnscaledUnscaled(getScaleMetrics(), rounding, uDecimal, unscaled, scale);
	}

	@Override
	public final long avg(long uDecimal1, long uDecimal2) {
		return Avg.avg(this, rounding, uDecimal1, uDecimal2);
	}

	@Override
	public final long multiply(long uDecimal1, long uDecimal2) {
		return Mul.multiply(this, rounding, uDecimal1, uDecimal2);
	}
	
	@Override
	public final long multiplyByUnscaled(long uDecimal, long unscaled, int scale) {
		return Mul.multiplyByUnscaled(rounding, uDecimal, unscaled, scale);
	}

	@Override
	public final long square(long uDecimal) {
		return Square.square(getScaleMetrics(), rounding, uDecimal);
	}

	@Override
	public final long sqrt(long uDecimal) {
		return Sqrt.sqrt(this, rounding, uDecimal);
	}

	@Override
	public final long divideByLong(long uDecimalDividend, long lDivisor) {
		return Div.divideByLong(rounding, uDecimalDividend, lDivisor);
	}
	
	@Override
	public final long divideByUnscaled(long uDecimal, long unscaled, int scale) {
		return Div.divideByUnscaled(rounding, uDecimal, unscaled, scale);
	}

	@Override
	public final long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Div.divide(this, rounding, uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public final long invert(long uDecimal) {
		return Invert.invert(this, rounding, uDecimal);
	}

	@Override
	public final long pow(long uDecimal, int exponent) {
		return Pow.pow(this, rounding, uDecimal, exponent);
	}

	@Override
	public final long shiftLeft(long uDecimal, int positions) {
		return Shift.shiftLeft(rounding, uDecimal, positions);
	}

	@Override
	public final long shiftRight(long uDecimal, int positions) {
		return Shift.shiftRight(rounding, uDecimal, positions);
	}

	@Override
	public final long multiplyByPowerOf10(long uDecimal, int n) {
		return Pow10.multiplyByPowerOf10(rounding, uDecimal, n);
	}

	@Override
	public final long divideByPowerOf10(long uDecimal, int n) {
		return Pow10.divideByPowerOf10(rounding, uDecimal, n);
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
	public final long fromUnscaled(long unscaledValue, int scale) {
		return UnscaledConversion.unscaledToUnscaled(this, rounding, unscaledValue, scale);
	}

	@Override
	public final long fromBigDecimal(BigDecimal value) {
		return BigDecimalConversion.bigDecimalToUnscaled(getScaleMetrics(), getRoundingMode(), value);
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
	public final long toLong(long uDecimal) {
		return LongConversion.unscaledToLong(getScaleMetrics(), rounding, uDecimal);
	}
	
	@Override
	public long toUnscaled(long uDecimal, int scale) {
		return UnscaledConversion.unscaledToUnscaled(rounding, scale, this, uDecimal);
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
	public final long parse(String value) {
		return StringConversion.parseUnscaledDecimal(this, rounding, value);
	}
}
