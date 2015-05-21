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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.truncate.OverflowMode;

/**
 * Base class for arithmetic implementations with {@link OverflowMode#CHECKED
 * CHECKED} overflow mode.
 */
abstract public class AbstractCheckedArithmetic extends AbstractArithmetic {

	@Override
	public OverflowMode getOverflowMode() {
		return OverflowMode.CHECKED;
	}

	@Override
	public long add(long uDecimal1, long uDecimal2) {
		return Checked.add(this, uDecimal1, uDecimal2);
	}

	@Override
	public long subtract(long uDecimalMinuend, long uDecimalSubtrahend) {
		return Checked.subtract(this, uDecimalMinuend, uDecimalSubtrahend);
	}

	@Override
	public long multiplyByLong(long uDecimal, long lValue) {
		return Checked.multiplyByLong(this, uDecimal, lValue);
	}

	@Override
	public long abs(long uDecimal) {
		return Checked.abs(this, uDecimal);
	}

	@Override
	public long negate(long uDecimal) {
		return Checked.negate(this, uDecimal);
	}

	@Override
	public long fromLong(long value) {
		return getScaleMetrics().multiplyByScaleFactorExact(value);
	}

	@Override
	public long fromBigInteger(BigInteger value) {
		if (value.bitLength() <= 63) {
			return fromLong(value.longValue());
		}
		throw new ArithmeticException("Overflow: cannot convert " + value + " to Decimal with scale " + getScale());
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		//TODO any chance to make this garbage free? 
		//Difficult as we cannot look inside the BigDecimal value
		final BigDecimal scaled = value.multiply(getScaleMetrics().getScaleFactorAsBigDecimal()).setScale(0, getRoundingMode());
		return JDKSupport.bigIntegerToLongValueExact(scaled.toBigInteger());
	}
}
