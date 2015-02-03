package org.decimal4j.op;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#toBigInteger()}, {@link Decimal#toBigIntegerExact()}
 * and {@link Decimal#toBigInteger(RoundingMode)}
 */
@RunWith(Parameterized.class)
public class ToBigIntegerTest extends Abstract1DecimalArgToAnyResultTest<BigInteger> {
	
	private final boolean exact;

	public ToBigIntegerTest(ScaleMetrics scaleMetrics, RoundingMode rounding, boolean exact, DecimalArithmetic arithmetic) {
		super(arithmetic);
		this.exact = exact;
	}

	@Parameters(name = "{index}: scale={0}, rounding={1}, exact={2}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] {s, RoundingMode.DOWN, true, s.getDefaultArithmetic()});
			for (final RoundingMode rounding : TestSettings.UNCHECKED_ROUNDING_MODES) {
				data.add(new Object[] {s, rounding, false, s.getArithmetic(rounding)});
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
		if (isRoundingDown() && RND.nextBoolean()) {
			return operand.toBigInteger();
		}
		return operand.setScale(0, getRoundingMode()).toBigInteger();
	}
	
	@Override
	protected <S extends ScaleMetrics> BigInteger actualResult(Decimal<S> operand) {
		if (exact) {
			return operand.toBigIntegerExact();
		}
		if (isRoundingDown() && RND.nextBoolean()) {
			return operand.toBigInteger();
		}
		return operand.toBigInteger(getRoundingMode());
	}
}
