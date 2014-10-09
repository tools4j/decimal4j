package ch.javasoft.decimal.arithmetic;

import ch.javasoft.decimal.scale.ScaleMetrics;

class Sqrt {
	/**
	 * This mask is used to obtain the value of an int as if it were unsigned.
	 */
	private static final long LONG_MASK = 0xffffffffL;

	public static long sqrtLong(long lValue) {
		if (lValue < 0) {
			throw new ArithmeticException("square root of a negative value: " + lValue);
		}
		//http://www.codecodex.com/wiki/Calculate_an_integer_square_root
		if ((lValue & 0xfff0000000000000L) == 0) {
			return (long) StrictMath.sqrt(lValue);
		}
		final long result = (long) StrictMath.sqrt(2.0d * (lValue >>> 1));
		return result * result - lValue > 0L ? result - 1 : result;
	}

	public static long sqrtLong(DecimalRounding rounding, long lValue) {
		if (lValue < 0) {
			throw new ArithmeticException("square root of a negative value: " + lValue);
		}
		//square root
		//@see http://www.embedded.com/electronics-blogs/programmer-s-toolbox/4219659/Integer-Square-Roots
		long rem = 0;
		long root = 0;
		final int zerosHalf = Long.numberOfLeadingZeros(lValue) >> 1;
		long scaled = lValue << (zerosHalf << 1);
		for (int i = zerosHalf; i < 32; i++) {
			root <<= 1;
			rem = ((rem << 2) + (scaled >>> 62));
			scaled <<= 2;
			root++;
			if (root <= rem) {
				rem -= root;
				root++;
			} else {
				root--;
			}
		}
		final long truncated = root >>> 1;
		if (rounding == null | rem == 0 | rounding == DecimalRounding.DOWN | rounding == DecimalRounding.FLOOR) {
			return truncated;
		}
		return truncated + getRoundingIncrement(rounding, truncated, rem);
	}

	public static long sqrt(DecimalArithmetics arith, long uDecimal) {
		return sqrt(arith, null, uDecimal);
	}

	public static long sqrt(DecimalArithmetics arith, DecimalRounding rounding, long uDecimal) {
		if (uDecimal < 0) {
			throw new ArithmeticException("square root of a negative value: " + arith.toString(uDecimal));
		}
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();

		//multiply by scale factor into a 128bit integer
		final int lFactor = (int) (uDecimal & LONG_MASK);
		final int hFactor = (int) (uDecimal >>> 32);
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

		//square root
		//@see http://www.embedded.com/electronics-blogs/programmer-s-toolbox/4219659/Integer-Square-Roots
		int zerosHalf;
		long rem = 0;
		long root = 0;
		zerosHalf = Long.numberOfLeadingZeros(hScaled) >> 1;
		hScaled <<= (zerosHalf << 1);
		for (int i = zerosHalf; i < 32; i++) {
			root <<= 1;
			rem = ((rem << 2) + (hScaled >>> 62));
			hScaled <<= 2;
			root++;
			if (root <= rem) {
				rem -= root;
				root++;
			} else {
				root--;
			}
		}
		zerosHalf = zerosHalf == 32 ? Long.numberOfLeadingZeros(lScaled) >> 1 : 0;
		lScaled <<= (zerosHalf << 1);
		for (int i = zerosHalf; i < 32; i++) {
			root <<= 1;
			rem = ((rem << 2) + (lScaled >>> 62));
			lScaled <<= 2;
			root++;
			if (root <= rem) {
				rem -= root;
				root++;
			} else {
				root--;
			}
		}

		final long truncated = root >>> 1;
		if (rounding == null | rem == 0 | rounding == DecimalRounding.DOWN | rounding == DecimalRounding.FLOOR) {
			return truncated;
		}
		return truncated + getRoundingIncrement(rounding, truncated, rem);
	}

	//PRECONDITION: rem != 0
	//NOTE: TruncatedPart cannot be 0.5 because this would square to 0.25
	private static int getRoundingIncrement(DecimalRounding rounding, long truncated, long rem) {
		if (truncated < rem) {
			return rounding.calculateRoundingIncrement(1, truncated, TruncatedPart.GREATER_THAN_HALF);
		}
		return rounding.calculateRoundingIncrement(1, truncated, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
	}
}
