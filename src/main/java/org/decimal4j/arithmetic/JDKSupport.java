/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 decimal4j (tools4j), Marco Terzer
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

import java.math.BigInteger;

/**
 * Provides ports of methods that are available in JDK 1.8 to make code run in 
 * earlier JDK's.
 */
public final class JDKSupport {

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
	public static final long bigIntegerToLongValueExact(BigInteger value) {
		if (value.bitLength() <= 63) return value.longValue();
		else throw new ArithmeticException("BigInteger out of long range: " + value);
	}

	// no instances
	private JDKSupport() {
	}

}
