<@pp.dropOutputFile />
<#list 0..maxScale as scale>
<@pp.changeOutputFile name=pp.home + "ch/javasoft/decimal/immutable/Decimal" + scale + "f.java" />
package ch.javasoft.decimal.immutable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.base.AbstractImmutableDecimal;
import ch.javasoft.decimal.mutable.MutableDecimal${scale}f;
import ch.javasoft.decimal.factory.Factory${scale}f;
import ch.javasoft.decimal.scale.Scale${scale}f;

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
     * ${"9223372036854775807"?substring(0, 19-scale)}<#if (scale>0)>.${"9223372036854775808"?substring(19-scale)}</#if>.
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
		super(SCALE.getDefaultArithmetics().parse(value));
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
        if (value > 0 && value <= MAX_CONSTANT)
            return POS_CONST[(int) value];
        else if (value < 0 && value >= -MAX_CONSTANT)
            return NEG_CONST[(int) -value];
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromLong(value));
	}

	public static Decimal${scale}f valueOf(double value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromDouble(value));
	}

	public static Decimal${scale}f valueOf(double value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetics(roundingMode).fromDouble(value));
	}

	public static Decimal${scale}f valueOf(BigInteger value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromBigInteger(value));
	}

	public static Decimal${scale}f valueOf(BigDecimal value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromBigDecimal(value));
	}

	public static Decimal${scale}f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetics(roundingMode).fromBigDecimal(value));
	}


	public static Decimal${scale}f valueOf(Decimal<?> value) {
		return valueOfUnscaled(value.unscaledValue(), value.getScale());
	}

	public static Decimal${scale}f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return valueOfUnscaled(value.unscaledValue(), value.getScale(), roundingMode);
	}

	public static Decimal${scale}f valueOf(String value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().parse(value));
	}

	public static Decimal${scale}f valueOf(String value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetics(roundingMode).parse(value));
	}

	/**
	 * Converts the specified unscaled decimal with the given scale to a
	 * {@code Decimal${scale}f} value. If the given scale is more precise than the scale
	 * for {@code Decimal${scale}f} and decimals need to be truncated,
	 * {@link RoundingMode#HALF_EVEN HALF_EVEN} rounding mode is applied.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @return the {@code Decimal${scale}f} for the specified unscaled decimal value
	 *         with the given scale
	 */
	public static Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromUnscaled(unscaledValue, scale));
	}

	/**
	 * Converts the specified unscaled decimal with the given scale to a
	 * {@code Decimal${scale}f} value. If the given scale is more precise than the scale
	 * for {@code Decimal${scale}f} and decimals need to be truncated, the specified
	 * rounding mode is applied.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param roundingMode
	 *            the rounding mode to apply if the value needs to be truncated
	 * @return the {@code Decimal${scale}f} for the specified unscaled decimal value
	 *         with the given scale
	 */
	public static Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetics(roundingMode).fromUnscaled(unscaledValue, scale));
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

}
</#list> 