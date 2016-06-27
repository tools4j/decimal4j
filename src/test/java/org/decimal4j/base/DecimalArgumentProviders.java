/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 decimal4j (tools4j), Marco Terzer
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;

/**
 * Defines several providers those supply {@link Decimal} arguments used in parameterized unit
 * tests.
 *
 */
class DecimalArgumentProviders {

  private static final Random RND = new Random();

  private DecimalArgumentProviders() {
    // no instances
  }

  public static class UnaryDecimalArgumentProvider {
    public static Object[] provideInput() {
      final List<Object[]> data = new ArrayList<Object[]>();

      Decimal<ScaleMetrics> decimal;
      for (final ScaleMetrics s : TestSettings.SCALES) {
        decimal = newDecimal(s, RND.nextLong());

        data.add(new Object[] {decimal});
        data.add(new Object[] {decimal.toMutableDecimal()});
      }
      return data.toArray();
    }
  }

  public static class BinaryDecimalArgumentProvider {
    public static Object[] provideInput() {
      final List<Object[]> data = new ArrayList<Object[]>();

      long random;
      Decimal<ScaleMetrics> firstDecimal, secondDecimal;

      for (final ScaleMetrics s : TestSettings.SCALES) {
        random = RND.nextLong();

        firstDecimal = newDecimal(s, random);
        secondDecimal = newDecimal(s, random);

        data.add(new Object[] {firstDecimal, secondDecimal});
        data.add(new Object[] {firstDecimal, firstDecimal.toMutableDecimal()});
        data.add(new Object[] {firstDecimal, secondDecimal.toMutableDecimal()});
        data.add(new Object[] {firstDecimal.toMutableDecimal(), secondDecimal.toMutableDecimal()});
        data.add(new Object[] {secondDecimal, secondDecimal.toMutableDecimal()});
        data.add(new Object[] {secondDecimal, firstDecimal.toMutableDecimal()});
      }

      return data.toArray();
    }
  }

  public static class TernaryDecimalArgumentProvider {
    public static Object[] provideInput() {
      final List<Object[]> data = new ArrayList<Object[]>();

      long random;
      Decimal<ScaleMetrics> firstDecimal, secondDecimal, thirdDecimal;

      for (final ScaleMetrics s : TestSettings.SCALES) {
        random = RND.nextLong();

        firstDecimal = newDecimal(s, random);
        secondDecimal = newDecimal(s, random);
        thirdDecimal = newDecimal(s, random);

        data.add(new Object[] {firstDecimal, secondDecimal, thirdDecimal});
        data.add(new Object[] {firstDecimal, secondDecimal, thirdDecimal.toMutableDecimal()});
      }

      return data.toArray();
    }
  }

  static Decimal<ScaleMetrics> newDecimal(final ScaleMetrics scaleMetrics, final long unscaled) {
    return Factories.getDecimalFactory(scaleMetrics).valueOfUnscaled(unscaled);
  }

}
