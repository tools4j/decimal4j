package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.truncate.DecimalRounding;
import ch.javasoft.decimal.truncate.TruncatedPart;

/**
 * Converts from and to double.
 */
class DoubleConversion {
	
	private static final long LONG_MASK = 0xffffffffL;

	// The mask for the significand, according to the {@link
	// Double#doubleToRawLongBits(double)} spec.
	private static final long SIGNIFICAND_MASK = 0x000fffffffffffffL;

	private static final int SIGNIFICAND_BITS = 52;

	/**
	 * The implicit 1 bit that is omitted in significands of normal doubles.
	 */
	static final long IMPLICIT_BIT = SIGNIFICAND_MASK + 1;

	private static final double MIN_LONG_AS_DOUBLE = -0x1p63;
	/*
	 * We cannot store Long.MAX_VALUE as a double without losing precision.
	 * Instead, we store Long.MAX_VALUE + 1 == -Long.MIN_VALUE, and then offset
	 * all comparisons by 1.
	 */
	private static final double MAX_LONG_AS_DOUBLE_PLUS_ONE = 0x1p63;

	public static long doubleToLong(double value) {
		if (Double.isNaN(value)) {
			throw new NumberFormatException("Cannot convert double to decimal: " + value);
		}
		if (isInLongRange(value)) {
			return (long) value;
		}
		throw new NumberFormatException("Overflow for conversion from double to decimal: " + value);
	}

	public static long doubleToLong(double value, RoundingMode rounding) {
		if (Double.isNaN(value)) {
			throw new NumberFormatException("Cannot convert double to decimal: " + value);
		}
		if (isInLongRange(value)) {
			return (long)roundIntermediate(value, rounding);
		}
		throw new NumberFormatException("Overflow for conversion from double to decimal: " + value);
	}

	/*
	 * Copied from guava.
	 * This method returns a value y such that rounding y DOWN (towards zero)
	 * gives the same result as rounding x according to the specified mode.
	 * PRECONDITION: isFinite(x)
	 */
	private static double roundIntermediate(double x, RoundingMode mode) {
		switch (mode) {
		case UNNECESSARY:
			if (!isMathematicalInteger(x)) {
				throw new ArithmeticException("Rounding necessary to convert to an integer value: " + x);
			}
			return x;

		case FLOOR:
			if (x >= 0.0 || isMathematicalInteger(x)) {
				return x;
			} else {
				return x - 1.0;
			}

		case CEILING:
			if (x <= 0.0 || isMathematicalInteger(x)) {
				return x;
			} else {
				return x + 1.0;
			}

		case DOWN:
			return x;

		case UP:
			if (isMathematicalInteger(x)) {
				return x;
			} else {
				return x + Math.copySign(1.0, x);
			}

		case HALF_EVEN:
			return Math.rint(x);

		case HALF_UP: {
			double z = Math.rint(x);
			if (Math.abs(x - z) == 0.5) {
				return x + Math.copySign(0.5, x);
			} else {
				return z;
			}
		}

		case HALF_DOWN: {
			double z = Math.rint(x);
			if (Math.abs(x - z) == 0.5) {
				return x;
			} else {
				return z;
			}
		}
		default:
			throw new IllegalArgumentException("Unsupported rounding mode: " + mode);
		}
	}
	
	public static final long doubleToUnscaled(DecimalArithmetics arith, double value) {
		return doubleToUnscaled(arith, value, DecimalRounding.DOWN);
	}
	public static final long doubleToUnscaled(DecimalArithmetics arith, double value, DecimalRounding rounding) {
		if (value == 0) {
			return 0;
		}
		final int exp = Math.getExponent(value);
		if (exp >= Long.SIZE) {
			throw new NumberFormatException("Overflow for conversion from double to decimal: " + value);
		}
		
		//multiply significand by scale factor into a 128bit integer
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final long significand = getSignificand(value);
		//HD + Knuth's Algorithm M from [Knu2] section 4.3.1.
		final int lFactor = (int) (significand & LONG_MASK);
		final int hFactor = (int) (significand >>> 32);
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

		//now multiply or divide by powers of two as instructed by the double exponent
		final int shift = exp - SIGNIFICAND_BITS;
		if (shift > 0) {
			//multiply: shift right
			if (hScaled != 0) {
				throw new NumberFormatException("Overflow for conversion from double to decimal: " + value);
			}
			final int zeros = Long.numberOfLeadingZeros(lScaled);
			if (shift >= zeros) {
				throw new NumberFormatException("Overflow for conversion from double to decimal: " + value);
			}
			final long absResult = lScaled << shift;
			return value >= 0 ? absResult : -absResult;
		} else if (shift < 0) {
			final int right = -shift;
			final long absResult;
			final TruncatedPart truncatedPart;
			if (right < Long.SIZE) {
				if ((hScaled >>> right) != 0) {
					throw new NumberFormatException("Overflow for conversion from double to decimal: " + value);
				}
				absResult = (hScaled << (Long.SIZE - right)) | (lScaled >>> right);
				final long rem = modPow2(lScaled, right);
				truncatedPart = RoundingUtil.truncatedPartFor2powN(rem, right);
			} else if (right < 2*Long.SIZE) {
				absResult = (hScaled >>> (right - Long.SIZE));
				final long rem = modPow2(hScaled, right - Long.SIZE);
				truncatedPart = RoundingUtil.truncatedPartFor2powN(rem, lScaled, right);
			} else {
				absResult = 0;//rounded down
				truncatedPart = RoundingUtil.truncatedPartFor2powN(hScaled, lScaled, right);
			}
			final int inc = rounding.calculateRoundingIncrement(value >= 0 ? 1 : -1, absResult, truncatedPart);
			if (absResult < 0 | (absResult == Long.MAX_VALUE & inc == 1)) {
				throw new NumberFormatException("Overflow for conversion from double to decimal: " + value);
			}
			return (value >= 0 ? absResult : -absResult) + inc;
		} else {
			//shift == 0
			if (hScaled != 0 || lScaled < 0) {
				throw new NumberFormatException("Overflow for conversion from double to decimal: " + value);
			}
			return value >= 0 ? lScaled : -lScaled;
		}
	}
	
	//@return value % (2^n)
	private static final long modPow2(long value, int n) {
//		return value & ((1L << n) - 1);
		return n == 0 ? 0 : value & (-1L >>> (Long.SIZE-n));
	}

	private static boolean isInLongRange(double value) {
		return MIN_LONG_AS_DOUBLE - value < 1.0 & value < MAX_LONG_AS_DOUBLE_PLUS_ONE;
	}

	private static boolean isMathematicalInteger(double x) {
		return isFinite(x) && (x == 0.0 || SIGNIFICAND_BITS - Long.numberOfTrailingZeros(getSignificand(x)) <= Math.getExponent(x));
	}

	private static boolean isFinite(double d) {
		return Math.getExponent(d) <= Double.MAX_EXPONENT;
	}

	//PRECONDITION: isFinite(d)
	private static long getSignificand(double d) {
		int exponent = Math.getExponent(d);
		long bits = Double.doubleToRawLongBits(d);
		bits &= SIGNIFICAND_MASK;
		return (exponent == Double.MIN_EXPONENT - 1) ? bits << 1 : bits | IMPLICIT_BIT;
	}

	//no instances
	private DoubleConversion() {
		super();
	}
}
