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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.op.AbstractRandomAndSpecialValueTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.test.ArithmeticResult;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for the different scale methods of a Decimal such as
 * {@link Decimal#scale(int)} etc.
 */
@RunWith(Parameterized.class)
public class ScaleTest extends AbstractRandomAndSpecialValueTest {

	private final int targetScale;

	/**
	 * Constructor for parameterized test.
	 *
	 * @param sourceScale
	 *            the source scale metrics
	 * @param rm
	 *            the rounding mode to apply
	 * @param targetScale
	 *            the target scale
	 * @param arithmetic
	 *            the arithmetic object passed to the constructor
	 */
	public ScaleTest(ScaleMetrics sourceScale, RoundingMode rm, int targetScale, DecimalArithmetic arithmetic) {
		super(arithmetic);
		this.targetScale = targetScale;
	}

	@Parameters(name = "{index}: {0}, {1}, targetScale={2}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final RoundingMode rm : TestSettings.UNCHECKED_ROUNDING_MODES) {
				for (int targetScale = 0; targetScale < Scales.MAX_SCALE; targetScale++) {
					final DecimalArithmetic arith = s.getArithmetic(rm);
					data.add(new Object[] { s, rm, targetScale, arith });
				}
			}
		}
		return data;
	}

	@Override
	protected int getRandomTestCount() {
		switch (TestSettings.TEST_CASES) {
		case ALL:
			return 2000;
		case LARGE:
			return 2000;
		case STANDARD:
			return 1000;
		case SMALL:
			return 1000;
		case TINY:
			return 100;
		default:
			throw new RuntimeException("unsupported: " + TestSettings.TEST_CASES);
		}
	}

	@Override
	protected String operation() {
		return "scale";
	}

	private BigDecimal expectedResult(BigDecimal a, int targetScale) {
		if (Scales.MIN_SCALE <= targetScale & targetScale <= Scales.MAX_SCALE) {
			final BigDecimal result = a.setScale(targetScale, getRoundingMode());
			if (result.unscaledValue().bitLength() > 63) {
				throw new ArithmeticException("Overflow: " + result);
			}
			return result;
		}
		throw new IllegalArgumentException("Illegal target scale: " + targetScale);
	}

	private <S extends ScaleMetrics> Decimal<?> actualResult(Decimal<S> a, int targetScale) {
		final ScaleMetrics metrics = Scales.getScaleMetrics(targetScale);
		final RoundingMode mode = getRoundingMode();
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			return RND.nextBoolean() ? a.scale(targetScale) : a.scale(metrics);
		}
		return RND.nextBoolean() ? a.scale(targetScale, mode) : a.scale(metrics, mode);
	}

	@Override
	protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index) {
		final Decimal<S> decimalOperand = randomDecimal(scaleMetrics);
		runTest(scaleMetrics, "[" + index + "]", decimalOperand, targetScale);
	}

	@Override
	protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics) {
		final long[] specialValues = getSpecialValues(scaleMetrics);
		for (int i = 0; i < specialValues.length; i++) {
			runTest(scaleMetrics, "[" + i + "]", newDecimal(scaleMetrics, specialValues[i]), targetScale);
		}
	}

	protected <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, Decimal<S> dOperandA, int targetScale) {
		final String messagePrefix = getClass().getSimpleName() + name + ": " + dOperandA + " " + operation() + " "
				+ targetScale;

		final BigDecimal bdOperandA = toBigDecimal(dOperandA);
		final DecimalArithmetic resultArithmetic = arithmetic.deriveArithmetic(targetScale);

		// expected
		ArithmeticResult<Long> expected;
		try {
			expected = ArithmeticResult.forResult(resultArithmetic, expectedResult(bdOperandA, targetScale));
		} catch (IllegalArgumentException e) {
			expected = ArithmeticResult.forException(e);
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		}

		// actual
		ArithmeticResult<Long> actual;
		try {
			actual = ArithmeticResult.forResult(actualResult(dOperandA, targetScale));
		} catch (IllegalArgumentException e) {
			actual = ArithmeticResult.forException(e);
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		}

		// assert
		actual.assertEquivalentTo(expected, messagePrefix);
	}
}
