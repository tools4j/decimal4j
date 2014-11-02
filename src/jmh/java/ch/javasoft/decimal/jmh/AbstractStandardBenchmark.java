package ch.javasoft.decimal.jmh;

import java.math.BigDecimal;

import org.openjdk.jmh.annotations.Benchmark;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.scale.ScaleMetrics;

abstract public class AbstractStandardBenchmark extends AbstractBenchmark {
	@Benchmark
	public double doubles(StandardBenchmarkState state) {
		return doubles(state, state.values);
	}

	@Benchmark
	public BigDecimal bigDecimals(StandardBenchmarkState state) {
		return bigDecimals(state, state.values);
	}

	@Benchmark
	public Decimal<?> immutableDecimals(StandardBenchmarkState state) {
		return immitableDecimals(state, state.values);
	}

	@Benchmark
	public Decimal<?> mutableDecimals(StandardBenchmarkState state) {
		return mutableDecimals(state, state.values);
	}

	@Benchmark
	public long nativeDecimals(StandardBenchmarkState state) {
		return nativeDecimals(state, state.values);
	}

	abstract protected <S extends ScaleMetrics> double doubles(StandardBenchmarkState state, Values<S> values);
	
	abstract protected <S extends ScaleMetrics> BigDecimal bigDecimals(StandardBenchmarkState state, Values<S> values);

	abstract protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(StandardBenchmarkState state, Values<S> values);

	abstract protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(StandardBenchmarkState state, Values<S> values);

	abstract protected <S extends ScaleMetrics> long nativeDecimals(StandardBenchmarkState state, Values<S> values);

}
