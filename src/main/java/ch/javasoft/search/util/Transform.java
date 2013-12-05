package ch.javasoft.search.util;

public final class Transform {

	public final static int transform(int value, int[] bits) {
		final int len = bits.length;
		int result = 0;
		for (int i = 0; i < len; i++) {
			if (0 != (value & (1 << bits[i]))) {
				result |= (1 << i);
			}
		}
		return result;
	}
	
	public static int transform(int value, int mask) {
		int srcBit = 1;
		int dstBit = 1;
		int result = 0;
		for (int i = 0; i < 32; i++) {
			if (0 != (mask & srcBit)) {
				if (0 != (value & srcBit)) {
					result |= dstBit;
				}
				dstBit <<= 1;
			}
			srcBit <<= 1;
		}
		return result;
	}

	public static long transform(long value, long mask) {
		long srcBit = 1L;
		long dstBit = 1L;
		long result = 0L;
		for (int i = 0; i < 64; i++) {
			if (0 != (mask & srcBit)) {
				if (0 != (value & srcBit)) {
					result |= dstBit;
				}
				dstBit <<= 1;
			}
			srcBit <<= 1;
		}
		return result;
	}

	public final static int backTransform(int transformed, int[] bits) {
		final int len = bits.length;
		int result = 0;
		for (int i = 0; i < len; i++) {
			if (0 != (transformed & (1 << i))) {
				result |= (1 << bits[i]);
			}
		}
		return result;
	}
	
	public static final int backTransform(int transformed, int mask) {
		int srcBit = 1;
		int dstBit = 1;
		int result = 0;
		for (int i = 0; i < 32; i++) {
			if (0 != (mask & dstBit)) {
				if (0 != (transformed & srcBit)) {
					result |= dstBit;
				}
				srcBit <<= 1;
			}
			dstBit <<= 1;
		}
		return result;
	}

	public static final long backTransform(long transformed, long mask) {
		long srcBit = 1L;
		long dstBit = 1L;
		long result = 0L;
		for (int i = 0; i < 64; i++) {
			if (0 != (mask & dstBit)) {
				if (0 != (transformed & srcBit)) {
					result |= dstBit;
				}
				srcBit <<= 1;
			}
			dstBit <<= 1;
		}
		return result;
	}

	// no instances
	private Transform() {
		super();
	}
}
