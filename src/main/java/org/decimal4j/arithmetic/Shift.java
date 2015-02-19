package org.decimal4j.arithmetic;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;


/**
 * Provides methods for left and right shifts.
 */
final class Shift {

	public static long shiftLeft(DecimalRounding rounding, long uDecimal, int positions) {
		if (positions >= 0) {
			return positions < Long.SIZE ? uDecimal << positions : 0; 
		}
		//one shift missing for (-Integer.MIN_VALUE) but does not matter as result is always between 0 (incl) and 0.5 (excl)
		return shiftRight(rounding, uDecimal, -positions > 0 ? -positions : Integer.MAX_VALUE);
	}

	public static long shiftRight(DecimalRounding rounding, long uDecimal, int positions) {
		if (uDecimal == 0 | positions == 0) {
			return uDecimal;
		}
		if (positions >= 0) {
			if (rounding == DecimalRounding.FLOOR) {
				return positions < Long.SIZE ? uDecimal >> positions : (uDecimal >= 0 ? 0 : -1); 
			}
			if (positions < Long.SIZE) {
				final long truncated = uDecimal >= 0 ? (uDecimal >>> positions) : -(-uDecimal >>> positions);
				final long remainder = uDecimal - (truncated << positions);
				final TruncatedPart truncatedPart = positions == 63 ? RoundingUtil.truncatedPartFor2pow63(remainder) : RoundingUtil.truncatedPartFor(Math.abs(remainder), 1L << positions);
				return truncated + rounding.calculateRoundingIncrement(Long.signum(uDecimal), truncated, truncatedPart);
			}
			if (positions == Long.SIZE) {
				return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, RoundingUtil.truncatedPartFor2pow64(Math.abs(uDecimal)));
			}
			return rounding.calculateRoundingIncrement(Long.signum(uDecimal), 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
		}
		return positions > -Long.SIZE ? uDecimal << -positions : 0; 
	}

	public static long shiftLeftChecked(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal, int positions) {
		if (positions >= 0) {
			if (uDecimal == 0 | positions == 0) {
				return uDecimal;
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
		//one shift missing for (-Integer.MIN_VALUE) but does not matter as result is always between 0 (incl) and 0.5 (excl)
		return shiftRight(rounding, uDecimal, -positions > 0 ? -positions : Integer.MAX_VALUE);
	}


	public static long shiftRightChecked(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal, int positions) {
		if (uDecimal == 0) {
			return 0;
		}
		if (positions >= 0) {
			return shiftRight(rounding, uDecimal, positions);
		}
		if (positions > -Long.SIZE) {
			try {
				return shiftLeftChecked(arith, rounding, uDecimal, -positions);
			} catch (ArithmeticException e) {
				//ignore, throw again below with correct shift direction
			}
		}
		throw new ArithmeticException("Overflow: " + arith.toString(uDecimal) + " >> " + positions + " = " + arith.toString(uDecimal >> positions));
	}

	//no instances
	private Shift() {
		super();
	}
}
