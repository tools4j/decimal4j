package ch.javasoft.decimal.op;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.test.TestSettings;

/**
 * Tests {@link DecimalArithmetics#fromDouble(double)} and {@link DecimalArithmetics#toDouble(long)}
 * and checks that the result is the same as the original input (if appropriate rounding modes
 * are used and some tolerance is allowed for 2 possible truncations).
 */
@RunWith(Parameterized.class)
@Ignore	//FIXME define proper tolerance and enable this test
public class DoubleFromToTest {

	private static final Random RND = new Random();

	private final DecimalArithmetics arithmetics;
	private final RoundingMode backRounding;

	public DoubleFromToTest(ScaleMetrics s, RoundingMode roundingMode, DecimalArithmetics arithmetics) {
		this.arithmetics = arithmetics;
		this.backRounding = Doubles.getOppositeRoundingMode(roundingMode);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final RoundingMode mode : TestSettings.UNCHECKED_ROUNDING_MODES) {
				if (mode != RoundingMode.UNNECESSARY) {
					final DecimalArithmetics arith = s.getArithmetics(mode);
					data.add(new Object[] { s, mode, arith });
				}
			}
		}
		return data;
	}

	@Test
	public void testSpecialDoubles() {
		int index = 0;
		for (final double d : Doubles.specialDoubleOperands(arithmetics.getScaleMetrics())) {
			runTest("special[" + index + "]", d);
			index++;
		}
	}

	@Test
	public void testRandomDoubles() {
		final int n = TestSettings.getRandomTestCount();
		for (int i = 0; i < n; i++) {
			runTest("random[" + i + "]", Doubles.randomDoubleOperand(RND));
		}
	}

	private void runTest(String name, double d) {
		try {
			final long uDecimal = arithmetics.fromDouble(d);
			final double result = arithmetics.getScaleMetrics().getArithmetics(backRounding).toDouble(uDecimal);
			final double tolerance = 1.0/arithmetics.getScaleMetrics().getScaleFactor();
			Assert.assertEquals(name + ": result after 2 conversions should be same as input", d, result, tolerance);
		} catch (NumberFormatException e) {
			//ignore, must be out of range, tested elsewhere
		}
	}

}
