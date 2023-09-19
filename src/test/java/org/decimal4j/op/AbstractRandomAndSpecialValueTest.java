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
package org.decimal4j.op;

import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.AbstractDecimalTest;
import org.decimal4j.test.TestSettings;
import org.junit.Test;

/**
 * Base class for {@link Decimal} unit tests performing two types of tests, one with
 * random values and another one with special values.
 *  
 * @see #runRandomTest()
 * @see #runSpecialValueTest()
 */
abstract public class AbstractRandomAndSpecialValueTest extends AbstractDecimalTest {

	/**
	 * Constructor with arithmetic determining scale, rounding mode and
	 * overflow mode.
	 * 
	 * @param arithmetic
	 *            the arithmetic determining scale, rounding mode and overflow
	 *            mode
	 */
	public AbstractRandomAndSpecialValueTest(DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	protected int getRandomTestCount() {
		return TestSettings.getRandomTestCount();
	}

	@Test
	public void runRandomTest() {
		final int n = getRandomTestCount();
		final ScaleMetrics scaleMetrics = arithmetic.getScaleMetrics();
		for (int i = 0; i < n; i++) {
			runRandomTest(scaleMetrics, i);
		}
	}

	@Test
	public void runSpecialValueTest() {
		final ScaleMetrics scaleMetrics = arithmetic.getScaleMetrics();
		runSpecialValueTest(scaleMetrics);
	}

	protected static BigDecimal toBigDecimal(Decimal<?> decimal) {
		return decimal.toBigDecimal();
	}

	/**
	 * Returns the operation string, such as "+", "-", "*", "/", "abs" etc.
	 * 
	 * @return the operation string used in exceptions and log statements
	 */
	abstract protected String operation();

	abstract protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index);

	abstract protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics);

}
