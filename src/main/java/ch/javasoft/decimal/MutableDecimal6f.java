package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.ScaleMetrics.Scale6f;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

@SuppressWarnings("serial")
public class MutableDecimal6f extends
		AbstractMutableDecimal<Scale6f, MutableDecimal6f, Decimal6f> implements
		Cloneable {

	/**
	 * Creates a new {@code MutableDecimal6f} with value zero.
	 */
	public MutableDecimal6f() {
		super(0, Scale6f.INSTANCE, Decimal6f.ARITHMETICS);
	}

	/**
	 * Creates a new {@code MutableDecimal6f} with value zero and the specified
	 * rounding mode.
	 * 
	 * @param roundingMode
	 *            the rounding mode to use for arithmetic operations performed
	 *            on this decimal
	 */
	public MutableDecimal6f(RoundingMode roundingMode) {
		super(0, Scale6f.INSTANCE, Scale6f.INSTANCE.getTruncatingArithmetics().derive(roundingMode));
	}

	/**
	 * Creates a new {@code MutableDecimal6f} with value zero and the specified
	 * rounding and overflow mode.
	 * 
	 * @param roundingMode
	 *            the rounding mode to use for arithmetic operations performed
	 *            on this decimal
	 * @param overflowMode
	 *            the overflow mode to use for arithmetic operations performed
	 *            on this decimal
	 */
	public MutableDecimal6f(RoundingMode roundingMode, OverflowMode overflowMode) {
		super(0, Scale6f.INSTANCE, Scale6f.INSTANCE.getTruncatingArithmetics().derive(roundingMode).derive(overflowMode));
	}

	private MutableDecimal6f(long unscaledValue, DecimalArithmetics arithmetics) {
		super(unscaledValue, Scale6f.INSTANCE, arithmetics);
	}

	public MutableDecimal6f(String value) {
		this(Decimal6f.ARITHMETICS.parse(value));
	}

	//FIXME apply rounding mode to decimal or not?
	public MutableDecimal6f(String value, RoundingMode roundingMode) {
		this(Decimal6f.ARITHMETICS.derive(roundingMode).parse(value));
	}

	public MutableDecimal6f(long value) {
		this();
		set(value);
	}

	public MutableDecimal6f(double value) {
		this();
		add(value);
	}

	//FIXME apply rounding mode to decimal or not?
	public MutableDecimal6f(double value, RoundingMode roundingMode) {
		this();
		add(value, roundingMode);
	}

	public MutableDecimal6f(BigInteger value) {
		this();
		add(value);
	}

	//FIXME apply rounding mode to decimal or not?
	public MutableDecimal6f(BigDecimal value, RoundingMode roundingMode) {
		this();
		add(value, roundingMode);
	}

	/**
	 * Converts the specified unscaled decimal with the given scale to a
	 * {@code Decimal6} value. If the given scale is more precise than the scale
	 * for {@code Decimal6} and decimals need to be truncated,
	 * {@link RoundingMode#HALF_EVEN HALF_EVEN} rounding mode is applied.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 */
	public MutableDecimal6f(long unscaledValue, int scale) {
		this();
		add(unscaledValue, scale);
	}

	/**
	 * Converts the specified unscaled decimal with the given scale to a
	 * {@code Decimal6} value. If the given scale is more precise than the scale
	 * for {@code Decimal6} and decimals need to be truncated, the specified
	 * rounding mode is applied.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a decimal number
	 */
	//FIXME apply rounding mode to decimal or not?
	public MutableDecimal6f(long unscaledValue, int scale, RoundingMode roundingMode) {
		this();
		add(unscaledValue, scale, roundingMode);
	}

	public MutableDecimal6f(Decimal<Scale6f> value) {
		this(value.unscaledValue(), value.getArithmetics());
	}

	@Override
	protected MutableDecimal6f self() {
		return this;
	}

	@Override
	public MutableDecimal6f clone() {
		return new MutableDecimal6f(this);
	}

	@Override
	public MutableDecimal6f convert(RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		if (roundingMode == arith.getRoundingMode()) {
			return this;
		}
		return new MutableDecimal6f(unscaledValue(), arith.derive(roundingMode));
	}

	@Override
	public MutableDecimal6f convert(OverflowMode overflowMode) {
		final DecimalArithmetics arith = getArithmetics();
		if (overflowMode == arith.getOverflowMode()) {
			return this;
		}
		return new MutableDecimal6f(unscaledValue(), arith.derive(overflowMode));
	}

	public static MutableDecimal6f unscaled(long unscaledValue) {
		return new MutableDecimal6f(unscaledValue, Decimal6f.ARITHMETICS);
	}

	public static MutableDecimal6f zero() {
		return new MutableDecimal6f();
	}

	public static MutableDecimal6f ulp() {
		return new MutableDecimal6f(Decimal6f.ULP);
	}

	public static MutableDecimal6f one() {
		return new MutableDecimal6f(Decimal6f.ONE);
	}

	public static MutableDecimal6f two() {
		return new MutableDecimal6f(Decimal6f.TWO);
	}

	public static MutableDecimal6f three() {
		return new MutableDecimal6f(Decimal6f.THREE);
	}

	public static MutableDecimal6f four() {
		return new MutableDecimal6f(Decimal6f.FOUR);
	}

	public static MutableDecimal6f five() {
		return new MutableDecimal6f(Decimal6f.FIVE);
	}

	public static MutableDecimal6f six() {
		return new MutableDecimal6f(Decimal6f.SIX);
	}

	public static MutableDecimal6f seven() {
		return new MutableDecimal6f(Decimal6f.SEVEN);
	}

	public static MutableDecimal6f eigth() {
		return new MutableDecimal6f(Decimal6f.EIGHT);
	}

	public static MutableDecimal6f nine() {
		return new MutableDecimal6f(Decimal6f.NINE);
	}

	public static MutableDecimal6f ten() {
		return new MutableDecimal6f(Decimal6f.TEN);
	}

	public static MutableDecimal6f hundred() {
		return new MutableDecimal6f(Decimal6f.HUNDRED);
	}

	public static MutableDecimal6f thousand() {
		return new MutableDecimal6f(Decimal6f.THOUSAND);
	}

	public static MutableDecimal6f million() {
		return new MutableDecimal6f(Decimal6f.MILLION);
	}

	public static MutableDecimal6f billion() {
		return new MutableDecimal6f(Decimal6f.BILLION);
	}

	public static MutableDecimal6f minusOne() {
		return new MutableDecimal6f(Decimal6f.MINUS_ONE);
	}

	public static MutableDecimal6f half() {
		return new MutableDecimal6f(Decimal6f.HALF);
	}

	public static MutableDecimal6f tenth() {
		return new MutableDecimal6f(Decimal6f.TENTH);
	}

	public static MutableDecimal6f hundredth() {
		return new MutableDecimal6f(Decimal6f.HUNDREDTH);
	}

	public static MutableDecimal6f thousandth() {
		return new MutableDecimal6f(Decimal6f.THOUSANDTH);
	}

	public static MutableDecimal6f millionth() {
		return new MutableDecimal6f(Decimal6f.MILLIONTH);
	}

	@Override
	public Decimal6f toImmutableDecimal() {
		if (getArithmetics() == Decimal6f.ARITHMETICS) {
			return Decimal6f.valueOf(this);
		}
		return new Decimal6f(unscaledValue(), getArithmetics());
	}

}
