package ch.javasoft.decimal.truncate;

import java.math.RoundingMode;

/**
 * Default implementation of {@link TruncationPolicy}. Instances can be accessed
 * through one of the following methods:
 * <ul>
 * <li>{@link TruncationPolicy#DEFAULT}</li>
 * <li>{@link OverflowMode#getPolicyFor(RoundingMode)}</li>
 * <li>{@link DecimalRounding#getUncheckedPolicy()}</li>
 * <li>{@link DecimalRounding#getCheckedPolicy()}</li>
 * </ul>
 */
final class DefaultTruncationPolicy implements TruncationPolicy {

	private final OverflowMode overflowMode;
	private final RoundingMode roundingMode;

	DefaultTruncationPolicy(OverflowMode overflowMode, DecimalRounding decimalRounding) {
		this.overflowMode = overflowMode;
		this.roundingMode = decimalRounding.getRoundingMode();
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
