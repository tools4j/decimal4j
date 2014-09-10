package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

/**
 * Common base class for {@link AbstractImmutableDecimal immutable} and
 * {@link AbstractMutableDecimal mutable} {@link Decimal} numbers of different
 * scales.
 * 
 * @param <S>
 *            the scale metrics type associated with this decimal
 * @param <D>
 *            the concrete class implementing this decimal
 */
@SuppressWarnings("serial")
abstract public class AbstractDecimal<S extends ScaleMetrics, D extends AbstractDecimal<S, D>>
		extends Number implements Decimal<S> {

	private final S scaleMetrics;

	/**
	 * Constructor with specified scale metrics.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics for this decimal number
	 */
	public AbstractDecimal(S scaleMetrics) {
		this.scaleMetrics = scaleMetrics;
	}

	/**
	 * Returns this or a new {@code Decimal} whose value is
	 * <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>.
	 * <p>
	 * The returned value is a new instance if this decimal is an
	 * {@link ImmutableDecimal}. If it is a {@link MutableDecimal} then its
	 * internal state is altered and {@code this} is returned as result now
	 * representing <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>.
	 * 
	 * @param unscaled
	 *            unscaled value to be returned as a {@code Decimal}
	 * @return <tt>unscaled &times; 10<sup>-scale</sup></tt>
	 */
	abstract protected D createOrAssign(long unscaled);
	
	/**
	 * Returns {@code this} decimal value as concrete implementation subtype.
	 * 
	 * @return {@code this}
	 */
	abstract protected D self();	

	@Override
	public final S getScaleMetrics() {
		return scaleMetrics;
	}

	@Override
	public int getScale() {
		return getScaleMetrics().getScale();
	}

	protected DecimalArithmetics getDefaultArithmetics() {
		return scaleMetrics.getHalfUpArithmetics();
	}

	protected DecimalArithmetics getArithmeticsFor(RoundingMode roundingMode) {
		return scaleMetrics.getArithmetics(roundingMode);
	}

	protected long unscaledOne() {
		return scaleMetrics.getScaleFactor();
	}

	/* ------------------------------- Number ------------------------------- */

	@Override
	public int intValue() {
		return (int) longValue();
	}

	@Override
	public long longValue() {
		return getDefaultArithmetics().toLong(unscaledValue());
	}

	@Override
	public float floatValue() {
		return (float) doubleValue();
	}

	@Override
	public double doubleValue() {
		return getDefaultArithmetics().toDouble(unscaledValue());
	}

	/* -------------------------------- add -------------------------------- */

	@Override
	public D add(Decimal<S> augend) {
		return addUnscaled(augend.unscaledValue());
	}

	@Override
	public D add(Decimal<?> augend, RoundingMode roundingMode) {
		return addUnscaled(augend.unscaledValue(), augend.getScale(), roundingMode);
	}

	@Override
	public D add(long augend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.add(unscaledValue(), arith.fromLong(augend)));
	}

	@Override
	public D add(double augend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.add(unscaledValue(), arith.fromDouble(augend)));
	}

	@Override
	public D add(double augend, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.add(unscaledValue(), arith.fromDouble(augend)));
	}

	@Override
	public D add(BigInteger augend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.add(unscaledValue(), arith.fromBigInteger(augend)));
	}

	@Override
	public D add(BigDecimal augend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.add(unscaledValue(), arith.fromBigDecimal(augend)));
	}

	@Override
	public D add(BigDecimal augend, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.add(unscaledValue(), arith.fromBigDecimal(augend)));
	}

	@Override
	public D addUnscaled(long unscaledAugend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.add(unscaledValue(), unscaledAugend));
	}

	@Override
	public D addUnscaled(long unscaledAugend, int scale) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.add(unscaledValue(), arith.fromUnscaled(unscaledAugend, scale)));
	}

	@Override
	public D addUnscaled(long unscaledAugend, int scale, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.add(unscaledValue(), arith.fromUnscaled(unscaledAugend, scale)));
	}

	/* ------------------------------ subtract ------------------------------ */

	@Override
	public D subtract(Decimal<S> subtrahend) {
		return subtractUnscaled(subtrahend.unscaledValue());
	}

	@Override
	public D subtract(Decimal<?> subtrahend, RoundingMode roundingMode) {
		return subtractUnscaled(subtrahend.unscaledValue(), subtrahend.getScale(), roundingMode);
	}

	@Override
	public D subtract(long subtrahend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromLong(subtrahend)));
	}

	@Override
	public D subtract(double subtrahend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromDouble(subtrahend)));
	}

	@Override
	public D subtract(double subtrahend, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromDouble(subtrahend)));
	}

	@Override
	public D subtract(BigInteger subtrahend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromBigInteger(subtrahend)));
	}

	@Override
	public D subtract(BigDecimal subtrahend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromBigDecimal(subtrahend)));
	}

	@Override
	public D subtract(BigDecimal subtrahend, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromBigDecimal(subtrahend)));
	}

	@Override
	public D subtractUnscaled(long unscaledSubtrahend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.subtract(unscaledValue(), unscaledSubtrahend));
	}

	@Override
	public D subtractUnscaled(long unscaledSubtrahend, int scale) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromUnscaled(unscaledSubtrahend, scale)));
	}

	@Override
	public D subtractUnscaled(long unscaledSubtrahend, int scale, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromUnscaled(unscaledSubtrahend, scale)));
	}
	
	/* ------------------------------ multiply ------------------------------ */

	@Override
	public D multiply(Decimal<S> multiplicand) {
		return multiplyUnscaled(multiplicand.unscaledValue());
	}

	@Override
	public D multiply(Decimal<S> multiplicand, RoundingMode roundingMode) {
		return multiplyUnscaled(multiplicand.unscaledValue(), roundingMode);
	}

	@Override
	public D multiplyBy(Decimal<?> multiplicand) {
		return multiplyUnscaled(multiplicand.unscaledValue(), multiplicand.getScale());
	}

	@Override
	public D multiplyBy(Decimal<?> multiplicand, RoundingMode roundingMode) {
		return multiplyUnscaled(multiplicand.unscaledValue(), multiplicand.getScale(), roundingMode);
	}

	@Override
	public D multiply(long multiplicand) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.multiplyByLong(unscaledValue(), multiplicand));
	}

	@Override
	public D multiply(double multiplicand) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.multiply(unscaledValue(), arith.fromDouble(multiplicand)));
	}

	@Override
	public D multiply(double multiplicand, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.multiply(unscaledValue(), arith.fromDouble(multiplicand)));
	}

	@Override
	public D multiply(BigInteger multiplicand) {
		return multiply(multiplicand.longValue());
	}

	@Override
	public D multiply(BigDecimal multiplicand) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.multiply(unscaledValue(), arith.fromBigDecimal(multiplicand)));
	}

	@Override
	public D multiply(BigDecimal multiplicand, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.multiply(unscaledValue(), arith.fromBigDecimal(multiplicand)));
	}

	@Override
	public D multiplyUnscaled(long unscaledMultiplicand) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.multiply(unscaledValue(), unscaledMultiplicand));
	}

	@Override
	public D multiplyUnscaled(long unscaledMultiplicand, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.multiply(unscaledValue(), unscaledMultiplicand));
	}

	@Override
	public D multiplyUnscaled(long unscaledMultiplicand, int scale) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.multiply(unscaledValue(), arith.fromUnscaled(unscaledMultiplicand, scale)));
	}

	@Override
	public D multiplyUnscaled(long unscaledMultiplicand, int scale, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.multiply(unscaledValue(), arith.fromUnscaled(unscaledMultiplicand, scale)));
	}
	
	@Override
	public Decimal<?> multiplyExact(Decimal<?> multiplicand) {
		final int targetScale = getScale() + multiplicand.getScale();
		final ScaleMetrics targetMetrics = ScaleMetrics.valueOf(targetScale);
		return targetMetrics.createMutable(unscaledValue() * multiplicand.unscaledValue());
	}

	@Override
	public D multiplyByPowerOfTen(int n) {
		return createOrAssign(getDefaultArithmetics().multiplyByPowerOf10(unscaledValue(), n));
	}

	@Override
	public D multiplyByPowerOfTen(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).multiplyByPowerOf10(unscaledValue(), n));
	}	

	/* ------------------------------ divide ------------------------------ */

	@Override
	public D divide(Decimal<S> divisor) {
		return divideUnscaled(divisor.unscaledValue());
	}

	@Override
	public D divide(Decimal<S> divisor, RoundingMode roundingMode) {
		return divideUnscaled(divisor.unscaledValue(), roundingMode);
	}

	@Override
	public D divideBy(Decimal<?> divisor) {
		return divideUnscaled(divisor.unscaledValue(), divisor.getScale());
	}

	@Override
	public D divideBy(Decimal<?> divisor, RoundingMode roundingMode) {
		return divideUnscaled(divisor.unscaledValue(), divisor.getScale(), roundingMode);
	}

	@Override
	public D divide(long divisor) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.divideByLong(unscaledValue(), divisor));
	}

	@Override
	public D divide(long divisor, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.divideByLong(unscaledValue(), divisor));
	}

	@Override
	public D divide(double divisor) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.divide(unscaledValue(), arith.fromDouble(divisor)));
	}

	@Override
	public D divide(double divisor, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.divide(unscaledValue(), arith.fromDouble(divisor)));
	}

	@Override
	public D divide(BigInteger divisor) {
		return divide(divisor, RoundingMode.HALF_UP);
	}
	
	@Override
	public D divide(BigInteger divisor, RoundingMode roundingMode) {
		if (fitsInLong(divisor)) {
			return divide(divisor.longValue(), roundingMode);
		}
		return createOrAssign(0);
	}

	@Override
	public D divide(BigDecimal divisor) {
		return divide(divisor, RoundingMode.HALF_UP);
	}

	@Override
	public D divide(BigDecimal divisor, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		final BigInteger intDivisor = divisor.toBigInteger();
		if (fitsInLong(intDivisor)) {
			return createOrAssign(arith.divide(unscaledValue(), arith.fromBigDecimal(divisor)));
		}
		return createOrAssign(0);
	}

	@Override
	public D divideUnscaled(long unscaledDivisor) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.divide(unscaledValue(), unscaledDivisor));
	}

	@Override
	public D divideUnscaled(long unscaledDivisor, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.divide(unscaledValue(), unscaledDivisor));
	}

	@Override
	public D divideUnscaled(long unscaledDivisor, int scale) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.divide(unscaledValue(), arith.fromUnscaled(unscaledDivisor, scale)));
	}

	@Override
	public D divideUnscaled(long unscaledDivisor, int scale, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.divide(unscaledValue(), arith.fromUnscaled(unscaledDivisor, scale)));
	}
	
	@Override
	public D divideExact(Decimal<S> divisor) {
		return divide(divisor, RoundingMode.UNNECESSARY);
	}
	
	@Override
	public D divideTruncate(Decimal<S> divisor) {
		return divide(divisor, RoundingMode.DOWN);
	}

	@Override
	public D divideByPowerOfTen(int n) {
		return createOrAssign(getDefaultArithmetics().divideByPowerOf10(unscaledValue(), n));
	}

	@Override
	public D divideByPowerOfTen(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).divideByPowerOf10(unscaledValue(), n));
	}

	/* ------------------------- other arithmetic  ------------------------- */
	
	@Override
	public int signum() {
		return Long.signum(unscaledValue());
	}
	
	@Override
	public D negate() {
		return createOrAssign(getDefaultArithmetics().negate(unscaledValue()));
	}

	@Override
	public D abs() {
		return unscaledValue() >= 0 ? self() : negate();
	}

	@Override
	public D invert() {
		return createOrAssign(getDefaultArithmetics().invert(unscaledValue()));
	}

	@Override
	public D invert(RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).invert(unscaledValue()));
	}

	@Override
	public D shiftLeft(int n) {
		return createOrAssign(getDefaultArithmetics().shiftLeft(unscaledOne(), n));
	}

	@Override
	public D shiftLeft(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).shiftLeft(unscaledOne(), n));
	}

	@Override
	public D shiftRight(int n) {
		return createOrAssign(getDefaultArithmetics().shiftRight(unscaledOne(), n));
	}

	@Override
	public D shiftRight(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).shiftRight(unscaledOne(), n));
	}

	@Override
	public D pow(int n) {
		return createOrAssign(getDefaultArithmetics().pow(unscaledValue(), n));
	}

	@Override
	public D pow(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).pow(unscaledValue(), n));
	}
	
	/* ---------------------------- equals etc.  ---------------------------- */
	
	@Override
	public int hashCode() {
		final long unscaled = unscaledValue();
		return (int) (unscaled ^ (unscaled >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Decimal) {
			final Decimal<?> other = (Decimal<?>) obj;
			return unscaledValue() == other.unscaledValue() && getScale() == other.getScale();
		}
		return false;
	}

	@Override
	public int compareTo(Decimal<S> anotherDecimal) {
		return getDefaultArithmetics().compare(unscaledValue(), anotherDecimal.unscaledValue());
	}

	@Override
	public String toString() {
		return getDefaultArithmetics().toString(unscaledValue());
	}
	
	private static boolean fitsInLong(BigInteger value) {
		return value.bitLength() < 64;
	}
}
