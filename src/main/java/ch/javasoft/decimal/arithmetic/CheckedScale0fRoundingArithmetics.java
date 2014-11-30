package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

import ch.javasoft.decimal.truncate.DecimalRounding;

import com.google.common.math.DoubleMath;

public class CheckedScale0fRoundingArithmetics extends
		AbstractCheckedScale0fArithmetics {

	private final DecimalRounding rounding;

	public CheckedScale0fRoundingArithmetics(RoundingMode roundingMode) {
		this(DecimalRounding.valueOf(roundingMode));
	}
	
	public CheckedScale0fRoundingArithmetics(DecimalRounding rounding) {
		this.rounding = rounding;
	}
	
	@Override
	public RoundingMode getRoundingMode() {
		return rounding.getRoundingMode();
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int n) {
		return Pow10.divideByPowerOf10(rounding, uDecimal, n);
	}

	@Override
	public long sqrt(long uDecimal) {
		return Sqrt.sqrtLong(rounding, uDecimal);
	}

	@Override
	public long pow(long uDecimalBase, int exponent) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long round(long uDecimal, int precision) {
		return Round.round(this, rounding, uDecimal, precision);
	}

	@Override
	public long fromDouble(double value) {
		// FIXME should be in a helper like checkExtremalDoubleValue(value)
		if (Double.isNaN(value) || Double.isInfinite(value)) {
			throw new NumberFormatException("cannot convert double to long: " + value);
		}
		
		return DoubleMath.roundToLong(value, getRoundingMode());
	}

	@Override
	public long shiftLeft(long uDecimal, int n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long shiftRight(long uDecimal, int n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		// TODO Auto-generated method stub
		return 0;
	}

}
