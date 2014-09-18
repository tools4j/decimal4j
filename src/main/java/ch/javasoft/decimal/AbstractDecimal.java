package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.Scales;
import ch.javasoft.decimal.scale.ScaleMetrics;

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
	 * Returns a new {@code Decimal} whose value is
	 * <tt>(unscaled &times; 10<sup>-scale</sup>)</tt>.
	 * 
	 * @param unscaled
	 *            unscaled value to be returned as a {@code Decimal}
	 * @return <tt>unscaled &times; 10<sup>-scale</sup></tt>
	 */
	abstract protected D create(long unscaled);

	/**
	 * Returns {@code this} decimal value as concrete implementation subtype.
	 * 
	 * @return {@code this}
	 */
	abstract protected D self();

	@Override
	public int getScale() {
		return getScaleMetrics().getScale();
	}

	protected DecimalArithmetics getDefaultArithmetics() {
		return getScaleMetrics().getDefaultArithmetics();
	}

	protected DecimalArithmetics getArithmeticsFor(RoundingMode roundingMode) {
		return getScaleMetrics().getArithmetics(roundingMode);
	}

	protected long unscaledOne() {
		return getScaleMetrics().getScaleFactor();
	}

	/* -------------------- Number and simular conversion ------------------- */

	@Override
	public byte byteValueExact() {
		final long num = longValueExact(); // will check decimal part
		if ((byte) num != num) {
			throw new java.lang.ArithmeticException("Overflow: " + num + " is out of the possible range for a byte");
		}
		return (byte) num;
	}

	@Override
	public short shortValueExact() {
		final long num = longValueExact(); // will check decimal part
		if ((short) num != num) {
			throw new java.lang.ArithmeticException("Overflow: " + num + " is out of the possible range for a short");
		}
		return (short) num;
	}

	@Override
	public int intValue() {
		return (int) longValue();
	}

	@Override
	public int intValueExact() {
		final long num = longValueExact(); // will check decimal part
		if ((int) num != num) {
			throw new java.lang.ArithmeticException("Overflow: " + num + " is out of the possible range for an int");
		}
		return (int) num;
	}

	@Override
	public long longValue() {
		return getArithmeticsFor(RoundingMode.DOWN).toLong(unscaledValue());
	}

	@Override
	public long longValueExact() {
		return longValue(RoundingMode.UNNECESSARY);
	}

	@Override
	public long longValue(RoundingMode roundingMode) {
		return getArithmeticsFor(roundingMode).toLong(unscaledValue());
	}

	@Override
	public float floatValue() {
		return getDefaultArithmetics().toFloat(unscaledValue());
	}

	@Override
	public float floatValue(RoundingMode roundingMode) {
		return getArithmeticsFor(roundingMode).toFloat(unscaledValue());
	}

	@Override
	public double doubleValue() {
		return getDefaultArithmetics().toDouble(unscaledValue());
	}

	@Override
	public double doubleValue(RoundingMode roundingMode) {
		return getArithmeticsFor(roundingMode).toDouble(unscaledValue());
	}

	@Override
	public BigInteger toBigInteger() {
		return BigInteger.valueOf(longValue());
	}

	@Override
	public BigInteger toBigIntegerExact() {
		return BigInteger.valueOf(longValueExact());
	}

	@Override
	public BigInteger toBigInteger(RoundingMode roundingMode) {
		return BigInteger.valueOf(longValue(roundingMode));
	}

	@Override
	public BigDecimal toBigDecimal() {
		return getDefaultArithmetics().toBigDecimal(unscaledValue());
	}

	@Override
	public BigDecimal toBigDecimal(int scale, RoundingMode roundingMode) {
		final int thisScale = getScale();
		if (scale == thisScale) {
			return toBigDecimal();
		}
		final long unscaled = unscaledValue();
		if (scale < thisScale) {
			if (scale < 0) {
				return BigDecimal.valueOf(unscaled, thisScale).setScale(scale, roundingMode);
			}
			if (scale == 0) {
				//for scale 0 we need room for add/sub of 1 for the potential rounding
				final ScaleMetrics myScale = getScaleMetrics();
				if (unscaled <= myScale.getMinIntegerValue() & unscaled >= myScale.getMaxIntegerValue()) {
					return BigDecimal.valueOf(unscaled, thisScale).setScale(scale, roundingMode);
				}
			}
			final ScaleMetrics m = Scales.valueOf(scale);
			if (scale != 0) {
				final long rescaled = m.getArithmetics(roundingMode).fromUnscaled(unscaled, thisScale);
				return BigDecimal.valueOf(rescaled, scale);
			}
		} else {
			//does it fit in a long?
			final int diff = scale - thisScale;
			if (diff <= 18) {
				final ScaleMetrics diffMetrics = Scales.valueOf(diff);
				if (unscaled >= diffMetrics.getMinIntegerValue() && unscaled <= diffMetrics.getMaxIntegerValue()) {
					final long rescaled = diffMetrics.multiplyByScaleFactor(unscaled);
					return BigDecimal.valueOf(rescaled, scale);
				}
			}
		}
		//let the big decimal deal with such large numbers then
		return toBigDecimal().setScale(scale, roundingMode);
	}

	@Override
	public Decimal<S> integralPart() {
		final long unscaled = unscaledValue();
		final long integral = unscaled - getScaleMetrics().moduloByScaleFactor(unscaled);
		return createOrAssign(integral);
	}

	@Override
	public Decimal<S> fractionalPart() {
		return createOrAssign(getScaleMetrics().moduloByScaleFactor(unscaledValue()));
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
		final ScaleMetrics targetMetrics = Scales.valueOf(targetScale);
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

	@Override
	public Decimal<S> divideToIntegralValue(Decimal<S> divisor) {
		final DecimalArithmetics arith = getArithmeticsFor(RoundingMode.DOWN);
		final long quot = arith.divide(unscaledValue(), divisor.unscaledValue());
		final long frac = getScaleMetrics().moduloByScaleFactor(quot);
		return createOrAssign(quot - frac);
	}

	@Override
	public Decimal<S>[] divideAndRemainder(Decimal<S> divisor) {
		final DecimalArithmetics arith = getArithmeticsFor(RoundingMode.DOWN);
		final long quot = arith.divide(unscaledValue(), divisor.unscaledValue());
		final long frac = getScaleMetrics().moduloByScaleFactor(quot);
		@SuppressWarnings("unchecked")
		//safe cast
		final Decimal<S>[] result = (Decimal<S>[]) new Decimal<?>[2];
		result[0] = createOrAssign(quot - frac);
		result[1] = createOrAssign(frac);
		return result;
	}

	@Override
	public Decimal<S> remainder(Decimal<S> divisor) {
		final DecimalArithmetics arith = getArithmeticsFor(RoundingMode.DOWN);
		final long quot = arith.divide(unscaledValue(), divisor.unscaledValue());
		final long frac = getScaleMetrics().moduloByScaleFactor(quot);
		return createOrAssign(frac);
	}

	/* ------------------------- other arithmetic ------------------------- */

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

	/* --------------------------- compare etc. ---------------------------- */

	@Override
	public int compareTo(Decimal<S> other) {
		return getDefaultArithmetics().compare(unscaledValue(), other.unscaledValue());
	}

	@Override
	public boolean isEqualTo(Decimal<S> other) {
		return compareTo(other) == 0;
	}

	@Override
	public boolean isGreaterThan(Decimal<S> other) {
		return compareTo(other) > 0;
	}

	@Override
	public boolean isGreaterThanOrEqualTo(Decimal<S> other) {
		return compareTo(other) >= 0;
	}

	@Override
	public boolean isLessThan(Decimal<S> other) {
		return compareTo(other) < 0;
	}

	@Override
	public boolean isLessThanOrEqualTo(Decimal<S> other) {
		return compareTo(other) <= 0;
	}

	@Override
	public boolean isZero() {
		return unscaledValue() == 0;
	}

	@Override
	public boolean isOne() {
		return unscaledValue() == unscaledOne();
	}

	@Override
	public boolean isUlp() {
		return unscaledValue() == 1;
	}

	@Override
	public boolean isMinusOne() {
		return unscaledOne() == -unscaledOne();
	}

	@Override
	public boolean isPositive() {
		return unscaledValue() > 0;
	}

	@Override
	public boolean isNonNegative() {
		return unscaledValue() >= 0;
	}

	@Override
	public boolean isNegative() {
		return unscaledValue() < 0;
	}

	@Override
	public boolean isNonPositive() {
		return unscaledValue() <= 0;
	}

	@Override
	public boolean isIntegral() {
		return getScaleMetrics().moduloByScaleFactor(unscaledValue()) == 0;
	}

	@Override
	public boolean isIntegralPartZero() {
		return getScaleMetrics().divideByScaleFactor(unscaledValue()) == 0;
	}

	@Override
	public boolean isBetweenZeroAndOne() {
		final long unscaled = unscaledValue();
		return unscaled >= 0 && getScaleMetrics().divideByScaleFactor(unscaled) == 0;
	}

	@Override
	public boolean isBetweenZeroAndMinusOne() {
		final long unscaled = unscaledValue();
		return unscaled <= 0 && getScaleMetrics().divideByScaleFactor(unscaled) == 0;
	}

	@Override
	public int compareToNumerically(Decimal<?> other) {
		final long unscaled = unscaledValue();
		final long otherUnscaled = other.unscaledValue();
		final int scale = getScale();
		final int otherScale = other.getScale();
		if (scale == otherScale) {
			return getDefaultArithmetics().compare(unscaled, otherUnscaled);
		}
		if (scale < otherScale) {
			final DecimalArithmetics arith = getDefaultArithmetics();
			final ScaleMetrics diffMetrics = Scales.valueOf(otherScale - scale);
			final long otherRescaled = diffMetrics.divideByScaleFactor(otherUnscaled);
			final int cmp = arith.compare(unscaled, otherRescaled);
			if (cmp != 0) {
				return cmp;
			}
			//remainder must be zero for equality
			final long otherRemainder = otherUnscaled - diffMetrics.multiplyByScaleFactor(otherRescaled);
			return -arith.signum(otherRemainder);
		} else {
			final DecimalArithmetics arith = other.getScaleMetrics().getDefaultArithmetics();
			final ScaleMetrics diffMetrics = Scales.valueOf(scale - otherScale);
			final long rescaled = diffMetrics.divideByScaleFactor(unscaled);
			final int cmp = arith.compare(rescaled, otherUnscaled);
			if (cmp != 0) {
				return cmp;
			}
			//remainder must be zero for equality
			final long remainder = unscaled - diffMetrics.multiplyByScaleFactor(rescaled);
			return arith.signum(remainder);
		}
	}

	@Override
	public boolean isEqualToNumerically(Decimal<?> other) {
		return compareToNumerically(other) == 0;
	}

	@Override
	public Decimal<S> min(Decimal<S> val) {
		return isLessThanOrEqualTo(val) ? this : val;
	}

	@Override
	public Decimal<S> max(Decimal<S> val) {
		return isGreaterThanOrEqualTo(val) ? this : val;
	}

	@Override
	public Decimal<S> average(Decimal<S> val) {
		return createOrAssign(getDefaultArithmetics().average(unscaledValue(), val.unscaledValue()));
	}

	@Override
	public Decimal<S> average(Decimal<S> val, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).average(unscaledValue(), val.unscaledValue()));
	}

	/* ---------------------------- equals etc. ---------------------------- */

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
	public String toString() {
		return getDefaultArithmetics().toString(unscaledValue());
	}

	private static boolean fitsInLong(BigInteger value) {
		return value.bitLength() < 64;
	}
}
