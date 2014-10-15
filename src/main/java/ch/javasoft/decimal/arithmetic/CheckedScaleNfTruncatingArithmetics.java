package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

import ch.javasoft.decimal.scale.Scale9f;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.scale.Scales;

/**
 * Arithmetics implementation throwing an exception if an operation leads to on
 * overflow. Decimals after the last scale digit are truncated without rounding.
 */
public class CheckedScaleNfTruncatingArithmetics extends AbstractCheckedScaleNfArithmetics {

	//lazy init
	private Double minDouble;
	private Double maxDouble;

	public CheckedScaleNfTruncatingArithmetics(ScaleMetrics scaleMetrics) {
		super(scaleMetrics);
	}

	@Override
	public RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		try {
			final ScaleMetrics scaleMetrics = getScaleMetrics();
			final int scale = scaleMetrics.getScale();
			final long i1 = scaleMetrics.divideByScaleFactor(uDecimal1);
			final long i2 = scaleMetrics.divideByScaleFactor(uDecimal2);
			final long f1 = uDecimal1 - scaleMetrics.multiplyByScaleFactor(i1);
			final long f2 = uDecimal2 - scaleMetrics.multiplyByScaleFactor(i2);
			final long i1xf2 = i1 * f2;//cannot overflow
			final long i2xf1 = i2 * f1;//cannot overflow
			final long i1xi2;
			final long f1xf2;
			if (scale <= 9) {
				i1xi2 = multiplyByLong(i1, i2);//checked
				//product fits, hence unchecked
				f1xf2 = scaleMetrics.divideByScaleFactor(f1 * f2);
			} else {
				i1xi2 = i1 * i2;//cannot overflow for this scale
				
				//low order product f1*f2 does not fit in long, do component wise multiplication with Scale9f
				final Scale9f scale9f = Scale9f.INSTANCE;
				final ScaleMetrics scaleDiff09 = Scales.valueOf(scale - 9);
				final ScaleMetrics scaleDiff18 = Scales.valueOf(18 - scale);
				final long hf1 = scale9f.divideByScaleFactor(f1);
				final long hf2 = scale9f.divideByScaleFactor(f2);
				final long lf1 = f1 - scale9f.multiplyByScaleFactor(hf1);
				final long lf2 = f2 - scale9f.multiplyByScaleFactor(hf2);

				//partial products, cannot overflow
				final long hf1xhf2 = hf1 * hf2;
				final long hf1xlf2 = hf1 * lf2;
				final long lf1xhf2 = lf1 * hf2;
				final long lf1xlf2 = lf1 * lf2;

				//scale each part
				final long hf1xhf2s = scaleDiff18.multiplyByScaleFactorExact(hf1 * hf2);
				
				
				final long lf1xlf2d = scale9f.divideByScaleFactor(lf1xlf2);
				final long hl_lh_ll_f1xf2 = hf1 * lf2 + hf2 * lf1 + lf1xlf2d;
				final long hl_lh_ll_f1xf2d = scaleDiff09.divideByScaleFactor(hl_lh_ll_f1xf2);
				
				f1xf2 = add(hf1xhf2s, hl_lh_ll_f1xf2d);//checked
//				f1xf2 = scaleDiff18.multiplyByScaleFactorExact(hf1 * hf2) + scaleDiff09.divideByScaleFactor(hf1 * lf2 + hf2 * lf1 + scale9f.divideByScaleFactor(lf1 * lf2));
			}
			//add it all up now, every addition checked
			long result = scaleMetrics.multiplyByScaleFactorExact(i1xi2);
			result = add(result, i1xf2);
			result = add(result, i2xf1);
			result = add(result, f1xf2);
			return result;
		} catch (ArithmeticException e) {
			final ArithmeticException ex = new ArithmeticException("overflow: " + toString(uDecimal1) + " * " + toString(uDecimal2));
			e.initCause(e);
			throw ex;
		}
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
		if (uDecimalDividend <= scaleMetrics.getMaxIntegerValue() && uDecimalDividend >= scaleMetrics.getMinIntegerValue()) {
			//just do it, multiplication result fits in long
			return scaleMetrics.multiplyByScaleFactor(uDecimalDividend) / uDecimalDivisor;
		}
		//too big, use divide128 now
		final long result = Div.scaleTo128divBy64(scaleMetrics, uDecimalDividend, uDecimalDivisor);
		//check by multiply
		if (multiply(uDecimalDivisor, result) == uDecimalDividend) {
			return result;
		}
		throw new ArithmeticException("overflow: " + toString(uDecimalDividend) + " / " + toString(uDecimalDivisor) + " = " + toString(result));
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
			quot = scaleMetrics.multiplyByScaleFactorExact(uDecimalDividend);
		}
		return uDecimalDivisor > 0 ? quot : -quot;
	}
	
	@Override
	public long avg(long a, long b) {
		return UncheckedScale0fTruncatingArithmetics._avg(a, b);
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
		final ScaleMetrics pow10 = Scales.findByScaleFactor(Math.abs(uDecimal));
		if (pow10 != null) {
			return divideByPowerOf10(one, pow10.getScaleFactor(), pow10);
		}
		//check if one * one fits in long
		final ScaleMetrics scaleMetrics = getScaleMetrics();
		if (scaleMetrics.getScale() <= 9) {
			return getScaleMetrics().multiplyByScaleFactorExact(one) / uDecimal;
		}
		//too big, use divide128 now
		final long result = Div.scaleTo128divBy64(scaleMetrics, one, uDecimal);
		//check by multiply
		if (multiply(uDecimal, result) == one) {
			return result;
		}
		throw new ArithmeticException("overflow: 1 / " + toString(uDecimal) + " = " + toString(result));
	}

	@Override
	public long sqrt(long uDecimal) {
		return Sqrt.sqrt(this, uDecimal);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int positions) {
		return CheckedScale0fTruncatingArithmetics.divideByPowerOf10(this, uDecimal, positions);
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int positions) {
		return CheckedScale0fTruncatingArithmetics.multiplyByPowerOf10(this, uDecimal, positions);
	}

	@Override
	public long fromDouble(double value) {
		initDoubleMinMax();
		if (value <= maxDouble & value >= minDouble) { 
			return unchecked.fromDouble(value);
		}
		throw new ArithmeticException("overflow for conversion from double: " + value);
	}

	private void initDoubleMinMax() {
		if (minDouble == null) {
			minDouble = toDouble(Long.MIN_VALUE);
		}
		if (maxDouble == null) {
			maxDouble = toDouble(Long.MAX_VALUE);
		}
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		if (scale == 0) {
			return fromLong(unscaledValue);
		}
		return CheckedScale0fTruncatingArithmetics.multiplyByPowerOf10(this, unscaledValue, getScale() - scale);
	}

}
