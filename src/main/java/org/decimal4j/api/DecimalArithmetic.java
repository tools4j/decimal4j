/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 decimal4j (tools4j), Marco Terzer
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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * <tt>DecimalArithmetic</tt> defines the basic primitive operations for {@link Decimal} numbers for one particular
 * combination of {@link #getScale() scale}, {@link #getRoundingMode() rounding mode} and {@link #getOverflowMode()
 * overflow mode}. Primitive here means that <tt>Decimal</tt> values are simply represented by their underlying unscaled
 * <tt>long</tt> value. All operations therefore use unscaled longs for <tt>Decimal</tt> arguments and return longs for
 * <tt>Decimal</tt> number results.
 * <p>
 * Application code does not usually need to use <tt>DecimalArithmetic</tt> directly. It may be appropriate however for
 * very specialized applications with low latency, high frequency or zero garbage requirements. All operations of
 * <tt>DecimalArithmetic</tt> do not allocate any objects (zero garbage) unless otherwise indicated.
 */
public interface DecimalArithmetic {
	/**
	 * Returns the scale {@code f} applied to all unscaled decimal values passed to and returned by this
	 * {@code DecimalArithmetic}. Corresponds to the number of digits to the right of the decimal point (cannot be
	 * negative).
	 * <p>
	 * A given {@link Decimal} value multiplied with <tt>10<sup>f</sup></tt> results in an unscaled long value.
	 * Conversely, a {@code Decimal} value {@code d} can be computed from the unscaled value {@code u} as
	 * <tt>d = u*10<sup>-f</sup></tt>.
	 * 
	 * @return the non-negative scale {@code f} applied to unscaled decimal values within this {@code DecimalArithmetic}
	 *         object
	 * @see ScaleMetrics#getScale()
	 */
	int getScale();

	/**
	 * Returns the scale metrics associated with this decimal arithmetic object.
	 * 
	 * @return the scale metrics
	 */
	ScaleMetrics getScaleMetrics();

	/**
	 * Returns the <i>rounding mode</i> applied to operations of this {@code DecimalArithmetic} object if rounding is
	 * necessary.
	 * 
	 * @return the rounding mode applied to operations of this {@code DecimalArithmetic} object if rounding is necessary
	 */
	RoundingMode getRoundingMode();

	/**
	 * Returns the <i>overflow mode</i> applied to operations of this {@code DecimalArithmetic} object if an overflow
	 * occurs. The overflow mode defines whether an operation should throw an exception if an overflow occurs.
	 * 
	 * @return the overflow mode applied to operations of this {@code DecimalArithmetic} object if an overflow occurs
	 */
	OverflowMode getOverflowMode();

	/**
	 * Returns the <i>truncation policy</i> defining how to handle truncation due to overflow or rounding. The
	 * {@code TruncationPolicy} is defined by the {@link #getOverflowMode() overflow mode} and the
	 * {@link #getRoundingMode() rounding mode}.
	 * 
	 * @return the truncation policy defining how this {@code DecimalArithmetic} handles truncation
	 */
	TruncationPolicy getTruncationPolicy();

	/**
	 * Derives an arithmetic instance for the specified {@code scale} using this arithmetic's {@link #getRoundingMode()
	 * rounding mode} and {@link #getOverflowMode() overflow mode}.
	 * 
	 * @param scale
	 *            the scale for the new arithmetic; must be in {@code [0,18]} both ends inclusive
	 * @return an arithmetic instance with the given scale and this arithmetic's rounding and overflow mode
	 * @throws IllegalArgumentException
	 *             if scale is not in {@code [0, 18]}
	 * @see Scales#getScaleMetrics(int)
	 * @see ScaleMetrics#getArithmetic(RoundingMode)
	 * @see ScaleMetrics#getArithmetic(TruncationPolicy)
	 */
	DecimalArithmetic deriveArithmetic(int scale);

	/**
	 * Derives an arithmetic instance for the same {@link #getScale() scale} as this arithmetic but for the specified
	 * {@code roundingMode}. The returned arithmetic uses the same {@link #getOverflowMode() overflow mode} as this
	 * arithmetic.
	 * 
	 * @param roundingMode
	 *            the rounding mode for the new arithmetic
	 * @return an arithmetic instance with the given rounding mode and this arithmetic's scale and overflow mode
	 * @throws NullPointerException
	 *             if rounding mode is null
	 */
	DecimalArithmetic deriveArithmetic(RoundingMode roundingMode);

	/**
	 * Derives an arithmetic instance for the same {@link #getScale() scale} as this arithmetic but for the specified
	 * {@code roundingMode} and {@code overflowMode}.
	 * 
	 * @param roundingMode
	 *            the rounding mode for the new arithmetic
	 * @param overflowMode
	 *            the overflow mode for the new arithmetic
	 * @return an arithmetic instance with the given rounding and overflow mode and this arithmetic's scale
	 * @throws NullPointerException
	 *             if any of the arguments is null
	 */
	DecimalArithmetic deriveArithmetic(RoundingMode roundingMode, OverflowMode overflowMode);

	/**
	 * Derives an arithmetic instance for the same {@link #getScale() scale} as this arithmetic but for the specified
	 * {@code overflowMode}. The returned arithmetic uses the same {@link #getRoundingMode() rounding mode} as this
	 * arithmetic.
	 * 
	 * @param overflowMode
	 *            the overflow mode for the new arithmetic
	 * @return an arithmetic instance with the given overflow mode and this arithmetic's scale and rounding mode
	 * @throws NullPointerException
	 *             if overflow mode is null
	 */
	DecimalArithmetic deriveArithmetic(OverflowMode overflowMode);

	/**
	 * Derives an arithmetic instance for the same {@link #getScale() scale} as this arithmetic but with rounding and
	 * overflow mode specified by the given {@code truncationPolicy}.
	 * 
	 * @param truncationPolicy
	 *            the truncation policy specifying rounding and overflow mode for the new arithmetic
	 * @return an arithmetic instance with rounding and overflow mode specified by the truncation policy using this
	 *         arithmetic's scale
	 * @throws NullPointerException
	 *             if truncation policy is null
	 */
	DecimalArithmetic deriveArithmetic(TruncationPolicy truncationPolicy);

	/**
	 * Returns the unscaled decimal for the decimal value {@code 1}. One is the value <tt>10<sup>scale</sup></tt> which
	 * is also the multiplier used to get the unscaled decimal from the true decimal value.
	 * 
	 * @return the unscaled decimal representing the decimal value 1
	 */
	long one();

	/**
	 * Returns the signum function of the specified unscaled decimal.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal
	 * @return -1, 0, or 1 as the value of the specified unscaled decimal is negative, zero, or positive.
	 */
	int signum(long uDecimal);

	/**
	 * Compares two unscaled decimal values numerically.
	 * 
	 * @param uDecimal1
	 *            the first unscaled decimal to compare
	 * @param uDecimal2
	 *            the second unscaled decimal to compare
	 * @return the value {@code 0} if {@code unscaled1 == unscaled2}; a value less than {@code 0} if
	 *         {@code unscaled1 < unscaled2}; and a value greater than {@code 0} if {@code unscaled1 > unscaled2}
	 */
	int compare(long uDecimal1, long uDecimal2);

	/**
	 * Compares two unscaled decimal values numerically. Note that scale of the first operand is determined by this
	 * arithmetic's {@link #getScale() scale} whereas the scale of the second {@code unscaled} value is explicitly
	 * specified by the {@code scale} argument.
	 * 
	 * @param uDecimal
	 *            the first unscaled decimal to compare
	 * @param unscaled
	 *            the second unscaled decimal to compare
	 * @param scale
	 *            the scale of {@code unscaled}
	 * @return the value {@code 0} if {@code unscaled1 == unscaled2}; a value less than {@code 0} if
	 *         {@code unscaled1 < unscaled2}; and a value greater than {@code 0} if {@code unscaled1 > unscaled2}
	 */
	int compareToUnscaled(long uDecimal, long unscaled, int scale);

	/**
	 * Returns an unscaled decimal whose value is {@code (uDecimal1 + uDecimal2)}.
	 * 
	 * @param uDecimal1
	 *            first unscaled decimal value to be added
	 * @param uDecimal2
	 *            second unscaled decimal value to be added
	 * @return {@code uDecimal1 + uDecimal2}
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long add(long uDecimal1, long uDecimal2);

	/**
	 * Returns an unscaled decimal whose value is the sum of the specified arguments: {@code (uDecimal + lValue)}.
	 * <p>
	 * Mathematically the method calculates <tt>(uDecimal + lValue * 10<sup>scale</sup>)</tt> avoiding information loss
	 * due to overflow of intermediary results.
	 * 
	 * @param uDecimal
	 *            unscaled decimal value to be added
	 * @param lValue
	 *            long value to be added
	 * @return <tt>(uDecimal + lValue * 10<sup>scale</sup>)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long addLong(long uDecimal, long lValue);

	/**
	 * Returns an unscaled decimal whose value is <tt>(uDecimal + unscaled * 10<sup>-scale</sup>)</tt>. If rounding must
	 * be performed, this arithmetic's {@link #getRoundingMode() rounding mode} is applied. Note that scale of the first
	 * operand is determined by this arithmetic's {@link #getScale() scale} whereas the scale of the second
	 * {@code unscaled} value is explicitly specified by the {@code scale} argument.
	 * <p>
	 * Mathematically the method calculates <tt>round(uDecimal + lValue * 10<sup>-scale + s</sup>)</tt> where {@code s}
	 * refers to this arithetic's scale. The method avoids information loss due to overflow of intermediary results.
	 * 
	 * @param uDecimal
	 *            unscaled decimal value to be added to
	 * @param unscaled
	 *            unscaled value to add
	 * @param scale
	 *            scale associated with the {@code unscaled} value
	 * @return <tt>round(uDecimal + unscaled * 10<sup>-scale</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary or if an
	 *             overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long addUnscaled(long uDecimal, long unscaled, int scale);

	/**
	 * Returns an unscaled decimal whose value is {@code (uDecimalMinuend -
	 * uDecimalSubtrahend)}.
	 * 
	 * @param uDecimalMinuend
	 *            unscaled decimal value to subtract from
	 * @param uDecimalSubtrahend
	 *            unscaled decimal value to subtract from the minuend
	 * @return {@code uDecimalMinuend - uDecimalSubtrahend}
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long subtract(long uDecimalMinuend, long uDecimalSubtrahend);

	/**
	 * Returns an unscaled decimal whose value is the difference of the specified arguments: {@code (uDecimal - lValue)}
	 * .
	 * <p>
	 * Mathematically the method calculates <tt>(uDecimal - lValue * 10<sup>scale</sup>)</tt> avoiding information loss
	 * due to overflow of intermediary results.
	 * 
	 * @param uDecimal
	 *            unscaled decimal value to subtract from
	 * @param lValue
	 *            long value to subtract
	 * @return <tt>(uDecimal - lValue * 10<sup>scale</sup>)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long subtractLong(long uDecimal, long lValue);

	/**
	 * Returns an unscaled decimal whose value is <tt>(uDecimal - unscaled * 10<sup>-scale</sup>)</tt>. If rounding must
	 * be performed, this arithmetic's {@link #getRoundingMode() rounding mode} is applied. Note that scale of the first
	 * operand is determined by this arithmetic's {@link #getScale() scale} whereas the scale of the second
	 * {@code unscaled} value is explicitly specified by the {@code scale} argument.
	 * <p>
	 * Mathematically the method calculates <tt>round(uDecimal - lValue * 10<sup>-scale + s</sup>)</tt> where {@code s}
	 * refers to this arithetic's scale. The method avoids information loss due to overflow of intermediary results.
	 * 
	 * @param uDecimal
	 *            unscaled decimal value to subtract from
	 * @param unscaled
	 *            unscaled value to subtract
	 * @param scale
	 *            scale associated with the {@code unscaled} value
	 * @return <tt>round(uDecimal - unscaled * 10<sup>-scale</sup>)</tt> as unscaled decimal
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary or if an
	 *             overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long subtractUnscaled(long uDecimal, long unscaled, int scale);

	/**
	 * Returns an unscaled decimal whose value is the product of the specified arguments:
	 * {@code (uDecimal1 * uDecimal2)}. If rounding must be performed, this arithmetic's {@link #getRoundingMode()
	 * rounding mode} is applied.
	 * <p>
	 * Mathematically the method calculates <tt>round((uDecimal1 * uDecimal2) * 10<sup>-scale</sup>)</tt> avoiding
	 * information loss due to overflow of intermediary results.
	 * 
	 * @param uDecimal1
	 *            first unscaled decimal value to be multiplied
	 * @param uDecimal2
	 *            second unscaled decimal value to be multiplied
	 * @return {@code round(uDecimal1 * uDecimal2)}
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary or if an
	 *             overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long multiply(long uDecimal1, long uDecimal2);

	/**
	 * Returns an unscaled decimal whose value is {@code (uDecimal * lValue)} where the second argument is a true long
	 * value instead of an unscaled decimal.
	 * 
	 * @param uDecimal
	 *            unscaled decimal value to be multiplied
	 * @param lValue
	 *            long value to be multiplied
	 * @return {@code uDecimal * lValue}
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long multiplyByLong(long uDecimal, long lValue);

	/**
	 * Returns an unscaled decimal whose value is <tt>(uDecimal * unscaled * 10<sup>-scale</sup>)</tt>. If rounding must
	 * be performed, this arithmetic's {@link #getRoundingMode() rounding mode} is applied. Note that scale of the first
	 * operand is determined by this arithmetic's {@link #getScale() scale} whereas the scale of the second
	 * {@code unscaled} value is explicitly specified by the {@code scale} argument.
	 * <p>
	 * Mathematically the method calculates <tt>round((uDecimal * unscaled) * 10<sup>-scale</sup>)</tt> avoiding
	 * information loss due to overflow of intermediary results.
	 * 
	 * @param uDecimal
	 *            unscaled decimal value to be multiplied
	 * @param unscaled
	 *            unscaled value to be multiplied
	 * @param scale
	 *            scale associated with the {@code unscaled} value
	 * @return <tt>round(uDecimal * (unscaled * 10<sup>-scale</sup>))</tt>
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary or if an
	 *             overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long multiplyByUnscaled(long uDecimal, long unscaled, int scale);

	/**
	 * Returns an unscaled decimal whose value is <tt>(uDecimal * 10<sup>n</sup>)</tt>.
	 * <p>
	 * The power, {@code n}, may be negative, in which case this method performs a division by a power of ten. If
	 * rounding must be performed (for negative n), this arithmetic's {@link #getRoundingMode() rounding mode} is
	 * applied.
	 * 
	 * @param uDecimal
	 *            value to be multiplied
	 * @param n
	 *            the power of ten
	 * @return <tt>round(uDecimal &times; 10<sup>n</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary or if an
	 *             overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long multiplyByPowerOf10(long uDecimal, int n);

	/**
	 * Returns an unscaled decimal whose value is the quotient of the specified arguments:
	 * {@code (uDecimalDividend / uDecimalDivisor)}. If rounding must be performed, this arithmetic's
	 * {@link #getRoundingMode() rounding mode} is applied.
	 * <p>
	 * Mathematically the method calculates <tt>round((uDecimalDividend * 10<sup>scale</sup>) / uDecimalDivisor)</tt>
	 * avoiding information loss due to overflow of intermediary results.
	 * 
	 * @param uDecimalDividend
	 *            value to be divided.
	 * @param uDecimalDivisor
	 *            value by which the dividend is to be divided.
	 * @return {@code round(uDecimalDividend / uDecimalDivisor)}
	 * @throws ArithmeticException
	 *             if {@code uDecimalDividend} is zero, if {@link #getRoundingMode() rounding mode} is UNNECESSARY and
	 *             rounding is necessary or if an overflow occurs and the {@link #getOverflowMode() overflow mode} is
	 *             set to throw an exception
	 */
	long divide(long uDecimalDividend, long uDecimalDivisor);

	/**
	 * Returns an unscaled decimal whose value is {@code (uDecimalDividend / lDivisor)} where the second argument is a
	 * true long value instead of an unscaled decimal. If rounding must be performed, this arithmetic's
	 * {@link #getRoundingMode() rounding mode} is applied.
	 * 
	 * @param uDecimalDividend
	 *            value to be divided.
	 * @param lDivisor
	 *            long value by which the dividend is to be divided.
	 * @return {@code round(uDecimalDividend / lDivisor)}
	 * @throws ArithmeticException
	 *             if {@code uDecimalDividend} is zero, if {@link #getRoundingMode() rounding mode} is UNNECESSARY and
	 *             rounding is necessary or if an overflow occurs and the {@link #getOverflowMode() overflow mode} is
	 *             set to throw an exception
	 */
	long divideByLong(long uDecimalDividend, long lDivisor);

	/**
	 * Returns an unscaled decimal whose value is <tt>(uDecimal / (unscaled * 10<sup>-scale</sup>))</tt>. If rounding
	 * must be performed, this arithmetic's {@link #getRoundingMode() rounding mode} is applied. Note that scale of the
	 * first operand is determined by this arithmetic's {@link #getScale() scale} whereas the scale of the second
	 * {@code unscaled} value is explicitly specified by the {@code scale} argument.
	 * <p>
	 * Mathematically the method calculates <tt>round((uDecimal * 10<sup>scale</sup>) / unscaled)</tt> avoiding
	 * information loss due to overflow of intermediary results.
	 * 
	 * @param uDecimal
	 *            value to be divided.
	 * @param unscaled
	 *            unscaled value by which the dividend is to be divided.
	 * @param scale
	 *            scale associated with the {@code unscaled} value
	 * @return <tt>round(uDecimal / (unscaled * 10<sup>-scale</sup>))</tt>
	 * @throws ArithmeticException
	 *             if {@code uDecimal} is zero, if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding
	 *             is necessary or if an overflow occurs and the {@link #getOverflowMode() overflow mode} is set to
	 *             throw an exception
	 */
	long divideByUnscaled(long uDecimal, long unscaled, int scale);

	/**
	 * Returns an unscaled decimal whose value is <tt>(uDecimal / 10<sup>n</sup>)</tt>. If rounding must be performed,
	 * this arithmetic's {@link #getRoundingMode() rounding mode} is applied.
	 * <p>
	 * The power, {@code n}, may be negative, in which case this method performs a multiplication by a power of ten.
	 * 
	 * @param uDecimal
	 *            value to be divided.
	 * @param n
	 *            the power of ten
	 * @return <tt>round(uDecimal / 10<sup>n</sup>)</tt>
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary or if an
	 *             overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long divideByPowerOf10(long uDecimal, int n);

	/**
	 * Returns an unscaled decimal whose value is the average of {@code uDecimal1} and {@code uDecimal2}. The method is
	 * much more efficient than an addition and subsequent long division and is guaranteed not to overflow. If rounding
	 * must be performed, this arithmetic's {@link #getRoundingMode() rounding mode} is applied.
	 *
	 * @param uDecimal1
	 *            the first unscaled decimal value to average
	 * @param uDecimal2
	 *            the second unscaled decimal value to average
	 * @return {@code round((uDecimal1 + uDecimal2) / 2)}
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary
	 */
	long avg(long uDecimal1, long uDecimal2);

	/**
	 * Returns an unscaled decimal whose value is {@code abs(uDecimal)}, which is the value itself if
	 * {@code uDecimal>=0} and {@code -uDecimal} if the given value is negative.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value
	 * @return {@code abs(uDecimal)}
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long abs(long uDecimal);

	/**
	 * Returns an unscaled decimal whose value is {@code -uDecimal}.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to negate
	 * @return {@code -uDecimal}
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long negate(long uDecimal);

	/**
	 * Returns an unscaled decimal whose value is the inverse of the argument: {@code 1/uDecimal}. If rounding must be
	 * performed, this arithmetic's {@link #getRoundingMode() rounding mode} is applied.
	 * <p>
	 * Mathematically the method calculates <tt>round((10<sup>scale</sup> * 10<sup>scale</sup>) / uDecimalDivisor)</tt>
	 * avoiding information loss due to overflow of intermediary results.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to invert
	 * @return {@code round(1/uDecimal)}
	 * @throws ArithmeticException
	 *             if {@code uDecimal} is zero, if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding
	 *             is necessary or if an overflow occurs and the {@link #getOverflowMode() overflow mode} is set to
	 *             throw an exception
	 */
	long invert(long uDecimal);

	/**
	 * Returns an unscaled decimal whose value is the square of the specified argument: <tt>uDecimal<sup>2</sup></tt>.
	 * If rounding must be performed, this arithmetic's {@link #getRoundingMode() rounding mode} is applied.
	 * <p>
	 * Mathematically the method calculates <tt>round((uDecimal * uDecimal) * 10<sup>-scale</sup>)</tt> avoiding
	 * information loss due to overflow of intermediary results.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to be squared
	 * @return {@code round(uDecimal * uDecimal)}
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary or if an
	 *             overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long square(long uDecimal);

	/**
	 * Returns an unscaled decimal whose value is the square root of the specified argument: <tt>sqrt(uDecimal)</tt>. If
	 * rounding must be performed, this arithmetic's {@link #getRoundingMode() rounding mode} is applied.
	 * <p>
	 * Mathematically the method calculates <tt>round(sqrt(uDecimal * 10<sup>scale</sup>))</tt> avoiding information
	 * loss due to overflow of intermediary results.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value
	 * @return {@code round(sqrt(uDecimal))}
	 * @throws ArithmeticException
	 *             if {@code uDecimal} is negative or if {@link #getRoundingMode() rounding mode} is UNNECESSARY and
	 *             rounding is necessary
	 */
	long sqrt(long uDecimal);

	/**
	 * Returns an unscaled decimal whose value is <tt>(uDecimalBase<sup>exponent</sup>)</tt>. Note that {@code exponent}
	 * is an integer rather than a decimal. If rounding must be performed, this arithmetic's {@link #getRoundingMode()
	 * rounding mode} is applied.
	 * <p>
	 * The current implementation uses the core algorithm defined in ANSI standard X3.274-1996. For {@code exponent >= 0}, the
	 * returned numerical value is within 1 ULP of the exact numerical value; the result is actually exact for all
	 * rounding modes other than HALF_UP, HALF_EVEN and HALF_DOWN. No precision is guaranteed for {@code exponent < 0} but the
	 * result is usually exact up to 10-20 ULP.
	 * <p>
	 * Properties of the X3.274-1996 algorithm are:
	 * <ul>
	 * <li>An {@code IllegalArgumentException} is thrown if {@code abs(n) > 999999999}</li>
	 * <li>if {@code exponent} is zero, one is returned even if {@code uDecimalBase} is zero, otherwise
	 * <ul>
	 * <li>if {@code exponent} is positive, the result is calculated via the repeated squaring technique into a single
	 * accumulator</li>
	 * <li>if {@code exponent} is negative, the result is calculated as if {@code exponent} were positive; this value is then divided
	 * into one</li>
	 * <li>The final value from either the positive or negative case is then rounded using this arithmetic's
	 * {@link #getRoundingMode() rounding mode}</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p>
	 * Note: this operation is <b>not</b> strictly garbage free since internally, two {@link ThreadLocal} objects are
	 * used to calculate the result. The {@code ThreadLocal} values may become garbage if the thread becomes garbage.
	 * 
	 * @param uDecimalBase
	 *            the unscaled decimal base value
	 * @param exponent
	 *            exponent to which {@code uDecimalBase} is to be raised.
	 * @return <tt>uDecimalBase<sup>exponent</sup></tt>
	 * @throws IllegalArgumentException
	 *             if {@code abs(exponent) > 999999999}
	 * @throws ArithmeticException
	 *             if {@code uDecimalBase==0} and {@code exponent} is negative. (This would cause a division by zero.),
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary or if an
	 *             overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long pow(long uDecimalBase, int exponent);

	/**
	 * Returns an unscaled decimal whose value is {@code (uDecimal << n)}. The shift distance, {@code n}, may be
	 * negative, in which case this method performs a right shift. The result is equal to
	 * <tt>round(uDecimal * 2<sup>n</sup>)</tt> using this arithmetic's {@link #getRoundingMode() rounding mode} if
	 * rounding is necessary.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to shift
	 * @param n
	 *            shift distance, in bits.
	 * @return {@code round(uDecimal << n)}
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary or if an
	 *             overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 * @see #shiftRight(long, int)
	 */
	long shiftLeft(long uDecimal, int n);

	/**
	 * Returns an unscaled decimal whose value is {@code (uDecimal >> n)}. The shift distance, {@code n}, may be
	 * negative, in which case this method performs a left shift. The result is equal to
	 * <tt>round(uDecimal / 2<sup>n</sup>)</tt> using this arithmetic's {@link #getRoundingMode() rounding mode} if
	 * rounding is necessary.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to shift
	 * @param n
	 *            shift distance, in bits.
	 * @return {@code round(uDecimal >> n)}
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary or if an
	 *             overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 * @see #shiftLeft(long, int)
	 */
	long shiftRight(long uDecimal, int n);

	/**
	 * Returns an unscaled decimal whose value is rounded to the specified {@code precision} using the
	 * {@link #getRoundingMode() rounding mode} of this arithmetic.
	 * <p>
	 * Note that this method does not change the scale of the value --- extra digits are simply zeroised.
	 * <p>
	 * <i>Examples and special cases:</i>
	 * <ul>
	 * <li><b>precision = 0</b><br>
	 * value is rounded to an integer value</li>
	 * <li><b>precision = 2</b><br>
	 * value is rounded to the second digit after the decimal point</li>
	 * <li><b>precision = -3</b><br>
	 * value is rounded to the thousands</li>
	 * <li><b>precision &ge; scale</b><br>
	 * values is returned unchanged</li>
	 * <li><b>precision &lt; scale - 18</b><br>
	 * {@code IllegalArgumentException} is thrown</li>
	 * </ul>
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to round
	 * @param precision
	 *            the precision to use for the rounding, for instance 2 to round to the second digit after the decimal
	 *            point; must be at least {@code (scale - 18)}
	 * @return an unsigned decimal rounded to the given precision
	 * @throws IllegalArgumentException
	 *             if {@code precision < scale - 18}
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary or if an
	 *             overflow occurs and the {@link #getOverflowMode() overflow mode} is set to throw an exception
	 */
	long round(long uDecimal, int precision);

	/**
	 * Converts the specified long value to an unscaled decimal. An exception is thrown if the specified value is too
	 * large to be represented as a Decimal of this arithmetic's {@link #getScale() scale}.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the unscaled decimal representing the same value as the given long value
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a Decimal with the scale of this arithmetic
	 */
	long fromLong(long value);

	/**
	 * Converts the specified float value to an unscaled decimal. An exception is thrown if the specified value is too
	 * large to be represented as a Decimal of this arithmetic's {@link #getScale() scale}.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the unscaled decimal representing the same value as the given float value
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large for the float to be represented
	 *             as a {@code Decimal} with the scale of this arithmetic
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary
	 */
	long fromFloat(float value);

	/**
	 * Converts the specified double value to an unscaled decimal. An exception is thrown if the specified value is too
	 * large to be represented as a Decimal of this arithmetic's {@link #getScale() scale}.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the unscaled decimal representing the same value as the given double value
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large for the double to be represented
	 *             as a {@code Decimal} with the scale of this arithmetic
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary
	 */
	long fromDouble(double value);

	/**
	 * Converts the specified {@link BigInteger} value to an unscaled decimal. An exception is thrown if the specified
	 * value is too large to be represented as a Decimal of this arithmetic's {@link #getScale() scale}.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the unscaled decimal representing the same value as the given big integer value
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a Decimal with the scale of this arithmetic
	 */
	long fromBigInteger(BigInteger value);

	/**
	 * Converts the specified {@link BigDecimal} value to an unscaled decimal. An exception is thrown if the specified
	 * value is too large to be represented as a Decimal of this arithmetic's {@link #getScale() scale}.
	 * <p>
	 * Note: this operation is <b>not</b> garbage free, meaning that new temporary objects may be allocated during the
	 * conversion.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the unscaled decimal representing the same value as the given big decimal value
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a Decimal with the scale of this arithmetic
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary
	 */
	long fromBigDecimal(BigDecimal value);

	/**
	 * Converts the specified unscaled decimal with the given scale to another unscaled decimal of the scale of this
	 * arithmetic.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @param scale
	 *            the scale associated with {@code unscaledValue}
	 * @return the unscaled decimal representing the same value as the given unscaled decimal with the new scale defined
	 *         by this arithmetic
	 * @throws IllegalArgumentException
	 *             if the unscaled value with the specified scale is too large to be represented as a Decimal with the
	 *             scale of this arithmetic
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary
	 */
	long fromUnscaled(long unscaledValue, int scale);

	/**
	 * Translates the string representation of a {@code Decimal} into an unscaled Decimal. The string representation
	 * consists of an optional sign, {@code '+'} or {@code '-'} , followed by a sequence of zero or more decimal digits
	 * ("the integer"), optionally followed by a fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal digits. The string must contain at
	 * least one digit in either the integer or the fraction. If the fraction contains more digits than this
	 * arithmetic's {@link #getScale() scale}, the value is rounded using the arithmetic's {@link #getRoundingMode()
	 * rounding mode}. An exception is thrown if the value is too large to be represented as a Decimal of this
	 * arithmetic's scale.
	 * 
	 * @param value
	 *            a {@code String} containing the decimal value representation to be parsed
	 * @return the decimal as unscaled {@code long} value
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal} or if the value is too large to be
	 *             represented as a Decimal with the scale of this arithmetic
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary
	 */
	long parse(String value);

	/**
	 * Translates the string representation of a {@code Decimal} into an unscaled Decimal. The string representation
	 * consists of an optional sign, {@code '+'} or {@code '-'} , followed by a sequence of zero or more decimal digits
	 * ("the integer"), optionally followed by a fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal digits. The string must contain at
	 * least one digit in either the integer or the fraction. If the fraction contains more digits than this
	 * arithmetic's {@link #getScale() scale}, the value is rounded using the arithmetic's {@link #getRoundingMode()
	 * rounding mode}. An exception is thrown if the value is too large to be represented as a Decimal of this
	 * arithmetic's scale.
	 * 
	 * @param value
	 *            a character sequence such as a {@code String} containing the decimal value representation to be parsed
	 * @param start
	 *            the start index to read characters in {@code value}, inclusive
	 * @param end
	 *            the end index where to stop reading in characters in {@code value}, exclusive
	 * @return the decimal as unscaled {@code long} value
	 * @throws IndexOutOfBoundsException
	 *             if {@code start < 0} or {@code end > value.length()}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal} or if the value is too large to be
	 *             represented as a Decimal with the scale of this arithmetic
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary
	 */
	long parse(CharSequence value, int start, int end);

	/**
	 * Converts the specified unscaled decimal value into a long value and returns it. The arithmetic's
	 * {@link #getRoundingMode() rounding mode} is applied if rounding is necessary.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to convert into a long value
	 * @return the {@code uDecimal} value converted into a long value, possibly rounded or truncated
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary
	 */
	long toLong(long uDecimal);

	/**
	 * Converts the specified unscaled decimal value into an unscaled value of the given scale. The arithmetic's
	 * {@link #getRoundingMode() rounding mode} is applied if rounding is necessary.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to convert into an unscaled value of the specified scale
	 * @param scale
	 *            the target scale for the result value
	 * @return the {@code uDecimal} value converted into an unscaled value of the specified scale, possibly rounded or
	 *         truncated
	 * @throws IllegalArgumentException
	 *             if the unscaled value with this arithmetic's scale is too large to be represented as an unscaled
	 *             decimal with the specified scale
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary
	 */
	long toUnscaled(long uDecimal, int scale);

	/**
	 * Converts the specified unscaled decimal value into a float value and returns it. The arithmetic's
	 * {@link #getRoundingMode() rounding mode} is applied if rounding is necessary.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to convert into a float value
	 * @return the {@code uDecimal} value converted into a float value, possibly rounded or truncated
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary
	 */
	float toFloat(long uDecimal);

	/**
	 * Converts the specified unscaled decimal value into a double value and returns it. The arithmetic's
	 * {@link #getRoundingMode() rounding mode} is applied if rounding is necessary.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to convert into a double value
	 * @return the {@code uDecimal} value converted into a double value, possibly rounded or truncated
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary
	 */
	double toDouble(long uDecimal);

	/**
	 * Converts the specified unscaled decimal value into a {@link BigDecimal} value using this arithmetic's
	 * {@link #getScale() scale} for the result value.
	 * <p>
	 * Note: this operation is <b>not</b> strictly garbage free since the result value is usually allocated; however no
	 * temporary objects other than the result are allocated during the conversion.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to convert into a {@code BigDecimal} value
	 * @return the {@code uDecimal} value converted into a {@code BigDecimal} value
	 */
	BigDecimal toBigDecimal(long uDecimal);

	/**
	 * Converts the specified unscaled decimal value into a {@link BigDecimal} value using the specified {@code scale}
	 * for the result value. The arithmetic's {@link #getRoundingMode() rounding mode} is applied if rounding is
	 * necessary.
	 * <p>
	 * Note: this operation is <b>not</b> garbage free since the result value is usually allocated and also temporary
	 * objects may be allocated during the conversion. Note however that temporary objects are only allocated if the
	 * unscaled value of the result exceeds the range of a long value.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to convert into a {@code BigDecimal} value
	 * @param scale
	 *            the scale to use for the resulting {@code BigDecimal} value
	 * @return the {@code uDecimal} value converted into a {@code BigDecimal} value, possibly rounded or truncated
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY and rounding is necessary
	 */
	BigDecimal toBigDecimal(long uDecimal, int scale);

	/**
	 * Converts the specified unscaled decimal value into a {@link String} and returns it. If the {@link #getScale()
	 * scale} is zero, the conversion is identical to {@link Long#toString(long)}. For all other scales a value with
	 * exactly {@code scale} fraction digits is returned even if some trailing fraction digits are zero.
	 * <p>
	 * Note: this operation is <b>not</b> strictly garbage free since the result value is allocated; however no
	 * temporary objects other than the result are allocated during the conversion (internally a {@link ThreadLocal}
	 * {@link StringBuilder} object is used to construct the string value, which may become garbage if the thread
	 * becomes garbage).
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to convert into a {@code String}
	 * @return the {@code uDecimal} value as into a {@code String}
	 */
	String toString(long uDecimal);

	/**
	 * Converts the specified unscaled decimal value into a {@link String} and appends the string to the
	 * {@code appendable}.
	 * <p>
	 * If the {@link #getScale() scale} is zero, the conversion into a string is identical to
	 * {@link Long#toString(long)}. For all other scales a string value with exactly {@code scale} fraction digits is
	 * created even if some trailing fraction digits are zero.
	 * <p>
	 * Note: this operation is <b>not</b> strictly garbage free since internally, a {@link ThreadLocal} string builder
	 * is used to construct the string. The {@code ThreadLocal} value may become garbage if the thread becomes garbage.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to convert into a {@code String}
	 * @param appendable
	 *            the appendable to which the string representation of the unscaled decimal value is to be appended
	 * @throws IOException
	 *             If an I/O error occurs when appending to {@code appendable}
	 */
	void toString(long uDecimal, Appendable appendable) throws IOException;
}
