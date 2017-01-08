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
package org.decimal4j.truncate;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.test.AbstractFinalTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test enforcing that all methods and fields of enums and classes in this package are final.
 */
@RunWith(Parameterized.class)
public class FinalTruncateClassTest extends AbstractFinalTest {
	
	private final Class<?> clazz;
	
	public FinalTruncateClassTest(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Parameters(name = "{index}: {0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		data.add(new Object[] {CheckedRounding.class});
		data.add(new Object[] {DecimalRounding.class});
		data.add(new Object[] {OverflowMode.class});
		data.add(new Object[] {TruncatedPart.class});
		data.add(new Object[] {UncheckedRounding.class});
		return data;
	}
	
	@Test
	public void classShouldBeFinal() {
		final int mod = clazz.getModifiers();
		Assert.assertTrue("class should be abstract or final: " + clazz, Modifier.isAbstract(mod) || Modifier.isFinal(mod));
	}

	@Test
	public void allMethodsShouldBeFinal() {
		assertAllMethodsAreFinal(clazz);
	}

	@Test
	public void allFieldsShouldBeFinal() {
		assertAllFieldsAreFinal(clazz);
	}
	
}
