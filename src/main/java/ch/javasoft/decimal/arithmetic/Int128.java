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
	
	public int signum() {
		if (hi64 > 0) return 1; 
		if (hi64 < 0) return -1;
		return lo64 == 0 ? 0 : 1;
	}
	
	public Int128 neg() {
		final long notLo64 = ~this.lo64;
		final long notHi64 = ~this.hi64;
		final long negLo64 = notLo64 + 1;
		final long negHi64 = Math.signum(notLo64) == Math.signum(negLo64) ? notHi64 : notHi64 + 1;
		return new Int128(negHi64, negLo64);
	}
	public Int128 abs() {
		return signum() >= 0 ? this : neg();
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
		final long lo32_1 = value1 & 0x00000000ffffffffL;
		final long lo32_2 = value2 & 0x00000000ffffffffL;
		final long hi32_1 = value1 >>> 32;
		final long hi32_2 = value2 >>> 32;
		final long lo64 = lo32_1 * lo32_2;
		final long hi64 = hi32_1 * hi32_2 + ((lo32_1 * hi32_2) >>> 32) + ((lo32_2 * hi32_1) >>> 32);
		return new Int128(hi64, lo64);
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
	@Override
	public String toString() {
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
