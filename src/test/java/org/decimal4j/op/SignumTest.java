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
 * Unit test for {@link Decimal#signum()}
 */
@RunWith(Parameterized.class)
public class SignumTest extends Abstract1DecimalArgToAnyResultTest<Integer> {
	
	public SignumTest(ScaleMetrics scaleMetrics, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: scale={0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] {s, s.getDefaultArithmetic()});
		}
		return data;
	}

	@Override
	protected String operation() {
		return "sgn";
	}
	
	@Override
	protected Integer expectedResult(BigDecimal operand) {
		return operand.signum();
	}
	
	@Override
	protected <S extends ScaleMetrics> Integer actualResult(Decimal<S> operand) {
		return operand.signum();
	}
}
