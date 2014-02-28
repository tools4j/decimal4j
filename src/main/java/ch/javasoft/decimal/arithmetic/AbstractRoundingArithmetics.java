package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;

/**
 * Base class for arithmetic implementations which involve rounding strategies.
 * The result of an operation that leads to an overflow is silently truncated.
 */
abstract public class AbstractRoundingArithmetics extends
		TruncatingArithmetics {

	private final RoundingMode roundingMode;

	/**
	 * Constructor for silent decimal arithmetics with given scale,
	 * {@link RoundingMode#HALF_EVEN HALF_EVEN} rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative or zero
	 */
	public AbstractRoundingArithmetics(int scale, RoundingMode roundingMode) {
		super(scale);
		this.roundingMode = roundingMode;
	}

	@Override
	public final RoundingMode getRoundingMode() {
		return roundingMode;
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		final long one = one();
		final long sqrtOne = this.sqrtOne;
		final long i1 = uDecimal1 / sqrtOne;
		final long i2 = uDecimal2 / sqrtOne;
		final long f1 = uDecimal1 % sqrtOne;
		final long f2 = uDecimal2 % sqrtOne;
		final long rest = i1 * f2 * sqrtOne + i2 * f1 * sqrtOne + f1 * f2;
		final long unrounded = i1 * i2 + rest / one;
		return unrounded + calculateRoundingIncrement(unrounded, rest % one);
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		final long unrounded = super.divide(uDecimalDividend, uDecimalDivisor);
		final long product = super.multiply(unrounded, uDecimalDivisor);
		final long delta = uDecimalDividend - product;
		if (delta != 0) {
			final long one = one();
			final long remainder = super.divide((delta % one) * one, uDecimalDivisor);
			if (unrounded != 0) {
				return unrounded + calculateRoundingIncrement(unrounded, remainder);
			}
			return Long.signum(uDecimalDividend) * Long.signum(uDecimalDivisor) * calculateRoundingIncrement(unrounded, remainder);
		}
		return unrounded;
	}

	@Override
	public long invert(long uDecimal) {
		final long one = one();
		//special cases first
		if (uDecimal == 0) {
			throw new ArithmeticException("divide by zero");
		} else if (uDecimal == one) {
			return one;
		} else if (uDecimal == -one) {
			return -one;
		}
		final long oneSquare = one * one;
		final long truncatedValue = oneSquare / uDecimal;
		final long truncatedDigits = oneSquare % uDecimal;
		return truncatedValue + calculateRoundingIncrement(truncatedValue, truncatedDigits);
	}

	@Override
	public long pow(long uDecimal, int exponent) {
		//FIXME implement with rounding (not only on multiplications!)
		return super.pow(uDecimal, exponent);
	}

	@Override
	public long fromDouble(double value) {
		if (Double.isNaN(value) || Double.isInfinite(value)) {
			throw new ArithmeticException("cannot convert double to decimal: " + value);
		}
		final long one = one();
		final double iValue = value >= 0 ? Math.floor(value) : Math.ceil(value);
		final double fValue = value - iValue;
		final double sValue = fValue * one;
		final double tValue = fValue >= 0 ? Math.floor(fValue) : Math.ceil(fValue);
		final long truncatedValue = ((long) iValue) * one + (long)tValue; 
		return truncatedValue + calculateRoundingIncrement(truncatedValue, sValue - tValue);
	}
	
	@Override
	public long fromBigDecimal(BigDecimal value) {
		return value.multiply(BigDecimal.valueOf(one())).
				setScale(0, getRoundingMode()).longValue();
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		final int targetScale = getScale();
		if (scale == 0) {
			return fromLong(unscaledValue);
		}
		long result = unscaledValue;
		if (scale < targetScale) {
			for (int i = scale; i < targetScale; i++) {
				result *= 10;
			}
		} else if (scale > targetScale) {
			int lastDigit = 0;
			boolean zeroAfterLastDigit = true;
			for (int i = targetScale; i < scale; i++) {
				zeroAfterLastDigit &= (lastDigit == 0);
				lastDigit = (int) Math.abs(result % 10);
				result /= 10;
			}
			//rounding
			result += calculateRoundingIncrement(result, lastDigit, zeroAfterLastDigit);
		}
		return result;
	}

	@Override
	public long parse(String value) {
		final int indexOfDot = value.indexOf('.');
		if (indexOfDot < 0) {
			return fromLong(Long.parseLong(value));
		}
		final long iValue;
		if (indexOfDot > 0) {
			//NOTE: here we handle the special case "-.xxx" e.g. "-.25"
			iValue = indexOfDot == 1 && value.charAt(0) == '-' ? 0 : Long.parseLong(value.substring(0, indexOfDot));
		} else {
			iValue = 0;
		}
		final String fractionalPart = value.substring(indexOfDot + 1);
		final long fValue;
		final int fractionalLength = fractionalPart.length();
		if (fractionalLength > 0) {
			long fractionDigits = Long.parseLong(fractionalPart);
			final int scale = getScale();
			for (int i = fractionalLength; i < scale; i++) {
				fractionDigits *= 10;
			}
			int lastDigit = 0;
			boolean zeroAfterLastDigit = true;
			for (int i = scale; i < fractionalLength; i++) {
				zeroAfterLastDigit &= (lastDigit == 0);
				lastDigit = (int) Math.abs(fractionDigits % 10);
				fractionDigits /= 10;
			}
			//rounding
			fractionDigits += calculateRoundingIncrement(fractionDigits, lastDigit, zeroAfterLastDigit);
			fValue = fractionDigits;
		} else {
			fValue = 0;
		}
		final boolean negative = iValue < 0 || value.startsWith("-");
		return iValue * one() + (negative ? -fValue : fValue);
	}

	@Override
	public long toLong(long uDecimal) {
		final long one = one();
		final long truncated = uDecimal / one;
		return truncated + calculateRoundingIncrement(truncated, uDecimal % one);
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal, int scale) {
		return toBigDecimal(uDecimal).round(new MathContext(scale, getRoundingMode()));
	}

	/**
	 * Returns the rounding increment appropriate for the
	 * {@link #getRoundingMode()} of this arithmetics. The returned value is one
	 * of -1, 0 or 1.
	 * 
	 * @param truncatedValue
	 *            the truncated result before rounding is applied
	 * @param truncatedPart
	 *            the truncated part of a double, must be {@code >-1} and
	 *            {@code <1}
	 * @return the value to add to {@code truncatedValue} to get the rounded
	 *         result, one of -1, 0 or 1
	 */
	protected int calculateRoundingIncrement(long truncatedValue, double truncatedPart) {
		final double nonNegativeTruncatedPart = Math.abs(truncatedPart);
		final int firstTruncatedDigit = (int) (nonNegativeTruncatedPart * 10);
		final boolean zeroAfterFirstTruncatedDigit = 0 == (nonNegativeTruncatedPart - firstTruncatedDigit / 10d);
		return calculateRoundingIncrement(truncatedValue, firstTruncatedDigit, zeroAfterFirstTruncatedDigit);
	}

	/**
	 * Returns the rounding increment appropriate for the
	 * {@link #getRoundingMode()} of this arithmetics. The returned value is one
	 * of -1, 0 or 1.
	 * 
	 * @param truncatedValue
	 *            the truncated result before rounding is applied
	 * @param truncatedDigits
	 *            the truncated part of a double, must be {@code >-one()} and
	 *            {@code <one()}
	 * @return the value to add to {@code truncatedValue} to get the rounded
	 *         result, one of -1, 0 or 1
	 */
	protected int calculateRoundingIncrement(long truncatedValue, long truncatedDigits) {
		final long nonNegativeTruncatedDigits = Math.abs(truncatedDigits);
		final long oneDivBy10 = one() / 10;
		final int firstTruncatedDigit = (int) (nonNegativeTruncatedDigits / oneDivBy10);
		final long truncatedDigitsAfterFirst = nonNegativeTruncatedDigits % oneDivBy10;
		return calculateRoundingIncrement(truncatedValue, firstTruncatedDigit, truncatedDigitsAfterFirst == 0);
	}

	/**
	 * Returns the rounding increment appropriate for the
	 * {@link #getRoundingMode()} of this arithmetics. The returned value is one
	 * of -1, 0 or 1.
	 * 
	 * @param truncatedValue
	 *            the truncated result before rounding is applied
	 * @param firstTruncatedDigit
	 *            the first truncated digit, must be in {@code [0, 1, ..., 9]}
	 * @param zeroAfterFirstTruncatedDigit
	 *            true if all truncated digits after the first truncated digit
	 *            are zero, and false otherwise
	 * @return the value to add to {@code truncatedValue} to get the rounded
	 *         result, one of -1, 0 or 1
	 */
	abstract protected int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit);

}
