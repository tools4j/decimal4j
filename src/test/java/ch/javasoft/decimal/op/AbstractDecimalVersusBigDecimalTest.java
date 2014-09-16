package ch.javasoft.decimal.op;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.ScaleMetrics;
import ch.javasoft.decimal.ScaleMetrics.Scale0f;
import ch.javasoft.decimal.ScaleMetrics.Scale17f;
import ch.javasoft.decimal.ScaleMetrics.Scale6f;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

/**
 * Base class for tests comparing the result of an operation of the
 * {@link Decimal} with the expected result produced by the equivalent operation
 * of the {@link BigDecimal}. The test operand values created based on random
 * long values.
 */
abstract public class AbstractDecimalVersusBigDecimalTest {

//	protected static final List<ScaleMetrics> SCALES = ScaleMetrics.VALUES;
	protected static final List<ScaleMetrics> SCALES = Arrays.asList(Scale0f.INSTANCE, Scale6f.INSTANCE, Scale17f.INSTANCE);
	
	private final Random rnd = new Random();

	protected final DecimalArithmetics arithmetics;
	protected final MathContext mathContextLong64;
	protected final MathContext mathContextLong128;

	/**
	 * Constructor with arithemtics determining scale, rounding mode and
	 * overflow policy.
	 * 
	 * @param arithmetics
	 *            the arithmetics determining scale, rounding mode and overlfow
	 *            policy
	 */
	public AbstractDecimalVersusBigDecimalTest(DecimalArithmetics arithmetics) {
		this.arithmetics = arithmetics;
		this.mathContextLong64 = new MathContext(19, arithmetics.getRoundingMode());
		this.mathContextLong128 = new MathContext(39, arithmetics.getRoundingMode());
	}

	protected void runTest(int n) {
		final ScaleMetrics scaleMetrics = arithmetics.getScaleMetrics();
		for (int i = 0; i < n; i++) {
			runTest(scaleMetrics, i);
		}
	}

	abstract protected <S extends ScaleMetrics> void runTest(S scaleMetrics, int index);

	@SuppressWarnings("unchecked")
	protected <S extends ScaleMetrics> Decimal<S> randomDecimal(S scaleMetrics) {
		return (Decimal<S>) scaleMetrics.createImmutable(rnd.nextLong());
	}

	protected BigDecimal toBigDecimal(Decimal<?> decimal) {
		return decimal.toBigDecimal();
	}

	protected static class AssertableResult {
		private final String resultString;
		private final long resultUnscaled;
		private final ArithmeticException exception;

		protected AssertableResult(String resultString, long resultUnscaled) {
			this.resultString = resultString;
			this.resultUnscaled = resultUnscaled;
			this.exception = null;
		}

		protected AssertableResult(ArithmeticException exception) {
			this.resultString = null;
			this.resultUnscaled = 0;
			this.exception = exception;
		}

		public void assertEquivalentTo(AssertableResult expected, String messagePrefix) {
			if (expected.exception != null) {
				assertNotNull(messagePrefix + " was " + resultString + " but should lead to an exception: " + expected.exception);
			} else {
				assertNull(messagePrefix + " = " + expected.resultString + " but lead to an exception: " + exception, exception);
				assertEquals(messagePrefix + " = " + expected.resultString, expected.resultUnscaled, resultUnscaled);
			}
		}
	}
}
