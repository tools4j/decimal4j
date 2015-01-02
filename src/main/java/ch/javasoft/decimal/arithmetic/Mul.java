package ch.javasoft.decimal.arithmetic;

import ch.javasoft.decimal.scale.Scale9f;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.scale.Scales;
import ch.javasoft.decimal.truncate.DecimalRounding;

/**
 * Computes different variants of multiplications.
 */
final class Mul {

	/**
	 * Calculates the multiple {@code uDecimal1 * uDecimal2 / scaleFactor}
	 * without rounding.
	 * 
	 * @param arith
	 *            the arithmetics with access to scale metrics etc.
	 * @param uDecimal1
	 *            the first unscaled decimal factor
	 * @param uDecimal2
	 *            the second unscaled decimal factor
	 * @return the multiplication result without rounding
	 */
	public static long multiply(DecimalArithmetics arith, long uDecimal1, long uDecimal2) {
		final SpecialMultiplicationResult special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2);
		if (special != null) {
			return special.multiply(arith, uDecimal1, uDecimal2);
		}
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final int scale = scaleMetrics.getScale();

		final int leadingZeros = Long.numberOfLeadingZeros(uDecimal1) + Long.numberOfLeadingZeros(~uDecimal1) + Long.numberOfLeadingZeros(uDecimal2) + Long.numberOfLeadingZeros(~uDecimal2);
		if (leadingZeros > Long.SIZE + 1) {
			//product fits in long, just do it
			return scaleMetrics.divideByScaleFactor(uDecimal1 * uDecimal2);
		}

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
			final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
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
	 *            the arithmetics with access to scale metrics etc.
	 * @param rounding
	 *            the rounding to apply for truncated decimals
	 * @param uDecimal1
	 *            the first unscaled decimal factor
	 * @param uDecimal2
	 *            the second unscaled decimal factor
	 * @return the multiplication result with rounding
	 */
	public static long multiply(DecimalArithmetics arith, DecimalRounding rounding, long uDecimal1, long uDecimal2) {
		final SpecialMultiplicationResult special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2);
		if (special != null) {
			return special.multiply(arith, uDecimal1, uDecimal2);
		}

		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final int scale = scaleMetrics.getScale();
		
		final int leadingZeros = Long.numberOfLeadingZeros(uDecimal1) + Long.numberOfLeadingZeros(~uDecimal1) + Long.numberOfLeadingZeros(uDecimal2) + Long.numberOfLeadingZeros(~uDecimal2);
		if (leadingZeros > Long.SIZE + 1) {
			//product fits in long, just do it
			final long u1xu2 = uDecimal1 * uDecimal2;
			final long u1xu2d = scaleMetrics.divideByScaleFactor(u1xu2);
			final long u1xu2r = u1xu2 - scaleMetrics.multiplyByScaleFactor(u1xu2d);
			return u1xu2d + RoundingUtil.calculateRoundingIncrement(rounding, u1xu2d, u1xu2r, scaleMetrics.getScaleFactor());
		}
		
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
			final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
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

	/**
	 * Calculates the multiple {@code uDecimal1 * uDecimal2 / scaleFactor}
	 * with rounding.
	 * 
	 * @param arith
	 *            the arithmetics with access to scale metrics etc.
	 * @param uDecimal1
	 *            the first unscaled decimal factor
	 * @param uDecimal2
	 *            the second unscaled decimal factor
	 *            
	 * @return the multiplication result with rounding and overflow checking
	 */
	// FIXME refactor/reconcile the rounding/overflow checking versions of these methods
	public static long multiplyChecked(final DecimalArithmetics arith, final DecimalRounding rounding, final long uDecimal1, final long uDecimal2) {
		try {
			final SpecialMultiplicationResult special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2);
			if (special != null) {
				return special.multiply(arith, uDecimal1, uDecimal2);
			}
	
			final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
			final int scale = scaleMetrics.getScale();
			
			final int leadingZeros = Long.numberOfLeadingZeros(uDecimal1) + Long.numberOfLeadingZeros(~uDecimal1) + Long.numberOfLeadingZeros(uDecimal2) + Long.numberOfLeadingZeros(~uDecimal2);
			if (leadingZeros > Long.SIZE + 1) {
				//product fits in long, just do it
				final long u1xu2 = uDecimal1 * uDecimal2;
				final long u1xu2d = scaleMetrics.divideByScaleFactor(u1xu2);
				final long u1xu2r = u1xu2 - scaleMetrics.multiplyByScaleFactor(u1xu2d);
				return u1xu2d + RoundingUtil.calculateRoundingIncrement(rounding, u1xu2d, u1xu2r, scaleMetrics.getScaleFactor());
			}
			
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
				
				final long i1xi2 = arith.multiplyByLong(i1, i2);//checked
				final long i1xf2 = i1 * f2;//cannot overflow
				final long i2xf1 = i2 * f1;//cannot overflow
	
				//add it all up now, every operation checked
				long result = scaleMetrics.multiplyByScaleFactorExact(i1xi2);
				result = arith.add(result, i1xf2);
				result = arith.add(result, i2xf1);
				result = arith.add(result, f1xf2d);
				
				return result + RoundingUtil.calculateRoundingIncrement(rounding, result, f1xf2r, scaleMetrics.getScaleFactor());
			} else {
				//use scale9 to split into 2 parts: h (high) and l (low)
				final ScaleMetrics scale9f = Scale9f.INSTANCE;
				final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
				final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
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
				
				final long h1xh2 = arith.multiplyByLong(h1, h2);//checked
				//add it all up now, every operation checked
				long result = scaleDiff18.multiplyByScaleFactorExact(h1xh2);
				result = arith.add(result, h1xl2d);
				result = arith.add(result, h2xl1d);
				result = arith.add(result, scaleDiff09.divideByScaleFactor(h1xl2r + h2xl1r + l1xl2d));
				
				final long remainder = scale9f.multiplyByScaleFactorExact(h1xl2_h2xl1_l1xl1r) + l1xl2r;
				return arith.add(result, RoundingUtil.calculateRoundingIncrement(rounding, result, remainder, scaleMetrics.getScaleFactor()));
			}
		} catch (ArithmeticException e) {
			final ArithmeticException ex = new ArithmeticException("Overflow: " + arith.toString(uDecimal1) + " * " + arith.toString(uDecimal2));
			ex.initCause(e);
			throw ex;
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
			final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
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
			final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
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
	
	// FIXME merge with other versions
	public static long squareChecked(DecimalArithmetics arith, DecimalRounding rounding, long uDecimal) {
		try {
			final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
			final int scale = scaleMetrics.getScale();
			if (scale <= 9) {
				// use scale to split into 2 parts: i (integral) and f (fractional)
				final long i = scaleMetrics.divideByScaleFactor(uDecimal);
				final long f = uDecimal - scaleMetrics.multiplyByScaleFactor(i);

				// checked operations
				final long ixi = arith.multiplyByLong(i, i);
				final long ixiScaled = scaleMetrics.multiplyByScaleFactorExact(ixi);

				// with this scale, the low order product f*f fits in a long
				final long fxf = f * f;

				final long fxfd = scaleMetrics.divideByScaleFactor(fxf);
				final long fxfr = fxf - scaleMetrics.multiplyByScaleFactor(fxfd);
				
				final long unrounded = ixiScaled + ((i * f) << 1) + fxfd;
				return unrounded + RoundingUtil.calculateRoundingIncrement(rounding,
								unrounded, fxfr, scaleMetrics.getScaleFactor());
			}
			else {
				// use scale9 to split into 2 parts: h (high) and l (low)
				final ScaleMetrics scale9f = Scale9f.INSTANCE;
				final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
				final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
				final long h = scale9f.divideByScaleFactor(uDecimal);
				final long l = uDecimal - scale9f.multiplyByScaleFactor(h);
				
				// checked operations
				final long hxh = arith.multiplyByLong(h, h);
				final long hxhScaled = scaleDiff18.multiplyByScaleFactorExact(hxh);
				
				final long hxl = h * l;
				final long hxld = scaleDiff09.divideByScaleFactor(hxl);

				final long lxl = l * l;
				final long lxld = scale9f.divideByScaleFactor(lxl);
				final long lxlr = lxl - scale9f.multiplyByScaleFactor(lxld);
				
				final long hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld);
				final long hxlx2_lxl = (hxlr << 1) + lxld;
				final long hxlx2_lxld = scaleDiff09.divideByScaleFactor(hxlx2_lxl);
				final long hxlx2_lxlr = hxlx2_lxl - scaleDiff09.multiplyByScaleFactor(hxlx2_lxld);
				
				final long unrounded = hxhScaled + (hxld << 1) + hxlx2_lxld;
				final long remainder = scale9f.multiplyByScaleFactor(hxlx2_lxlr) + lxlr;
				
				return unrounded + RoundingUtil.calculateRoundingIncrement(rounding,
								unrounded, remainder,
								scaleMetrics.getScaleFactor());
			}
		} catch (ArithmeticException e) {
			final ArithmeticException ex = new ArithmeticException("Overflow: " + arith.toString(uDecimal) + "^2");
			ex.initCause(e);
			throw ex;
		}
	}

	public static long multiplyChecked(DecimalArithmetics arith, long uDecimal1, long uDecimal2) {
		try {
			final SpecialMultiplicationResult special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2);
			if (special != null) {
				return special.multiply(arith, uDecimal1, uDecimal2);
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
				final long i1xi2 = arith.multiplyByLong(i1, i2);//checked
				final long i1xf2 = i1 * f2;//cannot overflow
				final long i2xf1 = i2 * f1;//cannot overflow
				final long f1xf2 = scaleMetrics.divideByScaleFactor(f1 * f2);//product fits for this scale, hence unchecked
				//add it all up now, every operation checked
				long result = scaleMetrics.multiplyByScaleFactorExact(i1xi2);
				result = arith.add(result, i1xf2);
				result = arith.add(result, i2xf1);
				result = arith.add(result, f1xf2);
				return result;
			} else {
				//use scale9 to split into 2 parts: h (high) and l (low)
				final ScaleMetrics scale9f = Scale9f.INSTANCE;
				final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
				final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
				final long h1 = scale9f.divideByScaleFactor(uDecimal1);
				final long h2 = scale9f.divideByScaleFactor(uDecimal2);
				final long l1 = uDecimal1 - scale9f.multiplyByScaleFactor(h1);
				final long l2 = uDecimal2 - scale9f.multiplyByScaleFactor(h2);
				final long h1xh2 = arith.multiplyByLong(h1, h2);//checked
				final long h1xl2 = h1 * l2;//cannot overflow
				final long h2xl1 = h2 * l1;//cannot overflow
				final long l1xl2d = scale9f.divideByScaleFactor(l1 * l2);//product fits for scale 9, hence unchecked
				final long h1xl2d = scaleDiff09.divideByScaleFactor(h1xl2);
				final long h2xl1d = scaleDiff09.divideByScaleFactor(h2xl1);
				final long h1xl2r = h1xl2 - scaleDiff09.multiplyByScaleFactor(h1xl2d);
				final long h2xl1r = h2xl1 - scaleDiff09.multiplyByScaleFactor(h2xl1d);
				//add it all up now, every operation checked
				long result = scaleDiff18.multiplyByScaleFactorExact(h1xh2);
				result = arith.add(result, h1xl2d);
				result = arith.add(result, h2xl1d);
				result = arith.add(result, scaleDiff09.divideByScaleFactor(h1xl2r + h2xl1r + l1xl2d));
				return result;
			}
		} catch (ArithmeticException e) {
			final ArithmeticException ex = new ArithmeticException("Overflow: " + arith.toString(uDecimal1) + " * " + arith.toString(uDecimal2));
			ex.initCause(e);
			throw ex;
		}
	}

	public static long squareChecked(DecimalArithmetics arith, long uDecimal) {
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final int scale = scaleMetrics.getScale();
		try {
			if (scale <= 9) {
				//use scale to split into 2 parts: i (integral) and f (fractional)
				//with this scale, the low order product f*f fits in a long
				final long i = scaleMetrics.divideByScaleFactor(uDecimal);
				final long f = uDecimal - scaleMetrics.multiplyByScaleFactor(i);
				final long ixi = arith.multiplyByLong(i, i);//checked
				final long ixf = i * f;//cannot overflow
				final long fxf = scaleMetrics.divideByScaleFactor(f * f);//product fits for this scale, hence unchecked
				//check whether we can multiply ixf by 2
				if (ixf < 0) throw new ArithmeticException("Overflow: " + ixf + "<<1");
				final long ixfx2 = ixf << 1;
				//add it all up now, every operation checked
				long result = scaleMetrics.multiplyByScaleFactorExact(ixi);
				result = arith.add(result, ixfx2);
				result = arith.add(result, fxf);
				return result;
			} else {
				//use scale9 to split into 2 parts: h (high) and l (low)
				final ScaleMetrics scale9f = Scale9f.INSTANCE;
				final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
				final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
				final long h = scale9f.divideByScaleFactor(uDecimal);
				final long l = uDecimal - scale9f.multiplyByScaleFactor(h);
				
				final long hxh = arith.multiplyByLong(h, h);//checked
				final long hxl = h * l;//cannot overflow
				final long lxld = scale9f.divideByScaleFactor(l * l);//product fits for scale 9, hence unchecked
				final long hxld = scaleDiff09.divideByScaleFactor(hxl);
				final long hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld);
				//check whether we can multiply hxld by 2
				if (hxld < 0) throw new ArithmeticException("Overflow: " + hxld + "<<1");
				final long hxldx2 = hxld << 1;
				//add it all up now, every operation checked
				long result = scaleDiff18.multiplyByScaleFactorExact(hxh);
				result = arith.add(result, hxldx2);
				result = arith.add(result, scaleDiff09.divideByScaleFactor((hxlr<<1) + lxld));
				return result;
			}
		} catch (ArithmeticException e) {
			final ArithmeticException ex = new ArithmeticException("Overflow: " + arith.toString(uDecimal) + "^2");
			ex.initCause(e);
			throw ex;
		}
	}

	//no instances
	private Mul() {
	}
}
