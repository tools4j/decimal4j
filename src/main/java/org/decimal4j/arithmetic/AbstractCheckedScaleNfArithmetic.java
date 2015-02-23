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
package org.decimal4j.arithmetic;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;

/**
 * Base class for arithmetic implementations with overflow check for scales
 * other than zero.
 */
abstract public class AbstractCheckedScaleNfArithmetic extends
		AbstractCheckedArithmetic {

	private final ScaleMetrics scaleMetrics;

	/* The unchecked version of arithmetic with the same rounding mode */
	protected final DecimalArithmetic unchecked;

	public AbstractCheckedScaleNfArithmetic(ScaleMetrics scaleMetrics) {
		this.scaleMetrics = scaleMetrics;
		this.unchecked = scaleMetrics.getArithmetic(getRoundingMode());
	}

	public AbstractCheckedScaleNfArithmetic(ScaleMetrics scaleMetrics, DecimalRounding rounding) {
		this.scaleMetrics = scaleMetrics;
		this.unchecked = scaleMetrics.getArithmetic(rounding.getRoundingMode());
	}

	@Override
	public ScaleMetrics getScaleMetrics() {
		return scaleMetrics;
	}

	@Override
	public OverflowMode getOverflowMode() {
		return OverflowMode.CHECKED;
	}

	@Override
	public long fromLong(long value) {
		return getScaleMetrics().multiplyByScaleFactorExact(value);
	}

	@Override
	public final long fromUnscaled(long unscaledValue, int scale) {
		return Scale.rescale(this, unscaledValue, scale, getScale());
	}

	@Override
	public long toLong(long uDecimal) {
		return unchecked.toLong(uDecimal);
	}

	@Override
	public float toFloat(long uDecimal) {
		return unchecked.toFloat(uDecimal);
	}

	@Override
	public double toDouble(long uDecimal) {
		return unchecked.toDouble(uDecimal);
	}

	@Override
	public String toString(long uDecimal) {
		return StringConversion.unscaledToString(this, uDecimal);
	}

}
