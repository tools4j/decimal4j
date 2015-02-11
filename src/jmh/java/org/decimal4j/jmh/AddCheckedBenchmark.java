package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.arithmetic.JDKSupport;
import org.decimal4j.jmh.state.AddBenchmarkState;
import org.decimal4j.jmh.state.Values;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for checked addition.
 */
public class AddCheckedBenchmark extends AbstractBenchmark {

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void bigDecimals(AddBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(bigDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void immutableDecimals(AddBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(immutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void mutableDecimals(AddBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(mutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void nativeDecimals(AddBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(nativeDecimals(state, state.values[i]));
		}
	}

	private static final <S extends ScaleMetrics> BigDecimal bigDecimals(AddBenchmarkState state, Values<S> values) {
		try {
			final BigDecimal result = values.bigDecimal1.add(values.bigDecimal2, state.mcLong64);
			//check overflow
			JDKSupport.bigIntegerToLongValueExact(result.unscaledValue());
			return result;
		} catch (ArithmeticException e) {
			return null;
		}
	}

	private static final <S extends ScaleMetrics> Decimal<S> immutableDecimals(AddBenchmarkState state, Values<S> values) {
		try {
			return values.immutable1.add(values.immutable2, state.checkedTruncationPolicy);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	private static final <S extends ScaleMetrics> Decimal<S> mutableDecimals(AddBenchmarkState state, Values<S> values) {
		try {
			return values.mutable.set(values.immutable1).add(values.immutable2, state.checkedTruncationPolicy);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	private static final <S extends ScaleMetrics> long nativeDecimals(AddBenchmarkState state, Values<S> values) {
		try {
			return state.checkedArithmetic.add(values.unscaled1, values.unscaled2);
		} catch (ArithmeticException e) {
			return 0;
		}
	}
	
	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(AddCheckedBenchmark.class);
	}
}
