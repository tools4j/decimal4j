package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Interface implemented by mutable {@link Decimal} classes of different scales.
 * Arithmetic operations of mutable decimals modify the state of {@code this}
 * {@code Decimal} and return {@code this} as result value.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this mutable decimal
 * @param <I>
 *            the concrete class implementing the immutable counterpart of this
 *            mutable decimal
 */
public interface MutableDecimal<S extends ScaleMetrics, D extends MutableDecimal<S, D, I>, I extends ImmutableDecimal<S, I, D>>
		extends Decimal<S> {

	/**
	 * Creates a new immutable value representing the same decimal as
	 * {@code this} mutable decimal and returns it.
	 * 
	 * @return {@code this} as new immutable decimal value
	 */
	I toImmutableDecimal();

	@Override
	MutableDecimal<?, ?, ?> convert(int scale);

	@Override
	MutableDecimal<?, ?, ?> convert(int scale, RoundingMode roundingMode);

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
	 * Sets {@code this} decimal to 10 and returns {@code this} now representing
	 * ten.
	 * 
	 * @return {@code this} decimal after assigning {@code this = 10}
	 */
	D setTen();

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
	 * @return {@code this} decimal after assigning it the given {@code value}
	 */
	D setDecimal(Decimal<?> value);

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
	D setDecimal(Decimal<?> value, RoundingMode roundingMode);

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
	D set(long unscaledValue, int scale);

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
	D set(long unscaledValue, int scale, RoundingMode roundingMode);

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

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	@Override
	D add(Decimal<S> augend);

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	D addDecimal(Decimal<?> augend);

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the augend argument needs to be
	 *            truncated when converted to the appropriate scale
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	D addDecimal(Decimal<?> augend, RoundingMode roundingMode);

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	D add(long augend);

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	D add(double augend);

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the augend argument needs to be
	 *            truncated when converted into a decimal number
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	D add(double augend, RoundingMode roundingMode);

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	D add(BigInteger augend);

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	D add(BigDecimal augend);

	/**
	 * Adds the specified {@code augend} value to {@code this} decimal and
	 * returns {@code this} now representing the result of the addition.
	 * 
	 * @param augend
	 *            value to be added to this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the augend argument needs to be
	 *            truncated when converted into a decimal number
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this + augend}
	 */
	D add(BigDecimal augend, RoundingMode roundingMode);

	D add(long unscaledAugend, int scale);

	D add(long unscaledAugend, int scale, RoundingMode roundingMode);

	D addUnscaled(long unscaledAugend);

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	@Override
	D subtract(Decimal<S> subtrahend);

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	D subtract(long subtrahend);

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	D subtract(double subtrahend);

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the subtrahend argument needs to
	 *            be truncated when converted into a decimal number
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	D subtract(double subtrahend, RoundingMode roundingMode);

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	D subtract(BigInteger subtrahend);

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	D subtract(BigDecimal subtrahend);

	/**
	 * Subtracts the specified {@code subtrahend} value from {@code this}
	 * decimal and returns {@code this} now representing the result of the
	 * subtraction.
	 * 
	 * @param subtrahend
	 *            value to be subtracted from this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if the subtrahend argument needs to
	 *            be truncated when converted into a decimal number
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this - subtrahend}
	 */
	D subtract(BigDecimal subtrahend, RoundingMode roundingMode);

	D subtract(long unscaledSubtrahend, int scale);

	D subtract(long unscaledSubtrahend, int scale, RoundingMode roundingMode);

	D subtractUnscaled(long unscaledSubtrahend);

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	@Override
	D multiply(Decimal<S> multiplicand);

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication, applying the specified rounding mode if rounding is
	 * necessary.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if rounding is necessary
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	@Override
	D multiply(Decimal<S> multiplicand, RoundingMode roundingMode);

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	D multiply(long multiplicand);

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	D multiply(double multiplicand);

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if rounding becomes necessary
	 *            either when converting the {@code multiplicand} argument into
	 *            a decimal number or after the multiplication
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	D multiply(double multiplicand, RoundingMode roundingMode);

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	D multiply(BigInteger multiplicand);

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	D multiply(BigDecimal multiplicand);

	/**
	 * Multiplies {@code this} decimal with the specified {@code multiplicand}
	 * and returns {@code this} now representing the result of the
	 * multiplication.
	 * 
	 * @param multiplicand
	 *            value to be multiplied with this {@code Decimal}
	 * @param roundingMode
	 *            the rounding mode to apply if rounding becomes necessary
	 *            either when converting the {@code multiplicand} argument into
	 *            a decimal number or after the multiplication
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this * multiplicand}
	 */
	D multiply(BigDecimal multiplicand, RoundingMode roundingMode);

	D multiply(long unscaledMultiplicand, int scale);

	D multiply(long unscaledMultiplicand, int scale, RoundingMode roundingMode);

	D multiplyUnscaled(long unscaledMultiplicand);

	D multiplyUnscaled(long unscaledMultiplicand, RoundingMode roundingMode);

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	@Override
	D divide(Decimal<S> divisor);

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @param roundingMode
	 *            the rounding mode to apply if rounding is necessary
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	@Override
	D divide(Decimal<S> divisor, RoundingMode roundingMode);

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	D divide(long divisor);

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @param roundingMode
	 *            the rounding mode to apply if rounding is necessary
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	D divide(long divisor, RoundingMode roundingMode);

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	D divide(double divisor);

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @param roundingMode
	 *            the rounding mode to apply if rounding becomes necessary
	 *            either when converting the {@code divisor} argument into a
	 *            decimal number or after the multiplication
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	D divide(double divisor, RoundingMode roundingMode);

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	D divide(BigInteger divisor);

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @param roundingMode
	 *            the rounding mode to apply if rounding is necessary
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	D divide(BigInteger divisor, RoundingMode roundingMode);

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	D divide(BigDecimal divisor);

	/**
	 * Divides {@code this} decimal by the specified {@code divisor} and returns
	 * {@code this} now representing the result of the division.
	 * 
	 * @param divisor
	 *            value to by which this {@code Decimal} is to be divided
	 * @param roundingMode
	 *            the rounding mode to apply if rounding becomes necessary
	 *            either when converting the {@code divisor} argument into a
	 *            decimal number or after the multiplication
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = this / multiplicand}
	 */
	D divide(BigDecimal divisor, RoundingMode roundingMode);

	D divide(long unscaledDivisor, int scale);

	D divide(long unscaledDivisor, int scale, RoundingMode roundingMode);

	D divideUnscaled(long unscaledDivisor);

	D divideUnscaled(long unscaledDivisor, RoundingMode roundingMode);

	/**
	 * Negates {@code this} decimal and returns it.
	 * 
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = -this}
	 */
	@Override
	D negate();

	/**
	 * Inverts {@code this} decimal and returns it.
	 * 
	 * @return {@code this} decimal after performing the operation
	 *         {@code this = 1/this}
	 */
	@Override
	D invert();

	@Override
	D invert(RoundingMode roundingMode);

	@Override
	D movePointLeft(int n);

	@Override
	D movePointLeft(int n, RoundingMode roundingMode);

	@Override
	D movePointRight(int n);

	@Override
	D movePointRight(int n, RoundingMode roundingMode);

	@Override
	D pow(int n);

	@Override
	D pow(int n, RoundingMode roundingMode);
}
