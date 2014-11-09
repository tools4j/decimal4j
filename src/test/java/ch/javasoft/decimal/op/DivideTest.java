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
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Unit test for {@link Decimal#divide(Decimal, RoundingMode)}
 */
@RunWith(Parameterized.class)
public class DivideTest extends Abstract2DecimalArgsToDecimalResultTest {
	
	public DivideTest(ScaleMetrics scaleMetrics, TruncationPolicy truncationPolicy, DecimalArithmetics arithmetics) {
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
	public void runSpecialValueTest() {
		super.runSpecialValueTest();
	}

	@Override
	protected String operation() {
		return "/";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b) {
		return a.divide(b, mathContextLong128);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		if (isStandardTruncationPolicy() && rnd.nextBoolean()) {
			return a.divide(b);
		} else {
			if (isUnchecked() && rnd.nextBoolean()) {
				return a.divide(b, getRoundingMode());
			} else {
				return a.divide(b, getTruncationPolicy());
			}
		}
	}
}
