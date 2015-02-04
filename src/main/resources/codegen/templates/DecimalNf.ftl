<@pp.dropOutputFile />
<#list 0..maxScale as scale>
<@pp.changeOutputFile name=pp.home + "org/decimal4j/immutable/Decimal" + scale + "f.java" />
package org.decimal4j.immutable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.base.AbstractImmutableDecimal;
import org.decimal4j.mutable.MutableDecimal${scale}f;
import org.decimal4j.factory.Factory${scale}f;
import org.decimal4j.scale.Scale${scale}f;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * <tt>Decimal${scale}f</tt> represents an immutable decimal number with a fixed
 * number of ${scale} digits to the right of the decimal point.
 */
@SuppressWarnings("serial")
public final class Decimal${scale}f extends AbstractImmutableDecimal<Scale${scale}f, Decimal${scale}f> {

	/** Scale metrics constant for Decimal${scale}f returned by {@link #getScaleMetrics()}*/
	public static final Scale${scale}f SCALE = Scale${scale}f.INSTANCE;

	/** Factory constant for Decimal${scale}f returned by {@link #getFactory()}.*/
	public static final Factory${scale}f FACTORY = Factory${scale}f.INSTANCE;
	
	/** The unscaled long value that represents one.*/
	public static final long ONE_UNSCALED = SCALE.getScaleFactor();

	/** The Decimal${scale}f constant zero.*/
	public static final Decimal${scale}f ZERO = new Decimal${scale}f(0);
    /**
     * A constant holding the smallest positive value a {@code Decimal${scale}f}
     * can have, 10<sup>-${scale}</sup><#if scale==0>=1</#if>.
     */
	public static final Decimal${scale}f ULP = new Decimal${scale}f(1);

    /**
     * Initialize static constant array when class is loaded.
     */
    private static final int MAX_CONSTANT = 9;
    private static Decimal${scale}f POS_CONST[] = new Decimal${scale}f[MAX_CONSTANT+1];
    private static Decimal${scale}f NEG_CONST[] = new Decimal${scale}f[MAX_CONSTANT+1];

    static {
        for (int i = 1; i <= MAX_CONSTANT; i++) {
            POS_CONST[i] = new Decimal${scale}f(ONE_UNSCALED * i);
            NEG_CONST[i] = new Decimal${scale}f(-ONE_UNSCALED * i);
        }
    }

	/** The Decimal${scale}f constant 1.*/
	public static final Decimal${scale}f ONE = valueOf(1);
	/** The Decimal${scale}f constant 2.*/
	public static final Decimal${scale}f TWO = valueOf(2);
	/** The Decimal${scale}f constant 3.*/
	public static final Decimal${scale}f THREE = valueOf(3);
	/** The Decimal${scale}f constant 4.*/
	public static final Decimal${scale}f FOUR = valueOf(4);
	/** The Decimal${scale}f constant 5.*/
	public static final Decimal${scale}f FIVE = valueOf(5);
	/** The Decimal${scale}f constant 6.*/
	public static final Decimal${scale}f SIX = valueOf(6);
	/** The Decimal${scale}f constant 7.*/
	public static final Decimal${scale}f SEVEN = valueOf(7);
	/** The Decimal${scale}f constant 8.*/
	public static final Decimal${scale}f EIGHT = valueOf(8);
	/** The Decimal${scale}f constant 9.*/
	public static final Decimal${scale}f NINE = valueOf(9);
<#if (scale <= 17)>
	/** The Decimal${scale}f constant 10.*/
	public static final Decimal${scale}f TEN = new Decimal${scale}f(10 * ONE_UNSCALED);
<#if (scale <= 16)>
	/** The Decimal${scale}f constant 100.*/
	public static final Decimal${scale}f HUNDRED = new Decimal${scale}f(100 * ONE_UNSCALED);
<#if (scale <= 15)>
	/** The Decimal${scale}f constant 1000.*/
	public static final Decimal${scale}f THOUSAND = new Decimal${scale}f(1000 * ONE_UNSCALED);
<#if (scale <= 12)>
	/** The Decimal${scale}f constant 10<sup>6</sup>.*/
	public static final Decimal${scale}f MILLION = new Decimal${scale}f(1000000 * ONE_UNSCALED);
<#if (scale <= 9)>
	/** The Decimal${scale}f constant 10<sup>9</sup>.*/
	public static final Decimal${scale}f BILLION = new Decimal${scale}f(1000000000 * ONE_UNSCALED);
<#if (scale <= 6)>
	/** The Decimal${scale}f constant 10<sup>12</sup>.*/
	public static final Decimal${scale}f TRILLION = new Decimal${scale}f(1000000000000L * ONE_UNSCALED);
<#if (scale <= 3)>
	/** The Decimal${scale}f constant 10<sup>15</sup>.*/
	public static final Decimal${scale}f QUADRILLION = new Decimal${scale}f(1000000000000000L * ONE_UNSCALED);
<#if (scale <= 0)>
	/** The Decimal${scale}f constant 10<sup>18</sup>.*/
	public static final Decimal${scale}f QUINTILLION = new Decimal${scale}f(1000000000000000000L * ONE_UNSCALED);
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>

	/** The Decimal${scale}f constant -1.*/
	public static final Decimal${scale}f MINUS_ONE = valueOf(-1);

<#if (scale >= 1)>
	/** The Decimal${scale}f constant 0.5.*/
	public static final Decimal${scale}f HALF = new Decimal${scale}f(ONE_UNSCALED / 2);
	/** The Decimal${scale}f constant 0.1.*/
	public static final Decimal${scale}f TENTH = new Decimal${scale}f(ONE_UNSCALED / 10);
<#if (scale >= 2)>
	/** The Decimal${scale}f constant 0.01.*/
	public static final Decimal${scale}f HUNDREDTH = new Decimal${scale}f(ONE_UNSCALED / 100);
<#if (scale >= 3)>
	/** The Decimal${scale}f constant 0.001.*/
	public static final Decimal${scale}f THOUSANDTH = new Decimal${scale}f(ONE_UNSCALED / 1000);
<#if (scale >= 6)>
	/** The Decimal${scale}f constant 10<sup>-6</sup>.*/
	public static final Decimal${scale}f MILLIONTH = new Decimal${scale}f(ONE_UNSCALED / 1000000);
<#if (scale >= 9)>
	/** The Decimal${scale}f constant 10<sup>-9</sup>.*/
	public static final Decimal${scale}f BILLIONTH = new Decimal${scale}f(ONE_UNSCALED / 1000000000);
<#if (scale >= 12)>
	/** The Decimal${scale}f constant 10<sup>-12</sup>.*/
	public static final Decimal${scale}f TRILLIONTH = new Decimal${scale}f(ONE_UNSCALED / 1000000000000L);
<#if (scale >= 15)>
	/** The Decimal${scale}f constant 10<sup>-15</sup>.*/
	public static final Decimal${scale}f QUADRILLIONTH = new Decimal${scale}f(ONE_UNSCALED / 1000000000000000L);
<#if (scale >= 18)>
	/** The Decimal${scale}f constant 10<sup>-18</sup>.*/
	public static final Decimal${scale}f QUINTILLIONTH = new Decimal${scale}f(ONE_UNSCALED / 1000000000000000000L);
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>

    /**
     * A constant holding the maximum value a {@code Decimal${scale}f} can have,
     * ${"9223372036854775807"?substring(0, 19-scale)}<#if (scale>0)>.${"9223372036854775807"?substring(19-scale)}</#if>.
     */
	public static final Decimal${scale}f MAX_VALUE = new Decimal${scale}f(Long.MAX_VALUE);
    /**
     * A constant holding the maximum integer value a {@code Decimal${scale}f}
     * can have, ${"9223372036854775807"?substring(0, 19-scale)}<#if (scale>0)>.${"0000000000000000000"?substring(19-scale)}</#if>.
     */
	public static final Decimal${scale}f MAX_INTEGER_VALUE = new Decimal${scale}f((Long.MAX_VALUE / ONE_UNSCALED) * ONE_UNSCALED);
    /**
     * A constant holding the minimum value a {@code Decimal${scale}f} can have,
     * -${"9223372036854775807"?substring(0, 19-scale)}<#if (scale>0)>.${"9223372036854775808"?substring(19-scale)}</#if>.
     */
	public static final Decimal${scale}f MIN_VALUE = new Decimal${scale}f(Long.MIN_VALUE);
    /**
     * A constant holding the minimum integer value a {@code Decimal${scale}f}
     * can have, -${"9223372036854775808"?substring(0, 19-scale)}<#if (scale>0)>.${"0000000000000000000"?substring(19-scale)}</#if>.
     */
	public static final Decimal${scale}f MIN_INTEGER_VALUE = new Decimal${scale}f((Long.MIN_VALUE / ONE_UNSCALED) * ONE_UNSCALED);

	private Decimal${scale}f(long unscaled) {
		super(unscaled);
	}

	public Decimal${scale}f(String value) {
		super(SCALE.getDefaultArithmetic().parse(value));
	}
	
	@Override
	public Scale${scale}f getScaleMetrics() {
		return SCALE;
	}

	@Override
	public Factory${scale}f getFactory() {
		return FACTORY;
	}

	@Override
	protected Decimal${scale}f self() {
		return this;
	}

    /**
     * Returns a Decimal${scale}f whose value is equal to that of the
     * specified {@code long}.
     *
     * @param  value value of the Decimal${scale}f to return.
     * @return a Decimal${scale}f with the specified value.
     */
	public static Decimal${scale}f valueOf(long value) {
        if (value == 0)
            return ZERO;
        if (value > 0 & value <= MAX_CONSTANT)
            return POS_CONST[(int) value];
        else if (value < 0 & value >= -MAX_CONSTANT)
            return NEG_CONST[(int) -value];
		return valueOfUnscaled(SCALE.getDefaultArithmetic().fromLong(value));
	}

	public static Decimal${scale}f valueOf(long value, OverflowMode overflowMode) {
        if (value == 0)
            return ZERO;
        if (value > 0 & value <= MAX_CONSTANT)
            return POS_CONST[(int) value];
        else if (value < 0 & value >= -MAX_CONSTANT)
            return NEG_CONST[(int) -value];
		return valueOfUnscaled(SCALE.getTruncatingArithmetic(overflowMode).fromLong(value));
	}

	public static Decimal${scale}f valueOf(double value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetic().fromDouble(value));
	}

	public static Decimal${scale}f valueOf(double value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetic(roundingMode).fromDouble(value));
	}

	public static Decimal${scale}f valueOf(double value, TruncationPolicy truncationPolicy) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetic(truncationPolicy).fromDouble(value));
	}

	public static Decimal${scale}f valueOf(BigInteger value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetic().fromBigInteger(value));
	}

	public static Decimal${scale}f valueOf(BigInteger value, OverflowMode overflowMode) {
		return valueOfUnscaled(SCALE.getTruncatingArithmetic(overflowMode).fromBigInteger(value));
	}

	public static Decimal${scale}f valueOf(BigDecimal value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetic().fromBigDecimal(value));
	}

	public static Decimal${scale}f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetic(roundingMode).fromBigDecimal(value));
	}

	public static Decimal${scale}f valueOf(BigDecimal value, TruncationPolicy truncationPolicy) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetic(truncationPolicy).fromBigDecimal(value));
	}

	public static Decimal${scale}f valueOf(Decimal<?> value) {
		return valueOfUnscaled(value.unscaledValue(), value.getScale());
	}

	public static Decimal${scale}f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return valueOfUnscaled(value.unscaledValue(), value.getScale(), roundingMode);
	}

	public static Decimal${scale}f valueOf(Decimal<?> value, TruncationPolicy truncationPolicy) {
		return valueOfUnscaled(value.unscaledValue(), value.getScale(), truncationPolicy);
	}

	public static Decimal${scale}f valueOf(String value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetic().parse(value));
	}

	public static Decimal${scale}f valueOf(String value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetic(roundingMode).parse(value));
	}

	public static Decimal${scale}f valueOf(String value, TruncationPolicy truncationPolicy) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetic(truncationPolicy).parse(value));
	}

	public static Decimal${scale}f valueOfUnscaled(long unscaledValue) {
		if (unscaledValue == 0) {
			return ZERO;
		}
		if (unscaledValue == 1) {
			return ULP;
		}
		if (unscaledValue == ONE_UNSCALED) {
			return ONE;
		}
		if (unscaledValue == -ONE_UNSCALED) {
			return MINUS_ONE;
		}
		return new Decimal${scale}f(unscaledValue);
	}

	public static Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale) {
		return valueOfUnscaled(SCALE.getDefaultArithmetic().fromUnscaled(unscaledValue, scale));
	}

	public static Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetic(roundingMode).fromUnscaled(unscaledValue, scale));
	}

	public static Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale, TruncationPolicy truncationPolicy) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetic(truncationPolicy).fromUnscaled(unscaledValue, scale));
	}

	@Override
	protected Decimal${scale}f createOrAssign(long unscaled) {
		return valueOfUnscaled(unscaled);
	}
	
	@Override
	protected Decimal${scale}f create(long unscaled) {
		return valueOfUnscaled(unscaled);
	}
	
	@Override
	protected Decimal${scale}f[] createArray(int length) {
		return new Decimal${scale}f[length];
	}

	@Override
	public MutableDecimal${scale}f toMutableDecimal() {
		return new MutableDecimal${scale}f(this);
	}

	@Override
	public Decimal${scale}f toImmutableDecimal() {
		return this;
	}
}
</#list> 