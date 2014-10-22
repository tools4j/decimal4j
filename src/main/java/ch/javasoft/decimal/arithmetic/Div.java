package ch.javasoft.decimal.arithmetic;

import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.scale.Scales;
import ch.javasoft.decimal.truncate.DecimalRounding;
import ch.javasoft.decimal.truncate.OverflowMode;
import ch.javasoft.decimal.truncate.TruncatedPart;

/**
 * Calculates division results.
 */
final class Div {

	private static final long LONG_MASK = 0xffffffffL;

	public static long divideByLong(DecimalRounding rounding, long uDecimalDividend, long lDivisor) {
		final long quotient = uDecimalDividend / lDivisor;
		final long remainder = uDecimalDividend - quotient * lDivisor;
		return quotient + Rounding.calculateRoundingIncrementForDivision(rounding, quotient, remainder, lDivisor);
	}

	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * without rounding.
	 * <p>
	 * <b>Note:</b> this methods supports {@link OverflowMode#CHECKED} if set in
	 * the decimal arithmetics.
	 * 
	 * @param arith
	 *            the arithmetics with scale metrics and overflow mode
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * @return the division result without rounding but with support for
	 *         overflow checks if desired
	 */
	public static long divide(DecimalArithmetics arith, long uDecimalDividend, long uDecimalDivisor) {
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
		if (uDecimalDividend <= (maxInteger) & uDecimalDividend >= minInteger) {
			//just do it, multiplication result fits in long
			return scaleMetrics.multiplyByScaleFactor(uDecimalDividend) / uDecimalDivisor;
		}
		//perform component wise division
		final long integralPart = uDecimalDividend / uDecimalDivisor;
		final long reminder = uDecimalDividend - integralPart * uDecimalDivisor;
		final long fractionalPart;
		if (reminder <= maxInteger & reminder >= minInteger) {
			fractionalPart = scaleMetrics.multiplyByScaleFactor(reminder) / uDecimalDivisor;
		} else {
			fractionalPart = scaleTo128divBy64(arith, scaleMetrics, DecimalRounding.DOWN, reminder, uDecimalDivisor);
		}
		return scaleMetrics.multiplyByScaleFactor(integralPart) + fractionalPart;
	}

	/**
	 * Calculates {@code (uDecimalDividend * scaleFactor) / uDecimalDivisor}
	 * with rounding.
	 * <p>
	 * <b>Note:</b> this methods supports {@link OverflowMode#CHECKED} if set in
	 * the decimal arithmetics.
	 * 
	 * @param arith
	 *            the arithmetics with scale metrics and overflow mode
	 * @param rounding
	 *            the decimal rounding to apply if rounding is necessary
	 * @param uDecimalDividend
	 *            the unscaled decimal dividend
	 * @param uDecimalDivisor
	 *            the unscaled decimal divisor
	 * @return the division result with rounding and support for overflow checks
	 *         if desired
	 */
	public static long divide(DecimalArithmetics arith, DecimalRounding rounding, long uDecimalDividend, long uDecimalDivisor) {
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
			return quot + Rounding.calculateRoundingIncrementForDivision(rounding, quot, rem, uDecimalDivisor);
		}
		//perform component wise division
		final long integralPart = uDecimalDividend / uDecimalDivisor;
		final long reminder = uDecimalDividend - integralPart * uDecimalDivisor;
		if (reminder <= maxInteger & reminder >= minInteger) {
			final long scaledReminder = scaleMetrics.multiplyByScaleFactor(reminder);
			final long fractionalPart = scaledReminder / uDecimalDivisor;
			final long subFractionalPart = scaledReminder - fractionalPart * uDecimalDivisor;
			final long truncated = scaleMetrics.multiplyByScaleFactor(integralPart) + fractionalPart;
			return truncated + Rounding.calculateRoundingIncrementForDivision(rounding, truncated, subFractionalPart, uDecimalDivisor);
		} else {
			final long fractionalPart = Div.scaleTo128divBy64(arith, scaleMetrics, rounding, reminder, uDecimalDivisor);
			return scaleMetrics.multiplyByScaleFactor(integralPart) + fractionalPart;
		}
	}

	private static long scaleTo128divBy64(DecimalArithmetics arith, ScaleMetrics scaleMetrics, DecimalRounding rounding, long uDecimalDividend, long uDecimalDivisor) {
		final boolean negative = (uDecimalDividend < 0) != (uDecimalDivisor < 0);
		final long absDividend = Math.abs(uDecimalDividend);
		final long absDivisor = Math.abs(uDecimalDivisor);

		//multiply by scale factor into a 128bit integer
		final int lFactor = (int) (absDividend & LONG_MASK);
		final int hFactor = (int) (absDividend >>> 32);
		long lScaled;
		long hScaled;
		long product;

		product = scaleMetrics.mulloByScaleFactor(lFactor);
		lScaled = product & LONG_MASK;
		product = scaleMetrics.mulhiByScaleFactor(lFactor) + (product >>> 32);
		hScaled = product >>> 32;
		product = scaleMetrics.mulloByScaleFactor(hFactor) + (product & LONG_MASK);
		lScaled |= ((product & LONG_MASK) << 32);
		hScaled = scaleMetrics.mulhiByScaleFactor(hFactor) + hScaled + (product >>> 32);

		if (arith.getOverflowMode() == OverflowMode.CHECKED) {
			//pre-check for overflow
			if (Unsigned.compare(hScaled, absDivisor) >= 0) {
				throw new ArithmeticException("overflow: " + arith.toString(uDecimalDividend) + " / " + arith.toString(uDecimalDivisor));
			}

			final long result = div128by64(rounding, negative, hScaled, lScaled, absDivisor);

			//post-check for overflow 
			//FIXME not sure if correct especially for neg / neg = pos
			if (result < 0 & ((uDecimalDividend ^ uDecimalDivisor) >= 0)) {
				throw new ArithmeticException("overflow: " + arith.toString(uDecimalDividend) + " / " + arith.toString(uDecimalDivisor));
			}

			return result;
		}

		//divide 128 bit product by 64 bit divisor
		return div128by64(rounding, negative, hScaled, lScaled, absDivisor);

	}

	/**
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
	private static long div128by64(final DecimalRounding rounding, final boolean neg, final long u1, final long u0, final long v0) {
		long q;
		long r;

		long un1, un0, vn1, vn0, q1, q0, un32, un21, un10, rhat, left, right;

		final int s = Long.numberOfLeadingZeros(v0);

		final long v = v0 << s;
		vn1 = v >>> 32;
		vn0 = v & LONG_MASK;

		if (s > 0) {
			un32 = (u1 << s) | (u0 >>> (64 - s));
			un10 = u0 << s;
		} else {
			un32 = u1;
			un10 = u0;
		}

		un1 = un10 >>> 32;
		un0 = un10 & LONG_MASK;

		q1 = unsignedDiv64by32(un32, vn1);
		rhat = un32 - q1 * vn1;

		left = q1 * vn0;
		right = (rhat << 32) + un1;

		while (((q1 >>> 32) != 0) | Unsigned.isGreater(left, right)) {
			--q1;
			rhat += vn1;
			if ((rhat >>> 32) != 0) {
				break;
			}
			left -= vn0;
			right = (rhat << 32) | un1;
		}

		un21 = (un32 << 32) + (un1 - (q1 * v));

		q0 = unsignedDiv64by32(un21, vn1);
		rhat = un21 - q0 * vn1;

		left = q0 * vn0;
		right = (rhat << 32) | un0;

		while (((q0 >>> 32) != 0) | Unsigned.isGreater(left, right)) {
			--q0;
			rhat += vn1;
			if ((rhat >>> 32) != 0) {
				break;
			}
			left -= vn0;
			right = (rhat << 32) | un0;
		}

		q = (q1 << 32) | q0;

		//apply sign and rounding
		if (rounding == DecimalRounding.DOWN) {
			return neg ? -q : q;
		}

		r = ((un21 << 32) + (un0 - (q0 * v))) >>> s;
		final TruncatedPart truncatedPart = Rounding.truncatedPartFor(Math.abs(r), Math.abs(v0));
		final int inc = rounding.calculateRoundingIncrement(neg ? -1 : 1, q, truncatedPart);
		return (neg ? -q : q) + inc;
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
		return quotient + (((rem > divisor) | (rem < 0)) ? 1 : 0);
	}

	//no instances
	private Div() {
		super();
	}

}
