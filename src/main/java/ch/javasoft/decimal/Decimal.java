package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.factory.DecimalFactory;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.truncate.OverflowMode;
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Mutable or immutable fixed-precision signed decimal numbers similar to
 * {@link BigDecimal}. A {@code Decimal} consists of an <i>unscaled long
 * value</i> and a {@link #getScaleMetrics() scale}. The scale defines the
 * number of digits to the right of the decimal point. The value of the number
 * represented by the {@code Decimal} is
 * <code>(unscaledValue &times; 10<sup>-f</sup>)</code> with scale {@code f}.
 * <p>
 * Certain operations can only be performed with other {@code Decimal} numbers
 * of the same scale. Scale compatibility of such operations is enforced through
 * the generic {@link ScaleMetrics} parameter of the {@code Decimal}. The
 * {@link ScaleMetrics} class defines all supported scale metrics subclasses
 * each with a singleton constant.
 * <p>
 * Arithmetic operations that may lead to truncation usually provide the
 * possibility to specify a {@link RoundingMode} to apply when truncating. If no
 * rounding mode is specified, {@link RoundingMode#HALF_UP} is used by default.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 */
public interface Decimal<S extends ScaleMetrics> extends Comparable<Decimal<S>> {

	/**
	 * Returns the metrics associated with the scale of this decimal. Scale
	 * defines the number of fraction digits and the scale factor applied to the
	 * {@code long} value underlying this {@code Decimal}.
	 * 
	 * @return the scale metrics object
	 * @see ScaleMetrics#getScale()
	 * @see ScaleMetrics#getScaleFactor()
	 */
	S getScaleMetrics();

	/**
	 * Returns the scale associated with this decimal. The scale defines the
	 * number of fraction digits applied to the {@code long} value underlying
	 * this {@code Decimal}.
	 * <p>
	 * This is a shortcut for {@link ScaleMetrics#getScale()}.
	 * 
	 * @return the scale object
	 * @see #getScaleMetrics()
	 * @see ScaleMetrics#getScale()
	 */
	int getScale();

	/**
	 * Returns the unscaled value underlying this {@code Decimal}. This
	 * {@code Decimal} is <code>(unscaledValue &times; 10<sup>-n</sup>)</code>
	 * with {@code n} representing the {@link #getScale() scale}, hence the
	 * returned value equals <code>(this &times; 10<sup>n</sup>)</code>.
	 * 
	 * @return the unscaled numeric value, same as this decimal without applying
	 *         the scale factor
	 * @see #getScale()
	 * @see ScaleMetrics#getScaleFactor()
	 */
	long unscaledValue();

	DecimalFactory<S> getFactory();

	/**
	 * Returns a {@code Decimal} whose value represents the integral part of
	 * {@code (this)} value. The integral part corresponds to digits at the left
	 * of decimal point. The result is {@code this} decimal rounded at scale
	 * zero with {@link RoundingMode#DOWN}.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @return <code>&lfloor;this&rfloor;</code> for non-negative and
	 *         &lceil;this&rceil;</code> for negative values
	 * @see #fractionalPart()
	 * @see #isIntegral()
	 * @see #isIntegralPartZero()
	 */
	Decimal<S> integralPart();

	/**
	 * Returns a {@code Decimal} whose value represents the fractional part of
	 * {@code (this)} value. The fractional part corresponds to digits at the
	 * right of the decimal point. The result is {@code this} minus the integral
	 * part of this decimal.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @return {@code this-integralPart()}
	 * @see #integralPart()
	 * @see #isIntegral()
	 * @see #isIntegralPartZero()
	 */
	Decimal<S> fractionalPart();

	//some methods "inherited" from Number and BigDecimal

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
	 * {@code Decimal} will be discarded, and if the resulting "
	 * {@code BigInteger}" is too big to fit in a {@code long}, only the
	 * low-order 64 bits are returned. Note that this conversion can lose
	 * information about the overall magnitude and precision of this
	 * {@code Decimal} value as well as return a result with the opposite sign.
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
	 *             if {@code this} has a nonzero fractional part, or will not
	 *             fit in a {@code long}.
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
	 * {@link #getScale() scale} as this decimal value.
	 *
	 * @return this {@code Decimal} converted to a {@code BigDecimal} with the
	 *         same scale as this decimal value.
	 * @throws ArithmeticException
	 *             if {@code this} has a nonzero fractional part.
	 */
	BigDecimal toBigDecimal();

	//some conversion methods with rounding mode

	long longValue(RoundingMode roundingMode);

	long longValue(TruncationPolicy truncationPolicy);

	float floatValue(RoundingMode roundingMode);

	double doubleValue(RoundingMode roundingMode);

	BigInteger toBigInteger(RoundingMode roundingMode);

	BigDecimal toBigDecimal(int scale, RoundingMode roundingMode);

	//methods to round and change the scale
	/**
	 * Returns a {@code Decimal} value rounded to the specified
	 * {@code precision} using {@link RoundingMode#HALF_UP HALF_UP} rounding.
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
	 * <dd>values is left unchanged</dd>
	 * <dt>precision < scale - 18</dt>
	 * <dd>{@code IllegalArgumentException} is thrown</dd>
	 * </dl>
	 * 
	 * @param precision
	 *            the precision to use for the rounding, for instance 2 to round
	 *            to the second digit after the decimal point; must be at least
	 *            {@code (scale - 18)}
	 * @return a decimal instance rounded to the given precision
	 * @throws IllegalArgumentException
	 *             if {@code precision < scale - 18}
	 */
	Decimal<S> round(int precision);

	/**
	 * Returns a {@code Decimal} value rounded to the specified
	 * {@code precision} using the given rounding mode.
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
	 * <dd>values is left unchanged</dd>
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
	 * @return a decimal instance rounded to the given precision
	 * @throws IllegalArgumentException
	 *             if {@code precision < scale - 18}
	 * @throws ArithmeticException
	 *             if {@code roundingMode=UNNECESSARY} and rounding is necessary
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
	 * <dd>values is left unchanged</dd>
	 * <dt>precision < scale - 18</dt>
	 * <dd>{@code IllegalArgumentException} is thrown</dd>
	 * </dl>
	 * 
	 * @param precision
	 *            the precision to use for the rounding, for instance 2 to round
	 *            to the second digit after the decimal point; must be at least
	 *            {@code (scale - 18)}
	 * @param truncationPolicy
	 *            the truncation policy to apply when rounding to the desired
	 *            precision
	 * @return a decimal instance rounded to the given precision
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
	 * 
	 * @param scale
	 *            the scale to use for the result, must be in {@code [0,18]}
	 * @return a decimal instance with the given new scale
	 * @throws IllegalArgumentException
	 *             if {@code scale < 0} or {@code scale > 18}
	 */
	Decimal<?> scale(int scale);

	/**
	 * Returns a {@code Decimal} value whose {@link #getScaleMetrics() scale} is
	 * changed to the give value. {@link RoundingMode#HALF_UP HALF_UP} rounding
	 * is used if the scale change involves rounding.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics to use for the result
	 * @return a decimal instance with the given new scale metrics
	 */
	@SuppressWarnings("hiding")
	<S extends ScaleMetrics> Decimal<S> scale(S scaleMetrics);

	/**
	 * Returns a {@code Decimal} value whose {@link #getScaleMetrics() scale} is
	 * changed to the give value. The specified {@code roundingMode} is used if
	 * the scale change involves rounding.
	 * 
	 * @param scale
	 *            the scale to use for the result, must be in {@code [0,18]}
	 * @param roundingMode
	 *            the rounding mode to apply if the scale change involves
	 *            rounding
	 * @return a decimal instance with the given new scale
	 * @throws IllegalArgumentException
	 *             if {@code scale < 0} or {@code scale > 18}
	 * @throws ArithmeticException
	 *             if {@code roundingMode=UNNECESSARY} and rounding is necessary
	 */
	Decimal<?> scale(int scale, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} value whose {@link #getScaleMetrics() scale} is
	 * changed to the give value. The specified {@code roundingMode} is used if
	 * the scale change involves rounding.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics to use for the result
	 * @param roundingMode
	 *            the rounding mode to apply if the scale change involves
	 *            rounding
	 * @return a decimal instance with the given new scale metrics
	 * @throws ArithmeticException
	 *             if {@code roundingMode=UNNECESSARY} and rounding is necessary
	 */
	@SuppressWarnings("hiding")
	<S extends ScaleMetrics> Decimal<S> scale(S scaleMetrics, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} value whose {@link #getScaleMetrics() scale} is
	 * changed to the give value. The specified {@code truncationPolicy} is used
	 * if the scale change involves rounding or overflow.
	 * 
	 * @param scale
	 *            the scale to use for the result, must be in {@code [0,18]}
	 * @param truncationPolicy
	 *            the truncation policy to apply if the scale change involves
	 *            rounding or overflow
	 * @return a decimal instance with the given new scale
	 * @throws IllegalArgumentException
	 *             if {@code scale < 0} or {@code scale > 18}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} specifies
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<?> scale(int scale, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} value whose {@link #getScaleMetrics() scale} is
	 * changed to the give value. The specified {@code truncationPolicy} is used
	 * if the scale change involves rounding or overflow.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics to use for the result
	 * @param truncationPolicy
	 *            the truncation policy to apply if the scale change involves
	 *            rounding or overflow
	 * @return a decimal instance with the given new scale metrics
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} specifies
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	@SuppressWarnings("hiding")
	<S extends ScaleMetrics> Decimal<S> scale(S scaleMetrics, TruncationPolicy truncationPolicy);

	//add

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)}.
	 * <p>
	 * The returned value is a new instance if this decimal is an
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
	 * Returns a {@code Decimal} whose value is {@code (this + augend)}.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * <p>
	 * The augend argument is converted into a decimal number of the same scale
	 * as {@code this} decimal before performing the operation. If the
	 * conversion involves rounding, the specified {@code roundingMode} is used.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the augend argument needs to be
	 *            truncated when converted into a decimal number of the same
	 *            scale as {@code this} decimal
	 * @return {@code this + augend}
	 * @throws ArithmeticException
	 *             if {@code roundingMode=UNNECESSARY} and rounding is necessary
	 */
	Decimal<S> add(Decimal<?> augend, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)}.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * <p>
	 * The augend argument is converted into a decimal number of the same scale
	 * as {@code this} decimal before performing the operation. If the
	 * conversion involves truncation, the specified {@code truncationPolicy} is
	 * used.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param truncationPolicy
	 *            the truncation policy to apply if the augend argument needs to
	 *            be truncated when converted into a decimal number of the same
	 *            scale as {@code this} decimal
	 * @return {@code this + augend}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} specifies
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> add(Decimal<?> augend, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)}.
	 * <p>
	 * The returned value is a new instance if this decimal is an
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
	 * Returns a {@code Decimal} whose value is {@code (this + augend)}.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param overflowMode
	 *            the mode to apply if the operation leads to an overflow
	 * @return {@code this + augend}
	 * @throws ArithmeticException
	 *             if {@code overflowMode==CHECKED} and an overflow occurs
	 */
	Decimal<S> add(long augend, OverflowMode overflowMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)}.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * <p>
	 * The augend argument is converted into a decimal number of the same scale
	 * as {@code this} decimal before performing the operation. If the
	 * conversion involves rounding, {@link RoundingMode#HALF_UP HALF_UP}
	 * rounding is used.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this + augend}
	 */
	Decimal<S> add(double augend);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)}.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * <p>
	 * The augend argument is converted into a decimal number of the same scale
	 * as {@code this} decimal before performing the operation. If the
	 * conversion involves rounding, the specified {@code roundingMode} is used.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the augend argument needs to be
	 *            truncated when converted into a decimal number of the same
	 *            scale as {@code this} decimal
	 * @return {@code this + augend}
	 * @throws ArithmeticException
	 *             if {@code roundingMode=UNNECESSARY} and rounding is necessary
	 */
	Decimal<S> add(double augend, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)}.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * <p>
	 * The augend argument is converted into a decimal number of the same scale
	 * as {@code this} decimal before performing the operation. If the
	 * conversion involves rounding, the specified {@code truncationPolicy} is
	 * used.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param truncationPolicy
	 *            the truncation policy to apply if the augend argument needs to
	 *            be truncated when converted into a decimal number of the same
	 *            scale as {@code this} decimal
	 * @return {@code this + augend}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} specifies
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> add(double augend, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <code>(this + unscaledAugend &times; 10<sup>-scale</sup>)</code>.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * <p>
	 * The augend argument is converted into a decimal number of the same scale
	 * as {@code this} decimal before performing the operation. If the
	 * conversion involves rounding, {@link RoundingMode#HALF_UP HALF_UP}
	 * rounding is used.
	 * 
	 * @param unscaledAugend
	 *            value to be added to this {@code Decimal}
	 * @param scale
	 *            the scale to apply to {@code unscaledAugend}, must be in
	 *            {@code [0,18]}
	 * @return <code>this + unscaledAugend &times; 10<sup>-scale</sup></code>
	 * @throws IllegalArgumentException
	 *             if {@code scale < 0} or {@code scale > 18}
	 */
	Decimal<S> addUnscaled(long unscaledAugend, int scale);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <code>(this + unscaledAugend &times; 10<sup>-scale</sup>)</code>.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * <p>
	 * The augend argument is converted into a decimal number of the same scale
	 * as {@code this} decimal before performing the operation. If the
	 * conversion involves rounding, the specified {@code roundingMode} is
	 * applied.
	 * 
	 * @param unscaledAugend
	 *            value to be added to this {@code Decimal}
	 * @param scale
	 *            the scale to apply to {@code unscaledAugend}, must be in
	 *            {@code [0,18]}
	 * @param roundingMode
	 *            the rounding mode to apply if the augend argument needs to be
	 *            truncated when converted into a decimal number of the same
	 *            scale as {@code this} decimal
	 * @return <code>this + unscaledAugend &times; 10<sup>-scale</sup></code>
	 * @throws IllegalArgumentException
	 *             if {@code scale < 0} or {@code scale > 18}
	 * @throws ArithmeticException
	 *             if {@code roundingMode=UNNECESSARY} and rounding is necessary
	 */
	Decimal<S> addUnscaled(long unscaledAugend, int scale, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <code>(this + unscaledAugend &times; 10<sup>-scale</sup>)</code>.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * <p>
	 * The augend argument is converted into a decimal number of the same scale
	 * as {@code this} decimal before performing the operation. If the
	 * conversion involves truncation, the specified {@code truncationPolicy} is
	 * applied.
	 * 
	 * @param unscaledAugend
	 *            value to be added to this {@code Decimal}
	 * @param scale
	 *            the scale to apply to {@code unscaledAugend}, must be in
	 *            {@code [0,18]}
	 * @param truncationPolicy
	 *            the truncation policy to apply if the augend argument needs to
	 *            be truncated when converted into a decimal number of the same
	 *            scale as {@code this} decimal
	 * @return <code>this + unscaledAugend &times; 10<sup>-scale</sup></code>
	 * @throws IllegalArgumentException
	 *             if {@code scale < 0} or {@code scale > 18}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} specifies
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> addUnscaled(long unscaledAugend, int scale, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <code>(this + unscaledAugend &times; 10<sup>-scale</sup>)</code> with the
	 * {@link #getScale() scale} of this decimal.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param unscaledAugend
	 *            value to be added to this {@code Decimal}
	 * @return <code>this + unscaledAugend &times; 10<sup>-scale</sup></code>
	 */
	Decimal<S> addUnscaled(long unscaledAugend);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <code>(this + unscaledAugend &times; 10<sup>-scale</sup>)</code> with the
	 * {@link #getScale() scale} of this decimal.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param unscaledAugend
	 *            value to be added to this {@code Decimal}
	 * @param overflowMode
	 *            the mode to apply if the operation leads to an overflow
	 * @return <code>this + unscaledAugend &times; 10<sup>-scale</sup></code>
	 * @throws ArithmeticException
	 *             if {@code overflowMode==CHECKED} and an overflow occurs
	 */
	Decimal<S> addUnscaled(long unscaledAugend, OverflowMode overflowMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + value^2)}.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param value
	 *            value to be added to this {@code Decimal}
	 * @return {@code this + value*value}
	 */
	Decimal<S> addSquared(Decimal<S> value);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + value^2)}
	 * applying the specified rounding mode.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param value
	 *            value to be added to this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply for this operation
	 * @return {@code this + value*value}
	 * @throws ArithmeticException
	 *             if {@code roundingMode=UNNECESSARY} and rounding is necessary
	 */
	Decimal<S> addSquared(Decimal<S> value, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + value^2)}
	 * applying the specified rounding mode.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param value
	 *            value to be added to this {@code Decimal}
	 * @param truncationPolicy
	 *            the truncation policy to apply for this operation
	 * @return {@code this + value*value}
	 * @throws ArithmeticException
	 *             if {@code truncationPolicy} specifies
	 *             {@link RoundingMode#UNNECESSARY} and rounding is necessary or
	 *             if an overflow occurs and the policy declares
	 *             {@link OverflowMode#CHECKED}
	 */
	Decimal<S> addSquared(Decimal<S> value, TruncationPolicy truncationPolicy);

	//subtract

	Decimal<S> subtract(Decimal<S> subtrahend);

	Decimal<S> subtract(Decimal<?> subtrahend, RoundingMode roundingMode);

	Decimal<S> subtract(Decimal<?> subtrahend, TruncationPolicy truncationPolicy);

	Decimal<S> subtract(long subtrahend);

	Decimal<S> subtract(long subtrahend, OverflowMode overflowMode);

	Decimal<S> subtract(double subtrahend);

	Decimal<S> subtract(double subtrahend, RoundingMode roundingMode);

	Decimal<S> subtract(double subtrahend, TruncationPolicy truncationPolicy);

	Decimal<S> subtractUnscaled(long unscaledSubtrahend, int scale);

	Decimal<S> subtractUnscaled(long unscaledSubtrahend, int scale, RoundingMode roundingMode);

	Decimal<S> subtractUnscaled(long unscaledSubtrahend, int scale, TruncationPolicy truncationPolicy);

	Decimal<S> subtractUnscaled(long unscaledSubtrahend);

	Decimal<S> subtractUnscaled(long unscaledSubtrahend, OverflowMode overflowMode);

	Decimal<S> subtractSquared(Decimal<S> value);

	Decimal<S> subtractSquared(Decimal<S> value, RoundingMode roundingMode);

	Decimal<S> subtractSquared(Decimal<S> value, TruncationPolicy truncationPolicy);

	//multiply

	Decimal<S> multiply(Decimal<S> multiplicand);

	Decimal<S> multiply(Decimal<S> multiplicand, RoundingMode roundingMode);

	Decimal<S> multiply(Decimal<S> multiplicand, TruncationPolicy truncationPolicy);

	Decimal<S> multiplyBy(Decimal<?> multiplicand);

	Decimal<S> multiplyBy(Decimal<?> multiplicand, RoundingMode roundingMode);

	Decimal<S> multiplyBy(Decimal<?> multiplicand, TruncationPolicy truncationPolicy);

	Decimal<?> multiplyExact(Decimal<?> multiplicand);

	Decimal<?> multiplyExact(Decimal<?> multiplicand, OverflowMode overflowMode);

	Decimal<S> multiply(long multiplicand);

	Decimal<S> multiply(long multiplicand, OverflowMode overflowMode);

	Decimal<S> multiply(double multiplicand);

	Decimal<S> multiply(double multiplicand, RoundingMode roundingMode);

	Decimal<S> multiply(double multiplicand, TruncationPolicy truncationPolicy);

	Decimal<S> multiplyUnscaled(long unscaledMultiplicand);

	Decimal<S> multiplyUnscaled(long unscaledMultiplicand, RoundingMode roundingMode);

	Decimal<S> multiplyUnscaled(long unscaledMultiplicand, TruncationPolicy truncationPolicy);

	Decimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale);

	Decimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale, RoundingMode roundingMode);

	Decimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale, TruncationPolicy truncationPolicy);

	Decimal<S> multiplyByPowerOfTen(int n);

	Decimal<S> multiplyByPowerOfTen(int n, RoundingMode roundingMode);

	Decimal<S> multiplyByPowerOfTen(int n, TruncationPolicy truncationPolicy);

	//divide

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

	Decimal<S> divide(double divisor, TruncationPolicy truncationPolicy);

	Decimal<S> divideUnscaled(long unscaledDivisor);

	Decimal<S> divideUnscaled(long unscaledDivisor, RoundingMode roundingMode);

	Decimal<S> divideUnscaled(long unscaledDivisor, TruncationPolicy truncationPolicy);

	Decimal<S> divideUnscaled(long unscaledDivisor, int scale);

	Decimal<S> divideUnscaled(long unscaledDivisor, int scale, RoundingMode roundingMode);

	Decimal<S> divideUnscaled(long unscaledDivisor, int scale, TruncationPolicy truncationPolicy);

	Decimal<S> divideByPowerOfTen(int n);

	Decimal<S> divideByPowerOfTen(int n, RoundingMode roundingMode);

	Decimal<S> divideByPowerOfTen(int n, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is the integer part of the quotient
	 * {@code (this / divisor)} rounded down.
	 *
	 * @param divisor
	 *            value by which this {@code Decimal} is to be divided.
	 * @return The integer part of {@code this / divisor}.
	 * @throws ArithmeticException
	 *             if {@code divisor==0}
	 */
	Decimal<S> divideToIntegralValue(Decimal<S> divisor);

	/**
	 * Returns a {@code Decimal} whose value is the integer part of the quotient
	 * {@code (this / divisor)} rounded down.
	 *
	 * @param divisor
	 *            value by which this {@code Decimal} is to be divided.
	 * @param overflowMode
	 *            the mode to apply if the operation leads to an overflow
	 * @return The integer part of {@code this / divisor}.
	 * @throws ArithmeticException
	 *             if {@code divisor==0} or if {@code overflowMode==CHECKED} and
	 *             an overflow occurs
	 */
	Decimal<S> divideToIntegralValue(Decimal<S> divisor, OverflowMode overflowMode);

	/**
	 * Returns a two-element {@code Decimal} array containing the result of
	 * {@code divideToIntegralValue} followed by the result of {@code remainder}
	 * on the two operands.
	 *
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
	 * Returns a {@code Decimal} whose value is {@code (this % divisor)}.
	 *
	 * <p>
	 * The remainder is given by
	 * {@code this.subtract(this.divideToIntegralValue(divisor).multiply(divisor))}
	 * . Note that this is not the modulo operation (the result can be
	 * negative).
	 *
	 * @param divisor
	 *            value by which this {@code Decimal} is to be divided.
	 * @return {@code this % divisor}.
	 * @throws ArithmeticException
	 *             if {@code divisor==0}
	 * @see #divideToIntegralValue(Decimal)
	 */
	Decimal<S> remainder(Decimal<S> divisor);

	//other arithmetic operations

	/**
	 * Returns a {@code Decimal} whose value is {@code (-this)}. Depending on
	 * the implementation, a new decimal instance may be created and returned
	 * for the result, or this decimal may be modified and returned.
	 * 
	 * @return {@code -this}
	 */
	Decimal<S> negate();

	/**
	 * Returns a {@code Decimal} whose value is {@code (-this)}. Depending on
	 * the implementation, a new decimal instance may be created and returned
	 * for the result, or this decimal may be modified and returned.
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
	 * {@code Decimal}. Depending on the implementation, a new decimal instance
	 * may be created and returned for the result, or this decimal may be
	 * modified and returned.
	 * 
	 * @return {@code abs(this)}
	 */
	Decimal<S> abs();

	/**
	 * Returns a {@code Decimal} whose value is the absolute value of this
	 * {@code Decimal}. Depending on the implementation, a new decimal instance
	 * may be created and returned for the result, or this decimal may be
	 * modified and returned.
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
	 * Returns a {@code Decimal} whose value is {@code (1/this)}. Depending on
	 * the implementation, a new decimal instance may be created and returned
	 * for the result, or this decimal may be modified and returned.
	 * 
	 * @return {@code 1/this}
	 */
	Decimal<S> invert();

	/**
	 * Returns a {@code Decimal} whose value is {@code (1/this)} applying the
	 * specified rounding mode. Depending on the implementation, a new decimal
	 * instance may be created and returned for the result, or this decimal may
	 * be modified and returned.
	 * 
	 * @param roundingMode
	 *            the rounding mode to apply for this operation
	 * @return {@code 1/this}
	 */
	Decimal<S> invert(RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (1/this)} applying the
	 * specified rounding mode. Depending on the implementation, a new decimal
	 * instance may be created and returned for the result, or this decimal may
	 * be modified and returned.
	 * 
	 * @param truncationPolicy
	 *            the truncation policy to apply for this operation
	 * @return {@code 1/this}
	 */
	Decimal<S> invert(TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this^2)}. Depending on
	 * the implementation, a new decimal instance may be created and returned
	 * for the result, or this decimal may be modified and returned.
	 * 
	 * @return {@code this*this}
	 */
	Decimal<S> square();

	/**
	 * Returns a {@code Decimal} whose value is {@code (this^2)} applying the
	 * specified rounding mode. Depending on the implementation, a new decimal
	 * instance may be created and returned for the result, or this decimal may
	 * be modified and returned.
	 * 
	 * @param roundingMode
	 *            the rounding mode to apply for this operation
	 * @return {@code this*this}
	 */
	Decimal<S> square(RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this^2)} applying the
	 * specified rounding mode. Depending on the implementation, a new decimal
	 * instance may be created and returned for the result, or this decimal may
	 * be modified and returned.
	 * 
	 * @param truncationPolicy
	 *            the truncation policy to apply for this operation
	 * @return {@code this*this}
	 */
	Decimal<S> square(TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is the square root of {@code this}
	 * decimal value. Depending on the implementation, a new decimal instance
	 * may be created and returned for the result, or this decimal may be
	 * modified and returned.
	 * 
	 * @return {@code sqrt(this)}
	 */
	Decimal<S> sqrt();

	/**
	 * Returns a {@code Decimal} whose value is the square root of {@code this}
	 * decimal value applying the specified rounding mode. Depending on the
	 * implementation, a new decimal instance may be created and returned for
	 * the result, or this decimal may be modified and returned.
	 * 
	 * @param roundingMode
	 *            the rounding mode to apply for this operation
	 * @return {@code sqrt(this)}
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
	 * Computes <code>floor(this * 2<sup>n</sup>)</code>.
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
	 * Computes <code>floor(this * 2<sup>n</sup>)</code>.
	 *
	 * @param n
	 *            shift distance, in bits.
	 * @param roundingMode
	 *            the rounding mode to use if truncation is involved for
	 *            negative {@code n}, i.e. for right shifts
	 * @return {@code this << n}
	 * @see #shiftRight
	 */
	Decimal<S> shiftLeft(int n, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this << n)}. The shift
	 * distance, {@code n}, may be negative, in which case this method performs
	 * a right shift.
	 * <p>
	 * Computes <code>floor(this * 2<sup>n</sup>)</code>.
	 *
	 * @param n
	 *            shift distance, in bits.
	 * @param truncationPolicy
	 *            the truncation policy to use if truncation is involved
	 * @return {@code this << n}
	 * @see #shiftRight
	 */
	Decimal<S> shiftLeft(int n, TruncationPolicy truncationPolicy);

	/**
	 * Returns a BigInteger whose value is {@code (this >> n)}. Sign extension
	 * is performed. The shift distance, {@code n}, may be negative, in which
	 * case this method performs a left shift.
	 * <p>
	 * Computes <code>floor(this / 2<sup>n</sup>)</code>.
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
	 * Computes <code>floor(this / 2<sup>n</sup>)</code>.
	 *
	 * @param n
	 *            shift distance, in bits.
	 * @param roundingMode
	 *            the rounding mode to use if truncation is involved
	 * @return {@code this >> n}
	 * @see #shiftLeft
	 */
	Decimal<S> shiftRight(int n, RoundingMode roundingMode);

	/**
	 * Returns a BigInteger whose value is {@code (this >> n)}. Sign extension
	 * is performed. The shift distance, {@code n}, may be negative, in which
	 * case this method performs a left shift.
	 * <p>
	 * Computes <code>floor(this / 2<sup>n</sup>)</code>.
	 *
	 * @param n
	 *            shift distance, in bits.
	 * @param truncationPolicy
	 *            the truncation policy to use if truncation is involved
	 * @return {@code this >> n}
	 * @see #shiftLeft
	 */
	Decimal<S> shiftRight(int n, TruncationPolicy truncationPolicy);

	/**
	 * Returns a {@code Decimal} whose value is <code>(this<sup>n</sup>)</code>.
	 * 
	 * @param n
	 *            power to raise this {@code Decimal} to.
	 * @return <code>this<sup>n</sup></code>
	 * @throws ArithmeticException
	 *             if {@code n} is negative and {@code this==0}
	 */
	Decimal<S> pow(int n);

	/**
	 * Returns a {@code Decimal} whose value is <code>(this<sup>n</sup>)</code>
	 * applying the specified rounding mode.
	 * 
	 * @param n
	 *            power to raise this {@code Decimal} to.
	 * @param roundingMode
	 *            the rounding mode to apply for this operation
	 * @return <code>this<sup>n</sup></code>
	 * @throws ArithmeticException
	 *             if {@code n} is negative and {@code this==0}
	 */
	Decimal<S> pow(int n, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is <code>(this<sup>n</sup>)</code>
	 * applying the specified rounding mode.
	 * 
	 * @param n
	 *            power to raise this {@code Decimal} to.
	 * @param truncationPolicy
	 *            the truncation policy to apply for this operation
	 * @return <code>this<sup>n</sup></code>
	 * @throws ArithmeticException
	 *             if {@code n} is negative and {@code this==0}
	 */
	Decimal<S> pow(int n, TruncationPolicy truncationPolicy);

	//compare and related methods

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
	 * returns true if this decimal is numerically greater than {@code other}.
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
	 * returns true if this decimal is numerically greater than or equal to
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
	 * returns true if this decimal is numerically less than {@code other}.
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
	 * returns true if this decimal is numerically less than or equal to
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
	 * number representable by a decimal with the current {@link #getScale()
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
	 * 2.00) are considered equal by this method.
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

	//finally some basic object methods plus equals

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
	 * if and only if the argument is not {@code null} and is a {@code Decimal}
	 * object that contains the same value as this object and if the two
	 * decimals have the same {@link #getScaleMetrics() scale}.
	 * 
	 * @param obj
	 *            the object to compare with.
	 * @return {@code true} if the argument is {@code Decimal} object that
	 *         contains the same value and arithmetics as this object;
	 *         {@code false} otherwise.
	 */
	@Override
	boolean equals(Object obj);

	/**
	 * Returns a string representation of this {@code Decimal} object as fixed
	 * precision decimal always showing all decimal places (also trailing zeros)
	 * and a leading sign character if negative.
	 * 
	 * @return a {@code String} decimal representation of this {@code Decimal}
	 *         object with all the fraction digits (including trailing zeros)
	 */
	@Override
	String toString();
}
