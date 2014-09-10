package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Mutable or immutable fixed-precision signed decimal numbers similar to
 * {@link BigDecimal}. A {@code Decimal} consists of an <i>unscaled long
 * value</i> and a {@link #getScaleMetrics() scale}. The scale defines the
 * number of digits to the right of the decimal point. The value of the number
 * represented by the {@code Decimal} is
 * <tt>(unscaledValue &times; 10<sup>-f</sup>)</tt> with scale {@code f}.
 * <p>
 * Certain operations can only be performed with other <tt>Decimal</tt> numbers
 * of the same scale. Scale compatibility of such operations is enforced through
 * the generic {@link ScaleMetrics} parameter of the <tt>Decimal</tt>. The
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
	 * {@code long} value underlying this <tt>Decimal</tt>.
	 * 
	 * @return the scale metrics object
	 * @see ScaleMetrics#getScale()
	 * @see ScaleMetrics#getScaleFactor()
	 */
	S getScaleMetrics();

	/**
	 * Returns the scale associated with this decimal. The scale defines the
	 * number of fraction digits applied to the {@code long} value underlying
	 * this <tt>Decimal</tt>.
	 * <p>
	 * This is a shortcut for {@link ScaleMetrics#getScale()}.
	 * 
	 * @return the scale object
	 * @see #getScaleMetrics()
	 * @see ScaleMetrics#getScale()
	 */
	int getScale();

	/**
	 * Returns the value of this <tt>Decimal</tt> as a <tt>byte</tt>. This may
	 * involve rounding or truncation.
	 * 
	 * @return the numeric value represented by this object after conversion to
	 *         type <tt>byte</tt>.
	 * @see Number#byteValue()
	 */
	byte byteValue();

	/**
	 * Returns the value of this <tt>Decimal</tt> as a <tt>short</tt>. This may
	 * involve rounding or truncation.
	 * 
	 * @return the numeric value represented by this object after conversion to
	 *         type <tt>short</tt>.
	 * @see Number#shortValue()
	 */
	short shortValue();

	/**
	 * Returns the value of this <tt>Decimal</tt> as an <tt>int</tt>. This may
	 * involve rounding or truncation.
	 * 
	 * @return the numeric value represented by this object after conversion to
	 *         type <tt>int</tt>.
	 * @see Number#intValue()
	 */
	int intValue();

	/**
	 * Returns the value of this <tt>Decimal</tt> as a <tt>long</tt>. This may
	 * involve rounding.
	 * 
	 * @return the numeric value represented by this object after conversion to
	 *         type <tt>long</tt>.
	 * @see Number#longValue()
	 */
	long longValue();

	/**
	 * Returns the value of this <tt>Decimal</tt> as a <tt>float</tt>. This may
	 * involve rounding.
	 * 
	 * @return the numeric value represented by this object after conversion to
	 *         type <tt>float</tt>.
	 * @see Number#floatValue()
	 */
	float floatValue();

	/**
	 * Returns the value of this <tt>Decimal</tt> as a <tt>double</tt>. This may
	 * involve rounding.
	 * 
	 * @return the numeric value represented by this object after conversion to
	 *         type <tt>double</tt>.
	 * @see Number#doubleValue()
	 */
	double doubleValue();

	/**
	 * Returns the unscaled value underlying this <tt>Decimal</tt>. Since the
	 * value of this {@code Decimal} is
	 * <tt>(unscaledValue &times; 10<sup>-n</sup>)</tt>, the returned value
	 * equals <tt>(this &times; 10<sup>n</sup>)</tt> with {@code n} standing for
	 * the number of {@link ScaleMetrics#getScale() fraction digits}.
	 * 
	 * @return the unscaled numeric value represented by this object when to
	 *         type <tt>long</tt>
	 * @see #getScaleMetrics()
	 * @see ScaleMetrics#getScale()
	 * @see ScaleMetrics#getScaleFactor()
	 */
	long unscaledValue();

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
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this + augend}
	 */
	Decimal<S> add(BigInteger augend);

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
	Decimal<S> add(BigDecimal augend);

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
	Decimal<S> add(BigDecimal augend, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this + unscaledAugend &times; 10<sup>-scale</sup>)</tt>.
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
	 * @return <tt>this + unscaledAugend &times; 10<sup>-scale</sup></tt>
	 * @throws IllegalArgumentException
	 *             if {@code scale < 0} or {@code scale > 18}
	 */
	Decimal<S> addUnscaled(long unscaledAugend, int scale);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this + unscaledAugend &times; 10<sup>-scale</sup>)</tt>.
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
	 * @return <tt>this + unscaledAugend &times; 10<sup>-scale</sup></tt>
	 * @throws IllegalArgumentException
	 *             if {@code scale < 0} or {@code scale > 18}
	 * @throws ArithmeticException
	 *             if {@code roundingMode=UNNECESSARY} and rounding is necessary
	 */
	Decimal<S> addUnscaled(long unscaledAugend, int scale, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this + unscaledAugend &times; 10<sup>-scale</sup>)</tt> with the
	 * {@link #getScale() scale} of this decimal.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing the outcome of the addition.
	 * 
	 * @param unscaledAugend
	 *            value to be added to this {@code Decimal}
	 * @return <tt>this + unscaledAugend &times; 10<sup>-scale</sup></tt>
	 */
	Decimal<S> addUnscaled(long unscaledAugend);

	Decimal<S> subtract(Decimal<S> subtrahend);

	Decimal<S> subtract(Decimal<?> subtrahend, RoundingMode roundingMode);

	Decimal<S> subtract(long subtrahend);

	Decimal<S> subtract(double subtrahend);

	Decimal<S> subtract(double subtrahend, RoundingMode roundingMode);

	Decimal<S> subtract(BigInteger subtrahend);

	Decimal<S> subtract(BigDecimal subtrahend);

	Decimal<S> subtract(BigDecimal subtrahend, RoundingMode roundingMode);

	Decimal<S> subtractUnscaled(long unscaledSubtrahend, int scale);

	Decimal<S> subtractUnscaled(long unscaledSubtrahend, int scale, RoundingMode roundingMode);

	Decimal<S> subtractUnscaled(long unscaledSubtrahend);

	Decimal<S> multiply(Decimal<S> multiplicand);

	Decimal<S> multiply(Decimal<S> multiplicand, RoundingMode roundingMode);

	Decimal<S> multiplyBy(Decimal<?> multiplicand);

	Decimal<S> multiplyBy(Decimal<?> multiplicand, RoundingMode roundingMode);

	Decimal<?> multiplyExact(Decimal<?> multiplicand);

	Decimal<S> multiply(long multiplicand);

	Decimal<S> multiply(double multiplicand);

	Decimal<S> multiply(double multiplicand, RoundingMode roundingMode);

	Decimal<S> multiply(BigInteger multiplicand);

	Decimal<S> multiply(BigDecimal multiplicand);

	Decimal<S> multiply(BigDecimal multiplicand, RoundingMode roundingMode);

	Decimal<S> multiplyUnscaled(long unscaledMultiplicand);

	Decimal<S> multiplyUnscaled(long unscaledMultiplicand, RoundingMode roundingMode);

	Decimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale);

	Decimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale, RoundingMode roundingMode);

	Decimal<S> multiplyByPowerOfTen(int n);
	
	Decimal<S> multiplyByPowerOfTen(int n, RoundingMode roundingMode);

	Decimal<S> divide(Decimal<S> divisor);

	Decimal<S> divide(Decimal<S> divisor, RoundingMode roundingMode);

	Decimal<S> divideBy(Decimal<?> divisor);

	Decimal<S> divideBy(Decimal<?> divisor, RoundingMode roundingMode);

	Decimal<S> divideTruncate(Decimal<S> divisor);

	Decimal<S> divideExact(Decimal<S> divisor);

	Decimal<S> divide(long divisor);

	Decimal<S> divide(long divisor, RoundingMode roundingMode);

	Decimal<S> divide(double divisor);

	Decimal<S> divide(double divisor, RoundingMode roundingMode);

	Decimal<S> divide(BigInteger divisor);

	Decimal<S> divide(BigInteger divisor, RoundingMode roundingMode);

	Decimal<S> divide(BigDecimal divisor);

	Decimal<S> divide(BigDecimal divisor, RoundingMode roundingMode);

	Decimal<S> divideUnscaled(long unscaledDivisor);

	Decimal<S> divideUnscaled(long unscaledDivisor, RoundingMode roundingMode);

	Decimal<S> divideUnscaled(long unscaledDivisor, int scale);

	Decimal<S> divideUnscaled(long unscaledDivisor, int scale, RoundingMode roundingMode);
	
	Decimal<S> divideByPowerOfTen(int n);

	Decimal<S> divideByPowerOfTen(int n, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (-this)}. Depending on
	 * the implementation, a new decimal instance may be created and returned
	 * for the result, or this decimal may be modified and returned.
	 * 
	 * @return {@code -this}
	 */
	Decimal<S> negate();

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
	 * Returns the signum function of this {@code Decimal}.
	 * 
	 * @return -1, 0, or 1 as the value of this {@code Decimal} is negative,
	 *         zero, or positive.
	 */
	int signum();

	Decimal<S> shiftLeft(int n);
	
	Decimal<S> shiftLeft(int n, RoundingMode roundingMode);

	Decimal<S> shiftRight(int n);

	Decimal<S> shiftRight(int n, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is <tt>(this<sup>n</sup>)</tt>.
	 * 
	 * @param n
	 *            power to raise this {@code Decimal} to.
	 * @return <tt>this<sup>n</sup></tt>
	 * @throws ArithmeticException
	 *             if {@code n} is negative and {@code this==0}
	 */
	Decimal<S> pow(int n);

	/**
	 * Returns a {@code Decimal} whose value is <tt>(this<sup>n</sup>)</tt>
	 * applying the specified rounding mode.
	 * 
	 * @param n
	 *            power to raise this {@code Decimal} to.
	 * @param roundingMode
	 *            the rounding mode to apply for this operation
	 * @return <tt>this<sup>n</sup></tt>
	 * @throws ArithmeticException
	 *             if {@code n} is negative and {@code this==0}
	 */
	Decimal<S> pow(int n, RoundingMode roundingMode);

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
	 * Compares two {@code Decimal} objects numerically.
	 * 
	 * @param anotherDecimal
	 *            the {@code Decimal} to be compared.
	 * @return the value {@code 0} if this {@code Decimal} is equal to the
	 *         argument {@code Decimal}; a value less than {@code 0} if this
	 *         {@code Decimal} is numerically less than the argument
	 *         {@code Decimal}; and a value greater than {@code 0} if this
	 *         {@code Decimal} is numerically greater than the argument
	 *         {@code Decimal} (signed comparison).
	 */
	@Override
	int compareTo(Decimal<S> anotherDecimal);

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
