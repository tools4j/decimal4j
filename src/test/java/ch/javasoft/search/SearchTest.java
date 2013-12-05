package ch.javasoft.search;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import ch.javasoft.search.util.KBitValueBuffer;
import ch.javasoft.search.util.LongArrayKBitValueBuffer;

public class SearchTest {

	private final KBitValueBuffer.Factory bufferFactory = LongArrayKBitValueBuffer.FACTORY;
//	private final KBitValueBuffer.Factory bufferFactory = SingleAccessLongArrayKBitValueBuffer.FACTORY;
	

	private final Random rnd = new Random();
//	private final int n = 100000000;
//	private final int h = 100000000;
//	private final int m = 100000000;
//	private final int n = 20000000;
//	private final int h = 20000000;
//	private final int m = 20000000;
	private final int n = 10000000;
	private final int h = 10000000;
	private final int m = 10000000;
//	private final int n = 1000000;
//	private final int h = 1000000;
//	private final int m = 1000000;
//	private final int n = 1000;
//	private final int h = 1000;
//	private final int m = 1000;
//	private final int n = 100;
//	private final int n = 100;
//	private final int h = 100;
//	private final int m = 100;
	private final int maxval = Integer.MAX_VALUE/10;//<=0 to allow any int
	private final int minval = 0;//-10;//-2*n;//Integer.MIN_VALUE;//Integer.MAX_VALUE;//<=0 to allow any int
	
	@Test
	public void testBinarySearch() {
		final int[] vals = randomValues();
		testSearch(new BinarySearch(vals), vals);
	}

	@Test
	public void testNearlyCompleteBinaryTree() {
		final int[] vals = randomValues();
		testSearch(new NearlyCompleteBinaryTree(vals), vals);
	}

	@Test
	public void testCountSlotsMembershipSearch() {
		final int[] vals = randomValues();
		testSearch(new CountSlotsMembershipSearch(bufferFactory, vals), vals);
	}

	@Test
	public void testCountSlotsRelevantBitsMembershipSearch() {
		final int[] vals = randomValues();
		testSearch(new CountSlotsRelevantBitsMembershipSearch(bufferFactory, vals), vals);
	}
	
	@Test
	public void testMaskCountSlotsMembershipSearch() {
		final int[] vals = randomValues();
		testSearch(new MaskCountSlotsMembershipSearch(bufferFactory, vals), vals);
	}

	private int[] randomValues() {
		final int[] vals = new int[n];
		for (int i = 0; i < vals.length; i++) {
			if (minval >= maxval) {
				vals[i] = rnd.nextInt();
			} else {
				if (minval < 0) {
					if (maxval > 0) {
						if (minval == Integer.MIN_VALUE) {
							if (maxval == Integer.MAX_VALUE) {
								vals[i] = rnd.nextInt();
							} else {
								vals[i] = rnd.nextInt(maxval);
								vals[i] -= rnd.nextInt(Integer.MAX_VALUE);
								vals[i] -= rnd.nextInt(1);
							}
						} else {
							vals[i] = rnd.nextInt(maxval);
							vals[i] -= rnd.nextInt(Math.max(-minval+1, -minval));
						}
					} else {
						vals[i] = maxval + rnd.nextInt(-(minval - maxval));
					}
				} else {
					vals[i] = minval + rnd.nextInt(maxval - minval);
				}
			}
		}
		return vals;
	}

	private void testSearch(Search search, int[] vals) {
		final long timeStart = System.currentTimeMillis();
		testHits(search, vals);
		final long timeMid = System.currentTimeMillis();
		final int hits = testMisses(search);
		final long timeEnd = System.currentTimeMillis();
		System.out.println(search.getClass().getSimpleName() + ": n=" + n + ", t=" + h + ", f=" + m + " (" + hits + "), size=" + sizeString(search.byteSize()) + " <==> " + (search.byteSize() *8f / n ) + " bits/value, dt=" + (timeEnd-timeStart) + "ms, t/f=(" + (timeMid-timeStart) + "/" + (timeEnd-timeMid) + ") ms");
	}
	private String sizeString(long byteSize) {
		int unit = 0;
		long scaled = byteSize;
		while ((scaled >>> 10) >= 10) {
			unit++;
			scaled >>>= 10;
		}
		return scaled + new String[] {"b", "kb", "mb", "gb", "tb", "pb", "eb", "zb", "yb"}[unit];
	}

	private void testHits(Search search, int[] vals) {
		int i = 0;
		while (true) {
			for (final int v : vals) {
				final int found = search.find(v);
				Assert.assertTrue("should find: " + v + " [i=" + i + "]", found >= 0);
				Assert.assertEquals(v, search.get(found));
				i++;
				if (i >= h) return;
			}
		}
	}
	private int testMisses(Search search) {
		int cnt = 0;
		for (int j = 0; j < m; j++) {
			final int v = rnd.nextInt();
			final int found = search.find(v);
			if (found >= 0) cnt++;
		}
		return cnt;
	}
}
