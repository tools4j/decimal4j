package ch.javasoft.decimal.mutable;

import java.math.RoundingMode;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.base.AbstractMutableDecimal;
import ch.javasoft.decimal.immutable.Decimal0f;
import ch.javasoft.decimal.scale.Scale0f;

@SuppressWarnings("serial")
public class MutableDecimal0f extends
		AbstractMutableDecimal<Scale0f, MutableDecimal0f> implements Cloneable {

	/**
	 * Creates a new {@code MutableDecimal0f} with value zero.
	 */
	public MutableDecimal0f() {
		super(0);
	}

	private MutableDecimal0f(long unscaledValue, Scale0f scale) {
		super(unscaledValue);
	}

	public MutableDecimal0f(String value) {
		this(Decimal0f.SCALE.getDefaultArithmetics().parse(value));
	}

	public MutableDecimal0f(String value, RoundingMode roundingMode) {
		this(Decimal0f.SCALE.getArithmetics(roundingMode).parse(value));
	}

	public MutableDecimal0f(long value) {
		this();
		set(value);
	}

	public MutableDecimal0f(double value) {
		this();
		add(value);
	}

	public MutableDecimal0f(double value, RoundingMode roundingMode) {
		this();
		add(value, roundingMode);
	}

	/**
	 * Converts the specified unscaled decimal with the given scale to a
	 * {@code MutableDecimal0f} value. If the given scale is more precise than
	 * the scale for {@code MutableDecimal0f} and decimals need to be truncated,
	 * {@link RoundingMode#HALF_EVEN HALF_EVEN} rounding mode is applied.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 */
	public MutableDecimal0f(long unscaledValue, int scale) {
		this();
		addUnscaled(unscaledValue, scale);
	}

	/**
	 * Converts the specified unscaled decimal with the given scale to a
	 * {@code MutableDecimal0f} value. If the given scale is more precise than
	 * the scale for {@code MutableDecimal0f} and decimals need to be truncated,
	 * the specified rounding mode is applied.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a decimal number
	 */
	public MutableDecimal0f(long unscaledValue, int scale, RoundingMode roundingMode) {
		this();
		addUnscaled(unscaledValue, scale, roundingMode);
	}

	public MutableDecimal0f(Decimal<Scale0f> value) {
		this(value.unscaledValue(), value.getScaleMetrics());
	}

	@Override
	protected MutableDecimal0f create(long unscaled) {
		return new MutableDecimal0f(unscaled, Decimal0f.SCALE);
	}
	
	@Override
	protected MutableDecimal0f[] createArray(int length) {
		return new MutableDecimal0f[length];
	}

	@Override
	protected MutableDecimal0f self() {
		return this;
	}

	@Override
	public Scale0f getScaleMetrics() {
		return Decimal0f.SCALE;
	}

	@Override
	public MutableDecimal0f clone() {
		return new MutableDecimal0f(this);
	}

	public static MutableDecimal0f unscaled(long unscaledValue) {
		return new MutableDecimal0f(unscaledValue, Decimal0f.SCALE);
	}

	public static MutableDecimal0f zero() {
		return new MutableDecimal0f();
	}

	public static MutableDecimal0f ulp() {
		return new MutableDecimal0f(Decimal0f.ULP);
	}

	public static MutableDecimal0f one() {
		return new MutableDecimal0f(Decimal0f.ONE);
	}

	public static MutableDecimal0f two() {
		return new MutableDecimal0f(Decimal0f.TWO);
	}

	public static MutableDecimal0f three() {
		return new MutableDecimal0f(Decimal0f.THREE);
	}

	public static MutableDecimal0f four() {
		return new MutableDecimal0f(Decimal0f.FOUR);
	}

	public static MutableDecimal0f five() {
		return new MutableDecimal0f(Decimal0f.FIVE);
	}

	public static MutableDecimal0f six() {
		return new MutableDecimal0f(Decimal0f.SIX);
	}

	public static MutableDecimal0f seven() {
		return new MutableDecimal0f(Decimal0f.SEVEN);
	}

	public static MutableDecimal0f eigth() {
		return new MutableDecimal0f(Decimal0f.EIGHT);
	}

	public static MutableDecimal0f nine() {
		return new MutableDecimal0f(Decimal0f.NINE);
	}

	public static MutableDecimal0f ten() {
		return new MutableDecimal0f(Decimal0f.TEN);
	}

	public static MutableDecimal0f hundred() {
		return new MutableDecimal0f(Decimal0f.HUNDRED);
	}

	public static MutableDecimal0f thousand() {
		return new MutableDecimal0f(Decimal0f.THOUSAND);
	}

	public static MutableDecimal0f million() {
		return new MutableDecimal0f(Decimal0f.MILLION);
	}

	public static MutableDecimal0f billion() {
		return new MutableDecimal0f(Decimal0f.BILLION);
	}

	public static MutableDecimal0f minusOne() {
		return new MutableDecimal0f(Decimal0f.MINUS_ONE);
	}

	@Override
	public Decimal0f toImmutableDecimal() {
		return Decimal0f.valueOf(this);
	}

}
