package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.Scale.Scale0f;

/**
 * The special case for longs with {@link Scale0f} and rounding.
 */
public class RoundingLongArithmetics extends LongArithmetics {
	
	private final DecimalRounding rounding;
	
	public RoundingLongArithmetics(RoundingMode roundingMode) {
		this(DecimalRounding.valueOf(roundingMode));
	}
	public RoundingLongArithmetics(DecimalRounding rounding) {
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
		return scale == 0 ? this : new RoundingArithmetics(scale, getDecimalRounding());
	}

	@Override
	public DecimalArithmetics derive(RoundingMode roundingMode) {
		if (roundingMode == getRoundingMode()) {
			return this;
		}
		if (roundingMode == RoundingMode.DOWN) {
			return LongArithmetics.INSTANCE;
		}
		return new RoundingLongArithmetics(roundingMode);
	}

	@Override
	public DecimalArithmetics derive(OverflowMode overflowMode) {
		if (overflowMode == getOverflowMode()) {
			return this;
		}
		return new ExceptionOnOverflowArithmetics(this);
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		final long unrounded = uDecimalDividend / uDecimalDivisor;
		final long product = unrounded * uDecimalDivisor;
		final long delta = uDecimalDividend - product;
		if (delta != 0) {
			if (unrounded != 0) {
				return unrounded + rounding.calculateRoundingIncrementForDivision(unrounded, delta, uDecimalDivisor);
			}
			return Long.signum(uDecimalDividend) * Long.signum(uDecimalDivisor) * rounding.calculateRoundingIncrementForDivision(unrounded, delta, uDecimalDivisor);
		}
		return unrounded;
	}

	@Override
	public long invert(long uDecimal) {
		//special cases first
		if (uDecimal == 0) {
			throw new ArithmeticException("divide by zero");
		} else if (uDecimal == 1) {
			return 1;
		} else if (uDecimal == -1) {
			return -1;
		}
		final long abs = Math.abs(uDecimal);
		final int firstTruncatedDigit = (int)(10 / abs);
		final long remainder = 10 - abs * firstTruncatedDigit;
		return Long.signum(uDecimal) * rounding.calculateRoundingIncrement(0, firstTruncatedDigit, remainder == 0);
	}

	@Override
	public long pow(long uDecimal, int exponent) {
		//FIXME implement with rounding (not only on multiplications!)
		return super.pow(uDecimal, exponent);
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		return value.setScale(0, getRoundingMode()).longValue();
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		if (scale == 0) {
			return fromLong(unscaledValue);
		}
		long result = unscaledValue;
		if (scale < 0) {
			for (int i = scale; i < 0; i++) {
				result *= 10;
			}
		} else if (scale > 0) {
			int lastDigit = 0;
			boolean zeroAfterLastDigit = true;
			for (int i = 0; i < scale; i++) {
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
	public long toLong(long uDecimal) {
		return uDecimal;
	}

}
