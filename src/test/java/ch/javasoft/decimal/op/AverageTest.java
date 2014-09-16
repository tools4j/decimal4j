package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.ScaleMetrics;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

@Ignore//FIXME make this pass
@RunWith(Parameterized.class)
public class AverageTest extends AbstractBinaryOperationTest {
	
	private static final BigDecimal TWO = BigDecimal.valueOf(2);
	
	public AverageTest(ScaleMetrics scaleMetrics, RoundingMode roundingMode, DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	@Parameters(name = "{index}: scale={0}, rounding={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			for (final RoundingMode rm : RoundingMode.values()) {
				data.add(new Object[] {s, rm, s.getArithmetics(rm)});
			}
		}
		return data;
	}

	@Test
	public void runTest() {
		runTest(100000);
	}
	

	@Override
	protected String operation() {
		return "avg";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b) {
		return a.add(b).divide(TWO, arithmetics.getRoundingMode());
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		return a.average(b, arithmetics.getRoundingMode());
	}
}
