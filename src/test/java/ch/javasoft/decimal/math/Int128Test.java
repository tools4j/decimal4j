package ch.javasoft.decimal.math;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import ch.javasoft.decimal.math.Int128;

/**
 * Unit test for {@link Int128}
 */
@Ignore
public class Int128Test {
	
	@Test
	public void testMultiply() {
		assertEquals("1 * 1", "1", Int128.multiply(1, 1).toString());
		assertEquals("-1 * -1", "1", Int128.multiply(-1, -1).toString());
		assertEquals("1 * -1", "-1", Int128.multiply(1, -1).toString());
		assertEquals("-1 * 1", "-1", Int128.multiply(-1, 1).toString());
		
		//specials: value*0 / value*1 / value*-1
		for (int i = -100; i < 100; i++) {
			for (long j : new long[] {0, Integer.MIN_VALUE, Integer.MAX_VALUE, Long.MIN_VALUE, Long.MAX_VALUE}) {
				final long value = j + i;
				assertEquals("0 * " + value, "0", Int128.multiply(0, value).toString());
				assertEquals(value + " * 0", "0", Int128.multiply(value, 0).toString());
				assertEquals("1 * " + value, "" + value, Int128.multiply(1, value).toString());
				assertEquals(value + " * 1", "" + value, Int128.multiply(value, 1).toString());
				if (value != Long.MIN_VALUE) {
					assertEquals("-1 * " + value, "" + -value, Int128.multiply(-1, value).toString());
					assertEquals(value + " * -1", "" + -value, Int128.multiply(value, -1).toString());
					assertEquals("1 * " + -value, "" + -value, Int128.multiply(1, -value).toString());
					assertEquals(-value + " * 1", "" + -value, Int128.multiply(-value, 1).toString());
					assertEquals("-1 * " + -value, "" + value, Int128.multiply(-1, -value).toString());
					assertEquals(-value + " * -1", "" + value, Int128.multiply(-value, -1).toString());
				}
			}
		}
		
		//some loops
		for (long i = -255; i < 255; i++) {
			for (long j = -255; j < 255; j++) {
				//small value combinations
				assertEquals(i + " * " + j, i*j, Int128.multiply(i, j).getLo64());
				assertEquals(i + " * " + j, i*j < 0 ? -1 : 0, Int128.multiply(i, j).getHi64());

				assertEquals(i + " * " + j, String.valueOf(i*j), Int128.multiply(i, j).toString());

				//small/large value combinations
				for (long k : new long[] {Short.MIN_VALUE, Short.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Long.MIN_VALUE, Long.MAX_VALUE}) {
					if (i != 0 && !((k+j) == Long.MIN_VALUE && i == -1)) {
						long l = (k+j)/i;
						assertEquals(i + " * " + l, i*l, Int128.multiply(i, l).getLo64());
						assertEquals(i + " * " + l, (i >= 0) != (l >= 0) ? -1 : 0, Int128.multiply(i, l).getHi64());

						assertEquals(i + " * " + l, String.valueOf(i*l), Int128.multiply(i, l).toString());
					}
				}
			}
		}
		
		//large special values
		
	}

	@Test
	public void testToString() {
		assertEquals("0", new Int128(0, 0).toString());
		assertEquals("1", new Int128(0, 1).toString());
		assertEquals("1048576", new Int128(0, 1048576).toString());
		assertEquals("4294967295", new Int128(0, 0x00000000FFFFFFFFL).toString());
		assertEquals("4294967296", new Int128(0, 0x0000000100000000L).toString());
		assertEquals("9223372036854775807", new Int128(0, 0x7FFFFFFFFFFFFFFFL).toString());
		assertEquals("9223372036854775808", new Int128(0, 0x8000000000000000L).toString());
		assertEquals("18446744073709551615", new Int128(0, 0xFFFFFFFFFFFFFFFFL).toString());
		assertEquals("18446744073709551616", new Int128(0x0000000000000001L, 0).toString());
		
		assertEquals("170141183460469231731687303715884105727", new Int128(0x7FFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL).toString());
		assertEquals("-170141183460469231731687303715884105728", new Int128(0x8000000000000000L, 0x0000000000000000L).toString());
		assertEquals("-1", new Int128(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL).toString());
		assertEquals("-2", new Int128(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFeL).toString());
		assertEquals("-9223372036854775808", new Int128(0xFFFFFFFFFFFFFFFFL, 0x8000000000000000L).toString());
		assertEquals("-18446744073709551616", new Int128(0xFFFFFFFFFFFFFFFFL, 0x0000000000000000L).toString());
		
		assertEquals("1000000000000000000000000000000000000", new Int128( 0x00c097ce7bc90715L, 0xb34b9f1000000000L).toString());
		assertEquals("-1000000000000000000000000000000000000", new Int128(0xff3f68318436f8eaL, 0x4cb460efffffffffL + 1).toString());
		assertEquals("1000000000000000000000000000000000001", new Int128( 0x00c097ce7bc90715L, 0xb34b9f1000000000L + 1).toString());
		assertEquals("-1000000000000000000000000000000000001", new Int128(0xff3f68318436f8eaL, 0x4cb460efffffffffL).toString());
		assertEquals("999999999999999999999999999999999999", new Int128( 0x00c097ce7bc90715L, 0xb34b9f1000000000L - 1).toString());
		assertEquals("-999999999999999999999999999999999999", new Int128(0xff3f68318436f8eaL, 0x4cb460efffffffffL + 2).toString());
		assertEquals("2000000000000000000000000000000000000", new Int128( (0x00c097ce7bc90715L << 1) + 1, 0xb34b9f1000000000L << 1).toString());
		assertEquals("-2000000000000000000000000000000000000", new Int128(0xff3f68318436f8eaL << 1, (0x4cb460efffffffffL + 1) << 1).toString());
	}
}
