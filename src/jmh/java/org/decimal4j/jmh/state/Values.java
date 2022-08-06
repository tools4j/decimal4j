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
package org.decimal4j.jmh.state;

import java.math.BigDecimal;

import org.decimal4j.api.ImmutableDecimal;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factories;
import org.decimal4j.jmh.value.BenchmarkType;
import org.decimal4j.jmh.value.ValueType;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;

public final class Values<S extends ScaleMetrics> {
	public final long unscaled1;
	public final long unscaled2;
	public final BigDecimal bigDecimal1;
	public final BigDecimal bigDecimal2;
	public final BigDecimal bigDecimalLong2;
	public final BigDecimal bigDecimalExact2;
	public final ImmutableDecimal<S> immutable1;
	public final ImmutableDecimal<S> immutable2;
	public final ImmutableDecimal<?> immutableExact2;
	public final MutableDecimal<S> mutable;
	public final String string1;

	private Values(long unscaled1, long unscaled2, int scale, DecimalFactory<S> decimalFactory) {
		this.unscaled1 = unscaled1;
		this.unscaled2 = unscaled2;
		this.bigDecimal1 = BigDecimal.valueOf(unscaled1, scale);
		this.bigDecimal2 = BigDecimal.valueOf(unscaled2, scale);
		this.bigDecimalLong2 = BigDecimal.valueOf(unscaled2);
		this.bigDecimalExact2 = scale <= 9 ? bigDecimal2 : BigDecimal.valueOf(unscaled2, 18 - scale);
		this.immutable1 = decimalFactory.valueOfUnscaled(unscaled1);
		this.immutable2 = decimalFactory.valueOfUnscaled(unscaled2);
		this.immutableExact2 = scale <= 9 ? immutable2 : Factories.getDecimalFactory(18 - scale).valueOfUnscaled(unscaled2);
		this.mutable = decimalFactory.newMutable();
		this.string1 = immutable1.toString();
	}

	static Values<?> create(BenchmarkType benchmarkType, AbstractValueBenchmarkState benchmarkState, ValueType valueType1, ValueType valueType2) {
		final long value1 = benchmarkType.randomFirst(benchmarkState, valueType1);
		final long value2 = valueType2 == null ? 0 : benchmarkType.randomSecond(benchmarkState, valueType2, value1);
		return create(value1, value2, Scales.getScaleMetrics(benchmarkState.scale));
	}
	private static <S extends ScaleMetrics> Values<S> create(long unscaled1, long unscaled2, S scaleMetrics) {
		return new Values<S>(unscaled1, unscaled2, scaleMetrics.getScale(), Factories.getDecimalFactory(scaleMetrics));
	}
}