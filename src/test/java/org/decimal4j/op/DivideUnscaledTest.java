package org.decimal4j.op;

import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Unit test for {@link Decimal#addUnscaled(long)}
 */
@RunWith(Parameterized.class)
public class DivideUnscaledTest extends AbstractUnscaledOperandTest {
	
	public DivideUnscaledTest(ScaleMetrics sm, TruncationPolicy tp, int scale, DecimalArithmetic arithmetic) {
		super(sm, tp, scale, arithmetic);
	}

	@Override
	protected String operation() {
		return "/ 10^" + (-scale) + " /";
	}
	
	@Test
	public void testProblem1() {
		if (getScale() == 17 && isUnchecked() && scale == 16) {
			runTest(getScaleMetrics(), "problem1", newDecimal(getScaleMetrics(), 1000000000000000L), Integer.MIN_VALUE);
		}
	}
	
	@Test
	public void testProblem2() {
		if (getScale() == 17 && isUnchecked() && scale == 17) {
			runTest(getScaleMetrics(), "problem2", newDecimal(getScaleMetrics(), -9223372036854775807L), 100000000000000000L);
		}
	}

	@Override
	protected BigDecimal expectedResult(BigDecimal a, long b) {
		return a.divide(toBigDecimal(b), mathContextLong128);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, long b) {
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			if (scale == getScale() && RND.nextBoolean()) {
				return a.divideUnscaled(b);
			}
			return a.divideUnscaled(b, scale);
		}
		if (isUnchecked() && RND.nextBoolean()) {
			if (scale == getScale() && RND.nextBoolean()) {
				return a.divideUnscaled(b, getRoundingMode());
			}
			return a.divideUnscaled(b, scale, getRoundingMode());
		}
		if (scale == getScale() && RND.nextBoolean()) {
			return a.divideUnscaled(b, getTruncationPolicy());
		}
		return a.divideUnscaled(b, scale, getTruncationPolicy());
	}
}
