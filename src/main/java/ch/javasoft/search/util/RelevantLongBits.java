package ch.javasoft.search.util;

public class RelevantLongBits {
	public RelevantLongBits(long relevantBitMask, long alwaysOneBitMask) {
		this.relevantBitMask = relevantBitMask;
		this.alwaysOneBitMask = alwaysOneBitMask;
	}
	private final long relevantBitMask;
	private final long alwaysOneBitMask;
	
	public long getRelevantBitMask() {
		return relevantBitMask;
	}
	public long getAlwaysOneBitMask() {
		return alwaysOneBitMask;
	}
	public int[] getRelevantBits() {
		final int[] relevantBits = new int[Long.bitCount(relevantBitMask)];
		int index = 0;
		for (int i = 0; i < 64; i++) {
			if (0 != (relevantBitMask & (1L << i))) {
				relevantBits[index] = i;
				index++;
			}
		}
		return relevantBits;
	}
	
	public static RelevantLongBits findFor(long... values) {
		long relevantBitMask = 0;
		long alwaysOneBitMask = 0xffffffffffffffffL;
		for (final long val : values) {
			relevantBitMask |= val;
			alwaysOneBitMask &= val;
		}
		return new RelevantLongBits(relevantBitMask ^ alwaysOneBitMask, alwaysOneBitMask);
	}
	
}