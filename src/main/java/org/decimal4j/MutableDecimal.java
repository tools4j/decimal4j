package org.decimal4j;

import java.math.RoundingMode;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Interface implemented by mutable {@link Decimal} classes of different scales.
 * Arithmetic operations of mutable decimals modify the state of {@code this}
 * {@code Decimal} and return {@code this} as result value.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 */
public interface MutableDecimal<S extends ScaleMetrics> extends Decimal<S> {

	/**
	 * Sets {@code this} decimal to 0 and returns {@code this} now representing
	 * zero.
	 * 
	 * @return {@code this} decimal after assigning {@code this = 0}
	 */
	MutableDecimal<S> setZero();

	/**
	 * Sets {@code this} decimal to 1 and returns {@code this} now representing
	 * one.
	 * 
	 * @return {@code this} decimal after assigning {@code this = 1}
	 */
	MutableDecimal<S> setOne();

	/**
	 * Sets {@code this} decimal to -1 and returns {@code this} now representing
	 * minus one.
	 * 
	 * @return {@code this} decimal after assigning {@code this = -1}
	 */
	MutableDecimal<S> setMinusOne();

	/**
	 * Sets {@code this} decimal to the smallest positive value representable by
	 * this mutable decimal and returns {@code this} now representing one ULP.
	 * 
	 * @return {@code this} decimal after assigning {@code this = ULP}
	 */
	MutableDecimal<S> setUlp();

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	MutableDecimal<S> set(Decimal<S> value);

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
	MutableDecimal<S> set(Decimal<?> value, RoundingMode roundingMode);

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @param truncationPolicy
	 *            the truncation policy to apply if the value argument needs to
	 *            be truncated when converted to the appropriate scale
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	MutableDecimal<S> set(Decimal<?> value, TruncationPolicy truncationPolicy);

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	MutableDecimal<S> set(long value);

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @param overflowMode
	 *            the mode to apply if an overflow occurs
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	MutableDecimal<S> set(long value, OverflowMode overflowMode);

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	MutableDecimal<S> set(double value);

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
	MutableDecimal<S> set(double value, RoundingMode roundingMode);

	/**
	 * Sets {@code this} decimal to the specified {@code value} and returns
	 * {@code this} now representing {@code value}.
	 * 
	 * @param value
	 *            value to be set
	 * @param truncationPolicy
	 *            the truncation policy to apply if the value argument needs to
	 *            be truncated when converted into a decimal number
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	MutableDecimal<S> set(double value, TruncationPolicy truncationPolicy);

	/**
	 * Sets {@code this} decimal to the specified {@code unscaledValue} with the
	 * given {@code scale} and returns {@code this} now representing
	 * <code>this = round(unscaledValue * 10<sup>-scale</sup>)</code>. Note that
	 * the conversion may involve rounding if the specified {@code scale} is
	 * larger than {@link #getScale()}; the default rounding mode is applied in
	 * this case.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @return {@code this} decimal after assigning
	 *         <code>this = round(unscaledValue * 10<sup>-scale)</sup></code>.
	 */
	MutableDecimal<S> setUnscaled(long unscaledValue, int scale);

	/**
	 * Sets {@code this} decimal to the specified {@code unscaledValue} with the
	 * given {@code scale} and returns {@code this} now representing
	 * <code>this = round(unscaledValue * 10<sup>-scale</sup>)</code>. Note that
	 * the conversion may involve rounding if the specified {@code scale} is
	 * larger than {@link #getScale()}; the specified {@code roundingMode} is
	 * applied in this case.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param roundingMode
	 *            the rounding mode to apply if the value argument needs to be
	 *            truncated when converted to this decimal's scale
	 * @return {@code this} decimal after assigning
	 *         <code>this = round(unscaledValue * 10<sup>-scale</sup>)</code>.
	 */
	MutableDecimal<S> setUnscaled(long unscaledValue, int scale, RoundingMode roundingMode);

	/**
	 * Sets {@code this} decimal to the specified {@code unscaledValue} with the
	 * given {@code scale} and returns {@code this} now representing
	 * <code>this = unscaledValue * 10<sup>-scale</sup></code>. Note that the
	 * conversion may involve rounding if the specified {@code scale} is larger
	 * than {@link #getScale()} or lead to an overflow. The specified
	 * {@code truncationPolicy} is applied in such cases.
	 * 
	 * @param unscaledValue
	 *            value to be set
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param truncationPolicy
	 *            the truncation policy to apply if the value argument needs to
	 *            be truncated when converted to this decimal's scale
	 * @return {@code this} decimal after assigning
	 *         <code>this = unscaledValue * 10<sup>-scale</sup></code>.
	 */
	MutableDecimal<S> setUnscaled(long unscaledValue, int scale, TruncationPolicy truncationPolicy);

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
	MutableDecimal<S> setUnscaled(long unscaledValue);
}
