package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Unit test for {@link Decimal#byteValue()} and
 * {@link Decimal#byteValueExact()}.
 */
@RunWith(Parameterized.class)
public class ByteValueTest extends AbstractOneAryDecimalToAnyTest<Byte> {

	private final boolean exact;

	public ByteValueTest(ScaleMetrics scaleMetrics, boolean exact, DecimalArithmetics arithmetics) {
		super(arithmetics);
		this.exact = exact;
	}

	@Parameters(name = "{index}: scale={0}, exact={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			data.add(new Object[] { s, true, s.getDefaultArithmetics() });
			data.add(new Object[] { s, false, s.getDefaultArithmetics() });
		}
		return data;
	}

	@Override
	protected String operation() {
		return exact ? "byteValueExact" : "byteValue";
	}

	@Override
	protected Byte expectedResult(BigDecimal operand) {
		return exact ? operand.byteValueExact() : operand.byteValue();
	}

	@Override
	protected <S extends ScaleMetrics> Byte actualResult(Decimal<S> operand) {
		return exact ? operand.byteValueExact() : operand.byteValue();
	}
}
