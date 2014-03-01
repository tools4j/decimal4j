package ch.javasoft.decimal.arithmetic;

import org.junit.Assert;
import org.junit.Test;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.arithmetic.TruncatingArithmetics;

/**
 * Unit test for {@link TruncatingArithmetics} and subclasses.
 */
public class TruncatingDecimalArithmeticsTest extends AbstractDecimalArithmeticsTest {
	
	@Override
	protected DecimalArithmetics initArithmetics() {
		return new TruncatingArithmetics(6);
	}
	
	@Override
	@Test
	public void testFromToLong() {
		super.testFromToLong();
	}
	
	@Override
	@Test
	public void testFromToDoubleForLongs() {
		super.testFromToDoubleForLongs();
	}

	@Override
	@Test
	public void testAdd() {
		super.testAdd();
	}

	@Override
	@Test
	public void testSubtract() {
		super.testSubtract();
	}

	@Override
	@Test
	public void testMultiply() {
		super.testMultiply();
		
		//truncated, not rounded correctly
		assertEquals("0.000000", arith.multiply(arith.parse("0.0009"), arith.parse("0.0009")));
		assertEquals("100.018000", arith.multiply(arith.parse("10.0009"), arith.parse("10.0009")));
		assertEquals("0.000000", arith.multiply(arith.parse("-0.0009"), arith.parse("-0.0009")));
		assertEquals("100.018000", arith.multiply(arith.parse("-10.0009"), arith.parse("-10.0009")));
		assertEquals("0.000000", arith.multiply(arith.parse("-0.0009"), arith.parse("0.0009")));
		assertEquals("-100.018000", arith.multiply(arith.parse("-10.0009"), arith.parse("10.0009")));
		assertEquals("0.000000", arith.multiply(arith.parse("0.0009"), arith.parse("-0.0009")));
		assertEquals("-100.018000", arith.multiply(arith.parse("10.0009"), arith.parse("-10.0009")));

		//truncated half-even specials
		assertEquals("0.000000", arith.multiply(arith.parse("0.0007"), arith.parse("0.0007")));
		assertEquals("0.000000", arith.multiply(arith.parse("0.0005"), arith.parse("0.001")));
		assertEquals("0.000001", arith.multiply(arith.parse("0.0015"), arith.parse("0.001")));
		assertEquals("0.000000", arith.multiply(arith.parse("-0.0007"), arith.parse("-0.0007")));
		assertEquals("0.000000", arith.multiply(arith.parse("-0.0005"), arith.parse("-0.001")));
		assertEquals("0.000001", arith.multiply(arith.parse("-0.0015"), arith.parse("-0.001")));
		assertEquals("0.000000", arith.multiply(arith.parse("-0.0007"), arith.parse("0.0007")));
		assertEquals("0.000000", arith.multiply(arith.parse("-0.0005"), arith.parse("0.001")));
		assertEquals("-0.000001", arith.multiply(arith.parse("-0.0015"), arith.parse("0.001")));
		assertEquals("0.000000", arith.multiply(arith.parse("0.0007"), arith.parse("-0.0007")));
		assertEquals("0.000000", arith.multiply(arith.parse("0.0005"), arith.parse("-0.001")));
		assertEquals("-0.000001", arith.multiply(arith.parse("0.0015"), arith.parse("-0.001")));
	}
	
	@Override
	@Test
	public void testDivide() {
		super.testDivide();
	}

	@Test
	public void testFromToDoubleExact() {
		final int scale = arith.getScale();
		final double[] noiseSignificant = new double[scale];
		System.arraycopy(new double[] {0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001, 0.000000001, 0.0000000001}, 0, noiseSignificant, 0, scale);
		for (final double noise : noiseSignificant) {
			assertFromToDoubleExact(Long.MIN_VALUE / arith.one(), noise);
			assertFromToDoubleExact((Long.MIN_VALUE + 1) / arith.one(), noise);
			assertFromToDoubleExact((Long.MIN_VALUE / 2) / arith.one(), noise);
			assertFromToDoubleExact(-1000000, noise);
			for (int i = -1024; i <= 1024; i++) {
				assertFromToDoubleExact(i, noise);
			}
			for (final int factor : new int[] {2, 3, 5, 7, 10, 11, 13}) {
				for (double value : new double[] {1000, 1024}) {
					final long max = Long.MAX_VALUE / arith.one();
					while (value < max) {
						assertFromToDoubleExact(value, noise);
						assertFromToDoubleExact(-value, noise);
						value *= factor;
					}
				}
			}
			assertFromToDoubleExact(1000000, noise);
			assertFromToDoubleExact((Long.MAX_VALUE / 2) / arith.one(), noise);
			assertFromToDoubleExact((Long.MAX_VALUE - 1) / arith.one(), noise);
			assertFromToDoubleExact(Long.MAX_VALUE / arith.one(), noise);
		}
	}

	@Test
	public void testFromToDoubleWithInsignificantNoise() {
		assertFromToDoubleWithNoise(Long.MIN_VALUE / arith.one());
		assertFromToDoubleWithNoise((Long.MIN_VALUE + 1) / arith.one());
		assertFromToDoubleWithNoise((Long.MIN_VALUE / 2) / arith.one());
		assertFromToDoubleWithNoise(-1000000);
		for (int i = -1024; i <= 1024; i++) {
			assertFromToDoubleWithNoise(i);
		}
		for (final int factor : new int[] {2, 3, 5, 7, 10, 11, 13}) {
			for (double value : new double[] {1000, 1024}) {
				final long max = Long.MAX_VALUE / arith.one();
				while (value < max) {
					assertFromToDoubleWithNoise(value);
					assertFromToDoubleWithNoise(-value);
					value *= factor;
				}
			}
		}
		assertFromToDoubleWithNoise((Long.MAX_VALUE / 2) / arith.one());
		assertFromToDoubleWithNoise((Long.MAX_VALUE - 1) / arith.one());
		assertFromToDoubleWithNoise(Long.MAX_VALUE / arith.one());
	}
	
	@Test 
	public void testParseRoundDown() {
		/*
		 * @see RoundingMode#DOWN;
         *<tr align=right><td>5.5</td>  <td>5</td>
         *<tr align=right><td>2.5</td>  <td>2</td>
         *<tr align=right><td>1.6</td>  <td>1</td>
         *<tr align=right><td>1.1</td>  <td>1</td>
         *<tr align=right><td>1.0</td>  <td>1</td>
         *<tr align=right><td>-1.0</td> <td>-1</td>
         *<tr align=right><td>-1.1</td> <td>-1</td>
         *<tr align=right><td>-1.6</td> <td>-1</td>
         *<tr align=right><td>-2.5</td> <td>-2</td>
         *<tr align=right><td>-5.5</td> <td>-5</td>
		 */
		final int s = arith.getScale();
		Assert.assertEquals(arith.fromUnscaled(5, s), arith.parse("0.000005501"));
		Assert.assertEquals(arith.fromUnscaled(5, s), arith.parse("0.0000055"));
		Assert.assertEquals(arith.fromUnscaled(2, s), arith.parse("0.000002501"));
		Assert.assertEquals(arith.fromUnscaled(2, s), arith.parse("0.0000025"));
		Assert.assertEquals(arith.fromUnscaled(1, s), arith.parse("0.0000016"));
		Assert.assertEquals(arith.fromUnscaled(1, s), arith.parse("0.0000011"));
		Assert.assertEquals(arith.fromUnscaled(1, s), arith.parse("0.0000010"));
		Assert.assertEquals(arith.fromUnscaled(-1, s), arith.parse("-0.0000010"));
		Assert.assertEquals(arith.fromUnscaled(-1, s), arith.parse("-0.0000011"));
		Assert.assertEquals(arith.fromUnscaled(-1, s), arith.parse("-0.0000016"));
		Assert.assertEquals(arith.fromUnscaled(-2, s), arith.parse("-0.0000025"));
		Assert.assertEquals(arith.fromUnscaled(-2, s), arith.parse("-0.000002501"));
		Assert.assertEquals(arith.fromUnscaled(-5, s), arith.parse("-0.0000055"));
		Assert.assertEquals(arith.fromUnscaled(-5, s), arith.parse("-0.000005501"));
	}

	@Test 
	public void testFromUnscaledRoundDown() {
		/*
		 * @see RoundingMode#DOWN;
         *<tr align=right><td>5.5</td>  <td>5</td>
         *<tr align=right><td>2.5</td>  <td>2</td>
         *<tr align=right><td>1.6</td>  <td>1</td>
         *<tr align=right><td>1.1</td>  <td>1</td>
         *<tr align=right><td>1.0</td>  <td>1</td>
         *<tr align=right><td>-1.0</td> <td>-1</td>
         *<tr align=right><td>-1.1</td> <td>-1</td>
         *<tr align=right><td>-1.6</td> <td>-1</td>
         *<tr align=right><td>-2.5</td> <td>-2</td>
         *<tr align=right><td>-5.5</td> <td>-5</td>
		 */
		final int s = arith.getScale();
		Assert.assertEquals(arith.fromUnscaled(5, s), arith.fromUnscaled(5501, s+3));
		Assert.assertEquals(arith.fromUnscaled(5, s), arith.fromUnscaled(55, s+1));
		Assert.assertEquals(arith.fromUnscaled(2, s), arith.fromUnscaled(2501, s+3));
		Assert.assertEquals(arith.fromUnscaled(2, s), arith.fromUnscaled(25, s+1));
		Assert.assertEquals(arith.fromUnscaled(1, s), arith.fromUnscaled(16, s+1));
		Assert.assertEquals(arith.fromUnscaled(1, s), arith.fromUnscaled(11, s+1));
		Assert.assertEquals(arith.fromUnscaled(1, s), arith.fromUnscaled(10, s+1));
		Assert.assertEquals(arith.fromUnscaled(-1, s), arith.fromUnscaled(-10, s+1));
		Assert.assertEquals(arith.fromUnscaled(-1, s), arith.fromUnscaled(-11, s+1));
		Assert.assertEquals(arith.fromUnscaled(-1, s), arith.fromUnscaled(-16, s+1));
		Assert.assertEquals(arith.fromUnscaled(-2, s), arith.fromUnscaled(-25, s+1));
		Assert.assertEquals(arith.fromUnscaled(-2, s), arith.fromUnscaled(-2501, s+3));
		Assert.assertEquals(arith.fromUnscaled(-5, s), arith.fromUnscaled(-55, s+1));
		Assert.assertEquals(arith.fromUnscaled(-5, s), arith.fromUnscaled(-5501, s+3));
	}

	private void assertFromToDoubleWithNoise(double value) {
		final double precision = Math.pow(10, -arith.getScale()); 
		final double noise = precision - Math.ulp(precision);//max tolerable noise for decimal
		try {
			Assert.assertEquals(value + noise, arith.toDouble(arith.fromDouble(value + noise)), noise);
			Assert.assertEquals(value - noise, arith.toDouble(arith.fromDouble(value - noise)), noise);
		} catch (AssertionError e) {
			throw new AssertionError(e + "[decimal=" + arith.toString(arith.fromDouble(value)) + ", noise=" + noise + "]", e);
		}
	}
	private void assertFromToDoubleExact(double value, double noise) {
		final double precision = Math.pow(10, -arith.getScale()); 
		final double tolerance = precision*2 - Math.ulp(precision);//max tolerable noise for decimal
		assertFromToDoubleExact(value);
		assertFromToDoubleWithTolerance(value + noise, tolerance, noise);
		assertFromToDoubleWithTolerance(value - noise, tolerance, noise);
	}
}
