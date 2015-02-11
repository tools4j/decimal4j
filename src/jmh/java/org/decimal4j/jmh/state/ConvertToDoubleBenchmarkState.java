package org.decimal4j.jmh.state;

import java.math.RoundingMode;

import org.decimal4j.jmh.value.BenchmarkType;
import org.decimal4j.jmh.value.ValueType;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ConvertToDoubleBenchmarkState extends AbstractValueBenchmarkState {
	@Param({ "HALF_EVEN" , "DOWN"})
	public RoundingMode roundingMode;
	@Param({"Int", "Long"})
	public ValueType valueType;
	@Setup
	public void init() {
		super.initForUnaryOp(BenchmarkType.ConvertToDouble, roundingMode, valueType);
	}
}