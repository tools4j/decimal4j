package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.Scale.Scale6f;

@SuppressWarnings("serial")
public class MutableDecimal6f extends AbstractMutableDecimal<Scale6f> {

	/**
	 * Creates a new {@code MutableDecimal6f} with value zero.
	 */
	public MutableDecimal6f() {
		super(0, Scale6f.INSTANCE, Decimal6f.ARITHMETICS);
	}

	public MutableDecimal6f(long value, boolean unscaled) {
		super(value, Scale6f.INSTANCE, Decimal6f.ARITHMETICS);
		if (!unscaled) {
			reset().add(value);
		}
	}

	public MutableDecimal6f(String value) {
		this(Decimal6f.ARITHMETICS.parse(value));
	}

	public MutableDecimal6f(String value, RoundingMode roundingMode) {
		this(Decimal6f.ARITHMETICS.derive(roundingMode).parse(value));
	}

	public MutableDecimal6f(long value) {
		this(value, false);
	}

	public MutableDecimal6f(double value) {
		this();
		add(value);
	}

	public MutableDecimal6f(double value, RoundingMode roundingMode) {
		this();
		add(value, roundingMode);
	}

	public MutableDecimal6f(BigInteger value) {
		this();
		add(value);
	}

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
	 */
	public MutableDecimal6f(long unscaledValue, int scale, RoundingMode roundingMode) {
		this();
		add(unscaledValue, scale, roundingMode);
	}

	public MutableDecimal6f(Decimal<Scale6f> value) {
		this();
		add(value);
	}

	@Override
	public MutableDecimal6f ulp() {
		return new MutableDecimal6f(1);
	}

	@Override
	public Decimal6f toImmutableValue() {
		return Decimal6f.valueOf(this);
	}

}
