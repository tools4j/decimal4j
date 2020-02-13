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
import java.math.BigInteger;

import org.decimal4j.api.Decimal;
import org.decimal4j.jmh.state.SqrtBenchmarkState;
import org.decimal4j.jmh.state.Values;
import org.decimal4j.scale.ScaleMetrics;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Micro benchmarks for square root.
 */
public class SqrtBenchmark extends AbstractBenchmark {

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void bigDecimals(SqrtBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(bigDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void immutableDecimals(SqrtBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(immutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void mutableDecimals(SqrtBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(mutableDecimals(state, state.values[i]));
		}
	}

	@Benchmark
	@OperationsPerInvocation(OPERATIONS_PER_INVOCATION)
	public final void nativeDecimals(SqrtBenchmarkState state, Blackhole blackhole) {
		for (int i = 0; i < OPERATIONS_PER_INVOCATION; i++) {
			blackhole.consume(nativeDecimals(state, state.values[i]));
		}
	}

	private static BigDecimal sqrt(BigDecimal bigDecimal) {
		if (bigDecimal.signum() < 0) {
			throw new ArithmeticException("Square root of a negative value: " + bigDecimal);
		}
		final int scale = bigDecimal.scale();
		final BigInteger bigInt = bigDecimal.unscaledValue().multiply(BigInteger.TEN.pow(scale));
		int len = bigInt.bitLength();
		len += len & 0x1;//round up if odd
		BigInteger rem = BigInteger.ZERO;
		BigInteger root = BigInteger.ZERO;
		for (int i = len-1; i >= 0; i-=2) {
			root = root.shiftLeft(1);
			rem = rem.shiftLeft(2);
			final int add = (bigInt.testBit(i) ? 2 : 0) + (bigInt.testBit(i-1) ? 1 : 0);
			rem = rem.add(BigInteger.valueOf(add));
			final BigInteger rootPlusOne = root.add(BigInteger.ONE);
			if (rootPlusOne.compareTo(rem) <= 0) {
				rem = rem.subtract(rootPlusOne);
				root = rootPlusOne.add(BigInteger.ONE);
			}
		}
		return new BigDecimal(root.shiftRight(1), scale);
	}
	
	private static final <S extends ScaleMetrics> BigDecimal bigDecimals(SqrtBenchmarkState state, Values<S> values) {
		return sqrt(values.bigDecimal1);
	}

	private static final <S extends ScaleMetrics> Decimal<S> immutableDecimals(SqrtBenchmarkState state, Values<S> values) {
		return values.immutable1.sqrt(state.roundingMode);
	}

	private static final <S extends ScaleMetrics> Decimal<S> mutableDecimals(SqrtBenchmarkState state, Values<S> values) {
		return values.mutable.set(values.immutable1).sqrt(state.roundingMode);
	}

	private static final <S extends ScaleMetrics> long nativeDecimals(SqrtBenchmarkState state, Values<S> values) {
		return state.arithmetic.sqrt(values.unscaled1);
	}
	
	public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
		run(SqrtBenchmark.class);
	}
}
