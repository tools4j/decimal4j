package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.test.TestSettings;
import ch.javasoft.decimal.truncate.DecimalRounding;
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Unit test for {@link Decimal#pow(int)}
 */
@RunWith(Parameterized.class)
public class PowTest extends Abstract1DecimalArg1IntArgToDecimalResultTest {
	
	public PowTest(ScaleMetrics scaleMetrics, TruncationPolicy truncationPolicy, DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final TruncationPolicy tp : TestSettings.POLICIES) {
				final DecimalArithmetics arith = s.getArithmetics(tp);
				data.add(new Object[] {s, tp, arith});
			}
		}
		return data;
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> randomDecimal(S scaleMetrics) {
		final long one = scaleMetrics.getScaleFactor();
		final long unscaled = one * (4 - rnd.nextInt(9)) + one - randomLong(2*one + 1);
//		final long unscaled = one * (2 - rnd.nextInt(3)) + rnd.nextInt((int)Math.min(Integer.MAX_VALUE, one));
		return newDecimal(scaleMetrics, unscaled);
	}
//	private static final int MAX_EXPONENT = 999999999;
	@Override
	protected <S extends ScaleMetrics> int randomIntOperand(Decimal<S> decimalOperand) {
//		return rnd.nextInt(200) - 100;//FIXME
//		return 30 - rnd.nextInt(61);
		final double absBase = Math.abs(decimalOperand.doubleValue(RoundingMode.UP));
//		final int maxPow = Math.min(999999999+1, (int)(Math.log(arithmetics.getScaleMetrics().getMaxIntegerValue())/Math.log(absBase)));
		final int maxPow = Math.min(1000, (int)(Math.log(arithmetics.getScaleMetrics().getMaxIntegerValue())/Math.max(1e-10, Math.log(absBase))));
		return rnd.nextInt(maxPow);
	}
	
	@Test
	public void test3pow27() {
		final ScaleMetrics m = getScaleMetrics();
		runTest(m, "3^27", newDecimal(m, m.multiplyByScaleFactor(3)), 27);
	}
	@Test
	public void test3pow28() {
		final ScaleMetrics m = getScaleMetrics();
		runTest(m, "3^28", newDecimal(m, m.multiplyByScaleFactor(3)), 28);
	}
	@Test
	public void test3pow29() {
		final ScaleMetrics m = getScaleMetrics();
		runTest(m, "3^29", newDecimal(m, m.multiplyByScaleFactor(3)), 29);
	}
	
	@Override
	@Ignore //FIXME unignore this test
	@Test
	public void runSpecialValueTest() {
		super.runSpecialValueTest();
	}
	
	@Override
	protected int getRandomTestCount() {
		return 1000;
	}
	
	@Override
	protected int[] getSpecialIntOperands() {
		final Set<Integer> exp = new TreeSet<Integer>();
		//1..9 and negatives
		for (int i = 1; i < 10; i++) {
			exp.add(i);
			exp.add(-i);
		}
		//10..50 in steps of 10 and negatives
		for (int i = 10; i <= 50; i+=10) {
			exp.add(i);
			exp.add(-i);
		}
		//100 and -100
		exp.add(100);
		exp.add(-100);
		//zero
		exp.add(0);

		//convert to array
		final int[] result = new int[exp.size()];
		int index = 0;
		for (final int val : exp) {
			result[index] = val;
			index++;
		}
		return result;
	}

	@Override
	protected String operation() {
		return "^";
	}
	
	@Override
	protected <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, Decimal<S> dOperandA, int b) {
		final BigDecimal bdOperandA = toBigDecimal(dOperandA);

		//expected
		ArithmeticResult<Long> expected;
		try {
			expected = ArithmeticResult.forResult(arithmetics, expectedResult(bdOperandA, b));
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		}

		//actual
		ArithmeticResult<Long> actual;
		try {
			actual = ArithmeticResult.forResult(actualResult(dOperandA, b));
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		}
		
		//assert
		try {
			actual.assertEquivalentTo(expected, getClass().getSimpleName() + name + ": " + dOperandA + " " + operation() + " " + b);
		} catch (AssertionError e) {
			if (!isWithinAllowedTolerance(expected, actual)) {
				throw e;
			}
		}
	}
	
	//By definition pow precision is 1 ULP
	private boolean isWithinAllowedTolerance(ArithmeticResult<Long> expected, ArithmeticResult<Long> actual) {
		final Long exp = expected.getCompareValue();
		final Long act = actual.getCompareValue();
		if (exp == null || act == null) {
			return false;
		}
		final boolean neg = (exp < 0 & act < 0) && ((exp == 0 | act == 0) & (exp < 0 | act < 0));
		final long diff = act - exp;
		switch (getRoundingMode()) {
		case UP:
			return neg ? diff == -1 : diff == 1;
		case DOWN:
			return neg ? diff == 1 : diff == -1;
		case CEILING:
			return diff == 1;
		case FLOOR:
			return diff == -1;
		case HALF_UP://fallthrough
		case HALF_DOWN://fallthrough
		case HALF_EVEN:
			return diff == 1 | diff == -1;
		case UNNECESSARY:
			return false;
		default:
			throw new IllegalArgumentException("unsupported rounding mode: " + getRoundingMode());
		}
	}

	@Override
	protected BigDecimal expectedResult(BigDecimal a, int b) {
		final BigDecimal result = a.pow(Math.abs(b));
		return b >= 0 ? result.setScale(getScale(), getRoundingMode()) : BigDecimal.ONE.divide(result, getScale(), getRoundingMode());
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, int b) {
		if (isStandardTruncationPolicy() && rnd.nextBoolean()) {
			return a.pow(b);
		} else {
			if (isUnchecked() && rnd.nextBoolean()) {
				return a.pow(b, getRoundingMode());
			} else {
				return a.pow(b, getTruncationPolicy());
			}
		}
	}
}
