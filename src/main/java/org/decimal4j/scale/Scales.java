/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.scale;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.decimal4j.scale.Scale10f;
import org.decimal4j.scale.Scale11f;
import org.decimal4j.scale.Scale12f;
import org.decimal4j.scale.Scale13f;
import org.decimal4j.scale.Scale14f;
import org.decimal4j.scale.Scale15f;
import org.decimal4j.scale.Scale16f;
import org.decimal4j.scale.Scale17f;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.scale.Scale1f;
import org.decimal4j.scale.Scale2f;
import org.decimal4j.scale.Scale3f;
import org.decimal4j.scale.Scale4f;
import org.decimal4j.scale.Scale5f;
import org.decimal4j.scale.Scale6f;
import org.decimal4j.scale.Scale7f;
import org.decimal4j.scale.Scale8f;
import org.decimal4j.scale.Scale9f;

/**
 * Utility class with static members to access {@link ScaleMetrics} instances.
 */
public final class Scales {

	//@formatter:off
	private static final ScaleMetrics[] SCALES = {
		Scale0f.INSTANCE,
		Scale1f.INSTANCE,
		Scale2f.INSTANCE,
		Scale3f.INSTANCE,
		Scale4f.INSTANCE,
		Scale5f.INSTANCE,
		Scale6f.INSTANCE,
		Scale7f.INSTANCE,
		Scale8f.INSTANCE,
		Scale9f.INSTANCE,
		Scale10f.INSTANCE,
		Scale11f.INSTANCE,
		Scale12f.INSTANCE,
		Scale13f.INSTANCE,
		Scale14f.INSTANCE,
		Scale15f.INSTANCE,
		Scale16f.INSTANCE,
		Scale17f.INSTANCE,
		Scale18f.INSTANCE
	};
	//@formatter:on

	/**
	 * All scale metric constants in an immutable ordered list:
	 * <br>
	 * {@code VALUES=[Scale0f.INSTANCE, Scale1f.INSTANCE, ..., Scale18f.INSTANCE]}
	 */
	public static final List<ScaleMetrics> VALUES = Collections.unmodifiableList(Arrays.asList(SCALES));
	
	/**
	 * The minimum scale that can be passed to {@link #getScaleMetrics(int)} without causing an
	 * exception; the minimum scale is 0.
	 */
	public static final int MIN_SCALE = 0;

	/**
	 * The maximum scale that can be passed to {@link #getScaleMetrics(int)} without causing an
	 * exception; the maximum scale is 18.
	 */
	public static final int MAX_SCALE = 18;

	//@formatter:off
	private static final long[] SCALE_FACTORS = {
		1, 
		10, 
		100, 
		1000, 
		10000, 
		100000, 
		1000000, 
		10000000, 
		100000000, 
		1000000000, 
		10000000000L, 
		100000000000L, 
		1000000000000L, 
		10000000000000L, 
		100000000000000L, 
		1000000000000000L, 
		10000000000000000L, 
		100000000000000000L, 
		1000000000000000000L 
	};
	//@formatter:on

	/**
	 * Returns the {@code ScaleMetrics} constant based on a given scale
	 * 
	 * @param scale
	 *            the scale value; must be in {@code [0,18]} both ends inclusive
	 * @return the scale metrics constant corresponding to {@code scale}
	 * @throws IllegalArgumentException
	 *             if scale is not in {@code [0, 18]}
	 * @see #MIN_SCALE
	 * @see #MAX_SCALE
	 */
	public static final ScaleMetrics getScaleMetrics(int scale) {
		if (MIN_SCALE <= scale & scale <= MAX_SCALE) {
			return SCALES[scale];
		}
		throw new IllegalArgumentException("illegal scale, must be in ["+ MIN_SCALE + "," + MAX_SCALE + "] but was: " + scale);
	}

	/**
	 * Returns the {@code ScaleMetrics} constant that matches the given
	 * {@code scaleFactor} if any and null otherwise.
	 * 
	 * @param scaleFactor
	 *            the scale factor to find
	 * @return the scale metrics constant with
	 *         {@link ScaleMetrics#getScaleFactor()} equal to
	 *         {@code scaleFactor} if it exists and null otherwise
	 * @see ScaleMetrics#getScaleFactor()
	 */
	public static final ScaleMetrics findByScaleFactor(long scaleFactor) {
		final int index = Arrays.binarySearch(SCALE_FACTORS, scaleFactor);
		return index < 0 ? null : VALUES.get(index);
	}

	//no instances
	private Scales() {
		super();
	}
}
