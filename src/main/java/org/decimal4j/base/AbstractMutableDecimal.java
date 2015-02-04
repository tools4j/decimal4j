package org.decimal4j.base;

import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Base class for mutable {@link Decimal} classes of different scales.
 * Arithmetic operations of mutable decimals modify the state of {@code this}
 * {@code Decimal} and return {@code this} as result value.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this mutable decimal
 */
@SuppressWarnings("serial")
abstract public class AbstractMutableDecimal<S extends ScaleMetrics, D extends AbstractMutableDecimal<S, D>>
		extends AbstractDecimal<S, D> implements MutableDecimal<S> {

	private long unscaled;

	/**
	 * Constructor with specified unscaled value.
	 * 
	 * @param unscaled
	 *            the unscaled decimal value
	 */
	public AbstractMutableDecimal(long unscaled) {
		this.unscaled = unscaled;
	}

	@Override
	public final long unscaledValue() {
		return unscaled;
	}

	/**
	 * Returns {@code this} decimal after assigning the value
	 * <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>.
	 * 
	 * @param unscaled
	 *            unscaled value to assign to this {@code Decimal}
	 * @return {@code this} decimal value now representing
	 *         <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>
	 */
	@Override
	protected D createOrAssign(long unscaled) {
		this.unscaled = unscaled;
		return self();
	}

	@Override
	public MutableDecimal<?> scale(int scale) {
		return scale(scale, RoundingMode.HALF_UP);
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> MutableDecimal<S> scale(S scaleMetrics) {
		return scale(scaleMetrics, RoundingMode.HALF_UP);
	}

	@Override
	public MutableDecimal<?> scale(int scale, RoundingMode roundingMode) {
		if (scale == getScale()) {
			return this;
		}
		return Factories.valueOf(scale).newMutable().set(this, roundingMode);
	}

	@Override
	public MutableDecimal<?> scale(int scale, TruncationPolicy truncationPolicy) {
		if (scale == getScale()) {
			return this;
		}
		return Factories.valueOf(scale).newMutable().set(this, truncationPolicy);
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> MutableDecimal<S> scale(S scaleMetrics, RoundingMode roundingMode) {
		if (scaleMetrics == getScaleMetrics()) {
			@SuppressWarnings("unchecked")
			//safe: we know it is the same scale metrics
			final MutableDecimal<S> self = (MutableDecimal<S>) this;
			return self;
		} else {}
		return Factories.valueOf(scaleMetrics).newMutable().set(this, roundingMode);
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> MutableDecimal<S> scale(S scaleMetrics, TruncationPolicy truncationPolicy) {
		if (scaleMetrics == getScaleMetrics()) {
			@SuppressWarnings("unchecked")
			//safe: we know it is the same scale metrics
			final MutableDecimal<S> self = (MutableDecimal<S>) this;
			return self;
		} else {}
		return Factories.valueOf(scaleMetrics).newMutable().set(this, truncationPolicy);
	}

	@Override
	public MutableDecimal<?> multiplyExact(Decimal<?> multiplicand) {
		final int targetScale = getScale() + multiplicand.getScale();
		return Factories.valueOf(targetScale).newMutable().setUnscaled(unscaled * multiplicand.unscaledValue());
	}

	@Override
	public MutableDecimal<?> multiplyExact(Decimal<?> multiplicand, OverflowMode overflowMode) {
		final int targetScale = getScale() + multiplicand.getScale();
		final MutableDecimal<?> result = Factories.valueOf(targetScale).newMutable().setUnscaled(unscaled);
		try {
			result.multiply(multiplicand.unscaledValue(), overflowMode);
		} catch (ArithmeticException e) {
			throw new ArithmeticException("Overflow: " + this + " * " + multiplicand);
		}
		return result;
	}

	@Override
	public D setZero() {
		unscaled = 0;
		return self();
	}

	@Override
	public D setOne() {
		unscaled = getScaleMetrics().getScaleFactor();
		return self();
	}

	@Override
	public D setMinusOne() {
		unscaled = -getScaleMetrics().getScaleFactor();
		return self();
	}

	@Override
	public D setUlp() {
		unscaled = 1;
		return self();
	}

	@Override
	public D set(Decimal<S> value) {
		return setUnscaled(value.unscaledValue());
	}

	@Override
	public D set(Decimal<?> value, RoundingMode roundingMode) {
		return setUnscaled(value.unscaledValue(), value.getScale(), roundingMode);
	}

	@Override
	public D set(Decimal<?> value, TruncationPolicy truncationPolicy) {
		return setUnscaled(value.unscaledValue(), value.getScale(), truncationPolicy);
	}

	@Override
	public D set(long value) {
		unscaled = getDefaultArithmetic().fromLong(value);
		return self();
	}

	@Override
	public D set(long value, OverflowMode overflowMode) {
		unscaled = getArithmeticFor(overflowMode).fromLong(value);
		return self();
	}

	@Override
	public D set(double value) {
		unscaled = getDefaultArithmetic().fromDouble(value);
		return self();
	}

	@Override
	public D set(double value, RoundingMode roundingMode) {
		unscaled = getArithmeticFor(roundingMode).fromDouble(value);
		return self();
	}

	@Override
	public D set(double value, TruncationPolicy truncationPolicy) {
		unscaled = getArithmeticFor(truncationPolicy).fromDouble(value);
		return self();
	}

	@Override
	public D setUnscaled(long unscaledValue) {
		unscaled = unscaledValue;
		return self();
	}

	@Override
	public D setUnscaled(long unscaledValue, int scale) {
		unscaled = getDefaultArithmetic().fromUnscaled(unscaledValue, scale);
		return self();
	}

	@Override
	public D setUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		unscaled = getArithmeticFor(roundingMode).fromUnscaled(unscaledValue, scale);
		return self();
	}

	@Override
	public D setUnscaled(long unscaledValue, int scale, TruncationPolicy truncationPolicy) {
		unscaled = getArithmeticFor(truncationPolicy).fromUnscaled(unscaledValue, scale);
		return self();
	}
}
