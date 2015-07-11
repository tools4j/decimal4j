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
package org.decimal4j.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Base class for all arithmetic implementations providing operations which are
 * common irrespective of {@link #getScale() scale}, {@link RoundingMode
 * rounding mode} and {@link #getOverflowMode() overflow mode}.
 */
abstract public class AbstractArithmetic implements DecimalArithmetic {

	@Override
	public final TruncationPolicy getTruncationPolicy() {
		return getOverflowMode().getTruncationPolicyFor(getRoundingMode());
	}

	@Override
	public final DecimalArithmetic deriveArithmetic(int scale) {
		if (scale != getScale()) {
			return Scales.getScaleMetrics(scale).getArithmetic(getTruncationPolicy());
		}
		return this;
	}

	@Override
	public final DecimalArithmetic deriveArithmetic(RoundingMode roundingMode) {
		return deriveArithmetic(roundingMode, getOverflowMode());
	}

	@Override
	public final DecimalArithmetic deriveArithmetic(RoundingMode roundingMode, OverflowMode overflowMode) {
		if (roundingMode != getRoundingMode() | overflowMode != getOverflowMode()) {
			return overflowMode.isChecked() ? getScaleMetrics().getCheckedArithmetic(roundingMode) : getScaleMetrics().getArithmetic(roundingMode);
		}
		return this;
	}

	@Override
	public final DecimalArithmetic deriveArithmetic(OverflowMode overflowMode) {
		return deriveArithmetic(getRoundingMode(), overflowMode);
	}

	@Override
	public final DecimalArithmetic deriveArithmetic(TruncationPolicy truncationPolicy) {
		return deriveArithmetic(truncationPolicy.getRoundingMode(), truncationPolicy.getOverflowMode());
	}

	@Override
	public final int signum(long uDecimal) {
		return Long.signum(uDecimal);
	}

	@Override
	public final int compare(long uDecimal1, long uDecimal2) {
		return Long.compare(uDecimal1, uDecimal2);
	}
	
	@Override
	public final int compareToUnscaled(long uDecimal, long unscaled, int scale) {
		return Compare.compareUnscaled(uDecimal, getScale(), unscaled, scale);
	}

	@Override
	public final long fromBigInteger(BigInteger value) {
		return BigIntegerConversion.bigIntegerToUnscaled(getScaleMetrics(), value);
	}

	@Override
	public final BigDecimal toBigDecimal(long uDecimal) {
		return BigDecimalConversion.unscaledToBigDecimal(getScaleMetrics(), uDecimal);
	}

	@Override
	public final BigDecimal toBigDecimal(long uDecimal, int scale) {
		return BigDecimalConversion.unscaledToBigDecimal(getScaleMetrics(), getRoundingMode(), uDecimal, scale);
	}

}
