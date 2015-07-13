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
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Default implementation of {@link TruncationPolicy}. Instances can be accessed
 * as follows:
 * <ul>
 * <li>{@link TruncationPolicy#DEFAULT}</li>
 * <li>{@link TruncationPolicy#VALUES}</li>
 * <li>{@link OverflowMode#getTruncationPolicyFor(RoundingMode)}</li>
 * <li>{@link DecimalRounding#getUncheckedTruncationPolicy()}</li>
 * <li>{@link DecimalRounding#getCheckedTruncationPolicy()}</li>
 * </ul>
 */
final class DefaultTruncationPolicy implements TruncationPolicy {

	private final OverflowMode overflowMode;
	private final RoundingMode roundingMode;

	DefaultTruncationPolicy(OverflowMode overflowMode, DecimalRounding decimalRounding) {
		this.overflowMode = overflowMode;
		this.roundingMode = decimalRounding.getRoundingMode();
	}

	static final Set<TruncationPolicy> values() {
		final Set<TruncationPolicy> values = new LinkedHashSet<TruncationPolicy>(2*DecimalRounding.VALUES.size());
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			values.add(dr.getUncheckedTruncationPolicy());
		}
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			values.add(dr.getCheckedTruncationPolicy());
		}
		return values;
	}
	@Override
	public final OverflowMode getOverflowMode() {
		return overflowMode;
	}

	@Override
	public final RoundingMode getRoundingMode() {
		return roundingMode;
	}

	@Override
	public final String toString() {
		return TruncationPolicy.class.getSimpleName() + "[overflow=" + overflowMode + ", rounding=" + roundingMode + "]";
	}

}
