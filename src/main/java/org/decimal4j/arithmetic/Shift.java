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
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncatedPart;

/**
 * Provides methods for left and right shifts.
 */
final class Shift {

	/**
	 * Performs a shift left operation applying the given rounding mode if
	 * rounding is necessary. Overflows are siltently truncated.
	 * 
	 * @param rounding
	 *            the rounding to apply for negative position (i.e. a right
	 *            shift)
	 * @param uDecimal
	 *            the value to shift
	 * @param positions
	 *            the positions to shift
	 * @return <tt>round(uDecimal << positions)</tt>
	 */
	public static final long shiftLeft(DecimalRounding rounding, long uDecimal, int positions) {
		if (positions >= 0) {
			return positions < Long.SIZE ? uDecimal << positions : 0;
		}
		// one shift missing for (-Integer.MIN_VALUE) but does not matter as
		// result is always between 0 (incl) and 0.5 (excl)
		return shiftRight(rounding, uDecimal, -positions > 0 ? -positions : Integer.MAX_VALUE);
	}

	/**
	 * Performs a shift right operation applying the given rounding mode if
	 * rounding is necessary. Overflows are siltently truncated.
	 * 
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param uDecimal
	 *            the value to shift
	 * @param positions
	 *            the positions to shift
	 * @return <tt>round(uDecimal >> positions)</tt>
	 */
	public static final long shiftRight(DecimalRounding rounding, long uDecimal, int positions) {
		if (uDecimal == 0 | positions == 0) {
			return uDecimal;
		}
		if (positions >= 0) {
			if (rounding == DecimalRounding.FLOOR) {
				return positions < Long.SIZE ? uDecimal >> positions : (uDecimal >= 0 ? 0 : -1);
			}
			if (positions < Long.SIZE) {
				final long truncated = uDecimal >= 0 ? (uDecimal >>> positions) : -(-uDecimal >>> positions);
				final long remainder = uDecimal - (truncated << positions);
				final TruncatedPart truncatedPart = positions == 63 ? Rounding.truncatedPartFor2pow63(remainder)
						: Rounding.truncatedPartFor(Math.abs(remainder), 1L << positions);
				return truncated + rounding.calculateRoundingIncrement(Long.signum(uDecimal), truncated, truncatedPart);
			}
			if (positions == Long.SIZE) {
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0,
						Rounding.truncatedPartFor2pow64(Math.abs(uDecimal)));
			}
			return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0,
					TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
		return positions > -Long.SIZE ? uDecimal << -positions : 0;
	}

	/**
	 * Performs a shift left operation applying the given rounding mode if
	 * rounding is necessary. Throws an exception if an overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the shifted value
	 * @param rounding
	 *            the rounding to apply for negative position (i.e. a right
	 *            shift)
	 * @param uDecimal
	 *            the value to shift
	 * @param positions
	 *            the positions to shift
	 * @return <tt>round(uDecimal << positions)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and the arithmetic's
	 *             {@link OverflowMode} is set to throw an exception
	 */
	public static final long shiftLeftChecked(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal, int positions) {
		if (positions >= 0) {
			if (uDecimal == 0 | positions == 0) {
				return uDecimal;
			}
			if (positions < Long.SIZE) {
				if (uDecimal > 0) {
					if (positions < Long.SIZE - 1) {
						final int leadingZeros = Long.numberOfLeadingZeros(uDecimal);
						if (leadingZeros > positions) {
							return uDecimal << positions;
						}
					}
				} else if (uDecimal > Long.MIN_VALUE) {
					final int leadingZeros = Long.numberOfLeadingZeros(~uDecimal);
					if (leadingZeros > positions) {
						return uDecimal << positions;
					}
				}
			}
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " << " + positions + " = "
					+ arith.toString(uDecimal << positions));
		}
		// one shift missing for (-Integer.MIN_VALUE) but does not matter as
		// result is always between 0 (incl) and 0.5 (excl)
		return shiftRight(rounding, uDecimal, -positions > 0 ? -positions : Integer.MAX_VALUE);
	}

	/**
	 * Performs a shift right operation applying the given rounding mode if
	 * rounding is necessary. Throws an exception if an overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the shifted value
	 * @param rounding
	 *            the rounding to apply if necessary
	 * @param uDecimal
	 *            the value to shift
	 * @param positions
	 *            the positions to shift
	 * @return <tt>round(uDecimal >> positions)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and the arithmetic's
	 *             {@link OverflowMode} is set to throw an exception
	 */
	public static final long shiftRightChecked(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal, int positions) {
		if (uDecimal == 0) {
			return 0;
		}
		if (positions >= 0) {
			return shiftRight(rounding, uDecimal, positions);
		}
		if (positions > -Long.SIZE) {
			try {
				return shiftLeftChecked(arith, rounding, uDecimal, -positions);
			} catch (ArithmeticException e) {
				// ignore, throw again below with correct shift direction
			}
		}
		throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " >> " + positions + " = "
				+ arith.toString(uDecimal >> positions));
	}

	// no instances
	private Shift() {
		super();
	}
}
