package org.decimal4j.op;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test the {@link DecimalArithmetic#fromDouble(double)} method which is the
 * basis of the static {@code valueOf(double)} methods of decimals. It is also
 * used in arithmetic operations involving a double operand such as
 * {@link Decimal#add(double)}.
 */
@RunWith(Parameterized.class)
public class FromDoubleTest extends Abstract1DoubleArgToDecimalResultTest {

	public FromDoubleTest(ScaleMetrics s, RoundingMode mode, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final RoundingMode mode : TestSettings.UNCHECKED_ROUNDING_MODES) {
				final DecimalArithmetic arith = s.getArithmetic(mode);
				data.add(new Object[] { s, mode, arith });
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return "fromDouble";
	}
	
	@Test
	public void testProblem1() {
		if (getScale() == 4 && getRoundingMode() == RoundingMode.HALF_DOWN) {
			runTest(getScaleMetrics(), "testProblem1", 3.354719257560035e-4);
		}
	}
	@Test
	public void testProblem2() {
		if (getScale() == 4 && getRoundingMode() == RoundingMode.HALF_DOWN) {
			runTest(getScaleMetrics(), "testProblem2", 3.9541250940045014e-4);
		}
	}

	@Override
	protected BigDecimal expectedResult(double operand) {
		return Doubles.doubleToBigDecimal(operand, getScale(), getRoundingMode());
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(S scaleMetrics, double operand) {
		final long unscaled = scaleMetrics.getArithmetic(getRoundingMode()).fromDouble(operand);
		return Factories.valueOf(scaleMetrics).valueOfUnscaled(unscaled);
	}

}
