package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.Scale;

public interface DecimalArithmetics {
	/**
	 * Returns the <i>scale</i> applied to all unscaled decimal values passed to
	 * and returned by this {@code DecimalArithmetics}. The scale is the number
	 * of digits to the right of the decimal point (cannot be negative).
	 * 
	 * @return the non-negative scale applied to unscaled decimal values within
	 *         this {@code DecimalArithmetics} object
	 * @see Scale#getFractionDigits()
	 */
	int getScale();

	/**
	 * Returns the <i>rounding mode</i> applied to operations of this
	 * {@code DecimalArithmetics} object if rounding is necessary.
	 * 
	 * @return the rounding mode applied to operations of this
	 *         {@code DecimalArithmetics} object if rounding is necessary
	 */
	RoundingMode getRoundingMode();

	/**
	 * Returns the <i>overflow mode</i> applied to operations of this
	 * {@code DecimalArithmetics} object if an overflow occurs. The overflow
	 * mode defines whether an operation should throw an exception if an
	 * overflow occurs.
	 * 
	 * @return the overflow mode applied to operations of this
	 *         {@code DecimalArithmetics} object if an overflow occurs
	 */
	OverflowMode getOverflowMode();

	/**
	 * Returns a new decimal arithmetics object with the same
	 * {@link #getRoundingMode() rounding mode} and {@link #getOverflowMode()
	 * overflow mode} but for the new {@code scale} specified here.
	 * 
	 * @param scale
	 *            the scale for the new decimal arithmetics, a non-negative
	 *            integer denoting the number of digits to the right of the
	 *            decimal point
	 * @return a new decimal arithmetics object for the given scale
	 * @throws IllegalArgumentException
	 *             if scale is negative
	 */
	DecimalArithmetics derive(int scale);

	/**
	 * Returns a new decimal arithmetics object with the same
	 * {@link #getScale() scale} and {@link #getOverflowMode() overflow mode}
	 * but for the new rounding mode specified here.
	 * 
	 * @param roundingMode
	 *            the rounding mode for the new decimal arithmetics
	 * @return a new decimal arithmetics object for the given rounding mode
	 */
	DecimalArithmetics derive(RoundingMode roundingMode);

	/**
	 * Returns a new decimal arithmetics object with the same
	 * {@link #getScale() scale} and {@link #getRoundingMode() rounding mode}
	 * but for the new overflow mode specified here.
	 * 
	 * @param overflowMode
	 *            the overflow mode for the new decimal arithmetics
	 * @return a new decimal arithmetics object for the given overflow mode
	 */
	DecimalArithmetics derive(OverflowMode overflowMode);

	/**
	 * Returns the unscaled decimal for the decimal value {@code 1}. One is the
	 * value <code>10<sup>scale</sup></code> which is also the multiplier used
	 * to get the unscaled decimal from the true decimal value.
	 * 
	 * @return the unscaled decimal representing the decimal value 1
	 */
	long one();

	/**
	 * Returns the signum function of the specified unscaled decimal.
	 * 
	 * @return -1, 0, or 1 as the value of the specified unscaled decimal is
	 *         negative, zero, or positive.
	 */
	int signum(long uDecimal);

	/**
	 * Compares two unscaled decimal values numerically.
	 * 
	 * @param uDecimal1
	 *            the first unscaled decimal to compare
	 * @param uDecimal2
	 *            the second unscaled decimal to compare
	 * @return the value {@code 0} if {@code unscaled1 == unscaled2}; a value
	 *         less than {@code 0} if {@code unscaled1 < unscaled2}; and a value
	 *         greater than {@code 0} if {@code unscaled1 > unscaled2}
	 */
	int compare(long uDecimal1, long uDecimal2);

	/**
	 * Returns an unscaled decimal whose value is
	 * {@code (uDecimal1 + uDecimal2)}.
	 * 
	 * @param uDecimal1
	 *            first unscaled decimal value to be added
	 * @param uDecimal2
	 *            second unscaled decimal value to be added
	 * @return {@code uDecimal1 + uDecimal2}
	 */
	long add(long uDecimal1, long uDecimal2);

	/**
	 * Returns an unscaled decimal whose value is {@code (uDecimalMinuend +
	 * uDecimalSubtrahend)}.
	 * 
	 * @param uDecimalMinuend
	 *            unscaled decimal value to be subtracted from
	 * @param uDecimalSubtrahend
	 *            unscaled decimal value to subtract from the minuend
	 * @return {@code uDecimalMinuend + uDecimalSubtrahend}
	 */
	long subtract(long uDecimalMinuend, long uDecimalSubtrahend);

	/**
	 * Returns an unscaled decimal whose value is <tt>(uDecimal1 &times;
	 * uDecimal2)}.
	 * 
	 * @param uDecimal1
	 *            first unscaled decimal value to be multiplied
	 * @param uDecimal2
	 *            second unscaled decimal value to be multiplied
	 * @return {@code uDecimal1 * uDecimal2}
	 */
	long multiply(long uDecimal1, long uDecimal2);

	/**
	 * Returns an unscaled decimal whose value is {@code (dividend / divisor)}.
	 * If rounding must be performed, this arithmetic's
	 * {@link #getRoundingMode() rounding mode} is applied.
	 * 
	 * @param uDecimalDividend
	 *            value to be divided.
	 * @param uDecimalDivisor
	 *            value by which the dividend is to be divided.
	 * @return {@code dividend / divisor} as unscaled decimal
	 * @throws ArithmeticException
	 *             if {@code divisor} is zero or if an overflow occurs and the
	 *             {@link #getOverflowMode() overflow mode} is set to throw an
	 *             exception
	 */
	long divide(long uDecimalDividend, long uDecimalDivisor);

	/**
	 * Returns the non-negative value {@code abs(uDecimal)}, which is the value
	 * itself if {@code uDecimal>=0} and {@code -uDecimal} if the given value is
	 * negative.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value
	 * @return {@code uDecimal} if {@code uDecimal>=0} and {@code -uDecimal}
	 *         otherwise
	 */
	long abs(long uDecimal);

	/**
	 * Returns the negated value {@code -uDecimal} again as unscaled decimal
	 * value.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to negate
	 * @return {@code -uDecimal} as unscaled decimal value
	 */
	long negate(long uDecimal);

	/**
	 * Returns the inverted value {@code 1/uDecimal} again as unscaled decimal
	 * value.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to invert
	 * @return {@code 1/uDecimal} as unscaled decimal value
	 * @throws ArithmeticException
	 *             if {@code uDecmial} is zero
	 */
	long invert(long uDecimal);

	/**
	 * Returns an unscaled decimal whose value is
	 * <tt>(uDecimalBase<sup>exponent</sup>)</tt>. Note that {@code exponent} is
	 * an integer rather than a decimal.
	 * 
	 * @param uDecimalBase
	 *            the unscaled decimal base value
	 * @param exponent
	 *            exponent to which {@code uDecimalBase} is to be raised.
	 * @return <tt>uDecimalBase<sup>exponent</sup></tt>
	 * @throws ArithmeticException
	 *             {@code uDecimalBase==0} and {@code exponent} is negative.
	 *             (This would cause a division by zero.)
	 */
	long pow(long uDecimalBase, int exponent);

	/**
	 * Returns an unscaled decimal whose value is {@code (uDecimal << n)}. The
	 * shift distance, {@code n}, may be negative, in which case this method
	 * performs a right shift. (Computes
	 * <tt>floor(uDecimal * 2<sup>n</sup>)</tt>.)
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to shift
	 * @param n
	 *            shift distance, in bits.
	 * @return {@code uDecimal << n}
	 * @see #shiftRight
	 */
	long shiftLeft(long uDecimal, int positions);

	/**
	 * Returns an unscaled decimal whose value is {@code (uDecimal >> n)}. Sign
	 * extension is performed. The shift distance, {@code n}, may be negative,
	 * in which case this method performs a left shift. (Computes
	 * <tt>floor(this / 2<sup>n</sup>)</tt>.)
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to shift
	 * @param n
	 *            shift distance, in bits.
	 * @return {@code this >> n}
	 * @throws ArithmeticException
	 *             if the shift distance is {@code Integer.MIN_VALUE}.
	 * @see #shiftLeft
	 */
	long shiftRight(long uDecimal, int positions);

	/**
	 * Returns an unscaled decimal which is equivalent to {@code uDecimal} with
	 * the decimal point moved {@code n} places to the left. If {@code n} is
	 * negative, the call is equivalent to {@code movePointRight(-n)}. The
	 * unscaled decimal value returned by this call has value <tt>(this &times;
	 * 10<sup>-n</sup>)</tt>.
	 * 
	 * @param n
	 *            number of places to move the decimal point to the left.
	 * @return an unscaled decimal which is equivalent to {@code uDecimal} with
	 *         the decimal point moved {@code n} places to the left.
	 * @see #movePointRight(long, int)
	 */
	long movePointLeft(long uDecimal, int positions);

	/**
	 * Returns an unscaled decimal which is equivalent to {@code uDecimal} with
	 * the decimal point moved {@code n} places to the right. If {@code n} is
	 * negative, the call is equivalent to {@code movePointLeft(-n)}. The
	 * unscaled decimal value returned by this call has value <tt>(this
	 * &times; 10<sup>n</sup>)</tt>.
	 * 
	 * @param n
	 *            number of places to move the decimal point to the right.
	 * @return an unscaled decimal which is equivalent to {@code uDecimal} with
	 *         the decimal point moved {@code n} places to the right.
	 * @see #movePointLeft(long, int)
	 */
	long movePointRight(long uDecimal, int positions);

	/**
	 * Converts the specified long value to an unscaled decimal.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the unscaled decimal representing the same value as the given
	 *         long value
	 */
	long fromLong(long value);

	/**
	 * Converts the specified double value to an unscaled decimal.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the unscaled decimal representing the same value as the given
	 *         double value
	 * @throws ArithmeticException
	 *             if value is {@link Double#NaN} or {@link Double#isInfinite()
	 *             infinite}
	 */
	long fromDouble(double value);

	/**
	 * Converts the specified {@link BigInteger} value to an unscaled decimal.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the unscaled decimal representing the same value as the given big
	 *         integer value
	 */
	long fromBigInteger(BigInteger value);

	/**
	 * Converts the specified {@link BigDecimal} value to an unscaled decimal.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the unscaled decimal representing the same value as the given big
	 *         decimal value
	 */
	long fromBigDecimal(BigDecimal value);

	/**
	 * Converts the specified unscaled decimal with the given scale to another
	 * unscaled decimal using the scale of this arithmetic object.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @return the unscaled decimal representing the same value as the given
	 *         unscaled decimal with the new scale defined by this arithmetic
	 *         object
	 */
	long fromUnscaled(long unscaledValue, int scale);

	/**
	 * Parses the string argument as a signed decimal returned as unscaled long
	 * value. The characters in the string must all be decimal digits, except an
	 * optional decimal point {@code '.'} and the first character that may be an
	 * ASCII minus sign {@code '-'} (<code>&#92;u002D'</code>) to indicate a
	 * negative value or an ASCII plus sign {@code '+'} (
	 * <code>'&#92;u002B'</code>) to indicate a positive value.
	 * 
	 * @param value
	 *            a {@code String} containing the decimal value representation
	 *            to be parsed
	 * @return the decimal as unscaled {@code long} value
	 * @throws NumberFormatException
	 *             if the string does not contain a parsable decimal.
	 */
	long parse(String value);

	/**
	 * Converts the specified unscaled decimal value {@code uDecimal} into a
	 * long value and returns it. The arithmetic's {@link #getRoundingMode()
	 * rounding mode} is applied if rounding is necessary.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value convert into a long value
	 * @return the {@code uDecimal} value converted into a long value, possibly
	 *         rounded or truncated
	 */
	long toLong(long uDecimal);

	double toDouble(long uDecimal);

	BigDecimal toBigDecimal(long uDecimal);

	BigDecimal toBigDecimal(long uDecimal, int scale);

	String toString(long uDecimal);

}
