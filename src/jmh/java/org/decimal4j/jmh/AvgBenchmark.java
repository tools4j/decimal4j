package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for average.
 */
public class AvgBenchmark extends AbstractBinaryOpLongValRoundingBenchmark {
	
	private static final BigDecimal TWO = BigDecimal.valueOf(2);

	@State(Scope.Benchmark)
	public static class AvgType extends BenchmarkTypeHolder {
		@Override
		public BenchmarkType getBenchmarkType() {
			return BenchmarkType.Avg;
		}
	}

	@Override
	protected <S extends ScaleMetrics> BigDecimal bigDecimals(BenchmarkState state, Values<S> values) {
		return values.bigDecimal1.add(values.bigDecimal2).divide(TWO, state.mcLong64);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(BenchmarkState state, Values<S> values) {
		return values.immutable1.avg(values.immutable2, state.roundingMode);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(BenchmarkState state, Values<S> values) {
		return values.mutable.set(values.immutable1).avg(values.immutable2, state.roundingMode);
	}

	@Override
	protected <S extends ScaleMetrics> long nativeDecimals(BenchmarkState state, Values<S> values) {
		return state.arithmetic.avg(values.unscaled1, values.unscaled2);
	}
	
	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(AvgBenchmark.class);
	}
}
