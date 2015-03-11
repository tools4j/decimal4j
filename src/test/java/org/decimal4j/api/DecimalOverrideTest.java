/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Unit test asserting that all methods returning a {@link Decimal} value in
 * the {@code Decimal} interface are override with specialized return values
 * in {@link ImmutableDecimal} and in {@link MutableDecimal}.
 */
public class DecimalOverrideTest {
	
	private static final List<String> EXCEPT_METHODS = Arrays.asList("min", "max");

	@Test
	public void shouldOverrideWithImmutableDecimal() throws NoSuchMethodException, SecurityException {
		shouldOverrideWith(ImmutableDecimal.class);
	}
	@Test
	public void shouldOverrideWithMutableDecimal() throws NoSuchMethodException, SecurityException {
		shouldOverrideWith(MutableDecimal.class);
	}
	
	private void shouldOverrideWith(Class<?> subtype) throws NoSuchMethodException, SecurityException {
		for (final Method method : getDecimalMethods()) {
			final Method overrideMethod = subtype.getMethod(method.getName(), method.getParameterTypes());
			assertNotNull("method should have override in " + subtype.getSimpleName() + ": " + method, overrideMethod);
			assertEquals("return type should be " + subtype.getSimpleName() + ": " + overrideMethod, subtype, overrideMethod.getReturnType());
		}
	}

	private List<Method> getDecimalMethods() {
		final List<Method> methods = new ArrayList<Method>();
		for (final Method method : Decimal.class.getMethods()) {
			if (Decimal.class.equals(method.getReturnType())) {
				if (!EXCEPT_METHODS.contains(method.getName())) {
					methods.add(method);
				}
			}
		}
		return methods;
	}
}
