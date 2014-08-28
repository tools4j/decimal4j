package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.ScaleMetrics;
import ch.javasoft.decimal.arithmetic.TruncatingArithmetics.SpecialDivisionResult;

/**
 * Base class for arithmetic implementations which involve rounding strategies.
 * The result of an operation that leads to an overflow is silently truncated.
 */
public class RoundingArithmetics extends AbstractScaledArithmetics {

	private final DecimalRounding rounding;

	/**
	 * Constructor for decimal arithmetics with given scale, rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics for this decimal arithmetics
	 * @param roundingMode
	 *            the rounding mode to use for all decimal arithmetics
	 */
	RoundingArithmetics(ScaleMetrics scaleMetrics, RoundingMode roundingMode) {
		this(scaleMetrics, DecimalRounding.valueOf(roundingMode));
	}

	/**
	 * Constructor for decimal arithmetics with given scale, rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics for this decimal arithmetics
	 * @param rounding
	 *            the rounding mode to use for all decimal arithmetics
	 */
	RoundingArithmetics(ScaleMetrics scaleMetrics, DecimalRounding rounding) {
		super(scaleMetrics);
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
		return ScaleMetrics.valueOf(scale).getTruncatingArithmetics().derive(getRoundingMode());
	}

	@Override
	public DecimalArithmetics derive(RoundingMode roundingMode) {
		if (roundingMode == getRoundingMode()) {
			return this;
		}
		return getScaleMetrics().getTruncatingArithmetics().derive(roundingMode);
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
		final ScaleMetrics scaleMetrics = getScaleMetrics();
		final long i1 = scaleMetrics.divideByScaleFactor(uDecimal1);
		final long i2 = scaleMetrics.divideByScaleFactor(uDecimal2);
		final long f1 = scaleMetrics.moduloByScaleFactor(uDecimal1);
		final long f2 = scaleMetrics.moduloByScaleFactor(uDecimal2);
		final long f1xf2 = f1 * f2;
		final long inc = scaleMetrics.divideByScaleFactor(f1xf2);
		final long rem = scaleMetrics.moduloByScaleFactor(f1xf2);
		final long unrounded = scaleMetrics.multiplyByScaleFactor(i1 * i2) + i1 * f2 + i2 * f1 + inc;
		return unrounded + rounding.calculateRoundingIncrement(unrounded, rem, one());
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		//special cases first
		final SpecialDivisionResult special = SpecialDivisionResult.getFor(this, uDecimalDividend, uDecimalDivisor);
		if (special != null) {
			return special.divide(this, uDecimalDividend, uDecimalDivisor);
		}
		return divide128(uDecimalDividend, uDecimalDivisor);
	}
	private long divide128(long uDecimalDividend, long uDecimalDivisor) {
		final ScaleMetrics scaleMetrics = getScaleMetrics();
		final TruncatingArithmetics truncArith = scaleMetrics.getTruncatingArithmetics();
		final long unrounded = truncArith.divide(uDecimalDividend, uDecimalDivisor);
		final long product = truncArith.multiply(unrounded, uDecimalDivisor);
		final long delta = uDecimalDividend - product;
		if (delta != 0) {
			final long deltaScaled = scaleMetrics.multiplyByScaleFactor(delta);
			return unrounded + rounding.calculateRoundingIncrementForDivision(unrounded, deltaScaled, uDecimalDivisor);//OVERFLOW possible
		}
		return unrounded;
	}

	@Override
	public long invert(long uDecimal) {
		//special cases first
		final long one = one();
		final SpecialDivisionResult special = SpecialDivisionResult.getFor(this, one, uDecimal);
		if (special != null) {
			return special.divide(this, one, uDecimal);
		}
		//check if one * one fits in long
		final ScaleMetrics scaleMetrics = getScaleMetrics();
		if (scaleMetrics.getScale() <= 9) {
			final long oneSquare = scaleMetrics.multiplyByScaleFactor(one);
			final long truncatedValue = oneSquare / uDecimal;
			final long truncatedDigits = oneSquare % uDecimal;
			return truncatedValue + rounding.calculateRoundingIncrementForDivision(truncatedValue, truncatedDigits, uDecimal);
		}
		//too big, use divide128 now
		return divide128(one, uDecimal);
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
			result += rounding.calculateRoundingIncrement(result, unscaledValue < 0, lastDigit, zeroAfterLastDigit);
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
			fractionDigits += rounding.calculateRoundingIncrement(fractionDigits, false, lastDigit, zeroAfterLastDigit);
			fValue = fractionDigits;
		} else {
			fValue = 0;
		}
		final boolean negative = iValue < 0 || value.startsWith("-");
		return iValue * one() + (negative ? -fValue : fValue);
	}

	@Override
	public long toLong(long uDecimal) {
		final ScaleMetrics scaleMetrics = getScaleMetrics();
		final long truncated = scaleMetrics.divideByScaleFactor(uDecimal);
		final long reminder = scaleMetrics.moduloByScaleFactor(uDecimal);
		return truncated + rounding.calculateRoundingIncrement(truncated, reminder, one());
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal, int scale) {
		return toBigDecimal(uDecimal).round(new MathContext(scale, getRoundingMode()));
	}
}
