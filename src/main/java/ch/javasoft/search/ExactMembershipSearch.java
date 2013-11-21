package ch.javasoft.search;

import java.util.Arrays;

public class ExactMembershipSearch implements Search {

	private final int k;
	private final int kmask;
	private final int[] counts;
	private final NBitValueArray values;

	public ExactMembershipSearch(int... values) {
		final int[] sorted = Arrays.copyOf(values, values.length);
		Arrays.sort(sorted);
		this.k = Math.max(1, findBestK(sorted.length));
		this.counts = initCounts(sorted, k);
		this.values = new NBitValueArray(values.length, 32 - k);
		this.kmask = 0xffffffff ^ (int) this.values.getMask();
		for (int i = 0; i < sorted.length; i++) {
			this.values.set(i, sorted[i]);
		}
	}

	private static int findBestK(int n) {
		int bestK = 0;
		long bestSize = sizeOf(n, bestK);
		for (int k = 1; k <= 32; k++) {
			final long size = sizeOf(n, k);
			if (size < bestSize) {
				bestSize = size;
				bestK = k;
			}
		}
		return bestK;
	}

	private static long sizeOf(int n, int k) {
		final long slots = 1L << k;
//		return (32L - k) * n + (slots - 1) * 32L;
		return (32L - k) * n + slots * 32L;
	}

	private static int[] initCounts(int[] sorted, int k) {
		final int[] counts = new int[1 << k];
		int index = sorted.length == 0 ? 0 : icount(sorted[0], k);
		int kmask = index;
		int count = 0;
		for (final int val : sorted) {
			final int valmask = icount(val, k);
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
	 * Returns the count array index for a given value.
	 * <p>
	 * If all positive values, formula would be simple:
	 * result = value >>> (32 - k)
	 * 
	 * With negative values, we shift the whole index by half the count array 
	 * size, i.e. by (1<<k)/2 = 1<<(k-1)
	 * but than we must do modulo array size, i.e. (% kpow2) == (& (kpow2-1)) 
	 * all together, we get the shown formula
	 */
	private static int icount(int value, int k) {
		final int kpow2 = 1 << k;
		return ((value >>> (32 - k)) + (kpow2 >> 1)) & (kpow2-1);
	}
	
	/**
	 * Returns the high-order k-bits given the count array index. This is the
	 * reverse function of {@link #icount(int, int)}.
	 * <p>
	 * If all positive values, formula would be simple:
	 * result = icount << (32 - k)
	 * 
	 * With negative values, we must undo the shift operation applied in icout, 
	 * i.e. we must first subtract half the size of the array, and than do modulo
	 * the array size. Because this could be negative, we add the array size before
	 * doing the modulo, and adding the array size plus subtracting half the array
	 * size is the same as adding half the array size. 
	 * 
	 * Given modulo array size is (% kpow2) == (& (kpow2-1)) we get the formula 
	 * below.
	 */
	private static int kbits(int icount, int k) {
		final int kpow2 = 1 << k;
		return ((icount + (kpow2 >> 1)) & (kpow2-1)) << (32 - k);
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
		final int kbits = kbits(countIndex, k);
		return kbits | (int) values.get(index);
	}

	@Override
	public int find(int value) {
		final int[] counts = this.counts;
		final int countIndex = icount(value, k);
		final int start = countIndex == 0 ? 0 : counts[countIndex - 1];
		final int end = counts[countIndex];
		return binarySearch(start, end, value);
	}

	private int binarySearch(int fromIndex, int toIndex, int value) {
		final int key = value & ~kmask;
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
