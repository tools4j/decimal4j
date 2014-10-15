package ch.javasoft.decimal.scale;

/**
 * Scale class for decimals with 14 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 100,000,000,000,000.
 */
public final class Scale14f extends AbstractScale {
	public static final Scale14f INSTANCE = new Scale14f();

	@Override
	public int getScale() {
		return 14;
	}

	@Override
	public long getScaleFactor() {
		return 100000000000000L;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 100000000000000L;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 276447232;//(scaleFactor & LONG_MASK)
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 23283;//(scaleFactor >>> 32)
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 100000000000000L;
	}

	@Override
	public long multiplyByScaleFactorHalf(long dividend) {
		return dividend * 50000000000000L;
	}

	@Override
	public long divideByScaleFactorHalf(long dividend) {
		return dividend / 50000000000000L;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 100000000000000L;
	}
}