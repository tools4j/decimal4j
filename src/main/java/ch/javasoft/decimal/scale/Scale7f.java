package ch.javasoft.decimal.scale;

/**
 * Scale class for decimals with 7 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 10,000,000.
 */
public final class Scale7f extends AbstractScale {
	public static final Scale7f INSTANCE = new Scale7f();

	@Override
	public int getScale() {
		return 7;
	}

	@Override
	public long getScaleFactor() {
		return 10000000;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 10000000;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 10000000;
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return 0;
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 10000000;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 10000000;
	}
}