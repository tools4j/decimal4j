<@pp.dropOutputFile />
<#list 1..maxScale as scale>
<@pp.changeOutputFile name=pp.home + "ch/javasoft/decimal/scale/Scale" + scale + "f.java" />
package ch.javasoft.decimal.scale;

import ch.javasoft.decimal.immutable.Decimal${scale}f;
import ch.javasoft.decimal.mutable.MutableDecimal${scale}f;

/**
 * Scale class for decimals with ${scale} {@link #getScale() fraction digit} and
 * {@link #getScaleFactor() scale factor} ${"1"?right_pad(scale+1, "0")}.
 */
public final class Scale${scale}f extends AbstractScale {
	public static final Scale${scale}f INSTANCE = new Scale${scale}f();

	private static final long SCALE_FACTOR = ${"1"?right_pad(scale+1, "0")}L;
	private static final long SCALE_FACTOR_HALF = SCALE_FACTOR/2;

<#if 9 < scale>
	private static final long SCALE_FACTOR_HIGH_BITS = SCALE_FACTOR >>> 32;
	private static final long SCALE_FACTOR_LOW_BITS = SCALE_FACTOR & LONG_MASK;
</#if>

	@Override
	public int getScale() {
		return ${scale};
	}

	@Override
	public long getScaleFactor() {
		return SCALE_FACTOR;
	}

	@Override
	public long multiplyByScaleFactor(long factor) {
		return factor * SCALE_FACTOR;
	}

	@Override
	public long mulloByScaleFactor(int factor) {
	<#if 9 < scale>
		return (factor & LONG_MASK) * SCALE_FACTOR_LOW_BITS;
	<#else>
		return (factor & LONG_MASK) * SCALE_FACTOR;
	</#if>
	}

	@Override
	public long mulhiByScaleFactor(int factor) {
	<#if 9 < scale>
		return (factor & LONG_MASK) * SCALE_FACTOR_HIGH_BITS;
	<#else>
		return 0;
	</#if>
	}

	@Override
	public long divideByScaleFactor(long dividend) {
		return dividend / SCALE_FACTOR;
	}

	@Override
	public long moduloByScaleFactor(long dividend) {
		return dividend % SCALE_FACTOR;
	}

	@Override
	public long multiplyByScaleFactorHalf(long dividend) {
		return dividend * SCALE_FACTOR_HALF;
	}

	@Override
	public long divideByScaleFactorHalf(long dividend) {
		return dividend / SCALE_FACTOR_HALF;
	}
	
	@Override
	public Decimal${scale}f createImmutable(long unscaled) {
		return Decimal${scale}f.valueOfUnscaled(unscaled);
	}

	@Override
	public MutableDecimal${scale}f createMutable(long unscaled) {
		return MutableDecimal${scale}f.unscaled(unscaled);
	}
	
}
</#list>