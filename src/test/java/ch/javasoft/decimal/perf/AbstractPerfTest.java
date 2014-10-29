package ch.javasoft.decimal.perf;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.MutableDecimal;
import ch.javasoft.decimal.Timer;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.factory.DecimalFactory;
import ch.javasoft.decimal.factory.Factories;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.scale.Scales;

/**
 * Base class for tests comparing {@link Decimal}, {@link BigDecimal} and
 * {@code double} in terms of operation performance. The subclass implements the
 * operation to test.
 */
@RunWith(Parameterized.class)
abstract public class AbstractPerfTest {

//	private static final List<ScaleMetrics> SCALE_METRICES = Scales.VALUES;
	private static final List<ScaleMetrics> SCALE_METRICES = Arrays.asList(Scales.valueOf(0), Scales.valueOf(6), Scales.valueOf(9), Scales.valueOf(17), Scales.valueOf(18));

	private static final int R = 1*4; //runs
	private static final int N = 1024/16/4; //numbers per run
	private static final int I = 1024*16;//iterations on the same dataset
	private static final int W = I; //warm-up iterations;

	private final Random rnd = new Random();

	private final ScaleMetrics scaleMetrics;
	private final DecimalArithmetics arithmetics;
	private final MathContext mcLong64;
	private final MathContext mcLong128;
	
	protected long nextRandomLong() {
//		return rnd.nextInt(Integer.MAX_VALUE);
//		return rnd.nextInt();
		return rnd.nextBoolean() ? rnd.nextLong() : rnd.nextInt();
//		return rnd.nextLong();
	}

	private static final ThreadLocal<Set<Class<?>>> tracedTestClasses = new ThreadLocal<Set<Class<?>>>() {
		@Override
		protected Set<Class<?>> initialValue() {
			return new HashSet<Class<?>>();
		};
	};

	public AbstractPerfTest(ScaleMetrics scaleMetrics) {
		if (!tracedTestClasses.get().contains(getClass())) {
			System.out.println("running " + getClass().getSimpleName() + ":");//for test data output on new line
			tracedTestClasses.get().add(getClass());
		}
		this.scaleMetrics = scaleMetrics;
		this.arithmetics = scaleMetrics.getDefaultArithmetics();
		this.mcLong64 = new MathContext(19, arithmetics.getRoundingMode());
		this.mcLong128 = new MathContext(39, arithmetics.getRoundingMode());
	}

	abstract protected String operation();

	abstract protected BigDecimal expectedResult(BigDecimal a, BigDecimal b, MathContext mathContext);

	abstract protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b);

	abstract protected int signumOfResult(BigDecimal a, BigDecimal b, MathContext mathContext);

	abstract protected int signumOfResult(double a, double b);

	abstract protected <S extends ScaleMetrics> int signumOfResult(Decimal<S> a, Decimal<S> b);

	abstract protected <S extends ScaleMetrics> int signumOfResult(MutableDecimal<S, ?> m, Decimal<S> a, Decimal<S> b);

	abstract protected <S extends ScaleMetrics> int signumOfResult(DecimalArithmetics arith, long a, long b);

	@Parameters(name = "{index}: {0}")
	public static Iterable<Object[]> data() {
		final Object[][] data = new Object[SCALE_METRICES.size()][];
		for (int i = 0; i < data.length; i++) {
			data[i] = new Object[] { SCALE_METRICES.get(i) };
		}
		return Arrays.asList(data);
	}

	@Test
	public void runPerfTest() {
		runPerfTest(scaleMetrics);
	}

	protected <S extends ScaleMetrics> void runPerfTest(S scaleMetrics) {
		final Timer timer = new Timer(5);

		long cnt = 0;

		//allocate
		final DecimalFactory<S> factory = Factories.valueOf(scaleMetrics);
		final BigDecimal[] aBigDec = new BigDecimal[N];
		final BigDecimal[] bBigDec = new BigDecimal[N];
		final double[] aDouble = new double[N];
		final double[] bDouble = new double[N];
		final Decimal<S>[] aDec = factory.createImmutableArray(N);
		final Decimal<S>[] bDec = factory.createImmutableArray(N);
		final MutableDecimal<S, ?> mutable = factory.createMutable(0);

		for (int r = 0; r < R; r++) {

			//prepare input values
			randomBigDecimals(aBigDec);
			randomBigDecimals(bBigDec);
			toDouble(aBigDec, aDouble);
			toDouble(bBigDec, bDouble);
			toDecimal(mutable, aBigDec, aDec);
			toDecimal(mutable, bBigDec, bDec);

			Runtime.getRuntime().gc();

			//BigDecimal
			for (int iW = 0; iW < W; iW++) {
				for (int iN = 0; iN < N; iN++) {
					cnt += signumOfResult(aBigDec[iN], bBigDec[iN], mcLong64);
				}
			}
			timer.firstAndStart();
			for (int iI = 0; iI < I; iI++) {
				for (int iN = 0; iN < N; iN++) {
					cnt += signumOfResult(aBigDec[iN], bBigDec[iN], mcLong64);
				}
			}
			timer.stop();
			Runtime.getRuntime().gc();

			//double
			for (int iW = 0; iW < W; iW++) {
				for (int iN = 0; iN < N; iN++) {
					cnt += signumOfResult(aDouble[iN], bDouble[iN]);
				}
			}
			timer.nextAndStart();
			for (int iI = 0; iI < I; iI++) {
				for (int iN = 0; iN < N; iN++) {
					cnt += signumOfResult(aDouble[iN], bDouble[iN]);
				}
			}
			timer.stop();
			Runtime.getRuntime().gc();

			//Decimal
			for (int iW = 0; iW < W; iW++) {
				for (int iN = 0; iN < N; iN++) {
					cnt += signumOfResult(aDec[iN], bDec[iN]);
				}
			}
			timer.nextAndStart();
			for (int iI = 0; iI < I; iI++) {
				for (int iN = 0; iN < N; iN++) {
					cnt += signumOfResult(aDec[iN], bDec[iN]);
				}
			}
			timer.stop();
			Runtime.getRuntime().gc();

			//MutableDecimal
			for (int iW = 0; iW < W; iW++) {
				for (int iN = 0; iN < N; iN++) {
					cnt += signumOfResult(mutable, aDec[iN], bDec[iN]);
				}
			}
			timer.nextAndStart();
			for (int iI = 0; iI < I; iI++) {
				for (int iN = 0; iN < N; iN++) {
					cnt += signumOfResult(mutable, aDec[iN], bDec[iN]);
				}
			}
			timer.stop();
			Runtime.getRuntime().gc();

			//native Decimal
			final DecimalArithmetics arith = arithmetics;
			for (int iW = 0; iW < W; iW++) {
				for (int iN = 0; iN < N; iN++) {
					cnt += signumOfResult(arith, aDec[iN].unscaledValue(), bDec[iN].unscaledValue());
				}
			}
			timer.nextAndStart();
			for (int iI = 0; iI < I; iI++) {
				for (int iN = 0; iN < N; iN++) {
					cnt += signumOfResult(arith, aDec[iN].unscaledValue(), bDec[iN].unscaledValue());
				}
			}
			timer.stop();
			Runtime.getRuntime().gc();

			for (int iN = 0; iN < N; iN++) {
				final BigDecimal expected = expectedResult(aBigDec[iN], bBigDec[iN], mcLong128);
				if (expected != null) {
					final BigDecimal expectedScaled = expected.setScale(scaleMetrics.getScale(), arithmetics.getRoundingMode());
					final Decimal<S> actual = actualResult(aDec[iN], bDec[iN]);
					assertEquals("test[" + iN + "]: " + aDec[iN] + " " + operation() + " " + bDec[iN] + " = " + expected.toPlainString(), expectedScaled.unscaledValue().longValue(), actual.unscaledValue());
				}
			}
		}

		//trick the optimizer by using cnt
		final long fac = cnt == 0 ? 1 : cnt / cnt;
		//report times
		logTime((R * N * I) * fac, timer);
	}

	private void logTime(long count, Timer timer) {
		final String msg = getClass().getSimpleName().substring(0, 3) + ": " + count + " ops";
		final long tBigDec = timer.getTimeMillis(0);
		final long tDouble = timer.getTimeMillis(1);
		final long tDecNf = timer.getTimeMillis(2);
		final long tMutNf = timer.getTimeMillis(3);
		final long tNatNf = timer.getTimeMillis(4);
		System.out.println(msg + ", scale=" + scaleMetrics.getScale() + ", rounding=" + arithmetics.getRoundingMode() + ": BigDecimal=" + tBigDec + "ms, double=" + tDouble + "ms, Decimal=" + tDecNf + "ms, Mutable=" + tMutNf + "ms, native=" + tNatNf + "ms, relative=" //
				+ ((100f * tDouble) / tBigDec) + "% / " + ((100f * tDecNf) / tBigDec) + "% / " + ((100f * tMutNf) / tBigDec) + "% / " + +((100f * tNatNf) / tBigDec) + "%");
	}
	
	private BigDecimal[] randomBigDecimals(BigDecimal[] values) {
		final int n = values.length;
		final int scale = arithmetics.getScale();
		for (int i = 0; i < n; i++) {
			final long val = nextRandomLong();
			values[i] = BigDecimal.valueOf(val, scale);
		}
		return values;
	}

	private double[] toDouble(BigDecimal[] source, double[] target) {
		final int n = Math.min(source.length, target.length);
		for (int i = 0; i < n; i++) {
			target[i] = source[i].doubleValue();
		}
		return target;
	}

	private <S extends ScaleMetrics> Decimal<S>[] toDecimal(MutableDecimal<S, ?> mutable, BigDecimal[] source, Decimal<S>[] target) {
		final int n = Math.min(source.length, target.length);
		for (int i = 0; i < n; i++) {
			target[i] = mutable.setUnscaled(source[i].unscaledValue().longValueExact()).toImmutableDecimal();
		}
		return target;
	}

}
