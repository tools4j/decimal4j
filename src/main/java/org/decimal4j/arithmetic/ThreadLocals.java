/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 decimal4j (tools4j), Marco Terzer
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

/**
 * Helper class to remove all values held by {@link ThreadLocal} variables. This 
 * may be useful if the library is used in a web service or servlet container.
 */
public final class ThreadLocals {
	
	/**
	 * Removes all values held by {@link ThreadLocal} variables that are used by 
	 * the decimal4j library. {@link ThreadLocal#remove()} is called on every
	 * variable to make objects held by those variables available for garbage 
	 * collection.
	 */
	public static final void removeAll() {
		StringConversion.STRING_BUILDER_THREAD_LOCAL.remove();
		UnsignedDecimal9i36f.THREAD_LOCAL_1.remove();
		UnsignedDecimal9i36f.THREAD_LOCAL_2.remove();
	}
	
	// no instances
	private ThreadLocals() {
		super();
	}

}
