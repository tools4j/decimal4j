package ch.javasoft.decimal.arithmetic;

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

	private static final TruncatedPart[] COMPARE_HALF_VALUES = { LESS_THAN_HALF_BUT_NOT_ZERO, EQUAL_TO_HALF, GREATER_THAN_HALF };

	/**
	 * Returns a truncated part constant given a non-negative remainder
	 * resulting from a division by the given non-negative divisor.
	 * 
	 * @param nonNegativeRemainder
	 *            the remainder part, not negative and 
	 *            {@code nonNegativeRemainder < nonNegativeDivisor}
	 * @param nonNegativeDivisor
	 *            the divisor, not negative or LONG.MIN_VALUE --- the latter
	 *            equal to {@code abs(Long.MIN_VALUE)}
	 * @return the truncated part constant equivalent to the given arguments
	 */
	public static TruncatedPart valueOf(long nonNegativeRemainder, long nonNegativeDivisor) {
		if (nonNegativeRemainder == 0) {
			return ZERO;
		}
		final long nonNegativeRemainderTimes2 = nonNegativeRemainder << 1;
		if (nonNegativeRemainderTimes2 >= 0) {
			final int compareWithHalf = (nonNegativeRemainderTimes2 < nonNegativeDivisor) ? -1 : ((nonNegativeRemainderTimes2 == nonNegativeDivisor) ? 0 : 1); 
			return COMPARE_HALF_VALUES[compareWithHalf + 1];
		} else {
			if (nonNegativeDivisor < 0) {
				//nonNegativeDivisor == MIN_VALUE
				if (nonNegativeRemainderTimes2 == nonNegativeDivisor) {
					return EQUAL_TO_HALF;
				}
				return LESS_THAN_HALF_BUT_NOT_ZERO;
			} else {
				return GREATER_THAN_HALF;
			}
		}
	}

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