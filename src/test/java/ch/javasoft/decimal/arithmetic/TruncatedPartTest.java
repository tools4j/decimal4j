package ch.javasoft.decimal.arithmetic;

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
	public void testRemainingZeroOfOne() {
		assertEquals(TruncatedPart.ZERO, TruncatedPart.valueOf(0, 1));
	}
	@Test
	public void testRemainingOneOfTwo() {
		assertEquals(TruncatedPart.EQUAL_TO_HALF, TruncatedPart.valueOf(1, 2));
	}
	@Test
	public void testRemainingOneOfThree() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(1, 3));
	}
	@Test
	public void testRemainingTwoOfThree() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(2, 3));
	}
	@Test
	public void testRemainingLongMaxHalfOfLongMax() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(Long.MAX_VALUE/2, Long.MAX_VALUE));
	}
	@Test
	public void testRemainingLongMaxHalfMinusOneOfLongMax() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(Long.MAX_VALUE/2-1, Long.MAX_VALUE));
	}
	@Test
	public void testRemainingLongMaxHalfPlusOneOfLongMax() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(Long.MAX_VALUE/2+1, Long.MAX_VALUE));
	}
	@Test
	public void testRemainingLongMaxHalfOfLongMaxMinusOne() {
		assertEquals(TruncatedPart.EQUAL_TO_HALF, TruncatedPart.valueOf(Long.MAX_VALUE/2, Long.MAX_VALUE - 1));
	}
	@Test
	public void testRemainingLongMaxHalfMinusOneOfLongMaxMinusOne() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(Long.MAX_VALUE/2-1, Long.MAX_VALUE - 1));
	}
	@Test
	public void testRemainingLongMaxHalfPlusOneOfLongMaxMinusOne() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(Long.MAX_VALUE/2+1, Long.MAX_VALUE - 1));
	}
	@Test
	public void testRemainingLongMinHalfOfLongMin() {
		assertEquals(TruncatedPart.EQUAL_TO_HALF, TruncatedPart.valueOf(Math.abs(Long.MIN_VALUE/2), Long.MIN_VALUE));
	}
	@Test
	public void testRemainingLongMinHalfMinusOneOfLongMin() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(Math.abs(Long.MIN_VALUE/2)-1, Long.MIN_VALUE));
	}
	@Test
	public void testRemainingLongMinHalfPlusOneOfLongMin() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(Math.abs(Long.MIN_VALUE/2)+1, Long.MIN_VALUE));
	}
}