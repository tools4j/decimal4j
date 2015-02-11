package org.decimal4j.jmh;

import java.io.IOException;

import org.decimal4j.jmh.state.ConvertToDoubleBenchmarkState;
import org.decimal4j.jmh.state.Values;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for to-double conversion.
 */
public class ConvertToDoubleBenchmark extends AbstractBenchmark {

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void bigDecimals(ConvertToDoubleBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(bigDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void immutableDecimals(ConvertToDoubleBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(immitableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void mutableDecimals(ConvertToDoubleBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(mutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void nativeDecimals(ConvertToDoubleBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(nativeDecimals(state, state.values[i]));
		}
	}
	
	private static final <S extends ScaleMetrics> double bigDecimals(ConvertToDoubleBenchmarkState state, Values<S> values) {
		return values.bigDecimal1.doubleValue();//rounding mode not supported
	}

	private static final <S extends ScaleMetrics> double immitableDecimals(ConvertToDoubleBenchmarkState state, Values<S> values) {
		return values.immutable1.doubleValue(state.roundingMode);
	}

	private static final <S extends ScaleMetrics> double mutableDecimals(ConvertToDoubleBenchmarkState state, Values<S> values) {
		return values.mutable.set(values.immutable1).doubleValue(state.roundingMode);
	}

	private static final <S extends ScaleMetrics> double nativeDecimals(ConvertToDoubleBenchmarkState state, Values<S> values) {
		return state.arithmetic.toDouble(values.unscaled1);//rounding mode is in arithmetic
	}

	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(ConvertToDoubleBenchmark.class);
	}
}
