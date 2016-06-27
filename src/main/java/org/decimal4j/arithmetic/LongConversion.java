/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 decimal4j (tools4j), Marco Terzer
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

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.DecimalRounding;

/**
 * Contains methods to convert from and to long.
 */
final class LongConversion {

	/**
	 * Converts the specified long value to an unscaled value of the scale
	 * defined by the given {@code scaleMetrics}. Performs no overflow checks.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics defining the result scale
	 * @param value
	 *            the long value to convert
	 * @return the value converted to the scale defined by {@code scaleMetrics}
	 */
	public static final long longToUnscaledUnchecked(ScaleMetrics scaleMetrics, long value) {
		return scaleMetrics.multiplyByScaleFactor(value);
	}

	/**
	 * Converts the specified long value to an unscaled value of the scale
	 * defined by the given {@code scaleMetrics}. An exception is thrown if an
	 * overflow occurs.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics defining the result scale
	 * @param value
	 *            the long value to convert
	 * @return the value converted to the scale defined by {@code scaleMetrics}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a Decimal
	 *             with the scale of this factory
	 */
	public static final long longToUnscaled(ScaleMetrics scaleMetrics, long value) {
		if (scaleMetrics.isValidIntegerValue(value)) {
			return scaleMetrics.multiplyByScaleFactor(value);
		}
		throw new IllegalArgumentException(
				"Overflow: cannot convert " + value + " to Decimal with scale " + scaleMetrics.getScale());
	}

	/**
	 * Converts the specified unscaled value to a long truncating the result if
	 * necessary.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics associated with {@code uDecimal}
	 * @param uDecimal
	 *            the unscaled decimal value to convert
	 * @return <tt>round<sub>DOWN</sub>(uDecimal)</tt>
	 */
	public static final long unscaledToLong(ScaleMetrics scaleMetrics, long uDecimal) {
		return scaleMetrics.divideByScaleFactor(uDecimal);
	}

	/**
	 * Converts the specified unscaled value to a long rounding the result if
	 * necessary.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics associated with {@code uDecimal}
	 * @param rounding
	 *            the rounding to apply during the conversion if necessary
	 * @param uDecimal
	 *            the unscaled decimal value to convert
	 * @return <tt>round(uDecimal)</tt>
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	public static final long unscaledToLong(ScaleMetrics scaleMetrics, DecimalRounding rounding, long uDecimal) {
		final long truncated = scaleMetrics.divideByScaleFactor(uDecimal);
		final long remainder = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated);
		return truncated
				+ Rounding.calculateRoundingIncrement(rounding, truncated, remainder, scaleMetrics.getScaleFactor());
	}

	// no instances
	private LongConversion() {
		super();
	}
}
