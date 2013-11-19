package ch.javasoft.search;

/**
 * Array for n-bit values, where {@code n in [0, 64]}. The n-bit values are
 * stored in a long array.
 */
public class NBitValueArray {
	private final int count;
	private final int bitsPerValue;
	private final long mask;
	private final long[] bits;
	public NBitValueArray(int count, int bitsPerValue) {
		if (count < 0) {
			throw new IllegalArgumentException("count cannot be negative: " + count);
		}
		if (bitsPerValue < 0 || bitsPerValue > 64) {
			throw new IllegalArgumentException("bitsPerValue must be in [0, 64] but was " + bitsPerValue);
		}
		this.count = count;
		this.bitsPerValue = bitsPerValue;
		this.mask = 0xffffffffffffffffL >>> (64 - (bitsPerValue % 64));
		final long totalBits = ((long)count) * bitsPerValue;
		final long arraySize = 1 + (totalBits - 1) / 64;
		if (arraySize > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("not enough storage for " + totalBits + " bits");
		}
		this.bits = new long[(int)arraySize];
	}
	
	public final int getCount() {
		return count;
	}
	
	public final int getBitsPerValue() {
		return bitsPerValue;
	}
	
	public long getMask() {
		return mask;
	}
	
	public void checkIndex(int index) {
		if (index < 0 || index > count) {
			throw new IndexOutOfBoundsException("index expected in [0, " + (count-1) + "] but was " + index);
		}
	}
	public long get(int index) {
		checkIndex(index);
		return getUnchecked(index);
	}

	public long getUnchecked(int index) {
		final long[] bits = this.bits;
		final int bitsPerValue = this.bitsPerValue;
		final long bitPos = index * bitsPerValue;
		final long arrPos = bitPos / 64;
		final long arrMod = bitPos % 64;
		final int bits0 = (int)(64 - arrMod);
		final int bits1 = bitsPerValue - bits0;
		final long val0 = bits[(int)arrPos];
		long result = (val0 >>> arrMod);
		if (bits1 > 0) {
			final long val1 = bits[(int)(arrPos+1)];
			result |= (val1 << bits0);
		}
		return result & mask;
	}
	
	public void set(int index, long value) {
		checkIndex(index);
		setUnchecked(index, value & mask);
	}
	public void setUnchecked(int index, long value) {
		final long[] bits = this.bits;
		final long mask = this.mask;
		final int bitsPerValue = this.bitsPerValue;
		final long bitPos = index * bitsPerValue;
		final long arrPos = bitPos / 64;
		final long arrMod = bitPos % 64;
		final int bits0 = (int)(64 - arrMod);
		final int bits1 = bitsPerValue - bits0;
		long val0 = bits[(int)arrPos];
		val0 |= (mask << arrMod);
		val0 ^= ((value ^ mask) << arrMod);
		bits[(int)arrPos] = val0;
		if (bits1 > 0) {
			long val1 = bits[(int)(arrPos+1)];
			val1 = ((val1 >>> bits1) << bits1);//clear our bits
			val1 |= (value >>> bits0);
			bits[(int)(arrPos+1)] = val1;
		}		
	}

	public long byteSize() {
		return 8L * bits.length;
	}
}
