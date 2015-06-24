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

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.DecimalRounding;


/**
 * Contains methods to convert from and to long.
 */
final class LongConversion {
	
	public static final long longToUnscaledUnchecked(ScaleMetrics scaleMetrics, long value) {
		return scaleMetrics.multiplyByScaleFactor(value);
	}
	public static final long longToUnscaled(ScaleMetrics scaleMetrics, long value) {
		if (scaleMetrics.isValidIntegerValue(value)) {
			return scaleMetrics.multiplyByScaleFactor(value);
		}
		throw new IllegalArgumentException("Overflow: cannot convert " + value + " to Decimal with scale " + scaleMetrics.getScale());
	}

	public static final long unscaledToLong(ScaleMetrics scaleMetrics, long uDecimal) {
		return scaleMetrics.divideByScaleFactor(uDecimal);
	}
	public static final long unscaledToLong(ScaleMetrics scaleMetrics, DecimalRounding rounding, long uDecimal) {
		final long truncated = scaleMetrics.divideByScaleFactor(uDecimal);
		final long remainder = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated);
		return truncated + RoundingUtil.calculateRoundingIncrement(rounding, truncated, remainder, scaleMetrics.getScaleFactor());
	}

	// no instances
	private LongConversion() {
		super();
	}
}
