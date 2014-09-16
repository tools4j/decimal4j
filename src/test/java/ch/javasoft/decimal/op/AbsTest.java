package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.ScaleMetrics;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

@RunWith(Parameterized.class)
public class AbsTest extends AbstractUnaryOperationTest {
	
	public AbsTest(ScaleMetrics scaleMetrics, RoundingMode roundingMode, DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	@Parameters(name = "{index}: scale={0}, rounding={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			data.add(new Object[] {s, RoundingMode.DOWN, s.getArithmetics(RoundingMode.DOWN)});
		}
		return data;
	}

	@Test
	public void runTest() {
		runTest(100000);
	}
	

	@Override
	protected String operation() {
		return "abs";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal operand) {
		return operand.abs();
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> operand) {
		return operand.abs();
	}
}
