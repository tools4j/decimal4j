package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.OverflowMode;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for power function.
 */
public class PowBenchmark extends AbstractBenchmark {
	
	private static final MathContext MC_ANSI_X3_274 = new MathContext(18);
	
	@State(Scope.Benchmark)
	public static class BenchmarkState {
		@Param({ "0", "1", "2", "6", "8", "17" })
		public int scale;
		@Param({"DOWN", "HALF_EVEN"})
		public RoundingMode roundingMode;
		@Param({"3", "10", "20", "100", "1000"})
		public int maxExponent;
		
		public BigDecimal[] bigDecimals = new BigDecimal[OPERATIONS_PER_INVOCATION];
		public Decimal<?>[] immutables = new Decimal<?>[OPERATIONS_PER_INVOCATION];
		public MutableDecimal<?>[] mutables = new MutableDecimal<?>[OPERATIONS_PER_INVOCATION];
		public long[] unscaled = new long[OPERATIONS_PER_INVOCATION];
		public DecimalArithmetic arithmetic;

		public int[] exponents = new int[OPERATIONS_PER_INVOCATION];
		
		@Setup
		public void initValues() {
			initValues(Scales.valueOf(scale));
		}
		private <S extends ScaleMetrics> void initValues(S scaleMetrics) {
			final double maxBase = Math.pow(scaleMetrics.getMaxIntegerValue(), 1.0/maxExponent);
			for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
				final double doubleValue = maxBase * Math.random() * Math.signum(Math.random());
				final long unscaledValue = scaleMetrics.getTruncatingArithmetic(OverflowMode.CHECKED).fromDouble(doubleValue);
				final Decimal<S> value = Factories.valueOf(scaleMetrics).createImmutable(unscaledValue);
				bigDecimals[i] = value.toBigDecimal();
				immutables[i] = value;
				mutables[i] = Factories.valueOf(scaleMetrics).createMutable(unscaledValue);
				unscaled[i] = unscaledValue;
				exponents[i] = maxExponent;
			}
			arithmetic = scaleMetrics.getArithmetic(roundingMode);
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public void bigDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			final int exp = state.exponents[i];
			if (exp >= 0) {
				blackhole.consume(state.bigDecimals[i].pow(exp).setScale(state.scale, state.roundingMode));
			} else {
				blackhole.consume(BigDecimal.ONE.divide(state.bigDecimals[i].pow(-exp), state.scale, state.roundingMode));
			}
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public void bigDecimals_ANSI_X3_274(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.bigDecimals[i].pow(state.exponents[i], MC_ANSI_X3_274).setScale(state.scale, state.roundingMode));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public void decimalDoubleDecimal(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.arithmetic.fromDouble(Math.pow(state.immutables[i].doubleValue(), state.exponents[i])));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public void immutableDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.immutables[i].pow(state.exponents[i]));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public void mutableDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.mutables[i].setUnscaled(state.unscaled[i], state.scale).pow(state.exponents[i]));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public void nativeDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.arithmetic.pow(state.unscaled[i], state.exponents[i]));
		}
	}
	
	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(PowBenchmark.class);
	}
}
