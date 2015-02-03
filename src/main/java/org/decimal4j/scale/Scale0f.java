package org.decimal4j.scale;

import java.math.RoundingMode;
import java.util.EnumMap;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.arithmetic.CheckedScale0fRoundingArithmetic;
import org.decimal4j.arithmetic.CheckedScale0fTruncatingArithmetic;
import org.decimal4j.arithmetic.UncheckedScale0fRoundingArithmetic;
import org.decimal4j.arithmetic.UncheckedScale0fTruncatingArithmetic;
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
	protected EnumMap<RoundingMode, DecimalArithmetic> initArithmetic() {
		final EnumMap<RoundingMode, DecimalArithmetic> map = new EnumMap<RoundingMode, DecimalArithmetic>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, UncheckedScale0fTruncatingArithmetic.INSTANCE);
			} else {
				map.put(roundingMode, new UncheckedScale0fRoundingArithmetic(dr));
			}
		}
		return map;
	}

	@Override
	protected EnumMap<RoundingMode, DecimalArithmetic> initCheckedArithmetic() {
		final EnumMap<RoundingMode, DecimalArithmetic> map = new EnumMap<RoundingMode, DecimalArithmetic>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, CheckedScale0fTruncatingArithmetic.INSTANCE);
			} else {
				map.put(roundingMode, new CheckedScale0fRoundingArithmetic(dr));
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