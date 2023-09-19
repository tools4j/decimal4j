/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.op.convert;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.op.AbstractBigDecimalToDecimalTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test {@link DecimalArithmetic#fromBigDecimal(BigDecimal)} via
 * {@link DecimalFactory#valueOf(BigDecimal)}, {@link MutableDecimal#set(BigDecimal)}
 * and the static {@code valueOf(BigDecimal)} methods of the Immutable Decimal
 * implementations.
 */
@RunWith(Parameterized.class)
public class FromBigDecimalTest extends AbstractBigDecimalToDecimalTest {

	public FromBigDecimalTest(ScaleMetrics s, RoundingMode rm, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final RoundingMode rm: TestSettings.UNCHECKED_ROUNDING_MODES) {
				data.add(new Object[] { s, rm, s.getArithmetic(rm) });
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return "fromBigDecimal";
	}

	@Override
	protected BigDecimal expectedResult(BigDecimal operand) {
		final BigDecimal result = operand.setScale(getScale(), getRoundingMode());
		if (result.unscaledValue().bitLength() > 63) {
			throw new IllegalArgumentException("Overflow: " + result);
		}
		return result;
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(S scaleMetrics, BigDecimal operand) {
		switch(RND.nextInt(5)) {
		case 0:
			//Factory, immutable
			if (isRoundingDefault() && RND.nextBoolean()) {
				return getDecimalFactory(scaleMetrics).valueOf(operand);
			} else {
				return getDecimalFactory(scaleMetrics).valueOf(operand, getRoundingMode());
			}
		case 1:
			//Factory, mutable
			if (isRoundingDefault() && RND.nextBoolean()) {
				return getDecimalFactory(scaleMetrics).newMutable().set(operand);
			} else {
				return getDecimalFactory(scaleMetrics).newMutable().set(operand, getRoundingMode());
			}
		case 2:
			//Immutable, valueOf method
			return valueOf(scaleMetrics, operand);
		case 3:
			//Mutable, constructor
			if (isRoundingDefault()) {
				return newMutableInstance(scaleMetrics, operand);
			}//else: fallthrough
		case 4://fallthrough
		default:
			return newDecimal(scaleMetrics, arithmetic.fromBigDecimal(operand));
		}
	}

	private <S extends ScaleMetrics> Decimal<S> newMutableInstance(S scaleMetrics, BigDecimal operand) {
		try {
			@SuppressWarnings("unchecked")
			final Class<Decimal<S>> clazz = (Class<Decimal<S>>) Class.forName(getMutableClassName());
			return clazz.getConstructor(BigDecimal.class).newInstance(operand);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException) e.getTargetException();
			}
			throw new RuntimeException("could not invoke constructor, e=" + e, e);
		} catch (Exception e) {
			throw new RuntimeException("could not invoke constructor, e=" + e, e);
		}
	}

	@SuppressWarnings("unchecked")
	private <S extends ScaleMetrics> Decimal<S> valueOf(S scaleMetrics, BigDecimal operand) {
		try {
			final Class<?> clazz = Class.forName(getImmutableClassName());
			if (isRoundingDefault() && RND.nextBoolean()) {
				return (Decimal<S>) clazz.getMethod("valueOf", BigDecimal.class).invoke(null, operand);
			} else {
				return (Decimal<S>) clazz.getMethod("valueOf", BigDecimal.class, RoundingMode.class).invoke(null, operand, getRoundingMode());
			}
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException)e.getTargetException();
			}
			throw new RuntimeException("could not invoke valueOf method, e=" + e, e);
		} catch (Exception e) {
			throw new RuntimeException("could not invoke valueOf method, e=" + e, e);
		}
	}

}
