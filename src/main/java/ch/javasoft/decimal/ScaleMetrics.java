package ch.javasoft.decimal;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.javasoft.decimal.arithmetic.TruncatingArithmetics;

/**
 * <tt>ScaleMetrics</tt> is associated with {@link Decimal} numbers and
 * represents the factor applied to the {@code long} value underlying a
 * {@code Decimal}. Scale stands for the fixed number of fraction digits of a
 * {@code Decimal}.
 * <p>
 * The <tt>Scale</tt> class contains a number of subclasses used by different
 * decimal types. With <tt>Scale</tt> subclasses, it is possible to distinguish
 * different decimal types and we can ensure that only decimals of the same
 * scale can directly operate with each other.
 */
abstract public class ScaleMetrics {

	/**
	 * Scale class for decimals with 0 {@link #getScale() fraction digits} (aka
	 * as integers) and {@link #getScaleFactor() scale factor} 1.
	 */
	public static final class Scale0f extends ScaleMetrics {
		public static final Scale0f INSTANCE = new Scale0f();

		private Scale0f() {
			super(0, 1);
		}
	}

	/**
	 * Scale class for decimals with 1 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 10.
	 */
	public static final class Scale1f extends ScaleMetrics {
		public static final Scale1f INSTANCE = new Scale1f();

		private Scale1f() {
			super(1, 10);
		}
	}

	/**
	 * Scale class for decimals with 2 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 100.
	 */
	public static final class Scale2f extends ScaleMetrics {
		public static final Scale2f INSTANCE = new Scale2f();

		private Scale2f() {
			super(2, 100);
		}
	}

	/**
	 * Scale class for decimals with 3 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 1,000.
	 */
	public static final class Scale3f extends ScaleMetrics {
		public static final Scale3f INSTANCE = new Scale3f();

		private Scale3f() {
			super(3, 1000);
		}
	}

	/**
	 * Scale class for decimals with 4 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 10,000.
	 */
	public static final class Scale4f extends ScaleMetrics {
		public static final Scale4f INSTANCE = new Scale4f();

		private Scale4f() {
			super(4, 10000);
		}
	}

	/**
	 * Scale class for decimals with 5 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 100,000.
	 */
	public static final class Scale5f extends ScaleMetrics {
		public static final Scale5f INSTANCE = new Scale5f();

		private Scale5f() {
			super(5, 100000);
		}
	}

	/**
	 * Scale class for decimals with 6 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 1,000,000.
	 */
	public static final class Scale6f extends ScaleMetrics {
		public static final Scale6f INSTANCE = new Scale6f();

		private Scale6f() {
			super(6, 1000000);
		}
		@Override
		public Decimal6f createImmutable(long unscaled, RoundingMode roundingMode, OverflowMode overflowMode) {
			return new Decimal6f(unscaled, getTruncatingArithmetics().derive(roundingMode).derive(overflowMode));
		}
		@Override
		public MutableDecimal6f createMutable(RoundingMode roundingMode, OverflowMode overflowMode) {
			return new MutableDecimal6f(roundingMode, overflowMode);
		}
	}

	/**
	 * Scale class for decimals with 7 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 10,000,000.
	 */
	public static final class Scale7f extends ScaleMetrics {
		public static final Scale7f INSTANCE = new Scale7f();

		private Scale7f() {
			super(7, 10000000);
		}
	}

	/**
	 * Scale class for decimals with 8 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 100,000,000.
	 */
	public static final class Scale8f extends ScaleMetrics {
		public static final Scale8f INSTANCE = new Scale8f();

		private Scale8f() {
			super(8, 100000000);
		}
	}

	/**
	 * Scale class for decimals with 9 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 1,000,000,000.
	 */
	public static final class Scale9f extends ScaleMetrics {
		public static final Scale9f INSTANCE = new Scale9f();

		private Scale9f() {
			super(9, 1000000000);
		}
	}

	/**
	 * Scale class for decimals with 10 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 10,000,000,000.
	 */
	public static final class Scale10f extends ScaleMetrics {
		public static final Scale10f INSTANCE = new Scale10f();

		private Scale10f() {
			super(10, 10000000000L);
		}
	}

	/**
	 * Scale class for decimals with 11 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 100,000,000,000.
	 */
	public static final class Scale11f extends ScaleMetrics {
		public static final Scale11f INSTANCE = new Scale11f();

		private Scale11f() {
			super(11, 100000000000L);
		}
	}

	/**
	 * Scale class for decimals with 12 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 1,000,000,000,000.
	 */
	public static final class Scale12f extends ScaleMetrics {
		public static final Scale12f INSTANCE = new Scale12f();

		private Scale12f() {
			super(12, 1000000000000L);
		}
	}

	/**
	 * Scale class for decimals with 13 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 10,000,000,000,000.
	 */
	public static final class Scale13f extends ScaleMetrics {
		public static final Scale13f INSTANCE = new Scale13f();

		private Scale13f() {
			super(13, 10000000000000L);
		}
	}

	/**
	 * Scale class for decimals with 14 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 100,000,000,000,000.
	 */
	public static final class Scale14f extends ScaleMetrics {
		public static final Scale14f INSTANCE = new Scale14f();

		private Scale14f() {
			super(14, 100000000000000L);
		}
	}

	/**
	 * Scale class for decimals with 15 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 1,000,000,000,000,000.
	 */
	public static final class Scale15f extends ScaleMetrics {
		public static final Scale15f INSTANCE = new Scale15f();

		private Scale15f() {
			super(15, 1000000000000000L);
		}
	}

	/**
	 * Scale class for decimals with 16 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 10,000,000,000,000,000.
	 */
	public static final class Scale16f extends ScaleMetrics {
		public static final Scale16f INSTANCE = new Scale16f();

		private Scale16f() {
			super(16, 10000000000000000L);
		}
	}

	/**
	 * Scale class for decimals with 17 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 100,000,000,000,000,000.
	 */
	public static final class Scale17f extends ScaleMetrics {
		public static final Scale17f INSTANCE = new Scale17f();

		private Scale17f() {
			super(17, 100000000000000000L);
		}
	}

	/**
	 * Scale class for decimals with 18 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 1,000,000,000,000,000,000.
	 */
	public static final class Scale18f extends ScaleMetrics {
		public static final Scale18f INSTANCE = new Scale18f();

		private Scale18f() {
			super(18, 1000000000000000000L);
		}
	}

	/**
	 * All scale metric constants in an immutable ordered list:
	 * <p>
	 * {@code VALUES=[Scale0f.INSTANCE, Scale1f.INSTANCE, ..., Scale18f.INSTANCE]}
	 */
	public static final List<ScaleMetrics> VALUES = Collections.unmodifiableList(Arrays.asList(Scale0f.INSTANCE, Scale1f.INSTANCE, Scale2f.INSTANCE, Scale3f.INSTANCE, Scale4f.INSTANCE, Scale5f.INSTANCE, Scale6f.INSTANCE, Scale7f.INSTANCE, Scale8f.INSTANCE, Scale9f.INSTANCE, Scale10f.INSTANCE, Scale11f.INSTANCE, Scale12f.INSTANCE, Scale13f.INSTANCE, Scale14f.INSTANCE, Scale15f.INSTANCE, Scale16f.INSTANCE, Scale17f.INSTANCE, Scale18f.INSTANCE));

	//Long.MAX_VALUE: 9,223,372,036,854,775,807

	private final int scale;
	private final long scaleFactor;
	private final TruncatingArithmetics truncatingArithmetics;

	private ScaleMetrics(int scale, long scaleFactor) {
		this.scale = scale;
		this.scaleFactor = scaleFactor;
		this.truncatingArithmetics = new TruncatingArithmetics(this);
	}

	/**
	 * Returns the {@code ScaleMetrics} constant based on a given scale
	 * 
	 * @param scale
	 *            the scale value also known as {@link #getScale() fraction
	 *            digits}; must be in {@code [0,18]} both ends inclusive
	 * @return the scale metrics constant corresponding to {@code scale}
	 * @throws IllegalArgumentException
	 *             if scale is not in {@code [0, 18]}
	 */
	public static ScaleMetrics valueOf(int scale) {
		if (0 <= scale && scale <= 18) {
			return VALUES.get(scale);
		}
		throw new IllegalArgumentException("illegal scale, must be in [0,18] but was: " + scale);
	}

	/**
	 * Returns the scale, the number of fraction digits to the right of the
	 * decimal point of a {@link Decimal} value.
	 * 
	 * @return the scale also known as number of fraction digits
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * Returns the scale factor, which is 10<sup>f</sup> where {@code f} stands
	 * for the {@link #getScale() scale}.
	 * 
	 * @return the scale factor
	 */
	public long getScaleFactor() {
		return scaleFactor;
	}

	/**
	 * Creates and returns an immutable value.
	 *
	 * @param unscaled
	 *            the unscaled long value
	 * @param roundingMode
	 *            the rounding mode to use for arithmetic operations performed
	 *            on the returned value
	 * @param overflowMode
	 *            the overflow mode to use for arithmetic operations performed
	 *            on the returned value
	 * @return an immutable value.
	 */
	public AbstractImmutableDecimal<?, ?, ?> createImmutable(long unscaled, RoundingMode roundingMode, OverflowMode overflowMode) {
		// FIXME impl
		throw new RuntimeException("not implemented for " + getClass().getSimpleName());
	}

	/**
	 * Creates and returns a mutable zero value.
	 *
	 * @param roundingMode
	 *            the rounding mode to use for arithmetic operations performed
	 *            on the returned value
	 * @param overflowMode
	 *            the overflow mode to use for arithmetic operations performed
	 *            on the returned value
	 * @return an mutable value.
	 */
	public AbstractMutableDecimal<?, ?, ?> createMutable(RoundingMode roundingMode, OverflowMode overflowMode) {
		// FIXME impl
		throw new RuntimeException("not implemented for " + getClass().getSimpleName());
	}

	/**
	 * Returns the truncating arithmetics for this scale that performs all
	 * operations without rounding.
	 * 
	 * @return truncating arithmetics for this scale
	 * @see RoundingMode#DOWN
	 */
	public TruncatingArithmetics getTruncatingArithmetics() {
		return truncatingArithmetics;
	}
}
