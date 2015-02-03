package org.decimal4j.op;

import java.math.BigDecimal;
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

	public FloatValueTest(ScaleMetrics scaleMetrics, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: scale={0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] { s, s.getDefaultArithmetic() });
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
		return operand.floatValue();
	}
}
