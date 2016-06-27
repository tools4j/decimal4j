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
package org.decimal4j.op;

import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.ArithmeticResult;

/**
 * Base class for tests comparing the result of some binary operation of the
 * {@link Decimal} with another {@code Decimal<?>} of unknown scale and a result
 * of the type {@code <R>}.
 * 
 * @param <R> the result type of the operation
 */
abstract public class AbstractDecimalUnknownDecimalToAnyTest<R> extends AbstractRandomAndSpecialValueTest {

	protected final int unknownDecimalScale;

	/**
	 * Constructor with arithemtics determining scale, rounding mode and
	 * overflow policy.
	 * 
	 * @param arithmetic
	 *            the arithmetic determining scale, rounding mode and overlfow
	 *            policy
	 * @param unknownDecimalScale
	 *            the scale of the second Decimal argument
	 */
	public AbstractDecimalUnknownDecimalToAnyTest(DecimalArithmetic arithmetic, int unknownDecimalScale) {
		super(arithmetic);
		this.unknownDecimalScale = unknownDecimalScale;
	}

	abstract protected R expectedResult(BigDecimal a, BigDecimal b);
	abstract protected <S extends ScaleMetrics> R actualResult(Decimal<S> a, Decimal<?> b);

	@Override
	protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index) {
		final Decimal<S> dOpA = randomDecimal(scaleMetrics);
		final Decimal<?> dOpB = Factories.getDecimalFactory(unknownDecimalScale).valueOfUnscaled(randomSecondUnscaled(dOpA.unscaledValue()));
		runTest(scaleMetrics, "[" + index + "]", dOpA, dOpB);
	}

	protected long randomSecondUnscaled(long firstUnscaled) {
		return nextLongOrInt();
	}

	@Override
	protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics) {
		final long[] specialValues = getSpecialValues(scaleMetrics);
		for (int i = 0; i < specialValues.length; i++) {
			for (int j = 0; j < specialValues.length; j++) {
				final Decimal<S> dOpA = newDecimal(scaleMetrics, specialValues[i]);
				final Decimal<?> dOpB = Factories.getDecimalFactory(unknownDecimalScale).valueOfUnscaled(specialValues[j]);
				runTest(scaleMetrics, "[" + i + ", " + j + "]", dOpA, dOpB);
			}
		}
	}

	protected <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, Decimal<S> a, Decimal<?> b) {
		final String messagePrefix = getClass().getSimpleName() + name + ": " + a + " " + operation() + " " + b;

		final BigDecimal bdA = toBigDecimal(a);
		final BigDecimal bdB = toBigDecimal(b);

		//expected
		ArithmeticResult<R> expected;
		try {
			final R exp = expectedResult(bdA, bdB);
			expected = ArithmeticResult.forResult(exp.toString(), exp);
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		}

		//actual
		ArithmeticResult<R> actual;
		try {
			final R act = actualResult(a, b);
			actual = ArithmeticResult.forResult(act.toString(), act);
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		}
		
		//assert
		actual.assertEquivalentTo(expected, messagePrefix);
	}
}
