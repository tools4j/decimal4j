package ch.javasoft.decimal.truncate;

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

	static Set<TruncationPolicy> values() {
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
