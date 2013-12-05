package ch.javasoft.search.util;

public final class ExactMembership {

	public static final int findBestK(int n, int b) {
		int bestK = 0;
		long bestSize = sizeOf(n, b, bestK);
		for (int k = 1; k <= b; k++) {
			final long size = sizeOf(n, b, k);
			if (size < bestSize) {
				bestSize = size;
				bestK = k;
			}
		}
		return bestK;
	}

	public static final long sizeOf(int n, int b, int k) {
		final long slots = 1L << k;
		return (b - k) * (long)n + slots * b;
	}
	
	//no instances
	private ExactMembership() {
		super();
	}
}
