package ch.javasoft.decimal.scale;

/**
 * Scale class for decimals with 18 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 1,000,000,000,000,000,000.
 */
public final class Scale18f extends AbstractScale {
	public static final Scale18f INSTANCE = new Scale18f();

	@Override
	public int getScale() {
		return 18;
	}

	@Override
	public long getScaleFactor() {
		return 1000000000000000000L;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 1000000000000000000L;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 2808348672L;//(scaleFactor & LONG_MASK)
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 232830643;//(scaleFactor >>> 32)
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 1000000000000000000L;
	}

	@Override
	public long multiplyByScaleFactorHalf(long dividend) {
		return dividend * 500000000000000000L;
	}

	@Override
	public long divideByScaleFactorHalf(long dividend) {
		return dividend / 500000000000000000L;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 1000000000000000000L;
	}
}