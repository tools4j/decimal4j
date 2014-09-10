package ch.javasoft.decimal;

import java.math.RoundingMode;

/**
 * Interface implemented by immutable {@link Decimal} classes of different
 * scales. Arithmetic operations of immutable decimals return a new decimal
 * instance as result value hence {@link MutableDecimal mutable} decimals may be
 * a better choice for chained operations.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this immutable decimal
 * @param <M>
 *            the concrete class implementing the mutable counterpart of this
 *            immutable decimal
 */
public interface ImmutableDecimal<S extends ScaleMetrics, D extends ImmutableDecimal<S, D, M>, M extends MutableDecimal<S, M, D>>
		extends Decimal<S> {

	/**
	 * Creates a new mutable value representing the same decimal as {@code this}
	 * immutable decimal and returns it.
	 * 
	 * @return {@code this} as new mutable decimal value
	 */
	M toMutableDecimal();

	@Override
	ImmutableDecimal<?, ?, ?> scale(int scale);

	@Override
	@SuppressWarnings("hiding")
	<S extends ScaleMetrics> ImmutableDecimal<S, ? extends ImmutableDecimal<S, ?, ?>, ? extends MutableDecimal<S, ?, ?>> scale(S scaleMetrics);

	@Override
	ImmutableDecimal<?, ?, ?> scale(int scale, RoundingMode roundingMode);

	@Override
	@SuppressWarnings("hiding")
	<S extends ScaleMetrics> ImmutableDecimal<S, ? extends ImmutableDecimal<S, ?, ?>, ? extends MutableDecimal<S, ?, ?>> scale(S scaleMetrics, RoundingMode roundingMode);

	@Override
	D add(Decimal<S> augend);

	@Override
	D subtract(Decimal<S> subtrahend);

	@Override
	D multiply(Decimal<S> multiplicand);

	@Override
	D multiply(Decimal<S> multiplicand, RoundingMode roundingMode);

	@Override
	D divide(Decimal<S> divisor);

	@Override
	D divide(Decimal<S> divisor, RoundingMode roundingMode);

	@Override
	D negate();

	@Override
	D invert();

	@Override
	D invert(RoundingMode roundingMode);

	@Override
	D divideByPowerOfTen(int n);

	@Override
	D divideByPowerOfTen(int n, RoundingMode roundingMode);

	@Override
	D multiplyByPowerOfTen(int n);

	@Override
	D multiplyByPowerOfTen(int n, RoundingMode roundingMode);

	@Override
	D pow(int n);

	@Override
	D pow(int n, RoundingMode roundingMode);
}
