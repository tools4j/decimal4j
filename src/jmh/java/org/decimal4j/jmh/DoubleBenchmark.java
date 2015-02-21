/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.jmh;

import java.io.IOException;

import org.decimal4j.jmh.value.SignType;
import org.decimal4j.jmh.value.ValueType;
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
	public final double add(BenchmarkState state) {
		return state.double1 + state.double2;
	}
	
	@Benchmark
	public final double subtract(BenchmarkState state) {
		return state.double1 - state.double2;
	}

	@Benchmark
	public final double multiply(BenchmarkState state) {
		return state.double1 * state.double2;
	}

	@Benchmark
	public final double divide(BenchmarkState state) {
		return state.double1 / state.double2;
	}

	@Benchmark
	public final double avg(BenchmarkState state) {
		return (state.double1 + state.double2) / 2;
	}

	@Benchmark
	public final double sqrt(BenchmarkState state) {
		return Math.sqrt(state.positive);
	}

	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		new JmhRunner(DoubleBenchmark.class).run();
	}
}
