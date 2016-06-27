/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.scale;

import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.FLOOR;
import static java.math.RoundingMode.HALF_EVEN;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UNNECESSARY;
import static org.decimal4j.truncate.OverflowMode.CHECKED;
import static org.decimal4j.truncate.OverflowMode.UNCHECKED;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.arithmetic.CheckedScale0fRoundingArithmetic;
import org.decimal4j.arithmetic.CheckedScale0fTruncatingArithmetic;
import org.decimal4j.arithmetic.UncheckedScale0fRoundingArithmetic;
import org.decimal4j.arithmetic.UncheckedScale0fTruncatingArithmetic;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Scale class for decimals with {@link #getScale() scale} 0 and
 * {@link #getScaleFactor() scale factor} 1. Decimals with scale zero are
 * essentially longs.
 */
public enum Scale0f implements ScaleMetrics {

	/**
	 * The singleton instance for scale 0.
	 */
	INSTANCE;

	/**
	 * The scale value <code>0</code>.
	 */
	public static final int SCALE = 0;

	/**
	 * The scale factor <code>10<sup>0</sup></code>.
	 */
	public static final long SCALE_FACTOR = 1L;
	
	/** Long.numberOfLeadingZeros(SCALE_FACTOR)*/
	private static final int NLZ_SCALE_FACTOR = 63;

	private static final long LONG_MASK = 0xffffffffL;

	private static final DecimalArithmetic[] UNCHECKED_ARITHMETIC = initArithmetic(UNCHECKED);
	private static final DecimalArithmetic[] CHECKED_ARITHMETIC = initArithmetic(CHECKED);

	private static final DecimalArithmetic DEFAULT_ARITHMETIC = UNCHECKED_ARITHMETIC[HALF_UP.ordinal()];
	private static final DecimalArithmetic DEFAULT_CHECKED_ARITHMETIC = CHECKED_ARITHMETIC[HALF_UP.ordinal()];
	private static final DecimalArithmetic ROUNDING_DOWN_ARITHMETIC = UNCHECKED_ARITHMETIC[DOWN.ordinal()];
	private static final DecimalArithmetic ROUNDING_FLOOR_ARITHMETIC = UNCHECKED_ARITHMETIC[FLOOR.ordinal()];
	private static final DecimalArithmetic ROUNDING_HALF_EVEN_ARITHMETIC = UNCHECKED_ARITHMETIC[HALF_EVEN.ordinal()];
	private static final DecimalArithmetic ROUNDING_UNNECESSARY_ARITHMETIC = UNCHECKED_ARITHMETIC[UNNECESSARY.ordinal()];

	private static final DecimalArithmetic[] initArithmetic(OverflowMode overflowMode) {
		final boolean checked = overflowMode == CHECKED;
		final DecimalArithmetic[] arith = new DecimalArithmetic[DecimalRounding.VALUES.size()];
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final int index = dr.getRoundingMode().ordinal();
			if (dr == DecimalRounding.DOWN) {
				arith[index] = checked ? CheckedScale0fTruncatingArithmetic.INSTANCE
						: UncheckedScale0fTruncatingArithmetic.INSTANCE;
			} else {
				arith[index] = checked ? new CheckedScale0fRoundingArithmetic(dr)
						: new UncheckedScale0fRoundingArithmetic(dr);
			}
		}
		return arith;
	}

	@Override
	public final int getScale() {
		return SCALE;
	}

	@Override
	public final long getScaleFactor() {
		return SCALE_FACTOR;
	}

	@Override
	public final int getScaleFactorNumberOfLeadingZeros() {
		return NLZ_SCALE_FACTOR;
	}

	@Override
	public final BigInteger getScaleFactorAsBigInteger() {
		return BigInteger.ONE;
	}

	@Override
	public final BigDecimal getScaleFactorAsBigDecimal() {
		return BigDecimal.ONE;
	}

	@Override
	public final long getMaxIntegerValue() {
		return Long.MAX_VALUE;
	}

	@Override
	public final long getMinIntegerValue() {
		return Long.MIN_VALUE;
	}

	@Override
	public final boolean isValidIntegerValue(long value) {
		return true;
	}

	@Override
	public final long multiplyByScaleFactor(long factor) {
		return factor;
	}

	@Override
	public final long multiplyByScaleFactorExact(long factor) {
		return factor;
	}

	@Override
	public final long mulloByScaleFactor(int factor) {
		return factor & LONG_MASK;
	}

	@Override
	public final long mulhiByScaleFactor(int factor) {
		return 0;
	}

	@Override
	public final long divideByScaleFactor(long dividend) {
		return dividend;
	}

	@Override
	public final long divideUnsignedByScaleFactor(long unsignedDividend) {
		return unsignedDividend;
	}

	@Override
	public final long moduloByScaleFactor(long dividend) {
		return 0;
	}

	@Override
	public final String toString(long value) {
		return Long.toString(value);
	}

	@Override
	public final DecimalArithmetic getDefaultArithmetic() {
		return DEFAULT_ARITHMETIC;
	}

	@Override
	public final DecimalArithmetic getDefaultCheckedArithmetic() {
		return DEFAULT_CHECKED_ARITHMETIC;
	}

	@Override
	public final DecimalArithmetic getRoundingDownArithmetic() {
		return ROUNDING_DOWN_ARITHMETIC;
	}

	@Override
	public final DecimalArithmetic getRoundingFloorArithmetic() {
		return ROUNDING_FLOOR_ARITHMETIC;
	}

	@Override
	public final DecimalArithmetic getRoundingHalfEvenArithmetic() {
		return ROUNDING_HALF_EVEN_ARITHMETIC;
	}

	@Override
	public final DecimalArithmetic getRoundingUnnecessaryArithmetic() {
		return ROUNDING_UNNECESSARY_ARITHMETIC;
	}

	@Override
	public final DecimalArithmetic getArithmetic(RoundingMode roundingMode) {
		return UNCHECKED_ARITHMETIC[roundingMode.ordinal()];
	}

	@Override
	public final DecimalArithmetic getCheckedArithmetic(RoundingMode roundingMode) {
		return CHECKED_ARITHMETIC[roundingMode.ordinal()];
	}

	@Override
	public final DecimalArithmetic getArithmetic(TruncationPolicy truncationPolicy) {
		final OverflowMode overflow = truncationPolicy.getOverflowMode();
		final RoundingMode rounding = truncationPolicy.getRoundingMode();
		return (overflow == UNCHECKED ? UNCHECKED_ARITHMETIC : CHECKED_ARITHMETIC)[rounding.ordinal()];
	}

	@Override
	public final String toString() {
		return "Scale0f";
	}
}