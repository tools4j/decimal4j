package ch.javasoft.decimal;

import java.math.RoundingMode;

import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.scale.Scales;

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
	public final long unscaledValue() {
		return unscaled;
	}

}
