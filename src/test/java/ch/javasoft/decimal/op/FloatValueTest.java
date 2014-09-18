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
 * Unit test for {@link Decimal#floatValue()}
 */
@RunWith(Parameterized.class)
public class FloatValueTest extends AbstractOneAryDecimalToAnyTest<Float> {

	public FloatValueTest(ScaleMetrics scaleMetrics, DecimalArithmetics arithmetics) {
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
