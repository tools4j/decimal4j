package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.ScaleMetrics.Scale17f;

/**
 * <tt>Decimal17f</tt> represents a immutable decimal number with 6 fractional
 * digits.
 */
@SuppressWarnings("serial")
public class Decimal17f extends AbstractImmutableDecimal<Scale17f, Decimal17f, MutableDecimal17f> {

	public static final Scale17f SCALE = Scale17f.INSTANCE;
	
	public static final long ONE_UNSCALED = SCALE.getScaleFactor();

	public static final Decimal17f ZERO = new Decimal17f(0);
	public static final Decimal17f ULP = new Decimal17f(1);

	public static final Decimal17f ONE = new Decimal17f(1 * ONE_UNSCALED);
	public static final Decimal17f TWO = new Decimal17f(2 * ONE_UNSCALED);
	public static final Decimal17f THREE = new Decimal17f(3 * ONE_UNSCALED);
	public static final Decimal17f FOUR = new Decimal17f(4 * ONE_UNSCALED);
	public static final Decimal17f FIVE = new Decimal17f(5 * ONE_UNSCALED);
	public static final Decimal17f SIX = new Decimal17f(6 * ONE_UNSCALED);
	public static final Decimal17f SEVEN = new Decimal17f(7 * ONE_UNSCALED);
	public static final Decimal17f EIGHT = new Decimal17f(8 * ONE_UNSCALED);
	public static final Decimal17f NINE = new Decimal17f(9 * ONE_UNSCALED);

	public static final Decimal17f TEN = new Decimal17f(10 * ONE_UNSCALED);

	public static final Decimal17f HALF = new Decimal17f(ONE_UNSCALED / 2);
	public static final Decimal17f TENTH = new Decimal17f(ONE_UNSCALED / 10);
	public static final Decimal17f HUNDREDTH = new Decimal17f(ONE_UNSCALED / 100);
	public static final Decimal17f THOUSANDTH = new Decimal17f(ONE_UNSCALED / 1000);
	public static final Decimal17f MILLIONTH = new Decimal17f(ONE_UNSCALED / 1000000);

	public static final Decimal17f MINUS_ONE = new Decimal17f(-ONE_UNSCALED);

	public static final Decimal17f MAX_VALUE = new Decimal17f(Long.MAX_VALUE);
	public static final Decimal17f MAX_INTEGER_VALUE = new Decimal17f((Long.MAX_VALUE / ONE_UNSCALED) * ONE_UNSCALED);
	public static final Decimal17f MIN_VALUE = new Decimal17f(Long.MIN_VALUE);
	public static final Decimal17f MIN_INTEGER_VALUE = new Decimal17f((Long.MIN_VALUE / ONE_UNSCALED) * ONE_UNSCALED);

	private Decimal17f(long unscaled) {
		super(unscaled);
	}

	public Decimal17f(String value) {
		super(SCALE.getDefaultArithmetics().parse(value));
	}
	
	@Override
	public Scale17f getScaleMetrics() {
		return SCALE;
	}

	@Override
	protected Decimal17f self() {
		return this;
	}

	public static Decimal17f valueOf(long value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromLong(value));
	}

	public static Decimal17f valueOf(double value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromDouble(value));
	}

	public static Decimal17f valueOf(double value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale17f.INSTANCE.getArithmetics(roundingMode).fromDouble(value));
	}

	public static Decimal17f valueOf(BigInteger value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromBigInteger(value));
	}

	public static Decimal17f valueOf(BigDecimal value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromBigDecimal(value));
	}

	public static Decimal17f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale17f.INSTANCE.getArithmetics(roundingMode).fromBigDecimal(value));
	}


	public static Decimal17f valueOf(Decimal<?> value) {
		return valueOfUnscaled(value.unscaledValue(), value.getScale());
	}

	public static Decimal17f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return valueOfUnscaled(value.unscaledValue(), value.getScale(), roundingMode);
	}

	public static Decimal17f valueOf(String value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().parse(value));
	}

	public static Decimal17f valueOf(String value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale17f.INSTANCE.getArithmetics(roundingMode).parse(value));
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
	 * @return the {@code Decimal6} for the specified unscaled decimal value
	 *         with the given scale
	 */
	public static Decimal17f valueOfUnscaled(long unscaledValue, int scale) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromUnscaled(unscaledValue, scale));
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
	 *            the rounding mode to apply if the value needs to be truncated
	 * @return the {@code Decimal6} for the specified unscaled decimal value
	 *         with the given scale
	 */
	public static Decimal17f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale17f.INSTANCE.getArithmetics(roundingMode).fromUnscaled(unscaledValue, scale));
	}

	public static Decimal17f valueOfUnscaled(long unscaledValue) {
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
		return new Decimal17f(unscaledValue);
	}

	@Override
	protected Decimal17f createOrAssign(long unscaled) {
		return valueOfUnscaled(unscaled);
	}
	
	@Override
	protected Decimal17f create(long unscaled) {
		return valueOfUnscaled(unscaled);
	}

	@Override
	public MutableDecimal17f toMutableDecimal() {
		return new MutableDecimal17f(this);
	}

}
