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

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scales;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Unit test addressing issues raised on github.
 */
public class GithubIssueTest {

    private static final double ZERO_TOLERANCE = 0;

    /**
     * Issue #17: Difference in RoundingMode.HALF_UP rounded value for 0.663125 at scale 5
     *
     * Part I -- reproduction of provided example
     *
     * See <a href="https://github.com/tools4j/decimal4j/issues/17">Github Issue 17</a>
     */
    @Test
    public void roundDoubleHalfUp_exact() {
        //given
        final double input = 0.663125;
        final String exactString = new BigDecimal(input).toPlainString();
        final DecimalArithmetic arith = Scales.getScaleMetrics(5).getArithmetic(RoundingMode.HALF_UP);
        final double expected = new BigDecimal(input).setScale(5, RoundingMode.HALF_UP).doubleValue();
        final double unexpected = BigDecimal.valueOf(input).setScale(5, RoundingMode.HALF_UP).doubleValue();

        //when
        final double actual = arith.toDouble(arith.fromDouble(input));

        //then
        assertEquals("round(" + exactString + ", 5)", expected, actual, ZERO_TOLERANCE);
        assertNotEquals("round(" + exactString + ", 5)", unexpected, actual, ZERO_TOLERANCE);
    }

    /**
     * Issue #17: Difference in RoundingMode.HALF_UP rounded value for 0.663125 at scale 5
     *
     * Part II -- proposed alternative ways to handle rounding of a double
     *
     * See <a href="https://github.com/tools4j/decimal4j/issues/17">Github Issue 17</a>
     */
    @Test
    public void roundDoubleHalfUp_string() {
        //given
        final double input = 0.663125;
        final double expected = 0.66313;
        final StringBuilder str = new StringBuilder().append(input);
        final DecimalArithmetic arith8 = Scales.getScaleMetrics(8).getRoundingHalfEvenArithmetic();
        final DecimalArithmetic arith5 = Scales.getScaleMetrics(5).getArithmetic(RoundingMode.HALF_UP);

        //when
        final double actual0 = BigDecimal.valueOf(input).setScale(5, RoundingMode.HALF_UP).doubleValue();
        final double actual1 = arith5.toDouble(arith5.fromUnscaled(arith8.fromDouble(input), arith8.getScale()));
        final double actual2 = arith5.toDouble(arith5.parse(String.valueOf(input)));
        final double actual3 = arith5.toDouble(arith5.parse(str, 0, str.length()));

        //then
        assertEquals("round(" + input + ", 5) via BigDecimal.valueOf", expected, actual0, ZERO_TOLERANCE);
        assertEquals("round(" + input + ", 5) via 2 step rounding", expected, actual1, ZERO_TOLERANCE);
        assertEquals("round(" + input + ", 5) via String", expected, actual2, ZERO_TOLERANCE);
        assertEquals("round(" + input + ", 5) via StringBuilder", expected, actual3, ZERO_TOLERANCE);
    }
}
