package ch.javasoft.decimal.arithmetic;

import java.math.BigInteger;

/**
 * Some methods available in the JDK 1.8 ported to make code run in earlier JDK's.
 */
public final class Java8 {

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
	private Java8() {
		super();
	}

}
