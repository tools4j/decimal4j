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
package org.decimal4j.op.arith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.op.AbstractDecimalUnknownDecimalToDecimalTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#divideBy(Decimal)} etc.
 */
@RunWith(Parameterized.class)
public class DivideByDecimalTest extends AbstractDecimalUnknownDecimalToDecimalTest {
	
	public DivideByDecimalTest(ScaleMetrics scaleMetrics, int scale, TruncationPolicy tp, DecimalArithmetic arithmetic) {
		super(arithmetic, scale);
	}

	@Parameters(name = "{index}: {0}, scale={1}, {2}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final ScaleMetrics otherScale : TestSettings.SCALES) {
				for (final TruncationPolicy tp : TruncationPolicy.VALUES) {
					data.add(new Object[] {s, otherScale.getScale(), tp, s.getArithmetic(tp)});
				}
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return "/";
	}
	
	@Test
	public void testProblem1() {
		final long a = -2147483648L;
		final long b = 100000000000001L;
		if (getScale() == 0 & unknownDecimalScale == 18) {
			final Decimal<?> da = newDecimal(getScaleMetrics(), a);
			final Decimal<?> db = newDecimal(Scales.getScaleMetrics(unknownDecimalScale), b);
			runTest(getScaleMetrics(), "testProblem1: " + da + "/" + db, newDecimal(getScaleMetrics(), a), db);
		}
	}
	
	@Test
	public void testProblem2() {
		final long a = -9223372036854775807L;
		final long b = -8000000000000000000L;
		if (getScale() == 18 & unknownDecimalScale == 18) {
			final Decimal<?> da = newDecimal(getScaleMetrics(), a);
			final Decimal<?> db = newDecimal(Scales.getScaleMetrics(unknownDecimalScale), b);
			runTest(getScaleMetrics(), "testProblem2: " + da + "/" + db, newDecimal(getScaleMetrics(), a), db);
		}
	}

	@Test
	public void testProblem3() {
		final long a =  1152921504606846975L;
		final long b = -8000000000000000000L;
		if (getScale() == 18 & unknownDecimalScale == 18) {
			final Decimal<?> da = newDecimal(getScaleMetrics(), a);
			final Decimal<?> db = newDecimal(Scales.getScaleMetrics(unknownDecimalScale), b);
			runTest(getScaleMetrics(), "testProblem3: " + da + "/" + db, newDecimal(getScaleMetrics(), a), db);
		}
	}

	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b) {
		return a.divide(b, mathContextLong128);//works
//		return a.divide(b, getScale(), getRoundingMode());//does not work (JDK bug?)
//		return a.divide(b, Math.max(getScale(), unknownDecimalScale), getRoundingMode());//works
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<?> b) {
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			return a.divideBy(b);
		}
		if (isUnchecked() && RND.nextBoolean()) {
			return a.divideBy(b, getRoundingMode());
		}
		return a.divideBy(b, getTruncationPolicy());
	}
}
