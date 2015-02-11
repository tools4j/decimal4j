package org.decimal4j.jmh;

import java.util.Random;

import org.decimal4j.scale.Scales;

/**
 * Describes the type of benchmark. This information is used when the benchmark
 * state is initialized to constrain the random generation of the test values,
 * for instance to avoid overflows or division by zero.
 */
public enum BenchmarkType {
	Add {
		@Override
		public long randomSecond(ValueType valueType, int scale, long first) {
			//avoid overflows
			long second = valueType.random(SignType.ALL);
			while ((first ^ second) >= 0 & (first ^ second) < 0) {
				//overflow, try again
				second = valueType.random(SignType.ALL);
			}
			return second;
		}
	},
	Subtract {
		@Override
		public long randomSecond(ValueType valueType, int scale, long first) {
			//avoid overflows
			long second = valueType.random(SignType.ALL);
			while ((first ^ second) < 0 & (first ^ (first - second)) < 0) {
				//overflow, try again
				second = valueType.random(SignType.ALL);
			}
			return second;
		}
	},
	Square {
		@Override
		public long randomFirst(ValueType valueType, int scale) {
			//avoid overflows
			final long one = Scales.getScaleMetrics(scale).getScaleFactor();
			final long max = (long)Math.ceil(Math.sqrt(Long.MAX_VALUE * (double)one));//no long overflow because of sqrt
			final long value = randomLong(Math.min(max, valueType.maxValue));
			
			//positive or negative?
			return RND.nextBoolean() ? -value : value;
		}
		@Override
		public long randomSecond(ValueType valueType, int scale, long first) {
			throw new RuntimeException("internal error: square has no second decimal argument");
		}
	},
	Sqrt {
		@Override
		public long randomFirst(ValueType valueType, int scale) {
			return valueType.random(SignType.POSITIVE);
		}
		@Override
		public long randomSecond(ValueType valueType, int scale, long first) {
			throw new RuntimeException("internal error: sqrt has no second decimal argument");
		}
	},
	Multiply {
		@Override
		public long randomSecond(ValueType valueType, int scale, long first) {
			//avoid overflows
			final long one = Scales.getScaleMetrics(scale).getScaleFactor();
			if (-one <= first & first <= one) {
				//no overflow possible
				long value = valueType.random(SignType.ALL);
				while (first < 0 & value == Long.MIN_VALUE) {
					//overflow theoretically possible, hence try again
					value = valueType.random(SignType.ALL);
				}
				return value;
			}
			final long max = (long)Math.ceil(Long.MAX_VALUE * (one / Math.abs((double)first)));
			final long value = randomLong(Math.min(max, valueType.maxValue));
			
			//positive or negative?
			return RND.nextBoolean() ? -value : value;
		}
	},
	Divide {
		@Override
		public long randomSecond(ValueType valueType, int scale, long first) {
			//avoid overflows
			final long one = Scales.getScaleMetrics(scale).getScaleFactor();
			final long min = (long)Math.ceil(one * Math.abs((double)first) / Long.MAX_VALUE);
			final long value = min + randomLong(Math.max(0, valueType.maxValue - min));
			//NOTE: value may be larger than valueType.maxValue, but no overflow seems more important here
			
			//positive or negative?
			return RND.nextBoolean() ? -value : value;
		}
	},
	Avg {
		@Override
		public long randomSecond(ValueType valueType, int scale, long first) {
			//no overflow possible
			return valueType.random(SignType.ALL);
		}
	},
	ConvertToDouble {
		@Override
		public long randomSecond(ValueType valueType, int scale, long first) {
			throw new RuntimeException("internal error: conversion to double has no second decimal argument");
		}
	},
	Double {
		@Override
		public long randomSecond(ValueType valueType, int scale, long first) {
			//double arith does not cause exceptions for overflow or div by zero
			return valueType.random(SignType.ALL);
		}
	};
	
	private static final Random RND = new Random();
	public long randomFirst(ValueType valueType, int scale) {
		return valueType.random(SignType.ALL);
	}
	abstract public long randomSecond(ValueType valueType, int scale, long first);

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
