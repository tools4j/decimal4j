package org.decimal4j.truncate;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.Set;

/**
 * Policy defining how to handle truncation due to overflow or rounding. A
 * {@code TruncationPolicy} is uniquely defined by the two elements
 * {@link #getOverflowMode overflow mode} and {@link #getRoundingMode() rounding
 * mode}.
 * <p>
 * Truncation policies can be accessed as follows:
 * <ul>
 * <li>{@link #DEFAULT}</li>
 * <li>{@link #VALUES}</li>
 * <li>{@link OverflowMode#getTruncationPolicyFor(RoundingMode)}</li>
 * <li>{@link DecimalRounding#getUncheckedTruncationPolicy()}</li>
 * <li>{@link DecimalRounding#getCheckedTruncationPolicy()}</li>
 * </ul>
 */
public interface TruncationPolicy {
	/**
	 * Default truncation policy using {@link OverflowMode#UNCHECKED} and
	 * {@link RoundingMode#HALF_UP}.
	 */
	TruncationPolicy DEFAULT = DecimalRounding.HALF_UP.getUncheckedTruncationPolicy();
	
	/**
	 * Unmodifiable set with all possible truncation policies.
	 */
	Set<TruncationPolicy> VALUES = Collections.unmodifiableSet(DefaultTruncationPolicy.values());

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
