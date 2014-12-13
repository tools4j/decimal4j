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
import ch.javasoft.decimal.test.TestSettings;
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Unit test for {@link Decimal#longValue()}, {@link Decimal#longValueExact()}
 * and {@link Decimal#longValue(RoundingMode)}.
 */
@RunWith(Parameterized.class)
public class LongValueTest extends Abstract1DecimalArgToAnyResultTest<Long> {
	
	private final boolean exact;

	public LongValueTest(ScaleMetrics scaleMetrics, TruncationPolicy truncationPolicy, boolean exact, DecimalArithmetics arithmetics) {
		super(arithmetics);
		this.exact = exact;
	}

	@Parameters(name = "{index}: {0}, {1}, exact={2}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] {s, TruncationPolicy.DEFAULT, true, s.getDefaultArithmetics()});
			for (final TruncationPolicy tp : TestSettings.POLICIES) {
				data.add(new Object[] {s, tp, false, s.getArithmetics(tp)});
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return exact ? "longValueExact" : "longValue";
	}
	
	@Override
	protected Long expectedResult(BigDecimal operand) {
		if (exact) {
			return operand.longValueExact();
		}
		if (isRoundingDown() && rnd.nextBoolean()) {
			return operand.longValue();
		}
		return operand.setScale(0, getRoundingMode()).longValue();
	}
	
	@Override
	protected <S extends ScaleMetrics> Long actualResult(Decimal<S> operand) {
		if (exact) {
			return operand.longValueExact();
		}
		if (isUnchecked() && isRoundingDown() && rnd.nextBoolean()) {
			return operand.longValue();
		} else {
			if (isUnchecked() && rnd.nextBoolean()) {
				return operand.longValue(getRoundingMode());
			} else {
				return operand.longValue(getTruncationPolicy());
			}
		}
	}
}
