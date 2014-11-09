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
 * Unit test for {@link Decimal#square()}
 */
@RunWith(Parameterized.class)
public class SquareTest extends Abstract1DecimalArgToDecimalResultTest {
	
	public SquareTest(ScaleMetrics scaleMetrics, TruncationPolicy truncationPolicy, DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			for (final TruncationPolicy tp : TruncationPolicy.VALUES) {
				final DecimalArithmetics arith = s.getArithmetics(tp);
				if (arith != null) {//FIXME this if can be removed later
					data.add(new Object[] {s, tp, arith});
				}
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return "^2";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal operand) {
		return operand.multiply(operand, mathContextLong128);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> operand) {
		if (isStandardTruncationPolicy() & rnd.nextBoolean()) {
			return operand.square();
		} else {
			if (isUnchecked() && rnd.nextBoolean()) {
				return operand.square(getRoundingMode());
			} else {
				return operand.square(getTruncationPolicy());
			}
		}
	}
}
