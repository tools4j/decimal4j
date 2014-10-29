<@pp.dropOutputFile />
<#list 0..maxScale as scale>
<@pp.changeOutputFile name=pp.home + "ch/javasoft/decimal/immutable/Decimal" + scale + "f.java" />
package ch.javasoft.decimal.immutable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.base.AbstractImmutableDecimal;
import ch.javasoft.decimal.mutable.MutableDecimal${scale}f;
import ch.javasoft.decimal.factory.Factory${scale}f;
import ch.javasoft.decimal.scale.Scale${scale}f;

/**
 * <tt>Decimal${scale}f</tt> represents an immutable decimal number with a fixed
 * number of ${scale} digits to the right of the decimal point.
 */
@SuppressWarnings("serial")
public final class Decimal${scale}f extends AbstractImmutableDecimal<Scale${scale}f, Decimal${scale}f> {

	public static final Scale${scale}f SCALE = Scale${scale}f.INSTANCE;

	public static final Factory${scale}f FACTORY = Factory${scale}f.INSTANCE;
	
	public static final long ONE_UNSCALED = SCALE.getScaleFactor();

	public static final Decimal${scale}f ZERO = new Decimal${scale}f(0);
	public static final Decimal${scale}f ULP = new Decimal${scale}f(1);

	public static final Decimal${scale}f ONE = new Decimal${scale}f(1 * ONE_UNSCALED);
	public static final Decimal${scale}f TWO = new Decimal${scale}f(2 * ONE_UNSCALED);
	public static final Decimal${scale}f THREE = new Decimal${scale}f(3 * ONE_UNSCALED);
	public static final Decimal${scale}f FOUR = new Decimal${scale}f(4 * ONE_UNSCALED);
	public static final Decimal${scale}f FIVE = new Decimal${scale}f(5 * ONE_UNSCALED);
	public static final Decimal${scale}f SIX = new Decimal${scale}f(6 * ONE_UNSCALED);
	public static final Decimal${scale}f SEVEN = new Decimal${scale}f(7 * ONE_UNSCALED);
	public static final Decimal${scale}f EIGHT = new Decimal${scale}f(8 * ONE_UNSCALED);
	public static final Decimal${scale}f NINE = new Decimal${scale}f(9 * ONE_UNSCALED);

	public static final Decimal${scale}f TEN = new Decimal${scale}f(10 * ONE_UNSCALED);

	public static final Decimal${scale}f HALF = new Decimal${scale}f(ONE_UNSCALED / 2);
	public static final Decimal${scale}f TENTH = new Decimal${scale}f(ONE_UNSCALED / 10);
	public static final Decimal${scale}f HUNDREDTH = new Decimal${scale}f(ONE_UNSCALED / 100);
	public static final Decimal${scale}f THOUSANDTH = new Decimal${scale}f(ONE_UNSCALED / 1000);
	public static final Decimal${scale}f MILLIONTH = new Decimal${scale}f(ONE_UNSCALED / 1000000);

	public static final Decimal${scale}f MINUS_ONE = new Decimal${scale}f(-ONE_UNSCALED);

	public static final Decimal${scale}f MAX_VALUE = new Decimal${scale}f(Long.MAX_VALUE);
	public static final Decimal${scale}f MAX_INTEGER_VALUE = new Decimal${scale}f((Long.MAX_VALUE / ONE_UNSCALED) * ONE_UNSCALED);
	public static final Decimal${scale}f MIN_VALUE = new Decimal${scale}f(Long.MIN_VALUE);
	public static final Decimal${scale}f MIN_INTEGER_VALUE = new Decimal${scale}f((Long.MIN_VALUE / ONE_UNSCALED) * ONE_UNSCALED);

	private Decimal${scale}f(long unscaled) {
		super(unscaled);
	}

	public Decimal${scale}f(String value) {
		super(SCALE.getDefaultArithmetics().parse(value));
	}
	
	@Override
	public Scale${scale}f getScaleMetrics() {
		return SCALE;
	}

	@Override
	public Factory${scale}f getFactory() {
		return FACTORY;
	}

	@Override
	protected Decimal${scale}f self() {
		return this;
	}

	public static Decimal${scale}f valueOf(long value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromLong(value));
	}

	public static Decimal${scale}f valueOf(double value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromDouble(value));
	}

	public static Decimal${scale}f valueOf(double value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetics(roundingMode).fromDouble(value));
	}

	public static Decimal${scale}f valueOf(BigInteger value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromBigInteger(value));
	}

	public static Decimal${scale}f valueOf(BigDecimal value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromBigDecimal(value));
	}

	public static Decimal${scale}f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetics(roundingMode).fromBigDecimal(value));
	}


	public static Decimal${scale}f valueOf(Decimal<?> value) {
		return valueOfUnscaled(value.unscaledValue(), value.getScale());
	}

	public static Decimal${scale}f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return valueOfUnscaled(value.unscaledValue(), value.getScale(), roundingMode);
	}

	public static Decimal${scale}f valueOf(String value) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().parse(value));
	}

	public static Decimal${scale}f valueOf(String value, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetics(roundingMode).parse(value));
	}

	/**
	 * Converts the specified unscaled decimal with the given scale to a
	 * {@code Decimal${scale}f} value. If the given scale is more precise than the scale
	 * for {@code Decimal${scale}f} and decimals need to be truncated,
	 * {@link RoundingMode#HALF_EVEN HALF_EVEN} rounding mode is applied.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @return the {@code Decimal${scale}f} for the specified unscaled decimal value
	 *         with the given scale
	 */
	public static Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale) {
		return valueOfUnscaled(SCALE.getDefaultArithmetics().fromUnscaled(unscaledValue, scale));
	}

	/**
	 * Converts the specified unscaled decimal with the given scale to a
	 * {@code Decimal${scale}f} value. If the given scale is more precise than the scale
	 * for {@code Decimal${scale}f} and decimals need to be truncated, the specified
	 * rounding mode is applied.
	 * 
	 * @param unscaledValue
	 *            the unscaled decimal value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param roundingMode
	 *            the rounding mode to apply if the value needs to be truncated
	 * @return the {@code Decimal${scale}f} for the specified unscaled decimal value
	 *         with the given scale
	 */
	public static Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return valueOfUnscaled(Scale${scale}f.INSTANCE.getArithmetics(roundingMode).fromUnscaled(unscaledValue, scale));
	}

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

	@Override
	public MutableDecimal${scale}f toMutableDecimal() {
		return new MutableDecimal${scale}f(this);
	}

}
</#list> 