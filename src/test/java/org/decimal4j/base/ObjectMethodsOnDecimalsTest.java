package org.decimal4j.base;

import static com.google.common.truth.Truth.ASSERT;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factories;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.test.TestSettings;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test for proving that {@link Decimal#equals(Object)} obeys the contract of
 * {code}equals{code}.
 * <p>
 * Note: equals is implemented in {@link AbstractDecimal} class which has mutable and immutable
 * extensions.
 *
 */
@RunWith(JUnitParamsRunner.class)
public class ObjectMethodsOnDecimalsTest {

  private static final Random RND = new Random();

  // Test cases for equals

  /**
   * Checks the reflexivity requirement of {@link Object#equals(Object)} in case of different
   * {@link Decimal} implementations.
   */
  @Test
  @Parameters(source = ParameterProvider.class, method = "provideInputForReflexivityCheck")
  @TestCaseName("reflexivity check: {0}")
  public void equalsIsReflexive(final Decimal<ScaleMetrics> first) {
    ASSERT.that(first).isEqualTo(first);
  }

  /**
   * Checks the symmetry requirement of {@link Object#equals(Object)} in case of different
   * {@link Decimal} implementations.
   */
  @Test
  @Parameters(source = ParameterProvider.class, method = "provideInputForSymmetryCheck")
  @TestCaseName("symmetry check: [{0}, {1}]")
  public void equalsIsSymmetric(final Decimal<ScaleMetrics> first,
      final Decimal<ScaleMetrics> second) {
    // given
    ASSERT.that(first).isEqualTo(second);

    // then
    ASSERT.that(second).isEqualTo(first);
  }

  /**
   * Checks the transitivity requirement of {@link Object#equals(Object)} in case of different
   * {@link Decimal} implementations.
   */
  @Test
  @Parameters(source = ParameterProvider.class, method = "provideInputForTransitivityCheck")
  @TestCaseName("transitivity check: [{0}, {1}, {2}]")
  public void equalsIsTransitive(final Decimal<ScaleMetrics> first,
      final Decimal<ScaleMetrics> second, final Decimal<ScaleMetrics> third) {
    // given
    ASSERT.that(first).isEqualTo(second);
    ASSERT.that(second).isEqualTo(third);

    // then
    ASSERT.that(first).isEqualTo(third);
  }

  @Test
  @Parameters(source = ParameterProvider.class, method = "provideInputForReflexivityCheck")
  @TestCaseName("equals is null-safe: {0}")
  public void equalsIsNullSafe(final Decimal<ScaleMetrics> first) {
    // given
    ASSERT.that(first).isNotNull();

    // then
    ASSERT.that(first).isNotEqualTo(null);
  }

  @Test
  @Parameters(source = ParameterProvider.class, method = "provideInputForReflexivityCheck")
  @TestCaseName("non-decimal test: {0}")
  public void isNotEqualToNonDecimalObjects(final Decimal<ScaleMetrics> first) {
    // given
    final Object nonDecimalObj = new Object();

    // then
    ASSERT.that(first).isNotEqualTo(nonDecimalObj);
  }

  @Test
  public void isNotEqualToDecimalHavingDifferentScale() {
    // given
    final long unscaled = 123;

    final Decimal<ScaleMetrics> first = newDecimal(Scales.getScaleMetrics(0), unscaled);
    final Decimal<ScaleMetrics> second = newDecimal(Scales.getScaleMetrics(5), unscaled);

    // then
    ASSERT.that(first).isNotEqualTo(second);
  }

  @Test
  public void isNotEqualToDecimalHavingDifferentValue() {
    // given
    final long unscaled = 123;
    final int scale = 2;

    final Decimal<ScaleMetrics> first = newDecimal(Scales.getScaleMetrics(scale), unscaled);
    final Decimal<ScaleMetrics> second = newDecimal(Scales.getScaleMetrics(scale), 2 * unscaled);

    // then
    ASSERT.that(first).isNotEqualTo(second);
  }

  // Helpers

  public static class ParameterProvider {
    public static Object[] provideInputForReflexivityCheck() {
      final List<Object[]> data = new ArrayList<Object[]>();

      Decimal<ScaleMetrics> decimal;
      for (final ScaleMetrics s : TestSettings.SCALES) {
        decimal = newDecimal(s, RND.nextLong());

        data.add(new Object[] {decimal});
        data.add(new Object[] {decimal.toMutableDecimal()});
      }
      return data.toArray();
    }

    public static Object[] provideInputForSymmetryCheck() {
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

    public static Object[] provideInputForTransitivityCheck() {
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

  private static Decimal<ScaleMetrics> newDecimal(final ScaleMetrics scaleMetrics,
      final long unscaled) {
    return Factories.getDecimalFactory(scaleMetrics).valueOfUnscaled(unscaled);
  }

}
