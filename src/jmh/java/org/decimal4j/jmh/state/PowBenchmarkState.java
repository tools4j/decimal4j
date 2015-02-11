package org.decimal4j.jmh.state;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.Factories;
import org.decimal4j.jmh.PowBenchmark;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.OverflowMode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class PowBenchmarkState {
	@Param({ "0", "1", "2", "6", "8", "17" })
	public int scale;
	@Param({"DOWN", "HALF_EVEN"})
	public RoundingMode roundingMode;
	@Param({"3", "10", "20", "100", "1000"})
	public int maxExponent;
	
	public BigDecimal[] bigDecimals = new BigDecimal[PowBenchmark.OPERATIONS_PER_INVOCATION];
	public Decimal<?>[] immutables = new Decimal<?>[PowBenchmark.OPERATIONS_PER_INVOCATION];
	public MutableDecimal<?>[] mutables = new MutableDecimal<?>[PowBenchmark.OPERATIONS_PER_INVOCATION];
	public long[] unscaled = new long[PowBenchmark.OPERATIONS_PER_INVOCATION];
	public DecimalArithmetic arithmetic;

	public int[] exponents = new int[PowBenchmark.OPERATIONS_PER_INVOCATION];
	
	@Setup
	public void initValues() {
		initValues(Scales.getScaleMetrics(scale));
	}
	private <S extends ScaleMetrics> void initValues(S scaleMetrics) {
		final double maxBase = Math.pow(scaleMetrics.getMaxIntegerValue(), 1.0/maxExponent);
		for (int i = 0; i < PowBenchmark.OPERATIONS_PER_INVOCATION; i++) {
			final double doubleValue = maxBase * Math.random() * Math.signum(Math.random());
			final long unscaledValue = scaleMetrics.getTruncatingArithmetic(OverflowMode.CHECKED).fromDouble(doubleValue);
			final Decimal<S> value = Factories.getDecimalFactory(scaleMetrics).valueOfUnscaled(unscaledValue);
			bigDecimals[i] = value.toBigDecimal();
			immutables[i] = value;
			mutables[i] = Factories.getDecimalFactory(scaleMetrics).newMutable().setUnscaled(unscaledValue);
			unscaled[i] = unscaledValue;
			exponents[i] = maxExponent;
		}
		arithmetic = scaleMetrics.getArithmetic(roundingMode);
	}
}