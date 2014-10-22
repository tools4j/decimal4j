package ch.javasoft.decimal.base;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.ImmutableDecimal;
import ch.javasoft.decimal.MutableDecimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.scale.Scales;
import ch.javasoft.decimal.truncate.OverflowMode;
import ch.javasoft.decimal.truncate.TruncationPolicy;

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
	 * Returns a new {@code Decimal} array of the specified {@code length}.
	 * 
	 * @param length
	 *            the length of the array to return
	 * @return {@code new D[length]}
	 */
	abstract protected D[] createArray(int length);

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

	protected DecimalArithmetics getArithmeticsFor(TruncationPolicy truncationPolicy) {
		return getScaleMetrics().getArithmetics(truncationPolicy);
	}

	protected DecimalArithmetics getArithmeticsFor(OverflowMode overflowMode) {
		return getScaleMetrics().getArithmetics(overflowMode.getPolicyFor(RoundingMode.UNNECESSARY));
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
	public long longValue(TruncationPolicy truncationPolicy) {
		return getArithmeticsFor(truncationPolicy).toLong(unscaledValue());
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
		return getArithmeticsFor(roundingMode).toBigDecimal(unscaledValue(), scale);
	}

	@Override
	public D integralPart() {
		final long unscaled = unscaledValue();
		final long integral = unscaled - getScaleMetrics().moduloByScaleFactor(unscaled);
		return createOrAssign(integral);
	}

	@Override
	public D fractionalPart() {
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
	public D add(Decimal<?> augend, TruncationPolicy truncationPolicy) {
		return addUnscaled(augend.unscaledValue(), augend.getScale(), truncationPolicy);
	}

	@Override
	public D add(long augend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.add(unscaledValue(), arith.fromLong(augend)));
	}
	
	@Override
	public D add(long augend, OverflowMode overflowMode) {
		final DecimalArithmetics arith = getArithmeticsFor(overflowMode);
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
	public D add(double augend, TruncationPolicy truncationPolicy) {
		final DecimalArithmetics arith = getArithmeticsFor(truncationPolicy);
		return createOrAssign(arith.add(unscaledValue(), arith.fromDouble(augend)));
	}

	@Override
	public D addUnscaled(long unscaledAugend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.add(unscaledValue(), unscaledAugend));
	}
	
	@Override
	public D addUnscaled(long unscaledAugend, OverflowMode overflowMode) {
		final DecimalArithmetics arith = getArithmeticsFor(overflowMode);
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
	
	@Override
	public D addUnscaled(long unscaledAugend, int scale, TruncationPolicy truncationPolicy) {
		final DecimalArithmetics arith = getArithmeticsFor(truncationPolicy);
		return createOrAssign(arith.add(unscaledValue(), arith.fromUnscaled(unscaledAugend, scale)));
	}

	@Override
	public D addSquared(Decimal<S> value) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.add(unscaledValue(), arith.square(value.unscaledValue())));
	}
	
	@Override
	public D addSquared(Decimal<S> value, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.add(unscaledValue(), arith.square(value.unscaledValue())));
	}

	@Override
	public D addSquared(Decimal<S> value, TruncationPolicy truncationPolicy) {
		final DecimalArithmetics arith = getArithmeticsFor(truncationPolicy);
		return createOrAssign(arith.add(unscaledValue(), arith.square(value.unscaledValue())));
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
	public D subtract(Decimal<?> subtrahend, TruncationPolicy truncationPolicy) {
		return subtractUnscaled(subtrahend.unscaledValue(), subtrahend.getScale(), truncationPolicy);
	}

	@Override
	public D subtract(long subtrahend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromLong(subtrahend)));
	}
	
	@Override
	public D subtract(long subtrahend, OverflowMode overflowMode) {
		final DecimalArithmetics arith = getArithmeticsFor(overflowMode);
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
	public D subtract(double subtrahend, TruncationPolicy truncationPolicy) {
		final DecimalArithmetics arith = getArithmeticsFor(truncationPolicy);
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromDouble(subtrahend)));
	}

	@Override
	public D subtractUnscaled(long unscaledSubtrahend) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.subtract(unscaledValue(), unscaledSubtrahend));
	}
	
	@Override
	public D subtractUnscaled(long unscaledSubtrahend, OverflowMode overflowMode) {
		final DecimalArithmetics arith = getArithmeticsFor(overflowMode);
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

	@Override
	public D subtractUnscaled(long unscaledSubtrahend, int scale, TruncationPolicy truncationPolicy) {
		final DecimalArithmetics arith = getArithmeticsFor(truncationPolicy);
		return createOrAssign(arith.subtract(unscaledValue(), arith.fromUnscaled(unscaledSubtrahend, scale)));
	}

	@Override
	public D subtractSquared(Decimal<S> value) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.subtract(unscaledValue(), arith.square(value.unscaledValue())));
	}
	
	@Override
	public D subtractSquared(Decimal<S> value, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.subtract(unscaledValue(), arith.square(value.unscaledValue())));
	}

	@Override
	public D subtractSquared(Decimal<S> value, TruncationPolicy truncationPolicy) {
		final DecimalArithmetics arith = getArithmeticsFor(truncationPolicy);
		return createOrAssign(arith.subtract(unscaledValue(), arith.square(value.unscaledValue())));
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
	public D multiply(Decimal<S> multiplicand, TruncationPolicy truncationPolicy) {
		return multiplyUnscaled(multiplicand.unscaledValue(), truncationPolicy);
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
	public D multiplyBy(Decimal<?> multiplicand, TruncationPolicy truncationPolicy) {
		return multiplyUnscaled(multiplicand.unscaledValue(), multiplicand.getScale(), truncationPolicy);
	}

	@Override
	public D multiply(long multiplicand) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.multiplyByLong(unscaledValue(), multiplicand));
	}

	@Override
	public D multiply(long multiplicand, OverflowMode overflowMode) {
		final DecimalArithmetics arith = getArithmeticsFor(overflowMode);
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
	public D multiply(double multiplicand, TruncationPolicy truncationPolicy) {
		final DecimalArithmetics arith = getArithmeticsFor(truncationPolicy);
		return createOrAssign(arith.multiply(unscaledValue(), arith.fromDouble(multiplicand)));
	}

	@Override
	public D multiplyUnscaled(long unscaledMultiplicand) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.multiply(unscaledValue(), unscaledMultiplicand));
	}

	@Override
	public D multiplyUnscaled(long unscaledMultiplicand, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.multiply(unscaledValue(), unscaledMultiplicand));
	}

	@Override
	public D multiplyUnscaled(long unscaledMultiplicand, TruncationPolicy truncationPolicy) {
		final DecimalArithmetics arith = getArithmeticsFor(truncationPolicy);
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
	public D multiplyUnscaled(long unscaledMultiplicand, int scale, TruncationPolicy truncationPolicy) {
		final DecimalArithmetics arith = getArithmeticsFor(truncationPolicy);
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

	@Override
	public D multiplyByPowerOfTen(int n, TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticsFor(truncationPolicy).multiplyByPowerOf10(unscaledValue(), n));
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
	public D divide(Decimal<S> divisor, TruncationPolicy truncationPolicy) {
		return divideUnscaled(divisor.unscaledValue(), truncationPolicy);
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
	public D divideBy(Decimal<?> divisor, TruncationPolicy truncationPolicy) {
		return divideUnscaled(divisor.unscaledValue(), divisor.getScale(), truncationPolicy);
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
	public D divide(long divisor, TruncationPolicy truncationPolicy) {
		final DecimalArithmetics arith = getArithmeticsFor(truncationPolicy);
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
	public D divide(double divisor, TruncationPolicy truncationPolicy) {
		final DecimalArithmetics arith = getArithmeticsFor(truncationPolicy);
		return createOrAssign(arith.divide(unscaledValue(), arith.fromDouble(divisor)));
	}

	@Override
	public D divideUnscaled(long unscaledDivisor) {
		final DecimalArithmetics arith = getDefaultArithmetics();
		return createOrAssign(arith.divide(unscaledValue(), unscaledDivisor));
	}

	@Override
	public D divideUnscaled(long unscaledDivisor, RoundingMode roundingMode) {
		final DecimalArithmetics arith = getArithmeticsFor(roundingMode);
		return createOrAssign(arith.divide(unscaledValue(), unscaledDivisor));
	}

	@Override
	public D divideUnscaled(long unscaledDivisor, TruncationPolicy truncationPolicy) {
		final DecimalArithmetics arith = getArithmeticsFor(truncationPolicy);
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
	public D divideUnscaled(long unscaledDivisor, int scale, TruncationPolicy truncationPolicy) {
		final DecimalArithmetics arith = getArithmeticsFor(truncationPolicy);
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
	public D divideByPowerOfTen(int n, TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticsFor(truncationPolicy).divideByPowerOf10(unscaledValue(), n));
	}

	@Override
	public D divideToIntegralValue(Decimal<S> divisor) {
		final long quot = unscaledValue() / divisor.unscaledValue();
		return createOrAssign(getDefaultArithmetics().fromLong(quot));
	}

	@Override
	public D[] divideAndRemainder(Decimal<S> divisor) {
		final long uDividend = unscaledValue();
		final long uDivisor = divisor.unscaledValue();
		final long lIntegral = uDividend / uDivisor;
		final long uIntegral = getDefaultArithmetics().fromLong(lIntegral);
		final long uReminder = uDividend - uDivisor * lIntegral;
		final D[] result = createArray(2);
		result[0] = create(uIntegral);
		result[1] = create(uReminder);
		return result;
	}

	@Override
	public D remainder(Decimal<S> divisor) {
		return createOrAssign(unscaledValue() % divisor.unscaledValue());
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
	public D negate(OverflowMode overflowMode) {
		return createOrAssign(getArithmeticsFor(overflowMode).negate(unscaledValue()));
	}

	@Override
	public D abs() {
		final long unscaled = unscaledValue();
		return unscaled >= 0 ? self() : createOrAssign(getDefaultArithmetics().negate(unscaled));
	}

	@Override
	public D abs(OverflowMode overflowMode) {
		final long unscaled = unscaledValue();
		return unscaled >= 0 ? self() : createOrAssign(getArithmeticsFor(overflowMode).negate(unscaled));
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
	public D invert(TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticsFor(truncationPolicy).invert(unscaledValue()));
	}
	
	@Override
	public D square() {
		return createOrAssign(getDefaultArithmetics().square(unscaledValue()));
	}
	
	@Override
	public D square(RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).square(unscaledValue()));
	}

	@Override
	public D square(TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticsFor(truncationPolicy).square(unscaledValue()));
	}

	@Override
	public D sqrt() {
		return createOrAssign(getDefaultArithmetics().sqrt(unscaledValue()));
	}
	
	@Override
	public D sqrt(RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).sqrt(unscaledValue()));
	}

	@Override
	public D shiftLeft(int n) {
		return createOrAssign(getDefaultArithmetics().shiftLeft(unscaledValue(), n));
	}

	@Override
	public D shiftLeft(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).shiftLeft(unscaledValue(), n));
	}

	@Override
	public D shiftLeft(int n, TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticsFor(truncationPolicy).shiftLeft(unscaledValue(), n));
	}

	@Override
	public D shiftRight(int n) {
		return createOrAssign(getDefaultArithmetics().shiftRight(unscaledValue(), n));
	}

	@Override
	public D shiftRight(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).shiftRight(unscaledValue(), n));
	}

	@Override
	public D shiftRight(int n, TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticsFor(truncationPolicy).shiftRight(unscaledValue(), n));
	}

	@Override
	public D pow(int n) {
		return createOrAssign(getDefaultArithmetics().pow(unscaledValue(), n));
	}

	@Override
	public D pow(int n, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).pow(unscaledValue(), n));
	}

	@Override
	public D pow(int n, TruncationPolicy truncationPolicy) {
		return createOrAssign(getArithmeticsFor(truncationPolicy).pow(unscaledValue(), n));
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
		return unscaledValue() == getScaleMetrics().getScaleFactor();
	}

	@Override
	public boolean isUlp() {
		return unscaledValue() == 1;
	}

	@Override
	public boolean isMinusOne() {
		return unscaledValue() == -getScaleMetrics().getScaleFactor();
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
		return isLessThanOrEqualTo(val) ? self() : val;
	}

	@Override
	public Decimal<S> max(Decimal<S> val) {
		return isGreaterThanOrEqualTo(val) ? this : val;
	}

	@Override
	public D avg(Decimal<S> val) {
		return createOrAssign(getDefaultArithmetics().avg(unscaledValue(), val.unscaledValue()));
	}

	@Override
	public D avg(Decimal<S> val, RoundingMode roundingMode) {
		return createOrAssign(getArithmeticsFor(roundingMode).avg(unscaledValue(), val.unscaledValue()));
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
}
