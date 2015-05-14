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
package org.decimal4j.op;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scale0f;
import org.decimal4j.scale.Scale17f;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.scale.Scale6f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

public class DivideProblemTest extends DivideTest {

	public DivideProblemTest(ScaleMetrics scaleMetrics, TruncationPolicy truncationPolicy, DecimalArithmetic arithmetic) {
		super(scaleMetrics, truncationPolicy, arithmetic);
	}
	
	@Parameters(name = "{index}: scale={0}, rounding={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();

		ScaleMetrics s;
		TruncationPolicy tp;
		
		s = Scale6f.INSTANCE;
		tp = DecimalRounding.DOWN.getCheckedTruncationPolicy();
		data.add(new Object[] {s, tp, s.getArithmetic(tp)});
		
		s = Scale0f.INSTANCE;
		tp = DecimalRounding.HALF_EVEN.getUncheckedTruncationPolicy();
		data.add(new Object[] {s, tp, s.getArithmetic(tp)});
		
		s = Scale6f.INSTANCE;
		tp = DecimalRounding.HALF_EVEN.getUncheckedTruncationPolicy();
		data.add(new Object[] {s, tp, s.getArithmetic(tp)});

		s = Scale6f.INSTANCE;
		tp = DecimalRounding.UNNECESSARY.getUncheckedTruncationPolicy();
		data.add(new Object[] {s, tp, s.getArithmetic(tp)});

		s = Scale17f.INSTANCE;
		tp = DecimalRounding.DOWN.getUncheckedTruncationPolicy();
		data.add(new Object[] {s, tp, s.getArithmetic(tp)});

		return data;
	}
	
	@Test
	public void runProblemTest0() {
		if (getScale() == 6 && !isUnchecked()) {
			final Decimal<Scale6f> dOpA = newDecimal(Scale6f.INSTANCE, 345);
			final Decimal<Scale6f> dOpB = newDecimal(Scale6f.INSTANCE, 0);
			runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest1() {
		if (getScale() == 0 && isUnchecked() && getRoundingMode() == RoundingMode.HALF_EVEN) {
			final Decimal<Scale0f> dOpA = newDecimal(Scale0f.INSTANCE, Long.MIN_VALUE + 1);
			final Decimal<Scale0f> dOpB = newDecimal(Scale0f.INSTANCE, Long.MIN_VALUE);
			runTest(Scale0f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest2() {
		if (getScale() == 0 && isUnchecked() && getRoundingMode() == RoundingMode.HALF_EVEN) {
			final Decimal<Scale0f> dOpA = newDecimal(Scale0f.INSTANCE, Long.MIN_VALUE);
			final Decimal<Scale0f> dOpB = newDecimal(Scale0f.INSTANCE, -Scale18f.INSTANCE.getScaleFactor());
			runTest(Scale0f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest3() {
		if (getScale() == 6 && isUnchecked() && getRoundingMode() == RoundingMode.HALF_EVEN) {
			final Decimal<Scale6f> dOpA = newDecimal(Scale6f.INSTANCE, Long.MIN_VALUE);
			final Decimal<Scale6f> dOpB = newDecimal(Scale6f.INSTANCE, -10000000000000000L);
			runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest4() {
		if (getScale() == 6 && isUnchecked() && getRoundingMode() == RoundingMode.HALF_EVEN) {
			final Decimal<Scale6f> dOpA = newDecimal(Scale6f.INSTANCE, Long.MIN_VALUE);
			final Decimal<Scale6f> dOpB = newDecimal(Scale6f.INSTANCE, -4611686018427387905L);
			runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest5() {
		if (getScale() == 17 && isUnchecked()) {
			final Decimal<Scale17f> dOpA = newDecimal(Scale17f.INSTANCE, Scale17f.INSTANCE.getScaleFactor());
			final Decimal<Scale17f> dOpB = newDecimal(Scale17f.INSTANCE, -92233720368547L);
			runTest(Scale17f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest6() {
		if (getScale() == 17 && isUnchecked()) {
			final Decimal<Scale17f> dOpA = newDecimal(Scale17f.INSTANCE, Scale17f.INSTANCE.getScaleFactor());
			final Decimal<Scale17f> dOpB = newDecimal(Scale17f.INSTANCE, Integer.MIN_VALUE * 1000L);
			runTest(Scale17f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest7() {
		if (getScale() == 6 && isUnchecked() && getRoundingMode() == RoundingMode.UNNECESSARY) {
			final Decimal<Scale6f> dOpA = newDecimal(Scale6f.INSTANCE, 99999999000000L);
			final Decimal<Scale6f> dOpB = newDecimal(Scale6f.INSTANCE, 5);
			runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB);
		}
	}

}
