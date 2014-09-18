package ch.javasoft.decimal.scale;

/**
 * Scale class for decimals with 5 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 100,000.
 */
public final class Scale5f extends AbstractScale {
	public static final Scale5f INSTANCE = new Scale5f();

	@Override
	public int getScale() {
		return 5;
	}

	@Override
	public long getScaleFactor() {
		return 100000;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 100000;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 100000;
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return 0;
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 100000;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 100000;
	}
}