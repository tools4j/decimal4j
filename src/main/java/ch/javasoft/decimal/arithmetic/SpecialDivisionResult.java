package ch.javasoft.decimal.arithmetic;

enum SpecialDivisionResult {
	DIVIDEND_IS_ZERO {
		@Override
		long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
			return 0;
		}
	},
	DIVISOR_IS_ZERO {
		@Override
		long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
			throw new ArithmeticException("division by zero: " + arithmetics.toString(uDecimalDividend) + "/" + arithmetics.toString(uDecimalDivisor));
		}
	},
	DIVISOR_IS_ONE {
		@Override
		long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
			return uDecimalDividend;
		}
	},
	DIVISOR_IS_MINUS_ONE {
		@Override
		long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
			return -uDecimalDividend;
		}
	},
	DIVISOR_EQUALS_DIVIDEND {
		@Override
		long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
			return arithmetics.one();
		}
	},
	DIVISOR_EQUALS_MINUS_DIVIDEND {
		@Override
		long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
			return -arithmetics.one();
		}
	};
	abstract long divide(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor);

	static SpecialDivisionResult getFor(DecimalArithmetics arithmetics, long uDecimalDividend, long uDecimalDivisor) {
		//special cases first
		if (uDecimalDividend == 0) {
			return DIVIDEND_IS_ZERO;
		}
		if (uDecimalDivisor == 0) {
			return DIVISOR_IS_ZERO;
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