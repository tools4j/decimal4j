package ch.javasoft.decimal;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;

import org.junit.Test;

import ch.javasoft.decimal.ScaleMetrics.Scale6f;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

/**
 * Compares {@link Decimal} and {@link BigDecimal} reporting some performance
 * results.
 */
public class BigDecimalComparisonPerfTest {

	//	private final int R = 1024>>4;
	//	private final int N = 4096<<4;
	//	private final int R = 1024;
	//	private final int N = 4096;
	//		private final int R = 4096;
	//		private final int N = 1024;
	private final int R = 4096 << 2;
	private final int N = 256;
	private final int W = 256;
	//	private final int R = 4096<<4;
	//	private final int N = 64;
	//		private final int R = 4096<<6;
	//		private final int N = 16;
	//	private final int R = 4096*1024;
	//	private final int N = 1;

	private final Random rnd = new Random();
	private final DecimalArithmetics arithmetics = initArithmetics();
	private final MathContext mathContext = new MathContext(MathContext.DECIMAL64.getPrecision(), arithmetics.getRoundingMode());
	private final MathContext unlimited = new MathContext(MathContext.UNLIMITED.getPrecision(), RoundingMode.UNNECESSARY);
	private final MathContext decimal128 = new MathContext(MathContext.DECIMAL128.getPrecision(), arithmetics.getRoundingMode());

	private DecimalArithmetics initArithmetics() {
		return Decimal6f.ARITHMETICS;
	}

	@Test
	public void testAddition() {
		final int r = R;
		final int n = N;
		final int w = W;

		final Timer timer = new Timer(5);

		long cnt = 0;

		//allocate
		final BigDecimal[] aBigDec = new BigDecimal[n];
		final BigDecimal[] bBigDec = new BigDecimal[n];
		final double[] aDouble = new double[n];
		final double[] bDouble = new double[n];
		final Decimal<Scale6f>[] aDec6f = new Decimal6f[n];
		final Decimal<Scale6f>[] bDec6f = new Decimal6f[n];
		final MutableDecimal6f mutable = Decimal6f.ZERO.toMutableDecimal();

		for (int j = 0; j < r + w; j++) {
			//prepare input values
			randomLongBigDecimals(aBigDec);
			randomLongBigDecimals(bBigDec);
			toDouble(aBigDec, aDouble);
			toDouble(bBigDec, bDouble);
			toDecimal6f(aBigDec, aDec6f);
			toDecimal6f(bBigDec, bDec6f);
			
			if (j == w) {
				timer.reset();
			}

			//BigDecimal
			timer.firstAndStart();
			for (int i = 0; i < n; i++) {
				cnt += aBigDec[i].add(bBigDec[i]).signum();
			}

			//double
			timer.stopAndNextStart();
			for (int i = 0; i < n; i++) {
				cnt += Math.signum(aDouble[i] + bDouble[i]);
			}

			//Decimal6f
			timer.stopAndNextStart();
			for (int i = 0; i < n; i++) {
				cnt += aDec6f[i].add(bDec6f[i]).signum();
			}

			//MutableDecimal6f
			timer.stopAndNextStart();
			for (int i = 0; i < n; i++) {
				mutable.set(aDec6f[i]).add(bDec6f[i]);
				cnt += mutable.signum();
			}

			//native Decimal6f
			timer.stopAndNextStart();
			final DecimalArithmetics arith = Decimal6f.ARITHMETICS;
			for (int i = 0; i < n; i++) {
				cnt += arith.signum(arith.add(aDec6f[i].unscaledValue(), bDec6f[i].unscaledValue()));
			}

			//assert
			timer.stop();
			for (int i = 0; i < n; i++) {
				assertEquals("test[" + i + "]: " + aDec6f[i] + " + " + bDec6f[i], Decimal6f.valueOf(aBigDec[i].add(bBigDec[i])), aDec6f[i].add(bDec6f[i]));
			}
		}

		//report times
		logTime("add of " + (r * n) + " values", timer);
		if (cnt > 0) {
			return;
		}
	}

	@Test
	public void testSubtraction() {
		final int r = R;
		final int n = N;
		final int w = W;

		final Timer timer = new Timer(5);

		long cnt = 0;

		//allocate
		final BigDecimal[] aBigDec = new BigDecimal[n];
		final BigDecimal[] bBigDec = new BigDecimal[n];
		final double[] aDouble = new double[n];
		final double[] bDouble = new double[n];
		final Decimal<Scale6f>[] aDec6f = new Decimal6f[n];
		final Decimal<Scale6f>[] bDec6f = new Decimal6f[n];
		final MutableDecimal6f mutable = Decimal6f.ZERO.toMutableDecimal();

		for (int j = 0; j < r + w; j++) {
			//prepare input values
			randomLongBigDecimals(aBigDec);
			randomLongBigDecimals(bBigDec);
			toDouble(aBigDec, aDouble);
			toDouble(bBigDec, bDouble);
			toDecimal6f(aBigDec, aDec6f);
			toDecimal6f(bBigDec, bDec6f);

			if (j == w) {
				timer.reset();
			}

			//BigDecimal
			timer.firstAndStart();
			for (int i = 0; i < n; i++) {
				cnt += aBigDec[i].subtract(bBigDec[i]).signum();
			}

			//double
			timer.stopAndNextStart();
			for (int i = 0; i < n; i++) {
				cnt += Math.signum(aDouble[i] - bDouble[i]);
			}

			//Decimal6f
			timer.stopAndNextStart();
			for (int i = 0; i < n; i++) {
				cnt += aDec6f[i].subtract(bDec6f[i]).signum();
			}

			//MutableDecimal6f
			timer.stopAndNextStart();
			for (int i = 0; i < n; i++) {
				mutable.set(aDec6f[i]).subtract(bDec6f[i]);
				cnt += mutable.signum();
			}

			//native Decimal6f
			timer.stopAndNextStart();
			final DecimalArithmetics arith = Decimal6f.ARITHMETICS;
			for (int i = 0; i < n; i++) {
				cnt += arith.signum(arith.subtract(aDec6f[i].unscaledValue(), bDec6f[i].unscaledValue()));
			}

			//assert
			timer.stop();
			for (int i = 0; i < n; i++) {
				assertEquals("test[" + i + "]: " + aDec6f[i] + " - " + bDec6f[i], Decimal6f.valueOf(aBigDec[i].subtract(bBigDec[i])), aDec6f[i].subtract(bDec6f[i]));
			}
		}

		//report times
		logTime("sub of " + (r * n) + " values", timer);
		if (cnt > 0) {
			return;
		}
	}

	@Test
	public void testMultiplication() {
		final int r = R;
		final int n = N;
		final int w = W;

		final Timer timer = new Timer(5);

		long cnt = 0;

		//allocate
		final BigDecimal[] aBigDec = new BigDecimal[n];
		final BigDecimal[] bBigDec = new BigDecimal[n];
		final double[] aDouble = new double[n];
		final double[] bDouble = new double[n];
		final Decimal<Scale6f>[] aDec6f = new Decimal6f[n];
		final Decimal<Scale6f>[] bDec6f = new Decimal6f[n];
		final MutableDecimal6f mutable = Decimal6f.ZERO.toMutableDecimal();

		for (int j = 0; j < r + w; j++) {

			//prepare input values
			randomIntBigDecimals(aBigDec);
			randomIntBigDecimals(bBigDec);
			toDouble(aBigDec, aDouble);
			toDouble(bBigDec, bDouble);
			toDecimal6f(aBigDec, aDec6f);
			toDecimal6f(bBigDec, bDec6f);

			if (j == w) {
				timer.reset();
			}

			//BigDecimal
			timer.firstAndStart();
			for (int i = 0; i < n; i++) {
				cnt += aBigDec[i].multiply(bBigDec[i], mathContext).signum();
			}

			//double
			timer.stopAndNextStart();
			for (int i = 0; i < n; i++) {
				cnt += Math.signum(aDouble[i] * bDouble[i]);
			}

			//Decimal6f
			timer.stopAndNextStart();
			for (int i = 0; i < n; i++) {
				cnt += aDec6f[i].multiply(bDec6f[i]).signum();
			}

			//MutableDecimal6f
			timer.stopAndNextStart();
			for (int i = 0; i < n; i++) {
				mutable.set(aDec6f[i]).multiply(bDec6f[i]);
				cnt += mutable.signum();
			}

			//native Decimal6f
			timer.stopAndNextStart();
			final DecimalArithmetics arith = Decimal6f.ARITHMETICS;
			for (int i = 0; i < n; i++) {
				cnt += arith.signum(arith.multiply(aDec6f[i].unscaledValue(), bDec6f[i].unscaledValue()));
			}

			//assert
			timer.stop();
			for (int i = 0; i < n; i++) {
				//			assertEquals("test[" + i + "]: " + aDec6f[i] + " * " + bDec6f[i], Decimal6f.valueOf(aBigDec[i].multiply(bBigDec[i], mathContext)), aDec6f[i].multiply(bDec6f[i]));
				assertEquals("test[" + i + "]: " + aDec6f[i] + " * " + bDec6f[i], aBigDec[i].multiply(bBigDec[i], unlimited).setScale(6, arithmetics.getRoundingMode()).toString(), aDec6f[i].multiply(bDec6f[i]).toString());
			}
		}

		//report times
		logTime("mul of " + (r * n) + " values", timer);
		if (cnt > 0) {
			return;
		}
	}

	@Test
	public void testDivision() {
		final int r = R;
		final int n = N;
		final int w = W;

		final Timer timer = new Timer(5);

		long cnt = 0;

		//allocate
		final BigDecimal[] aBigDec = new BigDecimal[n];
		final BigDecimal[] bBigDec = new BigDecimal[n];
		final double[] aDouble = new double[n];
		final double[] bDouble = new double[n];
		final Decimal<Scale6f>[] aDec6f = new Decimal6f[n];
		final Decimal<Scale6f>[] bDec6f = new Decimal6f[n];
		final MutableDecimal6f mutable = Decimal6f.ZERO.toMutableDecimal();

		for (int j = 0; j < r + w; j++) {
			//prepare input values
			randomLongBigDecimals(aBigDec);
			randomLongBigDecimals(bBigDec);
			toDouble(aBigDec, aDouble);
			toDouble(bBigDec, bDouble);
			toDecimal6f(aBigDec, aDec6f);
			toDecimal6f(bBigDec, bDec6f);

			if (j == w) {
				timer.reset();
			}

			//BigDecimal
			timer.firstAndStart();
			for (int i = 0; i < n; i++) {
				cnt += aBigDec[i].divide(bBigDec[i], mathContext).signum();
			}

			//double
			timer.stopAndNextStart();
			for (int i = 0; i < n; i++) {
				cnt += Math.signum(aDouble[i] / bDouble[i]);
			}

			//Decimal6f
			timer.stopAndNextStart();
			for (int i = 0; i < n; i++) {
				cnt += aDec6f[i].divide(bDec6f[i]).signum();
			}

			//MutableDecimal6f
			timer.stopAndNextStart();
			for (int i = 0; i < n; i++) {
				mutable.set(aDec6f[i]).divide(bDec6f[i]);
				cnt += mutable.signum();
			}

			//native Decimal6f
			timer.stopAndNextStart();
			final DecimalArithmetics arith = Decimal6f.ARITHMETICS;
			for (int i = 0; i < n; i++) {
				cnt += arith.signum(arith.divide(aDec6f[i].unscaledValue(), bDec6f[i].unscaledValue()));
			}

			//assert
			timer.stop();
			for (int i = 0; i < n; i++) {
				assertEquals("test[" + i + "]: " + aDec6f[i] + " / " + bDec6f[i], aBigDec[i].divide(bBigDec[i], decimal128).setScale(6, arithmetics.getRoundingMode()).toString(), aDec6f[i].divide(bDec6f[i]).toString());
			}
		}

		//report times
		logTime("div of " + (r * n) + " values", timer);
		if (cnt > 0) {
			return;
		}
	}

	private void logTime(String msg, Timer timer) {
		final long tBigDec = timer.getTimeMillis(0);
		final long tDouble = timer.getTimeMillis(1);
		final long tDec6f = timer.getTimeMillis(2);
		final long tMut6f = timer.getTimeMillis(3);
		final long tNat6f = timer.getTimeMillis(4);
		System.out.println(msg + ", trounding=" + Decimal6f.ARITHMETICS.getRoundingMode() + ": BigDecimal=" + tBigDec + "ms, double=" + tDouble + "ms, Decimal6f=" + tDec6f + "ms, mutable=" + tMut6f + "ms, native=" + tNat6f + "ms, relative=" //
				+ ((100f * tDouble) / tBigDec) + "% / " + ((100f * tDec6f) / tBigDec) + "% / " + ((100f * tMut6f) / tBigDec) + "% / " + +((100f * tNat6f) / tBigDec) + "%");
	}

	private BigDecimal[] randomLongBigDecimals(BigDecimal[] values) {
		final int n = values.length;
		final int scale = arithmetics.getScale();
		for (int i = 0; i < n; i++) {
			values[i] = BigDecimal.valueOf(rnd.nextLong(), scale);
		}
		return values;
	}

	private BigDecimal[] randomIntBigDecimals(BigDecimal[] values) {
		final int n = values.length;
		final int scale = arithmetics.getScale();
		for (int i = 0; i < n; i++) {
			values[i] = BigDecimal.valueOf(rnd.nextInt(), scale);
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

	private Decimal<Scale6f>[] toDecimal6f(BigDecimal[] source, Decimal<Scale6f>[] target) {
		final int n = Math.min(source.length, target.length);
		for (int i = 0; i < n; i++) {
			target[i] = Decimal6f.valueOf(source[i]);
		}
		return target;
	}

}
