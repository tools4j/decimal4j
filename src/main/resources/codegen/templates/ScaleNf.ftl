<@pp.dropOutputFile />
<#list 1..maxScale as scale>
<@pp.changeOutputFile name=pp.home + "ch/javasoft/decimal/scale/Scale" + scale + "f.java" />
package ch.javasoft.decimal.scale;

import ch.javasoft.decimal.arithmetic.Unsigned;

/**
 * Scale class for decimals with {@link #getScale() scale} ${scale} and
 * {@link #getScaleFactor() scale factor} ${"1"?right_pad(scale+1, "0")}.
 */
public final class Scale${scale}f extends AbstractScale {

	/**
	 * The singleton instance for scale ${scale}.
	 */
	public static final Scale${scale}f INSTANCE = new Scale${scale}f();

	private static final long SCALE_FACTOR = ${"1"?right_pad(scale+1, "0")}L;

<#if (scale > 9)>
	private static final long SCALE_FACTOR_HIGH_BITS = SCALE_FACTOR >>> 32;
	private static final long SCALE_FACTOR_LOW_BITS = SCALE_FACTOR & LONG_MASK;
</#if>

	@Override
	public final int getScale() {
		return ${scale};
	}

	@Override
	public final long getScaleFactor() {
		return SCALE_FACTOR;
	}

	@Override
	public final long multiplyByScaleFactor(long factor) {
		return factor * SCALE_FACTOR;
	}

	@Override
	public final long multiplyByScaleFactorExact(long factor) {
		final int leadingZeros = Long.numberOfLeadingZeros(factor) + Long.numberOfLeadingZeros(~factor) + Long.numberOfLeadingZeros(SCALE_FACTOR);
		final long result = multiplyByScaleFactor(factor);
		if (leadingZeros > Long.SIZE + 1) {
			return result;
		}
		if (leadingZeros < Long.SIZE | divideByScaleFactor(result) != factor) {
			throw new ArithmeticException("Overflow: " + factor + " * " + SCALE_FACTOR + " = " + result);
		}
		return result;
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
		// Optimization - use signed division if dividend < 2^63
		if (unsignedDividend >= 0) {
			return unsignedDividend / SCALE_FACTOR;
		}

		final long quotient = ((unsignedDividend >>> 1) / SCALE_FACTOR) << 1;
		final long rem = unsignedDividend - quotient * SCALE_FACTOR;
		return quotient + (Unsigned.isLess(rem, SCALE_FACTOR) ? 0 : 1);
	}

	@Override
	public final long moduloByScaleFactor(long dividend) {
		return dividend % SCALE_FACTOR;
	}	
}
</#list>