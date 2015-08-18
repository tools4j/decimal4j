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

import java.io.IOException;
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
 * Unit test for {@link Decimal#abs()}
 */
@RunWith(Parameterized.class)
public class ToStringTest extends AbstractDecimalToAnyTest<String> {
	
	public ToStringTest(ScaleMetrics scaleMetrics, RoundingMode roundingMode, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: scale={0}, rounding={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] {s, RoundingMode.DOWN, s.getArithmetic(RoundingMode.DOWN)});
		}
		return data;
	}

	@Override
	protected String operation() {
		return "toString";
	}
	
	@Override
	protected String expectedResult(BigDecimal operand) {
		return operand.toPlainString();
	}
	
	private static final String STRING = "BLABLABLKJSLDFJLKJOI_)$(@U)DKSLDFLKJSLKXCMFREWOKLRJT";
	@Override
	protected <S extends ScaleMetrics> String actualResult(Decimal<S> operand) {
		if (RND.nextBoolean()) {
			return operand.toString();
		}
		//use appendable version
		try {
			final StringBuilder sb = new StringBuilder();
			if (RND.nextBoolean()) {
				arithmetic.toString(operand.unscaledValue(), sb);
				return sb.toString();
			}
			//use appendable version with some existing string
			final String prefix = STRING.substring(0, RND.nextInt(STRING.length()));
			sb.append(prefix);
			arithmetic.toString(operand.unscaledValue(), sb);
			return sb.substring(prefix.length());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
