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
package org.decimal4j.api;

import java.math.RoundingMode;

import org.decimal4j.scale.ScaleMetrics;

/**
 * Interface implemented by mutable {@link Decimal} classes of different scales.
 * Mutable Decimals modify their state when performing arithmetic operations;
 * they represent the result after the operation. Arithmetic operations
 * therefore return {@code this} as return value. Note however that the the
 * {@link #getScale() scale} of a Mutable Decimal does not change and remains
 * constant throughout the lifetime of a {@code MutableDecimal} instance.
 * <p>
 * Mutable Decimals may be preferred over {@link ImmutableDecimal} descendants
 * e.g. if the allocation of new objects is undesired or if a chain of
 * operations is performed.
 * <p>
 * Mutable Decimals are <i>NOT</i> thread safe.
 * 
 * @param <S>
 *            the scale metrics type associated with this Decimal
 */
public interface MutableDecimal<S extends ScaleMetrics> extends Decimal<S> {

	/**
	 * Sets {@code this} Decimal to 0 and returns {@code this} now representing
	 * zero.
	 * 
	 * @return {@code this} Decimal after assigning {@code this = 0}
	 */
	MutableDecimal<S> setZero();

	/**
	 * Sets {@code this} Decimal to 1 and returns {@code this} now representing
	 * one.
	 * 
	 * @return {@code this} Decimal after assigning {@code this = 1}
	 */
	MutableDecimal<S> setOne();

	/**
	 * Sets {@code this} Decimal to -1 and returns {@code this} now representing
	 * minus one.
	 * 
	 * @return {@code this} Decimal after assigning {@code this = -1}
	 */
	MutableDecimal<S> setMinusOne();

	/**
	 * Sets {@code this} Decimal to the smallest positive value representable by
	 * this Mutable Decimal and returns {@code this} now representing one ULP.
	 * 
	 * @return {@code this} Decimal after assigning {@code this = ULP}
	 */
	MutableDecimal<S> setUlp();

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} Decimal after assigning the given {@code value}
	 */
	MutableDecimal<S> set(Decimal<S> value);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted to the appropriate scale
	 * @return {@code this} Decimal after assigning the given {@code value}
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(Decimal<?> value, RoundingMode roundingMode);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} Decimal after assigning the given {@code value}
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(long value);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} Decimal after assigning the given {@code value}
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(float value);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a Decimal number
	 * @return {@code this} decimal after assigning the given {@code value}
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(float value, RoundingMode roundingMode);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} Decimal after assigning the given {@code value}
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(double value);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a Decimal number
	 * @return {@code this} Decimal after assigning the given {@code value}
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(double value, RoundingMode roundingMode);

	/**
	 * Sets {@code this} Decimal to the specified {@code unscaledValue} and
	 * returns {@code this} now representing
	 * <code>this = unscaledValue * 10<sup>-scale</sup></code> where scale is
	 * the scale factor of this Mutable Decimal.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @return {@code this} Decimal after assigning
	 *         <code>this = unscaledValue * 10<sup>-scale</sup></code>.
	 * @see #getScaleMetrics()
	 * @see ScaleMetrics#getScaleFactor()
	 */
	MutableDecimal<S> setUnscaled(long unscaledValue);

	/**
	 * Sets {@code this} Decimal to the specified {@code unscaledValue} with the
	 * given {@code scale} and returns {@code this} now representing
	 * <code>this = round(unscaledValue * 10<sup>-scale</sup>)</code>. Note that
	 * the conversion may involve rounding if the specified {@code scale} is
	 * larger than {@link #getScale()}; the default rounding mode is applied in
	 * this case.
	 * <p>
	 * An exception is thrown if the value cannot be represented as a Decimal
	 * with this Mutable Decimal's {@link #getScale() scale} for instance
	 * because it would cause an overflow.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @return {@code this} Decimal after assigning
	 *         <code>this = round(unscaledValue * 10<sup>-scale)</sup></code>.
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal with this
	 *             Mutable Decimal's {@code scale}
	 */
	MutableDecimal<S> setUnscaled(long unscaledValue, int scale);

	/**
	 * Sets {@code this} Decimal to the specified {@code unscaledValue} with the
	 * given {@code scale} and returns {@code this} now representing
	 * <code>this = round(unscaledValue * 10<sup>-scale</sup>)</code>. Note that
	 * the conversion may involve rounding if the specified {@code scale} is
	 * larger than {@link #getScale()}; the specified {@code roundingMode} is
	 * applied in this case.
	 * <p>
	 * An exception is thrown if the value cannot be represented as a Decimal
	 * with this Mutable Decimal's {@link #getScale() scale} for instance
	 * because it would cause an overflow.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted to this Decimal's scale
	 * @return {@code this} Decimal after assigning
	 *         <code>this = round(unscaledValue * 10<sup>-scale</sup>)</code>.
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal with this
	 *             Mutable Decimal's {@code scale}
	 */
	MutableDecimal<S> setUnscaled(long unscaledValue, int scale, RoundingMode roundingMode);

	/**
	 * Parses the given string value and sets {@code this} Decimal to the parsed
	 * {@code value}. An exception is thrown if the value cannot be parsed.
	 * 
	 * @param value
	 *            the string value to parse and set
	 * @return {@code this} Decimal after assigning the parsed value
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 * @throws NumberFormatException
	 *             if the value cannot be parsed
	 */
	MutableDecimal<S> set(String value);

	/**
	 * Parses the given string value and sets {@code this} Decimal to the parsed
	 * {@code value}. An exception is thrown if the value cannot be parsed.
	 * 
	 * @param value
	 *            the string value to parse and set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a decimal number
	 * @return {@code this} Decimal after assigning the parsed value
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 * @throws NumberFormatException
	 *             if the value cannot be parsed
	 */
	MutableDecimal<S> set(String value, RoundingMode roundingMode);
}
