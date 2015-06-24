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
package org.decimal4j.base;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.ImmutableDecimal;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Common base class for {@link AbstractImmutableDecimal immutable} and
 * {@link AbstractMutableDecimal mutable} {@link Decimal} numbers of different
 * scales.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this decimal
 */
@SuppressWarnings("serial")
abstract public class AbstractDecimal<S extends ScaleMetrics, D extends AbstractDecimal<S, D>> extends Number implements
		Decimal<S> {

	/**
	 * Returns this or a new {@code Decimal} whose value is
	 * <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>.
	 * 
	 * @param unscaled
	 *            unscaled value to be returned as a {@code Decimal}
	 * @return <tt>unscaled &times; 10<sup>-scale</sup></tt>
	 */
	abstract protected D createOrAssign(long unscaled);

	/**
	 * Returns a new {@code Decimal} whose value is
	 * <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>.
	 * 
	 * @param unscaled
	 *            unscaled value to be returned as a {@code Decimal}
	 * @return <tt>unscaled &times; 10<sup>-scale</sup></tt>
	 */
	abstract protected D create(long unscaled);

	/**
	 * Returns a new {@code Decimal} array of the specified {@code length}.
	 * 
	 * @param length
	 *            the length of the array to return
	 * @return {@code new D[length]}
	 */
	abstract protected D[] createArray(int length);

	/**
	 * Returns {@code this} decimal value as concrete implementation subtype.
	 * 
	 * @return {@code this}
	 */
	abstract protected D self();

	/**
	 * Returns the default arithmetic performing unchecked operations with
	 * rounding mode {@link RoundingMode#HALF_UP HALF_UP}.
	 * 
	 * @return default arithmetic with {@link RoundingMode#HALF_UP HALF_UP}
	 *         rounding and {@link OverflowMode#UNCHECKED UNCHECKED} overflow
	 *         mode
	 */
	protected DecimalArithmetic getDefaultArithmetic() {
		return getScaleMetrics().getDefaultArithmetic();
	}

	/**
	 * Returns the default arithmetic performing checked operations with
	 * rounding mode {@link RoundingMode#HALF_UP HALF_UP}.
	 * 
	 * @return default arithmetic with {@link RoundingMode#HALF_UP HALF_UP}
	 *         rounding and {@link OverflowMode#CHECKED CHECKED} overflow mode
	 */
	protected DecimalArithmetic getDefaultCheckedArithmetic() {
		return getScaleMetrics().getDefaultCheckedArithmetic();
	}

	/**
	 * Returns the default arithmetic performing checked operations with
	 * rounding mode {@link RoundingMode#HALF_UP HALF_UP} and the specified
	 * {@code overflowMode}.
	 * 
	 * @param overflowMode
	 *            the overflow for the returned arithmetic
	 * 
	 * @return default arithmetic with {@link RoundingMode#HALF_UP HALF_UP}
	 *         rounding and the given {@code overflowMode}
	 */
	protected DecimalArithmetic getArithmeticFor(OverflowMode overflowMode) {
		return getScaleMetrics().getArithmetic(overflowMode.getTruncationPolicyFor(RoundingMode.HALF_UP));
	}

	/**
	 * Returns the arithmetic performing unchecked operations with rounding mode
	 * {@link RoundingMode#DOWN DOWN}.
	 * 
	 * @return arithmetic with {@link RoundingMode#DOWN DOWN} rounding and
	 *         {@link OverflowMode#UNCHECKED UNCHECKED} overflow mode
	 */
	protected DecimalArithmetic getRoundingDownArithmetic() {
		return getScaleMetrics().getRoundingDownArithmetic();
	}

	/**
	 * Returns the arithmetic performing unchecked operations with rounding mode
	 * {@link RoundingMode#FLOOR FLOOR}.
	 * 
	 * @return arithmetic with {@link RoundingMode#FLOOR FLOOR} rounding and
	 *         {@link OverflowMode#UNCHECKED UNCHECKED} overflow mode
	 */
	protected DecimalArithmetic getRoundingFloorArithmetic() {
		return getScaleMetrics().getRoundingFloorArithmetic();
	}

	/**
	 * Returns the arithmetic performing unchecked operations with rounding mode
	 * {@link RoundingMode#HALF_EVEN HALF_EVEN}.
	 * 
	 * @return arithmetic with {@link RoundingMode#HALF_UP HALF_UP} rounding and
	 *         {@link OverflowMode#UNCHECKED UNCHECKED} overflow mode
	 */
	protected DecimalArithmetic getRoundingHalfEvenArithmetic() {
		return getScaleMetrics().getRoundingHalfEvenArithmetic();
	}

	/**
	 * Returns the arithmetic performing unchecked operations with rounding mode
	 * {@link RoundingMode#UNNECESSARY UNNECESSARY}.
	 * 
	 * @return arithmetic with {@link RoundingMode#UNNECESSARY UNNECESSARY}
	 *         rounding and {@link OverflowMode#UNCHECKED UNCHECKED} overflow
	 *         mode
	 */
	protected DecimalArithmetic getRoundingUnnecessaryArithmetic() {
		return getScaleMetrics().getRoundingUnnecessaryArithmetic();
	}

	/**
	 * Returns the arithmetic performing unchecked operations with the specified
	 * {@link RoundingMode}.
	 * 
	 * @param roundingMode
	 *            the rounding for the returned arithmetic
	 * @return arithmetic with specified {@code roundingMode} and
	 *         {@link OverflowMode#UNCHECKED UNCHECKED} overflow mode
	 */
	protected DecimalArithmetic getArithmeticFor(RoundingMode roundingMode) {
		return getScaleMetrics().getArithmetic(roundingMode);
	}

	/**
	 * Returns the arithmetic performing checked operations with the specified
	 * {@link RoundingMode}.
	 * 
	 * @param roundingMode
	 *            the rounding for the returned arithmetic
	 * @return arithmetic with specified {@code roundingMode} and
	 *         {@link OverflowMode#CHECKED CHECKED} overflow mode
	 */
	protected DecimalArithmetic getCheckedArithmeticFor(RoundingMode roundingMode) {
		return getScaleMetrics().getCheckedArithmetic(roundingMode);
	}

	/**
	 * Returns the arithmetic for the specified {@code truncationPolicy}.
	 * 
	 * @param truncationPolicy
	 *            the truncation policy for the returned arithmetic
	 * @return arithmetic performing operations according to the given
	 *         {@code truncationPolicy}
	 */
	protected DecimalArithmetic getArithmeticFor(TruncationPolicy truncationPolicy) {
		return getScaleMetrics().getArithmetic(truncationPolicy);
	}

	/* -------------------- Number and simular conversion ------------------- */

	@Override
	public byte byteValueExact() {
		final long num = longValueExact(); // will check decimal part
		if ((byte) num != num) {
			throw new ArithmeticException("Overflow: " + num + " is out of the possible range for a byte");
		}
		return (byte) num;
	}

	@Override
	public short shortValueExact() {
		final long num = longValueExact(); // will check decimal part
		if ((short) num != num) {
			throw new ArithmeticException("Overflow: " + num + " is out of the possible range for a short");
		}
		return (short) num;
	}

	@Override
	public int intValue() {
		return (int) longValue();
	}

	@Override
	public int intValueExact() {
		final long num = longValueExact(); // will check decimal part
		if ((int) num != num) {
			throw new ArithmeticException("Overflow: " + num + " is out of the possible range for an int");
		}
		return (int) num;
	}

	@Override
	public long longValue() {
		return getRoundingDownArithmetic().toLong(unscaledValue());
	}

	@Override
	public long longValueExact() {
		return getRoundingUnnecessaryArithmetic().toLong(unscaledValue());
	}

	@Override
	public long longValue(RoundingMode roundingMode) {
		return getArithmeticFor(roundingMode).toLong(unscaledValue());
	}

	@Override
	public float floatValue() {
		// NOTE: Must be HALF_EVEN rounding mode according to The Java Language
		// Specification
		// @see section 5.1.3 narrowing primitive conversion
		// @see section 4.2.3. Floating-Point Types, Formats, and Values
		// @see IEEE 754-1985 Standard for Binary Floating-Point Arithmetic
		return getRoundingHalfEvenArithmetic().toFloat(unscaledValue());
	}

	@Override
	public float floatValue(RoundingMode roundingMode) {
		return getArithmeticFor(roundingMode).toFloat(unscaledValue());
	}

	@Override
	public double doubleValue() {
		// NOTE: Must be HALF_EVEN rounding mode according to The Java Language
		// Specification
		// @see section 5.1.3 narrowing primitive conversion
		// @see section 4.2.3. Floating-Point Types, Formats, and Values
		// @see IEEE 754-1985 Standard for Binary Floating-Point Arithmetic
		return getRoundingHalfEvenArithmetic().toDouble(unscaledValue());
	}

	@Override
	public double doubleValue(RoundingMode roundingMode) {
		return getArithmeticFor(roundingMode).toDouble(unscaledValue());
	}

	@Override
	public BigInteger toBigInteger() {
		return BigInteger.valueOf(longValue());
	}

	@Override
	public BigInteger toBigIntegerExact() {
		return BigInteger.valueOf(longValueExact());
	}

	@Override
	public BigInteger toBigInteger(RoundingMode roundingMode) {
		return BigInteger.valueOf(longValue(roundingMode));
	}

	@Override
	public BigDecimal toBigDecimal() {
		return getDefaultArithmetic().toBigDecimal(unscaledValue());
	}

	@Override
	public BigDecimal toBigDecimal(int scale, RoundingMode roundingMode) {
		return getArithmeticFor(roundingMode).toBigDecimal(unscaledValue(), scale);
	}

	@Override
	public D integralPart() {
		final long unscaled = unscaledValue();
		final long integral = unscaled - getScaleMetrics().moduloByScaleFactor(unscaled);
		return createOrAssign(integral);
	}

	@Override
	public D fractionalPart() {
		return createOrAssign(getScaleMetrics().moduloByScaleFactor(unscaledValue()));
	}

	/* ----------------------------- rounding ------------------------------ */
	@Override
	public D round(int precision) {
		if (precision < getScale()) {
			return createOrAssign(getDefaultArithmetic().round(unscaledValue(), precision));
		}
		return self();
	}

	@Override
	public D round(int precision, RoundingMode roundingMode) {
		if (precision < getScale()) {
			return createOrAssign(getArithmeticFor(roundingMode).round(unscaledValue(), precision));
		}
		return self();
	}

	@Override
	public D round(int precision, TruncationPolicy truncationPolicy) {
		if (precision < getScale()) {
			return createOrAssign(getArithmeticFor(truncationPolicy).round(unscaledValue(), precision));
		}
		return self();
	}

	/* -------------------------------- add -------------------------------- */

	@Override
	public D add(Decimal<S> augend) {
		return addUnscaled(augend.unscaledValue());
	}

	@Override
	public D add(Decimal<S> augend, OverflowMode overflowMode) {
		return addUnscaled(augend.unscaledValue(), overflowMode);
	}

	@Override
	public D add(Decimal<?> augend, RoundingMode roundingMode) {
		return addUnscaled(augend.unscaledValue(), augend.getScale(), roundingMode);
	}

	@Override
	public D add(Decimal<?> augend, TruncationPolicy truncationPolicy) {
		return addUnscaled(augend.unscaledValue(), augend.getScale(), truncationPolicy);
	}

	@Override
	public D add(long augend) {
		return createOrAssign(getDefaultArithmetic().addLong(unscaledValue(), augend));
	}

	@Override
	public D add(long augend, OverflowMode overflowMode) {
		return createOrAssign(getArithmeticFor(overflowMode).addLong(unscaledValue(), augend));
	}

	@Override
	public D add(double augend) {
		final DecimalArithmetic arith = getDefaultCheckedArithmetic();
		return createOrAssign(arith.add(unscaledValue(), arith.fromDouble(augend)));
	}

	@Override
	public D add(double augend, RoundingMode roundingMode) {
		final DecimalArithmetic arith = getCheckedArithmeticFor(roundingMode);
		return createOrAssign(arith.add(unscaledValue(), arith.fromDouble(augend)));
	}

	@Override
	public D addUnscaled(long unscaledAugend) {
		final DecimalArithmetic arith = getDefaultArithmetic();
		return createOrAssign(arith.add(unscaledValue(), unscaledAugend));
	}

	@Override
	public D addUnscaled(long unscaledAugend, OverflowMode overflowMode) {
		final DecimalArithmetic arith = getArithmeticFor(overflowMode);
		return createOrAssign(arith.add(unscaledValue(), unscaledAugend));
	}

	@Override
	public D addUnscaled(long unscaledAugend, int scale) {
		final DecimalArithmetic arith = getDefaultArithmetic();
		return createOrAssign(arith.add(unscaledValue(), arith.fromUnscaled(unscaledAugend, scale)));
	}

	@Override
	public D addUnscaled(long unscaledAugend, int scale, RoundingMode roundingMode) {
		final DecimalArithmetic arith = getArithmeticFor(roundingMode);
		return createOrAssign(arith.add(unscaledValue(), arith.fromUnscaled(unscaledAugend, scale)));
	}

	@Override
	public D addUnscaled(long unscaledAugend, int scale, TruncationPolicy truncationPolicy) {
		final DecimalArithmetic arith = getArithmeticFor(truncationPolicy);
		return createOrAssign(arith.add(unscaledValue(), arith.fromUnscaled(unscaledAugend, scale)));
	}

	@Override
	public D addSquared(Decimal<S> value) {
		final DecimalArithmetic arith = getDefaultArithmetic();
		return createOrAssign(arith.add(unscaledValue(), arith.square(value.unscaledValue())));
	}

	@Override
	public D addSquared(Decimal<S> value, RoundingMode roundingMode) {
		final DecimalArithmetic arith = getArithmeticFor(roundingMode);
		return createOrAssign(arith.add(unscaledValue(), arith.square(value.unscaledValue())));
	}

	@Override
	public D addSquared(Decimal<S> value, TruncationPolicy truncationPolicy) {
		final DecimalArithmetic arith = getArithmeticFor(truncationPolicy);
		return createOrAssign(arith.add(unscaledValue(), arith.square(value.unscaledValue())));
	}

	/* ------------------------------ subtract ------------------------------ */

	@Override
	public D subtract(Decimal<S> subtrahend) {
		return subtractUnscaled(subtrahend.unscaledValue());
	}

	@Override
	public D subtract(Decimal<S> subtrahend, OverflowMode overflowMode) {
		return subtractUnscaled(subtrahend.unscaledValue(), overflowMode);
	}

	@Override
	public D subtract(Decimal<?> subtrahend, RoundingMode roundingMode) {
		return subtractUnscaled(subtrahend.unscaledValue(), subtrahend.getScale(), roundingMode);
	}

	@Override
	public D subtract(Decimal<?> subtrahend, TruncationPolicy truncationPolicy) {
		return subtractUnscaled(subtrahend.unscaledValue(), subtrahend.getScale(), truncationPolicy);
	}

	@Override
	public D subtract(long subtrahend) {
		return createOrAssign(getDefaultArithmetic().subtractLong(unscaledValue(), subtrahend));
	}

	@Override
	public D subtract(long subtrahend, OverflowMode overflowMode) {
		return createOrAssign(getArithmeticFor(overflowMode).subtractLong(unscaledValue(), subtrahend));
	}

	@Override
	public D subtract(double subtrahend) {
		final DecimalArithmetic arith = getDefaultCheckedArithmetic();
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromDouble(subtrahend)));
	}

	@Override
	public D subtract(double subtrahend, RoundingMode roundingMode) {
		final DecimalArithmetic arith = getCheckedArithmeticFor(roundingMode);
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromDouble(subtrahend)));
	}

	@Override
	public D subtractUnscaled(long unscaledSubtrahend) {
		final DecimalArithmetic arith = getDefaultArithmetic();
		return createOrAssign(arith.subtract(unscaledValue(), unscaledSubtrahend));
	}

	@Override
	public D subtractUnscaled(long unscaledSubtrahend, OverflowMode overflowMode) {
		final DecimalArithmetic arith = getArithmeticFor(overflowMode);
		return createOrAssign(arith.subtract(unscaledValue(), unscaledSubtrahend));
	}

	@Override
	public D subtractUnscaled(long unscaledSubtrahend, int scale) {
		final DecimalArithmetic arith = getDefaultArithmetic();
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromUnscaled(unscaledSubtrahend, scale)));
	}

	@Override
	public D subtractUnscaled(long unscaledSubtrahend, int scale, RoundingMode roundingMode) {
		final DecimalArithmetic arith = getArithmeticFor(roundingMode);
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromUnscaled(unscaledSubtrahend, scale)));
	}

	@Override
	public D subtractUnscaled(long unscaledSubtrahend, int scale, TruncationPolicy truncationPolicy) {
		final DecimalArithmetic arith = getArithmeticFor(truncationPolicy);
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromUnscaled(unscaledSubtrahend, scale)));
	}

	@Override
	public D subtractSquared(Decimal<S> value) {
		final DecimalArithmetic arith = getDefaultArithmetic();
		return createOrAssign(arith.subtract(unscaledValue(), arith.square(value.unscaledValue())));
	}

	@Override
	public D subtractSquared(Decimal<S> value, RoundingMode roundingMode) {
		final DecimalArithmetic arith = getArithmeticFor(roundingMode);
		return createOrAssign(arith.subtract(unscaledValue(), arith.square(value.unscaledValue())));
	}

	@Override
	public D subtractSquared(Decimal<S> value, TruncationPolicy truncationPolicy) {
		final DecimalArithmetic arith = getArithmeticFor(truncationPolicy);
		return createOrAssign(arith.subtract(unscaledValue(), arith.square(value.unscaledValue())));
	}

	/* ------------------------------ multiply ------------------------------ */

	@Override
	public D multiply(Decimal<S> multiplicand) {
		return multiplyUnscaled(multiplicand.unscaledValue());
	}

	@Override
	public D multiply(Decimal<S> multiplicand, RoundingMode roundingMode) {
		return multiplyUnscaled(multiplicand.unscaledValue(), roundingMode);
	}

	@Override
	public D multiply(Decimal<S> multiplicand, TruncationPolicy truncationPolicy) {
		return multiplyUnscaled(multiplicand.unscaledValue(), truncationPolicy);
	}

	@Override
	public D multiplyBy(Decimal<?> multiplicand) {
		return multiplyUnscaled(multiplicand.unscaledValue(), multiplicand.getScale());
	}

	@Override
	public D multiplyBy(Decimal<?> multiplicand, RoundingMode roundingMode) {
		return multiplyUnscaled(multiplicand.unscaledValue(), multiplicand.getScale(), roundingMode);
	}

	@Override
	public D multiplyBy(Decimal<?> multiplicand, TruncationPolicy truncationPolicy) {
		return multiplyUnscaled(multiplicand.unscaledValue(), multiplicand.getScale(), truncationPolicy);
	}

	@Override
	public D multiply(long multiplicand) {
		final DecimalArithmetic arith = getDefaultArithmetic();
		return createOrAssign(arith.multiplyByLong(unscaledValue(), multiplicand));
	}

	@Override
	public D multiply(long multiplicand, OverflowMode overflowMode) {
		final DecimalArithmetic arith = getArithmeticFor(overflowMode);
		return createOrAssign(arith.multiplyByLong(unscaledValue(), multiplicand));
	}

	@Override
	public D multiply(double multiplicand) {
		final DecimalArithmetic arith = getDefaultCheckedArithmetic();
		return createOrAssign(arith.multiply(unscaledValue(), arith.fromDouble(multiplicand)));
	}

	@Override
	public D multiply(double multiplicand, RoundingMode roundingMode) {
		final DecimalArithmetic arith = getCheckedArithmeticFor(roundingMode);
		return createOrAssign(arith.multiply(unscaledValue(), arith.fromDouble(multiplicand)));
	}

	@Override
	public D multiplyUnscaled(long unscaledMultiplicand) {
		final DecimalArithmetic arith = getDefaultArithmetic();
		return createOrAssign(arith.multiply(unscaledValue(), unscaledMultiplicand));
	}

	@Override
	public D multiplyUnscaled(long unscaledMultiplicand, RoundingMode roundingMode) {
		final DecimalArithmetic arith = getArithmeticFor(roundingMode);
		return createOrAssign(arith.multiply(unscaledValue(), unscaledMultiplicand));
	}

	@Override
	public D multiplyUnscaled(long unscaledMultiplicand, TruncationPolicy truncationPolicy) {
		final DecimalArithmetic arith = getArithmeticFor(truncationPolicy);
		return createOrAssign(arith.multiply(unscaledValue(), unscaledMultiplicand));
	}

	@Override
	public D multiplyUnscaled(long unscaledMultiplicand, int scale) {
		final DecimalArithmetic arith = getDefaultArithmetic();
		return createOrAssign(arith.multiply(unscaledValue(), arith.fromUnscaled(unscaledMultiplicand, scale)));
	}

	@Override
	public D multiplyUnscaled(long unscaledMultiplicand, int scale, RoundingMode roundingMode) {
		final DecimalArithmetic arith = getArithmeticFor(roundingMode);
		return createOrAssign(arith.multiply(unscaledValue(), arith.fromUnscaled(unscaledMultiplicand, scale)));
	}

	@Override
	public D multiplyUnscaled(long unscaledMultiplicand, int scale, TruncationPolicy truncationPolicy) {
		final DecimalArithmetic arith = getArithmeticFor(truncationPolicy);
		return createOrAssign(arith.multiply(unscaledValue(), arith.fromUnscaled(unscaledMultiplicand, scale)));
	}

	@Override
	public D multiplyByPowerOfTen(int n) {
		return createOrAssign(getDefaultArithmetic().multiplyByPowerOf10(unscaledValue(), n));
	}

	@Override
	public D multiplyByPowerOfTen(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticFor(roundingMode).multiplyByPowerOf10(unscaledValue(), n));
	}

	@Override
	public D multiplyByPowerOfTen(int n, TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticFor(truncationPolicy).multiplyByPowerOf10(unscaledValue(), n));
	}

	/* ------------------------------ divide ------------------------------ */

	@Override
	public D divide(Decimal<S> divisor) {
		return divideUnscaled(divisor.unscaledValue());
	}

	@Override
	public D divide(Decimal<S> divisor, RoundingMode roundingMode) {
		return divideUnscaled(divisor.unscaledValue(), roundingMode);
	}

	@Override
	public D divide(Decimal<S> divisor, TruncationPolicy truncationPolicy) {
		return divideUnscaled(divisor.unscaledValue(), truncationPolicy);
	}

	@Override
	public D divideBy(Decimal<?> divisor) {
		return divideUnscaled(divisor.unscaledValue(), divisor.getScale());
	}

	@Override
	public D divideBy(Decimal<?> divisor, RoundingMode roundingMode) {
		return divideUnscaled(divisor.unscaledValue(), divisor.getScale(), roundingMode);
	}

	@Override
	public D divideBy(Decimal<?> divisor, TruncationPolicy truncationPolicy) {
		return divideUnscaled(divisor.unscaledValue(), divisor.getScale(), truncationPolicy);
	}

	@Override
	public D divide(long divisor) {
		final DecimalArithmetic arith = getDefaultArithmetic();
		return createOrAssign(arith.divideByLong(unscaledValue(), divisor));
	}

	@Override
	public D divide(long divisor, RoundingMode roundingMode) {
		final DecimalArithmetic arith = getArithmeticFor(roundingMode);
		return createOrAssign(arith.divideByLong(unscaledValue(), divisor));
	}

	@Override
	public D divide(long divisor, TruncationPolicy truncationPolicy) {
		final DecimalArithmetic arith = getArithmeticFor(truncationPolicy);
		return createOrAssign(arith.divideByLong(unscaledValue(), divisor));
	}

	@Override
	public D divide(double divisor) {
		final DecimalArithmetic arith = getDefaultCheckedArithmetic();
		return createOrAssign(arith.divide(unscaledValue(), arith.fromDouble(divisor)));
	}

	@Override
	public D divide(double divisor, RoundingMode roundingMode) {
		final DecimalArithmetic arith = getCheckedArithmeticFor(roundingMode);
		return createOrAssign(arith.divide(unscaledValue(), arith.fromDouble(divisor)));
	}

	@Override
	public D divideUnscaled(long unscaledDivisor) {
		final DecimalArithmetic arith = getDefaultArithmetic();
		return createOrAssign(arith.divide(unscaledValue(), unscaledDivisor));
	}

	@Override
	public D divideUnscaled(long unscaledDivisor, RoundingMode roundingMode) {
		final DecimalArithmetic arith = getArithmeticFor(roundingMode);
		return createOrAssign(arith.divide(unscaledValue(), unscaledDivisor));
	}

	@Override
	public D divideUnscaled(long unscaledDivisor, TruncationPolicy truncationPolicy) {
		final DecimalArithmetic arith = getArithmeticFor(truncationPolicy);
		return createOrAssign(arith.divide(unscaledValue(), unscaledDivisor));
	}

	@Override
	public D divideUnscaled(long unscaledDivisor, int scale) {
		final DecimalArithmetic arith = getDefaultArithmetic();
		return createOrAssign(arith.divide(unscaledValue(), arith.fromUnscaled(unscaledDivisor, scale)));
	}

	@Override
	public D divideUnscaled(long unscaledDivisor, int scale, RoundingMode roundingMode) {
		final DecimalArithmetic arith = getArithmeticFor(roundingMode);
		return createOrAssign(arith.divide(unscaledValue(), arith.fromUnscaled(unscaledDivisor, scale)));
	}

	@Override
	public D divideUnscaled(long unscaledDivisor, int scale, TruncationPolicy truncationPolicy) {
		final DecimalArithmetic arith = getArithmeticFor(truncationPolicy);
		return createOrAssign(arith.divide(unscaledValue(), arith.fromUnscaled(unscaledDivisor, scale)));
	}

	@Override
	public D divideExact(Decimal<S> divisor) {
		return divide(divisor, DecimalRounding.UNNECESSARY.getCheckedTruncationPolicy());
	}

	@Override
	public D divideTruncate(Decimal<S> divisor) {
		return createOrAssign(getRoundingDownArithmetic().divide(unscaledValue(), divisor.unscaledValue()));
	}

	@Override
	public D divideByPowerOfTen(int n) {
		return createOrAssign(getDefaultArithmetic().divideByPowerOf10(unscaledValue(), n));
	}

	@Override
	public D divideByPowerOfTen(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticFor(roundingMode).divideByPowerOf10(unscaledValue(), n));
	}

	@Override
	public D divideByPowerOfTen(int n, TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticFor(truncationPolicy).divideByPowerOf10(unscaledValue(), n));
	}

	@Override
	public D divideToIntegralValue(Decimal<S> divisor) {
		final long longValue = unscaledValue() / divisor.unscaledValue();
		return createOrAssign(getDefaultArithmetic().fromLong(longValue));
	}

	@Override
	public D divideToIntegralValue(Decimal<S> divisor, OverflowMode overflowMode) {
		final long longValue = divideToLongValue(divisor, overflowMode);
		return createOrAssign(getArithmeticFor(overflowMode).fromLong(longValue));
	}

	@Override
	public long divideToLongValue(Decimal<S> divisor) {
		return unscaledValue() / divisor.unscaledValue();
	}

	@Override
	public long divideToLongValue(Decimal<S> divisor, OverflowMode overflowMode) {
		final DecimalArithmetic arith = getArithmeticFor(overflowMode.getTruncationPolicyFor(RoundingMode.DOWN));
		try {
			return arith.divideByLong(unscaledValue(), divisor.unscaledValue());
		} catch (ArithmeticException e) {
			if (divisor.isZero()) {
				throw new ArithmeticException("Division by zero: integral(" + this + " / " + divisor + ")");
			}
			throw new ArithmeticException("Overflow: integral(" + this + " / " + divisor + ")");
		}
	}

	@Override
	public D[] divideAndRemainder(Decimal<S> divisor) {
		final long uDividend = unscaledValue();
		final long uDivisor = divisor.unscaledValue();
		final long lIntegral = uDividend / uDivisor;
		final long uIntegral = getScaleMetrics().multiplyByScaleFactor(lIntegral);
		final long uReminder = uDividend - uDivisor * lIntegral;
		final D[] result = createArray(2);
		result[0] = create(uIntegral);
		result[1] = create(uReminder);
		return result;
	}

	@Override
	public D[] divideAndRemainder(Decimal<S> divisor, OverflowMode overflowMode) {
		if (!overflowMode.isChecked()) {
			return divideAndRemainder(divisor);
		}
		try {
			final DecimalArithmetic arith = getArithmeticFor(DecimalRounding.DOWN.getCheckedTruncationPolicy());
			final long uDividend = unscaledValue();
			final long uDivisor = divisor.unscaledValue();
			final long lIntegral = arith.divideByLong(uDividend, uDivisor);
			final long uIntegral = getScaleMetrics().multiplyByScaleFactorExact(lIntegral);
			final long uReminder = uDividend - uDivisor * lIntegral;
			final D[] result = createArray(2);
			result[0] = create(uIntegral);
			result[1] = create(uReminder);
			return result;
		} catch (ArithmeticException e) {
			if (divisor.isZero()) {
				throw new ArithmeticException("Division by zero: integral(" + this + " / " + divisor + ")");
			}
			throw new ArithmeticException("Overflow: integral(" + this + " / " + divisor + ")");
		}
	}

	@Override
	public D remainder(Decimal<S> divisor) {
		return createOrAssign(unscaledValue() % divisor.unscaledValue());
	}

	/* ------------------------- other arithmetic ------------------------- */

	@Override
	public int signum() {
		return Long.signum(unscaledValue());
	}

	@Override
	public D negate() {
		return createOrAssign(getDefaultArithmetic().negate(unscaledValue()));
	}

	@Override
	public D negate(OverflowMode overflowMode) {
		return createOrAssign(getArithmeticFor(overflowMode).negate(unscaledValue()));
	}

	@Override
	public D abs() {
		final long unscaled = unscaledValue();
		return unscaled >= 0 ? self() : createOrAssign(getDefaultArithmetic().negate(unscaled));
	}

	@Override
	public D abs(OverflowMode overflowMode) {
		final long unscaled = unscaledValue();
		return unscaled >= 0 ? self() : createOrAssign(getArithmeticFor(overflowMode).negate(unscaled));
	}

	@Override
	public D invert() {
		return createOrAssign(getDefaultArithmetic().invert(unscaledValue()));
	}

	@Override
	public D invert(RoundingMode roundingMode) {
		return createOrAssign(getArithmeticFor(roundingMode).invert(unscaledValue()));
	}

	@Override
	public D invert(TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticFor(truncationPolicy).invert(unscaledValue()));
	}

	@Override
	public D square() {
		return createOrAssign(getDefaultArithmetic().square(unscaledValue()));
	}

	@Override
	public D square(RoundingMode roundingMode) {
		return createOrAssign(getArithmeticFor(roundingMode).square(unscaledValue()));
	}

	@Override
	public D square(TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticFor(truncationPolicy).square(unscaledValue()));
	}

	@Override
	public D sqrt() {
		return createOrAssign(getDefaultArithmetic().sqrt(unscaledValue()));
	}

	@Override
	public D sqrt(RoundingMode roundingMode) {
		return createOrAssign(getArithmeticFor(roundingMode).sqrt(unscaledValue()));
	}

	@Override
	public D shiftLeft(int n) {
		// NOTE: FLOOR is default for shift!
		return createOrAssign(getRoundingFloorArithmetic().shiftLeft(unscaledValue(), n));
	}

	@Override
	public D shiftLeft(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticFor(roundingMode).shiftLeft(unscaledValue(), n));
	}

	@Override
	public D shiftLeft(int n, TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticFor(truncationPolicy).shiftLeft(unscaledValue(), n));
	}

	@Override
	public D shiftRight(int n) {
		// NOTE: FLOOR is default for shift!
		return createOrAssign(getRoundingFloorArithmetic().shiftRight(unscaledValue(), n));
	}

	@Override
	public D shiftRight(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticFor(roundingMode).shiftRight(unscaledValue(), n));
	}

	@Override
	public D shiftRight(int n, TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticFor(truncationPolicy).shiftRight(unscaledValue(), n));
	}

	@Override
	public D pow(int n) {
		return createOrAssign(getDefaultArithmetic().pow(unscaledValue(), n));
	}

	@Override
	public D pow(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticFor(roundingMode).pow(unscaledValue(), n));
	}

	@Override
	public D pow(int n, TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticFor(truncationPolicy).pow(unscaledValue(), n));
	}

	/* --------------------------- compare etc. ---------------------------- */

	@Override
	public int compareTo(Decimal<S> other) {
		return getDefaultArithmetic().compare(unscaledValue(), other.unscaledValue());
	}

	@Override
	public boolean isEqualTo(Decimal<S> other) {
		return compareTo(other) == 0;
	}

	@Override
	public boolean isGreaterThan(Decimal<S> other) {
		return compareTo(other) > 0;
	}

	@Override
	public boolean isGreaterThanOrEqualTo(Decimal<S> other) {
		return compareTo(other) >= 0;
	}

	@Override
	public boolean isLessThan(Decimal<S> other) {
		return compareTo(other) < 0;
	}

	@Override
	public boolean isLessThanOrEqualTo(Decimal<S> other) {
		return compareTo(other) <= 0;
	}

	@Override
	public boolean isZero() {
		return unscaledValue() == 0;
	}

	@Override
	public boolean isOne() {
		return unscaledValue() == getScaleMetrics().getScaleFactor();
	}

	@Override
	public boolean isUlp() {
		return unscaledValue() == 1;
	}

	@Override
	public boolean isMinusOne() {
		return unscaledValue() == -getScaleMetrics().getScaleFactor();
	}

	@Override
	public boolean isPositive() {
		return unscaledValue() > 0;
	}

	@Override
	public boolean isNonNegative() {
		return unscaledValue() >= 0;
	}

	@Override
	public boolean isNegative() {
		return unscaledValue() < 0;
	}

	@Override
	public boolean isNonPositive() {
		return unscaledValue() <= 0;
	}

	@Override
	public boolean isIntegral() {
		return getScaleMetrics().moduloByScaleFactor(unscaledValue()) == 0;
	}

	@Override
	public boolean isIntegralPartZero() {
		final long unscaled = unscaledValue();
		final long one = getScaleMetrics().getScaleFactor();
		return one > unscaled & unscaled > -one;
	}

	@Override
	public boolean isBetweenZeroAndOne() {
		final long unscaled = unscaledValue();
		return 0 <= unscaled && unscaled < getScaleMetrics().getScaleFactor();
	}

	@Override
	public boolean isBetweenZeroAndMinusOne() {
		final long unscaled = unscaledValue();
		return 0 >= unscaled && unscaled > -(getScaleMetrics().getScaleFactor());
	}

	@Override
	public int compareToNumerically(Decimal<?> other) {
		final long unscaled = unscaledValue();
		final long otherUnscaled = other.unscaledValue();
		final int scale = getScale();
		final int otherScale = other.getScale();
		if (scale == otherScale) {
			return getDefaultArithmetic().compare(unscaled, otherUnscaled);
		}
		if (scale < otherScale) {
			final DecimalArithmetic arith = getDefaultArithmetic();
			final ScaleMetrics diffMetrics = Scales.getScaleMetrics(otherScale - scale);
			final long otherRescaled = diffMetrics.divideByScaleFactor(otherUnscaled);
			final int cmp = arith.compare(unscaled, otherRescaled);
			if (cmp != 0) {
				return cmp;
			}
			// remainder must be zero for equality
			final long otherRemainder = otherUnscaled - diffMetrics.multiplyByScaleFactor(otherRescaled);
			return -arith.signum(otherRemainder);
		} else {
			final DecimalArithmetic arith = other.getScaleMetrics().getDefaultArithmetic();
			final ScaleMetrics diffMetrics = Scales.getScaleMetrics(scale - otherScale);
			final long rescaled = diffMetrics.divideByScaleFactor(unscaled);
			final int cmp = arith.compare(rescaled, otherUnscaled);
			if (cmp != 0) {
				return cmp;
			}
			// remainder must be zero for equality
			final long remainder = unscaled - diffMetrics.multiplyByScaleFactor(rescaled);
			return arith.signum(remainder);
		}
	}

	@Override
	public boolean isEqualToNumerically(Decimal<?> other) {
		return compareToNumerically(other) == 0;
	}

	@Override
	public Decimal<S> min(Decimal<S> val) {
		return isLessThanOrEqualTo(val) ? this : val;
	}

	@Override
	public Decimal<S> max(Decimal<S> val) {
		return isGreaterThanOrEqualTo(val) ? this : val;
	}

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
	public D min(D val) {
		return isLessThanOrEqualTo(val) ? self() : val;
	}

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
	public D max(D val) {
		return isGreaterThanOrEqualTo(val) ? self() : val;
	}

	@Override
	public D avg(Decimal<S> val) {
		return createOrAssign(getDefaultArithmetic().avg(unscaledValue(), val.unscaledValue()));
	}

	@Override
	public D avg(Decimal<S> val, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticFor(roundingMode).avg(unscaledValue(), val.unscaledValue()));
	}

	/* ---------------------------- equals etc. ---------------------------- */

	@Override
	public int hashCode() {
		final long unscaled = unscaledValue();
		long hash = getScale();
		hash = 31 * hash + (unscaled >>> 32);
		hash = 31 * hash + unscaled;
		return (int) hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Decimal) {
			final Decimal<?> other = (Decimal<?>) obj;
			return unscaledValue() == other.unscaledValue() && getScale() == other.getScale();
		}
		return false;
	}

	@Override
	public String toString() {
		return getDefaultArithmetic().toString(unscaledValue());
	}
}
