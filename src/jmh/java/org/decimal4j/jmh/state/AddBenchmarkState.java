package org.decimal4j.jmh.state;

import java.math.RoundingMode;

import org.decimal4j.jmh.value.BenchmarkType;
import org.decimal4j.jmh.value.ValueType;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class AddBenchmarkState extends AbstractValueBenchmarkState {
	@Setup
	public void init() {
		initForBinaryOp(BenchmarkType.Add, RoundingMode.DOWN, ValueType.Long, ValueType.Long);
	}
}