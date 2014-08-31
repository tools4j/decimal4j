package ch.javasoft.decimal.arithmetic;

/**
 * Helper class used by division and inversion to handle some special cases. 
 */
enum SpecialDivisionResult {
	/**
	 *{@code a/b} with {@code a==0, b!=0} leading to {@code 0/b=0} 
	 */
	DIVIDEND_IS_ZERO {
		@Override
		long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
			return 0;
		}
	},
	/**
	 *{@code a/b} with {@code b==0} leading to an arithmetic exception 
	 */
	DIVISOR_IS_ZERO {
		@Override
		long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
			throw new ArithmeticException("division by zero: " + arithmetics.toString(uDecimalDividend) + "/" + arithmetics.toString(uDecimalDivisor));
		}
	},
	/**
	 *{@code a/b} with {@code b==1} leading to {@code a/1=a} 
	 */
	DIVISOR_IS_ONE {
		@Override
		long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
			return uDecimalDividend;
		}
	},
	/**
	 *{@code a/b} with {@code b==-1} resulting in {@code a/-1=-a} 
	 */
	DIVISOR_IS_MINUS_ONE {
		@Override
		long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
			return -uDecimalDividend;
		}
	},
	/**
	 *{@code a/b} with {@code a==b} resulting in {@code a/a=b/b=1} 
	 */
	DIVISOR_EQUALS_DIVIDEND {
		@Override
		long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
			return arithmetics.one();
		}
	},
	/**
	 *{@code a/b} with {@code a==-b} resulting in {@code a/-a=-b/b=-1} 
	 */
	DIVISOR_EQUALS_MINUS_DIVIDEND {
		@Override
		long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
			return -arithmetics.one();
		}
	};
	abstract long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor);

	static SpecialDivisionResult getFor(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
		//special cases first
		if (uDecimalDivisor == 0) {
			return DIVISOR_IS_ZERO;
		}
		if (uDecimalDividend == 0) {
			return DIVIDEND_IS_ZERO;
		}
		final long one = arithmetics.one();
		if (uDecimalDivisor == one) {
			return DIVISOR_IS_ONE;
		}
		if (uDecimalDivisor == -one) {
			return DIVISOR_IS_MINUS_ONE;
		}
		if (uDecimalDividend == uDecimalDivisor) {
			return DIVISOR_EQUALS_DIVIDEND;
		}
		if (uDecimalDividend == -uDecimalDivisor) {
			return DIVISOR_EQUALS_MINUS_DIVIDEND;
		}
		return null;
	}
}