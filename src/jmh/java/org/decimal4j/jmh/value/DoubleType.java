/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.jmh.value;

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