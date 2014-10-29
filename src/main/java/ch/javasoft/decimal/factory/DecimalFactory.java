package ch.javasoft.decimal.factory;

import ch.javasoft.decimal.ImmutableDecimal;
import ch.javasoft.decimal.MutableDecimal;
import ch.javasoft.decimal.scale.ScaleMetrics;

public interface DecimalFactory<S extends ScaleMetrics> {
	/**
	 * Creates and returns an immutable value.
	 *
	 * @param unscaled
	 *            the unscaled long value
	 * @return an immutable value.
	 */
	ImmutableDecimal<S, ?> createImmutable(long unscaled);

	/**
	 * Creates and returns a mutable value.
	 *
	 * @param unscaled
	 *            the unscaled long value
	 * @return an mutable value.
	 */
	MutableDecimal<S, ?> createMutable(long unscaled);

	/**
	 * Creates a one dimensional array of the specified {@code length} for
	 * immutable values.
	 * 
	 * @param length
	 *            the length of the returned array
	 * @return a new array of the specified length
	 */
	ImmutableDecimal<S, ?>[] createImmutableArray(int length);

	/**
	 * Creates a one dimensional array of the specified {@code length} for
	 * mutable values.
	 * 
	 * @param length
	 *            the length of the returned array
	 * @return a new array of the specified length
	 */
	MutableDecimal<S, ?>[] createMutableArray(int length);
}
