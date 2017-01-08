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
package org.decimal4j.jmh.state;

import java.math.RoundingMode;

import org.decimal4j.jmh.AbstractBenchmark;
import org.decimal4j.jmh.value.BenchmarkType;
import org.decimal4j.jmh.value.ValueType;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
abstract public class AbstractValueBenchmarkState extends AbstractBenchmarkState {
	
	public final Values<?>[] values = new Values<?>[AbstractBenchmark.OPERATIONS_PER_INVOCATION];
	
	protected void initForUnaryOp(BenchmarkType benchmarkType, RoundingMode roundingMode, ValueType valueType) {
		init(benchmarkType, roundingMode, valueType, null);
	}
	protected void initForBinaryOp(BenchmarkType benchmarkType, RoundingMode roundingMode, ValueType valueType1, ValueType valueType2) {
		init(benchmarkType, roundingMode, valueType1, valueType2);
	}
	private void init(BenchmarkType benchmarkType, RoundingMode roundingMode, ValueType valueType1, ValueType valueType2) {
		super.init(roundingMode);
		for (int i = 0; i < AbstractBenchmark.OPERATIONS_PER_INVOCATION; i++) {
			this.values[i] = Values.create(benchmarkType, this, valueType1, valueType2);
		}
	}
}