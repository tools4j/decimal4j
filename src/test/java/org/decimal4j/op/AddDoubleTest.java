package org.decimal4j.op;

import java.math.BigDecimal;

import org.decimal4j.Decimal;
import org.decimal4j.arithmetic.DecimalArithmetics;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Unit test for {@link Decimal#add(double)} etc. 
 */
@RunWith(Parameterized.class)
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
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			return a.add(b);
		} else if (isUnchecked() && RND.nextBoolean()) {
			return a.add(b, getRoundingMode());
		}
		return a.add(b, getTruncationPolicy());
	}
}
