package ch.javasoft.decimal;

import java.math.RoundingMode;

import ch.javasoft.decimal.scale.Scales;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Base class for immutable {@link Decimal} classes of different scales.
 * Arithmetic operations of immutable decimals return a new decimal instance as
 * result value hence {@link AbstractMutableDecimal mutable} decimals may be a
 * better choice for chained operations.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this mutable decimal
 */
@SuppressWarnings("serial")
abstract public class AbstractImmutableDecimal<S extends ScaleMetrics, D extends AbstractImmutableDecimal<S, D>>
		extends AbstractDecimal<S, D> implements ImmutableDecimal<S, D> {

	private final long unscaled;

	public AbstractImmutableDecimal(long unscaled) {
		this.unscaled = unscaled;
	}

	/**
	 * Returns a new {@code Decimal} whose value is
	 * <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>.
	 * 
	 * @param unscaled
	 *            unscaled value to be returned as a new {@code Decimal}
	 * @return a new decimal instance representing
	 *         <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>
	 */
	@Override
	abstract protected D createOrAssign(long unscaled);

	@Override
	public ImmutableDecimal<?, ?> scale(int scale) {
		return scale(scale, RoundingMode.HALF_UP);
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> ImmutableDecimal<S, ? extends ImmutableDecimal<S, ?>> scale(S scaleMetrics) {
		return scale(scaleMetrics, RoundingMode.HALF_UP);
	}

	@Override
	public ImmutableDecimal<?, ?> scale(int scale, RoundingMode roundingMode) {
		final int myScale = getScale();
		if (scale == myScale) {
			return this;
		}
		final ScaleMetrics targetMetrics = Scales.valueOf(scale);
		final long targetUnscaled = targetMetrics.getArithmetics(roundingMode).fromUnscaled(unscaledValue(), myScale);
		return targetMetrics.createImmutable(targetUnscaled);
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> ImmutableDecimal<S, ? extends ImmutableDecimal<S, ?>> scale(S scaleMetrics, RoundingMode roundingMode) {
		final ImmutableDecimal<?, ?> value;
		if (scaleMetrics == getScaleMetrics()) {
			value = this;
		} else {
			final long targetUnscaled = scaleMetrics.getArithmetics(roundingMode).fromUnscaled(unscaledValue(), getScale());
			value = scaleMetrics.createImmutable(targetUnscaled);
		}
		@SuppressWarnings("unchecked")
		//safe: we know it is the same scale metrics
		final ImmutableDecimal<S, ? extends ImmutableDecimal<S, ?>> result = (ImmutableDecimal<S, ? extends ImmutableDecimal<S, ?>>) value;
		return result;
	}

	@Override
	public D add(Decimal<S> augend) {
		return createOrAssign(getDefaultArithmetics().add(unscaled, augend.unscaledValue()));
	}

	@Override
	public D subtract(Decimal<S> subtrahend) {
		return createOrAssign(getDefaultArithmetics().subtract(unscaled, subtrahend.unscaledValue()));
	}

	@Override
	public D multiply(Decimal<S> multiplicand) {
		return createOrAssign(getDefaultArithmetics().multiply(unscaled, multiplicand.unscaledValue()));
	}

	@Override
	public D multiply(Decimal<S> multiplicand, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).multiply(unscaled, multiplicand.unscaledValue()));
	}

	@Override
	public D divide(Decimal<S> divisor) {
		return createOrAssign(getDefaultArithmetics().divide(unscaled, divisor.unscaledValue()));
	}

	@Override
	public D divide(Decimal<S> divisor, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).divide(unscaled, divisor.unscaledValue()));
	}

	@Override
	public D negate() {
		return createOrAssign(getDefaultArithmetics().negate(unscaled));
	}

	@Override
	public D invert() {
		return createOrAssign(getDefaultArithmetics().invert(unscaled));
	}

	@Override
	public D invert(RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).invert(unscaled));
	}

	@Override
	public D divideByPowerOfTen(int n) {
		if (n > 0) {
			return createOrAssign(getDefaultArithmetics().divideByPowerOf10(unscaled, n));
		}
		return n == 0 ? self() : multiplyByPowerOfTen(-n);
	}

	@Override
	public D divideByPowerOfTen(int n, RoundingMode roundingMode) {
		if (n > 0) {
			return createOrAssign(getArithmeticsFor(roundingMode).divideByPowerOf10(unscaled, n));
		}
		return n == 0 ? self() : multiplyByPowerOfTen(-n, roundingMode);
	}

	@Override
	public D multiplyByPowerOfTen(int n) {
		if (n > 0) {
			return createOrAssign(getDefaultArithmetics().multiplyByPowerOf10(unscaled, n));
		}
		return n == 0 ? self() : divideByPowerOfTen(-n);
	}

	@Override
	public D multiplyByPowerOfTen(int n, RoundingMode roundingMode) {
		if (n > 0) {
			return createOrAssign(getArithmeticsFor(roundingMode).multiplyByPowerOf10(unscaled, n));
		}
		return n == 0 ? self() : divideByPowerOfTen(-n, roundingMode);
	}

	@Override
	public D pow(int n) {
		return createOrAssign(getDefaultArithmetics().pow(unscaled, n));
	}

	@Override
	public D pow(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).pow(unscaled, n));
	}

	@Override
	public final long unscaledValue() {
		return unscaled;
	}

}
