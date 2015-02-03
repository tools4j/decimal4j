package org.decimal4j.arithmetic;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.DecimalRounding;

/**
 * Performs rounding of decimal values.
 */
class Round {

	public static long round(DecimalArithmetic arith, long uDecimal, int precision) {
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final int scale = scaleMetrics.getScale();
		final ScaleMetrics deltaMetrics;
		if (precision == 0) {
			deltaMetrics = scaleMetrics;
		} else if (precision < scale) {
			final int deltaScale = scale - precision;
			if (deltaScale <= 18) {
				deltaMetrics = Scales.valueOf(scale - precision);
			} else {
				throw new IllegalArgumentException("scale - precision must be <= 18 but was " + deltaScale + " for scale=" + scale + " and precision=" + precision);
			}
		} else {
			//precision >= scale
			return uDecimal;
		}
		return uDecimal - deltaMetrics.moduloByScaleFactor(uDecimal);
	}
	
	public static long round(DecimalArithmetic arith, DecimalRounding rounding, long uDecimal, int precision) {
		final ScaleMetrics scaleMetrics = arith.getScaleMetrics();
		final int scale = scaleMetrics.getScale();
		final int deltaScale = scale - precision;
		final ScaleMetrics deltaMetrics;
		if (precision == 0) {
			deltaMetrics = scaleMetrics;
		} else if (precision < scale) {
			if (deltaScale <= 18) {
				deltaMetrics = Scales.valueOf(scale - precision);
			} else {
				throw new IllegalArgumentException("scale - precision must be <= 18 but was " + deltaScale + " for scale=" + scale + " and precision=" + precision);
			}
		} else {
			//precision >= scale
			return uDecimal;
		}
		if (uDecimal == 0) {
			return 0;
		}
		final long truncatedDigits = deltaMetrics.moduloByScaleFactor(uDecimal);
		final long truncatedValue = uDecimal - truncatedDigits;
		final long truncatedOddEven = truncatedValue >> deltaScale; //move odd bit into place for HALF_EVEN rounding
		final long roundingInc = RoundingUtil.calculateRoundingIncrement(rounding, truncatedOddEven, truncatedDigits, deltaMetrics.getScaleFactor());
		return arith.add(truncatedValue, roundingInc == 0 ? 0 : deltaMetrics.multiplyByScaleFactor(roundingInc));//must add via arith to check for overflow
	}

	// no instances
	private Round() {
		
	}
	
}
