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
package org.decimal4j.factory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.ImmutableDecimal;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.scale.ScaleMetrics;

/**
 * Factory for {@link Decimal} values and Decimal arrays of the
 * {@link #getScale() scale} defined by {@code <S>}.
 *
 * @param <S>
 *            the {@link #getScaleMetrics() scale metrics} type associated with
 *            decimals created by this factory
 */
public interface DecimalFactory<S extends ScaleMetrics> {
	/**
	 * Returns the scale metrics type associated with Decimal values created by
	 * this factory.
	 * 
	 * @return the scale metrics defining the scale for Decimal values created
	 *         by this factory
	 */
	S getScaleMetrics();

	/**
	 * Returns the scale of values created by this factory.
	 * 
	 * @return the scale for Decimal values created by this factory
	 */
	int getScale();

	/**
	 * Returns the implementing class for immutable values.
	 * 
	 * @return the implementation type for immutable Decimal values
	 */
	Class<? extends ImmutableDecimal<S>> immutableType();

	/**
	 * Returns the implementing class for mutable values.
	 * 
	 * @return the implementation type for mutable Decimal values
	 */
	Class<? extends MutableDecimal<S>> mutableType();

	/**
	 * Returns a factory for the given {@code scale}.
	 * 
	 * @param scale
	 *            the scale of Decimal numbers created by the returned factory
	 * @return a decimal factory for numbers with the given scale
	 */
	DecimalFactory<?> deriveFactory(int scale);

	/**
	 * Returns a factory for the given {@code scaleMetrics}.
	 * 
	 * @param scaleMetrics
	 *            the metrics defining the scale of the Decimal numbers created
	 *            by the returned factory
	 * @param <S>
	 *            the generic type for {@code scaleMetrics}
	 * @return a decimal factory for numbers with the scale specified by
	 *         {@code scaleMetrics}
	 */
	@SuppressWarnings("hiding")
	<S extends ScaleMetrics> DecimalFactory<S> deriveFactory(S scaleMetrics);

	/**
	 * Returns a new immutable Decimal whose value is numerically equal to that
	 * of the specified {@code long} value. An exception is thrown if the
	 * specified value is too large to be represented as a Decimal of this
	 * factory's {@link #getScale() scale}.
	 *
	 * @param value
	 *            long value to convert into an immutable Decimal value
	 * @return a Decimal value numerically equal to the specified {@code long}
	 *         value
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a Decimal
	 *             with the scale of this factory
	 */
	ImmutableDecimal<S> valueOf(long value);

	/**
	 * Returns a new immutable Decimal whose value is calculated by rounding the
	 * specified {@code float} argument to the {@link #getScale() scale} of this
	 * factory using {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception
	 * is thrown if the specified value is too large to be represented as a
	 * Decimal of this factory's scale.
	 *
	 * @param value
	 *            float value to convert into an immutable Decimal value
	 * @return a Decimal calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is
	 *             too large for the float to be represented as a
	 *             {@code Decimal} with the scale of this factory
	 */
	ImmutableDecimal<S> valueOf(float value);

	/**
	 * Returns a new immutable Decimal whose value is calculated by rounding the
	 * specified {@code float} argument to the {@link #getScale() scale} of this
	 * factory using the specified {@code roundingMode}. An exception is thrown
	 * if the specified value is too large to be represented as a Decimal of
	 * this factory's scale.
	 *
	 * @param value
	 *            float value to convert into an immutable Decimal value
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a Decimal calculated as: <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is
	 *             too large for the float to be represented as a
	 *             {@code Decimal} with the scale of this factory
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	ImmutableDecimal<S> valueOf(float value, RoundingMode roundingMode);

	/**
	 * Returns a new immutable Decimal whose value is calculated by rounding the
	 * specified {@code double} argument to the {@link #getScale() scale} of
	 * this factory using {@link RoundingMode#HALF_UP HALF_UP} rounding. An
	 * exception is thrown if the specified value is too large to be represented
	 * as a Decimal of this factory's scale.
	 *
	 * @param value
	 *            double value to convert into an immutable Decimal value
	 * @return a Decimal calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is
	 *             too large for the double to be represented as a
	 *             {@code Decimal} with the scale of this factory
	 */
	ImmutableDecimal<S> valueOf(double value);

	/**
	 * Returns a new immutable Decimal whose value is calculated by rounding the
	 * specified {@code double} argument to the {@link #getScale() scale} of
	 * this factory using the specified {@code roundingMode}. An exception is
	 * thrown if the specified value is too large to be represented as a Decimal
	 * of this factory's scale.
	 *
	 * @param value
	 *            double value to convert into an immutable Decimal value
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a Decimal calculated as: <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is
	 *             too large for the double to be represented as a
	 *             {@code Decimal} with the scale of this factory
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	ImmutableDecimal<S> valueOf(double value, RoundingMode roundingMode);

	/**
	 * Returns a new immutable Decimal whose value is numerically equal to that
	 * of the specified {@link BigInteger} value. An exception is thrown if the
	 * specified value is too large to be represented as a Decimal of this
	 * factory's {@link #getScale() scale}.
	 *
	 * @param value
	 *            {@code BigInteger} value to convert into an immutable Decimal
	 *            value
	 * @return a Decimal value numerically equal to the specified big integer
	 *         value
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a Decimal
	 *             with the scale of this factory
	 */
	ImmutableDecimal<S> valueOf(BigInteger value);

	/**
	 * Returns a new immutable Decimal whose value is calculated by rounding the
	 * specified {@link BigDecimal} argument to the {@link #getScale() scale} of
	 * this factory using {@link RoundingMode#HALF_UP HALF_UP} rounding. An
	 * exception is thrown if the specified value is too large to be represented
	 * as a Decimal of this factory's scale.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into an immutable Decimal
	 *            value
	 * @return a Decimal calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} too large to be represented as a Decimal
	 *             with the scale of this factory
	 */
	ImmutableDecimal<S> valueOf(BigDecimal value);

	/**
	 * Returns a new immutable Decimal whose value is calculated by rounding the
	 * specified {@link BigDecimal} argument to the {@link #getScale() scale} of
	 * this factory using the specified {@code roundingMode}. An exception is
	 * thrown if the specified value is too large to be represented as a Decimal
	 * of this factory's scale.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into an immutable Decimal
	 *            value
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a Decimal calculated as: <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} too large to be represented as a Decimal
	 *             with the scale of this factory
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	ImmutableDecimal<S> valueOf(BigDecimal value, RoundingMode roundingMode);

	/**
	 * Returns a new immutable Decimal whose value is calculated by rounding the
	 * specified {@link Decimal} argument to the {@link #getScale() scale} of
	 * this factory using {@link RoundingMode#HALF_UP HALF_UP} rounding. An
	 * exception is thrown if the specified value is too large to be represented
	 * as a Decimal of this factory's scale.
	 *
	 * @param value
	 *            Decimal value to convert into an immutable Decimal value of
	 *            this factory's scale
	 * @return a Decimal calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} too large to be represented as a Decimal
	 *             with the scale of this factory
	 */
	ImmutableDecimal<S> valueOf(Decimal<?> value);

	/**
	 * Returns a new immutable Decimal whose value is calculated by rounding the
	 * specified {@link Decimal} argument to the {@link #getScale() scale} of
	 * this factory using the specified {@code roundingMode}. An exception is
	 * thrown if the specified value is too large to be represented as a Decimal
	 * of this factory's scale.
	 *
	 * @param value
	 *            Decimal value to convert into an immutable Decimal value of
	 *            this factory's scale
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a Decimal calculated as: <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} too large to be represented as a Decimal
	 *             with the scale of this factory
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	ImmutableDecimal<S> valueOf(Decimal<?> value, RoundingMode roundingMode);

	/**
	 * Translates the string representation of a {@code Decimal} into an
	 * immutable {@code Decimal}. The string representation consists of an
	 * optional sign, {@code '+'} or {@code '-'} , followed by a sequence of
	 * zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more digits than this factory's
	 * {@link #getScale() scale}, the value is rounded using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if
	 * the value is too large to be represented as a Decimal of this factory's
	 * scale.
	 *
	 * @param value
	 *            String value to convert into an immutable Decimal value of
	 *            this factory's scale
	 * @return a Decimal calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a Decimal
	 *             with the scale of this factory
	 */
	ImmutableDecimal<S> parse(String value);

	/**
	 * Translates the string representation of a {@code Decimal} into an
	 * immutable {@code Decimal}. The string representation consists of an
	 * optional sign, {@code '+'} or {@code '-'} , followed by a sequence of
	 * zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more digits than this factory's
	 * {@link #getScale() scale}, the value is rounded using the specified
	 * {@code roundingMode}. An exception is thrown if the value is too large to
	 * be represented as a Decimal of this factory's scale.
	 *
	 * @param value
	 *            String value to convert into an immutable Decimal value of
	 *            this factory's scale
	 * @param roundingMode
	 *            the rounding mode to apply if the fraction contains more
	 *            digits than the scale of this factory
	 * @return a Decimal calculated as: <tt>round(value)</tt>
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a Decimal
	 *             with the scale of this factory
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	ImmutableDecimal<S> parse(String value, RoundingMode roundingMode);

	/**
	 * Returns a new immutable Decimal whose value is numerically equal to
	 * <tt>(unscaled &times; 10<sup>-scale</sup>)</tt> where {@code scale}
	 * refers to this factory's {@link #getScale() scale}.
	 *
	 * @param unscaled
	 *            unscaled value to convert into an immutable Decimal value
	 * @return a Decimal calculated as:
	 *         <tt>unscaled &times; 10<sup>-scale</sup></tt>
	 */
	ImmutableDecimal<S> valueOfUnscaled(long unscaled);

	/**
	 * Returns a new immutable Decimal whose value is numerically equal to
	 * <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>. The result is rounded to
	 * the {@link #getScale() scale} of this factory using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if
	 * the specified value is too large to be represented as a Decimal of this
	 * factory's scale.
	 *
	 * @param unscaled
	 *            unscaled value to convert into an immutable Decimal value
	 * @param scale
	 *            the scale to apply to the {@code unscaled} value
	 * @return a Decimal calculated as:
	 *         <tt>round<sub>HALF_UP</sub>(unscaled &times; 10<sup>-scale</sup>)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} too large to be represented as a Decimal of
	 *             this factory's scale
	 */
	ImmutableDecimal<S> valueOfUnscaled(long unscaled, int scale);

	/**
	 * Returns a new immutable Decimal whose value is numerically equal to
	 * <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>. The result is rounded to
	 * the {@link #getScale() scale} of this factory using the specified
	 * {@code roundingMode}. An exception is thrown if the specified value is
	 * too large to be represented as a Decimal of this factory's scale.
	 *
	 * @param unscaled
	 *            unscaled value to convert into an immutable Decimal value
	 * @param scale
	 *            the scale to apply to the {@code unscaled} value
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a Decimal calculated as:
	 *         <tt>round(unscaled &times; 10<sup>-scale</sup>)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} too large to be represented as a Decimal of
	 *             this factory's scale
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	ImmutableDecimal<S> valueOfUnscaled(long unscaled, int scale, RoundingMode roundingMode);

	/**
	 * Creates a one dimensional array of the specified {@code length} for
	 * immutable Decimal values.
	 * 
	 * @param length
	 *            the length of the returned array
	 * @return a new array of the specified length
	 */
	ImmutableDecimal<S>[] newArray(int length);

	/**
	 * Creates a new mutable value initialized with zero.
	 * 
	 * @return a new mutable Decimal value representing zero.
	 */
	MutableDecimal<S> newMutable();

	/**
	 * Creates a one dimensional array of the specified {@code length} for
	 * mutable Decimal values.
	 * 
	 * @param length
	 *            the length of the returned array
	 * @return a new array of the specified length
	 */
	MutableDecimal<S>[] newMutableArray(int length);
}
