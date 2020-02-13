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

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.DecimalRounding;

/**
 * Provides static methods to calculate average of two numbers, that is,
 * {@code (a+b)/2}.
 */
final class Avg {

	/**
	 * Calculates and returns the average of the two values rounded DOWN.
	 * 
	 * @param a
	 *            the first value
	 * @param b
	 *            the second value
	 * @return <tt>round<sub>DOWN</sub>((a + b) / 2)</tt>
	 */
	public static final long avg(long a, long b) {
		final long xor = a ^ b;
		final long floor = (a & b) + (xor >> 1);
		return floor + ((floor >>> 63) & xor);
	}

	/**
	 * Calculates and returns the average of the two values applying the given
	 * roundign if necessary.
	 * 
	 * @param arith
	 *            the arithmetic associated with the two values
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param a
	 *            the first value
	 * @param b
	 *            the second value
	 * @return <tt>round((a + b) / 2)</tt>
	 */
	public static final long avg(DecimalArithmetic arith, DecimalRounding rounding, long a, long b) {
		final long xor = a ^ b;
		switch (rounding) {
		case FLOOR: {
			return (a & b) + (xor >> 1);
		}
		case CEILING: {
			return (a | b) - (xor >> 1);
		}
		case DOWN:// fallthrough
		case HALF_DOWN: {
			final long floor = (a & b) + (xor >> 1);
			return floor + ((floor >>> 63) & xor);
		}
		case UP:// fallthrough
		case HALF_UP: {
			final long floor = (a & b) + (xor >> 1);
			return floor + ((~floor >>> 63) & xor);
		}
		case HALF_EVEN: {
			final long xorShifted = xor >> 1;
			final long floor = (a & b) + xorShifted;
			// use ceiling if floor is odd
			return ((floor & 0x1) == 0) ? floor : (a | b) - xorShifted;
		}
		case UNNECESSARY: {
			final long floor = (a & b) + (xor >> 1);
			if ((xor & 0x1) != 0) {
				throw new ArithmeticException("Rounding necessary: " + arith.toString(a) + " avg " + arith.toString(b)
						+ " = " + arith.toString(floor));
			}
			return floor;
		}
		default: {
			// should not get here
			throw new IllegalArgumentException("Unsupported rounding mode: " + rounding);
		}
		}
	}

	// no instances
	private Avg() {
		super();
	}
}
