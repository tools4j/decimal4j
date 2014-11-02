package ch.javasoft.decimal.jmh;

import java.math.BigDecimal;

import org.openjdk.jmh.annotations.Benchmark;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.scale.ScaleMetrics;

abstract public class AbstractPositiveOnlyBenchmark extends AbstractBenchmark {
	@Benchmark
	public double doubles(PositiveOnlyBenchmarkState state) {
		return doubles(state, state.values);
	}

	@Benchmark
	public BigDecimal bigDecimals(PositiveOnlyBenchmarkState state) {
		return bigDecimals(state, state.values);
	}

	@Benchmark
	public Decimal<?> immutableDecimals(PositiveOnlyBenchmarkState state) {
		return immitableDecimals(state, state.values);
	}

	@Benchmark
	public Decimal<?> mutableDecimals(PositiveOnlyBenchmarkState state) {
		return mutableDecimals(state, state.values);
	}

	@Benchmark
	public long nativeDecimals(PositiveOnlyBenchmarkState state) {
		return nativeDecimals(state, state.values);
	}

	abstract protected <S extends ScaleMetrics> double doubles(PositiveOnlyBenchmarkState state, Values<S> values);
	
	abstract protected <S extends ScaleMetrics> BigDecimal bigDecimals(PositiveOnlyBenchmarkState state, Values<S> values);

	abstract protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(PositiveOnlyBenchmarkState state, Values<S> values);

	abstract protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(PositiveOnlyBenchmarkState state, Values<S> values);

	abstract protected <S extends ScaleMetrics> long nativeDecimals(PositiveOnlyBenchmarkState state, Values<S> values);

}
