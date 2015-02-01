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
public class MultiplyUnscaledTest extends AbstractUnscaledOperandTest {
	
	public MultiplyUnscaledTest(ScaleMetrics sm, TruncationPolicy tp, int scale, DecimalArithmetics arithmetics) {
		super(sm, tp, scale, arithmetics);
	}

	@Override
	protected String operation() {
		return "* 10^" + (-scale) + " *";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, long b) {
		return a.multiply(toBigDecimal(b));
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, long b) {
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			if (scale == getScale() && RND.nextBoolean()) {
				return a.multiplyUnscaled(b);
			}
			return a.multiplyUnscaled(b, scale);
		}
		if (isUnchecked() && RND.nextBoolean()) {
			if (scale == getScale() && RND.nextBoolean()) {
				return a.multiplyUnscaled(b, getRoundingMode());
			}
			return a.multiplyUnscaled(b, scale, getRoundingMode());
		}
		if (scale == getScale() && RND.nextBoolean()) {
			return a.multiplyUnscaled(b, getTruncationPolicy());
		}
		return a.multiplyUnscaled(b, scale, getTruncationPolicy());
	}
}
