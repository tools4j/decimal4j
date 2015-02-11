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
			final int exp = state.exponent;
			if (exp >= 0) {
				blackhole.consume(state.values[i].bigDecimal1.pow(exp).setScale(state.scale, state.roundingMode));
			} else {
				blackhole.consume(BigDecimal.ONE.divide(state.values[i].bigDecimal1.pow(-exp), state.scale, state.roundingMode));
			}
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public final void bigDecimals_ANSI_X3_274(PowBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.values[i].bigDecimal1.pow(state.exponent, MC_ANSI_X3_274).setScale(state.scale, state.roundingMode));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public final void decimalDoubleDecimal(PowBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.arithmetic.fromDouble(Math.pow(state.values[i].immutable1.doubleValue(), state.exponent)));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public final void immutableDecimals(PowBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.values[i].immutable1.pow(state.exponent));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public final void mutableDecimals(PowBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.values[i].mutable.setUnscaled(state.values[i].unscaled1, state.scale).pow(state.exponent));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public final void nativeDecimals(PowBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.arithmetic.pow(state.values[i].unscaled1, state.exponent));
		}
	}
	
	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(PowBenchmark.class);
	}
}
