package ch.javasoft.decimal.scale;

/**
 * Scale class for decimals with 12 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 1,000,000,000,000.
 */
public final class Scale12f extends AbstractScale {
	public static final Scale12f INSTANCE = new Scale12f();

	@Override
	public int getScale() {
		return 12;
	}

	@Override
	public long getScaleFactor() {
		return 1000000000000L;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 1000000000000L;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 3567587328L;//(scaleFactor & LONG_MASK)
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 232;//(scaleFactor >>> 32)
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 1000000000000L;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 1000000000000L;
	}
}