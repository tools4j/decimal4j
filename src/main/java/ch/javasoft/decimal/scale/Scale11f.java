package ch.javasoft.decimal.scale;

/**
 * Scale class for decimals with 11 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 100,000,000,000.
 */
public final class Scale11f extends AbstractScale {
	public static final Scale11f INSTANCE = new Scale11f();

	@Override
	public int getScale() {
		return 11;
	}

	@Override
	public long getScaleFactor() {
		return 100000000000L;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 100000000000L;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 1215752192;//(scaleFactor & LONG_MASK)
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 23;//(scaleFactor >>> 32)
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 100000000000L;
	}

	@Override
	public long multiplyByScaleFactorHalf(long dividend) {
		return dividend * 50000000000L;
	}

	@Override
	public long divideByScaleFactorHalf(long dividend) {
		return dividend / 50000000000L;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 100000000000L;
	}
}