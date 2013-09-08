package ch.javasoft.decimal;

import java.math.RoundingMode;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

public interface Decimal<S extends Scale> extends
		Comparable<Decimal<S>> {

	DecimalArithmetics getArithmetics();
	
	S getScale();

	byte byteValue();

	short shortValue();

	int intValue();

	long longValue();

	float floatValue();

	double doubleValue();

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
	 * Returns the size of an ulp, a unit in the last place, of this
	 * {@code Decimal}. An ulp of a nonzero {@code Decimal} value is the
	 * positive distance between this value and the {@code Decimal} value next
	 * larger in magnitude with the same number of digits. An ulp of a zero
	 * value is numerically equal to 1 with the scale of {@code this}. The
	 * result is stored with the same scale as {@code this} so the result for
	 * zero and nonzero values is equal to {@code [1,
	 * getArithmethics().scale()]}.
	 * 
	 * @return the size of an ulp of {@code this}
	 */
	Decimal<S> ulp();

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
	 * object that contains the same value as this object with the same
	 * underlying arithmetics.
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

	@Override
	String toString();

}
