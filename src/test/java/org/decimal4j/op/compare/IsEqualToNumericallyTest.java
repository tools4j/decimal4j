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
package org.decimal4j.op.compare;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.op.AbstractDecimalUnknownDecimalToAnyTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#isEqualToNumerically(Decimal)}.
 */
@RunWith(Parameterized.class)
public class IsEqualToNumericallyTest extends AbstractDecimalUnknownDecimalToAnyTest<Boolean> {

	public IsEqualToNumericallyTest(ScaleMetrics scale, int unknownDecimalScale, DecimalArithmetic arithmetic) {
		super(arithmetic, unknownDecimalScale);
	}

	@Parameters(name = "{index}: {0}, scale={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final ScaleMetrics otherScale : TestSettings.SCALES) {
				data.add(new Object[] {s, otherScale.getScale(), s.getDefaultArithmetic()});
			}
		}
		return data;
	}

	protected long randomSecondUnscaled(long firstUnscaled) {
		if (RND.nextBoolean()) {
			return arithmetic.multiplyByPowerOf10(firstUnscaled, unknownDecimalScale - getScale());
		}
		return nextLongOrInt();
	}

	@Override
	protected String operation() {
		return "isEqualToNumerically";
	}

	@Override
	protected Boolean expectedResult(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) == 0;
	}

	@Override
	protected <S extends ScaleMetrics> Boolean actualResult(Decimal<S> a, Decimal<?> b) {
		return a.isEqualToNumerically(b);
//		return a.equals(b);//FAILS
	}

}
