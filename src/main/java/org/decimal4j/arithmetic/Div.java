package org.decimal4j.arithmetic;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;

/**
 * Provides static methods to calculate division results.
 */
final class Div {

	private static final long LONG_MASK = 0xffffffffL;

	public static long divideByLong(DecimalRounding rounding, long uDecimalDividend, long lDivisor) {
		final long quotient = uDecimalDividend / lDivisor;
		final long remainder = uDecimalDividend - quotient * lDivisor;
		return quotient + RoundingUtil.calculateRoundingIncrementForDivision(rounding, quotient, remainder, lDivisor);
	}

	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * without rounding.
	 * 
	 * @param arith
	 *            the arithmetic with scale metrics and overflow mode
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * @return the division result without rounding but with support for
	 *         overflow checks if desired
	 */
	public static long divide(DecimalArithmetic arith, long uDecimalDividend, long uDecimalDivisor) {
		//special cases first
		final SpecialDivisionResult special = SpecialDivisionResult.getFor(arith, uDecimalDividend, uDecimalDivisor);
		if (special != null) {
			return special.divide(arith, uDecimalDividend, uDecimalDivisor);
		}
		//div by power of 10
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final ScaleMetrics pow10 = Scales.findByScaleFactor(Math.abs(uDecimalDivisor));
		if (pow10 != null) {
			return Pow10.divideByPowerOf10(uDecimalDividend, scaleMetrics, uDecimalDivisor > 0, pow10);
		}
		//WE WANT: uDecimalDividend * one / uDecimalDivisor
		final long maxInteger = scaleMetrics.getMaxIntegerValue();
		final long minInteger = scaleMetrics.getMinIntegerValue();
		if (uDecimalDividend <= maxInteger & uDecimalDividend >= minInteger) {
			//just do it, multiplication result fits in long
			return scaleMetrics.multiplyByScaleFactor(uDecimalDividend) / uDecimalDivisor;
		}
		//perform component wise division
		final long integralPart = uDecimalDividend / uDecimalDivisor;
		final long remainder = uDecimalDividend - integralPart * uDecimalDivisor;
		final long fractionalPart;
		if (remainder <= maxInteger & remainder >= minInteger) {
			fractionalPart = scaleMetrics.multiplyByScaleFactor(remainder) / uDecimalDivisor;
		} else {
			fractionalPart = scaleTo128divBy64(scaleMetrics, DecimalRounding.DOWN, remainder, uDecimalDivisor);
		}
		return scaleMetrics.multiplyByScaleFactor(integralPart) + fractionalPart;
//		return scaleTo128divBy64(scaleMetrics, DecimalRounding.DOWN, uDecimalDividend, uDecimalDivisor);
	}

	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * with rounding.
	 * 
	 * @param arith
	 *            the arithmetic with scale metrics and overflow mode
	 * @param rounding
	 *            the decimal rounding to apply if rounding is necessary
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * @return the division result with rounding and support for overflow checks
	 *         if desired
	 */
	public static long divide(DecimalArithmetic arith, DecimalRounding rounding, long uDecimalDividend, long uDecimalDivisor) {
		//special cases first
		final SpecialDivisionResult special = SpecialDivisionResult.getFor(arith, uDecimalDividend, uDecimalDivisor);
		if (special != null) {
			return special.divide(arith, uDecimalDividend, uDecimalDivisor);
		}
		//div by power of 10
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final ScaleMetrics pow10 = Scales.findByScaleFactor(Math.abs(uDecimalDivisor));
		if (pow10 != null) {
			return Pow10.divideByPowerOf10(rounding, uDecimalDividend, scaleMetrics, uDecimalDivisor > 0, pow10);
		}
		//WE WANT: uDecimalDividend * one / uDecimalDivisor
		final long maxInteger = scaleMetrics.getMaxIntegerValue();
		final long minInteger = scaleMetrics.getMinIntegerValue();
		if (uDecimalDividend <= maxInteger & uDecimalDividend >= minInteger) {
			//just do it, multiplication result fits in long
			final long scaledDividend = scaleMetrics.multiplyByScaleFactor(uDecimalDividend);
			final long quot = scaledDividend / uDecimalDivisor;
			final long rem = scaledDividend - quot * uDecimalDivisor;
			return quot + RoundingUtil.calculateRoundingIncrementForDivision(rounding, quot, rem, uDecimalDivisor);
		}
		//perform component wise division
		final long integralPart = uDecimalDividend / uDecimalDivisor;
		final long remainder = uDecimalDividend - integralPart * uDecimalDivisor;

		if (remainder <= maxInteger & remainder >= minInteger) {
			final long scaledReminder = scaleMetrics.multiplyByScaleFactor(remainder);
			final long fractionalPart = scaledReminder / uDecimalDivisor;
			final long subFractionalPart = scaledReminder - fractionalPart * uDecimalDivisor;
			final long truncated = scaleMetrics.multiplyByScaleFactor(integralPart) + fractionalPart;
			return truncated + RoundingUtil.calculateRoundingIncrementForDivision(rounding, truncated, subFractionalPart, uDecimalDivisor);
		} 
		else {
			final long fractionalPart = Div.scaleTo128divBy64(scaleMetrics, rounding, remainder, uDecimalDivisor);
			return scaleMetrics.multiplyByScaleFactor(integralPart) + fractionalPart;
		}
//		return Div.scaleTo128divBy64(scaleMetrics, rounding, uDecimalDividend, uDecimalDivisor);
	}
	
	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * with rounding.
	 * 
	 * @param arith
	 *            the arithmetic with scale metrics and overflow mode
	 * @param rounding
	 *            the decimal rounding to apply if rounding is necessary
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * 
	 * @return the division result with rounding and overflow checking
	 */
	// TODO reconcile this method with the other overloaded versions
	public static long divideChecked(DecimalArithmetic arith, DecimalRounding rounding, long uDecimalDividend, long uDecimalDivisor) {
		//special cases first
		if (uDecimalDivisor == 0) {
			throw new ArithmeticException("Division by zero: " + arith.toString(uDecimalDividend) + " / " + arith.toString(uDecimalDivisor));
		}
		try {
			final SpecialDivisionResult special = SpecialDivisionResult.getFor(arith, uDecimalDividend, uDecimalDivisor);
			if (special != null) {
				return special.divide(arith, uDecimalDividend, uDecimalDivisor);
			}
			//div by power of 10
			final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
			final ScaleMetrics pow10 = Scales.findByScaleFactor(Math.abs(uDecimalDivisor));
			if (pow10 != null) {
				return Pow10.divideByPowerOf10Checked(arith, rounding, uDecimalDividend, scaleMetrics, uDecimalDivisor > 0, pow10);
			}
			//WE WANT: uDecimalDividend * one / uDecimalDivisor
			final long maxInteger = scaleMetrics.getMaxIntegerValue();
			final long minInteger = scaleMetrics.getMinIntegerValue();
			if (uDecimalDividend <= maxInteger & uDecimalDividend >= minInteger) {
				//just do it, multiplication result fits in long
				final long scaledDividend = scaleMetrics.multiplyByScaleFactor(uDecimalDividend);
				final long quot = scaledDividend / uDecimalDivisor;
				final long rem = scaledDividend - quot * uDecimalDivisor;
				
				return quot + RoundingUtil.calculateRoundingIncrementForDivision(rounding, quot, rem, uDecimalDivisor);
			}
			//perform component wise division
			final long integralPart = uDecimalDividend / uDecimalDivisor;
			final long remainder = uDecimalDividend - integralPart * uDecimalDivisor;
		
			if (remainder <= maxInteger & remainder >= minInteger) {
				final long scaledReminder = scaleMetrics.multiplyByScaleFactorExact(remainder);
				final long fractionalPart = scaledReminder / uDecimalDivisor;
				final long subFractionalPart = scaledReminder - fractionalPart * uDecimalDivisor;
				
				long result = arith.add(scaleMetrics.multiplyByScaleFactorExact(integralPart), fractionalPart);
				return arith.add(result,RoundingUtil.calculateRoundingIncrementForDivision(rounding, result, subFractionalPart, uDecimalDivisor));
			} 
			else {
				final long fractionalPart = Div.scaleTo128divBy64(scaleMetrics, rounding, remainder, uDecimalDivisor);
				return arith.add(scaleMetrics.multiplyByScaleFactorExact(integralPart), fractionalPart);
			}
		} catch (ArithmeticException e) {
			final ArithmeticException ae = new ArithmeticException("Overflow: " + arith.toString(uDecimalDividend) + " / " + arith.toString(uDecimalDivisor));
			ae.initCause(e);
			throw ae;
		}
	}
				
	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * without rounding.
	 * 
	 * @param arith
	 *            the arithmetic with scale metrics and overflow mode
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * @return the division result without rounding but with support for
	 *         overflow checks if desired
	 */
	public static long divideChecked(DecimalArithmetic arith, long uDecimalDividend, long uDecimalDivisor) {
		//special cases first
		if (uDecimalDivisor == 0) {
			throw new ArithmeticException("Division by zero: " + arith.toString(uDecimalDividend) + " / " + arith.toString(uDecimalDivisor));
		}
		try {
			final SpecialDivisionResult special = SpecialDivisionResult.getFor(arith, uDecimalDividend, uDecimalDivisor);
			if (special != null) {
				return special.divide(arith, uDecimalDividend, uDecimalDivisor);
			}
			//div by power of 10
			final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
			final ScaleMetrics pow10 = Scales.findByScaleFactor(Math.abs(uDecimalDivisor));
			if (pow10 != null) {
				return Pow10.divideByPowerOf10Checked(arith, uDecimalDividend, scaleMetrics, uDecimalDivisor > 0, pow10);
			}
			//WE WANT: uDecimalDividend * one / uDecimalDivisor
			final long maxInteger = scaleMetrics.getMaxIntegerValue();
			final long minInteger = scaleMetrics.getMinIntegerValue();
			if (uDecimalDividend <= maxInteger & uDecimalDividend >= minInteger) {
				//just do it, multiplication result fits in long
				return scaleMetrics.multiplyByScaleFactor(uDecimalDividend) / uDecimalDivisor;
			}
			//perform component wise division
			final long integralPart = uDecimalDividend / uDecimalDivisor;
			final long remainder = uDecimalDividend - integralPart * uDecimalDivisor;
			final long fractionalPart;
			if (remainder <= maxInteger & remainder >= minInteger) {
				//scaling and result can't overflow because of the above condition
				fractionalPart = scaleMetrics.multiplyByScaleFactor(remainder) / uDecimalDivisor;
			} else {
				//result can't overflow because reminder is smaller than divisor, i.e. -1 < result < 1
				fractionalPart = scaleTo128divBy64(scaleMetrics, DecimalRounding.DOWN, remainder, uDecimalDivisor);
			}
			return arith.add(scaleMetrics.multiplyByScaleFactorExact(integralPart), fractionalPart);
		} catch (ArithmeticException e) {
			final ArithmeticException ae = new ArithmeticException("Overflow: " + arith.toString(uDecimalDividend) + " / " + arith.toString(uDecimalDivisor));
			ae.initCause(e);
			throw ae;
		}
	}

	private static long scaleTo128divBy64(ScaleMetrics scaleMetrics, DecimalRounding rounding, long uDecimalDividend, long uDecimalDivisor) {
		final boolean negative = (uDecimalDividend ^ uDecimalDivisor) < 0;
		final long absDividend = Math.abs(uDecimalDividend);
		final long absDivisor = Math.abs(uDecimalDivisor);

		//multiply by scale factor into a 128bit integer
		//HD + Knuth's Algorithm M from [Knu2] section 4.3.1.
		final int lFactor = (int) (absDividend & LONG_MASK);
		final int hFactor = (int) (absDividend >>> 32);
		final long w1, w2, w3;
		long k, t;

		t = scaleMetrics.mulloByScaleFactor(lFactor);
		w3 = t & LONG_MASK;
		k = t >>> 32;

		t = scaleMetrics.mulloByScaleFactor(hFactor) + k;
		w2 = t & LONG_MASK;
		w1 = t >>> 32;

		t = scaleMetrics.mulhiByScaleFactor(lFactor) + w2;
		k = t >>> 32;

		final long hScaled = scaleMetrics.mulhiByScaleFactor(hFactor) + w1 + k;
		final long lScaled = (t << 32) + w3;

		//divide 128 bit product by 64 bit divisor
		final long hQuotient, lQuotient;
		if (Unsigned.isLess(hScaled, absDivisor)) {
			hQuotient = 0;
			lQuotient = div128by64(rounding, negative, hScaled, lScaled, absDivisor);
		} else {
			hQuotient = Unsigned.divide(hScaled, absDivisor);
			final long rem = hScaled - hQuotient * absDivisor;
			lQuotient = div128by64(rounding, negative, rem, lScaled, absDivisor);
		}
		return lQuotient;
	}

	/**
	 * PRECONDITION: Unsigned.isLess(u1, v0)
	 * <p>
	 * Divides a 128 bit divisor by a 64 bit dividend and returns the signed 64
	 * bit result. Rounding is applied if {@code rounding != null}, otherwise
	 * the value is truncated.
	 * <p>
	 * From <a
	 * href="http://www.codeproject.com/Tips/785014/UInt-Division-Modulus"
	 * >www.codeproject.com</a>.
	 * 
	 * @param neg
	 *            true if result is negative
	 * @param u1
	 *            high order 64 bits of dividend
	 * @param u0
	 *            low order 64 bits of dividend
	 * @param v0
	 *            64 bit divisor
	 * @param rounding
	 *            rounding to apply, or null to truncate result
	 * @return the signed quotient, rounded if {@code rounding != null}
	 */
	static long div128by64(final DecimalRounding rounding, final boolean neg, final long u1, final long u0, final long v0) {
		final long q, r;

		final long un1, un0, vn1, vn0, un32, un21, un10;
		long q1, q0;

		final int s = Long.numberOfLeadingZeros(v0);

		final long v = v0 << s;
		vn1 = v >>> 32;
		vn0 = v & LONG_MASK;

		un32 = (u1 << s) | (u0 >>> (64 - s)) & (-s >> 63);
		un10 = u0 << s;

		un1 = un10 >>> 32;
		un0 = un10 & LONG_MASK;
		
		q1 = div128by64part(un32, un1, vn1, vn0);
		un21 = (un32 << 32) + (un1 - (q1 * v));
		q0 = div128by64part(un21, un0, vn1, vn0);
		q = (q1 << 32) | q0;

		//apply sign and rounding
		if (rounding == DecimalRounding.DOWN) {
			return neg ? -q : q;
		}

		r = ((un21 << 32) + un0 - q0 * v) >>> s;
		final TruncatedPart truncatedPart = RoundingUtil.truncatedPartFor(Math.abs(r), v0);
		final int inc = rounding.calculateRoundingIncrement(neg ? -1 : 1, q, truncatedPart);
		return (neg ? -q : q) + inc;
	}

	private static long div128by64part(final long unCB, final long unA, final long vn1, final long vn0) {
		//quotient and reminder, first guess
		long q = unsignedDiv64by32(unCB, vn1);
		long rhat = unCB - q * vn1;
		
		//correct, first attempt
		while ((q >>> 32) != 0) {
			q--;
			rhat += vn1;
			if ((rhat >>> 32) != 0) {
				return q;
			}
		}
		//correct, second attempt
		long left = q * vn0;
		long right = (rhat << 32) | unA;
		while (Unsigned.isGreater(left, right)) {
			q--;
			rhat += vn1;
			if ((rhat >>> 32) != 0) {
				return q;
			}
			left -= vn0;
			right = (rhat << 32) | unA;
		}
		return q;
	}

	/**
	 * 
	 * Returns dividend / divisor, where the dividend and divisor are treated as
	 * unsigned 64-bit quantities.
	 * <p>
	 * From Guava's <a href=
	 * "http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/primitives/UnsignedLongs.html"
	 * >UnsignedLongs</a>.
	 *
	 * @param dividend
	 *            the dividend (numerator)
	 * @param divisor
	 *            the divisor (denominator)
	 * @throws ArithmeticException
	 *             if divisor is 0
	 * 
	 */
	private static long unsignedDiv64by32(long dividend, long divisor) {
		// Optimization - use signed division if dividend < 2^63
		if (dividend >= 0) {
			return dividend / divisor;
		}

		/*
		 * Otherwise, approximate the quotient, check, and correct if necessary.
		 * Our approximation is guaranteed to be either exact or one less than
		 * the correct value. This follows from fact that floor(floor(x)/i) ==
		 * floor(x/i) for any real x and integer i != 0. The proof is not quite
		 * trivial.
		 */
		final long quotient = ((dividend >>> 1) / divisor) << 1;
		final long rem = dividend - quotient * divisor;
		return quotient + (((rem >= divisor) | (rem < 0)) ? 1 : 0);
	}

	//no instances
	private Div() {
		super();
	}

}
