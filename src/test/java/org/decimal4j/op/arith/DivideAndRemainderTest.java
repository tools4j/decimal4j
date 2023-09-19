/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 decimal4j (tools4j), Marco Terzer
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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.op.AbstractDecimalDecimalToAnyTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.ArithmeticResult;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.OverflowMode;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#divideAndRemainder(Decimal)}
 */
@RunWith(Parameterized.class)
public class DivideAndRemainderTest extends AbstractDecimalDecimalToAnyTest<Object[]> {
	
	public DivideAndRemainderTest(ScaleMetrics scaleMetrics, OverflowMode overflowMode, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: {0} {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] {s, OverflowMode.UNCHECKED, s.getRoundingDownArithmetic()});
			data.add(new Object[] {s, OverflowMode.CHECKED, s.getDefaultCheckedArithmetic()});
		}
		return data;
	}

	@Override
	protected String operation() {
		return "divideAndRemainder";
	}
	
	@Override
	protected BigDecimal[] expectedResult(BigDecimal a, BigDecimal b) {
		final BigDecimal[] res = a.divideAndRemainder(b, mathContextLong64);
		res[0] = res[0].setScale(getScale(), RoundingMode.UNNECESSARY);
		res[1] = res[1].setScale(getScale(), RoundingMode.UNNECESSARY);
		return res;
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S>[] actualResult(Decimal<S> a, Decimal<S> b) {
		if (isUnchecked() && RND.nextBoolean()) {
			return a.divideAndRemainder(b);
		}
		return a.divideAndRemainder(b, getOverflowMode());
	}
	@Override
	protected <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, Decimal<S> dOpA, Decimal<S> dOpB) {
		final String messagePrefix0 = getClass().getSimpleName() + name + ": " + dOpA + " " + operation() + "[0] " + dOpB;
		final String messagePrefix1 = getClass().getSimpleName() + name + ": " + dOpA + " " + operation() + "[1] " + dOpB;
		
		final BigDecimal bdOpA = toBigDecimal(dOpA);
		final BigDecimal bdOpB = toBigDecimal(dOpB);

		//expected
		ArithmeticResult<Long> expected0;
		ArithmeticResult<Long> expected1;
		try {
			final BigDecimal[] exp = expectedResult(bdOpA, bdOpB);
			expected0 = ArithmeticResult.forResult(arithmetic, exp[0]);
			expected1 = ArithmeticResult.forResult(arithmetic, exp[1]);
		} catch (ArithmeticException e) {
			expected0 = ArithmeticResult.forException(e);
			expected1 = ArithmeticResult.forException(e);
		}

		//actual
		ArithmeticResult<Long> actual0;
		ArithmeticResult<Long> actual1;
		try {
			final Decimal<S>[] act = actualResult(dOpA, dOpB);
			actual0 = ArithmeticResult.forResult(act[0]);
			actual1 = ArithmeticResult.forResult(act[1]);
		} catch (ArithmeticException e) {
			actual0 = ArithmeticResult.forException(e);
			actual1 = ArithmeticResult.forException(e);
		}

		//assert
		actual0.assertEquivalentTo(expected0, messagePrefix0);
		actual1.assertEquivalentTo(expected1, messagePrefix1);
	}
}
