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
 * Tests {@link DecimalArithmetic#toFloat(long)} and {@link DecimalArithmetic#fromFloat(float)}
 * and checks that the result is the same as the original input (if appropriate rounding modes
 * are used and some tolerance is allowed for 2 possible truncations).
 */
@RunWith(Parameterized.class)
public class FloatToFromTest {

	private static final Random RND = new Random();

	private final DecimalArithmetic arithmetic;
	private final RoundingMode backRounding;

	public FloatToFromTest(ScaleMetrics s, RoundingMode roundingMode, DecimalArithmetic arithmetic) {
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
	public void testSpecialFloats() {
		int index = 0;
		for (final long value : TestSettings.TEST_CASES.getSpecialValuesFor(arithmetic.getScaleMetrics())) {
			runTest("special[" + index + "]", value);
			index++;
		}
	}

	@Test
	public void testRandomFloats() {
		final int n = TestSettings.getRandomTestCount();
		for (int i = 0; i < n; i++) {
			final long value = RND.nextLong();
			runTest("random[" + i + "]", value);
		}
	}

	private void runTest(String name, long value) {
		try {
			final float flt = arithmetic.toFloat(value);
			final long result = arithmetic.getScaleMetrics().getArithmetic(backRounding).fromFloat(flt);
			final long tolerance = (long)(Math.ceil(((double)Math.ulp(flt)) * arithmetic.getScaleMetrics().getScaleFactor()));
			Assert.assertTrue(name + ": result after 2 conversions should be same as input: input=<" + value + ">, output=<" + result + ">, tolerance=<" + tolerance + ">, delta=<" + Math.abs(value - result) + ">", Math.abs(value - result) <= tolerance);
		} catch (NumberFormatException e) {
			//ignore, must be out of range, tested elsewhere
		}
	}
	
}
