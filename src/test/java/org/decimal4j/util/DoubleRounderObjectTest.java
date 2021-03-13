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
package org.decimal4j.util;

import org.decimal4j.scale.Scales;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for general object methods of {@link DoubleRounder} such as hashCode() and
 * equals(..), toString() etc.
 */
public class DoubleRounderObjectTest {
	
	@Test
	public void testHashCode() {
		for (int scale = Scales.MIN_SCALE; scale <= Scales.MAX_SCALE; scale++) {
			final DoubleRounder rounder1 = new DoubleRounder(scale);
			final DoubleRounder rounder2 = new DoubleRounder(Scales.getScaleMetrics(scale));
			Assert.assertEquals("should be same hash code for same scale", rounder1.hashCode(), rounder2.hashCode());
		}
	}

	@Test
	public void testEquals() {
		for (int scale1 = Scales.MIN_SCALE; scale1 <= Scales.MAX_SCALE; scale1++) {
			for (int scale2 = Scales.MIN_SCALE; scale2 <= Scales.MAX_SCALE; scale2++) {
				final DoubleRounder rounder1 = new DoubleRounder(scale1);
				final DoubleRounder rounder2 = new DoubleRounder(scale2);
				Assert.assertEquals("should be equal to itself", rounder1, rounder1);
				Assert.assertEquals("should be equal to itself", rounder2, rounder2);
				if (scale1 == scale2) {
					Assert.assertEquals("should be equal for same scale", rounder1, rounder2);
				} else {
					Assert.assertNotEquals("should not be equal for different scales", rounder1, rounder2);
				}
			}
		}
	}

	@Test
	public void testNotEquals() {
		final DoubleRounder rounder = new DoubleRounder(7);
		Assert.assertNotEquals("should not be equal to null", rounder, null);
		Assert.assertNotEquals("should not be equal to other object type", rounder, "bla");
	}

	@Test
	public void testToString() {
		for (int scale1 = Scales.MIN_SCALE; scale1 <= Scales.MAX_SCALE; scale1++) {
			for (int scale2 = Scales.MIN_SCALE; scale2 <= Scales.MAX_SCALE; scale2++) {
				final DoubleRounder rounder1 = new DoubleRounder(scale1);
				final DoubleRounder rounder2 = new DoubleRounder(scale2);
				if (scale1 == scale2) {
					Assert.assertEquals("should be same string for same scale", rounder1.toString(), rounder2.toString());
				} else {
					Assert.assertNotEquals("should be different strings for different scales", rounder1.toString(), rounder2.toString());
				}
			}
		}
	}
	
	@Test
	public void testGetPrecision() {
		for (int scale = Scales.MIN_SCALE; scale <= Scales.MAX_SCALE; scale++) {
			final DoubleRounder rounder = new DoubleRounder(scale);
			Assert.assertEquals("precision should be same as scale", scale, rounder.getPrecision());
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForPrecisionLessThanMinScale() {
		new DoubleRounder(Scales.MIN_SCALE - 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionForPrecisionGreaterThanMaxScale() {
		new DoubleRounder(Scales.MAX_SCALE + 1);
	}
}
