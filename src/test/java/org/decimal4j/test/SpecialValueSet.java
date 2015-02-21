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
package org.decimal4j.test;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public enum SpecialValueSet {
	//for-loop-sets
	MINUS_TEN_TO_TEN(forLoop(-10, 10)),
	MINUS_TWENTY_TO_TWENTY(forLoop(-20, 20)),
	MINUS_HUNDRED_TO_HUNDRED(forLoop(-100, 100)),
	//power-of-10-sets
	POW_10_POSITIVE(powLoop(1, 1000000000000000000L, 10)),
	POW_10_NEGATIVE(powLoop(-1000000000000000000L, -1, 10)),
	POW_10(POW_10_NEGATIVE, POW_10_POSITIVE),
	//min/max-value-sets
	MIN_MAX_LONG_INT(Long.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE),
	MIN_MAX_ALL(Long.MIN_VALUE, Integer.MIN_VALUE, Short.MIN_VALUE, Byte.MIN_VALUE, Byte.MAX_VALUE, Short.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE),
	//plus/minus 1 of base sets
	POW_10_PLUS_MINUS_ONE(plus(POW_10, -1, 0, 1)),
	MIN_MAX_LONG_INT_PLUS_MINUS_ONE(plus(MIN_MAX_LONG_INT, -1, 0, 1)),
	MIN_MAX_ALL_PLUS_MINUS_ONE(plus(MIN_MAX_ALL, -1, 0, 1)),
	//half of base sets
	POW_10_HALF(div(POW_10, 2)),
	MIN_MAX_ALL_HALF(div(MIN_MAX_ALL, 2)),
	MIN_MAX_ALL_HALF_PLUS_MINUS_ONE(plus(MIN_MAX_ALL_HALF, -1, 0, 1)),
	//predefined sets
	TINY(MINUS_TEN_TO_TEN, POW_10, MIN_MAX_LONG_INT),
	SMALL(MINUS_TEN_TO_TEN, POW_10_PLUS_MINUS_ONE, MIN_MAX_LONG_INT_PLUS_MINUS_ONE),
	STANDARD(MINUS_TWENTY_TO_TWENTY, POW_10_PLUS_MINUS_ONE, MIN_MAX_LONG_INT_PLUS_MINUS_ONE),
	LARGE(MINUS_TWENTY_TO_TWENTY, POW_10_PLUS_MINUS_ONE, MIN_MAX_ALL_PLUS_MINUS_ONE),
	ALL(MINUS_TWENTY_TO_TWENTY, POW_10_PLUS_MINUS_ONE, POW_10_HALF, MIN_MAX_ALL_PLUS_MINUS_ONE, MIN_MAX_ALL_HALF_PLUS_MINUS_ONE);
	;
	private final SortedSet<Long> values;
	private SpecialValueSet(SortedSet<Long> values) {
		this.values = Collections.unmodifiableSortedSet(values);
	}
	private SpecialValueSet(long... values) {
		this(toSortedSet(values));
	}
	private SpecialValueSet(SpecialValueSet... sets) {
		this(toSortedSet(sets));
	}
	
	private SpecialValueSet(SortedSet<Long>... sets) {
		this(toSortedSet(sets));
	}
	
	public SortedSet<Long> getValues() {
		return values;
	}
	
	private static SortedSet<Long> toSortedSet(Set<Long>[] sets) {
		final SortedSet<Long> vals = new TreeSet<Long>();
		for (Set<Long> set : sets) {
			for (final long val : set) {
				vals.add(val);
			}
		}
		return vals;
	}
	
	private static SortedSet<Long> toSortedSet(SpecialValueSet... sets) {
		final SortedSet<Long> vals = new TreeSet<Long>();
		for (SpecialValueSet set : sets) {
			for (final long val : set.values) {
				vals.add(val);
			}
		}
		return vals;
	}
	private static SortedSet<Long> toSortedSet(long... values) {
		final SortedSet<Long> vals = new TreeSet<Long>();
		for (final long val : values) {
			vals.add(val);
		}
		return vals;
	}
	private static SortedSet<Long> forLoop(long from, long to) {
		return forLoop(from, to, 1);
	}
	private static SortedSet<Long> forLoop(long from, long to, int increment) {
		final SortedSet<Long> vals = new TreeSet<Long>();
		for (long val = from; val <= to; val += increment) {
			vals.add(val);
		}
		return vals;
	}
	private static SortedSet<Long> powLoop(long from, long to, int factor) {
		final SortedSet<Long> vals = new TreeSet<Long>();
		for (long val = from; val <= to; val *= factor) {
			vals.add(val);
		}
		return vals;
	}
	private static SortedSet<Long> plus(SpecialValueSet set, long... inc) {
		final SortedSet<Long> vals = new TreeSet<Long>();
		for (final long val : set.values) {
			for (final long i : inc) {
				vals.add(val + i);
			}
		}
		return vals;
	}
	private static SortedSet<Long> div(SpecialValueSet set, long divisor) {
		final SortedSet<Long> vals = new TreeSet<Long>();
		for (final long val : set.values) {
			vals.add(val/divisor);
		}
		return vals;
	}
	public static SortedSet<Long> select(ValueRange range, SpecialValueSet... sets) {
		final SortedSet<Long> vals = new TreeSet<Long>();
		for (final SpecialValueSet set : sets) {
			for (final long val : set.values) {
				if (range.include(val)) {
					vals.add(val);
				}
			}
		}
		return vals;
	}
}
