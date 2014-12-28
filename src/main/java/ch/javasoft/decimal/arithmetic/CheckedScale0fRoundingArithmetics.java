package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

import ch.javasoft.decimal.truncate.DecimalRounding;

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
	public long invert(long uDecimal) {
		// special cases first
		if (uDecimal == 0) {
			throw new ArithmeticException("Division by zero: " + uDecimal
					+ "^-1");
		}
		if (uDecimal == 1) {
			return 1;
		}
		if (uDecimal == -1) {
			return -1;
		}
		
		return divide(one(), uDecimal);
	}

	@Override
	public final long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Div.divideChecked(this, rounding, uDecimalDividend, uDecimalDivisor);
	}
	
	@Override
	public long multiplyByPowerOf10(long uDecimal, int n) {
		return Pow10.multiplyByPowerOf10Checked(this, uDecimal, n);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int n) {
		return Pow10.divideByPowerOf10Checked(this, rounding, uDecimal, n);
	}

	@Override
	public long sqrt(long uDecimal) {
		return Sqrt.sqrtLong(rounding, uDecimal);
	}

	@Override
	public long pow(long uDecimalBase, int exponent) {
		return Pow.powLongChecked(this, rounding, uDecimalBase, exponent);
	}

	@Override
	public long round(long uDecimal, int precision) {
		return Round.round(this, rounding, uDecimal, precision);
	}

	@Override
	public long fromDouble(double value) {
		return DoubleConversion.doubleToLong(rounding, value);
	}

	@Override
	public long shiftLeft(long uDecimal, int n) {
		return Shift.shiftLeftChecked(this, rounding, uDecimal, n);
	}

	@Override
	public long shiftRight(long uDecimal, int n) {
		return Shift.shiftRightChecked(this, rounding, uDecimal, n);
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		return Scale.rescale(this, unscaledValue, scale, getScale());
	}

}
