package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.jmh.state.SquareBenchmarkState;
import org.decimal4j.jmh.state.Values;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for unchecked square.
 */
public class SquareBenchmark extends AbstractBenchmark {

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void bigDecimals(SquareBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(bigDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void immutableDecimals(SquareBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(immutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void mutableDecimals(SquareBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(mutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void nativeDecimals(SquareBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(nativeDecimals(state, state.values[i]));
		}
	}

	private static final <S extends ScaleMetrics> BigDecimal bigDecimals(SquareBenchmarkState state, Values<S> values) {
		return values.bigDecimal1.multiply(values.bigDecimal1, state.mcLong64);
	}

	private static final <S extends ScaleMetrics> Decimal<S> immutableDecimals(SquareBenchmarkState state, Values<S> values) {
		return values.immutable1.square(state.roundingMode);
	}

	private static final <S extends ScaleMetrics> Decimal<S> mutableDecimals(SquareBenchmarkState state, Values<S> values) {
		return values.mutable.set(values.immutable1).square(state.roundingMode);
	}

	private static final <S extends ScaleMetrics> long nativeDecimals(SquareBenchmarkState state, Values<S> values) {
		return state.arithmetic.square(values.unscaled1);
	}

	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(SquareBenchmark.class);
	}
}
