package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.Scale;

/**
 * Base class for arithmetic implementations which involve rounding strategies.
 * The result of an operation that leads to an overflow is silently truncated.
 */
public class RoundingArithmetics extends AbstractArithmetics {

	private final DecimalRounding rounding;

	/**
	 * Constructor for decimal arithmetics with given scale, rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @param roundingMode
	 *            the rounding mode to use for all decimal arithmetics
	 * @throws IllegalArgumentException
	 *             if scale is negative or zero
	 */
	public RoundingArithmetics(int scale, RoundingMode roundingMode) {
		this(scale, DecimalRounding.valueOf(roundingMode));
	}

	/**
	 * Constructor for decimal arithmetics with given scale, rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @param rounding
	 *            the rounding mode to use for all decimal arithmetics
	 * @throws IllegalArgumentException
	 *             if scale is negative or zero
	 */
	public RoundingArithmetics(int scale, DecimalRounding rounding) {
		super(scale);
		this.rounding = rounding;
	}

	/**
	 * Constructor for decimal arithmetics with given scale, rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale
	 * @param rounding
	 *            the rounding mode to use for all decimal arithmetics
	 */
	public RoundingArithmetics(Scale scale, DecimalRounding rounding) {
		super(scale);
		this.rounding = rounding;
	}

	public DecimalRounding getDecimalRounding() {
		return rounding;
	}

	@Override
	public final RoundingMode getRoundingMode() {
		return getDecimalRounding().getRoundingMode();
	}

	@Override
	public DecimalArithmetics derive(int scale) {
		if (scale == getScale()) {
			return this;
		}
		return new RoundingArithmetics(scale, getDecimalRounding());
	}

	@Override
	public DecimalArithmetics derive(RoundingMode roundingMode) {
		if (roundingMode == getRoundingMode()) {
			return this;
		}
		if (roundingMode == RoundingMode.DOWN) {
			return new TruncatingArithmetics(getScale());
		}
		return new RoundingArithmetics(getScale(), roundingMode);
	}

	@Override
	public DecimalArithmetics derive(OverflowMode overflowMode) {
		if (overflowMode == getOverflowMode()) {
			return this;
		}
		return new ExceptionOnOverflowArithmetics(this);
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
		final int scale = getScale();
		final long one = one();
		final long unrounded = TruncatingArithmetics.divide(uDecimalDividend, uDecimalDivisor, scale, one);
		final long product = TruncatingArithmetics.multiply(unrounded, uDecimalDivisor, one, sqrtOne);
		final long delta = uDecimalDividend - product;
		if (delta != 0) {
			final long remainder = TruncatingArithmetics.divide((delta % one) * one, uDecimalDivisor, scale, one);
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
	public long fromBigDecimal(BigDecimal value) {
		return value.multiply(oneBigDecimal()).setScale(0, getRoundingMode()).longValue();
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
			result += rounding.calculateRoundingIncrement(result, lastDigit, zeroAfterLastDigit);
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
			fractionDigits += rounding.calculateRoundingIncrement(fractionDigits, lastDigit, zeroAfterLastDigit);
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
		return rounding.calculateRoundingIncrement(truncatedValue, firstTruncatedDigit, zeroAfterFirstTruncatedDigit);
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
		return rounding.calculateRoundingIncrement(truncatedValue, firstTruncatedDigit, truncatedDigitsAfterFirst == 0);
	}

}
