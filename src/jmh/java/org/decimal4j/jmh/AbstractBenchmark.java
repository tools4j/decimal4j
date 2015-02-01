package org.decimal4j.jmh;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.decimal4j.ImmutableDecimal;
import org.decimal4j.MutableDecimal;
import org.decimal4j.arithmetic.DecimalArithmetics;
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
	abstract public static class AbstractBenchmarkState {
		//		@Param({ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18" })
		//		@Param({ "0", "6", "9", "17", "18" })
		@Param({ "0", "6", "17" })
		public int scale;

		public DecimalArithmetics arithmetics;
		public DecimalFactory<?> factory;
		public MathContext mcLong64;
		public MathContext mcLong128;

		public Values<?>[] values = new Values<?>[OPERATIONS_PER_INVOCATION];

		//for checked benchmarks only
		public TruncationPolicy checkedTruncationPolicy;
		public DecimalArithmetics checkedArithmetics;

		protected void initParams(RoundingMode roundingMode) {
			arithmetics = Scales.valueOf(scale).getArithmetics(roundingMode);
			factory = Factories.valueOf(scale);
			checkedTruncationPolicy = OverflowMode.CHECKED.getTruncationPolicyFor(roundingMode);
			checkedArithmetics = Scales.valueOf(scale).getArithmetics(checkedTruncationPolicy);
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
		public final ImmutableDecimal<S, ?> immutable1;
		public final ImmutableDecimal<S, ?> immutable2;
		public final MutableDecimal<S, ?> mutable;

		public Values(long unscaled1, long unscaled2, int scale, DecimalFactory<S> decimalFactory) {
			this.unscaled1 = unscaled1;
			this.unscaled2 = unscaled2;
			this.bigDecimal1 = BigDecimal.valueOf(unscaled1, scale);
			this.bigDecimal2 = BigDecimal.valueOf(unscaled2, scale);
			this.immutable1 = (ImmutableDecimal<S, ?>) decimalFactory.createImmutable(unscaled1);
			this.immutable2 = (ImmutableDecimal<S, ?>) decimalFactory.createImmutable(unscaled2);
			this.mutable = (MutableDecimal<S, ?>) decimalFactory.createMutable(0);
		}

		public static Values<?> create(long unscaled1, long unscaled2, int scale) {
			return create(unscaled1, unscaled2, Scales.valueOf(scale));
		}

		public static <S extends ScaleMetrics> Values<S> create(long unscaled1, long unscaled2, S scaleMetrics) {
			return new Values<S>(unscaled1, unscaled2, scaleMetrics.getScale(), Factories.valueOf(scaleMetrics));
		}
	}
	
	protected static void run(Class<? extends AbstractBenchmark> benchmarkClass) throws RunnerException, IOException, InterruptedException {
		new JmhRunner(benchmarkClass).run();
	}
}
