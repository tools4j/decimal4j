package ch.javasoft.search;


public class ExactMembershipSizeTest {

	public static void main(String[] args) {
		final int b = 32;
		final long N = 1L<<(b-2);
		
		for (long n = 1; n >= 0 && n < N; n*=10) {
			for (final Strategy s : Strategy.values()) {
				printSize(s, n, b);
			}
		}
	}
	
	private static void printSize(Strategy strategy, long n, int b) {
		final int k = findOptK(strategy, n, b);
		final long size = strategy.sizeOf(n, b, k);
		System.out.println(n + " [" + strategy + "]: " + size + "=" + (size/(n+0f)) + " bits/value with k=" + k);
	}

	private static enum Strategy {
		Standard {
			@Override
			public long sizeOf(long n, int b, int k) {
				return (b-k)*n + n+(1L<<k);
			}
		},
		SkipOpti {
			@Override
			public long sizeOf(long n, int b, int k) {
				final int log2n = 64 - Long.numberOfLeadingZeros(n-1);
				final long slots = 1 + (n-1) / Math.max(log2n, 1);
				return (b-k)*n + n + (1L<<k) + slots*(log2n);
			}
		},
		SkipArry {
			@Override
			public long sizeOf(long n, int b, int k) {
				final int log2n = 64 - Long.numberOfLeadingZeros(n-1);
				final long slots = 1 + (n-1) / Math.max(log2n, 1);
				return (b-k)*n + n + (1L<<k) + slots*(log2n + 16*8);
			}
		},
		SlotOpti {
			@Override
			public long sizeOf(long n, int b, int k) {
				final int log2n = 64 - Long.numberOfLeadingZeros(n-1);
				final long slots = 1L<<k;
				return (b-k)*n + (slots-1)*log2n;
			}
		},
		SlotBits {
			@Override
			public long sizeOf(long n, int b, int k) {
				final long slots = 1L<<k;
				return (b-k)*n + (slots-1)*b;
			}
		},
		SlotHash {
			@Override
			public long sizeOf(long n, int b, int k) {
				final long slots = 1L<<k;
				return (b-k)*n + (slots-1)*2L*b;
			}
		},
		SlotArry {
			@Override
			public long sizeOf(long n, int b, int k) {
				final long slots = 1L<<k;
				return (b-k)*n + (slots-1)*(b + 16*8);
			}
		};
		
		abstract public long sizeOf(long n, int b, int k);
	}

	private static int findOptK(Strategy strategy, long n, int b) {
		long bestSize = strategy.sizeOf(n, b, 0);
		int bestK = 0;
		for (int k = 1; k <= b; k++) {
			final long size = strategy.sizeOf(n, b, k);
			if (size < bestSize) {
				bestSize = size;
				bestK = k;
			}
		}
		return bestK;
	}
}
