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
package org.decimal4j.op.arith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.op.AbstractDecimalDecimalToDecimalTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.OverflowMode;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#subtract(Decimal)} and {@link Decimal#subtract(Decimal, OverflowMode)}.
 */
@RunWith(Parameterized.class)
public class SubtractTest extends AbstractDecimalDecimalToDecimalTest {
	
	public SubtractTest(ScaleMetrics scaleMetrics, OverflowMode overflowMode, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] {s, OverflowMode.UNCHECKED, s.getRoundingDownArithmetic()});
			data.add(new Object[] {s, OverflowMode.CHECKED, s.getArithmetic(CheckedRounding.DOWN)});
		}
		return data;
	}

	@Override
	protected String operation() {
		return "-";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b) {
		return a.subtract(b);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		if (isUnchecked() && RND.nextBoolean()) {
			return a.subtract(b);
		}
		return a.subtract(b, getOverflowMode());
	}
}
