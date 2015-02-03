package org.decimal4j.op;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.arithmetic.JDKSupport;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#addSquared(Decimal)}
 */
@RunWith(Parameterized.class)
public class AddSquaredTest extends Abstract2DecimalArgsToDecimalResultTest {
	
	public AddSquaredTest(ScaleMetrics scaleMetrics, TruncationPolicy tp, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final TruncationPolicy tp : TestSettings.POLICIES) {
				final DecimalArithmetic arith = s.getArithmetic(tp);
				data.add(new Object[] {s, tp, arith});
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return "+ square ";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b) {
		//NOTE: by definition we apply rounding and overflow check to the squaring
		final BigDecimal b2 = b.multiply(b).setScale(getScale(), getRoundingMode());
		if (!isUnchecked()) {
			JDKSupport.bigIntegerToLongValueExact(b2.unscaledValue());
		}
		return a.add(b2);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			return a.addSquared(b);
		}
		if (isUnchecked() && RND.nextBoolean()) {
			return a.addSquared(b, getRoundingMode());
		}
		return a.addSquared(b, getTruncationPolicy());
	}
}
