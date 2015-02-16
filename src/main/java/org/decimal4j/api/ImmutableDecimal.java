package org.decimal4j.api;

import org.decimal4j.scale.ScaleMetrics;

/**
 * Interface implemented by immutable {@link Decimal} classes of different
 * scales. Immutable Decimals allocate a new Decimals instance for results of
 * arithmetic operations.
 * <p>
 * Consider also {@link MutableDecimal} descendants especially for chained
 * operations.
 * <p>
 * Immutable Decimals are thread safe.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 */
public interface ImmutableDecimal<S extends ScaleMetrics> extends Decimal<S> {
	//nothing to add
}
