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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.test.ArithmeticResult;

/**
 * Base class for tests asserting the result of some unary operation of the
 * {@link Decimal} with a wildcard {@code Decimal<?>} argument. The expected
 * result is produced by the equivalent operation of the {@link BigDecimal}.
 */
abstract public class AbstractUnknownDecimalToDecimalTest extends AbstractRandomAndSpecialValueTest {

	/**
	 * Constructor with arithemtics determining scale, rounding mode and
	 * overflow policy.
	 * 
	 * @param arithmetic
	 *            the arithmetic determining scale, rounding mode and overlfow
	 *            policy
	 */
	public AbstractUnknownDecimalToDecimalTest(DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	abstract protected BigDecimal expectedResult(Decimal<?> operand);

	abstract protected <S extends ScaleMetrics> Decimal<S> actualResult(S scaleMetrics, Decimal<?> operand);

	protected Decimal<?> randomDecimalOperand() {
		final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(RND.nextInt(1 + Scales.MAX_SCALE));
		return newDecimal(scaleMetrics, nextLongOrInt());
	}

	protected Decimal<?>[] getSpecialDecimalOperands() {
		final long[] specials = getSpecialValues(getScaleMetrics());
		final Set<Decimal<?>> set = new HashSet<Decimal<?>>();
		for (int i = 0; i < specials.length; i++) {
			for (final ScaleMetrics scaleMetrics : Scales.VALUES) {
				set.add(newDecimal(scaleMetrics, specials[i]));
			}
		}
		return set.toArray(new Decimal<?>[set.size()]);
	}

	@Override
	protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index) {
		runTest(scaleMetrics, "[" + index + "]", randomDecimalOperand());
	}

	@Override
	protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics) {
		final Decimal<?>[] specialOperands = getSpecialDecimalOperands();
		for (int i = 0; i < specialOperands.length; i++) {
			runTest(scaleMetrics, "[" + i + "]", specialOperands[i]);
		}
	}

	protected <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, Decimal<?> operand) {

		// expected
		ArithmeticResult<Long> expected;
		try {
			expected = ArithmeticResult.forResult(arithmetic, expectedResult(operand));
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		} catch (IllegalArgumentException e) {
			expected = ArithmeticResult.forException(e);
		}

		// actual
		ArithmeticResult<Long> actual;
		try {
			actual = ArithmeticResult.forResult(actualResult(scaleMetrics, operand));
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		} catch (IllegalArgumentException e) {
			actual = ArithmeticResult.forException(e);
		}

		// assert
		actual.assertEquivalentTo(expected, getClass().getSimpleName() + name + ": " + operation() + " " + operand);
	}
}
