<@pp.dropOutputFile />
<#list 0..maxScale as scale>
<@pp.changeOutputFile name=pp.home + "ch/javasoft/decimal/mutable/MutableDecimal" + scale + "f.java" />
package ch.javasoft.decimal.mutable;

import java.math.RoundingMode;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.base.AbstractMutableDecimal;
import ch.javasoft.decimal.immutable.Decimal${scale}f;
import ch.javasoft.decimal.scale.Scale${scale}f;

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
		this(Decimal${scale}f.SCALE.getDefaultArithmetics().parse(value));
	}

	public MutableDecimal${scale}f(String value, RoundingMode roundingMode) {
		this(Decimal${scale}f.SCALE.getArithmetics(roundingMode).parse(value));
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
	public MutableDecimal${scale}f clone() {
		return new MutableDecimal${scale}f(this);
	}

	public static MutableDecimal${scale}f unscaled(long unscaledValue) {
		return new MutableDecimal${scale}f(unscaledValue, Decimal${scale}f.SCALE);
	}

	public static MutableDecimal${scale}f zero() {
		return new MutableDecimal${scale}f();
	}

	public static MutableDecimal${scale}f ulp() {
		return new MutableDecimal${scale}f(Decimal${scale}f.ULP);
	}

	public static MutableDecimal${scale}f one() {
		return new MutableDecimal${scale}f(Decimal${scale}f.ONE);
	}

	public static MutableDecimal${scale}f two() {
		return new MutableDecimal${scale}f(Decimal${scale}f.TWO);
	}

	public static MutableDecimal${scale}f three() {
		return new MutableDecimal${scale}f(Decimal${scale}f.THREE);
	}

	public static MutableDecimal${scale}f four() {
		return new MutableDecimal${scale}f(Decimal${scale}f.FOUR);
	}

	public static MutableDecimal${scale}f five() {
		return new MutableDecimal${scale}f(Decimal${scale}f.FIVE);
	}

	public static MutableDecimal${scale}f six() {
		return new MutableDecimal${scale}f(Decimal${scale}f.SIX);
	}

	public static MutableDecimal${scale}f seven() {
		return new MutableDecimal${scale}f(Decimal${scale}f.SEVEN);
	}

	public static MutableDecimal${scale}f eigth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.EIGHT);
	}

	public static MutableDecimal${scale}f nine() {
		return new MutableDecimal${scale}f(Decimal${scale}f.NINE);
	}

	public static MutableDecimal${scale}f ten() {
		return new MutableDecimal${scale}f(Decimal${scale}f.TEN);
	}

	public static MutableDecimal${scale}f minusOne() {
		return new MutableDecimal${scale}f(Decimal${scale}f.MINUS_ONE);
	}

	public static MutableDecimal${scale}f half() {
		return new MutableDecimal${scale}f(Decimal${scale}f.HALF);
	}

	public static MutableDecimal${scale}f tenth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.TENTH);
	}

	public static MutableDecimal${scale}f hundredth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.HUNDREDTH);
	}

	public static MutableDecimal${scale}f thousandth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.THOUSANDTH);
	}

	public static MutableDecimal${scale}f millionth() {
		return new MutableDecimal${scale}f(Decimal${scale}f.MILLIONTH);
	}

	@Override
	public Decimal${scale}f toImmutableDecimal() {
		return Decimal${scale}f.valueOf(this);
	}

}
</#list>