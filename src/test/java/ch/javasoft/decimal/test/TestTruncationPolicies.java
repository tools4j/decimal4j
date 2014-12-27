package ch.javasoft.decimal.test;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

import ch.javasoft.decimal.truncate.DecimalRounding;
import ch.javasoft.decimal.truncate.OverflowMode;
import ch.javasoft.decimal.truncate.TruncationPolicy;

import com.google.common.collect.ImmutableList;

public enum TestTruncationPolicies {
	
	TINY(Arrays.asList(DecimalRounding.DOWN.getUncheckedTruncationPolicy(), DecimalRounding.HALF_UP.getUncheckedTruncationPolicy(), 
			DecimalRounding.DOWN.getCheckedTruncationPolicy(), DecimalRounding.UNNECESSARY.getCheckedTruncationPolicy())),
	
	SMALL(Arrays.asList(DecimalRounding.DOWN.getUncheckedTruncationPolicy(), DecimalRounding.HALF_UP.getUncheckedTruncationPolicy(), DecimalRounding.UNNECESSARY.getUncheckedTruncationPolicy(), 
			DecimalRounding.DOWN.getCheckedTruncationPolicy(), DecimalRounding.HALF_UP.getCheckedTruncationPolicy(), DecimalRounding.UNNECESSARY.getCheckedTruncationPolicy())),
	
	STANDARD(Arrays.asList(DecimalRounding.UP.getUncheckedTruncationPolicy(), DecimalRounding.DOWN.getUncheckedTruncationPolicy(), DecimalRounding.HALF_UP.getUncheckedTruncationPolicy(), DecimalRounding.HALF_EVEN.getUncheckedTruncationPolicy(), DecimalRounding.UNNECESSARY.getUncheckedTruncationPolicy(), 
			DecimalRounding.UP.getCheckedTruncationPolicy(), DecimalRounding.DOWN.getCheckedTruncationPolicy(), DecimalRounding.HALF_UP.getCheckedTruncationPolicy(), DecimalRounding.HALF_EVEN.getCheckedTruncationPolicy(), DecimalRounding.UNNECESSARY.getCheckedTruncationPolicy())),
	
	ALL(TruncationPolicy.VALUES);
	
	private final Collection<TruncationPolicy> policies;
	
	private TestTruncationPolicies(Collection<TruncationPolicy> policies) {
		this.policies = ImmutableList.copyOf(filterSupportedPolicies(policies));
	}
	
	//FIXME remove this method
	private Collection<TruncationPolicy> filterSupportedPolicies(Collection<TruncationPolicy> policies) {
		final Set<TruncationPolicy> supported = new LinkedHashSet<TruncationPolicy>();
		for (final TruncationPolicy p : policies) {
			if (!p.getOverflowMode().isChecked() || RoundingMode.DOWN.equals(p.getRoundingMode())) {
				supported.add(p);
			}
			
			// FIXME
			if (p.getOverflowMode().isChecked() || RoundingMode.DOWN != p.getRoundingMode()) {
				supported.add(p);
			}
			
		}
		return supported;
	}
	
	public Collection<TruncationPolicy> getPolicies() {
		return policies;
	}
	
	public Set<RoundingMode> getUncheckedRoundingModes() {
		return getRoundingModesFor(OverflowMode.UNCHECKED);
	}
	
	public Set<RoundingMode> getRoundingModesFor(OverflowMode overflowMode) {
		final Set<RoundingMode> rounding = EnumSet.noneOf(RoundingMode.class);
		for (final TruncationPolicy policy : getPolicies()) {
			if (overflowMode.equals(policy.getOverflowMode())) {
				rounding.add(policy.getRoundingMode());
			}
		}
		return Collections.unmodifiableSet(rounding);
	}

}
