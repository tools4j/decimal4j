package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.scale.Scale0f;

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
			SpecialDivisionResult.DIVISOR_IS_ZERO.divide(this, 1, uDecimal);
		}
		if (uDecimal == 1) {
			return 1;
		}
		if (uDecimal == -1) {
			return -1;
		}
		return rounding.calculateRoundingIncrementForDivision(0, 1, uDecimal);
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
		//FIXME implement with rounding (not only on multiplications!)
		return super.pow(uDecimal, exponent);
	}

//	@Override
//	public long fromDouble(double value) {
//		// FIXME impl with rounding
//		return (long) value;
//	}

	@Override
	public long fromBigInteger(BigInteger value) {
		return value.longValue();
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		return value.setScale(0, getRoundingMode()).longValue();
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		if (scale == 0 | unscaledValue == 0) {
			return unscaledValue;
		}
		return Pow10.divideByPowerOf10(rounding, unscaledValue, scale);
	}

	@Override
	public long parse(String value) {
		return Long.parseLong(value);
	}

	@Override
	public String toString(long uDecimal) {
		return Long.toString(uDecimal);
	}
}
