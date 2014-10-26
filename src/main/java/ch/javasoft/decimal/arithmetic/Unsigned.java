package ch.javasoft.decimal.arithmetic;

/**
 * Helper class for division.
 */
final class Unsigned {

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

	// no instances
	private Unsigned() {
		super();
	}
}
