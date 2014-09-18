package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Unit test for {@link Decimal#subtract(Decimal)}
 */
@RunWith(Parameterized.class)
public class SubtractTest extends AbstractTwoAryDecimalToDecimalTest {
	
	public SubtractTest(ScaleMetrics scaleMetrics, RoundingMode roundingMode, DecimalArithmetics arithmetics) {
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

	@Override
	protected String operation() {
		return "-";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b) {
		return a.subtract(b);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		return a.subtract(b);
	}
}
