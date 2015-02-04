package org.decimal4j.factory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.ImmutableDecimal;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Factory for {@link Decimal} values of the scale defined by {@code <S>} and
 * arrays thereof.
 *
 * @param <S>
 *            the scale metrics type associated with decimals created by this
 *            factory
 */
public interface DecimalFactory<S extends ScaleMetrics> {
	/**
	 * Returns the scale metrics type associated with decimals created by this
	 * factory.
	 * 
	 * @return the scale metrics defining the scale for decimals created by this
	 *         factory
	 */
	S getScaleMetrics();

	/**
	 * Returns a new decimal value whose value is numerically equal to that of
	 * the specified {@code long} value.
	 *
	 * @param value
	 *            long value to convert into a decimal
	 * @return a decimal numerically equal to the specified long value
	 */
	ImmutableDecimal<S> valueOf(long value);

	ImmutableDecimal<S> valueOf(long value, OverflowMode overflowMode);

	ImmutableDecimal<S> valueOf(double value);

	ImmutableDecimal<S> valueOf(double value, RoundingMode roundingMode);

	ImmutableDecimal<S> valueOf(double value, TruncationPolicy truncationPolicy);

	ImmutableDecimal<S> valueOf(BigInteger value);

	ImmutableDecimal<S> valueOf(BigInteger value, OverflowMode overflowMode);

	ImmutableDecimal<S> valueOf(BigDecimal value);

	ImmutableDecimal<S> valueOf(BigDecimal value, RoundingMode roundingMode);

	ImmutableDecimal<S> valueOf(BigDecimal value, TruncationPolicy truncationPolicy);

	ImmutableDecimal<S> valueOf(Decimal<?> value);

	ImmutableDecimal<S> valueOf(Decimal<?> value, RoundingMode roundingMode);

	ImmutableDecimal<S> valueOf(Decimal<?> value, TruncationPolicy truncationPolicy);

	ImmutableDecimal<S> valueOf(String value);

	ImmutableDecimal<S> valueOf(String value, RoundingMode roundingMode);

	ImmutableDecimal<S> valueOf(String value, TruncationPolicy truncationPolicy);

	/**
	 * Creates and returns an immutable value from an unscaled long value.
	 *
	 * @param unscaled
	 *            the unscaled long value
	 * @return an immutable value.
	 */
	ImmutableDecimal<S> valueOfUnscaled(long unscaled);

	/**
	 * Returns a new immutable decimal value from the specified unscaled value
	 * with the given scale. If the scale conversion leads to rounding
	 * {@link RoundingMode#HALF_EVEN HALF_EVEN} rounding mode is applied;
	 * potential overflows are silently truncated.
	 * 
	 * @param unscaledValue
	 *            the unscaled value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @return a new immutable decimal value converted from the specified
	 *         unscaled value with the given scale
	 */
	ImmutableDecimal<S> valueOfUnscaled(long unscaledValue, int scale);

	/**
	 * Returns a new immutable decimal value from the specified unscaled value
	 * with the given scale. If the scale conversion leads to rounding the
	 * specified rounding mode is applied; potential overflows are silently
	 * truncated.
	 * 
	 * @param unscaledValue
	 *            the unscaled value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param roundingMode
	 *            the rounding mode to apply if the conversion leads to rounding
	 * @return a new immutable decimal value converted from the specified
	 *         unscaled value with the given scale
	 */
	ImmutableDecimal<S> valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode);

	/**
	 * Returns a new immutable decimal value from the specified unscaled value
	 * with the given scale. The specified truncation policy defines rounding
	 * and overflow mode to use if the scale conversion leads to rounding or
	 * overflow.
	 * 
	 * @param unscaledValue
	 *            the unscaled value to convert
	 * @param scale
	 *            the scale used for {@code unscaledValue}
	 * @param truncationPolicy
	 *            the rounding and overflow modes to apply if necessary
	 * @return a new immutable decimal value converted from the specified
	 *         unscaled value with the given scale
	 */
	ImmutableDecimal<S> valueOfUnscaled(long unscaledValue, int scale, TruncationPolicy truncationPolicy);

	/**
	 * Creates a one dimensional array of the specified {@code length} for
	 * immutable decimal values.
	 * 
	 * @param length
	 *            the length of the returned array
	 * @return a new array of the specified length
	 */
	ImmutableDecimal<S>[] newArray(int length);

	/**
	 * Creates a new mutable value initialized with zero.
	 * 
	 * @return a new mutable decimal value representing zero.
	 */
	MutableDecimal<S> newMutable();

	/**
	 * Creates a one dimensional array of the specified {@code length} for
	 * mutable decimal values.
	 * 
	 * @param length
	 *            the length of the returned array
	 * @return a new array of the specified length
	 */
	MutableDecimal<S>[] newMutableArray(int length);

}
