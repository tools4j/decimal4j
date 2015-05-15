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
package org.decimal4j.op.compare;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.ImmutableDecimal;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.base.AbstractDecimal;
import org.decimal4j.op.AbstractDecimalDecimalToDecimalTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link Decimal#min(Decimal)} and {@link Decimal#max(Decimal)} 
 * and all its overload variants.
 */
@RunWith(Parameterized.class)
public class MinMaxTest extends AbstractDecimalDecimalToDecimalTest {
	
	public static enum Op {
		Min, Max
	}
	public static enum Mutability {
		Immutable, Mutable
	}

	public static enum OverloadVariant {
		Decimal, ImmutableMutable, Concrete
	}
	
	private final Op op;
	private final Mutability mutability;
	private final OverloadVariant overloadVariant;
	
	public MinMaxTest(ScaleMetrics scaleMetrics, Op op, Mutability mutability, OverloadVariant overloadVariant, DecimalArithmetic arithmetic) {
		super(arithmetic);
		this.op = Objects.requireNonNull(op, "op cannot be null");
		this.mutability = Objects.requireNonNull(mutability, "mutability cannot be null");
		this.overloadVariant = Objects.requireNonNull(overloadVariant, "overloadVariant cannot be null");
	}

	@Parameters(name = "{index}: scale={0}, op={1}, mutability={2}, overload-variant={3}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final Op op : Op.values()) {
				for (final Mutability mutability : Mutability.values()) {
					for (final OverloadVariant variant : OverloadVariant.values()) {
						data.add(new Object[] {s, op, mutability, variant, s.getDefaultArithmetic()});
					}
				}
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return op.name().toLowerCase();
	}
	
	private boolean isMin() {
		return op == Op.Min;
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> newDecimal(S scaleMetrics,
			long unscaled) {
		final Decimal<S> decimal = super.newDecimal(scaleMetrics, unscaled);
		switch (mutability) {
		case Immutable:
			return decimal.toImmutableDecimal();
		case Mutable:
			return decimal.toMutableDecimal();
		default:
			throw new RuntimeException("unsupported mutability: " + mutability);
		}
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b) {
		return isMin() ? a.min(b) : a.max(b);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		switch (overloadVariant) {
		case Decimal:
			return isMin() ? a.min(b) : a.max(b);
		case ImmutableMutable:
			if (a instanceof ImmutableDecimal && b instanceof ImmutableDecimal) {
				return isMin() ? ((ImmutableDecimal<S>)a).min((ImmutableDecimal<S>)b) : ((ImmutableDecimal<S>)a).max((ImmutableDecimal<S>)b);
			} else if (a instanceof MutableDecimal && b instanceof MutableDecimal) {
				return isMin() ? ((MutableDecimal<S>)a).min((MutableDecimal<S>)b) : ((MutableDecimal<S>)a).max((MutableDecimal<S>)b);
			} else {
				throw new IllegalArgumentException("a and b should have same mutability");
			}
 		case Concrete:
 			final Class<?> type = a.getClass();
 			final Object ret;
			try {
				ret = type.getMethod(operation(), AbstractDecimal.class).invoke(a, b);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
 			@SuppressWarnings("unchecked")//we know it is a Decimal of scale <S>
			final Decimal<S> dec = (Decimal<S>)ret;
 			return dec;
		default:
			throw new RuntimeException("unknown overflow variant: " + overloadVariant);
		}
	}
}
