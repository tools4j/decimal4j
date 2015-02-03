package org.decimal4j.op;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#multiplyByPowerOfTen(int)}
 */
@RunWith(Parameterized.class)
public class MultiplyByPowerOf10Test extends Abstract1DecimalArg1IntArgToDecimalResultTest {
	
	public MultiplyByPowerOf10Test(ScaleMetrics scaleMetrics, TruncationPolicy truncationPolicy, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final TruncationPolicy tp : TestSettings.POLICIES) {
				final DecimalArithmetic arith = s.getArithmetic(tp);
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
		return "*10^";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, int b) {
		final BigDecimal multiplier = BigDecimal.TEN.pow(Math.abs(b));
		return b >= 0 ? a.multiply(multiplier) : a.divide(multiplier, mathContextLong128);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, int b) {
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			return a.multiplyByPowerOfTen(b);
		} else {
			if (isUnchecked() && RND.nextBoolean()) {
				return a.multiplyByPowerOfTen(b, getRoundingMode());
			} else {
				return a.multiplyByPowerOfTen(b, getTruncationPolicy());
			}
		}
	}
}
