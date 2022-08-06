/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 decimal4j (tools4j), Marco Terzer
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.decimal4j.base.AbstractDecimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.Scales;
import org.junit.Test;

/**
 * Unit test asserting that all methods returning a {@link Decimal} value in
 * the {@code Decimal} interface are override with specialized return values
 * in {@link ImmutableDecimal} and in {@link MutableDecimal}.
 */
public class DecimalOverrideTest {
	
	private static final Random RND = new Random();
	private static final List<String> EXCEPT_METHODS_IFACE = Arrays.asList("min", "max");
	private static final List<String> EXCEPT_METHODS_CLASS = Arrays.asList("min", "max", "multiplyExact", "scale");

	@Test
	public void shouldOverrideWithImmutableDecimal() throws NoSuchMethodException, SecurityException {
		shouldOverrideWith(ImmutableDecimal.class);
		shouldOverrideWith(Factories.getGenericDecimalFactory(RND.nextInt(Scales.MAX_SCALE + 1)).immutableType());
		for (final DecimalFactory<?> factory : Factories.VALUES) {
			shouldOverrideWith(factory.immutableType());
		}
	}
	@Test
	public void shouldOverrideWithMutableDecimal() throws NoSuchMethodException, SecurityException {
		shouldOverrideWith(MutableDecimal.class);
		shouldOverrideWith(Factories.getGenericDecimalFactory(RND.nextInt(Scales.MAX_SCALE + 1)).mutableType());
		for (final DecimalFactory<?> factory : Factories.VALUES) {
			shouldOverrideWith(factory.mutableType());
		}
	}
	@Test
	public void shouldOverrideWithImmutableDecimalArray() throws NoSuchMethodException, SecurityException {
		shouldOverrideWithArrayOf(ImmutableDecimal.class);
		shouldOverrideWithArrayOf(Factories.getGenericDecimalFactory(RND.nextInt(Scales.MAX_SCALE + 1)).immutableType());
		for (final DecimalFactory<?> factory : Factories.VALUES) {
			shouldOverrideWithArrayOf(factory.immutableType());
		}
	}
	@Test
	public void shouldOverrideWithMutableDecimalArray() throws NoSuchMethodException, SecurityException {
		shouldOverrideWithArrayOf(MutableDecimal.class);
		shouldOverrideWithArrayOf(Factories.getGenericDecimalFactory(RND.nextInt(Scales.MAX_SCALE + 1)).mutableType());
		for (final DecimalFactory<?> factory : Factories.VALUES) {
			shouldOverrideWithArrayOf(factory.mutableType());
		}
	}
	
	private void shouldOverrideWith(Class<?> subtype) throws NoSuchMethodException, SecurityException {
		for (final Method method : getDecimalMethods(subtype.isInterface())) {
			final Method overrideMethod = getMethod(subtype, method.getName(), method.getParameterTypes());
			assertNotNull("method should have override in " + subtype.getSimpleName() + ": " + method, overrideMethod);
			assertTrue("return type should be " + subtype.getSimpleName() + ": " + overrideMethod, isReturnType(subtype, overrideMethod));
		}
	}
	private boolean isReturnType(Class<?> expectedType, Method method) {
		Class<?> actualType = method.getReturnType();
		Type actualGenericType = method.getGenericReturnType();
		//handle array type
		if (method.getReturnType().isArray()) {
			actualType = actualType.getComponentType();
			actualGenericType = actualGenericType instanceof GenericArrayType ? ((GenericArrayType)actualGenericType).getGenericComponentType() : actualGenericType;
		}
		//a) direct check
		if (expectedType.equals(actualType)) {
			return true;
		}
		//b) check generic return type
		if (actualGenericType instanceof TypeVariable<?>) {
			final Type[] bounds = ((TypeVariable<?>)actualGenericType).getBounds();
			if (bounds.length == 1) {
				final Type bound = bounds[0];
				if (bound instanceof ParameterizedType) {
					final ParameterizedType ptype = (ParameterizedType)bound;
					return ptype.getRawType() instanceof Class && AbstractDecimal.class.isAssignableFrom((Class<?>)ptype.getRawType());
				}
			}
		}
		return false;
	}
	private void shouldOverrideWithArrayOf(Class<?> subtype) throws NoSuchMethodException, SecurityException {
		for (final Method method : getDecimalArrayMethods()) {
			final Method overrideMethod = getMethod(subtype, method.getName(), method.getParameterTypes());
			assertNotNull("method should have override in " + subtype.getSimpleName() + ": " + method, overrideMethod);
			assertTrue("return type should be an array: " + overrideMethod, overrideMethod.getReturnType().isArray());
			assertTrue("return type should be " + subtype.getSimpleName() + "[]: " + overrideMethod, isReturnType(subtype, overrideMethod));
		}
	}

	private static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		if (clazz == null) {
			return null;
		}
		try {
			final Method method = clazz.getDeclaredMethod(name, parameterTypes);
			if (!method.isSynthetic()) {
				return method;
			}
		} catch (NoSuchMethodException e) {
			//ignore, try superclass below
		}
		return getMethod(clazz.getSuperclass(), name, parameterTypes);
	}
	private static List<Method> getDecimalMethods(boolean subtypeIsInterface) {
		final List<Method> methods = new ArrayList<Method>();
		for (final Method method : Decimal.class.getMethods()) {
			if (Decimal.class.equals(method.getReturnType())) {
				if (!toExclude(subtypeIsInterface).contains(method.getName())) {
					methods.add(method);
				}
			}
		}
		return methods;
	}
	private static List<Method> getDecimalArrayMethods() {
		final List<Method> methods = new ArrayList<Method>();
		for (final Method method : Decimal.class.getMethods()) {
			if (method.getReturnType().isArray() && method.getReturnType().getComponentType().equals(Decimal.class)) {
				methods.add(method);
			}
		}
		return methods;
	}
	
	private static List<String> toExclude(boolean subtypeIsInterface) {
		return subtypeIsInterface ? EXCEPT_METHODS_IFACE : EXCEPT_METHODS_CLASS;
	}
}
