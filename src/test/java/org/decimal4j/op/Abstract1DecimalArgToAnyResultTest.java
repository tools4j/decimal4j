package org.decimal4j.op;

import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;

/**
 * Base class for tests comparing the result of some unary operation of the
 * {@link Decimal} with the expected result produced by the equivalent operation
 * of the {@link BigDecimal}. The test operand values are created based on random
 * long values.
 * 
 * @param <R> the result type of the operation, common type for {@link Decimal} and {@link BigDecimal}
 */
abstract public class Abstract1DecimalArgToAnyResultTest<R> extends AbstractOperandTest {

	/**
	 * Constructor with arithemtics determining scale, rounding mode and
	 * overflow policy.
	 * 
	 * @param arithmetic
	 *            the arithmetic determining scale, rounding mode and overlfow
	 *            policy
	 */
	public Abstract1DecimalArgToAnyResultTest(DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	abstract protected R expectedResult(BigDecimal operand);
	abstract protected <S extends ScaleMetrics> R actualResult(Decimal<S> operand);

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

	protected <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, Decimal<S> dOperand) {
		final BigDecimal bdOperand = toBigDecimal(dOperand);

		//expected
		ArithmeticResult<R> expected;
		try {
			final R exp = expectedResult(bdOperand);
			expected = ArithmeticResult.forResult(exp.toString(), exp);
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		}

		//actual
		ArithmeticResult<R> actual;
		try {
			final R act = actualResult(dOperand);
			actual = ArithmeticResult.forResult(act.toString(), act);
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		}
		
		//assert
		actual.assertEquivalentTo(expected, getClass().getSimpleName() + name + ": " + dOperand + " " + operation());
	}
}
