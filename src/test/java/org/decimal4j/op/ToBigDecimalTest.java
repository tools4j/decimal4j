package org.decimal4j.op;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.Decimal;
import org.decimal4j.arithmetic.DecimalArithmetics;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#toBigDecimal()} and {@link Decimal#toBigDecimal(int, RoundingMode)} 
 */
@RunWith(Parameterized.class)
public class ToBigDecimalTest extends Abstract1DecimalArgToAnyResultTest<BigDecimal> {

	private final Integer newScale;
	
	public ToBigDecimalTest(ScaleMetrics scaleMetrics, Integer newScale, RoundingMode rounding, DecimalArithmetics arithmetics) {
		super(arithmetics);
		this.newScale = newScale;
	}

	@Parameters(name = "{index}: scale={0}, newScale={1}, rounding={2}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] { s, null, RoundingMode.DOWN, s.getDefaultArithmetics() });
			for (int scale = -100; scale < 100; scale++) {
				for (final RoundingMode rounding : TestSettings.UNCHECKED_ROUNDING_MODES) {
					data.add(new Object[] { s, scale, rounding, s.getArithmetics(rounding) });
				}
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return "toBigDecimal";
	}
	
	@Override
	protected int getRandomTestCount() {
		return 100;
	}

	@Override
	protected BigDecimal expectedResult(BigDecimal operand) {
		if (newScale == null) {
			return operand;
		}
		return operand.setScale(newScale, getRoundingMode());
	}

	@Override
	protected <S extends ScaleMetrics> BigDecimal actualResult(Decimal<S> operand) {
		if (newScale == null) {
			return operand.toBigDecimal();
		}
		return operand.toBigDecimal(newScale, getRoundingMode());
	}
}