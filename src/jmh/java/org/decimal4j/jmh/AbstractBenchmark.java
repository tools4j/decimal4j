package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.ImmutableDecimal;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Base class for micro benchmarks based on the jmh library.
 */
abstract public class AbstractBenchmark {

	public static final int OPERATIONS_PER_INVOCATION = 100;

	@State(Scope.Benchmark)
	abstract public static class BenchmarkTypeHolder {
		abstract public BenchmarkType getBenchmarkType();
	}
	@State(Scope.Benchmark)
	abstract public static class AbstractBenchmarkState {
		//		@Param({ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18" })
		//		@Param({ "0", "6", "9", "17", "18" })
		@Param({ "0", "6", "17" })
		public int scale;

		public DecimalArithmetic arithmetic;
		public DecimalFactory<?> factory;
		public MathContext mcLong64;
		public MathContext mcLong128;

		public Values<?>[] values = new Values<?>[OPERATIONS_PER_INVOCATION];

		//for checked benchmarks only
		public TruncationPolicy checkedTruncationPolicy;
		public DecimalArithmetic checkedArithmetic;

		protected void initParams(RoundingMode roundingMode) {
			arithmetic = Scales.getScaleMetrics(scale).getArithmetic(roundingMode);
			factory = Factories.getDecimalFactory(scale);
			checkedTruncationPolicy = OverflowMode.CHECKED.getTruncationPolicyFor(roundingMode);
			checkedArithmetic = Scales.getScaleMetrics(scale).getArithmetic(checkedTruncationPolicy);
			mcLong64 = new MathContext(19, roundingMode);
			mcLong128 = new MathContext(39, roundingMode);
		}
	}
	@State(Scope.Benchmark)
	abstract public static class TruncatingBenchmarkState extends AbstractBenchmarkState {
		@Setup
		public void initParams() {
			super.initParams(RoundingMode.DOWN);
		}
	}

	@State(Scope.Benchmark)
	abstract public static class RoundingBenchmarkState extends AbstractBenchmarkState {
		//@Param({ "HALF_UP" })
		@Param({"DOWN", "HALF_UP"})
		public RoundingMode roundingMode;

		@Setup
		public void initParams() {
			super.initParams(roundingMode);
		}
	}

	protected static class Values<S extends ScaleMetrics> {
		public final long unscaled1;
		public final long unscaled2;
		public final BigDecimal bigDecimal1;
		public final BigDecimal bigDecimal2;
		public final ImmutableDecimal<S> immutable1;
		public final ImmutableDecimal<S> immutable2;
		public final MutableDecimal<S> mutable;

		private Values(long unscaled1, long unscaled2, int scale, DecimalFactory<S> decimalFactory) {
			this.unscaled1 = unscaled1;
			this.unscaled2 = unscaled2;
			this.bigDecimal1 = BigDecimal.valueOf(unscaled1, scale);
			this.bigDecimal2 = BigDecimal.valueOf(unscaled2, scale);
			this.immutable1 = (ImmutableDecimal<S>) decimalFactory.valueOfUnscaled(unscaled1);
			this.immutable2 = (ImmutableDecimal<S>) decimalFactory.valueOfUnscaled(unscaled2);
			this.mutable = (MutableDecimal<S>) decimalFactory.newMutable();
		}

		public static Values<?> create(BenchmarkType benchmarkType, int scale, ValueType valueType) {
			return create(benchmarkType, scale, valueType, null);
		}
		public static Values<?> create(BenchmarkType benchmarkType, int scale, ValueType valueType1, ValueType valueType2) {
			final long value1 = benchmarkType.randomFirst(valueType1, scale);
			final long value2 = valueType2 == null ? 0 : benchmarkType.randomSecond(valueType2, scale, value1);
			return create(value1, value2, Scales.getScaleMetrics(scale));
		}
		private static <S extends ScaleMetrics> Values<S> create(long unscaled1, long unscaled2, S scaleMetrics) {
			return new Values<S>(unscaled1, unscaled2, scaleMetrics.getScale(), Factories.getDecimalFactory(scaleMetrics));
		}
	}
	
	protected static void run(Class<? extends AbstractBenchmark> benchmarkClass) throws RunnerException, IOException, InterruptedException {
		new JmhRunner(benchmarkClass).run();
	}
}
