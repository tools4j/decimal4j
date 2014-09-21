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
 * Unit test for {@link Decimal#divideToIntegralValue(Decimal)}
 */
@RunWith(Parameterized.class)
public class DivideToIntegralValueTest extends AbstractTwoAryDecimalToDecimalTest {
	
	public DivideToIntegralValueTest(ScaleMetrics scaleMetrics, DecimalArithmetics arithmetics) {
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
	public void runSpecialValueTest() {
		super.runSpecialValueTest();
	}

	@Override
	protected String operation() {
		return "divideToIntegralValue";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b) {
		return a.divideToIntegralValue(b, mathContextLong64);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		return a.divideToIntegralValue(b);
	}
}
