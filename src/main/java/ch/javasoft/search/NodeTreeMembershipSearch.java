package ch.javasoft.search;

import java.util.Arrays;

public class NodeTreeMembershipSearch implements Search {
	
	private final int indexOfFirstNonNeg;
	private final Node11 pos;
	private final Node11 neg;

	public NodeTreeMembershipSearch(int... values) {
		final int[] sorted = Arrays.copyOf(values, values.length);
		Arrays.sort(sorted);
		final int insertionPoint = Arrays.binarySearch(sorted, 0);
		this.indexOfFirstNonNeg = insertionPoint >= 0 ? insertionPoint : -insertionPoint; //if not found, then -(insertionPoint+1)+1 == -insertionPoint
		this.pos = new Node11(false, sorted, indexOfFirstNonNeg, values.length);
		this.neg= new Node11(true, sorted, 0, indexOfFirstNonNeg);
	}
	
	@Override
	public int get(int index) {
		return index >= indexOfFirstNonNeg ? pos.get(index - indexOfFirstNonNeg) : neg.get(index);
	}

	@Override
	public int find(int value) {
		return value >= 0 ? pos.find(value) : neg.find(value);
	}

	@Override
	public long byteSize() {
		return pos.byteSize() + neg.byteSize();
	}
	
	private static interface Node extends Search {
		//nothing to add
	}
	private abstract static class AbstractNode implements Node {
		protected final int[] counts;
		protected final Node[] children;
		@Override
		public int get(int index) {
			int cindex = Arrays.binarySearch(counts, index);
			if (cindex < 0) {
				cindex = -(cindex + 1);
			}
			final Node child = children[cindex];
			if (child != null) {
				final int offset = cindex == 0 ? 0 : counts[cindex - 1];
				return child.get(index - offset);
			}
			throw new IndexOutOfBoundsException("no such index: " + index);
		}
		public AbstractNode(int nodeCount, int[] sorted, int start, int end) {
			this.counts = new int[1 << 11];
			this.children = new Node[1 << 11];
			int index = 0;
			int childStart = start;
			for (int i = start; i < end; i++) {
				final int value = maskValue(sorted[i]);
				if (value != index) {
					if (childStart < i) {
						counts[index] = i - start;//cumsum
						children[index] = newChildNode(sorted, childStart, i);
					}
					childStart = i;
					do {
						index++;
					} while (value != index);
				}
			}
			if (childStart < end) {
				counts[index] = end - start;//cumsum
				children[index] = newChildNode(sorted, childStart, end);
			}
			while (index < counts.length) {
				counts[index] = end - start;
				index++;
			}
		}
		@Override
		public long byteSize() {
			long size = 0;
			for (final Node node : children) {
				if (node != null) {
					size += node.byteSize();
				}
			}
			return size + 4L * (counts.length + children.length);
		}
		@Override
		public int find(int value) {
			final int childIndex = maskValue(value);
			final Node child = children[childIndex];
			if (child != null) {
				final int offset = childIndex == 0 ? 0 : counts[childIndex-1];
				return offset + child.find(value);
			}
			return -1;
		}
		abstract protected int maskValue(int value);
		abstract protected Node newChildNode(int[] sorted, int start, int end);
	}
	private static class Node11 extends AbstractNode {
		public Node11(boolean negative, int[] sorted, int start, int end) {
			super(1 << 11, sorted, start, end);
		}
		@Override
		protected int maskValue(int value) {
			 return (value >>> 20) & 0x7ff;
		}
		@Override
		protected Node newChildNode(int[] sorted, int start, int end) {
			return new Node8(sorted, start, end);
		}
	}
	private static class Node8 extends AbstractNode {
		public Node8(int[] sorted, int start, int end) {
			super(1 << 8, sorted, start, end);
		}
		@Override
		protected int maskValue(int value) {
			return (value >>> 12) & 0xff;
		}
		@Override
		protected Node newChildNode(int[] sorted, int start, int end) {
			return new Node4(sorted, start, end);
		}
	}
	private static class Node4 extends AbstractNode {
		public Node4(int[] sorted, int start, int end) {
			super(1 << 4, sorted, start, end);
		}
		@Override
		protected int maskValue(int value) {
			return (value >>> 8) & 0xf;
		}
		@Override
		protected Node newChildNode(int[] sorted, int start, int end) {
			return new Leaf(sorted, start, end);
		}
	}
	
	private static class Leaf implements Node {
		private final long[] bitSet = new long[4];
		public Leaf(int[] sorted, int start, int end) {
			for (int i = start; i < end; i++) {
				final int val = sorted[i];
				final int ind = (val >>> 6) & 0x3;
				final int bit = val & 0x3f;
				bitSet[ind] |= (1L << bit);
			}
		}
		@Override
		public int find(int value) {
			final int ind = (value >>> 6) & 0x3;
			final int bit = value & 0x3f;
			return 0 != (bitSet[ind] & (1L << bit)) ? 1 : -1;//FIXME return the real position
		}
		@Override
		public int get(int index) {
			if (index >= 0 && index < 256) {
				throw new RuntimeException("not implemented");//FIXME implement
			}
			throw new IndexOutOfBoundsException("no such index: " + index);
		}
		@Override
		public long byteSize() {
			return 8L * bitSet.length;
		}
	}
	

}
