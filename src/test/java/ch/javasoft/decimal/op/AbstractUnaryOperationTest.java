package ch.javasoft.decimal.op;

import java.math.BigDecimal;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.ScaleMetrics;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

/**
 * Base class for tests comparing the result of some unary operation of the
 * {@link Decimal} with the expected result produced by the equivalent operation
 * of the {@link BigDecimal}. The test operand values created based on random
 * long values.
 */
abstract public class AbstractUnaryOperationTest extends AbstractDecimalVersusBigDecimalTest {

	/**
	 * Constructor with arithemtics determining scale, rounding mode and
	 * overflow policy.
	 * 
	 * @param arithmetics
	 *            the arithmetics determining scale, rounding mode and overlfow
	 *            policy
	 */
	public AbstractUnaryOperationTest(DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	/**
	 * Returns the operation string, such as "+", "-", "*", "/", "abs" etc.
	 * 
	 * @return the operation string used in exceptions and log statements
	 */
	abstract protected String operation();
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
		AssertableResult expected;
		try {
			final BigDecimal exp = expectedResult(bdOperand).setScale(arithmetics.getScale(), arithmetics.getRoundingMode());
			final long expUnscaled = arithmetics.getOverflowMode().isChecked() ? exp.unscaledValue().longValueExact() : exp.unscaledValue().longValue();
			expected = new AssertableResult(exp.toPlainString(), expUnscaled);
		} catch (ArithmeticException e) {
			expected = new AssertableResult(e);
		}

		//actual
		AssertableResult actual;
		try {
			final Decimal<S> act = actualResult(dOperand);
			actual = new AssertableResult(act.toString(), act.unscaledValue());
		} catch (ArithmeticException e) {
			actual = new AssertableResult(e);
		}
		
		//assert
		actual.assertEquivalentTo(expected, getClass().getSimpleName() + name + ": " + dOperand + " " + operation());
	}
}
