package ch.javasoft.decimal.arithmetic;

import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Helper class for division.
 */
class Div {

	private static final long LONG_MASK = 0xffffffffL;

	public static long scaleTo128divBy64(ScaleMetrics scaleMetrics, long uDecimalDividend, long uDecimalDivisor) {
		return scaleTo128divBy64(scaleMetrics, null, uDecimalDividend, uDecimalDivisor);
	}

	public static long scaleTo128divBy64(ScaleMetrics scaleMetrics, DecimalRounding rounding, long uDecimalDividend, long uDecimalDivisor) {
		final boolean negative = (uDecimalDividend < 0) != (uDecimalDivisor < 0);
		final long absDividend = Math.abs(uDecimalDividend);
		final long absDivisor = Math.abs(uDecimalDivisor);

		//multiply by scale factor into a 128bit integer
		final int lFactor = (int) (absDividend & LONG_MASK);
		final int hFactor = (int) (absDividend >>> 32);
		long lScaled;
		long hScaled;
		long product;

		product = scaleMetrics.mulloByScaleFactor(lFactor);
		lScaled = product & LONG_MASK;
		product = scaleMetrics.mulhiByScaleFactor(lFactor) + (product >>> 32);
		hScaled = product >>> 32;
		product = scaleMetrics.mulloByScaleFactor(hFactor) + (product & LONG_MASK);
		lScaled |= ((product & LONG_MASK) << 32);
		hScaled = scaleMetrics.mulhiByScaleFactor(hFactor) + hScaled + (product >>> 32);

		//divide 128 bit product by 64 bit divisor
		return div128by64(negative, hScaled, lScaled, absDivisor, rounding);
	}

	/**
	 * Divides a 128 bit divisor by a 64 bit dividend and returns the signed 64
	 * bit result. Rounding is applied if {@code rounding != null}, otherwise
	 * the value is truncated.
	 * <p>
	 * From <a
	 * href="http://www.codeproject.com/Tips/785014/UInt-Division-Modulus"
	 * >www.codeproject.com</a>.
	 * 
	 * @param neg
	 *            true if result is negative
	 * @param u1
	 *            high order 64 bits of dividend
	 * @param u0
	 *            low order 64 bits of dividend
	 * @param v0
	 *            64 bit divisor
	 * @param rounding
	 *            rounding to apply, or null to truncate result
	 * @return the signed quotient, rounded if {@code rounding != null}
	 */
	public static long div128by64(final boolean neg, final long u1, final long u0, final long v0, final DecimalRounding rounding) {
		long q;
		long r;

		long un1, un0, vn1, vn0, q1, q0, un32, un21, un10, rhat, left, right;
		final int s = Long.numberOfLeadingZeros(v0);

		final long v = v0 << s;
		vn1 = v >>> 32;
		vn0 = v & LONG_MASK;

		if (s > 0) {
			un32 = (u1 << s) | (u0 >>> (64 - s));
			un10 = u0 << s;
		} else {
			un32 = u1;
			un10 = u0;
		}

		un1 = un10 >>> 32;
		un0 = un10 & LONG_MASK;

		q1 = unsignedDiv64by32(un32, vn1);
		rhat = un32 - q1 * vn1;

		left = q1 * vn0;
		right = (rhat << 32) + un1;

		while (((q1 >>> 32) != 0) | unsignedLongCompare(left, right)) {
			--q1;
			rhat += vn1;
			if ((rhat >>> 32) != 0) {
				break;
			}
			left -= vn0;
			right = (rhat << 32) | un1;
		}

		un21 = (un32 << 32) + (un1 - (q1 * v));

		q0 = unsignedDiv64by32(un21, vn1);
		rhat = un21 - q0 * vn1;

		left = q0 * vn0;
		right = (rhat << 32) | un0;

		while (((q0 >>> 32) != 0) | unsignedLongCompare(left, right)) {
			--q0;
			rhat += vn1;
			if ((rhat >>> 32) != 0) {
				break;
			}
			left -= vn0;
			right = (rhat << 32) | un0;
		}

		r = ((un21 << 32) + (un0 - (q0 * v))) >>> s;
		q = (q1 << 32) | q0;

		//apply sign and rounding
		if (rounding == null || rounding == DecimalRounding.DOWN) {
			return neg ? -q : q;
		}
		final TruncatedPart truncatedPart = TruncatedPart.valueOf(Math.abs(r), Math.abs(v0));
		final int inc = rounding.calculateRoundingIncrement(neg ? -1 : 1, q, truncatedPart);
		return (neg ? -q : q) + inc;
	}

	/**
	 * 
	 * Returns dividend / divisor, where the dividend and divisor are treated as
	 * unsigned 64-bit quantities.
	 * <p>
	 * From Guava's <a href=
	 * "http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/primitives/UnsignedLongs.html"
	 * >UnsignedLongs</a>.
	 *
	 * @param dividend
	 *            the dividend (numerator)
	 * @param divisor
	 *            the divisor (denominator)
	 * @throws ArithmeticException
	 *             if divisor is 0
	 * 
	 */
	private static long unsignedDiv64by32(long dividend, long divisor) {
		// Optimization - use signed division if dividend < 2^63
		if (dividend >= 0) {
			return dividend / divisor;
		}

		/*
		 * Otherwise, approximate the quotient, check, and correct if necessary.
		 * Our approximation is guaranteed to be either exact or one less than
		 * the correct value. This follows from fact that floor(floor(x)/i) ==
		 * floor(x/i) for any real x and integer i != 0. The proof is not quite
		 * trivial.
		 */
		final long quotient = ((dividend >>> 1) / divisor) << 1;
		final long rem = dividend - quotient * divisor;
		return quotient + (((rem > divisor) | (rem < 0)) ? 1 : 0);
	}

	/**
	 * 
	 * Returns dividend / divisor, where the dividend and divisor are treated as
	 * unsigned 64-bit quantities.
	 * <p>
	 * From Guava's <a href=
	 * "http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/primitives/UnsignedLongs.html"
	 * >UnsignedLongs</a>.
	 *
	 * @param dividend
	 *            the dividend (numerator)
	 * @param divisor
	 *            the divisor (denominator)
	 * @throws ArithmeticException
	 *             if divisor is 0
	 * @return result of unsigned division {@code (dividend / divisor)}
	 */
	public static long unsignedDiv64by64(long dividend, long divisor) {
		if (divisor < 0) { // i.e., divisor >= 2^63:
			if (compare(dividend, divisor) < 0) {
				return 0; // dividend < divisor
			} else {
				return 1; // dividend >= divisor
			}
		}

		// Optimization - use signed division if dividend < 2^63
		if (dividend >= 0) {
			return dividend / divisor;
		}

		/*
		 * Otherwise, approximate the quotient, check, and correct if necessary.
		 * Our approximation is guaranteed to be either exact or one less than
		 * the correct value. This follows from fact that floor(floor(x)/i) ==
		 * floor(x/i) for any real x and integer i != 0. The proof is not quite
		 * trivial.
		 */
		final long quotient = ((dividend >>> 1) / divisor) << 1;
		final long rem = dividend - quotient * divisor;
		return quotient + (compare(rem, divisor) >= 0 ? 1 : 0);
	}

	/**
	 * A (self-inverse) bijection which converts the ordering on unsigned longs
	 * to the ordering on longs, that is, {@code a <= b} as unsigned longs if
	 * and only if {@code flip(a) <= flip(b)} as signed longs.
	 * <p>
	 * From Guava's <a href=
	 * "http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/primitives/UnsignedLongs.html"
	 * >UnsignedLongs</a>.
	 */
	private static long flip(long a) {
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
	public static int compare(long a, long b) {
		return Long.compare(flip(a), flip(b));
	}

	/**
	 * Compare two longs as if they were unsigned. Returns true iff one is
	 * bigger than two.
	 */
	private static boolean unsignedLongCompare(long one, long two) {
		return (one + Long.MIN_VALUE) > (two + Long.MIN_VALUE);
	}
}
