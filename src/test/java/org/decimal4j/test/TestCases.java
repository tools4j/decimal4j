/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.test;

import java.util.SortedSet;
import java.util.TreeSet;

import org.decimal4j.scale.ScaleMetrics;

public enum TestCases {
	/** Run full set of test cases */
	ALL(SpecialValueSet.ALL),
	/** Run standard set of test cases */
	LARGE(SpecialValueSet.LARGE),
	/** Run standard set of test cases */
	STANDARD(SpecialValueSet.STANDARD),
	/** Run small set of test cases */
	SMALL(SpecialValueSet.SMALL),
	/** Run tiny set of test cases */
	TINY(SpecialValueSet.TINY);

	private final SpecialValueSet specialValueSet;
	private TestCases(SpecialValueSet specialValueSet) {
		this.specialValueSet = specialValueSet;
	}
	public long[] getSpecialValuesFor(ScaleMetrics scaleMetrics) {
		return getSpecialValuesInternal(scaleMetrics, this != TINY, specialValueSet);
	}
	public long[] getSpecialValuesFor(ScaleMetrics scaleMetrics, SpecialValueSet... extras) {
		return getSpecialValuesInternal(scaleMetrics, this != TINY, specialValueSet, extras);
	}
	private static long[] getSpecialValuesInternal(ScaleMetrics scaleMetrics, boolean addWithoutFractionalPart, SpecialValueSet base, SpecialValueSet... extras) {
		final SortedSet<Long> set = new TreeSet<Long>();
		add(scaleMetrics, addWithoutFractionalPart, set, base);
		for (final SpecialValueSet extra : extras) {
			add(scaleMetrics, addWithoutFractionalPart, set, extra);
		}
		return toArray(set);
	}
	private static long[] toArray(SortedSet<Long> set) {
		final long[] result = new long[set.size()];
		int index = 0;
		for (final long val : set) {
			result[index++] = val;
		}
		return result;
	}
	private static void add(ScaleMetrics scaleMetrics, boolean addWithoutFractionalPart, SortedSet<Long> result, SpecialValueSet add) {
		for (final long val : add.getValues()) {
			result.add(val);
			if (addWithoutFractionalPart) {
				result.add(val - scaleMetrics.moduloByScaleFactor(val));
			}
		}
	}
}