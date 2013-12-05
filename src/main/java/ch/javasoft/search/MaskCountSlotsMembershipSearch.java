package ch.javasoft.search;

import java.util.Arrays;

import ch.javasoft.search.util.KBitValueBuffer;
import ch.javasoft.search.util.RelevantIntBits;
import ch.javasoft.search.util.Transform;

public class MaskCountSlotsMembershipSearch implements Search {

	private final int alwaysOne;
	private final int[] relevantBits;
	private final int[] valueBits;
	private final int kmask;
	private final int[] kbits;
	private final int[] counts;
	private final KBitValueBuffer values;

	public MaskCountSlotsMembershipSearch(KBitValueBuffer.Factory bufferFactory, int... values) {
		final RelevantIntBits relevantBits = RelevantIntBits.findFor(values);
		this.relevantBits = relevantBits.getRelevantBits();
		this.alwaysOne = relevantBits.getAlwaysOneBitMask();
		final int b = this.relevantBits.length;
		final int[] sorted = new int[values.length];
		for (int i = 0; i < sorted.length; i++) {
			sorted[i] = Transform.transform(values[i], this.relevantBits);
		}
		Arrays.sort(sorted);
		final int k = findK(this.relevantBits, sorted);
		this.valueBits = Arrays.copyOf(this.relevantBits, b - k);
		this.kmask = getKMask(this.relevantBits, k);
		final int[][] kbitsAndCounts = initKBitsAndCounts(sorted, k, kmask);
		this.kbits = kbitsAndCounts[0];
		this.counts = kbitsAndCounts[1];
		if (kbits[kbits.length - 1] < 0) {
			throw new IllegalArgumentException("all bits are relevant, use " + CountSlotsMembershipSearch.class.getSimpleName() + " instead");
		}
		this.values = bufferFactory.create(values.length, b - k);
		if (b > k) {
			for (int i = 0; i < sorted.length; i++) {
				this.values.set(i, sorted[i]);
			}
		}
	}
	
	private static int findK(int[] relevantBits, int[] sorted) {
		int bestK = -1;
		long bestSize = Long.MAX_VALUE;
		int k = 0;
		long size = sizeOf(sorted.length, relevantBits.length, 0, 1);
		while (k < relevantBits.length && k < 31) {
			if (size < bestSize) {
				bestK = k;
				bestSize = size;
			}
			k++;
			final int kmask = getKMask(relevantBits, k);
			final int count = countMasks(sorted, kmask);
			size = sizeOf(sorted.length, relevantBits.length, k, count);
		}
		if (size < bestSize) {
			bestK = k;
			bestSize = size;
		}
		return bestK;
	}
	
	private static int countMasks(int[] sorted, int kmask) {
		int count = 1;
		int curmask = sorted.length == 0 ? 0 : toKMask(sorted[0], kmask);
		for (final int val : sorted) {
			final int valmask = toKMask(val, kmask);
			if (valmask != curmask) {
				count++;
				curmask = valmask;
			}
		}
		return count;
	}

	private static int getKMask(int[] relevantBits, int k) {
		int mask = 0;
		for (int i = relevantBits.length - k; i < relevantBits.length; i++) {
			mask |= 1 << relevantBits[i];
		}
		return mask;
	}

	private static int[][] initKBitsAndCounts(int[] sorted, int k, int kmask) {
		final int len = countMasks(sorted, kmask);
		final int[] kbits = new int[len];
		final int[] counts = new int[len];
		int curmask = sorted.length == 0 ? 0 : toKMask(sorted[0], kmask);
		int count = 0;
		int index = 0;
		for (final int val : sorted) {
			final int valmask = toKMask(val, kmask);
			if (valmask == curmask) {
				count++;
			} else {
				kbits[index] = curmask;
				counts[index] = count;
				curmask = valmask;
				count = 1;
				index++;
			}
		}
		kbits[index] = curmask;
		counts[index] = count;
		//we want cumulative sums
		for (int i = 1; i <= index; i++) {
			counts[i] += counts[i - 1];
		}
		//assert
		if (counts[index] != sorted.length) {
			throw new RuntimeException("internal error, counts[end] = " + counts[index] + " should be " + sorted.length);
		}
		if (index + 1 != len) {
			throw new RuntimeException("internal error, index=" + index + " should be one less than len=" + len);
		}
		//return result
		final int[][] result = new int[2][];
		result[0] = kbits;
		result[1] = counts;
		return result;
	}
	
	@Override
	public int get(int index) {
		final KBitValueBuffer values = this.values;
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
		final int kbits = this.kbits[countIndex];
		final int vbits = Transform.backTransform((int)values.get(index), valueBits); 
		return kbits | vbits | alwaysOne;
	}

	@Override
	public int find(int value) {
		final int[] counts = this.counts;
		final int countIndex = Arrays.binarySearch(kbits, toKMask(value, kmask));
		if (countIndex < 0) {
			return -1;
		}
		final int start = countIndex == 0 ? 0 : counts[countIndex - 1];
		final int end = counts[countIndex];
		final int key = Transform.transform(value, valueBits);
		return values.binarySearch(key, start, end);
	}

	private static int toKMask(int value, int kmask) {
		return value & kmask;
	}

	@Override
	public long byteSize() {
		return 4L * counts.length + 4L * kbits.length + values.byteSize();
	}
	private static long sizeOf(long n, int b, int k, long slots) {
		return (b - k) * n + slots * (32 + 32);
		//value.bytesize + counts + kbits
	}

}
