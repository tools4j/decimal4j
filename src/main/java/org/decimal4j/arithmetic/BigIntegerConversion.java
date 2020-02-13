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

import java.math.BigInteger;

import org.decimal4j.scale.ScaleMetrics;


/**
 * Contains methods to convert from and to {@link BigInteger}.
 */
final class BigIntegerConversion {
	
	/**
	 * Converts the specified big integer value to an unscaled decimal value. An exception is thrown if the value
	 * exceeds the valid Decimal range.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics of the result value
	 * @param value
	 *            the big integer value to convert
	 * @return the decimal value of the scale as defined by {@code ScaleMetrics}
	 * @throws IllegalArgumentException
	 *             if the value is outside of the valid Decimal range
	 */
	public static final long bigIntegerToUnscaled(ScaleMetrics scaleMetrics, BigInteger value) {
		if (value.bitLength() <= 63) {
			return LongConversion.longToUnscaled(scaleMetrics, value.longValue());
		}
		throw new IllegalArgumentException("Overflow: cannot convert " + value + " to Decimal with scale " + scaleMetrics.getScale());
	}

	// no instances
	private BigIntegerConversion() {
		super();
	}
}
