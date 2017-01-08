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

import java.math.BigInteger;
import java.util.Random;

import org.decimal4j.jmh.state.AbstractValueBenchmarkState;
import org.decimal4j.jmh.state.PowBenchmarkState;
import org.decimal4j.jmh.state.ScaleBenchmarkState;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.CheckedRounding;

/**
 * Describes the type of benchmark. This information is used when the benchmark
 * state is initialized to constrain the random generation of the test values,
 * for instance to avoid overflows or division by zero.
 */
public enum BenchmarkType {
	Add {
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			//avoid overflows
			long second = valueType.random(SignType.ALL);
			while ((first ^ second) >= 0 & (first ^ (first + second)) < 0) {
				//overflow, try again
				second = valueType.random(SignType.ALL);
			}
			return second;
		}
	},
	Subtract {
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
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
		public long randomFirst(AbstractValueBenchmarkState benchmarkState, ValueType valueType) {
			//avoid overflows
			final long one = Scales.getScaleMetrics(benchmarkState.scale).getScaleFactor();
			final long max = (long)Math.ceil(Math.sqrt(Long.MAX_VALUE * (double)one));//no long overflow because of sqrt
			final long value = randomLong(Math.min(max, valueType.maxValue));
			
			//positive or negative?
			return RND.nextBoolean() ? -value : value;
		}
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			throw new RuntimeException("internal error: square has no second decimal argument");
		}
	},
	Sqrt {
		@Override
		public long randomFirst(AbstractValueBenchmarkState benchmarkState, ValueType valueType) {
			return valueType.random(SignType.POSITIVE);
		}
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			throw new RuntimeException("internal error: sqrt has no second decimal argument");
		}
	},
	Multiply {
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			//avoid overflows
			final long one = Scales.getScaleMetrics(benchmarkState.scale).getScaleFactor();
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
	MultiplyExact {
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			//avoid overflows
			if (first == Long.MIN_VALUE) {
				return randomLong(2);//only 0 or 1 without overflow
			}
			long max = Long.MAX_VALUE / Math.abs(first);
			if (max < Long.MAX_VALUE) max++;
			final long value = randomLong(Math.min(max, valueType.maxValue));
			
			//positive or negative?
			return RND.nextBoolean() ? -value : value;
		}
	},
	Divide {
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			//avoid overflows
			//one * first / second <= Long.MAX_VALUE 
			//   -->        second >= one * first / (Long.MAX_VALUE - 1)
			final BigInteger one = Scales.getScaleMetrics(benchmarkState.scale).getScaleFactorAsBigInteger();
			//min = 1 + abs(one * first / Long.MAX_VALUE)
			final long min = 1 + one.multiply(BigInteger.valueOf(first)).divide(BigInteger.valueOf(Long.MAX_VALUE)).abs().longValue();
			final long value = min + randomLong(Math.max(1, valueType.maxValue - min));
			//NOTE: value may be larger than valueType.maxValue, but no overflow seems more important here
			
			//positive or negative?
			return RND.nextBoolean() ? -value : value;
		}
	},
	DivideExact {
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			//avoid overflow, only possible for Long.MIN_VALUE / -1
			long second = valueType.random(SignType.ALL);
			while (first == Long.MIN_VALUE & second == -1) {
				second = valueType.random(SignType.ALL);
			}
			return second;
		}
	},
	Avg {
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			//no overflow possible
			return valueType.random(SignType.ALL);
		}
	},
	Pow {
		@Override
		public long randomFirst(AbstractValueBenchmarkState benchmarkState, ValueType valueType) {
			//create a base that does not overflow with the given exponent
			final PowBenchmarkState powState = (PowBenchmarkState)benchmarkState;
			final ScaleMetrics scaleMetrics = Scales.getScaleMetrics(benchmarkState.scale);
			final double maxBase = Math.pow(scaleMetrics.getMaxIntegerValue(), 1.0/powState.exponent);
			final double doubleValue = maxBase * Math.random() * Math.signum(Math.random());
			return scaleMetrics.getArithmetic(CheckedRounding.DOWN).fromDouble(doubleValue);
		}
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			throw new RuntimeException("internal error: pow has no second decimal argument");
		}
	},
	Round {
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			throw new RuntimeException("internal error: round has no second decimal argument");
		}
	},
	Scale {
		@Override
		public long randomFirst(AbstractValueBenchmarkState benchmarkState, ValueType valueType) {
			//create a value that does not overflow when rescaled
			final ScaleBenchmarkState scaleState = (ScaleBenchmarkState)benchmarkState;
			if (scaleState.targetScale <= scaleState.scale) {
				return valueType.random(SignType.ALL);
			}
			final ScaleMetrics diffMetrics = Scales.getScaleMetrics(scaleState.targetScale - scaleState.scale);
			final long minValue = Math.max(valueType.minValue, diffMetrics.getMinIntegerValue());
			final long maxValue = Math.min(valueType.maxValue, diffMetrics.getMaxIntegerValue());
			return randomLong(maxValue) - randomLong(-Math.max(minValue, -Long.MAX_VALUE));
		}
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			throw new RuntimeException("internal error: scale has no second decimal argument");
		}
	},
	ConvertFromString {
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			throw new RuntimeException("internal error: conversion from string has no second decimal argument");
		}
	},
	ConvertToString {
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			throw new RuntimeException("internal error: conversion to string has no second decimal argument");
		}
	},
	ConvertToDouble {
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			throw new RuntimeException("internal error: conversion to double has no second decimal argument");
		}
	},
	Double {
		@Override
		public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first) {
			//double arith does not cause exceptions for overflow or div by zero
			return valueType.random(SignType.ALL);
		}
	};
	
	private static final Random RND = new Random();
	public long randomFirst(AbstractValueBenchmarkState benchmarkState, ValueType valueType) {
		return valueType.random(SignType.ALL);
	}
	abstract public long randomSecond(AbstractValueBenchmarkState benchmarkState, ValueType valueType, long first);

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
