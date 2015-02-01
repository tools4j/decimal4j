package org.decimal4j;

import java.util.Random;

/**
 * Double value types.
 */
public enum DoubleType {
	/** Uniform between 0 and 1 */ 
	Uniform {
		@Override
		public double random(int scale) {
			return RND.nextDouble();
		}
	},
	/** Gaussian between 0 and 1 */ 
	Gaussian {
		@Override
		public double random(int scale) {
			return RND.nextGaussian();
		}
	},
	/** {@link Double#longBitsToDouble(long)} with a random long */ 
	LongBitsToDouble {
		@Override
		public double random(int scale) {
			final int exp = randomExponent(scale);
			final long longBits = (RND.nextLong() & (0x8000000000000000L | 0x000fffffffffffffL)) | ((exp + 1023L) << 52);
			return Double.longBitsToDouble(longBits);
		}
		private int randomExponent(int scale) {
			if (RND.nextBoolean()) {
				//positive exponent, limit to log2 of max value
				final double maxValue = Long.MAX_VALUE / Math.pow(10, scale);
				final int log2 = (int)Math.floor(Math.log(maxValue) / Math.log(2));
				return RND.nextInt(log2);
			}
			//negative exponent, keep half of them within magnitued values
			return RND.nextBoolean() ? -RND.nextInt(52) : -RND.nextInt(1024);
		}
	},
	;

	private static final Random RND = new Random();

	abstract protected double random(int scale);
	
	public double random(SignType signType, int scale) {
		double val = random(scale);
		if (signType == SignType.NON_ZERO) {
			while (val == 0) {
				val = RND.nextDouble();
			}
		} else if (signType == SignType.ALL) {
			if (RND.nextBoolean()) {
				val = -val;
			}
		}
		return val;
		
	}
}