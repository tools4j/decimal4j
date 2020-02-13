/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 decimal4j (tools4j), Marco Terzer
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
import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.jmh.state.MultiplyBenchmarkState;
import org.decimal4j.jmh.state.Values;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for unchecked multiplication.
 */
public class MultiplyBenchmark extends AbstractBenchmark {

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void bigDecimals(MultiplyBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(bigDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void immutableDecimals(MultiplyBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(immutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void mutableDecimals(MultiplyBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(mutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void nativeDecimals(MultiplyBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(nativeDecimals(state, state.values[i]));
		}
	}

	private static final <S extends ScaleMetrics> BigDecimal bigDecimals(MultiplyBenchmarkState state, Values<S> values) {
		return values.bigDecimal1.multiply(values.bigDecimal2, state.mcLong64);
	}

	private static final <S extends ScaleMetrics> Decimal<S> immutableDecimals(MultiplyBenchmarkState state, Values<S> values) {
		return values.immutable1.multiply(values.immutable2, state.roundingMode);
	}

	private static final <S extends ScaleMetrics> Decimal<S> mutableDecimals(MultiplyBenchmarkState state, Values<S> values) {
		return values.mutable.set(values.immutable1).multiply(values.immutable2, state.roundingMode);
	}

	private static final <S extends ScaleMetrics> long nativeDecimals(MultiplyBenchmarkState state, Values<S> values) {
		return state.arithmetic.multiply(values.unscaled1, values.unscaled2);
	}
	
	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(MultiplyBenchmark.class);
	}
}
