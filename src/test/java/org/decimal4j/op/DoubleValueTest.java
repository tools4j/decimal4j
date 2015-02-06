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
 * Unit test for {@link Decimal#doubleValue()}
 */
@RunWith(Parameterized.class)
public class DoubleValueTest extends Abstract1DecimalArgToAnyResultTest<Double> {

	public DoubleValueTest(ScaleMetrics scaleMetrics, RoundingMode rounding, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: {0} {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			//TODO how should we test rounding modes other than HALF_EVEN, i.e. how do we compute the expected result?
//			for (final RoundingMode rm : TestSettings.UNCHECKED_ROUNDING_MODES) {
//				data.add(new Object[] { s, rm, s.getArithmetic(rm) });
//			}
			data.add(new Object[] { s, RoundingMode.HALF_EVEN, s.getArithmetic(RoundingMode.HALF_EVEN) });
		}
		return data;
	}

	@Override
	protected String operation() {
		return "doubleValue";
	}
	
	@Test
	public void scaleFactorPlusUlpTest() {
		runTest(getScaleMetrics(), "scaleFactorPlusUlpTest", Factories.getDecimalFactory(getScaleMetrics()).valueOfUnscaled(1+getScaleMetrics().getScaleFactor()));
	}

	@Override
	protected Double expectedResult(BigDecimal operand) {
		return operand.doubleValue();
	}

	@Override
	protected <S extends ScaleMetrics> Double actualResult(Decimal<S> operand) {
		if (getRoundingMode() == RoundingMode.HALF_EVEN && RND.nextBoolean()) {
			return operand.doubleValue();
		}
		return operand.doubleValue(getRoundingMode());
	}
	
}
