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
package org.decimal4j.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Assert;

/**
 * Base class for unit test to enforce final fields and methods.
 */
abstract public class AbstractFinalTest {
	
	protected void assertClassIsFinal(Class<?> clazz) {
		final int mod = clazz.getModifiers();
		Assert.assertTrue("class should be abstract or final: " + clazz, Modifier.isAbstract(mod) || Modifier.isFinal(mod));
	}

	protected void assertAllMethodsAreFinal(Class<?> clazz) {
		//all methods of a class should be final
		for (final Method method : clazz.getDeclaredMethods()) {
			assertMethodIsFinal(method);
		}
		//all methods of an enum constant should also be final
		if (clazz.isEnum()) {
			for (final Object constValue : clazz.getEnumConstants()) {
				for (final Method method : constValue.getClass().getDeclaredMethods()) {
					assertMethodIsFinal(method);
				}
			}
		}
	}

	protected void assertAllFieldsAreFinal(Class<?> clazz) {
		//all methods of a class should be final
		for (final Field field : clazz.getDeclaredFields()) {
			assertFieldFinal(field);
		}
		//all fields of an enum constant should also be final
		if (clazz.isEnum()) {
			for (final Object constValue : clazz.getEnumConstants()) {
				for (final Field field : constValue.getClass().getDeclaredFields()) {
					assertFieldFinal(field);
				}
			}
		}
	}
	
	private void assertMethodIsFinal(Method method) {
		final int mod = method.getModifiers();
		if (!method.isSynthetic() && !isSyntheticEnumMethod(method)) {
			Assert.assertTrue("method should be abstract or final: " + method, Modifier.isAbstract(mod) || Modifier.isFinal(mod));
		}
	}
	private void assertFieldFinal(Field field) {
		final int mod = field.getModifiers();
		if (!field.isSynthetic()) {
			if (isAllowedNonFinalField(field)) {
				Assert.assertFalse("field should be non-static: " + field, Modifier.isStatic(mod));
				Assert.assertTrue("field should be private: " + field, Modifier.isPrivate(mod));
			} else {
				if (!isAllowedNonStaticField(field)) { 
					Assert.assertTrue("field should be static: " + field, Modifier.isStatic(mod));
				}
				Assert.assertTrue("field should be final: " + field, Modifier.isFinal(mod));
			}
			if (field.getType().isArray()) {
				//array should be private as it can be modified
				Assert.assertTrue("array field should be private: " + field, Modifier.isPrivate(mod));
			}
			//TODO collections and maps should be immutable
		}
	}
	
	protected boolean isAllowedNonStaticField(Field field) {
		return false;
	}
	protected boolean isAllowedNonFinalField(Field field) {
		return false;
	}

	private boolean isSyntheticEnumMethod(Method method) {
		if (method.getDeclaringClass().isEnum()) {
			if ("values".equals(method.getName()) && method.getParameterCount() == 0) {
				return true;
			};
			if ("valueOf".equals(method.getName()) && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(String.class)) {
				return true;
			};
		}
		return false;
	}

}
