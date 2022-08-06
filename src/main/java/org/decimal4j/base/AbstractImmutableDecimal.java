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
package org.decimal4j.base;

import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.ImmutableDecimal;
import org.decimal4j.arithmetic.Exceptions;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;

/**
 * Base class for immutable {@link Decimal} classes of different scales.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this {@code ImmutableDecimal}
 */
@SuppressWarnings("serial")
abstract public class AbstractImmutableDecimal<S extends ScaleMetrics, D extends AbstractImmutableDecimal<S, D>>
		extends AbstractDecimal<S, D>implements ImmutableDecimal<S> {

	private final long unscaled;

    /*Used to store the string representation, if computed */
    private transient String stringCache;

	/**
	 * Constructor with unscaled value.
	 * 
	 * @param unscaled
	 *            the unscaled value
	 */
	public AbstractImmutableDecimal(long unscaled) {
		this.unscaled = unscaled;
	}

	@Override
	public final long unscaledValue() {
		return unscaled;
	}

	@Override
	public ImmutableDecimal<?> scale(int scale) {
		return scale(scale, RoundingMode.HALF_UP);
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> ImmutableDecimal<S> scale(S scaleMetrics) {
		return scale(scaleMetrics, RoundingMode.HALF_UP);
	}

	@Override
	public ImmutableDecimal<?> scale(int scale, RoundingMode roundingMode) {
		final int myScale = getScale();
		if (scale == myScale) {
			return this;
		}
		final ScaleMetrics targetMetrics = Scales.getScaleMetrics(scale);
		try {
			final long targetUnscaled = targetMetrics.getArithmetic(roundingMode).fromUnscaled(unscaled, myScale);
			return getFactory().deriveFactory(targetMetrics).valueOfUnscaled(targetUnscaled);
		} catch (IllegalArgumentException e) {
			throw Exceptions.newArithmeticExceptionWithCause("Overflow: cannot convert " + this + " to scale " + scale,
					e);
		}
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> ImmutableDecimal<S> scale(S scaleMetrics, RoundingMode roundingMode) {
		if (scaleMetrics == getScaleMetrics()) {
			@SuppressWarnings("unchecked")
			// safe: we know it is the same scale metrics
			final ImmutableDecimal<S> self = (ImmutableDecimal<S>) this;
			return self;
		}
		try {
			final long targetUnscaled = scaleMetrics.getArithmetic(roundingMode).fromUnscaled(unscaled, getScale());
			return getFactory().deriveFactory(scaleMetrics).valueOfUnscaled(targetUnscaled);
		} catch (IllegalArgumentException e) {
			throw Exceptions.newArithmeticExceptionWithCause(
					"Overflow: cannot convert " + this + " to scale " + scaleMetrics.getScale(), e);
		}
	}

	@Override
	public ImmutableDecimal<?> multiplyExact(Decimal<?> multiplicand) {
		final int targetScale = getScale() + multiplicand.getScale();
		if (targetScale > Scales.MAX_SCALE) {
			throw new IllegalArgumentException("sum of scales in exact multiplication exceeds max scale "
					+ Scales.MAX_SCALE + ": " + this + " * " + multiplicand);
		}
		try {
			final long unscaledProduct = getDefaultCheckedArithmetic().multiplyByLong(unscaled,
					multiplicand.unscaledValue());
			return getFactory().deriveFactory(targetScale).valueOfUnscaled(unscaledProduct);
		} catch (ArithmeticException e) {
			throw new ArithmeticException("Overflow: " + this + " * " + multiplicand);
		}
	}

	@Override
	public ImmutableDecimal<S> min(ImmutableDecimal<S> val) {
		return isLessThanOrEqualTo(val) ? this : val;
	}

	@Override
	public ImmutableDecimal<S> max(ImmutableDecimal<S> val) {
		return isGreaterThanOrEqualTo(val) ? this : val;
	}
	
	@Override
	public final String toString() {
		String s = stringCache;
		if (s == null) {
			stringCache = s = getDefaultArithmetic().toString(unscaledValue());
		}
		return s;
	}
}
