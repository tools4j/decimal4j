package ch.javasoft.search.util;

/**
 * Implementation of {@link KBitValueBuffer} storing the k-bit values in a long
 * array. Every value leads to at least one and at most two value accesses. No
 * bits are wasted due to padding except for those at the very end of the array.
 */
public class LongArrayKBitValueBuffer extends AbstractKBitValueBuffer {

	public static final Factory FACTORY = new Factory() {
		@Override
		public KBitValueBuffer create(int count, int k) {
			return new LongArrayKBitValueBuffer(count, k);
		}
	};

	private final long[] bits;

	public LongArrayKBitValueBuffer(int count, int bitsPerValue) {
		super(count, bitsPerValue);
		final long totalBits = ((long) count) * bitsPerValue;
		final long arraySize = 1 + (totalBits - 1) / 64;
		if (arraySize > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("not enough storage for " + totalBits + " bits");
		}
		this.bits = new long[(int) arraySize];
	}

	@Override
	protected long getUnchecked(int index) {
		final long[] bits = this.bits;
		final int bitsPerValue = getBitsPerValue();
		final long bitPos = index * bitsPerValue;
		final int arrPos = (int) (bitPos >>> 6);//bitPos / 64
		final int arrMod = (int) (bitPos & 63);//bitPos % 64
		final int bits0 = 64 - arrMod;
		final int bits1 = bitsPerValue - bits0;
		final long val0 = bits[arrPos];
		long result = (val0 >>> arrMod);
		if (bits1 > 0) {
			final long val1 = bits[arrPos + 1];
			result |= (val1 << bits0);
		}
		return result & getMask();
	}

	@Override
	protected void setUnchecked(int index, long value) {
		final long[] bits = this.bits;
		final long mask = getMask();
		final int bitsPerValue = getBitsPerValue();
		final long bitPos = index * bitsPerValue;
		final int arrPos = (int) (bitPos >>> 6);//bitPos / 64
		final int arrMod = (int) (bitPos & 63);//bitPos % 64
		final int bits0 = (int) (64 - arrMod);
		final int bits1 = bitsPerValue - bits0;
		long val0 = bits[arrPos];
		val0 |= (mask << arrMod);
		val0 ^= ((value ^ mask) << arrMod);
		bits[arrPos] = val0;
		if (bits1 > 0) {
			long val1 = bits[arrPos + 1];
			val1 = ((val1 >>> bits1) << bits1);//clear our bits
			val1 |= (value >>> bits0);
			bits[(int) (arrPos + 1)] = val1;
		}
	}

	@Override
	public long byteSize() {
		return 8L * bits.length;
	}
}
