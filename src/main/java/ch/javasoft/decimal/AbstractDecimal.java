package ch.javasoft.decimal;

import java.math.RoundingMode;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

/**
 * Common base class for {@link AbstractImmutableDecimal immutable} and
 * {@link AbstractMutableDecimal mutable} {@link Decimal} numbers of different
 * scales.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this decimal
 */
@SuppressWarnings("serial")
abstract public class AbstractDecimal<S extends ScaleMetrics, D extends AbstractDecimal<S, D>>
		extends Number implements Decimal<S> {

	private final S scaleMetrics;
	private final DecimalArithmetics arithmetics;

	/**
	 * Constructor with specified scale using the given {@code arithmetics}.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics for this decimal number
	 * @param arithmetics
	 *            the arithmetics used for operations with decimals
	 * @throws IllegalArgumentException
	 *             if {@code scale} is not consistent with the scale used by the
	 *             specified {@code arithmetics} argument
	 */
	public AbstractDecimal(S scaleMetrics, DecimalArithmetics arithmetics) {
		if (scaleMetrics.getScale() != arithmetics.getScale()) {
			throw new IllegalArgumentException("scale and arithmetics are not compatible: scaleMetrics.getScale() != arithmetics.getScale(): " + scaleMetrics.getScale() + " != " + arithmetics.getScale());
		}
		this.scaleMetrics = scaleMetrics;
		this.arithmetics = arithmetics;
	}

	@Override
	public final S getScaleMetrics() {
		return scaleMetrics;
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
	
	//overrides with the concrete implementation subtype
	
	/**
	 * Returns {@code this} decimal value as concrete implementation subtype.
	 * 
	 * @return {@code this}
	 */
	abstract protected D self();
	
	@Override
	abstract public D convert(RoundingMode roundingMode);

	@Override
	abstract public D convert(OverflowMode overflowMode);

	@Override
	abstract public D add(Decimal<S> augend);

	@Override
	abstract public D subtract(Decimal<S> subtrahend);

	@Override
	abstract public D multiply(Decimal<S> multiplicand);

	@Override
	abstract public D multiply(Decimal<S> multiplicand, RoundingMode roundingMode);

	@Override
	abstract public D divide(Decimal<S> divisor);

	@Override
	abstract public D divide(Decimal<S> divisor, RoundingMode roundingMode);

	@Override
	abstract public D negate();

	@Override
	public D abs() {
		return unscaledValue() >= 0 ? self() : negate();
	}

	@Override
	abstract public D invert();

	@Override
	abstract public D invert(RoundingMode roundingMode);

	@Override
	abstract public D movePointLeft(int n);

	@Override
	abstract public D movePointLeft(int n, RoundingMode roundingMode);

	@Override
	abstract public D movePointRight(int n);

	@Override
	abstract public D movePointRight(int n, RoundingMode roundingMode);

	@Override
	abstract public D pow(int n);

	@Override
	abstract public D pow(int n, RoundingMode roundingMode);
}
