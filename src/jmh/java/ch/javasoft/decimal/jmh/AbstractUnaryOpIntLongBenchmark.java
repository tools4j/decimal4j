package ch.javasoft.decimal.jmh;

import java.math.BigDecimal;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.scale.ScaleMetrics;

abstract public class AbstractUnaryOpIntLongBenchmark extends AbstractBenchmark {
	@State(Scope.Benchmark)
	public static class BenchmarkState extends AbstractBenchmarkState {
		@Param({"Int", "Long"})
		public ValueType valueType;
		@Setup
		public void initValues() {
			values = Values.create(valueType.random(SignType.ALL), 0, scale);
		}
	}
	@Benchmark
	public double doubles(BenchmarkState state) {
		return doubles(state, state.values);
	}

	@Benchmark
	public BigDecimal bigDecimals(BenchmarkState state) {
		return bigDecimals(state, state.values);
	}

	@Benchmark
	public Decimal<?> immutableDecimals(BenchmarkState state) {
		return immitableDecimals(state, state.values);
	}

	@Benchmark
	public Decimal<?> mutableDecimals(BenchmarkState state) {
		return mutableDecimals(state, state.values);
	}

	@Benchmark
	public long nativeDecimals(BenchmarkState state) {
		return nativeDecimals(state, state.values);
	}

	abstract protected <S extends ScaleMetrics> double doubles(BenchmarkState state, Values<S> values);
	
	abstract protected <S extends ScaleMetrics> BigDecimal bigDecimals(BenchmarkState state, Values<S> values);

	abstract protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(BenchmarkState state, Values<S> values);

	abstract protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(BenchmarkState state, Values<S> values);

	abstract protected <S extends ScaleMetrics> long nativeDecimals(BenchmarkState state, Values<S> values);

}
