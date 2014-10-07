package ch.javasoft.decimal.scale;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.EnumMap;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.ImmutableDecimal;
import ch.javasoft.decimal.MutableDecimal;
import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.arithmetic.CheckedScaleNfTruncatingArithmetics;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.arithmetic.DecimalRounding;
import ch.javasoft.decimal.arithmetic.UncheckedScaleNfRoundingArithmetics;
import ch.javasoft.decimal.arithmetic.UncheckedScaleNfTruncatingArithmetics;

/**
 * <tt>ScaleMetrics</tt> is associated with {@link Decimal} numbers and
 * represents the factor applied to the {@code long} value underlying a
 * {@code Decimal}. Scale stands for the fixed number of fraction digits of a
 * {@code Decimal}.
 * <p>
 * The <tt>Scale</tt> class contains a number of subclasses used by different
 * decimal types. With <tt>Scale</tt> subclasses, it is possible to distinguish
 * different decimal types and we can ensure that only decimals of the same
 * scale can directly operate with each other.
 */
abstract public class AbstractScale implements ScaleMetrics {

	/**
	 * This mask is used to obtain the value of an int as if it were unsigned.
	 */
	final static long LONG_MASK = 0xffffffffL;

	private final long maxIntegerValue = divideByScaleFactor(Long.MAX_VALUE);
	private final long minIntegerValue = divideByScaleFactor(Long.MIN_VALUE);
	private final BigInteger biScaleFactor = BigInteger.valueOf(getScaleFactor());
	private final BigDecimal bdScaleFactor = new BigDecimal(biScaleFactor);

	private final EnumMap<RoundingMode, DecimalArithmetics> roundingModeToArithmetics = initArithmetics();
	private final EnumMap<RoundingMode, DecimalArithmetics> roundingModeToCheckedArithmetics = initCheckedArithmetics();

	/**
	 * Initialises the arithmetics map. {@link Scale0f} overrides this method.
	 * 
	 * @return the rounding mode to arithmetics map
	 */
	protected EnumMap<RoundingMode, DecimalArithmetics> initArithmetics() {
		final EnumMap<RoundingMode, DecimalArithmetics> map = new EnumMap<RoundingMode, DecimalArithmetics>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, new UncheckedScaleNfTruncatingArithmetics(this));
			} else {
				map.put(roundingMode, new UncheckedScaleNfRoundingArithmetics(this, dr));
			}
		}
		return map;
	}

	/**
	 * Initialises the checked arithmetics map. {@link Scale0f} overrides this
	 * method.
	 * 
	 * @return the rounding mode to checked arithmetics map
	 * @see OverflowMode#CHECKED
	 */
	protected EnumMap<RoundingMode, DecimalArithmetics> initCheckedArithmetics() {
		final EnumMap<RoundingMode, DecimalArithmetics> map = new EnumMap<RoundingMode, DecimalArithmetics>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, new CheckedScaleNfTruncatingArithmetics(this));
			} else {
				//FIXME add when implemented
				//				map.put(roundingMode, new CheckedScaledRoundingArithmetics(this, dr));
			}
		}
		return map;
	}

	@Override
	public BigInteger getScaleFactorAsBigInteger() {
		return biScaleFactor;
	}

	@Override
	public BigDecimal getScaleFactorAsBigDecimal() {
		return bdScaleFactor;
	}

	@Override
	public long getMaxIntegerValue() {
		return maxIntegerValue;
	}

	@Override
	public long getMinIntegerValue() {
		return minIntegerValue;
	}

	@Override
	abstract public long multiplyByScaleFactor(long factor);

	@Override
	public long multiplyByScaleFactorExact(long factor) {
		final long scaleFactor = getScaleFactor();
		final int leadingZeros = Long.numberOfLeadingZeros(factor) + Long.numberOfLeadingZeros(~factor) + Long.numberOfLeadingZeros(scaleFactor);
		final long result = multiplyByScaleFactor(factor);
		if (leadingZeros > Long.SIZE + 1) {
			return result;
		}
		if (leadingZeros < Long.SIZE | divideByScaleFactor(result) != factor) {
			throw new ArithmeticException("overflow: " + factor + " * " + scaleFactor + " = " + result);
		}
		return result;
	}

	@Override
	public ImmutableDecimal<?, ?> createImmutable(long unscaled) {
		// FIXME impl
		throw new RuntimeException("not implemented for " + this);
	}

	@Override
	public MutableDecimal<?, ?> createMutable(long unscaled) {
		// FIXME impl
		throw new RuntimeException("not implemented for " + this);
	}

	@Override
	public DecimalArithmetics getDefaultArithmetics() {
		return getArithmetics(RoundingMode.HALF_UP);
	}

	@Override
	public DecimalArithmetics getTruncatingArithmetics() {
		return getArithmetics(RoundingMode.DOWN);
	}

	@Override
	public DecimalArithmetics getArithmetics(RoundingMode roundingMode) {
		return roundingModeToArithmetics.get(roundingMode);
	}

	@Override
	public DecimalArithmetics getCheckedArithmetics(RoundingMode roundingMode) {
		return roundingModeToCheckedArithmetics.get(roundingMode);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
