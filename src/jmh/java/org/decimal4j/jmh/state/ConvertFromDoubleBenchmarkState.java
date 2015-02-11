package org.decimal4j.jmh.state;

import java.math.RoundingMode;

import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.Factories;
import org.decimal4j.jmh.ConvertFromDoubleBenchmark;
import org.decimal4j.jmh.value.DoubleType;
import org.decimal4j.jmh.value.SignType;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ConvertFromDoubleBenchmarkState extends AbstractBenchmarkState {
	@Param({ "HALF_EVEN" , "DOWN"})
	public RoundingMode roundingMode;
	@Param
	public DoubleType doubleType;
	
	public double[] doubles = new double[ConvertFromDoubleBenchmark.OPERATIONS_PER_INVOCATION];
	public MutableDecimal<?> mutable;

	@Setup
	public void init() {
		super.init(roundingMode);
	}
	@Setup
	public void initValues() {
		for (int i = 0; i < ConvertFromDoubleBenchmark.OPERATIONS_PER_INVOCATION; i++) {
			doubles[i] = doubleType.random(SignType.ALL, scale);
			mutable = Factories.getDecimalFactory(scale).newMutable();
		}
	}
}