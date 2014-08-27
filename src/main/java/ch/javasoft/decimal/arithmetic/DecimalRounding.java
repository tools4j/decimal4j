package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Defines the same constants as {@link RoundingMode} and implements the functionality
 * to actually perform such rounding.
 */
public enum DecimalRounding {

	/**
	 * Rounding mode to round away from zero. Always increments the digit prior
	 * to a non-zero discarded fraction. Note that this rounding mode never
	 * decreases the magnitude of the calculated value.
	 * 
	 * @see RoundingMode#UP
	 */
	UP(RoundingMode.UP) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, boolean negativeTruncatedReminder, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (firstTruncatedDigit > 0 || !zeroAfterFirstTruncatedDigit) {
				return signedRoundingIncrement(truncatedValue, negativeTruncatedReminder);
			}
			return 0;
		}
	},

	/**
	 * Rounding mode to round towards zero. Never increments the digit prior to
	 * a discarded fraction (i.e., truncates). Note that this rounding mode
	 * never increases the magnitude of the calculated value.
	 * 
	 * @see RoundingMode#DOWN
	 */
	DOWN(RoundingMode.DOWN) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, boolean negativeTruncatedReminder, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			return 0;
		}
	},

	/**
	 * Rounding mode to round towards positive infinity. If the result is
	 * positive, behaves as for {@code RoundingMode.UP}; if negative, behaves as
	 * for {@code RoundingMode.DOWN}. Note that this rounding mode never
	 * decreases the calculated value.
	 * 
	 * @see RoundingMode#CEILING
	 */
	CEILING(RoundingMode.CEILING) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, boolean negativeTruncatedReminder, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (truncatedValue > 0 || (truncatedValue == 0 && !negativeTruncatedReminder)) {
				if (firstTruncatedDigit > 0 || !zeroAfterFirstTruncatedDigit) {
					return 1;
				}
			}
			return 0;
		}
	},

	/**
	 * Rounding mode to round towards negative infinity. If the result is
	 * positive, behave as for {@code RoundingMode.DOWN}; if negative, behave as
	 * for {@code RoundingMode.UP}. Note that this rounding mode never increases
	 * the calculated value.
	 * 
	 * @see RoundingMode#FLOOR
	 */
	FLOOR(RoundingMode.FLOOR) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, boolean negativeTruncatedReminder, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (truncatedValue < 0 || (truncatedValue == 0 && negativeTruncatedReminder)) {
				if (firstTruncatedDigit > 0 || !zeroAfterFirstTruncatedDigit) {
					return -1;
				}
			}
			return 0;
		}
	},

	/**
	 * Rounding mode to round towards {@literal "nearest neighbor"} unless both
	 * neighbors are equidistant, in which case round up. Behaves as for
	 * {@code RoundingMode.UP} if the discarded fraction is &ge; 0.5; otherwise,
	 * behaves as for {@code RoundingMode.DOWN}. Note that this is the rounding
	 * mode commonly taught at school.
	 * 
	 * @see RoundingMode#HALF_UP
	 */
	HALF_UP(RoundingMode.HALF_UP) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, boolean negativeTruncatedReminder, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (firstTruncatedDigit >= 5) {
				return signedRoundingIncrement(truncatedValue, negativeTruncatedReminder);
			}
			return 0;
		}
	},

	/**
	 * Rounding mode to round towards {@literal "nearest neighbor"} unless both
	 * neighbors are equidistant, in which case round down. Behaves as for
	 * {@code RoundingMode.UP} if the discarded fraction is &gt; 0.5; otherwise,
	 * behaves as for {@code RoundingMode.DOWN}.
	 * 
	 * @see RoundingMode#HALF_DOWN
	 */
	HALF_DOWN(RoundingMode.HALF_DOWN) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, boolean negativeTruncatedReminder, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (firstTruncatedDigit > 5 || (firstTruncatedDigit == 5 && !zeroAfterFirstTruncatedDigit)) {
				return signedRoundingIncrement(truncatedValue, negativeTruncatedReminder);
			}
			return 0;
		}
	},

	/**
	 * Rounding mode to round towards the {@literal "nearest neighbor"} unless
	 * both neighbors are equidistant, in which case, round towards the even
	 * neighbor. Behaves as for {@code RoundingMode.HALF_UP} if the digit to the
	 * left of the discarded fraction is odd; behaves as for
	 * {@code RoundingMode.HALF_DOWN} if it's even. Note that this is the
	 * rounding mode that statistically minimizes cumulative error when applied
	 * repeatedly over a sequence of calculations. It is sometimes known as
	 * {@literal "Banker's rounding,"} and is chiefly used in the USA. This
	 * rounding mode is analogous to the rounding policy used for {@code float}
	 * and {@code double} arithmetic in Java.
	 * 
	 * @see RoundingMode#HALF_EVEN
	 */
	HALF_EVEN(RoundingMode.HALF_EVEN) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, boolean negativeTruncatedReminder, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (firstTruncatedDigit >= 5) {
				if (firstTruncatedDigit > 5 || !zeroAfterFirstTruncatedDigit || ((truncatedValue & 0x1) != 0)) {
					return signedRoundingIncrement(truncatedValue, negativeTruncatedReminder);
				}
			}
			return 0;
		}
	},

	/**
	 * Rounding mode to assert that the requested operation has an exact result,
	 * hence no rounding is necessary. If this rounding mode is specified on an
	 * operation that yields an inexact result, an {@code ArithmeticException}
	 * is thrown.
	 * 
	 * @see RoundingMode#UNNECESSARY
	 */
	UNNECESSARY(RoundingMode.UNNECESSARY) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, boolean negativeTruncatedReminder, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (firstTruncatedDigit != 0 || !zeroAfterFirstTruncatedDigit) {
				throw new ArithmeticException("rounding necessary for: " + truncatedValue + "." + firstTruncatedDigit + (zeroAfterFirstTruncatedDigit ? "" : "..."));
			}
			return 0;
		}
	};

	/**
	 * Immutable set with all values of this enum. Avoids object creation in
	 * contrast to {@link #values()}.
	 */
	public static final Set<DecimalRounding> VALUES = Collections.unmodifiableSet(EnumSet.allOf(DecimalRounding.class));

	/**
	 * Constructor with {@link RoundingMode}.
	 * 
	 * @param roundingMode
	 *            the rounding mode corresponding to this decimal rounding
	 */
	private DecimalRounding(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}

	private final RoundingMode roundingMode;

	/**
	 * Returns the {@link RoundingMode} associated with this decimal rounding
	 * constant.
	 * 
	 * @return the corresponding rounding mode
	 */
	public RoundingMode getRoundingMode() {
		return roundingMode;
	}

	/**
	 * Returns the rounding increment, either +1 or -1 depending on the given
	 * arguments. The signed rounding increment is negative if either the
	 * truncated value is negative or if it is zero and the truncated reminder
	 * is negative.
	 * 
	 * @param truncatedValue
	 *            the truncated result before rounding is applied
	 * @param negativeTruncatedReminder
	 *            true if the reminder is negative, important only if
	 *            {@code truncatedValue==0}
	 * @return +1 or -1 to reflect a rounding increment, negative if value or reminder is negative
	 */
	private static int signedRoundingIncrement(long truncatedValue, boolean negativeTruncatedReminder) {
		return truncatedValue < 0 || (truncatedValue == 0 && negativeTruncatedReminder) ? -1 : 1;
	}

	/**
	 * Returns the rounding increment appropriate for this decimal rounding. The
	 * returned value is one of -1, 0 or 1.
	 * 
	 * @param truncatedValue
	 *            the truncated result before rounding is applied
	 * @param negativeTruncatedReminder
	 *            true if the reminder is negative, important only if
	 *            {@code truncatedValue==0}
	 * @param firstTruncatedDigit
	 *            the first truncated digit, must be in {@code [0, 1, ..., 9]}
	 * @param zeroAfterFirstTruncatedDigit
	 *            true if all truncated digits after the first truncated digit
	 *            are zero, and false otherwise
	 * @return the value to add to {@code truncatedValue} to get the rounded
	 *         result, one of -1, 0 or 1
	 */
	abstract public int calculateRoundingIncrement(long truncatedValue, boolean negativeTruncatedReminder, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit);

	/**
	 * Returns the rounding increment appropriate for this decimal rounding
	 * given the remaining truncated digits truncated by modulo one. The
	 * returned value is one of -1, 0 or 1.
	 * 
	 * @param truncatedValue
	 *            the truncated result before rounding is applied
	 * @param truncatedDigits
	 *            the truncated part of a double, must be {@code >-one} and
	 *            {@code <one}
	 * @param one
	 *            the value representing 1 which is {@code 10^scale}, must be
	 *            {@code >= 10}
	 * @return the value to add to {@code truncatedValue} to get the rounded
	 *         result, one of -1, 0 or 1
	 */
	int calculateRoundingIncrement(long truncatedValue, long truncatedDigits, long one) {
//		final long nonNegativeTruncatedDigits = Math.abs(truncatedDigits);
//		final long oneDivBy10 = one / 10;
//		final int firstTruncatedDigit = (int) (nonNegativeTruncatedDigits / oneDivBy10);
//		final long truncatedDigitsAfterFirst = nonNegativeTruncatedDigits % oneDivBy10;
//		return calculateRoundingIncrement(truncatedValue, truncatedDigits < 0, firstTruncatedDigit, truncatedDigitsAfterFirst == 0);
		if (truncatedDigits == 0) {
			return 0;
		}
		final long nonNegativeTruncatedDigits = Math.abs(truncatedDigits);
		final int compare = Long.compare(nonNegativeTruncatedDigits << 1, one);
		final int firstTruncatedDigit = compare < 0 ? 4 : compare > 0 ? 6 : 5;//FIXME make this nicer
		return calculateRoundingIncrement(truncatedValue, truncatedDigits < 0, firstTruncatedDigit, true);
	}

	/**
	 * Returns the rounding increment appropriate for this decimal rounding
	 * given the remaining truncated digits truncated by a given divisor. The
	 * returned value is one of -1, 0 or 1.
	 * 
	 * @param truncatedValue
	 *            the truncated result before rounding is applied
	 * @param truncatedDigits
	 *            the truncated part of a double, it most hold that
	 *            {@code abs(truncatedDigits) < abs(divisor)}
	 * @param divisor
	 *            the divisor that led to the truncated digits
	 * @return the value to add to {@code truncatedValue} to get the rounded
	 *         result, one of -1, 0 or 1
	 */
	int calculateRoundingIncrementForDivision(long truncatedValue, long truncatedDigits, long divisor) {
		if (truncatedDigits == 0) {
			return 0;
		}
		final long absTruncatedDigits = Math.abs(truncatedDigits);
		final long absDivisor = Math.abs(divisor);

		final int firstTruncatedDigit;
		final boolean zeroAfterFirstTruncatedDigit;
//		if (absTruncatedDigits < 922337203685477581L /* ceil(Long.MAX_VALUE/10) */) {
//			final long nonNgativeTruncatedDigitsX10 = absTruncatedDigits * 10;
//			firstTruncatedDigit = (int) (nonNgativeTruncatedDigitsX10 / absDivisor);
//			zeroAfterFirstTruncatedDigit = (nonNgativeTruncatedDigitsX10 % absDivisor) == 0;
//		} else {
		if (absTruncatedDigits < (Long.MAX_VALUE >> 1)) {
			final int compare = Long.compare(absTruncatedDigits << 1, absDivisor);
			firstTruncatedDigit = compare < 0 ? 4 : compare > 0 ? 6 : 5;//FIXME make this nicer
			zeroAfterFirstTruncatedDigit = true;
		} else {
			final long absDivisorBy10 = absDivisor / 10;
			final long absDivisorMod10 = absDivisor % 10;
			long div = absTruncatedDigits / absDivisorBy10;
			int cmp;
			div++;
			do {
				div--;
				long mul = div * absDivisorBy10;
				long del = absTruncatedDigits - mul;
				long rem = div * absDivisorMod10;
				cmp = Long.compare(del, rem / 10);
				if (cmp == 0 && (rem % 10) != 0) cmp = -1;
			} while (cmp < 0);
			firstTruncatedDigit = (int) div;

			final long p0 = absDivisorBy10 * div;
			final long p1x10 = absDivisorMod10 * div;
			final long p1 = p1x10 / 10;
			final long p1r = p1x10 % 10;
			zeroAfterFirstTruncatedDigit = p1r == 0 && absTruncatedDigits == (p0 + p1);
		}
		final boolean reminderIsNegative = truncatedDigits < 0 != divisor < 0;
		return calculateRoundingIncrement(truncatedValue, reminderIsNegative, firstTruncatedDigit, zeroAfterFirstTruncatedDigit);
	}

	private static final DecimalRounding[] VALUES_BY_ROUNDING_MODE_ORDINAL = sortByRoundingModeOrdinal();

	/**
	 * Returns the decimal rounding constant for the given rounding mode.
	 * 
	 * @param roundingMode
	 *            the rounding mode
	 * @return the constant corresponding to the given rounding mode
	 */
	public static DecimalRounding valueOf(RoundingMode roundingMode) {
		return VALUES_BY_ROUNDING_MODE_ORDINAL[roundingMode.ordinal()];
	}

	private static DecimalRounding[] sortByRoundingModeOrdinal() {
		final RoundingMode[] modes = RoundingMode.values();
		final DecimalRounding[] sorted = new DecimalRounding[modes.length];
		for (int i = 0; i < modes.length; i++) {
			sorted[modes[i].ordinal()] = valueOf(modes[i].name());
		}
		return sorted;
	}
}
