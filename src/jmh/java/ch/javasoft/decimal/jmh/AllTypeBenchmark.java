package ch.javasoft.decimal.jmh;

import java.math.BigDecimal;

import org.openjdk.jmh.annotations.Benchmark;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.scale.ScaleMetrics;

abstract public class AllTypeBenchmark extends AbstractBenchmark {
	@Benchmark
	public double doubles(BenchmarkState state) {
		return doubles(state, state.values);
	}

	@Benchmark
	public BigDecimal bigDecimals(BenchmarkState state) {
		return bigDecimals(state, state.values);
	}

	@Benchmark
	public Decimal<?> immitableDecimals(BenchmarkState state) {
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
