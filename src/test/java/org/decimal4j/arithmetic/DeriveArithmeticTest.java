/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.arithmetic;

import static org.junit.Assert.assertSame;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link DecimalArithmetic#deriveArithmetic(int)} and all the other
 * derive methods of arithmetic.
 */
@RunWith(Parameterized.class)
public class DeriveArithmeticTest {
	
	private final DecimalArithmetic arithmetic;

	public DeriveArithmeticTest(ScaleMetrics scaleMetrics, TruncationPolicy truncationPolicy, DecimalArithmetic arithmetic) {
		this.arithmetic = Objects.requireNonNull(arithmetic, "arithmetic is null");
	}
	
	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final TruncationPolicy tp : TestSettings.POLICIES) {
				final DecimalArithmetic arith = s.getArithmetic(tp);
				data.add(new Object[] {s, tp, arith});
			}
		}
		return data;
	}

	@Test
	public void shouldDeriveOtherScale() {
		for (int scale = Scales.MIN_SCALE; scale <= Scales.MAX_SCALE; scale++) {
			final DecimalArithmetic expected = Scales.getScaleMetrics(scale).getArithmetic(arithmetic.getTruncationPolicy());
			assertSame("unexpected arithmetic instance for scale " + scale, expected, arithmetic.deriveArithmetic(scale));
		}
	}

	@Test
	public void shouldDeriveOtherRoundingMode() {
		for (final RoundingMode roundingMode : RoundingMode.values()) {
			final TruncationPolicy tp = arithmetic.getOverflowMode().isChecked() ? CheckedRounding.valueOf(roundingMode) : UncheckedRounding.valueOf(roundingMode);
			final DecimalArithmetic expected = arithmetic.getScaleMetrics().getArithmetic(tp);
			assertSame("unexpected arithmetic instance for rounding mode " + roundingMode, expected, arithmetic.deriveArithmetic(roundingMode));
		}
	}

	@Test
	public void shouldDeriveOtherOverflowMode() {
		for (final OverflowMode overflowMode : OverflowMode.values()) {
			final TruncationPolicy tp = overflowMode.isChecked() ? CheckedRounding.valueOf(arithmetic.getRoundingMode()) : UncheckedRounding.valueOf(arithmetic.getRoundingMode());
			final DecimalArithmetic expected = arithmetic.getScaleMetrics().getArithmetic(tp);
			assertSame("unexpected arithmetic instance for overflow mode " + overflowMode, expected, arithmetic.deriveArithmetic(overflowMode));
		}
	}
	@Test
	public void shouldDeriveOtherRoundingModeAndOverflowMode() {
		for (final RoundingMode roundingMode : RoundingMode.values()) {
			for (final OverflowMode overflowMode : OverflowMode.values()) {
				final TruncationPolicy tp = overflowMode.isChecked() ? CheckedRounding.valueOf(roundingMode) : UncheckedRounding.valueOf(roundingMode);
				final DecimalArithmetic expected = arithmetic.getScaleMetrics().getArithmetic(tp);
				assertSame("unexpected arithmetic instance for rounding/overflow mode " + roundingMode + "/" + overflowMode, expected, arithmetic.deriveArithmetic(roundingMode, overflowMode));
			}
		}
	}

	@Test
	public void shouldDeriveOtherTruncationPolicy() {
		for (final TruncationPolicy truncationPolicy : TruncationPolicy.VALUES) {
			final DecimalArithmetic expected = arithmetic.getScaleMetrics().getArithmetic(truncationPolicy);
			assertSame("unexpected arithmetic instance for truncation policy " + truncationPolicy, expected, arithmetic.deriveArithmetic(truncationPolicy));
		}
	}
}
