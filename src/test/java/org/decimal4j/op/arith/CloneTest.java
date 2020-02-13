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
package org.decimal4j.op.arith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.AbstractDecimalTest;
import org.decimal4j.test.TestSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#abs()}
 */
@RunWith(Parameterized.class)
public class CloneTest extends AbstractDecimalTest {
	
	public CloneTest(ScaleMetrics scaleMetrics, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: scale={0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] {s, s.getDefaultArithmetic()});
		}
		return data;
	}

	@Test
	public void testRandom() {
		final int count = TestSettings.getRandomTestCount();
		for (int i = 0; i < count; i++) {
			final Decimal<?> value = newDecimal(getScaleMetrics(), nextLongOrInt());
			runTest("random[" + i + "]: + value", value);
		}
	}
	
	@Test
	public void testSpecial() {
		int i = 0;
		for (final long special : TestSettings.TEST_CASES.getSpecialValuesFor(getScaleMetrics())) {
			final Decimal<?> value = newDecimal(getScaleMetrics(), special);
			runTest("special[" + i + "]: + value", value);
			i++;
		}
	}
	
	private <S extends ScaleMetrics> void runTest(String testName, Decimal<S> value) {
		final MutableDecimal<?> mutable = value.toMutableDecimal();
		final MutableDecimal<?> clone = mutable.clone();
		
		assertEquals("unscaled value should be equal", value.unscaledValue(), clone.unscaledValue());
		assertEquals("unscaled value should be equal", mutable.unscaledValue(), clone.unscaledValue());
		
		assertEquals("value should be equal", value, clone);
		assertEquals("value should be equal", mutable, clone);
		
		assertEquals("should be same scale", value.getScale(), clone.getScale());
		assertEquals("should be same scale", mutable.getScale(), clone.getScale());

		assertNotSame("value should be different instances", mutable, clone);
		
		assertEquals("should be same class", mutable.getClass(), clone.getClass());
	}

}
