package ch.javasoft.decimal.op;

import java.math.BigDecimal;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Unit test for {@link Decimal#divide(double)} etc. 
 */
@RunWith(Parameterized.class)
public class DivideDoubleTest extends AbstractDoubleOperandTest {
	
	public DivideDoubleTest(ScaleMetrics sm, TruncationPolicy tp, DecimalArithmetics arithmetics) {
		super(sm, tp, arithmetics);
	}

	@Override
	protected String operation() {
		return "/";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, double b) {
		return a.divide(toBigDecimal(b), mathContextLong128);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, double b) {
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			return a.divide(b);
		} else if (isUnchecked() && RND.nextBoolean()) {
			return a.divide(b, getRoundingMode());
		}
		return a.divide(b, getTruncationPolicy());
	}
}
