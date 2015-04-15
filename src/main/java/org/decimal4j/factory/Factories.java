/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.factory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	 * All decimal factory constants in an immutable ordered list:
	 * <p>
	 * {@code VALUES=[Factory0f.INSTANCE, Factory1f.INSTANCE, ..., Factory18f.INSTANCE]}
	 */
	public static List<DecimalFactory<?>> VALUES = Collections.unmodifiableList(Arrays.asList(FACTORIES));

	/**
	 * Returns the {@code DecimalFactory} constant based on a given scale.
	 * 
	 * @param scale
	 *            the scale value; must be in {@code [0,18]} both ends inclusive
	 * @return the factory constant corresponding to {@code scale}
	 * @throws IllegalArgumentException
	 *             if scale is not in {@code [0, 18]}
	 */
	public static final DecimalFactory<?> getDecimalFactory(int scale) {
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
	public static <S extends ScaleMetrics> DecimalFactory<S> getDecimalFactory(S scaleMetrics) {
		@SuppressWarnings("unchecked")
		final DecimalFactory<S> factory = (DecimalFactory<S>)getDecimalFactory(scaleMetrics.getScale());
		return factory;
	}

	//no instances
	private Factories() {
		super();
	}
}
