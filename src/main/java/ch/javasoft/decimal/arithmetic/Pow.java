package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;

import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.truncate.DecimalRounding;
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
		assert(exponent > 0);
		
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
		assert(exponent > 0);
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
	private Pow() {
		super();
	}
}
