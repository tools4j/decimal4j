<@pp.dropOutputFile />
<#list 0..maxScale as scale>
<@pp.changeOutputFile name=pp.home + "org/decimal4j/mutable/MutableDecimal" + scale + "f.java" />
package org.decimal4j.mutable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.base.AbstractMutableDecimal;
import org.decimal4j.immutable.Decimal${scale}f;
import org.decimal4j.factory.Factory${scale}f;
import org.decimal4j.scale.Scale${scale}f;

/**
 * <tt>MutableDecimal${scale}f</tt> represents a mutable decimal number with a fixed
 * number of ${scale} digits to the right of the decimal point.
 */
public final class MutableDecimal${scale}f extends AbstractMutableDecimal<Scale${scale}f, MutableDecimal${scale}f> implements Cloneable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@code MutableDecimal${scale}f} with value zero.
	 * @see #zero()
	 */
	public MutableDecimal${scale}f() {
		super(0);
	}

	/**
	 * Private constructor with unscaled value.
	 *
	 * @param unscaledValue the unscaled value
	 * @param scale 		the scale metrics used to distinguish this constructor signature
	 *						from {@link #MutableDecimal${scale}f(long)}
	 */
	private MutableDecimal${scale}f(long unscaledValue, Scale${scale}f scale) {
		super(unscaledValue);
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code MutableDecimal${scale}f}. The string representation consists 
	 * of an optional sign, {@code '+'} or {@code '-'} , followed by a sequence 
	 * of zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than ${scale} digits, the 
	 * value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An 
	 * exception is thrown if the value is too large to be represented as a 
	 * {@code MutableDecimal${scale}f}.
	 *
	 * @param value
	 *            String value to convert into a {@code MutableDecimal${scale}f}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code MutableDecimal${scale}f}
	 * @see #set(String, RoundingMode)
	 */
	public MutableDecimal${scale}f(String value) {
		this();
		set(value);
	}

 	/**
	 * Constructs a {@code MutableDecimal${scale}f} whose value is numerically equal 
	 * to that of the specified {@code long} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal${scale}f}.
	 *
	 * @param value
	 *            long value to convert into a {@code MutableDecimal${scale}f}
	 * @throws IllegalArgumentException
	 *            if {@code value} is too large to be represented as a 
	 *            {@code MutableDecimal${scale}f}
	 */
	public MutableDecimal${scale}f(long value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal${scale}f} whose value is calculated by
	 * rounding the specified {@code double} argument to scale ${scale} using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal${scale}f}. 
	 *
	 * @param value
	 *            double value to convert into a {@code MutableDecimal${scale}f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large
	 *             for the double to be represented as a {@code MutableDecimal${scale}f}
	 * @see #set(double, RoundingMode)
	 * @see #set(float)
	 * @see #set(float, RoundingMode)
	 */
	public MutableDecimal${scale}f(double value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal${scale}f} whose value is numerically equal to
	 * that of the specified {@link BigInteger} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal${scale}f}.
	 *
	 * @param value
	 *            {@code BigInteger} value to convert into a {@code MutableDecimal${scale}f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal${scale}f}
	 */
	public MutableDecimal${scale}f(BigInteger value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal${scale}f} whose value is calculated by
	 * rounding the specified {@link BigDecimal} argument to scale ${scale} using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the 
	 * specified value is too large to be represented as a {@code MutableDecimal${scale}f}.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into a {@code MutableDecimal${scale}f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal${scale}f}
	 * @see #set(BigDecimal, RoundingMode)
	 */
	public MutableDecimal${scale}f(BigDecimal value) {
		this();
		set(value);
	}

	/**
	 * Constructs a {@code MutableDecimal${scale}f} whose value is calculated by
	 * rounding the specified {@link Decimal} argument to scale ${scale} using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if 
	 * the specified value is too large to be represented as a {@code MutableDecimal${scale}f}. 
	 *
	 * @param value
	 *            Decimal value to convert into a {@code MutableDecimal${scale}f} 
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal${scale}f}
	 * @see #set(Decimal, RoundingMode)
	 */
	public MutableDecimal${scale}f(Decimal<?> value) {
		this();
		setUnscaled(value.unscaledValue(), value.getScale());
	}

	@Override
	protected final MutableDecimal${scale}f create(long unscaled) {
		return new MutableDecimal${scale}f(unscaled, Decimal${scale}f.METRICS);
	}
	
	@Override
	protected final MutableDecimal${scale}f[] createArray(int length) {
		return new MutableDecimal${scale}f[length];
	}

	@Override
	protected final MutableDecimal${scale}f self() {
		return this;
	}

	@Override
	public final Scale${scale}f getScaleMetrics() {
		return Decimal${scale}f.METRICS;
	}

	@Override
	public final int getScale() {
		return Decimal${scale}f.SCALE;
	}

	@Override
	public Factory${scale}f getFactory() {
		return Decimal${scale}f.FACTORY;
	}
	
	@Override
	protected DecimalArithmetic getDefaultArithmetic() {
		return Decimal${scale}f.DEFAULT_ARITHMETIC;
	}
	
	@Override
	protected DecimalArithmetic getDefaultCheckedArithmetic() {
		return Decimal${scale}f.METRICS.getDefaultCheckedArithmetic();
	}

	@Override
	protected DecimalArithmetic getRoundingDownArithmetic() {
		return Decimal${scale}f.METRICS.getRoundingDownArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingFloorArithmetic() {
		return Decimal${scale}f.METRICS.getRoundingFloorArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingHalfEvenArithmetic() {
		return Decimal${scale}f.METRICS.getRoundingHalfEvenArithmetic();
	}
	
	@Override
	protected DecimalArithmetic getRoundingUnnecessaryArithmetic() {
		return Decimal${scale}f.METRICS.getRoundingUnnecessaryArithmetic();
	}

	@Override
	public MutableDecimal${scale}f clone() {
		return new MutableDecimal${scale}f(unscaledValue(), Decimal${scale}f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to 
	 * <code>unscaledValue * 10<sup>-${scale}</sup></code>.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @return a new {@code MutableDecimal${scale}f} value initialised with <code>unscaledValue * 10<sup>-${scale}</sup></code>
	 * @see #setUnscaled(long, int)
	 * @see #setUnscaled(long, int, RoundingMode)
	 */
	public static MutableDecimal${scale}f unscaled(long unscaledValue) {
		return new MutableDecimal${scale}f(unscaledValue, Decimal${scale}f.METRICS);
	}

	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to zero.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 0.
	 */
	public static MutableDecimal${scale}f zero() {
		return new MutableDecimal${scale}f();
	}

	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one ULP.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>-${scale}</sup><#if scale==0>=1</#if>.
	 */
	public static MutableDecimal${scale}f ulp() {
		return new MutableDecimal${scale}f(Decimal${scale}f.ULP);
	}

	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 1.
	 */
	public static MutableDecimal${scale}f one() {
		return new MutableDecimal${scale}f(Decimal${scale}f.ONE);
	}

	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to two.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 2.
	 */
	public static MutableDecimal${scale}f two() {
		return new MutableDecimal${scale}f(Decimal${scale}f.TWO);
	}

	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to three.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 3.
	 */
	public static MutableDecimal${scale}f three() {
		return new MutableDecimal${scale}f(Decimal${scale}f.THREE);
	}

	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to four.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 4.
	 */
	public static MutableDecimal${scale}f four() {
		return new MutableDecimal${scale}f(Decimal${scale}f.FOUR);
	}

	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to five.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 5.
	 */
	public static MutableDecimal${scale}f five() {
		return new MutableDecimal${scale}f(Decimal${scale}f.FIVE);
	}

	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to six.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 6.
	 */
	public static MutableDecimal${scale}f six() {
		return new MutableDecimal${scale}f(Decimal${scale}f.SIX);
	}

	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to seven.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 7.
	 */
	public static MutableDecimal${scale}f seven() {
		return new MutableDecimal${scale}f(Decimal${scale}f.SEVEN);
	}

	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to eight.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 8.
	 */
	public static MutableDecimal${scale}f eight() {
		return new MutableDecimal${scale}f(Decimal${scale}f.EIGHT);
	}

	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to nine.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 9.
	 */
	public static MutableDecimal${scale}f nine() {
		return new MutableDecimal${scale}f(Decimal${scale}f.NINE);
	}

<#if (scale <= 17)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to ten.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 10.
	 */
	public static MutableDecimal${scale}f ten() {
		return new MutableDecimal${scale}f(Decimal${scale}f.TEN);
	}
<#if (scale <= 16)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one hundred.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 100.
	 */
	public static MutableDecimal${scale}f hundred() {
		return new MutableDecimal${scale}f(Decimal${scale}f.HUNDRED);
	}
<#if (scale <= 15)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one thousand.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 1000.
	 */
	public static MutableDecimal${scale}f thousand() {
		return new MutableDecimal${scale}f(Decimal${scale}f.THOUSAND);
	}
<#if (scale <= 12)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one million.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>6</sup>.
	 */
	public static MutableDecimal${scale}f million() {
		return new MutableDecimal${scale}f(Decimal${scale}f.MILLION);
	}
<#if (scale <= 9)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one billion.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>9</sup>.
	 */
	public static MutableDecimal${scale}f billion() {
		return new MutableDecimal${scale}f(Decimal${scale}f.BILLION);
	}
<#if (scale <= 6)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one trillion.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>12</sup>.
	 */
	public static MutableDecimal${scale}f trillion() {
		return new MutableDecimal${scale}f(Decimal${scale}f.TRILLION);
	}
<#if (scale <= 3)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one quadrillion.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>15</sup>.
	 */
	public static MutableDecimal${scale}f quadrillion() {
		return new MutableDecimal${scale}f(Decimal${scale}f.QUADRILLION);
	}
<#if (scale <= 0)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one quintillion.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>18</sup>.
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
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to minus one.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with -1.
	 */
	public static MutableDecimal${scale}f minusOne() {
		return new MutableDecimal${scale}f(Decimal${scale}f.MINUS_ONE);
	}

<#if (scale >= 1)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one half.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 0.5.
	 */
	public static MutableDecimal${scale}f half() {
		return new MutableDecimal${scale}f(Decimal${scale}f.HALF);
	}

	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one tenth.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 0.1.
	 */
	public static MutableDecimal${scale}f tenth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.TENTH);
	}

<#if (scale >= 2)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one hundredth.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 0.01.
	 */
	public static MutableDecimal${scale}f hundredth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.HUNDREDTH);
	}

<#if (scale >= 3)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one thousandth.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 0.001.
	 */
	public static MutableDecimal${scale}f thousandth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.THOUSANDTH);
	}

<#if (scale >= 6)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one millionth.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>-6</sup>.
	 */
	public static MutableDecimal${scale}f millionth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.MILLIONTH);
	}

<#if (scale >= 9)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one billionth.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>-9</sup>.
	 */
	public static MutableDecimal${scale}f billionth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.BILLIONTH);
	}

<#if (scale >= 12)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one trillionth.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>-12</sup>.
	 */
	public static MutableDecimal${scale}f trillionth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.TRILLIONTH);
	}

<#if (scale >= 15)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one quadrillionth.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>-15</sup>.
	 */
	public static MutableDecimal${scale}f quadrillionth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.QUADRILLIONTH);
	}

<#if (scale >= 18)>
	/**
	 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one quintillionth.
	 * 
	 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>-18</sup>.
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