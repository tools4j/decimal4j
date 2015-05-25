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
package org.decimal4j.op.compare;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.test.AbstractDecimalTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for Decimal constants and constant setter methods of the
 * {@link MutableDecimal}.
 */
@RunWith(Parameterized.class)
public class ConstantTest extends AbstractDecimalTest {

	public ConstantTest(ScaleMetrics scaleMetrics, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: scale={0}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : Scales.VALUES) {
			data.add(new Object[] { s, s.getDefaultArithmetic() });
		}
		return data;
	}
	
	@Test
	public void testZero() {
		assertUnscaled("should be zero", 0, immutableConstant("ZERO"));
		assertUnscaled("should be zero", 0, immutableValueOf(long.class, 0L));
		assertUnscaled("should be zero", 0, mutableConstant("zero"));
		assertUnscaled("should be zero", 0, newMutable().setZero());
	}
	@Test
	public void testOne() {
		final long one = arithmetic.one();
		assertUnscaled("should be one", one, immutableConstant("ONE"));
		assertUnscaled("should be one", one, immutableValueOf(long.class, 1L));
		assertUnscaled("should be one", one, mutableConstant("one"));
		assertUnscaled("should be one", one, newMutable().setOne());
	}
	@Test
	public void testTwo() {
		final long two = 2*arithmetic.one();
		assertUnscaled("should be two", two, immutableConstant("TWO"));
		assertUnscaled("should be two", two, immutableValueOf(long.class, 2L));
		assertUnscaled("should be two", two, mutableConstant("two"));
		assertUnscaled("should be two", two, newMutable().set(2));
	}
	@Test
	public void testThree() {
		final long three = 3*arithmetic.one();
		assertUnscaled("should be three", three, immutableConstant("THREE"));
		assertUnscaled("should be three", three, immutableValueOf(long.class, 3L));
		assertUnscaled("should be three", three, mutableConstant("three"));
		assertUnscaled("should be three", three, newMutable().set(3));
	}
	@Test
	public void testFour() {
		final long four = 4*arithmetic.one();
		assertUnscaled("should be four", four, immutableConstant("FOUR"));
		assertUnscaled("should be four", four, immutableValueOf(long.class, 4L));
		assertUnscaled("should be four", four, mutableConstant("four"));
		assertUnscaled("should be four", four, newMutable().set(4));
	}
	@Test
	public void testFive() {
		final long five = 5*arithmetic.one();
		assertUnscaled("should be five", five, immutableConstant("FIVE"));
		assertUnscaled("should be five", five, immutableValueOf(long.class, 5L));
		assertUnscaled("should be five", five, mutableConstant("five"));
		assertUnscaled("should be five", five, newMutable().set(5));
	}
	@Test
	public void testSix() {
		final long six = 6*arithmetic.one();
		assertUnscaled("should be six", six, immutableConstant("SIX"));
		assertUnscaled("should be six", six, immutableValueOf(long.class, 6L));
		assertUnscaled("should be six", six, mutableConstant("six"));
		assertUnscaled("should be six", six, newMutable().set(6));
	}
	@Test
	public void testSeven() {
		final long seven = 7*arithmetic.one();
		assertUnscaled("should be seven", seven, immutableConstant("SEVEN"));
		assertUnscaled("should be seven", seven, immutableValueOf(long.class, 7L));
		assertUnscaled("should be seven", seven, mutableConstant("seven"));
		assertUnscaled("should be seven", seven, newMutable().set(7));
	}
	@Test
	public void testEight() {
		final long eight = 8*arithmetic.one();
		assertUnscaled("should be eight", eight, immutableConstant("EIGHT"));
		assertUnscaled("should be eight", eight, immutableValueOf(long.class, 8L));
		assertUnscaled("should be eight", eight, mutableConstant("eight"));
		assertUnscaled("should be eight", eight, newMutable().set(8));
	}
	@Test
	public void testNine() {
		final long nine = 9*arithmetic.one();
		assertUnscaled("should be nine", nine, immutableConstant("NINE"));
		assertUnscaled("should be nine", nine, immutableValueOf(long.class, 9L));
		assertUnscaled("should be nine", nine, mutableConstant("nine"));
		assertUnscaled("should be nine", nine, newMutable().set(9));
	}
	@Test
	public void testUlp() {
		assertUnscaled("should be 1", 1, immutableConstant("ULP"));
		assertUnscaled("should be 1", 1, mutableConstant("ulp"));
		assertUnscaled("should be 1", 1, newMutable().setUlp());
	}
	@Test
	public void testMinusOne() {
		final long minusOne = -arithmetic.one();
		assertUnscaled("should be minus one", minusOne, immutableConstant("MINUS_ONE"));
		assertUnscaled("should be minus one", minusOne, mutableConstant("minusOne"));
		assertUnscaled("should be minus one", minusOne, newMutable().setMinusOne());
	}
	@Test
	public void testTen() {
		if (getScale() <= 17) {
			final long ten = 10*arithmetic.one();
			assertUnscaled("should be ten", ten, immutableConstant("TEN"));
			assertUnscaled("should be ten", ten, mutableConstant("ten"));
		}
	}
	@Test
	public void testHundred() {
		if (getScale() <= 16) {
			final long ten = 100*arithmetic.one();
			assertUnscaled("should be hundred", ten, immutableConstant("HUNDRED"));
			assertUnscaled("should be hundred", ten, mutableConstant("hundred"));
		}
	}
	@Test
	public void testThousand() {
		if (getScale() <= 15) {
			final long ten = 1000*arithmetic.one();
			assertUnscaled("should be thousand", ten, immutableConstant("THOUSAND"));
			assertUnscaled("should be thousand", ten, mutableConstant("thousand"));
		}
	}
	@Test
	public void testMillion() {
		if (getScale() <= 12) {
			final long ten = 1000000*arithmetic.one();
			assertUnscaled("should be million", ten, immutableConstant("MILLION"));
			assertUnscaled("should be million", ten, mutableConstant("million"));
		}
	}
	@Test
	public void testBillion() {
		if (getScale() <= 9) {
			final long ten = 1000000000*arithmetic.one();
			assertUnscaled("should be billion", ten, immutableConstant("BILLION"));
			assertUnscaled("should be billion", ten, mutableConstant("billion"));
		}
	}
	@Test
	public void testTrillion() {
		if (getScale() <= 6) {
			final long ten = 1000000000000L*arithmetic.one();
			assertUnscaled("should be trillion", ten, immutableConstant("TRILLION"));
			assertUnscaled("should be trillion", ten, mutableConstant("trillion"));
		}
	}
	@Test
	public void testQuadrillion() {
		if (getScale() <= 3) {
			final long ten = 1000000000000000L*arithmetic.one();
			assertUnscaled("should be quadrillion", ten, immutableConstant("QUADRILLION"));
			assertUnscaled("should be quadrillion", ten, mutableConstant("quadrillion"));
		}
	}
	@Test
	public void testQuintillion() {
		if (getScale() <= 0) {
			final long ten = 1000000000000000000L*arithmetic.one();
			assertUnscaled("should be quintillion", ten, immutableConstant("QUINTILLION"));
			assertUnscaled("should be quintillion", ten, mutableConstant("quintillion"));
		}
	}
	@Test
	public void testHalf() {
		if (getScale() > 0) {
			final long half = arithmetic.one()/2;
			assertUnscaled("should be half", half, immutableConstant("HALF"));
			assertUnscaled("should be half", half, mutableConstant("half"));
		}
	}
	@Test
	public void testTenth() {
		if (getScale() >= 1) {
			final long tenth = arithmetic.one()/10;
			assertUnscaled("should be tenth", tenth, immutableConstant("TENTH"));
			assertUnscaled("should be tenth", tenth, mutableConstant("tenth"));
		}
	}
	@Test
	public void testHundredth() {
		if (getScale() >= 2) {
			final long hundredth = arithmetic.one()/100;
			assertUnscaled("should be hundredth", hundredth, immutableConstant("HUNDREDTH"));
			assertUnscaled("should be hundredth", hundredth, mutableConstant("hundredth"));
		}
	}
	@Test
	public void testThousandth() {
		if (getScale() >= 3) {
			final long thousanth = arithmetic.one()/1000;
			assertUnscaled("should be thousanth", thousanth, immutableConstant("THOUSANDTH"));
			assertUnscaled("should be thousanth", thousanth, mutableConstant("thousandth"));
		}
	}
	@Test
	public void testMillionth() {
		if (getScale() >= 6) {
			final long millionth = arithmetic.one()/1000000;
			assertUnscaled("should be millionth", millionth, immutableConstant("MILLIONTH"));
			assertUnscaled("should be millionth", millionth, mutableConstant("millionth"));
		}
	}
	@Test
	public void testBillionth() {
		if (getScale() >= 9) {
			final long billionth = arithmetic.one()/1000000000;
			assertUnscaled("should be billionth", billionth, immutableConstant("BILLIONTH"));
			assertUnscaled("should be billionth", billionth, mutableConstant("billionth"));
		}
	}
	@Test
	public void testTrillionth() {
		if (getScale() >= 12) {
			final long trillionth = arithmetic.one()/1000000000000L;
			assertUnscaled("should be trillionth", trillionth, immutableConstant("TRILLIONTH"));
			assertUnscaled("should be trillionth", trillionth, mutableConstant("trillionth"));
		}
	}
	@Test
	public void testQuadrillionth() {
		if (getScale() >= 15) {
			final long quadrillionth = arithmetic.one()/1000000000000000L;
			assertUnscaled("should be quadrillionth", quadrillionth, immutableConstant("QUADRILLIONTH"));
			assertUnscaled("should be quadrillionth", quadrillionth, mutableConstant("quadrillionth"));
		}
	}
	@Test
	public void testQuintillionth() {
		if (getScale() >= 18) {
			final long quintillionth = arithmetic.one()/1000000000000000000L;
			assertUnscaled("should be quintillionth", quintillionth, immutableConstant("QUINTILLIONTH"));
			assertUnscaled("should be quintillionth", quintillionth, mutableConstant("quintillionth"));
		}
	}
	

	private void assertUnscaled(String msg, long unscaled, Decimal<?> value) {
		assertEquals(msg, unscaled, value.unscaledValue());
	}

	private MutableDecimal<?> newMutable() {
		return getDecimalFactory(getScaleMetrics()).newMutable().setUnscaled(RND.nextLong());
	}

	private Decimal<?> immutableConstant(String constantName) {
		try {
			final Class<?> clazz = Class.forName(getImmutableClassName());
			return (Decimal<?>) clazz.getField(constantName).get(null);
		} catch (Exception e) {
			throw new RuntimeException("could not access static field '" + constantName + "', e=" + e, e);
		}
	}

	private Decimal<?> mutableConstant(String methodName) {
		try {
			final Class<?> clazz = Class.forName(getMutableClassName());
			return (Decimal<?>) clazz.getMethod(methodName).invoke(null);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException)e.getTargetException();
			}
			throw new RuntimeException("could not invoke static method '" + methodName + "', e=" + e, e);
		} catch (Exception e) {
			throw new RuntimeException("could not invoke static method '" + methodName + "', e=" + e, e);
		}
	}
	private <V> Decimal<?> immutableValueOf(Class<V> paramType, V paramValue) {
		try {
			final Class<?> clazz = Class.forName(getImmutableClassName());
			return (Decimal<?>) clazz.getMethod("valueOf", paramType).invoke(null, paramValue);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException)e.getTargetException();
			}
			throw new RuntimeException("could not invoke valueOf method, e=" + e, e);
		} catch (Exception e) {
			throw new RuntimeException("could not invoke valueOf method, e=" + e, e);
		}
	}

}
