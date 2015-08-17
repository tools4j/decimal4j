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
package org.decimal4j.util;

import java.math.RoundingMode;
import java.util.Objects;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;

/**
 * Utility class to round doubles and floats to an arbitrary decimal precision
 * between 0 and 18. The rounding is efficient and garbage free.
 */
public final class DoubleRounder {

	private final DecimalArithmetic arithmeticsHalfEven;
	private final double ulp;

	/**
	 * Creates a rounder for the given decimal precision.
	 * 
	 * @param precision
	 *            the decimal rounding precision, must be in {@code [0,18]}
	 * @throws IllegalArgumentException
	 *             if precision is negative or larger than 18
	 */
	public DoubleRounder(int precision) {
		this(toScaleMetrics(precision));
	}

	/**
	 * Creates a rounder with the given scale metrics defining the decimal
	 * precision.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics determining the rounding precision
	 * @throws NullPointerException
	 *             if scale metrics is null
	 */
	public DoubleRounder(ScaleMetrics scaleMetrics) {
		Objects.requireNonNull(scaleMetrics, "scaleMetrics cannot be null");
		this.arithmeticsHalfEven = scaleMetrics.getArithmetic(RoundingMode.HALF_EVEN);
		this.ulp = arithmeticsHalfEven.toDouble(1);
	}

	/**
	 * Returns the precision of this rounder, a value between zero and 18.
	 * 
	 * @return this rounder's decimal precision
	 */
	public int getPrecision() {
		return arithmeticsHalfEven.getScale();
	}

	/**
	 * Rounds the given double value to the decimal precision of this rounder
	 * using {@link RoundingMode#HALF_UP HALF_UP} rounding.
	 * 
	 * @param value
	 *            the value to round
	 * @return the rounded value
	 * @see #getPrecision()
	 */
	public double round(double value) {
		return round(value, RoundingMode.HALF_UP);
	}

	/**
	 * Rounds the given double value to the decimal precision of this rounder
	 * using the specified rounding mode.
	 * 
	 * @param value
	 *            the value to round
	 * @param roundingMode
	 *            the rounding mode indicating how the least significant
	 *            returned decimal digit of the result is to be calculated
	 * @return the rounded value
	 * @see #getPrecision()
	 */
	public double round(double value, RoundingMode roundingMode) {
		if (!isFinite(value) || 2*ulp <= Math.ulp(value)) {
			return value;
		}
		final long uDecimal = arithmeticsHalfEven.deriveArithmetic(roundingMode).fromDouble(value);
		return arithmeticsHalfEven.toDouble(uDecimal);
	}

	/**
	 * Rounds the given double value to the specified decimal {@code precision}
	 * using {@link RoundingMode#HALF_UP HALF_UP} rounding.
	 * 
	 * @param value
	 *            the value to round
	 * @param precision
	 *            the decimal precision to round to (aka decimal places)
	 * @return the rounded value
	 */
	public static final double round(double value, int precision) {
		return round(value, precision, RoundingMode.HALF_UP);
	}

	/**
	 * Rounds the given double value to the specified decimal {@code precision}
	 * using the specified rounding mode.
	 * 
	 * @param value
	 *            the value to round
	 * @param precision
	 *            the decimal precision to round to (aka decimal places)
	 * @param roundingMode
	 *            the rounding mode indicating how the least significant
	 *            returned decimal digit of the result is to be calculated
	 * @return the rounded value
	 */
	public static final double round(double value, int precision, RoundingMode roundingMode) {
		final ScaleMetrics scaleMetrics = toScaleMetrics(precision);
		final DecimalArithmetic arith = scaleMetrics.getArithmetic(roundingMode);
		final DecimalArithmetic arithHalfEven = scaleMetrics.getArithmetic(RoundingMode.HALF_EVEN);
		if (!isFinite(value) || 2*arithHalfEven.toDouble(1) <= Math.ulp(value)) {
			return value;
		}
		final long uDecimal = arith.fromDouble(value);
		return arithHalfEven.toDouble(uDecimal);
	}

	private static final ScaleMetrics toScaleMetrics(int precision) {
		if (precision < Scales.MIN_SCALE | precision > Scales.MAX_SCALE) {
			throw new IllegalArgumentException(
					"Precision must be in [" + Scales.MIN_SCALE + "," + Scales.MAX_SCALE + "] but was " + precision);
		}
		return Scales.getScaleMetrics(precision);
	}

    /**
     * Java-7 port of {@link Double#isFinite(double)}.
     * <p>
     * Returns {@code true} if the argument is a finite floating-point
     * value; returns {@code false} otherwise (for NaN and infinity
     * arguments).
     *
     * @param d the {@code double} value to be tested
     * @return {@code true} if the argument is a finite
     * floating-point value, {@code false} otherwise.
     * @see Double#isFinite(double)
     */
    private static boolean isFinite(double d) {
        return Math.abs(d) <= Double.MAX_VALUE;
    }
}
