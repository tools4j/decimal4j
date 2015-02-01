package org.decimal4j.scale;

import java.math.RoundingMode;
import java.util.EnumMap;

import org.decimal4j.arithmetic.CheckedScale0fRoundingArithmetics;
import org.decimal4j.arithmetic.CheckedScale0fTruncatingArithmetics;
import org.decimal4j.arithmetic.DecimalArithmetics;
import org.decimal4j.arithmetic.UncheckedScale0fRoundingArithmetics;
import org.decimal4j.arithmetic.UncheckedScale0fTruncatingArithmetics;
import org.decimal4j.truncate.DecimalRounding;

/**
 * Scale class for decimals with {@link #getScale() scale} 0 (aka as integers)
 * and {@link #getScaleFactor() scale factor} 1.
 */
public final class Scale0f extends AbstractScale {

	/**
	 * The singleton instance for scale 0.
	 */
	public static final Scale0f INSTANCE = new Scale0f();

	@Override
	protected EnumMap<RoundingMode, DecimalArithmetics> initArithmetics() {
		final EnumMap<RoundingMode, DecimalArithmetics> map = new EnumMap<RoundingMode, DecimalArithmetics>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, UncheckedScale0fTruncatingArithmetics.INSTANCE);
			} else {
				map.put(roundingMode, new UncheckedScale0fRoundingArithmetics(dr));
			}
		}
		return map;
	}

	@Override
	protected EnumMap<RoundingMode, DecimalArithmetics> initCheckedArithmetics() {
		final EnumMap<RoundingMode, DecimalArithmetics> map = new EnumMap<RoundingMode, DecimalArithmetics>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, CheckedScale0fTruncatingArithmetics.INSTANCE);
			} else {
				map.put(roundingMode, new CheckedScale0fRoundingArithmetics(dr));
			}
		}
		return map;
	}

	@Override
	public int getScale() {
		return 0;
	}

	@Override
	public long getScaleFactor() {
		return 1;
	}

	@Override
	public final int getScaleFactorNumberOfLeadingZeros() {
		return 63;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor;
	}

	@Override
	public long multiplyByScaleFactorExact(long factor) {
		return factor;
	}
	
	@Override
	public long mulloByScaleFactor(int factor) {
		return factor & LONG_MASK;
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return 0;
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend;
	}
	
	@Override
	public long divideUnsignedByScaleFactor(long unsignedDividend) {
		return unsignedDividend;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return 0;
	}
}