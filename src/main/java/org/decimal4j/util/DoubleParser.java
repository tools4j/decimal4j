package org.decimal4j.util;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scale0f;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.scale.Scales;

public class DoubleParser {

    private static final double[] POW10 = {
            1e0, 1e1, 1e2, 1e3, 1e4, 1e5, 1e6, 1e7, 1e8, 1e9,
            1e10, 1e11, 1e12, 1e13, 1e14, 1e15, 1e16, 1e17, 1e18, 1e19,
            1e20, 1e21, 1e22
    };

    public static double parseDouble(final CharSequence s) {
//        return ChronicleDoubleParser.parseDouble(s);
        return parseDouble(s, 0, s.length());
    }
    public static double parseDouble(final CharSequence s, final int off, final int len) {
        final int end = skipTrailingWhitespace(s, off, off + len);
        final int start = skipLeadingWhitespace(s, off, end);
        if (start >= end) {
            throw new IllegalArgumentException("empty string");
        }
        int sgn = 1;
        int pos = start;
        char ch = s.charAt(pos);
        if (ch == '-') {
            pos++;
            sgn = -1;
        } else if (ch == '+') {
            pos++;
        }
        if (pos == end) {
            throw new IllegalArgumentException("Invalid number: " + s.subSequence(start, end));
        }
        if (equalsIgnoreCase("NaN", s, pos, end)) {
            return Double.NaN;
        }
        if (equalsIgnoreCase("Infinity", s, pos, end) || equalsIgnoreCase("Inf", s, pos, end)) {
            return sgn * Double.POSITIVE_INFINITY;
        }
        pos = skipLeadingZeros(s, pos, end);
        if (pos == end) {
            return sgn > 0 ? +0.0 : -0.0;
        }
        if (pos + 1 == end) {
            return sgn * parseDigit(s, start, end, pos);
        }
        final int ixE = indexOf(s, pos, end, 'e', 'E');
        final int ixP = indexOf(s, pos, ixE, '.');
        final int exp = ixE < end ? parseExponent(s, start, end, ixE + 1) : 0;
        final int ixF = ixP < ixE ? skipTrailingZeros(s, pos, ixE) : ixP;

        final int integralDigits = ixP - pos;
        final int fractionalDigits = ixP < ixF ? ixF - ixP - 1 : 0;
        if (fractionalDigits > 0 & integralDigits <= 18 & exp < fractionalDigits) {
            final int scale = Scales.MAX_SCALE - integralDigits;
//        if (integralDigits + fractionalDigits <= 18) {
//        if (integralDigits + fractionalDigits <= 18 && -22 <= exp && exp <= 22) {
//        if (integralDigits + fractionalDigits - Math.min(0, exp) <= 18 && -22 <= exp && exp <= 22) {
            final DecimalArithmetic arith = Scales.getScaleMetrics(scale).getRoundingHalfEvenArithmetic();
            final double d = sgn * arith.toDouble(arith.parse(s, pos, ixF));
            return pow10(d, exp);
        }

        double di = 0;
        long li = 0;
        while (pos < ixP) {
            final int plen = Math.min(ixP - pos, 18);
            final int nextPos = Math.min(pos + plen, ixP);
            final long pow10 = (long)POW10[plen];
            if (li > Long.MAX_VALUE / pow10) {
                break;
            }
            li *= pow10;
            final long lval = Scale0f.INSTANCE.getRoundingDownArithmetic().parse(s, pos, nextPos);
            pos = nextPos;
            if (li > Long.MAX_VALUE - lval) {
                di = Scale0f.INSTANCE.getRoundingHalfEvenArithmetic().toDouble(lval);
                break;
            }
            li += lval;
        }
        di += Scale0f.INSTANCE.getRoundingHalfEvenArithmetic().toDouble(li);
        while (pos < ixP) {
            final int plen = Math.min(ixP - pos, 18);
            final int nextPos = Math.min(pos + plen, ixP);
            di *= POW10[plen];
            final long lval = Scale0f.INSTANCE.getRoundingDownArithmetic().parse(s, pos, nextPos);
            final double dval = Scale0f.INSTANCE.getRoundingHalfEvenArithmetic().toDouble(lval);
            di += dval;
            pos = nextPos;
        }
//        pos++;//skip the decimal point
        if (pos < ixF) {
            final DecimalArithmetic arith = Scale18f.INSTANCE.getRoundingHalfEvenArithmetic();
            final double d = sgn * (di + arith.toDouble(arith.parse(s, pos, ixF)));
            return pow10(d, exp);
        }

        return pow10(sgn * di, exp);
    }

    private static double pow10(final double val, final int exp) {
        double d = val;
        int e = exp;
        while (e != 0) {
            final int p = Math.min(Math.abs(e), 22);
            if (e > 0) d *= POW10[p];
            else d /= POW10[p];
            e -= Math.signum(e) * p;
        }
        return d;
    }

    private static int parseExponent(final CharSequence s, final int start, final int end, final int expStart) {
        if (expStart == end) {
            throw new IllegalArgumentException("Empty exponent in " + s.subSequence(start, end));
        }
        int sgn = 1;
        int pos = expStart;
        char ch = s.charAt(pos);
        if (ch == '-') {
            pos++;
            sgn = -1;
        } else if (ch == '+') {
            pos++;
        }
        if (pos == end) {
            throw new IllegalArgumentException("Empty exponent in " + s.subSequence(start, end));
        }
        if (expStart + 4 < end) {
            throw new IllegalArgumentException("Illegal exponent in " + s.subSequence(start, end));
        }
        int exp = parseDigit(s, start, end, pos);
        while (++pos < end) {
            exp = 10 * exp + parseDigit(s, start, end, pos);
        }
        if (Double.MIN_EXPONENT <= exp & exp <= Double.MAX_EXPONENT) {
            return sgn*(int)exp;
        }
        throw new IllegalArgumentException("Exponent out of bounds in " + s.subSequence(start, end));
    }

    private static int parseDigit(final CharSequence s, final int start, final int end, final int pos) {
        final char ch = s.charAt(pos);
        if ('0' <= ch & ch <= '9') {
            return ch - '0';
        }
        throw new IllegalArgumentException("Invalid character at position " + pos + " in: " + s.subSequence(start, end));
    }

    private static int skipLeadingZeros(final CharSequence s, final int start, final int end) {
        for (int i = start; i < end; i++) {
            if ('0' != s.charAt(i)) {
                return i;
            }
        }
        return end;
    }

    private static int skipTrailingZeros(final CharSequence s, final int start, final int end) {
        for (int i = end; i > start; i--) {
            if ('0' != s.charAt(i - 1)) {
                return i;
            }
        }
        return start;
    }

    private static int skipLeadingWhitespace(final CharSequence s, final int start, final int end) {
        for (int i = start; i < end; i++) {
            if (!isWhitespace(s.charAt(i))) {
                return i;
            }
        }
        return end;
    }

    private static int skipTrailingWhitespace(final CharSequence s, final int start, final int end) {
        for (int i = end; i > start; i--) {
            if (!isWhitespace(s.charAt(i - 1))) {
                return i;
            }
        }
        return start;
    }

    private static boolean equalsIgnoreCase(final String str, final CharSequence s, final int start, final int end) {
        if (end - start == str.length()) {
            for (int i = start; i < end; i++) {
                if (str.charAt(i - start) != s.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static int indexOf(final CharSequence s, final int start, final int end, final char ch) {
        for (int i = start; i < end; i++) {
            if (s.charAt(i) == ch) {
                return i;
            }
        }
        return end;
    }

    private static int indexOf(final CharSequence s, final int start, final int end, final char ch1, final char ch2) {
        for (int i = start; i < end; i++) {
            final char ch = s.charAt(i);
            if (ch == ch1 | ch == ch2) {
                return i;
            }
        }
        return end;
    }

    private static boolean isWhitespace(final char ch) {
        return ch <= ' ';
    }
}
