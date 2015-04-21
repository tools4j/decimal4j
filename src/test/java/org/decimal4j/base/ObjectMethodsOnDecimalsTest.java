package org.decimal4j.base;

import static com.google.common.truth.Truth.ASSERT;
import static org.decimal4j.base.DecimalArgumentProviders.newDecimal;
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
 * Unit test for proving that {@link Decimal#equals(Object)} obeys the contract of
 * {code}equals{code}.
 * <p>
 * Note: equals is implemented in {@link AbstractDecimal} class which has mutable and immutable
 * extensions.
 *
 */
@RunWith(JUnitParamsRunner.class)
public class ObjectMethodsOnDecimalsTest {

  // Test cases for equals

  /**
   * Checks the reflexivity requirement of {@link Object#equals(Object)} in case of different
   * {@link Decimal} implementations.
   */
  @Test
  @Parameters(source = UnaryDecimalArgumentProvider.class)
  @TestCaseName("reflexivity check: {0}")
  public void equalsIsReflexive(final Decimal<ScaleMetrics> first) {
    ASSERT.that(first).isEqualTo(first);
  }

  /**
   * Checks the symmetry requirement of {@link Object#equals(Object)} in case of different
   * {@link Decimal} implementations.
   */
  @Test
  @Parameters(source = BinaryDecimalArgumentProvider.class)
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
  @Parameters(source = TernaryDecimalArgumentProvider.class)
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
  @Parameters(source = UnaryDecimalArgumentProvider.class)
  @TestCaseName("equals is null-safe: {0}")
  public void equalsIsNullSafe(final Decimal<ScaleMetrics> first) {
    // given
    ASSERT.that(first).isNotNull();

    // then
    ASSERT.that(first).isNotEqualTo(null);
  }

  @Test
  @Parameters(source = UnaryDecimalArgumentProvider.class)
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

  // Test cases for hashCode

  @Test
  @Parameters(source = BinaryDecimalArgumentProvider.class)
  @TestCaseName("hashCode check: [{0}, {1}]")
  public void equalDecimalsHaveEqualHashCodes(final Decimal<ScaleMetrics> first,
      final Decimal<ScaleMetrics> second) {
    // given
    ASSERT.that(first).isEqualTo(second);

    // when
    final int hashCodeFirst = first.hashCode();
    final int hashCodeSecond = second.hashCode();

    // then
    ASSERT.that(hashCodeFirst).isEqualTo(hashCodeSecond);
  }

  // Test cases for toString
  // NOTE: Decimal#toString does not have a fixed format to parse/check, so this case just checks,
  // that it is overridden, thus equal decimals must have the same string representation

  @Test
  @Parameters(source = BinaryDecimalArgumentProvider.class)
  @TestCaseName("toString() check: [{0}, {1}]")
  public void equalDecimalsHaveSameStringRepresentation(final Decimal<ScaleMetrics> first,
      final Decimal<ScaleMetrics> second) {
    // given
    ASSERT.that(first).isEqualTo(second);

    // when
    final String stringFirst = first.toString();
    final String stringSecond = second.toString();

    // then
    ASSERT.that(stringFirst).isEqualTo(stringSecond);
  }

}
