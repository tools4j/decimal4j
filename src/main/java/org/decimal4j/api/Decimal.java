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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Signed fixed-precision decimal number similar to {@link BigDecimal}. A
 * Decimal number can be immutable or mutable and it is based on an underlying
 * <i>unscaled</i> long value and a {@link #getScaleMetrics() scale}. The scale
 * defines the number of digits to the right of the decimal point. If the scale
 * is {@code f} then the value represented by a {@code Decimal} instance is
 * <tt>(unscaledValue &times; 10<sup>-f</sup>)</tt>.
 * <p>
 * <i>Scale of Result and Operands</i> <br>
 * The result of an arithmetic operation is generally of the same scale as this
 * Decimal unless otherwise indicated. Decimal operands of arithmetic operations
 * are typically also of the same scale as this Decimal. Scale compatibility of
 * Decimal operands is enforced through the generic {@link ScaleMetrics}
 * parameter {@code <S>}.
 * <p>
 * <i>Operands involving Type Conversion</i> <br>
 * For convenience, arithmetic operations with other data types are sometimes
 * also provided. Such operations usually perform a value conversion into a
 * Decimal of the current scale before performing the actual operation.
 * <p>
 * <i>Rounding</i> <br>
 * If the result of an arithmetic operation exceeds the precision of the current
 * scale, {@link RoundingMode#HALF_UP HALF_UP} rounding is applied to the least
 * significant digit of the result if no other rounding mode is explicitly
 * specified. Note that in a few exceptional cases
 * {@link RoundingMode#HALF_EVEN HALF_EVEN} rounding is used by default to
 * comply with inherited specification constraints (e.g. see
 * {@link #doubleValue()}, {@link #floatValue()} etc.).
 * <p>
 * <i>Overflows</i> <br>
 * Operations with Decimal values can lead to overflows in marked contrast to
 * the {@link BigDecimal}. This is a direct consequence of the construction of a
 * Decimal value on the basis of a long value. Unless otherwise indicated
 * operations silently truncate overflows by default. This choice has been made
 * for performance reasons and because Java programmers are already familiar
 * with this behavior from operations with primitive integer types. If this
 * behavior is inappropriate the user can request exceptions in overflow
 * situations through an optional {@link OverflowMode} or
 * {@link TruncationPolicy} argument. Some operations like those with a double
 * operand <i>always</i> throw an exception if an overflow occurs. The
 * documentation of operations which can cause an overflow always indicates the
 * exact overflow behavior.
 * 
 * @param <S>
 *            the scale metrics type associated with this Decimal
 */
public interface Decimal<S extends ScaleMetrics> extends Comparable<Decimal<S>> {

	/**
	 * Returns the metrics associated with the scale of this Decimal. Scale
	 * defines the number of fraction digits and the scale factor applied to the
	 * {@code long} value underlying this {@code Decimal}.
	 * 
	 * @return the scale metrics object
	 * @see ScaleMetrics#getScale()
	 * @see ScaleMetrics#getScaleFactor()
	 */
	S getScaleMetrics();

	/**
	 * Returns the scale associated with this Decimal. The scale defines the
	 * number of fraction digits and the scale factor applied to the
	 * {@code long} value underlying this {@code Decimal}.
	 * <p>
	 * If the scale is {@code f} then the value represented by a {@code Decimal}
	 * instance is <tt>(unscaledValue &times; 10<sup>-f</sup>)</tt>.
	 * <p>
	 * This method is a shortcut for {@code getScaleMetrics().getScale()}.
	 * 
	 * @return the scale
	 * @see #getScaleMetrics()
	 * @see ScaleMetrics#getScale()
	 */
	int getScale();

	/**
	 * Returns the unscaled value underlying this {@code Decimal}. This
	 * {@code Decimal} is <tt>(unscaledValue &times; 10<sup>-f</sup>)</tt> with
	 * {@code f} representing the {@link #getScale() scale}, hence the returned
	 * value equals <tt>(10<sup>f</sup> &times; this)</tt>.
	 * 
	 * @return the unscaled numeric value, the same as this Decimal without
	 *         applying the scale factor
	 * @see #getScale()
	 * @see ScaleMetrics#getScaleFactor()
	 */
	long unscaledValue();

	/**
	 * Returns the factory that can be used to create other Decimal values of
	 * the same scale as {@code this} Decimal.
	 * 
	 * @return the factory to create other Decimal values of the same scale as
	 *         this Decimal
	 */
	DecimalFactory<S> getFactory();

	/**
	 * Returns a {@code Decimal} whose value represents the integral part of
	 * {@code (this)} value. The integral part corresponds to digits at the left
	 * of the decimal point. The result is {@code this} Decimal rounded to
	 * precision zero with {@link RoundingMode#DOWN}.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the operation.
	 * 
	 * @return <tt>&lfloor;this&rfloor;</tt> for non-negative and
	 *         &lceil;this&rceil;</tt> for negative values
	 * @see #fractionalPart()
	 * @see #isIntegral()
	 * @see #isIntegralPartZero()
	 */
	Decimal<S> integralPart();

	/**
	 * Returns a {@code Decimal} whose value represents the fractional part of
	 * {@code (this)} value. The fractional part corresponds to digits at the
	 * right of the decimal point. The result is {@code this} minus the integral
	 * part of this Decimal.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the operation.
	 * 
	 * @return {@code this-integralPart()}
	 * @see #integralPart()
	 * @see #isIntegral()
	 * @see #isIntegralPartZero()
	 */
	Decimal<S> fractionalPart();

	// some methods "inherited" from Number and BigDecimal

	/**
	 * Returns the value of this {@code Decimal} as a {@code byte} after a
	 * narrowing primitive conversion.
	 * 
	 * @return this {@code Decimal} converted to an {@code byte}.
	 * @see Number#byteValue()
	 */
	byte byteValue();

	/**
	 * Converts this {@code Decimal} to a {@code byte}, checking for lost
	 * information. If this {@code Decimal} has a nonzero fractional part or is
	 * out of the possible range for a {@code byte} result then an
	 * {@code ArithmeticException} is thrown.
	 *
	 * @return this {@code Decimal} converted to a {@code byte}.
	 * @throws ArithmeticException
	 *             if {@code this} has a nonzero fractional part, or will not
	 *             fit in a {@code byte}.
	 */
	byte byteValueExact();

	/**
	 * Returns the value of this {@code Decimal} as a {@code short} after a
	 * narrowing primitive conversion.
	 * 
	 * @return this {@code Decimal} converted to an {@code short}.
	 * @see Number#shortValue()
	 */
	short shortValue();

	/**
	 * Converts this {@code Decimal} to a {@code short}, checking for lost
	 * information. If this {@code Decimal} has a nonzero fractional part or is
	 * out of the possible range for a {@code short} result then an
	 * {@code ArithmeticException} is thrown.
	 *
	 * @return this {@code Decimal} converted to a {@code short}.
	 * @throws ArithmeticException
	 *             if {@code this} has a nonzero fractional part, or will not
	 *             fit in a {@code short}.
	 */
	short shortValueExact();

	/**
	 * Converts this {@code Decimal} to an {@code int}. This conversion is
	 * analogous to the <i>narrowing primitive conversion</i> from
	 * {@code double} to {@code short} as defined in section 5.1.3 of <cite>The
	 * Java&trade; Language Specification</cite>: any fractional part of this
	 * {@code Decimal} will be discarded, and if the resulting "{@code long}" is
	 * too big to fit in an {@code int}, only the low-order 32 bits are
	 * returned. Note that this conversion can lose information about the
	 * overall magnitude and precision of this {@code Decimal} value as well as
	 * return a result with the opposite sign.
	 *
	 * @return this {@code Decimal} converted to an {@code int}.
	 * @see Number#intValue()
	 */
	int intValue();

	/**
	 * Converts this {@code Decimal} to an {@code int}, checking for lost
	 * information. If this {@code Decimal} has a nonzero fractional part or is
	 * out of the possible range for an {@code int} result then an
	 * {@code ArithmeticException} is thrown.
	 *
	 * @return this {@code Decimal} converted to an {@code int}.
	 * @throws ArithmeticException
	 *             if {@code this} has a nonzero fractional part, or will not
	 *             fit in an {@code int}.
	 */
	int intValueExact();

	/**
	 * Converts this {@code Decimal} to a {@code long}. This conversion is
	 * analogous to the <i>narrowing primitive conversion</i> from
	 * {@code double} to {@code short} as defined in section 5.1.3 of <cite>The
	 * Java&trade; Language Specification</cite>: any fractional part of this
	 * {@code Decimal} will be discarded. Note that this conversion can lose
	 * information about the precision of the {@code Decimal} value.
	 *
	 * @return this {@code Decimal} converted to a {@code long}.
	 * @see Number#longValue()
	 */
	long longValue();

	/**
	 * Converts this {@code Decimal} to a {@code long}, checking for lost
	 * information. If this {@code Decimal} has a nonzero fractional part or is
	 * out of the possible range for a {@code long} result then an
	 * {@code ArithmeticException} is thrown.
	 *
	 * @return this {@code Decimal} converted to a {@code long}.
	 * @throws ArithmeticException
	 *             if {@code this} has a nonzero fractional part
	 */
	long longValueExact();

	/**
	 * Converts this {@code Decimal} to a {@code float}. This conversion is
	 * similar to the <i>narrowing primitive conversion</i> from {@code double}
	 * to {@code float} as defined in section 5.1.3 of <cite>The Java&trade;
	 * Language Specification</cite>. Note that this conversion can lose
	 * information about the precision of the {@code Decimal} value.
	 *
	 * @return this {@code Decimal} converted to a {@code float}.
	 */
	float floatValue();

	/**
	 * Converts this {@code Decimal} to a {@code double}. This conversion is
	 * similar to the <i>narrowing primitive conversion</i> from {@code double}
	 * to {@code float} as defined in section 5.1.3 of <cite>The Java&trade;
	 * Language Specification</cite>. Note that this conversion can lose
	 * information about the precision of the {@code Decimal} value.
	 *
	 * @return this {@code Decimal} converted to a {@code double}.
	 */
	double doubleValue();

	/**
	 * Converts this {@code Decimal} to a {@code BigInteger}. This conversion is
	 * analogous to the <i>narrowing primitive conversion</i> from
	 * {@code double} to {@code long} as defined in section 5.1.3 of <cite>The
	 * Java&trade; Language Specification</cite>: any fractional part of this
	 * {@code Decimal} will be discarded. Note that this conversion can lose
	 * information about the precision of the {@code Decimal} value.
	 * <p>
	 * To have an exception thrown if the conversion is inexact (in other words
	 * if a nonzero fractional part is discarded), use the
	 * {@link #toBigIntegerExact()} method.
	 *
	 * @return this {@code Decimal} converted to a {@code BigInteger}.
	 */
	BigInteger toBigInteger();

	/**
	 * Converts this {@code Decimal} to a {@code BigInteger}, checking for lost
	 * information. An exception is thrown if this {@code Decimal} has a nonzero
	 * fractional part.
	 *
	 * @return this {@code Decimal} converted to a {@code BigInteger}.
	 * @throws ArithmeticException
	 *             if {@code this} has a nonzero fractional part.
	 */
	BigInteger toBigIntegerExact();

	/**
	 * Converts this {@code Decimal} to a {@code BigDecimal} using the same
	 * {@link #getScale() scale} as this Decimal value.
	 *
	 * @return this {@code Decimal} converted to a {@code BigDecimal} with the
	 *         same scale as this Decimal value.
	 */
	BigDecimal toBigDecimal();

	// mutable/immutable conversion methods

	/**
	 * If this {@code Decimal} value is already an {@link ImmutableDecimal} it
	 * is simply returned. Otherwise a new immutable value with the same scale
	 * and numerical value as {@code this} Decimal is created and returned.
	 * 
	 * @return {@code this} if immutable and a new {@link ImmutableDecimal} with
	 *         the same scale and value as {@code this} Decimal otherwise
	 */
	ImmutableDecimal<S> toImmutableDecimal();

	/**
	 * If this {@code Decimal} value is already an {@link MutableDecimal} it is
	 * simply returned. Otherwise a new mutable value with the same scale and
	 * numerical value as {@code this} Decimal is created and returned.
	 * 
	 * @return {@code this} if mutable and a new {@link MutableDecimal} with the
	 *         same scale and value as {@code this} Decimal otherwise
	 */
	MutableDecimal<S> toMutableDecimal();

	// some conversion methods with rounding mode

	/**
	 * Converts this {@code Decimal} to a {@code long} using the specified
	 * rounding mode if necessary. Rounding is applied if the Decimal value can
	 * not be represented as a long value, that is, if it has a nonzero
	 * fractional part. Note that this conversion can lose information about the
	 * precision of the {@code Decimal} value.
	 * 
	 * @param roundingMode
	 *            the rounding mode to apply when rounding is necessary to
	 *            convert this Decimal into a long
	 * @return this {@code Decimal} converted to a {@code long}.
	 */
	long longValue(RoundingMode roundingMode);

	/**
	 * Converts this {@code Decimal} to a {@code float} using the specified
	 * rounding mode if the Decimal value can not be exactly represented as a
	 * float value. Note that this conversion can lose information about the
	 * precision of the {@code Decimal} value.
	 *
	 * @param roundingMode
	 *            the rounding mode to apply when rounding is necessary to
	 *            convert this Decimal into a float value
	 * @return this {@code Decimal} converted to a {@code float}.
	 */
	float floatValue(RoundingMode roundingMode);

	/**
	 * Converts this {@code Decimal} to a {@code double} using the specified
	 * rounding mode if the Decimal value can not be exactly represented as a
	 * double value. Note that this conversion can lose information about the
	 * precision of the {@code Decimal} value.
	 *
	 * @param roundingMode
	 *            the rounding mode to apply when rounding is necessary to
	 *            convert this Decimal into a double value
	 * @return this {@code Decimal} converted to a {@code double}.
	 */
	double doubleValue(RoundingMode roundingMode);

	/**
	 * Converts this {@code Decimal} to a {@link BigInteger} value using the
	 * specified rounding mode if necessary. Rounding is applied if the Decimal
	 * value can not be represented as a {@code BigInteger}, that is, if it has
	 * a nonzero fractional part. Note that this conversion can lose information
	 * about the precision of the {@code Decimal} value.
	 * 
	 * @param roundingMode
	 *            the rounding mode to apply when rounding is necessary to
	 *            convert this Decimal into a {@code BigInteger}
	 * @return this {@code Decimal} converted to a {@code BigInteger}.
	 */
	BigInteger toBigInteger(RoundingMode roundingMode);

	/**
	 * Returns a {@code BigDecimal} value of the given scale using the specified
	 * rounding mode if necessary.
	 * 
	 * @param scale
	 *            the scale used for the returned {@code BigDecimal}
	 * @param roundingMode
	 *            the rounding mode to apply when rounding is necessary to
	 *            convert from the this Decimal's {@link #getScale() scale} to
	 *            the target scale
	 * @return a {@code BigDecimal} instance of the specified scale
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	BigDecimal toBigDecimal(int scale, RoundingMode roundingMode);

	// methods to round and change the scale

	/**
	 * Returns a {@code Decimal} value rounded to the specified
	 * {@code precision} using {@link RoundingMode#HALF_UP HALF_UP} rounding. If
	 * an overflow occurs due to the rounding operation, the result is silently
	 * truncated.
	 * <p>
	 * Note that contrary to the {@code scale(..)} operations this method does
	 * not change the scale of the value --- extra digits are simply zeroised.
	 * <p>
	 * <i>Examples and special cases:</i>
	 * <dl>
	 * <dt>precision = 0</dt>
	 * <dd>value is rounded to an integer value</dd>
	 * <dt>precision = 2</dt>
	 * <dd>value is rounded to the second digit after the decimal point</dd>
	 * <dt>precision = -3</dt>
	 * <dd>value is rounded to the thousands</dd>
	 * <dt>precision >= scale</dt>
	 * <dd>values is returned unchanged</dd>
	 * <dt>precision < scale - 18</dt>
	 * <dd>{@code IllegalArgumentException} is thrown</dd>
	 * </dl>
	 * 
	 * @param precision
	 *            the precision to use for the rounding, for instance 2 to round
	 *            to the second digit after the decimal point; must be at least
	 *            {@code (scale - 18)}
	 * @return a Decimal instance rounded to the given precision
	 * @throws IllegalArgumentException
	 *             if {@code precision < scale - 18}
	 */
	Decimal<S> round(int precision);

	/**
	 * Returns a {@code Decimal} value rounded to the specified
	 * {@code precision} using the given rounding mode. If an overflow occurs
	 * due to the rounding operation, the result is silently truncated.
	 * <p>
	 * Note that contrary to the {@code scale(..)} operations this method does
	 * not change the scale of the value --- extra digits are simply zeroised.
	 * <p>
	 * <i>Examples and special cases:</i>
	 * <dl>
	 * <dt>precision = 0</dt>
	 * <dd>value is rounded to an integer value</dd>
	 * <dt>precision = 2</dt>
	 * <dd>value is rounded to the second digit after the decimal point</dd>
	 * <dt>precision = -3</dt>
	 * <dd>value is rounded to the thousands</dd>
	 * <dt>precision >= scale</dt>
	 * <dd>values is returned unchanged</dd>
	 * <dt>precision < scale - 18</dt>
	 * <dd>{@code IllegalArgumentException} is thrown</dd>
	 * </dl>
	 * 
	 * @param precision
	 *            the precision to use for the rounding, for instance 2 to round
	 *            to the second digit after the decimal point; must be at least
	 *            {@code (scale - 18)}
	 * @param roundingMode
	 *            the rounding mode to apply when rounding to the desired
	 *            precision
	 * @return a Decimal instance rounded to the given precision
	 * @throws IllegalArgumentException
	 *             if {@code precision < scale - 18}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	Decimal<S> round(int precision, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} value rounded to the specified
	 * {@code precision} using the given truncation policy.
	 * <p>
	 * Note that contrary to the {@code scale(..)} operations this method does
	 * not change the scale of the value --- extra digits are simply zeroised.
	 * <p>
	 * <i>Examples and special cases:</i>
	 * <dl>
	 * <dt>precision = 0</dt>
	 * <dd>value is rounded to an integer value</dd>
	 * <dt>precision = 2</dt>
	 * <dd>value is rounded to the second digit after the decimal point</dd>
	 * <dt>precision = -3</dt>
	 * <dd>value is rounded to the thousands</dd>
	 * <dt>precision >= scale</dt>
	 * <dd>values is returned unchanged</dd>
	 * <dt>precision < scale - 18</dt>
	 * <dd>{@code IllegalArgumentException} is thrown</dd>
	 * </dl>
	 * 
	 * @param precision
	 *            the precision to use for the rounding, for instance 2 to round
	 *            to the second digit after the decimal point; must be at least
	 *            {@code (scale - 18)}
	 * @param truncationPolicy
	 *            the truncation policy defining {@link RoundingMode} and
	 *            {@link OverflowMode} for the rounding operation
	 * @return a Decimal instance rounded to the given precision
	 * @throws IllegalArgumentException
	 *             if {@code precision < scale - 18}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} specifies
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> round(int precision, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} value whose {@link #getScaleMetrics() scale} is
	 * changed to the give value. {@link RoundingMode#HALF_UP HALF_UP} rounding
	 * is used if the scale change involves rounding.
	 * <p>
	 * An exception is thrown if the scale conversion leads to an overflow.
	 * 
	 * @param scale
	 *            the scale to use for the result, must be in {@code [0,18]}
	 * @return a Decimal instance with the given new scale
	 * @throws IllegalArgumentException
	 *             if {@code scale < 0} or {@code scale > 18}
	 */
	Decimal<?> scale(int scale);

	/**
	 * Returns a {@code Decimal} value whose {@link #getScaleMetrics() scale} is
	 * changed to the give value. {@link RoundingMode#HALF_UP HALF_UP} rounding
	 * is used if the scale change involves rounding. If an overflow occurs due
	 * to the scale conversion, the result is silently truncated.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics to use for the result
	 * @return a Decimal instance with the given new scale metrics
	 */
	@SuppressWarnings("hiding")
	<S extends ScaleMetrics> Decimal<S> scale(S scaleMetrics);

	/**
	 * Returns a {@code Decimal} value whose {@link #getScaleMetrics() scale} is
	 * changed to the give value. The specified {@code roundingMode} is used if
	 * the scale change involves rounding. If an overflow occurs due to the
	 * scale conversion, the result is silently truncated.
	 * 
	 * @param scale
	 *            the scale to use for the result, must be in {@code [0,18]}
	 * @param roundingMode
	 *            the rounding mode to apply if the scale change involves
	 *            rounding
	 * @return a Decimal instance with the given new scale
	 * @throws IllegalArgumentException
	 *             if {@code scale < 0} or {@code scale > 18}
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 */
	Decimal<?> scale(int scale, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} value whose {@link #getScaleMetrics() scale} is
	 * changed to the give value. The specified {@code roundingMode} is used if
	 * the scale change involves rounding. If an overflow occurs due to the
	 * scale conversion, the result is silently truncated.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics to use for the result
	 * @param roundingMode
	 *            the rounding mode to apply if the scale change involves
	 *            rounding
	 * @return a Decimal instance with the given new scale metrics
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 */
	@SuppressWarnings("hiding")
	<S extends ScaleMetrics> Decimal<S> scale(S scaleMetrics, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} value whose {@link #getScaleMetrics() scale} is
	 * changed to the give value. The specified {@code truncationPolicy} defines
	 * {@link RoundingMode} and {@link OverflowMode} to apply if the scale
	 * conversion leads to rounding or causes an overflow.
	 * 
	 * @param scale
	 *            the scale to use for the result, must be in {@code [0,18]}
	 * @param truncationPolicy
	 *            the truncation policy defining {@link RoundingMode} and
	 *            {@link OverflowMode} for the scale conversion
	 * @return a Decimal instance with the given new scale
	 * @throws IllegalArgumentException
	 *             if {@code scale < 0} or {@code scale > 18}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<?> scale(int scale, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} value whose {@link #getScaleMetrics() scale} is
	 * changed to the give value. The specified {@code truncationPolicy} defines
	 * {@link RoundingMode} and {@link OverflowMode} to apply if the scale
	 * conversion leads to rounding or causes an overflow.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics to use for the result
	 * @param truncationPolicy
	 *            the truncation policy defining {@link RoundingMode} and
	 *            {@link OverflowMode} for the scale conversion
	 * @return a Decimal instance with the given new scale metrics
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	@SuppressWarnings("hiding")
	<S extends ScaleMetrics> Decimal<S> scale(S scaleMetrics, TruncationPolicy truncationPolicy);

	// add

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)}. If the
	 * addition causes an overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this + augend}
	 */
	Decimal<S> add(Decimal<S> augend);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)}. The
	 * specified {@code overflowMode} determines whether to truncate the result
	 * silently or to throw an exception if an overflow occurs.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param overflowMode
	 *            the overflow mode to apply if the addition leads to an
	 *            overflow
	 * @return {@code this + augend}
	 * @throws ArithmeticException
	 *             if {@code overflowMode==CHECKED} and an overflow occurs
	 */
	Decimal<S> add(Decimal<S> augend, OverflowMode overflowMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)} after
	 * converting the given {@code augend} argument to the scale of {@code this}
	 * Decimal. Rounding, if necessary, uses the specified {@code roundingMode}
	 * and is applied during the conversion step <i>before</i> the addition
	 * operation. Overflows during scale conversion or addition are silently
	 * truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the augend argument needs to be
	 *            truncated when converted into a Decimal number of the same
	 *            scale as {@code this} Decimal
	 * @return {@code this + augend}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	Decimal<S> add(Decimal<?> augend, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)} after
	 * converting the given {@code augend} argument to the scale of {@code this}
	 * Decimal. Rounding, if necessary, is defined by the specified
	 * {@code truncationPolicy} argument and is applied during the conversion
	 * step <i>before</i> the addition operation. The {@code truncationPolicy}
	 * also defines the {@link OverflowMode} to apply if an overflow occurs
	 * during scale conversion or addition.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary during
	 *            the scale conversion or if an overflow occurs during
	 *            conversion or addition operation
	 * @return {@code this + augend}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} specifies
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> add(Decimal<?> augend, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)}. If the
	 * addition causes an overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this + augend}
	 */
	Decimal<S> add(long augend);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)}. The
	 * specified {@code overflowMode} determines whether to truncate the result
	 * silently or to throw an exception if an overflow occurs.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param overflowMode
	 *            the overflow mode to apply if the addition leads to an
	 *            overflow
	 * @return {@code this + augend}
	 * @throws ArithmeticException
	 *             if {@code overflowMode==CHECKED} and an overflow occurs
	 */
	Decimal<S> add(long augend, OverflowMode overflowMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)} after
	 * converting the given {@code double} argument into a Decimal value of the
	 * same scale as {@code this} Decimal. If rounding is necessary,
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding mode is used and applied
	 * during the conversion step <i>before</i> the addition operation.
	 * Overflows due to conversion or addition result in an exception.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this + augend}
	 * @throws NumberFormatException
	 *             if {@code augend} is NaN or infinite or if the magnitude is
	 *             too large for the double to be represented as a
	 *             {@code Decimal}
	 * @throws ArithmeticException
	 *             if an overflow occurs during the addition operation
	 */
	Decimal<S> add(double augend);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)} after
	 * converting the given {@code double} argument into a Decimal value of the
	 * same scale as {@code this} Decimal. If rounding is necessary, the
	 * specifed {@code roundingMode} is used and applied during the conversion
	 * step <i>before</i> the addition operation. Overflows due to conversion or
	 * addition result in an exception.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the augend argument needs to be
	 *            rounded when converted into a Decimal number of the same scale
	 *            as {@code this} Decimal
	 * @return {@code this + augend}
	 * @throws NumberFormatException
	 *             if {@code augend} is NaN or infinite or if the magnitude is
	 *             too large for the double to be represented as a
	 *             {@code Decimal}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary or if an overflow occurs during the addition
	 *             operation
	 */
	Decimal<S> add(double augend, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this + unscaledAugend &times; 10<sup>-scale</sup>)</tt> with the
	 * {@link #getScale() scale} of this Decimal. If the addition causes an
	 * overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param unscaledAugend
	 *            value to be added to this {@code Decimal}
	 * @return <tt>this + unscaledAugend &times; 10<sup>-scale</sup></tt>
	 */
	Decimal<S> addUnscaled(long unscaledAugend);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this + unscaledAugend &times; 10<sup>-scale</sup>)</tt> with the
	 * {@link #getScale() scale} of this Decimal. The specified
	 * {@code overflowMode} determines whether to truncate the result silently
	 * or to throw an exception if an overflow occurs.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param unscaledAugend
	 *            value to be added to this {@code Decimal}
	 * @param overflowMode
	 *            the overflow mode to apply if the addition leads to an
	 *            overflow
	 * @return <tt>this + unscaledAugend &times; 10<sup>-scale</sup></tt>
	 * @throws ArithmeticException
	 *             if {@code overflowMode==CHECKED} and an overflow occurs
	 */
	Decimal<S> addUnscaled(long unscaledAugend, OverflowMode overflowMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this + round(unscaledAugend &times; 10<sup>-scale</sup>))</tt>. The
	 * {@code unscaledAugend} argument is converted to the same scale as
	 * {@code this} Decimal. If rounding is necessary,
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding mode is used and applied
	 * during the conversion step <i>before</i> the addition operation.
	 * Overflows during scale conversion or addition are silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param unscaledAugend
	 *            value to be added to this {@code Decimal}
	 * @param scale
	 *            the scale to apply to {@code unscaledAugend}, positive to
	 *            indicate the number of fraction digits to the right of the
	 *            Decimal point and negative to indicate up-scaling with a power
	 *            of ten
	 * @return <tt>this + round<sub>HALF_UP</sub>(unscaledAugend &times; 10<sup>-scale</sup>)</tt>
	 */
	Decimal<S> addUnscaled(long unscaledAugend, int scale);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this + round(unscaledAugend &times; 10<sup>-scale</sup>))</tt>. The
	 * {@code unscaledAugend} argument is converted to the same scale as
	 * {@code this} Decimal. If rounding is necessary, the specified
	 * {@code roundingMode} is used and applied during the conversion step
	 * <i>before</i> the addition operation. Overflows during scale conversion
	 * or addition are silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param unscaledAugend
	 *            value to be added to this {@code Decimal}
	 * @param scale
	 *            the scale to apply to {@code unscaledAugend}, positive to
	 *            indicate the number of fraction digits to the right of the
	 *            Decimal point and negative to indicate up-scaling with a power
	 *            of ten
	 * @param roundingMode
	 *            the rounding mode to apply if the augend argument needs to be
	 *            rounded when converted into a Decimal number of the same scale
	 *            as {@code this} Decimal
	 * @return <tt>this + round(unscaledAugend &times; 10<sup>-scale</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	Decimal<S> addUnscaled(long unscaledAugend, int scale, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this + round(unscaledAugend &times; 10<sup>-scale</sup>))</tt>. The
	 * {@code unscaledAugend} argument is converted to the same scale as
	 * {@code this} Decimal. Rounding, if necessary, is defined by the specified
	 * {@code truncationPolicy} argument and is applied during the conversion
	 * step <i>before</i> the addition operation. The {@code truncationPolicy}
	 * also defines the {@link OverflowMode} to apply if an overflow occurs
	 * during scale conversion or addition.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param unscaledAugend
	 *            value to be added to this {@code Decimal}
	 * @param scale
	 *            the scale to apply to {@code unscaledAugend}, positive to
	 *            indicate the number of fraction digits to the right of the
	 *            Decimal point and negative to indicate up-scaling with a power
	 *            of ten
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary during
	 *            the scale conversion or if an overflow occurs during
	 *            conversion or addition operation
	 * @return <tt>this + round(unscaledAugend &times; 10<sup>-scale</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> addUnscaled(long unscaledAugend, int scale, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this + value<sup>2</sup>)</tt>. The squared value is rounded
	 * <i>before</i> the addition if necessary using default
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. Overflows during squaring
	 * or addition are silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param value
	 *            value to be squared and added to this {@code Decimal}
	 * @return {@code this + value*value}
	 */
	Decimal<S> addSquared(Decimal<S> value);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this + value<sup>2</sup>)</tt>. The squared value is rounded
	 * <i>before</i> the addition if necessary using the specified
	 * {@code roundingMode}. Overflows during squaring or addition are silently
	 * truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param value
	 *            value to be squared and added to this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if necessary when squaring the
	 *            value
	 * @return {@code this + value*value}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	Decimal<S> addSquared(Decimal<S> value, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this + value<sup>2</sup>)</tt>. The squared value is rounded
	 * <i>before</i> the addition if necessary using the {@link RoundingMode}
	 * specified by the {@code truncationPolicy} argument. The
	 * {@code truncationPolicy} also defines the {@link OverflowMode} to apply
	 * if an overflow occurs during square or add operation.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param value
	 *            value to be squared and added to this {@code Decimal}
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary when
	 *            squaring the value or if an overflow occurs during the square
	 *            or add operation
	 * @return {@code this + value*value}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> addSquared(Decimal<S> value, TruncationPolicy truncationPolicy);

	// subtract

	/**
	 * Returns a {@code Decimal} whose value is {@code (this - subtrahend)}. If
	 * the subtraction causes an overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return {@code this - subtrahend}
	 */
	Decimal<S> subtract(Decimal<S> subtrahend);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this - subtrahend)}. The
	 * specified {@code overflowMode} determines whether to truncate the result
	 * silently or to throw an exception if an overflow occurs.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @param overflowMode
	 *            the overflow mode to apply if the subtraction leads to an
	 *            overflow
	 * @return {@code this - subtrahend}
	 * @throws ArithmeticException
	 *             if {@code overflowMode==CHECKED} and an overflow occurs
	 */
	Decimal<S> subtract(Decimal<S> subtrahend, OverflowMode overflowMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this - subtrahend)}
	 * after converting the given {@code subtrahend} argument to the scale of
	 * {@code this} Decimal. Rounding, if necessary, uses the specified
	 * {@code roundingMode} and is applied during the conversion step
	 * <i>before</i> the subtraction operation. Overflows during scale
	 * conversion or subtraction are silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the subtrahend argument needs to
	 *            be truncated when converted into a Decimal number of the same
	 *            scale as {@code this} Decimal
	 * @return {@code this - subtrahend}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	Decimal<S> subtract(Decimal<?> subtrahend, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this - subtrahend)}
	 * after converting the given {@code subtrahend} argument to the scale of
	 * {@code this} Decimal. Rounding, if necessary, is defined by the specified
	 * {@code truncationPolicy} argument and is applied during the conversion
	 * step <i>before</i> the subtraction operation. The
	 * {@code truncationPolicy} also defines the {@link OverflowMode} to apply
	 * if an overflow occurs during scale conversion or subtraction.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary during
	 *            the scale conversion or if an overflow occurs during
	 *            conversion or subtraction operation
	 * @return {@code this - subtrahend}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} specifies
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> subtract(Decimal<?> subtrahend, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this - subtrahend)}. If
	 * the subtraction causes an overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return {@code this - subtrahend}
	 */
	Decimal<S> subtract(long subtrahend);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this - subtrahend)}. The
	 * specified {@code overflowMode} determines whether to truncate the result
	 * silently or to throw an exception if an overflow occurs.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @param overflowMode
	 *            the overflow mode to apply if the subtraction leads to an
	 *            overflow
	 * @return {@code this - subtrahend}
	 * @throws ArithmeticException
	 *             if {@code overflowMode==CHECKED} and an overflow occurs
	 */
	Decimal<S> subtract(long subtrahend, OverflowMode overflowMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this - subtrahend)}
	 * after converting the given {@code double} argument into a Decimal value
	 * of the same scale as {@code this} Decimal. If rounding is necessary,
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding mode is used and applied
	 * during the conversion step <i>before</i> the subtraction operation.
	 * Overflows due to conversion or subtraction result in an exception.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return {@code this - subtrahend}
	 * @throws NumberFormatException
	 *             if {@code subtrahend} is NaN or infinite or if the magnitude
	 *             is too large for the double to be represented as a
	 *             {@code Decimal}
	 * @throws ArithmeticException
	 *             if an overflow occurs during the subtraction operation
	 */
	Decimal<S> subtract(double subtrahend);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this - subtrahend)}
	 * after converting the given {@code double} argument into a Decimal value
	 * of the same scale as {@code this} Decimal. If rounding is necessary, the
	 * specifed {@code roundingMode} is used and applied during the conversion
	 * step <i>before</i> the subtraction operation. Overflows due to conversion
	 * or subtraction result in an exception.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the subtrahend argument needs to
	 *            be rounded when converted into a Decimal number of the same
	 *            scale as {@code this} Decimal
	 * @return {@code this - subtrahend}
	 * @throws NumberFormatException
	 *             if {@code subtrahend} is NaN or infinite or if the magnitude
	 *             is too large for the double to be represented as a
	 *             {@code Decimal}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary or if an overflow occurs during the subtraction
	 *             operation
	 */
	Decimal<S> subtract(double subtrahend, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this - unscaledSubtrahend &times; 10<sup>-scale</sup>)</tt> with the
	 * {@link #getScale() scale} of this Decimal. If the subtraction causes an
	 * overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param unscaledSubtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return <tt>this - unscaledSubtrahend &times; 10<sup>-scale</sup></tt>
	 */
	Decimal<S> subtractUnscaled(long unscaledSubtrahend);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this - unscaledSubtrahend &times; 10<sup>-scale</sup>)</tt> with the
	 * {@link #getScale() scale} of this Decimal. The specified
	 * {@code overflowMode} determines whether to truncate the result silently
	 * or to throw an exception if an overflow occurs.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param unscaledSubtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @param overflowMode
	 *            the overflow mode to apply if the subtraction leads to an
	 *            overflow
	 * @return <tt>this - unscaledSubtrahend &times; 10<sup>-scale</sup></tt>
	 * @throws ArithmeticException
	 *             if {@code overflowMode==CHECKED} and an overflow occurs
	 */
	Decimal<S> subtractUnscaled(long unscaledSubtrahend, OverflowMode overflowMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this - round(unscaledSubtrahend &times; 10<sup>-scale</sup>))</tt> .
	 * The {@code unscaledSubtrahend} argument is converted to the same scale as
	 * {@code this} Decimal. If rounding is necessary,
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding mode is used and applied
	 * during the conversion step <i>before</i> the subtraction operation.
	 * Overflows during scale conversion or subtraction are silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param unscaledSubtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @param scale
	 *            the scale to apply to {@code unscaledSubtrahend}, positive to
	 *            indicate the number of fraction digits to the right of the
	 *            Decimal point and negative to indicate up-scaling with a power
	 *            of ten
	 * @return <tt>this - round<sub>HALF_UP</sub>(unscaledSubtrahend &times; 10<sup>-scale</sup>)</tt>
	 */
	Decimal<S> subtractUnscaled(long unscaledSubtrahend, int scale);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this - round(unscaledSubtrahend &times; 10<sup>-scale</sup>))</tt> .
	 * The {@code unscaledSubtrahend} argument is converted to the same scale as
	 * {@code this} Decimal. If rounding is necessary, the specified
	 * {@code roundingMode} is used and applied during the conversion step
	 * <i>before</i> the subtraction operation. Overflows during scale
	 * conversion or subtraction are silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param unscaledSubtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @param scale
	 *            the scale to apply to {@code unscaledSubtrahend}, positive to
	 *            indicate the number of fraction digits to the right of the
	 *            Decimal point and negative to indicate up-scaling with a power
	 *            of ten
	 * @param roundingMode
	 *            the rounding mode to apply if the subtrahend argument needs to
	 *            be rounded when converted into a Decimal number of the same
	 *            scale as {@code this} Decimal
	 * @return <tt>this - round(unscaledSubtrahend &times; 10<sup>-scale</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	Decimal<S> subtractUnscaled(long unscaledSubtrahend, int scale, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this - round(unscaledSubtrahend &times; 10<sup>-scale</sup>))</tt> .
	 * The {@code unscaledSubtrahend} argument is converted to the same scale as
	 * {@code this} Decimal. Rounding, if necessary, is defined by the specified
	 * {@code truncationPolicy} argument and is applied during the conversion
	 * step <i>before</i> the subtraction operation. The
	 * {@code truncationPolicy} also defines the {@link OverflowMode} to apply
	 * if an overflow occurs during scale conversion or subtraction.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param unscaledSubtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @param scale
	 *            the scale to apply to {@code unscaledSubtrahend}, positive to
	 *            indicate the number of fraction digits to the right of the
	 *            Decimal point and negative to indicate up-scaling with a power
	 *            of ten
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary during
	 *            the scale conversion or if an overflow occurs during
	 *            conversion or subtraction operation
	 * @return <tt>this - round(unscaledSubtrahend &times; 10<sup>-scale</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> subtractUnscaled(long unscaledSubtrahend, int scale, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this - value<sup>2</sup>)</tt>. The squared value is rounded
	 * <i>before</i> the subtraction if necessary using default
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. Overflows during squaring
	 * or subtraction are silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param value
	 *            value to be squared and subtracted from this {@code Decimal}
	 * @return {@code this - value*value}
	 */
	Decimal<S> subtractSquared(Decimal<S> value);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this - value<sup>2</sup>)</tt>. The squared value is rounded
	 * <i>before</i> the subtraction if necessary using the specified
	 * {@code roundingMode}. Overflows during squaring or subtraction are
	 * silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param value
	 *            value to be squared and subtracted from this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if necessary when squaring the
	 *            value
	 * @return {@code this - value*value}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	Decimal<S> subtractSquared(Decimal<S> value, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this - value<sup>2</sup>)</tt>. The squared value is rounded
	 * <i>before</i> the subtraction if necessary using the {@link RoundingMode}
	 * specified by the {@code truncationPolicy} argument. The
	 * {@code truncationPolicy} also defines the {@link OverflowMode} to apply
	 * if an overflow occurs during square or subtract operation.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the subtraction.
	 * 
	 * @param value
	 *            value to be squared and subtracted from this {@code Decimal}
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary when
	 *            squaring the value or if an overflow occurs during the square
	 *            or subtract operation
	 * @return {@code this - value*value}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> subtractSquared(Decimal<S> value, TruncationPolicy truncationPolicy);

	// multiply

	/**
	 * Returns a {@code Decimal} whose value is
	 * {@code round(this * multiplicand)}. The result is rounded to the
	 * {@link #getScale() scale} of this Decimal using default
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. If the multiplication
	 * causes an overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param multiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @return <tt>round<sub>HALF_UP</sub>(this * multiplicand)</tt>
	 */
	Decimal<S> multiply(Decimal<S> multiplicand);

	/**
	 * Returns a {@code Decimal} whose value is
	 * {@code round(this * multiplicand)}. The result is rounded to the
	 * {@link #getScale() scale} of this Decimal using the specified
	 * {@code roundingMode}. If the multiplication causes an overflow, the
	 * result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param multiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the result needs to be rounded
	 * @return <tt>round(this * multiplicand)</tt>
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	Decimal<S> multiply(Decimal<S> multiplicand, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * {@code round(this * multiplicand)}. The result is rounded to the
	 * {@link #getScale() scale} of this Decimal using the {@link RoundingMode}
	 * specified by the {@code truncationPolicy} argument. The
	 * {@code truncationPolicy} also defines the {@link OverflowMode} to apply
	 * if an overflow occurs during the multiply operation.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param multiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary or if
	 *            an overflow occurs
	 * @return <tt>round(this * multiplicand)</tt>
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> multiply(Decimal<S> multiplicand, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is
	 * {@code round(this * multiplicand)} after converting the given
	 * {@code multiplicand} argument to the scale of {@code this} Decimal.
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding mode is used if necessary
	 * and applied twice during the conversion step <i>before</i> the
	 * multiplication and again when rounding the product to the
	 * {@link #getScale() scale} of this Decimal. If the multiplication causes
	 * an overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param multiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @return <tt>round<sub>HALF_UP</sub>(this * multiplicand)</tt>
	 */
	Decimal<S> multiplyBy(Decimal<?> multiplicand);

	/**
	 * Returns a {@code Decimal} whose value is
	 * {@code round(this * multiplicand)} after converting the given
	 * {@code multiplicand} argument to the scale of {@code this} Decimal.
	 * Rounding, if necessary, uses the specified {@code roundingMode} and is
	 * applied during the conversion step <i>before</i> the multiplication and
	 * again when rounding the product to the {@link #getScale() scale} of this
	 * Decimal. If the multiplication causes an overflow, the result is silently
	 * truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param multiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the converted multiplicand or
	 *            the resulting product needs to be rounded
	 * @return <tt>round(this * multiplicand)</tt>
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	Decimal<S> multiplyBy(Decimal<?> multiplicand, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * {@code round(this * multiplicand)} after converting the given
	 * {@code multiplicand} argument to the scale of {@code this} Decimal.
	 * Rounding, if necessary, is defined by the specified
	 * {@code truncationPolicy} argument and is applied during the conversion
	 * step <i>before</i> the multiplication and again when rounding the product
	 * to the {@link #getScale() scale} of this Decimal. The
	 * {@code truncationPolicy} also defines the {@link OverflowMode} to apply
	 * if an overflow occurs during the multiply operation.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param multiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary or if
	 *            an overflow occurs
	 * @return <tt>round(this * multiplicand)</tt>
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> multiplyBy(Decimal<?> multiplicand, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * multiplicand)}.
	 * The scale of the returned value is the sum of the scales of {@code this}
	 * Decimal and the {@code multiplicand} argument. If the result scale
	 * exceeds 18, an {@link IllegalArgumentException} is thrown. If the product
	 * is out of the possible range for a Decimal with the result scale then an
	 * {@code ArithmeticException} is thrown.
	 * <p>
	 * Note that the result is <i>always</i> a new instance --- immutable if
	 * this Decimal is an {@link ImmutableDecimal} and mutable if it is a
	 * {@link MutableDecimal}.
	 * 
	 * @param multiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @return {@code (this * multiplicand)} with scale equal to the sum of
	 *         scales of {@code this} and {@code multiplicand}
	 * @throws IllegalArgumentException
	 *             if the sum of the scales of {@code this} Decimal and the
	 *             {@code multiplicand} argument exceeds 18
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a Decimal with the result scale
	 */
	Decimal<?> multiplyExact(Decimal<?> multiplicand);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * multiplicand)}.
	 * If the multiplication causes an overflow, the result is silently
	 * truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param multiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @return {@code (this * multiplicand)}
	 */
	Decimal<S> multiply(long multiplicand);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * multiplicand)}.
	 * The specified {@code overflowMode} determines whether to truncate the
	 * result silently or to throw an exception if an overflow occurs.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param multiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @param overflowMode
	 *            the overflow mode to apply if the multiplication leads to an
	 *            overflow
	 * @return {@code (this * multiplicand)}
	 * @throws ArithmeticException
	 *             if {@code overflowMode==CHECKED} and an overflow occurs
	 */
	Decimal<S> multiply(long multiplicand, OverflowMode overflowMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * {@code round(this * multiplicand)} after converting the given
	 * {@code double} argument into a Decimal value of the same scale as
	 * {@code this} Decimal. {@link RoundingMode#HALF_UP HALF_UP} rounding mode
	 * is used if necessary and applied twice during the conversion step
	 * <i>before</i> the multiplication and again when rounding the product to
	 * the {@link #getScale() scale} of this Decimal. Overflows due to
	 * conversion or multiplication result in an exception.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param multiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @return <tt>round<sub>HALF_UP</sub>(this * multiplicand)</tt>
	 * @throws NumberFormatException
	 *             if {@code multiplicand} is NaN or infinite or if the
	 *             magnitude is too large for the double to be represented as a
	 *             {@code Decimal}
	 * @throws ArithmeticException
	 *             if an overflow occurs during the multiply operation
	 */
	Decimal<S> multiply(double multiplicand);

	/**
	 * Returns a {@code Decimal} whose value is
	 * {@code round(this * multiplicand)} after converting the given
	 * {@code double} argument into a Decimal value of the same scale as
	 * {@code this} Decimal. Rounding, if necessary, uses the specified
	 * {@code roundingMode} and is applied during the conversion step
	 * <i>before</i> the multiplication and again when rounding the product to
	 * the {@link #getScale() scale} of this Decimal. Overflows due to
	 * conversion or multiplication result in an exception.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param multiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the converted multiplicand or
	 *            the resulting product needs to be rounded
	 * @return {@code round(this * multiplicand)}
	 * @throws NumberFormatException
	 *             if {@code multiplicand} is NaN or infinite or if the
	 *             magnitude is too large for the double to be represented as a
	 *             {@code Decimal}
	 * @throws ArithmeticException
	 *             if an overflow occurs during the multiply operation
	 */
	Decimal<S> multiply(double multiplicand, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>round(this * unscaledMultiplicand &times; 10<sup>-scale</sup>)</tt>
	 * with the {@link #getScale() scale} of this Decimal. The result is rounded
	 * to the scale of this Decimal using default {@link RoundingMode#HALF_UP
	 * HALF_UP} rounding. If the multiplication causes an overflow, the result
	 * is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param unscaledMultiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @return <tt>round<sub>HALF_UP</sub>(this * unscaledMultiplicand &times; 10<sup>-scale</sup>)</tt>
	 */
	Decimal<S> multiplyUnscaled(long unscaledMultiplicand);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>round(this * unscaledMultiplicand &times; 10<sup>-scale</sup>)</tt>
	 * with the {@link #getScale() scale} of this Decimal. The result is rounded
	 * to the scale of this Decimal using the specified {@code roundingMode}. If
	 * the multiplication causes an overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param unscaledMultiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the result needs to be rounded
	 * @return <tt>round(this * unscaledMultiplicand &times; 10<sup>-scale</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	Decimal<S> multiplyUnscaled(long unscaledMultiplicand, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>round(this * unscaledMultiplicand &times; 10<sup>-scale</sup>)</tt>
	 * with the {@link #getScale() scale} of this Decimal. The result is rounded
	 * to the scale of this Decimal using the using the {@link RoundingMode}
	 * specified by the {@code truncationPolicy} argument. The
	 * {@code truncationPolicy} also defines the {@link OverflowMode} to apply
	 * if an overflow occurs during the multiplication.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param unscaledMultiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary or if
	 *            an overflow occurs
	 * @return <tt>round(this * unscaledMultiplicand &times; 10<sup>-scale</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> multiplyUnscaled(long unscaledMultiplicand, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>round(this * unscaledMultiplicand &times; 10<sup>-scale</sup>)</tt>.
	 * The {@code unscaledMultiplicand} argument is converted to the same scale
	 * as {@code this} Decimal. {@link RoundingMode#HALF_UP HALF_UP} rounding
	 * mode is used if necessary and rounding is applied twice during the
	 * conversion step <i>before</i> the multiplication and again when rounding
	 * the product to the {@link #getScale() scale} of this Decimal. If the
	 * multiplication causes an overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param unscaledMultiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @param scale
	 *            the scale to apply to {@code unscaledMultiplicand}, positive
	 *            to indicate the number of fraction digits to the right of the
	 *            Decimal point and negative to indicate up-scaling with a power
	 *            of ten
	 * @return <tt>round<sub>HALF_UP</sub>(this * unscaledMultiplicand &times; 10<sup>-scale</sup>)</tt>
	 */
	Decimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>round(this * unscaledMultiplicand &times; 10<sup>-scale</sup>)</tt>.
	 * The {@code unscaledMultiplicand} argument is converted to the same scale
	 * as {@code this} Decimal. Rounding, if necessary, uses the specified
	 * {@code roundingMode} and is applied during the conversion step
	 * <i>before</i> the multiplication and again when rounding the product to
	 * the {@link #getScale() scale} of this Decimal. If the multiplication
	 * causes an overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param unscaledMultiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @param scale
	 *            the scale to apply to {@code unscaledMultiplicand}, positive
	 *            to indicate the number of fraction digits to the right of the
	 *            Decimal point and negative to indicate up-scaling with a power
	 *            of ten
	 * @param roundingMode
	 *            the rounding mode to apply if the result needs to be rounded
	 * @return <tt>round(this * unscaledMultiplicand &times; 10<sup>-scale</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	Decimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>round(this * unscaledMultiplicand &times; 10<sup>-scale</sup>)</tt>.
	 * The {@code unscaledMultiplicand} argument is converted to the same scale
	 * as {@code this} Decimal. Rounding, if necessary, is defined by the
	 * specified {@code truncationPolicy} argument and is applied during the
	 * conversion step <i>before</i> the multiplication and again when rounding
	 * the product to the {@link #getScale() scale} of this Decimal. The
	 * {@code truncationPolicy} also defines the {@link OverflowMode} to apply
	 * if an overflow occurs during the multiplication.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param unscaledMultiplicand
	 *            factor to multiply with this {@code Decimal}
	 * @param scale
	 *            the scale to apply to {@code unscaledMultiplicand}, positive
	 *            to indicate the number of fraction digits to the right of the
	 *            Decimal point and negative to indicate up-scaling with a power
	 *            of ten
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary or if
	 *            an overflow occurs
	 * @return <tt>round(this * unscaledMultiplicand &times; 10<sup>-scale</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>round(this * 10<sup>n</sup>)</tt>. For negative <tt>n</tt> the
	 * multiplication turns into a de-facto division and the result is rounded
	 * to the {@link #getScale() scale} of this Decimal using default
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. If the multiplication
	 * causes an overflow, the result is silently truncated.
	 * <p>
	 * The result of this operation is the same as for
	 * {@link #divideByPowerOfTen(int) divideByPowerOfTen(-n)} given
	 * {@code n > }{@link Integer#MIN_VALUE}.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param n
	 *            the exponent of the power-of-ten factor to multiply with this
	 *            {@code Decimal}
	 * @return <tt>round<sub>HALF_UP</sub>(this * 10<sup>n</sup>)</tt>
	 */
	Decimal<S> multiplyByPowerOfTen(int n);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>round(this * 10<sup>n</sup>)</tt>. For negative <tt>n</tt> the
	 * multiplication turns into a de-facto division and the result is rounded
	 * to the {@link #getScale() scale} of this Decimal using the specified
	 * {@code roundingMode}. If the multiplication causes an overflow, the
	 * result is silently truncated.
	 * <p>
	 * The result of this operation is the same as for
	 * {@link #divideByPowerOfTen(int, RoundingMode) divideByPowerOfTen(-n,
	 * roundingMode)} given {@code n > }{@link Integer#MIN_VALUE}.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param n
	 *            the exponent of the power-of-ten factor to multiply with this
	 *            {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the result needs to be rounded
	 *            for the case {@code n < 0}
	 * @return <tt>round(this * 10<sup>n</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@code n < 0} and {@code roundingMode==UNNECESSARY} and
	 *             rounding is necessary
	 */
	Decimal<S> multiplyByPowerOfTen(int n, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>round(this * 10<sup>n</sup>)</tt>. For negative <tt>n</tt> the
	 * multiplication turns into a de-facto division and the result is rounded
	 * to the {@link #getScale() scale} of this Decimal using the
	 * {@link RoundingMode} specified by the {@code truncationPolicy} argument.
	 * The {@code truncationPolicy} also defines the {@link OverflowMode} to
	 * apply if an overflow occurs during the multiplication.
	 * <p>
	 * The result of this operation is the same as for
	 * {@link #divideByPowerOfTen(int, TruncationPolicy) divideByPowerOfTen(-n,
	 * truncationPolicy)} given {@code n > }{@link Integer#MIN_VALUE}.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the multiplication.
	 * 
	 * @param n
	 *            the exponent of the power-of-ten factor to multiply with this
	 *            {@code Decimal}
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} to apply
	 *            if rounding is necessary when {@code n < 0} as well
	 *            {@link OverflowMode} to use if {@code n > 0} and an overflow
	 *            occurs during the multiplication
	 * @return <tt>round(this * 10<sup>n</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary
	 *             when {@code n < 0}, or if an overflow occurs and the policy
	 *             declares {@link OverflowMode#CHECKED} for the case
	 *             {@code n > 0}
	 */
	Decimal<S> multiplyByPowerOfTen(int n, TruncationPolicy truncationPolicy);

	// FIXME

	// divide

	Decimal<S> divide(Decimal<S> divisor);

	Decimal<S> divide(Decimal<S> divisor, RoundingMode roundingMode);

	Decimal<S> divide(Decimal<S> divisor, TruncationPolicy truncationPolicy);

	Decimal<S> divideBy(Decimal<?> divisor);

	Decimal<S> divideBy(Decimal<?> divisor, RoundingMode roundingMode);

	Decimal<S> divideBy(Decimal<?> divisor, TruncationPolicy truncationPolicy);

	Decimal<S> divideTruncate(Decimal<S> divisor);

	Decimal<S> divideExact(Decimal<S> divisor);

	Decimal<S> divide(long divisor);

	Decimal<S> divide(long divisor, RoundingMode roundingMode);

	Decimal<S> divide(long divisor, TruncationPolicy truncationPolicy);

	Decimal<S> divide(double divisor);

	Decimal<S> divide(double divisor, RoundingMode roundingMode);

	Decimal<S> divideUnscaled(long unscaledDivisor);

	Decimal<S> divideUnscaled(long unscaledDivisor, RoundingMode roundingMode);

	Decimal<S> divideUnscaled(long unscaledDivisor, TruncationPolicy truncationPolicy);

	Decimal<S> divideUnscaled(long unscaledDivisor, int scale);

	Decimal<S> divideUnscaled(long unscaledDivisor, int scale, RoundingMode roundingMode);

	Decimal<S> divideUnscaled(long unscaledDivisor, int scale, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>round(this / 10<sup>n</sup>)</tt>. The result is rounded to the
	 * {@link #getScale() scale} of this Decimal using default
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding.
	 * <p>
	 * For negative <tt>n</tt> the division turns into a de-facto
	 * multiplication. If the multiplication causes an overflow, the result is
	 * silently truncated.
	 * <p>
	 * The result of this operation is the same as for
	 * {@link #multiplyByPowerOfTen(int) multiplyByPowerOfTen(-n)} given
	 * {@code n > } {@link Integer#MIN_VALUE}.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the division.
	 * 
	 * @param n
	 *            the exponent of the power-of-ten divisor by which this
	 *            {@code Decimal} is to be divided
	 * @return <tt>round<sub>HALF_UP</sub>(this / 10<sup>n</sup>)</tt>
	 */
	Decimal<S> divideByPowerOfTen(int n);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>round(this / 10<sup>n</sup>)</tt>. The result is rounded to the
	 * {@link #getScale() scale} of this Decimal using the specified
	 * {@code roudningMode}.
	 * <p>
	 * For negative <tt>n</tt> the division turns into a de-facto
	 * multiplication. If the multiplication causes an overflow, the result is
	 * silently truncated.
	 * <p>
	 * The result of this operation is the same as for
	 * {@link #multiplyByPowerOfTen(int, RoundingMode) multiplyByPowerOfTen(-n,
	 * roundingMode)} given {@code n > } {@link Integer#MIN_VALUE}.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the division.
	 * 
	 * @param n
	 *            the exponent of the power-of-ten divisor by which this
	 *            {@code Decimal} is to be divided
	 * @param roundingMode
	 *            the rounding mode to apply if the result needs to be rounded
	 *            for the case {@code n > 0}
	 * @return <tt>round(this / 10<sup>n</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@code n > 0} and {@code roundingMode==UNNECESSARY} and
	 *             rounding is necessary
	 */
	Decimal<S> divideByPowerOfTen(int n, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>round(this / 10<sup>n</sup>)</tt>. The result is rounded to the
	 * {@link #getScale() scale} of this Decimal using the {@link RoundingMode}
	 * specified by the {@code truncationPolicy} argument.
	 * <p>
	 * For negative <tt>n</tt> the division turns into a de-facto multiplication
	 * and {@code truncationPolicy} defines the {@link OverflowMode} to apply if
	 * an overflow occurs during the multiplication.
	 * <p>
	 * The result of this operation is the same as for
	 * {@link #multiplyByPowerOfTen(int, TruncationPolicy)
	 * multiplyByPowerOfTen(-n, truncationPolicy)} given {@code n > }
	 * {@link Integer#MIN_VALUE}.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the division.
	 * 
	 * @param n
	 *            the exponent of the power-of-ten divisor by which this
	 *            {@code Decimal} is to be divided
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} to apply
	 *            if rounding is necessary when {@code n > 0} as well
	 *            {@link OverflowMode} to use if {@code n < 0} and an overflow
	 *            occurs during the de-facto multiplication
	 * @return <tt>round(this / 10<sup>n</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary
	 *             when {@code n > 0}, or if an overflow occurs and the policy
	 *             declares {@link OverflowMode#CHECKED} for the case
	 *             {@code n < 0}
	 */
	Decimal<S> divideByPowerOfTen(int n, TruncationPolicy truncationPolicy);

	//FIXME

	/**
	 * Returns a {@code Decimal} whose value is the integer part of the quotient
	 * {@code (this / divisor)} rounded down. If the division causes an
	 * overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the division.
	 *
	 * @param divisor
	 *            value by which this {@code Decimal} is to be divided.
	 * @return The integer part of {@code (this / divisor)}.
	 * @throws ArithmeticException
	 *             if {@code divisor==0}
	 */
	Decimal<S> divideToIntegralValue(Decimal<S> divisor);

	/**
	 * Returns a {@code Decimal} whose value is the integer part of the quotient
	 * {@code (this / divisor)} rounded down. The specified {@code overflowMode}
	 * determines whether to truncate the result silently or to throw an
	 * exception if an overflow occurs.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the division.
	 *
	 * @param divisor
	 *            value by which this {@code Decimal} is to be divided.
	 * @param overflowMode
	 *            the overflow mode to apply if the division leads to an
	 *            overflow
	 * @return The integer part of {@code (this / divisor)}.
	 * @throws ArithmeticException
	 *             if {@code divisor==0} or if {@code overflowMode==CHECKED} and
	 *             an overflow occurs
	 */
	Decimal<S> divideToIntegralValue(Decimal<S> divisor, OverflowMode overflowMode);

	/**
	 * Returns a {@code Decimal} whose value is the integer part of the quotient
	 * {@code (this / divisor)} rounded down. The result is returned as
	 * {@code long} value.
	 *
	 * @param divisor
	 *            value by which this {@code Decimal} is to be divided.
	 * @return The integer part of {@code (this / divisor)} returned as
	 *         {@code long}
	 * @throws ArithmeticException
	 *             if {@code divisor==0}
	 */
	long divideToLongValue(Decimal<S> divisor);

	/**
	 * Returns a two-element {@code Decimal} array containing the result of
	 * {@code divideToIntegralValue} followed by the result of {@code remainder}
	 * on the two operands. If the division causes an overflow, the result is
	 * silently truncated.
	 * <p>
	 * Note that if both the integer quotient and remainder are needed, this
	 * method is faster than using the {@code divideToIntegralValue} and
	 * {@code remainder} methods separately because the division need only be
	 * carried out once.
	 *
	 * @param divisor
	 *            value by which this {@code Decimal} is to be divided, and the
	 *            remainder computed.
	 * @return a two element {@code Decimal} array: the quotient (the result of
	 *         {@code divideToIntegralValue}) is the initial element and the
	 *         remainder is the final element.
	 * @throws ArithmeticException
	 *             if {@code divisor==0}
	 * @see #divideToIntegralValue(Decimal)
	 * @see #remainder(Decimal)
	 */
	Decimal<S>[] divideAndRemainder(Decimal<S> divisor);

	/**
	 * Returns a two-element {@code Decimal} array containing the result of
	 * {@code divideToIntegralValue} followed by the result of {@code remainder}
	 * on the two operands. The specified {@code overflowMode} determines
	 * whether to truncate the result silently or to throw an exception if an
	 * overflow occurs.
	 * <p>
	 * Note that if both the integer quotient and remainder are needed, this
	 * method is faster than using the {@code divideToIntegralValue} and
	 * {@code remainder} methods separately because the division need only be
	 * carried out once.
	 *
	 * @param divisor
	 *            value by which this {@code Decimal} is to be divided, and the
	 *            remainder computed.
	 * @param overflowMode
	 *            the overflow mode to apply if the division leads to an
	 *            overflow
	 * @return a two element {@code Decimal} array: the quotient (the result of
	 *         {@code divideToIntegralValue}) is the initial element and the
	 *         remainder is the final element.
	 * @throws ArithmeticException
	 *             if {@code divisor==0} or if {@code overflowMode==CHECKED} and
	 *             an overflow occurs
	 * @see #divideToIntegralValue(Decimal)
	 * @see #remainder(Decimal)
	 */
	Decimal<S>[] divideAndRemainder(Decimal<S> divisor, OverflowMode overflowMode);

	//@formatter:off
	/**
	 * Returns a {@code Decimal} whose value is {@code (this % divisor)}.
	 * <p>
	 * The remainder is given by
	 * {@code this.subtract(this.divideToIntegralValue(divisor).multiply(divisor))}.
	 * Note that this is not the modulo operation (the result can be
	 * negative).
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the operation.
	 *
	 * @param divisor
	 *            value by which this {@code Decimal} is to be divided.
	 * @return {@code this % divisor}.
	 * @throws ArithmeticException
	 *             if {@code divisor==0}
	 * @see #divideToIntegralValue(Decimal)
	 */
	//@formatter:on
	Decimal<S> remainder(Decimal<S> divisor);

	// other arithmetic operations

	/**
	 * Returns a {@code Decimal} whose value is {@code (-this)}.
	 * <p>
	 * If an overflow occurs (which is true iff
	 * {@code this.unscaledValue()==Long.MIN_VALUE}) then the result is still
	 * negative and numerically equal to {@code this} value.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the division.
	 * 
	 * @return {@code -this}
	 */
	Decimal<S> negate();

	/**
	 * Returns a {@code Decimal} whose value is {@code (-this)}.
	 * <p>
	 * The specified {@code overflowMode} determines whether to truncate the
	 * result silently or to throw an exception if an overflow occurs (which is
	 * true iff {@code this.unscaledValue()==Long.MIN_VALUE}).
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the division.
	 * 
	 * @param overflowMode
	 *            the overflow mode to apply
	 * @return {@code -this}
	 * @throws ArithmeticException
	 *             if {@code overflowMode==CHECKED} and an overflow occurs
	 *             (which is true iff
	 *             {@code this.unscaledValue()==Long.MIN_VALUE})
	 */
	Decimal<S> negate(OverflowMode overflowMode);

	/**
	 * Returns a {@code Decimal} whose value is the absolute value of this
	 * {@code Decimal}.
	 * <p>
	 * If an overflow occurs (which is true iff
	 * {@code this.unscaledValue()==Long.MIN_VALUE}) then the result is still
	 * negative and numerically equal to {@code this} value.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the division.
	 * 
	 * @return {@code abs(this)}
	 */
	Decimal<S> abs();

	/**
	 * Returns a {@code Decimal} whose value is the absolute value of this
	 * {@code Decimal}.
	 * <p>
	 * The specified {@code overflowMode} determines whether to truncate the
	 * result silently or to throw an exception if an overflow occurs (which is
	 * true iff {@code this.unscaledValue()==Long.MIN_VALUE}).
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the division.
	 * 
	 * @param overflowMode
	 *            the overflow mode to apply
	 * @return {@code abs(this)}
	 * @throws ArithmeticException
	 *             if {@code overflowMode==CHECKED} and an overflow occurs
	 *             (which is true iff
	 *             {@code this.unscaledValue()==Long.MIN_VALUE})
	 */
	Decimal<S> abs(OverflowMode overflowMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code round(1 / this)}. The
	 * result is rounded to the {@link #getScale() scale} of this Decimal using
	 * default {@link RoundingMode#HALF_UP HALF_UP} rounding. If the inversion
	 * causes an overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the inversion.
	 * 
	 * @return <tt>round<sub>HALF_UP</sub>(1 / this)</tt>
	 * @throws ArithmeticException
	 *             if {@code this==0}
	 */
	Decimal<S> invert();

	/**
	 * Returns a {@code Decimal} whose value is {@code round(1 / this)}. The
	 * result is rounded to the {@link #getScale() scale} of this Decimal using
	 * the specified {@code roundingMode}. If the inversion causes an overflow,
	 * the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the inversion.
	 * 
	 * @param roundingMode
	 *            the rounding mode to apply if the result needs to be rounded
	 * @return {@code round(1 / this)}
	 * @throws ArithmeticException
	 *             if {@code this==0} or if {@code roundingMode==UNNECESSARY}
	 *             and rounding is necessary
	 */
	Decimal<S> invert(RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code round(1 / this)}. The
	 * result is rounded to the {@link #getScale() scale} of this Decimal using
	 * the {@link RoundingMode} specified by the {@code truncationPolicy}
	 * argument. The {@code truncationPolicy} also defines the
	 * {@link OverflowMode} to apply if an overflow occurs during the invert
	 * operation.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the inversion.
	 * 
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary or if
	 *            an overflow occurs
	 * @return {@code round(1 / this)}
	 * @throws ArithmeticException
	 *             if {@code this==0}, if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> invert(TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is <tt>(this<sup>2</sup>)</tt>. The
	 * result is rounded to the {@link #getScale() scale} of this Decimal using
	 * default {@link RoundingMode#HALF_UP HALF_UP} rounding. If the square
	 * operation causes an overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the square operation.
	 * 
	 * @return <tt>round<sub>HALF_UP</sub>(this * this)</tt>
	 */
	Decimal<S> square();

	/**
	 * Returns a {@code Decimal} whose value is <tt>(this<sup>2</sup>)</tt>. The
	 * result is rounded to the {@link #getScale() scale} of this Decimal using
	 * the specified {@code roundingMode}. If the square operation causes an
	 * overflow, the result is silently truncated.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the square operation.
	 * 
	 * @param roundingMode
	 *            the rounding mode to apply if the result needs to be rounded
	 * @return {@code round(this * this)}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	Decimal<S> square(RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is <tt>(this<sup>2</sup>)</tt>. The
	 * result is rounded to the {@link #getScale() scale} of this Decimal using
	 * the {@link RoundingMode} specified by the {@code truncationPolicy}
	 * argument. The {@code truncationPolicy} also defines the
	 * {@link OverflowMode} to apply if an overflow occurs during the square
	 * operation.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the square operation.
	 * 
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary or if
	 *            an overflow occurs
	 * @return {@code round(this * this)}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> square(TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is the square root of {@code this}
	 * Decimal value. The result is rounded to the {@link #getScale() scale} of
	 * this Decimal using default {@link RoundingMode#HALF_UP HALF_UP} rounding.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the operation.
	 * 
	 * @return {@code sqrt(this)}
	 * @throws ArithmeticException
	 *             if {@code this < 0}
	 */
	Decimal<S> sqrt();

	/**
	 * Returns a {@code Decimal} whose value is the square root of {@code this}
	 * Decimal value. The result is rounded to the {@link #getScale() scale} of
	 * this Decimal using the specified {@code roundingMode}.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the operation.
	 * 
	 * @param roundingMode
	 *            the rounding mode to apply if the result needs to be rounded
	 * @return {@code sqrt(this)}
	 * @throws ArithmeticException
	 *             if {@code this < 0} or if {@code roundingMode==UNNECESSARY}
	 *             and rounding is necessary
	 */
	Decimal<S> sqrt(RoundingMode roundingMode);

	/**
	 * Returns the signum function of this {@code Decimal}.
	 * 
	 * @return -1, 0, or 1 as the value of this {@code Decimal} is negative,
	 *         zero, or positive.
	 */
	int signum();

	/**
	 * Returns a {@code Decimal} whose value is {@code (this << n)}. The shift
	 * distance, {@code n}, may be negative, in which case this method performs
	 * a right shift.
	 * <p>
	 * Computes <tt>floor(this * 2<sup>n</sup>)</tt>.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the shift operation.
	 *
	 * @param n
	 *            shift distance, in bits.
	 * @return {@code this << n}
	 * @see #shiftRight
	 */
	Decimal<S> shiftLeft(int n);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this << n)}. The shift
	 * distance, {@code n}, may be negative, in which case this method performs
	 * a right shift.
	 * <p>
	 * Computes <tt>round(this * 2<sup>n</sup>)</tt> using the specified
	 * {@code roundingMode}.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the shift operation.
	 *
	 * @param n
	 *            shift distance, in bits.
	 * @param roundingMode
	 *            the rounding mode to use if truncation is involved for
	 *            negative {@code n}, that is, for right shifts
	 * @return {@code this << n}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 * @see #shiftRight
	 */
	Decimal<S> shiftLeft(int n, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this << n)}. The shift
	 * distance, {@code n}, may be negative, in which case this method performs
	 * a right shift.
	 * <p>
	 * Computes <tt>round(this * 2<sup>n</sup>)</tt> using the
	 * {@link RoundingMode} specified by the {@code truncationPolicy} argument.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the shift operation.
	 *
	 * @param n
	 *            shift distance, in bits.
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary or if
	 *            an overflow occurs
	 * @return {@code this << n}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 * @see #shiftRight
	 */
	Decimal<S> shiftLeft(int n, TruncationPolicy truncationPolicy);

	/**
	 * Returns a BigInteger whose value is {@code (this >> n)}. Sign extension
	 * is performed. The shift distance, {@code n}, may be negative, in which
	 * case this method performs a left shift.
	 * <p>
	 * Computes <tt>floor(this / 2<sup>n</sup>)</tt>.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the shift operation.
	 *
	 * @param n
	 *            shift distance, in bits.
	 * @return {@code this >> n}
	 * @see #shiftLeft
	 */
	Decimal<S> shiftRight(int n);

	/**
	 * Returns a BigInteger whose value is {@code (this >> n)}. Sign extension
	 * is performed. The shift distance, {@code n}, may be negative, in which
	 * case this method performs a left shift.
	 * <p>
	 * Computes <tt>round(this / 2<sup>n</sup>)</tt> using the specified
	 * {@code roundingMode}.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the shift operation.
	 *
	 * @param n
	 *            shift distance, in bits.
	 * @param roundingMode
	 *            the rounding mode to use if truncation is involved
	 * @return {@code this >> n}
	 * @see #shiftLeft
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	Decimal<S> shiftRight(int n, RoundingMode roundingMode);

	/**
	 * Returns a BigInteger whose value is {@code (this >> n)}. Sign extension
	 * is performed. The shift distance, {@code n}, may be negative, in which
	 * case this method performs a left shift.
	 * <p>
	 * Computes <tt>round(this / 2<sup>n</sup>)</tt> using the
	 * {@link RoundingMode} specified by the {@code truncationPolicy} argument.
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the shift operation.
	 *
	 * @param n
	 *            shift distance, in bits.
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary or if
	 *            an overflow occurs
	 * @return {@code this >> n}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 * @see #shiftLeft
	 */
	Decimal<S> shiftRight(int n, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is <tt>(this<sup>n</sup>)</tt>
	 * using default {@link RoundingMode#HALF_UP HALF_UP} rounding.
	 * <p>
	 * The current implementation uses the core algorithm defined in ANSI
	 * standard X3.274-1996. For {@code n >= 0}, the returned numerical value is
	 * within 1 ULP of the exact numerical value. No precision is guaranteed for
	 * {@code n < 0} but the result is usually exact up to 10-20 ULP.
	 * <p>
	 * Properties of the X3.274-1996 algorithm are:
	 * <ul>
	 * <li>An {@code ArithmeticException} exception is thrown if
	 * {@code abs(n) > 999999999}</li>
	 * <li>if {@code n} is zero, one is returned even if {@code this} is zero,
	 * otherwise</li>
	 * <ul>
	 * <li>if {@code n} is positive, the result is calculated via the repeated
	 * squaring technique into a single accumulator</li>
	 * <li>if {@code n} is negative, the result is calculated as if {@code n}
	 * were positive; this value is then divided into one</li>
	 * <li>The final value from either the positive or negative case is then
	 * rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding</li>
	 * </ul>
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the shift operation.
	 * 
	 * @param n
	 *            power to raise this {@code Decimal} to
	 * @return <tt>this<sup>n</sup></tt> using the ANSI standard X3.274-1996
	 *         algorithm
	 * @throws ArithmeticException
	 *             if {@code abs(n) > 999999999} or if {@code n} is negative and
	 *             {@code this} equals zero
	 */
	Decimal<S> pow(int n);

	/**
	 * Returns a {@code Decimal} whose value is <tt>(this<sup>n</sup>)</tt>
	 * applying the specified {@code roundingMode}.
	 * <p>
	 * The current implementation uses the core algorithm defined in ANSI
	 * standard X3.274-1996. For {@code n >= 0}, the returned numerical value is
	 * within 1 ULP of the exact numerical value; the result is actually exact
	 * for all rounding modes other than HALF_UP, HALF_EVEN and HALF_DOWN. No
	 * precision is guaranteed for {@code n < 0} but the result is usually exact
	 * up to 10-20 ULP.
	 * <p>
	 * Properties of the X3.274-1996 algorithm are:
	 * <ul>
	 * <li>An {@code ArithmeticException} exception is thrown if
	 * {@code abs(n) > 999999999}</li>
	 * <li>if {@code n} is zero, one is returned even if {@code this} is zero,
	 * otherwise</li>
	 * <ul>
	 * <li>if {@code n} is positive, the result is calculated via the repeated
	 * squaring technique into a single accumulator</li>
	 * <li>if {@code n} is negative, the result is calculated as if {@code n}
	 * were positive; this value is then divided into one</li>
	 * <li>The final value from either the positive or negative case is then
	 * rounded using the specified {@code roundingMode}</li>
	 * </ul>
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the shift operation.
	 * 
	 * @param n
	 *            power to raise this {@code Decimal} to
	 * @param roundingMode
	 *            the rounding mode to apply if rounding is necessary
	 * @return <tt>this<sup>n</sup></tt> using the ANSI standard X3.274-1996
	 *         algorithm
	 * @throws ArithmeticException
	 *             if {@code abs(n) > 999999999}; if {@code n} is negative and
	 *             {@code this} equals zero or if {@code roundingMode} equals
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary
	 */
	Decimal<S> pow(int n, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is <tt>(this<sup>n</sup>)</tt>
	 * applying the {@link RoundingMode} specified by {@code truncationPolicy}.
	 * The {@code truncationPolicy} argument also defines the
	 * {@link OverflowMode} to apply if an overflow occurs during the power
	 * operation.
	 * <p>
	 * The current implementation uses the core algorithm defined in ANSI
	 * standard X3.274-1996. For {@code n >= 0}, the returned numerical value is
	 * within 1 ULP of the exact numerical value; the result is actually exact
	 * for all rounding modes other than HALF_UP, HALF_EVEN and HALF_DOWN. No
	 * precision is guaranteed for {@code n < 0} but the result is usually exact
	 * up to 10-20 ULP.
	 * <p>
	 * Properties of the X3.274-1996 algorithm are:
	 * <ul>
	 * <li>An {@code ArithmeticException} exception is thrown if
	 * {@code abs(n) > 999999999}</li>
	 * <li>if {@code n} is zero, one is returned even if {@code this} is zero,
	 * otherwise</li>
	 * <ul>
	 * <li>if {@code n} is positive, the result is calculated via the repeated
	 * squaring technique into a single accumulator</li>
	 * <li>if {@code n} is negative, the result is calculated as if {@code n}
	 * were positive; this value is then divided into one</li>
	 * <li>The final value from either the positive or negative case is then
	 * rounded using the {@link RoundingMode} specified by
	 * {@code truncationPolicy}</li>
	 * </ul>
	 * <p>
	 * The returned value is a new instance if this Decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the shift operation.
	 * 
	 * @param n
	 *            power to raise this {@code Decimal} to
	 * @param truncationPolicy
	 *            the truncation policy specifying {@link RoundingMode} and
	 *            {@link OverflowMode} to apply if rounding is necessary or if
	 *            an overflow occurs
	 * @return <tt>this<sup>n</sup></tt> using the ANSI standard X3.274-1996
	 *         algorithm
	 * @throws ArithmeticException
	 *             if {@code abs(n) > 999999999}; if {@code n} is negative and
	 *             {@code this} equals zero; if {@code truncationPolicy} defines
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> pow(int n, TruncationPolicy truncationPolicy);

	// compare and related methods

	/**
	 * Compares two {@code Decimal} objects numerically.
	 * 
	 * @param other
	 *            {@code Decimal} to which this {@code Decimal} is to be
	 *            compared
	 * @return the value {@code 0} if this {@code Decimal} is equal to the
	 *         argument {@code Decimal}; a value less than {@code 0} if this
	 *         {@code Decimal} is numerically less than the argument
	 *         {@code Decimal}; and a value greater than {@code 0} if this
	 *         {@code Decimal} is numerically greater than the argument
	 *         {@code Decimal}
	 */
	@Override
	int compareTo(Decimal<S> other);

	/**
	 * Compares this {@code Decimal} with the specified {@code Decimal} and
	 * returns true if the two are numerically equal.
	 * <p>
	 * Returns true iff {@link #compareTo(Decimal)} returns 0.
	 * 
	 * @param other
	 *            {@code Decimal} to which this {@code Decimal} is to be
	 *            compared
	 * @return true this {@code Decimal} is numerically equal to {@code other}
	 *         and false otherwise
	 */
	boolean isEqualTo(Decimal<S> other);

	/**
	 * Compares this {@code Decimal} with the specified {@code Decimal} and
	 * returns true if this Decimal is numerically greater than {@code other}.
	 * <p>
	 * Returns true iff {@link #compareTo(Decimal)} returns a value greater than
	 * 0.
	 * 
	 * @param other
	 *            {@code Decimal} to which this {@code Decimal} is to be
	 *            compared
	 * @return true if {@code this > other}
	 */
	boolean isGreaterThan(Decimal<S> other);

	/**
	 * Compares this {@code Decimal} with the specified {@code Decimal} and
	 * returns true if this Decimal is numerically greater than or equal to
	 * {@code other}.
	 * <p>
	 * Returns true iff {@link #compareTo(Decimal)} returns a non-negative
	 * value.
	 * 
	 * @param other
	 *            {@code Decimal} to which this {@code Decimal} is to be
	 *            compared
	 * @return true if {@code this >= other}
	 */
	boolean isGreaterThanOrEqualTo(Decimal<S> other);

	/**
	 * Compares this {@code Decimal} with the specified {@code Decimal} and
	 * returns true if this Decimal is numerically less than {@code other}.
	 * <p>
	 * Returns true iff {@link #compareTo(Decimal)} returns a negative value.
	 * 
	 * @param other
	 *            {@code Decimal} to which this {@code Decimal} is to be
	 *            compared.
	 * @return true if {@code this < other}
	 */
	boolean isLessThan(Decimal<S> other);

	/**
	 * Compares this {@code Decimal} with the specified {@code Decimal} and
	 * returns true if this Decimal is numerically less than or equal to
	 * {@code other}.
	 * <p>
	 * Returns true iff {@link #compareTo(Decimal)} returns a non-positive
	 * value.
	 * 
	 * @param other
	 *            {@code Decimal} to which this {@code Decimal} is to be
	 *            compared
	 * @return true if {@code this <= other}
	 */
	boolean isLessThanOrEqualTo(Decimal<S> other);

	/**
	 * Returns the minimum of this {@code Decimal} and {@code val}.
	 *
	 * @param val
	 *            value with which the minimum is to be computed.
	 * @return the {@code Decimal} whose value is the lesser of this
	 *         {@code Decimal} and {@code val}. If they are equal, as defined by
	 *         the {@link #compareTo(Decimal) compareTo} method, {@code this} is
	 *         returned.
	 * @see #compareTo(Decimal)
	 */
	Decimal<S> min(Decimal<S> val);

	/**
	 * Returns the maximum of this {@code Decimal} and {@code val}.
	 *
	 * @param val
	 *            value with which the maximum is to be computed.
	 * @return the {@code Decimal} whose value is the greater of this
	 *         {@code Decimal} and {@code val}. If they are equal, as defined by
	 *         the {@link #compareTo(Decimal) compareTo} method, {@code this} is
	 *         returned.
	 * @see #compareTo(Decimal)
	 */
	Decimal<S> max(Decimal<S> val);

	/**
	 * Returns the average of this {@code Decimal} and {@code val} using the
	 * default rounding mode if rounding is necessary. The method is much more
	 * efficient than an addition and subsequent long division and is guaranteed
	 * not to overflow.
	 *
	 * @param val
	 *            value with which the average is to be computed.
	 * @return {@code (this+val)/2} using the default rounding mode
	 */
	Decimal<S> avg(Decimal<S> val);

	/**
	 * Returns the average of this {@code Decimal} and {@code val} using the
	 * specified rounding mode if rounding is necessary. The method is much more
	 * efficient than an addition and subsequent long division and is guaranteed
	 * not to overflow.
	 *
	 * @param val
	 *            value with which the average is to be computed.
	 * @param roundingMode
	 *            the rounding mode to use if rounding is necessary
	 * @return {@code (this+val)/2} using the specified rounding mode
	 */
	Decimal<S> avg(Decimal<S> val, RoundingMode roundingMode);

	/**
	 * Returns true if this {@code Decimal} is zero.
	 * 
	 * @return true if {@code this == 0}
	 */
	boolean isZero();

	/**
	 * Returns true if this {@code Decimal} is one.
	 * 
	 * @return true if {@code this == 1}
	 */
	boolean isOne();

	/**
	 * Returns true if this {@code Decimal} is minus one.
	 * 
	 * @return true if {@code this == -1}
	 */
	boolean isMinusOne();

	/**
	 * Returns true if this {@code Decimal} is equal to the smallest positive
	 * number representable by a Decimal with the current {@link #getScale()
	 * scale}.
	 * 
	 * @return true if {@code unscaledValue() == 1}
	 */
	boolean isUlp();

	/**
	 * Returns true if this {@code Decimal} is strictly positive.
	 * 
	 * @return true if {@code this > 0}
	 */
	boolean isPositive();

	/**
	 * Returns true if this {@code Decimal} is not negative.
	 * 
	 * @return true if {@code this >= 0}
	 */
	boolean isNonNegative();

	/**
	 * Returns true if this {@code Decimal} is negative.
	 * 
	 * @return true if {@code this < 0}
	 */
	boolean isNegative();

	/**
	 * Returns true if this {@code Decimal} is not positive.
	 * 
	 * @return true if {@code this <= 0}
	 */
	boolean isNonPositive();

	/**
	 * Returns true if this {@code Decimal} number is integral, or equivalently
	 * if its {@link #fractionalPart() fractional part} is zero.
	 * 
	 * @return true if {@code this} is an integer number
	 */
	boolean isIntegral();

	/**
	 * Returns true if the {@link #integralPart() integral part} of this
	 * {@code Decimal} number is zero.
	 * 
	 * @return true if {@code -1 < this < 1}
	 */
	boolean isIntegralPartZero();

	/**
	 * Returns true if this {@code Decimal} is between zero (inclusive) and one
	 * (exclusive). The result value is true if and only if this {@code Decimal}
	 * is not negative and its {@link #integralPart() integral part} is zero.
	 * 
	 * @return true if {@code 0 <= this < 1}
	 */
	boolean isBetweenZeroAndOne();

	/**
	 * Returns true if this {@code Decimal} is between zero (inclusive) and
	 * minus one (exclusive). The result value is true if and only if this
	 * {@code Decimal} is not positive and its {@link #integralPart() integral
	 * part} is zero.
	 * 
	 * @return true if {@code -1 < this <= 0}
	 */
	boolean isBetweenZeroAndMinusOne();

	/**
	 * Compares this {@code Decimal} with the specified {@code Decimal}. Two
	 * {@code Decimal} objects that are equal in value but have a different
	 * scale (like 2.0 and 2.00) are considered equal by this method.
	 *
	 * @param other
	 *            {@code Decimal} to which this {@code Decimal} is to be
	 *            compared.
	 * @return the value {@code 0} if this {@code Decimal} is equal to the
	 *         argument {@code Decimal}; a value less than {@code 0} if this
	 *         {@code Decimal} is numerically less than the argument
	 *         {@code Decimal}; and a value greater than {@code 0} if this
	 *         {@code Decimal} is numerically greater than the argument
	 *         {@code Decimal}
	 * @see #isEqualToNumerically(Decimal)
	 * @see #compareTo(Decimal)
	 */
	int compareToNumerically(Decimal<?> other);

	/**
	 * Compares this {@code Decimal} with the specified {@code Decimal} and
	 * returns true if the two are numerically equal. Two {@code Decimal}
	 * objects that are equal in value but have a different scale (like 2.0 and
	 * 2.00) are considered equal by this method as opposed to the
	 * {@link #equals(Object) equals} method which requires identical scales of
	 * the compared values.
	 * <p>
	 * Returns true iff {@link #compareToNumerically(Decimal)} returns 0.
	 *
	 * @param other
	 *            {@code Decimal} to which this {@code Decimal} is to be
	 *            compared.
	 * @return true if this {@code Decimal} is numerically equal to
	 *         {@code other} and false otherwise.
	 * @see #compareToNumerically(Decimal)
	 * @see #compareTo(Decimal)
	 */
	boolean isEqualToNumerically(Decimal<?> other);

	// finally some basic object methods plus equals

	/**
	 * Returns a hash code for this {@code Decimal}. The result is the exclusive
	 * OR of the two halves of the primitive unscaled {@code long} value making
	 * up this {@code Decimal} object. That is, the hashcode is the value of the
	 * expression:
	 * 
	 * <blockquote>
	 * {@code (int)(this.unscaledValue()^(this.unscaledValue()>>>32))}
	 * </blockquote>
	 * 
	 * @return a hash code value for this object
	 */
	@Override
	int hashCode();

	/**
	 * Compares this object to the specified object. The result is {@code true}
	 * if and only if the argument is a {@code Decimal} value with the same
	 * {@link #getScaleMetrics() scale} and {@link #unscaledValue() unscaled
	 * value} as this Decimal.
	 * 
	 * @param obj
	 *            the object to compare with.
	 * @return {@code true} if the argument is a {@code Decimal} object that
	 *         contains the same value and scale as this object; {@code false}
	 *         otherwise.
	 */
	@Override
	boolean equals(Object obj);

	/**
	 * Returns a string representation of this {@code Decimal} object as fixed
	 * precision Decimal always showing all Decimal places (also trailing zeros)
	 * and a leading sign character if negative.
	 * 
	 * @return a {@code String} Decimal representation of this {@code Decimal}
	 *         object with all the fraction digits (including trailing zeros)
	 */
	@Override
	String toString();
}
