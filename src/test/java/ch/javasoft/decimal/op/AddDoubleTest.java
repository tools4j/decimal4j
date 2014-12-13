package ch.javasoft.decimal.op;

import java.math.BigDecimal;

import org.junit.Ignore;
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
@Ignore	//FIXME unignore and fix
public class AddDoubleTest extends AbstractDoubleOperandTest {
	
	public AddDoubleTest(ScaleMetrics sm, TruncationPolicy tp, DecimalArithmetics arithmetics) {
		super(sm, tp, arithmetics);
	}

	@Override
	protected String operation() {
		return "+";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, double b) {
		return a.add(toBigDecimal(b));
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, double b) {
		if (isStandardTruncationPolicy() && rnd.nextBoolean()) {
			return a.add(b);
		} else if (isUnchecked() && rnd.nextBoolean()) {
			return a.add(b, getRoundingMode());
		}
		return a.add(b, getTruncationPolicy());
	}
}
