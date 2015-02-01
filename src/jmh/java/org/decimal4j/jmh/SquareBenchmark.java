package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;

import org.decimal4j.Decimal;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for unchecked square.
 */
public class SquareBenchmark extends AbstractUnaryOpIntLongValRoundingBenchmark {

	@Override
	protected <S extends ScaleMetrics> BigDecimal bigDecimals(BenchmarkState state, Values<S> values) {
		return values.bigDecimal1.multiply(values.bigDecimal1, state.mcLong64);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(BenchmarkState state, Values<S> values) {
		return values.immutable1.square(state.roundingMode);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(BenchmarkState state, Values<S> values) {
		return values.mutable.set(values.immutable1).square(state.roundingMode);
	}

	@Override
	protected <S extends ScaleMetrics> long nativeDecimals(BenchmarkState state, Values<S> values) {
		return state.arithmetics.square(values.unscaled1);
	}

	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(SquareBenchmark.class);
	}
}
