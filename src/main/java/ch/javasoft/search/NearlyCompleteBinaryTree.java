package ch.javasoft.search;

import java.util.Arrays;

public class NearlyCompleteBinaryTree implements Search {

	private final int[] values;

	public NearlyCompleteBinaryTree(int... values) {
		final int[] sorted = Arrays.copyOf(values, values.length);
		Arrays.sort(sorted);
		final int ceilLog2len = 32 - Integer.numberOfLeadingZeros(values.length - 1);
		this.values = new int[1 << ceilLog2len];
		insert(sorted, 0, 0, sorted.length);
	}

	@Override
	public int get(int index) {
		return values[index];
	}

	private void insert(int[] sorted, int index, int low, int high) {
		if (high - low > 1) {
			final int mid = (low + high) >>> 1;
			values[index] = sorted[mid];
			insert(sorted, 1 + (index << 1), low, mid);
			insert(sorted, 2 + (index << 1), mid, high);
		} else {
			if (index < values.length) {
				values[index] = sorted[low];
			}
		}
	}

	@Override
	public int find(int value) {
		return find(value, 0);
	}

	private int find(int value, int index) {
		final int[] vals = this.values;
		final int len = vals.length;
		while (index < len) {
			final int cur = vals[index];
			if (cur < value) {
				// go right: index = 2*index + 2
				index = 2 + (index << 1);
			} else if (cur > value) {
				// go left: index = 2*index + 1
				index = 1 + (index << 1);
			} else {
				return index;
			}
		}
		return -1;
	}
	
	@Override
	public long byteSize() {
		return values.length * 4L;
	}

}
