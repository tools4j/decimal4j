package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.scale.Scale9f;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.scale.Scales;

/**
 * An arithmetic implementation which truncates decimals after the last scale
 * digit without rounding. Operations are unchecked, that is, the result of an
 * operation that leads to an overflow is silently truncated.
 */
public class UncheckedScaleNfTruncatingArithmetics extends
		AbstractUncheckedScaleNfArithmetics implements DecimalArithmetics {
	
	/**
	 * sqrt(Long.MAX_VALUE) used in {@link #square(long)}
	 * @see Long#MAX_VALUE
	 */
	private static final long SQRT_LONG_MAX_VALUE = 3037000499L;

	/**
	 * Constructor for silent decimal arithmetics with given scale, truncating
	 * {@link RoundingMode#DOWN DOWN} rounding mode and
	 * {@link OverflowMode#STANDARD SILENT} overflow mode.
	 * 
	 * @param scaleMetrics
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative or uneven
	 */
	public UncheckedScaleNfTruncatingArithmetics(ScaleMetrics scaleMetrics) {
		super(scaleMetrics);
	}

	@Override
	public RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		final SpecialMultiplicationResult special = SpecialMultiplicationResult.getFor(this, uDecimal1, uDecimal2);
		if (special != null) {
			return special.multiply(this, uDecimal1, uDecimal2);
		}
		final ScaleMetrics scaleMetrics = getScaleMetrics();
		final int scale = scaleMetrics.getScale();

		//use scale to split into 2 parts: i (integral) and f (fractional)
		final long i1 = scaleMetrics.divideByScaleFactor(uDecimal1);
		final long i2 = scaleMetrics.divideByScaleFactor(uDecimal2);
		final long f1 = uDecimal1 - scaleMetrics.multiplyByScaleFactor(i1);
		final long f2 = uDecimal2 - scaleMetrics.multiplyByScaleFactor(i2);
		if (scale <= 9) {
			//low order product f1*f2 fits in long
			return scaleMetrics.multiplyByScaleFactor(i1 * i2) + i1 * f2 + i2 * f1 + scaleMetrics.divideByScaleFactor(f1 * f2);
		} else {
			//low order product f1*f2 does not fit in long, do component wise multiplication with Scale9f
			final Scale9f scale9f = Scale9f.INSTANCE;
			final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
			final long hf1 = scale9f.divideByScaleFactor(f1);
			final long hf2 = scale9f.divideByScaleFactor(f2);
			final long lf1 = f1 - scale9f.multiplyByScaleFactor(hf1);
			final long lf2 = f2 - scale9f.multiplyByScaleFactor(hf2);

			final long f1xf2 = scaleDiff18.multiplyByScaleFactor(hf1 * hf2) + scaleDiff09.divideByScaleFactor(hf1 * lf2 + hf2 * lf1 + scale9f.divideByScaleFactor(lf1 * lf2));
			return scaleMetrics.multiplyByScaleFactor(i1 * i2) + i1 * f2 + i2 * f1 + f1xf2;
		}
	}

	@Override
	public long square(long uDecimal) {
		final ScaleMetrics scaleMetrics = getScaleMetrics();
		final int scale = scaleMetrics.getScale();

		//use scale to split into 2 parts: i (integral) and f (fractional)
		final long i = scaleMetrics.divideByScaleFactor(uDecimal);
		final long f = uDecimal - scaleMetrics.multiplyByScaleFactor(i);
		if (f >= -SQRT_LONG_MAX_VALUE & f <= SQRT_LONG_MAX_VALUE) {
			//low order product f*f fits in long
			return scaleMetrics.multiplyByScaleFactor(i * i) + ((i * f)<<1) + scaleMetrics.divideByScaleFactor(f * f);
		} else {
			//low order product f1*f2 does not fit in long, do component wise multiplication with Scale9f
			final Scale9f scale9f = Scale9f.INSTANCE;
			final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
			final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
			final long hf = scale9f.divideByScaleFactor(f);
			final long lf = f - scale9f.multiplyByScaleFactor(hf);

			final long fxf = scaleDiff18.multiplyByScaleFactor(hf * hf) + scaleDiff09.divideByScaleFactor(((hf * lf)<<1) + scale9f.divideByScaleFactor(lf * lf));
			return scaleMetrics.multiplyByScaleFactor(i * i) + ((i * f)<<1) + fxf;
		}
	}

	@Override
	public long sqrt(long uDecimal) {
		return Sqrt.sqrt(this, uDecimal);
	}

	@Override
	public long divideByLong(long uDecimalDividend, long lDivisor) {
		return uDecimalDividend / lDivisor;
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
		if (uDecimalDividend <= (maxInteger) & uDecimalDividend >= minInteger) {
			//just do it, multiplication result fits in long
			return scaleMetrics.multiplyByScaleFactor(uDecimalDividend) / uDecimalDivisor;
		}
		//perform component wise division
		final long integralPart = uDecimalDividend / uDecimalDivisor;
		final long reminder = uDecimalDividend - integralPart * uDecimalDivisor;
		final long fractionalPart;
		if (reminder <= maxInteger & reminder >= minInteger) {
			fractionalPart = scaleMetrics.multiplyByScaleFactor(reminder) / uDecimalDivisor;
		} else {
			fractionalPart = Div.scaleTo128divBy64(scaleMetrics, reminder, uDecimalDivisor);
		}
		return scaleMetrics.multiplyByScaleFactor(integralPart) + fractionalPart; 
	}

	private long divideByPowerOf10(long uDecimalDividend, long uDecimalDivisor, ScaleMetrics pow10) {
		final int scaleDiff = getScale() - pow10.getScale();
		final long quot;
		if (scaleDiff <= 0) {
			//divide
			final ScaleMetrics scaleMetrics = Scales.valueOf(-scaleDiff);
			quot = scaleMetrics.divideByScaleFactor(uDecimalDividend);

		} else {
			//multiply
			final ScaleMetrics scaleMetrics = Scales.valueOf(scaleDiff);
			quot = scaleMetrics.multiplyByScaleFactor(uDecimalDividend);
		}
		return uDecimalDivisor > 0 ? quot : -quot;
	}

	@Override
	public long avg(long a, long b) {
		return UncheckedScale0fTruncatingArithmetics._avg(a, b);
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int positions) {
		return UncheckedScale0fTruncatingArithmetics._multiplyByPowerOf10(uDecimal, positions);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int positions) {
		return UncheckedScale0fTruncatingArithmetics._divideByPowerOf10(uDecimal, positions);
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		return value.multiply(getScaleMetrics().getScaleFactorAsBigDecimal()).longValue();
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		if (scale == 0) {
			return fromLong(unscaledValue);
		}
		return UncheckedScale0fTruncatingArithmetics._multiplyByPowerOf10(unscaledValue, getScale() - scale);
	}

	@Override
	public long toLong(long uDecimal) {
		return getScaleMetrics().divideByScaleFactor(uDecimal);
	}

	@Override
	public float toFloat(long uDecimal) {
		//NOTE: not very efficient
		return Float.valueOf(toString(uDecimal));
	}

	@Override
	public double toDouble(long uDecimal) {
		//NOTE: not very efficient
		return Double.valueOf(toString(uDecimal));
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
			for (int i = scale; i < fractionalLength; i++) {
				fractionDigits /= 10;
			}
			fValue = fractionDigits;
		} else {
			fValue = 0;
		}
		final boolean negative = iValue < 0 || value.startsWith("-");
		return iValue * one() + (negative ? -fValue : fValue);
	}

}
