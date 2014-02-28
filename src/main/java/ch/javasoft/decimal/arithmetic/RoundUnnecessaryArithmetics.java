package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.Scale;

public class RoundUnnecessaryArithmetics extends AbstractRoundingArithmetics {

	/**
	 * Constructor for silent decimal arithmetics with given scale,
	 * {@link RoundingMode#UNNECESSARY UNNECESSARY} rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative
	 */
	public RoundUnnecessaryArithmetics(int scale) {
		super(scale, RoundingMode.UNNECESSARY);
	}
	/**
	 * Constructor for silent decimal arithmetics with given scale,
	 * {@link RoundingMode#UNNECESSARY UNNECESSARY} rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative
	 */
	public RoundUnnecessaryArithmetics(Scale scale) {
		this(scale.getFractionDigits());
	}

	@Override
	public DecimalArithmetics derive(int scale) {
		return scale == getScale() ? this : new RoundUnnecessaryArithmetics(scale);
	}

	@Override
	protected int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
		if (firstTruncatedDigit != 0 || !zeroAfterFirstTruncatedDigit) {
			throw new ArithmeticException("rounding necessary for: " + truncatedValue + "." + firstTruncatedDigit + (zeroAfterFirstTruncatedDigit ? "" : "..."));
		}
		return 0;
	}

}
