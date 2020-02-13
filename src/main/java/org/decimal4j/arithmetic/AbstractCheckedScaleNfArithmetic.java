/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 decimal4j (tools4j), Marco Terzer
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

import java.io.IOException;

import org.decimal4j.scale.ScaleMetrics;

/**
 * Base class for arithmetic implementations with overflow check for scales
 * other than zero.
 */
abstract public class AbstractCheckedScaleNfArithmetic extends AbstractCheckedArithmetic {

	private final ScaleMetrics scaleMetrics;

	/**
	 * Constructor with scale metrics for this arithmetic.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics
	 */
	public AbstractCheckedScaleNfArithmetic(ScaleMetrics scaleMetrics) {
		this.scaleMetrics = scaleMetrics;
	}

	@Override
	public final ScaleMetrics getScaleMetrics() {
		return scaleMetrics;
	}

	@Override
	public final int getScale() {
		return scaleMetrics.getScale();
	}

	@Override
	public final long one() {
		return scaleMetrics.getScaleFactor();
	}

	@Override
	public final long addLong(long uDecimal, long lValue) {
		return Add.addUnscaledLongChecked(this, uDecimal, lValue);
	}

	@Override
	public final long subtractLong(long uDecimal, long lValue) {
		return Sub.subtractUnscaledLongChecked(this, uDecimal, lValue);
	}

	@Override
	public final String toString(long uDecimal) {
		return StringConversion.unscaledToString(this, uDecimal);
	}
	
	@Override
	public final void toString(long uDecimal, Appendable appendable) throws IOException {
		StringConversion.unscaledToString(this, uDecimal, appendable);
	}
}
