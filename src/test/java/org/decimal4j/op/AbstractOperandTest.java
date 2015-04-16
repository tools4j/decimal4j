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
package org.decimal4j.op;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.Test;

/**
 * Base class for tests of operands with random and special values. The class
 * provides also some helper methods for subclasses comparing the result of an
 * operation of the {@link Decimal} with the expected result produced by the
 * equivalent operation of the {@link BigDecimal}.
 */
abstract public class AbstractOperandTest {

	protected static final Random RND = new Random();

	protected final DecimalArithmetic arithmetic;
	protected final MathContext mathContextLong64;
	protected final MathContext mathContextLong128;

	/**
	 * Constructor with arithemtics determining scale, rounding mode and
	 * overflow policy.
	 * 
	 * @param arithmetic
	 *            the arithmetic determining scale, rounding mode and overlfow
	 *            policy
	 */
	public AbstractOperandTest(DecimalArithmetic arithmetic) {
		this.arithmetic = arithmetic;
		this.mathContextLong64 = new MathContext(19, arithmetic.getRoundingMode());
		this.mathContextLong128 = new MathContext(39, arithmetic.getRoundingMode());
	}

	protected int getScale() {
		return arithmetic.getScale();
	}
	
	protected ScaleMetrics getScaleMetrics() {
		return arithmetic.getScaleMetrics();
	}

	protected TruncationPolicy getTruncationPolicy() {
		return arithmetic.getTruncationPolicy();
	}
	protected RoundingMode getRoundingMode() {
		return arithmetic.getRoundingMode();
	}
	protected OverflowMode getOverflowMode() {
		return arithmetic.getOverflowMode();
	}

	protected boolean isStandardTruncationPolicy() {
		return arithmetic.getRoundingMode() == TruncationPolicy.DEFAULT.getRoundingMode() && arithmetic.getOverflowMode() == TruncationPolicy.DEFAULT.getOverflowMode();
	}

	protected boolean isRoundingDown() {
		return arithmetic.getRoundingMode() == RoundingMode.DOWN;
	}
	protected boolean isRoundingDefault() {
		return arithmetic.getRoundingMode() == TruncationPolicy.DEFAULT.getRoundingMode();
	}
	protected boolean isUnchecked() {
		return !arithmetic.getOverflowMode().isChecked();
	}

	protected int getRandomTestCount() {
		return TestSettings.getRandomTestCount();
	}

	@Test
	public void runRandomTest() {
		final int n = getRandomTestCount();
		final ScaleMetrics scaleMetrics = arithmetic.getScaleMetrics();
		for (int i = 0; i < n; i++) {
			runRandomTest(scaleMetrics, i);
		}
	}

	@Test
	public void runSpecialValueTest() {
		final ScaleMetrics scaleMetrics = arithmetic.getScaleMetrics();
		runSpecialValueTest(scaleMetrics);
	}

	/**
	 * Returns the operation string, such as "+", "-", "*", "/", "abs" etc.
	 * 
	 * @return the operation string used in exceptions and log statements
	 */
	abstract protected String operation();

	abstract protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index);

	abstract protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics);

	protected long[] getSpecialValues(ScaleMetrics scaleMetrics) {
		return TestSettings.TEST_CASES.getSpecialValuesFor(scaleMetrics);
	}

	protected <S extends ScaleMetrics> Decimal<S> randomDecimal(S scaleMetrics) {
		final long unscaled = RND.nextBoolean() ? RND.nextLong() : RND.nextInt();
		return newDecimal(scaleMetrics, unscaled);
	}
	
	protected static long randomLong(long n) {
        if (n <= 0)
            throw new IllegalArgumentException("n must be positive, but was " + n);

        long bits, val;
        do {
            bits = RND.nextLong() >>> 1;
            val = bits % n;
        } while (bits - val + (n-1) < 0);
        return val;
	}

	protected <S extends ScaleMetrics> Decimal<S> newDecimal(S scaleMetrics, long unscaled) {
		return Factories.getDecimalFactory(scaleMetrics).valueOfUnscaled(unscaled);
	}

	protected static BigDecimal toBigDecimal(Decimal<?> decimal) {
		return decimal.toBigDecimal();
	}
}
