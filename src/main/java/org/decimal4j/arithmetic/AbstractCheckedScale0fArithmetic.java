package org.decimal4j.arithmetic;

import org.decimal4j.scale.Scale0f;

/**
 * Base class for arithmetic implementations with overflow check for the special
 * case with {@link Scale0f}, that is, for longs.
 */
abstract public class AbstractCheckedScale0fArithmetic extends
		AbstractCheckedArithmetic {

	@Override
	public final Scale0f getScaleMetrics() {
		return Scale0f.INSTANCE;
	}

	@Override
	public final int getScale() {
		return 0;
	}

	@Override
	public final long one() {
		return 1;
	}

	@Override
	public final long multiply(long uDecimal1, long uDecimal2) {
		return Checked.multiplyByLong(this, uDecimal1, uDecimal2);
	}
	
	@Override
	public final long square(long uDecimal) {
		return Checked.multiplyByLong(this, uDecimal, uDecimal);
	}
	
	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return Checked.divideByLong(this, uDecimalDividend, uDecimalDivisor);
	}
	
	@Override
	public final long avg(long a, long b) {
		return Avg.avg(a, b);
	}

	@Override
	public final long fromLong(long value) {
		return value;
	}

	@Override
	public final long toLong(long uDecimal) {
		return uDecimal;
	}

	@Override
	public final String toString(long uDecimal) {
		return Long.toString(uDecimal);
	}

}
