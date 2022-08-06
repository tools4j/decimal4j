/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 decimal4j (tools4j), Marco Terzer
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
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.op.AbstractLongValueToDecimalTest;
import org.decimal4j.scale.Scale0f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test {@link DecimalArithmetic#fromLong(long)},
 * {@link DecimalFactory#valueOf(long)} etc., {@link MutableDecimal#set(long)}
 * etc. and the static {@code valueOf(long)} method of the Immutable Decimal
 * implementations.
 */
@RunWith(Parameterized.class)
public class FromLongTest extends AbstractLongValueToDecimalTest {

	public FromLongTest(ScaleMetrics sm, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Override
	protected String operation() {
		return "fromLong";
	}

	@Parameters(name = "{index}: {0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] { s, s.getDefaultArithmetic()});
			data.add(new Object[] { s, s.getDefaultCheckedArithmetic()});
		}
		return data;
	}

	@Override
	protected long[] getSpecialLongOperands() {
		return TestSettings.TEST_CASES.getSpecialValuesFor(Scale0f.INSTANCE);
	}

	@Override
	protected BigDecimal expectedResult(long operand) {
		final BigDecimal result = BigDecimal.valueOf(operand).setScale(getScale());
		if (result.unscaledValue().bitLength() > 63) {
			throw new IllegalArgumentException("Overflow: " + result);
		}
		return result;
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(S scaleMetrics, long operand) {
		if (isUnchecked()) {
			return newDecimal(scaleMetrics, arithmetic.fromLong(operand));
		}
		switch (RND.nextInt(4)) {
		case 0:
			// Factory, immutable
			return getDecimalFactory(scaleMetrics).valueOf(operand);
		case 1:
			// Factory, mutable
			return getDecimalFactory(scaleMetrics).newMutable().set(operand);
		case 2:
			return newMutableInstance(scaleMetrics, operand);
		case 3://fall through
		default:
			// Immutable, valueOf method
			return valueOf(scaleMetrics, operand);
		}
	}

	private <S extends ScaleMetrics> Decimal<S> newMutableInstance(S scaleMetrics, long operand) {
		try {
			@SuppressWarnings("unchecked")
			final Class<Decimal<S>> clazz = (Class<Decimal<S>>) Class.forName(getMutableClassName());
			return clazz.getConstructor(long.class).newInstance(operand);
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
	private <S extends ScaleMetrics> Decimal<S> valueOf(S scaleMetrics, long operand) {
		try {
			final Class<?> clazz = Class.forName(getImmutableClassName());
			return (Decimal<S>) clazz.getMethod("valueOf", long.class).invoke(null, operand);
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
