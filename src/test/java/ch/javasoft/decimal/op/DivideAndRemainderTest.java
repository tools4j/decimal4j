package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.test.TestSettings;

/**
 * Unit test for {@link Decimal#divideAndRemainder(Decimal)}
 */
@RunWith(Parameterized.class)
public class DivideAndRemainderTest extends Abstract2DecimalArgsToAnyResultTest<Object[]> {
	
	public DivideAndRemainderTest(ScaleMetrics scaleMetrics, DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	@Parameters(name = "{index}: scale={0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] {s, s.getDefaultArithmetics()});
		}
		return data;
	}

	@Override
	protected String operation() {
		return "divideAndRemainder";
	}
	
	@Override
	protected BigDecimal[] expectedResult(BigDecimal a, BigDecimal b) {
		return a.divideAndRemainder(b, mathContextLong64);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S>[] actualResult(Decimal<S> a, Decimal<S> b) {
		return a.divideAndRemainder(b);
	}
	@Override
	protected <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, Decimal<S> dOpA, Decimal<S> dOpB) {
		final BigDecimal bdOpA = toBigDecimal(dOpA);
		final BigDecimal bdOpB = toBigDecimal(dOpB);

		//expected
		ArithmeticResult<Long> expected0;
		ArithmeticResult<Long> expected1;
		try {
			final BigDecimal[] exp = expectedResult(bdOpA, bdOpB);
			expected0 = ArithmeticResult.forResult(arithmetics, exp[0]);
			expected1 = ArithmeticResult.forResult(arithmetics, exp[1]);
		} catch (ArithmeticException e) {
			expected0 = ArithmeticResult.forException(e);
			expected1 = ArithmeticResult.forException(e);
		}

		//actual
		ArithmeticResult<Long> actual0;
		ArithmeticResult<Long> actual1;
		try {
			final Decimal<S>[] act = actualResult(dOpA, dOpB);
			actual0 = ArithmeticResult.forResult(act[0]);
			actual1 = ArithmeticResult.forResult(act[1]);
		} catch (ArithmeticException e) {
			actual0 = ArithmeticResult.forException(e);
			actual1 = ArithmeticResult.forException(e);
		}

		//assert
		actual0.assertEquivalentTo(expected0, getClass().getSimpleName() + name + ": " + dOpA + " " + operation() + "[0] " + dOpB);
		actual1.assertEquivalentTo(expected1, getClass().getSimpleName() + name + ": " + dOpA + " " + operation() + "[1] " + dOpB);
	}
}
