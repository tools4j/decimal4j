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
package org.decimal4j.test;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncationPolicy;

public enum TestTruncationPolicies {
	
	TINY(Arrays.asList(DecimalRounding.DOWN.getUncheckedTruncationPolicy(), DecimalRounding.HALF_UP.getUncheckedTruncationPolicy(), 
			DecimalRounding.DOWN.getCheckedTruncationPolicy(), DecimalRounding.UNNECESSARY.getCheckedTruncationPolicy())),
	
	SMALL(Arrays.asList(DecimalRounding.DOWN.getUncheckedTruncationPolicy(), DecimalRounding.HALF_UP.getUncheckedTruncationPolicy(), DecimalRounding.UNNECESSARY.getUncheckedTruncationPolicy(), 
			DecimalRounding.DOWN.getCheckedTruncationPolicy(), DecimalRounding.HALF_UP.getCheckedTruncationPolicy())),
	
	STANDARD(Arrays.asList(DecimalRounding.DOWN.getUncheckedTruncationPolicy(), DecimalRounding.HALF_UP.getUncheckedTruncationPolicy(), DecimalRounding.HALF_EVEN.getUncheckedTruncationPolicy(), DecimalRounding.UNNECESSARY.getUncheckedTruncationPolicy(), 
			DecimalRounding.DOWN.getCheckedTruncationPolicy(), DecimalRounding.HALF_UP.getCheckedTruncationPolicy())),
	
	LARGE(Arrays.asList(DecimalRounding.UP.getUncheckedTruncationPolicy(), DecimalRounding.DOWN.getUncheckedTruncationPolicy(), DecimalRounding.HALF_UP.getUncheckedTruncationPolicy(), DecimalRounding.HALF_EVEN.getUncheckedTruncationPolicy(), DecimalRounding.UNNECESSARY.getUncheckedTruncationPolicy(), 
			DecimalRounding.UP.getCheckedTruncationPolicy(), DecimalRounding.DOWN.getCheckedTruncationPolicy(), DecimalRounding.HALF_UP.getCheckedTruncationPolicy(), DecimalRounding.HALF_EVEN.getCheckedTruncationPolicy(), DecimalRounding.UNNECESSARY.getCheckedTruncationPolicy())),
			
	ALL(TruncationPolicy.VALUES);
	
	private final Collection<TruncationPolicy> policies;
	
	private TestTruncationPolicies(Collection<TruncationPolicy> policies) {
		this.policies = Collections.unmodifiableCollection(policies);
	}
	
	public Collection<TruncationPolicy> getPolicies() {
		return policies;
	}
	
	public Collection<TruncationPolicy> getCheckedPolicies() {
		final List<TruncationPolicy> policies = new ArrayList<TruncationPolicy>();
		for (final TruncationPolicy policy : getPolicies()) {
			if (policy.getOverflowMode().isChecked()) {
				policies.add(policy);
			}
		}
		return Collections.unmodifiableCollection(policies);
	}
	public Set<RoundingMode> getUncheckedRoundingModes() {
		final Set<RoundingMode> rounding = EnumSet.noneOf(RoundingMode.class);
		for (final TruncationPolicy policy : getPolicies()) {
			if (!policy.getOverflowMode().isChecked()) {
				rounding.add(policy.getRoundingMode());
			}
		}
		return Collections.unmodifiableSet(rounding);
	}

}
