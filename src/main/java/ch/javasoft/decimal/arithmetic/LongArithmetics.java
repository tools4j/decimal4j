package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.Scale.Scale0f;

/**
 * The special case for longs with {@link Scale0f} and no rounding.
 */
public class LongArithmetics extends AbstractArithmetics {
	
	/**
	 * Default singleton instance
	 */
	public static final LongArithmetics INSTANCE = new LongArithmetics();

	public int getScale() {
		return 0;
	}

	public RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	public DecimalArithmetics derive(int scale) {
		return scale == 0 ? this : new TruncatingArithmetics(scale);
	}

	public DecimalArithmetics derive(RoundingMode roundingMode) {
		// TODO impl rounding version of this
		throw new RuntimeException("not implemented with rounding for scale " + + getScale());
	}

	public DecimalArithmetics derive(OverflowMode overflowMode) {
		return overflowMode == OverflowMode.SILENT ? this : new ExceptionOnOverflowArithmetics(this);
	}

	public long one() {
		return 1L;
	}

	public long multiply(long uDecimal1, long uDecimal2) {
		return uDecimal1 * uDecimal2;
	}

	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return uDecimalDividend / uDecimalDivisor;
	}

	@Override
	public long fromLong(long value) {
		return value;
	}

	@Override
	public long fromDouble(double value) {
		return (long)value;
	}

	public long fromBigInteger(BigInteger value) {
		return value.longValue();
	}

	public long fromBigDecimal(BigDecimal value) {
		return value.longValue();
	}

	public long fromUnscaled(long unscaledValue, int scale) {
		while (scale > 0) {
			unscaledValue /= 10;
			scale--;
		}
		while (scale < 0) {
			unscaledValue *= 10;
			scale++;
		}
		return unscaledValue;
	}

	public long parse(String value) {
		return Long.parseLong(value);
	}

	@Override
	public long toLong(long uDecimal) {
		return uDecimal;
	}

	@Override
	public double toDouble(long uDecimal) {
		return (double)uDecimal;
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal) {
		return BigDecimal.valueOf(uDecimal);
	}

	@Override
	public String toString(long uDecimal) {
		return Long.toString(uDecimal);
	}

}
