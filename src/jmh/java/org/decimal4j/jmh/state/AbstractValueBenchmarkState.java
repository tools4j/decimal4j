package org.decimal4j.jmh.state;

import java.math.RoundingMode;

import org.decimal4j.jmh.AbstractBenchmark;
import org.decimal4j.jmh.value.BenchmarkType;
import org.decimal4j.jmh.value.ValueType;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
abstract public class AbstractValueBenchmarkState extends AbstractBenchmarkState {
	
	public Values<?>[] values = new Values<?>[AbstractBenchmark.OPERATIONS_PER_INVOCATION];
	
	protected void initForUnaryOp(BenchmarkType benchmarkType, RoundingMode roundingMode, ValueType valueType) {
		init(benchmarkType, roundingMode, valueType, null);
	}
	protected void initForBinaryOp(BenchmarkType benchmarkType, RoundingMode roundingMode, ValueType valueType1, ValueType valueType2) {
		init(benchmarkType, roundingMode, valueType1, valueType2);
	}
	private void init(BenchmarkType benchmarkType, RoundingMode roundingMode, ValueType valueType1, ValueType valueType2) {
		super.init(roundingMode);
		for (int i = 0; i < AbstractBenchmark.OPERATIONS_PER_INVOCATION; i++) {
			this.values[i] = Values.create(benchmarkType, scale, valueType1, valueType2);
		}
	}
}