package ch.javasoft.decimal.truncate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for {@link TruncatedPart}.
 */
public class TruncatedPartTest {
	
	@Test
	public void testFirstZeroRestZero() {
		assertEquals(TruncatedPart.ZERO, TruncatedPart.valueOf(0, true));
	}
	@Test
	public void testFirstOneRestZero() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(1, true));
	}
	@Test
	public void testFirstFiveRestZero() {
		assertEquals(TruncatedPart.EQUAL_TO_HALF, TruncatedPart.valueOf(5, true));
	}
	@Test
	public void testFirstSixRestZero() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(6, true));
	}
	@Test
	public void testFirstNineRestZero() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(9, false));
	}
	@Test
	public void testFirstZeroRestNonZero() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(0, false));
	}
	@Test
	public void testFirstOneRestNonZero() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(1, false));
	}
	@Test
	public void testFirstFiveRestNonZero() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(5, false));
	}
	@Test
	public void testFirstSixRestNonZero() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(6, false));
	}
	@Test
	public void testFirstNineRestNonZero() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(9, false));
	}
}