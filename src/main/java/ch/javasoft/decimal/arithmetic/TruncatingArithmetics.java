package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.ScaleMetrics;
import ch.javasoft.decimal.math.UInt128;

/**
 * An arithmetic implementation which truncates digits after the last scale
 * digit without rounding; the result of an operation that leads to an overflow
 * is silently truncated.
 */
public class TruncatingArithmetics extends AbstractScaledArithmetics implements
		DecimalArithmetics {

	private transient volatile EnumMap<DecimalRounding, DecimalArithmetics> roundingArithmetics = null;

	/**
	 * Constructor for silent decimal arithmetics with given scale, truncating
	 * {@link RoundingMode#DOWN DOWN} rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scaleMetrics
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative or uneven
	 */
	public TruncatingArithmetics(ScaleMetrics scaleMetrics) {
		super(scaleMetrics);
	}

	@Override
	public RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public DecimalArithmetics derive(int scale) {
		if (scale == getScale()) {
			return this;
		}
		return ScaleMetrics.valueOf(scale).getTruncatingArithmetics();
	}

	@Override
	public DecimalArithmetics derive(RoundingMode roundingMode) {
		if (roundingMode == RoundingMode.DOWN) {
			return this;
		}
		if (roundingArithmetics == null) {
			synchronized(this) {
				if (roundingArithmetics == null) {
					roundingArithmetics = initRoundingArithmetics();
				}
			}
		}
		return roundingArithmetics.get(DecimalRounding.valueOf(roundingMode));
	}

	private EnumMap<DecimalRounding, DecimalArithmetics> initRoundingArithmetics() {
		final EnumMap<DecimalRounding, DecimalArithmetics> result = new EnumMap<DecimalRounding, DecimalArithmetics>(DecimalRounding.class);
		for (final DecimalRounding rounding : DecimalRounding.VALUES) {
			if (rounding != DecimalRounding.DOWN) {
				result.put(rounding, createDecimalArithmeticsFor(rounding));
			} else {
				result.put(rounding, this);
			}
		}
		return result;
	}
	
	protected DecimalArithmetics createDecimalArithmeticsFor(DecimalRounding rounding) {
		return new RoundingArithmetics(getScaleMetrics(), rounding);
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
//		final long f1 = scaleMetrics.moduloByScaleFactor(uDecimal1);
//		final long f2 = scaleMetrics.moduloByScaleFactor(uDecimal2);
		final long f1 = uDecimal1 - scaleMetrics.multiplyByScaleFactor(i1);
		final long f2 = uDecimal2 - scaleMetrics.multiplyByScaleFactor(i2);
		return scaleMetrics.multiplyByScaleFactor(i1 * i2) + i1 * f2 + i2 * f1 + scaleMetrics.divideByScaleFactor(f1 * f2);
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
		final ScaleMetrics pow10 = ScaleMetrics.findByScaleFactor(Math.abs(uDecimalDivisor));
		if (pow10 != null) {
			return divideByPowerOf10(uDecimalDividend, uDecimalDivisor, pow10);
		}
		//WE WANT: uDecimalDividend * one / uDecimalDivisor
		final ScaleMetrics scaleMetrics = getScaleMetrics();
		if (uDecimalDividend <= scaleMetrics.getMaxIntegerValue() && uDecimalDividend >= scaleMetrics.getMinIntegerValue()) {
			//just do it, multiplication result fits in long
			return scaleMetrics.multiplyByScaleFactor(uDecimalDividend) / uDecimalDivisor;
		}
		return UInt128.divide128(scaleMetrics, uDecimalDividend, uDecimalDivisor);
	}
	private long divideByPowerOf10(long uDecimalDividend, long uDecimalDivisor, ScaleMetrics pow10) {
		final int scaleDiff = getScale() - pow10.getScale();
		final long quot;
		if (scaleDiff <= 0) {
			//divide
			final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(-scaleDiff);
			quot = scaleMetrics.divideByScaleFactor(uDecimalDividend);
			
		} else {
			//multiply
			final ScaleMetrics scaleMetrics = ScaleMetrics.valueOf(scaleDiff);
			quot = scaleMetrics.multiplyByScaleFactor(uDecimalDividend);
		}
		return uDecimalDivisor > 0 ? quot : -quot;
	}

	@Override
	public long invert(long uDecimal) {
		//special cases first
		final long one = one();
		final SpecialDivisionResult special = SpecialDivisionResult.getFor(this, one, uDecimal);
		if (special != null) {
			return special.divide(this, one, uDecimal);
		}
		//div by power of 10
		final ScaleMetrics pow10 = ScaleMetrics.findByScaleFactor(Math.abs(uDecimal));
		if (pow10 != null) {
			return divideByPowerOf10(one(), pow10.getScaleFactor(), pow10);
		}
		//check if one * one fits in long
		final ScaleMetrics scaleMetrics = getScaleMetrics();
		if (scaleMetrics.getScale() <= 9) {
			return getScaleMetrics().multiplyByScaleFactor(one()) / uDecimal;
		}
		//too big, use divide128 now
		return UInt128.divide128(scaleMetrics, one(), uDecimal);
	}

	@Override
	public long pow(long uDecimal, int exponent) {
		return pow(this, uDecimal, exponent);
	}

	static long pow(DecimalArithmetics arithmetics, long uDecimal, int exponent) {
		if (exponent == 0) {
			return arithmetics.one();
		}
		long base;
		int exp;
		if (exponent > 0) {
			base = uDecimal;
			exp = exponent;
		} else {/* exponent < 0 */
			base = arithmetics.invert(uDecimal);
			exp = -exponent;
		}
		long result = base;
		//FIXME eliminate repeated truncation with multiplications in loop
		while (exp != 1 && result != 0) {
			if (exp % 2 == 0) {
				//even
				result = arithmetics.multiply(result, result);
				exp >>>= 1;
			} else {
				//odd
				result = arithmetics.multiply(result, base);
				exp--;
			}
		}
		return result;
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		return value.multiply(oneBigDecimal()).longValue();
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		final int targetScale = getScale();
		if (scale == 0) {
			return fromLong(unscaledValue);
		}
		long result = unscaledValue;
		for (int i = scale; i < targetScale; i++) {
			result *= 10;
		}
		for (int i = targetScale; i < scale; i++) {
			result /= 10;
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
