package org.decimal4j.jmh;

import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

abstract public class AbstractBinaryOpIntLongValRoundingBenchmark extends AbstractBenchmark {
	
	@State(Scope.Benchmark)
	public static class BenchmarkState extends RoundingBenchmarkState {
		@Param({"Int", "Long"})
		public ValueType valueType1;
		@Param({"Int", "Long"})
		public ValueType valueType2;
		@Setup
		public void initValues(BenchmarkTypeHolder type) {
			for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
				values[i] = Values.create(type.getBenchmarkType(), scale, valueType1, valueType2);
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

	abstract protected <S extends ScaleMetrics> BigDecimal bigDecimals(BenchmarkState state, Values<S> values);

	abstract protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(BenchmarkState state, Values<S> values);

	abstract protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(BenchmarkState state, Values<S> values);

	abstract protected <S extends ScaleMetrics> long nativeDecimals(BenchmarkState state, Values<S> values);

}
