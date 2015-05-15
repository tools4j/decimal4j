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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.op.AbstractDecimalDecimalToDecimalTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.OverflowMode;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#avg(Decimal)} and {@link Decimal#avg(Decimal, RoundingMode)}
 */
@RunWith(Parameterized.class)
public class AvgTest extends AbstractDecimalDecimalToDecimalTest {
	
	private static final BigDecimal TWO = BigDecimal.valueOf(2);
	
	public AvgTest(ScaleMetrics scaleMetrics, RoundingMode roundingMode, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final RoundingMode rm : TestSettings.UNCHECKED_ROUNDING_MODES) {
				data.add(new Object[] {s, rm, s.getArithmetic(rm)});
			}
		}
		return data;
	}
	
	@Override
	protected String operation() {
		return "avg";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b) {
		return a.add(b).divide(TWO, getRoundingMode());
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			return a.avg(b);
		}
		if (RND.nextBoolean()) {
			return a.avg(b, getRoundingMode());
		}
		//also test checked arithmetic otherwise this is not covered
		final DecimalArithmetic checkedAith = a.getScaleMetrics().getArithmetic(OverflowMode.CHECKED.getTruncationPolicyFor(getRoundingMode()));
		return newDecimal(a.getScaleMetrics(), checkedAith.avg(a.unscaledValue(), b.unscaledValue()));
	}
}
