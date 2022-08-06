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
package org.decimal4j.factory;

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
 * Unit test enforcing that all methods and fields of {@link Factories} and all
 * {@link DecimalFactory} implementations are final.
 */
@RunWith(Parameterized.class)
public class FinalFactoryTest extends AbstractFinalTest {
	
	private final Class<?> clazz;
	
	public FinalFactoryTest(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Parameters(name = "{index}: {0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		data.add(new Object[] {Factories.class});
		data.add(new Object[] {Factory0f.class});
		data.add(new Object[] {Factory1f.class});
		data.add(new Object[] {Factory2f.class});
		data.add(new Object[] {Factory3f.class});
		data.add(new Object[] {Factory4f.class});
		data.add(new Object[] {Factory5f.class});
		data.add(new Object[] {Factory6f.class});
		data.add(new Object[] {Factory7f.class});
		data.add(new Object[] {Factory8f.class});
		data.add(new Object[] {Factory9f.class});
		data.add(new Object[] {Factory10f.class});
		data.add(new Object[] {Factory11f.class});
		data.add(new Object[] {Factory12f.class});
		data.add(new Object[] {Factory13f.class});
		data.add(new Object[] {Factory14f.class});
		data.add(new Object[] {Factory15f.class});
		data.add(new Object[] {Factory16f.class});
		data.add(new Object[] {Factory17f.class});
		data.add(new Object[] {Factory18f.class});
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
