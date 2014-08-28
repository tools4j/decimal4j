package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.ScaleMetrics.Scale6f;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

/**
 * <tt>Decimal6f</tt> represents a immutable decimal number with 6 fractional
 * digits.
 */
@SuppressWarnings("serial")
public class Decimal6f extends AbstractImmutableDecimal<Scale6f, Decimal6f, MutableDecimal6f> {

	public static final DecimalArithmetics ARITHMETICS = Scale6f.INSTANCE.getTruncatingArithmetics().derive(RoundingMode.HALF_UP);
//	public static final DecimalArithmetics ARITHMETICS = Scale6f.INSTANCE.getTruncatingArithmetics().derive(RoundingMode.HALF_EVEN);
//	public static final DecimalArithmetics ARITHMETICS = Scale6f.INSTANCE.getTruncatingArithmetics();

	public static final long ONE_UNSCALED = ARITHMETICS.one();

	public static final Decimal6f ZERO = new Decimal6f(0);
	public static final Decimal6f ULP = new Decimal6f(1);

	public static final Decimal6f ONE = new Decimal6f(1 * ONE_UNSCALED);
	public static final Decimal6f TWO = new Decimal6f(2 * ONE_UNSCALED);
	public static final Decimal6f THREE = new Decimal6f(3 * ONE_UNSCALED);
	public static final Decimal6f FOUR = new Decimal6f(4 * ONE_UNSCALED);
	public static final Decimal6f FIVE = new Decimal6f(5 * ONE_UNSCALED);
	public static final Decimal6f SIX = new Decimal6f(6 * ONE_UNSCALED);
	public static final Decimal6f SEVEN = new Decimal6f(7 * ONE_UNSCALED);
	public static final Decimal6f EIGHT = new Decimal6f(8 * ONE_UNSCALED);
	public static final Decimal6f NINE = new Decimal6f(9 * ONE_UNSCALED);

	public static final Decimal6f TEN = new Decimal6f(10 * ONE_UNSCALED);
	public static final Decimal6f HUNDRED = new Decimal6f(100 * ONE_UNSCALED);
	public static final Decimal6f THOUSAND = new Decimal6f(1000 * ONE_UNSCALED);
	public static final Decimal6f MILLION = new Decimal6f(1000000 * ONE_UNSCALED);
	public static final Decimal6f BILLION = new Decimal6f(1000000000 * ONE_UNSCALED);

	public static final Decimal6f HALF = new Decimal6f(ONE_UNSCALED / 2);
	public static final Decimal6f TENTH = new Decimal6f(ONE_UNSCALED / 10);
	public static final Decimal6f HUNDREDTH = new Decimal6f(ONE_UNSCALED / 100);
	public static final Decimal6f THOUSANDTH = new Decimal6f(ONE_UNSCALED / 1000);
	public static final Decimal6f MILLIONTH = new Decimal6f(ONE_UNSCALED / 1000000);

	public static final Decimal6f MINUS_ONE = new Decimal6f(-ONE_UNSCALED);

	public static final Decimal6f MAX_VALUE = new Decimal6f(Long.MAX_VALUE);
	public static final Decimal6f MAX_INTEGER_VALUE = new Decimal6f((Long.MAX_VALUE / ONE_UNSCALED) * ONE_UNSCALED);
	public static final Decimal6f MIN_VALUE = new Decimal6f(Long.MIN_VALUE);
	public static final Decimal6f MIN_INTEGER_VALUE = new Decimal6f((Long.MIN_VALUE / ONE_UNSCALED) * ONE_UNSCALED);

	Decimal6f(long unscaled, DecimalArithmetics arithmetics) {
		super(unscaled, Scale6f.INSTANCE, arithmetics);
	}
	private Decimal6f(long unscaled) {
		super(unscaled, Scale6f.INSTANCE, ARITHMETICS);
	}

	public Decimal6f(String value) {
		super(ARITHMETICS.parse(value), Scale6f.INSTANCE, ARITHMETICS);
	}

	@Override
	protected Decimal6f self() {
		return this;
	}

	@Override
	public Decimal6f convert(RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmetics();
		if (roundingMode == arith.getRoundingMode()) {
			return this;
		}
		return new Decimal6f(unscaledValue(), arith.derive(roundingMode));
	}
	
	@Override
	public Decimal6f convert(OverflowMode overflowMode) {
		final DecimalArithmetics arith = getArithmetics();
		if (overflowMode == arith.getOverflowMode()) {
			return this;
		}
		return new Decimal6f(unscaledValue(), arith.derive(overflowMode));
	}

	public static Decimal6f valueOf(long value) {
		return valueOfUnscaled(ARITHMETICS.fromLong(value));
	}

	public static Decimal6f valueOf(double value) {
		return valueOfUnscaled(ARITHMETICS.fromDouble(value));
	}

	//FIXME apply rounding mode to decimal or not?
	public static Decimal6f valueOf(double value, RoundingMode roundingMode) {
		return valueOfUnscaled(ARITHMETICS.derive(roundingMode).fromDouble(value));
	}

	public static Decimal6f valueOf(BigInteger value) {
		return valueOfUnscaled(ARITHMETICS.fromBigInteger(value));
	}

	public static Decimal6f valueOf(BigDecimal value) {
		return valueOfUnscaled(ARITHMETICS.fromBigDecimal(value));
	}

	//FIXME apply rounding mode to decimal or not?
	public static Decimal6f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return valueOfUnscaled(ARITHMETICS.derive(roundingMode).fromBigDecimal(value));
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
	public static Decimal6f valueOf(long unscaledValue, int scale) {
		return valueOfUnscaled(ARITHMETICS.fromUnscaled(unscaledValue, scale));
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
	//FIXME apply rounding mode to decimal or not?
	public static Decimal6f valueOf(long unscaledValue, int scale, RoundingMode roundingMode) {
		return valueOfUnscaled(ARITHMETICS.derive(roundingMode).fromUnscaled(unscaledValue, scale));
	}

	public static Decimal6f valueOf(Decimal<?> value) {
		return valueOf(value.unscaledValue(), value.getArithmetics().getScale());
	}

	//FIXME apply rounding mode to decimal or not?
	public static Decimal6f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return valueOf(value.unscaledValue(), value.getArithmetics().getScale(), roundingMode);
	}

	public static Decimal6f valueOf(String value) {
		return valueOfUnscaled(ARITHMETICS.parse(value));
	}

	//FIXME apply rounding mode to decimal or not?
	public static Decimal6f valueOf(String value, RoundingMode roundingMode) {
		return valueOfUnscaled(ARITHMETICS.derive(roundingMode).parse(value));
	}

	public static Decimal6f valueOfUnscaled(long unscaledValue) {
		if (unscaledValue == 0) {
			return ZERO;
		}
		if (unscaledValue == 1) {
			return ULP;
		}
		if (unscaledValue == ONE_UNSCALED) {
			return ONE;
		}
		return new Decimal6f(unscaledValue);
	}

	@Override
	protected Decimal6f create(long unscaled) {
		return valueOfUnscaled(unscaled);
	}

	@Override
	public MutableDecimal6f toMutableDecimal() {
		return new MutableDecimal6f(this);
	}

}
