package org.decimal4j.jmh.state;

import java.math.RoundingMode;

import org.decimal4j.jmh.value.BenchmarkType;
import org.decimal4j.jmh.value.ValueType;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class PowBenchmarkState extends AbstractValueBenchmarkState {
	@Param({"Int", "Long"})
	public ValueType valueType;
	@Param({"DOWN", "HALF_EVEN"})
	public RoundingMode roundingMode;
	@Param({"3", "10", "20", "100", "1000"})
	public int exponent;
	
	@Setup
	public void init() {
		initForUnaryOp(BenchmarkType.Pow, roundingMode, valueType);
	}
}