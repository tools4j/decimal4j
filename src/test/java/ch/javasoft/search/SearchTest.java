package ch.javasoft.search;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class SearchTest {

	private final Random rnd = new Random();
	private final int n = 20000000;
	private final int h = 20000000;
	private final int m = 20000000;
//	private final int n = 10000000;
//	private final int h = 10000000;
//	private final int m = 10000000;
	private final int maxval = 0;//Integer.MAX_VALUE;//<=0 to allow any int
	
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
	public void testExactMembershipSearch() {
		final int[] vals = randomValues();
		testSearch(new ExactMembershipSearch(vals), vals);
	}

	private int[] randomValues() {
		final int[] vals = new int[n];
		for (int i = 0; i < vals.length; i++) {
			vals[i] = maxval <= 0 ? rnd.nextInt() : rnd.nextInt(maxval);
		}
		return vals;
	}

	private void testSearch(Search search, int[] vals) {
		final long timeStart = System.currentTimeMillis();
		testHits(search, vals);
		final long timeMid = System.currentTimeMillis();
		final int hits = testMisses(search);
		final long timeEnd = System.currentTimeMillis();
		System.out.println(search.getClass().getSimpleName() + ": n=" + n + ", t=" + h + ", f=" + m + " (" + hits + "), size=" + (search.byteSize() / (1L << 20)) + "mb = " + (search.byteSize() *8f / n ) + " bits/value, dt=" + (timeEnd-timeStart) + "ms, t/f=(" + (timeMid-timeStart) + "/" + (timeEnd-timeMid) + ") ms");
	}
	private void testHits(Search search, int[] vals) {
		int i = 0;
		while (true) {
			for (final int v : vals) {
				final int found = search.find(v);
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
