package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.javasoft.decimal.scale.Scale18f;
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
	
	private static long powLongCheckedOrUnchecked(DecimalArithmetics arith, DecimalRounding rounding, long longBase, int exponent) {
		return arith.getOverflowMode() == OverflowMode.UNCHECKED ? powLong(arith, rounding, longBase, exponent) : powLongChecked(arith, rounding, longBase, exponent);
	}

	public static long pow(DecimalArithmetics arith, DecimalRounding rounding, long uDecimalBase, int exponent) {
		if (exponent < -999999999 || exponent > 999999999) {
			throw new ArithmeticException("Exponent must be in [-999999999,999999999] but was: " + exponent);
		}
		final SpecialPowResult special = SpecialPowResult.getFor(arith, uDecimalBase, exponent);
		if (special != null) {
			return special.pow(arith, uDecimalBase, exponent);
		}

		//some other special cases
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();

		//integer?
		if (scaleMetrics.moduloByScaleFactor(uDecimalBase) == 0) {
			//integer
			final long intval = scaleMetrics.divideByScaleFactor(uDecimalBase);
			final long result = powLongCheckedOrUnchecked(arith, rounding, intval, exponent);
			return arith.fromLong(result);
		}

		return powWithPrecision18(arith, rounding, uDecimalBase, exponent);
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
	//PRECONDITION: n != 0 and n in [-999999999,999999999]
	private static long powWithPrecision18(DecimalArithmetics arith, DecimalRounding rounding, long uDecimalBase, int n) {
		//eliminate sign
		final int sgn = ((n & 0x1) != 0) ? Long.signum(uDecimalBase) : 1;
		final long absBase = Math.abs(uDecimalBase);
		final DecimalRounding powRounding = n >= 0 ? rounding : getOppositeRoundingMode(rounding);
		
		//36 digit left hand side, initialized with absBase
		final UnsignedDecimal36f lhs = new UnsignedDecimal36f(absBase, arith.getScaleMetrics());
		
		//36 digit accumulator, initialized with one without leading 1 digit
		final UnsignedDecimal36f acc = UnsignedDecimal36f.one();
		
		// ready to carry out power calculation...
		int mag = Math.abs(n);
        boolean seenbit = false;        // avoid squaring ONE
        for (int i=1;;i++) {            // for each bit [top bit ignored]
            mag += mag;                 // shift left 1 bit
            if (mag < 0) {              // top bit is set
            	seenbit = true;
                acc.multiply(sgn, lhs, powRounding); // acc=acc*x
            }
            if (i == 31)
                break;                  // that was the last bit
            if (seenbit)
            	acc.multiply(sgn, acc, powRounding);   // acc=acc*acc [square]
                // else (!seenbit) no point in squaring ONE
        }
        
        if (n < 0) {
//    		return invert(arith, rounding, powRounding, acc);
			return acc.getInverted(sgn, arith, rounding);
        }
        return acc.getDecimal(sgn, arith, rounding);
	}

	private static long invert(int sgn, DecimalArithmetics arith, DecimalRounding rounding, final DecimalRounding powRounding, final UnsignedDecimal36f acc) {
		//we apply pow2 after division
		final DecimalArithmetics arith18 = Scale18f.INSTANCE.getArithmetics(rounding.getRoundingMode());//unchecked is fine, see comments below 
		if (acc.getPow2() <= 0) {
			//rescale first, then multiply by pow2
			final int scale = arith.getScale();
			if (scale < 18) {
				final ScaleMetrics diffMetrics = Scales.valueOf(18 - scale);
				final UnsignedDecimal36f scaleFactor = new UnsignedDecimal36f(diffMetrics.getScaleFactor());
				acc.multiply(sgn, scaleFactor, powRounding);
			}
			final long divisor = acc.getRaw(sgn, arith, powRounding);
			final long inverted = arith18.invert(divisor);//can't overflow as divisor is in [-2, 2]
			return arith.shiftLeft(inverted, -acc.getPow2());//overflow possible
		}
		//divide by pow2 first, then rescale
		final long divisor = acc.getRaw(sgn, arith, powRounding);
		final long inverted = arith18.invert(divisor);//can't overflow as divisor is in [-2, 2]
		final long shifted = arith18.shiftRight(inverted, acc.getPow2());//no overflow as this is a division
		return arith.fromUnscaled(shifted, 18);
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

	private static DecimalRounding getOppositeRoundingMode(DecimalRounding roundingMode) {
		switch (roundingMode) {
		case UP:
			return DecimalRounding.DOWN;
		case DOWN:
			return DecimalRounding.UP;
		case CEILING:
			return DecimalRounding.FLOOR;
		case FLOOR:
			return DecimalRounding.CEILING;
		case HALF_UP:
			return DecimalRounding.HALF_DOWN;
		case HALF_DOWN:
			return DecimalRounding.HALF_UP;
		case HALF_EVEN:
			return DecimalRounding.HALF_EVEN;//HALF_UNEVEN?
		case UNNECESSARY:
			return DecimalRounding.UNNECESSARY;
		default:
			throw new IllegalArgumentException("unsupported rounding mode: " + roundingMode);
		}
	}

	//no instances
	private Pow() {}

}
