/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.arithmetic;

import org.decimal4j.truncate.DecimalRounding;

/**
 * Defines different inverses of a {@link DecimalRounding}. Each constant
 * provides its own implementation of an {@link #invert(DecimalRounding)}
 * method.
 */
enum RoundingInverse {
	/**
	 * Constant calculating inverted rounding due to sign reversion.
	 * <p>
	 * The inverted rounding mode can be used to round a value {@code a} instead
	 * of the value {@code -a}.
	 */
	SIGN_REVERSION {
		@Override
		public final DecimalRounding invert(DecimalRounding rounding) {
			switch (rounding) {
			case FLOOR:
				return DecimalRounding.CEILING;
			case CEILING:
				return DecimalRounding.FLOOR;
			case DOWN:
				return DecimalRounding.DOWN;
			case HALF_DOWN:
				return DecimalRounding.HALF_DOWN;
			case UP:
				return DecimalRounding.UP;
			case HALF_UP:
				return DecimalRounding.HALF_UP;
			case HALF_EVEN:
				return DecimalRounding.HALF_EVEN;
			case UNNECESSARY:
				return DecimalRounding.UNNECESSARY;
			default:
				// should not get here
				throw new IllegalArgumentException("Unsupported rounding mode: " + rounding);
			}
		}
	},
	/**
	 * Constant calculating inverted rounding due to sign reversion occurring
	 * after an addition or subtraction.
	 * <p>
	 * The inverted rounding mode can be used to round a value {@code x} instead
	 * of the sum {@code (a + x)} when sum and {@code x} have opposite sign
	 * (equivalent for a difference {@code (a - x)}).
	 */
	ADDITIVE_REVERSION {
		public final DecimalRounding invert(DecimalRounding rounding) {
			switch (rounding) {
			case FLOOR:
				return DecimalRounding.FLOOR;
			case CEILING:
				return DecimalRounding.CEILING;
			case DOWN:
				return DecimalRounding.UP;
			case HALF_DOWN:
				return DecimalRounding.HALF_UP;
			case UP:
				return DecimalRounding.DOWN;
			case HALF_UP:
				return DecimalRounding.HALF_DOWN;
			case HALF_EVEN:
				return DecimalRounding.HALF_EVEN;
			case UNNECESSARY:
				return DecimalRounding.UNNECESSARY;
			default:
				// should not get here
				throw new IllegalArgumentException("Unsupported rounding mode: " + rounding);
			}
		}
	},
	/**
	 * Constant calculating inverted rounding due to reciprocal of a value.
	 * <p>
	 * The inverted rounding mode can be used to round a value {@code x} instead
	 * of the reciprocal value {@code 1/x}.
	 */
	RECIPROCAL {
		public final DecimalRounding invert(DecimalRounding rounding) {
			switch (rounding) {
			case UP:
				return DecimalRounding.DOWN;
			case DOWN:
				return DecimalRounding.UP;
			case CEILING:
				return DecimalRounding.FLOOR;
			case FLOOR:
				return DecimalRounding.CEILING;
			case HALF_UP:
				return DecimalRounding.HALF_DOWN;
			case HALF_DOWN:
				return DecimalRounding.HALF_UP;
			case HALF_EVEN:
				return DecimalRounding.HALF_EVEN;// HALF_UNEVEN?
			case UNNECESSARY:
				return DecimalRounding.UNNECESSARY;
			default:
				// should not get here
				throw new IllegalArgumentException("Unsupported rounding mode: " + rounding);
			}
		}
	};
	/**
	 * Returns the inverted rounding for the inversion case defined by this
	 * constant.
	 * 
	 * @param rounding
	 *            the original rounding
	 * @return the inverted rounding
	 */
	abstract public DecimalRounding invert(DecimalRounding rounding);
}
