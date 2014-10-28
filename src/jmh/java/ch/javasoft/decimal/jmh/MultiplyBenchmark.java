package ch.javasoft.decimal.jmh;

import java.math.BigDecimal;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Micro benchmarks for multiplication based on the jmh library.
 */
public class MultiplyBenchmark extends BinaryOpBenchmark {

	@Override
	protected <S extends ScaleMetrics> double doubles(Values<S> values) {
		return values.double1 * values.double2;
	}
	
	@Override
	protected <S extends ScaleMetrics> BigDecimal bigDecimals(Values<S> values) {
		return values.bigDecimal1.multiply(values.bigDecimal2);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(Values<S> values) {
		return values.immutable1.multiply(values.immutable2);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(Values<S> values) {
		return values.mutable.set(values.immutable1).multiply(values.immutable2);
	}

	@Override
	protected <S extends ScaleMetrics> long nativeDecimals(DecimalArithmetics arith, Values<S> values) {
		return arith.multiply(values.unscaled1, values.unscaled2);
	}
}
