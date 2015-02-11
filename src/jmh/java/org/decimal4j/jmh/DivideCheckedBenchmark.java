package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.arithmetic.JDKSupport;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for checked division.
 */
public class DivideCheckedBenchmark extends AbstractBinaryOpIntLongValRoundingBenchmark {

	@State(Scope.Benchmark)
	public static class DivideType extends BenchmarkTypeHolder {
		@Override
		public BenchmarkType getBenchmarkType() {
			return BenchmarkType.Divide;
		}
	}

	@Override
	protected <S extends ScaleMetrics> BigDecimal bigDecimals(BenchmarkState state, Values<S> values) {
		try {
			final BigDecimal result = values.bigDecimal1.divide(values.bigDecimal2, state.mcLong64);
			//check overflow
			JDKSupport.bigIntegerToLongValueExact(result.unscaledValue());
			return result;
		} catch (ArithmeticException e) {
			return null;
		}
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(BenchmarkState state, Values<S> values) {
		try {
			return values.immutable1.divide(values.immutable2, state.checkedTruncationPolicy);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(BenchmarkState state, Values<S> values) {
		try {
			return values.mutable.set(values.immutable1).divide(values.immutable2, state.checkedTruncationPolicy);
		} catch (ArithmeticException e) {
			return null;
		}
	}

	@Override
	protected <S extends ScaleMetrics> long nativeDecimals(BenchmarkState state, Values<S> values) {
		try {
			return state.checkedArithmetic.divide(values.unscaled1, values.unscaled2);
		} catch (ArithmeticException e) {
			return 0;
		}
	}

	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(DivideCheckedBenchmark.class);
	}
}
