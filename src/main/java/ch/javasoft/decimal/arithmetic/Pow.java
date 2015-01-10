package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.javasoft.decimal.scale.Scale18f;
import ch.javasoft.decimal.scale.Scale9f;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.scale.Scales;
import ch.javasoft.decimal.truncate.DecimalRounding;
import ch.javasoft.decimal.truncate.OverflowMode;
import ch.javasoft.decimal.truncate.TruncatedPart;

/**
 * Calculates powers of a decimal.
 */
final class Pow {

	/**
	 * Constant for {@code floor(sqrt(Long.MAX_VALUE))}
	 */
	private static final long FLOOR_SQRT_MAX_LONG = 3037000499L;

	public static long powLong(DecimalArithmetics arith, DecimalRounding rounding, long lBase, int exponent) {
		final SpecialPowResult special = SpecialPowResult.getFor(arith, lBase, exponent);
		if (special != null) {
			return special.pow(arith, lBase, exponent);
		}
		if (exponent >= 0) {
			return powLongWithPositiveExponent(arith, lBase, exponent);
		} else {
			//result is 1/powered
			//we have dealt with special cases above hence powered is neither of 0, 1, -1
			//and everything else can't be 0.5 because sqrt_i(0.5) is not real
			final int sgn = lBase > 0 | (exponent & 0x1) == 0 ? 1 : -1;//lBase cannot be 0
			return rounding.calculateRoundingIncrement(sgn, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
	}

	public static long powLongChecked(DecimalArithmetics arith, DecimalRounding rounding, long lBase, int exponent) {
		final SpecialPowResult special = SpecialPowResult.getFor(arith, lBase, exponent);
		if (special != null) {
			return special.pow(arith, lBase, exponent);
		}
		if (exponent >= 0) {
			return powLongCheckedWithPositiveExponent(lBase, exponent);
		} else {
			//result is 1/powered
			//we have dealt with special cases above hence powered is neither of 0, 1, -1
			//and everything else can't be 0.5 because sqrt_i(0.5) is not real
			final int sgn = lBase > 0 | (exponent & 0x1) == 0 ? 1 : -1;//lBase cannot be 0
			return rounding.calculateRoundingIncrement(sgn, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
	}

	public static long pow(DecimalArithmetics arith, DecimalRounding rounding, long uDecimalBase, int exponent) {
		final SpecialPowResult special = SpecialPowResult.getFor(arith, uDecimalBase, exponent);
		if (special != null) {
			return special.pow(arith, uDecimalBase, exponent);
		}

		if (exponent > 0) {
			return powWithPositiveExponent(arith, rounding, uDecimalBase, exponent);
		}

		//some other special cases
		if (exponent > 0) {
			final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
			final long fractionalPart = scaleMetrics.moduloByScaleFactor(uDecimalBase);
			if (fractionalPart == 0) {
				final long lBase = scaleMetrics.divideByScaleFactor(uDecimalBase);
				final long lResult = powLongWithPositiveExponent(arith, lBase, exponent);
				return scaleMetrics.multiplyByScaleFactor(lResult);
			}

			//try long method checked
			try {
				final long powered = powLongCheckedWithPositiveExponent(uDecimalBase, exponent);
				return Pow10.divideByPowerOf10(rounding, powered, (exponent - 1) * scaleMetrics.getScale());
			} catch (ArithmeticException e) {
				//ignore, fallback to slower method below
			}
		}

		//ok, then the slow method via BigDecimal
		final BigDecimal bigDecimalResult = powToBigDecimal(arith, uDecimalBase, exponent);
		return bigDecimalResult.unscaledValue().longValue();
	}
	
	private static final int NLZ_SCALE_FACTOR_18 = 4;//Scale18f.NLZ_SCALE_FACTOR
	private static final int SIZE_SCALE_FACTOR_18 = Long.SIZE - NLZ_SCALE_FACTOR_18;//64 - Scale18f.NLZ_SCALE_FACTOR
	private static final class UnsignedDecimal36f {
		int pow10;
		int pow2;
		long hiDecimal18f;
		long loDecimal18f;
		UnsignedDecimal36f() {
			super();
		}
		UnsignedDecimal36f(long unscaled, ScaleMetrics scaleMetrics) {
			if (unscaled == 0) {
				return;
			}
			final long ival = scaleMetrics.divideByScaleFactor(unscaled);
			final long fval = unscaled - scaleMetrics.multiplyByScaleFactor(ival);
			final ScaleMetrics diffMetrics = Scales.valueOf(18 - scaleMetrics.getScale());
			int pow2 = 0;
			long hi = diffMetrics.multiplyByScaleFactor(fval);
			long lo = 0;
			long iv = ival;
			while (iv > 1 | iv < 0) {
				if ((iv & 0x1) != 0) {
					hi += Scale18f.SCALE_FACTOR;
				}
				iv >>>= 1;
				if ((hi & 0x1) != 0) {
					lo += Scale18f.SCALE_FACTOR;
				}
				hi >>>= 1;
				if ((lo & 0x1) != 0) {
					lo += 1;
					lo -= 1;//FIXME rounding, but we need the sign
				}
				lo >>>= 1;
				pow2++;
			}
			while (iv < 1) {
				lo <<= 1;
				if (lo >= Scale18f.SCALE_FACTOR) {
					lo -= Scale18f.SCALE_FACTOR;
					hi++;
				}
				hi <<= 1;
				if (hi >= Scale18f.SCALE_FACTOR) {
					hi -= Scale18f.SCALE_FACTOR;
					iv++;
				}
				pow2--;
			}
			normalize(0, pow2, hi + Scale18f.SCALE_FACTOR, lo);
		}
		void multiply(boolean isProductNegative, UnsignedDecimal36f factor, DecimalRounding rounding) {
			//split each factor into 9 digit parts
			long rhs3 = this.hiDecimal18f / Scale9f.SCALE_FACTOR;
			long rhs2 = this.hiDecimal18f - rhs3 * Scale9f.SCALE_FACTOR;
			long rhs1 = this.loDecimal18f / Scale9f.SCALE_FACTOR;
			long rhs0 = this.loDecimal18f - rhs1 * Scale9f.SCALE_FACTOR;
			long lhs3 = factor.hiDecimal18f / Scale9f.SCALE_FACTOR;
			long lhs2 = factor.hiDecimal18f - lhs3 * Scale9f.SCALE_FACTOR;
			long lhs1 = factor.loDecimal18f / Scale9f.SCALE_FACTOR;
			long lhs0 = factor.loDecimal18f - lhs1 * Scale9f.SCALE_FACTOR;
			
			//multiply now
			long scale72 = rhs0 * lhs0;
			long scale63 = rhs1 * lhs0 + lhs1 * rhs0;
			long scale54 = rhs2 * lhs0 + lhs2 * rhs0 + rhs1 * lhs1;
			long scale45 = rhs3 * lhs0 + lhs3 * rhs0 + rhs2 * lhs1 + lhs2 * rhs1;
			long scale36 = rhs3 * lhs1 + lhs3 * rhs1 + rhs2 * lhs2 + rhs0 + lhs0;
			long scale27 = rhs3 * lhs2 + lhs3 * rhs2 + rhs1 + lhs1;
			long scale18 = rhs3 * lhs3 + rhs2 + lhs2;
			long scale09 = rhs3 + lhs3;
			long scale00 = 1;
			
			//propagate carries
			long c;
			c = scale72 / Scale9f.SCALE_FACTOR;
			scale72 -= c * Scale9f.SCALE_FACTOR;
			scale63 += c;
			c = scale63 / Scale9f.SCALE_FACTOR;
			scale63 -= c * Scale9f.SCALE_FACTOR;
			scale54 += c;
			c = scale54 / Scale9f.SCALE_FACTOR;
			scale54 -= c * Scale9f.SCALE_FACTOR;
			scale45 += c;
			c = scale45 / Scale9f.SCALE_FACTOR;
			scale45 -= c * Scale9f.SCALE_FACTOR;
			scale36 += c;
			c = scale36 / Scale9f.SCALE_FACTOR;
			scale36 -= c * Scale9f.SCALE_FACTOR;
			scale27 += c;
			c = scale27 / Scale9f.SCALE_FACTOR;
			scale27 -= c * Scale9f.SCALE_FACTOR;
			scale18 += c;
			c = scale18 / Scale9f.SCALE_FACTOR;
			scale18 -= c * Scale9f.SCALE_FACTOR;
			scale09 += c;
			c = scale09 / Scale9f.SCALE_FACTOR;
			scale09 -= c * Scale9f.SCALE_FACTOR;
			scale00 += c;
			
			//assign highest 36 digits
			long hi = scale00 * Scale18f.SCALE_FACTOR + scale09 * Scale9f.SCALE_FACTOR + scale18;
			long lo = scale27 * Scale9f.SCALE_FACTOR + scale36;
			
			//apply rounding
			if (rounding != DecimalRounding.DOWN) {
				TruncatedPart truncatedPart = RoundingUtil.truncatedPartFor(scale45, Scale9f.SCALE_FACTOR);
				if (truncatedPart == TruncatedPart.ZERO) {
					if (scale54 != 0 | scale63 != 0 | scale72 != 0) {
						truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
					}
				} else if (truncatedPart == TruncatedPart.EQUAL_TO_HALF) {
					if (scale54 != 0 | scale63 != 0 | scale72 != 0) {
						truncatedPart = TruncatedPart.GREATER_THAN_HALF;
					}
				}
				final int inc;
				if (isProductNegative) {
					inc = -rounding.calculateRoundingIncrement(-1, -lo, truncatedPart); 
				} else {
					inc = rounding.calculateRoundingIncrement(1, lo, truncatedPart); 
				}
				if (inc != 0) {
					lo += inc;
					if (lo > Scale18f.SCALE_FACTOR) {
						lo -= Scale18f.SCALE_FACTOR;
						hi++;
					}
				}
			}
			normalize(this.pow10 + factor.pow10, this.pow2 + factor.pow2, hi, lo);
		}
		private void normalize(int pow10, int pow2, long hi, long lo) {
			while (hi >= 2*Scale18f.SCALE_FACTOR) {
				if ((hi & 0x1) != 0) {
					lo += Scale18f.SCALE_FACTOR;
				}
				hi >>>= 1;
				if ((lo & 0x1) != 0) {
					lo += 1;
					lo -= 1;//FIXME rounding, but we need the sign
				}
				lo >>>= 1;
				pow2++;
			}
			while (hi < Scale18f.SCALE_FACTOR) {
				hi <<= 1;
				lo <<= 1;
				if (lo >= Scale18f.SCALE_FACTOR) {
					hi |= 0x1;
					lo -= Scale18f.SCALE_FACTOR;
				}
				pow2--;
			}
			//the 36 digit value val=(hi|lo) with scale36 is aligned: 2.0 > val >= 1.0 
			
			//we don't want the leading 1 digit
			hi -= Scale18f.SCALE_FACTOR;
			
			//assign result
			this.hiDecimal18f = hi;
			this.loDecimal18f = lo;
			this.pow2 = pow2;
		}
		long round(int sgn, DecimalArithmetics arith, DecimalRounding rounding) {
			return round(sgn, hiDecimal18f + Scale18f.SCALE_FACTOR, loDecimal18f, arith, rounding);
		}
		private static long round(int sgn, long hi, long lo, DecimalArithmetics arith, DecimalRounding rounding) {
			final long truncated = sgn >= 0 ? hi : arith.negate(hi);
			return truncated + RoundingUtil.calculateRoundingIncrementForDivision(rounding, truncated, lo, Scale18f.SCALE_FACTOR); 
		}
		long getDecimal(int sgn, DecimalArithmetics arith, DecimalRounding rounding) {
			if (pow2 > 0) {
				final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
				final int nlzScaleFactor = scaleMetrics.getScaleFactorNumberOfLeadingZeros();
				if (pow2 < nlzScaleFactor || arith.getOverflowMode() == OverflowMode.UNCHECKED) {
					return getDecimalShiftLeft(sgn, pow2, hiDecimal18f + Scale18f.SCALE_FACTOR, loDecimal18f, arith, rounding);
				}
				//checked
				if (sgn < 0 & pow2 == nlzScaleFactor) {
					//only ok if result is exactly Long.MIN_VALUE (after rounding)
					final long result = getDecimalShiftLeft(sgn, pow2, hiDecimal18f + Scale18f.SCALE_FACTOR, loDecimal18f, arith, rounding);
					if (result == Long.MIN_VALUE) {
						return result;
					}
				}
				//pow2 > nlzScaleFactor or pow2 == nlzScaleFactor with overflow
				throw new ArithmeticException("Overflow: " + toString(pow2, sgn < 0 ? -hiDecimal18f : hiDecimal18f, loDecimal18f));
			}
			return getDecimalShiftRight(sgn, -pow2, hiDecimal18f + Scale18f.SCALE_FACTOR, loDecimal18f, arith, rounding);
		}
		private static long getDecimalShiftLeft(int sgn, int n, long hi, long lo, DecimalArithmetics arith, DecimalRounding rounding) {
			if (n >= 2 * SIZE_SCALE_FACTOR_18) {
				return 0;//overflow: all bits would be shifted out to the left
			}
			long iv = 1;
			hi -= Scale18f.SCALE_FACTOR;
			while (n > 0) {
				iv <<= 1;
				hi <<= 1;
				lo <<= 1;
				if (lo >= Scale18f.SCALE_FACTOR) {
					lo -= Scale18f.SCALE_FACTOR;
					hi |= 0x1;
				}
				if (hi >= Scale18f.SCALE_FACTOR) {
					hi -= Scale18f.SCALE_FACTOR;
					iv |= 0x1;
				}
				n--;
			}
			//create scale 18 value without integer part first
			long value18 = sgn < 0 ? -hi : hi;
			if (lo != 0 & rounding != DecimalRounding.DOWN) {
				value18 += RoundingUtil.calculateRoundingIncrement(rounding, value18, lo, Scale18f.SCALE_FACTOR);
			}
			//rescale and add integer part now
			final long fVal = arith.fromUnscaled(value18, 18);
			final long iVal = arith.fromLong(iv);
			return sgn >= 0 ? arith.add(iVal, fVal) : arith.add(arith.negate(iVal), fVal);
		}
		private static long getDecimalShiftRight(int sgn, int n, long hi, long lo, DecimalArithmetics arith, DecimalRounding rounding) {
			long value18;
			if (n < SIZE_SCALE_FACTOR_18) {
				final long truncated = hi >>> n;
				final long hiRemainder = (hi >>> (64 - n)) & (-n >> 63);
				TruncatedPart truncatedPart = RoundingUtil.truncatedPartFor2powN(hiRemainder, n);
				if (lo != 0) {
					if (truncatedPart == TruncatedPart.ZERO) truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
					else if (truncatedPart == TruncatedPart.EQUAL_TO_HALF) truncatedPart = TruncatedPart.GREATER_THAN_HALF;
				}
				final long signed = sgn >= 0 ? truncated : arith.negate(truncated);
				value18 = signed + rounding.calculateRoundingIncrement(sgn, signed, truncatedPart);
			} else {
				//n >= SIZE_SCALE_FACTOR_18, nothing left but rounding
				final TruncatedPart truncatedPart;
				if (n > SIZE_SCALE_FACTOR_18) {
					truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;//there is always the leading one digit
				} else {//n == SIZE_SCALE_FACTOR_18
					truncatedPart = (hi > Scale18f.SCALE_FACTOR | lo != 0) ? TruncatedPart.GREATER_THAN_HALF : TruncatedPart.EQUAL_TO_HALF;
				}
				value18 = rounding.calculateRoundingIncrement(sgn, 0, truncatedPart);
			}
			return arith.fromUnscaled(value18, 18);
		}
		@Override
		public String toString() {
			return toString(pow2, hiDecimal18f, loDecimal18f);
		}
		private static String toString(final int pow2, final long hi, final long lo) {
			int len;
			final StringBuilder sb = new StringBuilder(50);//2*18 + 1 sign + 1 leading digit + some space for pow2 stuff
			sb.append(hi >= 0 ? 1 : -1);
			sb.append('.');
			len = sb.length();
			sb.append(hi);
			sb.insert(len, "000000000000000000", 0, len + 18 - sb.length());
			len = sb.length(); 
			sb.append(lo);
			sb.insert(len, "000000000000000000", 0, len + 18 - sb.length());
			sb.append("*2^").append(pow2);
			return sb.toString();
		}
	}

	/**
	 * From {@link BigDecimal#pow(int, MathContext)}.
	 * <p>
	 * Returns an unscaled decimal whose value is <tt>(this<sup>n</sup>)</tt>.
	 * The current implementation uses the core algorithm defined in ANSI
	 * standard X3.274-1996 with rounding according to the context settings. In
	 * general, the returned numerical value is within two ulps of the exact
	 * numerical value for the chosen precision. Note that future releases may
	 * use a different algorithm with a decreased allowable error bound and
	 * increased allowable exponent range.
	 *
	 * <p>
	 * The X3.274-1996 algorithm is:
	 *
	 * <ul>
	 * <li>An {@code ArithmeticException} exception is thrown if
	 * <ul>
	 * <li>{@code abs(n) > 999999999}
	 * <li>{@code mc.precision == 0} and {@code n < 0}
	 * <li>{@code mc.precision > 0} and {@code n} has more than
	 * {@code mc.precision} decimal digits
	 * </ul>
	 *
	 * <li>if {@code n} is zero, {@link #ONE} is returned even if {@code this}
	 * is zero, otherwise
	 * <ul>
	 * <li>if {@code n} is positive, the result is calculated via the repeated
	 * squaring technique into a single accumulator. The individual
	 * multiplications with the accumulator use the same math context settings
	 * as in {@code mc} except for a precision increased to
	 * {@code mc.precision + elength + 1} where {@code elength} is the number of
	 * decimal digits in {@code n}.
	 *
	 * <li>if {@code n} is negative, the result is calculated as if {@code n}
	 * were positive; this value is then divided into one using the working
	 * precision specified above.
	 *
	 * <li>The final value from either the positive or negative case is then
	 * rounded to the destination precision.
	 * </ul>
	 * </ul>
	 *
	 * @param n
	 *            power to raise this {@code BigDecimal} to.
	 * @param mc
	 *            the context to use.
	 * @return <tt>this<sup>n</sup></tt> using the ANSI standard X3.274-1996
	 *         algorithm
	 * @throws ArithmeticException
	 *             if the result is inexact but the rounding mode is
	 *             {@code UNNECESSARY}, or {@code n} is out of range.
	 * @since 1.5
	 */
	private static long powWithPositiveExponent(DecimalArithmetics arith, DecimalRounding rounding, long uDecimalBase, int n) {
		if (n < -999999999 || n > 999999999) {
			throw new ArithmeticException("Exponent must be in [-999999999,999999999] but was: " + n);
		}
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final long scaleFactor = scaleMetrics.getScaleFactor();
		if (n == 0) {
			//return 1
			return scaleFactor;
		}

		//eliminate sign
		final int sgn = ((n & 0x1) != 0) & uDecimalBase < 0 ? -1 : 1;//zero is not possible 
		final long absBase = Math.abs(uDecimalBase);
		
		//shift right until it is smaller than 10^18
		final UnsignedDecimal36f lhs = new UnsignedDecimal36f(absBase, scaleMetrics);
		
		//36 digit accumulator, initialized with one without leading 1 digit
		final UnsignedDecimal36f acc = new UnsignedDecimal36f();
		
		// ready to carry out power calculation...
		int mag = n;
        int mul = 0;        			// how many times multiplied with lhs?
        for (int i=1;;i++) {            // for each bit [top bit ignored]
            mag += mag;                 // shift left 1 bit
            if (mag < 0) {              // top bit is set
            	mul++;
            	final boolean neg = sgn < 0 & ((mul & 0x1) != 0);
                acc.multiply(neg, lhs, rounding); // acc=acc*x
            }
            if (i == 31)
                break;                  // that was the last bit
            if (mul > 0)
            	acc.multiply(false, acc, rounding);   // acc=acc*acc [square]
                // else (!seenbit) no point in squaring ONE
        }
        
        if (n < 0) {
    		//we apply pow2 after division
        	final DecimalArithmetics arith18 = Scale18f.INSTANCE.getArithmetics(arith.getRoundingMode());//unchecked is fine, see comments below 
    		final long divisor = acc.round(sgn, arith, rounding);
    		final long inverted = arith18.invert(divisor);//can't overflow as divisor is in [-2, 2]
    		final int pow2 = acc.pow2;
    		if (pow2 <= 0) {
    			//rescale first, then multiply by pow2
    			final long rescaled = arith.fromUnscaled(inverted, 18);
    			return arith.shiftLeft(rescaled, -pow2);
    		}
    		//divide by pow2 first, then rescale
    		final long shifted = arith18.shiftRight(inverted, pow2);//no overflow as this is a division
			return arith.fromUnscaled(shifted, 18);
        }
        return acc.getDecimal(sgn, arith, rounding);
	}
	
	public static long powChecked(DecimalArithmetics arith, DecimalRounding rounding, long uDecimalBase, int exponent) {
		final SpecialPowResult special = SpecialPowResult.getFor(arith, uDecimalBase, exponent);
		if (special != null) {
			return special.pow(arith, uDecimalBase, exponent);
		}

		//some other special cases
		try {
			if (exponent > 0) {
				final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
				final long fractionalPart = scaleMetrics.moduloByScaleFactor(uDecimalBase);
				if (fractionalPart == 0) {
					final long lBase = scaleMetrics.divideByScaleFactor(uDecimalBase);
					final long lResult = powLongCheckedWithPositiveExponent(lBase, exponent);
					return scaleMetrics.multiplyByScaleFactorExact(lResult);
				}

				//try long method checked
				try {
					final long powered = powLongCheckedWithPositiveExponent(uDecimalBase, exponent);
					return Pow10.divideByPowerOf10(rounding, powered, (exponent - 1) * scaleMetrics.getScale());
				} catch (ArithmeticException e) {
					//ignore, fallback to slower method below
				}
			}

			//ok, then the slow method via BigDecimal
			final BigDecimal bigDecimalResult = powToBigDecimal(arith, uDecimalBase, exponent);
			return JDKSupport.bigIntegerToLongValueExact(bigDecimalResult.unscaledValue());
		} catch (ArithmeticException e) {
			throw new ArithmeticException("Overflow: " + arith.toString(uDecimalBase) + "^" + exponent);
		}
	}

	private static BigDecimal powToBigDecimal(DecimalArithmetics arith, long uDecimal, int exponent) {
		//don't know how to do better than by using a BigInteger
		final int scale = arith.getScale();
		final BigDecimal bigDecimalBase = BigDecimal.valueOf(uDecimal, scale);
		if (exponent >= 0) {
			return bigDecimalBase.pow(exponent).setScale(scale, arith.getRoundingMode());
		} else {
			return BigDecimal.ONE.divide(bigDecimalBase.pow(-exponent), scale, arith.getRoundingMode());
		}
	}

	private static long powLongWithPositiveExponent(DecimalArithmetics arith, long lBase, int exponent) {
		assert (exponent > 0);

		long accum = 1;
		while (true) {
			switch (exponent) {
			case 0:
				return accum;
			case 1:
				return accum * lBase;
			default:
				if ((exponent & 1) != 0) {
					accum *= lBase;
				}
				exponent >>= 1;
				if (exponent > 0) {
					lBase *= lBase;
				}
			}
		}
	}

	private static long powLongCheckedWithPositiveExponent(long lBase, int exponent) {
		assert (exponent > 0);
		if (lBase >= -2 & lBase <= 2) {
			switch ((int) lBase) {
			case 0:
				return (exponent == 0) ? 1 : 0;
			case 1:
				return 1;
			case (-1):
				return ((exponent & 1) == 0) ? 1 : -1;
			case 2:
				if (exponent >= Long.SIZE - 1) {
					throw new ArithmeticException("Overflow: " + lBase + "^" + exponent);
				}
				return 1L << exponent;
			case (-2):
				if (exponent >= Long.SIZE) {
					throw new ArithmeticException("Overflow: " + lBase + "^" + exponent);
				}
				return ((exponent & 1) == 0) ? (1L << exponent) : (-1L << exponent);
			default:
				throw new AssertionError();
			}
		}
		long accum = 1;
		while (true) {
			switch (exponent) {
			case 0:
				return accum;
			case 1:
				return Checked.multiplyLong(accum, lBase);
			default:
				if ((exponent & 1) != 0) {
					accum = Checked.multiplyLong(accum, lBase);
				}
				exponent >>= 1;
				if (exponent > 0) {
					if (lBase > FLOOR_SQRT_MAX_LONG | lBase < -FLOOR_SQRT_MAX_LONG) {
						throw new ArithmeticException("Overflow: " + lBase + "^" + exponent);
					}
					lBase *= lBase;
				}
			}
		}
	}

	//no instances
	private Pow() {}

}
