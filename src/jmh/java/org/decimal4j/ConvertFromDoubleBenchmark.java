package org.decimal4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.decimal4j.Decimal;
import org.decimal4j.factory.DecimalFactory;
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
public class ConvertFromDoubleBenchmark extends AbstractBenchmark {

	@State(Scope.Benchmark)
	public static class BenchmarkState extends AbstractBenchmarkState {
		@Param({ "HALF_EVEN" , "DOWN"})
		public RoundingMode roundingMode;
		@Param
		public DoubleType doubleType;
//		@Param({ "HALF_EVEN"})
//		public RoundingMode roundingMode;
//		@Param({"LongBitsToDouble"})
//		public DoubleType doubleType;
		
		public double[] doubles = new double[OPERATIONS_PER_INVOCATION];
		@Setup
		public void initParams() {
			super.initParams(roundingMode);
		}
		@Setup
		public void initValues() {
			for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
				doubles[i] = doubleType.random(SignType.ALL, scale);
				values[i] = Values.create(0, 0, scale);
			}
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public void bigDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(bigDecimals(state, state.doubles[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public void immutableDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(immitableDecimals(state, state.factory, state.doubles[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public void mutableDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(mutableDecimals(state, state.values[i], state.doubles[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public void nativeDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(nativeDecimals(state, state.doubles[i]));
		}
	}
	
	protected <S extends ScaleMetrics> BigDecimal bigDecimals(BenchmarkState state, double value) {
		return BigDecimal.valueOf(value);//rounding mode not supported
	}

	protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(BenchmarkState state, DecimalFactory<S> factory, double value) {
		return factory.createImmutable(state.arithmetics.fromDouble(value));//rounding mode is in arithmetics
	}

	protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(BenchmarkState state, Values<S> values, double value) {
		return values.mutable.set(value, state.roundingMode);
	}

	protected <S extends ScaleMetrics> long nativeDecimals(BenchmarkState state, double value) {
		return state.arithmetics.fromDouble(value);//rounding mode is in arithmetics
	}

	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(ConvertFromDoubleBenchmark.class);
	}
}
