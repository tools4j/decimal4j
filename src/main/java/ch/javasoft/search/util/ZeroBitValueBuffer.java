package ch.javasoft.search.util;

/**
 * Implementation for the special case to "store" zero bit values.
 */
public class ZeroBitValueBuffer implements KBitValueBuffer {
	
	/**
	 * Factory, throws an exception if {@code k != 0}.
	 */
	public static final Factory FACTORY = new Factory() {
		@Override
		public KBitValueBuffer create(int count, int k) {
			if (k != 0) {
				throw new IllegalArgumentException("k must be 0 but was " + k);
			}
			return new ZeroBitValueBuffer(count);
		}
	};
	
	private final int count;
	
	public ZeroBitValueBuffer(int count) {
		this.count = count;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public int getBitsPerValue() {
		return 0;
	}

	@Override
	public long getMask() {
		return 0;
	}

	@Override
	public long get(int index) {
		return 0;
	}

	@Override
	public void set(int index, long value) {
		//nothing to do
	}

	@Override
	public int binarySearch(long value, int fromIndex, int toIndex) {
		return 0;
	}

	@Override
	public long byteSize() {
		return 0;
	}

}
