package org.decimal4j.op;

import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;

/**
 * Base class for tests comparing the result of some unary operation of the
 * {@link Decimal} with the expected result produced by the equivalent operation
 * of the {@link BigDecimal}. The test operand are values created based on random
 * long values.
 */
abstract public class Abstract1DecimalArgToDecimalResultTest extends AbstractOperandTest {

	/**
	 * Constructor with arithemtics determining scale, rounding mode and
	 * overflow policy.
	 * 
	 * @param arithmetic
	 *            the arithmetic determining scale, rounding mode and overlfow
	 *            policy
	 */
	public Abstract1DecimalArgToDecimalResultTest(DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	abstract protected BigDecimal expectedResult(BigDecimal operand);

	abstract protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> operand);

	@Override
	protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index) {
		runTest(scaleMetrics, "[" + index + "]", randomDecimal(scaleMetrics));
	}

	@Override
	protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics) {
		final long[] specialValues = getSpecialValues(scaleMetrics);
		for (int i = 0; i < specialValues.length; i++) {
			runTest(scaleMetrics, "[" + i + "]", newDecimal(scaleMetrics, specialValues[i]));
		}
	}

	private <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, Decimal<S> dOperand) {
		final BigDecimal bdOperand = toBigDecimal(dOperand);

		//expected
		ArithmeticResult<Long> expected;
		try {
			expected = ArithmeticResult.forResult(arithmetic, expectedResult(bdOperand));
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		} catch (IllegalArgumentException e) {
			expected = ArithmeticResult.forException(e);
		}

		//actual
		ArithmeticResult<Long> actual;
		try {
			actual = ArithmeticResult.forResult(actualResult(dOperand));
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		} catch (IllegalArgumentException e) {
			actual = ArithmeticResult.forException(e);
		}
		
		//assert
		actual.assertEquivalentTo(expected, getClass().getSimpleName() + name + ": " + dOperand + " " + operation());
	}
}
