package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factories;
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
		public MutableDecimal<?> mutable;

		@Setup
		public void initParams() {
			super.initParams(roundingMode);
		}
		@Setup
		public void initValues() {
			for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
				doubles[i] = doubleType.random(SignType.ALL, scale);
				mutable = Factories.getDecimalFactory(scale).newMutable();
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
			blackhole.consume(immutableDecimals(state, state.factory, state.doubles[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public void mutableDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(mutableDecimals(state, state.mutable, state.doubles[i]));
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

	protected <S extends ScaleMetrics> Decimal<S> immutableDecimals(BenchmarkState state, DecimalFactory<S> factory, double value) {
		return factory.valueOf(value, state.roundingMode);//rounding mode is in arithmetic
	}

	protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(BenchmarkState state, MutableDecimal<S> mutable, double value) {
		return mutable.set(value, state.roundingMode);
	}

	protected <S extends ScaleMetrics> long nativeDecimals(BenchmarkState state, double value) {
		return state.arithmetic.fromDouble(value);//rounding mode is in arithmetic
	}

	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(ConvertFromDoubleBenchmark.class);
	}
}
