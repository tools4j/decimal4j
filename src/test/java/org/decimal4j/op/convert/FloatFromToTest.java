/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.op.convert;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.op.util.FloatAndDoubleUtil;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests {@link DecimalArithmetic#fromFloat(float)} and {@link DecimalArithmetic#toFloat(long)}
 * and checks that the result is the same as the original input (if appropriate rounding modes
 * are used and some tolerance is allowed for 2 possible truncations).
 */
@RunWith(Parameterized.class)
public class FloatFromToTest {

	private static final Random RND = new Random();

	private final DecimalArithmetic arithmetic;
	private final RoundingMode backRounding;

	public FloatFromToTest(ScaleMetrics s, RoundingMode roundingMode, DecimalArithmetic arithmetic) {
		this.arithmetic = arithmetic;
		this.backRounding = FloatAndDoubleUtil.getOppositeRoundingMode(roundingMode);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final RoundingMode mode : TestSettings.UNCHECKED_ROUNDING_MODES) {
				if (mode != RoundingMode.UNNECESSARY) {
					final DecimalArithmetic arith = s.getArithmetic(mode);
					data.add(new Object[] { s, mode, arith });
				}
			}
		}
		return data;
	}

	@Test
	public void testSpecialFloats() {
		int index = 0;
		for (final float f : FloatAndDoubleUtil.specialFloatOperands(arithmetic.getScaleMetrics())) {
			runTest("special[" + index + "]", f);
			index++;
		}
	}

	@Test
	public void testRandomFloats() {
		final int n = TestSettings.getRandomTestCount();
		for (int i = 0; i < n; i++) {
			runTest("random[" + i + "]", FloatAndDoubleUtil.randomFloatOperand(RND));
		}
	}

	private void runTest(String name, float f) {
		try {
			final long uDecimal = arithmetic.fromFloat(f);
			final float result = arithmetic.getScaleMetrics().getArithmetic(backRounding).toFloat(uDecimal);
			final float tolerance = 2.0f*max(Math.ulp(result), Math.ulp(f), 1.0f/arithmetic.getScaleMetrics().getScaleFactor());
			Assert.assertEquals(name + ": result after 2 conversions should be same as input with tolerance=<" + tolerance + ">, delta=<" + Math.abs(f-result) + ">",  f, result, tolerance);
		} catch (IllegalArgumentException e) {
			//ignore, must be out of range, tested elsewhere
		}
	}
	
	private static float max(float val1, float val2, float val3) {
		return Math.max(Math.max(val1, val2), val3);
	}

}
