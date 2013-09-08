package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

@SuppressWarnings("serial")
abstract public class AbstractMutableDecimal<S extends Scale> extends AbstractDecimal<S> {
	
	private long unscaled;
	
	public AbstractMutableDecimal(long unscaled, S scale) {
		super(scale);
		this.unscaled = unscaled;
	}
	public AbstractMutableDecimal(long unscaled, S scale, DecimalArithmetics arithmetics) {
		super(scale, arithmetics);
		this.unscaled = unscaled;
	}
	
	/**
	 * Resets this mutable decimal to the value zero.
     * @return {@code this} decimal after resetting the value to {@code 0}
	 */
	public AbstractMutableDecimal<S> reset() {
		unscaled = 0;
		return this;
	}
	
    /**
     * Adds the specified {@code augend} value to {@code this} decimal and 
     * this decimal now containing the value {@code (this +
     * augend)}.
     *
     * @param  augend value to be added to this {@code Decimal}
     * @return {@code this} decimal after addition of the {@code augend} value
     */
	@Override
    public AbstractMutableDecimal<S> add(Decimal<S> augend) {
		return addUnscaled(augend.unscaledValue());
    }
    /**
     * Adds the specified {@code augend} value to {@code this} decimal and 
     * this decimal now containing the value {@code (this +
     * augend)}.
     *
     * @param  augend value to be added to this {@code Decimal}
     * @return {@code this} decimal after addition of the {@code augend} value
     */
    public AbstractMutableDecimal<S> addDecimal(Decimal<?> augend) {
		return add(augend.unscaledValue(), augend.getArithmetics().getScale());
    }
	
	public AbstractMutableDecimal<S> add(long augend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.fromLong(augend));
		return this;
	}
	public AbstractMutableDecimal<S> add(double augend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.fromDouble(augend));
		return this;
	}
	public AbstractMutableDecimal<S> add(BigInteger augend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.fromBigInteger(augend));
		return this;
	}
	public AbstractMutableDecimal<S> add(BigDecimal augend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.add(unscaled, arith.fromBigDecimal(augend));
		return this;
	}
	public AbstractMutableDecimal<S> add(long unscaledAugend, int scale) {
		final DecimalArithmetics arith = getArithmetics();
		unscaledAugend = arith.add(unscaled, arith.fromUnscaled(unscaledAugend, scale));
		return this;
	}
	public AbstractMutableDecimal<S> addUnscaled(long unscaledAugend) {
		unscaled = getArithmetics().add(unscaled, unscaledAugend);
		return this;
	}
	
	@Override
	public AbstractMutableDecimal<S> subtract(Decimal<S> subtrahend) {
		return subtractUnscaled(subtrahend.unscaledValue());
	}
	public AbstractMutableDecimal<S> subtract(long subtrahend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.fromLong(subtrahend));
		return this;
	}
	public AbstractMutableDecimal<S> subtract(double subtrahend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.fromDouble(subtrahend));
		return this;
	}
	public AbstractMutableDecimal<S> subtract(BigInteger subtrahend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.fromBigInteger(subtrahend));
		return this;
	}
	public AbstractMutableDecimal<S> subtract(BigDecimal subtrahend) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.subtract(unscaled, arith.fromBigDecimal(subtrahend));
		return this;
	}
	public AbstractMutableDecimal<S> subtract(long unscaledSubtrahend, int scale) {
		final DecimalArithmetics arith = getArithmetics();
		unscaledSubtrahend = arith.subtract(unscaled, arith.fromUnscaled(unscaledSubtrahend, scale));
		return this;
	}
	public AbstractMutableDecimal<S> subtractUnscaled(long unscaledSubtrahend) {
		unscaled = getArithmetics().subtract(unscaled, unscaledSubtrahend);
		return this;
	}
	
	@Override
	public AbstractMutableDecimal<S> multiply(Decimal<S> multiplicand) {
		return multiplyUnscaled(multiplicand.unscaledValue());
	}
	@Override
	public Decimal<S> multiply(Decimal<S> multiplicand, RoundingMode roundingMode) {
		return multiplyUnscaled(multiplicand.unscaledValue(), roundingMode);
	}
	public AbstractMutableDecimal<S> multiply(long multiplicand) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.multiply(unscaled, arith.fromLong(multiplicand));
		return this;
	}
	public AbstractMutableDecimal<S> multiply(double multiplicand) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.multiply(unscaled, arith.fromDouble(multiplicand));
		return this;
	}
	public AbstractMutableDecimal<S> multiply(BigInteger multiplicand) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.multiply(unscaled, arith.fromBigInteger(multiplicand));
		return this;
	}
	public AbstractMutableDecimal<S> multiply(BigDecimal multiplicand) {
		final DecimalArithmetics arith = getArithmetics();
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
	
	@Override
	public AbstractMutableDecimal<S> divide(Decimal<S> divisor) {
		return divideUnscaled(divisor.unscaledValue());
	}
	@Override
	public Decimal<S> divide(Decimal<S> divisor, RoundingMode roundingMode) {
		return divideUnscaled(divisor.unscaledValue(), roundingMode);
	}
	public AbstractMutableDecimal<S> divide(long divisor) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.divide(unscaled, arith.fromLong(divisor));
		return this;
	}
	public AbstractMutableDecimal<S> divide(double divisor) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.divide(unscaled, arith.fromDouble(divisor));
		return this;
	}
	public AbstractMutableDecimal<S> divide(BigInteger divisor) {
		final DecimalArithmetics arith = getArithmetics();
		unscaled = arith.divide(unscaled, arith.fromBigInteger(divisor));
		return this;
	}
	public AbstractMutableDecimal<S> divide(BigDecimal divisor) {
		final DecimalArithmetics arith = getArithmetics();
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
	
	@Override
	public AbstractMutableDecimal<S> negate() {
		unscaled = getArithmetics().negate(unscaled);
		return this;
	}
	
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
	public Decimal<S> movePointLeft(int n, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).movePointLeft(unscaled, n);
		return this;
	}
	
	@Override
	public AbstractMutableDecimal<S> movePointRight(int n) {
		unscaled = getArithmetics().movePointRight(unscaled, n);
		return this;
	}
	@Override
	public Decimal<S> movePointRight(int n, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).movePointRight(unscaled, n);
		return this;
	}
	
	@Override
	public Decimal<S> pow(int n) {
		unscaled = getArithmetics().pow(unscaled, n);
		return this;
	}
	
	@Override
	public Decimal<S> pow(int n, RoundingMode roundingMode) {
		unscaled = getArithmetics().derive(roundingMode).pow(unscaled, n);
		return this;
	}

	abstract public Decimal<S> toImmutableValue();

	@Override
	public final long unscaledValue() {
		return unscaled;
	}

}
