package org.decimal4j.op;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.arithmetic.JDKSupport;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test {@link DecimalArithmetic#parse(String)} via
 * {@link DecimalFactory#valueOf(String)}, {@link MutableDecimal#set(String)}
 * and the static {@code valueOf(String)} methods of the Immutable Decimal
 * implementations.
 */
@RunWith(Parameterized.class)
public class FromStringTest extends AbstractOperandTest {

	public FromStringTest(ScaleMetrics s, RoundingMode mode, DecimalArithmetic arithmetic) {
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
		return "fromString";
	}

	protected String randomStringOperand() {
		final String s = Long.toString(RND.nextLong());
		return toDecimalString(s, RND.nextInt(s.length() + 1));
	}
	private static String toDecimalString(String s, int decimalIndex) {
		if (decimalIndex < 0 || decimalIndex >= s.length()) {
			return s;
		}
		if (decimalIndex == 0 && s.startsWith("-")) {
			decimalIndex++; 
		}
		return s.substring(0, decimalIndex) + "." + s.substring(decimalIndex);
	}

	protected String[] getSpecialStringOperands() {
		final Set<String> values = new LinkedHashSet<String>();
		for (final long value : TestSettings.TEST_CASES.getSpecialValuesFor(getScaleMetrics())) {
			final String s = Long.toString(value);
			for (int i = 0; i <= s.length(); i++) {
				values.add(toDecimalString(s, i));
				if (value > 0) {
					values.add("+" + toDecimalString(s, i));
				}
			}
		}
		return values.toArray(new String[values.size()]);
	}

	@Override
	protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index) {
		runTest(scaleMetrics, "[" + index + "]", randomStringOperand());
	}

	@Override
	protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics) {
		final String[] specialOperands = getSpecialStringOperands();
		for (int i = 0; i < specialOperands.length; i++) {
			runTest(scaleMetrics, "[" + i + "]", specialOperands[i]);
		}
	}

	protected <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, String operand) {

		//expected
		ArithmeticResult<Long> expected;
		try {
			expected = ArithmeticResult.forResult(arithmetic, expectedResult(operand));
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		} catch (IllegalArgumentException e) {
			expected = ArithmeticResult.forException(e);
		}

		//actual
		ArithmeticResult<Long> actual;
		try {
			actual = ArithmeticResult.forResult(actualResult(scaleMetrics, operand));
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		} catch (IllegalArgumentException e) {
			actual = ArithmeticResult.forException(e);
		}

		//assert
		actual.assertEquivalentTo(expected, getClass().getSimpleName() + name + ": " + operation() + " " + operand);
	}

	protected BigDecimal expectedResult(String operand) {
		final BigDecimal value = new BigDecimal(operand).setScale(getScale(), getRoundingMode());
		//check that the conversion does not overflow
		JDKSupport.bigIntegerToLongValueExact(value.unscaledValue());
		return value;
	}

	protected <S extends ScaleMetrics> Decimal<S> actualResult(S scaleMetrics, String operand) {
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
	private <S extends ScaleMetrics> Decimal<S> valueOf(S scaleMetrics, String operand) {
		try {
			final Class<?> clazz = Class.forName("org.decimal4j.immutable.Decimal" + getScale() + "f");
			if (isRoundingDefault() && RND.nextBoolean()) {
				return (Decimal<S>) clazz.getMethod("valueOf", String.class).invoke(null, operand);
			} else {
				return (Decimal<S>) clazz.getMethod("valueOf", String.class, RoundingMode.class).invoke(null, operand, getRoundingMode());
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
