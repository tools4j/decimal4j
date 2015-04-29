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

import java.math.RoundingMode;
import java.util.Objects;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.base.AbstractImmutableDecimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.OverflowMode;

/**
 * <tt>ImmutableDecimal</tt> represents an immutable decimal number with a variable
 * number of digits to the right of the decimal point.
 */
@SuppressWarnings("serial")
public final class GenericImmutableDecimal<S extends ScaleMetrics> extends AbstractImmutableDecimal<S, GenericImmutableDecimal<S>> {

	private final S scaleMetrics;
	private final DecimalFactory<S> factory;
	private final DecimalArithmetic defaultArithmetics;
	private final DecimalArithmetic defaultCheckedArithmetics;

	public GenericImmutableDecimal(S scaleMetrics, long unscaled) {
		super(unscaled);
		this.scaleMetrics = Objects.requireNonNull(scaleMetrics, "scaleMetrics cannot be null");
		this.factory = new GenericDecimalFactory<S>(scaleMetrics);
		this.defaultArithmetics = scaleMetrics.getDefaultArithmetic();
		this.defaultCheckedArithmetics = scaleMetrics.getArithmetic(OverflowMode.CHECKED.getTruncationPolicyFor(RoundingMode.HALF_UP));
	}

	public GenericImmutableDecimal(Decimal<S> decimal) {
		this(decimal.getScaleMetrics(), decimal.unscaledValue());
	}
	
	public static <S extends ScaleMetrics> GenericImmutableDecimal<S> valueOf(Decimal<S> decimal) {
		return new GenericImmutableDecimal<S>(decimal);
	}
	public static <S extends ScaleMetrics> GenericImmutableDecimal<S> valueOfUnscaled(S scaleMetrics, long unscaled) {
		return new GenericImmutableDecimal<S>(scaleMetrics, unscaled);
	}
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
	public DecimalFactory<S> getFactory() {
		return factory;
	}

	@Override
	protected GenericImmutableDecimal<S> self() {
		return this;
	}

	@Override
	protected DecimalArithmetic getDefaultArithmetic() {
		return defaultArithmetics;
	}
	
	@Override
	protected DecimalArithmetic getDefaultCheckedArithmetic() {
		return defaultCheckedArithmetics;
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
