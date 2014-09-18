package ch.javasoft.decimal.scale;

/**
 * Scale class for decimals with 3 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 1,000.
 */
public final class Scale3f extends AbstractScale {
	public static final Scale3f INSTANCE = new Scale3f();

	@Override
	public int getScale() {
		return 3;
	}

	@Override
	public long getScaleFactor() {
		return 1000;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 1000;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 1000;
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return 0;
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 1000;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 1000;
	}
}