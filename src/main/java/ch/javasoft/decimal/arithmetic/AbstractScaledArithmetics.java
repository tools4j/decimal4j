package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.Scale;

/**
 * Base class for arithmetic implementations implementing those functions where
 * rounding is no issue. Overflow is not checked, that is,
 * {@link #getOverflowMode()} returns {@link OverflowMode#SILENT SILENT}.
 */
abstract public class AbstractScaledArithmetics extends AbstractArithmetics {

	private final int scale;
	private final long one;//10^scale
	
	private transient BigInteger oneBigInteger;
	private transient BigDecimal oneBigDecimal;

	/**
	 * Constructor for silent decimal arithmetics with given scale, truncating
	 * {@link RoundingMode#DOWN DOWN} rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative, zero or uneven
	 */
	public AbstractScaledArithmetics(int scale) {
		if (scale < 1) {
			throw new IllegalArgumentException("scale cannot be zero or negative: " + scale);
		}
		if (scale > 18) {
			throw new IllegalArgumentException("scale is too large: " + scale);
		}
		this.scale = scale;
		long one = 1;
		for (int i = 0; i < scale; i++) {
			one *= 10;
		}
		if (Long.MAX_VALUE / one < one) {
			//one * one must still fit in a long for our computations in this class
			throw new IllegalArgumentException("scale is too large: " + scale);
		}
		this.one = one;
	}

	/**
	 * Constructor for silent decimal arithmetics with given scale, truncating
	 * {@link RoundingMode#DOWN DOWN} rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative or uneven
	 */
	public AbstractScaledArithmetics(Scale scale) {
		this(scale.getFractionDigits());
	}

	@Override
	public int getScale() {
		return scale;
	}

	@Override
	public long one() {
		return one;
	}
	
	protected BigInteger oneBigInteger() {
		if (oneBigInteger == null) {
			oneBigInteger = BigInteger.valueOf(one());
		}
		return oneBigInteger;
	}
	protected BigDecimal oneBigDecimal() {
		if (oneBigDecimal == null) {
			oneBigDecimal = BigDecimal.valueOf(one());
		}
		return oneBigDecimal;
	}

	@Override
	public long fromBigInteger(BigInteger value) {
		return value.multiply(oneBigInteger()).longValue();
	}

}
