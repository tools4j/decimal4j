/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
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
 * Value types.
 */
public enum ValueType {
	/** 8 bit integer value*/ 
	Byte(java.lang.Byte.MIN_VALUE, java.lang.Byte.MAX_VALUE) {
		@Override
		public long random(SignType signType) {
			long value = signType == SignType.ALL ? (byte)RND.nextInt() : RND.nextInt(java.lang.Byte.MAX_VALUE);
			if (signType == SignType.NON_ZERO) {
				while (value == 0) {
					value = signType == SignType.ALL ? (byte)RND.nextInt() : RND.nextInt(java.lang.Byte.MAX_VALUE);
				}
			}
			return value;
		}
	},
	/** 16 bit integer value*/ 
	Short(java.lang.Short.MIN_VALUE, java.lang.Short.MAX_VALUE) {
		@Override
		public long random(SignType signType) {
			long value = signType == SignType.ALL ? (short)RND.nextInt() : RND.nextInt(java.lang.Short.MAX_VALUE);
			if (signType == SignType.NON_ZERO) {
				while (value == 0) {
					value = signType == SignType.ALL ? (short)RND.nextInt() : RND.nextInt(java.lang.Short.MAX_VALUE);
				}
			}
			return value;
		}
	},
	/** 32 bit integer value*/ 
	Int(Integer.MIN_VALUE, Integer.MAX_VALUE) {
		@Override
		public long random(SignType signType) {
			long value = signType == SignType.ALL ? RND.nextInt() : RND.nextInt(Integer.MAX_VALUE);
			if (signType == SignType.NON_ZERO) {
				while (value == 0) {
					value = signType == SignType.ALL ? RND.nextInt() : RND.nextInt(Integer.MAX_VALUE);
				}
			}
			return value;
		}
	},
	/** 64 bit integer value*/ 
	Long(java.lang.Long.MIN_VALUE, java.lang.Long.MAX_VALUE) {
		@Override
		public long random(SignType signType) {
			long val = RND.nextLong();
			if (signType != SignType.ALL) {
				while ((val <= 0 && signType == SignType.POSITIVE) || (val == 0 && signType == SignType.NON_ZERO)) {
					val = RND.nextLong();
				}
			}
			return val;
		}
	};

	private static final Random RND = new Random();
	
	public final long minValue;
	public final long maxValue;
	
	private ValueType(long minValue, long maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	abstract public long random(SignType signType);
}