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
	private long val3;
	private long val2;
	private long val1;
	private long val0;
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
		long l3 = fval18;
		long l2 = 0;
		long iv = ival;
		while (iv >= 2 | iv < 0) {
			if ((iv & 0x1) != 0) {
				l3 += Scale18f.SCALE_FACTOR;
			}
			iv >>>= 1;
			if ((l3 & 0x1) != 0) {
				l2 += Scale18f.SCALE_FACTOR;
			}
			l3 >>>= 1;
			if ((l2 & 0x1) != 0) {
				throw new IllegalArgumentException("should not lead to rounding");
			}
			l2 >>>= 1;
			pow2++;
		}
		while (iv < 1) {
			l2 <<= 1;
			if (l2 >= Scale18f.SCALE_FACTOR) {
				l2 -= Scale18f.SCALE_FACTOR;
				l3++;
			}
			l3 <<= 1;
			if (l3 >= Scale18f.SCALE_FACTOR) {
				l3 -= Scale18f.SCALE_FACTOR;
				iv++;
			}
			pow2--;
		}

		//assign result
		this.pow2 = pow2;
		this.val3 = l3;
		this.val2 = l2;
	}
	public final void multiply(int sgn, UnsignedDecimal36f factor, DecimalRounding rounding) {
		roundToVal3Val2(sgn, rounding);
		multiply(sgn, val3, val2, factor, rounding);
	}
	private void roundToVal3Val2(int sgn, DecimalRounding rounding) {
		//round (val1|val0) into (val3|val2)
		final int inc = getRoundingIncrement(sgn, val2, val1, val0, rounding);
		if (inc != 0) {
			val2++;
			if (val2 >= Scale18f.SCALE_FACTOR) {
				val2 -= Scale18f.SCALE_FACTOR;
				val3++;
				if (val3 >= Scale18f.SCALE_FACTOR) {
					if ((val3 & 0x1) != 0) {
						val3++;//because this would overflow again: val2 += Scale18f.SCALE_FACTOR
					}
					val3 >>>= 1;
					val2 >>>= 1;//even if we shift out 1 bit we should not round again as we have already added 1 (rounding should never add 2)
					pow2++;
				}
			}
		}
		//truncate val1, val0 
		this.val1 = 0;
		this.val0 = 0;
	}
	private void multiply(int sgn, long val3, long val2, UnsignedDecimal36f factor, DecimalRounding rounding) {
		//split each factor into 9 digit parts
		long rhs3 = val3 / Scale9f.SCALE_FACTOR;
		long rhs2 = val3 - rhs3 * Scale9f.SCALE_FACTOR;
		long rhs1 = val2 / Scale9f.SCALE_FACTOR;
		long rhs0 = val2 - rhs1 * Scale9f.SCALE_FACTOR;
		long lhs3 = factor.val3 / Scale9f.SCALE_FACTOR;
		long lhs2 = factor.val3 - lhs3 * Scale9f.SCALE_FACTOR;
		long lhs1 = factor.val2 / Scale9f.SCALE_FACTOR;
		long lhs0 = factor.val2 - lhs1 * Scale9f.SCALE_FACTOR;
		
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
		long l3 = scale00 * Scale18f.SCALE_FACTOR + scale09 * Scale9f.SCALE_FACTOR + scale18;
		long l2 = scale27 * Scale9f.SCALE_FACTOR + scale36;
		long l1 = scale45 * Scale9f.SCALE_FACTOR + scale54;
		long l0 = scale63 * Scale9f.SCALE_FACTOR + scale72;
		
		normalize(sgn, pow2 + factor.pow2, l3, l2, l1, l0, rounding);
	}
	private void normalize(int sgn, int pow2, long l3, long l2, long l1, long l0, DecimalRounding rounding) {
		//shift right
		while (l3 >= 2*Scale18f.SCALE_FACTOR) {
			if ((l3 & 0x1) != 0) {
				l2 += Scale18f.SCALE_FACTOR;
			}
			l3 >>>= 1;
			if ((l2 & 0x1) != 0) {
				l1 += Scale18f.SCALE_FACTOR;
			}
			l2 >>>= 1;
			if ((l1 & 0x1) != 0) {
				l0 += Scale18f.SCALE_FACTOR;
			}
			l1 >>>= 1;
			if ((l0 & 0x1) != 0) {
				l0 >>>= 1;
				l0 += getRoundingIncrement(sgn, l0, rounding, TruncatedPart.EQUAL_TO_HALF);
			} else {
				l0 >>>= 1;
			}
			pow2++;
		}
		//shift left
		while (l3 < Scale18f.SCALE_FACTOR) {
			l3 <<= 1;
			l2 <<= 1;
			l1 <<= 1;
			l0 <<= 1;
			if (l0 >= Scale18f.SCALE_FACTOR) {
				l2 += 1;
				l0 -= Scale18f.SCALE_FACTOR;
			}
			if (l1 >= Scale18f.SCALE_FACTOR) {
				l2 += 1;
				l1 -= Scale18f.SCALE_FACTOR;
			}
			if (l2 >= Scale18f.SCALE_FACTOR) {
				l3 += 1;
				l2 -= Scale18f.SCALE_FACTOR;
			}
			pow2--;
		}
		//the 36 digit value val=(l3|l2) with scale36 is aligned: 2.0 > val >= 1.0
		
		this.pow2 = pow2;
		this.val3 = l3 - Scale18f.SCALE_FACTOR;//we don't want the leading 1 digit
		this.val2 = l2;
		this.val1 = l1;
		this.val0 = l0;
	}
	
	private static int getRoundingIncrement(int sgn, long l3, long l2, long l1, long l0, DecimalRounding rounding) {
		//apply rounding
		if (rounding != DecimalRounding.DOWN & (l2 != 0 | l1 != 0 | l0 != 0)) {
			TruncatedPart truncatedPart = RoundingUtil.truncatedPartFor(l2, Scale18f.SCALE_FACTOR);
			if (l1 != 0 | l0 != 0) {
				if (truncatedPart == TruncatedPart.ZERO) truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
				else if (truncatedPart == TruncatedPart.EQUAL_TO_HALF) truncatedPart = TruncatedPart.GREATER_THAN_HALF;
			}
			return getRoundingIncrement(sgn, l3, rounding, truncatedPart);
		}
		return 0;
	}
	private static int getRoundingIncrement(int sgn, long l2, long l1, long l0, DecimalRounding rounding) {
		//apply rounding
		if (rounding != DecimalRounding.DOWN & (l1 != 0 | l0 != 0)) {
			TruncatedPart truncatedPart = RoundingUtil.truncatedPartFor(l1, Scale18f.SCALE_FACTOR);
			if (l0 != 0) {
				if (truncatedPart == TruncatedPart.ZERO) truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
				else if (truncatedPart == TruncatedPart.EQUAL_TO_HALF) truncatedPart = TruncatedPart.GREATER_THAN_HALF;
			}
			return getRoundingIncrement(sgn, l2, rounding, truncatedPart);
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
		return round(sgn, val3 + Scale18f.SCALE_FACTOR, val2, arith, rounding);
	}
	private static long round(int sgn, long l3, long l2, DecimalArithmetics arith, DecimalRounding rounding) {
		final long truncated = sgn >= 0 ? l3 : arith.negate(l3);
		return truncated + RoundingUtil.calculateRoundingIncrementForDivision(rounding, truncated, l2, Scale18f.SCALE_FACTOR); 
	}
	public final long getDecimal(int sgn, DecimalArithmetics arith, DecimalRounding rounding) {
		return shift(sgn, pow2, val3 + Scale18f.SCALE_FACTOR, val2, val1, val0, arith, rounding);
	}
	public final long getInverted(int sgn, DecimalArithmetics arith, DecimalRounding rounding) {
		long hiDividend = Scale18f.SCALE_FACTOR;
		long loDividend = 0;
		final long hiDivisor = val3 + Scale18f.SCALE_FACTOR;
		final long loDivisor = val2;
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
		return shift(sgn, -pow2, hiQuot, loQuot, 0, 0, arith, rounding);
	}
	private boolean isGreaterOrEqual(long hiDividend, long loDividend, long hiDivisor, long loDivisor) {
		return hiDividend > hiDivisor | (hiDividend == hiDivisor & loDividend >= loDivisor);
	}
	private static long shift(int sgn, int pow2, long l3, long l2, long l1, long l0, DecimalArithmetics arith, DecimalRounding rounding) {
		if (pow2 > 0) {
			final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
			final int nlzScaleFactor = scaleMetrics.getScaleFactorNumberOfLeadingZeros();
			if (pow2 < nlzScaleFactor || arith.getOverflowMode() == OverflowMode.UNCHECKED) {
				return shiftLeft(sgn, pow2, l3, l2, l1, l0, arith, rounding);
			}
			//checked
			if (sgn < 0 & pow2 == nlzScaleFactor) {
				//only ok if result is exactly Long.MIN_VALUE (after rounding)
				final long result = shiftLeft(sgn, pow2, l3, l2, l1, l0, arith, rounding);
				if (result == Long.MIN_VALUE) {
					return result;
				}
			}
			//pow2 > nlzScaleFactor or pow2 == nlzScaleFactor with overflow
			throw new ArithmeticException("Overflow: " + toString(pow2, sgn < 0 ? -(l3 - Scale18f.SCALE_FACTOR): l3 - Scale18f.SCALE_FACTOR, l2));
		}
		return shiftRight(sgn, -pow2, l3, l2, l1, l0, arith, rounding);
	}
	private static long shiftLeft(int sgn, int n, long l3, long l2, long l1, long l0, DecimalArithmetics arith, DecimalRounding rounding) {
		if (n >= 4 * SIZE_SCALE_FACTOR_18) {
			return 0;//overflow: all bits would be shifted out to the left
		}
		long iv = 1;
		l3 -= Scale18f.SCALE_FACTOR;
		while (n > 0) {
			iv <<= 1;
			l3 <<= 1;
			l2 <<= 1;
			l1 <<= 1;
			l0 <<= 1;
			if (l0 >= Scale18f.SCALE_FACTOR) {
				l0 -= Scale18f.SCALE_FACTOR;
				l1 |= 0x1;
			}
			if (l1 >= Scale18f.SCALE_FACTOR) {
				l1 -= Scale18f.SCALE_FACTOR;
				l2 |= 0x1;
			}
			if (l2 >= Scale18f.SCALE_FACTOR) {
				l2 -= Scale18f.SCALE_FACTOR;
				l3 |= 0x1;
			}
			if (l3 >= Scale18f.SCALE_FACTOR) {
				l3 -= Scale18f.SCALE_FACTOR;
				iv |= 0x1;
			}
			n--;
		}

		//apply rounding now and forget about l2
		l3 += getRoundingIncrement(sgn, l3, l2, l1, l0, rounding);
		//it's ok if l3 is now equal to Scale18f.SCALE_FACTOR as we add iv and l3 anyway below
		
		//create separate values for integer and fractional parts
		final long fVal = arith.fromUnscaled(sgn >= 0 ? l3 : -l3, 18);//may involve rounding, hence signed
		final long iVal = arith.fromLong(iv);
		
		//apply sign to iVal and add together (overflows possible)
		return sgn >= 0 ? arith.add(fVal, iVal) : arith.subtract(fVal, iVal);
	}
	private static long shiftRight(int sgn, int n, long l3, long l2, long l1, long l0, DecimalArithmetics arith, DecimalRounding rounding) {
		long value18;
		if (n < SIZE_SCALE_FACTOR_18) {
			final long truncated = l3 >>> n;
			final long hiRemainder = l3 - (truncated << n);
			TruncatedPart truncatedPart = RoundingUtil.truncatedPartFor2powN(hiRemainder, n);
			if (l2 != 0 | l1 != 0 | l0 != 0) {
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
				truncatedPart = (l3 > Scale18f.SCALE_FACTOR | l2 != 0 | l1 != 0 | l0 != 0) ? TruncatedPart.GREATER_THAN_HALF : TruncatedPart.EQUAL_TO_HALF;
			}
			value18 = rounding.calculateRoundingIncrement(sgn, 0, truncatedPart);
		}
		return arith.fromUnscaled(value18, 18);
	}
	@Override
	public String toString() {
		return toString(pow2, val3, val2);
	}
	private static String toString(final int pow2, final long l3, final long l2) {
		int len;
		final StringBuilder sb = new StringBuilder(50);//2*18 + 1 sign + 1 leading digit + some space for pow2 stuff
		sb.append(l3 >= 0 ? 1 : -1);
		sb.append('.');
		len = sb.length();
		sb.append(l3);
		sb.insert(len, "000000000000000000", 0, len + 18 - sb.length());
		len = sb.length(); 
		sb.append(l2);
		sb.insert(len, "000000000000000000", 0, len + 18 - sb.length());
		sb.append("*2^").append(pow2);
		return sb.toString();
	}
}