package ch.javasoft.decimal.arithmetic;

/**
 * Calculates powers of a decimal.
 */
final class Pow {

	/**
	 * Constant for {@code floor(sqrt(Long.MAX_VALUE))}
	 */
	private static final long FLOOR_SQRT_MAX_LONG = 3037000499L;

	public static long pow(DecimalArithmetics arith, long uDecimal, int exponent) {
		if (exponent == 0) {
			return arith.one();
		}
		long base;
		long exp;//long to hold -Integer.MIN_VALUE
		if (exponent > 0) {
			base = uDecimal;
			exp = exponent;
		} else {/* exponent < 0 */
			base = arith.invert(uDecimal);
			exp = -exponent;
		}
		long result = base;
		//TODO eliminate repeated truncation with multiplications in loop
		while (exp != 1 && result != 0) {
			if (exp % 2 == 0) {
				//even
				result = arith.square(result);
				exp >>>= 1;
			} else {
				//odd
				result = arith.multiply(result, base);
				exp--;
			}
		}
		return result;
	}
	public static long powChecked(DecimalArithmetics arith, long uDecimalBase, int exponent) {
		if (exponent == 0) {
			return 1;
		}
		if (exponent < 0) {
			if (uDecimalBase == 1 | uDecimalBase == -1) {
				return uDecimalBase;
			}
			if (uDecimalBase != 0) {
				return 0;
			}
			throw new ArithmeticException("division by zero: " + arith.toString(uDecimalBase) + "^" + exponent);
		}
		if (uDecimalBase >= -2 & uDecimalBase <= 2) {
			switch ((int) uDecimalBase) {
			case 0:
				return (exponent == 0) ? 1 : 0;
			case 1:
				return 1;
			case (-1):
				return ((exponent & 1) == 0) ? 1 : -1;
			case 2:
				if (exponent >= Long.SIZE - 1) {
					throw new ArithmeticException("overflow: " + arith.toString(uDecimalBase) + "^" + exponent);
				}
				return 1L << exponent;
			case (-2):
				if (exponent >= Long.SIZE) {
					throw new ArithmeticException("overflow: " + arith.toString(uDecimalBase) + "^" + exponent);
				}
				return ((exponent & 1) == 0) ? (1L << exponent) : (-1L << exponent);
			default:
				throw new AssertionError();
			}
		}
		long accum = 1;
		while (true) {
			switch (exponent) {
			case 0:
				return accum;
			case 1:
				return arith.multiplyByLong(accum, uDecimalBase);
			default:
				if ((exponent & 1) != 0) {
					accum = arith.multiplyByLong(accum, uDecimalBase);
				}
				exponent >>= 1;
				if (exponent > 0) {
					if (uDecimalBase > FLOOR_SQRT_MAX_LONG) {
						throw new ArithmeticException("overflow: " + arith.toString(uDecimalBase) + "^" + exponent);
					}
					uDecimalBase *= uDecimalBase;
				}
			}
		}
	}

	//no instances
	private Pow() {
		super();
	}
}
