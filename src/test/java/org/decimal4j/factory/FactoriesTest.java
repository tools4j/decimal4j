/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.junit.Test;

/**
 * Unit test for {@link Factories}.
 */
public class FactoriesTest {
	
	@Test
	public void shouldGetFactoryByScale() {
		for (int scale = Scales.MIN_SCALE; scale <= Scales.MAX_SCALE; scale++) {
			//when
			final DecimalFactory<?> factory = Factories.getDecimalFactory(scale);
			//then
			assertNotNull("factory should not be null", factory);
			assertEquals("factory should have scale " + scale, scale, factory.getScaleMetrics().getScale());
			//when
			final DecimalFactory<?> generic = Factories.getGenericDecimalFactory(scale);
			//then
			assertNotNull("generic factory should not be null", generic);
			assertEquals("generic factory should have scale " + scale, scale, generic.getScaleMetrics().getScale());
		}
	}
	
	@Test
	public void shouldGetFactoryByScaleMetrics() {
		for(final ScaleMetrics scaleMetrics : Scales.VALUES) {
			//when
			final DecimalFactory<?> factory = Factories.getDecimalFactory(scaleMetrics);
			//then
			assertNotNull("factory should not be null", factory);
			assertSame("factory should have scale metrics " + scaleMetrics, scaleMetrics, factory.getScaleMetrics());
			//when
			final DecimalFactory<?> generic = Factories.getGenericDecimalFactory(scaleMetrics);
			//then
			assertNotNull("generic factory should not be null", generic);
			assertSame("generic factory should have scale metrics " + scaleMetrics, scaleMetrics, generic.getScaleMetrics());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForNegativeScale() {
		Factories.getDecimalFactory(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void genericShouldThrowExceptionForNegativeScale() {
		Factories.getGenericDecimalFactory(-1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForScaleExceedingMax() {
		Factories.getDecimalFactory(Scales.MAX_SCALE + 1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void genericShouldThrowExceptionForScaleExceedingMax() {
		Factories.getGenericDecimalFactory(Scales.MAX_SCALE + 1);
	}
	
	@Test
	public void valuesListShouldBeSortedByScale() {
		assertEquals("Factories.VALUES size not equal to all scales", Scales.MAX_SCALE - Scales.MIN_SCALE + 1, Factories.VALUES.size());
		int scale = 0;
		for (final DecimalFactory<?> factory : Factories.VALUES) {
			assertEquals("should have scale " + scale, scale, factory.getScaleMetrics().getScale());
			scale++;
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void valuesListShouldBeImmutable() {
		Factories.VALUES.clear();
	}

	@Test
	public void testFactorySingleton() throws Exception {
		for (final DecimalFactory<?> factory : Factories.VALUES) {
			//when
			final Object instance = factory.getClass().getMethod("valueOf", String.class).invoke(null, "INSTANCE");
			//then
			assertSame("should be factory instance", factory, instance);
			//when
			final Object instances = factory.getClass().getMethod("values").invoke(null);
			//then
			assertTrue("should be an array", instances instanceof Object[]);
			assertEquals("should be array length 1", 1, ((Object[])instances).length);
			assertSame("should be same factory", factory, ((Object[])instances)[0]);
		}
	}

}
