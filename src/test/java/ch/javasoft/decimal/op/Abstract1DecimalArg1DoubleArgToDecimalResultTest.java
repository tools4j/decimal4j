package ch.javasoft.decimal.op;

import java.math.BigDecimal;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Base class for tests comparing the result of some binary operation of the
 * {@link Decimal} with a Decimal argument and a double argument. The expected 
 * result is produced by the equivalent operation of the {@link BigDecimal}. The 
 * test operand values are created based on random long values.
 */
abstract public class Abstract1DecimalArg1DoubleArgToDecimalResultTest extends AbstractOperandTest {

	/**
	 * Constructor with arithemtics determining scale, rounding mode and
	 * overflow policy.
	 * 
	 * @param arithmetics
	 *            the arithmetics determining scale, rounding mode and overlfow
	 *            policy
	 */
	public Abstract1DecimalArg1DoubleArgToDecimalResultTest(DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	abstract protected BigDecimal expectedResult(BigDecimal a, double b);

	abstract protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, double b);
	
	protected double randomDoubleOperand() {
		return Doubles.randomDoubleOperand(rnd);
	}

	protected double[] getSpecialDoubleOperands() {
		return Doubles.specialDoubleOperands(getScaleMetrics());
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
		final BigDecimal bdOperandA = toBigDecimal(dOperandA);

		//expected
		ArithmeticResult<Long> expected;
		try {
			expected = ArithmeticResult.forResult(arithmetics, expectedResult(bdOperandA, b));
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
		actual.assertEquivalentTo(expected, getClass().getSimpleName() + name + ": " + dOperandA + " " + operation() + " " + b);
	}
}
