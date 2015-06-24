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

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.scale.Scale9f;

/**
 * Provides methods to calculate multiplication results.
 */
final class Mul {

	//floor(sqrt(Long.MAX_VALUE))
	private static final long SQRT_MAX_VALUE = 3037000499L;

	//sufficient (but not necessary) condition that product fits in long
	private static boolean doesProductFitInLong(long uDecimal1, long uDecimal2) {
		if (-SQRT_MAX_VALUE <= uDecimal1 & uDecimal1 <= SQRT_MAX_VALUE & -SQRT_MAX_VALUE <= uDecimal2 & uDecimal2 <= SQRT_MAX_VALUE) {
			return true;
		}
		return false;
		//NOTE: not worth checking (too much overhead for too few special cases):
//		final int leadingZeros = Long.numberOfLeadingZeros(uDecimal1) + Long.numberOfLeadingZeros(~uDecimal1) + Long.numberOfLeadingZeros(uDecimal2) + Long.numberOfLeadingZeros(~uDecimal2);
//		return leadingZeros > Long.SIZE + 1;
	}
	//necessary and sufficient condition that square fits in long
	private static boolean doesSquareFitInLong(long uDecimal) {
		return -SQRT_MAX_VALUE <= uDecimal & uDecimal <= SQRT_MAX_VALUE;
	}
	
	/**
	 * Calculates the multiple {@code uDecimal1 * uDecimal2 / scaleFactor}
	 * without rounding.
	 * 
	 * @param arith
	 *            the arithmetic with access to scale metrics etc.
	 * @param uDecimal1
	 *            the first unscaled decimal factor
	 * @param uDecimal2
	 *            the second unscaled decimal factor
	 * @return the multiplication result without rounding
	 */
	public static long multiply(DecimalArithmetic arith, long uDecimal1, long uDecimal2) {
		final SpecialMultiplicationResult special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2);
		if (special != null) {
			return special.multiply(arith, uDecimal1, uDecimal2);
		}
		
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();

		if (doesProductFitInLong(uDecimal1, uDecimal2)) {
			//product fits in long, just do it
			return scaleMetrics.divideByScaleFactor(uDecimal1 * uDecimal2);
		}

		final int scale = scaleMetrics.getScale();
		if (scale <= 9) {
			//use scale to split into 2 parts: i (integral) and f (fractional)
			//with this scale, the low order product f1*f2 fits in a long
			final long i1 = scaleMetrics.divideByScaleFactor(uDecimal1);
			final long i2 = scaleMetrics.divideByScaleFactor(uDecimal2);
			final long f1 = uDecimal1 - scaleMetrics.multiplyByScaleFactor(i1);
			final long f2 = uDecimal2 - scaleMetrics.multiplyByScaleFactor(i2);
			return scaleMetrics.multiplyByScaleFactor(i1 * i2) + i1 * f2 + i2 * f1 + scaleMetrics.divideByScaleFactor(f1 * f2);
		} else {
			//use scale9 to split into 2 parts: h (high) and l (low)
			final Scale9f scale9f = Scale9f.INSTANCE;
			final ScaleMetrics scaleDiff09 = Scales.getScaleMetrics(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.getScaleMetrics(18 - scale);
			final long h1 = scale9f.divideByScaleFactor(uDecimal1);
			final long h2 = scale9f.divideByScaleFactor(uDecimal2);
			final long l1 = uDecimal1 - scale9f.multiplyByScaleFactor(h1);
			final long l2 = uDecimal2 - scale9f.multiplyByScaleFactor(h2);
			final long h1xl2 = h1 * l2;
			final long h2xl1 = h2 * l1;
			final long l1xl2d = scale9f.divideByScaleFactor(l1 * l2);
			final long h1xl2d = scaleDiff09.divideByScaleFactor(h1xl2);
			final long h2xl1d = scaleDiff09.divideByScaleFactor(h2xl1);
			final long h1xl2r = h1xl2 - scaleDiff09.multiplyByScaleFactor(h1xl2d);
			final long h2xl1r = h2xl1 - scaleDiff09.multiplyByScaleFactor(h2xl1d);
			return scaleDiff18.multiplyByScaleFactor(h1 * h2) + h1xl2d + h2xl1d + scaleDiff09.divideByScaleFactor(h1xl2r + h2xl1r + l1xl2d); 
		}
	}

	/**
	 * Calculates the multiple {@code uDecimal1 * uDecimal2 / scaleFactor}
	 * applying the specified rounding for truncated decimals.
	 * 
	 * @param arith
	 *            the arithmetic with access to scale metrics etc.
	 * @param rounding
	 *            the rounding to apply for truncated decimals
	 * @param uDecimal1
	 *            the first unscaled decimal factor
	 * @param uDecimal2
	 *            the second unscaled decimal factor
	 * @return the multiplication result with rounding
	 */
	public static long multiply(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal1, long uDecimal2) {
		final SpecialMultiplicationResult special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2);
		if (special != null) {
			return special.multiply(arith, uDecimal1, uDecimal2);
		}

		if (doesProductFitInLong(uDecimal1, uDecimal2)) {
			//product fits in long, just do it
			return multiply32(arith, rounding, uDecimal1, uDecimal2);
		}
		
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final int scale = scaleMetrics.getScale();

		if (scale <= 9) {
			//use scale to split into 2 parts: i (integral) and f (fractional)
			//with this scale, the low order product f1*f2 fits in a long
			final long i1 = scaleMetrics.divideByScaleFactor(uDecimal1);
			final long i2 = scaleMetrics.divideByScaleFactor(uDecimal2);
			final long f1 = uDecimal1 - scaleMetrics.multiplyByScaleFactor(i1);
			final long f2 = uDecimal2 - scaleMetrics.multiplyByScaleFactor(i2);
			final long f1xf2 = f1 * f2;
			final long f1xf2d = scaleMetrics.divideByScaleFactor(f1xf2);
			final long f1xf2r = f1xf2 - scaleMetrics.multiplyByScaleFactor(f1xf2d);
			final long unrounded = scaleMetrics.multiplyByScaleFactor(i1 * i2) + i1 * f2 + i2 * f1 + f1xf2d;
			return unrounded + RoundingUtil.calculateRoundingIncrement(rounding, unrounded, f1xf2r, scaleMetrics.getScaleFactor());
		} else {
			//use scale9 to split into 2 parts: h (high) and l (low)
			final ScaleMetrics scale9f = Scale9f.INSTANCE;
			final ScaleMetrics scaleDiff09 = Scales.getScaleMetrics(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.getScaleMetrics(18 - scale);
			final long h1 = scale9f.divideByScaleFactor(uDecimal1);
			final long h2 = scale9f.divideByScaleFactor(uDecimal2);
			final long l1 = uDecimal1 - scale9f.multiplyByScaleFactor(h1);
			final long l2 = uDecimal2 - scale9f.multiplyByScaleFactor(h2);
			final long h1xl2 = h1 * l2;
			final long h2xl1 = h2 * l1;
			final long l1xl2 = l1 * l2;
			final long l1xl2d = scale9f.divideByScaleFactor(l1xl2);
			final long h1xl2d = scaleDiff09.divideByScaleFactor(h1xl2);
			final long h2xl1d = scaleDiff09.divideByScaleFactor(h2xl1);
			final long h1xl2r = h1xl2 - scaleDiff09.multiplyByScaleFactor(h1xl2d);
			final long h2xl1r = h2xl1 - scaleDiff09.multiplyByScaleFactor(h2xl1d);
			final long l1xl2r = l1xl2 - scale9f.multiplyByScaleFactor(l1xl2d);
			final long h1xl2_h2xl1_l1xl1 = h1xl2r + h2xl1r + l1xl2d; 
			final long h1xl2_h2xl1_l1xl1d = scaleDiff09.divideByScaleFactor(h1xl2_h2xl1_l1xl1); 
			final long h1xl2_h2xl1_l1xl1r = h1xl2_h2xl1_l1xl1 - scaleDiff09.multiplyByScaleFactor(h1xl2_h2xl1_l1xl1d); 
			final long unrounded = scaleDiff18.multiplyByScaleFactor(h1 * h2) + h1xl2d + h2xl1d + h1xl2_h2xl1_l1xl1d;
			final long remainder = scale9f.multiplyByScaleFactor(h1xl2_h2xl1_l1xl1r) + l1xl2r;
			return unrounded + RoundingUtil.calculateRoundingIncrement(rounding, unrounded, remainder, scaleMetrics.getScaleFactor());
		}
	}
	
	private static long multiply32(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal1, long uDecimal2) {
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final long u1xu2 = uDecimal1 * uDecimal2;
		final long u1xu2d = scaleMetrics.divideByScaleFactor(u1xu2);
		final long u1xu2r = u1xu2 - scaleMetrics.multiplyByScaleFactor(u1xu2d);
		return u1xu2d + RoundingUtil.calculateRoundingIncrement(rounding, u1xu2d, u1xu2r, scaleMetrics.getScaleFactor());
	}

	/**
	 * Calculates the multiple {@code uDecimal1 * uDecimal2 / scaleFactor}
	 * with rounding.
	 * 
	 * @param arith
	 *            the arithmetic with access to scale metrics etc.
	 * @param rounding
	 *            the rounding to apply for truncated decimals
	 * @param uDecimal1
	 *            the first unscaled decimal factor
	 * @param uDecimal2
	 *            the second unscaled decimal factor
	 *            
	 * @return the multiplication result with rounding and overflow checking
	 */
	// TODO refactor/reconcile the rounding/overflow checking versions of these methods
	public static long multiplyChecked(final DecimalArithmetic arith, final DecimalRounding rounding, final long uDecimal1, final long uDecimal2) {
		try {
			final SpecialMultiplicationResult special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2);
			if (special != null) {
				return special.multiply(arith, uDecimal1, uDecimal2);
			}
	
			if (doesProductFitInLong(uDecimal1, uDecimal2)) {
				//product fits in long, just do it
				return multiply32(arith, rounding, uDecimal1, uDecimal2);
			}

			final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
			final int scale = scaleMetrics.getScale();

			if (scale <= 9) {
				//use scale to split into 2 parts: i (integral) and f (fractional)
				//with this scale, the low order product f1*f2 fits in a long
				final long i1 = scaleMetrics.divideByScaleFactor(uDecimal1);
				final long i2 = scaleMetrics.divideByScaleFactor(uDecimal2);
				final long f1 = uDecimal1 - scaleMetrics.multiplyByScaleFactor(i1);
				final long f2 = uDecimal2 - scaleMetrics.multiplyByScaleFactor(i2);
				final long f1xf2 = f1 * f2;
				final long f1xf2d = scaleMetrics.divideByScaleFactor(f1xf2);
				final long f1xf2r = f1xf2 - scaleMetrics.multiplyByScaleFactor(f1xf2d);
				
				final long i1xi2 = Checked.multiplyLong(i1, i2);//checked
				final long i1xf2 = i1 * f2;//cannot overflow
				final long i2xf1 = i2 * f1;//cannot overflow
	
				//add it all up now, every operation checked
				long result = scaleMetrics.multiplyByScaleFactorExact(i1xi2);
				result = Checked.addLong(result, i1xf2);
				result = Checked.addLong(result, i2xf1);
				result = Checked.addLong(result, f1xf2d);
				
				return result + RoundingUtil.calculateRoundingIncrement(rounding, result, f1xf2r, scaleMetrics.getScaleFactor());
			} else {
				//use scale9 to split into 2 parts: h (high) and l (low)
				final ScaleMetrics scale9f = Scale9f.INSTANCE;
				final ScaleMetrics scaleDiff09 = Scales.getScaleMetrics(scale - 9);
				final ScaleMetrics scaleDiff18 = Scales.getScaleMetrics(18 - scale);
				final long h1 = scale9f.divideByScaleFactor(uDecimal1);
				final long h2 = scale9f.divideByScaleFactor(uDecimal2);
				final long l1 = uDecimal1 - scale9f.multiplyByScaleFactor(h1);
				final long l2 = uDecimal2 - scale9f.multiplyByScaleFactor(h2);
				final long h1xl2 = h1 * l2;
				final long h2xl1 = h2 * l1;
				final long l1xl2 = l1 * l2;
				final long l1xl2d = scale9f.divideByScaleFactor(l1xl2);
				final long h1xl2d = scaleDiff09.divideByScaleFactor(h1xl2);
				final long h2xl1d = scaleDiff09.divideByScaleFactor(h2xl1);
				final long h1xl2r = h1xl2 - scaleDiff09.multiplyByScaleFactor(h1xl2d);
				final long h2xl1r = h2xl1 - scaleDiff09.multiplyByScaleFactor(h2xl1d);
				final long l1xl2r = l1xl2 - scale9f.multiplyByScaleFactor(l1xl2d);
				final long h1xl2_h2xl1_l1xl1 = h1xl2r + h2xl1r + l1xl2d; 
				final long h1xl2_h2xl1_l1xl1d = scaleDiff09.divideByScaleFactor(h1xl2_h2xl1_l1xl1); 
				final long h1xl2_h2xl1_l1xl1r = h1xl2_h2xl1_l1xl1 - scaleDiff09.multiplyByScaleFactorExact(h1xl2_h2xl1_l1xl1d); 
				
				final long h1xh2 = Checked.multiplyLong(h1, h2);//checked
				//add it all up now, every operation checked
				long result = scaleDiff18.multiplyByScaleFactorExact(h1xh2);
				result = Checked.addLong(result, h1xl2d);
				result = Checked.addLong(result, h2xl1d);
				result = Checked.addLong(result, scaleDiff09.divideByScaleFactor(h1xl2r + h2xl1r + l1xl2d));//inner sum cannot overflow
				
				final long remainder = scale9f.multiplyByScaleFactor(h1xl2_h2xl1_l1xl1r) + l1xl2r;//cannot overflow
				return Checked.addLong(result, RoundingUtil.calculateRoundingIncrement(rounding, result, remainder, scaleMetrics.getScaleFactor()));
			}
		} catch (ArithmeticException e) {
			Exceptions.rethrowIfRoundingNecessary(e);
			throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + arith.toString(uDecimal1) + " * " + arith.toString(uDecimal2), e);
		}
	}
	
	/**
	 * Calculates the square {@code uDecimal^2 / scaleFactor} without rounding.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics defining the scale
	 * @param uDecimal
	 *            the unscaled decimal value to square
	 * @return the square result without rounding
	 */
	public static long square(ScaleMetrics scaleMetrics, long uDecimal) {
		if (doesSquareFitInLong(uDecimal)) {
			//square fits in long, just do it
			return scaleMetrics.divideByScaleFactor(uDecimal * uDecimal);
		}
		final int scale = scaleMetrics.getScale();
		if (scale <= 9) {
			//use scale to split into 2 parts: i (integral) and f (fractional)
			//with this scale, the low order product f*f fits in a long
			final long i = scaleMetrics.divideByScaleFactor(uDecimal);
			final long f = uDecimal - scaleMetrics.multiplyByScaleFactor(i);
			return scaleMetrics.multiplyByScaleFactor(i * i) + ((i * f) << 1) + scaleMetrics.divideByScaleFactor(f * f);
		} else {
			//use scale9 to split into 2 parts: h (high) and l (low)
			final ScaleMetrics scale9f = Scale9f.INSTANCE;
			final ScaleMetrics scaleDiff09 = Scales.getScaleMetrics(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.getScaleMetrics(18 - scale);
			final long h = scale9f.divideByScaleFactor(uDecimal);
			final long l = uDecimal - scale9f.multiplyByScaleFactor(h);
			final long hxl = h * l;
			final long lxld = scale9f.divideByScaleFactor(l * l);
			final long hxld = scaleDiff09.divideByScaleFactor(hxl);
			final long hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld);
			return scaleDiff18.multiplyByScaleFactor(h * h) + (hxld<<1) + scaleDiff09.divideByScaleFactor((hxlr<<1) + lxld); 
		}
	}

	/**
	 * Calculates the square {@code uDecimal^2 / scaleFactor}
	 * applying the specified rounding for truncated decimals.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics defining the scale
	 * @param rounding
	 *            the rounding to apply for truncated decimals
	 * @param uDecimal
	 *            the unscaled decimal value to square
	 * @return the square result with rounding
	 */
	public static long square(ScaleMetrics scaleMetrics, DecimalRounding rounding, long uDecimal) {
		if (doesSquareFitInLong(uDecimal)) {
			//square fits in long, just do it
			return square32(scaleMetrics, rounding, uDecimal);
		}
		final int scale = scaleMetrics.getScale();
		if (scale <= 9) {
			//use scale to split into 2 parts: i (integral) and f (fractional)
			//with this scale, the low order product f*f fits in a long
			final long i = scaleMetrics.divideByScaleFactor(uDecimal);
			final long f = uDecimal - scaleMetrics.multiplyByScaleFactor(i);
			final long fxf = f * f;
			final long fxfd = scaleMetrics.divideByScaleFactor(fxf);
			final long fxfr = fxf - scaleMetrics.multiplyByScaleFactor(fxfd);
			final long unrounded = scaleMetrics.multiplyByScaleFactor(i * i) + ((i * f) << 1) + fxfd;
			return unrounded + RoundingUtil.calculateRoundingIncrement(rounding, unrounded, fxfr, scaleMetrics.getScaleFactor());
		} else {
			//use scale9 to split into 2 parts: h (high) and l (low)
			final ScaleMetrics scale9f = Scale9f.INSTANCE;
			final ScaleMetrics scaleDiff09 = Scales.getScaleMetrics(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.getScaleMetrics(18 - scale);
			final long h = scale9f.divideByScaleFactor(uDecimal);
			final long l = uDecimal - scale9f.multiplyByScaleFactor(h);
			final long hxl = h * l;
			final long lxl = l * l;
			final long lxld = scale9f.divideByScaleFactor(lxl);
			final long hxld = scaleDiff09.divideByScaleFactor(hxl);
			final long hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld);
			final long lxlr = lxl - scale9f.multiplyByScaleFactor(lxld);
			final long hxlx2_lxl = (hxlr<<1) + lxld; 
			final long hxlx2_lxld = scaleDiff09.divideByScaleFactor(hxlx2_lxl); 
			final long hxlx2_lxlr = hxlx2_lxl - scaleDiff09.multiplyByScaleFactor(hxlx2_lxld); 
			final long unrounded = scaleDiff18.multiplyByScaleFactor(h * h) + (hxld<<1) + hxlx2_lxld;
			final long remainder = scale9f.multiplyByScaleFactor(hxlx2_lxlr) + lxlr;
			return unrounded + RoundingUtil.calculateRoundingIncrement(rounding, unrounded, remainder, scaleMetrics.getScaleFactor());
		}
	}

	//PRECONDITION: uDecimal <= SQRT_MAX_VALUE
	private static long square32(ScaleMetrics scaleMetrics, DecimalRounding rounding, long uDecimal) {
		final long u2 = uDecimal * uDecimal;
		final long u2d = scaleMetrics.divideByScaleFactor(u2);
		final long u2r = u2 - scaleMetrics.multiplyByScaleFactor(u2d);
		return u2d + RoundingUtil.calculateRoundingIncrement(rounding, u2d, u2r, scaleMetrics.getScaleFactor());
	}
	
	// TODO merge with other versions
	public static long squareChecked(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal) {
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		if (doesSquareFitInLong(uDecimal)) {
			//square fits in long, just do it
			return square32(scaleMetrics, rounding, uDecimal);
		}
		try {
			final int scale = scaleMetrics.getScale();
			if (scale <= 9) {
				// use scale to split into 2 parts: i (integral) and f (fractional)
				final long i = scaleMetrics.divideByScaleFactor(uDecimal);
				final long f = uDecimal - scaleMetrics.multiplyByScaleFactor(i);

				final long ixi = Checked.multiplyLong(i, i);
				final long fxf = f * f;// with this scale, the low order product f*f fits in a long
				final long ixf = i * f;//cannot overflow
				//check whether we can multiply ixf by 2
				if (ixf < 0) throw new ArithmeticException("Overflow: " + ixf + "<<1");
				final long ixfx2 = ixf << 1;

				final long fxfd = scaleMetrics.divideByScaleFactor(fxf);
				final long fxfr = fxf - scaleMetrics.multiplyByScaleFactor(fxfd);
				
				//add it all up now, every operation checked
				long unrounded = scaleMetrics.multiplyByScaleFactorExact(ixi);
				unrounded = Checked.addLong(unrounded, ixfx2);
				unrounded = Checked.addLong(unrounded, fxfd);
				return Checked.addLong(unrounded, RoundingUtil.calculateRoundingIncrement(rounding, unrounded, fxfr, scaleMetrics.getScaleFactor()));
			}
			else {
				// use scale9 to split into 2 parts: h (high) and l (low)
				final ScaleMetrics scale9f = Scale9f.INSTANCE;
				final ScaleMetrics scaleDiff09 = Scales.getScaleMetrics(scale - 9);
				final ScaleMetrics scaleDiff18 = Scales.getScaleMetrics(18 - scale);
				final long h = scale9f.divideByScaleFactor(uDecimal);
				final long l = uDecimal - scale9f.multiplyByScaleFactor(h);
				
				final long hxh = Checked.multiplyLong(h, h);
				final long hxl = h * l;//cannot overflow

				final long hxld = scaleDiff09.divideByScaleFactor(hxl);
				final long hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld);
				final long hxldx2 = hxld << 1;//cannot overflow

				final long lxl = l * l;//cannot overflow
				final long lxld = scale9f.divideByScaleFactor(lxl);
				final long lxlr = lxl - scale9f.multiplyByScaleFactor(lxld);
				
				final long hxlx2_lxl = (hxlr << 1) + lxld;//cannot overflow
				final long hxlx2_lxld = scaleDiff09.divideByScaleFactor(hxlx2_lxl);
				final long hxlx2_lxlr = hxlx2_lxl - scaleDiff09.multiplyByScaleFactor(hxlx2_lxld);

				//add it all up now, every operation checked
				long unrounded = scaleDiff18.multiplyByScaleFactorExact(hxh);
				unrounded = Checked.addLong(unrounded, hxldx2);
				unrounded = Checked.addLong(unrounded, hxlx2_lxld);
				final long remainder = scale9f.multiplyByScaleFactor(hxlx2_lxlr) + lxlr;//cannot overflow
				return Checked.addLong(unrounded, RoundingUtil.calculateRoundingIncrement(rounding, unrounded, remainder, scaleMetrics.getScaleFactor()));
			}
		} catch (ArithmeticException e) {
			Exceptions.rethrowIfRoundingNecessary(e);
			throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + arith.toString(uDecimal) + "^2", e);
		}
	}

	public static long multiplyChecked(DecimalArithmetic arith, long uDecimal1, long uDecimal2) {
		try {
			final SpecialMultiplicationResult special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2);
			if (special != null) {
				return special.multiply(arith, uDecimal1, uDecimal2);
			}

			final ScaleMetrics scaleMetrics = arith.getScaleMetrics();

			if (doesProductFitInLong(uDecimal1, uDecimal2)) {
				return scaleMetrics.divideByScaleFactor(uDecimal1 * uDecimal2);
			}
			
			final int scale = scaleMetrics.getScale();
			if (scale <= 9) {
				//use scale to split into 2 parts: i (integral) and f (fractional)
				//with this scale, the low order product f1*f2 fits in a long
				final long i1 = scaleMetrics.divideByScaleFactor(uDecimal1);
				final long i2 = scaleMetrics.divideByScaleFactor(uDecimal2);
				final long f1 = uDecimal1 - scaleMetrics.multiplyByScaleFactor(i1);
				final long f2 = uDecimal2 - scaleMetrics.multiplyByScaleFactor(i2);
				final long i1xi2 = Checked.multiplyLong(i1, i2);//checked
				final long i1xf2 = i1 * f2;//cannot overflow
				final long i2xf1 = i2 * f1;//cannot overflow
				final long f1xf2 = scaleMetrics.divideByScaleFactor(f1 * f2);//product fits for this scale, hence unchecked
				//add it all up now, every operation checked
				long result = scaleMetrics.multiplyByScaleFactorExact(i1xi2);
				result = Checked.addLong(result, i1xf2);
				result = Checked.addLong(result, i2xf1);
				result = Checked.addLong(result, f1xf2);
				return result;
			} else {
				//use scale9 to split into 2 parts: h (high) and l (low)
				final ScaleMetrics scale9f = Scale9f.INSTANCE;
				final ScaleMetrics scaleDiff09 = Scales.getScaleMetrics(scale - 9);
				final ScaleMetrics scaleDiff18 = Scales.getScaleMetrics(18 - scale);
				final long h1 = scale9f.divideByScaleFactor(uDecimal1);
				final long h2 = scale9f.divideByScaleFactor(uDecimal2);
				final long l1 = uDecimal1 - scale9f.multiplyByScaleFactor(h1);
				final long l2 = uDecimal2 - scale9f.multiplyByScaleFactor(h2);
				final long h1xh2 = Checked.multiplyLong(h1, h2);//checked
				final long h1xl2 = h1 * l2;//cannot overflow
				final long h2xl1 = h2 * l1;//cannot overflow
				final long l1xl2d = scale9f.divideByScaleFactor(l1 * l2);//product fits for scale 9, hence unchecked
				final long h1xl2d = scaleDiff09.divideByScaleFactor(h1xl2);
				final long h2xl1d = scaleDiff09.divideByScaleFactor(h2xl1);
				final long h1xl2r = h1xl2 - scaleDiff09.multiplyByScaleFactor(h1xl2d);
				final long h2xl1r = h2xl1 - scaleDiff09.multiplyByScaleFactor(h2xl1d);
				//add it all up now, every operation checked
				long result = scaleDiff18.multiplyByScaleFactorExact(h1xh2);
				result = Checked.addLong(result, h1xl2d);
				result = Checked.addLong(result, h2xl1d);
				result = Checked.addLong(result, scaleDiff09.divideByScaleFactor(h1xl2r + h2xl1r + l1xl2d));
				return result;
			}
		} catch (ArithmeticException e) {
			throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + arith.toString(uDecimal1) + " * " + arith.toString(uDecimal2), e);
		}
	}

	public static long squareChecked(DecimalArithmetic arith, long uDecimal) {
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		if (doesSquareFitInLong(uDecimal)) {
			//square fits in long, just do it
			return scaleMetrics.divideByScaleFactor(uDecimal * uDecimal);
		}
		final int scale = scaleMetrics.getScale();
		try {
			if (scale <= 9) {
				//use scale to split into 2 parts: i (integral) and f (fractional)
				//with this scale, the low order product f*f fits in a long
				final long i = scaleMetrics.divideByScaleFactor(uDecimal);
				final long f = uDecimal - scaleMetrics.multiplyByScaleFactor(i);
				final long ixi = Checked.multiplyLong(i, i);//checked
				final long ixf = i * f;//cannot overflow
				final long fxf = scaleMetrics.divideByScaleFactor(f * f);//product fits for this scale, hence unchecked
				//check whether we can multiply ixf by 2
				if (ixf < 0) throw new ArithmeticException("Overflow: " + ixf + "<<1");
				final long ixfx2 = ixf << 1;
				//add it all up now, every operation checked
				long result = scaleMetrics.multiplyByScaleFactorExact(ixi);
				result = Checked.addLong(result, ixfx2);
				result = Checked.addLong(result, fxf);
				return result;
			} else {
				//use scale9 to split into 2 parts: h (high) and l (low)
				final ScaleMetrics scale9f = Scale9f.INSTANCE;
				final ScaleMetrics scaleDiff09 = Scales.getScaleMetrics(scale - 9);
				final ScaleMetrics scaleDiff18 = Scales.getScaleMetrics(18 - scale);
				final long h = scale9f.divideByScaleFactor(uDecimal);
				final long l = uDecimal - scale9f.multiplyByScaleFactor(h);
				
				final long hxh = Checked.multiplyLong(h, h);//checked
				final long hxl = h * l;//cannot overflow
				final long lxld = scale9f.divideByScaleFactor(l * l);//product fits for scale 9, hence unchecked
				final long hxld = scaleDiff09.divideByScaleFactor(hxl);
				final long hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld);
				//check whether we can multiply hxld by 2
				if (hxld < 0) throw new ArithmeticException("Overflow: " + hxld + "<<1");
				final long hxldx2 = hxld << 1;
				//add it all up now, every operation checked
				long result = scaleDiff18.multiplyByScaleFactorExact(hxh);
				result = Checked.addLong(result, hxldx2);
				result = Checked.addLong(result, scaleDiff09.divideByScaleFactor((hxlr<<1) + lxld));
				return result;
			}
		} catch (ArithmeticException e) {
			throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + arith.toString(uDecimal) + "^2", e);
		}
	}

	//no instances
	private Mul() {
	}
}
