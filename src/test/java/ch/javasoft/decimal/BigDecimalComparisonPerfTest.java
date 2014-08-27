package ch.javasoft.decimal;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;

import org.junit.Test;

import ch.javasoft.decimal.Scale.Scale6f;
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
		private final int R = 4096<<2;
		private final int N = 256;
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

		long tBigDec = 0;
		long tDec6f = 0;
		long tMut6f = 0;
		long tNat6f = 0;

		long cnt = 0;

		//allocate
		final BigDecimal[] aBigDec = new BigDecimal[n];
		final BigDecimal[] bBigDec = new BigDecimal[n];
		final Decimal<Scale6f>[] aDec6f = new Decimal6f[n];
		final Decimal<Scale6f>[] bDec6f = new Decimal6f[n];
		final MutableDecimal6f mutable = Decimal6f.ZERO.toMutableValue();

		for (int j = 0; j < r; j++) {
			//prepare input values
			randomLongBigDecimals(aBigDec);
			randomLongBigDecimals(bBigDec);
			toDecimal6f(aBigDec, aDec6f);
			toDecimal6f(bBigDec, bDec6f);

			//BigDecimal
			final long t0 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				cnt += aBigDec[i].add(bBigDec[i]).signum();
			}

			//Decimal6f
			final long t1 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				cnt += aDec6f[i].add(bDec6f[i]).signum();
			}

			//MutableDecimal6f
			final long t2 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				mutable.set(aDec6f[i]).add(bDec6f[i]);
				cnt += mutable.signum();
			}

			//native Decimal6f
			final long t3 = System.currentTimeMillis();
			final DecimalArithmetics arith = Decimal6f.ARITHMETICS;
			for (int i = 0; i < n; i++) {
				cnt += arith.signum(arith.add(aDec6f[i].unscaledValue(), bDec6f[i].unscaledValue()));
			}

			//assert
			final long t4 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				assertEquals("test[" + i + "]: " + aDec6f[i] + " + " + bDec6f[i], Decimal6f.valueOf(aBigDec[i].add(bBigDec[i])), aDec6f[i].add(bDec6f[i]));
			}

			tBigDec += (t1 - t0);
			tDec6f += (t2 - t1);
			tMut6f += (t3 - t2);
			tNat6f += (t4 - t3);
		}

		//report times
		logTime("add of " + (r * n) + " values", tBigDec, tDec6f, tMut6f, tNat6f);
		if (cnt > 0) {
			return;
		}
	}

	@Test
	public void testSubtraction() {
		final int r = R;
		final int n = N;

		long tBigDec = 0;
		long tDec6f = 0;
		long tMut6f = 0;
		long tNat6f = 0;

		long cnt = 0;

		//allocate
		final BigDecimal[] aBigDec = new BigDecimal[n];
		final BigDecimal[] bBigDec = new BigDecimal[n];
		final Decimal<Scale6f>[] aDec6f = new Decimal6f[n];
		final Decimal<Scale6f>[] bDec6f = new Decimal6f[n];
		final MutableDecimal6f mutable = Decimal6f.ZERO.toMutableValue();

		for (int j = 0; j < r; j++) {
			//prepare input values
			randomLongBigDecimals(aBigDec);
			randomLongBigDecimals(bBigDec);
			toDecimal6f(aBigDec, aDec6f);
			toDecimal6f(bBigDec, bDec6f);

			//BigDecimal
			final long t0 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				cnt += aBigDec[i].subtract(bBigDec[i]).signum();
			}

			//Decimal6f
			final long t1 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				cnt += aDec6f[i].subtract(bDec6f[i]).signum();
			}

			//MutableDecimal6f
			final long t2 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				mutable.set(aDec6f[i]).subtract(bDec6f[i]);
				cnt += mutable.signum();
			}

			//native Decimal6f
			final long t3 = System.currentTimeMillis();
			final DecimalArithmetics arith = Decimal6f.ARITHMETICS;
			for (int i = 0; i < n; i++) {
				cnt += arith.signum(arith.subtract(aDec6f[i].unscaledValue(), bDec6f[i].unscaledValue()));
			}

			//assert
			final long t4 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				assertEquals("test[" + i + "]: " + aDec6f[i] + " - " + bDec6f[i], Decimal6f.valueOf(aBigDec[i].subtract(bBigDec[i])), aDec6f[i].subtract(bDec6f[i]));
			}

			tBigDec += (t1 - t0);
			tDec6f += (t2 - t1);
			tMut6f += (t3 - t2);
			tNat6f += (t4 - t3);
		}

		//report times
		logTime("sub of " + (r * n) + " values", tBigDec, tDec6f, tMut6f, tNat6f);
		if (cnt > 0) {
			return;
		}
	}

	@Test
	public void testMultiplication() {
		final int r = R;
		final int n = N;

		long tBigDec = 0;
		long tDec6f = 0;
		long tMut6f = 0;
		long tNat6f = 0;

		long cnt = 0;

		//allocate
		final BigDecimal[] aBigDec = new BigDecimal[n];
		final BigDecimal[] bBigDec = new BigDecimal[n];
		final Decimal<Scale6f>[] aDec6f = new Decimal6f[n];
		final Decimal<Scale6f>[] bDec6f = new Decimal6f[n];
		final MutableDecimal6f mutable = Decimal6f.ZERO.toMutableValue();

		for (int j = 0; j < r; j++) {

			//prepare input values
			randomIntBigDecimals(aBigDec);
			randomIntBigDecimals(bBigDec);
			toDecimal6f(aBigDec, aDec6f);
			toDecimal6f(bBigDec, bDec6f);

			//BigDecimal
			final long t0 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				cnt += aBigDec[i].multiply(bBigDec[i], mathContext).signum();
			}

			//Decimal6f
			final long t1 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				cnt += aDec6f[i].multiply(bDec6f[i]).signum();
			}

			//MutableDecimal6f
			final long t2 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				mutable.set(aDec6f[i]).multiply(bDec6f[i]);
				cnt += mutable.signum();
			}

			//native Decimal6f
			final long t3 = System.currentTimeMillis();
			final DecimalArithmetics arith = Decimal6f.ARITHMETICS;
			for (int i = 0; i < n; i++) {
				cnt += arith.signum(arith.multiply(aDec6f[i].unscaledValue(), bDec6f[i].unscaledValue()));
			}

			//assert
			final long t4 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				//			assertEquals("test[" + i + "]: " + aDec6f[i] + " * " + bDec6f[i], Decimal6f.valueOf(aBigDec[i].multiply(bBigDec[i], mathContext)), aDec6f[i].multiply(bDec6f[i]));
				assertEquals("test[" + i + "]: " + aDec6f[i] + " * " + bDec6f[i], aBigDec[i].multiply(bBigDec[i], unlimited).setScale(6, arithmetics.getRoundingMode()).toString(), aDec6f[i].multiply(bDec6f[i]).toString());
			}

			tBigDec += (t1 - t0);
			tDec6f += (t2 - t1);
			tMut6f += (t3 - t2);
			tNat6f += (t4 - t3);
		}

		//report times
		logTime("mul of " + (r * n) + " values", tBigDec, tDec6f, tMut6f, tNat6f);
		if (cnt > 0) {
			return;
		}
	}

	@Test
	public void testDivision() {
		final int r = R;
		final int n = N;

		long tBigDec = 0;
		long tDec6f = 0;
		long tMut6f = 0;
		long tNat6f = 0;

		long cnt = 0;

		//allocate
		final BigDecimal[] aBigDec = new BigDecimal[n];
		final BigDecimal[] bBigDec = new BigDecimal[n];
		final Decimal<Scale6f>[] aDec6f = new Decimal6f[n];
		final Decimal<Scale6f>[] bDec6f = new Decimal6f[n];
		final MutableDecimal6f mutable = Decimal6f.ZERO.toMutableValue();

		for (int j = 0; j < r; j++) {
			//prepare input values
			randomLongBigDecimals(aBigDec);
			randomLongBigDecimals(bBigDec);
			toDecimal6f(aBigDec, aDec6f);
			toDecimal6f(bBigDec, bDec6f);

			//BigDecimal
			final long t0 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				cnt += aBigDec[i].divide(bBigDec[i], mathContext).signum();
			}

			//Decimal6f
			final long t1 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				cnt += aDec6f[i].divide(bDec6f[i]).signum();
			}

			//MutableDecimal6f
			final long t2 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				mutable.set(aDec6f[i]).divide(bDec6f[i]);
				cnt += mutable.signum();
			}

			//native Decimal6f
			final long t3 = System.currentTimeMillis();
			final DecimalArithmetics arith = Decimal6f.ARITHMETICS;
			for (int i = 0; i < n; i++) {
				cnt += arith.signum(arith.divide(aDec6f[i].unscaledValue(), bDec6f[i].unscaledValue()));
			}

			//assert
			final long t4 = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				assertEquals("test[" + i + "]: " + aDec6f[i] + " / " + bDec6f[i], aBigDec[i].divide(bBigDec[i], decimal128).setScale(6, arithmetics.getRoundingMode()).toString(), aDec6f[i].divide(bDec6f[i]).toString());
			}

			tBigDec += (t1 - t0);
			tDec6f += (t2 - t1);
			tMut6f += (t3 - t2);
			tNat6f += (t4 - t3);
		}

		//report times
		logTime("div of " + (r * n) + " values", tBigDec, tDec6f, tMut6f, tNat6f);
		if (cnt > 0) {
			return;
		}
	}

	private void logTime(String msg, long tBigDec, long tDec6f, long tMut6f, long tNat6f) {
		System.out.println(msg + ", trounding=" + Decimal6f.ARITHMETICS.getRoundingMode() + ": BigDecimal=" + tBigDec + "ms, Decimal6f=" + tDec6f + "ms, mutable=" + tMut6f + "ms, native=" + tNat6f + "ms, relative=" + ((100f * tDec6f) / tBigDec) + "% / " + ((100f * tMut6f) / tBigDec) + "% / " + +((100f * tNat6f) / tBigDec) + "%");
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

	private Decimal<Scale6f>[] toDecimal6f(BigDecimal[] source, Decimal<Scale6f>[] target) {
		final int n = Math.min(source.length, target.length);
		for (int i = 0; i < n; i++) {
			target[i] = Decimal6f.valueOf(source[i]);
		}
		return target;
	}

}
