package ch.javasoft.decimal.scale;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.truncate.OverflowMode;
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * <tt>ScaleMetrics</tt> is associated with {@link Decimal} numbers and
 * represents the factor applied to the {@code long} value underlying a
 * {@code Decimal}. Scale stands for the fixed number of fraction digits of a
 * {@code Decimal}.
 * <p>
 * The <tt>Scale</tt> class contains a number of subclasses used by different
 * decimal types. With <tt>Scale</tt> subclasses, it is possible to distinguish
 * different decimal types and we can ensure that only decimals of the same
 * scale can directly operate with each other.
 */
public interface ScaleMetrics {
	/**
	 * Returns the scale, the number of fraction digits to the right of the
	 * decimal point of a {@link Decimal} value.
	 * 
	 * @return the scale also known as number of fraction digits
	 */
	int getScale();

	/**
	 * Returns the scale factor, which is 10<sup>f</sup> where {@code f} stands
	 * for the {@link #getScale() scale}.
	 * 
	 * @return the scale factor
	 */
	long getScaleFactor();

	/**
	 * Returns the {@link #getScaleFactor() scale factor} as a
	 * {@link BigInteger} value.
	 * 
	 * @return the scale factor as big integer
	 */
	BigInteger getScaleFactorAsBigInteger();

	/**
	 * Returns the {@link #getScaleFactor() scale factor} as a
	 * {@link BigDecimal} value.
	 * 
	 * @return the scale factor as big decimal
	 */
	BigDecimal getScaleFactorAsBigDecimal();

	/**
	 * Returns the largest integer value that can be represented using this
	 * scale.
	 * 
	 * @return {@code Long.MAX_VALUE / scaleFactor}
	 */
	long getMaxIntegerValue();

	/**
	 * Returns the smallest integer value that can be represented using this
	 * scale.
	 * 
	 * @return {@code Long.MIN_VALUE / scaleFactor}
	 */
	long getMinIntegerValue();

	/**
	 * Returns {@code factor*scaleFactor}.
	 * 
	 * @param factor
	 *            the factor
	 * @return {@code factor*scaleFactor}
	 */
	long multiplyByScaleFactor(long factor);

	/**
	 * Returns {@code factor*scaleFactor}, checking for lost information. If the
	 * result is out of the range of the {@code long} type, then an
	 * {@code ArithmeticException} is thrown.
	 * 
	 * @param factor
	 *            the factor
	 * @return {@code factor*scaleFactor}
	 * @throws ArithmeticException
	 *             if an overflow occurs
	 */
	long multiplyByScaleFactorExact(long factor);

	/**
	 * Returns {@code factor*low32(scaleFactor)} where low32 refers to the low
	 * 32 bits of the factor.
	 * 
	 * @param factor
	 *            the factor
	 * @return {@code factor*low32(scaleFactor)}
	 */
	long mulloByScaleFactor(int factor);

	/**
	 * Returns {@code factor*high32(scaleFactor)} where high32 refers to the
	 * high 32 bits of the factor.
	 * 
	 * @param factor
	 *            the factor
	 * @return {@code factor*high32(scaleFactor)}
	 */
	long mulhiByScaleFactor(int factor);

	/**
	 * Returns {@code dividend/scaleFactor}.
	 * 
	 * @param dividend
	 *            the dividend
	 * @return {@code dividend/scaleFactor}
	 */
	long divideByScaleFactor(long dividend);

	/**
	 * Returns {@code dividend % scaleFactor} also known as reminder.
	 * 
	 * @param dividend
	 *            the dividend
	 * @return {@code dividend % scaleFactor}
	 */
	long moduloByScaleFactor(long dividend);

	/**
	 * Returns the default arithmetics for this scale performing unchecked
	 * operations with rounding mode {@link RoundingMode#HALF_UP HALF_UP}.
	 * 
	 * @return default arithmetics for this scale
	 */
	DecimalArithmetics getDefaultArithmetics();

	/**
	 * Returns the truncating arithmetics for this scale and with the specified
	 * {@code overflowMode} that performs all operations without rounding.
	 * 
	 * @param overflowMode
	 *            the overflow mode used by the returned arithmetics
	 * @return truncating arithmetics for this scale
	 * @see RoundingMode#DOWN
	 */
	DecimalArithmetics getTruncatingArithmetics(OverflowMode overflowMode);

	/**
	 * Returns the arithmetics for this scale that performs all operations with
	 * the specified {@code roundingMode}.
	 *
	 * @param roundingMode
	 *            the rounding mode used by the returned arithmetics
	 * @return arithmetics for this scale with specified rounding mode
	 */
	DecimalArithmetics getArithmetics(RoundingMode roundingMode);

	/**
	 * Returns the arithmetics for this scale that performs all operations with
	 * the specified {@code truncationPolicy}.
	 *
	 * @param truncationPolicy
	 *            the truncation policy used by the returned arithmetics
	 * @return arithmetics for this scale with specified truncation policy
	 */
	DecimalArithmetics getArithmetics(TruncationPolicy truncationPolicy);
}
