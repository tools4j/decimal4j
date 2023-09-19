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
package org.decimal4j.util;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;

import java.math.RoundingMode;
import java.util.Objects;

/**
 * DoubleRounder Utility <b>(Deprecated)</b>.
 * <p>
 * DoubleRounder sometimes returns counter-intuitive results. The reason is that it performs mathematically
 * correct rounding. For instance <code>DoubleRounder.round(256.025d, 2)</code> will be rounded down to
 * <code>256.02</code> because the double value represented as <code>256.025d</code> is somewhat smaller than the rational
 * value <code>256.025</code> and hence will be rounded down.
 * <p>
 * Notes:
 * <ul>
 * <li>This behaviour is very similar to that of the {@link java.math.BigDecimal#BigDecimal(double) BigDecimal(double)}
 * constructor (but not to {@link java.math.BigDecimal#valueOf(double) valueOf(double)} which uses the string
 * constructor).</li>
 * <li>The problem can be circumvented with a double rounding step to a higher precision first, but it is complicated
 * and we are not going into the details here</li>
 * </ul>
 * For those reasons we <b>cannot recommend to use DoubleRounder</b>.
 */
@Deprecated
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
	 * Returns a hash code for this <code>DoubleRounder</code> instance.
	 * 
	 * @return a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return scaleMetrics.hashCode();
	}

	/**
	 * Returns true if {@code obj} is a <code>DoubleRounder</code> with the same precision as {@code this} rounder instance.
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
		if (!isFinite(value)) {
			return value;
		}
		return round(value, roundingArith.getRoundingMode(), roundingArith.getScaleMetrics().getScaleFactor(), ulp);
	}

	private static final double round(double value, RoundingMode mode, double scaleFactor, double ulp) {
		if (ulp < Math.ulp(value)) {
			return value;
		}
		// NOTE: condition (b) above prevents overflows as such cases do not get to here
		final double floor = floor(value, scaleFactor);
		if (floor == value) {
			return value;
		}
		final double ceil = ceil(value, scaleFactor);
		if (ceil == value) {
			return value;
		}
		switch (mode) {
			case UNNECESSARY:
				throw new ArithmeticException("rounding necessary");
			case FLOOR:
				return floor;
			case CEILING:
				return ceil;
			case UP:
				return value >= 0 ? ceil : floor;
			case DOWN:
				return value >= 0 ? floor : ceil;
			case HALF_UP:
				if (value >= 0) {
					return ceil - value <= value - floor && ceil + floor <= 2 * value ? ceil : floor;
//					return ceil - value <= value - floor ? ceil : floor;
				} else {
					return ceil - value < value - floor ? ceil : floor;
				}
			case HALF_DOWN:
//				if (value >= 0) {
//					return floor(Math.nextDown(value + ulp / 2), scaleFactor);
//				} else {
//					return ceil(Math.nextUp(value - ulp / 2), scaleFactor);
//				}
				if (value >= 0) {
					return ceil - value < value - floor ? ceil : floor;
				} else {
					return ceil - value <= value - floor ? ceil : floor;
				}
			case HALF_EVEN:
				if (2 * value == floor(2 * value, scaleFactor)) {
					final double floorScaled = Math.floor(value * scaleFactor);
					final boolean floorEven = floorScaled == 2 * Math.floor(floorScaled / 2);
					return floorEven ? floor : ceil;
				}
				if (2 * value == ceil(2 * value, scaleFactor)) {
					final double ceilScaled = Math.ceil(value * scaleFactor);
					final boolean ceilEven = ceilScaled == 2 * Math.floor(ceilScaled / 2);
					return ceilEven ? ceil : floor;
				}
				return ceil - value <= value - floor ? ceil : floor;
			default:
				throw new RuntimeException("unknown rounding mode: " + mode);
		}
	}

	private static double floor(final double value, final double scaleFactor) {
		double scaled = value * scaleFactor;
		double floor = Math.floor(scaled) / scaleFactor;
		while (floor > value) {
			scaled = Math.nextDown(scaled);
			floor = Math.floor(scaled) / scaleFactor;
		}
		return floor;
	}

	private static double ceil(final double value, final double scaleFactor) {
		double scaled = value * scaleFactor;
		double ceil = Math.ceil(scaled) / scaleFactor;
		while (ceil < value) {
			scaled = Math.nextUp(scaled);
			ceil = Math.ceil(scaled) / scaleFactor;
		}
		return ceil;
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
