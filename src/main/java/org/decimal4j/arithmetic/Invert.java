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
import org.decimal4j.truncate.DecimalRounding;

/**
 * Provides static methods to invert a Decimal number, that is, to calculate {@code 1/x}.
 */
final class Invert {

	public static final long invertLong(long uDecimal) {
		if (uDecimal == 0) {
			throw new ArithmeticException("Division by zero: " + uDecimal + "^-1");
		}
		if (uDecimal == 1) {
			return 1;
		}
		if (uDecimal == -1) {
			return -1;
		}
		return 0;
	}

	public static final long invertLong(DecimalRounding rounding, long uDecimal) {
		//special cases first
		if (uDecimal == 0) {
			throw new ArithmeticException("Division by zero: " + uDecimal + "^-1");
		}
		if (uDecimal == 1) {
			return 1;
		}
		if (uDecimal == -1) {
			return -1;
		}
		return RoundingUtil.calculateRoundingIncrementForDivision(rounding, 0, 1, uDecimal);
	}
	
	public static final long invert(DecimalArithmetic arith, long uDecimal) {
		//special cases are handled by divide
		return arith.divide(arith.one(), uDecimal);
	}

	public static final long invert(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal) {
		//special cases are handled by divide
		return arith.divide(arith.one(), uDecimal);
	}

	//no instances
	private Invert() {
		super();
	}
}
