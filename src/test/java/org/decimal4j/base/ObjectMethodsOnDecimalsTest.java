/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.base;

import static org.decimal4j.base.DecimalArgumentProviders.newDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;

import org.decimal4j.api.Decimal;
import org.decimal4j.base.DecimalArgumentProviders.BinaryDecimalArgumentProvider;
import org.decimal4j.base.DecimalArgumentProviders.TernaryDecimalArgumentProvider;
import org.decimal4j.base.DecimalArgumentProviders.UnaryDecimalArgumentProvider;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for proving that {@link Decimal#equals(Object)} obeys the contract
 * of {code}equals{code}.
 * <p>
 * Note: equals is implemented in {@link AbstractDecimal} class which has
 * mutable and immutable extensions.
 *
 */
@RunWith(JUnitParamsRunner.class)
public class ObjectMethodsOnDecimalsTest {

	// Test cases for equals

	/**
	 * Checks the reflexivity requirement of {@link Object#equals(Object)} in
	 * case of different {@link Decimal} implementations.
	 * 
	 * @param first the first argument of the equality comparison with itself
	 */
	@Test
	@Parameters(source = UnaryDecimalArgumentProvider.class)
	@TestCaseName("reflexivity check: {0}")
	public void equalsIsReflexive(final Decimal<ScaleMetrics> first) {
		assertEquals(first, first);
	}

	/**
	 * Checks the symmetry requirement of {@link Object#equals(Object)} in case
	 * of different {@link Decimal} implementations.
	 * 
	 * @param first the first argument of the equality comparison
	 * @param second the second argument of the equality comparison
	 */
	@Test
	@Parameters(source = BinaryDecimalArgumentProvider.class)
	@TestCaseName("symmetry check: [{0}, {1}]")
	public void equalsIsSymmetric(final Decimal<ScaleMetrics> first,
			final Decimal<ScaleMetrics> second) {
		// given
		assertEquals(first, second);

		// then
		assertEquals(second, first);
	}

	/**
	 * Checks the transitivity requirement of {@link Object#equals(Object)} in
	 * case of different {@link Decimal} implementations.
	 * 
	 * @param first the first argument of the equality comparison
	 * @param second the second argument of the equality comparison
	 * @param third the third argument of the equality comparison
	 */
	@Test
	@Parameters(source = TernaryDecimalArgumentProvider.class)
	@TestCaseName("transitivity check: [{0}, {1}, {2}]")
	public void equalsIsTransitive(final Decimal<ScaleMetrics> first,
			final Decimal<ScaleMetrics> second,
			final Decimal<ScaleMetrics> third) {
		// given
		assertEquals(first, second);
		assertEquals(second, third);

		// then
		assertEquals(first, third);
	}

	@Test
	@Parameters(source = UnaryDecimalArgumentProvider.class)
	@TestCaseName("equals is null-safe: {0}")
	public void equalsIsNullSafe(final Decimal<ScaleMetrics> first) {
		// given
		assertNotNull(first);

		// then
		assertNotEquals(first, null);
	}

	@Test
	@Parameters(source = UnaryDecimalArgumentProvider.class)
	@TestCaseName("non-decimal test: {0}")
	public void isNotEqualToNonDecimalObjects(final Decimal<ScaleMetrics> first) {
		// given
		final Object nonDecimalObj = new Object();

		// then
		assertNotEquals(first, nonDecimalObj);
	}

	@Test
	public void isNotEqualToDecimalHavingDifferentScale() {
		// given
		final long unscaled = 123;

		final Decimal<ScaleMetrics> first = newDecimal(
				Scales.getScaleMetrics(0), unscaled);
		final Decimal<ScaleMetrics> second = newDecimal(
				Scales.getScaleMetrics(5), unscaled);

		// then
		assertNotEquals(first, second);
	}

	@Test
	public void isNotEqualToDecimalHavingDifferentValue() {
		// given
		final long unscaled = 123;
		final int scale = 2;

		final Decimal<ScaleMetrics> first = newDecimal(
				Scales.getScaleMetrics(scale), unscaled);
		final Decimal<ScaleMetrics> second = newDecimal(
				Scales.getScaleMetrics(scale), 2 * unscaled);

		// then
		assertNotEquals(first, second);
	}

	// Test cases for hashCode

	@Test
	@Parameters(source = BinaryDecimalArgumentProvider.class)
	@TestCaseName("hashCode check: [{0}, {1}]")
	public void equalDecimalsHaveEqualHashCodes(
			final Decimal<ScaleMetrics> first,
			final Decimal<ScaleMetrics> second) {
		// given
		assertEquals(first, second);

		// when
		final int hashCodeFirst = first.hashCode();
		final int hashCodeSecond = second.hashCode();

		// then
		assertEquals(hashCodeFirst, hashCodeSecond);
	}

	// Test cases for toString
	// NOTE: Decimal#toString does not have a fixed format to parse/check, so
	// this case just checks, that it is overridden, thus equal decimals must
	// have the same string representation

	@Test
	@Parameters(source = BinaryDecimalArgumentProvider.class)
	@TestCaseName("toString() check: [{0}, {1}]")
	public void equalDecimalsHaveSameStringRepresentation(
			final Decimal<ScaleMetrics> first,
			final Decimal<ScaleMetrics> second) {
		// given
		assertEquals(first, second);

		// when
		final String stringFirst = first.toString();
		final String stringSecond = second.toString();

		// then
		assertEquals(stringFirst, stringSecond);
	}

}
