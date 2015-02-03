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
 * Unit test for {@link Decimal#shortValue()} and
 * {@link Decimal#shortValueExact()}.
 */
@RunWith(Parameterized.class)
public class ShortValueTest extends Abstract1DecimalArgToAnyResultTest<Short> {

	private final boolean exact;

	public ShortValueTest(ScaleMetrics scaleMetrics, boolean exact, DecimalArithmetic arithmetic) {
		super(arithmetic);
		this.exact = exact;
	}

	@Parameters(name = "{index}: scale={0}, exact={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] { s, true, s.getDefaultArithmetic() });
			data.add(new Object[] { s, false, s.getDefaultArithmetic() });
		}
		return data;
	}

	@Override
	protected String operation() {
		return exact ? "shortValueExact" : "shortValue";
	}

	@Override
	protected Short expectedResult(BigDecimal operand) {
		return exact ? operand.shortValueExact() : operand.shortValue();
	}

	@Override
	protected <S extends ScaleMetrics> Short actualResult(Decimal<S> operand) {
		return exact ? operand.shortValueExact() : operand.shortValue();
	}
}
