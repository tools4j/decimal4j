package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

/**
 * Mutable or immutable fixed-precision signed decimal numbers similar to
 * {@link BigDecimal}. A {@code Decimal} consists of an <i>unscaled long
 * value</i> and a {@link #getScaleMetrics() scale}. The scale defines the number of
 * digits to the right of the decimal point. The value of the number represented
 * by the {@code Decimal} is <tt>(unscaledValue &times; 10<sup>-f</sup>)</tt>
 * with scale {@code f}.
 * <p>
 * The generic {@link ScaleMetrics} parameter of the <tt>Decimal</tt> enforces
 * that only decimal numbers of the same scale can be combined directly in
 * arithmetic operations (without conversion); the {@link ScaleMetrics} class
 * defines all supported scale metrics subclasses and singleton constants.
 * <p>
 * The {@link #getArithmetics() arithmetics} object defines the
 * {@link RoundingMode} applied to methods that involve rounding. It also
 * determines an {@link OverflowMode} which defines the behavior in case of an
 * overflow. The arithmetics of {@code this} decimal always determines the
 * arithmetics of the result irrespective of rounding mode arguments and
 * potentially different arithmetics of other operands. Note that this may lead
 * to a violation of the commutative property inherent to certain mathematical
 * operations if operands are used with another arithmetic than that of
 * {@code this} decimal value. Values with a different arithmetics type can be
 * derived through one of the {@code convert(..)} methods.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 */
public interface Decimal<S extends ScaleMetrics> extends Comparable<Decimal<S>> {

	/**
	 * Returns the scale associated with this decimal. The scale defines the
	 * number of {@link ScaleMetrics#getScale() fraction digits} and the
	 * {@link ScaleMetrics#getScaleFactor() scale factor} applied to the
	 * {@code long} value underlying this <tt>Decimal</tt>.
	 * 
	 * @return the scale object
	 */
	S getScaleMetrics();

	/**
	 * Decimal arithmetics used for arithmetic operations and conversions using
	 * the same scale and defining the default
	 * {@link DecimalArithmetics#getRoundingMode() rounding mode} applied to
	 * operations where rounding is necessary.
	 * 
	 * @return the decimal arithmetics with the same scale as this decimal and
	 *         the default {@link DecimalArithmetics#getRoundingMode() rounding
	 *         mode} applied to operations with rounding
	 */
	DecimalArithmetics getArithmetics();

	/**
	 * Returns a {@code Decimal} whose value {@link #getArithmetics()
	 * arithmetics} is altered to use the specified rounding mode for future
	 * operations. The value itself does not change but the method usually
	 * returns a new decimal instance unless the specified rounding mode is the
	 * same as that of this decimal value.
	 * 
	 * @param roundingMode
	 *            the rounding mode to use for arithmetic operations performed
	 *            on the returned value
	 * @return a decimal instance with the same value as {@code this} decimal
	 *         using arithmetics with the given {@code roundingMode} for future
	 *         arithmetic operations performed on the value
	 */
	Decimal<S> convert(RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value {@link #getScaleMetrics() scale} is
	 * changed to the give value. If the scale change involves rounding, the
	 * standard rounding mode of this decimal's {@link #getArithmetics()
	 * arithmetics} is applied.
	 * 
	 * @param scale
	 *            the scale to use for the result
	 * @return a decimal instance with the given new scale
	 */
	Decimal<?> convert(int scale);

	/**
	 * Returns a {@code Decimal} whose value {@link #getScaleMetrics() scale} is
	 * changed to the give value. If the scale change involves rounding, the
	 * specified {@code roundingMode} is applied.
	 * 
	 * @param scale
	 *            the scale to use for the result
	 * @param roundingMode
	 *            the rounding mode to apply if the scale change involves
	 *            rounding
	 * @return a decimal instance with the given new scale
	 */
	Decimal<?> convert(int scale, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value {@link #getArithmetics()
	 * arithmetics} is altered to use the specified overflow mode for future
	 * operations. The value itself does not change but the method usually
	 * returns a new decimal instance unless the specified overflow mode is the
	 * same as that of this decimal value.
	 * 
	 * @param overflowMode
	 *            the overflow mode to use for arithmetic operations performed
	 *            on the returned value
	 * @return a decimal instance with the same value as {@code this} decimal
	 *         using arithmetics with the given {@code overflowMode} for future
	 *         arithmetic operations performed on the value
	 */
	Decimal<S> convert(OverflowMode overflowMode);

	/**
	 * Returns the value of this <tt>Decimal</tt> as a <code>byte</code>. This
	 * may involve rounding or truncation.
	 * 
	 * @return the numeric value represented by this object after conversion to
	 *         type <code>byte</code>.
	 * @see Number#byteValue()
	 */
	byte byteValue();

	/**
	 * Returns the value of this <tt>Decimal</tt> as a <code>short</code>. This
	 * may involve rounding or truncation.
	 * 
	 * @return the numeric value represented by this object after conversion to
	 *         type <code>short</code>.
	 * @see Number#shortValue()
	 */
	short shortValue();

	/**
	 * Returns the value of this <tt>Decimal</tt> as an <code>int</code>. This
	 * may involve rounding or truncation.
	 * 
	 * @return the numeric value represented by this object after conversion to
	 *         type <code>int</code>.
	 * @see Number#intValue()
	 */
	int intValue();

	/**
	 * Returns the value of this <tt>Decimal</tt> as a <code>long</code>. This
	 * may involve rounding.
	 * 
	 * @return the numeric value represented by this object after conversion to
	 *         type <code>long</code>.
	 * @see Number#longValue()
	 */
	long longValue();

	/**
	 * Returns the value of this <tt>Decimal</tt> as a <code>float</code>. This
	 * may involve rounding.
	 * 
	 * @return the numeric value represented by this object after conversion to
	 *         type <code>float</code>.
	 * @see Number#floatValue()
	 */
	float floatValue();

	/**
	 * Returns the value of this <tt>Decimal</tt> as a <code>double</code>. This
	 * may involve rounding.
	 * 
	 * @return the numeric value represented by this object after conversion to
	 *         type <code>double</code>.
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
	 *         type <code>long</code>
	 * @see #getScaleMetrics()
	 * @see ScaleMetrics#getScale()
	 * @see ScaleMetrics#getScaleFactor()
	 */
	long unscaledValue();

	/**
	 * Returns a {@code Decimal} whose value is {@code (this + augend)}.
	 * Depending on the implementation, a new decimal instance may be created
	 * and returned for the result, or this decimal may be modified and
	 * returned.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}.
	 * @return {@code this + augend}
	 */
	Decimal<S> add(Decimal<S> augend);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this - subtrahend)}.
	 * Depending on the implementation, a new decimal instance may be created
	 * and returned for the result, or this decimal may be modified and
	 * returned.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}.
	 * @return {@code this - subtrahend}
	 */
	Decimal<S> subtract(Decimal<S> subtrahend);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this &times; multiplicand)</tt>. Depending on the implementation, a
	 * new decimal instance may be created and returned for the result, or this
	 * decimal may be modified and returned.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}.
	 * @return {@code this * multiple}
	 */
	Decimal<S> multiply(Decimal<S> multiplicand);

	/**
	 * Returns a {@code Decimal} whose value is
	 * <tt>(this &times; multiplicand)</tt> applying the specified rounding
	 * mode. Depending on the implementation, a new decimal instance may be
	 * created and returned for the result, or this decimal may be modified and
	 * returned.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}.
	 * @param roundingMode
	 *            the rounding mode to apply for this operation
	 * @return {@code this * multiple}
	 */
	Decimal<S> multiply(Decimal<S> multiplicand, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this / divisor)}.
	 * Depending on the implementation, a new decimal instance may be created
	 * and returned for the result, or this decimal may be modified and
	 * returned.
	 * 
	 * @param divisor
	 *            value by which this {@code Decimal} is to be divided.
	 * @return {@code this / divisor}
	 */
	Decimal<S> divide(Decimal<S> divisor);

	/**
	 * Returns a {@code Decimal} whose value is {@code (this / divisor)}
	 * applying the specified rounding mode. Depending on the implementation, a
	 * new decimal instance may be created and returned for the result, or this
	 * decimal may be modified and returned.
	 * 
	 * @param divisor
	 *            value by which this {@code Decimal} is to be divided.
	 * @param roundingMode
	 *            the rounding mode to apply for this operation
	 * @return {@code this / divisor}
	 */
	Decimal<S> divide(Decimal<S> divisor, RoundingMode roundingMode);

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

	/**
	 * Returns a {@code Decimal} which is equivalent to this one with the
	 * decimal point moved {@code n} places to the left. If {@code n} is
	 * negative, the call is equivalent to {@code movePointRight(-n)}. The
	 * {@code Decimal} returned by this call has value <tt>(this &times;
	 * 10<sup>-n</sup>)</tt>.
	 * 
	 * @param n
	 *            number of places to move the decimal point to the left.
	 * @return a {@code Decimal} which is equivalent to this one with the
	 *         decimal point moved {@code n} places to the left.
	 * @throws ArithmeticException
	 *             if scale overflows.
	 */
	Decimal<S> movePointLeft(int n);

	/**
	 * Returns a {@code Decimal} which is equivalent to this one with the
	 * decimal point moved {@code n} places to the left. If {@code n} is
	 * negative, the call is equivalent to {@code movePointRight(-n)}. The
	 * {@code Decimal} returned by this call has value <tt>(this &times;
	 * 10<sup>-n</sup>)</tt>. The specified rounding mode is applied if rounding
	 * is necessary.
	 * 
	 * @param n
	 *            number of places to move the decimal point to the left.
	 * @param roundingMode
	 *            the rounding mode to apply for this operation
	 * @return a {@code Decimal} which is equivalent to this one with the
	 *         decimal point moved {@code n} places to the left.
	 * @throws ArithmeticException
	 *             if scale overflows.
	 */
	Decimal<S> movePointLeft(int n, RoundingMode roundingMode);

	/**
	 * Returns a {@code Decimal} which is equivalent to this one with the
	 * decimal point moved {@code n} places to the right. If {@code n} is
	 * negative, the call is equivalent to {@code movePointLeft(-n)}. The
	 * {@code Decimal} returned by this call has value <tt>(this
	 * &times; 10<sup>n</sup>)</tt>.
	 * 
	 * @param n
	 *            number of places to move the decimal point to the right.
	 * @return a {@code Decimal} which is equivalent to this one with the
	 *         decimal point moved {@code n} places to the right.
	 * @throws ArithmeticException
	 *             if scale overflows.
	 */
	Decimal<S> movePointRight(int n);

	/**
	 * Returns a {@code Decimal} which is equivalent to this one with the
	 * decimal point moved {@code n} places to the right. If {@code n} is
	 * negative, the call is equivalent to {@code movePointLeft(-n)}. The
	 * {@code Decimal} returned by this call has value <tt>(this
	 * &times; 10<sup>n</sup>)</tt>. The specified rounding mode is applied if
	 * rounding is necessary.
	 * 
	 * @param n
	 *            number of places to move the decimal point to the right.
	 * @param roundingMode
	 *            the rounding mode to apply for this operation
	 * @return a {@code Decimal} which is equivalent to this one with the
	 *         decimal point moved {@code n} places to the right.
	 * @throws ArithmeticException
	 *             if scale overflows.
	 */
	Decimal<S> movePointRight(int n, RoundingMode roundingMode);

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
