package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Unit test for {@link Decimal#toBigInteger()}, {@link Decimal#toBigIntegerExact()}
 * and {@link Decimal#toBigInteger(RoundingMode)}
 */
@RunWith(Parameterized.class)
public class ToBigIntegerTest extends Abstract1DecimalArgToAnyResultTest<BigInteger> {
	
	private final boolean exact;

	public ToBigIntegerTest(ScaleMetrics scaleMetrics, RoundingMode rounding, boolean exact, DecimalArithmetics arithmetics) {
		super(arithmetics);
		this.exact = exact;
	}

	@Parameters(name = "{index}: scale={0}, rounding={1}, exact={2}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			data.add(new Object[] {s, RoundingMode.DOWN, true, s.getDefaultArithmetics()});
			for (final RoundingMode rounding : UNCHECKED_ROUNDING_MODES) {
				data.add(new Object[] {s, rounding, false, s.getArithmetics(rounding)});
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return exact ? "toBigIntegerExact" : "toBigInteger";
	}
	
	@Override
	protected BigInteger expectedResult(BigDecimal operand) {
		if (exact) {
			return operand.toBigIntegerExact();
		}
		if (isRoundingDown() && rnd.nextBoolean()) {
			return operand.toBigInteger();
		}
		return operand.setScale(0, getRoundingMode()).toBigInteger();
	}
	
	@Override
	protected <S extends ScaleMetrics> BigInteger actualResult(Decimal<S> operand) {
		if (exact) {
			return operand.toBigIntegerExact();
		}
		if (isRoundingDown() && rnd.nextBoolean()) {
			return operand.toBigInteger();
		}
		return operand.toBigInteger(getRoundingMode());
	}
}
