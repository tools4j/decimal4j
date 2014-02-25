package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

import ch.javasoft.decimal.Scale.Scale6f;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.arithmetic.RoundHalfEvenDecimalArithmetics;

/**
 * <tt>Decimal6f</tt> represents a constant decimal number with 6 fractional 
 * digits.
 */
@SuppressWarnings("serial")
public class Decimal6f extends AbstractConstantDecimal<Scale6f> {
	
	public static final DecimalArithmetics ARITHMETICS = new RoundHalfEvenDecimalArithmetics(Scale6f.INSTANCE);

	private static final long ONE_UNSCALED = ARITHMETICS.one();

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

	//must be sorted!!!
	private static final Decimal6f[] CACHED = { MIN_VALUE, MIN_INTEGER_VALUE, MINUS_ONE, ZERO, MILLIONTH, THOUSANDTH, HUNDREDTH, TENTH, HALF, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, HUNDRED, THOUSAND, MILLION, BILLION, MAX_INTEGER_VALUE, MAX_VALUE };
	private static final long[] CACHED_UNSCALED = toUnscaled(CACHED);

	public Decimal6f(long unscaled) {
		super(unscaled, Scale6f.INSTANCE, ARITHMETICS);
	}

	public Decimal6f(String value) {
		super(ARITHMETICS.parse(value), Scale6f.INSTANCE, ARITHMETICS);
	}

	private static long[] toUnscaled(Decimal6f... decimals) {
		final long[] unscaled = new long[decimals.length];
		for (int i = 0; i < unscaled.length; i++) {
			unscaled[i] = decimals[i].unscaledValue();
		}
		return unscaled;
	}

	public static Decimal6f valueOf(long value) {
		return valueOfUnscaled(ARITHMETICS.fromLong(value));
	}

	public static Decimal6f valueOf(double value) {
		return valueOfUnscaled(ARITHMETICS.fromDouble(value));
	}

	public static Decimal6f valueOf(BigInteger value) {
		return valueOfUnscaled(ARITHMETICS.fromBigInteger(value));
	}

	public static Decimal6f valueOf(BigDecimal value) {
		return valueOfUnscaled(ARITHMETICS.fromBigDecimal(value));
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
	 * @return the {@code Decimal6} for the specified unscaled decimal value with the given scale
	 */
	public static Decimal6f valueOf(long unscaledValue, int scale) {
		return valueOfUnscaled(ARITHMETICS.fromUnscaled(unscaledValue, scale));
	}

	public static Decimal6f valueOf(Decimal<?> value) {
		return valueOf(value.unscaledValue(), value.getArithmetics().getScale());
	}

	public static Decimal6f valueOf(String value) {
		return valueOfUnscaled(ARITHMETICS.parse(value));
	}

	public static Decimal6f valueOfUnscaled(long unscaledValue) {
		final int cacheIndex = Arrays.binarySearch(CACHED_UNSCALED, unscaledValue);
		return cacheIndex >= 0 ? CACHED[cacheIndex] : new Decimal6f(unscaledValue);
	}
	
	@Override
	protected Decimal6f create(long unscaled) {
		return valueOfUnscaled(unscaled);
	}
	
	@Override
	public Decimal6f ulp() {
		return ULP;
	}

	@Override
	public MutableDecimal6f toMutableValue() {
		return new MutableDecimal6f(this);
	}
}
