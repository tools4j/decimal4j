package org.decimal4j.scale;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.EnumMap;

import org.decimal4j.Decimal;
import org.decimal4j.arithmetic.CheckedScaleNfRoundingArithmetics;
import org.decimal4j.arithmetic.CheckedScaleNfTruncatingArithmetics;
import org.decimal4j.arithmetic.DecimalArithmetics;
import org.decimal4j.arithmetic.UncheckedScaleNfRoundingArithmetics;
import org.decimal4j.arithmetic.UncheckedScaleNfTruncatingArithmetics;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * <tt>ScaleMetrics</tt> is associated with {@link Decimal} numbers and
 * represents the factor applied to the {@code long} value underlying a
 * {@code Decimal}. Scale stands for the fixed number of fraction digits of a
 * {@code Decimal}.
 * <p>
 * The <tt>Scale</tt> class contains a number of subclasses used by different
 * decimal types. With <tt>Scale</tt> subclasses, it is possible to distinguish
 * different decimal types and we can ensure that only decimals of the same
 * scale can directly operate with each other.
 */
abstract public class AbstractScale implements ScaleMetrics {

	/**
	 * This mask is used to obtain the value of an int as if it were unsigned.
	 */
	final static long LONG_MASK = 0xffffffffL;

	private final long maxIntegerValue = divideByScaleFactor(Long.MAX_VALUE);
	private final long minIntegerValue = divideByScaleFactor(Long.MIN_VALUE);
	private final BigInteger biScaleFactor = BigInteger.valueOf(getScaleFactor());
	private final BigDecimal bdScaleFactor = new BigDecimal(biScaleFactor);

	private final EnumMap<RoundingMode, DecimalArithmetics> roundingModeToArithmetics = initArithmetics();
	private final EnumMap<RoundingMode, DecimalArithmetics> roundingModeToCheckedArithmetics = initCheckedArithmetics();

	/**
	 * Initialises the arithmetics map. {@link Scale0f} overrides this method.
	 * 
	 * @return the rounding mode to arithmetics map
	 */
	protected EnumMap<RoundingMode, DecimalArithmetics> initArithmetics() {
		final EnumMap<RoundingMode, DecimalArithmetics> map = new EnumMap<RoundingMode, DecimalArithmetics>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, new UncheckedScaleNfTruncatingArithmetics(this));
			} else {
				map.put(roundingMode, new UncheckedScaleNfRoundingArithmetics(this, dr));
			}
		}
		return map;
	}

	/**
	 * Initialises the checked arithmetics map. {@link Scale0f} overrides this
	 * method.
	 * 
	 * @return the rounding mode to checked arithmetics map
	 * @see OverflowMode#CHECKED
	 */
	protected EnumMap<RoundingMode, DecimalArithmetics> initCheckedArithmetics() {
		final EnumMap<RoundingMode, DecimalArithmetics> map = new EnumMap<RoundingMode, DecimalArithmetics>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, new CheckedScaleNfTruncatingArithmetics(this));
			} else {
				map.put(roundingMode, new CheckedScaleNfRoundingArithmetics(this, dr));
			}
		}
		return map;
	}

	@Override
	public final BigInteger getScaleFactorAsBigInteger() {
		return biScaleFactor;
	}

	@Override
	public final BigDecimal getScaleFactorAsBigDecimal() {
		return bdScaleFactor;
	}

	@Override
	public final long getMaxIntegerValue() {
		return maxIntegerValue;
	}

	@Override
	public final long getMinIntegerValue() {
		return minIntegerValue;
	}

	@Override
	public final DecimalArithmetics getDefaultArithmetics() {
		return getArithmetics(TruncationPolicy.DEFAULT);
	}

	@Override
	public final DecimalArithmetics getTruncatingArithmetics(OverflowMode overflowMode) {
		return overflowMode == OverflowMode.UNCHECKED ? roundingModeToArithmetics.get(RoundingMode.DOWN) : roundingModeToCheckedArithmetics.get(RoundingMode.DOWN);
	}

	@Override
	public final DecimalArithmetics getArithmetics(RoundingMode roundingMode) {
		return roundingModeToArithmetics.get(roundingMode);
	}

	@Override
	public final DecimalArithmetics getArithmetics(TruncationPolicy truncationPolicy) {
		final OverflowMode overflow = truncationPolicy.getOverflowMode();
		final RoundingMode rounding = truncationPolicy.getRoundingMode();
		return overflow == OverflowMode.UNCHECKED ? roundingModeToArithmetics.get(rounding) : roundingModeToCheckedArithmetics.get(rounding);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
