/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 decimal4j (tools4j), Marco Terzer
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
 * Utility class to round double values to an arbitrary decimal precision between 0 and 18. The rounding is efficient
 * and garbage free.
 */
public final class DoubleRounder {

	private final ScaleMetrics scaleMetrics;
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
	 * Creates a rounder with the given scale metrics defining the decimal precision.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics determining the rounding precision
	 * @throws NullPointerException
	 *             if scale metrics is null
	 */
	public DoubleRounder(ScaleMetrics scaleMetrics) {
		this.scaleMetrics = Objects.requireNonNull(scaleMetrics, "scaleMetrics cannot be null");
		this.ulp = scaleMetrics.getRoundingHalfEvenArithmetic().toDouble(1);
	}

	/**
	 * Returns the precision of this rounder, a value between zero and 18.
	 * 
	 * @return this rounder's decimal precision
	 */
	public int getPrecision() {
		return scaleMetrics.getScale();
	}

	/**
	 * Rounds the given double value to the decimal precision of this rounder using {@link RoundingMode#HALF_UP HALF_UP}
	 * rounding.
	 * 
	 * @param value
	 *            the value to round
	 * @return the rounded value
	 * @see #getPrecision()
	 */
	public double round(double value) {
		return round(value, scaleMetrics.getDefaultArithmetic(), scaleMetrics.getRoundingHalfEvenArithmetic(), ulp);
	}

	/**
	 * Rounds the given double value to the decimal precision of this rounder using the specified rounding mode.
	 * 
	 * @param value
	 *            the value to round
	 * @param roundingMode
	 *            the rounding mode indicating how the least significant returned decimal digit of the result is to be
	 *            calculated
	 * @return the rounded value
	 * @see #getPrecision()
	 */
	public double round(double value, RoundingMode roundingMode) {
		return round(value, roundingMode, scaleMetrics.getRoundingHalfEvenArithmetic(), ulp);
	}

	/**
	 * Returns a hash code for this <tt>DoubleRounder</tt> instance.
	 * 
	 * @return a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return scaleMetrics.hashCode();
	}

	/**
	 * Returns true if {@code obj} is a <tt>DoubleRounder</tt> with the same precision as {@code this} rounder instance.
	 * 
	 * @param obj
	 *            the reference object with which to compare
	 * @return true for a double rounder with the same precision as this instance
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof DoubleRounder) {
			return scaleMetrics.equals(((DoubleRounder) obj).scaleMetrics);
		}
		return false;
	}

	/**
	 * Returns a string consisting of the simple class name and the precision.
	 * 
	 * @return a string like "DoubleRounder[precision=7]"
	 */
	@Override
	public String toString() {
		return "DoubleRounder[precision=" + getPrecision() + "]";
	}

	/**
	 * Rounds the given double value to the specified decimal {@code precision} using {@link RoundingMode#HALF_UP
	 * HALF_UP} rounding.
	 * 
	 * @param value
	 *            the value to round
	 * @param precision
	 *            the decimal precision to round to (aka decimal places)
	 * @return the rounded value
	 */
	public static final double round(double value, int precision) {
		final ScaleMetrics sm = toScaleMetrics(precision);
		final DecimalArithmetic halfEvenArith = sm.getRoundingHalfEvenArithmetic();
		return round(value, sm.getDefaultArithmetic(), halfEvenArith, halfEvenArith.toDouble(1));
	}

	/**
	 * Rounds the given double value to the specified decimal {@code precision} using the specified rounding mode.
	 * 
	 * @param value
	 *            the value to round
	 * @param precision
	 *            the decimal precision to round to (aka decimal places)
	 * @param roundingMode
	 *            the rounding mode indicating how the least significant returned decimal digit of the result is to be
	 *            calculated
	 * @return the rounded value
	 */
	public static final double round(double value, int precision, RoundingMode roundingMode) {
		final ScaleMetrics sm = toScaleMetrics(precision);
		final DecimalArithmetic halfEvenArith = sm.getRoundingHalfEvenArithmetic();
		return round(value, roundingMode, halfEvenArith, halfEvenArith.toDouble(1));
	}

	private static final double round(double value, RoundingMode roundingMode, DecimalArithmetic halfEvenArith, double ulp) {
		if (roundingMode == RoundingMode.UNNECESSARY) {
			return checkRoundingUnnecessary(value, halfEvenArith, ulp);
		}
		return round(value, halfEvenArith.deriveArithmetic(roundingMode), halfEvenArith, ulp);
	}

	private static final double round(double value, DecimalArithmetic roundingArith, DecimalArithmetic halfEvenArith, double ulp) {
		//return the value unchanged if
		// (a) the value is infinite or NaN
		// (b) the next double is 2 decimal UPLs away (or more):
		//     in this case no other double value represents the decimal value more accurately
		if (!isFinite(value) || ulp * 2 <= Math.ulp(value)) {
			return value;
		}
		// NOTE: condition (b) above prevents overflows as such cases do not get to here
		final long uDecimal = roundingArith.fromDouble(value);
		return halfEvenArith.toDouble(uDecimal);
	}

	private static final double checkRoundingUnnecessary(double value, DecimalArithmetic halfEvenArith, double ulp) {
		//same condition as in round(..) method above
		if (isFinite(value) && 2 * ulp > Math.ulp(value)) {
			//By definition, rounding is necessary if there is another double value that represents our decimal more
			//accurately. This is the case when we get a different double value after two conversions.
			final long uDecimal = halfEvenArith.fromDouble(value);
			if (halfEvenArith.toDouble(uDecimal) != value) {
				throw new ArithmeticException(
						"Rounding necessary for precision " + halfEvenArith.getScale() + ": " + value);
			}
		}
		return value;
	}

	private static final ScaleMetrics toScaleMetrics(int precision) {
		if (precision < Scales.MIN_SCALE | precision > Scales.MAX_SCALE) {
			throw new IllegalArgumentException(
					"Precision must be in [" + Scales.MIN_SCALE + "," + Scales.MAX_SCALE + "] but was " + precision);
		}
		return Scales.getScaleMetrics(precision);
	}

	/**
	 * Java-7 port of {@code Double#isFinite(double)}.
	 * <p>
	 * Returns {@code true} if the argument is a finite floating-point value; returns {@code false} otherwise (for NaN
	 * and infinity arguments).
	 *
	 * @param d
	 *            the {@code double} value to be tested
	 * @return {@code true} if the argument is a finite floating-point value, {@code false} otherwise.
	 */
	private static boolean isFinite(double d) {
		return Math.abs(d) <= Double.MAX_VALUE;
	}
}
