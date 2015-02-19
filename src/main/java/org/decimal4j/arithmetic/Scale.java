package org.decimal4j.arithmetic;

import org.decimal4j.api.DecimalArithmetic;


/**
 * Contains static methods to convert between different scales.
 */
class Scale {

	public static final long rescale(DecimalArithmetic arith, long unscaledValue, int scale, int targetScale) {
		final long deltaScaleLong = (long)scale - targetScale;
		final int deltaScale = (int)deltaScaleLong;
		if (deltaScale == deltaScaleLong) {
			return arith.divideByPowerOf10(unscaledValue, deltaScale);
		}
		throw new IllegalArgumentException("cannot convert from scale " + scale + " to + " + targetScale + " (scale difference " + deltaScaleLong + " is out of integer range)");
	}

	// no instances
	private Scale() {
	}
}
