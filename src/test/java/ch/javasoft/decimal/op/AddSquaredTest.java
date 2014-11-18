package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.arithmetic.JDKSupport;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Unit test for {@link Decimal#addSquared(Decimal)}
 */
@RunWith(Parameterized.class)
public class AddSquaredTest extends Abstract2DecimalArgsToDecimalResultTest {
	
	public AddSquaredTest(ScaleMetrics scaleMetrics, TruncationPolicy tp, DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			for (final TruncationPolicy tp : TruncationPolicy.VALUES) {
				final DecimalArithmetics arith = s.getArithmetics(tp);
				if (arith != null) {//FIXME this if can be removed later
					data.add(new Object[] {s, tp, arith});
				}
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
		if (isStandardTruncationPolicy() && rnd.nextBoolean()) {
			return a.addSquared(b);
		}
		if (isUnchecked() && rnd.nextBoolean()) {
			return a.addSquared(b, getRoundingMode());
		}
		return a.addSquared(b, getTruncationPolicy());
	}
}
