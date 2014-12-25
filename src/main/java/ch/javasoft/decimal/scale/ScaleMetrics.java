package ch.javasoft.decimal.scale;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.truncate.OverflowMode;
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * <tt>ScaleMetrics</tt> defines various metrics associated with the scale of a
 * {@link Decimal} number. It is mainly used internally from code implementing
 * the arithmetic operations of a {@code Decimal}.
 * <p>
 * The {@link #getScale() scale} determines the number of fraction digits of the
 * {@code Decimal}. The {@link #getScaleFactor() scale factor} is the
 * multiplier/divisor for conversions between the {@code Decimal} value and the
 * unscaled {@code long} value underlying every {@code Decimal}.
 * <p>
 * Operations such as {@link #multiplyByScaleFactor(long)
 * multiplyByScaleFactor(..)} are defined here as separate methods to allow for
 * compiler optimizations. Multiplications and divisions are for instance
 * translated into shifts and adds by the compiler instead of the more expensive
 * multiplication and division operations with non-constant long values.
 * <p>
 * {@code ScaleMetrics} also provides access to {@link DecimalArithmetics}
 * instances for different rounding modes and overflow policies.
 * {@code DecimalArithmetics} objects can be used to deal with decimal numbers
 * <i>natively</i> which means that {@code Decimal} numbers are passed to the
 * arithmetics class in their native form as unscaled {@code long} values.
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
	 * Returns the number of leading zeros of the scale factor
	 * @return {@link Long#numberOfLeadingZeros(long)} applied to the scale factor
	 */
	int getScaleFactorNumberOfLeadingZeros();

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
	 * Returns {@code unsignedDividend/scaleFactor} using unsigned division.
	 * 
	 * @param unsignedDividend
	 *            the unsigned dividend
	 * @return {@code unsignedDividend/scaleFactor}
	 */
	long divideUnsignedByScaleFactor(long unsignedDividend);

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
