package org.decimal4j;

import java.io.IOException;
import java.math.BigDecimal;

import org.decimal4j.Decimal;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for unchecked multiplication.
 */
public class MultiplyBenchmark extends AbstractBinaryOpIntLongValRoundingBenchmark {

	@Override
	protected <S extends ScaleMetrics> BigDecimal bigDecimals(BenchmarkState state, Values<S> values) {
		return values.bigDecimal1.multiply(values.bigDecimal2, state.mcLong64);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(BenchmarkState state, Values<S> values) {
		return values.immutable1.multiply(values.immutable2, state.roundingMode);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(BenchmarkState state, Values<S> values) {
		return values.mutable.set(values.immutable1).multiply(values.immutable2, state.roundingMode);
	}

	@Override
	protected <S extends ScaleMetrics> long nativeDecimals(BenchmarkState state, Values<S> values) {
		return state.arithmetics.multiply(values.unscaled1, values.unscaled2);
	}
	
	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(MultiplyBenchmark.class);
	}
}
