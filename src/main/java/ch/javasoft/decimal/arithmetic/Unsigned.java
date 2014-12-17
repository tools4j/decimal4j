package ch.javasoft.decimal.arithmetic;

/**
 * Helper class for division.
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
		return JDKSupport.longCompare(flip(a), flip(b));
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
	public static boolean isGreater(long one, long two) {
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
	public static boolean isLess(long one, long two) {
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
	public static boolean isLessOrEqual(long one, long two) {
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
	public static long divide(long dividend, long divisor) {
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
		long quotient = ((dividend >>> 1) / divisor) << 1;
		long rem = dividend - quotient * divisor;
		return quotient + (isLess(rem, divisor) ? 0 : 1);
	}

	// no instances
	private Unsigned() {
		super();
	}
}
