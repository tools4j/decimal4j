<@pp.dropOutputFile />
<#list 0..maxScale as scale>
<@pp.changeOutputFile name=pp.home + "org/decimal4j/immutable/Decimal" + scale + "f.java" />
package org.decimal4j.immutable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.base.AbstractImmutableDecimal;
import org.decimal4j.exact.Multipliable${scale}f;
import org.decimal4j.factory.Factory${scale}f;
import org.decimal4j.mutable.MutableDecimal${scale}f;
import org.decimal4j.scale.Scale${scale}f;

/**
 * <tt>Decimal${scale}f</tt> represents an immutable decimal number with a fixed
 * number of ${scale} digits to the right of the decimal point.
 * <p>
 * All methods for this class throw {@code NullPointerException} when passed a
 * {@code null} object reference for any input parameter.
 */
public final class Decimal${scale}f extends AbstractImmutableDecimal<Scale${scale}f, Decimal${scale}f> {

	private static final long serialVersionUID = 1L;

	/** Scale value ${scale} for {@code Decimal${scale}f} returned by {@link #getScale()}.*/
	public static final int SCALE = ${scale};

	/** Scale metrics constant for {@code Decimal${scale}f} returned by {@link #getScaleMetrics()}.*/
	public static final Scale${scale}f METRICS = Scale${scale}f.INSTANCE;

	/** Factory constant for {@code Decimal${scale}f} returned by {@link #getFactory()}.*/
	public static final Factory${scale}f FACTORY = Factory${scale}f.INSTANCE;
	
	/**
	 * Default arithmetic for {@code Decimal${scale}f} performing unchecked operations with rounding mode 
	 * {@link RoundingMode#HALF_UP HALF_UP}.
	 */
	public static final DecimalArithmetic DEFAULT_ARITHMETIC = METRICS.getDefaultArithmetic();
	
	/**
	 * Default arithmetic for {@code Decimal${scale}f} performing checked operations with rounding mode 
	 * {@link RoundingMode#HALF_UP HALF_UP}.
	 */
	public static final DecimalArithmetic DEFAULT_CHECKED_ARITHMETIC = METRICS.getDefaultCheckedArithmetic();

	/** The unscaled long value that represents one.*/
	public static final long ONE_UNSCALED = METRICS.getScaleFactor();

	/** The {@code Decimal${scale}f} constant zero.*/
	public static final Decimal${scale}f ZERO = new Decimal${scale}f(0);
    /**
     * A constant holding the smallest positive value a {@code Decimal${scale}f}
     * can have, 10<sup>-${scale}</sup><#if scale==0>=1</#if>.
     */
	public static final Decimal${scale}f ULP = new Decimal${scale}f(1);

    /**
     * Initialize static constant array when class is loaded.
     */
<#if (scale <= 17)>
    private static final int MAX_CONSTANT = 10;
<#else>
    private static final int MAX_CONSTANT = 9;
</#if>
    private static final Decimal${scale}f POS_CONST[] = new Decimal${scale}f[MAX_CONSTANT+1];
    private static final Decimal${scale}f NEG_CONST[] = new Decimal${scale}f[MAX_CONSTANT+1];

    static {
        for (int i = 1; i <= MAX_CONSTANT; i++) {
            POS_CONST[i] = new Decimal${scale}f(ONE_UNSCALED * i);
            NEG_CONST[i] = new Decimal${scale}f(-ONE_UNSCALED * i);
        }
    }

	/** The {@code Decimal${scale}f} constant 1.*/
	public static final Decimal${scale}f ONE = valueOf(1);
	/** The {@code Decimal${scale}f} constant 2.*/
	public static final Decimal${scale}f TWO = valueOf(2);
	/** The {@code Decimal${scale}f} constant 3.*/
	public static final Decimal${scale}f THREE = valueOf(3);
	/** The {@code Decimal${scale}f} constant 4.*/
	public static final Decimal${scale}f FOUR = valueOf(4);
	/** The {@code Decimal${scale}f} constant 5.*/
	public static final Decimal${scale}f FIVE = valueOf(5);
	/** The {@code Decimal${scale}f} constant 6.*/
	public static final Decimal${scale}f SIX = valueOf(6);
	/** The {@code Decimal${scale}f} constant 7.*/
	public static final Decimal${scale}f SEVEN = valueOf(7);
	/** The {@code Decimal${scale}f} constant 8.*/
	public static final Decimal${scale}f EIGHT = valueOf(8);
	/** The {@code Decimal${scale}f} constant 9.*/
	public static final Decimal${scale}f NINE = valueOf(9);
<#if (scale <= 17)>
	/** The {@code Decimal${scale}f} constant 10.*/
	public static final Decimal${scale}f TEN = valueOf(10);
<#if (scale <= 16)>
	/** The {@code Decimal${scale}f} constant 100.*/
	public static final Decimal${scale}f HUNDRED = new Decimal${scale}f(100 * ONE_UNSCALED);
<#if (scale <= 15)>
	/** The {@code Decimal${scale}f} constant 1000.*/
	public static final Decimal${scale}f THOUSAND = new Decimal${scale}f(1000 * ONE_UNSCALED);
<#if (scale <= 12)>
	/** The {@code Decimal${scale}f} constant 10<sup>6</sup>.*/
	public static final Decimal${scale}f MILLION = new Decimal${scale}f(1000000 * ONE_UNSCALED);
<#if (scale <= 9)>
	/** The {@code Decimal${scale}f} constant 10<sup>9</sup>.*/
	public static final Decimal${scale}f BILLION = new Decimal${scale}f(1000000000 * ONE_UNSCALED);
<#if (scale <= 6)>
	/** The {@code Decimal${scale}f} constant 10<sup>12</sup>.*/
	public static final Decimal${scale}f TRILLION = new Decimal${scale}f(1000000000000L * ONE_UNSCALED);
<#if (scale <= 3)>
	/** The {@code Decimal${scale}f} constant 10<sup>15</sup>.*/
	public static final Decimal${scale}f QUADRILLION = new Decimal${scale}f(1000000000000000L * ONE_UNSCALED);
<#if (scale <= 0)>
	/** The {@code Decimal${scale}f} constant 10<sup>18</sup>.*/
	public static final Decimal${scale}f QUINTILLION = new Decimal${scale}f(1000000000000000000L * ONE_UNSCALED);
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>

	/** The {@code Decimal${scale}f} constant -1.*/
	public static final Decimal${scale}f MINUS_ONE = valueOf(-1);

<#if (scale >= 1)>
	/** The {@code Decimal${scale}f} constant 0.5.*/
	public static final Decimal${scale}f HALF = new Decimal${scale}f(ONE_UNSCALED / 2);
	/** The {@code Decimal${scale}f} constant 0.1.*/
	public static final Decimal${scale}f TENTH = new Decimal${scale}f(ONE_UNSCALED / 10);
<#if (scale >= 2)>
	/** The {@code Decimal${scale}f} constant 0.01.*/
	public static final Decimal${scale}f HUNDREDTH = new Decimal${scale}f(ONE_UNSCALED / 100);
<#if (scale >= 3)>
	/** The {@code Decimal${scale}f} constant 0.001.*/
	public static final Decimal${scale}f THOUSANDTH = new Decimal${scale}f(ONE_UNSCALED / 1000);
<#if (scale >= 6)>
	/** The {@code Decimal${scale}f} constant 10<sup>-6</sup>.*/
	public static final Decimal${scale}f MILLIONTH = new Decimal${scale}f(ONE_UNSCALED / 1000000);
<#if (scale >= 9)>
	/** The {@code Decimal${scale}f} constant 10<sup>-9</sup>.*/
	public static final Decimal${scale}f BILLIONTH = new Decimal${scale}f(ONE_UNSCALED / 1000000000);
<#if (scale >= 12)>
	/** The {@code Decimal${scale}f} constant 10<sup>-12</sup>.*/
	public static final Decimal${scale}f TRILLIONTH = new Decimal${scale}f(ONE_UNSCALED / 1000000000000L);
<#if (scale >= 15)>
	/** The {@code Decimal${scale}f} constant 10<sup>-15</sup>.*/
	public static final Decimal${scale}f QUADRILLIONTH = new Decimal${scale}f(ONE_UNSCALED / 1000000000000000L);
<#if (scale >= 18)>
	/** The {@code Decimal${scale}f} constant 10<sup>-18</sup>.*/
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

	/**
	 * Private constructor with unscaled value.
	 *
	 * @param unscaled the unscaled value
	 */
	private Decimal${scale}f(long unscaled) {
		super(unscaled);
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code Decimal${scale}f}. The string representation consists of an
	 * optional sign, {@code '+'} or {@code '-'} , followed by a sequence of
	 * zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than ${scale} digits, the 
	 * value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An 
	 * exception is thrown if the value is too large to be represented as a 
	 * {@code Decimal${scale}f}.
	 *
	 * @param value
	 *            String value to convert into a {@code Decimal${scale}f}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code Decimal${scale}f}
	 */
	public Decimal${scale}f(String value) {
		super(DEFAULT_CHECKED_ARITHMETIC.parse(value));
	}
	
	@Override
	public final Scale${scale}f getScaleMetrics() {
		return METRICS;
	}

	@Override
	public final int getScale() {
		return SCALE;
	}

	@Override
	public final Factory${scale}f getFactory() {
		return FACTORY;
	}

	@Override
	protected final Decimal${scale}f self() {
		return this;
	}

	@Override
	protected final DecimalArithmetic getDefaultArithmetic() {
		return DEFAULT_ARITHMETIC;
	}
	
	@Override
	protected final DecimalArithmetic getDefaultCheckedArithmetic() {
		return DEFAULT_CHECKED_ARITHMETIC;
	}
	
	@Override
	protected final DecimalArithmetic getRoundingDownArithmetic() {
		return METRICS.getRoundingDownArithmetic();
	}
	
	@Override
	protected final DecimalArithmetic getRoundingFloorArithmetic() {
		return METRICS.getRoundingFloorArithmetic();
	}
	
	@Override
	protected final DecimalArithmetic getRoundingHalfEvenArithmetic() {
		return METRICS.getRoundingHalfEvenArithmetic();
	}
	
	@Override
	protected final DecimalArithmetic getRoundingUnnecessaryArithmetic() {
		return METRICS.getRoundingUnnecessaryArithmetic();
	}

 	/**
	 * Returns a {@code Decimal${scale}f} whose value is numerically equal to
	 * that of the specified {@code long} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code Decimal${scale}f}.
	 *
	 * @param value
	 *            long value to convert into a {@code Decimal${scale}f}
	 * @return a {@code Decimal${scale}f} value numerically equal to the specified 
	 *            {@code long} value
	 * @throws IllegalArgumentException
	 *            if {@code value} is too large to be represented as a 
	 *            {@code Decimal${scale}f}
	 */
	public static Decimal${scale}f valueOf(long value) {
        if (value == 0)
            return ZERO;
        if (value > 0 & value <= MAX_CONSTANT)
            return POS_CONST[(int) value];
        else if (value < 0 & value >= -MAX_CONSTANT)
            return NEG_CONST[(int) -value];
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromLong(value));
	}

	/**
	 * Returns a {@code Decimal${scale}f} whose value is calculated by
	 * rounding the specified {@code float} argument to scale ${scale}
	 * using {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown
	 * if the specified value is too large to be represented as a {@code Decimal${scale}f}. 
	 *
	 * @param value
	 *            float value to convert into a {@code Decimal${scale}f}
	 * @return a {@code Decimal${scale}f} calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is
	 *             too large for the float to be represented as a {@code Decimal${scale}f}
	 */
	public static Decimal${scale}f valueOf(float value) {
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromFloat(value));
	}

	/**
	 * Returns a {@code Decimal${scale}f} whose value is calculated by
	 * rounding the specified {@code float} argument to scale ${scale}
	 * using the specified {@code roundingMode}. An exception is thrown
	 * if the specified value is too large to be represented as a {@code Decimal${scale}f}. 
	 *
	 * @param value
	 *            float value to convert into a {@code Decimal${scale}f}
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a {@code Decimal${scale}f} calculated as: <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is
	 *             too large for the float to be represented as a {@code Decimal${scale}f}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	public static Decimal${scale}f valueOf(float value, RoundingMode roundingMode) {
		return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).fromFloat(value));
	}

	/**
	 * Returns a {@code Decimal${scale}f} whose value is calculated by
	 * rounding the specified {@code double} argument to scale ${scale}
	 * using {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown
	 * if the specified value is too large to be represented as a {@code Decimal${scale}f}. 
	 *
	 * @param value
	 *            double value to convert into a {@code Decimal${scale}f}
	 * @return a {@code Decimal${scale}f} calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is
	 *             too large for the double to be represented as a {@code Decimal${scale}f}
	 */
	public static Decimal${scale}f valueOf(double value) {
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromDouble(value));
	}

	/**
	 * Returns a {@code Decimal${scale}f} whose value is calculated by
	 * rounding the specified {@code double} argument to scale ${scale}
	 * using the specified {@code roundingMode}. An exception is thrown
	 * if the specified value is too large to be represented as a {@code Decimal${scale}f}. 
	 *
	 * @param value
	 *            double value to convert into a {@code Decimal${scale}f}
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a {@code Decimal${scale}f} calculated as: <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is
	 *             too large for the double to be represented as a {@code Decimal${scale}f}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	public static Decimal${scale}f valueOf(double value, RoundingMode roundingMode) {
		return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).fromDouble(value));
	}

	/**
	 * Returns a {@code Decimal${scale}f} whose value is numerically equal to that of
	 * the specified {@link BigInteger} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code Decimal${scale}f}.
	 *
	 * @param value
	 *            {@code BigInteger} value to convert into a {@code Decimal${scale}f}
	 * @return a {@code Decimal${scale}f} value numerically equal to the specified big 
	 *         integer value
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code Decimal${scale}f}
	 */
	public static Decimal${scale}f valueOf(BigInteger value) {
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromBigInteger(value));
	}

	/**
	 * Returns a {@code Decimal${scale}f} whose value is calculated by rounding
	 * the specified {@link BigDecimal} argument to scale ${scale} using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the 
	 * specified value is too large to be represented as a {@code Decimal${scale}f}.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into a {@code Decimal${scale}f}
	 * @return a {@code Decimal${scale}f} calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code Decimal${scale}f}
	 */
	public static Decimal${scale}f valueOf(BigDecimal value) {
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromBigDecimal(value));
	}

	/**
	 * Returns a {@code Decimal${scale}f} whose value is calculated by rounding
	 * the specified {@link BigDecimal} argument to scale ${scale} using 
	 * the specified {@code roundingMode}. An exception is thrown if the 
	 * specified value is too large to be represented as a {@code Decimal${scale}f}.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into a {@code Decimal${scale}f}
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a {@code Decimal${scale}f} calculated as: <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code Decimal${scale}f}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	public static Decimal${scale}f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).fromBigDecimal(value));
	}

	/**
	 * Returns a {@code Decimal${scale}f} whose value is calculated by rounding
	 * the specified {@link Decimal} argument to scale ${scale} using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the
	 * specified value is too large to be represented as a {@code Decimal${scale}f}. 
	 *
	 * @param value
	 *            Decimal value to convert into a {@code Decimal${scale}f} 
	 * @return a {@code Decimal${scale}f} calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code Decimal${scale}f}
	 */
	public static Decimal${scale}f valueOf(Decimal<?> value) {
		if (value instanceof Decimal${scale}f) {
			return (Decimal${scale}f)value;
		}
		return valueOfUnscaled(value.unscaledValue(), value.getScale());
	}

	/**
	 * Returns a {@code Decimal${scale}f} whose value is calculated by rounding
	 * the specified {@link Decimal} argument to scale ${scale} using
	 * the specified {@code roundingMode}. An exception is thrown if the
	 * specified value is too large to be represented as a {@code Decimal${scale}f}. 
	 *
	 * @param value
	 *            Decimal value to convert into a {@code Decimal${scale}f} 
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a {@code Decimal${scale}f} calculated as: <tt>round(value)</tt>
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code Decimal${scale}f}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	public static Decimal${scale}f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		if (value instanceof Decimal${scale}f) {
			return (Decimal${scale}f)value;
		}
		return valueOfUnscaled(value.unscaledValue(), value.getScale(), roundingMode);
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code Decimal${scale}f}. The string representation consists of an
	 * optional sign, {@code '+'} or {@code '-'} , followed by a sequence of
	 * zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than ${scale} digits, the 
	 * value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An 
	 * exception is thrown if the value is too large to be represented as a 
	 * {@code Decimal${scale}f}.
	 *
	 * @param value
	 *            String value to convert into a {@code Decimal${scale}f}
	 * @return a {@code Decimal${scale}f} calculated as: <tt>round<sub>HALF_UP</sub>(value)</tt>
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code Decimal${scale}f}
	 */
	public static Decimal${scale}f valueOf(String value) {
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.parse(value));
	}

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code Decimal${scale}f}. The string representation consists of an
	 * optional sign, {@code '+'} or {@code '-'} , followed by a sequence of
	 * zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than ${scale} digits, the 
	 * value is rounded using the specified {@code roundingMode}. An exception 
	 * is thrown if the value is too large to be represented as a {@code Decimal${scale}f}.
	 *
	 * @param value
	 *            String value to convert into a {@code Decimal${scale}f}
	 * @param roundingMode
	 *            the rounding mode to apply if the fraction contains more than
	 *            ${scale} digits
	 * @return a {@code Decimal${scale}f} calculated as: <tt>round(value)</tt>
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code Decimal${scale}f}
	 * @throws ArithmeticException
	 *             if {@code roundingMode==UNNECESSARY} and rounding is
	 *             necessary
	 */
	public static Decimal${scale}f valueOf(String value, RoundingMode roundingMode) {
		return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).parse(value));
	}

	/**
	 * Returns a {@code Decimal${scale}f} whose value is numerically equal to
	 * <tt>(unscaledValue &times; 10<sup>-${scale}</sup>)</tt>.
	 *
	 * @param unscaledValue
	 *            unscaled value to convert into a {@code Decimal${scale}f}
	 * @return a {@code Decimal${scale}f} calculated as:
	 *         <tt>unscaledValue &times; 10<sup>-${scale}</sup></tt>
	 */
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

	/**
	 * Returns a {@code Decimal${scale}f} whose value is numerically equal to
	 * <tt>(unscaledValue &times; 10<sup>-scale</sup>)</tt>. The result is
	 * rounded to scale ${scale} using {@link RoundingMode#HALF_UP HALF_UP} 
	 * rounding. An exception is thrown if the specified value is too large 
	 * to be represented as a {@code Decimal${scale}f}.
	 *
	 * @param unscaledValue
	 *            unscaled value to convert into a {@code Decimal${scale}f}
	 * @param scale
	 *            the scale to apply to {@code unscaledValue}
	 * @return a {@code Decimal${scale}f} calculated as:
	 *         <tt>round<sub>HALF_UP</sub>(unscaledValue &times; 10<sup>-scale</sup>)</tt>
	 * @throws IllegalArgumentException
	 *             if the specified value is too large to be represented as a 
	 *             {@code Decimal${scale}f}
	 */
	public static Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale) {
		return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromUnscaled(unscaledValue, scale));
	}

	/**
	 * Returns a {@code Decimal${scale}f} whose value is numerically equal to
	 * <tt>(unscaledValue &times; 10<sup>-scale</sup>)</tt>. The result
	 * is rounded to scale ${scale} using the specified {@code roundingMode}. 
	 * An exception is thrown if the specified value is too large to be 
	 * represented as a {@code Decimal${scale}f}.
	 *
	 * @param unscaledValue
	 *            unscaled value to convert into a Decimal${scale}
	 * @param scale
	 *            the scale to apply to {@code unscaledValue}
	 * @param roundingMode
	 *            the rounding mode to apply during the conversion if necessary
	 * @return a {@code Decimal${scale}f} calculated as:
	 *         <tt>round(unscaledValue &times; 10<sup>-scale</sup>)</tt>
	 * @throws IllegalArgumentException
	 *             if the specified value is too large to be represented as a {@code Decimal${scale}f}
	 */
	public static Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).fromUnscaled(unscaledValue, scale));
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
	
	/**
	 * Returns this {@code Decimal} as a multipliable factor for exact 
	 * typed exact multiplication. The second factor is passed to one of
	 * the {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of {@code this} Decimal and the
	 * second factor passed to the {@code by(..)} method.
	 * <p>
	 * The method is similar to {@link #multiplyExact(Decimal) multiplyExact(Decimal)} but the result
	 * is retrieved in exact typed form with the correct result scale. 
	 * <p>
	 * For instance one can write:
	 * <pre>
<#if (scale+2<=maxScale)>
	 * Decimal${scale+2}f product = this.multiplyExact().by(Decimal2f.FIVE);
<#else>
	 * Decimal${scale}f product = this.multiplyExact().by(Decimal0f.FIVE);
</#if>
	 * </pre>
	 * 
	 * @return a multipliable object encapsulating this Decimal as first factor
	 *             of an exact multiplication
	 */
	public Multipliable${scale}f multiplyExact() {
		return new Multipliable${scale}f(this);
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