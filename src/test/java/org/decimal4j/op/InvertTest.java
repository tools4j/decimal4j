package org.decimal4j.op;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#invert()}
 */
@RunWith(Parameterized.class)
public class InvertTest extends Abstract1DecimalArgToDecimalResultTest {
	
	public InvertTest(ScaleMetrics scaleMetrics, TruncationPolicy truncationPolicy, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final TruncationPolicy tp : TestSettings.POLICIES) {
				final DecimalArithmetic arith = s.getArithmetic(tp);
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
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			return operand.invert();
		} else {
			if (isUnchecked() && RND.nextBoolean()) {
				return operand.invert(getRoundingMode());
			} else {
				return operand.invert(getTruncationPolicy());
			}
		}
	}
}
