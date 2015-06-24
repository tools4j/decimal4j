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

import org.decimal4j.scale.Scale0f;
import org.decimal4j.truncate.DecimalRounding;

/**
 * Arithmetic implementation without rounding for the special case with
 * {@link Scale0f}, that is, for longs. If an operation leads to an overflow the
 * result is silently truncated.
 */
public final class UncheckedScale0fTruncatingArithmetic extends
		AbstractUncheckedScale0fArithmetic {

	/**
	 * The singleton instance.
	 */
	public static final UncheckedScale0fTruncatingArithmetic INSTANCE = new UncheckedScale0fTruncatingArithmetic();

	@Override
	public final RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public final long sqrt(long uDecimal) {
		return Sqrt.sqrtLong(uDecimal);
	}

	@Override
	public final long divide(long uDecimalDividend, long uDecimalDivisor) {
		return uDecimalDividend / uDecimalDivisor;
	}

	@Override
	public final long divideByLong(long uDecimalDividend, long lDivisor) {
		return uDecimalDividend / lDivisor;
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
	public final long invert(long uDecimal) {
		return Invert.invertLong(uDecimal);
	}

	@Override
	public final long pow(long uDecimal, int exponent) {
		return Pow.powLong(this, DecimalRounding.DOWN, uDecimal, exponent);
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
	public final long avg(long a, long b) {
		return Avg.avg(a, b);
	}

	@Override
	public final long round(long uDecimal, int precision) {
		return Round.round(this, uDecimal, precision);
	}

	@Override
	public final double toDouble(long uDecimal) {
		return DoubleConversion.longToDouble(this, uDecimal);
	}

	@Override
	public final float toFloat(long uDecimal) {
		return (float) uDecimal;
	}

	@Override
	public final long fromUnscaled(long unscaledValue, int scale) {
		return UnscaledConversion.unscaledToLong(this, unscaledValue, scale);
	}

	@Override
	public final long fromFloat(float value) {
		return FloatConversion.floatToLong(value);
	}

	@Override
	public final long fromDouble(double value) {
		return DoubleConversion.doubleToLong(value);
	}

	@Override
	public final long fromBigDecimal(BigDecimal value) {
		return BigDecimalConversion.bigDecimalToLong(RoundingMode.DOWN, value);
	}

	@Override
	public final long parse(String value) {
		return StringConversion.parseLong(this, DecimalRounding.DOWN, value);
	}

}
