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
import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.jmh.state.MultiplyExactBenchmarkState;
import org.decimal4j.jmh.state.Values;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for unchecked exact multiplication (result scale is sum-of-factor scales).
 */
public class MultiplyExactBenchmark extends AbstractBenchmark {

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void bigDecimals(MultiplyExactBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(bigDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void immutableDecimals(MultiplyExactBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(immutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void mutableDecimals(MultiplyExactBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(mutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void nativeDecimals(MultiplyExactBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(nativeDecimals(state, state.values[i]));
		}
	}

	private static final <S extends ScaleMetrics> BigDecimal bigDecimals(MultiplyExactBenchmarkState state, Values<S> values) {
		return values.bigDecimal1.multiply(values.bigDecimalExact2);
	}

	private static final <S extends ScaleMetrics> Decimal<?> immutableDecimals(MultiplyExactBenchmarkState state, Values<S> values) {
		return values.immutable1.multiplyExact(values.immutableExact2);
	}

	private static final <S extends ScaleMetrics> Decimal<?> mutableDecimals(MultiplyExactBenchmarkState state, Values<S> values) {
		return values.mutable.set(values.immutable1).multiplyExact(values.immutableExact2);
	}

	private static final <S extends ScaleMetrics> long nativeDecimals(MultiplyExactBenchmarkState state, Values<S> values) {
		return state.checkedArithmetic.multiplyByLong(values.unscaled1, values.unscaled2);
	}
	
	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(MultiplyExactBenchmark.class);
	}
}
