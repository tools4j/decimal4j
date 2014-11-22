package ch.javasoft.decimal.op;

import java.math.BigDecimal;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.arithmetic.JDKSupport;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Unit test for {@link Decimal#addUnscaled(long)}
 */
@RunWith(Parameterized.class)
public class AddUnscaledTest extends AbstractUnscaledTest {
	
	public AddUnscaledTest(ScaleMetrics scaleMetrics, TruncationPolicy tp, int scale, DecimalArithmetics arithmetics) {
		super(scale, arithmetics);
	}

	@Override
	protected String operation() {
		return "+";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, long b) {
		BigDecimal other = BigDecimal.valueOf(b, scale);
		if (scale != getScale()) {
			other = other.setScale(getScale(), getRoundingMode());
			if (!isUnchecked()) {
				//check for overflow
				JDKSupport.bigIntegerToLongValueExact(other.unscaledValue());
			}
		}
		return a.add(other);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, long b) {
		if (scale == getScale() && rnd.nextBoolean()) {
			if (isUnchecked() && rnd.nextBoolean()) {
				return a.addUnscaled(b);
			}
			return a.addUnscaled(b, getOverflowMode());
		}
		if (isUnchecked() && rnd.nextBoolean()) {
			return a.addUnscaled(b, scale, getRoundingMode());
		}
		return a.addUnscaled(b, scale, getTruncationPolicy());
	}
}
