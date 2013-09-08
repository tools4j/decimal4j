package ch.javasoft.decimal;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.arithmetic.RoundHalfEvenDecimalArithmetics;

@SuppressWarnings("serial")
abstract public class AbstractDecimal<S extends Scale> extends Number implements
		Decimal<S> {

	private final S scale;
	private final DecimalArithmetics arithmetics;

	public AbstractDecimal(S scale) {
		this.scale = scale;
		this.arithmetics = new RoundHalfEvenDecimalArithmetics(scale.getFractionDigits());
	}

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

	/**
	 * Returns a hash code for this {@code Decimal}. The result is the exclusive
	 * OR of the two halves of the primitive unscaled {@code long} value held by
	 * this {@code Decimal} object. That is, the hashcode is the value of the
	 * expression:
	 * 
	 * <blockquote>
	 * {@code (int)(this.unscaledValue()^(this.unscaledValue()>>>32))}
	 * </blockquote>
	 * 
	 * @return a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		final long unscaled = unscaledValue();
		return (int) (unscaled ^ (unscaled >>> 32));
	}

	/**
	 * Compares this object to the specified object. The result is {@code true}
	 * if and only if the argument is not {@code null} and is a {@code Decimal}
	 * object that contains the same value and scale as this object.
	 * 
	 * @param obj
	 *            the object to compare with.
	 * @return {@code true} if the argument is {@code Decimal} object that
	 *         contains the same value and scale as this object; {@code false}
	 *         otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Decimal) {
			final Decimal<?> other = (Decimal<?>) obj;
			return unscaledValue() == other.unscaledValue() && getArithmetics().getScale() == other.getArithmetics().getScale();
		}
		return false;
	}

	/**
	 * Compares two {@code Decimal} objects numerically.
	 * 
	 * @param anotherDecimal
	 *            the {@code Decimal} to be compared.
	 * @return the value {@code 0} if this {@code Decimal} is equal to the
	 *         argument {@code Decimal}; a value less than {@code 0} if this
	 *         {@code Decimal} is numerically less than the argument
	 *         {@code Decimal}; and a value greater than {@code 0} if this
	 *         {@code Decimal} is numerically greater than the argument
	 *         {@code Decimal} (signed comparison).
	 */
	@Override
	public int compareTo(Decimal<S> anotherDecimal) {
		return getArithmetics().compare(unscaledValue(), anotherDecimal.unscaledValue());
	}

	@Override
	public String toString() {
		return getArithmetics().toString(unscaledValue());
	}

}
