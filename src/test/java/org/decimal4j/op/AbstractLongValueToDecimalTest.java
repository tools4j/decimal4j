/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 decimal4j (tools4j), Marco Terzer
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
import org.decimal4j.test.ArithmeticResult;

/**
 * Base class for tests comparing the result of some unary operation of the
 * {@link Decimal} with long value argument.
 */
abstract public class AbstractLongValueToDecimalTest extends AbstractRandomAndSpecialValueTest {

	/**
	 * Constructor with arithmetic determining scale, rounding mode and
	 * overflow mode.
	 * 
	 * @param arithmetic
	 *            the arithmetic determining scale, rounding mode and overflow
	 *            mode
	 */
	public AbstractLongValueToDecimalTest(DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	abstract protected BigDecimal expectedResult(long operand);

	abstract protected <S extends ScaleMetrics> Decimal<S> actualResult(S scaleMetrics, long operand);
	
	protected long nextLongOperand() {
		return nextLongOrInt();
	}

	abstract protected long[] getSpecialLongOperands();
	
	@Override
	protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index) {
		runTest(scaleMetrics, "[" + index + "]", nextLongOperand());
	}

	@Override
	protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics) {
		final long[] specialLongOperands = getSpecialLongOperands();
		for (int i = 0; i < specialLongOperands.length; i++) {
			runTest(scaleMetrics, "[" + i + "]", specialLongOperands[i]);
		}
	}

	protected <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, long operand) {
		final String messagePrefix = getClass().getSimpleName() + name + ": " + operation() + " " + operand;

		//expected
		ArithmeticResult<Long> expected;
		try {
			expected = ArithmeticResult.forResult(arithmetic, expectedResult(operand));
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		} catch (IllegalArgumentException e) {
			expected = ArithmeticResult.forException(e);
		}

		//actual
		ArithmeticResult<Long> actual;
		try {
			actual = ArithmeticResult.forResult(actualResult(scaleMetrics, operand));
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		} catch (IllegalArgumentException e) {
			actual = ArithmeticResult.forException(e);
		}
		
		//assert
		actual.assertEquivalentTo(expected, messagePrefix);
	}
}
