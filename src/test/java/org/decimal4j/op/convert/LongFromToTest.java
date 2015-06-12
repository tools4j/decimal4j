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
package org.decimal4j.op.convert;

import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.op.AbstractFromToTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link DecimalFactory#valueOf(long)}, {@link MutableDecimal#set(long)} 
 * and indirectly also the static {@code valueOf(..)} method of the immutable Decimal.
 */
@RunWith(Parameterized.class)
public class LongFromToTest extends AbstractFromToTest<Long> {

	public LongFromToTest(ScaleMetrics s, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Override
	protected Long randomValue(ScaleMetrics scaleMetrics) {
		return randomLongOrInt();
	}

	@Parameters(name = "{index}: {0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] {s, s.getRoundingDownArithmetic()});
		}
		return data;
	}

	@Override
	protected Long[] specialValues(ScaleMetrics scaleMetrics) {
		final long[] specials = TestSettings.TEST_CASES.getSpecialValuesFor(scaleMetrics);
		final Long[] result = new Long[specials.length];
		for (int i = 0; i < specials.length; i++) {
			result[i] = specials[i];
		}
		return result;
	}

	@Override
	protected <S extends ScaleMetrics> Long expectedResult(S scaleMetrics, Long value) {
		if (scaleMetrics.isValidIntegerValue(value)) {
			return value;
		}
		throw new ArithmeticException("overflow for " + scaleMetrics + " with value " + value);
	}
	@Override
	protected <S extends ScaleMetrics> Long actualResult(DecimalFactory<S> factory, Long value) {
		final Decimal<S> decimal = RND.nextBoolean() ? factory.valueOf(value) : factory.newMutable().set(value);
		return RND.nextBoolean() ? decimal.longValue() : decimal.longValueExact();
	}
	
}
