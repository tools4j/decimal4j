package ch.javasoft.decimal.jmh;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for square root.
 */
public class PowBenchmark extends AbstractBenchmark {
	
	private static final Random RND = new Random();
	
	@State(Scope.Benchmark)
	public static class BenchmarkState extends RoundingBenchmarkState {
		@Param({"Byte", "Short", "Int", "Long"})
		public ValueType valueType;
//		@Param({"2", "5", "10", "20"})
		@Param({"3", "20"})
		public int maxExponent;

		public int[] exponents = new int[OPERATIONS_PER_INVOCATION];
		
		@Setup
		public void initValues() {
			for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
				values[i] = Values.create(valueType.random(SignType.NON_ZERO), 0, scale);
				exponents[i] = RND.nextInt(2*maxExponent + 1) - maxExponent;
			}
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public void bigDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			final int exp = state.exponents[i];
			if (exp >= 0) {
				blackhole.consume(state.values[i].bigDecimal1.pow(exp).setScale(state.scale, state.roundingMode));
			} else {
				blackhole.consume(BigDecimal.ONE.divide(state.values[i].bigDecimal1.pow(-exp), state.scale, state.roundingMode));
			}
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public void immutableDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.values[i].immutable1.pow(state.exponents[i]));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public void mutableDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.values[i].mutable.pow(state.exponents[i]));
		}
	}

	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	@Benchmark
	public void nativeDecimals(BenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(state.arithmetics.pow(state.values[i].unscaled1, state.exponents[i]));
		}
	}
	
	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(PowBenchmark.class);
	}
}
