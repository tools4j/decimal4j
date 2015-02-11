package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.arithmetic.JDKSupport;
import org.decimal4j.jmh.state.MultiplyBenchmarkState;
import org.decimal4j.jmh.state.Values;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for checked multiplication.
 */
public class MultiplyCheckedBenchmark extends AbstractBenchmark {

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void bigDecimals(MultiplyBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(bigDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void immutableDecimals(MultiplyBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(immutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void mutableDecimals(MultiplyBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(mutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void nativeDecimals(MultiplyBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(nativeDecimals(state, state.values[i]));
		}
	}

	private static final <S extends ScaleMetrics> BigDecimal bigDecimals(MultiplyBenchmarkState state, Values<S> values) {
		try {
			final BigDecimal result = values.bigDecimal1.multiply(values.bigDecimal2, state.mcLong64);
			//check overflow
			JDKSupport.bigIntegerToLongValueExact(result.unscaledValue());
			return result;
		} catch (ArithmeticException e) {
			return null;
		}
	}

	private static final <S extends ScaleMetrics> Decimal<S> immutableDecimals(MultiplyBenchmarkState state, Values<S> values) {
		try {
			return values.immutable1.multiply(values.immutable2, state.checkedTruncationPolicy);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	private static final <S extends ScaleMetrics> Decimal<S> mutableDecimals(MultiplyBenchmarkState state, Values<S> values) {
		try {
			return values.mutable.set(values.immutable1).multiply(values.immutable2, state.checkedTruncationPolicy);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	private static final <S extends ScaleMetrics> long nativeDecimals(MultiplyBenchmarkState state, Values<S> values) {
		try {
			return state.checkedArithmetic.multiply(values.unscaled1, values.unscaled2);
		} catch (ArithmeticException e) {
			return 0;
		}
	}
	
	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(MultiplyCheckedBenchmark.class);
	}
}
