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

import static org.decimal4j.arithmetic.Square.SQRT_MAX_VALUE;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scale9f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;

/**
 * Provides methods to calculate multiplication results.
 */
final class Mul {

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
		return multiply(uDecimal1, arith.getScaleMetrics(), uDecimal2);
	}
	
	/**
	 * Calculates unchecked multiplication by an unscaled value with the given scale
	 * without rounding.
	 * 
	 * @param uDecimal
	 *            the unscaled decimal factor
	 * @param unscaled
	 *            the second unscaled factor
	 * @param scale
	 *            the scale of the second factor
	 * @return the multiplication result without rounding and without overflow checks
	 */
	public static long multiplyByUnscaled(long uDecimal, long unscaled, int scale) {
		if (uDecimal == 0 | unscaled == 0) {
			return 0;
		} else if (scale == 0) {
			return uDecimal * unscaled;
		} else if (scale < 0) {
			return Pow10.divideByPowerOf10(uDecimal * unscaled, scale);
		} else if (scale > Scales.MAX_SCALE) {
			throw new IllegalArgumentException("Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale);
		}
		final ScaleMetrics scaleMetrics = Scales.findByScaleFactor(scale);
		return multiply(uDecimal, scaleMetrics, unscaled);
	}
	
	/**
	 * Calculates the multiple {@code uDecimal1 * uDecimal2 / scaleFactor}
	 * without rounding.
	 * 
	 * @param uDecimal1
	 *            the first unscaled decimal factor
	 * @param scaleMetrics2
	 *            the scale metrics associated with the second factor
	 * @param uDecimal2
	 *            the second unscaled decimal factor
	 * @return the multiplication result without rounding
	 */
	private static long multiply(long uDecimal1, ScaleMetrics scaleMetrics2, long uDecimal2) {
		if (doesProductFitInLong(uDecimal1, uDecimal2)) {
			//product fits in long, just do it
			return scaleMetrics2.divideByScaleFactor(uDecimal1 * uDecimal2);
		}
		final int scale = scaleMetrics2.getScale();
		if (scale <= 9) {
			//use scale to split into 2 parts: i (integral) and f (fractional)
			//with this scale, the low order product f1*f2 fits in a long
			final long i1 = scaleMetrics2.divideByScaleFactor(uDecimal1);
			final long i2 = scaleMetrics2.divideByScaleFactor(uDecimal2);
			final long f1 = uDecimal1 - scaleMetrics2.multiplyByScaleFactor(i1);
			final long f2 = uDecimal2 - scaleMetrics2.multiplyByScaleFactor(i2);
			return scaleMetrics2.multiplyByScaleFactor(i1 * i2) + i1 * f2 + i2 * f1 + scaleMetrics2.divideByScaleFactor(f1 * f2);
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
	 * applying the specified rounding if necessary.
	 * 
	 * @param arith
	 *            the arithmetic with access to scale metrics etc.
	 * @param rounding
	 *            the rounding to apply if necessary
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
		return multiply(rounding, uDecimal1, arith.getScaleMetrics(), uDecimal2);
	}

	/**
	 * Calculates unchecked multiplication by an unscaled value with the given
	 * scale with rounding.
	 * 
	 * @param rounding
	 *            the rounding to apply
	 * @param uDecimal
	 *            the unscaled decimal factor
	 * @param unscaled
	 *            the second unscaled factor
	 * @param scale
	 *            the scale of the second factor
	 * @return the multiplication result with rounding and without overflow checks
	 */
	public static long multiplyByUnscaled(DecimalRounding rounding, long uDecimal, long unscaled, int scale) {
		if (uDecimal == 0 | unscaled == 0) {
			return 0;
		} else if (scale == 0) {
			return uDecimal * unscaled;
		} else if (scale < 0) {
			return Pow10.divideByPowerOf10(rounding, uDecimal * unscaled, scale);
		} else if (scale > Scales.MAX_SCALE) {
			throw new IllegalArgumentException("Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale);
		}
		final ScaleMetrics scaleMetrics = Scales.findByScaleFactor(scale);
		return multiply(rounding, uDecimal, scaleMetrics, unscaled);
	}

	/**
	 * Calculates unchecked multiplication by an unscaled value with the given
	 * scale with rounding.
	 * 
	 * @param rounding
	 *            the rounding to apply
	 * @param uDecimal1
	 *            the first unscaled decimal factor
	 * @param scaleMetrics2
	 *            the scale metrics associated with the second factor
	 * @param uDecimal2
	 *            the second unscaled decimal factor
	 * @return the multiplication result with rounding and without overflow checks
	 */
	private static long multiply(DecimalRounding rounding, long uDecimal1, ScaleMetrics scaleMetrics2, long uDecimal2) {
		if (doesProductFitInLong(uDecimal1, uDecimal2)) {
			//product fits in long, just do it
			return multiply32(rounding, uDecimal1, scaleMetrics2, uDecimal2);
		}
		
		final int scale = scaleMetrics2.getScale();
		if (scale <= 9) {
			//use scale to split into 2 parts: i (integral) and f (fractional)
			//with this scale, the low order product f1*f2 fits in a long
			final long i1 = scaleMetrics2.divideByScaleFactor(uDecimal1);
			final long i2 = scaleMetrics2.divideByScaleFactor(uDecimal2);
			final long f1 = uDecimal1 - scaleMetrics2.multiplyByScaleFactor(i1);
			final long f2 = uDecimal2 - scaleMetrics2.multiplyByScaleFactor(i2);
			final long f1xf2 = f1 * f2;
			final long f1xf2d = scaleMetrics2.divideByScaleFactor(f1xf2);
			final long f1xf2r = f1xf2 - scaleMetrics2.multiplyByScaleFactor(f1xf2d);
			final long unrounded = scaleMetrics2.multiplyByScaleFactor(i1 * i2) + i1 * f2 + i2 * f1 + f1xf2d;
			return unrounded + RoundingUtil.calculateRoundingIncrement(rounding, unrounded, f1xf2r, scaleMetrics2.getScaleFactor());
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
			return unrounded + RoundingUtil.calculateRoundingIncrement(rounding, unrounded, remainder, scaleMetrics2.getScaleFactor());
		}
	}
	
	/**
	 * Calculates {@code round((uDecimal1 * uDecimal2) / scaleFactor2)} treating
	 * the factors as 32 bit values whose product must fit in a long result.
	 * 
	 * @param rounding
	 *            the rounding to use
	 * @param uDecimal1
	 *            the first factor
	 * @param scaleMetrics2
	 *            the scale metrics to apply to the product
	 * @param uDecimal2
	 *            the second factor
	 * @return the product rounded if necessary
	 */
	private static long multiply32(DecimalRounding rounding, long uDecimal1, ScaleMetrics scaleMetrics2, long uDecimal2) {
		final long u1xu2 = uDecimal1 * uDecimal2;
		final long u1xu2d = scaleMetrics2.divideByScaleFactor(u1xu2);
		final long u1xu2r = u1xu2 - scaleMetrics2.multiplyByScaleFactor(u1xu2d);
		return u1xu2d + RoundingUtil.calculateRoundingIncrement(rounding, u1xu2d, u1xu2r, scaleMetrics2.getScaleFactor());
	}

	/**
	 * Calculates the multiple {@code uDecimal1 * uDecimal2 / scaleFactor}
	 * without rounding checking for overflows.
	 * 
	 * @param arith
	 *            the arithmetic with access to scale metrics etc.
	 * @param uDecimal1
	 *            the first unscaled decimal factor
	 * @param uDecimal2
	 *            the second unscaled decimal factor
	 * @return the multiplication result without rounding and with overflow checks
	 */
	public static long multiplyChecked(final DecimalArithmetic arith, final long uDecimal1, final long uDecimal2) {
		final SpecialMultiplicationResult special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2);
		if (special != null) {
			return special.multiply(arith, uDecimal1, uDecimal2);
		}
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		return multiplyChecked(scaleMetrics, uDecimal1, scaleMetrics, uDecimal2);
	}
	
	/**
	 * Calculates checked multiplication by an unscaled value with the given scale
	 * without rounding.
	 * 
	 * @param arith
	 *            the decimal arithmetics associated with the first factor
	 * @param uDecimal
	 *            the unscaled decimal factor
	 * @param unscaled
	 *            the second unscaled factor
	 * @param scale
	 *            the scale of the second factor
	 * @return the multiplication result without rounding and with overflow checks
	 */
	public static long multiplyByUnscaledChecked(DecimalArithmetic arith, long uDecimal, long unscaled, int scale) {
		if (uDecimal == 0 | unscaled == 0) {
			return 0;
		} else if (scale == 0) {
			return arith.multiplyByLong(uDecimal, unscaled);
		} else if (scale < 0) {
			final long unscaledResult = Checked.multiplyLong(uDecimal, unscaled);
			return Pow10.divideByPowerOf10Checked(arith, unscaledResult, scale);
		} else if (scale > Scales.MAX_SCALE) {
			throw new IllegalArgumentException("Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale);
		}
		final ScaleMetrics scaleMetrics = Scales.findByScaleFactor(scale);
		return multiplyChecked(arith.getScaleMetrics(), uDecimal, scaleMetrics, unscaled);
	}
	
	/**
	 * Calculates checked multiplication by an unscaled value with the given scale
	 * without rounding.
	 * 
	 * @param scaleMetrics1
	 *            the scale matrics associated with the first factor
	 * @param uDecimal1
	 *            the first unscaled decimal factor
	 * @param scaleMetrics1
	 *            the scale matrics associated with the second factor
	 * @param uDecimal2
	 *            the second unscaled decimal factor
	 * @return the multiplication result without rounding and with overflow checks
	 */
	private static long multiplyChecked(ScaleMetrics scaleMetrics1, long uDecimal1, ScaleMetrics scaleMetrics2, long uDecimal2) {
		try {
			if (doesProductFitInLong(uDecimal1, uDecimal2)) {
				return scaleMetrics2.divideByScaleFactor(uDecimal1 * uDecimal2);
			}
			
			final int scale = scaleMetrics2.getScale();
			if (scale <= 9) {
				//use scale to split into 2 parts: i (integral) and f (fractional)
				//with this scale, the low order product f1*f2 fits in a long
				final long i1 = scaleMetrics2.divideByScaleFactor(uDecimal1);
				final long i2 = scaleMetrics2.divideByScaleFactor(uDecimal2);
				final long f1 = uDecimal1 - scaleMetrics2.multiplyByScaleFactor(i1);
				final long f2 = uDecimal2 - scaleMetrics2.multiplyByScaleFactor(i2);
				final long i1xi2 = Checked.multiplyLong(i1, i2);//checked
				final long i1xf2 = i1 * f2;//cannot overflow
				final long i2xf1 = i2 * f1;//cannot overflow
				final long f1xf2 = scaleMetrics2.divideByScaleFactor(f1 * f2);//product fits for this scale, hence unchecked
				//add it all up now, every operation checked
				long result = scaleMetrics2.multiplyByScaleFactorExact(i1xi2);
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
			throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + scaleMetrics1.toString(uDecimal1) + " * " + scaleMetrics2.toString(uDecimal2), e);
		}
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
	public static long multiplyChecked(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal1, long uDecimal2) {
		final SpecialMultiplicationResult special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2);
		if (special != null) {
			return special.multiply(arith, uDecimal1, uDecimal2);
		}
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		return multiplyChecked(rounding, scaleMetrics, uDecimal1, scaleMetrics, uDecimal2);
	}

	/**
	 * Calculates checked multiplication by an unscaled value with the given
	 * scale with rounding.
	 * 
	 * @param arith
	 *            the arithmetics associated with {@code uDecimal}
	 * @param rounding
	 *            the rounding to apply
	 * @param uDecimal
	 *            the unscaled decimal factor
	 * @param unscaled
	 *            the second unscaled factor
	 * @param scale
	 *            the scale of the second factor
	 * @return the multiplication result with rounding and overflow checks
	 */
	public static long multiplyByUnscaledChecked(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal, long unscaled, int scale) {
		if (uDecimal == 0 | unscaled == 0) {
			return 0;
		} else if (scale == 0) {
			return arith.multiplyByLong(uDecimal, unscaled);
		} else if (scale < 0) {
			final long unscaledResult = Checked.multiplyLong(uDecimal, unscaled);
			return Pow10.divideByPowerOf10Checked(arith, rounding, unscaledResult, scale);
		} else if (scale > Scales.MAX_SCALE) {
			throw new IllegalArgumentException("Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale);
		}
		final ScaleMetrics scaleMetrics2 = Scales.findByScaleFactor(scale);
		return multiplyChecked(rounding, arith.getScaleMetrics(), uDecimal, scaleMetrics2, unscaled);
	}

	/**
	 * Calculates the checked multiple
	 * {@code uDecimal1 * uDecimal2 / scaleFactor2} with rounding.
	 * 
	 * @param rounding
	 *            the rounding to apply for truncated decimals
	 * @param scaleMetrics1
	 *            the scale metrics of the first factor
	 * @param uDecimal1
	 *            the first unscaled decimal factor
	 * @param scaleMetrics2
	 *            the scale metrics of the second factor
	 * @param uDecimal2
	 *            the second unscaled decimal factor
	 * @return the multiplication result with rounding and overflow checking
	 */
	private static long multiplyChecked(DecimalRounding rounding, ScaleMetrics scaleMetrics1, long uDecimal1, ScaleMetrics scaleMetrics2, long uDecimal2) {
		try {
			if (doesProductFitInLong(uDecimal1, uDecimal2)) {
				//product fits in long, just do it
				return multiply32(rounding, uDecimal1, scaleMetrics2, uDecimal2);
			}

			final int scale = scaleMetrics2.getScale();
			if (scale <= 9) {
				//use scale to split into 2 parts: i (integral) and f (fractional)
				//with this scale, the low order product f1*f2 fits in a long
				final long i1 = scaleMetrics2.divideByScaleFactor(uDecimal1);
				final long i2 = scaleMetrics2.divideByScaleFactor(uDecimal2);
				final long f1 = uDecimal1 - scaleMetrics2.multiplyByScaleFactor(i1);
				final long f2 = uDecimal2 - scaleMetrics2.multiplyByScaleFactor(i2);
				final long f1xf2 = f1 * f2;
				final long f1xf2d = scaleMetrics2.divideByScaleFactor(f1xf2);
				final long f1xf2r = f1xf2 - scaleMetrics2.multiplyByScaleFactor(f1xf2d);
				
				final long i1xi2 = Checked.multiplyLong(i1, i2);//checked
				final long i1xf2 = i1 * f2;//cannot overflow
				final long i2xf1 = i2 * f1;//cannot overflow
	
				//add it all up now, every operation checked
				long result = scaleMetrics2.multiplyByScaleFactorExact(i1xi2);
				result = Checked.addLong(result, i1xf2);
				result = Checked.addLong(result, i2xf1);
				result = Checked.addLong(result, f1xf2d);
				
				return result + RoundingUtil.calculateRoundingIncrement(rounding, result, f1xf2r, scaleMetrics2.getScaleFactor());
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
				return Checked.addLong(result, RoundingUtil.calculateRoundingIncrement(rounding, result, remainder, scaleMetrics2.getScaleFactor()));
			}
		} catch (ArithmeticException e) {
			Exceptions.rethrowIfRoundingNecessary(e);
			throw Exceptions.newArithmeticExceptionWithCause("Overflow: " + scaleMetrics1.toString(uDecimal1) + " * " + scaleMetrics2.toString(uDecimal2), e);
		}
	}
	
	//no instances
	private Mul() {
	}
}
