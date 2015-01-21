package ch.javasoft.decimal.arithmetic;

import ch.javasoft.decimal.scale.Scale18f;
import ch.javasoft.decimal.scale.Scale9f;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.scale.Scales;
import ch.javasoft.decimal.truncate.DecimalRounding;
import ch.javasoft.decimal.truncate.OverflowMode;
import ch.javasoft.decimal.truncate.TruncatedPart;

final class UnsignedDecimal36f {
	private static final int NLZ_SCALE_FACTOR_18 = 4;//Scale18f.NLZ_SCALE_FACTOR
	private static final int SIZE_SCALE_FACTOR_18 = Long.SIZE - NLZ_SCALE_FACTOR_18;//64 - Scale18f.NLZ_SCALE_FACTOR
	private int pow2;
	private long hiDecimal18f;
	private long loDecimal18f;
	private UnsignedDecimal36f() {
		super();
	}
	public static UnsignedDecimal36f one() {
		return new UnsignedDecimal36f();
	}
	public int getPow2() {
		return pow2;
	}
	public UnsignedDecimal36f(long unscaled, ScaleMetrics scaleMetrics) {
		if (unscaled == 0) {
			return;
		}
		final long ival = scaleMetrics.divideByScaleFactor(unscaled);
		final long fval = unscaled - scaleMetrics.multiplyByScaleFactor(ival);
		final ScaleMetrics diffMetrics = Scales.valueOf(18 - scaleMetrics.getScale());
		init(ival, diffMetrics.multiplyByScaleFactor(fval));
	}
	public UnsignedDecimal36f(long longValue) {
		if (longValue == 0) {
			return;
		}
		init(longValue, 0);
	}
	public void init(long ival, long fval18) {
		int pow2 = 0;
		long hi = fval18;
		long lo = 0;
		long iv = ival;
		while (iv >= 2 | iv < 0) {
			if ((iv & 0x1) != 0) {
				hi += Scale18f.SCALE_FACTOR;
			}
			iv >>>= 1;
			if ((hi & 0x1) != 0) {
				lo += Scale18f.SCALE_FACTOR;
			}
			hi >>>= 1;
			if ((lo & 0x1) != 0) {
				throw new IllegalArgumentException("should not lead to rounding");
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

		//assign result
		this.pow2 = pow2;
		this.hiDecimal18f = hi;
		this.loDecimal18f = lo;
	}
	public final void multiply(int sgn, UnsignedDecimal36f factor, DecimalRounding rounding) {
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
		long l0 = scale45 * Scale9f.SCALE_FACTOR + scale54;
		long l1 = scale63 * Scale9f.SCALE_FACTOR + scale72;
		
		normalize(sgn, this.pow2 + factor.pow2, hi, lo, l0, l1, rounding);
	}
	private void normalize(int sgn, int pow2, long hi, long lo, long l0, long l1, DecimalRounding rounding) {
		//shift right
		while (hi >= 2*Scale18f.SCALE_FACTOR) {
			if ((hi & 0x1) != 0) {
				lo += Scale18f.SCALE_FACTOR;
			}
			hi >>>= 1;
			if ((lo & 0x1) != 0) {
				l0 += Scale18f.SCALE_FACTOR;
			}
			lo >>>= 1;
			if ((l0 & 0x1) != 0) {
				l1 += Scale18f.SCALE_FACTOR;
			}
			l0 >>>= 1;
			if ((l1 & 0x1) != 0) {
				l1 += getRoundingIncrement(sgn, l1, rounding, TruncatedPart.EQUAL_TO_HALF);
			}
			l1 >>>= 1;
			pow2++;
		}
		//shift left
		while (hi < Scale18f.SCALE_FACTOR) {
			hi <<= 1;
			lo <<= 1;
			l0 <<= 1;
			l1 <<= 1;
			if (l1 >= Scale18f.SCALE_FACTOR) {
				lo += 1;
				l1 -= Scale18f.SCALE_FACTOR;
			}
			if (l0 >= Scale18f.SCALE_FACTOR) {
				lo += 1;
				l0 -= Scale18f.SCALE_FACTOR;
			}
			if (lo >= Scale18f.SCALE_FACTOR) {
				hi += 1;
				lo -= Scale18f.SCALE_FACTOR;
			}
			pow2--;
		}
		//the 36 digit value val=(hi|lo) with scale36 is aligned: 2.0 > val >= 1.0
		
		//apply rounding
		assignRounded(sgn, pow2, hi, lo, l0, l1, rounding);
	}
	private void assignRounded(int sgn, int pow2, long hi, long lo, long l0, long l1, DecimalRounding rounding) {
		if (rounding != DecimalRounding.DOWN) {
			lo += getRoundingIncrement(sgn, hi, lo, l0, l1, rounding);
			if (lo >= Scale18f.SCALE_FACTOR) {
				hi += 1;
				lo -= Scale18f.SCALE_FACTOR;
				if (hi >= 2*Scale18f.SCALE_FACTOR) {
					//shift right
					if ((hi & 0x1) != 0) {
						//would just overflow once again: lo += Scale18f.SCALE_FACTOR
						//hence:
						hi += 1;//can't overflow twice in a row when adding 2
					}
					hi >>>= 1;
					if ((lo & 0x1) != 0) {
						//can't overflow twice in a row when adding 2
						lo += getRoundingIncrement(sgn, lo, rounding, TruncatedPart.EQUAL_TO_HALF);
					}
					lo >>>= 1;
					pow2++;
				}
			}
		}
		
		//we don't want the leading 1 digit
		hi -= Scale18f.SCALE_FACTOR;
		
		//assign result
		this.pow2 = pow2;
		this.hiDecimal18f = hi;
		this.loDecimal18f = lo;
	}
	
	private static int getRoundingIncrement(int sgn, long hi, long lo, DecimalRounding rounding) {
		final TruncatedPart truncatedPart = RoundingUtil.truncatedPartFor(lo, Scale18f.SCALE_FACTOR);
		return getRoundingIncrement(sgn, hi, rounding, truncatedPart);
	}
	private static int getRoundingIncrement(int sgn, long hi, long lo, long l0, long l1, DecimalRounding rounding) {
		//apply rounding
		if (rounding != DecimalRounding.DOWN & (l0 != 0 | l1 != 0)) {
			TruncatedPart truncatedPart = RoundingUtil.truncatedPartFor(l0, Scale18f.SCALE_FACTOR);
			if (l1 != 0) {
				if (truncatedPart == TruncatedPart.ZERO) truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
				else if (truncatedPart == TruncatedPart.EQUAL_TO_HALF) truncatedPart = TruncatedPart.GREATER_THAN_HALF;
			}
			return getRoundingIncrement(sgn, lo, rounding, truncatedPart);
		}
		return 0;
	}
	private static final int getRoundingIncrement(int sgn, long absValue, DecimalRounding rounding, TruncatedPart truncatedPart) {
		if (sgn < 0) {
			return -rounding.calculateRoundingIncrement(-1, -absValue, truncatedPart); 
		} else {
			return rounding.calculateRoundingIncrement(1, absValue, truncatedPart); 
		}
	}
	public final long getRaw(int sgn, DecimalArithmetics arith, DecimalRounding rounding) {
		return round(sgn, hiDecimal18f + Scale18f.SCALE_FACTOR, loDecimal18f, arith, rounding);
	}
	private static long round(int sgn, long hi, long lo, DecimalArithmetics arith, DecimalRounding rounding) {
		final long truncated = sgn >= 0 ? hi : arith.negate(hi);
		return truncated + RoundingUtil.calculateRoundingIncrementForDivision(rounding, truncated, lo, Scale18f.SCALE_FACTOR); 
	}
	public final long getDecimal(int sgn, DecimalArithmetics arith, DecimalRounding rounding) {
		return shift(sgn, pow2, hiDecimal18f + Scale18f.SCALE_FACTOR, loDecimal18f, arith, rounding);
	}
	public final long getInverted(int sgn, DecimalArithmetics arith, DecimalRounding rounding) {
		long hiDividend = Scale18f.SCALE_FACTOR;
		long loDividend = 0;
		final long hiDivisor = hiDecimal18f + Scale18f.SCALE_FACTOR;
		final long loDivisor = loDecimal18f;
		long hiQuot = 0;
		long loQuot = 0;
		for (int i = 0; i < 2*SIZE_SCALE_FACTOR_18-1; i++) {
			loDividend <<= 1;
			hiDividend <<= 1;
			if (loDividend >= Scale18f.SCALE_FACTOR) {
				hiDividend |= 0x1;
				loDividend -= Scale18f.SCALE_FACTOR;
			}
			loQuot <<= 1;
			hiQuot <<= 1;
			if (loQuot >= Scale18f.SCALE_FACTOR) {
				hiQuot |= 0x1;
				loQuot -= Scale18f.SCALE_FACTOR;
			}
			if (isGreaterOrEqual(hiDividend, loDividend, hiDivisor, loDivisor)) {
				loQuot |= 0x1;
				hiDividend -= hiDivisor;
				loDividend -= loDivisor;
				if (loDividend < 0) {
					loDividend += Scale18f.SCALE_FACTOR;
					hiDividend--;
				}
			}
		}
		//normalize (it is smaller than one atm)
		int pow2 = -this.pow2;
		while (hiQuot < Scale18f.SCALE_FACTOR) {
			loQuot <<= 1;
			hiQuot <<= 1;
			if (loQuot >= Scale18f.SCALE_FACTOR) {
				hiQuot |= 0x1;
				loQuot -= Scale18f.SCALE_FACTOR;
			}
			pow2--;
		}
		return shift(sgn, -pow2, hiQuot, loQuot, arith, rounding);
	}
	private boolean isGreaterOrEqual(long hiDividend, long loDividend, long hiDivisor, long loDivisor) {
		return hiDividend > hiDivisor | (hiDividend == hiDivisor & loDividend >= loDivisor);
	}
	private static long shift(int sgn, int pow2, long hi, long lo, DecimalArithmetics arith, DecimalRounding rounding) {
		if (pow2 > 0) {
			final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
			final int nlzScaleFactor = scaleMetrics.getScaleFactorNumberOfLeadingZeros();
			if (pow2 < nlzScaleFactor || arith.getOverflowMode() == OverflowMode.UNCHECKED) {
				return shiftLeft(sgn, pow2, hi, lo, arith, rounding);
			}
			//checked
			if (sgn < 0 & pow2 == nlzScaleFactor) {
				//only ok if result is exactly Long.MIN_VALUE (after rounding)
				final long result = shiftLeft(sgn, pow2, hi, lo, arith, rounding);
				if (result == Long.MIN_VALUE) {
					return result;
				}
			}
			//pow2 > nlzScaleFactor or pow2 == nlzScaleFactor with overflow
			throw new ArithmeticException("Overflow: " + toString(pow2, sgn < 0 ? -(hi - Scale18f.SCALE_FACTOR): hi - Scale18f.SCALE_FACTOR, lo));
		}
		return shiftRight(sgn, -pow2, hi, lo, arith, rounding);
	}
	private static long shiftLeft(int sgn, int n, long hi, long lo, DecimalArithmetics arith, DecimalRounding rounding) {
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

		//apply rounding now and forget about lo
		hi += getRoundingIncrement(sgn, hi, lo, rounding);
		//it's ok if hi is now equal to Scale18f.SCALE_FACTOR as we add iv and hi anyway below
		
		//create separate values for integer and fractional parts
		final long fVal = arith.fromUnscaled(sgn >= 0 ? hi : -hi, 18);//may involve rounding, hence signed
		final long iVal = arith.fromLong(iv);
		
		//apply sign to iVal and add together (overflows possible)
		return sgn >= 0 ? arith.add(fVal, iVal) : arith.subtract(fVal, iVal);
	}
	private static long shiftRight(int sgn, int n, long hi, long lo, DecimalArithmetics arith, DecimalRounding rounding) {
		long value18;
		if (n < SIZE_SCALE_FACTOR_18) {
			final long truncated = hi >>> n;
			final long hiRemainder = hi - (truncated << n);
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