package ch.javasoft.decimal;

import java.math.RoundingMode;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

/**
 * Base class for immutable {@link Decimal} classes of different scales.
 * Arithmetic operations of immutable decimals return a new decimal instance as
 * result value hence {@link AbstractMutableDecimal mutable} decimals may be a
 * better choice for chained operations.
 * 
 * @param <S>
 *            the scale subclass type associated with this decimal
 */
@SuppressWarnings("serial")
abstract public class AbstractImmutableDecimal<S extends Scale> extends
		AbstractDecimal<S> {

	private final long unscaled;

	public AbstractImmutableDecimal(long unscaled, S scale, DecimalArithmetics arithmetics) {
		super(scale, arithmetics);
		this.unscaled = unscaled;
	}

	abstract protected AbstractImmutableDecimal<S> create(long unscaled);

	abstract public AbstractMutableDecimal<S> toMutableValue();

	/**
	 * Returns a {@code Decimal} whose value is {@code (this +
	 * augend)}.
	 *
	 * @param augend
	 *            value to be added to this {@code Decimal}.
	 * @return {@code this + augend}
	 */
	@Override
	public AbstractImmutableDecimal<S> add(Decimal<S> augend) {
		return create(getArithmetics().add(unscaled, augend.unscaledValue()));
	}

	@Override
	public Decimal<S> subtract(Decimal<S> subtrahend) {
		return create(getArithmetics().subtract(unscaled, subtrahend.unscaledValue()));
	}

	@Override
	public Decimal<S> multiply(Decimal<S> multiplicand) {
		return create(getArithmetics().multiply(unscaled, multiplicand.unscaledValue()));
	}

	@Override
	public Decimal<S> multiply(Decimal<S> multiplicand, RoundingMode roundingMode) {
		return create(getArithmetics().derive(roundingMode).multiply(unscaled, multiplicand.unscaledValue()));
	}

	@Override
	public Decimal<S> divide(Decimal<S> divisor) {
		return create(getArithmetics().divide(unscaled, divisor.unscaledValue()));
	}

	@Override
	public Decimal<S> divide(Decimal<S> divisor, RoundingMode roundingMode) {
		return create(getArithmetics().derive(roundingMode).divide(unscaled, divisor.unscaledValue()));
	}

	@Override
	public Decimal<S> negate() {
		return create(getArithmetics().negate(unscaled));
	}

	@Override
	public AbstractImmutableDecimal<S> invert() {
		return create(getArithmetics().invert(unscaled));
	}

	@Override
	public Decimal<S> invert(RoundingMode roundingMode) {
		return create(getArithmetics().derive(roundingMode).invert(unscaled));
	}

	@Override
	public Decimal<S> movePointLeft(int n) {
		if (n > 0) {
			return create(getArithmetics().movePointLeft(unscaled, n));
		}
		return n == 0 ? this : movePointRight(-n);
	}

	@Override
	public Decimal<S> movePointLeft(int n, RoundingMode roundingMode) {
		if (n > 0) {
			return create(getArithmetics().derive(roundingMode).movePointLeft(unscaled, n));
		}
		return n == 0 ? this : movePointRight(-n, roundingMode);
	}

	@Override
	public Decimal<S> movePointRight(int n) {
		if (n > 0) {
			return create(getArithmetics().movePointRight(unscaled, n));
		}
		return n == 0 ? this : movePointLeft(-n);
	}

	@Override
	public Decimal<S> movePointRight(int n, RoundingMode roundingMode) {
		if (n > 0) {
			return create(getArithmetics().derive(roundingMode).movePointRight(unscaled, n));
		}
		return n == 0 ? this : movePointLeft(-n, roundingMode);
	}

	@Override
	public Decimal<S> pow(int n) {
		return create(getArithmetics().pow(unscaled, n));
	}

	@Override
	public Decimal<S> pow(int n, RoundingMode roundingMode) {
		return create(getArithmetics().derive(roundingMode).pow(unscaled, n));
	}

	@Override
	public final long unscaledValue() {
		return unscaled;
	}

}
