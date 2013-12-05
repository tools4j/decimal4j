package ch.javasoft.search;

import java.util.Arrays;

import ch.javasoft.search.util.ExactMembership;
import ch.javasoft.search.util.KBitValueBuffer;
import ch.javasoft.search.util.RelevantIntBits;
import ch.javasoft.search.util.Transform;

public class CountSlotsRelevantBitsMembershipSearch implements Search {

	private final int alwaysOne;
	private final int[] relevantBits;
	private final int[] countBits;
	private final int[] valueBits;
	private final int[] counts;
	private final KBitValueBuffer values;

	public CountSlotsRelevantBitsMembershipSearch(KBitValueBuffer.Factory bufferFactory, int... values) {
		final RelevantIntBits relevantBits = RelevantIntBits.findFor(values);
		this.relevantBits = relevantBits.getRelevantBits();
		this.alwaysOne = relevantBits.getAlwaysOneBitMask();
		final int b = this.relevantBits.length;
		if (b == 32) {
			throw new IllegalArgumentException("all bits are relevant, use " + CountSlotsMembershipSearch.class.getSimpleName() + " instead");
		}
		final int k = Math.max(1, ExactMembership.findBestK(values.length, b));
		this.valueBits = Arrays.copyOf(this.relevantBits, b - k);
		this.countBits = Arrays.copyOfRange(this.relevantBits, b - k, b);
		final int[] sorted = new int[values.length];
		for (int i = 0; i < sorted.length; i++) {
			sorted[i] = Transform.transform(values[i], this.relevantBits);
		}
		Arrays.sort(sorted);
		this.counts = initCounts(sorted, b, k);
		this.values = bufferFactory.create(values.length, b - k);
		if (b > k) {
			for (int i = 0; i < sorted.length; i++) {
				this.values.set(i, sorted[i]);
			}
		}
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
			throw new RuntimeException("internal error, counts[end] = " + counts[counts.length - 1] + " should be " + sorted.length);
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
		final KBitValueBuffer values = this.values;
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
		final int kbits = Transform.backTransform(countIndex, countBits);
		final int vbits = Transform.backTransform((int)values.get(index), valueBits); 
		return kbits | vbits | alwaysOne;
	}

	@Override
	public int find(int value) {
		final int[] counts = this.counts;
		final int countIndex = Transform.transform(value, countBits);
		final int start = countIndex == 0 ? 0 : counts[countIndex - 1];
		final int end = counts[countIndex];
		final int key = Transform.transform(value, valueBits);
		return values.binarySearch(key, start, end);
	}

	@Override
	public long byteSize() {
		return 4L * counts.length + values.byteSize();
	}

}
