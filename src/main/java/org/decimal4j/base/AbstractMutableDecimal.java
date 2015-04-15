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
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Base class for mutable {@link Decimal} classes of different scales.
 * Arithmetic operations of mutable decimals modify the state of {@code this}
 * {@code Decimal} and return {@code this} as result value.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this mutable decimal
 */
@SuppressWarnings("serial")
abstract public class AbstractMutableDecimal<S extends ScaleMetrics, D extends AbstractMutableDecimal<S, D>>
		extends AbstractDecimal<S, D> implements MutableDecimal<S> {

	private long unscaled;

	/**
	 * Constructor with specified unscaled value.
	 * 
	 * @param unscaled
	 *            the unscaled decimal value
	 */
	public AbstractMutableDecimal(long unscaled) {
		this.unscaled = unscaled;
	}

	@Override
	public final long unscaledValue() {
		return unscaled;
	}

	/**
	 * Returns {@code this} decimal after assigning the value
	 * <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>.
	 * 
	 * @param unscaled
	 *            unscaled value to assign to this {@code Decimal}
	 * @return {@code this} decimal value now representing
	 *         <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>
	 */
	@Override
	protected D createOrAssign(long unscaled) {
		this.unscaled = unscaled;
		return self();
	}

	@Override
	public MutableDecimal<?> scale(int scale) {
		return scale(scale, DecimalRounding.HALF_UP.getUncheckedTruncationPolicy());
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> MutableDecimal<S> scale(S scaleMetrics) {
		return scale(scaleMetrics, DecimalRounding.HALF_UP.getUncheckedTruncationPolicy());
	}

	@Override
	public MutableDecimal<?> scale(int scale, RoundingMode roundingMode) {
		return scale(scale, OverflowMode.UNCHECKED.getTruncationPolicyFor(roundingMode));
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> MutableDecimal<S> scale(S scaleMetrics, RoundingMode roundingMode) {
		return scale(scaleMetrics, OverflowMode.UNCHECKED.getTruncationPolicyFor(roundingMode));
	}

	@Override
	public MutableDecimal<?> scale(int scale, TruncationPolicy truncationPolicy) {
		final int myScale = getScale();
		if (scale == myScale) {
			return this;
		}
		final ScaleMetrics targetMetrics = Scales.getScaleMetrics(scale);
		final long targetUnscaled = targetMetrics.getArithmetic(truncationPolicy).fromUnscaled(unscaled, myScale);
		return Factories.getDecimalFactory(scale).newMutable().setUnscaled(targetUnscaled);
	}

	@Override
	@SuppressWarnings("hiding")
	public <S extends ScaleMetrics> MutableDecimal<S> scale(S scaleMetrics, TruncationPolicy truncationPolicy) {
		if (scaleMetrics == getScaleMetrics()) {
			@SuppressWarnings("unchecked")
			//safe: we know it is the same scale metrics
			final MutableDecimal<S> self = (MutableDecimal<S>) this;
			return self;
		}
		final long targetUnscaled = scaleMetrics.getArithmetic(truncationPolicy).fromUnscaled(unscaled, getScale());
		return Factories.getDecimalFactory(scaleMetrics).newMutable().setUnscaled(targetUnscaled);
	}

	@Override
	public MutableDecimal<?> multiplyExact(Decimal<?> multiplicand) {
		final int targetScale = getScale() + multiplicand.getScale();
		try {
			final long unscaledProduct = getCheckedArithmeticFor(RoundingMode.DOWN).multiplyByLong(unscaled, multiplicand.unscaledValue());
			return Factories.getDecimalFactory(targetScale).newMutable().setUnscaled(unscaledProduct);
		} catch (ArithmeticException e) {
			throw new ArithmeticException("Overflow: " + this + " * " + multiplicand);
		}
	}

	@Override
	public D setZero() {
		unscaled = 0;
		return self();
	}

	@Override
	public D setOne() {
		unscaled = getScaleMetrics().getScaleFactor();
		return self();
	}

	@Override
	public D setMinusOne() {
		unscaled = -getScaleMetrics().getScaleFactor();
		return self();
	}

	@Override
	public D setUlp() {
		unscaled = 1;
		return self();
	}

	@Override
	public D set(Decimal<S> value) {
		return setUnscaled(value.unscaledValue());
	}

	@Override
	public D set(Decimal<?> value, RoundingMode roundingMode) {
		return setUnscaled(value.unscaledValue(), value.getScale(), roundingMode);
	}

	@Override
	public D set(long value) {
		unscaled = getDefaultCheckedArithmetic().fromLong(value);
		return self();
	}

	@Override
	public D set(float value) {
		unscaled = getDefaultCheckedArithmetic().fromFloat(value);
		return self();
	}

	@Override
	public D set(float value, RoundingMode roundingMode) {
		unscaled = getCheckedArithmeticFor(roundingMode).fromFloat(value);
		return self();
	}

	@Override
	public D set(double value) {
		unscaled = getDefaultCheckedArithmetic().fromDouble(value);
		return self();
	}

	@Override
	public D set(double value, RoundingMode roundingMode) {
		unscaled = getCheckedArithmeticFor(roundingMode).fromDouble(value);
		return self();
	}

	@Override
	public D setUnscaled(long unscaledValue) {
		unscaled = unscaledValue;
		return self();
	}

	@Override
	public D setUnscaled(long unscaledValue, int scale) {
		unscaled = getDefaultCheckedArithmetic().fromUnscaled(unscaledValue, scale);
		return self();
	}

	@Override
	public D setUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		unscaled = getCheckedArithmeticFor(roundingMode).fromUnscaled(unscaledValue, scale);
		return self();
	}

	@Override
	public D set(String value) {
		unscaled = getDefaultCheckedArithmetic().parse(value);
		return self();
	}

	@Override
	public D set(String value, RoundingMode roundingMode) {
		unscaled = getCheckedArithmeticFor(roundingMode).parse(value);
		return self();
	}

	@Override
	public MutableDecimal<S> min(MutableDecimal<S> val) {
		return isLessThanOrEqualTo(val) ? this : val;
	}

	@Override
	public MutableDecimal<S> max(MutableDecimal<S> val) {
		return isGreaterThanOrEqualTo(val) ? this : val;
	}
}
