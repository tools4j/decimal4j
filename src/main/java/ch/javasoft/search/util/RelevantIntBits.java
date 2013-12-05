package ch.javasoft.search.util;

public class RelevantIntBits {
	public RelevantIntBits(int relevantBitMask, int alwaysOneBitMask) {
		this.relevantBitMask = relevantBitMask;
		this.alwaysOneBitMask = alwaysOneBitMask;
	}
	private final int relevantBitMask;
	private final int alwaysOneBitMask;
	
	public int getRelevantBitMask() {
		return relevantBitMask;
	}
	public int getAlwaysOneBitMask() {
		return alwaysOneBitMask;
	}
	public int[] getRelevantBits() {
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
	public int getRelevantBitCount() {
		return Integer.bitCount(relevantBitMask);
	}
	
	public static RelevantIntBits findFor(int... values) {
		int relevantBitMask = 0;
		int alwaysOneBitMask = 0xffffffff;
		for (final int val : values) {
			relevantBitMask |= val;
			alwaysOneBitMask &= val;
		}
		return new RelevantIntBits(relevantBitMask ^ alwaysOneBitMask, alwaysOneBitMask);
	}
}