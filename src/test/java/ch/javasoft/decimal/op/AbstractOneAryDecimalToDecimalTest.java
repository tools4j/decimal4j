package ch.javasoft.decimal.op;

import java.math.BigDecimal;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Base class for tests comparing the result of some unary operation of the
 * {@link Decimal} with the expected result produced by the equivalent operation
 * of the {@link BigDecimal}. The test operand values created based on random
 * long values.
 */
abstract public class AbstractOneAryDecimalToDecimalTest extends AbstractDecimalVersusBigDecimalTest {

	/**
	 * Constructor with arithemtics determining scale, rounding mode and
	 * overflow policy.
	 * 
	 * @param arithmetics
	 *            the arithmetics determining scale, rounding mode and overlfow
	 *            policy
	 */
	public AbstractOneAryDecimalToDecimalTest(DecimalArithmetics arithmetics) {
		super(arithmetics);
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
			expected = ArithmeticResult.forResult(arithmetics, expectedResult(bdOperand));
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		}

		//actual
		ArithmeticResult<Long> actual;
		try {
			actual = ArithmeticResult.forResult(actualResult(dOperand));
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		}
		
		//assert
		actual.assertEquivalentTo(expected, getClass().getSimpleName() + name + ": " + dOperand + " " + operation());
	}
}
