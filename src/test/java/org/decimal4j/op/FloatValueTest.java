package org.decimal4j.op;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#floatValue()}
 */
@RunWith(Parameterized.class)
public class FloatValueTest extends Abstract1DecimalArgToAnyResultTest<Float> {

	public FloatValueTest(ScaleMetrics scaleMetrics, RoundingMode rounding, DecimalArithmetic arithmetic) {
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
		return "floatValue";
	}

	@Override
	protected Float expectedResult(BigDecimal operand) {
		return operand.floatValue();
	}

	@Override
	protected <S extends ScaleMetrics> Float actualResult(Decimal<S> operand) {
		if (getRoundingMode() == RoundingMode.HALF_EVEN && RND.nextBoolean()) {
			return operand.floatValue();
		}
		return operand.floatValue(getRoundingMode());
	}
}
