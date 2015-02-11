package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;

import org.decimal4j.jmh.state.PowBenchmarkState;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for power function.
 */
public class PowBenchmark extends AbstractBenchmark {
	
	private static final MathContext MC_ANSI_X3_274 = new MathContext(18);
	
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public final void bigDecimals(PowBenchmarkState state, Blackhole blackhole) {
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
	public final void bigDecimals_ANSI_X3_274(PowBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.bigDecimals[i].pow(state.exponents[i], MC_ANSI_X3_274).setScale(state.scale, state.roundingMode));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public final void decimalDoubleDecimal(PowBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.arithmetic.fromDouble(Math.pow(state.immutables[i].doubleValue(), state.exponents[i])));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public final void immutableDecimals(PowBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.immutables[i].pow(state.exponents[i]));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public final void mutableDecimals(PowBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.mutables[i].setUnscaled(state.unscaled[i], state.scale).pow(state.exponents[i]));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public final void nativeDecimals(PowBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.arithmetic.pow(state.unscaled[i], state.exponents[i]));
		}
	}
	
	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(PowBenchmark.class);
	}
}
