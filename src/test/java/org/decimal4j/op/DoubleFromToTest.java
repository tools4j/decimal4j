package org.decimal4j.op;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests {@link DecimalArithmetic#fromDouble(double)} and {@link DecimalArithmetic#toDouble(long)}
 * and checks that the result is the same as the original input (if appropriate rounding modes
 * are used and some tolerance is allowed for 2 possible truncations).
 */
@RunWith(Parameterized.class)
public class DoubleFromToTest {

	private static final Random RND = new Random();

	private final DecimalArithmetic arithmetic;
	private final RoundingMode backRounding;

	public DoubleFromToTest(ScaleMetrics s, RoundingMode roundingMode, DecimalArithmetic arithmetic) {
		this.arithmetic = arithmetic;
		this.backRounding = FloatAndDoubleUtil.getOppositeRoundingMode(roundingMode);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final RoundingMode mode : TestSettings.UNCHECKED_ROUNDING_MODES) {
				if (mode != RoundingMode.UNNECESSARY) {
					final DecimalArithmetic arith = s.getArithmetic(mode);
					data.add(new Object[] { s, mode, arith });
				}
			}
		}
		return data;
	}

	@Test
	public void testSpecialDoubles() {
		int index = 0;
		for (final double d : FloatAndDoubleUtil.specialDoubleOperands(arithmetic.getScaleMetrics())) {
			runTest("special[" + index + "]", d);
			index++;
		}
	}

	@Test
	public void testRandomDoubles() {
		final int n = TestSettings.getRandomTestCount();
		for (int i = 0; i < n; i++) {
			runTest("random[" + i + "]", FloatAndDoubleUtil.randomDoubleOperand(RND));
		}
	}

	private void runTest(String name, double d) {
		try {
			final long uDecimal = arithmetic.fromDouble(d);
			final double result = arithmetic.getScaleMetrics().getArithmetic(backRounding).toDouble(uDecimal);
			final double tolerance = 2.0*max(Math.ulp(result), Math.ulp(d), 1.0/arithmetic.getScaleMetrics().getScaleFactor());
			Assert.assertEquals(name + ": result after 2 conversions should be same as input with tolerance=<" + tolerance + ">, delta=<" + Math.abs(d-result) + ">",  d, result, tolerance);
		} catch (NumberFormatException e) {
			//ignore, must be out of range, tested elsewhere
		}
	}
	
	private static double max(double val1, double val2, double val3) {
		return Math.max(Math.max(val1, val2), val3);
	}

}
