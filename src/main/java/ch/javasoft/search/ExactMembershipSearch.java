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
		this.k = findBestK(sorted.length);
		this.counts = initCounts(sorted, k);
		this.values = new NBitValueArray(values.length, 32 - k);
		this.kmask = 0xffffffff ^ (int) this.values.getMask();
		for (int i = 0; i < sorted.length; i++) {
			this.values.set(i, sorted[i]);
		}
	}

	private static int findBestK(int n) {
		long bestSize = sizeOf(n, 0);
		int bestK = 0;
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
		return (32L - k) * n + (slots - 1) * 32L;
	}

	private int[] initCounts(int[] sorted, int k) {
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
				index = kmask % counts.length;
				count = 1;
			}
		}
		counts[index] = count;
		//we want cumulative sums
		for (int i = 1; i < counts.length; i++) {
			counts[i] += counts[i - 1];
		}
		return counts;
	}
	
	private static int icount(int value, int k) {
		final int kpow2 = 1 << k;
		return k == 0 ? 0 : ((value >>> (32 - k)) + (kpow2 >> 1)) % kpow2;
	}
	
	private static int kbits(int icount, int k) {
		final int kpow2 = 1 << k;
		return k == 0 ? 0 : ((kpow2 + icount - (kpow2 >> 1)) % kpow2) << (32 - k);
	}

	@Override
	public int get(int index) {
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
		final int countIndex = icount(value, k);
		final int start = countIndex == 0 ? 0 : counts[countIndex - 1];
		final int end = counts[countIndex];
		return binarySearch(start, end, value);
	}

	private int binarySearch(int fromIndex, int toIndex, int value) {
		final int key = value & ~kmask;
		final NBitValueArray arr = this.values;
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			final int mid = (low + high) >>> 1;
			final int midVal = (int) arr.getUnchecked(mid);

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
