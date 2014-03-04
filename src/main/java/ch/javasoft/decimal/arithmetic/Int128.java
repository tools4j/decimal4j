package ch.javasoft.decimal.arithmetic;

/**
 * Represents a signed 128 bit integer stored with two longs. 
 */
public class Int128 {
	private final long hi64;
	private final long lo64;
	
	public Int128(long hi64, long lo64) {
		this.hi64 = hi64;
		this.lo64 = lo64;
	}
	
	public long getHi64() {
		return hi64;
	}
	
	public long getLo64() {
		return lo64;
	}
	
	public boolean is64Bit() {
		return hi64 == 0 || (hi64 == -1 && lo64 < 0);
	}
	
	public boolean isNegative() {
		return hi64 < 0;
	}
	public int signum() {
		if (hi64 < 0) return -1;
		return (hi64 > 0 || lo64 != 0) ? 1 : 0; 
	}
	
	public Int128 neg() {
		return new Int128((~hi64) + (lo64 == 0 ? 1 : 0), (~lo64) + 1);
	}
	private static long negHi64(long hi64, long lo64) {
		return (~hi64) + (lo64 == 0 ? 1 : 0);
	}
	private static long negLo64(long hi64, long lo64) {
		return (~lo64) + 1;
	}
	public Int128 abs() {
		return isNegative() ? neg() : this;
	}

	public static Int128 add(long value1, long value2) {
		final long lo64 = value1 + value2;
		final int sgn1 = Long.signum(value1);
		final int sgn2 = Long.signum(value2);
		if (sgn1 == sgn2 && sgn1 != 0 && sgn2 != 0) {
			return new Int128(sgn1 > 0 ? 1 : -2, lo64);
		}
		return new Int128(lo64 >= 0 ? 0 : -1, lo64);
	}
	
	public static Int128 multiply(long value1, long value2) {
		int sgn = 1;
		if (value1 < 0) {
			value1 = (~value1) + 1;
			sgn = -sgn;
		} else if (value1 == 0) {
			sgn = 0;
		}
		if (value2 < 0) {
			value2 = (~value2) + 1;
			sgn = -sgn;
		} else if (value2 == 0) {
			sgn = 0;
		}
		final long lo32_1 = value1 & 0x00000000ffffffffL;
		final long lo32_2 = value2 & 0x00000000ffffffffL;
		final long hi32_1 = value1 >>> 32;
		final long hi32_2 = value2 >>> 32;
		final long lo64 = lo32_1 * lo32_2 + ((lo32_1 * hi32_2) << 32) + ((lo32_2 * hi32_1) << 32);
		final long hi64 = hi32_1 * hi32_2 + ((lo32_1 * hi32_2) >>> 32) + ((lo32_2 * hi32_1) >>> 32);
		if (sgn < 0) {
			return new Int128(negHi64(hi64, lo64), negLo64(hi64, lo64));
		}
		return new Int128(hi64, lo64);
	}
	
	public Int128 divideBy(long divisor) {
		final boolean isNeg = isNegative();
		long hi = isNeg ? negHi64(hi64, lo64) : hi64;
		long lo = isNeg ? negLo64(hi64, lo64) : lo64;
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
			if ((remainder > 0 && remainder >= divisor) || (remainder < 0 && (divisor > 0 || remainder <= divisor))) {
				remainder -= divisor;
				lo |= 1;
			}
		}

		if (divisor < 0) {
			hi = -hi;
			lo = -lo;
		}
		if (isNeg) {
			hi = negHi64(hi, lo);
			lo = negLo64(hi, lo);
		}
		return new Int128(hi, lo);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (hi64 ^ (hi64 >>> 32));
		result = prime * result + (int) (lo64 ^ (lo64 >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Int128 other = (Int128) obj;
		if (hi64 != other.hi64) return false;
		if (lo64 != other.lo64) return false;
		return true;
	}
	private static final String UINT128_MAXVALUE = "340282366920938463463374607431768211456";
	//                             Long.MAX_VALUE = 9223372036854775807
	private static final long DEC_LIMIT           = 1000000000000000000L;
	private static final String ZEROS             = "000000000000000000";
	private static final long UINT128_MAXVALUE_LO = 374607431768211456L;
	private static final long UINT128_MAXVALUE_MI = 282366920938463463L;
	private static final long UINT128_MAXVALUE_HI = 340L;
	@Override
	public String toString() {
		final int sgn = signum();
		long hi = hi64;
		long lo = lo64;
		long decHi = 0;
		long decMi = 0;
		long decLo = 0;
		for (int i = 0; i < 128; i++) {
		    final long carry = (hi >>> 63);
		    hi = (hi << 1) + (lo >>> 63); // shift left, plus carry 
		    lo = (lo << 1);

		    decLo <<= 1;
		    decMi <<= 1;
		    decHi <<= 1;
		    decLo += carry;
		    if (decLo >= DEC_LIMIT) {
		    	decLo -= DEC_LIMIT;
		    	decMi++;
		    }
		    if (decMi >= DEC_LIMIT) {
		    	decMi -= DEC_LIMIT;
		    	decHi++;
		    }
		    //decHi should not ever overflow
		}
		if (sgn < 0) {
			//subtract 2^128 to get actual signed result
			decLo -= UINT128_MAXVALUE_LO;
			decMi -= UINT128_MAXVALUE_MI;
			decHi -= UINT128_MAXVALUE_HI;
			
			//handle overflows now
			if (decHi >= 0) {
				//if most significant is positive, all should be positive
				if (decLo < 0) {
					decLo += DEC_LIMIT;
					decMi--;
				}
				if (decMi < 0) {
					decMi += DEC_LIMIT;
					decHi--;
				}
			}
			if (decHi < 0) {//test also if it was positive before, see decHi--
				//if most significant is negative, all should be negative
				if (decLo <= 0) {
					//ok but we want no sign char
					decLo = -decLo;
				} else {
					decLo = DEC_LIMIT - decLo;
					decMi++;
				}
				if (decMi <= 0) {
					//ok but we want no sign char
					decMi = -decMi;
				} else {
					decMi = DEC_LIMIT - decMi;
					decHi++;
				}
				
				//we want the negative sign at the most significant of the 3 longs
				if (decHi == 0) {
					if (decMi == 0) {
						decLo = -decLo;
					} else {
						decMi = -decMi;
					}
				}
			}
		}
		
		//convert to string now
		int off;
		final int zlen = ZEROS.length();
		final StringBuilder sb = new StringBuilder(UINT128_MAXVALUE.length() + 1);
		if (decHi != 0) {
			sb.append(decHi);
		}
		if (decHi != 0 || decMi != 0) {
			off = sb.length();
			sb.append(decMi);
			if (decHi != 0) {
				sb.insert(off, ZEROS, 0, zlen - (sb.length() - off));
			}
		}
		off = sb.length();
		sb.append(decLo);
		if (decHi != 0 || decMi != 0) {
			sb.insert(off, ZEROS, 0, ZEROS.length() - (sb.length() - off));
		}
		return sb.toString();
	}
	
	//nice but slower than above toString()
	@SuppressWarnings("unused")
	private String toStringWithDecimalArithmetics() {
		final int len = UINT128_MAXVALUE.length();
		final StringBuilder sb = new StringBuilder(len + 1);//maxlen + sign
		for (int i = 0; i < len; i++) {
			sb.append('0');
		}
		final int sgn = signum();
		long hi = hi64;
		long lo = lo64;
		for (int i = 0; i < 128; i++) {
		    long carry = (hi >>> 63);
		    hi = (hi << 1) + (lo >>> 63); // shift left, plus carry 
		    lo = (lo << 1);

		    // Add s[] to itself in decimal, doubling it
		    for (int j = 0; j < len; j++) {
		    	final char oldChar = sb.charAt(j);
		    	char newChar = (char)(oldChar + oldChar - '0' + carry); 
		    	if (newChar > '9') {
		    		carry = 1;
		    		newChar -= 10;
		    	} else {
		    		carry = 0;
		    	}
		    	sb.setCharAt(j, newChar);
			}
		}

		if (sgn < 0) {
			//subtract 2^128 to get actual signed result
			char carry = 0;
		    for (int j = 0; j < len; j++) {
		    	final char minChar = UINT128_MAXVALUE.charAt(len - j - 1);
		    	final char subChar = sb.charAt(j);
		    	char newChar = (char)('0' + minChar - subChar - carry); 
		    	if (newChar < '0') {
		    		carry = 1;
		    		newChar += 10;
		    	} else {
		    		carry = 0;
		    	}
		    	sb.setCharAt(j, newChar);
			}
		}
		int size = len;
		while (sb.charAt(size - 1) == '0' && size > 1) {
			size--;
		}
		if (sgn < 0) {
			sb.setLength(size + 1);
			sb.setCharAt(size, '-');
		} else {
			sb.setLength(size);
		}
		return sb.reverse().toString();
	}

}
