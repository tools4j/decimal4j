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
package org.decimal4j.scale;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import org.decimal4j.arithmetic.Unsigned;
import org.decimal4j.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link ScaleMetrics} implementations.
 */
public class ScaleMetricsTest {

	private static final Random RND = new Random();

	@Test
	public void shouldCalcByScaleFactor() {
		for (final ScaleMetrics scaleMetrics : Scales.VALUES) {
			for (int i = 0; i < TestSettings.getRandomTestCount(); i++) {
				final int value = RND.nextInt();
				final long scaleFactor = scaleMetrics.getScaleFactor();

				// expected
				final long expMul = value * scaleFactor;
				final long expDiv = value / scaleFactor;
				final long expDivU = Unsigned.divide(value, scaleFactor);
				final long expMod = value % scaleFactor;
				final long expLo = (0xffffffffL & value) * (0xffffffffL & scaleFactor);
				final long expHi = (0xffffffffL & value) * (scaleFactor >>> 32);

				// actual + assert
				Assert.assertEquals("unexpected result " + value + " * " + scaleFactor, expMul,
						scaleMetrics.multiplyByScaleFactor(value));
				Assert.assertEquals("unexpected result " + value + " / " + scaleFactor, expDiv,
						scaleMetrics.divideByScaleFactor(value));
				Assert.assertEquals("unexpected result unsigned(" + value + " / " + scaleFactor + ")", expDivU,
						scaleMetrics.divideUnsignedByScaleFactor(value));
				Assert.assertEquals("unexpected result " + value + " % " + scaleFactor, expMod,
						scaleMetrics.moduloByScaleFactor(value));
				Assert.assertEquals("unexpected result for mullo(" + value + " * " + scaleFactor + ")", expLo,
						scaleMetrics.mulloByScaleFactor(value));
				Assert.assertEquals("unexpected result for mulhi(" + value + " * " + scaleFactor + ")", expHi,
						scaleMetrics.mulhiByScaleFactor(value));
			}
		}
	}

	@Test
	public void assertScaleMetricsConstants() {
		int scale = 0;
		long scaleFactor = 1;
		for (final ScaleMetrics scaleMetrics : Scales.VALUES) {
			Assert.assertEquals("unexpected scale", scale, scaleMetrics.getScale());
			Assert.assertEquals("unexpected scale factor", scaleFactor, scaleMetrics.getScaleFactor());
			Assert.assertEquals("unexpected BigInteger scale factor", BigInteger.valueOf(scaleFactor),
					scaleMetrics.getScaleFactorAsBigInteger());
			Assert.assertEquals("unexpected BigDecimal scale factor", BigDecimal.valueOf(scaleFactor),
					scaleMetrics.getScaleFactorAsBigDecimal());
			Assert.assertEquals("unexpected min integer value", Long.MIN_VALUE / scaleFactor,
					scaleMetrics.getMinIntegerValue());
			Assert.assertEquals("unexpected max integer value", Long.MAX_VALUE / scaleFactor,
					scaleMetrics.getMaxIntegerValue());
			Assert.assertEquals("unexpected NLZ(scale factor)", Long.numberOfLeadingZeros(scaleFactor),
					scaleMetrics.getScaleFactorNumberOfLeadingZeros());

			scale++;
			scaleFactor *= 10;
		}
	}
}
