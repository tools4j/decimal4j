package org.decimal4j.arithmetic;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncatedPart;

import org.decimal4j.scale.Scale18f;
import org.decimal4j.scale.Scale8f;
import org.decimal4j.scale.Scale9f;

final class UnsignedDecimal9x36f {
	/** Thread local for (L)eft (H)and (S)ide operator*/
	static final ThreadLocal<UnsignedDecimal9x36f> LHS = new ThreadLocal<UnsignedDecimal9x36f>() {
		@Override
		protected UnsignedDecimal9x36f initialValue() {
			return new UnsignedDecimal9x36f();
		}
	};
	/** Thread local for accumulator*/
	static final ThreadLocal<UnsignedDecimal9x36f> ACC = new ThreadLocal<UnsignedDecimal9x36f>() {
		@Override
		protected UnsignedDecimal9x36f initialValue() {
			return new UnsignedDecimal9x36f();
		}
	};
	
	private int pow10;
	private long ival;
	private long val3;
	private long val2;
	private long val1;
	private long val0;
	
	/** Constructor for ONE*/
	private UnsignedDecimal9x36f() {
		super();
	}
	public UnsignedDecimal9x36f initOne() {
		this.pow10 = 0;
		this.ival = 1;
		this.val3 = 0;
		this.val2 = 0;
		this.val1 = 0;
		this.val0 = 0;
		return this;
	}
	public UnsignedDecimal9x36f init(UnsignedDecimal9x36f copy) {
		this.pow10 = copy.pow10;
		this.ival = copy.ival;
		this.val3 = copy.val3;
		this.val2 = copy.val2;
		this.val1 = copy.val1;
		this.val0 = copy.val0;
		return this;
	}
	public UnsignedDecimal9x36f init(long ival, long fval, ScaleMetrics scaleMetrics) {
		final ScaleMetrics diffMetrics = Scales.valueOf(18 - scaleMetrics.getScale());
		normalizeAndRound(1, 0, ival, diffMetrics.multiplyByScaleFactor(fval), 0, 0, 0, DecimalRounding.UNNECESSARY);
		return this;
	}
	public int getPow10() {
		return pow10;
	}
	private void normalizeAndRound(int sgn, int pow10, long ival, long val3, long val2, long val1, long val0, DecimalRounding rounding) {
		while (ival == 0) {
			ival = val3;
			val3 = val2;
			val2 = val1;
			val1 = val0;
			val0 = 0;
			pow10 -= 18;
		}
		if (ival >= Scale9f.SCALE_FACTOR) {
			long carry;

			final int log10 = log10(ival);
			final int div10 = log10 - 9;
			final ScaleMetrics divScale = Scales.valueOf(div10);
			final ScaleMetrics mulScale = Scales.valueOf(18 - div10);
			
			final long ivHi = divScale.divideByScaleFactor(ival);
			final long ivLo = ival - divScale.multiplyByScaleFactor(ivHi);
			ival = ivHi;
			carry = mulScale.multiplyByScaleFactor(ivLo);
			
			if (val3 != 0) {
				final long v3Hi = divScale.divideByScaleFactor(val3);
				final long v3Lo = val3 - divScale.multiplyByScaleFactor(v3Hi);
				val3 = v3Hi + carry;
				carry = mulScale.multiplyByScaleFactor(v3Lo);
			} else {
				val3 = carry;
				carry = 0;
			}
			
			if (val2 != 0) {
				final long v2Hi = divScale.divideByScaleFactor(val2);
				final long v2Lo = val2 - divScale.multiplyByScaleFactor(v2Hi);
				val2 = v2Hi + carry;
				carry = mulScale.multiplyByScaleFactor(v2Lo);
			} else {
				val2 = carry;
				carry = 0;
			}

			if (val1 != 0) {
				final long v1Hi = divScale.divideByScaleFactor(val1);
				final long v1Lo = val1 - divScale.multiplyByScaleFactor(v1Hi);
				val1 = v1Hi + carry;
				carry = mulScale.multiplyByScaleFactor(v1Lo);
			} else {
				val1 = carry;
				carry = 0;
			}
			roundToVal2(sgn, pow10 + div10, ival, val3, val2, val1, val0 != 0 | carry != 0, rounding);
		} else {
			roundToVal2(sgn, pow10, ival, val3, val2, val1, val0 != 0, rounding);
		}
	}
	private void roundToVal2(int sgn, int pow10, long ival, long val3, long val2, long val1, boolean nonZeroAfterVal1, DecimalRounding rounding) {
		//(ival|val3|val2) += round(val1|val0|carry) 
		final int inc = getRoundingIncrement(sgn, val2, Scale18f.INSTANCE, val1, nonZeroAfterVal1, rounding);
		if (inc > 0) {
			val2++;
			if (val2 >= Scale18f.SCALE_FACTOR) {
				val2 = 0;//val2 -= Scale18f.SCALE_FACTOR;
				val3++;
				if (val3 >= Scale18f.SCALE_FACTOR) {
					val3 = 0;//val3 -= Scale18f.SCALE_FACTOR;
					ival++;
					if (ival >= Scale9f.SCALE_FACTOR) {
						ival = Scale8f.SCALE_FACTOR;//ival /= 10
						pow10++;
					}
				}
			}
		}
		this.pow10 = pow10;
		this.ival = ival;
		this.val3 = val3;
		this.val2 = val2;
		this.val1 = 0;
		this.val0 = 0;
	}
	public final void multiply(int sgn, UnsignedDecimal9x36f factor, DecimalRounding rounding) {
		normalizeAndRound(sgn, pow10, ival, val3, val2, val1, val0, rounding);
		multiply(sgn, val3, val2, factor, rounding);
	}
	//PRECONDITION: this and factor normalized, i.e. ival < Scale9f.SCALE_FACTOR
	private void multiply(int sgn, long val3, long val2, UnsignedDecimal9x36f factor, DecimalRounding rounding) {
		//split each factor into 9 digit parts
		long rhs4 = ival;
		long rhs3 = val3 / Scale9f.SCALE_FACTOR;
		long rhs2 = val3 - rhs3 * Scale9f.SCALE_FACTOR;
		long rhs1 = val2 / Scale9f.SCALE_FACTOR;
		long rhs0 = val2 - rhs1 * Scale9f.SCALE_FACTOR;
		long lhs4 = factor.ival;
		long lhs3 = factor.val3 / Scale9f.SCALE_FACTOR;
		long lhs2 = factor.val3 - lhs3 * Scale9f.SCALE_FACTOR;
		long lhs1 = factor.val2 / Scale9f.SCALE_FACTOR;
		long lhs0 = factor.val2 - lhs1 * Scale9f.SCALE_FACTOR;
		//4: 00
		//3: 09
		//2: 18
		//1: 27
		//0: 36
		
		//multiply now
		long scale72 = rhs0 * lhs0;
		long scale63 = rhs1 * lhs0 + lhs1 * rhs0;
		long scale54 = rhs2 * lhs0 + lhs2 * rhs0 + rhs1 * lhs1;
		long scale45 = rhs3 * lhs0 + lhs3 * rhs0 + rhs2 * lhs1 + lhs2 * rhs1;
		long scale36 = rhs3 * lhs1 + lhs3 * rhs1 + rhs2 * lhs2 + rhs0 * lhs4 + lhs0 * rhs4;
		long scale27 = rhs3 * lhs2 + lhs3 * rhs2 + rhs1 * lhs4 + lhs1 * rhs4;
		long scale18 = rhs3 * lhs3 + rhs2 * lhs4 + lhs2 * rhs4;
		long scale09 = rhs3 * lhs4 + lhs3 * rhs4;
		long scale00 = rhs4 * lhs4;
		
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
		
		//convert to 18 digit values from 9 digit values
		this.pow10 += factor.pow10;
		this.ival = scale00;
		this.val3 = scale09 * Scale9f.SCALE_FACTOR + scale18;
		this.val2 = scale27 * Scale9f.SCALE_FACTOR + scale36;
		this.val1 = scale45 * Scale9f.SCALE_FACTOR + scale54;
		this.val0 = scale63 * Scale9f.SCALE_FACTOR + scale72;
	}
	
	private static int getRoundingIncrement(int sgn, long truncated, ScaleMetrics scaleMetrics, long remainder, boolean nonZeroAfterRemainder, DecimalRounding rounding) {
		if (rounding != DecimalRounding.DOWN & (remainder != 0 | nonZeroAfterRemainder)) {
			TruncatedPart truncatedPart = RoundingUtil.truncatedPartFor(remainder, scaleMetrics.getScaleFactor());
			if (nonZeroAfterRemainder) {
				if (truncatedPart == TruncatedPart.ZERO) truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
				else if (truncatedPart == TruncatedPart.EQUAL_TO_HALF) truncatedPart = TruncatedPart.GREATER_THAN_HALF;
			}
			return getRoundingIncrement(sgn, truncated, rounding, truncatedPart);
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
	private int getInvNormPow10() {
		final int log10 = log10(ival);
		return (ival >= Scales.valueOf(log10 - 1).getScaleFactor()*3) ? log10 : log10 - 1;//we want to normalize the ival part to be between 1 and 5
	}
	private final long getInvNorm(int sgn, DecimalArithmetics arith, DecimalRounding rounding) {
		final int pow10 = -getInvNormPow10();
		if (pow10 >= 0) {
			return getDecimal(sgn, pow10, ival, val3, val2, val1, val0, 0, 0, 0, 0, arith, rounding);
		}
		return getDecimal(sgn, pow10 + 18, 0, ival, val3, val2, val1, val0, 0, 0, 0, arith, rounding);
	}
	public long getInverted(int sgn, DecimalArithmetics arith, DecimalRounding rounding, DecimalRounding powRounding, UnsignedDecimal9x36f acc) {
		//1) get scale18 value normalized to 0.3 <= x < 3 (i.e. make it invertible without overflow for uninverted and inverted value)
		final DecimalArithmetics arith18 = Scale18f.INSTANCE.getArithmetics(rounding.getRoundingMode());//unchecked is fine, see comments below
		final long divisor = acc.getInvNorm(sgn, arith18, powRounding);
		//2) invert normalized scale18 value 
		final long inverted = arith18.invert(divisor);//can't overflow as for x=abs(divisor): 0.9 <= x < 9 
		//3) apply inverted powers of 10, including powers from normalization and rescaling 
		final int pow10 = acc.getPow10() + acc.getInvNormPow10() + (18 - arith.getScale());
		return arith.multiplyByPowerOf10(inverted, -pow10);//overflow possible
	}
	public final long getDecimal(int sgn, DecimalArithmetics arith, DecimalRounding rounding) {
		if (pow10 >= 0) {
			if (pow10 <= 18) {
				return getDecimal(sgn, pow10, ival, val3, val2, val1, val0, 0, 0, 0, 0, arith, rounding);
			}
			if (arith.getOverflowMode().isChecked()) {
				return checkedMultiplyByPowerOf10AndRound(sgn, arith, rounding);
			}
			return multiplyByPowerOf10AndRound(sgn, arith, rounding);
		} else {
			return divideByPowerOf10AndRound(sgn, arith, rounding);
		}
	}
	private final long multiplyByPowerOf10AndRound(int sgn, DecimalArithmetics arith, DecimalRounding rounding) {
		long iv = ival * Scale18f.SCALE_FACTOR + val3;
		if (pow10 <= 36) {
			return getDecimal(sgn, pow10 - 18, iv, val2, val1, val0, 0, 0, 0, 0, 0, arith, rounding);
		}
		iv *= Scale18f.SCALE_FACTOR + val2;
		if (pow10 <= 54) {
			return getDecimal(sgn, pow10 - 36, iv, val1, val0, 0, 0, 0, 0, 0, 0, arith, rounding);
		}
		iv *= Scale18f.SCALE_FACTOR + val1;
		if (pow10 <= 72) {
			return getDecimal(sgn, pow10 - 54, iv, val0, 0, 0, 0, 0, 0, 0, 0, arith, rounding);
		}
		iv *= Scale18f.SCALE_FACTOR + val0;
		int pow = pow10 - 72;
		while (pow > 18 & iv != 0) {
			iv *= Scale18f.SCALE_FACTOR;
			pow -= 18;
		}
		if (iv != 0) {
			final long absVal = arith.fromLong(iv);
			return sgn >= 0 ? absVal : -absVal;
		}
		return 0;//overflow, everything was shifted out to the left
	}
	private final long checkedMultiplyByPowerOf10AndRound(int sgn, DecimalArithmetics arith, DecimalRounding rounding) {
		final DecimalArithmetics arith18 = Scale18f.INSTANCE.getTruncatingArithmetics(arith.getOverflowMode());
		long iv = arith18.add(arith18.fromLong(ival), val3);//ival * 10^18 + val3
		if (pow10 <= 36) {
			return getDecimal(sgn, pow10 - 18, iv, val2, val1, val0, 0, 0, 0, 0, 0, arith, rounding);
		}
		iv = arith18.add(arith18.fromLong(iv), val2);//iv * 10^18 + val2
		if (pow10 <= 54) {
			return getDecimal(sgn, pow10 - 36, iv, val1, val0, 0, 0, 0, 0, 0, 0, arith, rounding);
		}
		iv = arith18.add(arith18.fromLong(iv), val1);//iv * 10^18 + val1
		if (pow10 <= 72) {
			return getDecimal(sgn, pow10 - 54, iv, val0, 0, 0, 0, 0, 0, 0, 0, arith, rounding);
		}
		iv = arith18.add(arith18.fromLong(iv), val0);//iv * 10^18 + val0
		int pow = pow10 - 72;
		while (pow > 18 & iv != 0) {
			iv = arith18.fromLong(iv);//iv * 10^18
			pow -= 18;
		}
		if (iv != 0) {
			final long absVal = arith.fromLong(iv);
			return sgn >= 0 ? absVal : arith.negate(absVal);
		}
		//should not get here, an overflow exception should have been thrown
		return 0;//overflow, everything was shifted out to the left
	}
	private final long divideByPowerOf10AndRound(int sgn, DecimalArithmetics arith, DecimalRounding rounding) {
		if (pow10 >= -18) {
			return getDecimal(sgn, pow10 + 18, 0, ival, val3, val2, val1, val0, 0, 0, 0, arith, rounding);
		} else if (pow10 >= -36) {
			return getDecimal(sgn, pow10 + 36, 0, 0, ival, val3, val2, val1, val0, 0, 0, arith, rounding);
		} else {
			//only rounding left
			if (rounding != DecimalRounding.DOWN & (ival != 0 | val3 != 0 | val2 != 0 | val1 != 0 | val0 != 0)) { 
				return rounding.calculateRoundingIncrement(sgn, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
			}
			return 0;
		}
	}
	//PRECONDITION: 0 <= pow10 <= 18
	private static long getDecimal(int sgn, int pow10, long ival, long val3, long val2, long val1, long val0, long rem1, long rem2, long rem3, long rem4, DecimalArithmetics arith, DecimalRounding rounding) {
		final OverflowMode overflowMode = arith.getOverflowMode();
		
		//apply pow10 first and convert to intVal and fra18 (with scale 18, w/o rounding)
		final long int18;
		final long fra18;
		final long rem18;
		if (pow10 > 0) {
			final ScaleMetrics mul10Scale = Scales.valueOf(pow10);
			final ScaleMetrics div10Scale = Scales.valueOf(18 - pow10);
			final long hiVal3 = div10Scale.divideByScaleFactor(val3);
			final long loVal3 = val3 - div10Scale.multiplyByScaleFactor(hiVal3);
			final long hiVal2 = div10Scale.divideByScaleFactor(val2);
			final long loVal2 = val2 - div10Scale.multiplyByScaleFactor(hiVal2);
			int18 = add(mulByScaleFactor(mul10Scale, ival, overflowMode), hiVal3, overflowMode);//overflow possible (2x)
			fra18 = mul10Scale.multiplyByScaleFactor(loVal3) + hiVal2;//cannot overflow because it is < 1
			rem18 = loVal2;
		} else {
			int18 = ival;
			fra18 = val3;
			rem18 = val2;
		}

		//apply scale now this time with rounding
		final ScaleMetrics diffMetrics = Scales.valueOf(18 - arith.getScale());
		final long fraVal = diffMetrics.divideByScaleFactor(fra18);
		final long fraRem = fra18 - diffMetrics.multiplyByScaleFactor(fraVal);
		final int inc = getRoundingIncrement(sgn, fraVal, diffMetrics, fraRem, rem18 != 0 | val1 != 0 | val0 != 0 | rem1 != 0 | rem2 != 0 | rem3 != 0 | rem4 != 0, rounding);
		final long fraRnd = fraVal + inc;//cannot overflow because it is <= 1
		final long absVal = add(arith.fromLong(int18), fraRnd, overflowMode);//overflow possible (2x)
		return sgn >= 0 ? absVal : arith.negate(absVal);
	}
	
	private static final long add(long l1, long l2, OverflowMode overflowMode) {
		return overflowMode == OverflowMode.UNCHECKED ? l1 + l2 : Checked.addLong(l1, l2);
	}
	private static final long mulByScaleFactor(ScaleMetrics scaleMetrics, long val, OverflowMode overflowMode) {
		return val == 0 ? 0 : overflowMode == OverflowMode.UNCHECKED ? scaleMetrics.multiplyByScaleFactor(val) : scaleMetrics.multiplyByScaleFactorExact(val);
	}

	private static final long[] LONG_TEN_POWERS_TABLE = {
        1,                     // 0 / 10^0
        10,                    // 1 / 10^1
        100,                   // 2 / 10^2
        1000,                  // 3 / 10^3
        10000,                 // 4 / 10^4
        100000,                // 5 / 10^5
        1000000,               // 6 / 10^6
        10000000,              // 7 / 10^7
        100000000,             // 8 / 10^8
        1000000000,            // 9 / 10^9
        10000000000L,          // 10 / 10^10
        100000000000L,         // 11 / 10^11
        1000000000000L,        // 12 / 10^12
        10000000000000L,       // 13 / 10^13
        100000000000000L,      // 14 / 10^14
        1000000000000000L,     // 15 / 10^15
        10000000000000000L,    // 16 / 10^16
        100000000000000000L,   // 17 / 10^17
        1000000000000000000L   // 18 / 10^18
    };
    /**
     * Returns the length of the absolute value of a {@code long}, in decimal
     * digits.
     *
     * @param absVal the {@code long}
     * @return the length of the unscaled value, in deciaml digits.
     */
    private static int log10(long absVal) {
        /*
         * As described in "Bit Twiddling Hacks" by Sean Anderson,
         * (http://graphics.stanford.edu/~seander/bithacks.html)
         * integer log 10 of x is within 1 of (1233/4096)* (1 +
         * integer log 2 of x). The fraction 1233/4096 approximates
         * log10(2). So we first do a version of log2 (a variant of
         * Long class with pre-checks and opposite directionality) and
         * then scale and check against powers table. This is a little
         * simpler in present context than the version in Hacker's
         * Delight sec 11-4. Adding one to bit length allows comparing
         * downward from the LONG_TEN_POWERS_TABLE that we need
         * anyway.
         */
        if (absVal < 10) // must screen for 0, might as well 10
            return 1;
        final int r = ((64 - Long.numberOfLeadingZeros(absVal) + 1) * 1233) >>> 12;
		final long[] tab = LONG_TEN_POWERS_TABLE;
        // if r >= length, must have max possible digits for long
        return (r >= tab.length || absVal < tab[r]) ? r : r + 1;
    }
	@Override
	public String toString() {
		return toString(pow10, ival, val3, val2);
	}
	private static String toString(final int pow10, final long ival, final long l3, final long l2) {
		int len;
		final StringBuilder sb = new StringBuilder(50);//2*18 + 1 sign + 1 leading digit + some space for pow2 stuff
		sb.append(l3 >= 0 ? ival : -ival);
		sb.append('.');
		len = sb.length();
		sb.append(l3);
		sb.insert(len, "000000000000000000", 0, len + 18 - sb.length());
		len = sb.length(); 
		sb.append(l2);
		sb.insert(len, "000000000000000000", 0, len + 18 - sb.length());
		sb.append("*10^").append(pow10);
		return sb.toString();
	}

}