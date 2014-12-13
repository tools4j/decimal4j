package ch.javasoft.decimal.op;

import java.math.BigDecimal;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Unit test for {@link Decimal#addUnscaled(long)}
 */
@RunWith(Parameterized.class)
public class AddUnscaledTest extends AbstractUnscaledOperandTest {
	
	public AddUnscaledTest(ScaleMetrics sm, TruncationPolicy tp, int scale, DecimalArithmetics arithmetics) {
		super(sm, tp, scale, arithmetics);
	}

	@Override
	protected String operation() {
		return "+ 10^" + (-scale) + " *";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, long b) {
		return a.add(toBigDecimal(b));
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, long b) {
		if (scale == getScale() && rnd.nextBoolean()) {
			if (isUnchecked() && rnd.nextBoolean()) {
				return a.addUnscaled(b);
			}
			return a.addUnscaled(b, getOverflowMode());
		}
		if (isStandardTruncationPolicy() && rnd.nextBoolean()) {
			return a.addUnscaled(b, scale);
		}
		if (isUnchecked() && rnd.nextBoolean()) {
			return a.addUnscaled(b, scale, getRoundingMode());
		}
		return a.addUnscaled(b, scale, getTruncationPolicy());
	}
}
