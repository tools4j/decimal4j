package ch.javasoft.decimal.scale;

import java.math.RoundingMode;
import java.util.EnumMap;

import ch.javasoft.decimal.arithmetic.CheckedScale0fTruncatingArithmetics;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.arithmetic.UncheckedScale0fRoundingArithmetics;
import ch.javasoft.decimal.arithmetic.UncheckedScale0fTruncatingArithmetics;
import ch.javasoft.decimal.immutable.Decimal0f;
import ch.javasoft.decimal.mutable.MutableDecimal0f;
import ch.javasoft.decimal.truncate.DecimalRounding;

/**
 * Scale class for decimals with 0 {@link #getScale() fraction digits} (aka
 * as integers) and {@link #getScaleFactor() scale factor} 1.
 */
public final class Scale0f extends AbstractScale {
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
				//FIXME add when implemented
				//					map.put(roundingMode, new CheckedLongRoundingArithmetics(dr));
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
	public long multiplyByScaleFactor(long factor) {
		return factor;
	}

	@Override
	public long multiplyByScaleFactorHalf(long dividend) {
		return dividend >> 1;
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
	public long divideByScaleFactorHalf(long dividend) {
		return dividend << 1;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return 0;
	}

	@Override
	public Decimal0f createImmutable(long unscaled) {
		return Decimal0f.valueOfUnscaled(unscaled);
	}

	@Override
	public MutableDecimal0f createMutable(long unscaled) {
		return MutableDecimal0f.unscaled(unscaled);
	}
}