package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.scale.Scale0f;

/**
 * <tt>Decimal0f</tt> represents an immutable decimal number with 0 fractional
 * digits, that is, essentially a long value.
 */
@SuppressWarnings("serial")
public class Decimal0f extends AbstractImmutableDecimal<Scale0f, Decimal0f> {

	public static final Scale0f SCALE = Scale0f.INSTANCE;
	
	public static final long ONE_UNSCALED = SCALE.getScaleFactor();

	public static final Decimal0f ZERO = new Decimal0f(0);
	public static final Decimal0f ULP = new Decimal0f(1);

	public static final Decimal0f ONE = new Decimal0f(1 * ONE_UNSCALED);
	public static final Decimal0f TWO = new Decimal0f(2 * ONE_UNSCALED);
	public static final Decimal0f THREE = new Decimal0f(3 * ONE_UNSCALED);
	public static final Decimal0f FOUR = new Decimal0f(4 * ONE_UNSCALED);
	public static final Decimal0f FIVE = new Decimal0f(5 * ONE_UNSCALED);
	public static final Decimal0f SIX = new Decimal0f(6 * ONE_UNSCALED);
	public static final Decimal0f SEVEN = new Decimal0f(7 * ONE_UNSCALED);
	public static final Decimal0f EIGHT = new Decimal0f(8 * ONE_UNSCALED);
	public static final Decimal0f NINE = new Decimal0f(9 * ONE_UNSCALED);

	public static final Decimal0f TEN = new Decimal0f(10 * ONE_UNSCALED);
	public static final Decimal0f HUNDRED = new Decimal0f(100 * ONE_UNSCALED);
	public static final Decimal0f THOUSAND = new Decimal0f(1000 * ONE_UNSCALED);
	public static final Decimal0f MILLION = new Decimal0f(1000000 * ONE_UNSCALED);
	public static final Decimal0f BILLION = new Decimal0f(1000000000 * ONE_UNSCALED);

	public static final Decimal0f MINUS_ONE = new Decimal0f(-ONE_UNSCALED);

	public static final Decimal0f MAX_VALUE = new Decimal0f(Long.MAX_VALUE);
	public static final Decimal0f MAX_INTEGER_VALUE = new Decimal0f((Long.MAX_VALUE / ONE_UNSCALED) * ONE_UNSCALED);
	public static final Decimal0f MIN_VALUE = new Decimal0f(Long.MIN_VALUE);
	public static final Decimal0f MIN_INTEGER_VALUE = new Decimal0f((Long.MIN_VALUE / ONE_UNSCALED) * ONE_UNSCALED);

	private Decimal0f(long unscaled) {
		super(unscaled);
	}

	public Decimal0f(String value) {
		super(SCALE.getDefaultArithmetics().parse(value));
	}
	
	@Override
	public Scale0f getScaleMetrics() {
		return SCALE;
	}

	@Override
	protected Decimal0f self() {
		return this;
	}

	public static Decimal0f valueOf(long value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromLong(value));
	}

	public static Decimal0f valueOf(double value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromDouble(value));
	}

	public static Decimal0f valueOf(double value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale0f.INSTANCE.getArithmetics(roundingMode).fromDouble(value));
	}

	public static Decimal0f valueOf(BigInteger value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromBigInteger(value));
	}

	public static Decimal0f valueOf(BigDecimal value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromBigDecimal(value));
	}

	public static Decimal0f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale0f.INSTANCE.getArithmetics(roundingMode).fromBigDecimal(value));
	}


	public static Decimal0f valueOf(Decimal<?> value) {
		return valueOfUnscaled(value.unscaledValue(), value.getScale());
	}

	public static Decimal0f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return valueOfUnscaled(value.unscaledValue(), value.getScale(), roundingMode);
	}

	public static Decimal0f valueOf(String value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().parse(value));
	}

	public static Decimal0f valueOf(String value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale0f.INSTANCE.getArithmetics(roundingMode).parse(value));
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
	public static Decimal0f valueOfUnscaled(long unscaledValue, int scale) {
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
	public static Decimal0f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale0f.INSTANCE.getArithmetics(roundingMode).fromUnscaled(unscaledValue, scale));
	}

	public static Decimal0f valueOfUnscaled(long unscaledValue) {
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
		return new Decimal0f(unscaledValue);
	}

	@Override
	protected Decimal0f createOrAssign(long unscaled) {
		return valueOfUnscaled(unscaled);
	}
	
	@Override
	protected Decimal0f create(long unscaled) {
		return valueOfUnscaled(unscaled);
	}

	@Override
	public MutableDecimal0f toMutableDecimal() {
		return new MutableDecimal0f(this);
	}

}
