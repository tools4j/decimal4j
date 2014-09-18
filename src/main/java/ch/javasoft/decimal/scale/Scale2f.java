package ch.javasoft.decimal.scale;

/**
 * Scale class for decimals with 2 {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} 100.
 */
public final class Scale2f extends AbstractScale {
	public static final Scale2f INSTANCE = new Scale2f();

	@Override
	public int getScale() {
		return 2;
	}

	@Override
	public long getScaleFactor() {
		return 100;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * 100;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
		return (factor & LONG_MASK) * 100;
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return 0;
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / 100;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % 100;
	}
}