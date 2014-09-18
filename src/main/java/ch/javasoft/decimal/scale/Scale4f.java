package ch.javasoft.decimal.scale;

/**
 * Scale class for decimals with 4 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 10,000.
 */
public final class Scale4f extends AbstractScale {
	public static final Scale4f INSTANCE = new Scale4f();

	@Override
	public int getScale() {
		return 4;
	}

	@Override
	public long getScaleFactor() {
		return 10000;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 10000;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 10000;
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return 0;
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 10000;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 10000;
	}
}