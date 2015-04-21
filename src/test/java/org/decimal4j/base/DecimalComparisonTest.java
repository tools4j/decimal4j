package org.decimal4j.base;

import static com.google.common.truth.Truth.ASSERT;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;

import org.decimal4j.api.Decimal;
import org.decimal4j.base.DecimalArgumentProviders.BinaryDecimalArgumentProvider;
import org.decimal4j.scale.ScaleMetrics;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Comparison tests for {@link Decimal} implementations.
 * 
 */
@RunWith(JUnitParamsRunner.class)
public class DecimalComparisonTest {

  // Test cases for compareTo

  /**
   * Checks that natural ordering given on {@link Decimal} implementations is consistent with
   * <code>equals<code>.
   * 
   * Note: (x.compareTo(y)==0) == (x.equals(y) is not strictly required by the contract, but {@link Decimal} implementations fulfill it
   */
  @Test
  @Parameters(source = BinaryDecimalArgumentProvider.class)
  @TestCaseName("{0}.compareTo({1}) == 0]")
  public void compareToIsConsistentWithEquals(final Decimal<ScaleMetrics> first,
      final Decimal<ScaleMetrics> second) {
    // given
    ASSERT.that(first).isEqualTo(second);

    // then
    ASSERT.that(first).comparesEqualTo(second);
  }

}
