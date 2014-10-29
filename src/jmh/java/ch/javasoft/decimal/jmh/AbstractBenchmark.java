package ch.javasoft.decimal.jmh;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import ch.javasoft.decimal.ImmutableDecimal;
import ch.javasoft.decimal.MutableDecimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.factory.DecimalFactory;
import ch.javasoft.decimal.factory.Factories;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.scale.Scales;
import ch.javasoft.decimal.truncate.OverflowMode;
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Base class for micro benchmarks based on the jmh library.
 */
abstract public class AbstractBenchmark {

	private static final Random RND = new Random();

	public static enum ValueType {
		Byte {
			@Override
			public long random() {
				return 0xff & RND.nextInt();
			}
		},
		Short {
			@Override
			public long random() {
				return 0xffff & RND.nextInt();
			}
		},
		Int {
			@Override
			public long random() {
				return RND.nextInt();
			}
		},
		Long {
			@Override
			public long random() {
				return RND.nextLong();
			}
		};

		abstract public long random();
	}

	@State(Scope.Benchmark)
	public static class BenchmarkState {
//		@Param({ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18" })
		@Param({ "0", "6", "9", "17", "18" })
		public int scale;
		@Param({"DOWN", "HALF_UP"})
		public RoundingMode roundingMode;
		@Param("UNCHECKED")
		public OverflowMode overflowMode;
		@Param({"Int", "Long"})
		public ValueType valueType1;
		@Param({"Int", "Long"})
		public ValueType valueType2;

		public TruncationPolicy truncationPolicy;
		public DecimalArithmetics arithmetics;
		public MathContext mcLong64;
		public MathContext mcLong128;

		public Values<?> values;

		@Setup
		public void setup() {
			truncationPolicy = overflowMode.getTruncationPolicyFor(roundingMode);
			arithmetics = Scales.valueOf(scale).getArithmetics(truncationPolicy);
			mcLong64 = new MathContext(19, roundingMode);
			mcLong128 = new MathContext(39, roundingMode);
			values = Values.create(valueType1.random(), valueType2.random(), scale);
		}
	}

	protected static class Values<S extends ScaleMetrics> {
		public final long unscaled1;
		public final long unscaled2;
		public final double double1;
		public final double double2;
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
			this.double1 = bigDecimal1.doubleValue();
			this.double2 = bigDecimal2.doubleValue();
			this.mutable = (MutableDecimal<S, ?>) decimalFactory.createMutable(0);
		}
		public static Values<?> create(long unscaled1, long unscaled2, int scale) {
			return create(unscaled1, unscaled2, Scales.valueOf(scale));
		}

		public static <S extends ScaleMetrics> Values<S> create(long unscaled1, long unscaled2, S scaleMetrics) {
			return new Values<S>(unscaled1, unscaled2, scaleMetrics.getScale(), Factories.valueOf(scaleMetrics));
		}
	}

}
