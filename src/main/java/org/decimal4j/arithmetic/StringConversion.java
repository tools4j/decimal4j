/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 decimal4j (tools4j), Marco Terzer
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

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;

import java.io.IOException;

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
		Long, IntegralPart, IntegralExtra, Exponent
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
		final int indexOfExponentIndicator = indexOfExponentIndicator(s, indexOfDecimalPoint < 0 ? start : indexOfDecimalPoint + 1, end);
		if (indexOfExponentIndicator >= 0) {
			return parseScientificNotation(arith, rounding, s, start, end, indexOfDecimalPoint, indexOfExponentIndicator);
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
				// allowed formats: "0.45", "+0.45", "-0.45", ".45", "+.45", "-.45" etc
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
			final long unscaledIntegral = scaleMetrics.multiplyByScaleFactorExact(integralPart);
			final long unscaledFractional = negative ? -fractionalPart : fractionalPart;// < Scale18.SCALE_FACTOR hence no overflow
			final long truncatedValue = Checked.add(arith, unscaledIntegral, unscaledFractional);
			final int roundingIncrement = rounding.calculateRoundingIncrement(negative ? -1 : 1, truncatedValue,
					truncatedPart);
			return roundingIncrement == 0 ? truncatedValue : Checked.add(arith, truncatedValue, roundingIncrement);
		} catch (ArithmeticException e) {
			throw newNumberFormatExceptionFor(arith, s, start, end, e);
		}
	}

	private static final long parseScientificNotation(DecimalArithmetic arith, DecimalRounding rounding, CharSequence s,
													  int start, int end, int indexOfDecimalPoint, int indexOfExponentIndicator) {
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();;
		final int scale = scaleMetrics.getScale();
		final long expLong = parseIntegralPart(arith, s, indexOfExponentIndicator + 1, end, ParseMode.Exponent);
		final int exp = expLong < -Integer.MAX_VALUE ? -Integer.MAX_VALUE : expLong > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)expLong;

		final int integralStart = start;
		final int integralEnd = indexOfDecimalPoint >= 0 ? indexOfDecimalPoint : indexOfExponentIndicator;
		final int fractionalStart = indexOfDecimalPoint >= 0 ? indexOfDecimalPoint + 1 : indexOfExponentIndicator;
		final int fractionalEnd = indexOfExponentIndicator;

		final int signLen = s.charAt(integralStart) == '-' | s.charAt(integralStart) == '+' ? 1 : 0;
		final int integralDigitsToConvert = exp >= 0 ? 0 : Math.min(-exp, integralEnd - integralStart - signLen);
		final int fractionalDigitsToConvert = exp <= 0 ? 0 : Math.min(exp, fractionalEnd - fractionalStart);
		final int exponentToMultiply = exp <= 0 ? exp + integralDigitsToConvert : exp - fractionalDigitsToConvert;
		final int integralToFractional = Math.min(scale, integralDigitsToConvert);
		final int integralToTruncated = integralDigitsToConvert - integralToFractional;
		final int fractionalToIntegral = fractionalDigitsToConvert;
		final int fractionalToTruncated = Math.min(0, (fractionalEnd - fractionalStart) - scale - fractionalDigitsToConvert);

		final int intgStart = integralStart;
		final int fraxStart = integralEnd - integralToTruncated - integralToFractional;
		final int truxStart = integralEnd - integralToTruncated;
		final int truxEnd = integralEnd;
		final int fraxEnd = truxStart;
		final int intgEnd = fraxStart;

		final int intxStart = fractionalStart;
		final int fracStart = fractionalStart + fractionalToIntegral;
		final int trunStart = fractionalStart + fractionalToIntegral + fractionalToTruncated;
		final int trunEnd = fractionalEnd;
		final int fracEnd = trunStart;
		final int intxEnd = fracStart;

		final long integralPart = parseIntegralPart(arith, s, intgStart, intgEnd, ParseMode.IntegralPart);
		final long integralExtra = parseIntegralPart(arith, s, intxStart, intxEnd, ParseMode.IntegralExtra);
		final long fractionalPart = parseFractionalPartRaw(arith, s, fracStart, fracEnd);
		final long fractionalExtra = parseFractionalPartRaw(arith, s, fraxStart, fraxEnd);
		TruncatedPart truncatedPart = parseTruncatedPart(arith, s, trunStart, trunEnd);
		if (integralToTruncated > 0) {
			final TruncatedPart truncatedIntegral = parseTruncatedPart(arith, s, truxStart, truxEnd);
			truncatedPart = truncatedIntegral.andThen(truncatedPart);
		}
		if (exponentToMultiply > 0) {
			truncatedPart = TruncatedPart.ZERO.andThen(truncatedPart);
		}
		final boolean negative = integralPart < 0 | (integralPart == 0 && s.charAt(start) == '-');

		if (truncatedPart.isGreaterThanZero() & rounding == DecimalRounding.UNNECESSARY) {
			throw Exceptions.newRoundingNecessaryArithmeticException();
		}
		try {
			final int fractionDigits = fracEnd - fracStart + fraxEnd - fraxStart;
			final ScaleMetrics fractionScale = Scales.getScaleMetrics(scale - fractionDigits);
			final ScaleMetrics fractionScaleH = Scales.getScaleMetrics(fracEnd - fracStart);
			final ScaleMetrics integralScaleH = Scales.getScaleMetrics(intxEnd - intxStart);

			final long unscaledIntH = integralScaleH.multiplyByScaleFactorExact(integralPart);
			final long unscaledIntL = integralExtra;
			final long unscaledIntHL = Checked.add(arith, unscaledIntH, unscaledIntL);
			final long unscaledIntegral = scaleMetrics.multiplyByScaleFactorExact(unscaledIntHL);
			final long unscaledFracH = fractionScaleH.multiplyByScaleFactorExact(fractionalExtra);
			final long unscaledFracL = fractionalPart;
			final long unscaledFracHL = negative ? -(unscaledFracH + unscaledFracL) : (unscaledFracH + unscaledFracL);
			final long unscaledFractional = fractionScale.multiplyByScaleFactorExact(unscaledFracHL);
			final long unscaledValue = Checked.add(arith, unscaledIntegral, unscaledFractional);
			final long truncatedValue = Pow10.multiplyByPowerOf10Checked(arith, rounding, unscaledValue, exponentToMultiply);
			final int roundingIncrement = rounding.calculateRoundingIncrement(negative ? -1 : 1, truncatedValue,
					truncatedPart);
			return roundingIncrement == 0 ? truncatedValue : Checked.add(arith, truncatedValue, roundingIncrement);
		} catch (ArithmeticException e) {
			throw newNumberFormatExceptionFor(arith, s, start, end, e);
		}
	}

	private static final long parseFractionalPart(DecimalArithmetic arith, CharSequence s, int start, int end) {
		final long value = parseFractionalPartRaw(arith, s, start, end);
		if (value != 0) {
			final int len = end - start;
			final int scale = arith.getScale();
			if (len < scale) {
				final ScaleMetrics diffScale = Scales.getScaleMetrics(scale - len);
				return diffScale.multiplyByScaleFactor(value);
			}
		}
		return value;
	}

	private static final long parseFractionalPartRaw(DecimalArithmetic arith, CharSequence s, int start, int end) {
		if (start < end) {
			long value = getDigit(arith, s, start, end, s.charAt(start));
			for (int i = start + 1; i < end; i++) {
				value *= 10;
				value += getDigit(arith, s, start, end, s.charAt(i));
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
				throw newNumberFormatExceptionFor(arith, s, start, end);
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
					throw newNumberFormatExceptionFor(arith, s, start, end);
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

	private static final int indexOfExponentIndicator(CharSequence s, int start, int end) {
		for (int i = start; i < end; i++) {
			final char ch = s.charAt(i);
			if (ch == 'e' || ch == 'E') {
				return i;
			}
		}
		return -1;
	}

	// see Long.parseLong(String, int) but for fixed radix 10
	private static final long parseIntegralPart(DecimalArithmetic arith, CharSequence s, int start, int end, ParseMode mode) {
		long result = 0;
		boolean negative = false;
		int i = start;
		long limit = -Long.MAX_VALUE;

		if (end > start) {
			char firstChar = s.charAt(start);
			if (firstChar < '0') { // Possible leading "+" or "-"
				if (mode == ParseMode.IntegralExtra) {
					throw newNumberFormatExceptionFor(arith, s, start, end);
				}
				if (firstChar == '-') {
					negative = true;
					limit = Long.MIN_VALUE;
				} else {
					if (firstChar != '+') {
						// invalid first character
						throw newNumberFormatExceptionFor(arith, s, start, end);
					}
				}

				if (end - start == 1) {
					if (mode == ParseMode.IntegralPart) {
						// we allow something like "-.75" or "+.75"
						return 0;
					}
					// Cannot have lone "+" or "-"
					throw newNumberFormatExceptionFor(arith, s, start, end);
				}
				i++;
			}
			
			final int end2 = end - 1;
			while (i < end2) {
				final int digit0 = getDigit(arith, s, start, end, s.charAt(i++));
				final int digit1 = getDigit(arith, s, start, end, s.charAt(i++));
				final int inc = TENS[digit0] + digit1;
				if (result < (-Long.MAX_VALUE / 100)) {//same limit with Long.MIN_VALUE
					if (mode != ParseMode.Exponent) {
						throw newNumberFormatExceptionFor(arith, s, start, end);
					}
					return excessiveExponent(arith, s, start, end, negative, i);
				}
				result *= 100;
				if (result < limit + inc) {
					if (mode != ParseMode.Exponent) {
						throw newNumberFormatExceptionFor(arith, s, start, end);
					}
					return excessiveExponent(arith, s, start, end, negative, i);
				}
				result -= inc;
			}
			if (i < end) {
				final int digit = getDigit(arith, s, start, end, s.charAt(i++));
				if (result < (-Long.MAX_VALUE / 10)) {//same limit with Long.MIN_VALUE
					if (mode != ParseMode.Exponent) {
						throw newNumberFormatExceptionFor(arith, s, start, end);
					}
					return excessiveExponent(arith, s, start, end, negative, i);
				}
				result *= 10;
				if (result < limit + digit) {
					if (mode != ParseMode.Exponent) {
						throw newNumberFormatExceptionFor(arith, s, start, end);
					}
					return excessiveExponent(arith, s, start, end, negative, i);
				}
				result -= digit;
			}
		} else {
			if (mode == ParseMode.IntegralPart || mode == ParseMode.IntegralExtra) {
				return 0;
			}
			throw newNumberFormatExceptionFor(arith, s, start, end);
		}
		return negative ? result : -result;
	}

	private static final long excessiveExponent(final DecimalArithmetic arith, final CharSequence s,
												final int start, final int end, final boolean negative,
												final int index) {
		//check all remaining exponent chars are indeed numeric
		for (int i = index; i < end; i++) {
			getDigit(arith, s, start, end, s.charAt(i));
		}
		return negative ? Long.MIN_VALUE : Long.MAX_VALUE;
	}
	
	private static final int getDigit(final DecimalArithmetic arith, final CharSequence s,
									  final int start, final int end, final char ch) {
		if (ch >= '0' & ch <= '9') {
			return (int) (ch - '0');
		} else {
			throw newNumberFormatExceptionFor(arith, s, start, end);
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

	private static final NumberFormatException newNumberFormatExceptionFor(DecimalArithmetic arith, CharSequence s, int start, int end) {
		return new NumberFormatException(
				"Cannot parse Decimal value with scale " + arith.getScale() + " for input string: \"" + s.subSequence(start, end) + "\"");
	}

	private static final NumberFormatException newNumberFormatExceptionFor(DecimalArithmetic arith, CharSequence s, int start, int end, Exception cause) {
		final NumberFormatException ex = newNumberFormatExceptionFor(arith, s, start, end);
		ex.initCause(cause);
		return ex;
	}

	// no instances
	private StringConversion() {
		super();
	}
}
