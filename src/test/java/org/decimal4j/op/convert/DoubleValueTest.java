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
package org.decimal4j.op.convert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.factory.Factories;
import org.decimal4j.op.AbstractDecimalToAnyTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.OverflowMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#doubleValue()}
 */
@RunWith(Parameterized.class)
public class DoubleValueTest extends AbstractDecimalToAnyTest<Double> {

	public DoubleValueTest(ScaleMetrics scaleMetrics, RoundingMode rounding, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: {0} {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final RoundingMode rm : TestSettings.UNCHECKED_ROUNDING_MODES) {
				data.add(new Object[] { s, rm, s.getArithmetic(rm) });
			}
			if (!TestSettings.UNCHECKED_ROUNDING_MODES.contains(RoundingMode.HALF_EVEN)) {
				data.add(new Object[] { s, RoundingMode.HALF_EVEN, s.getArithmetic(RoundingMode.HALF_EVEN) });
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return "doubleValue";
	}
	
	@Test
	public void scaleFactorPlusUlpTest() {
		runTest(getScaleMetrics(), "scaleFactorPlusUlpTest", Factories.getDecimalFactory(getScaleMetrics()).valueOfUnscaled(1+getScaleMetrics().getScaleFactor()));
	}

	@Override
	protected Double expectedResult(BigDecimal operand) {
		final double dval = operand.doubleValue();
		if (getRoundingMode() == RoundingMode.HALF_EVEN) {
			return dval;
		}
		final BigDecimal halfEven = new BigDecimal(dval);
		final int cmp = halfEven.compareTo(operand);
		if (cmp == 0) {
			return dval;
		}
		final double ceil;
		final double floor;
		if (cmp > 0) {
			ceil = dval;
			floor = Math.nextAfter(dval, Double.NEGATIVE_INFINITY);
		} else {
			floor = dval;
			ceil = Math.nextAfter(dval, Double.POSITIVE_INFINITY);
		}
		switch (getRoundingMode()) {
		case FLOOR:
			return floor;
		case CEILING:
			return ceil;
		case DOWN:
			return operand.signum() >= 0 ? floor : ceil;
		case UP:
			return operand.signum() >= 0 ? ceil : floor;
		case UNNECESSARY:
			throw new ArithmeticException("Rounding necessary: " + operand);
		case HALF_EVEN:
			throw new IllegalArgumentException("Unsupported rounding mode: " + getRoundingMode());
		default://HALF_UP + HALF_DOWN 
			break;
		}
		//HALF_DOWN/HALF_UP
		final BigDecimal upperHalf = new BigDecimal(ceil).subtract(operand).abs();
		final BigDecimal lowerHalf = operand.subtract(new BigDecimal(floor)).abs();
		final int halfCmp = upperHalf.compareTo(lowerHalf);
		if (halfCmp != 0) {
			return halfCmp < 0 ? ceil : floor;
		}
		// exactly HALF
		if (getRoundingMode() == RoundingMode.HALF_UP) {
			return operand.signum() > 0 ? ceil : floor;
		}
		//HALF_DOWN: opposite
		return operand.signum() > 0 ? floor : ceil;
	}

	@Override
	protected <S extends ScaleMetrics> Double actualResult(Decimal<S> operand) {
		if (getRoundingMode() == RoundingMode.HALF_EVEN && RND.nextBoolean()) {
			return operand.doubleValue();
		}
		if (RND.nextBoolean()) {
			return operand.doubleValue(getRoundingMode());
		}
		//use arithmetic
		if (RND.nextBoolean()) {
			return arithmetic.toDouble(operand.unscaledValue());
		} else {
			return arithmetic.deriveArithmetic(OverflowMode.CHECKED).toDouble(operand.unscaledValue());
		}
	}
	
}
