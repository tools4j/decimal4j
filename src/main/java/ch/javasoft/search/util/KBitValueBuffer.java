package ch.javasoft.search.util;

/**
 * Buffer to store k-bit values, where {@code k in [0, 64]}.
 */
public interface KBitValueBuffer {

	/**
	 * Factory interface for different buffer implementations
	 */
	static interface Factory {
		/**
		 * Creates and returns an array to store {@code count} k-bit values.
		 * 
		 * @param count
		 *            the number of k-bit values to be stored in the returned
		 *            buffer
		 * @param k
		 *            the number of bits per value
		 * @return a new buffer
		 */
		KBitValueBuffer create(int count, int k);
	}
	
	/**
	 * Returns the number of k-bit values currently stored in this buffer.
	 * 
	 * @return the number of k-bit values in this buffer
	 */
	int getCount();

	/**
	 * Returns {@code k}, the number of bits per value, where
	 * {@code k in [0, 64]}.
	 * 
	 * @return the number of bits per value in
	 */
	int getBitsPerValue();

	/**
	 * Returns the bit mask, that is, a value with the {@code k} low order bits
	 * set to one with, where {@code k} is the number of bits per value as
	 * returned by {@link #getBitsPerValue()}.
	 * <p>
	 * For instance, if {@code k=3}, this method returns {@code 7 == 0x111}.
	 * 
	 * @return the bit mask with the {@code k} low order bits set to one
	 */
	long getMask();

	/**
	 * Returns the value stored at the position {@code index} in this buffer.
	 * The index is is a value in {@code [0,(n-1)]} where n denotes the value
	 * {@link #getCount() count}. The result is returned in the k low order bits
	 * of the long value, all other bits are to be zero.
	 * 
	 * @param index
	 *            the zero based value index
	 * @return the k-bit value stored in the k low order bits of the returned
	 *         long value
	 * @throws IndexOutOfBoundsException
	 *             if index is not in {@code [0,(n-1)]} where {@code n=}
	 *             {@link #getCount()}
	 */
	long get(int index);

	/**
	 * Reads the k low-order bits from the given long value and stores them in
	 * this buffer at the position specified by {@code index}. It depends on the
	 * implementation whether the buffer must be pre-allocated or can grow
	 * dynamically. In either case, if the index is out of the valid bounds, an
	 * exception is thrown.
	 * 
	 * @param index
	 *            the zero based value index
	 * @param value
	 *            the k-bit value stored in the low order k bits of the given
	 *            long value
	 * @throws IndexOutOfBoundsException
	 *             if index is negative or out of bounds
	 */
	void set(int index, long value);

	/**
	 * Searches a range of this value buffer for the specified value using the
	 * binary search algorithm. The range must be sorted prior to making this
	 * call. If it is not sorted, the results are undefined. If the range
	 * contains multiple elements with the specified value, there is no
	 * guarantee which one will be found.
	 * 
	 * @param a
	 *            the array to be searched
	 * @param fromIndex
	 *            the index of the first element (inclusive) to be searched
	 * @param toIndex
	 *            the index of the last element (exclusive) to be searched
	 * @param value
	 *            the k-bit value to be searched for stored in the low order k
	 *            bits of the given long value
	 * 
	 * @return index of the search key, if it is contained in the array within
	 *         the specified range; otherwise,
	 *         <tt>(-(<i>insertion point</i>) - 1)</tt>. The <i>insertion
	 *         point</i> is defined as the point at which the key would be
	 *         inserted into the array: the index of the first element in the
	 *         range greater than the key, or <tt>toIndex</tt> if all elements
	 *         in the range are less than the specified key. Note that this
	 *         guarantees that the return value will be &gt;= 0 if and only if
	 *         the key is found.
	 * @throws IllegalArgumentException
	 *             if {@code fromIndex > toIndex}
	 * @throws ArrayIndexOutOfBoundsException
	 *             if {@code fromIndex < 0 or toIndex > a.length}
	 */
	int binarySearch(long value, int fromIndex, int toIndex);

	/**
	 * Returns the number of bytes currently used to store the values in this
	 * buffer
	 * 
	 * @return the number of bytes approximately used in memory for this buffer
	 */
	long byteSize();
}
