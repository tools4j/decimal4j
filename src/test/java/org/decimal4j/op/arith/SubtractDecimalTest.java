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
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.arithmetic.JDKSupport;
import org.decimal4j.op.AbstractDecimalUnknownDecimalToDecimalTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#subtract(Decimal)} and {@link Decimal#subtract(Decimal, OverflowMode)}.
 */
@RunWith(Parameterized.class)
public class SubtractDecimalTest extends AbstractDecimalUnknownDecimalToDecimalTest {
	
	public SubtractDecimalTest(ScaleMetrics scaleMetrics, int scale, TruncationPolicy tp, DecimalArithmetic arithmetic) {
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
		return "-";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b) {
		final BigDecimal bScaled = b.setScale(getScale(), getRoundingMode());
		if (!isUnchecked()) {
			JDKSupport.bigIntegerToLongValueExact(bScaled.unscaledValue());
		}
		return a.subtract(bScaled);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<?> b) {
		if (isUnchecked() && RND.nextBoolean()) {
			return a.subtract(b, getRoundingMode());
		}
		return a.subtract(b, getTruncationPolicy());
	}
}
