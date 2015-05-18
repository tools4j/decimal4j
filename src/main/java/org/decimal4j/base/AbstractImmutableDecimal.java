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
package org.decimal4j.base;

import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.ImmutableDecimal;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Base class for immutable {@link Decimal} classes of different scales.
 * Arithmetic operations of immutable decimals return a new decimal instance as
 * result value hence {@link AbstractMutableDecimal mutable} decimals may be a
 * better choice for chained operations.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this mutable decimal
 */
@SuppressWarnings("serial")
abstract public class AbstractImmutableDecimal<S extends ScaleMetrics, D extends AbstractImmutableDecimal<S, D>>
		extends AbstractDecimal<S, D> implements ImmutableDecimal<S> {

	private final long unscaled;

	public AbstractImmutableDecimal(long unscaled) {
		this.unscaled = unscaled;
	}

	@Override
	public final long unscaledValue() {
		return unscaled;
	}

	@Override
	public ImmutableDecimal<?> scale(int scale) {
		return scale(scale, DecimalRounding.HALF_UP.getUncheckedTruncationPolicy());
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> ImmutableDecimal<S> scale(S scaleMetrics) {
		return scale(scaleMetrics, DecimalRounding.HALF_UP.getUncheckedTruncationPolicy());
	}

	@Override
	public ImmutableDecimal<?> scale(int scale, RoundingMode roundingMode) {
		return scale(scale, OverflowMode.UNCHECKED.getTruncationPolicyFor(roundingMode));
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> ImmutableDecimal<S> scale(S scaleMetrics, RoundingMode roundingMode) {
		return scale(scaleMetrics, OverflowMode.UNCHECKED.getTruncationPolicyFor(roundingMode));
	}

	@Override
	public ImmutableDecimal<?> scale(int scale, TruncationPolicy truncationPolicy) {
		final int myScale = getScale();
		if (scale == myScale) {
			return this;
		}
		final ScaleMetrics targetMetrics = Scales.getScaleMetrics(scale);
		final long targetUnscaled = targetMetrics.getArithmetic(truncationPolicy).fromUnscaled(unscaled, myScale);
		return getFactory().deriveFactory(targetMetrics).valueOfUnscaled(targetUnscaled);
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> ImmutableDecimal<S> scale(S scaleMetrics, TruncationPolicy truncationPolicy) {
		if (scaleMetrics == getScaleMetrics()) {
			@SuppressWarnings("unchecked")
			//safe: we know it is the same scale metrics
			final ImmutableDecimal<S> self = (ImmutableDecimal<S>) this;
			return self;
		}
		final long targetUnscaled = scaleMetrics.getArithmetic(truncationPolicy).fromUnscaled(unscaled, getScale());
		return getFactory().deriveFactory(scaleMetrics).valueOfUnscaled(targetUnscaled);
	}

	@Override
	public ImmutableDecimal<?> multiplyExact(Decimal<?> multiplicand) {
		final int targetScale = getScale() + multiplicand.getScale();
		try {
			final long unscaledProduct = getDefaultCheckedArithmetic().multiplyByLong(unscaled, multiplicand.unscaledValue());
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
}
