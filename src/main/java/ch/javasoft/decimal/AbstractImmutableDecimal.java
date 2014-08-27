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
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this immutable decimal
 * @param <M>
 *            the concrete class implementing the mutable counterpart of this
 *            immutable decimal
 */
@SuppressWarnings("serial")
abstract public class AbstractImmutableDecimal<S extends ScaleMetrics, D extends AbstractImmutableDecimal<S, D, M>, M extends AbstractMutableDecimal<S, M, D>>
		extends AbstractDecimal<S, D> {

	private final long unscaled;

	public AbstractImmutableDecimal(long unscaled, S scale, DecimalArithmetics arithmetics) {
		super(scale, arithmetics);
		this.unscaled = unscaled;
	}

	/**
	 * Creates a new decimal instance based on the given unscaled value and
	 * returns it.
	 * 
	 * @param unscaled
	 *            the unscaled value
	 * @return a new decimal value representing
	 *         <code>unscaled*10<sup>-scale</sup></code>
	 */
	abstract protected D create(long unscaled);

	/**
	 * Creates a new mutable value representing the same decimal as {@code this}
	 * immutable decimal and returns it.
	 * 
	 * @return {@code this} as new mutable decimal value
	 */
	abstract public M toMutableDecimal();
	
	@Override
	public AbstractImmutableDecimal<?, ?, ?> convert(int scale) {
		return convert(scale, getArithmetics().getRoundingMode());
	}

	@Override
	public AbstractImmutableDecimal<?, ?, ?> convert(int scale, RoundingMode roundingMode) {
		final int curScale = getScaleMetrics().getScale();
		if (scale == curScale) {
			return this;
		}
		final DecimalArithmetics curArith = getArithmetics();
		final RoundingMode curRounding = curArith.getRoundingMode();
		final OverflowMode curOverflow = curArith.getOverflowMode();
		final ScaleMetrics newMetrics = ScaleMetrics.valueOf(scale);
		final DecimalArithmetics conversionArith = newMetrics.getTruncatingArithmetics().derive(roundingMode).derive(curOverflow);
		final long newUnscaled = conversionArith.fromUnscaled(unscaledValue(), curScale);
		return newMetrics.createImmutable(newUnscaled, curRounding, curOverflow);
	}

	@Override
	public D add(Decimal<S> augend) {
		return create(getArithmetics().add(unscaled, augend.unscaledValue()));
	}

	@Override
	public D subtract(Decimal<S> subtrahend) {
		return create(getArithmetics().subtract(unscaled, subtrahend.unscaledValue()));
	}

	@Override
	public D multiply(Decimal<S> multiplicand) {
		return create(getArithmetics().multiply(unscaled, multiplicand.unscaledValue()));
	}

	@Override
	public D multiply(Decimal<S> multiplicand, RoundingMode roundingMode) {
		return create(getArithmetics().derive(roundingMode).multiply(unscaled, multiplicand.unscaledValue()));
	}

	@Override
	public D divide(Decimal<S> divisor) {
		return create(getArithmetics().divide(unscaled, divisor.unscaledValue()));
	}

	@Override
	public D divide(Decimal<S> divisor, RoundingMode roundingMode) {
		return create(getArithmetics().derive(roundingMode).divide(unscaled, divisor.unscaledValue()));
	}

	@Override
	public D negate() {
		return create(getArithmetics().negate(unscaled));
	}

	@Override
	public D invert() {
		return create(getArithmetics().invert(unscaled));
	}

	@Override
	public D invert(RoundingMode roundingMode) {
		return create(getArithmetics().derive(roundingMode).invert(unscaled));
	}

	@Override
	public D movePointLeft(int n) {
		if (n > 0) {
			return create(getArithmetics().movePointLeft(unscaled, n));
		}
		return n == 0 ? self() : movePointRight(-n);
	}

	@Override
	public D movePointLeft(int n, RoundingMode roundingMode) {
		if (n > 0) {
			return create(getArithmetics().derive(roundingMode).movePointLeft(unscaled, n));
		}
		return n == 0 ? self() : movePointRight(-n, roundingMode);
	}

	@Override
	public D movePointRight(int n) {
		if (n > 0) {
			return create(getArithmetics().movePointRight(unscaled, n));
		}
		return n == 0 ? self() : movePointLeft(-n);
	}

	@Override
	public D movePointRight(int n, RoundingMode roundingMode) {
		if (n > 0) {
			return create(getArithmetics().derive(roundingMode).movePointRight(unscaled, n));
		}
		return n == 0 ? self() : movePointLeft(-n, roundingMode);
	}

	@Override
	public D pow(int n) {
		return create(getArithmetics().pow(unscaled, n));
	}

	@Override
	public D pow(int n, RoundingMode roundingMode) {
		return create(getArithmetics().derive(roundingMode).pow(unscaled, n));
	}

	@Override
	public final long unscaledValue() {
		return unscaled;
	}

}
