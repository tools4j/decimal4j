package ch.javasoft.decimal.jmh;

import java.io.IOException;
import java.math.BigDecimal;

import org.openjdk.jmh.runner.RunnerException;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Micro benchmarks for unchecked addition.
 */
public class AddBenchmark extends AbstractBinaryOpLongLongTruncatingBenchmark {

	@Override
	protected <S extends ScaleMetrics> BigDecimal bigDecimals(BenchmarkState state, Values<S> values) {
		return values.bigDecimal1.add(values.bigDecimal2, state.mcLong64);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(BenchmarkState state, Values<S> values) {
		return values.immutable1.add(values.immutable2);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(BenchmarkState state, Values<S> values) {
		return values.mutable.set(values.immutable1).add(values.immutable2);
	}

	@Override
	protected <S extends ScaleMetrics> long nativeDecimals(BenchmarkState state, Values<S> values) {
		return state.arithmetics.add(values.unscaled1, values.unscaled2);
	}
	
	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(AddBenchmark.class);
	}
}
