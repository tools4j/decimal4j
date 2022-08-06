/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 decimal4j (tools4j), Marco Terzer
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

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scale9f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;

/**
 * Provides methods to calculate squares.
 */
final class Square {

	private static final Scale9f SCALE9F = Scale9f.INSTANCE;
	
	/**
	 * Value representing: <code>floor(sqrt(Long.MAX_VALUE))</code>
	 */
	static final long SQRT_MAX_VALUE = 3037000499L;

	// necessary and sufficient condition that square fits in long
	private static final boolean doesSquareFitInLong(long uDecimal) {
		return -SQRT_MAX_VALUE <= uDecimal & uDecimal <= SQRT_MAX_VALUE;
	}

	/**
	 * Calculates the square {@code uDecimal^2 / scaleFactor} without rounding.
	 * Overflows are silently truncated.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics defining the scale
	 * @param uDecimal
	 *            the unscaled decimal value to square
	 * @return the square result without rounding
	 */
	public static final long square(ScaleMetrics scaleMetrics, long uDecimal) {
		if (doesSquareFitInLong(uDecimal)) {
			// square fits in long, just do it
			return scaleMetrics.divideByScaleFactor(uDecimal * uDecimal);
		}
		final int scale = scaleMetrics.getScale();
		if (scale <= 9) {
			// use scale to split into 2 parts: i (integral) and f (fractional)
			// with this scale, the low order product f*f fits in a long
			final long i = scaleMetrics.divideByScaleFactor(uDecimal);
			final long f = uDecimal - scaleMetrics.multiplyByScaleFactor(i);
			return scaleMetrics.multiplyByScaleFactor(i * i) + ((i * f) << 1) + scaleMetrics.divideByScaleFactor(f * f);
		} else {
			// use scale9 to split into 2 parts: h (high) and l (low)
			final ScaleMetrics scaleDiff09 = Scales.getScaleMetrics(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.getScaleMetrics(18 - scale);
			final long h = SCALE9F.divideByScaleFactor(uDecimal);
			final long l = uDecimal - SCALE9F.multiplyByScaleFactor(h);
			final long hxl = h * l;
			final long lxld = SCALE9F.divideByScaleFactor(l * l);
			final long hxld = scaleDiff09.divideByScaleFactor(hxl);
			final long hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld);
			return scaleDiff18.multiplyByScaleFactor(h * h) + (hxld << 1)
					+ scaleDiff09.divideByScaleFactor((hxlr << 1) + lxld);
		}
	}

	/**
	 * Calculates the square {@code uDecimal^2 / scaleFactor} applying the
	 * specified rounding for truncated decimals. Overflows are silently
	 * truncated.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics defining the scale
	 * @param rounding
	 *            the rounding to apply for truncated decimals
	 * @param uDecimal
	 *            the unscaled decimal value to square
	 * @return the square result with rounding
	 */
	public static final long square(ScaleMetrics scaleMetrics, DecimalRounding rounding, long uDecimal) {
		if (doesSquareFitInLong(uDecimal)) {
			// square fits in long, just do it
			return square32(scaleMetrics, rounding, uDecimal);
		}
		final int scale = scaleMetrics.getScale();
		if (scale <= 9) {
			// use scale to split into 2 parts: i (integral) and f (fractional)
			// with this scale, the low order product f*f fits in a long
			final long i = scaleMetrics.divideByScaleFactor(uDecimal);
			final long f = uDecimal - scaleMetrics.multiplyByScaleFactor(i);
			final long fxf = f * f;
			final long fxfd = scaleMetrics.divideByScaleFactor(fxf);
			final long fxfr = fxf - scaleMetrics.multiplyByScaleFactor(fxfd);
			final long unrounded = scaleMetrics.multiplyByScaleFactor(i * i) + ((i * f) << 1) + fxfd;
			return unrounded
					+ Rounding.calculateRoundingIncrement(rounding, unrounded, fxfr, scaleMetrics.getScaleFactor());
		} else {
			// use scale9 to split into 2 parts: h (high) and l (low)
			final ScaleMetrics scaleDiff09 = Scales.getScaleMetrics(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.getScaleMetrics(18 - scale);
			final long h = SCALE9F.divideByScaleFactor(uDecimal);
			final long l = uDecimal - SCALE9F.multiplyByScaleFactor(h);
			final long hxl = h * l;
			final long lxl = l * l;
			final long lxld = SCALE9F.divideByScaleFactor(lxl);
			final long hxld = scaleDiff09.divideByScaleFactor(hxl);
			final long hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld);
			final long lxlr = lxl - SCALE9F.multiplyByScaleFactor(lxld);
			final long hxlx2_lxl = (hxlr << 1) + lxld;
			final long hxlx2_lxld = scaleDiff09.divideByScaleFactor(hxlx2_lxl);
			final long hxlx2_lxlr = hxlx2_lxl - scaleDiff09.multiplyByScaleFactor(hxlx2_lxld);
			final long unrounded = scaleDiff18.multiplyByScaleFactor(h * h) + (hxld << 1) + hxlx2_lxld;
			final long remainder = SCALE9F.multiplyByScaleFactor(hxlx2_lxlr) + lxlr;
			return unrounded + Rounding.calculateRoundingIncrement(rounding, unrounded, remainder,
					scaleMetrics.getScaleFactor());
		}
	}

	// PRECONDITION: uDecimal <= SQRT_MAX_VALUE
	private static final long square32(ScaleMetrics scaleMetrics, DecimalRounding rounding, long uDecimal) {
		final long u2 = uDecimal * uDecimal;
		final long u2d = scaleMetrics.divideByScaleFactor(u2);
		final long u2r = u2 - scaleMetrics.multiplyByScaleFactor(u2d);
		return u2d + Rounding.calculateRoundingIncrement(rounding, u2d, u2r, scaleMetrics.getScaleFactor());
	}

	/**
	 * Calculates the square {@code uDecimal^2 / scaleFactor} truncating the
	 * result if necessary. Throws an exception if an overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param uDecimal
	 *            the unscaled decimal value to square
	 * @return the square result without rounding
	 */
	public static final long squareChecked(DecimalArithmetic arith, long uDecimal) {
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		if (doesSquareFitInLong(uDecimal)) {
			// square fits in long, just do it
			return scaleMetrics.divideByScaleFactor(uDecimal * uDecimal);
		}
		final int scale = scaleMetrics.getScale();
		try {
			if (scale <= 9) {
				// use scale to split into 2 parts: i (integral) and f
				// (fractional)
				// with this scale, the low order product f*f fits in a long
				final long i = scaleMetrics.divideByScaleFactor(uDecimal);
				final long f = uDecimal - scaleMetrics.multiplyByScaleFactor(i);
				final long ixi = Checked.multiplyLong(i, i);// checked
				final long ixf = i * f;// cannot overflow
				final long fxf = scaleMetrics.divideByScaleFactor(f * f);// unchecked:ok
				// check whether we can multiply ixf by 2
				if (ixf < 0)
					throw new ArithmeticException("Overflow: " + ixf + "<<1");
				final long ixfx2 = ixf << 1;
				// add it all up now, every operation checked
				long result = scaleMetrics.multiplyByScaleFactorExact(ixi);
				result = Checked.addLong(result, ixfx2);
				result = Checked.addLong(result, fxf);
				return result;
			} else {
				// use scale9 to split into 2 parts: h (high) and l (low)
				final ScaleMetrics scaleDiff09 = Scales.getScaleMetrics(scale - 9);
				final ScaleMetrics scaleDiff18 = Scales.getScaleMetrics(18 - scale);
				final long h = SCALE9F.divideByScaleFactor(uDecimal);
				final long l = uDecimal - SCALE9F.multiplyByScaleFactor(h);

				final long hxh = Checked.multiplyLong(h, h);// checked
				final long hxl = h * l;// cannot overflow
				final long lxld = SCALE9F.divideByScaleFactor(l * l);// unchecked:ok
				final long hxld = scaleDiff09.divideByScaleFactor(hxl);
				final long hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld);
				// check whether we can multiply hxld by 2
				if (hxld < 0)
					throw new ArithmeticException("Overflow: " + hxld + "<<1");
				final long hxldx2 = hxld << 1;
				// add it all up now, every operation checked
				long result = scaleDiff18.multiplyByScaleFactorExact(hxh);
				result = Checked.addLong(result, hxldx2);
				result = Checked.addLong(result, scaleDiff09.divideByScaleFactor((hxlr << 1) + lxld));
				return result;
			}
		} catch (ArithmeticException e) {
			throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + arith.toString(uDecimal) + "^2", e);
		}
	}

	/**
	 * Calculates the square {@code uDecimal^2 / scaleFactor} applying the
	 * specified rounding for truncated decimals. Throws an exception if an
	 * overflow occurs.
	 * 
	 * @param arith
	 *            the arithmetic associated with the value
	 * @param rounding
	 *            the rounding to apply for truncated decimals
	 * @param uDecimal
	 *            the unscaled decimal value to square
	 * @return the square result with rounding
	 */
	public static final long squareChecked(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal) {
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		if (doesSquareFitInLong(uDecimal)) {
			// square fits in long, just do it
			return square32(scaleMetrics, rounding, uDecimal);
		}
		try {
			final int scale = scaleMetrics.getScale();
			if (scale <= 9) {
				// use scale to split into 2 parts: i (integral) and f
				// (fractional)
				final long i = scaleMetrics.divideByScaleFactor(uDecimal);
				final long f = uDecimal - scaleMetrics.multiplyByScaleFactor(i);

				final long ixi = Checked.multiplyLong(i, i);
				final long fxf = f * f;// low order product f*f fits in a long
				final long ixf = i * f;// cannot overflow
				// check whether we can multiply ixf by 2
				if (ixf < 0)
					throw new ArithmeticException("Overflow: " + ixf + "<<1");
				final long ixfx2 = ixf << 1;

				final long fxfd = scaleMetrics.divideByScaleFactor(fxf);
				final long fxfr = fxf - scaleMetrics.multiplyByScaleFactor(fxfd);

				// add it all up now, every operation checked
				long unrounded = scaleMetrics.multiplyByScaleFactorExact(ixi);
				unrounded = Checked.addLong(unrounded, ixfx2);
				unrounded = Checked.addLong(unrounded, fxfd);
				return Checked.addLong(unrounded,
						Rounding.calculateRoundingIncrement(rounding, unrounded, fxfr, scaleMetrics.getScaleFactor()));
			} else {
				// use scale9 to split into 2 parts: h (high) and l (low)
				final ScaleMetrics scaleDiff09 = Scales.getScaleMetrics(scale - 9);
				final ScaleMetrics scaleDiff18 = Scales.getScaleMetrics(18 - scale);
				final long h = SCALE9F.divideByScaleFactor(uDecimal);
				final long l = uDecimal - SCALE9F.multiplyByScaleFactor(h);

				final long hxh = Checked.multiplyLong(h, h);
				final long hxl = h * l;// cannot overflow

				final long hxld = scaleDiff09.divideByScaleFactor(hxl);
				final long hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld);
				final long hxldx2 = hxld << 1;// cannot overflow

				final long lxl = l * l;// cannot overflow
				final long lxld = SCALE9F.divideByScaleFactor(lxl);
				final long lxlr = lxl - SCALE9F.multiplyByScaleFactor(lxld);

				final long hxlx2_lxl = (hxlr << 1) + lxld;// cannot overflow
				final long hxlx2_lxld = scaleDiff09.divideByScaleFactor(hxlx2_lxl);
				final long hxlx2_lxlr = hxlx2_lxl - scaleDiff09.multiplyByScaleFactor(hxlx2_lxld);

				// add it all up now, every operation checked
				long unrounded = scaleDiff18.multiplyByScaleFactorExact(hxh);
				unrounded = Checked.addLong(unrounded, hxldx2);
				unrounded = Checked.addLong(unrounded, hxlx2_lxld);
				final long remainder = SCALE9F.multiplyByScaleFactor(hxlx2_lxlr) + lxlr;// cannot
																						// overflow
				return Checked.addLong(unrounded, Rounding.calculateRoundingIncrement(rounding, unrounded, remainder,
						scaleMetrics.getScaleFactor()));
			}
		} catch (ArithmeticException e) {
			Exceptions.rethrowIfRoundingNecessary(e);
			throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + arith.toString(uDecimal) + "^2", e);
		}
	}

	// no instances
	private Square() {
	}
}
