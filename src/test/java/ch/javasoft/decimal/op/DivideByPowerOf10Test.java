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
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Unit test for {@link Decimal#divideByPowerOfTen(int)}
 */
@RunWith(Parameterized.class)
public class DivideByPowerOf10Test extends Abstract1DecimalArg1IntArgToDecimalResultTest {
	
	public DivideByPowerOf10Test(ScaleMetrics scaleMetrics, TruncationPolicy truncationPolicy, DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			for (final TruncationPolicy tp : TruncationPolicy.VALUES) {
				final DecimalArithmetics arith = s.getArithmetics(tp);
				if (arith != null) {//FIXME this if can be removed later
					data.add(new Object[] {s, tp, arith});
				}
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
		return "/10^";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, int b) {
		final BigDecimal multiplier = BigDecimal.TEN.pow(Math.abs(b));
		return b >= 0 ? a.divide(multiplier, mathContextLong128) : a.multiply(multiplier);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, int b) {
		if (isStandardTruncationPolicy() & rnd.nextBoolean()) {
			return a.divideByPowerOfTen(b);
		} else {
			if (isUnchecked() && rnd.nextBoolean()) {
				return a.divideByPowerOfTen(b, getRoundingMode());
			} else {
				return a.divideByPowerOfTen(b, getTruncationPolicy());
			}
		}
	}
}
