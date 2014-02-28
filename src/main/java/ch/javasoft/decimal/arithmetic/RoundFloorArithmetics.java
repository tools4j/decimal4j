package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.Scale;

public class RoundFloorArithmetics extends
		AbstractRoundingArithmetics {

	/**
	 * Constructor for silent decimal arithmetics with given scale,
	 * {@link RoundingMode#FLOOR FLOOR} rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative
	 */
	public RoundFloorArithmetics(int scale) {
		super(scale, RoundingMode.FLOOR);
	}
	/**
	 * Constructor for silent decimal arithmetics with given scale,
	 * {@link RoundingMode#FLOOR FLOOR} rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative
	 */
	public RoundFloorArithmetics(Scale scale) {
		this(scale.getFractionDigits());
	}

	@Override
	public DecimalArithmetics derive(int scale) {
		return scale == getScale() ? this : new RoundFloorArithmetics(scale);
	}

	@Override
	protected int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
		if (truncatedValue < 0) {
			if (firstTruncatedDigit > 0 || !zeroAfterFirstTruncatedDigit) {
				return -1;
			}
		}
		return 0;
	}

}
