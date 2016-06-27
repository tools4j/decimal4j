/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 decimal4j (tools4j), Marco Terzer
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

import java.math.RoundingMode;

/**
 * Utility for exception conversion and re-throwing.
 */
public final class Exceptions {

	private static final String ROUNDING_NECESSARY = "Rounding necessary";

	/**
	 * Returns a new {@link ArithmeticException} with the given {@code message}
	 * and nested {@code cause}.
	 * 
	 * @param message
	 *            the exception message
	 * @param cause
	 *            the causing exception
	 * @return an arithmetic exception with the given message and cause
	 */
	public static final ArithmeticException newArithmeticExceptionWithCause(String message, Exception cause) {
		return (ArithmeticException) new ArithmeticException(message).initCause(cause);
	}

	/**
	 * Returns new {@link ArithmeticException} indicating that rounding was
	 * necessary when attempting to apply rounding with
	 * {@link RoundingMode#UNNECESSARY}.
	 * 
	 * @return an arithmetic exception with the message "Rounding necessary"
	 */
	public static final ArithmeticException newRoundingNecessaryArithmeticException() {
		return new ArithmeticException(ROUNDING_NECESSARY);
	}

	/**
	 * Rethrows the given arithmetic exception if its message equals
	 * "Rounding necessary". Otherwise the method does nothing.
	 * 
	 * @param e
	 *            the exception to rethrow if it is of the "Rounding necessary"
	 *            type
	 * @throws ArithmeticException
	 *             rethrows the given exception {@code e} if its message equals
	 *             "Rounding necessary"
	 */
	public static final void rethrowIfRoundingNecessary(ArithmeticException e) {
		if (ROUNDING_NECESSARY.equals(e.getMessage())) {
			throw e;
		}
	}

	// no instances
	private Exceptions() {
		super();
	}
}
