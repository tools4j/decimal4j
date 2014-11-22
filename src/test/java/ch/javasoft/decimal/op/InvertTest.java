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
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Unit test for {@link Decimal#invert()}
 */
@RunWith(Parameterized.class)
public class InvertTest extends Abstract1DecimalArgToDecimalResultTest {
	
	public InvertTest(ScaleMetrics scaleMetrics, TruncationPolicy truncationPolicy, DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			for (final TruncationPolicy tp : POLICIES) {
				final DecimalArithmetics arith = s.getArithmetics(tp);
				data.add(new Object[] {s, tp, arith});
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
		if (isStandardTruncationPolicy() & rnd.nextBoolean()) {
			return operand.invert();
		} else {
			if (isUnchecked() && rnd.nextBoolean()) {
				return operand.invert(getRoundingMode());
			} else {
				return operand.invert(getTruncationPolicy());
			}
		}
	}
}
