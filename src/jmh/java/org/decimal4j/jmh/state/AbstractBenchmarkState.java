package org.decimal4j.jmh.state;

import java.math.MathContext;
import java.math.RoundingMode;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
abstract public class AbstractBenchmarkState {
	//		@Param({ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18" })
	//		@Param({ "0", "6", "9", "17", "18" })
	@Param({ "0", "6", "17" })
	public int scale;

	public RoundingMode roundingMode;
	public DecimalArithmetic arithmetic;
	public DecimalFactory<?> factory;
	public MathContext mcLong64;
	public MathContext mcLong128;

	//for checked benchmarks only
	public TruncationPolicy checkedTruncationPolicy;
	public DecimalArithmetic checkedArithmetic;

	protected void init(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
		this.arithmetic = Scales.getScaleMetrics(scale).getArithmetic(roundingMode);
		this.factory = Factories.getDecimalFactory(scale);
		this.checkedTruncationPolicy = OverflowMode.CHECKED.getTruncationPolicyFor(roundingMode);
		this.checkedArithmetic = Scales.getScaleMetrics(scale).getArithmetic(checkedTruncationPolicy);
		this.mcLong64 = new MathContext(19, roundingMode);
		this.mcLong128 = new MathContext(39, roundingMode);
	}
}