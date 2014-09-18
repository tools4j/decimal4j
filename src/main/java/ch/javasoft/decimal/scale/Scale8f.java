package ch.javasoft.decimal.scale;

/**
 * Scale class for decimals with 8 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 100,000,000.
 */
public final class Scale8f extends AbstractScale {
	public static final Scale8f INSTANCE = new Scale8f();

	@Override
	public int getScale() {
		return 8;
	}

	@Override
	public long getScaleFactor() {
		return 100000000;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 100000000;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 100000000;
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return 0;
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 100000000;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 100000000;
	}
}