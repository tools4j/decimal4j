package ch.javasoft.search.util;

/**
 * Implementation of {@link KBitValueBuffer} storing the k-bit values in a long array.
 */
abstract public class AbstractKBitValueBuffer implements KBitValueBuffer {
	
	private final int count;
	private final int bitsPerValue;
	private final long mask;
	public AbstractKBitValueBuffer(int count, int bitsPerValue) {
		if (count < 0) {
			throw new IllegalArgumentException("count cannot be negative: " + count);
		}
		if (bitsPerValue < 0 || bitsPerValue > 64) {
			throw new IllegalArgumentException("bitsPerValue must be in [0, 64] but was " + bitsPerValue);
		}
		this.count = count;
		this.bitsPerValue = bitsPerValue;
		this.mask = bitsPerValue == 0 ? 0 : 0xffffffffffffffffL >>> (64 - (bitsPerValue % 64));
	}
	
	@Override
	public final int getCount() {
		return count;
	}
	
	@Override
	public final int getBitsPerValue() {
		return bitsPerValue;
	}
	
	@Override
	public long getMask() {
		return mask;
	}
	
	public void checkIndex(int index) {
		if (index < 0 || index > count) {
			throw new IndexOutOfBoundsException("index expected in [0, " + (count-1) + "] but was " + index);
		}
	}
	@Override
	public long get(int index) {
		checkIndex(index);
		return getUnchecked(index);
	}
	@Override
	public void set(int index, long value) {
		checkIndex(index);
		setUnchecked(index, value & mask);
	}
	
	@Override
	public int binarySearch(long value, int fromIndex, int toIndex) {
		final long key = value & mask;
		if (bitsPerValue == 0) {
			return key == 0 ? fromIndex : - (fromIndex + 1); 
		}
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			final int mid = (low + high) >>> 1;
			final long midVal = getUnchecked(mid);

			if (midVal < key) low = mid + 1;
			else if (midVal > key) high = mid - 1;
			else return mid; // key found
		}
		return -(low + 1); // key not found.
	}

	abstract protected long getUnchecked(int index);
	abstract protected void setUnchecked(int index, long value);
	
}
