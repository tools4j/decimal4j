<@pp.dropOutputFile />
<#list 0..maxScale as scale>
<@pp.changeOutputFile name=pp.home + "org/decimal4j/mutable/MutableDecimal" + scale + "f.java" />
package org.decimal4j.mutable;

import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.base.AbstractMutableDecimal;
import org.decimal4j.immutable.Decimal${scale}f;
import org.decimal4j.factory.Factory${scale}f;
import org.decimal4j.scale.Scale${scale}f;

@SuppressWarnings("serial")
public class MutableDecimal${scale}f extends
		AbstractMutableDecimal<Scale${scale}f, MutableDecimal${scale}f> implements
		Cloneable {

	/**
	 * Creates a new {@code MutableDecimal${scale}f} with value zero.
	 */
	public MutableDecimal${scale}f() {
		super(0);
	}

	private MutableDecimal${scale}f(long unscaledValue, Scale${scale}f scale) {
		super(unscaledValue);
	}

	public MutableDecimal${scale}f(String value) {
		this(Decimal${scale}f.SCALE.getDefaultArithmetic().parse(value));
	}

	public MutableDecimal${scale}f(String value, RoundingMode roundingMode) {
		this(Decimal${scale}f.SCALE.getArithmetic(roundingMode).parse(value));
	}

	public MutableDecimal${scale}f(long value) {
		this();
		set(value);
	}

	public MutableDecimal${scale}f(double value) {
		this();
		add(value);
	}

	public MutableDecimal${scale}f(double value, RoundingMode roundingMode) {
		this();
		add(value, roundingMode);
	}

	/**
	 * Converts the specified unscaled decimal with the given scale to a
	 * {@code MutableDecimal${scale}f} value. If the given scale is more precise than
	 * the scale for {@code MutableDecimal${scale}f} and decimals need to be
	 * truncated, {@link RoundingMode#HALF_EVEN HALF_EVEN} rounding mode is
	 * applied.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 */
	public MutableDecimal${scale}f(long unscaledValue, int scale) {
		this();
		addUnscaled(unscaledValue, scale);
	}

	/**
	 * Converts the specified unscaled decimal with the given scale to a
	 * {@code MutableDecimal${scale}f} value. If the given scale is more precise than
	 * the scale for {@code MutableDecimal${scale}f} and decimals need to be
	 * truncated, the specified rounding mode is applied.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a decimal number
	 */
	public MutableDecimal${scale}f(long unscaledValue, int scale, RoundingMode roundingMode) {
		this();
		addUnscaled(unscaledValue, scale, roundingMode);
	}

	public MutableDecimal${scale}f(Decimal<Scale${scale}f> value) {
		this(value.unscaledValue(), value.getScaleMetrics());
	}

	@Override
	protected MutableDecimal${scale}f create(long unscaled) {
		return new MutableDecimal${scale}f(unscaled, Decimal${scale}f.SCALE);
	}
	
	@Override
	protected MutableDecimal${scale}f[] createArray(int length) {
		return new MutableDecimal${scale}f[length];
	}

	@Override
	protected MutableDecimal${scale}f self() {
		return this;
	}

	@Override
	public Scale${scale}f getScaleMetrics() {
		return Decimal${scale}f.SCALE;
	}

	@Override
	public Factory${scale}f getFactory() {
		return Decimal${scale}f.FACTORY;
	}

	@Override
	public MutableDecimal${scale}f clone() {
		return new MutableDecimal${scale}f(this);
	}

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to {@code unscaledValue*10<sup>-${scale}}</sup>.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @return a new MutableDecimal${scale}f value initialised with {@code unscaledValue*10^-${scale}}
	 */
	public static MutableDecimal${scale}f unscaled(long unscaledValue) {
		return new MutableDecimal${scale}f(unscaledValue, Decimal${scale}f.SCALE);
	}

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to zero.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 0.
	 */
	public static MutableDecimal${scale}f zero() {
		return new MutableDecimal${scale}f();
	}

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one ULP.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 10<sup>-${scale}</sup><#if scale==0>=1</#if>.
	 */
	public static MutableDecimal${scale}f ulp() {
		return new MutableDecimal${scale}f(Decimal${scale}f.ULP);
	}

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 1.
	 */
	public static MutableDecimal${scale}f one() {
		return new MutableDecimal${scale}f(Decimal${scale}f.ONE);
	}

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to two.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 2.
	 */
	public static MutableDecimal${scale}f two() {
		return new MutableDecimal${scale}f(Decimal${scale}f.TWO);
	}

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to three.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 3.
	 */
	public static MutableDecimal${scale}f three() {
		return new MutableDecimal${scale}f(Decimal${scale}f.THREE);
	}

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to four.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 4.
	 */
	public static MutableDecimal${scale}f four() {
		return new MutableDecimal${scale}f(Decimal${scale}f.FOUR);
	}

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to five.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 5.
	 */
	public static MutableDecimal${scale}f five() {
		return new MutableDecimal${scale}f(Decimal${scale}f.FIVE);
	}

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to six.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 6.
	 */
	public static MutableDecimal${scale}f six() {
		return new MutableDecimal${scale}f(Decimal${scale}f.SIX);
	}

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to seven.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 7.
	 */
	public static MutableDecimal${scale}f seven() {
		return new MutableDecimal${scale}f(Decimal${scale}f.SEVEN);
	}

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to eight.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 8.
	 */
	public static MutableDecimal${scale}f eight() {
		return new MutableDecimal${scale}f(Decimal${scale}f.EIGHT);
	}

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to nine.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 9.
	 */
	public static MutableDecimal${scale}f nine() {
		return new MutableDecimal${scale}f(Decimal${scale}f.NINE);
	}

<#if (scale <= 17)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to ten.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 10.
	 */
	public static MutableDecimal${scale}f ten() {
		return new MutableDecimal${scale}f(Decimal${scale}f.TEN);
	}
<#if (scale <= 16)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one hundred.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 100.
	 */
	public static MutableDecimal${scale}f hundred() {
		return new MutableDecimal${scale}f(Decimal${scale}f.HUNDRED);
	}
<#if (scale <= 15)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one thousand.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 1000.
	 */
	public static MutableDecimal${scale}f thousand() {
		return new MutableDecimal${scale}f(Decimal${scale}f.THOUSAND);
	}
<#if (scale <= 12)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one million.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 10<sup>6</sup>.
	 */
	public static MutableDecimal${scale}f million() {
		return new MutableDecimal${scale}f(Decimal${scale}f.MILLION);
	}
<#if (scale <= 9)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one billion.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 10<sup>9</sup>.
	 */
	public static MutableDecimal${scale}f billion() {
		return new MutableDecimal${scale}f(Decimal${scale}f.BILLION);
	}
<#if (scale <= 6)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one trillion.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 10<sup>12</sup>.
	 */
	public static MutableDecimal${scale}f trillion() {
		return new MutableDecimal${scale}f(Decimal${scale}f.TRILLION);
	}
<#if (scale <= 3)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one quadrillion.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 10<sup>15</sup>.
	 */
	public static MutableDecimal${scale}f quadrillion() {
		return new MutableDecimal${scale}f(Decimal${scale}f.QUADRILLION);
	}
<#if (scale <= 0)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one quintillion.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 10<sup>18</sup>.
	 */
	public static MutableDecimal${scale}f quintillion() {
		return new MutableDecimal${scale}f(Decimal${scale}f.QUINTILLION);
	}
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to minus one.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with -1.
	 */
	public static MutableDecimal${scale}f minusOne() {
		return new MutableDecimal${scale}f(Decimal${scale}f.MINUS_ONE);
	}

<#if (scale >= 1)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one half.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 0.5.
	 */
	public static MutableDecimal${scale}f half() {
		return new MutableDecimal${scale}f(Decimal${scale}f.HALF);
	}

	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one tenth.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 0.1.
	 */
	public static MutableDecimal${scale}f tenth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.TENTH);
	}

<#if (scale >= 2)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one hundredth.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 0.01.
	 */
	public static MutableDecimal${scale}f hundredth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.HUNDREDTH);
	}

<#if (scale >= 3)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one thousandth.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 0.001.
	 */
	public static MutableDecimal${scale}f thousandth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.THOUSANDTH);
	}

<#if (scale >= 6)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one millionth.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 10<sup>-6</sup>.
	 */
	public static MutableDecimal${scale}f millionth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.MILLIONTH);
	}

<#if (scale >= 9)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one billionth.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 10<sup>-9</sup>.
	 */
	public static MutableDecimal${scale}f billionth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.BILLIONTH);
	}

<#if (scale >= 12)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one trillionth.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 10<sup>-12</sup>.
	 */
	public static MutableDecimal${scale}f trillionth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.TRILLIONTH);
	}

<#if (scale >= 15)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one quadrillionth.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 10<sup>-15</sup>.
	 */
	public static MutableDecimal${scale}f quadrillionth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.QUADRILLIONTH);
	}

<#if (scale >= 18)>
	/**
	 * Returns a new MutableDecimal${scale}f whose value is equal to one quintillionth.
	 * 
	 * @return a new MutableDecimal${scale}f value initialised with 10<sup>-18</sup>.
	 */
	public static MutableDecimal${scale}f quintillionth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.QUINTILLIONTH);
	}
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>

	@Override
	public Decimal${scale}f toImmutableDecimal() {
		return Decimal${scale}f.valueOf(this);
	}

	@Override
	public MutableDecimal${scale}f toMutableDecimal() {
		return this;
	}
}
</#list>