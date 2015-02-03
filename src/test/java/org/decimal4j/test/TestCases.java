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