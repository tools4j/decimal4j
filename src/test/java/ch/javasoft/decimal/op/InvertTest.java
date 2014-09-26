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
 * Unit test for {@link Decimal#invert()}
 */
@RunWith(Parameterized.class)
public class InvertTest extends AbstractOneAryDecimalToDecimalTest {
	
	public InvertTest(ScaleMetrics scaleMetrics, RoundingMode roundingMode, DecimalArithmetics arithmetics) {
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

	@Override
	protected String operation() {
		return "^-1";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal operand) {
		return BigDecimal.ONE.divide(operand, mathContextLong128);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> operand) {
		if (isStandardRounding() & rnd.nextBoolean()) {
			return operand.invert();
		} else {
			return operand.invert(getRoundingMode());
		}
	}
}
