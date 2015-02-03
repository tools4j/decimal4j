package org.decimal4j.base;

import java.math.RoundingMode;

import org.decimal4j.Decimal;
import org.decimal4j.ImmutableDecimal;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

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
		extends AbstractDecimal<S, D> implements ImmutableDecimal<S> {

	private final long unscaled;

	public AbstractImmutableDecimal(long unscaled) {
		this.unscaled = unscaled;
	}

	@Override
	public ImmutableDecimal<?> scale(int scale) {
		return scale(scale, RoundingMode.HALF_UP);
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> ImmutableDecimal<S> scale(S scaleMetrics) {
		return scale(scaleMetrics, RoundingMode.HALF_UP);
	}

	@Override
	public ImmutableDecimal<?> scale(int scale, RoundingMode roundingMode) {
		final int myScale = getScale();
		if (scale == myScale) {
			return this;
		}
		final ScaleMetrics targetMetrics = Scales.valueOf(scale);
		final long targetUnscaled = targetMetrics.getArithmetics(roundingMode).fromUnscaled(unscaled, myScale);
		return Factories.valueOf(targetMetrics).createImmutable(targetUnscaled);
	}

	@Override
	public ImmutableDecimal<?> scale(int scale, TruncationPolicy truncationPolicy) {
		final int myScale = getScale();
		if (scale == myScale) {
			return this;
		}
		final ScaleMetrics targetMetrics = Scales.valueOf(scale);
		final long targetUnscaled = targetMetrics.getArithmetics(truncationPolicy).fromUnscaled(unscaled, myScale);
		return Factories.valueOf(targetMetrics).createImmutable(targetUnscaled);
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> ImmutableDecimal<S> scale(S scaleMetrics, RoundingMode roundingMode) {
		if (scaleMetrics == getScaleMetrics()) {
			@SuppressWarnings("unchecked")
			//safe: we know it is the same scale metrics
			final ImmutableDecimal<S> self = (ImmutableDecimal<S>) this;
			return self;
		}
		final long targetUnscaled = scaleMetrics.getArithmetics(roundingMode).fromUnscaled(unscaled, getScale());
		return Factories.valueOf(scaleMetrics).createImmutable(targetUnscaled);
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> ImmutableDecimal<S> scale(S scaleMetrics, TruncationPolicy truncationPolicy) {
		if (scaleMetrics == getScaleMetrics()) {
			@SuppressWarnings("unchecked")
			//safe: we know it is the same scale metrics
			final ImmutableDecimal<S> self = (ImmutableDecimal<S>) this;
			return self;
		}
		final long targetUnscaled = scaleMetrics.getArithmetics(truncationPolicy).fromUnscaled(unscaled, getScale());
		return Factories.valueOf(scaleMetrics).createImmutable(targetUnscaled);
	}

	@Override
	public ImmutableDecimal<?> multiplyExact(Decimal<?> multiplicand) {
		final int targetScale = getScale() + multiplicand.getScale();
		return Factories.valueOf(targetScale).createImmutable(unscaled * multiplicand.unscaledValue());
	}

	@Override
	public ImmutableDecimal<?> multiplyExact(Decimal<?> multiplicand, OverflowMode overflowMode) {
		final int targetScale = getScale() + multiplicand.getScale();
		try {
			final long unscaledProduct = getArithmeticsFor(overflowMode).multiplyByLong(unscaled, multiplicand.unscaledValue());
			return Factories.valueOf(targetScale).createImmutable(unscaledProduct);
		} catch (ArithmeticException e) {
			throw new ArithmeticException("Overflow: " + this + " * " + multiplicand);
		}
	}

	@Override
	public final long unscaledValue() {
		return unscaled;
	}

}