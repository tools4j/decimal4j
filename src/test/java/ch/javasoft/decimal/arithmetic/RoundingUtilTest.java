package ch.javasoft.decimal.arithmetic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.javasoft.decimal.truncate.TruncatedPart;

/**
 * Unit test for {@link RoundingUtil}
 */
public class RoundingUtilTest {
	@Test
	public void testRemainingZeroOfOne() {
		assertEquals(TruncatedPart.ZERO, RoundingUtil.truncatedPartFor(0, 1));
	}
	@Test
	public void testRemainingOneOfTwo() {
		assertEquals(TruncatedPart.EQUAL_TO_HALF, RoundingUtil.truncatedPartFor(1, 2));
	}
	@Test
	public void testRemainingOneOfThree() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, RoundingUtil.truncatedPartFor(1, 3));
	}
	@Test
	public void testRemainingTwoOfThree() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, RoundingUtil.truncatedPartFor(2, 3));
	}
	@Test
	public void testRemainingLongMaxHalfOfLongMax() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, RoundingUtil.truncatedPartFor(Long.MAX_VALUE/2, Long.MAX_VALUE));
	}
	@Test
	public void testRemainingLongMaxHalfMinusOneOfLongMax() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, RoundingUtil.truncatedPartFor(Long.MAX_VALUE/2-1, Long.MAX_VALUE));
	}
	@Test
	public void testRemainingLongMaxHalfPlusOneOfLongMax() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, RoundingUtil.truncatedPartFor(Long.MAX_VALUE/2+1, Long.MAX_VALUE));
	}
	@Test
	public void testRemainingLongMaxHalfOfLongMaxMinusOne() {
		assertEquals(TruncatedPart.EQUAL_TO_HALF, RoundingUtil.truncatedPartFor(Long.MAX_VALUE/2, Long.MAX_VALUE - 1));
	}
	@Test
	public void testRemainingLongMaxHalfMinusOneOfLongMaxMinusOne() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, RoundingUtil.truncatedPartFor(Long.MAX_VALUE/2-1, Long.MAX_VALUE - 1));
	}
	@Test
	public void testRemainingLongMaxHalfPlusOneOfLongMaxMinusOne() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, RoundingUtil.truncatedPartFor(Long.MAX_VALUE/2+1, Long.MAX_VALUE - 1));
	}
	@Test
	public void testRemainingLongMinHalfOfLongMin() {
		assertEquals(TruncatedPart.EQUAL_TO_HALF, RoundingUtil.truncatedPartFor(Math.abs(Long.MIN_VALUE/2), Long.MIN_VALUE));
	}
	@Test
	public void testRemainingLongMinHalfMinusOneOfLongMin() {
		assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, RoundingUtil.truncatedPartFor(Math.abs(Long.MIN_VALUE/2)-1, Long.MIN_VALUE));
	}
	@Test
	public void testRemainingLongMinHalfPlusOneOfLongMin() {
		assertEquals(TruncatedPart.GREATER_THAN_HALF, RoundingUtil.truncatedPartFor(Math.abs(Long.MIN_VALUE/2)+1, Long.MIN_VALUE));
	}

}
