package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.jmh.state.ConvertFromDoubleBenchmarkState;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for to-double conversion.
 */
public class ConvertFromDoubleBenchmark extends AbstractBenchmark {

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void bigDecimals(ConvertFromDoubleBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(bigDecimals(state, state.doubles[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void immutableDecimals(ConvertFromDoubleBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(immutableDecimals(state, state.factory, state.doubles[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void mutableDecimals(ConvertFromDoubleBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(mutableDecimals(state, state.mutable, state.doubles[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void nativeDecimals(ConvertFromDoubleBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(nativeDecimals(state, state.doubles[i]));
		}
	}
	
	private static final <S extends ScaleMetrics> BigDecimal bigDecimals(ConvertFromDoubleBenchmarkState state, double value) {
		return BigDecimal.valueOf(value);//rounding mode not supported
	}

	private static final <S extends ScaleMetrics> Decimal<S> immutableDecimals(ConvertFromDoubleBenchmarkState state, DecimalFactory<S> factory, double value) {
		return factory.valueOf(value, state.roundingMode);//rounding mode is in arithmetic
	}

	private static final <S extends ScaleMetrics> Decimal<S> mutableDecimals(ConvertFromDoubleBenchmarkState state, MutableDecimal<S> mutable, double value) {
		return mutable.set(value, state.roundingMode);
	}

	private static final <S extends ScaleMetrics> long nativeDecimals(ConvertFromDoubleBenchmarkState state, double value) {
		return state.arithmetic.fromDouble(value);//rounding mode is in arithmetic
	}

	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(ConvertFromDoubleBenchmark.class);
	}
}
