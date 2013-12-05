package ch.javasoft.search;

import java.util.BitSet;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import ch.javasoft.search.util.KBitValueBuffer;
import ch.javasoft.search.util.LongArrayKBitValueBuffer;

/**
 * Unit test for {@link KBitValueBuffer}
 */
public class KBitValueBufferTest {

	private final KBitValueBuffer.Factory bufferFactory = LongArrayKBitValueBuffer.FACTORY;
//	private final KBitValueBuffer.Factory bufferFactory = SingleAccessLongArrayKBitValueBuffer.FACTORY;
	
	private final Random rnd = new Random();

	@Test
	public void testZeroBits() {
		final int n = 1000000;
		final KBitValueBuffer arr = bufferFactory.create(n, 0);
		for (int i = 0; i < n; i++) {
			arr.set(i, rnd.nextLong());
		}
		for (int i = 0; i < n; i++) {
			Assert.assertEquals("value[" + i + "]=" + 0, 0, arr.get(i));
		}
	}
	@Test
	public void testSingleBits() {
		final int n = 1000000;
		final KBitValueBuffer arr = bufferFactory.create(n, 1);

		//set every bit and clear it again
		for (int i = 0; i < n; i++) {
			Assert.assertEquals("value[" + i + "]=" + 0, 0, arr.get(i));
			arr.set(i, 1);
			Assert.assertEquals("value[" + i + "]=" + 1, 1, arr.get(i));
			arr.set(i, 0);
			Assert.assertEquals("value[" + i + "]=" + 0, 0, arr.get(i));
		}

		//set some random bits and assert them
		final BitSet expected = new BitSet();
		for (int i = 0; i < n; i++) {
			final int val = rnd.nextInt(n);
			expected.set(val);
			arr.set(val, 1);
		}
		for (int i = 0; i < n; i++) {
			Assert.assertEquals(expected.get(i) ? 1 : 0, arr.get(i));
		}
	}

	@Test
	public void test1to64bitValues() {
		final int n = 100000;
		for (int b = 1; b < 64; b++) {
			final KBitValueBuffer arr = bufferFactory.create(n, b);
			final long mask = arr.getMask();
			Assert.assertEquals(b, Long.bitCount(mask));
			Assert.assertEquals(1L << b, mask + 1);
			
			//set mask value and clear it again
			for (int i = 0; i < n; i++) {
				Assert.assertEquals("value[" + i + "]=" + 0, 0, arr.get(i));
				arr.set(i, mask);
				Assert.assertEquals("value[" + i + "]=" + mask, mask, arr.get(i));
				arr.set(i, 0);
				Assert.assertEquals("value[" + i + "]=" + 0, 0, arr.get(i));
			}

			//set some random values and assert them
			final long[] vals = new long[n];
			for (int i = 0; i < n; i++) {
				vals[i] = mask & rnd.nextLong();
				arr.set(i, vals[i]);
			}
			for (int i = 0; i < n; i++) {
				Assert.assertEquals(vals[i], arr.get(i));
			}
			//clear them again
			for (int i = 0; i < n; i++) {
				arr.set(i, 0);
			}
			for (int i = 0; i < n; i++) {
				Assert.assertEquals(0, arr.get(i));
			}
			//no set random values at random indices
			final long[] vals2 = new long[n];
			for (int i = 0; i < n; i++) {
				final int index = rnd.nextInt(n);
				vals2[index] = mask & rnd.nextLong();
				arr.set(index, vals2[index]);
			}
			for (int i = 0; i < n; i++) {
				Assert.assertEquals(vals2[i], arr.get(i));
			}
		}
	}
}
