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

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.DecimalRounding;


/**
 * Contains static methods to convert between different scales.
 */
final class UnscaledConversion {

	private static final int getScaleDiff(ScaleMetrics scaleMetrics, int scale) {
		final int targetScale = scaleMetrics.getScale();
		final int diffScale = targetScale - scale;
		if (!Checked.isSubtractOverflow(targetScale, scale, diffScale)) {
			return diffScale;
		}
		throw new IllegalArgumentException("Cannot convert from scale " + scale + " to " + targetScale + " (scale difference is out of integer range)");
	}
	public static final long unscaledToLong(DecimalArithmetic arith, long unscaledValue, int scale) {
		try {
			return Pow10.divideByPowerOf10Checked(arith, unscaledValue, scale);
		} catch (ArithmeticException e) {
			throw toIllegalArgumentExceptionOrRethrow(e, unscaledValue, scale, arith.getScale());
		}
	}
	public static final long unscaledToLong(DecimalArithmetic arith, DecimalRounding rounding, long unscaledValue, int scale) {
		try {
			return Pow10.divideByPowerOf10Checked(arith, rounding, unscaledValue, scale);
		} catch (ArithmeticException e) {
			throw toIllegalArgumentExceptionOrRethrow(e, unscaledValue, scale, arith.getScale());
		}
	}
	public static final long unscaledToUnscaled(DecimalArithmetic arith, long unscaledValue, int scale) {
		final int scaleDiff = getScaleDiff(arith.getScaleMetrics(), scale);
		try {
			return Pow10.multiplyByPowerOf10Checked(arith, unscaledValue, scaleDiff);
		} catch (ArithmeticException e) {
			throw toIllegalArgumentExceptionOrRethrow(e, unscaledValue, scale, arith.getScale());
		}
	}
	public static final long unscaledToUnscaled(DecimalArithmetic arith, DecimalRounding rounding, long unscaledValue, int scale) {
		final int scaleDiff = getScaleDiff(arith.getScaleMetrics(), scale);
		try {
			return Pow10.multiplyByPowerOf10Checked(arith, rounding, unscaledValue, scaleDiff);
		} catch (ArithmeticException e) {
			throw toIllegalArgumentExceptionOrRethrow(e, unscaledValue, scale, arith.getScale());
		}
	}
	
	private static IllegalArgumentException toIllegalArgumentExceptionOrRethrow(ArithmeticException e, long unscaledValue, int sourceScale, int targetScale) {
		Exceptions.rethrowIfRoundingNecessary(e); 
		if (targetScale > 0) {
			return new IllegalArgumentException("Overflow: Cannot convert unscaled value " + unscaledValue + " from scale " + sourceScale + " to scale " + targetScale);
		} else {
			return new IllegalArgumentException("Overflow: Cannot convert unscaled value " + unscaledValue + " from scale " + sourceScale + " to long");
		}
	}

	// no instances
	private UnscaledConversion() {
	}
}
