package org.decimal4j.op;

import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Unit test for {@link Decimal#addUnscaled(long)}
 */
@RunWith(Parameterized.class)
public class SubtractUnscaledTest extends AbstractUnscaledOperandTest {
	
	public SubtractUnscaledTest(ScaleMetrics sm, TruncationPolicy tp, int scale, DecimalArithmetic arithmetic) {
		super(sm, tp, scale, arithmetic);
	}

	@Override
	protected String operation() {
		return "- 10^" + (-scale) + " *";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, long b) {
		return a.subtract(toBigDecimal(b));
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, long b) {
		if (scale == getScale() && RND.nextBoolean()) {
			if (isUnchecked() && RND.nextBoolean()) {
				return a.subtractUnscaled(b);
			}
			return a.subtractUnscaled(b, getOverflowMode());
		}
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			return a.subtractUnscaled(b, scale);
		}
		if (isUnchecked() && RND.nextBoolean()) {
			return a.subtractUnscaled(b, scale, getRoundingMode());
		}
		return a.subtractUnscaled(b, scale, getTruncationPolicy());
	}
}
