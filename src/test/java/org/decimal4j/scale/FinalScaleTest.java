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
package org.decimal4j.scale;

import java.lang.reflect.Field;
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
 * Unit test enforcing that all methods and fields of {@link Scales} and of 
 * {@link ScaleMetrics} implementations are final.
 */
@RunWith(Parameterized.class)
public class FinalScaleTest extends AbstractFinalTest {
	
	private final Class<?> clazz;
	
	public FinalScaleTest(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Parameters(name = "{index}: {0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		data.add(new Object[] {Scales.class});
		data.add(new Object[] {Scale0f.class});
		data.add(new Object[] {Scale1f.class});
		data.add(new Object[] {Scale2f.class});
		data.add(new Object[] {Scale3f.class});
		data.add(new Object[] {Scale4f.class});
		data.add(new Object[] {Scale5f.class});
		data.add(new Object[] {Scale6f.class});
		data.add(new Object[] {Scale7f.class});
		data.add(new Object[] {Scale8f.class});
		data.add(new Object[] {Scale9f.class});
		data.add(new Object[] {Scale10f.class});
		data.add(new Object[] {Scale11f.class});
		data.add(new Object[] {Scale12f.class});
		data.add(new Object[] {Scale13f.class});
		data.add(new Object[] {Scale14f.class});
		data.add(new Object[] {Scale15f.class});
		data.add(new Object[] {Scale16f.class});
		data.add(new Object[] {Scale17f.class});
		data.add(new Object[] {Scale18f.class});
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
	
	@Override
	protected boolean isAllowedNonStaticField(Field field) {
		return false;
	}
	
	@Override
	protected boolean isAllowedNonFinalField(Field field) {
		return false;
	}

}
