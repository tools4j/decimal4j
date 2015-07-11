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
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;


/**
 * Contains methods to convert from and to {@link BigDecimal}.
 */
final class BigDecimalConversion {
	
	public static final long bigDecimalToLong(RoundingMode roundingMode, BigDecimal value) {
		// TODO any chance to make this garbage free?
		// Difficult as we cannot look inside the BigDecimal value
		final BigInteger scaled = value//
				.setScale(0, roundingMode)//
				.toBigInteger();
		if (scaled.bitLength() <= 63) {
			return scaled.longValue();
		}
		throw new IllegalArgumentException("Overflow: cannot convert " + value + " to long");
	}
	public static final long bigDecimalToUnscaled(ScaleMetrics scaleMetrics, RoundingMode roundingMode, BigDecimal value) {
		// TODO any chance to make this garbage free?
		// Difficult as we cannot look inside the BigDecimal value
		final BigInteger scaled = value//
				.multiply(scaleMetrics.getScaleFactorAsBigDecimal())//
				.setScale(0, roundingMode)//
				.toBigInteger();
		if (scaled.bitLength() <= 63) {
			return scaled.longValue();
		}
		throw new IllegalArgumentException("Overflow: cannot convert " + value + " to Decimal with scale " + scaleMetrics.getScale());
	}

	public static final BigDecimal unscaledToBigDecimal(ScaleMetrics scaleMetrics, long uDecimal) {
		return BigDecimal.valueOf(uDecimal, scaleMetrics.getScale());
	}
	public static final BigDecimal unscaledToBigDecimal(ScaleMetrics scaleMetrics, RoundingMode roundingMode, long uDecimal, int targetScale) {
		final int sourceScale = scaleMetrics.getScale();
		if (targetScale == sourceScale) {
			return unscaledToBigDecimal(scaleMetrics, uDecimal);
		}
		if (targetScale < sourceScale) {
			final int diff = sourceScale - targetScale;
			if (diff <= 18) {
				final ScaleMetrics diffMetrics = Scales.getScaleMetrics(diff);
				final long rescaled = diffMetrics.getArithmetic(roundingMode).divideByPowerOf10(uDecimal, diff);
				return BigDecimal.valueOf(rescaled, targetScale);
			}
		} else {
			// does it fit in a long?
			final int diff = targetScale - sourceScale;
			if (diff <= 18) {
				final ScaleMetrics diffMetrics = Scales.getScaleMetrics(diff);
				if (diffMetrics.isValidIntegerValue(uDecimal)) {
					final long rescaled = diffMetrics.multiplyByScaleFactor(uDecimal);
					return BigDecimal.valueOf(rescaled, targetScale);
				}
			}
		}
		// let the big decimal deal with such large numbers then
		return BigDecimal.valueOf(uDecimal, sourceScale).setScale(targetScale, roundingMode);
	}

	// no instances
	private BigDecimalConversion() {
		super();
	}
}
