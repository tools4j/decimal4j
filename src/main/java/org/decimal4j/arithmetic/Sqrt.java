/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 decimal4j (tools4j), Marco Terzer
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

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;

/**
 * Provides static methods to calculate square roots of Decimal numbers.
 */
final class Sqrt {
	/**
	 * This mask is used to obtain the value of an int as if it were unsigned.
	 */
	private static final long LONG_MASK = 0xffffffffL;

	/**
	 * Calculates the square root of the specified long value truncating the
	 * result if necessary.
	 * 
	 * @param lValue
	 *            the long value
	 * @return <code>round<sub>DOWN</sub>(lValue)</code>
	 * @throws ArithmeticException
	 *             if {@code lValue < 0}
	 */
	public static final long sqrtLong(long lValue) {
		if (lValue < 0) {
			throw new ArithmeticException("Square root of a negative value: " + lValue);
		}
		// http://www.codecodex.com/wiki/Calculate_an_integer_square_root
		if ((lValue & 0xfff0000000000000L) == 0) {
			return (long) StrictMath.sqrt(lValue);
		}
		final long result = (long) StrictMath.sqrt(2.0d * (lValue >>> 1));
		return result * result - lValue > 0L ? result - 1 : result;
	}

	/**
	 * Calculates the square root of the specified long value rounding the
	 * result if necessary.
	 * 
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param lValue
	 *            the long value
	 * @return <code>round(lValue)</code>
	 * @throws ArithmeticException
	 *             if {@code lValue < 0}
	 */
	public static final long sqrtLong(DecimalRounding rounding, long lValue) {
		if (lValue < 0) {
			throw new ArithmeticException("Square root of a negative value: " + lValue);
		}
		// square root
		// @see
		// http://www.embedded.com/electronics-blogs/programmer-s-toolbox/4219659/Integer-Square-Roots
		long rem = 0;
		long root = 0;
		final int zerosHalf = Long.numberOfLeadingZeros(lValue) >> 1;
		long scaled = lValue << (zerosHalf << 1);
		for (int i = zerosHalf; i < 32; i++) {
			root <<= 1;
			rem = ((rem << 2) + (scaled >>> 62));
			scaled <<= 2;
			root++;
			if (root <= rem) {
				rem -= root;
				root++;
			} else {
				root--;
			}
		}
		final long truncated = root >>> 1;
		if (rem == 0 | rounding == DecimalRounding.DOWN | rounding == DecimalRounding.FLOOR) {
			return truncated;
		}
		return truncated + getRoundingIncrement(rounding, truncated, rem);
	}

	/**
	 * Calculates the square root of the specified unscaled decimal value
	 * truncating the result if necessary.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param uDecimal
	 *            the unscaled decimal value
	 * @return <code>round<sub>DOWN</sub>(uDecimal)</code>
	 * @throws ArithmeticException
	 *             if {@code uDecimal < 0}
	 */
	public static final long sqrt(DecimalArithmetic arith, long uDecimal) {
		return sqrt(arith, DecimalRounding.DOWN, uDecimal);
	}

	/**
	 * Calculates the square root of the specified unscaled decimal value
	 * rounding the result if necessary.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param uDecimal
	 *            the unscaled decimal value
	 * @return <code>round(uDecimal)</code>
	 * @throws ArithmeticException
	 *             if {@code uDecimal < 0}
	 */
	public static final long sqrt(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal) {
		if (uDecimal < 0) {
			throw new ArithmeticException("Square root of a negative value: " + arith.toString(uDecimal));
		}
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();

		// multiply by scale factor into a 128bit integer
		final int lFactor = (int) (uDecimal & LONG_MASK);
		final int hFactor = (int) (uDecimal >>> 32);
		long lScaled;
		long hScaled;
		long product;

		product = scaleMetrics.mulloByScaleFactor(lFactor);
		lScaled = product & LONG_MASK;
		product = scaleMetrics.mulhiByScaleFactor(lFactor) + (product >>> 32);
		hScaled = product >>> 32;
		product = scaleMetrics.mulloByScaleFactor(hFactor) + (product & LONG_MASK);
		lScaled |= ((product & LONG_MASK) << 32);
		hScaled = scaleMetrics.mulhiByScaleFactor(hFactor) + hScaled + (product >>> 32);

		// square root
		// @see
		// http://www.embedded.com/electronics-blogs/programmer-s-toolbox/4219659/Integer-Square-Roots
		int zerosHalf;
		long rem = 0;
		long root = 0;

		// iteration for high 32 bits
		zerosHalf = Long.numberOfLeadingZeros(hScaled) >> 1;
		hScaled <<= (zerosHalf << 1);
		for (int i = zerosHalf; i < 32; i++) {
			root <<= 1;
			rem = ((rem << 2) + (hScaled >>> 62));
			hScaled <<= 2;
			root++;
			if (root <= rem) {
				rem -= root;
				root++;
			} else {
				root--;
			}
		}

		// iteration for low 32 bits (last iteration below)
		zerosHalf = zerosHalf == 32 ? Long.numberOfLeadingZeros(lScaled) >> 1 : 0;
		lScaled <<= (zerosHalf << 1);
		for (int i = zerosHalf; i < 31; i++) {
			root <<= 1;
			rem = ((rem << 2) + (lScaled >>> 62));
			lScaled <<= 2;
			root++;
			if (root <= rem) {
				rem -= root;
				root++;
			} else {
				root--;
			}
		}

		// last iteration needs unsigned compare
		root <<= 1;
		rem = ((rem << 2) + (lScaled >>> 62));
		lScaled <<= 2;
		root++;
		if (Unsigned.isLessOrEqual(root, rem)) {
			rem -= root;
			root++;
		} else {
			root--;
		}

		// round result if necessary
		final long truncated = root >>> 1;
		if (rem == 0 | rounding == DecimalRounding.DOWN | rounding == DecimalRounding.FLOOR) {
			return truncated;
		}
		return truncated + getRoundingIncrement(rounding, truncated, rem);
	}

	// PRECONDITION: rem != 0
	// NOTE: TruncatedPart cannot be 0.5 because this would square to 0.25
	private static final int getRoundingIncrement(DecimalRounding rounding, long truncated, long rem) {
		if (truncated < rem) {
			return rounding.calculateRoundingIncrement(1, truncated, TruncatedPart.GREATER_THAN_HALF);
		}
		return rounding.calculateRoundingIncrement(1, truncated, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
	}

	// no instances
	private Sqrt() {
		super();
	}
}
