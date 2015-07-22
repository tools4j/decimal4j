/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.truncate;

import static org.junit.Assert.assertSame;

import java.math.RoundingMode;

import org.junit.Test;

/**
 * Unit test for {@link TruncationPolicy}.
 */
public class TruncationPolicyTest {

	@Test
	public void testUncheckedPolicies() {
		for (final UncheckedRounding policy : UncheckedRounding.VALUES) {
			//then
			assertSame("overflow mode should be UNCHECKED", OverflowMode.UNCHECKED, policy.getOverflowMode());
		}
	}

	@Test
	public void testCheckedPolicies() {
		for (final CheckedRounding policy : CheckedRounding.VALUES) {
			//then
			assertSame("overflow mode should be CHECKED", OverflowMode.CHECKED, policy.getOverflowMode());
		}
	}
	
	@Test
	public void testPoliciesByRoundingMode() {
		for (final DecimalRounding rounding : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = rounding.getRoundingMode();
			//when
			final TruncationPolicy policy1 = UncheckedRounding.valueOf(roundingMode);
			//then
			assertSame("overflow mode should be UNCHECKED", OverflowMode.UNCHECKED, policy1.getOverflowMode());
			assertSame("rounding mode should be " + roundingMode, roundingMode, policy1.getRoundingMode());
			//when
			final TruncationPolicy policy2 = CheckedRounding.valueOf(roundingMode);
			//then
			assertSame("overflow mode should be CHECKED", OverflowMode.CHECKED, policy2.getOverflowMode());
			assertSame("rounding mode should be " + roundingMode, roundingMode, policy2.getRoundingMode());
		}
	}

	@Test
	public void defaultShouldBeUncheckedRoundingHalfUp() {
		assertSame("overflow mode should be UNCHECKED", OverflowMode.UNCHECKED, TruncationPolicy.DEFAULT.getOverflowMode());
		assertSame("rounding mode should be HALF_UP", RoundingMode.HALF_UP, TruncationPolicy.DEFAULT.getRoundingMode());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void valuesListShouldBeImmutable() {
		TruncationPolicy.VALUES.clear();
	}
}
