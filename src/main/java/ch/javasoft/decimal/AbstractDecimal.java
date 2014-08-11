package ch.javasoft.decimal;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

/**
 * Common base class for {@link AbstractImmutableDecimal immutable} and
 * {@link AbstractMutableDecimal mutable} {@link Decimal} numbers of different
 * scales.
 * 
 * @param <S>
 *            the scale subclass type associated with this decimal
 */
@SuppressWarnings("serial")
abstract public class AbstractDecimal<S extends Scale> extends Number implements
		Decimal<S> {

	private final S scale;
	private final DecimalArithmetics arithmetics;

	/**
	 * Constructor with specified scale using the given {@code arithmetics}.
	 * 
	 * @param scale
	 *            the scale for this decimal number
	 * @param arithmetics
	 *            the arithmetics used for operations with decimals
	 * @throws IllegalArgumentException
	 *             if {@code scale} is not consistent with the scale used by the
	 *             specified {@code arithmetics} argument
	 */
	public AbstractDecimal(S scale, DecimalArithmetics arithmetics) {
		if (scale.getFractionDigits() != arithmetics.getScale()) {
			throw new IllegalArgumentException("scale and arithmetics are not compatible: scale.getFractionDigits() != arithmetics.getScale(): " + scale.getFractionDigits() + " != " + arithmetics.getScale());
		}
		this.scale = scale;
		this.arithmetics = arithmetics;
	}

	@Override
	public final S getScale() {
		return scale;
	}

	@Override
	public final DecimalArithmetics getArithmetics() {
		return arithmetics;
	}

	@Override
	public int intValue() {
		return (int) longValue();
	}

	@Override
	public long longValue() {
		return getArithmetics().toLong(unscaledValue());
	}

	@Override
	public float floatValue() {
		return (float) doubleValue();
	}

	@Override
	public double doubleValue() {
		return getArithmetics().toDouble(unscaledValue());
	}

	@Override
	public Decimal<S> abs() {
		return unscaledValue() < 0 ? negate() : this;
	}

	@Override
	public int signum() {
		return Long.signum(unscaledValue());
	}

	@Override
	public int hashCode() {
		final long unscaled = unscaledValue();
		return (int) (unscaled ^ (unscaled >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Decimal) {
			final Decimal<?> other = (Decimal<?>) obj;
			return unscaledValue() == other.unscaledValue() && getArithmetics().getScale() == other.getArithmetics().getScale();
		}
		return false;
	}

	@Override
	public int compareTo(Decimal<S> anotherDecimal) {
		return getArithmetics().compare(unscaledValue(), anotherDecimal.unscaledValue());
	}

	@Override
	public String toString() {
		return getArithmetics().toString(unscaledValue());
	}

}
