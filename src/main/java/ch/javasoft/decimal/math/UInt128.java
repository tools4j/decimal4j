package ch.javasoft.decimal.math;

import ch.javasoft.decimal.ScaleMetrics;

public class UInt128 {

	/**
	 * This mask is used to obtain the value of an int as if it were unsigned.
	 */
	private static final long LONG_MASK = 0xffffffffL;

	private static final ThreadLocal<int[]> VALUE_5W = new ThreadLocal<int[]>() {
		@Override
		protected int[] initialValue() {
			return new int[5];
		}
	};

	public static long divide128(ScaleMetrics scaleMetrics, long uDecimalDividend, long uDecimalDivisor) {
		final boolean negative = (uDecimalDividend < 0) != (uDecimalDivisor < 0);
		final long absDividend = Math.abs(uDecimalDividend);
		final long absDivisor = Math.abs(uDecimalDivisor);
		final int[] val160 = VALUE_5W.get();
		multiplyByScaleFactor(scaleMetrics, (int) (absDividend >> 32), (int)absDividend, val160);
		final long quot = divide128(val160, absDivisor);
		return negative ? -quot : quot;
	}

	//@see MutableBigInteger#multiply(MutableBigInteger y, MutableBigInteger)
	private static void multiplyByScaleFactor(ScaleMetrics scaleMetrics, int hFactor, int lFactor, int[] result160) {
		long product;
		long carry;

		//j=1;k=3
		product = scaleMetrics.mulloByScaleFactor(lFactor);
		carry = product >>> 32;
		result160[4] = (int) product;
		//j=0;k=2;
		product = scaleMetrics.mulhiByScaleFactor(lFactor) + carry;
		carry = product >>> 32;
		result160[3] = (int) product;
		result160[2] = (int) carry;
		//i=0;j=1;k=2;
		product = scaleMetrics.mulloByScaleFactor(hFactor) + (result160[3] & LONG_MASK);
		result160[3] = (int) product;
		carry = product >>> 32;
		//i=0;j=0;k=1;
		product = scaleMetrics.mulhiByScaleFactor(hFactor) + (result160[2] & LONG_MASK) + carry;
		result160[2] = (int) product;
		carry = product >>> 32;
		result160[1] = (int) carry;
		result160[0] = 0;
	}

	private static long divide128(int[] val160, long absDivisor) {
		final long hiBits = absDivisor >>> 32;
		if (hiBits == 0) {
			return divideOneWord(val160, (int) absDivisor);
		}
		return divideLongMagnitude(val160, absDivisor);
	}

	private static long divideOneWord(int[] dividend160, int divisor) {
		final long divisorLong = divisor & LONG_MASK;
		long remLong = dividend160[1] & LONG_MASK;
		if (remLong >= divisorLong) {
			final long tmp = remLong / divisorLong;
			remLong = remLong - (tmp * divisorLong);
		}
		long dividendEstimate;
		long remQuot;

		dividendEstimate = (remLong << 32) | (dividend160[2] & LONG_MASK);
		remQuot = divWord(dividendEstimate, divisor);
		remLong = remQuot >>> 32;
		dividendEstimate = (remLong << 32) | (dividend160[3] & LONG_MASK);
		remQuot = divWord(dividendEstimate, divisor);
		final long quotHi = remQuot & LONG_MASK;
		remLong = remQuot >>> 32;
		dividendEstimate = (remLong << 32) | (dividend160[4] & LONG_MASK);
		remQuot = divWord(dividendEstimate, divisor);
		final long quotLo = remQuot & LONG_MASK;
		remLong = remQuot >>> 32;

		return (quotHi << 32) | quotLo;
	}

	/**
	 * Divide the given value by the divisor represented by positive long value.
	 * The quotient will be placed into the provided value array.
	 */
	private static long divideLongMagnitude(int[] value, long ldivisor) {
		// Remainder starts as dividend with space for a leading zero
		//		MutableBigInteger rem = new MutableBigInteger(new int[intLen + 1]);
		//		System.arraycopy(value, offset, rem.value, 1, intLen);
		//		rem.intLen = intLen; ==== 4
		int offset = 1;

		//int nlen = rem.intLen ==== 4;

		//		int limit = nlen - 2 + 1; ==== 3
		//		if (quotient.value.length < limit) {
		//			quotient.value = new int[limit];
		//			quotient.offset = 0;
		//		}
		//		quotient.intLen = limit;
		int qh = 0;
		int ql = 0;

		// D1 normalize the divisor
		int shift = Long.numberOfLeadingZeros(ldivisor);
		if (shift > 0) {
			ldivisor <<= shift;
			offset = leftShift(value, 4, offset, shift);
		}

		// Must insert leading 0 in rem if its length did not change
		if (offset > 0) {
			offset = 0;
			value[0] = 0;
			//intLen++; ==== 5
		}

		int dh = (int) (ldivisor >>> 32);
		long dhLong = dh & LONG_MASK;
		int dl = (int) (ldivisor & LONG_MASK);

		// D2 Initialize j
		for (int j = 0; j < 3; j++) {
			// D3 Calculate qhat
			// estimate qhat
			int qhat = 0;
			int qrem = 0;
			boolean skipCorrection = false;
			int nh = value[j + offset];
			int nh2 = nh + 0x80000000;
			int nm = value[j + 1 + offset];

			if (nh == dh) {
				qhat = ~0;
				qrem = nh + nm;
				skipCorrection = qrem + 0x80000000 < nh2;
			} else {
				long nChunk = (((long) nh) << 32) | (nm & LONG_MASK);
				if (nChunk >= 0) {
					qhat = (int) (nChunk / dhLong);
					qrem = (int) (nChunk - (qhat * dhLong));
				} else {
					long tmp = divWord(nChunk, dh);
					qhat = (int) (tmp & LONG_MASK);
					qrem = (int) (tmp >>> 32);
				}
			}

			if (qhat == 0) continue;

			if (!skipCorrection) { // Correct qhat
				long nl = value[j + 2 + offset] & LONG_MASK;
				long rs = ((qrem & LONG_MASK) << 32) | nl;
				long estProduct = (dl & LONG_MASK) * (qhat & LONG_MASK);

				if (unsignedLongCompare(estProduct, rs)) {
					qhat--;
					qrem = (int) ((qrem & LONG_MASK) + dhLong);
					if ((qrem & LONG_MASK) >= dhLong) {
						estProduct -= (dl & LONG_MASK);
						rs = ((qrem & LONG_MASK) << 32) | nl;
						if (unsignedLongCompare(estProduct, rs)) qhat--;
					}
				}
			}

			// D4 Multiply and subtract
			value[j + offset] = 0;
			int borrow = mulsubLong(value, dh, dl, qhat, j + offset);

			// D5 Test remainder
			if (borrow + 0x80000000 > nh2) {
				// D6 Add back
				divaddLong(dh, dl, value, j + 1 + offset);
				qhat--;
			}

			// Store the quotient digit
			//			if (j == 0) qc = qhat;//carry
			if (j == 1) qh = qhat;
			else ql = qhat;
		} // D7 loop on j

		// D8 Unnormalize
		//		if (shift > 0) rem.rightShift(shift);

		//		quotient.normalize();
		//		rem.normalize();
		//		return rem;
		return ((qh & LONG_MASK) << 32) | (ql & LONG_MASK);
	}

	/**
	 * Returns long value where high 32 bits contain remainder value and low 32
	 * bits contain quotient value.
	 */
	private static long divWord(long n, int d) {
		final long dLong = d & LONG_MASK;
		long r;
		long q;
		if (n >= 0) {
			q = n / dLong;
			r = n - q * dLong;
		} else {
			if (dLong == 1) {
				return n & LONG_MASK;
			}

			// Approximate the quotient and remainder
			q = (n >>> 1) / (dLong >>> 1);
			r = n - q * dLong;

			// Correct the approximation
			while (r < 0) {
				r += dLong;
				q--;
			}
			while (r >= dLong) {
				r -= dLong;
				q++;
			}
			// n - q*dlong == r && 0 <= r <dLong, hence we're done.
		}
		return (r << 32) | (q & LONG_MASK);
	}

	/**
	 * A primitive used for division by long. Specialized version of the method
	 * divadd. dh is a high part of the divisor, dl is a low part
	 */
	private static int divaddLong(int dh, int dl, int[] result, int offset) {
		long carry = 0;

		long sum = (dl & LONG_MASK) + (result[1 + offset] & LONG_MASK);
		result[1 + offset] = (int) sum;

		sum = (dh & LONG_MASK) + (result[offset] & LONG_MASK) + carry;
		result[offset] = (int) sum;
		carry = sum >>> 32;
		return (int) carry;
	}

	/**
	 * This method is used for division by long. Specialized version of the
	 * method sulsub. dh is a high part of the divisor, dl is a low part
	 */
	private static int mulsubLong(int[] q, int dh, int dl, int x, int offset) {
		long xLong = x & LONG_MASK;
		offset += 2;
		long product = (dl & LONG_MASK) * xLong;
		long difference = q[offset] - product;
		q[offset--] = (int) difference;
		long carry = (product >>> 32) + (((difference & LONG_MASK) > (((~(int) product) & LONG_MASK))) ? 1 : 0);
		product = (dh & LONG_MASK) * xLong + carry;
		difference = q[offset] - product;
		q[offset--] = (int) difference;
		carry = (product >>> 32) + (((difference & LONG_MASK) > (((~(int) product) & LONG_MASK))) ? 1 : 0);
		return (int) carry;
	}

	/**
	 * Left shift this MutableBigInteger n bits.
	 */
	private static int leftShift(int[] value, int intLen, int offset, int n) {
		/*
		 * If there is enough storage space in this MutableBigInteger already
		 * the available space will be used. Space to the right of the used ints
		 * in the value array is faster to utilize, so the extra space will be
		 * taken from the right if possible.
		 */
		if (intLen == 0) return offset;
		int nInts = n >>> 5;
		int nBits = n & 0x1F;
		int bitsInHighWord = bitLengthForInt(value[offset]);

		// If shift can be done without moving words, do so
		if (n <= (32 - bitsInHighWord)) {
			primitiveLeftShift(value, intLen, offset, nBits);
			return offset;
		}

		int newLen = intLen + nInts + 1;
		if (nBits <= (32 - bitsInHighWord)) newLen--;
		if (value.length < newLen) {
			// The array must grow
			//            int[] result = new int[newLen];
			//            for (int i=0; i < intLen; i++)
			//                result[i] = value[offset+i];
			//            setValue(result, newLen);
			throw new RuntimeException("we should not get here");
		} else if (value.length - offset >= newLen) {
			// Use space on right
			for (int i = 0; i < newLen - intLen; i++)
				value[offset + intLen + i] = 0;
		} else {
			// Must use space on left
			for (int i = 0; i < intLen; i++)
				value[i] = value[offset + i];
			for (int i = intLen; i < newLen; i++)
				value[i] = 0;
			offset = 0;
		}
		intLen = newLen;
		if (nBits == 0) return offset;
		if (nBits <= (32 - bitsInHighWord)) primitiveLeftShift(value, intLen, offset, nBits);
		else primitiveRightShift(value, intLen, offset, 32 - nBits);
		return offset;
	}

	/**
	 * Right shift val n bits, where n is less than 32. Assumes that intLen > 0,
	 * n > 0 for speed
	 */
	private static final void primitiveRightShift(int[] val, int intLen, int offset, int n) {
		int n2 = 32 - n;
		for (int i = offset + intLen - 1, c = val[i]; i > offset; i--) {
			int b = c;
			c = val[i - 1];
			val[i] = (c << n2) | (b >>> n);
		}
		val[offset] >>>= n;
	}

	/**
	 * Left shift this val n bits, where n is less than 32. Assumes that intLen
	 * > 0, n > 0 for speed
	 */
	private static final void primitiveLeftShift(int[] val, int intLen, int offset, int n) {
		int n2 = 32 - n;
		for (int i = offset, c = val[i], m = i + intLen - 1; i < m; i++) {
			int b = c;
			c = val[i + 1];
			val[i] = (b << n) | (c >>> n2);
		}
		val[offset + intLen - 1] <<= n;
	}

    /**
     * Package private method to return bit length for an integer.
     */
    private static int bitLengthForInt(int n) {
        return 32 - Integer.numberOfLeadingZeros(n);
    }

    /**
	 * Compare two longs as if they were unsigned. Returns true iff one is
	 * bigger than two.
	 */
	private static boolean unsignedLongCompare(long one, long two) {
		return (one + Long.MIN_VALUE) > (two + Long.MIN_VALUE);
	}
}
