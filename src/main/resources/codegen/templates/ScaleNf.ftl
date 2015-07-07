<@pp.dropOutputFile />
<#list 1..maxScale as scale>
<@pp.changeOutputFile name=pp.home + "org/decimal4j/scale/Scale" + scale + "f.java" />
package org.decimal4j.scale;

import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.FLOOR;
import static java.math.RoundingMode.HALF_EVEN;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UNNECESSARY;
import static org.decimal4j.truncate.OverflowMode.CHECKED;
import static org.decimal4j.truncate.OverflowMode.UNCHECKED;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.arithmetic.CheckedScaleNfRoundingArithmetic;
import org.decimal4j.arithmetic.CheckedScaleNfTruncatingArithmetic;
import org.decimal4j.arithmetic.UncheckedScaleNfRoundingArithmetic;
import org.decimal4j.arithmetic.UncheckedScaleNfTruncatingArithmetic;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Scale class for decimals with {@link #getScale() scale} ${scale} and
 * {@link #getScaleFactor() scale factor} ${"1"?right_pad(scale+1, "0")}.
 */
public enum Scale${scale}f implements ScaleMetrics {

	/**
	 * The singleton instance for scale ${scale}.
	 */
	INSTANCE;

	private static final long LONG_MASK = 0xffffffffL;

	/**
	 * The scale value <code>${scale}</code>.
	 */
	public static final int SCALE = ${scale};

	/**
	 * The scale factor <code>10<sup>${scale}</sup></code>.
	 */
	public static final long SCALE_FACTOR = ${"1"?right_pad(scale+1, "0")}L;
	
	/** Long.numberOfLeadingZeros(SCALE_FACTOR)*/
	private static final int NLZ_SCALE_FACTOR = ${nlzScaleFactor[scale]};
	
<#if (scale > 9)>
	private static final long SCALE_FACTOR_HIGH_BITS = SCALE_FACTOR >>> 32;
	private static final long SCALE_FACTOR_LOW_BITS = SCALE_FACTOR & LONG_MASK;

</#if>
	private static final long MAX_INTEGER_VALUE = Long.MAX_VALUE / SCALE_FACTOR;
	private static final long MIN_INTEGER_VALUE = Long.MIN_VALUE / SCALE_FACTOR;
	private static final BigInteger BI_SCALE_FACTOR = BigInteger.valueOf(SCALE_FACTOR);
	private static final BigDecimal BD_SCALE_FACTOR = BigDecimal.valueOf(SCALE_FACTOR);

	private static final DecimalArithmetic[] UNCHECKED_ARITHMETIC = initArithmetic(UNCHECKED);
	private static final DecimalArithmetic[] CHECKED_ARITHMETIC = initArithmetic(CHECKED);

	private static final DecimalArithmetic DEFAULT_ARITHMETIC = UNCHECKED_ARITHMETIC[HALF_UP.ordinal()];
	private static final DecimalArithmetic DEFAULT_CHECKED_ARITHMETIC = CHECKED_ARITHMETIC[HALF_UP.ordinal()];
	private static final DecimalArithmetic ROUNDING_DOWN_ARITHMETIC = UNCHECKED_ARITHMETIC[DOWN.ordinal()];
	private static final DecimalArithmetic ROUNDING_FLOOR_ARITHMETIC = UNCHECKED_ARITHMETIC[FLOOR.ordinal()];
	private static final DecimalArithmetic ROUNDING_HALF_EVEN_ARITHMETIC = UNCHECKED_ARITHMETIC[HALF_EVEN.ordinal()];
	private static final DecimalArithmetic ROUNDING_UNNECESSARY_ARITHMETIC = UNCHECKED_ARITHMETIC[UNNECESSARY.ordinal()];

	private static DecimalArithmetic[] initArithmetic(OverflowMode overflowMode) {
		final boolean checked = overflowMode == CHECKED;
		final DecimalArithmetic[] arith = new DecimalArithmetic[DecimalRounding.VALUES.size()];
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final int index = dr.getRoundingMode().ordinal();
			if (dr == DecimalRounding.DOWN) {
				arith[index] = checked ? new CheckedScaleNfTruncatingArithmetic(INSTANCE)
						: new UncheckedScaleNfTruncatingArithmetic(INSTANCE);
			} else {
				arith[index] = checked ? new CheckedScaleNfRoundingArithmetic(INSTANCE, dr)
						: new UncheckedScaleNfRoundingArithmetic(INSTANCE, dr);
			}
		}
		return arith;
	}

	@Override
	public final int getScale() {
		return SCALE;
	}

	@Override
	public final long getScaleFactor() {
		return SCALE_FACTOR;
	}
	
	@Override
	public final int getScaleFactorNumberOfLeadingZeros() {
		return NLZ_SCALE_FACTOR;
	}

	@Override
	public final long multiplyByScaleFactor(long factor) {
		return factor * SCALE_FACTOR;
	}

	@Override
	public final BigInteger getScaleFactorAsBigInteger() {
		return BI_SCALE_FACTOR;
	}

	@Override
	public final BigDecimal getScaleFactorAsBigDecimal() {
		return BD_SCALE_FACTOR;
	}

	@Override
	public final long getMaxIntegerValue() {
		return MAX_INTEGER_VALUE;
	}

	@Override
	public final long getMinIntegerValue() {
		return MIN_INTEGER_VALUE;
	}

	@Override
	public final boolean isValidIntegerValue(long value) {
		return MIN_INTEGER_VALUE <= value & value <= MAX_INTEGER_VALUE;
	}

	@Override
	public final long multiplyByScaleFactorExact(long factor) {
		final long result = factor * SCALE_FACTOR;
		if (MIN_INTEGER_VALUE <= factor & factor <= MAX_INTEGER_VALUE) {
			return result;
		}
		throw new ArithmeticException("Overflow: " + factor + " * " + SCALE_FACTOR + " = " + result);
	}
	
	@Override
	public final long mulloByScaleFactor(int factor) {
	<#if (scale > 9)>
		return (factor & LONG_MASK) * SCALE_FACTOR_LOW_BITS;
	<#else>
		return (factor & LONG_MASK) * SCALE_FACTOR;
	</#if>
	}

	@Override
	public final long mulhiByScaleFactor(int factor) {
	<#if (scale > 9)>
		return (factor & LONG_MASK) * SCALE_FACTOR_HIGH_BITS;
	<#else>
		return 0;
	</#if>
	}

	@Override
	public final long divideByScaleFactor(long dividend) {
		return dividend / SCALE_FACTOR;
	}

	@Override
	public final long divideUnsignedByScaleFactor(long unsignedDividend) {
		//we can do this since SCALE_FACTOR > 1 and even
		return (unsignedDividend >>> 1) / (SCALE_FACTOR >>> 1);
	}

	@Override
	public final long moduloByScaleFactor(long dividend) {
		return dividend % SCALE_FACTOR;
	}

	@Override
	public String toString(long value) {
		return DEFAULT_ARITHMETIC.toString(value);
	}

	@Override
	public final DecimalArithmetic getDefaultArithmetic() {
		return DEFAULT_ARITHMETIC;
	}

	@Override
	public final DecimalArithmetic getDefaultCheckedArithmetic() {
		return DEFAULT_CHECKED_ARITHMETIC;
	}

	@Override
	public final DecimalArithmetic getRoundingDownArithmetic() {
		return ROUNDING_DOWN_ARITHMETIC;
	}

	@Override
	public final DecimalArithmetic getRoundingFloorArithmetic() {
		return ROUNDING_FLOOR_ARITHMETIC;
	}

	@Override
	public final DecimalArithmetic getRoundingHalfEvenArithmetic() {
		return ROUNDING_HALF_EVEN_ARITHMETIC;
	}

	@Override
	public final DecimalArithmetic getRoundingUnnecessaryArithmetic() {
		return ROUNDING_UNNECESSARY_ARITHMETIC;
	}

	@Override
	public final DecimalArithmetic getArithmetic(RoundingMode roundingMode) {
		return UNCHECKED_ARITHMETIC[roundingMode.ordinal()];
	}

	@Override
	public final DecimalArithmetic getCheckedArithmetic(RoundingMode roundingMode) {
		return CHECKED_ARITHMETIC[roundingMode.ordinal()];
	}

	@Override
	public final DecimalArithmetic getArithmetic(TruncationPolicy truncationPolicy) {
		final OverflowMode overflow = truncationPolicy.getOverflowMode();
		final RoundingMode rounding = truncationPolicy.getRoundingMode();
		return (overflow == UNCHECKED ? UNCHECKED_ARITHMETIC : CHECKED_ARITHMETIC)[rounding.ordinal()];
	}

	@Override
	public final String toString() {
		return "Scale${scale}f";
	}
}
</#list>