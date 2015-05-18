/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.op.arith;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.op.AbstractDecimalIntToDecimalTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.ArithmeticResult;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#pow(int)}
 */
@RunWith(Parameterized.class)
public class PowTest extends AbstractDecimalIntToDecimalTest {
	
	private static final int MAX_POW_EXPONENT = getMaxPowExponent();
	
	public PowTest(ScaleMetrics scaleMetrics, TruncationPolicy truncationPolicy, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	private static int getMaxPowExponent() {
		switch (TestSettings.TEST_CASES) {
		case ALL:
			return 2000;
		case STANDARD:
			return 500;
		case SMALL:
			return 200;
		case TINY:
			return 100;
		default:
			throw new RuntimeException("unsupported: " + TestSettings.TEST_CASES);
		}
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
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> randomDecimal(S scaleMetrics) {
		final long one = scaleMetrics.getScaleFactor();
//		final long unscaled = one * (4 - RND.nextInt(9)) + one - randomLong(2*one + 1);
		final long unscaled = one * (8 - RND.nextInt(17)) + one - randomLong(2*one + 1);
		return newDecimal(scaleMetrics, unscaled);
	}

	@Override
	protected <S extends ScaleMetrics> int randomIntOperand(Decimal<S> decimalOperand) {
		if (decimalOperand.isZero() || decimalOperand.isOne() || decimalOperand.isMinusOne()) {
			return MAX_POW_EXPONENT - RND.nextInt(2 * MAX_POW_EXPONENT + 1);
		}
		final boolean posExp = RND.nextBoolean();
		final double absBase;
		if (posExp) {
			absBase = Math.abs(decimalOperand.doubleValue(RoundingMode.UP));
		} else {
			absBase = Math.abs(1.0/decimalOperand.doubleValue(RoundingMode.DOWN));
		}
		final int maxPow;
		if (absBase >= 1) {
			maxPow = (int)(Math.log(decimalOperand.getScaleMetrics().getMaxIntegerValue())/Math.max(1e-10, Math.log(absBase)));
		} else {
			maxPow = -(int)(64 / (Math.log(absBase) / Math.log(2)));
		}
		final int pow = Math.max(1, Math.min(MAX_POW_EXPONENT, maxPow));
		return posExp ? RND.nextInt(pow) : -RND.nextInt(pow);
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
	@Test
	public void test10pow3() {
		final ScaleMetrics m = getScaleMetrics();
		runTest(m, "10^3", newDecimal(m, m.multiplyByScaleFactor(10)), 3);
	}
	@Test
	public void test100pow3() {
		final ScaleMetrics m = getScaleMetrics();
		runTest(m, "100^3", newDecimal(m, m.multiplyByScaleFactor(100)), 3);
	}
	@Test
	public void test2powNeg16() {
		final ScaleMetrics m = getScaleMetrics();
		runTest(m, "2^-16", newDecimal(m, m.multiplyByScaleFactor(3)), -16);
	}
	@Test
	public void test3powNeg10() {
		final ScaleMetrics m = getScaleMetrics();
		runTest(m, "3^-10", newDecimal(m, m.multiplyByScaleFactor(3)), -10);
	}
	@Test
	public void test3_1powNeg2() {
		final ScaleMetrics m = getScaleMetrics();
		runTest(m, "3.1^-2", newDecimal(m, m.multiplyByScaleFactor(3) + m.getScaleFactor()/10), -2);
	}
	@Test
	public void test3_2powNeg2() {
		final ScaleMetrics m = getScaleMetrics();
		runTest(m, "3.2^-2", newDecimal(m, m.multiplyByScaleFactor(3) + m.getScaleFactor()/5), -2);
	}
	@Test
	public void test0_84pow254() {
		if (getScale() == 18) {
			final ScaleMetrics m = getScaleMetrics();
			runTest(m, "0.849628138173771215^254", newDecimal(m, 849628138173771215L), 254);
		}
	}
	@Test
	public void test0_9979046pow914() {
		if (getScale() == 7) {
			final ScaleMetrics m = getScaleMetrics();
			runTest(m, "0.9979046^914", newDecimal(m, 9979046), 914);
		}
	}
	@Test
	public void testMinus0_943powMinus625() {
		if (getScale() == 3) {
			final ScaleMetrics m = getScaleMetrics();
			runTest(m, "-0.943^-625", newDecimal(m, -943), -625);
		}
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
			expected = ArithmeticResult.forResult(arithmetic, expectedResult(bdOperandA, b));
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
			if (isUnchecked() && expected.isOverflow() ) {
				//overflown results without CHECKED mode don't match
				return;
			}
			if (!isWithinAllowedTolerance(expected, actual, b)) {
				throw e;
			}
		}
	}
	
	//By definition pow precision is
	//n >= 0: rounding = HALF_UP, HALF_DOWN, HALF_EVEN: 1 ULP
	//n >= 0: other rounding modes: 0
	//n < 0: 16 ULP ??? 
	private boolean isWithinAllowedTolerance(ArithmeticResult<Long> expected, ArithmeticResult<Long> actual, int exponent) {
		final int maxTolerance = exponent >= 0 ? 0 : 16;
		final int maxRoundingHalfTolerance = exponent >= 0 ? 1 : 16;
		final Long exp = expected.getCompareValue();
		final Long act = actual.getCompareValue();
		if (exp == null || act == null) {
			return false;
		}
		final boolean neg = (exp < 0 & act < 0) | ((exp == 0 | act == 0) & (exp < 0 | act < 0));
		final long diff = act - exp;
		switch (getRoundingMode()) {
		case UP:
			return neg ? diff <= 0 & diff >= -maxTolerance : diff >= 0 & diff <= maxTolerance;
		case DOWN:
			return neg ? diff >= 0 & diff <= maxTolerance : diff <= 0 & diff >= -maxTolerance;
		case CEILING:
			return diff >= 0 & diff <= maxTolerance;
		case FLOOR:
			return diff <= 0 & diff >= -maxTolerance;
		case HALF_UP://fallthrough
		case HALF_DOWN://fallthrough
		case HALF_EVEN:
			return diff <= maxRoundingHalfTolerance & diff >= -maxRoundingHalfTolerance;
		case UNNECESSARY:
			return false;
		default:
			return false;
		}
	}
	
	

	@Override
	protected BigDecimal expectedResult(BigDecimal a, int b) {
		final BigDecimal result = a.pow(Math.abs(b));
		return b >= 0 ? result.setScale(getScale(), getRoundingMode()) : BigDecimal.ONE.divide(result, getScale(), getRoundingMode());
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, int b) {
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			return a.pow(b);
		} else {
			if (isUnchecked() && RND.nextBoolean()) {
				return a.pow(b, getRoundingMode());
			} else {
				return a.pow(b, getTruncationPolicy());
			}
		}
	}
}
