package org.decimal4j.factory;

import org.decimal4j.scale.ScaleMetrics;

import org.decimal4j.factory.Factory0f;
import org.decimal4j.factory.Factory10f;
import org.decimal4j.factory.Factory11f;
import org.decimal4j.factory.Factory12f;
import org.decimal4j.factory.Factory13f;
import org.decimal4j.factory.Factory14f;
import org.decimal4j.factory.Factory15f;
import org.decimal4j.factory.Factory16f;
import org.decimal4j.factory.Factory17f;
import org.decimal4j.factory.Factory18f;
import org.decimal4j.factory.Factory1f;
import org.decimal4j.factory.Factory2f;
import org.decimal4j.factory.Factory3f;
import org.decimal4j.factory.Factory4f;
import org.decimal4j.factory.Factory5f;
import org.decimal4j.factory.Factory6f;
import org.decimal4j.factory.Factory7f;
import org.decimal4j.factory.Factory8f;
import org.decimal4j.factory.Factory9f;

/**
 * Utility class with static methods to access {@link DecimalFactory} instances.
 */
public final class Factories {

	//@formatter:off
	private static final DecimalFactory<?>[] FACTORIES = {
		Factory0f.INSTANCE,
		Factory1f.INSTANCE,
		Factory2f.INSTANCE,
		Factory3f.INSTANCE,
		Factory4f.INSTANCE,
		Factory5f.INSTANCE,
		Factory6f.INSTANCE,
		Factory7f.INSTANCE,
		Factory8f.INSTANCE,
		Factory9f.INSTANCE,
		Factory10f.INSTANCE,
		Factory11f.INSTANCE,
		Factory12f.INSTANCE,
		Factory13f.INSTANCE,
		Factory14f.INSTANCE,
		Factory15f.INSTANCE,
		Factory16f.INSTANCE,
		Factory17f.INSTANCE,
		Factory18f.INSTANCE
	};
	//@formatter:on

	/**
	 * Returns the {@code DecimalFactory} constant based on a given scale.
	 * 
	 * @param scale
	 *            the scale value; must be in {@code [0,18]} both ends inclusive
	 * @return the factory constant corresponding to {@code scale}
	 * @throws IllegalArgumentException
	 *             if scale is not in {@code [0, 18]}
	 */
	public static final DecimalFactory<?> valueOf(int scale) {
		if (0 <= scale & scale <= 18) {
			return FACTORIES[scale];
		}
		throw new IllegalArgumentException("illegal scale, must be in [0,18] but was: " + scale);
	}

	/**
	 * Returns the {@code DecimalFactory} for the given scale metrics.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics
	 * @return the factory constant corresponding to {@code scaleMetrics}
	 */
	public static <S extends ScaleMetrics> DecimalFactory<S> valueOf(S scaleMetrics) {
		@SuppressWarnings("unchecked")
		final DecimalFactory<S> factory = (DecimalFactory<S>)valueOf(scaleMetrics.getScale());
		return factory;
	}

	//no instances
	private Factories() {
		super();
	}
}
