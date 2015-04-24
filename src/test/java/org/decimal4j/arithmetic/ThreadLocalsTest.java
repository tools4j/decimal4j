package org.decimal4j.arithmetic;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

/**
 * Unit test for {@link ThreadLocals}.
 */
public class ThreadLocalsTest {
	
	private static class ThreadLocalInstances {
		public final StringBuilder stringBuilder = StringConversion.STRING_BUILDER_THREAD_LOCAL.get();
		public final UnsignedDecimal9i36f unsignedDecimal1 = UnsignedDecimal9i36f.THREAD_LOCAL_1.get();
		public final UnsignedDecimal9i36f unsignedDecimal2 = UnsignedDecimal9i36f.THREAD_LOCAL_2.get();
	}

	@Test
	public void shoudUseDifferentThreadLocalInstancesInTwoThreads() {
		//given
		final AtomicReference<ThreadLocalInstances> ref = new AtomicReference<ThreadLocalInstances>();
		ThreadLocalInstances tli1, tli2;
		final Runnable runnable = new Runnable() {
			public void run() {
				ref.set(new ThreadLocalInstances());
			}
		};
		
		//when
		new Thread(runnable).start();
		do tli1 = ref.getAndSet(null);
		while (tli1 == null);
		
		new Thread(runnable).start();
		do tli2 = ref.getAndSet(null);
		while (tli2 == null);

		//then
		assertNotSame("string builder should be different instances", tli1.stringBuilder, tli2.stringBuilder);
		assertNotSame("unsigned decimal 1 should be different instances", tli1.unsignedDecimal1, tli2.unsignedDecimal1);
		assertNotSame("unsigned decimal 2 should be different instances", tli1.unsignedDecimal2, tli2.unsignedDecimal2);
	}

	@Test
	public void shoudReuseThreadLocalInstancesInSameThread() {
		//when
		final ThreadLocalInstances tli1 = new ThreadLocalInstances();
		final ThreadLocalInstances tli2 = new ThreadLocalInstances();
		
		//then
		assertSame("string builder should be same instance", tli1.stringBuilder, tli2.stringBuilder);
		assertSame("unsigned decimal 1 should be same instance", tli1.unsignedDecimal1, tli2.unsignedDecimal1);
		assertSame("unsigned decimal 2 should be same instance", tli1.unsignedDecimal2, tli2.unsignedDecimal2);
	}

	@Test
	public void shoudRemoveThreadLocalInstances() {
		//given
		final ThreadLocalInstances tli1 = new ThreadLocalInstances();

		//when
		ThreadLocals.removeAll();
		final ThreadLocalInstances tli2 = new ThreadLocalInstances();
		
		//then
		assertNotSame("string builder should be different instances", tli1.stringBuilder, tli2.stringBuilder);
		assertNotSame("unsigned decimal 1 should be different instances", tli1.unsignedDecimal1, tli2.unsignedDecimal1);
		assertNotSame("unsigned decimal 2 should be different instances", tli1.unsignedDecimal2, tli2.unsignedDecimal2);
	}

}
