package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.scale.Scales;
import ch.javasoft.decimal.scale.ScaleMetrics;

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
		extends AbstractDecimal<S, D> implements MutableDecimal<S, D> {

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
	public MutableDecimal<?, ?> scale(int scale) {
		return scale(scale, RoundingMode.HALF_UP);
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> MutableDecimal<S, ? extends MutableDecimal<S, ?>> scale(S scaleMetrics) {
		return scale(scaleMetrics, RoundingMode.HALF_UP);
	}

	@Override
	public MutableDecimal<?, ?> scale(int scale, RoundingMode roundingMode) {
		if (scale == getScale()) {
			return this;
		}
		return Scales.valueOf(scale).createMutable(0).set(this, roundingMode);
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> MutableDecimal<S, ? extends MutableDecimal<S, ?>> scale(S scaleMetrics, RoundingMode roundingMode) {
		final MutableDecimal<?, ?> value;
		if (scaleMetrics == getScaleMetrics()) {
			value = this;
		} else {
			value = scaleMetrics.createMutable(0).set(this, roundingMode);
		}
		@SuppressWarnings("unchecked")
		//safe: we know it is the same scale metrics
		final MutableDecimal<S, ? extends MutableDecimal<S, ?>> result = (MutableDecimal<S, ? extends MutableDecimal<S, ?>>) value;
		return result;
	}

	@Override
	public D setZero() {
		unscaled = 0;
		return self();
	}

	@Override
	public D setOne() {
		unscaled = unscaledOne();
		return self();
	}

	@Override
	public D setMinusOne() {
		unscaled = -unscaledOne();
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
	public D set(long value) {
		unscaled = getDefaultArithmetics().fromLong(value);
		return self();
	}

	@Override
	public D set(double value) {
		unscaled = getDefaultArithmetics().fromDouble(value);
		return self();
	}

	@Override
	public D set(double value, RoundingMode roundingMode) {
		unscaled = getArithmeticsFor(roundingMode).fromDouble(value);
		return self();
	}

	@Override
	public D set(BigInteger value) {
		unscaled = getDefaultArithmetics().fromBigInteger(value);
		return self();
	}

	@Override
	public D set(BigDecimal value) {
		unscaled = getDefaultArithmetics().fromBigDecimal(value);
		return self();
	}

	@Override
	public D set(BigDecimal value, RoundingMode roundingMode) {
		unscaled = getArithmeticsFor(roundingMode).fromBigDecimal(value);
		return self();
	}

	@Override
	public D setUnscaled(long unscaledValue) {
		unscaled = unscaledValue;
		return self();
	}

	@Override
	public D setUnscaled(long unscaledValue, int scale) {
		unscaled = getDefaultArithmetics().fromUnscaled(unscaledValue, scale);
		return self();
	}

	@Override
	public D setUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		unscaled = getArithmeticsFor(roundingMode).fromUnscaled(unscaledValue, scale);
		return self();
	}

}
