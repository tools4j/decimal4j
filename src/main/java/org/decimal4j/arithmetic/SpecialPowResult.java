package org.decimal4j.arithmetic;

import org.decimal4j.api.DecimalArithmetic;

/**
 * Helper class used by pow to handle some special cases.
 */
enum SpecialPowResult {
	/**
	 * {@code a^n} with {@code n==0} leading to {@code 1}
	 */
	EXPONENT_IS_ZERO {
		@Override
		long pow(DecimalArithmetic arithmetic, long uDecimal, int exponent) {
			return arithmetic.one();//yes 0^0 is also 1
		}
	},
	/**
	 * {@code a^n} with {@code n==1} leading to {@code a}
	 */
	EXPONENT_IS_ONE {
		@Override
		long pow(DecimalArithmetic arithmetic, long uDecimal, int exponent) {
			return uDecimal;
		}
	},
	/**
	 * {@code a^n} with {@code a==0} leading to {@code 0} if {@code n>=0} and to
	 * an arithmetic exception if {@code n<0}
	 */
	BASE_IS_ZERO {
		@Override
		long pow(DecimalArithmetic arithmetic, long uDecimal, int exponent) {
			if (exponent >= 0) {
				//uDecimal == 0 should never happen (0^0 is usually defined as 1)
				return 0;
			}
			throw new ArithmeticException("Division by zero: " + arithmetic.toString(uDecimal) + "^" + exponent);
		}
	},
	/**
	 * {@code a^n} with {@code a==1} leading to {@code 1}
	 */
	BASE_IS_ONE {
		@Override
		long pow(DecimalArithmetic arithmetic, long uDecimal, int exponent) {
			return uDecimal;//uDecimal is 1
		}
	},
	/**
	 * {@code a^n} with {@code a==-1} leading to {@code 1} if {@code n} is even
	 * and to {@code -1} if {@code n} is odd.
	 */
	BASE_IS_MINUS_ONE {
		@Override
		long pow(DecimalArithmetic arithmetic, long uDecimal, int exponent) {
			return ((exponent & 0x1) == 0) ? -uDecimal : uDecimal;//uDecimal is one and it's negation cannot overflow
		}
	},
	/**
	 * {@code a^n} with {@code n==-1} leading to {@code 1/a}
	 */
	EXPONENT_IS_MINUS_ONE {
		@Override
		long pow(DecimalArithmetic arithmetic, long uDecimal, int exponent) {
			return arithmetic.invert(uDecimal);
		}
	},
	/**
	 * {@code a^n} with {@code n==2} leading to {@code square(a)}
	 */
	EXPONENT_IS_TWO {
		@Override
		long pow(DecimalArithmetic arithmetic, long uDecimal, int exponent) {
			return arithmetic.square(uDecimal);
		}
	};
	
	abstract long pow(DecimalArithmetic arithmetic, long uDecimal, int exponent);

	/**
	 * Returns the special power case if it is one and null otherwise.
	 * 
	 * @param arithmetic
	 *            the arithmetic object
	 * @param uDecimal
	 *            the base
	 * @param n
	 *            the exponent
	 * @return the special case if it is one and null otherwise
	 */
	static SpecialPowResult getFor(DecimalArithmetic arithmetic, long uDecimal, long n) {
		if (n == 0) {
			return EXPONENT_IS_ZERO;
		}
		if (n == 1) {
			return EXPONENT_IS_ONE;
		}
		if (uDecimal == 0) {
			return BASE_IS_ZERO;
		}
		final long one = arithmetic.one();
		if (uDecimal == one) {
			return BASE_IS_ONE;
		}
		if (uDecimal == -one) {
			return BASE_IS_MINUS_ONE;
		}
		if (n == -1) {
			return EXPONENT_IS_MINUS_ONE;
		}
		if (n == 2) {
			return EXPONENT_IS_TWO;
		}
		return null;
	}
}