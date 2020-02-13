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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#toBigDecimal()} and {@link Decimal#toBigDecimal(int, RoundingMode)} 
 */
@RunWith(Parameterized.class)
public class ToBigDecimalTest extends AbstractDecimalToAnyTest<BigDecimal> {

	private final Integer newScale;
	
	public ToBigDecimalTest(ScaleMetrics scaleMetrics, Integer newScale, RoundingMode rounding, DecimalArithmetic arithmetic) {
		super(arithmetic);
		this.newScale = newScale;
	}

	@Parameters(name = "{index}: scale={0}, newScale={1}, rounding={2}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] { s, null, RoundingMode.DOWN, s.getDefaultArithmetic() });
			for (int scale = -100; scale < 100; scale++) {
				for (final RoundingMode rounding : TestSettings.UNCHECKED_ROUNDING_MODES) {
					data.add(new Object[] { s, scale, rounding, s.getArithmetic(rounding) });
				}
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return "toBigDecimal";
	}
	
	@Override
	protected int getRandomTestCount() {
		return 100;
	}

	@Override
	protected BigDecimal expectedResult(BigDecimal operand) {
		if (newScale == null) {
			return operand;
		}
		return operand.setScale(newScale, getRoundingMode());
	}

	@Override
	protected <S extends ScaleMetrics> BigDecimal actualResult(Decimal<S> operand) {
		if (newScale == null) {
			return operand.toBigDecimal();
		}
		return operand.toBigDecimal(newScale, getRoundingMode());
	}
}
