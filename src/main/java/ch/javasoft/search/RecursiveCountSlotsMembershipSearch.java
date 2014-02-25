package ch.javasoft.search;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecursiveCountSlotsMembershipSearch implements Search {
	
	private final Node root;

	public RecursiveCountSlotsMembershipSearch(int... values) {
		final int[] sorted = Arrays.copyOf(values, values.length);
		Arrays.sort(sorted);
		this.root = createNode(0, sorted, 0, sorted.length);
	}
	
	private static Node createNode(int bitOffset, int[] values, int start, int end) {
		final int minLen = 1 << 8;
		final int len = end - start;
		if (len == 0) {
			return Node.DEAD_END;
		} else if (len == 1) {
			return new ValueLeaf(values[start]);
		} else {
			final int log2 = 32 - Integer.numberOfLeadingZeros(len);
			if (bitOffset + log2 >= 32) {
				final int bits = Math.min(log2, 32 - bitOffset);
				return new CountLeaf(bits, values, start, end);
			} else {
				if (len <= minLen) {
					return new ArrayLeaf(values, start, end);
				}
				return new InterNode(bitOffset, log2, values, start, end);
			}
		}
	}

	@Override
	public int get(int index) {
		return root.get(index);
	}

	@Override
	public int find(int value) {
		return root.find(value);
	}

	@Override
	public long byteSize() {
		return root.byteSize(new AtomicBoolean(false));
	}

	private static interface Node {
		int find(int value);
		int get(int index);
		long byteSize(AtomicBoolean arrayCounted);
		Node DEAD_END = new Node() {
			@Override
			public int get(int index) {throw new IndexOutOfBoundsException("no value with index=" + index);}
			@Override
			public int find(int value) {return -1;}
			@Override
			public long byteSize(AtomicBoolean arrayCounted) {return 0;}
		};
	}
	
	private static class InterNode implements Node {
		private final int shift;
		private final int[] counts;
		private final Node[] children;
		
		public InterNode(int bitOffset, int bits, int[] values, int start, int end) {
			final int len = 1 << bits;
			final int mask = len - 1;
			final int shift = 32 - (bitOffset + bits);
			int index = 0;
			int istart = start;
			int count = 0;
			this.shift = shift;
			this.counts = new int[len];
			this.children = new Node[len];
			for (int i = start; i < end; i++) {
				final int value = (values[i] >>> shift) & mask;
				while (value != index) {
					counts[index] = count;
					children[index] = createNode(bitOffset + bits, values, istart, i);
					istart = i;
					index++;
				}
				count++;
			}
			do {
				counts[index] = count;
				children[index] = createNode(bitOffset + bits, values, istart, end);
				istart = end;
				index++;
			} while (index < len);
		}
		
		@Override
		public int get(int index) {
			int cindex = Arrays.binarySearch(counts, index);
			if (cindex < 0) {
				cindex = -(cindex + 1);
			}
			final int offset = cindex == 0 ? 0 : counts[cindex - 1];
			return children[cindex].get(index - offset) | (cindex << (32 - shift));
		}
		@Override
		public int find(int value) {
			final int len = children.length;
			final int cindex = (value >>> shift) & (len - 1);
			final int index = children[cindex].find(value);
			return cindex == 0 || index < 0 ? index : index + counts[cindex - 1];
		}
		@Override
		public long byteSize(AtomicBoolean arrayCounted) {
			long size = 0;
			for (final Node child : children) {
				size += child.byteSize(arrayCounted);
			}
			return size + 4L + 4L * counts.length;
		}
	}
	private static class CountLeaf implements Node {
		private final int[] counts;//must have pow2 length
		
		public CountLeaf(int bits, int[] values, int start, int end) {
			final int len = 1 << bits;
			final int mask = len - 1;
			int index = 0;
			int count = 0;
			this.counts = new int[len];
			for (int i = start; i < end; i++) {
				final int value = values[i] & mask;
				while (value != index) {
					counts[index] = count;
					index++;
				}
				count++;
			}
			do {
				counts[index] = count;
				index++;
			} while (index < len);
		}
		@Override
		public int get(int index) {
			final int len = counts.length;
			final int cindex = Arrays.binarySearch(counts, index & (len - 1));
			if (cindex >= 0) {
				return cindex;
			}
			return -(cindex + 1);
		}
		@Override
		public int find(int value) {
			final int len = counts.length;
			final int cindex = value & (len - 1);
			final int first = (cindex == 0 ? 0 : counts[cindex - 1]);
			if (first < counts[cindex]) {
				return first;
			}
			return -1;
		}
		@Override
		public long byteSize(AtomicBoolean arrayCounted) {
			return 4L * counts.length;
		}
	}
	
	private static class ValueLeaf implements Node {
		private final int value;
		public ValueLeaf(int value) {
			this.value = value;
		}
		@Override
		public int get(int index) {
			if (index == 0) {
				return value;
			}
			throw new IndexOutOfBoundsException();
		}
		@Override
		public int find(int value) {
			return this.value == value ? 0 : -1;
		}
		@Override
		public long byteSize(AtomicBoolean arrayCounted) {
			return 4L;
		}
	}
	private static class ArrayLeaf implements Node {
		private final int[] sorted;
		private final int start;
		private final int end;
		public ArrayLeaf(int[] sorted, int start, int end) {
			this.sorted = sorted;
			this.start = start;
			this.end = end;
		}
		@Override
		public int get(int index) {
			if (index >= start && index < end) {
				return sorted[index];
			}
			throw new IndexOutOfBoundsException("index " + index + " is not in [" + start + ".." + (end-1) + "]");
		}
		@Override
		public int find(int value) {
			return Arrays.binarySearch(sorted, start, end, value);
		}
		@Override
		public long byteSize(AtomicBoolean arrayCounted) {
			if (arrayCounted.compareAndSet(false, true)) {
				return 4L * sorted.length + 4L + 4L; 
			}
			return 12L;//the array is just a pointer, not a copy, and it has already been counted
		}
	}

}
