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
			final ScaleMetrics scale9f = Scale9f.INSTANCE;
			final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
			final long h1 = scale9f.divideByScaleFactor(uDecimal1);
			final long h2 = scale9f.divideByScaleFactor(uDecimal2);
			final long l1 = uDecimal1 - scale9f.multiplyByScaleFactor(h1);
			final long l2 = uDecimal2 - scale9f.multiplyByScaleFactor(h2);
			final long h1xl2 = h1 * l2;
			final long h2xl1 = h2 * l1;
			final long l1xl2d = scale9f.divideByScaleFactor(l1 * l2);
			final long sumOfLowsHalf = (h1xl2 >> 1) + (h2xl1 >> 1) + (l1xl2d >> 1) //sum halfs to avoid overflow
					+ (((h1xl2 & h2xl1) | (h1xl2 & l1xl2d) | (h2xl1 & l1xl2d)) & 0x1); //carry of lost bits
			return scaleDiff18.multiplyByScaleFactor(h1 * h2) + scaleDiff09.divideByScaleFactorHalf(sumOfLowsHalf);
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
			return unrounded + Rounding.calculateRoundingIncrement(rounding, unrounded, f1xf2r, scaleMetrics.getScaleFactor());
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
			final long l1xl2r = l1xl2 - scale9f.multiplyByScaleFactor(l1xl2d);
			final long sumOfLowsHalf = (h1xl2 >> 1) + (h2xl1 >> 1) + (l1xl2d >> 1) //sum halfs to avoid overflow
					+ (((h1xl2 & h2xl1) | (h1xl2 & l1xl2d) | (h2xl1 & l1xl2d)) & 0x1); //carry of lost bits
			final long sumOfLowsHalfDiv = scaleDiff09.divideByScaleFactorHalf(sumOfLowsHalf);
			final long sumOfLowsHalfRem = sumOfLowsHalf - scaleDiff09.multiplyByScaleFactorHalf(sumOfLowsHalfDiv);
			final long sumOfLowsHalfBit = ((h1xl2 ^ h2xl1 ^ l1xl2d) & 0x1); //lost bit
			final long unrounded = scaleDiff18.multiplyByScaleFactor(h1 * h2) + sumOfLowsHalfDiv;
			final long remainder = scale9f.multiplyByScaleFactor((sumOfLowsHalfRem << 1) + sumOfLowsHalfBit) + l1xl2r;
			return unrounded + Rounding.calculateRoundingIncrement(rounding, unrounded, remainder, scaleMetrics.getScaleFactor());
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
			final long sumOfLowsHalf = hxl + (lxld >> 1); //sum halfs to avoid overflow
			return scaleDiff18.multiplyByScaleFactor(h * h) + scaleDiff09.divideByScaleFactorHalf(sumOfLowsHalf);
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
			return unrounded + Rounding.calculateRoundingIncrement(rounding, unrounded, fxfr, scaleMetrics.getScaleFactor());
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
			final long lxlr = lxl - scale9f.multiplyByScaleFactor(lxld);
			final long sumOfLowsHalf = hxl + (lxld >> 1); //sum halfs to avoid overflow
			final long sumOfLowsHalfDiv = scaleDiff09.divideByScaleFactorHalf(sumOfLowsHalf);
			final long sumOfLowsHalfRem = sumOfLowsHalf - scaleDiff09.multiplyByScaleFactorHalf(sumOfLowsHalfDiv);
			final long sumOfLowsHalfBit = (lxld & 0x1); //lost bit
			final long unrounded = scaleDiff18.multiplyByScaleFactor(h * h) + sumOfLowsHalfDiv;
			final long remainder = scale9f.multiplyByScaleFactor((sumOfLowsHalfRem << 1) + sumOfLowsHalfBit) + lxlr;
			return unrounded + Rounding.calculateRoundingIncrement(rounding, unrounded, remainder, scaleMetrics.getScaleFactor());
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
				final long sumOfLowsHalf = (h1xl2 >> 1) + (h2xl1 >> 1) + (l1xl2d >> 1) //sum halfs to avoid overflow
						+ (((h1xl2 & h2xl1) | (h1xl2 & l1xl2d) | (h2xl1 & l1xl2d)) & 0x1); //carry of lost bits
				//add it all up now, every operation checked
				final long h1xh2s = scaleDiff18.multiplyByScaleFactor(h1xh2);
				return arith.add(h1xh2s, scaleDiff09.divideByScaleFactorHalf(sumOfLowsHalf));
			}
		} catch (ArithmeticException e) {
			final ArithmeticException ex = new ArithmeticException("overflow: " + arith.toString(uDecimal1) + " * " + arith.toString(uDecimal2));
			e.initCause(e);
			throw ex;
		}
	}

	//no instances
	private Mul() {
		super();
	}
}
