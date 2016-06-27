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

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;

/**
 * Contains static helper methods to round Decimal values.
 */
final class Round {

	/**
	 * Truncates the specified value to the given precision.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param uDecimal
	 *            the unscaled decimal value
	 * @param precision
	 *            the precision to round to
	 * @return <tt>round<sub>DOWN</sub>(uDecimal, precision)</tt>
	 */
	public static final long round(DecimalArithmetic arith, long uDecimal, int precision) {
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final int scale = scaleMetrics.getScale();
		final ScaleMetrics deltaMetrics;
		if (precision == 0) {
			deltaMetrics = scaleMetrics;
		} else if (precision < scale) {
			final int deltaScale = scale - precision;
			if (deltaScale <= 18) {
				deltaMetrics = Scales.getScaleMetrics(scale - precision);
			} else {
				throw new IllegalArgumentException("scale - precision must be <= 18 but was " + deltaScale
						+ " for scale=" + scale + " and precision=" + precision);
			}
		} else {
			// precision >= scale
			return uDecimal;
		}
		return uDecimal - deltaMetrics.moduloByScaleFactor(uDecimal);
	}

	/**
	 * Rounds the specified value to the given precision.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param rounding
	 *            the rounding to apply
	 * @param uDecimal
	 *            the unscaled decimal value
	 * @param precision
	 *            the precision to round to
	 * @return <tt>round(uDecimal, precision)</tt>
	 */
	public static final long round(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal, int precision) {
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final int scale = scaleMetrics.getScale();
		final int deltaScale = scale - precision;
		final ScaleMetrics deltaMetrics;
		if (precision == 0) {
			deltaMetrics = scaleMetrics;
		} else if (precision < scale) {
			if (deltaScale <= 18) {
				deltaMetrics = Scales.getScaleMetrics(scale - precision);
			} else {
				throw new IllegalArgumentException("scale - precision must be <= 18 but was " + deltaScale
						+ " for scale=" + scale + " and precision=" + precision);
			}
		} else {
			// precision >= scale
			return uDecimal;
		}
		if (uDecimal == 0) {
			return 0;
		}
		final long truncatedDigits = deltaMetrics.moduloByScaleFactor(uDecimal);
		final long truncatedValue = uDecimal - truncatedDigits;
		final long truncatedOddEven = truncatedValue >> deltaScale; // move odd
																	// bit into
																	// place for
																	// HALF_EVEN
																	// rounding
		final long roundingInc = Rounding.calculateRoundingIncrement(rounding, truncatedOddEven, truncatedDigits,
				deltaMetrics.getScaleFactor());
		return arith.add(truncatedValue, roundingInc == 0 ? 0 : deltaMetrics.multiplyByScaleFactor(roundingInc));// must
																													// add
																													// via
																													// arith
																													// to
																													// check
																													// for
																													// overflow
	}

	// no instances
	private Round() {
		super();
	}

}
