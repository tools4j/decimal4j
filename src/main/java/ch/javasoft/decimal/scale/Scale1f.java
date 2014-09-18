package ch.javasoft.decimal.scale;

/**
 * Scale class for decimals with 1 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 10.
 */
public final class Scale1f extends AbstractScale {
	public static final Scale1f INSTANCE = new Scale1f();

	@Override
	public int getScale() {
		return 1;
	}

	@Override
	public long getScaleFactor() {
		return 10;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 10;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 10;
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return 0;
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 10;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 10;
	}
}