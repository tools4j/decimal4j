package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ch.javasoft.decimal.scale.Scale0f;
import ch.javasoft.decimal.truncate.DecimalRounding;

/**
 * The special case for longs with {@link Scale0f} and rounding.
 */
public class UncheckedScale0fRoundingArithmetics extends AbstractUncheckedScale0fArithmetics {

	private final DecimalRounding rounding;

	public UncheckedScale0fRoundingArithmetics(RoundingMode roundingMode) {
		this(DecimalRounding.valueOf(roundingMode));
	}

	public UncheckedScale0fRoundingArithmetics(DecimalRounding rounding) {
		this.rounding = rounding;
	}

	@Override
	public final RoundingMode getRoundingMode() {
		return rounding.getRoundingMode();
	}

	@Override
	public long divideByLong(long uDecimalDividend, long lDivisor) {
		return Div.divideByLong(rounding, uDecimalDividend, lDivisor);
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Div.divideByLong(rounding, uDecimalDividend, uDecimalDivisor);
	}

	@Override
	public long invert(long uDecimal) {
		//special cases first
		if (uDecimal == 0) {
			throw new ArithmeticException("Division by zero: " + uDecimal + "^-1");
		}
		if (uDecimal == 1) {
			return 1;
		}
		if (uDecimal == -1) {
			return -1;
		}
		return RoundingUtil.calculateRoundingIncrementForDivision(rounding, 0, 1, uDecimal);
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		return Shift.shiftLeft(rounding, uDecimal, positions);
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		return Shift.shiftRight(rounding, uDecimal, positions);
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int positions) {
		return Pow10.divideByPowerOf10(rounding, uDecimal, positions);
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int positions) {
		return Pow10.multiplyByPowerOf10(rounding, uDecimal, positions);
	}

	@Override
	public long sqrt(long uDecimal) {
		return Sqrt.sqrtLong(rounding, uDecimal);
	}

	@Override
	public long pow(long uDecimal, int exponent) {
		return Pow.powLong(this, rounding, uDecimal, exponent);
	}

	@Override
	public long round(long uDecimal, int precision) {
		return Round.round(this, rounding, uDecimal, precision);
	}

	@Override
	public final long fromUnscaled(long unscaledValue, int scale) {
		return Pow10.divideByPowerOf10(rounding, unscaledValue, scale);
	}

//	@Override
//	public long fromDouble(double value) {
//		// FIXME impl with rounding
//		return (long) value;
//	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		return value.setScale(0, getRoundingMode()).longValue();
	}
}
