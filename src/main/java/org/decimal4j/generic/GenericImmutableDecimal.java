/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.generic;

import java.util.Objects;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.ImmutableDecimal;
import org.decimal4j.base.AbstractImmutableDecimal;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;

/**
 * <code>GenericImmutableDecimal</code> is an {@link ImmutableDecimal} implemented
 * in a generic way, that is, different instances can have different scales. In
 * contrast the classes defined in the {@code immutable} package have have no
 * generic parameter as they have a fixed scale per class.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 */
public final class GenericImmutableDecimal<S extends ScaleMetrics> extends AbstractImmutableDecimal<S, GenericImmutableDecimal<S>> {

	private static final long serialVersionUID = 1L;

	private final S scaleMetrics;

	/**
	 * Creates a new {@code GenericImmutableDecimal} with the scale specified by
	 * the given {@code scaleMetrics} argument. The numeric value of new the
	 * Decimal is <code>unscaledValue &times; 10<sup>-scale</sup></code>
	 * 
	 * @param scaleMetrics
	 *            the metrics object defining the scale for the new value
	 * @param unscaledValue
	 *            the unscaled long value representing the new Decimal's
	 *            numerical value before applying the scale factor
	 */
	public GenericImmutableDecimal(S scaleMetrics, long unscaledValue) {
		super(unscaledValue);
		this.scaleMetrics = Objects.requireNonNull(scaleMetrics, "scaleMetrics cannot be null");
	}

	/**
	 * Creates a new {@code GenericImmutableDecimal} with the same value and scale
	 * as the given {@code decimal} argument.
	 * 
	 * @param decimal
	 *            the numeric value to assign to the created immutable Decimal
	 */
	public GenericImmutableDecimal(Decimal<S> decimal) {
		this(decimal.getScaleMetrics(), decimal.unscaledValue());
	}
	
	/**
	 * Creates and returns a new {@code GenericImmutableDecimal} with the same
	 * value and scale as the given {@code decimal} argument.
	 * 
	 * @param decimal
	 *            the numeric value to assign to the created immutable Decimal
	 * @param <S> the scale metrics type
	 * @return a new generic immutable Decimal value with scale and value copied
	 *         from the {@code decimal} argument
	 */
	public static <S extends ScaleMetrics> GenericImmutableDecimal<S> valueOf(Decimal<S> decimal) {
		return new GenericImmutableDecimal<S>(decimal);
	}

	/**
	 * Creates and returns a new {@code GenericImmutableDecimal} with the scale
	 * specified by the given {@code scaleMetrics} argument. The numeric value
	 * of new the Decimal is
	 * <code>unscaledValue &times; 10<sup>-scale</sup></code>
	 * 
	 * @param scaleMetrics
	 *            the metrics object defining the scale for the new value
	 * @param unscaled
	 *            the unscaled long value representing the new Decimal's
	 *            numerical value before applying the scale factor
	 * @param <S> the scale metrics type
	 * @return a new Decimal value representing
	 *         <code>unscaledValue &times; 10<sup>-scale</sup></code>
	 */
	public static <S extends ScaleMetrics> GenericImmutableDecimal<S> valueOfUnscaled(S scaleMetrics, long unscaled) {
		return new GenericImmutableDecimal<S>(scaleMetrics, unscaled);
	}

	/**
	 * Creates and returns a new {@code GenericImmutableDecimal} with the
	 * specified {@code scale} and value. The numeric value of new the Decimal
	 * is <code>unscaledValue &times; 10<sup>-scale</sup></code>
	 * 
	 * @param scale
	 *            the scale for the new value
	 * @param unscaled
	 *            the unscaled long value representing the new Decimal's
	 *            numerical value before applying the scale factor
	 * @return a new Decimal value representing
	 *         <code>unscaledValue &times; 10<sup>-scale</sup></code>
	 */
	public static GenericImmutableDecimal<?> valueOfUnscaled(int scale, long unscaled) {
		return valueOfUnscaled(Scales.getScaleMetrics(scale), unscaled);
	}
	
	@Override
	public S getScaleMetrics() {
		return scaleMetrics;
	}

	@Override
	public int getScale() {
		return scaleMetrics.getScale();
	}

	@Override
	public GenericDecimalFactory<S> getFactory() {
		return Factories.getGenericDecimalFactory(scaleMetrics);
	}

	@Override
	protected GenericImmutableDecimal<S> self() {
		return this;
	}

	@Override
	protected GenericImmutableDecimal<S> createOrAssign(long unscaled) {
		return unscaledValue() == unscaled ? this : create(unscaled);
	}
	
	@Override
	protected GenericImmutableDecimal<S> create(long unscaled) {
		return new GenericImmutableDecimal<S>(scaleMetrics, unscaled);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected GenericImmutableDecimal<S>[] createArray(int length) {
		return new GenericImmutableDecimal[length];
	}

	@Override
	public GenericMutableDecimal<S> toMutableDecimal() {
		return new GenericMutableDecimal<S>(this);
	}

	@Override
	public GenericImmutableDecimal<S> toImmutableDecimal() {
		return this;
	}
}
