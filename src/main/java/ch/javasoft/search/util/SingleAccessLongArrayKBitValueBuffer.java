package ch.javasoft.search.util;

/**
 * Implementation of {@link KBitValueBuffer} storing the k-bit values in a long
 * array possibly wasting some bits for the padding at the end of the every long
 * value.
 */
public class SingleAccessLongArrayKBitValueBuffer extends
		AbstractKBitValueBuffer {

	public static final Factory FACTORY = new Factory() {
		@Override
		public KBitValueBuffer create(int count, int k) {
			return k == 0 ? ZeroBitValueBuffer.FACTORY.create(count, k) : new SingleAccessLongArrayKBitValueBuffer(count, k);
		}
	};

	private final int valuesPerLong;
	private final long[] bits;

	public SingleAccessLongArrayKBitValueBuffer(int count, int bitsPerValue) {
		super(count, bitsPerValue);
		if (bitsPerValue <= 0 || bitsPerValue > 64) {
			throw new IllegalArgumentException("bitsPerValue must be in [1,64] but was: " + bitsPerValue);
		}
		this.valuesPerLong = 64 / bitsPerValue;
		this.bits = new long[1 + ((count - 1) / valuesPerLong)];
	}

	@Override
	protected long getUnchecked(int index) {
		final int valuesPerLong = this.valuesPerLong;
		final int arrPos = index / valuesPerLong;
		final int valPos = index % valuesPerLong;
		final int shift = valPos * getBitsPerValue();
		return (bits[arrPos] >>> shift) & getMask();
	}

	@Override
	protected void setUnchecked(int index, long value) {
		final long[] bits = this.bits;
		final int valuesPerLong = this.valuesPerLong;
		final int arrPos = index / valuesPerLong;
		final int valPos = index % valuesPerLong;
		final int shift = valPos * getBitsPerValue();
		final long mask = getMask();
		long val = bits[arrPos];
		val |= (mask << shift);
		val ^= ((value ^ mask) << shift);
		bits[arrPos] = val;
	}

	@Override
	public long byteSize() {
		return 8L * bits.length;
	}
}
