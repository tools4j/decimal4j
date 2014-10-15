package ch.javasoft.decimal.scale;

import ch.javasoft.decimal.Decimal6f;
import ch.javasoft.decimal.MutableDecimal6f;

/**
 * Scale class for decimals with 6 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 1,000,000.
 */
public final class Scale6f extends AbstractScale {
	public static final Scale6f INSTANCE = new Scale6f();

	@Override
	public int getScale() {
		return 6;
	}

	@Override
	public long getScaleFactor() {
		return 1000000;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 1000000;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 1000000;
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return 0;
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 1000000;
	}

	@Override
	public long multiplyByScaleFactorHalf(long dividend) {
		return dividend * 500000;
	}

	@Override
	public long divideByScaleFactorHalf(long dividend) {
		return dividend / 500000;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 1000000;
	}

	@Override
	public Decimal6f createImmutable(long unscaled) {
		return Decimal6f.valueOfUnscaled(unscaled);
	}

	@Override
	public MutableDecimal6f createMutable(long unscaled) {
		return MutableDecimal6f.unscaled(unscaled);
	}
}