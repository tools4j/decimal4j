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
import org.decimal4j.base.AbstractMutableDecimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.OverflowMode;

@SuppressWarnings("serial")
public final class GenericMutableDecimal<S extends ScaleMetrics> extends AbstractMutableDecimal<S, GenericMutableDecimal<S>> implements Cloneable {

	private final S scaleMetrics;
	private final DecimalFactory<S> factory;
	private final DecimalArithmetic defaultArithmetics;
	private final DecimalArithmetic defaultCheckedArithmetics;
	
	/**
	 * Creates a new {@code GenericMutableDecimal} with value zero.
	 */
	public GenericMutableDecimal(S scaleMetrics) {
		this(scaleMetrics, 0);
	}

	/**
	 * Creates a new {@code GenericMutableDecimal} with the same value as 
	 * the given {@code decimal} argument.
	 * 
	 * @param decimal the numeric value to assign to the created mutable decimal
	 */
	public GenericMutableDecimal(Decimal<S> decimal) {
		this(decimal.getScaleMetrics(), decimal.unscaledValue());
	}

	public GenericMutableDecimal(S scaleMetrics, long unscaledValue) {
		super(unscaledValue);
		this.scaleMetrics = Objects.requireNonNull(scaleMetrics, "scaleMetrics cannot be null");
		this.factory = new GenericDecimalFactory<S>(scaleMetrics);
		this.defaultArithmetics = scaleMetrics.getDefaultArithmetic();
		this.defaultCheckedArithmetics = scaleMetrics.getArithmetic(OverflowMode.CHECKED.getTruncationPolicyFor(RoundingMode.HALF_UP));
	}

	public static <S extends ScaleMetrics> GenericMutableDecimal<S> valueOf(Decimal<S> decimal) {
		return new GenericMutableDecimal<S>(decimal);
	}
	public static <S extends ScaleMetrics> GenericMutableDecimal<S> valueOfUnscaled(S scaleMetrics, long unscaled) {
		return new GenericMutableDecimal<S>(scaleMetrics, unscaled);
	}
	public static GenericMutableDecimal<?> valueOfUnscaled(int scale, long unscaled) {
		return valueOfUnscaled(Scales.getScaleMetrics(scale), unscaled);
	}

	@Override
	protected GenericMutableDecimal<S> create(long unscaled) {
		return new GenericMutableDecimal<S>(scaleMetrics, unscaled);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected GenericMutableDecimal<S>[] createArray(int length) {
		return new GenericMutableDecimal[length];
	}

	@Override
	protected GenericMutableDecimal<S> self() {
		return this;
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
	protected DecimalArithmetic getDefaultArithmetic() {
		return defaultArithmetics;
	}
	
	@Override
	protected DecimalArithmetic getDefaultCheckedArithmetic() {
		return defaultCheckedArithmetics;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GenericMutableDecimal<S> clone() {
		try {
			return (GenericMutableDecimal<S>)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("clone should be supported", e);
		}
	}

	@Override
	public GenericImmutableDecimal<S> toImmutableDecimal() {
		return new GenericImmutableDecimal<S>(this);
	}

	@Override
	public GenericMutableDecimal<S> toMutableDecimal() {
		return this;
	}
}
