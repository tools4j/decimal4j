/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
