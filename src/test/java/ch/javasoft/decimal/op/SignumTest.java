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
 * Unit test for {@link Decimal#signum()}
 */
@RunWith(Parameterized.class)
public class SignumTest extends Abstract1DecimalArgToAnyResultTest<Integer> {
	
	public SignumTest(ScaleMetrics scaleMetrics, DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	@Parameters(name = "{index}: scale={0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			data.add(new Object[] {s, s.getDefaultArithmetics()});
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
