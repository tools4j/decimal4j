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
package org.decimal4j.op.arith;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.op.AbstractRandomAndSpecialValueTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.test.ArithmeticResult;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#multiplyExact(Decimal)}
 */
@RunWith(Parameterized.class)
public class MultiplyExactTest extends AbstractRandomAndSpecialValueTest {
	
	private final ScaleMetrics scaleMetrics2;
	
	public MultiplyExactTest(ScaleMetrics scaleMetrics1, ScaleMetrics scaleMetrics2, DecimalArithmetic arithmetic) {
		super(arithmetic);
		this.scaleMetrics2 = Objects.requireNonNull(scaleMetrics2, "scaleMetrics2 cannot be null");
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s1 : TestSettings.SCALES) {
			for (final ScaleMetrics s2 : TestSettings.SCALES) {
				data.add(new Object[] {s1, s2, s1.getDefaultCheckedArithmetic()});
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return "*";
	}

	@Override
	protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index) {
		final Decimal<S> dOpA = randomDecimal(scaleMetrics);
		final Decimal<?> dOpB = randomDecimal(scaleMetrics2);
		runTest(scaleMetrics, "[" + index + "]", dOpA, dOpB);
	}
	
	@Override
	protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics) {
		final long[] specialValues = getSpecialValues(scaleMetrics);
		for (int i = 0; i < specialValues.length; i++) {
			for (int j = 0; j < specialValues.length; j++) {
				final Decimal<S> dOpA = newDecimal(scaleMetrics, specialValues[i]);
				final Decimal<?> dOpB = newDecimal(scaleMetrics2, specialValues[j]);
				runTest(scaleMetrics, "[" + i + ", " + j + "]", dOpA, dOpB);
			}
		}
	}
	
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b) {
		return a.multiply(b);
	}

	protected <S extends ScaleMetrics> Decimal<?> actualResult(Decimal<S> a, Decimal<?> b) {
		return a.multiplyExact(b);
	}
	
	protected <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, Decimal<S> dOpA, Decimal<?> dOpB) {
		final int sumOfScales = dOpA.getScale() + dOpB.getScale();
		final BigDecimal bdOpA = toBigDecimal(dOpA);
		final BigDecimal bdOpB = toBigDecimal(dOpB);

		// expected
		ArithmeticResult<Long> expected;
		try {
			if (sumOfScales > Scales.MAX_SCALE) {
				throw new IllegalArgumentException("sum of scales exceeds max scale");
			}
			final BigDecimal exp = expectedResult(bdOpA, bdOpB);
			expected = ArithmeticResult.forResult(arithmetic.deriveArithmetic(sumOfScales), exp);
		} catch (IllegalArgumentException e) {
			expected = ArithmeticResult.forException(e);
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		}

		// actual
		Decimal<?> act = null;
		ArithmeticResult<Long> actual;
		try {
			act = actualResult(dOpA, dOpB);
			actual = ArithmeticResult.forResult(act);
		} catch (IllegalArgumentException e) {
			actual = ArithmeticResult.forException(e);
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		}

		// assert
		if (!actual.isException()) {
			assertEquals("result scale should be sum of scales", sumOfScales, act.getScale());
		}
		actual.assertEquivalentTo(expected, getClass().getSimpleName() + name + ": " + dOpA + " " + operation() + " "
				+ dOpB);
	}

}
