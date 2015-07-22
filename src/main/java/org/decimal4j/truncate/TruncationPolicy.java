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
package org.decimal4j.truncate;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Policy defining how to handle truncation due to overflow or rounding. A
 * {@code TruncationPolicy} is uniquely defined by the two elements
 * {@link #getOverflowMode overflow mode} and {@link #getRoundingMode() rounding
 * mode}.
 * <p>
 * Truncation policies are are defined by {@link UncheckedRounding} and {@link CheckedRounding}.
 * Some special truncation policies are also defined by
 * <ul>
 * <li>{@link #DEFAULT}</li>
 * <li>{@link #VALUES}</li>
 * <li>{@link UncheckedRounding#VALUES}</li>
 * <li>{@link CheckedRounding#VALUES}</li>
 * </ul>
 */
public interface TruncationPolicy {
	/**
	 * Default truncation policy: {@link UncheckedRounding#HALF_UP}.
	 */
	TruncationPolicy DEFAULT = UncheckedRounding.HALF_UP;

	/**
	 * Unmodifiable set with all possible truncation policies.
	 */
	Set<TruncationPolicy> VALUES = Collections.unmodifiableSet(new LinkedHashSet<TruncationPolicy>(UncheckedRounding.VALUES.size(), 2f) {
		private static final long serialVersionUID = 1L;
		{
			addAll(UncheckedRounding.VALUES);
			addAll(CheckedRounding.VALUES);
		}
	});

	/**
	 * Returns the overflow mode which defines how to deal the situation when an
	 * operation that causes an overflow.
	 * 
	 * @return the mode to apply if an arithmetic operation causes an overflow
	 */
	OverflowMode getOverflowMode();

	/**
	 * Returns the rounding mode which defines how to deal the situation when an
	 * operation leads to truncation or rounding.
	 * 
	 * @return the rounding mode indicating how the least significant returned
	 *         digit of a rounded result is to be calculated
	 */
	RoundingMode getRoundingMode();
}
