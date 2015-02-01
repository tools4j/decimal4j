package org.decimal4j.jmh;

import java.io.IOException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for all operations with double values.
 */
public class DoubleBenchmark {

	@State(Scope.Benchmark)
	public static class BenchmarkState {
		public double double1;
		public double double2;
		public double positive;
		
		@Setup
		public void initValues() {
			double1 = ValueType.Long.random(SignType.ALL); 
			double2 = ValueType.Long.random(SignType.NON_ZERO);//for division non-zero 
			positive = ValueType.Long.random(SignType.POSITIVE); 
		}
	}

	@Benchmark
	public double add(BenchmarkState state) {
		return state.double1 + state.double2;
	}
	
	@Benchmark
	public double subtract(BenchmarkState state) {
		return state.double1 - state.double2;
	}

	@Benchmark
	public double multiply(BenchmarkState state) {
		return state.double1 * state.double2;
	}

	@Benchmark
	public double divide(BenchmarkState state) {
		return state.double1 / state.double2;
	}

	@Benchmark
	public double avg(BenchmarkState state) {
		return (state.double1 + state.double2) / 2;
	}

	@Benchmark
	public double sqrt(BenchmarkState state) {
		return Math.sqrt(state.positive);
	}

	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		new JmhRunner(DoubleBenchmark.class).run();
	}
}
