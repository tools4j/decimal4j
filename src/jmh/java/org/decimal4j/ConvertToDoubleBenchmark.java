package org.decimal4j;

import java.io.IOException;
import java.math.RoundingMode;

import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for to-double conversion.
 */
public class ConvertToDoubleBenchmark extends AbstractBenchmark {

	@State(Scope.Benchmark)
	public static class BenchmarkState extends AbstractBenchmarkState {
		@Param({ "HALF_EVEN" , "DOWN"})
		public RoundingMode roundingMode;
		@Param({"Int", "Long"})
		public ValueType valueType;
		@Setup
		public void initParams() {
			super.initParams(roundingMode);
		}
		@Setup
		public void initValues() {
			for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
				values[i] = Values.create(valueType.random(SignType.ALL), 0, scale);
			}
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public void bigDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(bigDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public void immutableDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(immitableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public void mutableDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(mutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public void nativeDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(nativeDecimals(state, state.values[i]));
		}
	}
	
	protected <S extends ScaleMetrics> double bigDecimals(BenchmarkState state, Values<S> values) {
		return values.bigDecimal1.doubleValue();//rounding mode not supported
	}

	protected <S extends ScaleMetrics> double immitableDecimals(BenchmarkState state, Values<S> values) {
		return values.immutable1.doubleValue(state.roundingMode);
	}

	protected <S extends ScaleMetrics> double mutableDecimals(BenchmarkState state, Values<S> values) {
		return values.mutable.set(values.immutable1).doubleValue(state.roundingMode);
	}

	protected <S extends ScaleMetrics> double nativeDecimals(BenchmarkState state, Values<S> values) {
		return state.arithmetics.toDouble(values.unscaled1);//rounding mode is in arithmetics
	}

	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(ConvertToDoubleBenchmark.class);
	}
}
