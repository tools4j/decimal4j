package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.ScaleMetrics.Scale17f;

@SuppressWarnings("serial")
public class MutableDecimal17f extends
		AbstractMutableDecimal<Scale17f, MutableDecimal17f> implements
		Cloneable {

	/**
	 * Creates a new {@code MutableDecimal17f} with value zero.
	 */
	public MutableDecimal17f() {
		super(0);
	}

	private MutableDecimal17f(long unscaledValue, Scale17f scale) {
		super(unscaledValue);
	}

	public MutableDecimal17f(String value) {
		this(Decimal17f.SCALE.getDefaultArithmetics().parse(value));
	}

	public MutableDecimal17f(String value, RoundingMode roundingMode) {
		this(Decimal17f.SCALE.getArithmetics(roundingMode).parse(value));
	}

	public MutableDecimal17f(long value) {
		this();
		set(value);
	}

	public MutableDecimal17f(double value) {
		this();
		add(value);
	}

	public MutableDecimal17f(double value, RoundingMode roundingMode) {
		this();
		add(value, roundingMode);
	}

	public MutableDecimal17f(BigInteger value) {
		this();
		add(value);
	}

	public MutableDecimal17f(BigDecimal value, RoundingMode roundingMode) {
		this();
		add(value, roundingMode);
	}

	/**
	 * Converts the specified unscaled decimal with the given scale to a
	 * {@code MutableDecimal17f} value. If the given scale is more precise than
	 * the scale for {@code MutableDecimal17f} and decimals need to be
	 * truncated, {@link RoundingMode#HALF_EVEN HALF_EVEN} rounding mode is
	 * applied.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 */
	public MutableDecimal17f(long unscaledValue, int scale) {
		this();
		addUnscaled(unscaledValue, scale);
	}

	/**
	 * Converts the specified unscaled decimal with the given scale to a
	 * {@code MutableDecimal17f} value. If the given scale is more precise than
	 * the scale for {@code MutableDecimal17f} and decimals need to be
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
	public MutableDecimal17f(long unscaledValue, int scale, RoundingMode roundingMode) {
		this();
		addUnscaled(unscaledValue, scale, roundingMode);
	}

	public MutableDecimal17f(Decimal<Scale17f> value) {
		this(value.unscaledValue(), value.getScaleMetrics());
	}

	@Override
	protected MutableDecimal17f create(long unscaled) {
		return new MutableDecimal17f(unscaled, Decimal17f.SCALE);
	}

	@Override
	protected MutableDecimal17f self() {
		return this;
	}

	@Override
	public Scale17f getScaleMetrics() {
		return Decimal17f.SCALE;
	}

	@Override
	public MutableDecimal17f clone() {
		return new MutableDecimal17f(this);
	}

	public static MutableDecimal17f unscaled(long unscaledValue) {
		return new MutableDecimal17f(unscaledValue, Decimal17f.SCALE);
	}

	public static MutableDecimal17f zero() {
		return new MutableDecimal17f();
	}

	public static MutableDecimal17f ulp() {
		return new MutableDecimal17f(Decimal17f.ULP);
	}

	public static MutableDecimal17f one() {
		return new MutableDecimal17f(Decimal17f.ONE);
	}

	public static MutableDecimal17f two() {
		return new MutableDecimal17f(Decimal17f.TWO);
	}

	public static MutableDecimal17f three() {
		return new MutableDecimal17f(Decimal17f.THREE);
	}

	public static MutableDecimal17f four() {
		return new MutableDecimal17f(Decimal17f.FOUR);
	}

	public static MutableDecimal17f five() {
		return new MutableDecimal17f(Decimal17f.FIVE);
	}

	public static MutableDecimal17f six() {
		return new MutableDecimal17f(Decimal17f.SIX);
	}

	public static MutableDecimal17f seven() {
		return new MutableDecimal17f(Decimal17f.SEVEN);
	}

	public static MutableDecimal17f eigth() {
		return new MutableDecimal17f(Decimal17f.EIGHT);
	}

	public static MutableDecimal17f nine() {
		return new MutableDecimal17f(Decimal17f.NINE);
	}

	public static MutableDecimal17f ten() {
		return new MutableDecimal17f(Decimal17f.TEN);
	}

	public static MutableDecimal17f minusOne() {
		return new MutableDecimal17f(Decimal17f.MINUS_ONE);
	}

	public static MutableDecimal17f half() {
		return new MutableDecimal17f(Decimal17f.HALF);
	}

	public static MutableDecimal17f tenth() {
		return new MutableDecimal17f(Decimal17f.TENTH);
	}

	public static MutableDecimal17f hundredth() {
		return new MutableDecimal17f(Decimal17f.HUNDREDTH);
	}

	public static MutableDecimal17f thousandth() {
		return new MutableDecimal17f(Decimal17f.THOUSANDTH);
	}

	public static MutableDecimal17f millionth() {
		return new MutableDecimal17f(Decimal17f.MILLIONTH);
	}

	@Override
	public Decimal17f toImmutableDecimal() {
		return Decimal17f.valueOf(this);
	}

}
