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
		public final boolean isGreaterThanZero() {
			return false;
		}

		@Override
		public final boolean isEqualToHalf() {
			return false;
		}

		@Override
		public final boolean isGreaterEqualHalf() {
			return false;
		}

		@Override
		public final boolean isGreaterThanHalf() {
			return false;
		}
	},
	/**
	 * Truncated part {@code 0 < t < 0.5}.
	 */
	LESS_THAN_HALF_BUT_NOT_ZERO {
		@Override
		public final boolean isGreaterThanZero() {
			return true;
		}

		@Override
		public final boolean isEqualToHalf() {
			return false;
		}

		@Override
		public final boolean isGreaterEqualHalf() {
			return false;
		}

		@Override
		public final boolean isGreaterThanHalf() {
			return false;
		}
	},
	/**
	 * Truncated part {@code t == 0.5}.
	 */
	EQUAL_TO_HALF {
		@Override
		public final boolean isGreaterThanZero() {
			return true;
		}

		@Override
		public final boolean isEqualToHalf() {
			return true;
		}

		@Override
		public final boolean isGreaterEqualHalf() {
			return true;
		}

		@Override
		public final boolean isGreaterThanHalf() {
			return false;
		}
	},
	/**
	 * Truncated part {@code t > 0.5}.
	 */
	GREATER_THAN_HALF {
		@Override
		public final boolean isGreaterThanZero() {
			return true;
		}

		@Override
		public final boolean isEqualToHalf() {
			return false;
		}

		@Override
		public final boolean isGreaterEqualHalf() {
			return true;
		}

		@Override
		public final boolean isGreaterThanHalf() {
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
	public static final TruncatedPart valueOf(int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
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