package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Unit test for {@link Decimal#doubleValue()}
 */
@RunWith(Parameterized.class)
public class DoubleValueTest extends AbstractOneAryDecimalToAnyTest<Double> {

	public DoubleValueTest(ScaleMetrics scaleMetrics, DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	@Parameters(name = "{index}: scale={0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			data.add(new Object[] { s, s.getDefaultArithmetics() });
		}
		return data;
	}

	@Override
	protected String operation() {
		return "doubleValue";
	}

	@Override
	protected Double expectedResult(BigDecimal operand) {
		return operand.doubleValue();
	}

	@Override
	protected <S extends ScaleMetrics> Double actualResult(Decimal<S> operand) {
		return operand.doubleValue();
	}
}