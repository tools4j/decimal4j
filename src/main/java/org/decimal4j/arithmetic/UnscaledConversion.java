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
		return getScaleDiff(scaleMetrics.getScale(), scale);
	}

	private static final int getScaleDiff(int targetScale, int sourceScale) {
		final int diffScale = targetScale - sourceScale;
		if (!Checked.isSubtractOverflow(targetScale, sourceScale, diffScale)) {
			return diffScale;
		}
		throw new IllegalArgumentException("Cannot convert from scale " + sourceScale + " to " + targetScale
				+ " (scale difference is out of integer range)");
	}

	/**
	 * Converts the given {@code unscaledValue} with the specified {@code scale}
	 * to a long value. The value is rounded DOWN if necessary. An exception is
	 * thrown if the conversion is not possible.
	 * 
	 * @param arith
	 *            arithmetic of the target value
	 * @param unscaledValue
	 *            the unscaled value to convert
	 * @param scale
	 *            the scale of {@code unscaledValue}
	 * @return a long value rounded down if necessary
	 * @throws IllegalArgumentException
	 *             if the conversion cannot be performed due to overflow
	 */
	public static final long unscaledToLong(DecimalArithmetic arith, long unscaledValue, int scale) {
		try {
			return Pow10.divideByPowerOf10Checked(arith, unscaledValue, scale);
		} catch (ArithmeticException e) {
			throw toIllegalArgumentExceptionOrRethrow(e, unscaledValue, scale, arith.getScale());
		}
	}

	/**
	 * Converts the given {@code unscaledValue} with the specified {@code scale}
	 * to a long value. The value is rounded using the specified
	 * {@code rounding} if necessary. An exception is thrown if the conversion
	 * is not possible.
	 * 
	 * @param arith
	 *            arithmetic of the target value
	 * @param rounding
	 *            the rounding to apply if rounding is necessary
	 * @param unscaledValue
	 *            the unscaled value to convert
	 * @param scale
	 *            the scale of {@code unscaledValue}
	 * @return long value rounded with given rounding if necessary
	 * @throws IllegalArgumentException
	 *             if the conversion cannot be performed due to overflow
	 * @throws ArithmeticException
	 *             if rounding is necessary and {@code rounding==UNNECESSARY}
	 */
	public static final long unscaledToLong(DecimalArithmetic arith, DecimalRounding rounding, long unscaledValue, int scale) {
		try {
			return Pow10.divideByPowerOf10Checked(arith, rounding, unscaledValue, scale);
		} catch (ArithmeticException e) {
			throw toIllegalArgumentExceptionOrRethrow(e, unscaledValue, scale, arith.getScale());
		}
	}

	/**
	 * Returns an unscaled value of the scale defined by {@code arith} given an
	 * {@code unscaledValue} with its {@code scale}. The value is rounded DOWN
	 * if necessary. An exception is thrown if the conversion is not possible.
	 * 
	 * @param arith
	 *            arithmetic defining the target scale
	 * @param unscaledValue
	 *            the unscaled value to convert
	 * @param scale
	 *            the scale of {@code unscaledValue}
	 * @return the unscaled value in the arithmetic's scale
	 * @throws IllegalArgumentException
	 *             if the conversion cannot be performed due to overflow
	 */
	public static final long unscaledToUnscaled(DecimalArithmetic arith, long unscaledValue, int scale) {
		final int scaleDiff = getScaleDiff(arith.getScaleMetrics(), scale);
		try {
			return Pow10.multiplyByPowerOf10Checked(arith, unscaledValue, scaleDiff);
		} catch (ArithmeticException e) {
			throw toIllegalArgumentExceptionOrRethrow(e, unscaledValue, scale, arith.getScale());
		}
	}

	/**
	 * Returns an unscaled value of the scale defined by {@code arith} given an
	 * {@code unscaledValue} with its {@code scale}. The value is rounded using
	 * the specified {@code rounding} if necessary. An exception is thrown if
	 * the conversion is not possible.
	 * 
	 * @param arith
	 *            arithmetic defining the target scale
	 * @param rounding
	 *            the rounding to apply if rounding is necessary
	 * @param unscaledValue
	 *            the unscaled value to convert
	 * @param scale
	 *            the scale of {@code unscaledValue}
	 * @return the unscaled value in the arithmetic's scale
	 * @throws IllegalArgumentException
	 *             if the conversion cannot be performed due to overflow
	 * @throws ArithmeticException
	 *             if rounding is necessary and {@code rounding==UNNECESSARY}
	 */
	public static final long unscaledToUnscaled(DecimalArithmetic arith, DecimalRounding rounding, long unscaledValue, int scale) {
		final int scaleDiff = getScaleDiff(arith.getScaleMetrics(), scale);
		try {
			return Pow10.multiplyByPowerOf10Checked(arith, rounding, unscaledValue, scaleDiff);
		} catch (ArithmeticException e) {
			throw toIllegalArgumentExceptionOrRethrow(e, unscaledValue, scale, arith.getScale());
		}
	}

	/**
	 * Converts an unscaled value {@code uDecimal} having the scale specified by
	 * {@code arith} into another unscaled value of the provided
	 * {@code targetScale}. The value is rounded DOWN if necessary. An exception
	 * is thrown if the conversion is not possible.
	 * 
	 * @param targetScale
	 *            the scale of the result value
	 * @param arith
	 *            arithmetic defining the source scale
	 * @param uDecimal
	 *            the unscaled value to convert
	 * @return the unscaled value with {@code targetScale}
	 * @throws IllegalArgumentException
	 *             if the conversion cannot be performed due to overflow
	 */
	public static final long unscaledToUnscaled(int targetScale, DecimalArithmetic arith, long uDecimal) {
		final int scaleDiff = getScaleDiff(targetScale, arith.getScale());
		try {
			return Pow10.multiplyByPowerOf10Checked(arith, uDecimal, scaleDiff);
		} catch (ArithmeticException e) {
			throw toIllegalArgumentExceptionOrRethrow(e, uDecimal, arith.getScale(), targetScale);
		}
	}

	/**
	 * Converts an unscaled value {@code uDecimal} having the scale specified by
	 * {@code arith} into another unscaled value of the provided
	 * {@code targetScale}. The value is rounded using the specified
	 * {@code rounding} if necessary. An exception is thrown if the conversion
	 * is not possible.
	 * 
	 * 
	 * @param rounding
	 *            the rounding to apply if rounding is necessary
	 * @param targetScale
	 *            the scale of the result value
	 * @param arith
	 *            arithmetic defining the source scale
	 * @param uDecimal
	 *            the unscaled value to convert
	 * @return the unscaled value with {@code targetScale}
	 * @throws IllegalArgumentException
	 *             if the conversion cannot be performed due to overflow
	 */
	public static final long unscaledToUnscaled(DecimalRounding rounding, int targetScale, DecimalArithmetic arith, long uDecimal) {
		final int scaleDiff = getScaleDiff(targetScale, arith.getScale());
		try {
			return Pow10.multiplyByPowerOf10Checked(arith, rounding, uDecimal, scaleDiff);
		} catch (ArithmeticException e) {
			throw toIllegalArgumentExceptionOrRethrow(e, uDecimal, arith.getScale(), targetScale);
		}
	}

	private static final IllegalArgumentException toIllegalArgumentExceptionOrRethrow(ArithmeticException e, long unscaledValue, int sourceScale, int targetScale) {
		Exceptions.rethrowIfRoundingNecessary(e);
		if (targetScale > 0) {
			return new IllegalArgumentException("Overflow: Cannot convert unscaled value " + unscaledValue
					+ " from scale " + sourceScale + " to scale " + targetScale, e);
		} else {
			return new IllegalArgumentException("Overflow: Cannot convert unscaled value " + unscaledValue
					+ " from scale " + sourceScale + " to long", e);
		}
	}

	// no instances
	private UnscaledConversion() {
	}
}
