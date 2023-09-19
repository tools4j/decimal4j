/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 decimal4j (tools4j), Marco Terzer
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

/**
 * Helper class to emulate unsigned 64bit operations.
 */
public final class Unsigned {

	/**
	 * A (self-inverse) bijection which converts the ordering on unsigned longs
	 * to the ordering on longs, that is, {@code a <= b} as unsigned longs if
	 * and only if {@code flip(a) <= flip(b)} as signed longs.
	 * <p>
	 * From Guava's <a href=
	 * "http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/primitives/UnsignedLongs.html"
	 * >UnsignedLongs</a>.
	 * 
	 * @param a the unsigned long value to flip
	 * @return the flipped value
	 */
	private static final long flip(long a) {
		return a ^ Long.MIN_VALUE;
	}

	/**
	 * Compares the two specified {@code long} values, treating them as unsigned
	 * values between {@code 0} and {@code 2^64 - 1} inclusive.
	 * <p>
	 * From Guava's <a href=
	 * "http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/primitives/UnsignedLongs.html"
	 * >UnsignedLongs</a>.
	 *
	 * @param a
	 *            the first unsigned {@code long} to compare
	 * @param b
	 *            the second unsigned {@code long} to compare
	 * @return a negative value if {@code a} is less than {@code b}; a positive
	 *         value if {@code a} is greater than {@code b}; or zero if they are
	 *         equal
	 */
	public static final int compare(long a, long b) {
		return Long.compare(flip(a), flip(b));
	}

	/**
	 * Compare two longs as if they were unsigned. Returns true iff one is
	 * bigger than two.
	 * 
	 * @param one
	 *            the first unsigned {@code long} to compare
	 * @param two
	 *            the second unsigned {@code long} to compare
	 * @return true if {@code one > two}
	 */
	public static final boolean isGreater(long one, long two) {
		return flip(one) > flip(two);
	}

	/**
	 * Compare two longs as if they were unsigned. Returns true iff one is
	 * smaller than two.
	 * 
	 * @param one
	 *            the first unsigned {@code long} to compare
	 * @param two
	 *            the second unsigned {@code long} to compare
	 * @return true if {@code one < two}
	 */
	public static final boolean isLess(long one, long two) {
		return flip(one) < flip(two);
	}

	/**
	 * Compare two longs as if they were unsigned. Returns true iff one is less
	 * than or equal to two.
	 * 
	 * @param one
	 *            the first unsigned {@code long} to compare
	 * @param two
	 *            the second unsigned {@code long} to compare
	 * @return true if {@code one <= two}
	 */
	public static final boolean isLessOrEqual(long one, long two) {
		return flip(one) <= flip(two);
	}

	/**
	 * Returns dividend / divisor, where the dividend and divisor are treated as
	 * unsigned 64-bit quantities.
	 *
	 * @param dividend
	 *            the dividend (numerator)
	 * @param divisor
	 *            the divisor (denominator)
	 * @return {@code dividend / divisor}
	 * @throws ArithmeticException
	 *             if divisor is 0
	 */
	public static final long divide(long dividend, long divisor) {
		if (divisor < 0) { // i.e., divisor >= 2^63:
			return compare(dividend, divisor) < 0 ? 0 : 1;
		}

		// Optimization - use signed division if dividend < 2^63
		if (dividend >= 0) {
			return dividend / divisor;
		}
		// If divisor is even, we can divide both by 2
		if (0 == (divisor & 0x1)) {
			return (dividend >>> 1) / (divisor >>> 1);
		}

		/*
		 * Otherwise, approximate the quotient, check, and correct if necessary.
		 * Our approximation is guaranteed to be either exact or one less than
		 * the correct value. This follows from fact that floor(floor(x)/i) ==
		 * floor(x/i) for any real x and integer i != 0. The proof is not quite
		 * trivial.
		 */
		long quotient = ((dividend >>> 1) / divisor) << 1;
		long rem = dividend - quotient * divisor;
		return quotient + (isLess(rem, divisor) ? 0 : 1);
	}

	private Unsigned() {
		// no instances
	}

}
