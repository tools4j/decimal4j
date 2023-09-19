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
import org.decimal4j.op.util.FloatAndDoubleUtil;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.ArithmeticResult;

/**
 * Base class for tests comparing the result of some binary operation of the
 * {@link Decimal} with a Decimal argument and a double argument. The expected 
 * result is produced by the equivalent operation of the {@link BigDecimal}. The 
 * test operand values are created based on random long values.
 */
abstract public class AbstractDecimalDoubleValueToDecimalTest extends AbstractRandomAndSpecialValueTest {

	/**
	 * Constructor with arithemtics determining scale, rounding mode and
	 * overflow policy.
	 * 
	 * @param arithmetic
	 *            the arithmetic determining scale, rounding mode and overlfow
	 *            policy
	 */
	public AbstractDecimalDoubleValueToDecimalTest(DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	abstract protected BigDecimal expectedResult(BigDecimal a, double b);

	abstract protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, double b);
	
	protected double randomDoubleOperand() {
		return FloatAndDoubleUtil.randomDoubleOperand(RND);
	}

	protected double[] getSpecialDoubleOperands() {
		return FloatAndDoubleUtil.specialDoubleOperands(getScaleMetrics());
	}
	
	@Override
	protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index) {
		runTest(scaleMetrics, "[" + index + "]", randomDecimal(scaleMetrics), randomDoubleOperand());
	}

	@Override
	protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics) {
		final long[] specialValues = getSpecialValues(scaleMetrics);
		final double[] specialDoubleOperands = getSpecialDoubleOperands();
		for (int i = 0; i < specialValues.length; i++) {
			for (int j = 0; j < specialDoubleOperands.length; j++) {
				runTest(scaleMetrics, "[" + i + ", " + j + "]", newDecimal(scaleMetrics, specialValues[i]), specialDoubleOperands[j]);
			}
		}
	}

	protected <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, Decimal<S> dOperandA, double b) {
		final String messagePrefix = getClass().getSimpleName() + name + ": " + dOperandA + " " + operation() + " " + b;
		
		final BigDecimal bdOperandA = toBigDecimal(dOperandA);

		//expected
		ArithmeticResult<Long> expected;
		try {
			expected = ArithmeticResult.forResult(arithmetic, expectedResult(bdOperandA, b));
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		} catch (IllegalArgumentException e) {
			expected = ArithmeticResult.forException(e);
		}

		//actual
		ArithmeticResult<Long> actual;
		try {
			actual = ArithmeticResult.forResult(actualResult(dOperandA, b));
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		} catch (IllegalArgumentException e) {
			actual = ArithmeticResult.forException(e);
		}
		
		//assert
		actual.assertEquivalentTo(expected, messagePrefix);
	}
}
