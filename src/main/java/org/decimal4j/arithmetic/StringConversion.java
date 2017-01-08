/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 decimal4j (tools4j), Marco Terzer
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

import java.io.IOException;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;

/**
 * Contains methods to convert from and to String.
 */
final class StringConversion {

	/**
	 * Thread-local used to build Decimal strings. Allocated big enough to avoid growth.
	 */
	static final ThreadLocal<StringBuilder> STRING_BUILDER_THREAD_LOCAL = new ThreadLocal<StringBuilder>() {
		@Override
		protected StringBuilder initialValue() {
			return new StringBuilder(19 + 1 + 2);// unsigned long: 19 digits,
													// sign: 1, decimal point
													// and leading 0: 2
		}
	};

	private static enum ParseMode {
		Long, IntegralPart;
	}

	/**
	 * Parses the given string into a long and returns it, rounding extra digits if necessary.
	 * 
	 * @param arith
	 *            the arithmetic of the target value
	 * @param rounding
	 *            the rounding to apply if a fraction is present
	 * @param s
	 *            the string to parse
	 * @param start
	 *            the start index to read characters in {@code s}, inclusive
	 * @param end
	 *            the end index where to stop reading in characters in {@code s}, exclusive
	 * @return the parsed value
	 * @throws IndexOutOfBoundsException
	 *             if {@code start < 0} or {@code end > s.length()}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal} or if the value is too large to be
	 *             represented as a long
	 */
	static final long parseLong(DecimalArithmetic arith, DecimalRounding rounding, CharSequence s, int start, int end) {
		return parseUnscaledDecimal(arith, rounding, s, start, end);
	}

	/**
	 * Parses the given string into an unscaled decimal and returns it, rounding extra digits if necessary.
	 * 
	 * @param arith
	 *            the arithmetic of the target value
	 * @param rounding
	 *            the rounding to apply if extra fraction digits are present
	 * @param s
	 *            the string to parse
	 * @param start
	 *            the start index to read characters in {@code s}, inclusive
	 * @param end
	 *            the end index where to stop reading in characters in {@code s}, exclusive
	 * @return the parsed value
	 * @throws IndexOutOfBoundsException
	 *             if {@code start < 0} or {@code end > s.length()}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal} or if the value is too large to be
	 *             represented as a Decimal with the scale of the given arithmetic
	 */
	static final long parseUnscaledDecimal(DecimalArithmetic arith, DecimalRounding rounding, CharSequence s, int start, int end) {
		if (start < 0 | end > s.length()) {
			throw new IndexOutOfBoundsException("Start or end index is out of bounds: [" + start + ", " + end
					+ " must be <= [0, " + s.length() + "]");
		}
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final int scale = scaleMetrics.getScale();
		final int indexOfDecimalPoint = indexOfDecimalPoint(s, start, end);
		if (indexOfDecimalPoint == end & scale > 0) {
			throw newNumberFormatExceptionFor(arith, s);
		}

		// parse a decimal number
		final long integralPart;// unscaled
		final long fractionalPart;// scaled
		final TruncatedPart truncatedPart;
		final boolean negative;
		if (indexOfDecimalPoint < 0) {
			integralPart = parseIntegralPart(arith, s, start, end, ParseMode.Long);
			fractionalPart = 0;
			truncatedPart = TruncatedPart.ZERO;
			negative = integralPart < 0;
		} else {
			final int fractionalEnd = Math.min(end, indexOfDecimalPoint + 1 + scale);
			if (indexOfDecimalPoint == start) {
				// allowed format .45
				integralPart = 0;
				fractionalPart = parseFractionalPart(arith, s, start + 1, fractionalEnd);
				truncatedPart = parseTruncatedPart(arith, s, fractionalEnd, end);
				negative = false;
			} else {
				// allowed formats: "0.45", "+0.45", "-0.45", ".45", "+.45",
				// "-.45"
				integralPart = parseIntegralPart(arith, s, start, indexOfDecimalPoint, ParseMode.IntegralPart);
				fractionalPart = parseFractionalPart(arith, s, indexOfDecimalPoint + 1, fractionalEnd);
				truncatedPart = parseTruncatedPart(arith, s, fractionalEnd, end);
				negative = integralPart < 0 | (integralPart == 0 && s.charAt(start) == '-');
			}
		}
		if (truncatedPart.isGreaterThanZero() & rounding == DecimalRounding.UNNECESSARY) {
			throw Exceptions.newRoundingNecessaryArithmeticException();
		}
		try {
			final long unscaledIntegeral = scaleMetrics.multiplyByScaleFactorExact(integralPart);
			final long unscaledFractional = negative ? -fractionalPart : fractionalPart;// < Scale18.SCALE_FACTOR hence
																						// no overflow
			final long truncatedValue = Checked.add(arith, unscaledIntegeral, unscaledFractional);
			final int roundingIncrement = rounding.calculateRoundingIncrement(negative ? -1 : 1, truncatedValue,
					truncatedPart);
			return roundingIncrement == 0 ? truncatedValue : Checked.add(arith, truncatedValue, roundingIncrement);
		} catch (ArithmeticException e) {
			throw newNumberFormatExceptionFor(arith, s, e);
		}
	}

	private static final long parseFractionalPart(DecimalArithmetic arith, CharSequence s, int start, int end) {
		final int len = end - start;
		if (len > 0) {
			int i = start;
			long value = 0;
			while  (i < end) {
				final int digit = getDigit(arith, s, s.charAt(i++));
				value = value * 10 + digit;
			}
			final int scale = arith.getScale();
			if (len < scale) {
				final ScaleMetrics diffScale = Scales.getScaleMetrics(scale - len);
				return diffScale.multiplyByScaleFactor(value);
			}
			return value;
		}
		return 0;
	}

	private static final TruncatedPart parseTruncatedPart(DecimalArithmetic arith, CharSequence s, int start, int end) {
		if (start < end) {
			final char firstChar = s.charAt(start);
			TruncatedPart truncatedPart;
			if (firstChar == '0') {
				truncatedPart = TruncatedPart.ZERO;
			} else if (firstChar == '5') {
				truncatedPart = TruncatedPart.EQUAL_TO_HALF;
			} else if (firstChar > '0' & firstChar < '5') {
				truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
			} else if (firstChar > '5' & firstChar <= '9') {
				truncatedPart = TruncatedPart.GREATER_THAN_HALF;
			} else {
				throw newNumberFormatExceptionFor(arith, s);
			}
			int i = start + 1;
			while (i < end) {
				final char ch = s.charAt(i++);
				if (ch > '0' & ch <= '9') {
					if (truncatedPart == TruncatedPart.ZERO) {
						truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
					} else if (truncatedPart == TruncatedPart.EQUAL_TO_HALF) {
						truncatedPart = TruncatedPart.GREATER_THAN_HALF;
					}
				} else if (ch != '0') {
					throw newNumberFormatExceptionFor(arith, s);
				}
			}
			return truncatedPart;
		}
		return TruncatedPart.ZERO;
	}

	private static final int indexOfDecimalPoint(CharSequence s, int start, int end) {
		for (int i = start; i < end; i++) {
			if (s.charAt(i) == '.') {
				return i;
			}
		}
		return -1;
	}

	// copied from Long.parseLong(String, int) but for fixed radix 10
	private static final long parseIntegralPart(DecimalArithmetic arith, CharSequence s, int start, int end, ParseMode mode) {
		long result = 0;
		boolean negative = false;
		int i = start;
		long limit = -Long.MAX_VALUE;

		if (end > start) {
			char firstChar = s.charAt(start);
			if (firstChar < '0') { // Possible leading "+" or "-"
				if (firstChar == '-') {
					negative = true;
					limit = Long.MIN_VALUE;
				} else {
					if (firstChar != '+') {
						// invalid first character
						throw newNumberFormatExceptionFor(arith, s);
					}
				}

				if (end - start == 1) {
					if (mode == ParseMode.IntegralPart) {
						// we allow something like "-.75" or "+.75"
						return 0;
					}
					// Cannot have lone "+" or "-"
					throw newNumberFormatExceptionFor(arith, s);
				}
				i++;
			}
			
			final int end2 = end - 1;
			while (i < end2) {
				final int digit0 = getDigit(arith, s, s.charAt(i++));
				final int digit1 = getDigit(arith, s, s.charAt(i++));
				final int inc = TENS[digit0] + digit1;
				if (result < (-Long.MAX_VALUE / 100)) {//same limit with Long.MIN_VALUE
					throw newNumberFormatExceptionFor(arith, s);
				}
				result *= 100;
				if (result < limit + inc) {
					throw newNumberFormatExceptionFor(arith, s);
				}
				result -= inc;
			}
			if (i < end) {
				final int digit = getDigit(arith, s, s.charAt(i++));
				if (result < (-Long.MAX_VALUE / 10)) {//same limit with Long.MIN_VALUE
					throw newNumberFormatExceptionFor(arith, s);
				}
				result *= 10;
				if (result < limit + digit) {
					throw newNumberFormatExceptionFor(arith, s);
				}
				result -= digit;
			}
		} else {
			throw newNumberFormatExceptionFor(arith, s);
		}
		return negative ? result : -result;
	}
	
	private static final int getDigit(final DecimalArithmetic arith, final CharSequence s, final char ch) {
		if (ch >= '0' & ch <= '9') {
			return (int) (ch - '0');
		} else {
			throw newNumberFormatExceptionFor(arith, s);
		}
	}
	
	private static final int[] TENS = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90};

	/**
	 * Returns a {@code String} object representing the specified {@code long}. The argument is converted to signed
	 * decimal representation and returned as a string, exactly as if passed to {@link Long#toString(long)}.
	 *
	 * @param value
	 *            a {@code long} to be converted.
	 * @return a string representation of the argument in base&nbsp;10.
	 */
	static final String longToString(long value) {
		return Long.toString(value);
	}

	/**
	 * Creates a {@code String} object representing the specified {@code long} and appends it to the given
	 * {@code appendable}.
	 *
	 * @param value
	 *            a {@code long} to be converted.
	 * @param appendable
	 *            t the appendable to which the string is to be appended
	 * @throws IOException
	 *             If an I/O error occurs when appending to {@code appendable}
	 */
	static final void longToString(long value, Appendable appendable) throws IOException {
		final StringBuilder sb = STRING_BUILDER_THREAD_LOCAL.get();
		sb.setLength(0);
		sb.append(value);
		appendable.append(sb);
	}

	/**
	 * Returns a {@code String} object representing the specified unscaled Decimal value {@code uDecimal}. The argument
	 * is converted to signed decimal representation and returned as a string with {@code scale} decimal places event if
	 * trailing fraction digits are zero.
	 *
	 * @param uDecimal
	 *            a unscaled Decimal to be converted
	 * @param arith
	 *            the decimal arithmetics providing the scale to apply
	 * @return a string representation of the argument
	 */
	static final String unscaledToString(DecimalArithmetic arith, long uDecimal) {
		return unscaledToStringBuilder(arith, uDecimal).toString();
	}

	/**
	 * Constructs a {@code String} object representing the specified unscaled Decimal value {@code uDecimal} and appends
	 * the constructed string to the given appendable argument. The value is converted to signed decimal representation
	 * and converted to a string with {@code scale} decimal places event if trailing fraction digits are zero.
	 *
	 * @param uDecimal
	 *            a unscaled Decimal to be converted to a string
	 * @param arith
	 *            the decimal arithmetics providing the scale to apply
	 * @param appendable
	 *            t the appendable to which the string is to be appended
	 * @throws IOException
	 *             If an I/O error occurs when appending to {@code appendable}
	 */
	static final void unscaledToString(DecimalArithmetic arith, long uDecimal, Appendable appendable) throws IOException {
		final StringBuilder sb = unscaledToStringBuilder(arith, uDecimal);
		appendable.append(sb);
	}

	private static final StringBuilder unscaledToStringBuilder(DecimalArithmetic arith, long uDecimal) {
		final StringBuilder sb = STRING_BUILDER_THREAD_LOCAL.get();
		sb.setLength(0);

		final int scale = arith.getScale();
		sb.append(uDecimal);
		final int len = sb.length();
		final int negativeOffset = uDecimal < 0 ? 1 : 0;
		if (len <= scale + negativeOffset) {
			// Long.MAX_VALUE = 9,223,372,036,854,775,807
			sb.insert(negativeOffset, "0.00000000000000000000", 0, 2 + scale - len + negativeOffset);
		} else {
			sb.insert(len - scale, '.');
		}
		return sb;
	}

	private static final NumberFormatException newNumberFormatExceptionFor(DecimalArithmetic arith, CharSequence s) {
		return new NumberFormatException(
				"Cannot parse Decimal value with scale " + arith.getScale() + " for input string: \"" + s + "\"");
	}

	private static final NumberFormatException newNumberFormatExceptionFor(DecimalArithmetic arith, CharSequence s, Exception cause) {
		final NumberFormatException ex = newNumberFormatExceptionFor(arith, s);
		ex.initCause(cause);
		return ex;
	}

	// no instances
	private StringConversion() {
		super();
	}
}
