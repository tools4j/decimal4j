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
package org.decimal4j.scale;

import java.math.RoundingMode;
import java.util.EnumMap;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.arithmetic.CheckedScale0fRoundingArithmetic;
import org.decimal4j.arithmetic.CheckedScale0fTruncatingArithmetic;
import org.decimal4j.arithmetic.UncheckedScale0fRoundingArithmetic;
import org.decimal4j.arithmetic.UncheckedScale0fTruncatingArithmetic;
import org.decimal4j.truncate.DecimalRounding;

/**
 * Scale class for decimals with {@link #getScale() scale} 0 (aka as integers)
 * and {@link #getScaleFactor() scale factor} 1.
 */
public final class Scale0f extends AbstractScale {

	/**
	 * The singleton instance for scale 0.
	 */
	public static final Scale0f INSTANCE = new Scale0f();

	@Override
	protected EnumMap<RoundingMode, DecimalArithmetic> initArithmetic() {
		final EnumMap<RoundingMode, DecimalArithmetic> map = new EnumMap<RoundingMode, DecimalArithmetic>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, UncheckedScale0fTruncatingArithmetic.INSTANCE);
			} else {
				map.put(roundingMode, new UncheckedScale0fRoundingArithmetic(dr));
			}
		}
		return map;
	}

	@Override
	protected EnumMap<RoundingMode, DecimalArithmetic> initCheckedArithmetic() {
		final EnumMap<RoundingMode, DecimalArithmetic> map = new EnumMap<RoundingMode, DecimalArithmetic>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, CheckedScale0fTruncatingArithmetic.INSTANCE);
			} else {
				map.put(roundingMode, new CheckedScale0fRoundingArithmetic(dr));
			}
		}
		return map;
	}

	@Override
	public int getScale() {
		return 0;
	}

	@Override
	public long getScaleFactor() {
		return 1;
	}

	@Override
	public final int getScaleFactorNumberOfLeadingZeros() {
		return 63;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor;
	}

	@Override
	public long multiplyByScaleFactorExact(long factor) {
		return factor;
	}
	
	@Override
	public long mulloByScaleFactor(int factor) {
		return factor & LONG_MASK;
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
		return 0;
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend;
	}
	
	@Override
	public long divideUnsignedByScaleFactor(long unsignedDividend) {
		return unsignedDividend;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return 0;
	}
}