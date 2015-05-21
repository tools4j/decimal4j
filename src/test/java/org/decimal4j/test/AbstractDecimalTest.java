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
package org.decimal4j.test;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factories;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Base class for {@link Decimal} tests with random and special values.
 */
abstract public class AbstractDecimalTest {

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
	public AbstractDecimalTest(DecimalArithmetic arithmetic) {
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

	protected long[] getSpecialValues(ScaleMetrics scaleMetrics) {
		return TestSettings.TEST_CASES.getSpecialValuesFor(scaleMetrics);
	}

	protected <S extends ScaleMetrics> Decimal<S> randomDecimal(S scaleMetrics) {
		return newDecimal(scaleMetrics, randomLongOrInt());
	}

	protected static long randomLongOrInt() {
		return RND.nextBoolean() ? RND.nextLong() : RND.nextInt();
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
		switch (RND.nextInt(4)) {
		case 0:
			return Factories.getDecimalFactory(scaleMetrics).valueOfUnscaled(unscaled);
		case 1:
			return Factories.getDecimalFactory(scaleMetrics).newMutable().setUnscaled(unscaled);
		case 2:
			return Factories.getGenericDecimalFactory(scaleMetrics).valueOfUnscaled(unscaled);
		case 3:
			return Factories.getGenericDecimalFactory(scaleMetrics).newMutable().setUnscaled(unscaled);
		default:
			//should not get here
			throw new RuntimeException("random out of bounds");
		}
	}

	protected <S extends ScaleMetrics> DecimalFactory<S> getDecimalFactory(S scaleMetrics) {
		return RND.nextBoolean() ? Factories.getDecimalFactory(scaleMetrics) : Factories.getGenericDecimalFactory(scaleMetrics);
	}
	
	protected String getImmutableClassName() {
		return Decimal0f.class.getName().replace("0f", getScale() + "f");
	}

	protected String getMutableClassName() {
		return MutableDecimal0f.class.getName().replace("0f", getScale() + "f");
	}

}
