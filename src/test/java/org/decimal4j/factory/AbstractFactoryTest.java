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
package org.decimal4j.factory;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.AbstractDecimalTest;
import org.decimal4j.test.ArithmeticResult;
import org.decimal4j.test.TestSettings;
import org.junit.Test;

/**
 * Base class for unit tests of a {@link DecimalFactory} method.
 */
abstract public class AbstractFactoryTest<V> extends AbstractDecimalTest {
	public AbstractFactoryTest(DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	abstract protected V randomValue(ScaleMetrics scaleMetrics);

	abstract protected V[] specialValues(ScaleMetrics scaleMetrics);

	// override if should throw an exception
	protected <S extends ScaleMetrics> V expectedResult(S scaleMetrics, V value) {
		return value;
	}

	abstract protected <S extends ScaleMetrics> V actualResult(DecimalFactory<S> factory, V value);

	protected int getRandomTestCount() {
		return TestSettings.getRandomTestCount();
	}

	private DecimalFactory<?> getDecimalFactory() {
		return RND.nextBoolean() ? Factories.getDecimalFactory(getScaleMetrics()) : Factories
				.getGenericDecimalFactory(getScaleMetrics());
	}

	@Test
	public void runRandomTest() {
		final int n = getRandomTestCount();
		final ScaleMetrics scaleMetrics = arithmetic.getScaleMetrics();
		for (int i = 0; i < n; i++) {
			runTest(getDecimalFactory(), ".random[" + i + "]", randomValue(scaleMetrics));
		}
	}

	@Test
	public void runSpecialValueTest() {
		int index = 0;
		for (final V value : specialValues(getScaleMetrics())) {
			runTest(getDecimalFactory(), ".special[" + index + "]", value);
			index++;
		}
	}

	private <S extends ScaleMetrics> void runTest(DecimalFactory<S> decimalFactory, String name, V value) {
		//expected
		ArithmeticResult<V> expected;
		try {
			expectedResult(decimalFactory.getScaleMetrics(), value);
			expected = ArithmeticResult.forResult(value.toString(), value);
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		}

		//actual
		ArithmeticResult<V> actual;
		try {
			final V act = actualResult(decimalFactory, value);
			actual = ArithmeticResult.forResult(act.toString(), act);
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		}

		//assert
		actual.assertEquivalentTo(expected, getClass().getSimpleName() + name + ": " + value);
	}

}
