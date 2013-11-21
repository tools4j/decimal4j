package ch.javasoft.search;

import java.util.Arrays;

public class BitPatternExactMembershipSearch implements Search {

	private final int[] relevantBits;
	private final int[] countBits;
	private final int[] valueBits;
	private final int[] counts;
	private final NBitValueArray values;

	public BitPatternExactMembershipSearch(int... values) {
		this.relevantBits = findRelevantBits(values);
		final int b = relevantBits.length;
		if (b == 32) {
			throw new IllegalArgumentException("all bits are relevant, use " + ExactMembershipSearch.class.getSimpleName() + " instead");
		}
		final int k = Math.max(1, findBestK(values.length, b));
		this.valueBits = Arrays.copyOf(relevantBits, b - k);
		this.countBits = Arrays.copyOfRange(relevantBits, b - k, b);
		final int[] sorted = new int[values.length];
		for (int i = 0; i < sorted.length; i++) {
			sorted[i] = transform(values[i], relevantBits);
		}
		Arrays.sort(sorted);
		this.counts = initCounts(sorted, b, k);
		this.values = new NBitValueArray(values.length, b - k);
		if (b > k) {
			for (int i = 0; i < sorted.length; i++) {
				this.values.set(i, sorted[i]);
			}
		}
	}

	private static int transform(int value, int[] bits) {
		final int len = bits.length;
		int result = 0;
		for (int i = 0; i < len; i++) {
			if (0 != (value & (1 << bits[i]))) {
				result |= (1 << i);
			}
		}
		return result;
	}
	private static int backTransform(int transformed, int[] bits) {
		final int len = bits.length;
		int result = 0;
		for (int i = 0; i < len; i++) {
			if (0 != (transformed & (1 << i))) {
				result |= (1 << bits[i]);
			}
		}
		return result;
	}

	private int[] findRelevantBits(int[] values) {
		int relevantBitMask = 0;
		for (final int val : values) {
			relevantBitMask |= val;
			if (relevantBitMask == -1) break;
		}
		final int[] relevantBits = new int[Integer.bitCount(relevantBitMask)];
		int index = 0;
		for (int i = 0; i < 32; i++) {
			if (0 != (relevantBitMask & (1 << i))) {
				relevantBits[index] = i;
				index++;
			}
		}
		return relevantBits;
	}

	private static int findBestK(int n, int b) {
		int bestK = 0;
		long bestSize = sizeOf(n, b, bestK);
		for (int k = 1; k <= b; k++) {
			final long size = sizeOf(n, b, k);
			if (size < bestSize) {
				bestSize = size;
				bestK = k;
			}
		}
		return bestK;
	}

	private static long sizeOf(int n, int b, int k) {
		final long slots = 1L << k;
		return (b - k) * (long)n + slots * b;
	}

	private static int[] initCounts(int[] sorted, int b, int k) {
		final int[] counts = new int[1 << k];
		int index = sorted.length == 0 ? 0 : icount(sorted[0], b, k);
		int kmask = index;
		int count = 0;
		for (final int val : sorted) {
			final int valmask = icount(val, b, k);
			if (valmask == kmask) {
				count++;
			} else {
				counts[index] = count;
				kmask = valmask;
				index = kmask;
				count = 1;
			}
		}
		counts[index] = count;
		//we want cumulative sums
		for (int i = 1; i < counts.length; i++) {
			counts[i] += counts[i - 1];
		}
		//assert
		if (counts[counts.length - 1] != sorted.length) {
			throw new RuntimeException("internal error, counts[end] = " + counts[counts.length] + " should be " + sorted.length);
		}
		return counts;
	}
	
	/**
	 * Returns the count array index for a given sorted (i.e. already re-arranged) value.
	 */
	private static int icount(int value, int b, int k) {
		return value >>> (b - k);
	}

	@Override
	public int get(int index) {
		final NBitValueArray values = this.values;
		final int[] counts = this.counts;
		if (index < 0 || index >= values.getCount()) {
			throw new IndexOutOfBoundsException("index expected in [0, " + (values.getCount() - 1) + "] but was found to be " + index);
		}
		int countIndex = Arrays.binarySearch(counts, index);
		if (countIndex < 0) {
			countIndex = -(countIndex + 1);
		} else {
			do {
				countIndex++;
			} while (counts[countIndex] == index);
		}
		final int kbits = backTransform(countIndex, countBits);
		final int vbits = backTransform((int)values.get(index), valueBits); 
		return kbits | vbits;
	}

	@Override
	public int find(int value) {
		final int[] counts = this.counts;
		final int countIndex = transform(value, countBits);
		final int start = countIndex == 0 ? 0 : counts[countIndex - 1];
		final int end = counts[countIndex];
		return binarySearch(start, end, value);
	}

	private int binarySearch(int fromIndex, int toIndex, int value) {
		final int key = transform(value, valueBits);
		if (valueBits.length == 0) {
			return key == 0 ? fromIndex : - (fromIndex + 1); 
		}
		final NBitValueArray values = this.values;
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			final int mid = (low + high) >>> 1;
			final int midVal = (int) values.getUnchecked(mid);

			if (midVal < key) low = mid + 1;
			else if (midVal > key) high = mid - 1;
			else return mid; // key found
		}
		return -(low + 1); // key not found.
	}

	@Override
	public long byteSize() {
		return 4L * counts.length + values.byteSize();
	}

}
