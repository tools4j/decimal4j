package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

/**
 * Base class for mutable {@link Decimal} classes of different scales.
 * Arithmetic operations of mutable decimals modify the state of {@code this}
 * {@code Decimal} and return {@code this} as result value.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this mutable decimal
 * @param <I>
 *            the concrete class implementing the immutable counterpart of this
 *            mutable decimal
 */
@SuppressWarnings("serial")
abstract public class AbstractMutableDecimal<S extends ScaleMetrics, D extends AbstractMutableDecimal<S, D, I>, I extends AbstractImmutableDecimal<S, I, D>>
		extends AbstractDecimal<S, D> {

	private long unscaled;

	/**
	 * Constructor with specified scale using the given {@code arithmetics}.
	 * 
	 * @param unscaled
	 *            the unscaled decimal value
	 * @param scaleMetrics
	 *            the scale metrics for this decimal number
	 * @param arithmetics
	 *            the arithmetics used for operations with decimals
	 * @throws IllegalArgumentException
	 *             if {@code scale} is not consistent with the scale used by the
	 *             specified {@code arithmetics} argument
	 */
	public AbstractMutableDecimal(long unscaled, S scaleMetrics, DecimalArithmetics arithmetics) {
		super(scaleMetrics, arithmetics);
		this.unscaled = unscaled;
	}

	/**
	 * Creates a new immutable value representing the same decimal as
	 * {@code this} mutable decimal and returns it.
	 * 
	 * @return {@code this} as new immutable decimal value
	 */
	abstract public I toImmutableDecimal();

	@Override
	public AbstractMutableDecimal<?, ?, ?> convert(int scale) {
		if (scale == getScaleMetrics().getScale()) {
			return this;
		}
		final DecimalArithmetics arith = getArithmetics();
		return ScaleMetrics.valueOf(scale).createMutable(arith.getRoundingMode(), arith.getOverflowMode()).setDecimal(this);
	}

	@Override
	public AbstractMutableDecimal<?, ?, ?> convert(int scale, RoundingMode roundingMode) {
		if (scale == getScaleMetrics().getScale()) {
			return this;
		}
		final DecimalArithmetics arith = getArithmetics();
		return ScaleMetrics.valueOf(scale).createMutable(arith.getRoundingMode(), arith.getOverflowMode()).setDecimal(this, roundingMode);
	}

	/**
	 * Sets {@code this} decimal to 0 and returns {@code this} now representing
	 * zero.
	 * 
	 * @return {@code this} decimal after assigning {@code this = 0}
	 */
	public D setZero() {
		unscaled = 0;
		return self();
	}

	/**
	 * Sets {@code this} decimal to 1 and returns {@code this} now representing
	 * one.
	 * 
	 * @return {@code this} decimal after assigning {@code this = 1}
	 */
	public D setOne() {
		unscaled = getArithmetics().one();
		return self();
	}

	/**
	 * Sets {@code this} decimal to 10 and returns {@code this} now representing
	 * ten.
	 * 
	 * @return {@code this} decimal after assigning {@code this = 10}
	 */
	public D setTen() {
		unscaled = 10 * getArithmetics().one();
		return self();
	}

	/**
	 * Sets {@code this} decimal to -1 and returns {@code this} now representing
	 * minus one.
	 * 
	 * @return {@code this} decimal after assigning {@code this = -1}
	 */
	public D setMinusOne() {
		unscaled = -getArithmetics().one();
		return self();
	}

	/**
	 * Sets {@code this} decimal to the smallest positive value representable by
	 * this mutable decimal and returns {@code this} now representing one ULP.
	 * 
	 * @return {@code this} decimal after assigning {@code this = ULP}
	 */
	public D setUlp() {
		unscaled = 1;
		return self();
	}

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	public D set(Decimal<S> value) {
		return setUnscaled(value.unscaledValue());
	}

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	public D setDecimal(Decimal<?> value) {
		return set(value.unscaledValue(), value.getArithmetics().getScale());
	}

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted to the appropriate scale
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	public D setDecimal(Decimal<?> value, RoundingMode roundingMode) {
		return set(value.unscaledValue(), value.getArithmetics().getScale(), roundingMode);
	}

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	public D set(long value) {
		unscaled = getArithmetics().fromLong(value);
		return self();
	}

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	public D set(double value) {
		unscaled = getArithmetics().fromDouble(value);
		return self();
	}

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a decimal number
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	public D set(double value, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).fromDouble(value);
		return self();
	}

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	public D set(BigInteger value) {
		unscaled = getArithmetics().fromBigInteger(value);
		return self();
	}

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	public D set(BigDecimal value) {
		unscaled = getArithmetics().fromBigDecimal(value);
		return self();
	}

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a decimal number
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	public D set(BigDecimal value, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).fromBigDecimal(value);
		return self();
	}

	/**
	 * Sets {@code this} decimal to the specified {@code unscaledValue} with the
	 * given {@code scale} and returns {@code this} now representing
	 * <code>this = unscaledValue * 10<sup>-scale</sup></code>.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @return {@code this} decimal after assigning
	 *         <code>this = unscaledValue * 10<sup>-scale</sup></code>.
	 */
	public D set(long unscaledValue, int scale) {
		unscaled = getArithmetics().fromUnscaled(unscaledValue, scale);
		return self();
	}

	/**
	 * Sets {@code this} decimal to the specified {@code unscaledValue} with the
	 * given {@code scale} and returns {@code this} now representing
	 * <code>this = unscaledValue * 10<sup>-scale</sup></code>.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a decimal number
	 * @return {@code this} decimal after assigning
	 *         <code>this = unscaledValue * 10<sup>-scale</sup></code>.
	 */
	public D set(long unscaledValue, int scale, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).fromUnscaled(unscaledValue, scale);
		return self();
	}

	/**
	 * Sets {@code this} decimal to the specified {@code unscaledValue} and
	 * returns {@code this} now representing
	 * <code>this = unscaledValue * 10<sup>-scale</sup></code> where scale is
	 * the scale factor of this mutable decimal.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @return {@code this} decimal after assigning
	 *         <code>this = unscaledValue * 10<sup>-scale</sup></code>.
	 * @see #getScaleMetrics()
	 * @see ScaleMetrics#getScaleFactor()
	 */
	public D setUnscaled(long unscaledValue) {
		unscaled = unscaledValue;
		return self();
	}

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	@Override
	public D add(Decimal<S> augend) {
		return addUnscaled(augend.unscaledValue());
	}

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	public D addDecimal(Decimal<?> augend) {
		return add(augend.unscaledValue(), augend.getArithmetics().getScale());
	}

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the augend argument needs to be
	 *            truncated when converted to the appropriate scale
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	public D addDecimal(Decimal<?> augend, RoundingMode roundingMode) {
		return add(augend.unscaledValue(), augend.getArithmetics().getScale(), roundingMode);
	}

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	public D add(long augend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.fromLong(augend));
		return self();
	}

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	public D add(double augend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.fromDouble(augend));
		return self();
	}

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the augend argument needs to be
	 *            truncated when converted into a decimal number
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	public D add(double augend, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.derive(roundingMode).fromDouble(augend));
		return self();
	}

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	public D add(BigInteger augend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.fromBigInteger(augend));
		return self();
	}

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	public D add(BigDecimal augend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.fromBigDecimal(augend));
		return self();
	}

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the augend argument needs to be
	 *            truncated when converted into a decimal number
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	public D add(BigDecimal augend, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.derive(roundingMode).fromBigDecimal(augend));
		return self();
	}

	public D add(long unscaledAugend, int scale) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.fromUnscaled(unscaledAugend, scale));
		return self();
	}

	public D add(long unscaledAugend, int scale, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.derive(roundingMode).fromUnscaled(unscaledAugend, scale));
		return self();
	}

	public D addUnscaled(long unscaledAugend) {
		unscaled = getArithmetics().add(unscaled, unscaledAugend);
		return self();
	}

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	@Override
	public D subtract(Decimal<S> subtrahend) {
		return subtractUnscaled(subtrahend.unscaledValue());
	}

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	public D subtract(long subtrahend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.fromLong(subtrahend));
		return self();
	}

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	public D subtract(double subtrahend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.fromDouble(subtrahend));
		return self();
	}

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the subtrahend argument needs to
	 *            be truncated when converted into a decimal number
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	public D subtract(double subtrahend, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.derive(roundingMode).fromDouble(subtrahend));
		return self();
	}

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	public D subtract(BigInteger subtrahend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.fromBigInteger(subtrahend));
		return self();
	}

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	public D subtract(BigDecimal subtrahend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.fromBigDecimal(subtrahend));
		return self();
	}

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the subtrahend argument needs to
	 *            be truncated when converted into a decimal number
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	public D subtract(BigDecimal subtrahend, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.derive(roundingMode).fromBigDecimal(subtrahend));
		return self();
	}

	public D subtract(long unscaledSubtrahend, int scale) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.fromUnscaled(unscaledSubtrahend, scale));
		return self();
	}

	public D subtract(long unscaledSubtrahend, int scale, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.derive(roundingMode).fromUnscaled(unscaledSubtrahend, scale));
		return self();
	}

	public D subtractUnscaled(long unscaledSubtrahend) {
		unscaled = getArithmetics().subtract(unscaled, unscaledSubtrahend);
		return self();
	}

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	@Override
	public D multiply(Decimal<S> multiplicand) {
		return multiplyUnscaled(multiplicand.unscaledValue());
	}

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication, applying the specified rounding mode if rounding is
	 * necessary.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if rounding is necessary
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	@Override
	public D multiply(Decimal<S> multiplicand, RoundingMode roundingMode) {
		return multiplyUnscaled(multiplicand.unscaledValue(), roundingMode);
	}

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	public D multiply(long multiplicand) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.multiply(unscaled, arith.fromLong(multiplicand));
		return self();
	}

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	public D multiply(double multiplicand) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.multiply(unscaled, arith.fromDouble(multiplicand));
		return self();
	}

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if rounding becomes necessary
	 *            either when converting the {@code multiplicand} argument into
	 *            a decimal number or after the multiplication
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	public D multiply(double multiplicand, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.multiply(unscaled, arith.fromDouble(multiplicand));
		return self();
	}

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	public D multiply(BigInteger multiplicand) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.multiply(unscaled, arith.fromBigInteger(multiplicand));
		return self();
	}

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	public D multiply(BigDecimal multiplicand) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.multiply(unscaled, arith.fromBigDecimal(multiplicand));
		return self();
	}

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if rounding becomes necessary
	 *            either when converting the {@code multiplicand} argument into
	 *            a decimal number or after the multiplication
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	public D multiply(BigDecimal multiplicand, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.multiply(unscaled, arith.fromBigDecimal(multiplicand));
		return self();
	}

	public D multiply(long unscaledMultiplicand, int scale) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.multiply(unscaled, arith.fromUnscaled(unscaledMultiplicand, scale));
		return self();
	}

	public D multiply(long unscaledMultiplicand, int scale, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.multiply(unscaled, arith.fromUnscaled(unscaledMultiplicand, scale));
		return self();
	}

	public D multiplyUnscaled(long unscaledMultiplicand) {
		unscaled = getArithmetics().multiply(unscaled, unscaledMultiplicand);
		return self();
	}

	public D multiplyUnscaled(long unscaledMultiplicand, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).multiply(unscaled, unscaledMultiplicand);
		return self();
	}

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	@Override
	public D divide(Decimal<S> divisor) {
		return divideUnscaled(divisor.unscaledValue());
	}

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @param roundingMode
	 *            the rounding mode to apply if rounding is necessary
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	@Override
	public D divide(Decimal<S> divisor, RoundingMode roundingMode) {
		return divideUnscaled(divisor.unscaledValue(), roundingMode);
	}

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	public D divide(long divisor) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.divide(unscaled, arith.fromLong(divisor));
		return self();
	}

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @param roundingMode
	 *            the rounding mode to apply if rounding is necessary
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	public D divide(long divisor, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.divide(unscaled, arith.fromLong(divisor));
		return self();
	}

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	public D divide(double divisor) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.divide(unscaled, arith.fromDouble(divisor));
		return self();
	}

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @param roundingMode
	 *            the rounding mode to apply if rounding becomes necessary
	 *            either when converting the {@code divisor} argument into a
	 *            decimal number or after the multiplication
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	public D divide(double divisor, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.divide(unscaled, arith.fromDouble(divisor));
		return self();
	}

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	public D divide(BigInteger divisor) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.divide(unscaled, arith.fromBigInteger(divisor));
		return self();
	}

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @param roundingMode
	 *            the rounding mode to apply if rounding is necessary
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	public D divide(BigInteger divisor, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.divide(unscaled, arith.fromBigInteger(divisor));
		return self();
	}

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	public D divide(BigDecimal divisor) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.divide(unscaled, arith.fromBigDecimal(divisor));
		return self();
	}

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @param roundingMode
	 *            the rounding mode to apply if rounding becomes necessary
	 *            either when converting the {@code divisor} argument into a
	 *            decimal number or after the multiplication
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	public D divide(BigDecimal divisor, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.divide(unscaled, arith.fromBigDecimal(divisor));
		return self();
	}

	public D divide(long unscaledDivisor, int scale) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.divide(unscaled, arith.fromUnscaled(unscaledDivisor, scale));
		return self();
	}

	public D divide(long unscaledDivisor, int scale, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.divide(unscaled, arith.fromUnscaled(unscaledDivisor, scale));
		return self();
	}

	public D divideUnscaled(long unscaledDivisor) {
		unscaled = getArithmetics().divide(unscaled, unscaledDivisor);
		return self();
	}

	public D divideUnscaled(long unscaledDivisor, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).divide(unscaled, unscaledDivisor);
		return self();
	}

	/**
	 * Negates {@code this} decimal and returns it.
	 * 
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = -this}
	 */
	@Override
	public D negate() {
		unscaled = getArithmetics().negate(unscaled);
		return self();
	}

	/**
	 * Inverts {@code this} decimal and returns it.
	 * 
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = 1/this}
	 */
	@Override
	public D invert() {
		unscaled = getArithmetics().invert(unscaled);
		return self();
	}

	@Override
	public D invert(RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).invert(unscaled);
		return self();
	}

	@Override
	public D movePointLeft(int n) {
		unscaled = getArithmetics().movePointLeft(unscaled, n);
		return self();
	}

	@Override
	public D movePointLeft(int n, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).movePointLeft(unscaled, n);
		return self();
	}

	@Override
	public D movePointRight(int n) {
		unscaled = getArithmetics().movePointRight(unscaled, n);
		return self();
	}

	@Override
	public D movePointRight(int n, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).movePointRight(unscaled, n);
		return self();
	}

	@Override
	public D pow(int n) {
		unscaled = getArithmetics().pow(unscaled, n);
		return self();
	}

	@Override
	public D pow(int n, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).pow(unscaled, n);
		return self();
	}

	@Override
	public final long unscaledValue() {
		return unscaled;
	}

}
