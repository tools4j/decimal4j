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

import java.io.IOException;

import org.decimal4j.scale.Scale0f;

/**
 * Base class for arithmetic implementations with overflow check for the special
 * case with {@link Scale0f}, that is, for longs.
 */
abstract public class AbstractCheckedScale0fArithmetic extends AbstractCheckedArithmetic {

	@Override
	public final Scale0f getScaleMetrics() {
		return Scale0f.INSTANCE;
	}

	@Override
	public final int getScale() {
		return 0;
	}

	@Override
	public final long one() {
		return 1;
	}

	@Override
	public final long addLong(long uDecimal, long lValue) {
		return Checked.add(this, uDecimal, lValue);
	}

	@Override
	public final long subtractLong(long uDecimal, long lValue) {
		return Checked.subtract(this, uDecimal, lValue);
	}

	@Override
	public final long multiply(long uDecimal1, long uDecimal2) {
		return Checked.multiplyByLong(this, uDecimal1, uDecimal2);
	}

	@Override
	public final long square(long uDecimal) {
		return Checked.multiplyByLong(this, uDecimal, uDecimal);
	}

	@Override
	public final long fromLong(long value) {
		return value;
	}

	@Override
	public final long toLong(long uDecimal) {
		return uDecimal;
	}

	@Override
	public final String toString(long uDecimal) {
		return StringConversion.longToString(uDecimal);
	}
	
	@Override
	public final void toString(long uDecimal, Appendable appendable) throws IOException {
		StringConversion.longToString(uDecimal, appendable);
	}

}
