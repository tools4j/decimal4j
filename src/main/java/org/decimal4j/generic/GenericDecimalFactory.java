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
package org.decimal4j.generic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;

/**
 * <tt>GenericDecimalFactory</tt> is a {@link DecimalFactory} for generic
 * decimal values. Different generic decimal instances of the same class can
 * have different scales as opposed to the decimal values defined in the
 * {@code immutable} and {@code mutable} packages.
 * <p>
 * The recommended way to create a generic factory is via
 * {@link Factories#getGenericDecimalFactory(ScaleMetrics)}.
 * 
 * @see GenericImmutableDecimal
 * @see GenericMutableDecimal
 */
public final class GenericDecimalFactory<S extends ScaleMetrics> implements DecimalFactory<S> {

	private final S scaleMetrics;

	/**
	 * Constructor with scale metrics argument.
	 * <p>
	 * <b>Note:</b> Application code should use
	 * {@link Factories#getGenericDecimalFactory(ScaleMetrics)} instead.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics for the new generic factory.
	 */
	public GenericDecimalFactory(S scaleMetrics) {
		this.scaleMetrics = Objects.requireNonNull(scaleMetrics, "scaleMetrics cannot be null");
	}

	@Override
	public S getScaleMetrics() {
		return scaleMetrics;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends GenericImmutableDecimal<S>> immutableType() {
		return (Class<? extends GenericImmutableDecimal<S>>) (Class<?>) GenericImmutableDecimal.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends MutableDecimal<S>> mutableType() {
		return (Class<? extends GenericMutableDecimal<S>>) (Class<?>) GenericMutableDecimal.class;
	}

	@Override
	public GenericDecimalFactory<?> deriveFactory(int scale) {
		return Factories.getGenericDecimalFactory(scale);
	}

	public <T extends ScaleMetrics> GenericDecimalFactory<T> deriveFactory(T scaleMetrics) {
		return Factories.getGenericDecimalFactory(scaleMetrics);
	}

	@Override
	public GenericImmutableDecimal<S> valueOf(long value) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getDefaultArithmetic().fromLong(value));
	}

	@Override
	public GenericImmutableDecimal<S> valueOf(float value) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getDefaultArithmetic().fromFloat(value));
	}

	@Override
	public GenericImmutableDecimal<S> valueOf(float value, RoundingMode roundingMode) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getArithmetic(roundingMode).fromFloat(value));
	}

	@Override
	public GenericImmutableDecimal<S> valueOf(double value) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getDefaultArithmetic().fromDouble(value));
	}

	@Override
	public GenericImmutableDecimal<S> valueOf(double value, RoundingMode roundingMode) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getArithmetic(roundingMode).fromDouble(value));
	}

	@Override
	public GenericImmutableDecimal<S> valueOf(BigInteger value) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getDefaultArithmetic().fromBigInteger(value));
	}

	@Override
	public GenericImmutableDecimal<S> valueOf(BigDecimal value) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getDefaultArithmetic().fromBigDecimal(value));
	}

	@Override
	public GenericImmutableDecimal<S> valueOf(BigDecimal value, RoundingMode roundingMode) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getArithmetic(roundingMode).fromBigDecimal(
				value));
	}

	@Override
	public GenericImmutableDecimal<S> valueOf(Decimal<?> value) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getDefaultArithmetic().fromUnscaled(
				value.unscaledValue(), value.getScale()));
	}

	@Override
	public GenericImmutableDecimal<S> valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getArithmetic(roundingMode).fromUnscaled(
				value.unscaledValue(), value.getScale()));
	}

	@Override
	public GenericImmutableDecimal<S> parse(String value) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getDefaultArithmetic().parse(value));
	}

	@Override
	public GenericImmutableDecimal<S> parse(String value, RoundingMode roundingMode) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getArithmetic(roundingMode).parse(value));
	}

	@Override
	public GenericImmutableDecimal<S> valueOfUnscaled(long unscaled) {
		return new GenericImmutableDecimal<S>(scaleMetrics, unscaled);
	}

	@Override
	public GenericImmutableDecimal<S> valueOfUnscaled(long unscaledValue, int scale) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getDefaultArithmetic().fromUnscaled(
				unscaledValue, scale));
	}

	@Override
	public GenericImmutableDecimal<S> valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return new GenericImmutableDecimal<S>(scaleMetrics, scaleMetrics.getArithmetic(roundingMode).fromUnscaled(
				unscaledValue, scale));
	}

	@SuppressWarnings("unchecked")
	@Override
	public GenericImmutableDecimal<S>[] newArray(int length) {
		return new GenericImmutableDecimal[length];
	}

	@Override
	public GenericMutableDecimal<S> newMutable() {
		return new GenericMutableDecimal<S>(scaleMetrics);
	}

	@SuppressWarnings("unchecked")
	@Override
	public MutableDecimal<S>[] newMutableArray(int length) {
		return new GenericMutableDecimal[length];
	}
}