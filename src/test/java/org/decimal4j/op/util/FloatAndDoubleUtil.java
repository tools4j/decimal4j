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
package org.decimal4j.op.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeSet;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;

/**
 * Utility class with static helper methods for floats and doubles used in tests.
 */
public class FloatAndDoubleUtil {

	// The mask for the significand, according to the {@link
	// Double#doubleToRawLongBits(double)} spec.
	private static final long SIGNIFICAND_MASK_DOUBLE = 0x000fffffffffffffL;
	// The mask for the significand, according to the {@link
	// Float#floatToRawIntBits(float)} spec.
	private static final int SIGNIFICAND_MASK_FLOAT = 0x007fffff;

	private static final int SIGNIFICAND_BITS_DOUBLE = 52;

	private static final int SIGNIFICAND_BITS_FLOAT = 23;

	/**
	 * The implicit 1 bit that is omitted in significands of normal doubles.
	 */
	private static final long IMPLICIT_BIT_DOUBLE = SIGNIFICAND_MASK_DOUBLE + 1;
	/**
	 * The implicit 1 bit that is omitted in significands of normal floats.
	 */
	private static final int IMPLICIT_BIT_FLOAT = SIGNIFICAND_MASK_FLOAT + 1;

	private static final double[] SPECIALS_DOUBLE = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.MIN_VALUE, -Double.MIN_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, Double.MIN_NORMAL, -Double.MIN_NORMAL};
	private static final float[] SPECIALS_FLOAT = { Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.MIN_VALUE, -Float.MIN_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, Float.MIN_NORMAL, -Float.MIN_NORMAL};

	public static double randomDoubleOperand(Random rnd) {
		switch (rnd.nextInt(3)) {
		case 0:
			return rnd.nextDouble();
		case 1:
			return rnd.nextGaussian();
		default:// 2:
			return Double.longBitsToDouble(rnd.nextLong());
		}
	}

	public static float randomFloatOperand(Random rnd) {
		switch (rnd.nextInt(3)) {
		case 0:
			return rnd.nextFloat();
		case 1:
			return (float)rnd.nextGaussian();
		default:// 2:
			return Float.intBitsToFloat(rnd.nextInt());
		}
	}

	public static double[] specialDoubleOperands(ScaleMetrics scaleMetrics) {
		final NavigableSet<Double> set = new TreeSet<Double>();
		for (final double dbl : SPECIALS_DOUBLE) {
			set.add(dbl);
			set.add(dbl + Math.ulp(dbl));
			set.add(dbl - Math.ulp(dbl));
		}
		for (final long l : TestSettings.TEST_CASES.getSpecialValuesFor(scaleMetrics)) {
			final double dbl = l;
			set.add(dbl);
			set.add(dbl + Math.ulp(dbl));
			set.add(dbl - Math.ulp(dbl));
		}
		final double[] vals = new double[set.size()];
		final Iterator<Double> it = set.iterator();
		for (int i = 0; i < vals.length; i++) {
			vals[i] = it.next();
		}
		return vals;
	}

	public static float[] specialFloatOperands(ScaleMetrics scaleMetrics) {
		final NavigableSet<Float> set = new TreeSet<Float>();
		for (final float flt : SPECIALS_FLOAT) {
			set.add(flt);
			set.add(flt + Math.ulp(flt));
			set.add(flt - Math.ulp(flt));
		}
		for (final long l : TestSettings.TEST_CASES.getSpecialValuesFor(scaleMetrics)) {
			final float flt = l;
			set.add(flt);
			set.add(flt + Math.ulp(flt));
			set.add(flt - Math.ulp(flt));
		}
		final float[] vals = new float[set.size()];
		final Iterator<Float> it = set.iterator();
		for (int i = 0; i < vals.length; i++) {
			vals[i] = it.next();
		}
		return vals;
	}

	//PRECONDITION: isFinite(d)
	public static long getSignificand(double d) {
		int exponent = Math.getExponent(d);
		long bits = Double.doubleToRawLongBits(d);
		bits &= SIGNIFICAND_MASK_DOUBLE;
		return (exponent == Double.MIN_EXPONENT - 1) ? bits << 1 : bits | IMPLICIT_BIT_DOUBLE;
	}

	//PRECONDITION: isFinite(d)
	public static int getSignificand(float f) {
		int exponent = Math.getExponent(f);
		int bits = Float.floatToRawIntBits(f);
		bits &= SIGNIFICAND_MASK_FLOAT;
		return (exponent == Float.MIN_EXPONENT - 1) ? bits << 1 : bits | IMPLICIT_BIT_FLOAT;
	}

	/**
	 * Similar to {@link BigDecimal#valueOf(double)} but exact e.g. for -1pe63
	 * which is rounded by the BigDecimal standard conversion. Scale and rounding
	 * mode are only used to check that the double fits in a 64 bit decimal.
	 * 
	 * @param d the double to convert
	 * @param scale the scale for the result (which is returned with higher scale for debugging in case of error --- tests convert to correct scale later)
	 * @param roundingMode  the rounding mode
	 * @return a big decimal representing exactly d
	 */
	public static BigDecimal doubleToBigDecimal(double d, int scale, RoundingMode roundingMode) {
		final int exp = Math.getExponent(d);
		if (exp >= Long.SIZE) {
			throw new IllegalArgumentException("Overflow for conversion from double to Decimal: " + d);
		}
		if (exp < Double.MIN_EXPONENT) {
			return BigDecimal.valueOf(d);
		}
		final long significand = FloatAndDoubleUtil.getSignificand(d);
		final BigDecimal scaledBigDecimal = BigDecimal.valueOf(d < 0 ? -significand : significand); 
		final int shift = exp - FloatAndDoubleUtil.SIGNIFICAND_BITS_DOUBLE;
		final BigDecimal converted;
		if (shift >= 0) {
			converted = scaledBigDecimal.multiply(new BigDecimal(BigInteger.valueOf(2).pow(shift)));
		} else {
			converted = scaledBigDecimal.divide(new BigDecimal(BigInteger.valueOf(2).pow(-shift)), -shift, RoundingMode.UNNECESSARY);
		}
		//if rounding=UNNECESSARY, use round down first to trigger overflow exception before rounding unnecessary
		final boolean isRoundingUnnecessary = roundingMode == RoundingMode.UNNECESSARY;
		final BigDecimal rounded = converted.setScale(scale, isRoundingUnnecessary ? RoundingMode.DOWN : roundingMode);
		//now check that the conversion does not overflow
		if (rounded.unscaledValue().bitLength() > 63) {
			throw new IllegalArgumentException("Overflow: " + rounded + ": " + converted);
		}
		return isRoundingUnnecessary ? converted.setScale(scale, roundingMode) : rounded;
	}
	
	/**
	 * Similar to {@link BigDecimal#valueOf(double)} but exact e.g. for -1pe63
	 * which is rounded by the BigDecimal standard conversion. Scale and rounding
	 * mode are only used to check that the float fits in a 64 bit decimal.
	 * 
	 * @param f the float to convert
	 * @param scale the scale for the result (which is returned with higher scale for debugging in case of error --- tests convert to correct scale later)
	 * @param roundingMode  the rounding mode
	 * @return a big decimal representing exactly f
	 */
	public static BigDecimal floatToBigDecimal(float f, int scale, RoundingMode roundingMode) {
		final int exp = Math.getExponent(f);
		if (exp >= Long.SIZE) {
			throw new IllegalArgumentException("Overflow for conversion from float to long: " + f);
		}
		if (exp < Float.MIN_EXPONENT) {
			return BigDecimal.valueOf(f);
		}
		final int significand = FloatAndDoubleUtil.getSignificand(f);
		final BigDecimal scaledBigDecimal = BigDecimal.valueOf(f < 0 ? -significand : significand); 
		final int shift = exp - FloatAndDoubleUtil.SIGNIFICAND_BITS_FLOAT;
		final BigDecimal converted;
		if (shift >= 0) {
			converted = scaledBigDecimal.multiply(new BigDecimal(BigInteger.valueOf(2).pow(shift)));
		} else {
			converted = scaledBigDecimal.divide(new BigDecimal(BigInteger.valueOf(2).pow(-shift)), -shift, RoundingMode.UNNECESSARY);
		}
		//if rounding=UNNECESSARY, use round down first to trigger overflow exception before rounding unnecessary
		final boolean isRoundingUnnecessary = roundingMode == RoundingMode.UNNECESSARY;
		final BigDecimal rounded = converted.setScale(scale, isRoundingUnnecessary ? RoundingMode.DOWN : roundingMode);
		//now check that the conversion does not overflow
		if (rounded.unscaledValue().bitLength() > 63) {
			throw new IllegalArgumentException("Overflow: " + rounded + ": " + converted);
		}
		return isRoundingUnnecessary ? converted.setScale(scale, roundingMode) : rounded;
	}
	
	public static RoundingMode getOppositeRoundingMode(RoundingMode roundingMode) {
		switch (roundingMode) {
		case UP:
			return RoundingMode.DOWN;
		case DOWN:
			return RoundingMode.UP;
		case CEILING:
			return RoundingMode.FLOOR;
		case FLOOR:
			return RoundingMode.CEILING;
		case HALF_UP:
			return RoundingMode.HALF_DOWN;
		case HALF_DOWN:
			return RoundingMode.HALF_UP;
		case HALF_EVEN:
			return RoundingMode.HALF_EVEN;//HALF_UNEVEN?
		case UNNECESSARY:
			return RoundingMode.UNNECESSARY;
		default:
			throw new IllegalArgumentException("unsupported rounding mode: " + roundingMode);
		}
	}

	//no instances
	private FloatAndDoubleUtil() {
		super();
	}
	
}
