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
package org.decimal4j.scale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for {@link Scales}.
 */
public class ScalesTest {
	
	@Test
	public void shouldGetScaleMetricsByScale() {
		for (int scale = Scales.MIN_SCALE; scale <= Scales.MAX_SCALE; scale++) {
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(scale);
			assertNotNull("scaleMetrics should not be null", scaleMetrics);
			assertEquals("scaleMetrics should have scale " + scale, scale, scaleMetrics.getScale());
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForNegativeScale() {
		Scales.getScaleMetrics(-1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForScaleExceedingMax() {
		Scales.getScaleMetrics(Scales.MAX_SCALE + 1);
	}
	
	@Test
	public void valuesListShouldBeSortedByScale() {
		assertEquals("Scales.VALUES size does not equal all scales", Scales.MAX_SCALE - Scales.MIN_SCALE + 1, Scales.VALUES.size());
		int scale = 0;
		for (final ScaleMetrics scaleMetrics : Scales.VALUES) {
			assertEquals("should have scale " + scale, scale, scaleMetrics.getScale());
			scale++;
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void valuesListShouldBeImmutable() {
		Scales.VALUES.clear();
	}

	@Test
	public void testScaleMetricsSingleton() throws Exception {
		for (final ScaleMetrics scaleMetrics : Scales.VALUES) {
			//when
			final Object instance = scaleMetrics.getClass().getMethod("valueOf", String.class).invoke(null, "INSTANCE");
			//then
			assertSame("should be same metric instance", scaleMetrics, instance);
			//when
			final Object instances = scaleMetrics.getClass().getMethod("values").invoke(null);
			//then
			assertTrue("should be an array", instances instanceof Object[]);
			assertEquals("should be array length 1", 1, ((Object[])instances).length);
			assertSame("should be same metric instance", scaleMetrics, ((Object[])instances)[0]);
		}
	}

}
