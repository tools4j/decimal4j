package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Assert;
import org.junit.Test;

import ch.javasoft.decimal.ScaleMetrics.Scale6f;

/**
 * Unit test for {@link RoundingArithmetics} with {@link RoundingMode#HALF_EVEN}.
 */
public class RoundHalfUpDecimalArithmeticsTest extends AbstractDecimalArithmeticsTest {
	
	@Override
	protected DecimalArithmetics initArithmetics() {
		return Scale6f.INSTANCE.getTruncatingArithmetics().derive(RoundingMode.HALF_UP);
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
		
		//rounded up (through half-even mode)
		assertEquals("0.000001", arith.multiply(arith.parse("0.0009"), arith.parse("0.0009")));
		assertEquals("100.018001", arith.multiply(arith.parse("10.0009"), arith.parse("10.0009")));
		assertEquals("0.000001", arith.multiply(arith.parse("-0.0009"), arith.parse("-0.0009")));
		assertEquals("100.018001", arith.multiply(arith.parse("-10.0009"), arith.parse("-10.0009")));
		assertEquals("-0.000001", arith.multiply(arith.parse("-0.0009"), arith.parse("0.0009")));
		assertEquals("-100.018001", arith.multiply(arith.parse("-10.0009"), arith.parse("10.0009")));
		assertEquals("-0.000001", arith.multiply(arith.parse("0.0009"), arith.parse("-0.0009")));
		assertEquals("-100.018001", arith.multiply(arith.parse("10.0009"), arith.parse("-10.0009")));

		//rounded half-even
		assertEquals("0.000000", arith.multiply(arith.parse("0.0007"), arith.parse("0.0007")));
		assertEquals("0.000001", arith.multiply(arith.parse("0.0005"), arith.parse("0.001")));//ROUNDED
		assertEquals("0.000002", arith.multiply(arith.parse("0.0015"), arith.parse("0.001")));
		assertEquals("0.000000", arith.multiply(arith.parse("-0.0007"), arith.parse("-0.0007")));
		assertEquals("0.000001", arith.multiply(arith.parse("-0.0005"), arith.parse("-0.001")));//ROUNDED
		assertEquals("0.000002", arith.multiply(arith.parse("-0.0015"), arith.parse("-0.001")));
		assertEquals("0.000000", arith.multiply(arith.parse("-0.0007"), arith.parse("0.0007")));
		assertEquals("-0.000001", arith.multiply(arith.parse("-0.0005"), arith.parse("0.001")));//ROUNDED
		assertEquals("-0.000002", arith.multiply(arith.parse("-0.0015"), arith.parse("0.001")));
		assertEquals("0.000000", arith.multiply(arith.parse("0.0007"), arith.parse("-0.0007")));
		assertEquals("-0.000001", arith.multiply(arith.parse("0.0005"), arith.parse("-0.001")));
		assertEquals("-0.000002", arith.multiply(arith.parse("0.0015"), arith.parse("-0.001")));
	}

	@Override
	@Test
	public void testDivide() {
		assertEquals("0.001000", arith.divide(arith.parse("0.000001"), arith.parse("0.001")));
		assertEquals("0.002000", arith.divide(arith.parse("0.000002"), arith.parse("0.001")));
		assertEquals("0.000001", arith.divide(arith.parse("0.001"), arith.parse("1000")));
		assertEquals("0.000000", arith.divide(arith.parse("0.001"), arith.parse("10000")));
		assertEquals("10.002000", arith.divide(arith.parse("100.040004"), arith.parse("10.002")));
		assertEquals("0.333333", arith.divide(arith.parse("1.0"), arith.parse("3.000")));
		assertEquals("0.666667", arith.divide(arith.parse("2.0"), arith.parse("3.000")));//ROUNDED
		assertEquals("0.142857", arith.divide(arith.parse("1"), arith.parse("7")));
		assertEquals("1.000000", arith.divide(Long.MAX_VALUE, Long.MAX_VALUE));
		assertEquals("2.000000", arith.divide(Long.MAX_VALUE, Long.MAX_VALUE/2));
		assertEquals("2.000000", arith.divide(maxLongValue, maxLongValue/2));
		assertEquals("10.000000", arith.divide(maxLongValue, arith.parse("922337203685.400000")));
		assertEquals("1000000.000000", arith.divide(maxLongValue, arith.parse("9223372.036854")));
		assertEquals("0.500000", arith.divide(maxLongValue/2, maxLongValue));
		assertEquals("0.100000", arith.divide(maxLongValue/10, maxLongValue));
		assertEquals("0.010000", arith.divide(maxLongValue/100, maxLongValue));
		assertEquals("0.001000", arith.divide(maxLongValue/1000, maxLongValue));
		assertEquals("0.000100", arith.divide(maxLongValue/10000, maxLongValue));
		assertEquals("0.000010", arith.divide(maxLongValue/100000, maxLongValue));
		assertEquals("0.000001", arith.divide(maxLongValue/1000000, maxLongValue));
		assertEquals("0.000002", arith.divide(maxLongValue/500000, maxLongValue));
		assertEquals("0.000005", arith.divide(maxLongValue/200000, maxLongValue));
		assertEquals("0.000001", arith.divide(maxLongValue/1000000/2, maxLongValue));//ROUNDED
		assertEquals("0.500000", arith.divide((Long.MAX_VALUE-1)/2, (Long.MAX_VALUE-1)));
		assertEquals("0.100000", arith.divide((Long.MAX_VALUE-7)/10, Long.MAX_VALUE-7));
		assertEquals("0.010000", arith.divide((Long.MAX_VALUE-7)/100, Long.MAX_VALUE-7));
		assertEquals("0.001000", arith.divide((Long.MAX_VALUE-807)/1000, Long.MAX_VALUE-807));
		assertEquals("0.000100", arith.divide((Long.MAX_VALUE-5807)/10000, Long.MAX_VALUE-5807));
		assertEquals("0.000010", arith.divide((Long.MAX_VALUE-75807)/100000, Long.MAX_VALUE-75807));
		assertEquals("0.000001", arith.divide((Long.MAX_VALUE-775807)/1000000, Long.MAX_VALUE-775807));
		assertEquals("0.000002", arith.divide((Long.MAX_VALUE-775807)/500000, Long.MAX_VALUE-775807));
		assertEquals("0.000005", arith.divide((Long.MAX_VALUE-775807)/200000, Long.MAX_VALUE-775807));
		assertEquals("0.000001", arith.divide((Long.MAX_VALUE-4775807)/1000000/2, Long.MAX_VALUE-4775807));//ROUNDED

		assertEquals("0.001000", arith.divide(arith.parse("-0.000001"), arith.parse("-0.001")));
		assertEquals("0.002000", arith.divide(arith.parse("-0.000002"), arith.parse("-0.001")));
		assertEquals("0.000001", arith.divide(arith.parse("-0.001"), arith.parse("-1000")));
		assertEquals("0.000000", arith.divide(arith.parse("-0.001"), arith.parse("-10000")));
		assertEquals("10.002000", arith.divide(arith.parse("-100.040004"), arith.parse("-10.002")));
		assertEquals("0.333333", arith.divide(arith.parse("-1.0"), arith.parse("-3.000")));
		assertEquals("0.666667", arith.divide(arith.parse("-2.0"), arith.parse("-3.000")));//ROUNDED
		assertEquals("0.142857", arith.divide(arith.parse("-1"), arith.parse("-7")));
		assertEquals("1.000000", arith.divide(Long.MIN_VALUE, Long.MIN_VALUE));
		assertEquals("2.000000", arith.divide(Long.MIN_VALUE, Long.MIN_VALUE/2));
		assertEquals("2.000000", arith.divide(minLongValue, minLongValue/2));
		assertEquals("10.000000", arith.divide(minLongValue, arith.parse("-922337203685.400000")));
		assertEquals("1000000.000000", arith.divide(minLongValue, arith.parse("-9223372.036854")));
		assertEquals("0.500000", arith.divide(Long.MIN_VALUE/2, Long.MIN_VALUE));
		assertEquals("0.100000", arith.divide((Long.MIN_VALUE-8)/10, Long.MIN_VALUE-8));
		assertEquals("0.010000", arith.divide((Long.MIN_VALUE-8)/100, Long.MIN_VALUE-8));
		assertEquals("0.001000", arith.divide((Long.MIN_VALUE-808)/1000, Long.MIN_VALUE-808));
		assertEquals("0.000100", arith.divide((Long.MIN_VALUE-5808)/10000, Long.MIN_VALUE-5808));
		assertEquals("0.000010", arith.divide((Long.MIN_VALUE-75808)/100000, Long.MIN_VALUE-75808));
		assertEquals("0.000001", arith.divide((Long.MIN_VALUE-775808)/1000000, Long.MIN_VALUE-775808));
		assertEquals("0.000002", arith.divide((Long.MIN_VALUE-775808)/500000, Long.MIN_VALUE-775808));
		assertEquals("0.000005", arith.divide((Long.MIN_VALUE-775808)/200000, Long.MIN_VALUE-775808));
		assertEquals("0.000001", arith.divide((Long.MIN_VALUE-4775808)/1000000/2, Long.MIN_VALUE-4775808));//ROUNDED
		assertEquals("0.500000", arith.divide(minLongValue/2, minLongValue));
		assertEquals("0.100000", arith.divide(minLongValue/10, minLongValue));
		assertEquals("0.010000", arith.divide(minLongValue/100, minLongValue));
		assertEquals("0.001000", arith.divide(minLongValue/1000, minLongValue));
		assertEquals("0.000100", arith.divide(minLongValue/10000, minLongValue));
		assertEquals("0.000010", arith.divide(minLongValue/100000, minLongValue));
		assertEquals("0.000001", arith.divide(minLongValue/1000000, minLongValue));
		assertEquals("0.000002", arith.divide(minLongValue/500000, minLongValue));
		assertEquals("0.000005", arith.divide(minLongValue/200000, minLongValue));
		assertEquals("0.000001", arith.divide(minLongValue/1000000/2, minLongValue));//ROUNDED

		assertEquals("-0.001000", arith.divide(arith.parse("-0.000001"), arith.parse("0.001")));
		assertEquals("-0.002000", arith.divide(arith.parse("-0.000002"), arith.parse("0.001")));
		assertEquals("-0.000001", arith.divide(arith.parse("-0.001"), arith.parse("1000")));
		assertEquals("0.000000", arith.divide(arith.parse("-0.001"), arith.parse("10000")));
		assertEquals("-10.002000", arith.divide(arith.parse("-100.040004"), arith.parse("10.002")));
		assertEquals("-0.333333", arith.divide(arith.parse("-1.0"), arith.parse("3.000")));
		assertEquals("-0.666667", arith.divide(arith.parse("-2.0"), arith.parse("3.000")));//ROUNDED
		assertEquals("-0.142857", arith.divide(arith.parse("-1"), arith.parse("7")));
		assertEquals("-1.000000", arith.divide(-Long.MAX_VALUE, Long.MAX_VALUE));
		assertEquals("-2.000000", arith.divide(-maxLongValue, maxLongValue/2));
		assertEquals("-10.000000", arith.divide(-maxLongValue, arith.parse("922337203685.400000")));
		assertEquals("-1000000.000000", arith.divide(-maxLongValue, arith.parse("9223372.036854")));
		assertEquals("-0.500000", arith.divide(-(Long.MAX_VALUE-1)/2, (Long.MAX_VALUE-1)));
		assertEquals("-0.100000", arith.divide(-(Long.MAX_VALUE-7)/10, Long.MAX_VALUE-7));
		assertEquals("-0.010000", arith.divide(-(Long.MAX_VALUE-7)/100, Long.MAX_VALUE-7));
		assertEquals("-0.001000", arith.divide(-(Long.MAX_VALUE-807)/1000, Long.MAX_VALUE-807));
		assertEquals("-0.000100", arith.divide(-(Long.MAX_VALUE-5807)/10000, Long.MAX_VALUE-5807));
		assertEquals("-0.000010", arith.divide(-(Long.MAX_VALUE-75807)/100000, Long.MAX_VALUE-75807));
		assertEquals("-0.000001", arith.divide(-(Long.MAX_VALUE-775807)/1000000, Long.MAX_VALUE-775807));
		assertEquals("-0.000002", arith.divide(-(Long.MAX_VALUE-775807)/500000, Long.MAX_VALUE-775807));
		assertEquals("-0.000005", arith.divide(-(Long.MAX_VALUE-775807)/200000, Long.MAX_VALUE-775807));
		assertEquals("-0.000001", arith.divide(-(Long.MAX_VALUE-4775807)/1000000/2, Long.MAX_VALUE-4775807));//ROUNDED
		assertEquals("-0.500000", arith.divide(-maxLongValue/2, maxLongValue));
		assertEquals("-0.100000", arith.divide(-maxLongValue/10, maxLongValue));
		assertEquals("-0.010000", arith.divide(-maxLongValue/100, maxLongValue));
		assertEquals("-0.001000", arith.divide(-maxLongValue/1000, maxLongValue));
		assertEquals("-0.000100", arith.divide(-maxLongValue/10000, maxLongValue));
		assertEquals("-0.000010", arith.divide(-maxLongValue/100000, maxLongValue));
		assertEquals("-0.000001", arith.divide(-maxLongValue/1000000, maxLongValue));
		assertEquals("-0.000002", arith.divide(-maxLongValue/500000, maxLongValue));
		assertEquals("-0.000005", arith.divide(-maxLongValue/200000, maxLongValue));
		assertEquals("-0.000001", arith.divide(-maxLongValue/1000000/2, maxLongValue));//ROUNDED

		assertEquals("-0.001000", arith.divide(arith.parse("0.000001"), arith.parse("-0.001")));
		assertEquals("-0.002000", arith.divide(arith.parse("0.000002"), arith.parse("-0.001")));
		assertEquals("-0.000001", arith.divide(arith.parse("0.001"), arith.parse("-1000")));
		assertEquals("0.000000", arith.divide(arith.parse("0.001"), arith.parse("-10000")));
		assertEquals("-10.002000", arith.divide(arith.parse("100.040004"), arith.parse("-10.002")));
		assertEquals("-0.333333", arith.divide(arith.parse("1.0"), arith.parse("-3.000")));
		assertEquals("-0.666667", arith.divide(arith.parse("2.0"), arith.parse("-3.000")));//ROUNDED
		assertEquals("-0.142857", arith.divide(arith.parse("1"), arith.parse("-7")));
		assertEquals("-1.000000", arith.divide(Long.MAX_VALUE, -Long.MAX_VALUE));
		assertEquals("-2.000000", arith.divide(Long.MIN_VALUE, -(Long.MIN_VALUE/2)));
		assertEquals("-2.000000", arith.divide(maxLongValue, -maxLongValue/2));
		assertEquals("-10.000000", arith.divide(maxLongValue, arith.parse("-922337203685.400000")));
		assertEquals("-1000000.000000", arith.divide(maxLongValue, arith.parse("-9223372.036854")));
		assertEquals("-0.500000", arith.divide((Long.MAX_VALUE-1)/2, -(Long.MAX_VALUE-1)));
		assertEquals("-0.100000", arith.divide((Long.MAX_VALUE-7)/10, -(Long.MAX_VALUE-7)));
		assertEquals("-0.010000", arith.divide((Long.MAX_VALUE-7)/100, -(Long.MAX_VALUE-7)));
		assertEquals("-0.001000", arith.divide((Long.MAX_VALUE-807)/1000, -(Long.MAX_VALUE-807)));
		assertEquals("-0.000100", arith.divide((Long.MAX_VALUE-5807)/10000, -(Long.MAX_VALUE-5807)));
		assertEquals("-0.000010", arith.divide((Long.MAX_VALUE-75807)/100000, -(Long.MAX_VALUE-75807)));
		assertEquals("-0.000001", arith.divide((Long.MAX_VALUE-775807)/1000000, -(Long.MAX_VALUE-775807)));
		assertEquals("-0.000002", arith.divide((Long.MAX_VALUE-775807)/500000, -(Long.MAX_VALUE-775807)));
		assertEquals("-0.000005", arith.divide((Long.MAX_VALUE-775807)/200000, -(Long.MAX_VALUE-775807)));
		assertEquals("-0.000001", arith.divide((Long.MAX_VALUE-4775807)/1000000/2, -(Long.MAX_VALUE-4775807)));//ROUNDED
		assertEquals("-0.500000", arith.divide(maxLongValue/2, -maxLongValue));
		assertEquals("-0.100000", arith.divide(maxLongValue/10, -maxLongValue));
		assertEquals("-0.010000", arith.divide(maxLongValue/100, -maxLongValue));
		assertEquals("-0.001000", arith.divide(maxLongValue/1000, -maxLongValue));
		assertEquals("-0.000100", arith.divide(maxLongValue/10000, -maxLongValue));
		assertEquals("-0.000010", arith.divide(maxLongValue/100000, -maxLongValue));
		assertEquals("-0.000001", arith.divide(maxLongValue/1000000, -maxLongValue));
		assertEquals("-0.000002", arith.divide(maxLongValue/500000, -maxLongValue));
		assertEquals("-0.000005", arith.divide(maxLongValue/200000, -maxLongValue));
		assertEquals("-0.000001", arith.divide(maxLongValue/1000000/2, -maxLongValue));//ROUNDED

		//test around Long.MAX_VALUE
		assertEquals("9223372036854.000000", arith.divide(maxLongValue/2, arith.parse(".5")));
		assertEquals("9223372036854.775800", arith.divide(arith.parse("922337203685.477580"), arith.parse(".1")));
		//overflow resulting in negative  value
//		assertEquals("-9223372036854.775807", arith.divide(arith.parse("922337203685.477581"), arith.parse(".1")));//ROUNDED
		//test around Long.MIN_VALUE
		assertEquals("-9223372036854.000000", arith.divide(minLongValue/2, arith.parse(".5")));
		assertEquals("-9223372036854.775800", arith.divide(arith.parse("922337203685.477580"), arith.parse("-.1")));
		//overflow resulting in positive value
//		assertEquals("9223372036854.775807", arith.divide(arith.parse("922337203685.477581"), arith.parse("-.1")));//ROUNDED
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
	public void testParseRoundHalfEven() {
		/*
		 * @see RoundingMode#HALF_UP;
         *<tr align=right><td>5.5</td>  <td>6</td>
         *<tr align=right><td>2.5</td>  <td>3</td>
         *<tr align=right><td>1.6</td>  <td>2</td>
         *<tr align=right><td>1.1</td>  <td>1</td>
         *<tr align=right><td>1.0</td>  <td>1</td>
         *<tr align=right><td>-1.0</td> <td>-1</td>
         *<tr align=right><td>-1.1</td> <td>-1</td>
         *<tr align=right><td>-1.6</td> <td>-2</td>
         *<tr align=right><td>-2.5</td> <td>-3</td>
         *<tr align=right><td>-5.5</td> <td>-6</td>
		 */
		final int s = arith.getScale();
		Assert.assertEquals(arith.fromUnscaled(6, s), arith.parse("0.000005501"));
		Assert.assertEquals(arith.fromUnscaled(6, s), arith.parse("0.0000055"));
		Assert.assertEquals(arith.fromUnscaled(3, s), arith.parse("0.000002501"));
		Assert.assertEquals(arith.fromUnscaled(3, s), arith.parse("0.0000025"));
		Assert.assertEquals(arith.fromUnscaled(2, s), arith.parse("0.0000016"));
		Assert.assertEquals(arith.fromUnscaled(1, s), arith.parse("0.0000011"));
		Assert.assertEquals(arith.fromUnscaled(1, s), arith.parse("0.0000010"));
		Assert.assertEquals(arith.fromUnscaled(-1, s), arith.parse("-0.0000010"));
		Assert.assertEquals(arith.fromUnscaled(-1, s), arith.parse("-0.0000011"));
		Assert.assertEquals(arith.fromUnscaled(-2, s), arith.parse("-0.0000016"));
		Assert.assertEquals(arith.fromUnscaled(-3, s), arith.parse("-0.0000025"));
		Assert.assertEquals(arith.fromUnscaled(-3, s), arith.parse("-0.000002501"));
		Assert.assertEquals(arith.fromUnscaled(-6, s), arith.parse("-0.0000055"));
		Assert.assertEquals(arith.fromUnscaled(-6, s), arith.parse("-0.000005501"));
	}

	@Test 
	public void testFromUnscaledRoundHalfUp() {
		/*
		 * @see RoundingMode#HALF_UP;
         *<tr align=right><td>5.5</td>  <td>6</td>
         *<tr align=right><td>2.5</td>  <td>3</td>
         *<tr align=right><td>1.6</td>  <td>2</td>
         *<tr align=right><td>1.1</td>  <td>1</td>
         *<tr align=right><td>1.0</td>  <td>1</td>
         *<tr align=right><td>-1.0</td> <td>-1</td>
         *<tr align=right><td>-1.1</td> <td>-1</td>
         *<tr align=right><td>-1.6</td> <td>-2</td>
         *<tr align=right><td>-2.5</td> <td>-3</td>
         *<tr align=right><td>-5.5</td> <td>-6</td>
		 */
		final int s = arith.getScale();
		Assert.assertEquals(arith.fromUnscaled(6, s), arith.fromUnscaled(5501, s+3));
		Assert.assertEquals(arith.fromUnscaled(6, s), arith.fromUnscaled(55, s+1));
		Assert.assertEquals(arith.fromUnscaled(3, s), arith.fromUnscaled(2501, s+3));
		Assert.assertEquals(arith.fromUnscaled(3, s), arith.fromUnscaled(25, s+1));
		Assert.assertEquals(arith.fromUnscaled(2, s), arith.fromUnscaled(16, s+1));
		Assert.assertEquals(arith.fromUnscaled(1, s), arith.fromUnscaled(11, s+1));
		Assert.assertEquals(arith.fromUnscaled(1, s), arith.fromUnscaled(10, s+1));
		Assert.assertEquals(arith.fromUnscaled(-1, s), arith.fromUnscaled(-10, s+1));
		Assert.assertEquals(arith.fromUnscaled(-1, s), arith.fromUnscaled(-11, s+1));
		Assert.assertEquals(arith.fromUnscaled(-2, s), arith.fromUnscaled(-16, s+1));
		Assert.assertEquals(arith.fromUnscaled(-3, s), arith.fromUnscaled(-25, s+1));
		Assert.assertEquals(arith.fromUnscaled(-3, s), arith.fromUnscaled(-2501, s+3));
		Assert.assertEquals(arith.fromUnscaled(-6, s), arith.fromUnscaled(-55, s+1));
		Assert.assertEquals(arith.fromUnscaled(-6, s), arith.fromUnscaled(-5501, s+3));
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
		assertFromToDoubleExact(value);
		assertFromToDoubleExact(value + noise);
		assertFromToDoubleExact(value - noise);
	}
}
