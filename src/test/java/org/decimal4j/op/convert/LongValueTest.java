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
package org.decimal4j.op.convert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.op.AbstractDecimalToAnyTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.OverflowMode;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#longValue()}, {@link Decimal#longValueExact()}
 * and {@link Decimal#longValue(RoundingMode)}.
 */
@RunWith(Parameterized.class)
public class LongValueTest extends AbstractDecimalToAnyTest<Long> {
	
	private final boolean exact;

	public LongValueTest(ScaleMetrics scaleMetrics, RoundingMode roundingMode, boolean exact, DecimalArithmetic arithmetic) {
		super(arithmetic);
		this.exact = exact;
	}

	@Parameters(name = "{index}: {0}, {1}, exact={2}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] {s, RoundingMode.DOWN, true, s.getDefaultArithmetic()});
			for (final RoundingMode mode : TestSettings.UNCHECKED_ROUNDING_MODES) {
				data.add(new Object[] {s, mode, false, s.getArithmetic(mode)});
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return exact ? "longValueExact" : "longValue";
	}
	
	@Override
	protected Long expectedResult(BigDecimal operand) {
		if (exact) {
			return operand.longValueExact();
		}
		if (isRoundingDown() && RND.nextBoolean()) {
			return operand.longValue();
		}
		return operand.setScale(0, getRoundingMode()).longValue();
	}
	
	@Override
	protected <S extends ScaleMetrics> Long actualResult(Decimal<S> operand) {
		if (exact) {
			return operand.longValueExact();
		}
		if (isRoundingDown() && RND.nextBoolean()) {
			return operand.longValue();
		}
		if (RND.nextBoolean()) {
			return operand.longValue(getRoundingMode());
		}
		//use arithmetic
		if (RND.nextBoolean()) {
			return arithmetic.toLong(operand.unscaledValue());
		} else {
			return arithmetic.deriveArithmetic(OverflowMode.CHECKED).toLong(operand.unscaledValue());
		}
	}
}
