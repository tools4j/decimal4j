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
package org.decimal4j.arithmetic;

import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.OverflowMode;

/**
 * Base class for arithmetic implementations with {@link OverflowMode#CHECKED
 * CHECKED} overflow mode.
 */
abstract public class AbstractCheckedArithmetic extends AbstractArithmetic {
	
	//override to refine return type
	@Override
	abstract public CheckedRounding getTruncationPolicy();

	@Override
	public final OverflowMode getOverflowMode() {
		return OverflowMode.CHECKED;
	}

	@Override
	public final long add(long uDecimal1, long uDecimal2) {
		return Checked.add(this, uDecimal1, uDecimal2);
	}

	@Override
	public final long subtract(long uDecimalMinuend, long uDecimalSubtrahend) {
		return Checked.subtract(this, uDecimalMinuend, uDecimalSubtrahend);
	}

	@Override
	public final long multiplyByLong(long uDecimal, long lValue) {
		return Checked.multiplyByLong(this, uDecimal, lValue);
	}

	@Override
	public final long abs(long uDecimal) {
		return Checked.abs(this, uDecimal);
	}

	@Override
	public final long negate(long uDecimal) {
		return Checked.negate(this, uDecimal);
	}

}
