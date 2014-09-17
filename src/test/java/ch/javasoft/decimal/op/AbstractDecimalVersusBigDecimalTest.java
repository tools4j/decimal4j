package ch.javasoft.decimal.op;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

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
	
	protected final Random rnd = new Random();

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

	@Test
	public void runRandomTest() {
		final int n = getRandomTestCount();
		final ScaleMetrics scaleMetrics = arithmetics.getScaleMetrics();
		for (int i = 0; i < n; i++) {
			runRandomTest(scaleMetrics, i);
		}
	}

	@Test
	public void runSpecialValueTest() {
		final ScaleMetrics scaleMetrics = arithmetics.getScaleMetrics();
		runSpecialValueTest(scaleMetrics);
	}

	abstract protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index);
	abstract protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics);

	protected int getRandomTestCount() {
		return 10000;
	}
	protected long[] getSpecialValues(ScaleMetrics scaleMetrics) {
		final long min = Long.MIN_VALUE;
		final long max = Long.MAX_VALUE;
		final long minInt = scaleMetrics.getMinIntegerValue();
		final long maxInt = scaleMetrics.getMaxIntegerValue();
		return new long[] {//
				min, min + 1, min/2 - 1, min/2, min/2 + 1,//
				minInt - 1, minInt, minInt + 1, minInt / 2 - 1, minInt/2, minInt/2 + 1,//
				-10, -9, -8, -7, -6, -5, -4, -3, -2, -1,//
				0,//
				1, 2, 3, 4, 5, 6, 7, 8, 9,//
				maxInt/2 - 1, maxInt/2, maxInt/2 + 1, maxInt - 1, maxInt, maxInt + 1,//
				max/2 - 1, max/2, max/2 + 1, max - 1, max//
		};
	}

	protected <S extends ScaleMetrics> Decimal<S> randomDecimal(S scaleMetrics) {
		return newDecimal(scaleMetrics, rnd.nextLong());
	}
	@SuppressWarnings("unchecked")
	protected <S extends ScaleMetrics> Decimal<S> newDecimal(S scaleMetrics, long unscaled) {
		return (Decimal<S>) scaleMetrics.createImmutable(unscaled);
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
