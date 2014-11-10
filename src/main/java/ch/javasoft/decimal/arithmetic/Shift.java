package ch.javasoft.decimal.arithmetic;

import ch.javasoft.decimal.truncate.DecimalRounding;
import ch.javasoft.decimal.truncate.TruncatedPart;


/**
 * Calculates left and right shifts.
 */
final class Shift {

	public static long shiftLeft(DecimalRounding rounding, long uDecimal, int positions) {
		if (uDecimal == 0 | positions == 0) {
			return uDecimal;
		}
		if (positions < 0) {
			if (positions > -63) {
				return shiftRight(rounding, uDecimal, -positions);
			}
			return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
		return uDecimal << positions;
	}

	public static long shiftRight(DecimalRounding rounding, long uDecimal, int positions) {
		if (uDecimal == 0 | positions == 0) {
			return uDecimal;
		}
		if (positions > 0) {
			//rounding may be necessary
			if (positions < 63) {
				final long truncated = uDecimal >> positions;
				final long remainder = uDecimal - (truncated << positions);
				final TruncatedPart truncatedPart = RoundingUtil.truncatedPartFor(Math.abs(remainder), 1L << positions);
				return truncated + rounding.calculateRoundingIncrement(Long.signum(uDecimal), truncated, truncatedPart);
			}
			return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
		//shift left, no rounding
		return uDecimal >> positions;
	}

	public static long shiftLeftChecked(DecimalArithmetics arith, long uDecimal, int positions) {
		if (uDecimal == 0) {
			return 0;
		}
		if (positions <= 0) {
			if (positions > -64) {
				return uDecimal >> -positions;
			}
			return 0;
		}
		if (positions < Long.SIZE) {
			if (uDecimal > 0) {
				if (positions < Long.SIZE - 1) {
					final int leadingZeros = Long.numberOfLeadingZeros(uDecimal);
					if (leadingZeros > positions) {
						return uDecimal << positions;
					}
				}
			} else if (uDecimal > Long.MIN_VALUE) {
				final int leadingZeros = Long.numberOfLeadingZeros(~uDecimal);
				if (leadingZeros > positions) {
					return uDecimal << positions;
				}
			}
		}
		throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " << " + positions + " = " + arith.toString(uDecimal << positions));
	}


	public static long shiftRightChecked(DecimalArithmetics arith, long uDecimal, int positions) {
		if (uDecimal == 0) {
			return 0;
		}
		if (positions >= 0) {
			return uDecimal >> positions;
		}
		if (positions > -Long.SIZE) {
			return shiftLeftChecked(arith, uDecimal, -positions);
		}
		throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " >> " + positions + " = " + arith.toString(uDecimal >> positions));
	}

	//no instances
	private Shift() {
		super();
	}
}
