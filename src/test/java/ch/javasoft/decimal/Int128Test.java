package ch.javasoft.decimal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.javasoft.decimal.arithmetic.Int128;

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
	}
}
