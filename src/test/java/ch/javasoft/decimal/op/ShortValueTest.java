package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.ScaleMetrics;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

/**
 * Unit test for {@link Decimal#shortValue()} and
 * {@link Decimal#shortValueExact()}.
 */
@RunWith(Parameterized.class)
public class ShortValueTest extends AbstractOneAryDecimalToAnyTest<Short> {

	private final boolean exact;

	public ShortValueTest(ScaleMetrics scaleMetrics, boolean exact, DecimalArithmetics arithmetics) {
		super(arithmetics);
		this.exact = exact;
	}

	@Parameters(name = "{index}: scale={0}, exact={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			data.add(new Object[] { s, true, s.getDefaultArithmetics() });
			data.add(new Object[] { s, false, s.getDefaultArithmetics() });
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
