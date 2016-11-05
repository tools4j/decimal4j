/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 decimal4j (tools4j), Marco Terzer
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.decimal4j.op.util.FloatAndDoubleUtil;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.test.ArithmeticResult;
import org.decimal4j.test.TestSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link DoubleRounder}
 */
@RunWith(Parameterized.class)
public class DoubleRounderTest {

	private static final Random RND = new Random();

	private final int precision;
	private final RoundingMode roundingMode;

	public DoubleRounderTest(int precision, RoundingMode roundingMode) {
		this.precision = precision;
		this.roundingMode = roundingMode;
	}

	@Parameters(name = "{index}: precision={0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics precision : TestSettings.SCALES) {
			for (final RoundingMode mode : TestSettings.UNCHECKED_ROUNDING_MODES) {
				data.add(new Object[] { precision.getScale(), mode});
			}
		}
		return data;
	}

	@Test
	public void testSpecialDoubles() {
		int index = 0;
		for (final double d : FloatAndDoubleUtil.specialDoubleOperands(Scales.getScaleMetrics(precision))) {
			runTest("special[" + index + "]", d);
			index++;
		}
	}

	@Test
	public void testRandomDoubles() {
		final int n = TestSettings.getRandomTestCount();
		for (int i = 0; i < n; i++) {
			runTest("random[" + i + "]", FloatAndDoubleUtil.randomDoubleOperand(RND));
		}
	}

	private double expectedResult(double d) {
		if (!isFinite(d)) {
			return d;
		}
		//we need exact representation of the double, except when we check UNNECESSARY rounding mode
		final BigDecimal bd = roundingMode == RoundingMode.UNNECESSARY ? BigDecimal.valueOf(d) : new BigDecimal(d);
		return bd.setScale(precision, roundingMode).doubleValue();
	}
	
	private double actualResult(double d) {
		if (RND.nextBoolean()) {
			//static methods
			if (roundingMode == RoundingMode.HALF_UP & RND.nextBoolean()) {
				return DoubleRounder.round(d, precision);
			}
			return DoubleRounder.round(d, precision, roundingMode);
		}
		//create rounder instance
		final DoubleRounder rounder = new DoubleRounder(precision);
		if (roundingMode == RoundingMode.HALF_UP & RND.nextBoolean()) {
			return rounder.round(d);
		}
		return rounder.round(d, roundingMode);
	}

	private void runTest(String name, double d) {
		final String messagePrefix = getClass().getSimpleName() + name + ": round(" + d + ")";
		
		// expected
		ArithmeticResult<Double> expected;
		try {
			final double exp = expectedResult(d);
			expected = ArithmeticResult.forResult(String.valueOf(exp), exp);
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		} catch (NumberFormatException e) {
			expected = ArithmeticResult.forException(new IllegalArgumentException(e));
		} catch (IllegalArgumentException e) {
			expected = ArithmeticResult.forException(e);
		}

		// actual
		ArithmeticResult<Double> actual;
		try {
			final double act = actualResult(d);
			actual = ArithmeticResult.forResult(String.valueOf(act), act);
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		} catch (IllegalArgumentException e) {
			actual = ArithmeticResult.forException(e);
		}

		// assert
		actual.assertEquivalentTo(expected, messagePrefix);
	}

    private static boolean isFinite(double d) {
        return Math.abs(d) <= Double.MAX_VALUE;
    }
}
