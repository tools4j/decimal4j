package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeSet;

import ch.javasoft.decimal.arithmetic.JDKSupport;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.test.TestSettings;

/**
 * Utility class with static helper methods for doubles used in tests.
 */
class Doubles {

	// The mask for the significand, according to the {@link
	// Double#doubleToRawLongBits(double)} spec.
	private static final long SIGNIFICAND_MASK = 0x000fffffffffffffL;

	private static final int SIGNIFICAND_BITS = 52;

	/**
	 * The implicit 1 bit that is omitted in significands of normal doubles.
	 */
	private static final long IMPLICIT_BIT = SIGNIFICAND_MASK + 1;

	private static final double[] DOUBLE_SPECIALS = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.MIN_VALUE, -Double.MIN_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, Double.MIN_NORMAL, -Double.MIN_NORMAL};

	public static double randomDoubleOperand(Random rnd) {
		switch (rnd.nextInt(3)) {
		case 0:
			return rnd.nextDouble();
		case 1:
			return rnd.nextGaussian();
		default:// 2:
			return Double.longBitsToDouble(rnd.nextLong());
		}
	}

	public static double[] specialDoubleOperands(ScaleMetrics scaleMetrics) {
		final NavigableSet<Double> set = new TreeSet<Double>();
		for (final double d : DOUBLE_SPECIALS) {
			set.add(d);
		}
		for (final long l : TestSettings.TEST_CASES.getSpecialValuesFor(scaleMetrics)) {
			final double dbl = (double) l;
			set.add(dbl);
			set.add(dbl + Math.ulp(dbl));
			set.add(dbl - Math.ulp(dbl));
		}
		final double[] vals = new double[set.size()];
		final Iterator<Double> it = set.iterator();
		for (int i = 0; i < vals.length; i++) {
			vals[i] = it.next();
		}
		return vals;
	}

	//PRECONDITION: isFinite(d)
	public static long getSignificand(double d) {
		int exponent = Math.getExponent(d);
		long bits = Double.doubleToRawLongBits(d);
		bits &= SIGNIFICAND_MASK;
		return (exponent == Double.MIN_EXPONENT - 1) ? bits << 1 : bits | IMPLICIT_BIT;
	}

	/**
	 * Similar to {@link BigDecimal#valueOf(double)} but exact e.g. for -1pe63
	 * which is rounded by the BigDecimal standard conversion. Scale and rounding
	 * mode are only used to check that the double fits in a 64 bit decimal.
	 * 
	 * @param a the double to convert
	 * @param scale the scale for the result (which is returned with higher scale for debugging in case of error --- tests convert to correct scale later)
	 * @param roundingMode  the rounding mode
	 * @return a big decimal representing exactly b
	 */
	public static BigDecimal doubleToBigDecimal(double a, int scale, RoundingMode roundingMode) {
		final int exp = Math.getExponent(a);
		if (exp >= Long.SIZE) {
			throw new NumberFormatException("Overflow for conversion from double to long: " + a);
		}
		if (exp < Double.MIN_EXPONENT) {
			return BigDecimal.valueOf(a);
		}
		final long significand = Doubles.getSignificand(a);
		final BigDecimal scaledBigDecimal = BigDecimal.valueOf(a < 0 ? -significand : significand); 
		final int shift = exp - Doubles.SIGNIFICAND_BITS;
		final BigDecimal converted;
		if (shift >= 0) {
			converted = scaledBigDecimal.multiply(new BigDecimal(BigInteger.valueOf(2).pow(shift)));
		} else {
			converted = scaledBigDecimal.divide(new BigDecimal(BigInteger.valueOf(2).pow(-shift)), -shift, RoundingMode.UNNECESSARY);
		}
		try {
			final BigDecimal rounded = converted.setScale(scale, roundingMode);
			//check that the conversion does not overflow
			JDKSupport.bigIntegerToLongValueExact(rounded.unscaledValue());
			return converted;
		} catch (ArithmeticException e) {
			throw new ArithmeticException(e.toString() + ": " + converted);
		}
	}
	
	
	public static RoundingMode getOppositeRoundingMode(RoundingMode roundingMode) {
		switch (roundingMode) {
		case UP:
			return RoundingMode.DOWN;
		case DOWN:
			return RoundingMode.UP;
		case CEILING:
			return RoundingMode.FLOOR;
		case FLOOR:
			return RoundingMode.CEILING;
		case HALF_UP:
			return RoundingMode.HALF_DOWN;
		case HALF_DOWN:
			return RoundingMode.HALF_UP;
		case HALF_EVEN:
			return RoundingMode.HALF_EVEN;//HALF_UNEVEN?
		case UNNECESSARY:
			return RoundingMode.UNNECESSARY;
		default:
			throw new IllegalArgumentException("unsupported rounding mode: " + roundingMode);
		}
	}


	//no instances
	private Doubles() {
		super();
	}
}
