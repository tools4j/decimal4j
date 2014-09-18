package ch.javasoft.decimal.scale;

/**
 * Scale class for decimals with 9 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 1,000,000,000.
 */
public final class Scale9f extends AbstractScale {
	public static final Scale9f INSTANCE = new Scale9f();

	@Override
	public int getScale() {
		return 9;
	}

	@Override
	public long getScaleFactor() {
		return 1000000000;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 1000000000;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 1000000000;
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return 0;
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 1000000000;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 1000000000;
	}
}