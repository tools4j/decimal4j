package ch.javasoft.decimal.scale;

/**
 * Scale class for decimals with 15 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 1,000,000,000,000,000.
 */
public final class Scale15f extends AbstractScale {
	public static final Scale15f INSTANCE = new Scale15f();

	@Override
	public int getScale() {
		return 15;
	}

	@Override
	public long getScaleFactor() {
		return 1000000000000000L;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 1000000000000000L;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 2764472320L;//(scaleFactor & LONG_MASK)
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 232830;//(scaleFactor >>> 32)
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 1000000000000000L;
	}

	@Override
	public long multiplyByScaleFactorHalf(long dividend) {
		return dividend * 500000000000000L;
	}

	@Override
	public long divideByScaleFactorHalf(long dividend) {
		return dividend / 500000000000000L;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 1000000000000000L;
	}
}