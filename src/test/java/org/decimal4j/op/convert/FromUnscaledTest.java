/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
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
import org.decimal4j.op.AbstractUnscaledToDecimalTest;
import org.decimal4j.op.util.UnscaledUtil;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test {@link DecimalArithmetic#fromUnscaled(long, int)} via
 * {@link DecimalFactory#valueOfUnscaled(long)} etc.,
 * {@link MutableDecimal#setUnscaled(long)} etc. and the static
 * {@code valueOfUnscaled(...)} methods of the Immutable Decimal
 * implementations.
 */
@RunWith(Parameterized.class)
public class FromUnscaledTest extends AbstractUnscaledToDecimalTest {

	public FromUnscaledTest(ScaleMetrics sm, TruncationPolicy tp, int scale, DecimalArithmetic arithmetic) {
		super(scale, arithmetic);
	}

	@Override
	protected String operation() {
		return "fromUnscaled";
	}

	@Parameters(name = "{index}: {0}, {1}, scale={2}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final TruncationPolicy tp : TestSettings.POLICIES) {
				final DecimalArithmetic arith = s.getArithmetic(tp);
				for (int scale : UnscaledUtil.getScales(s)) {
					data.add(new Object[] { s, tp, scale, arith });
				}
			}
		}
		return data;
	}

	@Override
	protected BigDecimal expectedResult(long operand) {
		return toBigDecimal(operand);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(S scaleMetrics, long operand) {
		if (isUnchecked()) {
			return newDecimal(scaleMetrics, arithmetic.fromUnscaled(operand, scale));
		}
		final boolean noScale = scale == scaleMetrics.getScale() && RND.nextBoolean();
		final DecimalFactory<S> factory = getDecimalFactory(scaleMetrics);
		switch (RND.nextInt(4)) {
		case 0:
			// Factory, immutable
			if (isRoundingDefault() && RND.nextBoolean()) {
				return noScale ? factory.valueOfUnscaled(operand) : factory.valueOfUnscaled(operand, scale);
			} else {
				return factory.valueOfUnscaled(operand, scale, getRoundingMode());
			}
		case 1:
			// Factory, mutable
			final MutableDecimal<S> mutable = factory.newMutable();
			if (isRoundingDefault() && RND.nextBoolean()) {
				return noScale ? mutable.setUnscaled(operand) : mutable.setUnscaled(operand, scale);
			} else {
				return mutable.setUnscaled(operand, scale, getRoundingMode());
			}
		case 2:
			return newMutableInstance(scaleMetrics, operand);
		case 3:// fall through
		default:
			// Immutable, valueOfUnscaled method
			return valueOfUnscaled(scaleMetrics, operand);
		}
	}

	private <S extends ScaleMetrics> Decimal<S> newMutableInstance(S scaleMetrics, long operand) {
		try {
			@SuppressWarnings("unchecked")
			final Class<Decimal<S>> clazz = (Class<Decimal<S>>) Class.forName(getMutableClassName());
			if (isRoundingDefault() && RND.nextBoolean()) {
				if (scale == scaleMetrics.getScale() && RND.nextBoolean()) {
					@SuppressWarnings("unchecked")
					final Decimal<S> result = (Decimal<S>)clazz.getMethod("unscaled", long.class).invoke(null, operand);
					return result;
				} else {
					return clazz.getConstructor(long.class, int.class).newInstance(operand, scale);
				}
			} else {
				return clazz.getConstructor(long.class, int.class, RoundingMode.class)//
						.newInstance(operand, scale, getRoundingMode());
			}
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
	private <S extends ScaleMetrics> Decimal<S> valueOfUnscaled(S scaleMetrics, long operand) {
		try {
			final Class<?> clazz = Class.forName(getImmutableClassName());
			if (isRoundingDefault() && RND.nextBoolean()) {
				if (scale == scaleMetrics.getScale() && RND.nextBoolean()) {
					return (Decimal<S>) clazz.getMethod("valueOfUnscaled", long.class)//
							.invoke(null, operand);
				} else {
					return (Decimal<S>) clazz.getMethod("valueOfUnscaled", long.class, int.class)//
							.invoke(null, operand, scale);
				}
			} else {
				return (Decimal<S>) clazz.getMethod("valueOfUnscaled", long.class, int.class, RoundingMode.class)//
						.invoke(null, operand, scale, getRoundingMode());
			}
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException) e.getTargetException();
			}
			throw new RuntimeException("could not invoke valueOfUnscaled method, e=" + e, e);
		} catch (Exception e) {
			throw new RuntimeException("could not invoke valueOfUnscaled method, e=" + e, e);
		}
	}

}
