package ch.javasoft.decimal;

/**
 * Mode to apply if arithmetic operations cause an overflow.
 */
public enum OverflowMode {
	/**
	 * Operations causing an overflow silently return the truncated result; no
	 * exception is thrown.
	 */
	SILENT,
	/**
	 * Operations causing an overflow throw an {@link ArithmeticException};
	 * underflow causes no exception.
	 */
	EXCEPTION
}
