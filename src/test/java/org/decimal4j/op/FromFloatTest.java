package org.decimal4j.op;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test {@link DecimalArithmetic#fromFloat(float)} via
 * {@link DecimalFactory#valueOf(float)}, {@link MutableDecimal#set(float)} and
 * the static {@code valueOf(float)} methods of the Immutable Decimal
 * implementations.
 */
@RunWith(Parameterized.class)
public class FromFloatTest extends Abstract1FloatArgToDecimalResultTest {

	public FromFloatTest(ScaleMetrics s, RoundingMode mode, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final RoundingMode mode : TestSettings.UNCHECKED_ROUNDING_MODES) {
				final DecimalArithmetic arith = s.getArithmetic(mode);
				data.add(new Object[] { s, mode, arith });
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return "fromFloat";
	}

	@Override
	protected BigDecimal expectedResult(float operand) {
		return FloatAndDoubleUtil.floatToBigDecimal(operand, getScale(), getRoundingMode());
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(S scaleMetrics, float operand) {
		if (RND.nextBoolean()) {
			//Factory, immutable
			if (isRoundingDefault() && RND.nextBoolean()) {
				return Factories.getDecimalFactory(scaleMetrics).valueOf(operand);
			} else {
				return Factories.getDecimalFactory(scaleMetrics).valueOf(operand, getRoundingMode());
			}
		} else if (RND.nextBoolean()) {
			//Factory, mutable
			if (isRoundingDefault() && RND.nextBoolean()) {
				return Factories.getDecimalFactory(scaleMetrics).newMutable().set(operand);
			} else {
				return Factories.getDecimalFactory(scaleMetrics).newMutable().set(operand, getRoundingMode());
			}
		} else {
			//Immutable, valueOf method
			return valueOf(scaleMetrics, operand);
		}
	}

	@SuppressWarnings("unchecked")
	private <S extends ScaleMetrics> Decimal<S> valueOf(S scaleMetrics, float operand) {
		try {
			final Class<?> clazz = Class.forName("org.decimal4j.immutable.Decimal" + getScale() + "f");
			if (isRoundingDefault() && RND.nextBoolean()) {
				return (Decimal<S>) clazz.getMethod("valueOf", float.class).invoke(null, operand);
			} else {
				return (Decimal<S>) clazz.getMethod("valueOf", float.class, RoundingMode.class).invoke(null, operand, getRoundingMode());
			}
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException) e.getTargetException();
			}
			throw new RuntimeException("could not invoke valueOf method, e=" + e, e);
		} catch (Exception e) {
			throw new RuntimeException("could not invoke valueOf method, e=" + e, e);
		}
	}

}
