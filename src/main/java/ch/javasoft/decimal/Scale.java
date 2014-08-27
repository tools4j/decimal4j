package ch.javasoft.decimal;

/**
 * <tt>Scale</tt> is associated with {@link Decimal} numbers and represents the
 * factor applied to the {@code long} value underlying a {@code Decimal}. Scale
 * stands for the fixed number of fraction digits of a {@code Decimal}.
 * <p>
 * The <tt>Scale</tt> class contains a number of subclasses used by different
 * decimal types. With <tt>Scale</tt> subclasses, it is possible to distinguish
 * different decimal types and we can ensure that only decimals of the same
 * scale can directly operate with each other.
 */
public class Scale {

	/**
	 * Scale class for decimals with 0 {@link #getFractionDigits() fraction
	 * digits} (aka as integers) and {@link #getScaleFactor() scale factor} 1.
	 */
	public static final class Scale0f extends Scale {
		public static final Scale0f INSTANCE = new Scale0f();

		private Scale0f() {
			super(0);
		}
	}

	/**
	 * Scale class for decimals with 1 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 10.
	 */
	public static final class Scale1f extends Scale {
		public static final Scale1f INSTANCE = new Scale1f();

		private Scale1f() {
			super(1);
		}
	}

	/**
	 * Scale class for decimals with 2 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 100.
	 */
	public static final class Scale2f extends Scale {
		public static final Scale2f INSTANCE = new Scale2f();

		private Scale2f() {
			super(2);
		}
	}

	/**
	 * Scale class for decimals with 3 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 1,000.
	 */
	public static final class Scale3f extends Scale {
		public static final Scale3f INSTANCE = new Scale3f();

		private Scale3f() {
			super(3);
		}
	}

	/**
	 * Scale class for decimals with 4 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 10,000.
	 */
	public static final class Scale4f extends Scale {
		public static final Scale4f INSTANCE = new Scale4f();

		private Scale4f() {
			super(4);
		}
	}

	/**
	 * Scale class for decimals with 5 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 100,000.
	 */
	public static final class Scale5f extends Scale {
		public static final Scale5f INSTANCE = new Scale5f();

		private Scale5f() {
			super(5);
		}
	}

	/**
	 * Scale class for decimals with 6 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 1,000,000.
	 */
	public static final class Scale6f extends Scale {
		public static final Scale6f INSTANCE = new Scale6f();

		private Scale6f() {
			super(6);
		}
	}

	/**
	 * Scale class for decimals with 7 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 10,000,000.
	 */
	public static final class Scale7f extends Scale {
		public static final Scale7f INSTANCE = new Scale7f();

		private Scale7f() {
			super(7);
		}
	}

	/**
	 * Scale class for decimals with 8 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 100,000,000.
	 */
	public static final class Scale8f extends Scale {
		public static final Scale8f INSTANCE = new Scale8f();

		private Scale8f() {
			super(8);
		}
	}

	/**
	 * Scale class for decimals with 9 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 1,000,000,000.
	 */
	public static final class Scale9f extends Scale {
		public static final Scale9f INSTANCE = new Scale9f();

		private Scale9f() {
			super(9);
		}
	}

	/**
	 * Scale class for decimals with 10 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 10,000,000,000.
	 */
	public static final class Scale10f extends Scale {
		public static final Scale10f INSTANCE = new Scale10f();

		private Scale10f() {
			super(10);
		}
	}

	/**
	 * Scale class for decimals with 11 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 100,000,000,000.
	 */
	public static final class Scale11f extends Scale {
		public static final Scale11f INSTANCE = new Scale11f();

		private Scale11f() {
			super(11);
		}
	}

	/**
	 * Scale class for decimals with 12 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 1,000,000,000,000.
	 */
	public static final class Scale12f extends Scale {
		public static final Scale12f INSTANCE = new Scale12f();

		private Scale12f() {
			super(12);
		}
	}

	/**
	 * Scale class for decimals with 13 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 10,000,000,000,000.
	 */
	public static final class Scale13f extends Scale {
		public static final Scale13f INSTANCE = new Scale13f();

		private Scale13f() {
			super(13);
		}
	}

	/**
	 * Scale class for decimals with 14 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 100,000,000,000,000.
	 */
	public static final class Scale14f extends Scale {
		public static final Scale14f INSTANCE = new Scale14f();

		private Scale14f() {
			super(14);
		}
	}

	/**
	 * Scale class for decimals with 15 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 1,000,000,000,000,000.
	 */
	public static final class Scale15f extends Scale {
		public static final Scale15f INSTANCE = new Scale15f();

		private Scale15f() {
			super(15);
		}
	}

	/**
	 * Scale class for decimals with 16 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor} 10,000,000,000,000,000.
	 */
	public static final class Scale16f extends Scale {
		public static final Scale16f INSTANCE = new Scale16f();

		private Scale16f() {
			super(16);
		}
	}

	/**
	 * Scale class for decimals with 17 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor}
	 * 100,000,000,000,000,000.
	 */
	public static final class Scale17f extends Scale {
		public static final Scale17f INSTANCE = new Scale17f();

		private Scale17f() {
			super(17);
		}
	}

	/**
	 * Scale class for decimals with 18 {@link #getFractionDigits() fraction
	 * digit} and {@link #getScaleFactor() scale factor}
	 * 1,000,000,000,000,000,000.
	 */
	public static final class Scale18f extends Scale {
		public static final Scale18f INSTANCE = new Scale18f();

		private Scale18f() {
			super(18);
		}
	}

	private final int fractionDigits;

	private Scale(int fractionDigits) {
		this.fractionDigits = fractionDigits;
	}

	/**
	 * Returns the number of fraction digits (aka decimal digits).
	 * @return the number of fraction digits
	 */
	public int getFractionDigits() {
		return fractionDigits;
	}

	/**
	 * Returns the scale factor, which is 10<sup>f</sup> where {@code f} stands
	 * for the number of {@code #getFractionDigits() fraction digits}.
	 * @return the scale factor
	 */
	public long getScaleFactor() {
		long factor = 1;
		for (int i = 0; i < fractionDigits; i++) {
			factor *= 10;
		}
		return factor;
	}
	//Long.MAX_VALUE: 9,223,372,036,854,775,807
}
