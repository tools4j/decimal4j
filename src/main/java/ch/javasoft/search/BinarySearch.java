package ch.javasoft.search;

import java.util.Arrays;

public class BinarySearch implements Search {
	
	private final int[] sorted;
	
	public BinarySearch(int... values) {
		this.sorted = Arrays.copyOf(values, values.length);
		Arrays.sort(sorted);
	}
	
	@Override
	public int get(int index) {
		return sorted[index];
	}

	@Override
	public int find(int value) {
		return Arrays.binarySearch(sorted, value);
	}
	
	@Override
	public long byteSize() {
		return sorted.length * 4L;
	}

}
