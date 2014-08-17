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
 *            the scale subclass type associated with this decimal
 */
@SuppressWarnings("serial")
abstract public class AbstractMutableDecimal<S extends Scale> extends
		AbstractDecimal<S> {

	private long unscaled;

	/**
	 * Constructor with specified scale using the given {@code arithmetics}.
	 * 
	 * @param unscaled
	 *            the unscaled decimal value
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param arithmetics
	 *            the arithmetics used for operations with decimals
	 * @throws IllegalArgumentException
	 *             if {@code scale} is not consistent with the scale used by the
	 *             specified {@code arithmetics} argument
	 */
	public AbstractMutableDecimal(long unscaled, S scale, DecimalArithmetics arithmetics) {
		super(scale, arithmetics);
		this.unscaled = unscaled;
	}

	/**
	 * Resets this mutable decimal to the value zero.
	 * 
	 * @return {@code this} decimal after resetting the value to {@code 0}
	 */
	public AbstractMutableDecimal<S> reset() {
		unscaled = 0;
		return this;
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
	public AbstractMutableDecimal<S> add(Decimal<S> augend) {
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
	public AbstractMutableDecimal<S> addDecimal(Decimal<?> augend) {
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
	public AbstractMutableDecimal<S> addDecimal(Decimal<?> augend, RoundingMode roundingMode) {
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
	public AbstractMutableDecimal<S> add(long augend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.fromLong(augend));
		return this;
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
	public AbstractMutableDecimal<S> add(double augend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.fromDouble(augend));
		return this;
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
	public AbstractMutableDecimal<S> add(double augend, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.derive(roundingMode).fromDouble(augend));
		return this;
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
	public AbstractMutableDecimal<S> add(BigInteger augend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.fromBigInteger(augend));
		return this;
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
	public AbstractMutableDecimal<S> add(BigDecimal augend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.fromBigDecimal(augend));
		return this;
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
	public AbstractMutableDecimal<S> add(BigDecimal augend, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.derive(roundingMode).fromBigDecimal(augend));
		return this;
	}

	public AbstractMutableDecimal<S> add(long unscaledAugend, int scale) {
		final DecimalArithmetics arith = getArithmetics();
		unscaledAugend = arith.add(unscaled, arith.fromUnscaled(unscaledAugend, scale));
		return this;
	}

	public AbstractMutableDecimal<S> add(long unscaledAugend, int scale, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		unscaledAugend = arith.add(unscaled, arith.derive(roundingMode).fromUnscaled(unscaledAugend, scale));
		return this;
	}

	public AbstractMutableDecimal<S> addUnscaled(long unscaledAugend) {
		unscaled = getArithmetics().add(unscaled, unscaledAugend);
		return this;
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
	public AbstractMutableDecimal<S> subtract(Decimal<S> subtrahend) {
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
	public AbstractMutableDecimal<S> subtract(long subtrahend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.fromLong(subtrahend));
		return this;
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
	public AbstractMutableDecimal<S> subtract(double subtrahend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.fromDouble(subtrahend));
		return this;
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
	public AbstractMutableDecimal<S> subtract(double subtrahend, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.derive(roundingMode).fromDouble(subtrahend));
		return this;
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
	public AbstractMutableDecimal<S> subtract(BigInteger subtrahend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.fromBigInteger(subtrahend));
		return this;
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
	public AbstractMutableDecimal<S> subtract(BigDecimal subtrahend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.fromBigDecimal(subtrahend));
		return this;
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
	public AbstractMutableDecimal<S> subtract(BigDecimal subtrahend, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.derive(roundingMode).fromBigDecimal(subtrahend));
		return this;
	}

	public AbstractMutableDecimal<S> subtract(long unscaledSubtrahend, int scale) {
		final DecimalArithmetics arith = getArithmetics();
		unscaledSubtrahend = arith.subtract(unscaled, arith.fromUnscaled(unscaledSubtrahend, scale));
		return this;
	}

	public AbstractMutableDecimal<S> subtract(long unscaledSubtrahend, int scale, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		unscaledSubtrahend = arith.subtract(unscaled, arith.derive(roundingMode).fromUnscaled(unscaledSubtrahend, scale));
		return this;
	}

	public AbstractMutableDecimal<S> subtractUnscaled(long unscaledSubtrahend) {
		unscaled = getArithmetics().subtract(unscaled, unscaledSubtrahend);
		return this;
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
	public AbstractMutableDecimal<S> multiply(Decimal<S> multiplicand) {
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
	public Decimal<S> multiply(Decimal<S> multiplicand, RoundingMode roundingMode) {
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
	public AbstractMutableDecimal<S> multiply(long multiplicand) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.multiply(unscaled, arith.fromLong(multiplicand));
		return this;
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
	public AbstractMutableDecimal<S> multiply(double multiplicand) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.multiply(unscaled, arith.fromDouble(multiplicand));
		return this;
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
	public AbstractMutableDecimal<S> multiply(double multiplicand, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.multiply(unscaled, arith.fromDouble(multiplicand));
		return this;
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
	public AbstractMutableDecimal<S> multiply(BigInteger multiplicand) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.multiply(unscaled, arith.fromBigInteger(multiplicand));
		return this;
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
	public AbstractMutableDecimal<S> multiply(BigDecimal multiplicand) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.multiply(unscaled, arith.fromBigDecimal(multiplicand));
		return this;
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
	public AbstractMutableDecimal<S> multiply(BigDecimal multiplicand, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.multiply(unscaled, arith.fromBigDecimal(multiplicand));
		return this;
	}

	public AbstractMutableDecimal<S> multiply(long unscaledMultiplicand, int scale) {
		final DecimalArithmetics arith = getArithmetics();
		unscaledMultiplicand = arith.multiply(unscaled, arith.fromUnscaled(unscaledMultiplicand, scale));
		return this;
	}

	public AbstractMutableDecimal<S> multiply(long unscaledMultiplicand, int scale, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaledMultiplicand = arith.multiply(unscaled, arith.fromUnscaled(unscaledMultiplicand, scale));
		return this;
	}

	public AbstractMutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand) {
		unscaled = getArithmetics().multiply(unscaled, unscaledMultiplicand);
		return this;
	}

	public AbstractMutableDecimal<S> multiplyUnscaled(long unscaledMultiplicand, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).multiply(unscaled, unscaledMultiplicand);
		return this;
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
	public AbstractMutableDecimal<S> divide(Decimal<S> divisor) {
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
	public Decimal<S> divide(Decimal<S> divisor, RoundingMode roundingMode) {
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
	public AbstractMutableDecimal<S> divide(long divisor) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.divide(unscaled, arith.fromLong(divisor));
		return this;
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
	public AbstractMutableDecimal<S> divide(long divisor, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.divide(unscaled, arith.fromLong(divisor));
		return this;
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
	public AbstractMutableDecimal<S> divide(double divisor) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.divide(unscaled, arith.fromDouble(divisor));
		return this;
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
	public AbstractMutableDecimal<S> divide(double divisor, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.divide(unscaled, arith.fromDouble(divisor));
		return this;
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
	public AbstractMutableDecimal<S> divide(BigInteger divisor) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.divide(unscaled, arith.fromBigInteger(divisor));
		return this;
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
	public AbstractMutableDecimal<S> divide(BigInteger divisor, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.divide(unscaled, arith.fromBigInteger(divisor));
		return this;
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
	public AbstractMutableDecimal<S> divide(BigDecimal divisor) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.divide(unscaled, arith.fromBigDecimal(divisor));
		return this;
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
	public AbstractMutableDecimal<S> divide(BigDecimal divisor, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaled = arith.divide(unscaled, arith.fromBigDecimal(divisor));
		return this;
	}

	public AbstractMutableDecimal<S> divide(long unscaledDivisor, int scale) {
		final DecimalArithmetics arith = getArithmetics();
		unscaledDivisor = arith.divide(unscaled, arith.fromUnscaled(unscaledDivisor, scale));
		return this;
	}

	public AbstractMutableDecimal<S> divide(long unscaledDivisor, int scale, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics().derive(roundingMode);
		unscaledDivisor = arith.divide(unscaled, arith.fromUnscaled(unscaledDivisor, scale));
		return this;
	}

	public AbstractMutableDecimal<S> divideUnscaled(long unscaledDivisor) {
		unscaled = getArithmetics().divide(unscaled, unscaledDivisor);
		return this;
	}

	public AbstractMutableDecimal<S> divideUnscaled(long unscaledDivisor, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).divide(unscaled, unscaledDivisor);
		return this;
	}

	/**
	 * Negates {@code this} decimal and returns it.
	 * 
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = -this}
	 */
	@Override
	public AbstractMutableDecimal<S> negate() {
		unscaled = getArithmetics().negate(unscaled);
		return this;
	}

	/**
	 * Returns {@code this} decimal after negating it if negative.
	 * 
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = |this|}
	 */
	@Override
	public AbstractMutableDecimal<S> abs() {
		return unscaledValue() < 0 ? negate() : this;
	}

	/**
	 * Inverts {@code this} decimal and returns it.
	 * 
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = 1/this}
	 */
	@Override
	public AbstractMutableDecimal<S> invert() {
		unscaled = getArithmetics().invert(unscaled);
		return this;
	}

	@Override
	public Decimal<S> invert(RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).invert(unscaled);
		return this;
	}

	@Override
	public AbstractMutableDecimal<S> movePointLeft(int n) {
		unscaled = getArithmetics().movePointLeft(unscaled, n);
		return this;
	}

	@Override
	public AbstractMutableDecimal<S> movePointLeft(int n, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).movePointLeft(unscaled, n);
		return this;
	}

	@Override
	public AbstractMutableDecimal<S> movePointRight(int n) {
		unscaled = getArithmetics().movePointRight(unscaled, n);
		return this;
	}

	@Override
	public AbstractMutableDecimal<S> movePointRight(int n, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).movePointRight(unscaled, n);
		return this;
	}

	@Override
	public AbstractMutableDecimal<S> pow(int n) {
		unscaled = getArithmetics().pow(unscaled, n);
		return this;
	}

	@Override
	public AbstractMutableDecimal<S> pow(int n, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).pow(unscaled, n);
		return this;
	}

	@Override
	public final long unscaledValue() {
		return unscaled;
	}

	abstract public Decimal<S> toImmutableValue();

}
