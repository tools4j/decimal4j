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
	public final ImmutableDecimal<S> immutable1;
	public final ImmutableDecimal<S> immutable2;
	public final MutableDecimal<S> mutable;

	private Values(long unscaled1, long unscaled2, int scale, DecimalFactory<S> decimalFactory) {
		this.unscaled1 = unscaled1;
		this.unscaled2 = unscaled2;
		this.bigDecimal1 = BigDecimal.valueOf(unscaled1, scale);
		this.bigDecimal2 = BigDecimal.valueOf(unscaled2, scale);
		this.immutable1 = (ImmutableDecimal<S>) decimalFactory.valueOfUnscaled(unscaled1);
		this.immutable2 = (ImmutableDecimal<S>) decimalFactory.valueOfUnscaled(unscaled2);
		this.mutable = (MutableDecimal<S>) decimalFactory.newMutable();
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