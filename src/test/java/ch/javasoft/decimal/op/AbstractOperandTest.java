package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.Scale0f;
import ch.javasoft.decimal.scale.Scale17f;
import ch.javasoft.decimal.scale.Scale6f;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.truncate.OverflowMode;
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Base class for tests of operands with random and special values. The class
 * provides also some helper methods for subclasses comparing the result of an
 * operation of the {@link Decimal} with the expected result produced by the
 * equivalent operation of the {@link BigDecimal}.
 */
abstract public class AbstractOperandTest {

	//	protected static final List<ScaleMetrics> SCALES = ScaleMetrics.VALUES;
	protected static final List<ScaleMetrics> SCALES = Arrays.<ScaleMetrics> asList(Scale0f.INSTANCE, Scale6f.INSTANCE, Scale17f.INSTANCE);

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
	public AbstractOperandTest(DecimalArithmetics arithmetics) {
		this.arithmetics = arithmetics;
		this.mathContextLong64 = new MathContext(19, arithmetics.getRoundingMode());
		this.mathContextLong128 = new MathContext(39, arithmetics.getRoundingMode());
	}

	protected int getScale() {
		return arithmetics.getScale();
	}

	protected TruncationPolicy getTruncationPolicy() {
		return arithmetics.getTruncationPolicy();
	}
	protected RoundingMode getRoundingMode() {
		return arithmetics.getRoundingMode();
	}
	protected OverflowMode getOverflowMode() {
		return arithmetics.getOverflowMode();
	}

	protected boolean isStandardTruncationPolicy() {
		return arithmetics.getRoundingMode() == TruncationPolicy.DEFAULT.getRoundingMode() && arithmetics.getOverflowMode() == TruncationPolicy.DEFAULT.getOverflowMode();
	}

	protected boolean isRoundingDown() {
		return arithmetics.getRoundingMode() == RoundingMode.DOWN;
	}
	protected boolean isUnchecked() {
		return !arithmetics.getOverflowMode().isChecked();
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

	/**
	 * Returns the operation string, such as "+", "-", "*", "/", "abs" etc.
	 * 
	 * @return the operation string used in exceptions and log statements
	 */
	abstract protected String operation();

	abstract protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index);

	abstract protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics);

	protected int getRandomTestCount() {
		return 10000;
	}

	private static final long[] SPECIALS = { Long.MIN_VALUE, Integer.MIN_VALUE, Short.MIN_VALUE, Byte.MIN_VALUE, Long.MAX_VALUE, Integer.MAX_VALUE, Short.MAX_VALUE, Byte.MAX_VALUE, };

	protected long[] getSpecialValues(ScaleMetrics scaleMetrics) {
		final Set<Long> specials = new TreeSet<Long>();
		//boundary values of different types
		for (final long s : SPECIALS) {
			specials.add(s);
			//value +/- 1
			if (s < Long.MAX_VALUE) {
				specials.add(s + 1);
			}
			if (s > Long.MIN_VALUE) {
				specials.add(s - 1);
			}
			//half value and neighbours
			specials.add(s / 2);
			specials.add(s / 2 - 1);
			specials.add(s / 2 + 1);
			//divided by scale ten, pos and neg
			for (long d = s / 10; Math.abs(d) >= 10; d /= 10) {
				specials.add(d);
				specials.add(-d);
			}
		}
		//small numbers including zero
		for (long i = -9; i <= 9; i++) {
			specials.add(i);
		}
		//powers of 10
		long pow10 = 1;
		for (int i = 1; i <= 18; i++) {
			pow10 *= 10;
			specials.add(pow10);
			specials.add(-pow10);
		}
		//convert to array
		final long[] result = new long[specials.size()];
		int index = 0;
		for (long s : specials) {
			result[index++] = s;
		}
		return result;
	}

	protected <S extends ScaleMetrics> Decimal<S> randomDecimal(S scaleMetrics) {
		final long unscaled = rnd.nextBoolean() ? rnd.nextLong() : rnd.nextInt();
		return newDecimal(scaleMetrics, unscaled);
	}

	@SuppressWarnings("unchecked")
	protected <S extends ScaleMetrics> Decimal<S> newDecimal(S scaleMetrics, long unscaled) {
		return (Decimal<S>) scaleMetrics.createImmutable(unscaled);
	}

	protected BigDecimal toBigDecimal(Decimal<?> decimal) {
		return decimal.toBigDecimal();
	}
}
