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

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link DoubleParser}
 */
public class DoubleParserTest {

    private static final double NO_TOLERANCE = 0;
    private static final Random RND = new Random();
    private static final int RANDOM_RUNS = 100000;
    private static final int DECIMAL_RUNS = 100000;
    private static final int INTEGER_RUNS = 100000;

    private static final double toleranceUlp(final double d) {
        final double ulp = Math.ulp(d);
        final double pow10errorMultiplier = d == 0 ? 0 : 1 + Math.abs(Math.log10(Math.abs(d)) / 22);
//        return ulp * pow10errorMultiplier;
        return Math.max(ulp * pow10errorMultiplier, 1e-18);
//        return ulp;
//        return NO_TOLERANCE;
    }

    @Test
    public void parseInteger() throws Exception {
        for (int i = -INTEGER_RUNS; i < INTEGER_RUNS; i++) {
            assertEquals(i, DoubleParser.parseDouble(String.valueOf(i)), NO_TOLERANCE);
        }
        for (int i = 0; i < INTEGER_RUNS; i++) {
            testVariantsOf(i);
            assertEquals("" + (Integer.MIN_VALUE + i), Integer.MIN_VALUE + i, DoubleParser.parseDouble(String.valueOf(Integer.MIN_VALUE + i)), NO_TOLERANCE);
            assertEquals("" + (Integer.MAX_VALUE - i), Integer.MAX_VALUE - i, DoubleParser.parseDouble(String.valueOf(Integer.MAX_VALUE - i)), NO_TOLERANCE);
            assertEquals("" + (Long.MIN_VALUE + i), Long.MIN_VALUE + i, DoubleParser.parseDouble(String.valueOf(Long.MIN_VALUE + i)), NO_TOLERANCE);
            assertEquals("" + (Long.MAX_VALUE - i), Long.MAX_VALUE - i, DoubleParser.parseDouble(String.valueOf(Long.MAX_VALUE - i)), NO_TOLERANCE);
        }
        for (int i = -9; i < 9; i++) {
            for (long j = 100; j >= 0; j*=10) {
                final long value = i * j;
                for (long k = j/10; k > 0; k/=10) {
                    for (int l = 0; l < 9; l++) {
//                        System.out.println(value + Integer.signum(i)*l*k);
                        testVariantsOf(value + Integer.signum(i)*l*k);
                    }
                }
            }
        }
    }

    private void testVariantsOf(final long value) {
        assertEquals(value, DoubleParser.parseDouble("    " + value), NO_TOLERANCE);
        assertEquals(value, DoubleParser.parseDouble("    " + value + "   "), NO_TOLERANCE);
        if (value > 0) {
            assertEquals(value, DoubleParser.parseDouble("+" + value), NO_TOLERANCE);
            assertEquals(value, DoubleParser.parseDouble("0000" + value), NO_TOLERANCE);
            assertEquals(value, DoubleParser.parseDouble("+000" + value), NO_TOLERANCE);
            assertEquals(-value, DoubleParser.parseDouble("-00" + value), NO_TOLERANCE);
            assertEquals(value, DoubleParser.parseDouble("   +" + value), NO_TOLERANCE);
            assertEquals(value, DoubleParser.parseDouble("  +0" + value), NO_TOLERANCE);
            assertEquals(-value, DoubleParser.parseDouble(" -0" + value), NO_TOLERANCE);
            assertEquals(value, DoubleParser.parseDouble("+" + value), NO_TOLERANCE);
            assertEquals(value, DoubleParser.parseDouble(value + "            "), NO_TOLERANCE);
            assertEquals(value, DoubleParser.parseDouble("+000" + value + "   "), NO_TOLERANCE);
            assertEquals(value, DoubleParser.parseDouble("   +" + value + "   "), NO_TOLERANCE);
            assertEquals(value, DoubleParser.parseDouble("   0" + value + "   "), NO_TOLERANCE);
            assertEquals(value, DoubleParser.parseDouble("  +0" + value + "   "), NO_TOLERANCE);
            assertEquals(-value, DoubleParser.parseDouble(" -0" + value + "   "), NO_TOLERANCE);
        }
    }

    @Test
    public void parseDecimals() throws Exception {
        final String zeros = String.valueOf(DECIMAL_RUNS).substring(1);
        for (int integ = -9; integ <= 9; integ++) {
            for (int frac = 0; frac < DECIMAL_RUNS; frac++) {
                final String fract = String.valueOf(frac);
                final String pos = integ + "." + zeros.substring(fract.length()) + fract;
//                System.out.println(pos);
                assertEquals(pos, Double.parseDouble(pos), DoubleParser.parseDouble(pos), NO_TOLERANCE);
                if (integ == 0) {
                    final String neg = "-" + pos;
                    assertEquals(neg, Double.parseDouble(neg), DoubleParser.parseDouble(neg), NO_TOLERANCE);
                }
            }
        }
    }

    @Test
    public void parseDoubleRandomGaussian() throws Exception {
        for (int i = 0; i < RANDOM_RUNS; i++) {
            final double d = RND.nextGaussian();
            final double tolerance = toleranceUlp(d);
            assertEquals("+random-gaussian[" + i + "]", d, DoubleParser.parseDouble(String.valueOf(d)), tolerance);
            assertEquals("-random-gaussian[" + i + "]", -d, DoubleParser.parseDouble(String.valueOf(-d)), tolerance);
        }
    }

    @Test
    public void parseDoubleRandomUniform() throws Exception {
        for (int i = 0; i < RANDOM_RUNS; i++) {
            final double d = RND.nextDouble();
            final double tolerance = toleranceUlp(d);
            assertEquals("+random-uniform[" + i + "]", d, DoubleParser.parseDouble(String.valueOf(d)), tolerance);
            assertEquals("-random-uniform[" + i + "]", -d, DoubleParser.parseDouble(String.valueOf(-d)), tolerance);
        }
    }

    @Test
    public void parseDoubleRandomBits() throws Exception {
        for (int i = 0; i < RANDOM_RUNS; i++) {
            final double d = Double.longBitsToDouble(RND.nextLong());
            final double tolerance = toleranceUlp(d);
            assertEquals("+random-bits[" + i + "]", d, DoubleParser.parseDouble(String.valueOf(d)), tolerance);
            assertEquals("-random-bits[" + i + "]", -d, DoubleParser.parseDouble(String.valueOf(-d)), tolerance);
        }
    }

    @Test
    public void parseDoubleRandomLong() throws Exception {
        for (int i = 0; i < RANDOM_RUNS; i++) {
            final long l = RND.nextLong();
            assertEquals("+random-long[" + i + "]: " + l, l, DoubleParser.parseDouble(String.valueOf(l)), NO_TOLERANCE);
        }
    }

    @Test
    public void parseDouble() throws Exception {
        assertEquals( 0.0, DoubleParser.parseDouble("0.0"), NO_TOLERANCE);
        assertEquals(-0.0, DoubleParser.parseDouble("-0.0"), NO_TOLERANCE);
        assertEquals(+0.0, DoubleParser.parseDouble("+0.0"), NO_TOLERANCE);
        assertEquals( 1.0, DoubleParser.parseDouble("1.0"), NO_TOLERANCE);
        assertEquals(-1.0, DoubleParser.parseDouble("-1.0"), NO_TOLERANCE);
        assertEquals(+1.0, DoubleParser.parseDouble("+1.0"), NO_TOLERANCE);
        assertEquals( 1.1, DoubleParser.parseDouble("1.1"), NO_TOLERANCE);
        assertEquals(-1.1, DoubleParser.parseDouble("-1.1"), NO_TOLERANCE);
        assertEquals(+1.1, DoubleParser.parseDouble("+1.1"), NO_TOLERANCE);
        assertEquals( 0.1, DoubleParser.parseDouble("0.1"), NO_TOLERANCE);
        assertEquals(-0.1, DoubleParser.parseDouble("-0.1"), NO_TOLERANCE);
        assertEquals(+0.1, DoubleParser.parseDouble("+0.1"), NO_TOLERANCE);
        assertEquals(  .1, DoubleParser.parseDouble(".1"), NO_TOLERANCE);
        assertEquals( -.1, DoubleParser.parseDouble("-.1"), NO_TOLERANCE);
        assertEquals( +.1, DoubleParser.parseDouble("+.1"), NO_TOLERANCE);
    }

    @Test
    public void parseDoubleTricky() {
        assertEquals(2.3535312534527568E-4, DoubleParser.parseDouble("2.3535312534527568E-4"), NO_TOLERANCE);
        assertEquals(0.0014496816689130165, DoubleParser.parseDouble("0.0014496816689130165"), toleranceUlp(0.0014496816689130165));
        assertEquals(9.223372036854776E18, DoubleParser.parseDouble("9223372036854775679"), NO_TOLERANCE);
        assertEquals(-9.223372036854776E18, DoubleParser.parseDouble("-9223372036854775808"), NO_TOLERANCE);
    }
}