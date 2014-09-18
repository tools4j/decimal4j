package ch.javasoft.decimal.op;

import static org.junit.Assert.assertEquals;

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
	private final ArithmeticException exception;

	private ArithmeticResult(String resultString, T compareValue, ArithmeticException exception) {
		this.resultString = resultString;
		this.compareValue = compareValue;
		this.exception = exception;
	}

	public static <T> ArithmeticResult<T> forResult(String resultString, T comparableValue) {
		return new ArithmeticResult<T>(resultString, comparableValue, null);
	}
	public static <T> ArithmeticResult<T> forException(ArithmeticException e) {
		return new ArithmeticResult<T>(null, null, e);
	}

	public void assertEquivalentTo(ArithmeticResult<T> expected, String messagePrefix) {
		if ((expected.exception == null) != (exception == null)) {
			if (expected.exception != null) {
		        throw new AssertionError(messagePrefix + " was " + resultString + " but should lead to an exception: " + expected.exception, expected.exception);
			} else {
		        throw new AssertionError(messagePrefix + " = " + expected.resultString + " but lead to an exception: " + exception, exception);
			}
		} else {
			assertEquals(messagePrefix + " = " + expected.resultString, expected.compareValue, compareValue);
		}
	}
	
	@Override
	public String toString() {
		if (exception == null) {
			return getClass().getSimpleName() + "[" + resultString + ":" + compareValue + "]";
		}
		return getClass().getSimpleName() + "[" + exception + "]";
	}
}