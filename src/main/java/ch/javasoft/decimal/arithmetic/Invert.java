package ch.javasoft.decimal.arithmetic;

import ch.javasoft.decimal.truncate.DecimalRounding;

final class Invert {

	public static long invertLong(long uDecimal) {
		if (uDecimal == 0) {
			throw new ArithmeticException("Division by zero: " + uDecimal + "^-1");
		}
		if (uDecimal == 1) {
			return 1;
		}
		if (uDecimal == -1) {
			return -1;
		}
		return 0;
	}

	public static long invertLong(DecimalRounding rounding, long uDecimal) {
		//special cases first
		if (uDecimal == 0) {
			throw new ArithmeticException("Division by zero: " + uDecimal + "^-1");
		}
		if (uDecimal == 1) {
			return 1;
		}
		if (uDecimal == -1) {
			return -1;
		}
		return RoundingUtil.calculateRoundingIncrementForDivision(rounding, 0, 1, uDecimal);
	}
	
	public static long invert(DecimalArithmetics arith, long uDecimal) {
		//special cases are handled by divide
		return arith.divide(arith.one(), uDecimal);
	}

	public static long invert(DecimalArithmetics arith, DecimalRounding rounding, long uDecimal) {
		//special cases are handled by divide
		return arith.divide(arith.one(), uDecimal);
	}

	//no instances
	private Invert() {
		super();
	}
}
