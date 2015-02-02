package org.decimal4j;

import org.decimal4j.scale.ScaleMetrics;

/**
 * Interface implemented by immutable {@link Decimal} classes of different
 * scales. Arithmetic operations of immutable decimals return a new decimal
 * instance as result value hence {@link MutableDecimal mutable} decimals may be
 * a better choice for chained operations.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 */
public interface ImmutableDecimal<S extends ScaleMetrics> extends Decimal<S> {
	//nothing to add
}
