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
package org.decimal4j.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link GenericImmutableDecimal} and
 * {@link GenericMutableDecimal}
 */
@RunWith(Parameterized.class)
public class GenericDecimalTest {

	private static final Random RND = new Random();

	private final ScaleMetrics scaleMetrics;

	public GenericDecimalTest(ScaleMetrics scaleMetrics) {
		this.scaleMetrics = Objects.requireNonNull(scaleMetrics, "scaleMetrics cannot be null");
	}

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> getParameters() {
		final List<Object[]> params = new ArrayList<>();
		for (final ScaleMetrics sm : Scales.VALUES) {
			params.add(new Object[] { sm });
		}
		return params;
	}

	@Test
	public void shouldCreateGenericValueZero() {
		// when
		final GenericImmutableDecimal<?> immutable = GenericImmutableDecimal.valueOfUnscaled(scaleMetrics, 0);
		final GenericMutableDecimal<?> mutable = GenericMutableDecimal.valueOfUnscaled(scaleMetrics, 0);

		// then
		assertBase(immutable);
		assertBase(mutable);
		assertTrue("immutable should be zero", immutable.isZero());
		assertTrue("mutable should be zero", mutable.isZero());
		assertEquals("immutable and mutable value should be equal", immutable, mutable);
	}

	@Test
	public void shouldCreateGenericValue() {
		// given
		final long unscaled = RND.nextLong();

		// when
		final GenericImmutableDecimal<?> immutable = GenericImmutableDecimal.valueOfUnscaled(scaleMetrics, unscaled);
		final GenericMutableDecimal<?> mutable = GenericMutableDecimal.valueOfUnscaled(scaleMetrics, unscaled);

		// then
		assertBase(immutable);
		assertBase(mutable);
		assertEquals("immutable should have unscaled value " + unscaled, unscaled, immutable.unscaledValue());
		assertEquals("mutable should have unscaled value " + unscaled, unscaled, mutable.unscaledValue());
		assertEquals("immutable and mutable value should be equal", immutable, mutable);
	}

	@Test
	public void shouldCloneGenericValue() {
		// given
		final GenericImmutableDecimal<?> immutable = GenericImmutableDecimal.valueOfUnscaled(scaleMetrics,
				RND.nextLong());
		final GenericMutableDecimal<?> mutable = GenericMutableDecimal.valueOfUnscaled(scaleMetrics, RND.nextLong());

		// when
		final GenericImmutableDecimal<?> immutableCloneI = GenericImmutableDecimal.valueOf(immutable);
		final GenericImmutableDecimal<?> immutableCloneM = GenericImmutableDecimal.valueOf(mutable);
		final GenericMutableDecimal<?> mutableCloneI = GenericMutableDecimal.valueOf(immutable);
		final GenericMutableDecimal<?> mutableCloneM = GenericMutableDecimal.valueOf(mutable);
		final GenericMutableDecimal<?> mutableCloneII = mutableCloneI.clone();
		final GenericMutableDecimal<?> mutableCloneMM = mutableCloneM.clone();

		// then
		assertBase(immutableCloneI);
		assertBase(immutableCloneM);
		assertBase(mutableCloneI);
		assertBase(mutableCloneM);
		assertBase(mutableCloneII);
		assertBase(mutableCloneMM);
		assertEquals("immutableCloneI should be equal to immutable", immutable, immutableCloneI);
		assertEquals("immutableCloneM should be equal to mutable", mutable, immutableCloneM);
		assertEquals("mutableCloneI should be equal to immutable", immutable, mutableCloneI);
		assertEquals("mutableCloneM should be equal to mutable", mutable, mutableCloneM);
		assertEquals("mutableCloneII should be equal to immutable", immutable, mutableCloneII);
		assertEquals("mutableCloneMM should be equal to mutable", mutable, mutableCloneMM);
	}

	@Test
	public void shouldScaleGenericValue() {
		// given
		final GenericImmutableDecimal<?> immutable = GenericImmutableDecimal.valueOfUnscaled(scaleMetrics,
				RND.nextLong());
		final GenericMutableDecimal<?> mutable = GenericMutableDecimal.valueOfUnscaled(scaleMetrics, RND.nextLong());
		final int scaleI = RND.nextInt(Scales.MAX_SCALE + 1);
		final int scaleM = RND.nextInt(Scales.MAX_SCALE + 1);

		// when
		final Decimal<?> scaledI = immutable.scale(scaleI);
		final Decimal<?> scaledM = mutable.scale(scaleM);

		// then
		assertBase(Scales.getScaleMetrics(scaleI), scaledI);
		assertBase(Scales.getScaleMetrics(scaleM), scaledM);
		assertThat("should be instance of GenericImmutableDecimal", scaledI,
				CoreMatchers.instanceOf(GenericImmutableDecimal.class));
		assertThat("should be instance of GenericMutableDecimal", scaledM,
				CoreMatchers.instanceOf(GenericMutableDecimal.class));
	}

	@Test
	public void shouldMultiplyExactToGenericValue() {
		// given
		final GenericImmutableDecimal<?> immutable = GenericImmutableDecimal.valueOfUnscaled(scaleMetrics,
				RND.nextInt());
		final GenericMutableDecimal<?> mutable = GenericMutableDecimal.valueOfUnscaled(scaleMetrics, RND.nextInt());
		final int scaleI = RND.nextInt(18 - immutable.getScale() + 1);
		final int scaleM = RND.nextInt(18 - mutable.getScale() + 1);
		final GenericImmutableDecimal<?> factorI = GenericImmutableDecimal.valueOfUnscaled(scaleI, RND.nextInt());
		final GenericMutableDecimal<?> factorM = GenericMutableDecimal.valueOfUnscaled(scaleM, RND.nextInt());

		// when
		final Decimal<?> productI = immutable.multiplyExact(factorI);
		final Decimal<?> productM = mutable.multiplyExact(factorM);

		// then
		assertBase(Scales.getScaleMetrics(scaleMetrics.getScale() + scaleI), productI);
		assertBase(Scales.getScaleMetrics(scaleMetrics.getScale() + scaleM), productM);
		assertThat("should be instance of GenericImmutableDecimal", productI,
				CoreMatchers.instanceOf(GenericImmutableDecimal.class));
		assertThat("should be instance of GenericMutableDecimal", productM,
				CoreMatchers.instanceOf(GenericMutableDecimal.class));
		assertEquals("productI should be sum of factor scales", scaleMetrics.getScale() + scaleI, productI.getScale());
		assertEquals("productM should be sum of factor scales", scaleMetrics.getScale() + scaleM, productM.getScale());
		assertEquals("productI should be long product of factors", immutable.unscaledValue() * factorI.unscaledValue(), productI.unscaledValue());
		assertEquals("productM should be long product of factors", mutable.unscaledValue() * factorM.unscaledValue(), productM.unscaledValue());
	}

	private void assertBase(Decimal<?> decimal) {
		assertBase(scaleMetrics, decimal);
	}

	private static void assertBase(ScaleMetrics scaleMetrics, Decimal<?> decimal) {
		assertNotNull("decimal should not be null", decimal);
		assertEquals("decimal should have scale " + scaleMetrics.getScale(), scaleMetrics.getScale(),
				decimal.getScale());
		assertSame("decimal should have default scale metrics instance" + scaleMetrics.getScale(), scaleMetrics,
				decimal.getScaleMetrics());
		assertSame("decimal should have default factory instance", Factories.getGenericDecimalFactory(scaleMetrics),
				decimal.getFactory());
	}

}
