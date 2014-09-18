package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Interface implemented by mutable {@link Decimal} classes of different scales.
 * Arithmetic operations of mutable decimals modify the state of {@code this}
 * {@code Decimal} and return {@code this} as result value.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this mutable decimal
 */
public interface MutableDecimal<S extends ScaleMetrics, D extends MutableDecimal<S, D>> extends Decimal<S> {

	/**
	 * Creates a new immutable value representing the same decimal as
	 * {@code this} mutable decimal and returns it.
	 * 
	 * @return {@code this} as new immutable decimal value
	 */
	ImmutableDecimal<S, ?> toImmutableDecimal();

	/**
	 * Sets {@code this} decimal to 0 and returns {@code this} now representing
	 * zero.
	 * 
	 * @return {@code this} decimal after assigning {@code this = 0}
	 */
	D setZero();

	/**
	 * Sets {@code this} decimal to 1 and returns {@code this} now representing
	 * one.
	 * 
	 * @return {@code this} decimal after assigning {@code this = 1}
	 */
	D setOne();

	/**
	 * Sets {@code this} decimal to -1 and returns {@code this} now representing
	 * minus one.
	 * 
	 * @return {@code this} decimal after assigning {@code this = -1}
	 */
	D setMinusOne();

	/**
	 * Sets {@code this} decimal to the smallest positive value representable by
	 * this mutable decimal and returns {@code this} now representing one ULP.
	 * 
	 * @return {@code this} decimal after assigning {@code this = ULP}
	 */
	D setUlp();

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	D set(Decimal<S> value);

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted to the appropriate scale
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	D set(Decimal<?> value, RoundingMode roundingMode);

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	D set(long value);

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	D set(double value);

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a decimal number
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	D set(double value, RoundingMode roundingMode);

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	D set(BigInteger value);

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	D set(BigDecimal value);

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a decimal number
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	D set(BigDecimal value, RoundingMode roundingMode);

	/**
	 * Sets {@code this} decimal to the specified {@code unscaledValue} with the
	 * given {@code scale} and returns {@code this} now representing
	 * <code>this = unscaledValue * 10<sup>-scale</sup></code>.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @return {@code this} decimal after assigning
	 *         <code>this = unscaledValue * 10<sup>-scale</sup></code>.
	 */
	D setUnscaled(long unscaledValue, int scale);

	/**
	 * Sets {@code this} decimal to the specified {@code unscaledValue} with the
	 * given {@code scale} and returns {@code this} now representing
	 * <code>this = unscaledValue * 10<sup>-scale</sup></code>.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted into a decimal number
	 * @return {@code this} decimal after assigning
	 *         <code>this = unscaledValue * 10<sup>-scale</sup></code>.
	 */
	D setUnscaled(long unscaledValue, int scale, RoundingMode roundingMode);

	/**
	 * Sets {@code this} decimal to the specified {@code unscaledValue} and
	 * returns {@code this} now representing
	 * <code>this = unscaledValue * 10<sup>-scale</sup></code> where scale is
	 * the scale factor of this mutable decimal.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @return {@code this} decimal after assigning
	 *         <code>this = unscaledValue * 10<sup>-scale</sup></code>.
	 * @see #getScaleMetrics()
	 * @see ScaleMetrics#getScaleFactor()
	 */
	D setUnscaled(long unscaledValue);
}
