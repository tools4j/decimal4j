/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 decimal4j (tools4j), Marco Terzer
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

import java.math.RoundingMode;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.scale.Scale8f;
import org.decimal4j.scale.Scale9f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncatedPart;

/**
 * Helper class for an unsigned decimal value with 9 integral digits and 38 decimal
 * fraction digits used internally by {@link Pow} to calculate decimal powers.
 */
final class UnsignedDecimal9i36f {
	/** Thread local for factor 1*/
	static final ThreadLocal<UnsignedDecimal9i36f> THREAD_LOCAL_1 = new ThreadLocal<UnsignedDecimal9i36f>() {
		@Override
		protected UnsignedDecimal9i36f initialValue() {
			return new UnsignedDecimal9i36f();
		}
	};
	/** Thread local for accumulator*/
	static final ThreadLocal<UnsignedDecimal9i36f> THREAD_LOCAL_2 = new ThreadLocal<UnsignedDecimal9i36f>() {
		@Override
		protected UnsignedDecimal9i36f initialValue() {
			return new UnsignedDecimal9i36f();
		}
	};
	
	/**
	 * Normalization mode.
	 */
	private static enum Norm {
		/** Not normalized: ival and valX can be any positive longs */
		UNNORMALIZED,
		/** 18 digit normalization (standard): ival is 9 digits; val3/val2 are 18 digits, val1/val0 are zero*/
		NORMALIZED_18,
		/** 9 digit normalization (for multiplication): ival and valX are 9 digits values*/
		NORMALIZED_09
	}
	private Norm norm;
	private int pow10;
	private long ival;
	private long val3;
	private long val2;
	private long val1;
	private long val0;
	
	/** Constructor */
	private UnsignedDecimal9i36f() {
		super();
	}
	
	/**
	 * Assigns the value one to this unsigned 9x36 decimal and returns it.
	 * 
	 * @return this
	 */
	public final UnsignedDecimal9i36f initOne() {
		this.norm = Norm.NORMALIZED_18;
		this.pow10 = 0;
		this.ival = 1;
		this.val3 = 0;
		this.val2 = 0;
		this.val1 = 0;
		this.val0 = 0;
		return this;
	}

	/**
	 * Assigns the value one to this unsigned 9x36 decimal and returns it.
	 * 
	 * @param copy
	 *            the value to copy
	 * @return this
	 */
	public final UnsignedDecimal9i36f init(UnsignedDecimal9i36f copy) {
		this.norm = copy.norm;
		this.pow10 = copy.pow10;
		this.ival = copy.ival;
		this.val3 = copy.val3;
		this.val2 = copy.val2;
		this.val1 = copy.val1;
		this.val0 = copy.val0;
		return this;
	}

	/**
	 * Assigns the given integer and fraction component to this unsigned 9x36
	 * decimal and returns it.
	 * 
	 * @param ival
	 *            the integer part of the value to assign
	 * @param fval
	 *            the fractional part of the value to assign
	 * @param scaleMetrics
	 *            the scale metrics associated with the value
	 * @return this
	 */
	public final UnsignedDecimal9i36f init(long ival, long fval, ScaleMetrics scaleMetrics) {
		final ScaleMetrics diffMetrics = Scales.getScaleMetrics(18 - scaleMetrics.getScale());
		normalizeAndRound(1, 0, ival, diffMetrics.multiplyByScaleFactor(fval), 0, 0, 0, DecimalRounding.UNNECESSARY);
		return this;
	}
	
	/**
	 * Returns the current power-ten exponent.
	 * 
	 * @return the base-10 exponent of this value
	 */
	public final int getPow10() {
		return pow10;
	}
	private final void normalizeAndRound(int sgn, int pow10, long ival, long val3, long val2, long val1, long val0, DecimalRounding rounding) {
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
			final ScaleMetrics divScale = Scales.getScaleMetrics(div10);
			final ScaleMetrics mulScale = Scales.getScaleMetrics(18 - div10);
			
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
	private final void roundToVal2(int sgn, int pow10, long ival, long val3, long val2, long val1, boolean nonZeroAfterVal1, DecimalRounding rounding) {
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
		this.norm = Norm.NORMALIZED_18;
		this.pow10 = pow10;
		this.ival = ival;
		this.val3 = val3;
		this.val2 = val2;
		this.val1 = 0;
		this.val0 = 0;
	}
	private final void normalize09() {
		final long val3 = this.val3;
		final long val2 = this.val2;
		final long v3 = val3 / Scale9f.SCALE_FACTOR;
		final long v2 = val3 - v3 * Scale9f.SCALE_FACTOR;
		final long v1 = val2 / Scale9f.SCALE_FACTOR;
		final long v0 = val2 - v1 * Scale9f.SCALE_FACTOR;
		this.norm = Norm.NORMALIZED_09;
		this.val3 = v3;
		this.val2 = v2;
		this.val1 = v1;
		this.val0 = v0;
	}
	
	/**
	 * Multiplies this unsigned 9x36 decimal value with another one.
	 * 
	 * @param sgn
	 *            the sign of the final result
	 * @param factor
	 *            the factor to be multiplied with
	 * @param rounding
	 *            the rounding to apply
	 */
	public final void multiply(int sgn, UnsignedDecimal9i36f factor, DecimalRounding rounding) {
		if (norm != Norm.NORMALIZED_18) {
			normalizeAndRound(sgn, pow10, ival, val3, val2, val1, val0, rounding);
		}
		multiply(sgn, val3, val2, factor, rounding);
	}
	//PRECONDITION: this and factor normalized, i.e. ival < Scale9f.SCALE_FACTOR
	private final void multiply(int sgn, long val3, long val2, UnsignedDecimal9i36f factor, DecimalRounding rounding) {
		//split each factor into 9 digit parts
		if (this.norm != Norm.NORMALIZED_09) {
			this.normalize09();
		}
		final long lhs4 = this.ival;
		final long lhs3 = this.val3;
		final long lhs2 = this.val2;
		final long lhs1 = this.val1;
		final long lhs0 = this.val0;
		if (factor.norm != Norm.NORMALIZED_09) {
			factor.normalize09();
		}
		final long rhs4 = factor.ival;
		final long rhs3 = factor.val3;
		final long rhs2 = factor.val2;
		final long rhs1 = factor.val1;
		final long rhs0 = factor.val0;
		
		//multiply now
		long scale72 = lhs0 * rhs0;
		long scale63 = lhs1 * rhs0 + rhs1 * lhs0;
		long scale54 = lhs2 * rhs0 + rhs2 * lhs0 + lhs1 * rhs1;
		long scale45 = lhs3 * rhs0 + rhs3 * lhs0 + lhs2 * rhs1 + rhs2 * lhs1;
		long scale36 = lhs3 * rhs1 + rhs3 * lhs1 + lhs2 * rhs2 + lhs0 * rhs4 + rhs0 * lhs4;
		long scale27 = lhs3 * rhs2 + rhs3 * lhs2 + lhs1 * rhs4 + rhs1 * lhs4;
		long scale18 = lhs3 * rhs3 + lhs2 * rhs4 + rhs2 * lhs4;
		long scale09 = lhs3 * rhs4 + rhs3 * lhs4;
		long scale00 = lhs4 * rhs4;
		
		//reduce 8 to 4 parts and propagate carries
		long carry;

		//NOTE: largest value is val36: sum of 5 products + sum of 4 products 
		//      -- each product consists of 2 factors < Scale9f.SCALE_FACTOR
		//		-- hence each product < Scale18f.SCALE_FACTOR
		//		-- sum of 9 products each < Scale18f.SCALE_FACTOR 
		//		=> sum < 9 * Scale18f.SCALE_FACTOR < Long.MAX_VALUE
		//		=> no overflows
		
		carry = scale63 / Scale9f.SCALE_FACTOR;
		scale63 -= carry * Scale9f.SCALE_FACTOR;
		long val72 = scale63 * Scale9f.SCALE_FACTOR + scale72;
		while (val72 >= Scale18f.SCALE_FACTOR) {
			val72 -= Scale18f.SCALE_FACTOR;
			carry++;
		}
		scale54 += carry;

		carry = scale45 / Scale9f.SCALE_FACTOR;
		scale45 -= carry * Scale9f.SCALE_FACTOR;
		long val54 = scale45 * Scale9f.SCALE_FACTOR + scale54;
		while (val54 >= Scale18f.SCALE_FACTOR) {
			val54 -= Scale18f.SCALE_FACTOR;
			carry++;
		}
		scale36 += carry;

		carry = scale27 / Scale9f.SCALE_FACTOR;
		scale27 -= carry * Scale9f.SCALE_FACTOR;
		long val36 = scale27 * Scale9f.SCALE_FACTOR + scale36;
		while (val36 >= Scale18f.SCALE_FACTOR) {
			val36 -= Scale18f.SCALE_FACTOR;
			carry++;
		}
		scale18 += carry;
		
		carry = scale09 / Scale9f.SCALE_FACTOR;
		scale09 -= carry * Scale9f.SCALE_FACTOR;
		long val18 = scale09 * Scale9f.SCALE_FACTOR + scale18;
		while (val18 >= Scale18f.SCALE_FACTOR) {
			val18 -= Scale18f.SCALE_FACTOR;
			carry++;
		}
		scale00 += carry;

		//assign values
		this.norm = Norm.UNNORMALIZED;
		this.pow10 += factor.pow10;
		this.ival = scale00;
		this.val3 = val18;
		this.val2 = val36;
		this.val1 = val54;
		this.val0 = val72;
	}
	
	private static final int getRoundingIncrement(int sgn, long truncated, ScaleMetrics scaleMetrics, long remainder, boolean nonZeroAfterRemainder, DecimalRounding rounding) {
		if (rounding != DecimalRounding.DOWN & (remainder != 0 | nonZeroAfterRemainder)) {
			TruncatedPart truncatedPart = Rounding.truncatedPartFor(remainder, scaleMetrics.getScaleFactor());
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
	private final int getInvNormPow10() {
		final int log10 = log10(ival);
		return (ival >= Scales.getScaleMetrics(log10 - 1).getScaleFactor()*3) ? log10 : log10 - 1;//we want to normalize the ival part to be between 1 and 5
	}
	private final long getInvNorm(int sgn, DecimalArithmetic arith, DecimalRounding rounding) {
		final int pow10 = -getInvNormPow10();
		if (pow10 >= 0) {
			return getDecimal(sgn, pow10, ival, val3, val2, val1, val0, 0, 0, 0, 0, arith, rounding);
		}
		return getDecimal(sgn, pow10 + 18, 0, ival, val3, val2, val1, val0, 0, 0, 0, arith, rounding);
	}
	
	/**
	 * Returns the inverted result resulting from exponentiation with a negative
	 * exponent. The result is best-effort accurate.
	 * 
	 * @param sgn
	 *            the sign of the final result
	 * @param arith
	 *            the arithmetic of the base value
	 * @param rounding
	 *            the rounding to apply
	 * @param powRounding
	 *            reciprocal rounding if exponent is negative and rounding
	 *            otherwise
	 * @return <code>round(1 / this)</code>
	 */
	public final long getInverted(int sgn, DecimalArithmetic arith, DecimalRounding rounding, DecimalRounding powRounding) {
		//1) get scale18 value normalized to 0.3 <= x < 3 (i.e. make it invertible without overflow for uninverted and inverted value)
		final DecimalArithmetic arith18 = Scale18f.INSTANCE.getArithmetic(rounding.getRoundingMode());//unchecked is fine, see comments below
		final long divisor = this.getInvNorm(sgn, arith18, powRounding);
		//2) invert normalized scale18 value 
		final long inverted = arith18.invert(divisor);//can't overflow as for x=abs(divisor): 0.9 <= x < 9 
		//3) apply inverted powers of 10, including powers from normalization and rescaling 
		final int pow10 = this.getPow10() + this.getInvNormPow10() + (18 - arith.getScale());
		return arith.multiplyByPowerOf10(inverted, -pow10);//overflow possible
	}

	/**
	 * Returns the unscaled Decimal result resulting from exponentiation with a non-negative
	 * exponent. The result is accurate up to 1 ULP of the Decimal.
	 * 
	 * @param sgn
	 *            the sign of the final result
	 * @param arith
	 *            the arithmetic of the base value
	 * @param rounding
	 *            the rounding to apply
	 * @return <code>round(this)</code>
	 */
	public final long getDecimal(int sgn, DecimalArithmetic arith, DecimalRounding rounding) {
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
	private final long multiplyByPowerOf10AndRound(int sgn, DecimalArithmetic arith, DecimalRounding rounding) {
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
	private final long checkedMultiplyByPowerOf10AndRound(int sgn, DecimalArithmetic arith, DecimalRounding rounding) {
		final DecimalArithmetic arith18 = Scale18f.INSTANCE.getCheckedArithmetic(RoundingMode.DOWN);
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
	private final long divideByPowerOf10AndRound(int sgn, DecimalArithmetic arith, DecimalRounding rounding) {
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
	private static final long getDecimal(int sgn, int pow10, long ival, long val3, long val2, long val1, long val0, long rem1, long rem2, long rem3, long rem4, DecimalArithmetic arith, DecimalRounding rounding) {
		final OverflowMode overflowMode = arith.getOverflowMode();
		
		//apply pow10 first and convert to intVal and fra18 (with scale 18, w/o rounding)
		final long int18;
		final long fra18;
		final long rem18;
		if (pow10 > 0) {
			final ScaleMetrics mul10Scale = Scales.getScaleMetrics(pow10);
			final ScaleMetrics div10Scale = Scales.getScaleMetrics(18 - pow10);
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
		final ScaleMetrics diffMetrics = Scales.getScaleMetrics(18 - arith.getScale());
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
    private static final int log10(long absVal) {
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
	public final String toString() {
		int len;
		final StringBuilder sb = new StringBuilder(64);//9-18 integral digits + 1 decimal point + 2*18 fractional digits + some extra for pow10 etc
		sb.append(ival);
		sb.append('.');
		len = sb.length();
		sb.append(val3);
		sb.insert(len, "000000000000000000", 0, len + 18 - sb.length());
		len = sb.length(); 
		sb.append(val2);
		sb.insert(len, "000000000000000000", 0, len + 18 - sb.length());
		if (val1 != 0 | val0 != 0) {
			sb.append("..");
		}
		sb.append("*10^").append(pow10);
		return sb.toString();
	}

}