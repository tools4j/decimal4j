package org.decimal4j.arithmetic;

import java.math.BigInteger;

/**
 * Some methods available in JDK 1.8 or JDK 1.7 ported to make code run in earlier JDK's.
 */
public final class JDKSupport {

    /**
     * Compares two {@code long} values numerically.
     * The value returned is identical to what would be returned by:
     * <pre>
     *    Long.valueOf(x).compareTo(Long.valueOf(y))
     * </pre>
     *
     * @param  x the first {@code long} to compare
     * @param  y the second {@code long} to compare
     * @return the value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code x < y}; and
     *         a value greater than {@code 0} if {@code x > y}
     * @since 1.7
     */
    public static int longCompare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
	 * Copied from {@code BigInteger.longValueExact()} added in Java 1.8.
	 * <p>
	 * Converts the {@code BigInteger} argument to a {@code long}, checking for lost
	 * information. If the value of this {@code BigInteger} is out of the range
	 * of the {@code long} type, then an {@code ArithmeticException} is thrown.
	 * 
	 * @param value the {@code BigInteger} value to convert to a long
	 * @return {@code value} converted to a {@code long}.
	 * @throws ArithmeticException
	 *             if the {@code value} will not exactly fit in a {@code long}.
	 * @since JDK 1.8
	 */
	public static long bigIntegerToLongValueExact(BigInteger value) {
		if (value.bitLength() <= 63) return value.longValue();
		else throw new ArithmeticException("BigInteger out of long range: " + value);
	}

	// no instances
	private JDKSupport() {
	}

}
