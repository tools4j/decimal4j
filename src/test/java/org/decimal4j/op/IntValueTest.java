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
 * Unit test for {@link Decimal#intValue()} and {@link Decimal#intValueExact()}.
 */
@RunWith(Parameterized.class)
public class IntValueTest extends Abstract1DecimalArgToAnyResultTest<Integer> {
	
	private final boolean exact;

	public IntValueTest(ScaleMetrics scaleMetrics, boolean exact, DecimalArithmetic arithmetic) {
		super(arithmetic);
		this.exact = exact;
	}

	@Parameters(name = "{index}: scale={0}, exact={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] {s, true, s.getDefaultArithmetic()});
			data.add(new Object[] {s, false, s.getDefaultArithmetic()});
		}
		return data;
	}

	@Override
	protected String operation() {
		return exact ? "intValueExact" : "intValue";
	}
	
	@Override
	protected Integer expectedResult(BigDecimal operand) {
		return exact? operand.intValueExact() : operand.intValue();
	}
	
	@Override
	protected <S extends ScaleMetrics> Integer actualResult(Decimal<S> operand) {
		return exact? operand.intValueExact() : operand.intValue();
	}
}
