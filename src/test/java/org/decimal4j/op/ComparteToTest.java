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
 * Unit test for {@link Decimal#compareTo(Decimal)}
 */
@RunWith(Parameterized.class)
public class ComparteToTest extends Abstract2DecimalArgsToAnyResultTest<Integer> {
	
	public ComparteToTest(ScaleMetrics scaleMetrics, DecimalArithmetic arithmetic) {
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
		return "compareTo";
	}
	
	@Override
	protected Integer expectedResult(BigDecimal a, BigDecimal b) {
		return a.compareTo(b);
	}
	
	@Override
	protected <S extends ScaleMetrics> Integer actualResult(Decimal<S> a, Decimal<S> b) {
		return a.compareTo(b);
	}
}
