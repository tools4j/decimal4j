package ch.javasoft.decimal.jmh;

import java.math.RoundingMode;
import java.util.Random;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.factory.Factories;
import ch.javasoft.decimal.scale.ScaleMetrics;

class PowUtil {
	
	private static final Random RND = new Random();

	public static <S extends ScaleMetrics> Decimal<S> randomBaseOperand(S scaleMetrics) {
		final long one = scaleMetrics.getScaleFactor();
//		final long unscaled = one * (4 - RND.nextInt(9)) + one - randomLong(2*one + 1);
		final long unscaled = one * (8 - RND.nextInt(17)) + one - randomLong(2*one + 1);
		return Factories.valueOf(scaleMetrics).createImmutable(unscaled);
	}

	public static <S extends ScaleMetrics> int randomExponentForBase(Decimal<S> decimalOperand, int maxExponent) {
		if (decimalOperand.isZero()) {
			return RND.nextInt(maxExponent + 1);
		}
		if (decimalOperand.isOne() || decimalOperand.isMinusOne()) {
			return maxExponent - RND.nextInt(2 * maxExponent + 1);
		}
		final boolean posExp = RND.nextBoolean();
		final double absBase;
		if (posExp) {
			absBase = Math.abs(decimalOperand.doubleValue(RoundingMode.UP));
		} else {
			absBase = Math.abs(1.0/decimalOperand.doubleValue(RoundingMode.DOWN));
		}
		final int maxPow;
		if (absBase >= 1) {
			maxPow = (int)(Math.log(decimalOperand.getScaleMetrics().getMaxIntegerValue())/Math.max(1e-10, Math.log(absBase)));
		} else {
			maxPow = -(int)(64 / (Math.log(absBase) / Math.log(2)));
		}
		final int pow = Math.max(1, Math.min(maxExponent, maxPow));
		return posExp ? RND.nextInt(pow) : -RND.nextInt(pow);
	}

	private static long randomLong(long n) {
        if (n <= 0)
            throw new IllegalArgumentException("n must be positive, but was " + n);

        long bits, val;
        do {
            bits = RND.nextLong() >>> 1;
            val = bits % n;
        } while (bits - val + (n-1) < 0);
        return val;
	}
}
