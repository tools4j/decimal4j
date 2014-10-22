package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

/**
 * Computes the average of two numbers, that is, {@code (a+b)/2}.
 */
final class Avg {

	public static long avg(long a, long b) {
		final long xor = a ^ b;
		final long floor = (a & b) + (xor >> 1);
		return floor + ((floor >>> 63) & xor);
	}
	public static long avg(DecimalArithmetics arith, long a, long b) {
		final RoundingMode roundingMode = arith.getRoundingMode();
		final long xor = a ^ b;
		switch (roundingMode) {
		case FLOOR: {
			return (a & b) + (xor >> 1);
		}
		case CEILING: {
			return (a | b) - (xor >> 1);
		}
		case DOWN://fallthrough
		case HALF_DOWN: {
			final long floor = (a & b) + (xor >> 1);
			return floor + ((floor >>> 63) & xor);
		}
		case UP://fallthrough
		case HALF_UP: {
			final long floor = (a & b) + (xor >> 1);
			return floor + ((~floor >>> 63) & xor);
		}
		case HALF_EVEN: {
			final long xorShifted = xor >> 1;
			final long floor = (a & b) + xorShifted;
			//use ceiling if floor is odd
			return ((floor & 0x1) == 0) ? floor : (a | b) - xorShifted;
		}
		case UNNECESSARY: {
			final long floor = (a & b) + (xor >> 1);
			if ((xor & 0x1) != 0) {
				throw new ArithmeticException("rounding necessary: " + arith.toString(a) + " avg " + arith.toString(b) + " = " + arith.toString(floor));
			}
			return floor;
		}
		default: {
			//should not get here
			throw new IllegalArgumentException("unsupported rounding mode: " + roundingMode);
		}}
	}

	// no instances
	private Avg() {
		super();
	}
}
