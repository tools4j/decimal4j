package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.Scale;

/**
 * An arithmetic implementation which rounds the last scale digit towards
 * {@literal "nearest neighbor"} unless both neighbors are equidistant, in which
 * case round up. Behaves as for {@link RoundingMode#UP} if the discarded
 * fraction is &ge; 0.5; otherwise, behaves as for {@link RoundingMode#DOWN}.
 * Note that this is the rounding mode commonly taught at school.
 * <p>
 * The result of an operation that leads to an overflow is silently truncated.
 * 
 * @see RoundingMode#HALF_UP
 */
public class RoundHalfUpDecimalArithmetics extends
		AbstractRoundingDecimalArithmetics {

	/**
	 * Constructor for silent decimal arithmetics with given scale,
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative
	 */
	public RoundHalfUpDecimalArithmetics(int scale) {
		super(scale, RoundingMode.HALF_UP);
	}
	/**
	 * Constructor for silent decimal arithmetics with given scale,
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative
	 */
	public RoundHalfUpDecimalArithmetics(Scale scale) {
		this(scale.getFractionDigits());
	}

	@Override
	public DecimalArithmetics derive(int scale) {
		return scale == getScale() ? this : new RoundHalfUpDecimalArithmetics(scale);
	}

	@Override
	protected int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
		if (firstTruncatedDigit >= 5) {
			return truncatedValue < 0 ? -1 : 1;
		}
		return 0;
	}

}
