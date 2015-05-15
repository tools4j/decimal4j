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
package org.decimal4j.op.convert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.arithmetic.JDKSupport;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.op.AbstractFromToTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for {@link DecimalFactory#valueOf(BigInteger)}, {@link MutableDecimal#set(BigInteger)} 
 * and indirectly also the static {@code valueOf(..)} method of the immutable Decimal.
 */
@RunWith(Parameterized.class)
public class BigIntegerFromToTest extends AbstractFromToTest<BigInteger> {

	public BigIntegerFromToTest(ScaleMetrics s, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Override
	protected BigInteger randomValue(ScaleMetrics scaleMetrics) {
		if (RND.nextInt(10) != 0) {
			return BigInteger.valueOf(randomLongOrInt());
		}
		//every tenth potentially an overflow
		final byte[] bytes = new byte[1 + RND.nextInt(100)];
		RND.nextBytes(bytes);
		return new BigInteger(bytes);
	}

	@Parameters(name = "{index}: {0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			data.add(new Object[] {s, s.getRoundingDownArithmetic()});
		}
		return data;
	}

	@Override
	protected BigInteger[] specialValues(ScaleMetrics scaleMetrics) {
		final long[] specials = TestSettings.TEST_CASES.getSpecialValuesFor(scaleMetrics);
		final Set<BigInteger> set = new TreeSet<BigInteger>();
		for (int i = 0; i < specials.length; i++) {
			set.add(BigInteger.valueOf(specials[i]));
		}
		//add two non-long values
		set.add(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE));
		set.add(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE));
		return set.toArray(new BigInteger[set.size()]);
	}

	@Override
	protected <S extends ScaleMetrics> BigInteger expectedResult(S scaleMetrics, BigInteger value) {
		final long lvalue = JDKSupport.bigIntegerToLongValueExact(value);
		if (scaleMetrics.getMinIntegerValue() <= lvalue & lvalue <= scaleMetrics.getMaxIntegerValue()) {
			return value;
		}
		throw new ArithmeticException("overflow for " + scaleMetrics + " with value " + value);
	}
	@Override
	protected <S extends ScaleMetrics> BigInteger actualResult(DecimalFactory<S> factory, BigInteger value) {
		final Decimal<S> decimal = RND.nextBoolean() ? factory.valueOf(value) : factory.newMutable().set(value);
		return RND.nextBoolean() ? decimal.toBigInteger() : decimal.toBigIntegerExact();
	}
	
}
