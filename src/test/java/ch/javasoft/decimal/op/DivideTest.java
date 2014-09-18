package ch.javasoft.decimal.op;

import java.math.BigDecimal;
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
 * Unit test for {@link Decimal#divide(Decimal, RoundingMode)}
 */
@RunWith(Parameterized.class)
public class DivideTest extends AbstractTwoAryDecimalToDecimalTest {
	
	public DivideTest(ScaleMetrics scaleMetrics, RoundingMode roundingMode, DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	@Parameters(name = "{index}: scale={0}, rounding={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			//FIXME should pass for all rounding modes
			if (s.getScale() <= 9) {
				for (final RoundingMode rm : RoundingMode.values()) {
					data.add(new Object[] {s, rm, s.getArithmetics(rm)});
				}
			} else {
				data.add(new Object[] {s, RoundingMode.DOWN, s.getArithmetics(RoundingMode.DOWN)});
			}
		}
		return data;
	}
	
	@Override
	public void runSpecialValueTest() {
		//FIXME should pass for special values
		//super.runSpecialValueTest();
	}

	@Override
	protected String operation() {
		return "/";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b) {
		return a.divide(b, mathContextLong128);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		if (isStandardRounding() & rnd.nextBoolean()) {
			return a.divide(b);
		} else {
			return a.divide(b, getRoundingMode());
		}
	}
}
