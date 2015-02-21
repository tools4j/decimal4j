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
package org.decimal4j.op;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.arithmetic.JDKSupport;

/**
 * Result of an arithmetic operation which can also sometimes lead to an
 * {@link ArithmeticException}. The assertion is based on a comparable value
 * of type {@code <T>}.
 *
 * @param <T> the type of the compared value
 */
class ArithmeticResult<T> {
	private final String resultString;
	private final T compareValue;
	private final Exception exception;

	private ArithmeticResult(String resultString, T compareValue, ArithmeticException exception) {
		this.resultString = resultString;
		this.compareValue = compareValue;
		this.exception = exception;
	}
	private ArithmeticResult(String resultString, T compareValue, IllegalArgumentException exception) {
		this.resultString = resultString;
		this.compareValue = compareValue;
		this.exception = exception;
	}
	private ArithmeticResult(String resultString, T compareValue, NullPointerException exception) {
		this.resultString = resultString;
		this.compareValue = compareValue;
		this.exception = exception;
	}

	public static <T> ArithmeticResult<T> forResult(String resultString, T comparableValue) {
		return new ArithmeticResult<T>(resultString, comparableValue, (ArithmeticException)null);
	}
	public static ArithmeticResult<Long> forResult(DecimalArithmetic arithmetic, BigDecimal result) {
		final BigDecimal rnd = result.setScale(arithmetic.getScale(), arithmetic.getRoundingMode());
		final long resultUnscaled = arithmetic.getOverflowMode().isChecked() ? JDKSupport.bigIntegerToLongValueExact(rnd.unscaledValue()) : rnd.unscaledValue().longValue();
		return forResult(result.toPlainString(), resultUnscaled);
	}
	public static ArithmeticResult<Long> forResult(Decimal<?> result) {
		return forResult(result.toString(), result.unscaledValue());
	}
	public static <T> ArithmeticResult<T> forException(ArithmeticException e) {
		return new ArithmeticResult<T>(null, null, e);
	}
	public static <T> ArithmeticResult<T> forException(IllegalArgumentException e) {
		return new ArithmeticResult<T>(null, null, e);
	}
	public static <T> ArithmeticResult<T> forException(NullPointerException e) {
		return new ArithmeticResult<T>(null, null, e);
	}

	public void assertEquivalentTo(ArithmeticResult<T> expected, String messagePrefix) {
		if ((expected.exception == null) != (exception == null)) {
			if (expected.exception != null) {
				// XXX use proper throws declaration
		        throw (AssertionError)new AssertionError(messagePrefix + " was " + resultString + " but should lead to an exception: " + expected.exception).initCause(expected.exception);
			} else {
		        throw (AssertionError)new AssertionError(messagePrefix + " = " + expected.resultString + " but lead to an exception: " + exception).initCause(exception);
			}
		} else if ((expected.exception != null) != (exception != null) && expected.exception.getClass() != expected.exception.getClass()) {
	        throw (AssertionError)new AssertionError(messagePrefix + " exception lead to exception " + exception + " but expected was exception type: " + expected.exception).initCause(expected.exception);
		} else {
			assertEquals(messagePrefix + " = " + expected.resultString, expected.compareValue, compareValue);
		}
	}
	
	public T getCompareValue() {
		return compareValue;
	}
	
	@Override
	public String toString() {
		if (exception == null) {
			return getClass().getSimpleName() + "[" + resultString + ":" + compareValue + "]";
		}
		return getClass().getSimpleName() + "[" + exception + "]";
	}
}