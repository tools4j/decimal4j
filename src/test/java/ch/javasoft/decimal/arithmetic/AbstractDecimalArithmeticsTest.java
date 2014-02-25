package ch.javasoft.decimal.arithmetic;

import org.junit.Assert;
import org.junit.Test;

/**
 * Base class for unit tests that are testing {@link DecimalArithmetics}
 * subclasses.
 */
abstract public class AbstractDecimalArithmeticsTest {

	protected DecimalArithmetics arith = initArithmetics();
	protected final long maxLongValue = arith.fromLong(Long.MAX_VALUE/arith.one());
	protected final long minLongValue = arith.fromLong(Long.MIN_VALUE/arith.one());

	abstract protected DecimalArithmetics initArithmetics();

	@Test
	public void testAdd() {
		assertEquals("0.000002", arith.add(arith.parse("0.000001"), arith.parse("0.000001")));
		assertEquals("0.000000", arith.add(arith.parse("0.000001"), arith.parse("-0.000001")));
		assertEquals("100.000002", arith.add(arith.parse("99.000001"), arith.parse("1.000001")));
		//test around Long.MAX_VALUE
		assertEquals("9223372036854.000001", arith.add(maxLongValue, arith.parse("0.000001")));
		assertEquals("9223372036854.775807", arith.add(maxLongValue, arith.parse("0.775807")));
		//overflow resulting in Long.MIN_VALUE
		assertEquals("-9223372036854.775808", arith.add(maxLongValue, arith.parse("0.775808")));
		//test around Long.MIN_VALUE
		assertEquals("-9223372036854.000001", arith.add(minLongValue, arith.parse("-0.000001")));
		assertEquals("-9223372036854.775808", arith.add(minLongValue, arith.parse("-0.775808")));
		//overflow resulting in Long.MAX_VALUE
		assertEquals("9223372036854.775807", arith.add(minLongValue, arith.parse("-0.775809")));
	}

	@Test
	public void testSubtract() {
		assertEquals("0.000001", arith.subtract(arith.parse("0.000002"), arith.parse("0.000001")));
		assertEquals("0.000000", arith.subtract(arith.parse("0.000001"), arith.parse("0.000001")));
		assertEquals("99.000001", arith.subtract(arith.parse("100.000002"), arith.parse("1.000001")));
		//test around Long.MAX_VALUE
		assertEquals("9223372036854.000001", arith.subtract(maxLongValue, arith.parse("-0.000001")));
		assertEquals("9223372036854.775807", arith.subtract(maxLongValue, arith.parse("-0.775807")));
		//overflow resulting in Long.MIN_VALUE
		assertEquals("-9223372036854.775808", arith.subtract(maxLongValue, arith.parse("-0.775808")));
		//test around Long.MIN_VALUE
		assertEquals("-9223372036854.000001", arith.subtract(minLongValue, arith.parse("0.000001")));
		assertEquals("-9223372036854.775808", arith.subtract(minLongValue, arith.parse("0.775808")));
		//overflow resulting in Long.MAX_VALUE
		assertEquals("9223372036854.775807", arith.subtract(minLongValue, arith.parse("0.775809")));
	}

	@Test
	public void testMultiply() {
		assertEquals("0.000001", arith.multiply(arith.parse("0.001"), arith.parse("0.001")));
		assertEquals("0.000002", arith.multiply(arith.parse("0.001"), arith.parse("0.002")));
		assertEquals("0.000000", arith.multiply(arith.parse("0.001"), arith.parse("0.0001")));
		assertEquals("100.040004", arith.multiply(arith.parse("10.002"), arith.parse("10.002")));

		assertEquals("0.000001", arith.multiply(arith.parse("-0.001"), arith.parse("-0.001")));
		assertEquals("0.000002", arith.multiply(arith.parse("-0.001"), arith.parse("-0.002")));
		assertEquals("0.000000", arith.multiply(arith.parse("-0.001"), arith.parse("-0.0001")));
		assertEquals("100.040004", arith.multiply(arith.parse("-10.002"), arith.parse("-10.002")));
		
		assertEquals("-0.000001", arith.multiply(arith.parse("-0.001"), arith.parse("0.001")));
		assertEquals("-0.000002", arith.multiply(arith.parse("-0.001"), arith.parse("0.002")));
		assertEquals("0.000000", arith.multiply(arith.parse("-0.001"), arith.parse("0.0001")));
		assertEquals("-100.040004", arith.multiply(arith.parse("-10.002"), arith.parse("10.002")));
		
		assertEquals("-0.000001", arith.multiply(arith.parse("0.001"), arith.parse("-0.001")));
		assertEquals("-0.000002", arith.multiply(arith.parse("0.001"), arith.parse("-0.002")));
		assertEquals("0.000000", arith.multiply(arith.parse("0.001"), arith.parse("-0.0001")));
		assertEquals("-100.040004", arith.multiply(arith.parse("10.002"), arith.parse("-10.002")));
		
		//test around Long.MAX_VALUE
		assertEquals("9223372036854.000000", arith.multiply(maxLongValue/2, arith.parse("2")));
		assertEquals("9223372036854.775800", arith.multiply(arith.parse("922337203685.477580"), arith.parse("10")));
		//overflow resulting in negative  value
		assertEquals("-9223372036854.775806", arith.multiply(arith.parse("922337203685.477581"), arith.parse("10")));
		//test around Long.MIN_VALUE
		assertEquals("-9223372036854.000000", arith.multiply(minLongValue/2, arith.parse("2")));
		assertEquals("-9223372036854.775800", arith.multiply(arith.parse("922337203685.477580"), arith.parse("-10")));
		//overflow resulting in positive value
		assertEquals("9223372036854.775806", arith.multiply(arith.parse("922337203685.477581"), arith.parse("-10")));
	}

	@Test
	public void testDivide() {
		assertEquals("0.001000", arith.divide(arith.parse("0.000001"), arith.parse("0.001")));
		assertEquals("0.002000", arith.divide(arith.parse("0.000002"), arith.parse("0.001")));
		assertEquals("0.000001", arith.divide(arith.parse("0.001"), arith.parse("1000")));
		assertEquals("0.000000", arith.divide(arith.parse("0.001"), arith.parse("10000")));
		assertEquals("10.002000", arith.divide(arith.parse("100.040004"), arith.parse("10.002")));
		assertEquals("0.333333", arith.divide(arith.parse("1.0"), arith.parse("3.000")));
		assertEquals("0.666666", arith.divide(arith.parse("2.0"), arith.parse("3.000")));
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
		assertEquals("0.000000", arith.divide(maxLongValue/1000000/2, maxLongValue));
		assertEquals("0.500000", arith.divide((Long.MAX_VALUE-1)/2, (Long.MAX_VALUE-1)));
		assertEquals("0.100000", arith.divide((Long.MAX_VALUE-7)/10, Long.MAX_VALUE-7));
		assertEquals("0.010000", arith.divide((Long.MAX_VALUE-7)/100, Long.MAX_VALUE-7));
		assertEquals("0.001000", arith.divide((Long.MAX_VALUE-807)/1000, Long.MAX_VALUE-807));
		assertEquals("0.000100", arith.divide((Long.MAX_VALUE-5807)/10000, Long.MAX_VALUE-5807));
		assertEquals("0.000010", arith.divide((Long.MAX_VALUE-75807)/100000, Long.MAX_VALUE-75807));
		assertEquals("0.000001", arith.divide((Long.MAX_VALUE-775807)/1000000, Long.MAX_VALUE-775807));
		assertEquals("0.000002", arith.divide((Long.MAX_VALUE-775807)/500000, Long.MAX_VALUE-775807));
		assertEquals("0.000005", arith.divide((Long.MAX_VALUE-775807)/200000, Long.MAX_VALUE-775807));
		assertEquals("0.000000", arith.divide((Long.MAX_VALUE-4775807)/1000000/2, Long.MAX_VALUE-4775807));

		assertEquals("0.001000", arith.divide(arith.parse("-0.000001"), arith.parse("-0.001")));
		assertEquals("0.002000", arith.divide(arith.parse("-0.000002"), arith.parse("-0.001")));
		assertEquals("0.000001", arith.divide(arith.parse("-0.001"), arith.parse("-1000")));
		assertEquals("0.000000", arith.divide(arith.parse("-0.001"), arith.parse("-10000")));
		assertEquals("10.002000", arith.divide(arith.parse("-100.040004"), arith.parse("-10.002")));
		assertEquals("0.333333", arith.divide(arith.parse("-1.0"), arith.parse("-3.000")));
		assertEquals("0.666666", arith.divide(arith.parse("-2.0"), arith.parse("-3.000")));
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
		assertEquals("0.000000", arith.divide((Long.MIN_VALUE-4775808)/1000000/2, Long.MIN_VALUE-4775808));
		assertEquals("0.500000", arith.divide(minLongValue/2, minLongValue));
		assertEquals("0.100000", arith.divide(minLongValue/10, minLongValue));
		assertEquals("0.010000", arith.divide(minLongValue/100, minLongValue));
		assertEquals("0.001000", arith.divide(minLongValue/1000, minLongValue));
		assertEquals("0.000100", arith.divide(minLongValue/10000, minLongValue));
		assertEquals("0.000010", arith.divide(minLongValue/100000, minLongValue));
		assertEquals("0.000001", arith.divide(minLongValue/1000000, minLongValue));
		assertEquals("0.000002", arith.divide(minLongValue/500000, minLongValue));
		assertEquals("0.000005", arith.divide(minLongValue/200000, minLongValue));
		assertEquals("0.000000", arith.divide(minLongValue/1000000/2, minLongValue));

		assertEquals("-0.001000", arith.divide(arith.parse("-0.000001"), arith.parse("0.001")));
		assertEquals("-0.002000", arith.divide(arith.parse("-0.000002"), arith.parse("0.001")));
		assertEquals("-0.000001", arith.divide(arith.parse("-0.001"), arith.parse("1000")));
		assertEquals("0.000000", arith.divide(arith.parse("-0.001"), arith.parse("10000")));
		assertEquals("-10.002000", arith.divide(arith.parse("-100.040004"), arith.parse("10.002")));
		assertEquals("-0.333333", arith.divide(arith.parse("-1.0"), arith.parse("3.000")));
		assertEquals("-0.666666", arith.divide(arith.parse("-2.0"), arith.parse("3.000")));
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
		assertEquals("0.000000", arith.divide(-(Long.MAX_VALUE-4775807)/1000000/2, Long.MAX_VALUE-4775807));
		assertEquals("-0.500000", arith.divide(-maxLongValue/2, maxLongValue));
		assertEquals("-0.100000", arith.divide(-maxLongValue/10, maxLongValue));
		assertEquals("-0.010000", arith.divide(-maxLongValue/100, maxLongValue));
		assertEquals("-0.001000", arith.divide(-maxLongValue/1000, maxLongValue));
		assertEquals("-0.000100", arith.divide(-maxLongValue/10000, maxLongValue));
		assertEquals("-0.000010", arith.divide(-maxLongValue/100000, maxLongValue));
		assertEquals("-0.000001", arith.divide(-maxLongValue/1000000, maxLongValue));
		assertEquals("-0.000002", arith.divide(-maxLongValue/500000, maxLongValue));
		assertEquals("-0.000005", arith.divide(-maxLongValue/200000, maxLongValue));
		assertEquals("0.000000", arith.divide(-maxLongValue/1000000/2, maxLongValue));

		assertEquals("-0.001000", arith.divide(arith.parse("0.000001"), arith.parse("-0.001")));
		assertEquals("-0.002000", arith.divide(arith.parse("0.000002"), arith.parse("-0.001")));
		assertEquals("-0.000001", arith.divide(arith.parse("0.001"), arith.parse("-1000")));
		assertEquals("0.000000", arith.divide(arith.parse("0.001"), arith.parse("-10000")));
		assertEquals("-10.002000", arith.divide(arith.parse("100.040004"), arith.parse("-10.002")));
		assertEquals("-0.333333", arith.divide(arith.parse("1.0"), arith.parse("-3.000")));
		assertEquals("-0.666666", arith.divide(arith.parse("2.0"), arith.parse("-3.000")));
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
		assertEquals("0.000000", arith.divide((Long.MAX_VALUE-4775807)/1000000/2, -(Long.MAX_VALUE-4775807)));
		assertEquals("-0.500000", arith.divide(maxLongValue/2, -maxLongValue));
		assertEquals("-0.100000", arith.divide(maxLongValue/10, -maxLongValue));
		assertEquals("-0.010000", arith.divide(maxLongValue/100, -maxLongValue));
		assertEquals("-0.001000", arith.divide(maxLongValue/1000, -maxLongValue));
		assertEquals("-0.000100", arith.divide(maxLongValue/10000, -maxLongValue));
		assertEquals("-0.000010", arith.divide(maxLongValue/100000, -maxLongValue));
		assertEquals("-0.000001", arith.divide(maxLongValue/1000000, -maxLongValue));
		assertEquals("-0.000002", arith.divide(maxLongValue/500000, -maxLongValue));
		assertEquals("-0.000005", arith.divide(maxLongValue/200000, -maxLongValue));
		assertEquals("0.000000", arith.divide(maxLongValue/1000000/2, -maxLongValue));

		//test around Long.MAX_VALUE
		assertEquals("9223372036854.000000", arith.divide(maxLongValue/2, arith.parse(".5")));
		assertEquals("9223372036854.775800", arith.divide(arith.parse("922337203685.477580"), arith.parse(".1")));
		//overflow resulting in negative  value
		assertEquals("-9223372036854.775806", arith.divide(arith.parse("922337203685.477581"), arith.parse(".1")));
		//test around Long.MIN_VALUE
		assertEquals("-9223372036854.000000", arith.divide(minLongValue/2, arith.parse(".5")));
		assertEquals("-9223372036854.775800", arith.divide(arith.parse("922337203685.477580"), arith.parse("-.1")));
		//overflow resulting in positive value
		assertEquals("9223372036854.775806", arith.divide(arith.parse("922337203685.477581"), arith.parse("-.1")));
	}

	@Test
	public void testFromToLong() {
		assertFromToLong(Long.MIN_VALUE / arith.one());
		assertFromToLong((Long.MIN_VALUE + 1) / arith.one());
		assertFromToLong((Long.MIN_VALUE / 2) / arith.one());
		assertFromToLong(-1000000);
		for (int i = -1000; i <= 1000; i++) {
			assertFromToLong(i);
		}
		assertFromToLong((Long.MAX_VALUE / 2) / arith.one());
		assertFromToLong((Long.MAX_VALUE - 1) / arith.one());
		assertFromToLong(Long.MAX_VALUE / arith.one());
	}

	@Test
	public void testFromToDoubleForLongs() {
		assertFromToDoubleExact(Long.MIN_VALUE / arith.one());
		assertFromToDoubleExact((Long.MIN_VALUE + 1) / arith.one());
		assertFromToDoubleExact((Long.MIN_VALUE / 2) / arith.one());
		assertFromToDoubleExact(-1000000);
		for (int i = -1000; i <= 1000; i++) {
			assertFromToDoubleExact(i);
		}
		assertFromToDoubleExact(1000000);
		assertFromToDoubleExact((Long.MAX_VALUE / 2) / arith.one());
		assertFromToDoubleExact((Long.MAX_VALUE - 1) / arith.one());
		assertFromToDoubleExact(Long.MAX_VALUE / arith.one());
	}

	protected void assertEquals(String expected, String actual) {
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("expected=" + expected + ", actual=" + actual, arith.parse(expected), arith.parse(actual));
	}
	protected void assertEquals(String expected, long uActual) {
		Assert.assertEquals("expected=" + expected + ", actual=" + arith.toString(uActual), arith.parse(expected), uActual);
		Assert.assertEquals(expected, arith.toString(uActual));
	}
	protected void assertEquals(long uExpected, long uActual) {
		Assert.assertEquals("expected=" + arith.toString(uExpected) + ", actual=" + arith.toString(uActual), uExpected, uActual);
		Assert.assertEquals(arith.toString(uExpected), arith.toString(uActual));
	}
	
	protected void assertFromToLong(long value) {
		Assert.assertEquals(value, arith.toLong(arith.fromLong(value)));
	}

	protected void assertFromToDoubleExact(double value) {
		assertFromToDoubleWithTolerance(value, 0);
	}
	protected void assertFromToDoubleWithTolerance(double value, double tolerance) {
		try {
			Assert.assertEquals(value, arith.toDouble(arith.fromDouble(value)), tolerance);
		} catch (AssertionError e) {
			throw new AssertionError(e + "[decimal=" + arith.toString(arith.fromDouble(value)) + ", tolerance=" + tolerance + "]", e);
		}
	}
}
