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
package org.decimal4j.util;

/**
 * Created by terz on 17/9/17.
 */
public final class ChronicleDoubleParser {

    private static final long MAX_VALUE_DIVIDE_10 = Long.MAX_VALUE / 10;

    private static int lastDecimalPlaces = -1;

    public static double parseDouble(final CharSequence s) {
        if (s.length() == 0) {
            throw new IllegalArgumentException("Empty string");
        }
        long value = 0;
        int exp = 0;
        boolean negative = false;
        int decimalPlaces = Integer.MIN_VALUE;
        int ch;
        int index = 0;
        do {
            ch = s.charAt(index++);
        } while (ch == ' ' & index < s.length());

        try {
            switch (ch) {
                case 'N':
                    if (compareRest(s, index, "aN"))
                        return Double.NaN;
                    throw new IllegalArgumentException("Not a valid number: " + s);
                case 'I':
                    //noinspection SpellCheckingInspection
                    if (compareRest(s, index, "nfinity"))
                        return Double.POSITIVE_INFINITY;
                    throw new IllegalArgumentException("Not a valid number: " + s);
                case '-':
                    if (compareRest(s, index, "Infinity"))
                        return Double.NEGATIVE_INFINITY;
                    if (index == s.length()) {
                        throw new IllegalArgumentException("Not a valid number: " + s);
                    }
                    negative = true;
                    ch = s.charAt(index++);
                    break;
            }
            int tens = 0;
            while (true) {
                if (ch >= '0' && ch <= '9') {
                    while (value >= MAX_VALUE_DIVIDE_10) {
                        value >>>= 1;
                        exp++;
                    }
                    value = value * 10 + (ch - '0');
                    decimalPlaces++;

                } else if (ch == '.') {
                    decimalPlaces = 0;

                } else if (ch == 'E' || ch == 'e') {
                    tens = (int) parseLong(s, index);
                    break;

                } else {
                    break;
                }
                if (index == s.length())
                    break;
                ch = s.charAt(index++);
            }
            if (decimalPlaces < 0)
                decimalPlaces = 0;

            return asDouble(value, exp, negative, decimalPlaces - tens);
        } finally {
            lastDecimalPlaces = decimalPlaces;
        }
    }

    public static long parseLong(final CharSequence s) {
        return parseLong(s, 0);
    }

    private static long parseLong(final CharSequence s, final int offset) {
        if (offset >= s.length()) {
            throw new IllegalArgumentException("Empty string");
        }
        long num = 0;
        boolean negative = false;
        int index = offset;
        int b = s.charAt(index);
        if (b == '0') {
            index++;
            if (index < s.length()) {
                b = s.charAt(index);
                if (b == 'x' || b == 'X') {
                    index++;
                    throw new IllegalArgumentException("parseLongHexaDecimal(..) currently not implemented");
//                    return parseLongHexaDecimal(s);
                }
            }
        }
        while (index < s.length()) {
            b = s.charAt(index++);
            // if (b >= '0' && b <= '9')
            if ((b - ('0' + Integer.MIN_VALUE)) <= 9 + Integer.MIN_VALUE) {
                num = num * 10 + b - '0';
            } else if (b == '-') {
                negative = true;
            } else if (b == '_') {
                // ignore
            } else {
                break;
            }
        }
        for (int i = offset + s.length(); i < s.length(); i++) {
            if (s.charAt(i) != ' ') {
                throw new IllegalArgumentException("Not a long value: " + s);
            }
        }
        return negative ? -num : num;
    }

    private static double asDouble(long value, int exp, boolean negative, int decimalPlaces) {
        if (decimalPlaces > 0 && value < Long.MAX_VALUE / 2) {
            if (value < Long.MAX_VALUE / (1L << 32)) {
                exp -= 32;
                value <<= 32;
            }
            if (value < Long.MAX_VALUE / (1L << 16)) {
                exp -= 16;
                value <<= 16;
            }
            if (value < Long.MAX_VALUE / (1L << 8)) {
                exp -= 8;
                value <<= 8;
            }
            if (value < Long.MAX_VALUE / (1L << 4)) {
                exp -= 4;
                value <<= 4;
            }
            if (value < Long.MAX_VALUE / (1L << 2)) {
                exp -= 2;
                value <<= 2;
            }
            if (value < Long.MAX_VALUE / (1L << 1)) {
                exp -= 1;
                value <<= 1;
            }
        }
        if (decimalPlaces < 0) {
            for (; decimalPlaces < 0; decimalPlaces++) {
                exp++;
                int mod = 0;
                if (value > Long.MAX_VALUE / 5 * 4) {
                    mod = (int) (((value & 0x7) * 5 + 4) >> 3);
                    value >>= 3;
                    exp += 3;
                } else if (value > Long.MAX_VALUE / 5 * 2) {
                    mod = (int) (((value & 0x3) * 5 + 2) >> 2);
                    value >>= 2;
                    exp += 2;
                } else if (value > Long.MAX_VALUE / 5) {
                    mod = (int) (((value & 0x1) * 5 + 1) >> 1);
                    value >>= 1;
                    exp++;
                }
                value *= 5;
                value += mod;
            }

        } else {
            for (; decimalPlaces > 0; decimalPlaces--) {
                exp--;
                long mod = value % 5;
                value /= 5;
                int modDiv = 1;
                if (value < Long.MAX_VALUE / (1L << 4)) {
                    exp -= 4;
                    value <<= 4;
                    modDiv <<= 4;
                }
                if (value < Long.MAX_VALUE / (1L << 2)) {
                    exp -= 2;
                    value <<= 2;
                    modDiv <<= 2;
                }
                if (value < Long.MAX_VALUE / (1L << 1)) {
                    exp -= 1;
                    value <<= 1;
                    modDiv <<= 1;
                }
                if (decimalPlaces > 1)
                    value += modDiv * mod / 5;
                else
                    value += (modDiv * mod + 4) / 5;
            }
        }
        final double d = Math.scalb((double) value, exp);
        return negative ? -d : d;
    }

    private static boolean compareRest(final CharSequence in, final int offset, final String s) {
        if (s.length() > in.length() - offset)
            return false;
        for (int i = 0; i < s.length(); i++) {
            if (in.charAt(i + offset) != s.charAt(i)) {
                return false;
            }
        }
        for (int i = offset + s.length(); i < in.length(); i++) {
            if (in.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }
}
