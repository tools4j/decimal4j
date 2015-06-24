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

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * <tt>DecimalArithmetic</tt> defines the basic native operations for
 * {@link Decimal} numbers for one particular combination of {@link #getScale()
 * scale}, {@link #getRoundingMode() rounding mode} and
 * {@link #getOverflowMode() overflow mode}. Native here means that
 * <tt>Decimal</tt> values are simply represented by their underlying unscaled
 * <tt>long</tt> value. All operations therefore use unscaled longs for
 * <tt>Decimal</tt> arguments and return longs for <tt>Decimal</tt> number
 * results.
 * <p>
 * Application code does not usually need to use <tt>DecimalArithmetic</tt>
 * directly. It may be appropriate however for very specialized applications
 * with low latency, high frequency or zero garbage requirements. All operations
 * of <tt>DecimalArithmetic</tt> do not allocate any objects (zero garbage)
 * unless otherwise indicated.
 */
public interface DecimalArithmetic {
	/**
	 * Returns the scale {@code f} applied to all unscaled decimal values passed
	 * to and returned by this {@code DecimalArithmetic}. Corresponds to the
	 * number of digits to the right of the decimal point (cannot be negative).
	 * <p>
	 * A given {@link Decimal} value multiplied with <code>10<sup>f</sup></code>
	 * results in an unscaled long value. Conversely, a {@code Decimal} value
	 * {@code d} can be computed from the unscaled value {@code u} as
	 * <code>d = u*10<sup>-f</sup></code>.
	 * 
	 * @return the non-negative scale {@code f} applied to unscaled decimal
	 *         values within this {@code DecimalArithmetic} object
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
	 * Returns the <i>rounding mode</i> applied to operations of this
	 * {@code DecimalArithmetic} object if rounding is necessary.
	 * 
	 * @return the rounding mode applied to operations of this
	 *         {@code DecimalArithmetic} object if rounding is necessary
	 */
	RoundingMode getRoundingMode();

	/**
	 * Returns the <i>overflow mode</i> applied to operations of this
	 * {@code DecimalArithmetic} object if an overflow occurs. The overflow mode
	 * defines whether an operation should throw an exception if an overflow
	 * occurs.
	 * 
	 * @return the overflow mode applied to operations of this
	 *         {@code DecimalArithmetic} object if an overflow occurs
	 */
	OverflowMode getOverflowMode();

	/**
	 * Returns the <i>truncation policy</i> defining how to handle truncation
	 * due to overflow or rounding. The {@code TruncationPolicy} is defined by
	 * the {@link #getOverflowMode() overflow mode} and the
	 * {@link #getRoundingMode() rounding mode}.
	 * 
	 * @return the truncation policy defining how this {@code DecimalArithmetic}
	 *         handles truncation
	 */
	TruncationPolicy getTruncationPolicy();

	/**
	 * Derives an arithmetic instance for the specified {@code scale} using this
	 * arithmetic's {@link #getRoundingMode() rounding mode} and
	 * {@link #getOverflowMode() overflow mode}.
	 * 
	 * @param scale
	 *            the scale for the new arithmetic; must be in {@code [0,18]}
	 *            both ends inclusive
	 * @return an arithmetic instance with the given scale and this arithmetic's
	 *         rounding and overflow mode
	 * @throws IllegalArgumentException
	 *             if scale is not in {@code [0, 18]}
	 * @see Scales#getScaleMetrics(int)
	 * @see ScaleMetrics#getArithmetic(RoundingMode)
	 * @see ScaleMetrics#getArithmetic(TruncationPolicy)
	 */
	DecimalArithmetic deriveArithmetic(int scale);

	/**
	 * Derives an arithmetic instance for the same {@link #getScale() scale} as
	 * this arithmetic but for the specified {@code roundingMode}. The returned
	 * arithmetic uses the same {@link #getOverflowMode() overflow mode} as this
	 * arithmetic.
	 * 
	 * @param roundingMode
	 *            the rounding mode for the new arithmetic
	 * @return an arithmetic instance with the given rounding mode and this
	 *         arithmetic's scale and overflow mode
	 * @throws NullPointerException
	 *             if rounding mode is null
	 */
	DecimalArithmetic deriveArithmetic(RoundingMode roundingMode);

	/**
	 * Derives an arithmetic instance for the same {@link #getScale() scale} as
	 * this arithmetic but for the specified {@code roundingMode} and
	 * {@code overflowMode}.
	 * 
	 * @param roundingMode
	 *            the rounding mode for the new arithmetic
	 * @param overflowMode
	 *            the overflow mode for the new arithmetic
	 * @return an arithmetic instance with the given rounding and overflow mode
	 *         and this arithmetic's scale
	 * @throws NullPointerException
	 *             if any of the arguments is null
	 */
	DecimalArithmetic deriveArithmetic(RoundingMode roundingMode, OverflowMode overflowMode);

	/**
	 * Derives an arithmetic instance for the same {@link #getScale() scale} as
	 * this arithmetic but for the specified {@code overflowMode}. The returned
	 * arithmetic uses the same {@link #getRoundingMode() rounding mode} as this
	 * arithmetic.
	 * 
	 * @param overflowMode
	 *            the overflow mode for the new arithmetic
	 * @return an arithmetic instance with the given overflow mode and this
	 *         arithmetic's scale and rounding mode
	 * @throws NullPointerException
	 *             if overflow mode is null
	 */
	DecimalArithmetic deriveArithmetic(OverflowMode overflowMode);

	/**
	 * Derives an arithmetic instance for the same {@link #getScale() scale} as
	 * this arithmetic but with rounding and overflow mode specified by the
	 * given {@code truncationPolicy}.
	 * 
	 * @param truncationPolicy
	 *            the truncation policy specifying rounding and overflow mode
	 *            for the new arithmetic
	 * @return an arithmetic instance with rounding and overflow mode specified
	 *         by the truncation policy using this arithmetic's scale
	 * @throws NullPointerException
	 *             if truncation policy is null
	 */
	DecimalArithmetic deriveArithmetic(TruncationPolicy truncationPolicy);

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
	 * @param uDecimal
	 *            the unscaled decimal
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
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode()
	 *             overflow mode} is set to throw an exception
	 */
	long add(long uDecimal1, long uDecimal2);

	/**
	 * Returns an unscaled decimal whose value is
	 * {@code (uDecimal + lValue * scaleFactor)}.
	 * 
	 * @param uDecimal
	 *            unscaled decimal value to be added
	 * @param lValue
	 *            long value to be added
	 * @return (uDecimal + lValue * scaleFactor)}
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode()
	 *             overflow mode} is set to throw an exception
	 */
	long addLong(long uDecimal, long lValue);

	/**
	 * Returns an unscaled decimal whose value is {@code (uDecimalMinuend -
	 * uDecimalSubtrahend)}.
	 * 
	 * @param uDecimalMinuend
	 *            unscaled decimal value to be subtracted from
	 * @param uDecimalSubtrahend
	 *            unscaled decimal value to subtract from the minuend
	 * @return {@code uDecimalMinuend - uDecimalSubtrahend}
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode()
	 *             overflow mode} is set to throw an exception
	 */
	long subtract(long uDecimalMinuend, long uDecimalSubtrahend);

	/**
	 * Returns an unscaled decimal whose value is
	 * {@code (uDecimal - lValue * scaleFactor)}.
	 * 
	 * @param uDecimal
	 *            unscaled decimal value to be subtracted
	 * @param lValue
	 *            long value to be subtracted
	 * @return (uDecimal - lValue * scaleFactor)}
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode()
	 *             overflow mode} is set to throw an exception
	 */
	long subtractLong(long uDecimal, long lValue);

	/**
	 * Returns an unscaled decimal whose value is
	 * {@code (uDecimal1 * uDecimal2)}.
	 * 
	 * @param uDecimal1
	 *            first unscaled decimal value to be multiplied
	 * @param uDecimal2
	 *            second unscaled decimal value to be multiplied
	 * @return {@code uDecimal1 * uDecimal2}
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode()
	 *             overflow mode} is set to throw an exception
	 */
	long multiply(long uDecimal1, long uDecimal2);

	/**
	 * Returns an unscaled decimal whose value is {@code (uDecimal * lValue)}
	 * where the second argument is a true long value instead of an unscaled
	 * decimal.
	 * 
	 * @param uDecimal
	 *            unscaled decimal value to be multiplied
	 * @param lValue
	 *            long value to be multiplied
	 * @return {@code uDecimal * lValue}
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode()
	 *             overflow mode} is set to throw an exception
	 */
	long multiplyByLong(long uDecimal, long lValue);

	/**
	 * Returns an unscaled decimal whose value is
	 * <code>(uDecimal * 10<sup>n</sup>)</code>.
	 * <p>
	 * The power, {@code n}, may be negative, in which case this method performs
	 * a multiplication by a power of ten. If rounding must be performed (for
	 * negative n), this arithmetic's {@link #getRoundingMode() rounding mode}
	 * is applied.
	 * 
	 * @param uDecimal
	 *            value to be divided.
	 * @param n
	 *            the power of ten
	 * @return <code>(uDecimal &times; 10<sup>n</sup>)</code> as unscaled
	 *         decimal
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode()
	 *             overflow mode} is set to throw an exception
	 */
	long multiplyByPowerOf10(long uDecimal, int n);

	/**
	 * Returns an unscaled decimal whose value is
	 * {@code (uDecimalDividend / uDecimalDivisor)}. If rounding must be
	 * performed, this arithmetic's {@link #getRoundingMode() rounding mode} is
	 * applied.
	 * 
	 * @param uDecimalDividend
	 *            value to be divided.
	 * @param uDecimalDivisor
	 *            value by which the dividend is to be divided.
	 * @return {@code uDecimalDividend / uDecimalDivisor} as unscaled decimal
	 * @throws ArithmeticException
	 *             if {@code uDecimalDividend} is zero, if
	 *             {@link #getRoundingMode() rounding mode} is UNNECESSARY and
	 *             rounding is necessary or if an overflow occurs and the
	 *             {@link #getOverflowMode() overflow mode} is set to throw an
	 *             exception
	 */
	long divide(long uDecimalDividend, long uDecimalDivisor);

	/**
	 * Returns an unscaled decimal whose value is
	 * {@code (uDecimalDividend / lDivisor)} where the second argument is a true
	 * long value instead of an unscaled decimal. If rounding must be performed,
	 * this arithmetic's {@link #getRoundingMode() rounding mode} is applied.
	 * 
	 * @param uDecimalDividend
	 *            value to be divided.
	 * @param lDivisor
	 *            long value by which the dividend is to be divided.
	 * @return {@code uDecimalDividend / lDivisor} as unscaled decimal
	 * @throws ArithmeticException
	 *             if {@code uDecimalDividend} is zero, if
	 *             {@link #getRoundingMode() rounding mode} is UNNECESSARY and
	 *             rounding is necessary or if an overflow occurs and the
	 *             {@link #getOverflowMode() overflow mode} is set to throw an
	 *             exception
	 */
	long divideByLong(long uDecimalDividend, long lDivisor);

	/**
	 * Returns an unscaled decimal whose value is
	 * <code>(uDecimal / 10<sup>n</sup>)</code>. If rounding must be performed,
	 * this arithmetic's {@link #getRoundingMode() rounding mode} is applied.
	 * <p>
	 * The power, {@code n}, may be negative, in which case this method performs
	 * a multiplication by a power of ten.
	 * 
	 * @param uDecimal
	 *            value to be divided.
	 * @param n
	 *            the power of ten
	 * @return <code>(uDecimal / 10<sup>n</sup>)</code> as unscaled decimal
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY
	 *             and rounding is necessary or if an overflow occurs and the
	 *             {@link #getOverflowMode() overflow mode} is set to throw an
	 *             exception
	 */
	long divideByPowerOf10(long uDecimal, int n);

	/**
	 * Returns the average of {@code uDecimal1} and {@code uDecimal2}. The
	 * method is designed to avoid overflows. If rounding must be performed,
	 * this arithmetic's {@link #getRoundingMode() rounding mode} is applied.
	 *
	 * @param uDecimal1
	 *            the first unscaled decimal value to average
	 * @param uDecimal2
	 *            the second unscaled decimal value to average
	 * @return {@code (uDecimal1+uDecimal2)/2} using the rounding mode of this
	 *         arithetics
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY
	 *             and rounding is necessary or if an overflow occurs and the
	 *             {@link #getOverflowMode() overflow mode} is set to throw an
	 *             exception
	 */
	long avg(long uDecimal1, long uDecimal2);

	/**
	 * Returns the non-negative value {@code abs(uDecimal)}, which is the value
	 * itself if {@code uDecimal>=0} and {@code -uDecimal} if the given value is
	 * negative.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value
	 * @return {@code uDecimal} if {@code uDecimal>=0} and {@code -uDecimal}
	 *         otherwise
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode()
	 *             overflow mode} is set to throw an exception
	 */
	long abs(long uDecimal);

	/**
	 * Returns the negated value {@code -uDecimal} again as unscaled decimal
	 * value.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to negate
	 * @return {@code -uDecimal} as unscaled decimal value
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode()
	 *             overflow mode} is set to throw an exception
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
	 *             if {@code uDecimal} is zero, if {@link #getRoundingMode()
	 *             rounding mode} is UNNECESSARY and rounding is necessary or if
	 *             an overflow occurs and the {@link #getOverflowMode() overflow
	 *             mode} is set to throw an exception
	 */
	long invert(long uDecimal);

	/**
	 * Returns the square of {@code uDecimal} again as unscaled decimal value.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value
	 * @return {@code uDecimal^s}
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY
	 *             and rounding is necessary or if an overflow occurs and the
	 *             {@link #getOverflowMode() overflow mode} is set to throw an
	 *             exception
	 */
	long square(long uDecimal);

	/**
	 * Returns the square root of {@code uDecimal} again as unscaled decimal
	 * value.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value
	 * @return {@code sqrt(uDecimal)}
	 * @throws ArithmeticException
	 *             if {@code uDecimal} is negative or if
	 *             {@link #getRoundingMode() rounding mode} is UNNECESSARY and
	 *             rounding is necessary
	 */
	long sqrt(long uDecimal);

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
	 *             if {@code uDecimalBase==0} and {@code exponent} is negative.
	 *             (This would cause a division by zero.), if
	 *             {@link #getRoundingMode() rounding mode} is UNNECESSARY and
	 *             rounding is necessary or if an overflow occurs and the
	 *             {@link #getOverflowMode() overflow mode} is set to throw an
	 *             exception
	 */
	long pow(long uDecimalBase, int exponent);

	/**
	 * Returns an unscaled decimal whose value is {@code (uDecimal << n)}. The
	 * shift distance, {@code n}, may be negative, in which case this method
	 * performs a right shift. The result is <tt>(uDecimal * 2<sup>n</sup>)</tt>
	 * .
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to shift
	 * @param n
	 *            shift distance, in bits.
	 * @return {@code uDecimal << n}
	 * @throws ArithmeticException
	 *             if an overflow occurs and the {@link #getOverflowMode()
	 *             overflow mode} is set to throw an exception
	 * @see #shiftRight
	 */
	long shiftLeft(long uDecimal, int n);

	/**
	 * Returns an unscaled decimal whose value is {@code (uDecimal >> n)}. Sign
	 * extension is performed. The shift distance, {@code n}, may be negative,
	 * in which case this method performs a left shift. The result is
	 * <tt>floor(this / 2<sup>n</sup>)</tt>.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value to shift
	 * @param n
	 *            shift distance, in bits.
	 * @return {@code this >> n}
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY
	 *             and rounding is necessary or if an overflow occurs and the
	 *             {@link #getOverflowMode() overflow mode} is set to throw an
	 *             exception
	 * @see #shiftLeft
	 */
	long shiftRight(long uDecimal, int n);

	/**
	 * Returns an unscaled decimal whose value is rounded to the specified
	 * {@code precision} using the rounding mode of this arithmetic.
	 * <p>
	 * Note that this method does not change the scale of the value --- extra
	 * digits are simply zeroised.
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
	 *            the precision to use for the rounding, for instance 2 to round
	 *            to the second digit after the decimal point; must be at least
	 *            {@code (scale - 18)}
	 * @return an unsigned decimal rounded to the given precision
	 * @throws IllegalArgumentException
	 *             if {@code precision < scale - 18}
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY
	 *             and rounding is necessary or if an overflow occurs and the
	 *             {@link #getOverflowMode() overflow mode} is set to throw an
	 *             exception
	 */
	long round(long uDecimal, int precision);

	/**
	 * Converts the specified long value to an unscaled decimal.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the unscaled decimal representing the same value as the given
	 *         long value
	 * @throws IllegalArgumentException
	 *             if the specified long value cannot be converted to an
	 *             unscaled decimal because it would overflow
	 */
	long fromLong(long value);

	/**
	 * Converts the specified float value to an unscaled decimal.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the unscaled decimal representing the same value as the given
	 *         float value
	 * @throws IllegalArgumentException
	 *             if value is {@link Float#NaN} or {@link Float#isInfinite()
	 *             infinite} or if the floating point value cannot be converted
	 *             to an unscaled decimal because it would overflow
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY
	 *             and rounding is necessary
	 */
	long fromFloat(float value);

	/**
	 * Converts the specified double value to an unscaled decimal.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the unscaled decimal representing the same value as the given
	 *         double value
	 * @throws IllegalArgumentException
	 *             if value is {@link Double#NaN} or {@link Double#isInfinite()
	 *             infinite} or if the floating point value cannot be converted
	 *             to an unscaled decimal because it would overflow
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY
	 *             and rounding is necessary
	 */
	long fromDouble(double value);

	/**
	 * Converts the specified {@link BigInteger} value to an unscaled decimal.
	 * <p>
	 * Note: this operation is <b>not</b> strictly garbage free, meaning that
	 * temporary objects may be allocated if an unchecked overflow occurs during
	 * the conversion.
	 * 
	 * @param value
	 *            the value to convert
	 * @return the unscaled decimal representing the same value as the given big
	 *         integer value, or if an overflow occurs and the
	 *         {@link #getOverflowMode() overflow mode} is set to throw an
	 *         exception
	 */
	long fromBigInteger(BigInteger value);

	/**
	 * Converts the specified {@link BigDecimal} value to an unscaled decimal.
	 * <p>
	 * Note: this operation is <b>not</b> garbage free, meaning that new
	 * temporary objects may be allocated during the conversion.
	 * 
	 * @param value
	 *            the value to convert
	 * @throws IllegalArgumentException
	 *             if the big decimal value cannot be converted to an unscaled
	 *             decimal because it would overflow
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY
	 *             and rounding is necessary
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
	 * @throws IllegalArgumentException
	 *             if the unscaled value with the specified scale cannot be
	 *             converted to an unscaled value of this arithmetic's scale
	 *             decimal because it would overflow
	 * @throws ArithmeticException
	 *             if {@link #getRoundingMode() rounding mode} is UNNECESSARY
	 *             and rounding is necessary
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
	 *             if the string does not contain a parsable decimal
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

	/**
	 * Converts the specified unscaled decimal value {@code uDecimal} into a
	 * float value and returns it. The arithmetic's {@link #getRoundingMode()
	 * rounding mode} is applied if rounding is necessary.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value convert into a float value
	 * @return the {@code uDecimal} value converted into a float value, possibly
	 *         rounded or truncated
	 */
	float toFloat(long uDecimal);

	/**
	 * Converts the specified unscaled decimal value {@code uDecimal} into a
	 * double value and returns it. The arithmetic's {@link #getRoundingMode()
	 * rounding mode} is applied if rounding is necessary.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value convert into a double value
	 * @return the {@code uDecimal} value converted into a double value,
	 *         possibly rounded or truncated
	 */
	double toDouble(long uDecimal);

	/**
	 * Converts the specified unscaled decimal value {@code uDecimal} into a
	 * {@link BigDecimal} value using this arithmetic's {@link #getScale()
	 * scale} for the result value.
	 * <p>
	 * Note: this operation is <b>not</b> strictly garbage free since the result
	 * value is usually allocated; however no temporary objects other than the
	 * result are allocated during the conversion.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value convert into a {@code BigDecimal}
	 *            value
	 * @return the {@code uDecimal} value converted into a {@code BigDecimal}
	 *         value
	 */
	BigDecimal toBigDecimal(long uDecimal);

	/**
	 * Converts the specified unscaled decimal value {@code uDecimal} into a
	 * {@link BigDecimal} value using the specified {@code scale} for the result
	 * value. The arithmetic's {@link #getRoundingMode() rounding mode} is
	 * applied if rounding is necessary.
	 * <p>
	 * Note: this operation is <b>not</b> garbage free since the result value is
	 * usually allocated and also temporary objects may be allocated during the
	 * conversion.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value convert into a {@code BigDecimal}
	 *            value
	 * @param scale
	 *            the scale to use for the resulting {@code BigDecimal} value
	 * @return the {@code uDecimal} value converted into a {@code BigDecimal}
	 *         value, possibly rounded or truncated
	 */
	BigDecimal toBigDecimal(long uDecimal, int scale);

	/**
	 * Converts the specified unscaled decimal value {@code uDecimal} into a
	 * {@link String} and returns it. If the {@link #getScale() scale} is zero,
	 * the conversion is identical to {@link Long#toString(long)}. For all other
	 * scales a value with exactly {@code scale} fraction digits is returned
	 * even if some trailing fraction digits are zero.
	 * <p>
	 * Note: this operation is <b>not</b> strictly garbage free since the result
	 * value is allocated; however no temporary objects other than the result
	 * are allocated during the conversion (internally a {@link ThreadLocal}
	 * {@link StringBuilder} object is used to construct the string value, which
	 * may become garbage if the thread becomes garbage).
	 * 
	 * @param uDecimal
	 *            the unscaled decimal value convert into a {@code String}
	 * @return the {@code uDecimal} value as into a {@code String}
	 */
	String toString(long uDecimal);

}
