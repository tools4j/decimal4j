package ch.javasoft.decimal.jmh;

import java.math.BigDecimal;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.truncate.OverflowMode;

/**
 * Micro benchmarks for multiplication based on the jmh library.
 */
public class DivideBenchmark extends AbstractStandardBenchmark {

	@Override
	protected <S extends ScaleMetrics> double doubles(StandardBenchmarkState state, Values<S> values) {
		return values.double1 / values.double2;
	}
	
	@Override
	protected <S extends ScaleMetrics> BigDecimal bigDecimals(StandardBenchmarkState state, Values<S> values) {
		final BigDecimal result = values.bigDecimal1.divide(values.bigDecimal2, state.mcLong64);
		if (state.overflowMode == OverflowMode.CHECKED) {
			//check overflow
			result.unscaledValue().longValueExact();
		}
		return result;
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> immitableDecimals(StandardBenchmarkState state, Values<S> values) {
		return values.immutable1.divide(values.immutable2, state.truncationPolicy);
	}

	@Override
	protected <S extends ScaleMetrics> Decimal<S> mutableDecimals(StandardBenchmarkState state, Values<S> values) {
		return values.mutable.set(values.immutable1).divide(values.immutable2, state.truncationPolicy);
	}

	@Override
	protected <S extends ScaleMetrics> long nativeDecimals(StandardBenchmarkState state, Values<S> values) {
		return state.arithmetics.divide(values.unscaled1, values.unscaled2);
	}
}
