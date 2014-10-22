package ch.javasoft.decimal.truncate;

import java.math.RoundingMode;

/**
 * Mode to apply if arithmetic operations cause an overflow.
 */
public enum OverflowMode {
	/**
	 * Operations causing an overflow silently return the truncated result (the
	 * low order bytes of the extended result); no exception is thrown.
	 */
	UNCHECKED,
	/**
	 * Operations causing an overflow throw an {@link ArithmeticException}.
	 */
	CHECKED;

	/**
	 * Returns true if overflow leads to an {@link ArithmeticException}
	 * 
	 * @return true if {@code this == CHECKED}
	 */
	public boolean isChecked() {
		return this == CHECKED;
	}

	/**
	 * Returns the truncation policy for this {@code OverflowMode} and the
	 * specified {@code roundingMode}.
	 * 
	 * @param roundingMode
	 *            the rounding mode which defines the truncation policy together
	 *            with this overflow mode
	 * @return the truncation policy defined by this {@code OverflowMode} and
	 *         the specified {@code roundingMode}
	 */
	public TruncationPolicy getPolicyFor(RoundingMode roundingMode) {
		return DecimalRounding.valueOf(roundingMode).getCheckedPolicy();
	}
}
