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

import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;

/**
 * Utility class to calculate rounding increments in different situations; 
 * utilizes functionality provided by {@link DecimalRounding} and
 * {@link TruncatedPart}.
 */
final class RoundingUtil {

	/**
	 * Returns the rounding increment appropriate for the specified
	 * {@code rounding}. The returned value is one of -1, 0 or 1.
	 * 
	 * @param rounding
	 *            the rounding mode to apply
	 * @param sign
	 *            the sign of the total value, either +1 or -1; determines the
	 *            result value if rounded
	 * @param truncatedValue
	 *            the truncated result before rounding is applied (only used for
	 *            HALF_EVEN rounding)
	 * @param firstTruncatedDigit
	 *            the first truncated digit, must be in {@code [0, 1, ..., 9]}
	 * @param zeroAfterFirstTruncatedDigit
	 *            true if all truncated digits after the first truncated digit
	 *            are zero, and false otherwise
	 * @return the value to add to {@code truncatedValue} to get the rounded
	 *         result, one of -1, 0 or 1
	 */
	public static int calculateRoundingIncrement(DecimalRounding rounding, int sign, long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
		return rounding.calculateRoundingIncrement(sign, truncatedValue, TruncatedPart.valueOf(firstTruncatedDigit, zeroAfterFirstTruncatedDigit));
	}

	/**
	 * Returns the rounding increment appropriate for the specified
	 * {@code rounding} given the remaining truncated digits truncated by a
	 * given divisor. The returned value is one of -1, 0 or 1.
	 * 
	 * @param rounding
	 *            the rounding mode to apply
	 * @param truncatedValue
	 *            the truncated result before rounding is applied (only used for
	 *            HALF_EVEN rounding)
	 * @param truncatedDigits
	 *            the truncated part, it most hold that
	 *            {@code abs(truncatedDigits) < abs(divisor)}
	 * @param divisor
	 *            the divisor that led to the truncated digits
	 * @return the value to add to {@code truncatedValue} to get the rounded
	 *         result, one of -1, 0 or 1
	 */
	public static int calculateRoundingIncrementForDivision(DecimalRounding rounding, long truncatedValue, long truncatedDigits, long divisor) {
		if (truncatedDigits == 0) {
			return 0;
		}
		final TruncatedPart truncatedPart = truncatedPartFor(Math.abs(truncatedDigits), Math.abs(divisor));
		return rounding.calculateRoundingIncrement(Long.signum(truncatedDigits ^ divisor), truncatedValue, truncatedPart);
	}

	/**
	 * Returns the rounding increment appropriate for the specified
	 * {@code rounding} given the remaining truncated digits truncated by modulo
	 * one. The returned value is one of -1, 0 or 1.
	 * 
	 * @param rounding
	 *            the rounding mode to apply
	 * @param truncatedValue
	 *            the truncated result before rounding is applied (only used for
	 *            HALF_EVEN rounding)
	 * @param truncatedDigits
	 *            the truncated part of a double, must be {@code >-one} and
	 *            {@code <one}
	 * @param one
	 *            the value representing 1 which is {@code 10^scale}, must be
	 *            {@code >= 10}
	 * @return the value to add to {@code truncatedValue} to get the rounded
	 *         result, one of -1, 0 or 1
	 */
	public static final int calculateRoundingIncrement(DecimalRounding rounding, long truncatedValue, long truncatedDigits, long one) {
		if (truncatedDigits == 0) {
			return 0;
		}
		final TruncatedPart truncatedPart = truncatedPartFor(Math.abs(truncatedDigits), one);
		return rounding.calculateRoundingIncrement(Long.signum(truncatedDigits), truncatedValue, truncatedPart);
	}

	/**
	 * Returns a truncated part constant given a non-negative remainder
	 * resulting from a division by the given non-negative divisor.
	 * 
	 * @param nonNegativeRemainder
	 *            the remainder part, not negative and
	 *            {@code nonNegativeRemainder < nonNegativeDivisor}
	 * @param nonNegativeDivisor
	 *            the divisor, not negative or LONG.MIN_VALUE --- the latter
	 *            equal to {@code abs(Long.MIN_VALUE)}
	 * @return the truncated part constant equivalent to the given arguments
	 */
	public static final TruncatedPart truncatedPartFor(long nonNegativeRemainder, long nonNegativeDivisor) {
		if (nonNegativeRemainder == 0) {
			return TruncatedPart.ZERO;
		}
		final long halfNonNegativeDivisor = nonNegativeDivisor >>> 1;
		//NOTE: halfNonNegativeDivisor cannot be zero, because if it was 1 then nonNegativeRemainder was 0

		if (halfNonNegativeDivisor < nonNegativeRemainder) {
			return TruncatedPart.GREATER_THAN_HALF;
		}
		if ((nonNegativeDivisor & 0x1) == 0 & halfNonNegativeDivisor == nonNegativeRemainder) {
			return TruncatedPart.EQUAL_TO_HALF;
		}
		return TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
	}

	/**
	 * Returns a truncated part constant given a non-negative remainder
	 * resulting from a division by 10^19.
	 * 
	 * @param remainder
	 *            the remainder part
	 * @return the truncated part constant equivalent to the given arguments
	 */
	public static final TruncatedPart truncatedPartForScale19(long remainder) {
		if (remainder == 0) {
			return TruncatedPart.ZERO;
		}
		if (5000000000000000000L > remainder & remainder > -5000000000000000000L) {
			return TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
		}
		if (remainder == 5000000000000000000L | remainder == -5000000000000000000L) {
			return TruncatedPart.EQUAL_TO_HALF;
		}
		return TruncatedPart.GREATER_THAN_HALF;
	}

	/**
	 * Returns a truncated part constant given a non-negative remainder
	 * resulting from a division by 2^n
	 * 
	 * @param remainder
	 *            the remainder part
	 * @param n
	 *            the power of 2 of the divisor, must be > 0
	 * @return the truncated part constant equivalent to the given arguments
	 */
	public static final TruncatedPart truncatedPartFor2powN(long remainder, int n) {
		if (n < 63) {
			return truncatedPartFor(remainder, 1L << n);
		} else if (n == 63) {
			return truncatedPartFor2pow63(remainder);
		} else if (n == 64) {
			return truncatedPartFor2pow64(remainder);
		} else {
			return remainder == 0 ? TruncatedPart.ZERO : TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
		}
	}

	/**
	 * Returns a truncated part constant given a non-negative 128 bit remainder
	 * resulting from a division by 2^n
	 * 
	 * @param hRemainder
	 *            the high bits of the remainder part
	 * @param lRemainder
	 *            the low bits of the remainder part
	 * @param n
	 *            the power of 2 of the divisor, must be > 0
	 * @return the truncated part constant equivalent to the given arguments
	 */
	public static final TruncatedPart truncatedPartFor2powN(long hRemainder, long lRemainder, int n) {
		if (hRemainder == 0) {
			return truncatedPartFor2powN(lRemainder, n);
		}
		final TruncatedPart hPart = truncatedPartFor2powN(hRemainder, n - Long.SIZE);
		switch (hPart) {
		case ZERO:
			return lRemainder == 0 ? TruncatedPart.ZERO : TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
		case LESS_THAN_HALF_BUT_NOT_ZERO:
			return hPart; 
		case EQUAL_TO_HALF:
			return lRemainder == 0 ? TruncatedPart.EQUAL_TO_HALF: TruncatedPart.GREATER_THAN_HALF;
		case GREATER_THAN_HALF:
			return hPart; 
		default:
			throw new RuntimeException("internal error: unsupported truncated part: " + hPart);
		}
	}

	/**
	 * Returns a truncated part constant given a non-negative remainder
	 * resulting from a division by 2^63
	 * 
	 * @param remainder
	 *            the remainder part
	 * @return the truncated part constant equivalent to the given arguments
	 */
	public static final TruncatedPart truncatedPartFor2pow63(long remainder) {
		if (remainder == 0) {
			return TruncatedPart.ZERO;
		}
		if ((1L << 62) > remainder & remainder > -(1L << 62)) {
			return TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
		}
		if (remainder == (1L << 62) | remainder == -(1L << 62)) {
			return TruncatedPart.EQUAL_TO_HALF;
		}
		return TruncatedPart.GREATER_THAN_HALF;
	}

	/**
	 * Returns a truncated part constant given a non-negative remainder
	 * resulting from a division by 2^64
	 * 
	 * @param remainder
	 *            the remainder part
	 * @return the truncated part constant equivalent to the given arguments
	 */
	public static final TruncatedPart truncatedPartFor2pow64(long remainder) {
		if (remainder == 0) {
			return TruncatedPart.ZERO;
		}
		if (remainder == 0x8000000000000000L) {
			return TruncatedPart.EQUAL_TO_HALF;
		}
		if (remainder < 0) {
			return TruncatedPart.GREATER_THAN_HALF;
		}
		return TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
	}

	// no instances
	private RoundingUtil() {
		super();
	}
}
