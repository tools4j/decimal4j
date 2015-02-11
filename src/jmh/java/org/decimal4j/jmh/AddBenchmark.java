package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for unchecked addition.
 */
public class AddBenchmark extends AbstractBinaryOpLongValTruncatingBenchmark {

	@State(Scope.Benchmark)
	public static class AddType extends BenchmarkTypeHolder {
		@Override
		public BenchmarkType getBenchmarkType() {
			return BenchmarkType.Add;
		}
	}

	@Override
	protected <S extends ScaleMetrics> BigDecimal bigDecimals(BenchmarkState state, Values<S> values) {
		return values.bigDecimal1.add(values.bigDecimal2, state.mcLong64);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(BenchmarkState state, Values<S> values) {
		return values.immutable1.add(values.immutable2);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(BenchmarkState state, Values<S> values) {
		return values.mutable.set(values.immutable1).add(values.immutable2);
	}

	@Override
	protected <S extends ScaleMetrics> long nativeDecimals(BenchmarkState state, Values<S> values) {
		return state.arithmetic.add(values.unscaled1, values.unscaled2);
	}
	
	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(AddBenchmark.class);
	}
}
