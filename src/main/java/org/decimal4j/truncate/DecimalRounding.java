/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.truncate;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Defines the same constants as {@link RoundingMode} and implements the
 * functionality to actually perform such rounding.
 */
public enum DecimalRounding {

	/**
	 * Rounding mode to round away from zero. Always increments the digit prior
	 * to a non-zero discarded fraction. Note that this rounding mode never
	 * decreases the magnitude of the calculated value.
	 * 
	 * @see RoundingMode#UP
	 */
	UP {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.UP;
		}
		@Override
		public final int calculateRoundingIncrement(int sgn, long truncatedValue, TruncatedPart truncatedPart) {
			if (truncatedPart.isGreaterThanZero()) {
				return sgn;
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
	DOWN {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.DOWN;
		}
		@Override
		public final int calculateRoundingIncrement(int sgn, long truncatedValue, TruncatedPart truncatedPart) {
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
	CEILING {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.CEILING;
		}
		@Override
		public final int calculateRoundingIncrement(int sgn, long truncatedValue, TruncatedPart truncatedPart) {
			if (sgn > 0) {
				if (truncatedPart.isGreaterThanZero()) {
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
	FLOOR {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.FLOOR;
		}
		@Override
		public final int calculateRoundingIncrement(int sgn, long truncatedValue, TruncatedPart truncatedPart) {
			if (sgn < 0) {
				if (truncatedPart.isGreaterThanZero()) {
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
	HALF_UP {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.HALF_UP;
		}
		@Override
		public final int calculateRoundingIncrement(int sgn, long truncatedValue, TruncatedPart truncatedPart) {
			if (truncatedPart.isGreaterEqualHalf()) {
				return sgn;
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
	HALF_DOWN {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.HALF_DOWN;
		}
		@Override
		public final int calculateRoundingIncrement(int sgn, long truncatedValue, TruncatedPart truncatedPart) {
			if (truncatedPart.isGreaterThanHalf()) {
				return sgn;
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
	HALF_EVEN {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.HALF_EVEN;
		}
		@Override
		public final int calculateRoundingIncrement(int sgn, long truncatedValue, TruncatedPart truncatedPart) {
			if (truncatedPart.isGreaterEqualHalf()) {
				if (truncatedPart.isGreaterThanHalf() | ((truncatedValue & 0x1) != 0)) {
					return sgn;
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
	UNNECESSARY {
		@Override
		public final RoundingMode getRoundingMode() {
			return RoundingMode.UNNECESSARY;
		}
		@Override
		public final int calculateRoundingIncrement(int sgn, long truncatedValue, TruncatedPart truncatedPart) {
			if (truncatedPart.isGreaterThanZero()) {
				throw new ArithmeticException("Rounding necessary");
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
	private DecimalRounding() {
		this.uncheckedPolicy = new DefaultTruncationPolicy(OverflowMode.UNCHECKED, this);
		this.checkedPolicy = new DefaultTruncationPolicy(OverflowMode.CHECKED, this);
	}

	private final DefaultTruncationPolicy uncheckedPolicy;
	private final DefaultTruncationPolicy checkedPolicy;

	/**
	 * Returns the {@link RoundingMode} associated with this decimal rounding
	 * constant.
	 * 
	 * @return the corresponding rounding mode
	 */
	abstract public RoundingMode getRoundingMode();

	/**
	 * Returns the truncation policy defined by {@link #getRoundingMode()} and
	 * {@link OverflowMode#CHECKED}.
	 * 
	 * @return the truncation policy defined by this decimal rounding and the
	 *         checked overflow mode
	 */
	public final TruncationPolicy getCheckedTruncationPolicy() {
		return checkedPolicy;
	}

	/**
	 * Returns the truncation policy defined by {@link #getRoundingMode()} and
	 * {@link OverflowMode#UNCHECKED}.
	 * 
	 * @return the truncation policy defined by this decimal rounding and the
	 *         unchecked overflow mode
	 */
	public final TruncationPolicy getUncheckedTruncationPolicy() {
		return uncheckedPolicy;
	}

	/**
	 * Returns the rounding increment appropriate for this decimal rounding. The
	 * returned value is one of -1, 0 or 1.
	 * 
	 * @param sign
	 *            the sign of the total value, either +1 or -1; determines the
	 *            result value if rounded
	 * @param truncatedValue
	 *            the truncated result before rounding is applied
	 * @param truncatedPart
	 *            classification of the trunctated part of the value
	 * @return the value to add to {@code truncatedValue} to get the rounded
	 *         result, one of -1, 0 or 1
	 */
	abstract public int calculateRoundingIncrement(int sign, long truncatedValue, TruncatedPart truncatedPart);

	private static final DecimalRounding[] VALUES_BY_ROUNDING_MODE_ORDINAL = sortByRoundingModeOrdinal();

	/**
	 * Returns the decimal rounding constant for the given rounding mode.
	 * 
	 * @param roundingMode
	 *            the rounding mode
	 * @return the constant corresponding to the given rounding mode
	 */
	public static final DecimalRounding valueOf(RoundingMode roundingMode) {
		return VALUES_BY_ROUNDING_MODE_ORDINAL[roundingMode.ordinal()];
	}

	private static final DecimalRounding[] sortByRoundingModeOrdinal() {
		final DecimalRounding[] sorted = new DecimalRounding[VALUES.size()];
		for (final DecimalRounding dr : VALUES) {
			sorted[dr.getRoundingMode().ordinal()] = dr;
		}
		return sorted;
	}
}
