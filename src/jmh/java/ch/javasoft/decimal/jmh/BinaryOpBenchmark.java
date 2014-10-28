package ch.javasoft.decimal.jmh;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;

abstract public class BinaryOpBenchmark extends AbstractBenchmark {
	@Benchmark
	@Warmup(iterations=3)
	@Measurement(iterations=5)
	@Fork(value=0)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public double doubles(BenchmarkState state) {
		return doubles(state.values);
	}

	@Benchmark
	@Warmup(iterations=3)
	@Measurement(iterations=5)
	@Fork(value=0)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public BigDecimal bigDecimals(BenchmarkState state) {
		return bigDecimals(state.values);
	}

	@Benchmark
	@Warmup(iterations=3)
	@Measurement(iterations=5)
	@Fork(value=0)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public Decimal<?> immitableDecimals(BenchmarkState state) {
		return immitableDecimals(state.values);
	}

	@Benchmark
	@Warmup(iterations=3)
	@Measurement(iterations=5)
	@Fork(value=0)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public Decimal<?> mutableDecimals(BenchmarkState state) {
		return mutableDecimals(state.values);
	}

	@Benchmark
	@Warmup(iterations=3)
	@Measurement(iterations=5)
	@Fork(value=0)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public long nativeDecimals(BenchmarkState state) {
		return nativeDecimals(state.arithmetics, state.values);
	}

	abstract protected <S extends ScaleMetrics> double doubles(Values<S> values);
	
	abstract protected <S extends ScaleMetrics> BigDecimal bigDecimals(Values<S> values);

	abstract protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(Values<S> values);

	abstract protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(Values<S> values);

	abstract protected <S extends ScaleMetrics> long nativeDecimals(DecimalArithmetics arith, Values<S> values);

}
