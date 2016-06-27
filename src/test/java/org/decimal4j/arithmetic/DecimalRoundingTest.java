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
package org.decimal4j.arithmetic;

import static org.decimal4j.truncate.DecimalRounding.CEILING;
import static org.decimal4j.truncate.DecimalRounding.DOWN;
import static org.decimal4j.truncate.DecimalRounding.FLOOR;
import static org.decimal4j.truncate.DecimalRounding.HALF_DOWN;
import static org.decimal4j.truncate.DecimalRounding.HALF_EVEN;
import static org.decimal4j.truncate.DecimalRounding.HALF_UP;
import static org.decimal4j.truncate.DecimalRounding.UNNECESSARY;
import static org.decimal4j.truncate.DecimalRounding.UP;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.decimal4j.truncate.DecimalRounding;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link DecimalRounding}
 */
@RunWith(Parameterized.class)
public class DecimalRoundingTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	@Parameter(value=0)
	public String name;
	
	@Parameter(value=1)
	public DecimalRounding rounding;
	
	@Parameter(value=2)
	public double input;

	@Parameter(value=3)
	public double expected;

	/**
	 * InputNumber	UP	DOWN	CEILING	FLOOR	HALF_UP	HALF_DOWN	HALF_EVEN	UNNECESSARY
	 */
	private static final double[][] DATA = new double[][] {
		//first the values from RoundingMode javadoc
		{5.5,	6,	5,	6,	5,	6,	5,	6,	Double.NaN},
		{2.5,	3,	2,	3,	2,	3,	2,	2,	Double.NaN},
		{1.6,	2,	1,	2,	1,	2,	2,	2,	Double.NaN},
		{1.1,	2,	1,	2,	1,	1,	1,	1,	Double.NaN},
		{1.0,	1,	1,	1,	1,	1,	1,	1,	1},
		{-1.0,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1},
		{-1.1,	-2,	-1,	-1,	-2,	-1,	-1,	-1,	Double.NaN},
		{-1.6,	-2,	-1,	-1,	-2,	-2,	-2,	-2,	Double.NaN},
		{-2.5,	-3,	-2,	-2,	-3,	-3,	-2,	-2,	Double.NaN},
		{-5.5,	-6,	-5,	-5,	-6,	-6,	-5,	-6,	Double.NaN},
		//now some additional values with more after decimal digits
		{5.51,	6,	5,	6,	5,	6,	6,	6,	Double.NaN},
		{2.51,	3,	2,	3,	2,	3,	3,	3,	Double.NaN},
		{1.61,	2,	1,	2,	1,	2,	2,	2,	Double.NaN},
		{1.11,	2,	1,	2,	1,	1,	1,	1,	Double.NaN},
		{1.01,	2,	1,	2,	1,	1,	1,	1,	Double.NaN},
		{-1.01,	-2,	-1,	-1,	-2,	-1,	-1,	-1,	Double.NaN},
		{-1.11,	-2,	-1,	-1,	-2,	-1,	-1,	-1,	Double.NaN},
		{-1.61,	-2,	-1,	-1,	-2,	-2,	-2,	-2,	Double.NaN},
		{-2.51,	-3,	-2,	-2,	-3,	-3,	-3,	-3,	Double.NaN},
		{-5.51,	-6,	-5,	-5,	-6,	-6,	-6,	-6,	Double.NaN},
		//also interesting the special case with a leading zero
		{0.61,	1,	0,	1,	0,	1,	1,	1,	Double.NaN},
		{0.6,	1,	0,	1,	0,	1,	1,	1,	Double.NaN},
		{0.51,	1,	0,	1,	0,	1,	1,	1,	Double.NaN},
		{0.5,	1,	0,	1,	0,	1,	0,	0,	Double.NaN},
		{0.1,	1,	0,	1,	0,	0,	0,	0,	Double.NaN},
		{0.01,	1,	0,	1,	0,	0,	0,	0,	Double.NaN},
		{0.0,	0,	0,	0,	0,	0,	0,	0,	0},
		{-0.0,	0,	0,	0,	0,	0,	0,	0,	0},
		{-0.01,	-1,	0,	0,	-1,	0,	0,	0,	Double.NaN},
		{-0.1,	-1,	0,	0,	-1,	0,	0,	0,	Double.NaN},
		{-0.5,	-1,	0,	0,	-1,	-1,	0,	0,	Double.NaN},
		{-0.51,	-1,	0,	0,	-1,	-1,	-1,	-1,	Double.NaN},
		{-0.6,	-1,	0,	0,	-1,	-1,	-1,	-1,	Double.NaN},
		{-0.61,	-1,	0,	0,	-1,	-1,	-1,	-1,	Double.NaN},
	};
	private static final DecimalRounding[] DATA_COLS = new DecimalRounding[] {
		null, UP, DOWN, CEILING, FLOOR, HALF_UP, HALF_DOWN, HALF_EVEN, UNNECESSARY
	};
	
	@Parameters(name = "{0}")
	public static List<Object[]> getData() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final DecimalRounding rounding : DecimalRounding.values()) {
			data.addAll(getDataFor(rounding));
		}
		return data;
	}

	private static List<Object[]> getDataFor(DecimalRounding rounding) {
		final int expectedCol = getDataColumnFor(rounding);
		final List<Object[]> data = new ArrayList<Object[]>(DATA.length);
		for (double[] row : DATA) {
			data.add(new Object[] {
					row[0] + " " + rounding,
					rounding,
					row[0],
					row[expectedCol]
			});
		}
		return data;
	}

	private static int getDataColumnFor(DecimalRounding rounding) {
		for (int i = 0; i < DATA_COLS.length; i++) {
			if (DATA_COLS[i] == rounding) return i;
		}
		throw new IllegalArgumentException("No data column defined for " + rounding);
	}

	@Test
	public void shouldCalculateRoundingIncrement() {
		final long inputTimes100 = Math.round(input * 100);
		final long truncated = inputTimes100 / 100;
		final int reminder = (int)(inputTimes100 - truncated * 100);
		final int absReminder = Math.abs(reminder);
		final int firstTruncDigit = absReminder / 10;
		final boolean anyAfterFirstTruncDigit = 0 == absReminder - 10 * (absReminder / 10);
		if (Double.isNaN(expected)) {
			thrown.expect(ArithmeticException.class);
			thrown.expectMessage("necessary");
			Rounding.calculateRoundingIncrement(rounding, Long.signum(reminder), truncated, firstTruncDigit, anyAfterFirstTruncDigit);
		} else {
			final int increment = Rounding.calculateRoundingIncrement(rounding, Long.signum(reminder), truncated, firstTruncDigit, anyAfterFirstTruncDigit);
			final double actual = ((long)input) + increment;
			assertEquals("wrong rounding for " + input + " " + rounding, expected, actual, 0);
		}
	}
	
}
