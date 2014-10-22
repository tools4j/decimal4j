package ch.javasoft.decimal.truncate;

import java.math.RoundingMode;

/**
 * Policy defining how to handle truncation due to overflow or rounding. A
 * {@code TruncationPolicy} is uniquely defined by the two elements
 * {@link #getOverflowMode overflow mode} and {@link #getRoundingMode() rounding
 * mode}.
 * <p>
 * Truncation policies can be accessed through one of the following methods:
 * <ul>
 * <li>{@link #DEFAULT}</li>
 * <li>{@link OverflowMode#getPolicyFor(RoundingMode)}</li>
 * <li>{@link DecimalRounding#getUncheckedPolicy()}</li>
 * <li>{@link DecimalRounding#getCheckedPolicy()}</li>
 * </ul>
 */
public interface TruncationPolicy {
	/**
	 * Default truncation policy using {@link OverflowMode#UNCHECKED} and
	 * {@link RoundingMode#HALF_UP}.
	 */
	TruncationPolicy DEFAULT = DecimalRounding.HALF_UP.getUncheckedPolicy();

	/**
	 * Specifies the overflow behavior for numerical operations causing an
	 * overflow.
	 * 
	 * @return the mode to apply if an arithmetic operation causes an overflow
	 */
	OverflowMode getOverflowMode();

	/**
	 * Specifies the rounding behavior for numerical operations capable of
	 * discarding precision.
	 * 
	 * @return the rounding mode indicating how the least significant returned
	 *         digit of a rounded result is to be calculated
	 */
	RoundingMode getRoundingMode();
}
