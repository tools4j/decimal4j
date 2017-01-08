/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 decimal4j (tools4j), Marco Terzer
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

import java.math.RoundingMode;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Interface implemented by immutable {@link Decimal} classes of different
 * scales. Immutable Decimals allocate a new Decimals instance for results of
 * arithmetic operations.
 * <p>
 * Consider also {@link MutableDecimal} descendants especially for chained
 * operations.
 * <p>
 * Immutable Decimals are thread safe.
 * 
 * @param <S>
 *            the scale metrics type associated with this Decimal
 */
public interface ImmutableDecimal<S extends ScaleMetrics> extends Decimal<S> {

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
	ImmutableDecimal<S> min(ImmutableDecimal<S> val);

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
	ImmutableDecimal<S> max(ImmutableDecimal<S> val);

	//override some methods with specialized return type

	@Override
	ImmutableDecimal<S> integralPart();

	@Override
	ImmutableDecimal<S> fractionalPart();

	@Override
	ImmutableDecimal<S> round(int precision);

	@Override
	ImmutableDecimal<S> round(int precision, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> round(int precision, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<?> scale(int scale);

	@SuppressWarnings("hiding")
	@Override
	<S extends ScaleMetrics> ImmutableDecimal<S> scale(S scaleMetrics);

	@Override
	ImmutableDecimal<?> scale(int scale, RoundingMode roundingMode);

	@SuppressWarnings("hiding")
	@Override
	<S extends ScaleMetrics> ImmutableDecimal<S> scale(S scaleMetrics, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> add(Decimal<S> augend);

	@Override
	ImmutableDecimal<S> add(Decimal<S> augend, OverflowMode overflowMode);

	@Override
	ImmutableDecimal<S> add(Decimal<?> augend, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> add(Decimal<?> augend, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> add(long augend);

	@Override
	ImmutableDecimal<S> add(long augend, OverflowMode overflowMode);

	@Override
	ImmutableDecimal<S> add(double augend);

	@Override
	ImmutableDecimal<S> add(double augend, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> addUnscaled(long unscaledAugend);

	@Override
	ImmutableDecimal<S> addUnscaled(long unscaledAugend, OverflowMode overflowMode);

	@Override
	ImmutableDecimal<S> addUnscaled(long unscaledAugend, int scale);

	@Override
	ImmutableDecimal<S> addUnscaled(long unscaledAugend, int scale, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> addUnscaled(long unscaledAugend, int scale, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> addSquared(Decimal<S> value);

	@Override
	ImmutableDecimal<S> addSquared(Decimal<S> value, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> addSquared(Decimal<S> value, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> subtract(Decimal<S> subtrahend);

	@Override
	ImmutableDecimal<S> subtract(Decimal<S> subtrahend, OverflowMode overflowMode);

	@Override
	ImmutableDecimal<S> subtract(Decimal<?> subtrahend, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> subtract(Decimal<?> subtrahend, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> subtract(long subtrahend);

	@Override
	ImmutableDecimal<S> subtract(long subtrahend, OverflowMode overflowMode);

	@Override
	ImmutableDecimal<S> subtract(double subtrahend);

	@Override
	ImmutableDecimal<S> subtract(double subtrahend, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> subtractUnscaled(long unscaledSubtrahend);

	@Override
	ImmutableDecimal<S> subtractUnscaled(long unscaledSubtrahend, OverflowMode overflowMode);

	@Override
	ImmutableDecimal<S> subtractUnscaled(long unscaledSubtrahend, int scale);

	@Override
	ImmutableDecimal<S> subtractUnscaled(long unscaledSubtrahend, int scale, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> subtractUnscaled(long unscaledSubtrahend, int scale, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> subtractSquared(Decimal<S> value);

	@Override
	ImmutableDecimal<S> subtractSquared(Decimal<S> value, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> subtractSquared(Decimal<S> value, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> multiply(Decimal<S> multiplicand);

	@Override
	ImmutableDecimal<S> multiply(Decimal<S> multiplicand, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> multiply(Decimal<S> multiplicand, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> multiplyBy(Decimal<?> multiplicand);

	@Override
	ImmutableDecimal<S> multiplyBy(Decimal<?> multiplicand, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> multiplyBy(Decimal<?> multiplicand, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<?> multiplyExact(Decimal<?> multiplicand);

	@Override
	ImmutableDecimal<S> multiply(long multiplicand);

	@Override
	ImmutableDecimal<S> multiply(long multiplicand, OverflowMode overflowMode);

	@Override
	ImmutableDecimal<S> multiply(double multiplicand);

	@Override
	ImmutableDecimal<S> multiply(double multiplicand, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand);

	@Override
	ImmutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale);

	@Override
	ImmutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand, int scale, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> multiplyByPowerOfTen(int n);

	@Override
	ImmutableDecimal<S> multiplyByPowerOfTen(int n, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> multiplyByPowerOfTen(int n, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> divide(Decimal<S> divisor);

	@Override
	ImmutableDecimal<S> divide(Decimal<S> divisor, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> divide(Decimal<S> divisor, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> divideBy(Decimal<?> divisor);

	@Override
	ImmutableDecimal<S> divideBy(Decimal<?> divisor, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> divideBy(Decimal<?> divisor, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> divideTruncate(Decimal<S> divisor);

	@Override
	ImmutableDecimal<S> divideExact(Decimal<S> divisor);

	@Override
	ImmutableDecimal<S> divide(long divisor);

	@Override
	ImmutableDecimal<S> divide(long divisor, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> divide(long divisor, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> divide(double divisor);

	@Override
	ImmutableDecimal<S> divide(double divisor, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> divideUnscaled(long unscaledDivisor);

	@Override
	ImmutableDecimal<S> divideUnscaled(long unscaledDivisor, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> divideUnscaled(long unscaledDivisor, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> divideUnscaled(long unscaledDivisor, int scale);

	@Override
	ImmutableDecimal<S> divideUnscaled(long unscaledDivisor, int scale, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> divideUnscaled(long unscaledDivisor, int scale, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> divideByPowerOfTen(int n);

	@Override
	ImmutableDecimal<S> divideByPowerOfTen(int n, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> divideByPowerOfTen(int n, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> divideToIntegralValue(Decimal<S> divisor);

	@Override
	ImmutableDecimal<S> divideToIntegralValue(Decimal<S> divisor, OverflowMode overflowMode);

	@Override
	ImmutableDecimal<S>[] divideAndRemainder(Decimal<S> divisor);

	@Override
	ImmutableDecimal<S>[] divideAndRemainder(Decimal<S> divisor, OverflowMode overflowMode);

	@Override
	ImmutableDecimal<S> remainder(Decimal<S> divisor);

	@Override
	ImmutableDecimal<S> negate();

	@Override
	ImmutableDecimal<S> negate(OverflowMode overflowMode);

	@Override
	ImmutableDecimal<S> abs();

	@Override
	ImmutableDecimal<S> abs(OverflowMode overflowMode);

	@Override
	ImmutableDecimal<S> invert();

	@Override
	ImmutableDecimal<S> invert(RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> invert(TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> square();

	@Override
	ImmutableDecimal<S> square(RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> square(TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> sqrt();

	@Override
	ImmutableDecimal<S> sqrt(RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> shiftLeft(int n);

	@Override
	ImmutableDecimal<S> shiftLeft(int n, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> shiftLeft(int n, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> shiftRight(int n);

	@Override
	ImmutableDecimal<S> shiftRight(int n, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> shiftRight(int n, TruncationPolicy truncationPolicy);

	@Override
	ImmutableDecimal<S> pow(int n);

	@Override
	ImmutableDecimal<S> pow(int n, RoundingMode roundingMode);

	@Override
	ImmutableDecimal<S> pow(int n, TruncationPolicy truncationPolicy);
	
	@Override
	ImmutableDecimal<S> avg(Decimal<S> val);

	@Override
	ImmutableDecimal<S> avg(Decimal<S> val, RoundingMode roundingMode);
}
