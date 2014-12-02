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
public class MultiplyUnscaledTest extends AbstractUnscaledTest {
	
	public MultiplyUnscaledTest(ScaleMetrics scaleMetrics, TruncationPolicy tp, int scale, DecimalArithmetics arithmetics) {
		super(scale, arithmetics);
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
		if (isStandardTruncationPolicy() && rnd.nextBoolean()) {
			if (scale == getScale() && rnd.nextBoolean()) {
				return a.multiplyUnscaled(b);
			}
			return a.multiplyUnscaled(b, scale);
		}
		if (isUnchecked() && rnd.nextBoolean()) {
			if (scale == getScale() && rnd.nextBoolean()) {
				return a.multiplyUnscaled(b, getRoundingMode());
			}
			return a.multiplyUnscaled(b, scale, getRoundingMode());
		}
		if (scale == getScale() && rnd.nextBoolean()) {
			return a.multiplyUnscaled(b, getTruncationPolicy());
		}
		return a.multiplyUnscaled(b, scale, getTruncationPolicy());
	}
}
