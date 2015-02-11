package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.arithmetic.JDKSupport;
import org.decimal4j.jmh.state.SubtractBenchmarkState;
import org.decimal4j.jmh.state.Values;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for checked subtraction.
 */
public class SubtractCheckedBenchmark extends AbstractBenchmark {

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void bigDecimals(SubtractBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(bigDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void immutableDecimals(SubtractBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(immutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void mutableDecimals(SubtractBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(mutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void nativeDecimals(SubtractBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(nativeDecimals(state, state.values[i]));
		}
	}

	private static final <S extends ScaleMetrics> BigDecimal bigDecimals(SubtractBenchmarkState state, Values<S> values) {
		try {
			final BigDecimal result = values.bigDecimal1.subtract(values.bigDecimal2, state.mcLong64);
			//check overflow
			JDKSupport.bigIntegerToLongValueExact(result.unscaledValue());
			return result;
		} catch (ArithmeticException e) {
			return null;
		}
	}

	private static final <S extends ScaleMetrics> Decimal<S> immutableDecimals(SubtractBenchmarkState state, Values<S> values) {
		try {
			return values.immutable1.subtract(values.immutable2, state.checkedTruncationPolicy);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	private static final <S extends ScaleMetrics> Decimal<S> mutableDecimals(SubtractBenchmarkState state, Values<S> values) {
		try {
			return values.mutable.set(values.immutable1).subtract(values.immutable2, state.checkedTruncationPolicy);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	private static final <S extends ScaleMetrics> long nativeDecimals(SubtractBenchmarkState state, Values<S> values) {
		try {
			return state.checkedArithmetic.subtract(values.unscaled1, values.unscaled2);
		} catch (ArithmeticException e) {
			return 0;
		}
	}

	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(SubtractCheckedBenchmark.class);
	}
}
