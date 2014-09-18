package ch.javasoft.decimal;

import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Interface implemented by immutable {@link Decimal} classes of different
 * scales. Arithmetic operations of immutable decimals return a new decimal
 * instance as result value hence {@link MutableDecimal mutable} decimals may be
 * a better choice for chained operations.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this immutable decimal
 */
public interface ImmutableDecimal<S extends ScaleMetrics, D extends ImmutableDecimal<S, D>> extends Decimal<S> {

	/**
	 * Creates a new mutable value representing the same decimal as {@code this}
	 * immutable decimal and returns it.
	 * 
	 * @return {@code this} as new mutable decimal value
	 */
	MutableDecimal<S, ?> toMutableDecimal();
}
