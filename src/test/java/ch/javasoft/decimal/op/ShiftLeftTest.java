package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
 * Unit test for {@link Decimal#shiftLeft(int)}
 */
@RunWith(Parameterized.class)
public class ShiftLeftTest extends Abstract1DecimalArg1IntArgToDecimalResultTest {
	
	public ShiftLeftTest(ScaleMetrics scaleMetrics, TruncationPolicy truncationPolicy, DecimalArithmetics arithmetics) {
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
	protected <S extends ScaleMetrics> int randomIntOperand(Decimal<S> decimalOperand) {
		return RND.nextInt(200) - 100;
	}
	@Override
	protected int getRandomTestCount() {
		return 1000;
	}
	
	@Override
	protected int[] getSpecialIntOperands() {
		final Set<Integer> exp = new TreeSet<Integer>();
		//1..100 and negatives
		for (int i = 1; i < 100; i++) {
			exp.add(i);
			exp.add(-i);
		}
		//zero
		exp.add(0);
		exp.add(Long.SIZE-1);
		exp.add(Long.SIZE);
		exp.add(Long.SIZE+1);
		exp.add(-(Long.SIZE-1));
		exp.add(-Long.SIZE);
		exp.add(-(Long.SIZE+1));
		exp.add(Integer.MAX_VALUE);
		exp.add(Integer.MIN_VALUE);

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
		return "<<";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, int b) {
		final int exp = Math.max(Math.min(b, Long.SIZE+1), -Long.SIZE-1);
		if (b >= 0 || getRoundingMode() == RoundingMode.FLOOR) {
			return new BigDecimal(a.unscaledValue().shiftLeft(exp), a.scale());
		}
//		System.out.println(a + "<<" + b + ": " + exp);
		return a.divide(new BigDecimal(BigInteger.ONE.shiftLeft(-exp)), getScale(), getRoundingMode());
//		return new BigDecimal(a.unscaledValue().shiftRight(exp), a.scale());
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, int b) {
		if (getRoundingMode() == RoundingMode.FLOOR && isUnchecked() && RND.nextBoolean()) {
			return a.shiftLeft(b);
		} else {
			if (isUnchecked() && RND.nextBoolean()) {
				return a.shiftLeft(b, getRoundingMode());
			} else {
				return a.shiftLeft(b, getTruncationPolicy());
			}
		}
	}
}
