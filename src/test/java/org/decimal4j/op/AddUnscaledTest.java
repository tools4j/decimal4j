package org.decimal4j.op;

import java.math.BigDecimal;

import org.decimal4j.Decimal;
import org.decimal4j.arithmetic.DecimalArithmetics;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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
		if (scale == getScale() && RND.nextBoolean()) {
			if (isUnchecked() && RND.nextBoolean()) {
				return a.addUnscaled(b);
			}
			return a.addUnscaled(b, getOverflowMode());
		}
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			return a.addUnscaled(b, scale);
		}
		if (isUnchecked() && RND.nextBoolean()) {
			return a.addUnscaled(b, scale, getRoundingMode());
		}
		return a.addUnscaled(b, scale, getTruncationPolicy());
	}
}
