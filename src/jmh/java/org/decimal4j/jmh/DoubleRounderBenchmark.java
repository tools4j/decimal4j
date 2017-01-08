/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 decimal4j (tools4j), Marco Terzer
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
import java.math.RoundingMode;

import org.decimal4j.jmh.state.DoubleRounderBenchmarkState;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.util.DoubleRounder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for to-double conversion.
 */
public class DoubleRounderBenchmark extends AbstractBenchmark {

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void staticRoundDefault(DoubleRounderBenchmarkState state, Blackhole blackhole) {
		final int precision = state.scale;
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(staticRound(state.doubles[i], precision));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void staticRoundDown(DoubleRounderBenchmarkState state, Blackhole blackhole) {
		final int precision = state.scale;
		final RoundingMode roundingMode = state.roundingMode;//DOWN
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(staticRound(state.doubles[i], precision, roundingMode));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void roundDefault(DoubleRounderBenchmarkState state, Blackhole blackhole) {
		final DoubleRounder rounder = state.rounder;
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(round(rounder, state.doubles[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void roundDown(DoubleRounderBenchmarkState state, Blackhole blackhole) {
		final DoubleRounder rounder = state.rounder;
		final RoundingMode roundingMode = state.roundingMode;//DOWN
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(round(rounder, state.doubles[i], roundingMode));
		}
	}

	private static final <S extends ScaleMetrics> double round(DoubleRounder rounder, double value) {
		return rounder.round(value);
	}
	
	private static final <S extends ScaleMetrics> double round(DoubleRounder rounder, double value, RoundingMode roundingMode) {
		return rounder.round(value, roundingMode);
	}

	private static final <S extends ScaleMetrics> double staticRound(double value, int precision) {
		return DoubleRounder.round(value, precision);
	}
	
	private static final <S extends ScaleMetrics> double staticRound(double value, int precision, RoundingMode roundingMode) {
		return DoubleRounder.round(value, precision, roundingMode);
	}

	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(DoubleRounderBenchmark.class);
	}
}
