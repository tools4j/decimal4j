package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.Scale;

/**
 * An arithmetic implementation which rounds the last scale digit away from zero
 * (see {@link RoundingMode#UP}). The result of an operation that leads to an
 * overflow is silently truncated.
 */
public class RoundUpDecimalArithmetics extends
		AbstractRoundingDecimalArithmetics {

	/**
	 * Constructor for silent decimal arithmetics with given scale,
	 * {@link RoundingMode#UP UP} rounding mode and {@link OverflowMode#SILENT
	 * SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative
	 */
	public RoundUpDecimalArithmetics(int scale) {
		super(scale, RoundingMode.UP);
	}
	/**
	 * Constructor for silent decimal arithmetics with given scale,
	 * {@link RoundingMode#UP UP} rounding mode and {@link OverflowMode#SILENT
	 * SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative
	 */
	public RoundUpDecimalArithmetics(Scale scale) {
		this(scale.getFractionDigits());
	}

	@Override
	public DecimalArithmetics derive(int scale) {
		return scale == getScale() ? this : new RoundUpDecimalArithmetics(scale);
	}

	@Override
	protected int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
		if (firstTruncatedDigit > 0 || !zeroAfterFirstTruncatedDigit) {
			return truncatedValue < 0 ? -1 : 1;
		}
		return 0;
	}

}
