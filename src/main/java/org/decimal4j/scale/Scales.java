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

	private static ScaleMetrics[] SCALES = {//
	Scale0f.INSTANCE,//
	Scale1f.INSTANCE, //
	Scale2f.INSTANCE,//
	Scale3f.INSTANCE,//
	Scale4f.INSTANCE,//
	Scale5f.INSTANCE,//
	Scale6f.INSTANCE,//
	Scale7f.INSTANCE,//
	Scale8f.INSTANCE,//
	Scale9f.INSTANCE,//
	Scale10f.INSTANCE,//
	Scale11f.INSTANCE,//
	Scale12f.INSTANCE,//
	Scale13f.INSTANCE,//
	Scale14f.INSTANCE,//
	Scale15f.INSTANCE,//
	Scale16f.INSTANCE,//
	Scale17f.INSTANCE,//
	Scale18f.INSTANCE //
	};
	/**
	 * All scale metric constants in an immutable ordered list:
	 * <p>
	 * {@code VALUES=[Scale0f.INSTANCE, Scale1f.INSTANCE, ..., Scale18f.INSTANCE]}
	 */
	public static List<ScaleMetrics> VALUES = Collections.unmodifiableList(Arrays.asList(SCALES));

	private static final long[] SCALE_FACTORS = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L };

	/**
	 * Returns the {@code ScaleMetrics} constant based on a given scale
	 * 
	 * @param scale
	 *            the scale value; must be in {@code [0,18]} both ends inclusive
	 * @return the scale metrics constant corresponding to {@code scale}
	 * @throws IllegalArgumentException
	 *             if scale is not in {@code [0, 18]}
	 */
	public static ScaleMetrics valueOf(int scale) {
		if (0 <= scale & scale <= 18) {
			return SCALES[scale];
		}
		throw new IllegalArgumentException("illegal scale, must be in [0,18] but was: " + scale);
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
	public static ScaleMetrics findByScaleFactor(long scaleFactor) {
		final int index = Arrays.binarySearch(SCALE_FACTORS, scaleFactor);
		return index < 0 ? null : VALUES.get(index);
	}

	//no instances
	private Scales() {
		super();
	}
}
