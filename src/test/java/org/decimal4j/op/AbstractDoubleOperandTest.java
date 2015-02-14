package org.decimal4j.op;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.runners.Parameterized.Parameters;

/**
 * Base class for unit tests with a double operand.
 */
abstract public class AbstractDoubleOperandTest extends Abstract1DecimalArg1DoubleArgToDecimalResultTest {
	
	protected final MathContext MATH_CONTEXT_DOUBLE_TO_LONG_64 = new MathContext(19, RoundingMode.HALF_EVEN);

	public AbstractDoubleOperandTest(ScaleMetrics s, TruncationPolicy tp, DecimalArithmetic arithmetic) {
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
	
	protected BigDecimal toBigDecimal(double operand) {
		return FloatAndDoubleUtil.doubleToBigDecimal(operand, getScale(), getRoundingMode()).setScale(getScale(), getRoundingMode());
	}
	
}
