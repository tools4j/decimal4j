package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.Scale;

/**
 * An arithmetic implementation which truncates digits after the last scale
 * digit without rounding; the result of an operation that leads to an overflow 
 * is silently truncated.
 */
public class TruncatingDecimalArithmetics implements DecimalArithmetics {

	private final int scale;
	private final long one;//10^scale
	protected final long sqrtOne;//10^(scale/2) = sqrt(10^scale)

	/**
	 * Constructor for silent decimal arithmetics with given scale, truncating
	 * {@link RoundingMode#DOWN DOWN} rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative or uneven
	 */
	public TruncatingDecimalArithmetics(int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("scale cannot be negative: " + scale);
		}
		if (scale % 2 != 0) {
			throw new IllegalArgumentException("uneven scale currently not supported: " + scale);
		}
		if (scale > 18) {
			throw new IllegalArgumentException("scale is too large: " + scale);
		}
		this.scale = scale;
		long sqrtOne = 1;
		for (int i = 0; i < scale / 2; i++) {
			sqrtOne *= 10;
		}
		long one = sqrtOne;
		for (int i = scale / 2; i < scale; i++) {
			one *= 10;
		}
		if (Long.MAX_VALUE / one < one) {
			//one * one must still fit in a long for our computations in this class
			throw new IllegalArgumentException("scale is too large: " + scale);
		}
		this.one = one;
		this.sqrtOne = sqrtOne;
	}

	/**
	 * Constructor for silent decimal arithmetics with given scale, truncating
	 * {@link RoundingMode#DOWN DOWN} rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative or uneven
	 */
	public TruncatingDecimalArithmetics(Scale scale) {
		this(scale.getFractionDigits());
	}

	@Override
	public int getScale() {
		return scale;
	}

	@Override
	public RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public OverflowMode getOverflowMode() {
		return OverflowMode.SILENT;
	}

	@Override
	public DecimalArithmetics derive(int scale) {
		return scale == getScale() ? this : new TruncatingDecimalArithmetics(scale);
	}
	
	@Override
	public DecimalArithmetics derive(RoundingMode roundingMode) {
		final RoundingMode current = getRoundingMode();
		if (roundingMode == current) {
			return this;
		}
		switch (roundingMode) {
		case UP:
			return new RoundUpDecimalArithmetics(getScale());
		case DOWN:
			return new TruncatingDecimalArithmetics(getScale());
//			return new RoundDownDecimalArithmetics(getScale());
		case CEILING:
			return new RoundCeilingDecimalArithmetics(getScale());
		case FLOOR:
			return new RoundFloorDecimalArithmetics(getScale());
		case HALF_UP:
			return new RoundHalfUpDecimalArithmetics(getScale());
		case HALF_DOWN:
			return new RoundHalfDownDecimalArithmetics(getScale());
		case HALF_EVEN:
			return new RoundHalfEvenDecimalArithmetics(getScale());
		case UNNECESSARY:
			return new RoundUnnecessaryDecimalArithmetics(getScale());
		default:
			throw new IllegalArgumentException("unsupported rounding mode: " + roundingMode);
		}
	}

	@Override
	public long one() {
		return one;
	}

	@Override
	public int signum(long uDecimal) {
		return uDecimal > 0 ? 1 : uDecimal == 0 ? 0 : -1;
	}

	@Override
	public int compare(long uDecimal1, long uDecimal2) {
		return (uDecimal1 < uDecimal2) ? -1 : ((uDecimal1 == uDecimal2) ? 0 : 1);
	}

	@Override
	public long add(long uDecimal1, long uDecimal2) {
		return uDecimal1 + uDecimal2;
	}

	@Override
	public long subtract(long uDecimalMinuend, long uDecimalSubtrahend) {
		return uDecimalMinuend - uDecimalSubtrahend;
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		final long one = one();
		final long sqrtOne = this.sqrtOne;
		final long i1 = uDecimal1 / sqrtOne;
		final long i2 = uDecimal2 / sqrtOne;
		final long f1 = uDecimal1 % sqrtOne;
		final long f2 = uDecimal2 % sqrtOne;
		return i1 * i2 + (i1 * f2 * sqrtOne + i2 * f1 * sqrtOne + f1 * f2) / one;
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		final long one = one();
		//special cases first
		if (uDecimalDivisor == 0) {
			throw new ArithmeticException("division by zero: " + toString(uDecimalDividend) + "/" + toString(uDecimalDivisor));
		}
		if (uDecimalDividend == 0) {
			return 0;
		}
		if (uDecimalDivisor == one) {
			return uDecimalDividend;
		}
		if (uDecimalDivisor == -one) {
			return -uDecimalDividend;
		}
		if (uDecimalDividend == uDecimalDivisor) {
			return one;
		}
		//WE WANT: uDecimalDividend * one / uDecimalDivisor
		if (uDecimalDividend <= Long.MAX_VALUE / one && uDecimalDividend >= Long.MIN_VALUE / one) {
			//just do it, multiplication result fits in long
			return (uDecimalDividend * one) / uDecimalDivisor;
		}
		//128 bit multiplication and division now
		final boolean negative = (uDecimalDividend < 0) != (uDecimalDivisor < 0);
 		final long[] prod = unsignedMul128(Math.abs(uDecimalDividend), one);
		final long result = unsignedDiv128(prod, Math.abs(uDecimalDivisor));
		return negative ? -result : result;
	}

	//see http://svn.gnucash.org/docs/head/group__Math128.html
	//no negative values!
	private long[] unsignedMul128(long a, long b) {
		long[] prod = new long[2];
		long a0, a1;
		long b0, b1;
		long d, d0, d1;
		long e, e0, e1;
		long f, f0, f1;
		long g, g0, g1;
		long sum, carry, roll, pmax;

		a1 = a >>> 32;
		a0 = a - (a1 << 32);

		b1 = b >>> 32;
		b0 = b - (b1 << 32);

		d = a0 * b0;
		d1 = d >>> 32;
		d0 = d - (d1 << 32);

		e = a0 * b1;
		e1 = e >>> 32;
		e0 = e - (e1 << 32);

		f = a1 * b0;
		f1 = f >>> 32;
		f0 = f - (f1 << 32);

		g = a1 * b1;
		g1 = g >>> 32;
		g0 = g - (g1 << 32);

		sum = d1 + e0 + f0;
		carry = 0;
		roll = 1L << 32;

		pmax = roll - 1;
		while (pmax < sum) {
			sum -= roll;
			carry++;
		}

		prod[0] = d0 + (sum << 32);
		prod[1] = carry + e1 + f1 + g0 + (g1 << 32);

		return prod;
	}

	//see http://svn.gnucash.org/docs/head/group__Math128.html
	//no negative values!
	private long unsignedDiv128(long[] dividend, long divisor) {
		long lo = dividend[0];
		long hi = dividend[1];
		long remainder = 0;

		/* Use grade-school long division algorithm */
		for (int i = 0; i < 128; i++) {
			remainder <<= 1;
			if (hi < 0) remainder |= 1;
			//leftshift by 1, i.e. multiply by 2
			hi <<= 1;
			if (lo < 0) hi |= 1;
			lo <<= 1;
			//remainder
			if ((remainder > 0 && remainder >= divisor) || (remainder < 0 && (divisor >0 || remainder <= divisor))) {
				remainder -= divisor;
				lo |= 1;
			}
		}

		return divisor < 0 ? -lo : lo;//divisor could be Long.MIN_VALUE since abs(Long.MIN_VALUE) is still Long.MIN_VALUE
	}

	@Override
	public long abs(long uDecimal) {
		return Math.abs(uDecimal);
	}

	@Override
	public long negate(long uDecimal) {
		return -uDecimal;
	}

	@Override
	public long invert(long uDecimal) {
		final long one = one();
		return (one * one) / uDecimal;
	}

	@Override
	public long pow(long uDecimal, int exponent) {
		if (exponent == 0) {
			return one();
		}
		long base;
		int exp;
		if (exponent > 0) {
			base = uDecimal;
			exp = exponent;
		} else {/* exponent < 0 */
			base = invert(uDecimal);
			exp = -exponent;
		}
		long result = base;
		while (exp != 1 && result != 0) {
			if (exp % 2 == 0) {
				//even
				result = multiply(result, result);
				exp >>>= 1;
			} else {
				//odd
				result = multiply(result, base);
				exp--;
			}
		}
		return result;
	}

	@Override
	public double toDouble(long uDecimal) {
		return Double.valueOf(toString(uDecimal));
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal) {
		return BigDecimal.valueOf(uDecimal, getScale());
	}

	@Override
	public String toString(long uDecimal) {
		return toString(uDecimal, getScale());
	}

	@Override
	public String toString(long uDecimal, int precision) {
		if (precision < 0) {
			throw new IllegalArgumentException("precision cannot be negative: " + precision);
		}
		final int negativeOffset = uDecimal < 0 ? 1 : 0;
		final StringBuilder sb = new StringBuilder(precision + 2 + negativeOffset);
		sb.append(uDecimal);
		final int len = sb.length();
		if (len <= precision + negativeOffset) {
			//Long.MAX_VALUE = 9,223,372,036,854,775,807
			sb.insert(negativeOffset, "0.00000000000000000000", 0, 2 + precision - len + negativeOffset);
		} else {
			sb.insert(len - precision, '.');
		}
		return sb.toString();
	}

	@Override
	public long shiftLeft(long uDecimal, int positions) {
		return uDecimal << positions;
	}

	@Override
	public long shiftRight(long uDecimal, int positions) {
		return uDecimal >> positions;
	}

	@Override
	public long movePointLeft(long uDecimal, int positions) {
		if (positions >= 0) {
			long result = uDecimal;
			//NOTE: this is not very efficient
			for (int i = 0; i < positions && result != 0; i++) {
				result /= 10;
			}
			return result;
		} else {
			if (positions < Integer.MIN_VALUE) {
				return movePointRight(uDecimal, -positions);
			}
			long halfResult = movePointRight(uDecimal, -(positions / 2));
			return movePointRight(halfResult, -(positions / 2));
		}
	}

	@Override
	public long movePointRight(long uDecimal, int positions) {
		if (positions >= 0) {
			long result = uDecimal;
			//NOTE: this is not very efficient
			for (int i = 0; i < positions && result != 0; i++) {
				result *= 10;
			}
			return result;
		} else {
			return movePointLeft(uDecimal, -positions);
		}
	}

	@Override
	public long fromLong(long value) {
		return value * one();
	}

	@Override
	public long fromDouble(double value) {
		if (Double.isNaN(value) || Double.isInfinite(value)) {
			throw new ArithmeticException("cannot convert double to decimal: " + value);
		}
		final long one = one();
		final double iValue = Math.rint(value);
		final double fValue = value - iValue;
		return ((long) iValue) * one + (long) (fValue * one);
	}

	@Override
	public long fromBigInteger(BigInteger value) {
		return value.multiply(BigInteger.valueOf(one())).longValue();
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		return value.multiply(BigDecimal.valueOf(one())).longValue();
	}

	@Override
	public long fromUnscaled(long unscaledValue, int scale) {
		final int targetScale = getScale();
		if (scale == 0) {
			return fromLong(unscaledValue);
		}
		long result = unscaledValue;
		for (int i = scale; i < targetScale; i++) {
			result *= 10;
		}
		for (int i = targetScale; i < scale; i++) {
			result /= 10;
		}
		return result;
	}

	@Override
	public long parse(String value) {
		final int indexOfDot = value.indexOf('.');
		if (indexOfDot < 0) {
			return fromLong(Long.parseLong(value));
		}
		final long iValue;
		if (indexOfDot > 0) {
			//NOTE: here we handle the special case "-.xxx" e.g. "-.25"
			iValue = indexOfDot == 1 && value.charAt(0) == '-' ? 0 : Long.parseLong(value.substring(0, indexOfDot));
		} else {
			iValue = 0;
		}
		final String fractionalPart = value.substring(indexOfDot + 1);
		final long fValue;
		final int fractionalLength = fractionalPart.length();
		if (fractionalLength > 0) {
			long fractionDigits = Long.parseLong(fractionalPart);
			final int scale = getScale();
			for (int i = fractionalLength; i < scale; i++) {
				fractionDigits *= 10;
			}
			for (int i = scale; i < fractionalLength; i++) {
				fractionDigits /= 10;
			}
			fValue = fractionDigits;
		} else {
			fValue = 0;
		}
		final boolean negative = iValue < 0 || value.startsWith("-");
		return iValue * one() + (negative ? -fValue : fValue);
	}

	@Override
	public long toLong(long uDecimal) {
		return uDecimal / one();
	}

	@Override
	public BigDecimal toBigDecimal(long uDecimal, int scale) {
		return toBigDecimal(uDecimal).round(new MathContext(scale));
	}

}
