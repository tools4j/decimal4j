package ch.javasoft.decimal.truncate;


/**
 * Represents the truncated part for instance after division. It is passed to
 * the rounding methods in {@link DecimalRounding}
 */
public enum TruncatedPart {
	/**
	 * Truncated part {@code t == 0}.
	 */
	ZERO {
		@Override
		public boolean isGreaterThanZero() {
			return false;
		}

		@Override
		public boolean isEqualToHalf() {
			return false;
		}

		@Override
		public boolean isGreaterEqualHalf() {
			return false;
		}

		@Override
		public boolean isGreaterThanHalf() {
			return false;
		}
	},
	/**
	 * Truncated part {@code 0 < t < 0.5}.
	 */
	LESS_THAN_HALF_BUT_NOT_ZERO {
		@Override
		public boolean isGreaterThanZero() {
			return true;
		}

		@Override
		public boolean isEqualToHalf() {
			return false;
		}

		@Override
		public boolean isGreaterEqualHalf() {
			return false;
		}

		@Override
		public boolean isGreaterThanHalf() {
			return false;
		}
	},
	/**
	 * Truncated part {@code t == 0.5}.
	 */
	EQUAL_TO_HALF {
		@Override
		public boolean isGreaterThanZero() {
			return true;
		}

		@Override
		public boolean isEqualToHalf() {
			return true;
		}

		@Override
		public boolean isGreaterEqualHalf() {
			return true;
		}

		@Override
		public boolean isGreaterThanHalf() {
			return false;
		}
	},
	/**
	 * Truncated part {@code t > 0.5}.
	 */
	GREATER_THAN_HALF {
		@Override
		public boolean isGreaterThanZero() {
			return true;
		}

		@Override
		public boolean isEqualToHalf() {
			return false;
		}

		@Override
		public boolean isGreaterEqualHalf() {
			return true;
		}

		@Override
		public boolean isGreaterThanHalf() {
			return true;
		}
	};

	/**
	 * Returns true if the truncated part is greater than zero.
	 * 
	 * @return true if {@code this > 0}
	 */
	abstract public boolean isGreaterThanZero();

	/**
	 * Returns true if the truncated part is equal to one half.
	 * 
	 * @return true if {@code this == 0.5}
	 */
	abstract public boolean isEqualToHalf();

	/**
	 * Returns true if the truncated part is greater than or equal to one half.
	 * 
	 * @return true if {@code this >= 0.5}
	 */
	abstract public boolean isGreaterEqualHalf();

	/**
	 * Returns true if the truncated part is greater than one half.
	 * 
	 * @return true if {@code this > 0.5}
	 */
	abstract public boolean isGreaterThanHalf();

	/**
	 * Returns a truncated part constant given the first truncated digit and a
	 * boolean indicating whether there is non-zero digits after that.
	 * 
	 * @param firstTruncatedDigit
	 *            the first truncated digit, must be in {@code [0, 1, ..., 9]}
	 * @param zeroAfterFirstTruncatedDigit
	 *            true if all truncated digits after the first truncated digit
	 *            are zero, and false otherwise
	 * @return the truncated part constant equivalent to the given arguments
	 */
	public static TruncatedPart valueOf(int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
		if (firstTruncatedDigit > 5) {
			return GREATER_THAN_HALF;
		}
		if (zeroAfterFirstTruncatedDigit) {
			if (firstTruncatedDigit == 5) {
				return EQUAL_TO_HALF;
			}
			if (firstTruncatedDigit > 0) {
				return LESS_THAN_HALF_BUT_NOT_ZERO;
			}
			return ZERO;
		}
		if (firstTruncatedDigit < 5) {
			return LESS_THAN_HALF_BUT_NOT_ZERO;
		}
		return GREATER_THAN_HALF;
	}

}