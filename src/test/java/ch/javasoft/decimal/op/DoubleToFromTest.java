package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.math.BigInteger;
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
 * Tests {@link DecimalArithmetics#toDouble(long)} and {@link DecimalArithmetics#fromDouble(double)}
 * and checks that the result is the same as the original input (if appropriate rounding modes
 * are used and some tolerance is allowed for 2 possible truncations).
 */
@RunWith(Parameterized.class)
@Ignore	//FIXME define proper tolerance and enable this test
public class DoubleToFromTest {

	private static final Random RND = new Random();

	private final DecimalArithmetics arithmetics;
	private final RoundingMode backRounding;

	public DoubleToFromTest(ScaleMetrics s, RoundingMode roundingMode, DecimalArithmetics arithmetics) {
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
			final long value = Double.doubleToRawLongBits(d);
			runTest("special[" + index + "]", value);
			index++;
		}
	}

	@Test
	public void testRandomDoubles() {
		final int n = TestSettings.getRandomTestCount();
		for (int i = 0; i < n; i++) {
			final double d = Doubles.randomDoubleOperand(RND);
			final long value = Double.doubleToRawLongBits(d);
			runTest("random[" + i + "]", value);
		}
	}

	private void runTest(String name, long value) {
		try {
			final double dbl = arithmetics.toDouble(value);
			final long result = arithmetics.getScaleMetrics().getArithmetics(backRounding).fromDouble(dbl);
			final long tolerance = getTolerance(value);
			Assert.assertTrue(name + ": result after 2 conversions should be same as input: input=<" + value + ">, output=<" + result + ">, tolerance=<" + tolerance + ">", Math.abs(value - result) <= tolerance);
		} catch (NumberFormatException e) {
			//ignore, must be out of range, tested elsewhere
		}
	}
	
	private final long getTolerance(long value) {
		if (value == 0) {
			return 0;
		}
		final BigDecimal scaled = BigDecimal.valueOf(value).abs().divide(BigDecimal.TEN.pow(arithmetics.getScale()));
		BigInteger unscaled = scaled.unscaledValue();
		//eliminate trailing zeroes as they are powers of two
		int lowestOneBit = 0;
		while (!unscaled.testBit(lowestOneBit)) {
			lowestOneBit++;
		}
//		unscaled = unscaled.shiftRight(lowestOneBit);
		final int mantissaBits = unscaled.bitLength();
		final int truncatedBits = mantissaBits - 52 - 1;//minus one due to the implicit 1 bit in the mantissa
		if (truncatedBits > 0) {
			return (1L << truncatedBits) - 1;//max error with truncated bits is 2^truncatedBits - 1
		}
		return 0;
	}
}
