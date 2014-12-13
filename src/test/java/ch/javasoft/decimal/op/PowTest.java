package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.test.TestSettings;
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
	
//	private static final int MAX_EXPONENT = 999999999;
	@Override
	protected int randomIntOperand() {
		return rnd.nextInt(200) - 100;
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
	protected BigDecimal expectedResult(BigDecimal a, int b) {
		final BigDecimal result = a.pow(Math.abs(b));
		return b >= 0 ? result.setScale(getScale(), getRoundingMode()) : BigDecimal.ONE.divide(result, getScale(), getRoundingMode());
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, int b) {
		if (isStandardTruncationPolicy() & rnd.nextBoolean()) {
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
