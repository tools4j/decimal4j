package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.scale.Scale9f;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.scale.Scales;
/**
 * Arithmetic implementation for rounding strategies. For
 * {@link RoundingMode#DOWN} the more efficient
 * {@link UncheckedScaleNfTruncatingArithmetics} is available. If an operation
 * leads to an overflow the result is silently truncated.
 */
public class UncheckedScaleNfRoundingArithmetics extends
		AbstractUncheckedScaleNfArithmetics {

	private final DecimalRounding rounding;

	/**
	 * Constructor for decimal arithmetics with given scale, rounding mode and
	 * {@link OverflowMode#UNCHECKED SILENT} overflow mode.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics for this decimal arithmetics
	 * @param roundingMode
	 *            the rounding mode to use for all decimal arithmetics
	 */
	public UncheckedScaleNfRoundingArithmetics(ScaleMetrics scaleMetrics, RoundingMode roundingMode) {
		this(scaleMetrics, DecimalRounding.valueOf(roundingMode));
	}

	/**
	 * Constructor for decimal arithmetics with given scale, rounding mode and
	 * {@link OverflowMode#UNCHECKED SILENT} overflow mode.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics for this decimal arithmetics
	 * @param rounding
	 *            the rounding mode to use for all decimal arithmetics
	 */
	public UncheckedScaleNfRoundingArithmetics(ScaleMetrics scaleMetrics, DecimalRounding rounding) {
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
	public long multiply(long uDecimal1, long uDecimal2) {
		final SpecialMultiplicationResult special = SpecialMultiplicationResult.getFor(this, uDecimal1, uDecimal2);
		if (special != null) {
			return special.multiply(this, uDecimal1, uDecimal2);
		}

		final ScaleMetrics scaleMetrics = getScaleMetrics();
		final int scale = scaleMetrics.getScale();
		if (scale <= 9) {
			//use scale to split into 2 parts: i (integral) and f (fractional)
			//with this scale, the low order product f1*f2 fits in a long
			final long i1 = scaleMetrics.divideByScaleFactor(uDecimal1);
			final long i2 = scaleMetrics.divideByScaleFactor(uDecimal2);
			final long f1 = uDecimal1 - scaleMetrics.multiplyByScaleFactor(i1);
			final long f2 = uDecimal2 - scaleMetrics.multiplyByScaleFactor(i2);
			final long f1xf2 = f1 * f2;
			final long f1xf2d = scaleMetrics.divideByScaleFactor(f1xf2);
			final long f1xf2r = f1xf2 - scaleMetrics.multiplyByScaleFactor(f1xf2d);
			final long unrounded = scaleMetrics.multiplyByScaleFactor(i1 * i2) + i1 * f2 + i2 * f1 + f1xf2d;
			return unrounded + rounding.calculateRoundingIncrement(unrounded, f1xf2r, scaleMetrics.getScaleFactor());
		} else {
			//use scale9 to split into 2 parts: h (high) and l (low)
			final ScaleMetrics scale9f = Scale9f.INSTANCE;
			final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
			final long h1 = scale9f.divideByScaleFactor(uDecimal1);
			final long h2 = scale9f.divideByScaleFactor(uDecimal2);
			final long l1 = uDecimal1 - scale9f.multiplyByScaleFactor(h1);
			final long l2 = uDecimal2 - scale9f.multiplyByScaleFactor(h2);
			final long h1xl2 = h1 * l2;
			final long h2xl1 = h2 * l1;
			final long l1xl2 = l1 * l2;
			final long l1xl2d = scale9f.divideByScaleFactor(l1xl2);
			final long l1xl2r = l1xl2 - scale9f.multiplyByScaleFactor(l1xl2d);
			final long sumOfLowsHalf = (h1xl2 >> 1) + (h2xl1 >> 1) + (l1xl2d >> 1) //sum halfs to avoid overflow
					+ (((h1xl2 & h2xl1) | (h1xl2 & l1xl2d) | (h2xl1 & l1xl2d)) & 0x1); //carry of lost bits
			final long sumOfLowsHalfDiv = scaleDiff09.divideByScaleFactorHalf(sumOfLowsHalf);
			final long sumOfLowsHalfRem = sumOfLowsHalf - scaleDiff09.multiplyByScaleFactorHalf(sumOfLowsHalfDiv); 
			final long sumOfLowsHalfBit = ((h1xl2 ^ h2xl1 ^ l1xl2d) & 0x1); //lost bit
			final long unrounded = scaleDiff18.multiplyByScaleFactor(h1 * h2) + sumOfLowsHalfDiv;
			final long remainder = scale9f.multiplyByScaleFactor((sumOfLowsHalfRem<<1) + sumOfLowsHalfBit) + l1xl2r;
			return unrounded + rounding.calculateRoundingIncrement(unrounded, remainder, scaleMetrics.getScaleFactor());
		}
	}
	
	@Override
	public long square(long uDecimal) {
		final ScaleMetrics scaleMetrics = getScaleMetrics();
		final int scale = scaleMetrics.getScale();
		if (scale <= 9) {
			//use scale to split into 2 parts: i (integral) and f (fractional)
			//with this scale, the low order product f*f fits in a long
			final long i = scaleMetrics.divideByScaleFactor(uDecimal);
			final long f = uDecimal - scaleMetrics.multiplyByScaleFactor(i);
			final long fxf = f * f;
			final long fxfd = scaleMetrics.divideByScaleFactor(fxf);
			final long fxfr = fxf - scaleMetrics.multiplyByScaleFactor(fxfd);
			final long unrounded = scaleMetrics.multiplyByScaleFactor(i * i) + ((i * f)<<1) + fxfd;
			return unrounded + rounding.calculateRoundingIncrement(unrounded, fxfr, scaleMetrics.getScaleFactor());
		} else {
			//use scale9 to split into 2 parts: h (high) and l (low)
			final ScaleMetrics scale9f = Scale9f.INSTANCE;
			final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
			final long h = scale9f.divideByScaleFactor(uDecimal);
			final long l = uDecimal - scale9f.multiplyByScaleFactor(h);
			final long hxl = h * l;
			final long lxl = l * l;
			final long lxld = scale9f.divideByScaleFactor(lxl);
			final long lxlr = lxl - scale9f.multiplyByScaleFactor(lxld);
			final long sumOfLowsHalf = hxl + (lxld >> 1); //sum halfs to avoid overflow
			final long sumOfLowsHalfDiv = scaleDiff09.divideByScaleFactorHalf(sumOfLowsHalf);
			final long sumOfLowsHalfRem = sumOfLowsHalf - scaleDiff09.multiplyByScaleFactorHalf(sumOfLowsHalfDiv); 
			final long sumOfLowsHalfBit = (lxld & 0x1); //lost bit
			final long unrounded = scaleDiff18.multiplyByScaleFactor(h * h) + sumOfLowsHalfDiv;
			final long remainder = scale9f.multiplyByScaleFactor((sumOfLowsHalfRem<<1) + sumOfLowsHalfBit) + lxlr;
			return unrounded + rounding.calculateRoundingIncrement(unrounded, remainder, scaleMetrics.getScaleFactor());
		}
	}

	@Override
	public long sqrt(long uDecimal) {
		return Sqrt.sqrt(this, rounding, uDecimal);
	}

	@Override
	public long divideByLong(long uDecimalDividend, long lDivisor) {
		return UncheckedScale0fRoundingArithmetics.divideByLong(rounding, uDecimalDividend, lDivisor);
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		//special cases first
		final SpecialDivisionResult special = SpecialDivisionResult.getFor(this, uDecimalDividend, uDecimalDivisor);
		if (special != null) {
			return special.divide(this, uDecimalDividend, uDecimalDivisor);
		}
		//div by power of 10
		final ScaleMetrics pow10 = Scales.findByScaleFactor(Math.abs(uDecimalDivisor));
		if (pow10 != null) {
			return divideByPowerOf10(uDecimalDividend, uDecimalDivisor, pow10);
		}
		//WE WANT: uDecimalDividend * one / uDecimalDivisor
		final ScaleMetrics scaleMetrics = getScaleMetrics();
		final long maxInteger = scaleMetrics.getMaxIntegerValue();
		final long minInteger = scaleMetrics.getMinIntegerValue();
		if (uDecimalDividend <= maxInteger & uDecimalDividend >= minInteger) {
			//just do it, multiplication result fits in long
			final long scaledDividend = scaleMetrics.multiplyByScaleFactor(uDecimalDividend);
			final long quot = scaledDividend / uDecimalDivisor;
			final long rem = scaledDividend - quot * uDecimalDivisor;
			return quot + rounding.calculateRoundingIncrementForDivision(quot, rem, uDecimalDivisor);
		}
		//perform component wise division
		final long integralPart = uDecimalDividend / uDecimalDivisor;
		final long reminder = uDecimalDividend - integralPart * uDecimalDivisor;
		if (reminder <= maxInteger & reminder >= minInteger) {
			final long scaledReminder = scaleMetrics.multiplyByScaleFactor(reminder);
			final long fractionalPart = scaledReminder / uDecimalDivisor;
			final long subFractionalPart = scaledReminder - fractionalPart * uDecimalDivisor;
			final long truncated = scaleMetrics.multiplyByScaleFactor(integralPart) + fractionalPart;
			return truncated + rounding.calculateRoundingIncrementForDivision(truncated, subFractionalPart, uDecimalDivisor); 
		} else {
			final long fractionalPart = Div.scaleTo128divBy64(scaleMetrics, rounding, reminder, uDecimalDivisor);
			return scaleMetrics.multiplyByScaleFactor(integralPart) + fractionalPart; 
		}
	}

	private long divideByPowerOf10(long uDecimalDividend, long uDecimalDivisor, ScaleMetrics pow10) {
		final int scaleDiff = getScale() - pow10.getScale();
		if (scaleDiff <= 0) {
			//divide
			final ScaleMetrics scaler = Scales.valueOf(-scaleDiff);
			final long truncatedValue = scaler.divideByScaleFactor(uDecimalDividend);
			final long truncatedDigits = uDecimalDividend - scaler.multiplyByScaleFactor(truncatedValue);
			if (uDecimalDivisor > 0) {
				return truncatedValue + rounding.calculateRoundingIncrementForDivision(truncatedValue, truncatedDigits, scaler.getScaleFactor());
			}
			return -truncatedValue + rounding.calculateRoundingIncrementForDivision(-truncatedValue, -truncatedDigits, scaler.getScaleFactor());

		} else {
			//multiply
			final ScaleMetrics scaler = Scales.valueOf(scaleDiff);
			final long quot = scaler.multiplyByScaleFactor(uDecimalDividend);
			return uDecimalDivisor > 0 ? quot : -quot;
		}
	}
	
	@Override
	public long pow(long uDecimal, int exponent) {
		//FIXME implement with rounding (not only on multiplications!)
		return super.pow(uDecimal, exponent);
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		return UncheckedScale0fRoundingArithmetics.shiftLeft(rounding, uDecimal, positions);
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		return UncheckedScale0fRoundingArithmetics.shiftRight(rounding, uDecimal, positions);
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int n) {
		return UncheckedScale0fRoundingArithmetics.multiplyByPowerOf10(rounding, uDecimal, n);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int n) {
		return UncheckedScale0fRoundingArithmetics.divideByPowerOf10(rounding, uDecimal, n);
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		return value.multiply(getScaleMetrics().getScaleFactorAsBigDecimal()).setScale(0, getRoundingMode()).longValue();
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		if (scale == 0) {
			return fromLong(unscaledValue);
		}
		return UncheckedScale0fRoundingArithmetics.multiplyByPowerOf10(rounding, unscaledValue, getScale() - scale);
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
			fractionDigits += rounding.calculateRoundingIncrement(1, fractionDigits, lastDigit, zeroAfterLastDigit);
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
	public float toFloat(long uDecimal) {
		//FIXME apply proper rounding mode
		//NOTE: note very efficient
		return Float.valueOf(toString(uDecimal));
	}

	@Override
	public double toDouble(long uDecimal) {
		//FIXME apply proper rounding mode
		//NOTE: note very efficient
		return Double.valueOf(toString(uDecimal));
	}
}
