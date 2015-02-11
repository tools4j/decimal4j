package org.decimal4j.jmh.state;

import java.math.RoundingMode;

import org.decimal4j.jmh.value.BenchmarkType;
import org.decimal4j.jmh.value.ValueType;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class SubtractBenchmarkState extends AbstractValueBenchmarkState {
	@Setup
	public void init() {
		initForBinaryOp(BenchmarkType.Subtract, RoundingMode.DOWN, ValueType.Long, ValueType.Long);
	}
}