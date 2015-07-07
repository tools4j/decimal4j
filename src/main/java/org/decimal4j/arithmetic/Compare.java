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
package org.decimal4j.arithmetic;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;



/**
 * Contains static methods to compare unscaled decimals of different scales.
 */
final class Compare {

	/**
	 * Compares the two unscaled values with possibly different scales.
	 * 
	 * @param unscaled		the first unscaled value to compare
	 * @param scale			the scale of the first value
	 * @param otherUnscaled	the second unscaled value to compare
	 * @param otherScale	the scale of the second value
	 * @return 
	 */
	public static final int compareUnscaled(long unscaled, int scale, long otherUnscaled, int otherScale) {
		if (scale == otherScale) {
			return Long.compare(unscaled, otherUnscaled);
		}
		if (scale < otherScale) {
			final ScaleMetrics diffMetrics = Scales.getScaleMetrics(otherScale - scale);
			final long otherRescaled = diffMetrics.divideByScaleFactor(otherUnscaled);
			final int cmp = Long.compare(unscaled, otherRescaled);
			if (cmp != 0) {
				return cmp;
			}
			// remainder must be zero for equality
			final long otherRemainder = otherUnscaled - diffMetrics.multiplyByScaleFactor(otherRescaled);
			return -Long.signum(otherRemainder);
		} else {
			final ScaleMetrics diffMetrics = Scales.getScaleMetrics(scale - otherScale);
			final long rescaled = diffMetrics.divideByScaleFactor(unscaled);
			final int cmp = Long.compare(rescaled, otherUnscaled);
			if (cmp != 0) {
				return cmp;
			}
			// remainder must be zero for equality
			final long remainder = unscaled - diffMetrics.multiplyByScaleFactor(rescaled);
			return Long.signum(remainder);
		}
	}

	// no instances
	private Compare() {
	}
}
