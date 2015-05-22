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
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Interface implemented by mutable {@link Decimal} classes of different scales.
 * Mutable Decimals modify their state when performing arithmetic operations;
 * they represent the result after the operation. Arithmetic operations
 * therefore return {@code this} as return value. Note however that the the
 * {@link #getScale() scale} of a Mutable Decimal does not change and remains
 * constant throughout the lifetime of a {@code MutableDecimal} instance.
 * <p>
 * Mutable Decimals may be preferred over {@link ImmutableDecimal} descendants
 * e.g. if the allocation of new objects is undesired or if a chain of
 * operations is performed.
 * <p>
 * Mutable Decimals are <i>NOT</i> thread safe.
 * 
 * @param <S>
 *            the scale metrics type associated with this Decimal
 */
public interface MutableDecimal<S extends ScaleMetrics> extends Decimal<S> {

	/**
	 * Sets {@code this} Decimal to 0 and returns {@code this} now representing
	 * zero.
	 * 
	 * @return {@code this} Decimal after assigning {@code this = 0}
	 */
	MutableDecimal<S> setZero();

	/**
	 * Sets {@code this} Decimal to 1 and returns {@code this} now representing
	 * one.
	 * 
	 * @return {@code this} Decimal after assigning {@code this = 1}
	 */
	MutableDecimal<S> setOne();

	/**
	 * Sets {@code this} Decimal to -1 and returns {@code this} now representing
	 * minus one.
	 * 
	 * @return {@code this} Decimal after assigning {@code this = -1}
	 */
	MutableDecimal<S> setMinusOne();

	/**
	 * Sets {@code this} Decimal to the smallest positive value representable by
	 * this Mutable Decimal and returns {@code this} now representing one ULP.
	 * 
	 * @return {@code this} Decimal after assigning {@code this = ULP}
	 */
	MutableDecimal<S> setUlp();

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} Decimal after assigning the given {@code value}
	 */
	MutableDecimal<S> set(Decimal<S> value);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted to the appropriate scale
	 * @return {@code this} Decimal after assigning the given {@code value}
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(Decimal<?> value, RoundingMode roundingMode);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} Decimal after assigning the given {@code value}
	 * @throws ArithmeticException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(long value);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} Decimal after assigning the given {@code value}
	 * @throws ArithmeticException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(BigInteger value);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} Decimal after assigning the given {@code value}
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(float value);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a Decimal number
	 * @return {@code this} decimal after assigning the given {@code value}
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(float value, RoundingMode roundingMode);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} Decimal after assigning the given {@code value}
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(double value);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a Decimal number
	 * @return {@code this} Decimal after assigning the given {@code value}
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(double value, RoundingMode roundingMode);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} Decimal after assigning the given {@code value}
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(BigDecimal value);

	/**
	 * Sets {@code this} Decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}. An exception is thrown if
	 * the value cannot be represented as a Decimal.
	 * 
	 * @param value
	 *            value to be set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a Decimal number
	 * @return {@code this} Decimal after assigning the given {@code value}
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal
	 */
	MutableDecimal<S> set(BigDecimal value, RoundingMode roundingMode);

	/**
	 * Sets {@code this} Decimal to the specified {@code unscaledValue} and
	 * returns {@code this} now representing
	 * <code>this = unscaledValue * 10<sup>-scale</sup></code> where scale is
	 * the scale factor of this Mutable Decimal.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @return {@code this} Decimal after assigning
	 *         <code>this = unscaledValue * 10<sup>-scale</sup></code>.
	 * @see #getScaleMetrics()
	 * @see ScaleMetrics#getScaleFactor()
	 */
	MutableDecimal<S> setUnscaled(long unscaledValue);

	/**
	 * Sets {@code this} Decimal to the specified {@code unscaledValue} with the
	 * given {@code scale} and returns {@code this} now representing
	 * <code>this = round(unscaledValue * 10<sup>-scale</sup>)</code>. Note that
	 * the conversion may involve rounding if the specified {@code scale} is
	 * larger than {@link #getScale()}; the default rounding mode is applied in
	 * this case.
	 * <p>
	 * An exception is thrown if the value cannot be represented as a Decimal
	 * with this Mutable Decimal's {@link #getScale() scale} for instance
	 * because it would cause an overflow.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @return {@code this} Decimal after assigning
	 *         <code>this = round(unscaledValue * 10<sup>-scale)</sup></code>.
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal with this
	 *             Mutable Decimal's {@code scale}
	 */
	MutableDecimal<S> setUnscaled(long unscaledValue, int scale);

	/**
	 * Sets {@code this} Decimal to the specified {@code unscaledValue} with the
	 * given {@code scale} and returns {@code this} now representing
	 * <code>this = round(unscaledValue * 10<sup>-scale</sup>)</code>. Note that
	 * the conversion may involve rounding if the specified {@code scale} is
	 * larger than {@link #getScale()}; the specified {@code roundingMode} is
	 * applied in this case.
	 * <p>
	 * An exception is thrown if the value cannot be represented as a Decimal
	 * with this Mutable Decimal's {@link #getScale() scale} for instance
	 * because it would cause an overflow.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted to this Decimal's scale
	 * @return {@code this} Decimal after assigning
	 *         <code>this = round(unscaledValue * 10<sup>-scale</sup>)</code>.
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 * @throws NumberFormatException
	 *             if the value cannot be converted into a Decimal with this
	 *             Mutable Decimal's {@code scale}
	 */
	MutableDecimal<S> setUnscaled(long unscaledValue, int scale, RoundingMode roundingMode);

	/**
	 * Parses the given string value and sets {@code this} Decimal to the parsed
	 * {@code value}. An exception is thrown if the value cannot be parsed.
	 * 
	 * @param value
	 *            the string value to parse and set
	 * @return {@code this} Decimal after assigning the parsed value
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 * @throws NumberFormatException
	 *             if the value cannot be parsed
	 */
	MutableDecimal<S> set(String value);

	/**
	 * Parses the given string value and sets {@code this} Decimal to the parsed
	 * {@code value}. An exception is thrown if the value cannot be parsed.
	 * 
	 * @param value
	 *            the string value to parse and set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a decimal number
	 * @return {@code this} Decimal after assigning the parsed value
	 * @throws ArithmeticException
	 *             if {@code roundingMode} is {@link RoundingMode#UNNECESSARY
	 *             UNNESSESSARY} and rounding is necessary
	 * @throws NumberFormatException
	 *             if the value cannot be parsed
	 */
	MutableDecimal<S> set(String value, RoundingMode roundingMode);

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
	MutableDecimal<S> min(MutableDecimal<S> val);

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
	MutableDecimal<S> max(MutableDecimal<S> val);
	
	/**
	 * Returns a clone of this mutable Decimal numerically identical to this
	 * value.
	 * 
	 * @return a numerically identical clone of this value
	 */
	MutableDecimal<S> clone();

	//override some methods with specialized return type

	@Override
	MutableDecimal<S> integralPart();

	@Override
	MutableDecimal<S> fractionalPart();

	@Override
	MutableDecimal<S> round(int precision);

	@Override
	MutableDecimal<S> round(int precision, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> round(int precision, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<?> scale(int scale);

	@SuppressWarnings("hiding")
	@Override
	<S extends ScaleMetrics> MutableDecimal<S> scale(S scaleMetrics);

	@Override
	MutableDecimal<?> scale(int scale, RoundingMode roundingMode);

	@SuppressWarnings("hiding")
	@Override
	<S extends ScaleMetrics> MutableDecimal<S> scale(S scaleMetrics, RoundingMode roundingMode);

	@Override
	MutableDecimal<?> scale(int scale, TruncationPolicy truncationPolicy);

	@SuppressWarnings("hiding")
	@Override
	<S extends ScaleMetrics> MutableDecimal<S> scale(S scaleMetrics, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> add(Decimal<S> augend);

	@Override
	MutableDecimal<S> add(Decimal<S> augend, OverflowMode overflowMode);

	@Override
	MutableDecimal<S> add(Decimal<?> augend, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> add(Decimal<?> augend, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> add(long augend);

	@Override
	MutableDecimal<S> add(long augend, OverflowMode overflowMode);

	@Override
	MutableDecimal<S> add(double augend);

	@Override
	MutableDecimal<S> add(double augend, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> addUnscaled(long unscaledAugend);

	@Override
	MutableDecimal<S> addUnscaled(long unscaledAugend, OverflowMode overflowMode);

	@Override
	MutableDecimal<S> addUnscaled(long unscaledAugend, int scale);

	@Override
	MutableDecimal<S> addUnscaled(long unscaledAugend, int scale, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> addUnscaled(long unscaledAugend, int scale, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> addSquared(Decimal<S> value);

	@Override
	MutableDecimal<S> addSquared(Decimal<S> value, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> addSquared(Decimal<S> value, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> subtract(Decimal<S> subtrahend);

	@Override
	MutableDecimal<S> subtract(Decimal<S> subtrahend, OverflowMode overflowMode);

	@Override
	MutableDecimal<S> subtract(Decimal<?> subtrahend, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> subtract(Decimal<?> subtrahend, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> subtract(long subtrahend);

	@Override
	MutableDecimal<S> subtract(long subtrahend, OverflowMode overflowMode);

	@Override
	MutableDecimal<S> subtract(double subtrahend);

	@Override
	MutableDecimal<S> subtract(double subtrahend, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> subtractUnscaled(long unscaledSubtrahend);

	@Override
	MutableDecimal<S> subtractUnscaled(long unscaledSubtrahend, OverflowMode overflowMode);

	@Override
	MutableDecimal<S> subtractUnscaled(long unscaledSubtrahend, int scale);

	@Override
	MutableDecimal<S> subtractUnscaled(long unscaledSubtrahend, int scale, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> subtractUnscaled(long unscaledSubtrahend, int scale, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> subtractSquared(Decimal<S> value);

	@Override
	MutableDecimal<S> subtractSquared(Decimal<S> value, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> subtractSquared(Decimal<S> value, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> multiply(Decimal<S> multiplicand);

	@Override
	MutableDecimal<S> multiply(Decimal<S> multiplicand, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> multiply(Decimal<S> multiplicand, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> multiplyBy(Decimal<?> multiplicand);

	@Override
	MutableDecimal<S> multiplyBy(Decimal<?> multiplicand, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> multiplyBy(Decimal<?> multiplicand, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<?> multiplyExact(Decimal<?> multiplicand);

	@Override
	MutableDecimal<S> multiply(long multiplicand);

	@Override
	MutableDecimal<S> multiply(long multiplicand, OverflowMode overflowMode);

	@Override
	MutableDecimal<S> multiply(double multiplicand);

	@Override
	MutableDecimal<S> multiply(double multiplicand, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand);

	@Override
	MutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale);

	@Override
	MutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> multiplyByPowerOfTen(int n);

	@Override
	MutableDecimal<S> multiplyByPowerOfTen(int n, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> multiplyByPowerOfTen(int n, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> divide(Decimal<S> divisor);

	@Override
	MutableDecimal<S> divide(Decimal<S> divisor, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> divide(Decimal<S> divisor, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> divideBy(Decimal<?> divisor);

	@Override
	MutableDecimal<S> divideBy(Decimal<?> divisor, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> divideBy(Decimal<?> divisor, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> divideTruncate(Decimal<S> divisor);

	@Override
	MutableDecimal<S> divideExact(Decimal<S> divisor);

	@Override
	MutableDecimal<S> divide(long divisor);

	@Override
	MutableDecimal<S> divide(long divisor, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> divide(long divisor, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> divide(double divisor);

	@Override
	MutableDecimal<S> divide(double divisor, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> divideUnscaled(long unscaledDivisor);

	@Override
	MutableDecimal<S> divideUnscaled(long unscaledDivisor, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> divideUnscaled(long unscaledDivisor, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> divideUnscaled(long unscaledDivisor, int scale);

	@Override
	MutableDecimal<S> divideUnscaled(long unscaledDivisor, int scale, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> divideUnscaled(long unscaledDivisor, int scale, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> divideByPowerOfTen(int n);

	@Override
	MutableDecimal<S> divideByPowerOfTen(int n, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> divideByPowerOfTen(int n, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> divideToIntegralValue(Decimal<S> divisor);

	@Override
	MutableDecimal<S> divideToIntegralValue(Decimal<S> divisor, OverflowMode overflowMode);

	@Override
	MutableDecimal<S>[] divideAndRemainder(Decimal<S> divisor);

	@Override
	MutableDecimal<S>[] divideAndRemainder(Decimal<S> divisor, OverflowMode overflowMode);

	@Override
	MutableDecimal<S> remainder(Decimal<S> divisor);

	@Override
	MutableDecimal<S> negate();

	@Override
	MutableDecimal<S> negate(OverflowMode overflowMode);

	@Override
	MutableDecimal<S> abs();

	@Override
	MutableDecimal<S> abs(OverflowMode overflowMode);

	@Override
	MutableDecimal<S> invert();

	@Override
	MutableDecimal<S> invert(RoundingMode roundingMode);

	@Override
	MutableDecimal<S> invert(TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> square();

	@Override
	MutableDecimal<S> square(RoundingMode roundingMode);

	@Override
	MutableDecimal<S> square(TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> sqrt();

	@Override
	MutableDecimal<S> sqrt(RoundingMode roundingMode);

	@Override
	MutableDecimal<S> shiftLeft(int n);

	@Override
	MutableDecimal<S> shiftLeft(int n, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> shiftLeft(int n, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> shiftRight(int n);

	@Override
	MutableDecimal<S> shiftRight(int n, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> shiftRight(int n, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> pow(int n);

	@Override
	MutableDecimal<S> pow(int n, RoundingMode roundingMode);

	@Override
	MutableDecimal<S> pow(int n, TruncationPolicy truncationPolicy);

	@Override
	MutableDecimal<S> avg(Decimal<S> val);

	@Override
	MutableDecimal<S> avg(Decimal<S> val, RoundingMode roundingMode);
}
