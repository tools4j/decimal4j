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
package org.decimal4j.arithmetic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test enforcing that all methods and fields of certain classes are final.
 */
@RunWith(Parameterized.class)
public class FinalClassTest {
	
	private final Class<?> clazz;
	
	public FinalClassTest(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Parameters(name = "{index}: {0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		data.add(new Object[] {AbstractArithmetic.class});
		data.add(new Object[] {AbstractCheckedArithmetic.class});
		data.add(new Object[] {AbstractCheckedScale0fArithmetic.class});
		data.add(new Object[] {AbstractCheckedScaleNfArithmetic.class});
		data.add(new Object[] {AbstractUncheckedArithmetic.class});
		data.add(new Object[] {AbstractUncheckedScale0fArithmetic.class});
		data.add(new Object[] {AbstractUncheckedScaleNfArithmetic.class});
		data.add(new Object[] {Add.class});
		data.add(new Object[] {Avg.class});
		data.add(new Object[] {BigDecimalConversion.class});
		data.add(new Object[] {BigIntegerConversion.class});
		data.add(new Object[] {Checked.class});
		data.add(new Object[] {CheckedScale0fRoundingArithmetic.class});
		data.add(new Object[] {CheckedScale0fTruncatingArithmetic.class});
		data.add(new Object[] {CheckedScaleNfRoundingArithmetic.class});
		data.add(new Object[] {CheckedScaleNfTruncatingArithmetic.class});
		data.add(new Object[] {Compare.class});
		data.add(new Object[] {Div.class});
		data.add(new Object[] {DoubleConversion.class});
		data.add(new Object[] {Exceptions.class});
		data.add(new Object[] {FloatConversion.class});
		data.add(new Object[] {Invert.class});
		data.add(new Object[] {JDKSupport.class});
		data.add(new Object[] {LongConversion.class});
		data.add(new Object[] {Mul.class});
		data.add(new Object[] {Pow.class});
		data.add(new Object[] {Pow10.class});
		data.add(new Object[] {Round.class});
		data.add(new Object[] {RoundingUtil.class});
		data.add(new Object[] {Shift.class});
		data.add(new Object[] {SpecialDivisionResult.class});
		data.add(new Object[] {SpecialMultiplicationResult.class});
		data.add(new Object[] {SpecialPowResult.class});
		data.add(new Object[] {Sqrt.class});
		data.add(new Object[] {Square.class});
		data.add(new Object[] {StringConversion.class});
		data.add(new Object[] {Sub.class});
		data.add(new Object[] {ThreadLocals.class});
		data.add(new Object[] {UncheckedScale0fRoundingArithmetic.class});
		data.add(new Object[] {UncheckedScale0fTruncatingArithmetic.class});
		data.add(new Object[] {UncheckedScaleNfRoundingArithmetic.class});
		data.add(new Object[] {UncheckedScaleNfTruncatingArithmetic.class});
		data.add(new Object[] {UnscaledConversion.class});
		data.add(new Object[] {Unsigned.class});
		data.add(new Object[] {UnsignedDecimal9i36f.class});
		return data;
	}

	
	@Test
	public void classShouldBeFinal() {
		final int mod = clazz.getModifiers();
		Assert.assertTrue("class should be abstract or final: " + clazz, Modifier.isAbstract(mod) || Modifier.isFinal(mod));
	}

	@Test
	public void allMethodsShouldBeFinal() {
		//all methods of a class should be final
		for (final Method method : clazz.getDeclaredMethods()) {
			assertMethodFinal(method);
		}
		//all methods of an enum constant should also be final
		if (clazz.isEnum()) {
			for (final Object constValue : clazz.getEnumConstants()) {
				for (final Method method : constValue.getClass().getDeclaredMethods()) {
					assertMethodFinal(method);
				}
			}
		}
	}

	@Test
	public void allFieldsShouldBeFinal() {
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
	
	private void assertMethodFinal(Method method) {
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
				if (!isAllowedNonStaticField()) { 
					Assert.assertTrue("field should be static: " + field, Modifier.isStatic(mod));
				}
				Assert.assertTrue("field should be final: " + field, Modifier.isFinal(mod));
			}
		}
	}
	
	private boolean isAllowedNonStaticField() {
		return AbstractArithmetic.class.isAssignableFrom(clazz);
	}
	private boolean isAllowedNonFinalField(Field field) {
		if (UnsignedDecimal9i36f.class.equals(clazz)) {
			return Arrays.asList("norm", "pow10", "ival", "val3", "val2", "val1", "val0").contains(field.getName());
		}
		return false;
	}

	private boolean isSyntheticEnumMethod(Method method) {
		if (clazz.isEnum()) {
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
