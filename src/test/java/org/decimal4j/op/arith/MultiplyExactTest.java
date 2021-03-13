/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.op.arith;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.exact.Multiplier;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.op.AbstractRandomAndSpecialValueTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.test.ArithmeticResult;
import org.decimal4j.test.TestSettings;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#multiplyExact(Decimal)}
 */
@RunWith(Parameterized.class)
public class MultiplyExactTest extends AbstractRandomAndSpecialValueTest {
	
	private final ScaleMetrics scaleMetrics2;
	
	public MultiplyExactTest(ScaleMetrics scaleMetrics1, ScaleMetrics scaleMetrics2, DecimalArithmetic arithmetic) {
		super(arithmetic);
		this.scaleMetrics2 = Objects.requireNonNull(scaleMetrics2, "scaleMetrics2 cannot be null");
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s1 : TestSettings.SCALES) {
			for (final ScaleMetrics s2 : TestSettings.SCALES) {
				data.add(new Object[] {s1, s2, s1.getDefaultCheckedArithmetic()});
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return "*";
	}

	@Override
	protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index) {
		final Decimal<S> dOpA = randomDecimal(scaleMetrics);
		final Decimal<?> dOpB = randomDecimal(scaleMetrics2);
		runTest(scaleMetrics, "[" + index + "]", dOpA, dOpB);
	}
	
	@Override
	protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics) {
		final long[] specialValues = getSpecialValues(scaleMetrics);
		for (int i = 0; i < specialValues.length; i++) {
			for (int j = 0; j < specialValues.length; j++) {
				final Decimal<S> dOpA = newDecimal(scaleMetrics, specialValues[i]);
				final Decimal<?> dOpB = newDecimal(scaleMetrics2, specialValues[j]);
				runTest(scaleMetrics, "[" + i + ", " + j + "]", dOpA, dOpB);
			}
		}
	}
	
	protected <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, Decimal<S> dOpA, Decimal<?> dOpB) {
		final String messagePrefix = getClass().getSimpleName() + name + ": " + dOpA + " " + operation() + " " + dOpB;

		final int sumOfScales = dOpA.getScale() + dOpB.getScale();
		final BigDecimal bdOpA = toBigDecimal(dOpA);
		final BigDecimal bdOpB = toBigDecimal(dOpB);

		// expected
		ArithmeticResult<Long> expected;
		try {
			if (sumOfScales > Scales.MAX_SCALE) {
				throw new IllegalArgumentException("sum of scales exceeds max scale");
			}
			final BigDecimal exp = expectedResult(bdOpA, bdOpB);
			expected = ArithmeticResult.forResult(arithmetic.deriveArithmetic(sumOfScales), exp);
		} catch (IllegalArgumentException e) {
			expected = ArithmeticResult.forException(e);
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		}

		// actual
		Decimal<?> act = null;
		ArithmeticResult<Long> actual;
		try {
			act = actualResult(dOpA, dOpB);
			actual = ArithmeticResult.forResult(act);
		} catch (IllegalArgumentException e) {
			actual = ArithmeticResult.forException(e);
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		} catch (Error e) {
			throw e;
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

		// assert
		if (!actual.isException()) {
			assertEquals("result scale should be sum of scales", sumOfScales, act.getScale());
		}
		actual.assertEquivalentTo(expected, messagePrefix);
	}

	private BigDecimal expectedResult(BigDecimal a, BigDecimal b) {
		return a.multiply(b);
	}

	private Decimal<?> actualResult(Decimal<?> a, Decimal<?> b) throws Throwable {
		if (a.getScale() + b.getScale() > Scales.MAX_SCALE || RND.nextBoolean()) {
			return a.multiplyExact(b);
		}
		return multiplyExactTyped(a, b);
	}

	private static Decimal<?> multiplyExactTyped(Decimal<?> a, Decimal<?> b) throws Throwable {
		//convert to DecimalXf or MutableDecimalXf
		final Decimal<?> factorA = RND.nextBoolean() ? immutable(a) : mutable(a);
		final Decimal<?> factorB = RND.nextBoolean() ? immutable(b) : mutable(b);
		
		//MultibliableXf multipliable = ...
		final Object multipliable = multiplyExact(factorA);
		assertMultipliableObjectMethods(multipliable, factorA);
		
		try {
			if (b.getScale() == 0 & b.isOne()) {
				//return multipliable.getValue()
				return getMultiplierValue(multipliable);
			}
			if (a.getScale() == b.getScale()) {
				if (a.getScale() + b.getScale() <= Scales.MAX_SCALE && a.equals(b)) {
					//return multipliable.square();
					return exactSquare(multipliable);
				}
			}
			//return multipliable.by(factorB)
			return exactMultiplyBy(factorA, factorB, multipliable);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static Decimal<?> exactMultiplyBy(final Decimal<?> factorA, final Decimal<?> factorB, final Object multipliable) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (factorA.getScale() == factorB.getScale()) {
			final Object result = multipliable.getClass().getMethod("by", Decimal.class).invoke(multipliable, factorB);
			return Decimal.class.cast(result);
		}
		final Object result = multipliable.getClass().getMethod("by", factorB.getClass()).invoke(multipliable, factorB);
		return Decimal.class.cast(result);
	}

	private static Decimal<?> exactSquare(Object multipliable) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		final Object result = multipliable.getClass().getMethod("square").invoke(multipliable);
		return Decimal.class.cast(result);
	}

	private static Decimal<?> getMultiplierValue(final Object multipliable) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		final Object result = multipliable.getClass().getMethod("getValue").invoke(multipliable);
		return Decimal.class.cast(result);
	}
	
	private static void assertMultipliableObjectMethods(final Object multipliable, Decimal<?> value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, Exception {
		//perform some extra asserts here
		Assert.assertEquals("multipliable.toString() should equal value.toString()", value.toString(), multipliable.toString());
		Assert.assertEquals("multipliable.hashCode() should equal value.hashCode()", value.hashCode(), multipliable.hashCode());
		Assert.assertEquals("multipliable should be equal to itself", multipliable, multipliable);
		Assert.assertFalse("multipliable should not be equal to null", multipliable.equals(null));
		Assert.assertNotEquals("multipliable should not be equal to some other type of object", multipliable, "blabla");
		Assert.assertEquals("multipliable should be equal to another instance with same value", multipliable, multiplyExact(mutable(value)));
	}

	private static Object multiplyExact(final Decimal<?> factorA) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (RND.nextBoolean()) {
			//return factorA.multiplyExact()
			return factorA.getClass().getMethod("multiplyExact").invoke(factorA);
		} else {
			//return Multiplier.multiplyExact(factorA)
			return Multiplier.class.getMethod("multiplyExact", factorA.getClass()).invoke(null, factorA);
		}
	}
	
	private static Decimal<?> immutable(Decimal<?> value) throws Exception {
		final String className = Decimal0f.class.getName().replace("0", String.valueOf(value.getScale()));
		final Class<?> clazz = Class.forName(className);
		final Object instance = clazz.getMethod("valueOf", Decimal.class).invoke(null, value);
		return Decimal.class.cast(instance);
	}
	private static Decimal<?> mutable(Decimal<?> value) throws Exception {
		final String className = MutableDecimal0f.class.getName().replace("0", String.valueOf(value.getScale()));
		final Class<?> clazz = Class.forName(className);
		final Object instance = clazz.getConstructor(Decimal.class).newInstance(value);
		return Decimal.class.cast(instance);
	}
	
	
}
