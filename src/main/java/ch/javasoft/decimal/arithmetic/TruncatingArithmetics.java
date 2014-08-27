package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import ch.javasoft.decimal.OverflowMode;
import ch.javasoft.decimal.Scale;
import ch.javasoft.decimal.math.MutableBigInteger;

/**
 * An arithmetic implementation which truncates digits after the last scale
 * digit without rounding; the result of an operation that leads to an overflow
 * is silently truncated.
 */
public class TruncatingArithmetics extends AbstractScaledArithmetics implements
		DecimalArithmetics {

	/**
	 * Constructor for silent decimal arithmetics with given scale, truncating
	 * {@link RoundingMode#DOWN DOWN} rounding mode and
	 * {@link OverflowMode#SILENT SILENT} overflow mode.
	 * 
	 * @param scale
	 *            the scale, a non-negative integer denoting the number of
	 *            digits to the right of the decimal point
	 * @throws IllegalArgumentException
	 *             if scale is negative, zero or uneven
	 */
	public TruncatingArithmetics(int scale) {
		super(scale);
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
	public TruncatingArithmetics(Scale scale) {
		super(scale);
	}

	@Override
	public RoundingMode getRoundingMode() {
		return RoundingMode.DOWN;
	}

	@Override
	public DecimalArithmetics derive(int scale) {
		return scale == getScale() ? this : scale == 0 ? LongArithmetics.INSTANCE : new TruncatingArithmetics(scale);
	}

	@Override
	public DecimalArithmetics derive(RoundingMode roundingMode) {
		if (roundingMode == getRoundingMode()) {
			return this;
		}
		return new RoundingArithmetics(getScale(), roundingMode);
	}

	@Override
	public DecimalArithmetics derive(OverflowMode overflowMode) {
		if (overflowMode == getOverflowMode()) {
			return this;
		}
		//FIXME implement overflow mode derivative
		throw new RuntimeException("overflow mode not supported yet: " + overflowMode);
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		return multiply(uDecimal1, uDecimal2, one());
	}

	static long multiply(long uDecimal1, long uDecimal2, long one) {
		final long i1 = uDecimal1 / one;
		final long i2 = uDecimal2 / one;
		final long f1 = uDecimal1 - i1 * one;
		final long f2 = uDecimal2 - i2 * one;
		return i1 * i2 * one + i1 * f2 + i2 * f1 + (f1 * f2) / one;
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		return divide(uDecimalDividend, uDecimalDivisor, getScale(), one());
	}

	public static long divide(long uDecimalDividend, long uDecimalDivisor, int scale, long one) {
		//special cases first
		if (uDecimalDivisor == 0) {
			throw new ArithmeticException("division by zero: " + toString(uDecimalDividend, scale) + "/" + toString(uDecimalDivisor, scale));
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
//		if (uDecimalDividend <= Long.MAX_VALUE / one && uDecimalDividend >= Long.MIN_VALUE / one) {
//			//just do it, multiplication result fits in long
//			return (uDecimalDividend * one) / uDecimalDivisor;
//		}
		//128 bit multiplication and division now
		//		final boolean negative = (uDecimalDividend < 0) != (uDecimalDivisor < 0);
		//		final long[] prod = unsignedMul128(Math.abs(uDecimalDividend), one);
		//		final long result = unsignedDiv128(prod, Math.abs(uDecimalDivisor));
		//		final long result = unsignedMulDiv128(Math.abs(uDecimalDividend), one, Math.abs(uDecimalDivisor));
		//		return negative ? -result : result;

//		return BigInteger.valueOf(uDecimalDividend).multiply(BigInteger.valueOf(one)).divide(BigInteger.valueOf(uDecimalDivisor)).longValue();
		final boolean negative = (uDecimalDividend < 0) != (uDecimalDivisor < 0);
		final MutableBigInteger mDividend = new MutableBigInteger(Math.abs(uDecimalDividend));
		final MutableBigInteger mOne = new MutableBigInteger(one);
		final MutableBigInteger mResult = new MutableBigInteger(Long.MAX_VALUE);
		mDividend.multiply(mOne, mResult);
		mResult.divide(Math.abs(uDecimalDivisor), mOne);
		return negative ? -mOne.longValue() : mOne.longValue();
//		return BigInteger.valueOf(uDecimalDividend).multiply(BigInteger.valueOf(one)).divide(BigInteger.valueOf(uDecimalDivisor)).longValue();
	}

	//see http://svn.gnucash.org/docs/head/group__Math128.html
	//no negative values!
	private static long unsignedMulDiv128(long a, long b, long divisor) {
		final long a1 = a >>> 32;
		final long a0 = a - (a1 << 32);

		final long b1 = b >>> 32;
		final long b0 = b - (b1 << 32);

		final long d = a0 * b0;
		final long d1 = d >>> 32;
		final long d0 = d - (d1 << 32);

		final long e = a0 * b1;
		final long e1 = e >>> 32;
		final long e0 = e - (e1 << 32);

		final long f = a1 * b0;
		final long f1 = f >>> 32;
		final long f0 = f - (f1 << 32);

		final long g = a1 * b1;
		final long g1 = g >>> 32;
		final long g0 = g - (g1 << 32);

		long sum = d1 + e0 + f0;
		long carry = 0;
		long roll = 1L << 32;

		long pmax = roll - 1;
		while (pmax < sum) {
			sum -= roll;
			carry++;
		}

		long lo = d0 + (sum << 32);
		long hi = carry + e1 + f1 + g0 + (g1 << 32);
		long remainder = 0;

		/* Use grade-school long division algorithm */
		for (int i = 0; i < 128; i++) {
			remainder <<= 1;
			remainder += (hi >>> 63);//carry
			hi <<= 1;
			hi += (lo >>> 63);//carry
			lo <<= 1;
			//remainder
			if ((remainder > 0 && remainder >= divisor) || (remainder < 0 && (divisor > 0 || remainder <= divisor))) {
				remainder -= divisor;
				lo |= 1;
			}
		}

		return divisor < 0 ? -lo : lo;//divisor could be Long.MIN_VALUE since abs(Long.MIN_VALUE) is still Long.MIN_VALUE
	}

	//see http://svn.gnucash.org/docs/head/group__Math128.html
	//no negative values!
	private static long[] unsignedMul128(long a, long b) {
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
	private static long unsignedDiv128(long[] dividend, long divisor) {
		long lo = dividend[0];
		long hi = dividend[1];
		long remainder = 0;

		/* Use grade-school long division algorithm */
		for (int i = 0; i < 128; i++) {
			remainder <<= 1;
			remainder += (hi >>> 63);//carry
			hi <<= 1;
			hi += (lo >>> 63);//carry
			lo <<= 1;
			//remainder
			if ((remainder > 0 && remainder >= divisor) || (remainder < 0 && (divisor > 0 || remainder <= divisor))) {
				remainder -= divisor;
				lo |= 1;
			}
		}

		return divisor < 0 ? -lo : lo;//divisor could be Long.MIN_VALUE since abs(Long.MIN_VALUE) is still Long.MIN_VALUE
	}

	@Override
	public long invert(long uDecimal) {
		final long one = one();
		return (one * one) / uDecimal;
	}

	@Override
	public long pow(long uDecimal, int exponent) {
		return pow(this, uDecimal, exponent);
	}

	static long pow(DecimalArithmetics arithmetics, long uDecimal, int exponent) {
		if (exponent == 0) {
			return arithmetics.one();
		}
		long base;
		int exp;
		if (exponent > 0) {
			base = uDecimal;
			exp = exponent;
		} else {/* exponent < 0 */
			base = arithmetics.invert(uDecimal);
			exp = -exponent;
		}
		long result = base;
		//TODO eliminate repeated truncation with multiplications in loop
		while (exp != 1 && result != 0) {
			if (exp % 2 == 0) {
				//even
				result = arithmetics.multiply(result, result);
				exp >>>= 1;
			} else {
				//odd
				result = arithmetics.multiply(result, base);
				exp--;
			}
		}
		return result;
	}

	@SuppressWarnings("unused")
	private long fromDoubleBisect(double value) {
		if (Double.isNaN(value) || Double.isInfinite(value)) {
			throw new ArithmeticException("cannot convert double to decimal: " + value);
		}
		final long lOne = one();
		final double fOne = lOne;
		double fCeil = Math.ceil(value);
		double fFloor = Math.floor(value);
		long lCeil = ((long) fCeil) * lOne;
		long lFloor = ((long) fFloor) * lOne;
		if (Long.signum(lCeil) != Long.signum(lFloor)) {
			return value >= 0 ? lFloor : lCeil;
		}
		long lMed = (lFloor >> 1) + (lCeil >> 1) + ((lFloor & lCeil) & 0x1);//(lFloor + lCeil) / 2 --- but avoid overflow
		if (value >= 0) {
			while (lCeil - lFloor > 1) {
				if ((lMed / fOne) <= value) {
					lFloor = lMed;
				} else {
					lCeil = lMed;
				}
				lMed = (lFloor >> 1) + (lCeil >> 1) + ((lFloor & lCeil) & 0x1);//(lFloor + lCeil) / 2 --- but avoid overflow
			}
			return ((lCeil / fOne) <= value) ? lCeil : lFloor;
		} else {
			while (lCeil - lFloor > 1) {
				if ((lMed / fOne) >= value) {
					lCeil = lMed;
				} else {
					lFloor = lMed;
				}
				lMed = (lFloor >> 1) + (lCeil >> 1) + ((lFloor & lCeil) & 0x1);//(lFloor + lCeil) / 2 --- but avoid overflow
			}
			return ((lFloor / fOne) >= value) ? lFloor : lCeil;
		}
	}

	@Override
	public long fromBigDecimal(BigDecimal value) {
		return value.multiply(oneBigDecimal()).longValue();
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

}
