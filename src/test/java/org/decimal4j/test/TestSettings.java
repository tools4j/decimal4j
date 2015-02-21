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

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * Test settings for unit tests, defines the scales to test and the size of the
 * test case sets. Can be controlled via system properties during the build.
 */
public class TestSettings {

	public static final String SYSTEM_PROPERTY_TEST_VARIANT = "testVariant";
	public static final String SYSTEM_PROPERTY_TEST_SCALES = "testScales";
	public static final String SYSTEM_PROPERTY_TEST_CASES = "testCases";

	public static final TestScales TEST_SCALES = getTestScales(); 
	public static final TestTruncationPolicies TEST_POLICIES = getTestTruncationPolicies();
	public static final TestCases TEST_CASES = getTestCases();

	public static final List<ScaleMetrics> SCALES = TestSettings.TEST_SCALES.getScales();
	public static final Collection<TruncationPolicy> POLICIES = TestSettings.TEST_POLICIES.getPolicies();
	public static final Set<RoundingMode> UNCHECKED_ROUNDING_MODES = TestSettings.TEST_POLICIES.getUncheckedRoundingModes();

	public static int getRandomTestCount() {
		switch (TEST_CASES) {
		case ALL:
			return 10000;
		case LARGE:
			return 10000;
		case STANDARD:
			return 1000;
		case SMALL:
			return 1000;
		case TINY:
			return 100;
		default:
			throw new RuntimeException("unsupported: " + TestSettings.TEST_CASES);
		}
	}

	private static TestScales getTestScales() {
		final String testVariant = System.getProperty(SYSTEM_PROPERTY_TEST_VARIANT, TestScales.STANDARD.name());
		final String testScales = System.getProperty(SYSTEM_PROPERTY_TEST_SCALES, testVariant);
		try {
			return TestScales.valueOf(testScales);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("illegal system property for test scales, must be one of " + Arrays.toString(TestScales.values()) + " but was <" + testScales + ">");
		}
	}

	private static TestCases getTestCases() {
		final String testVariant = System.getProperty(SYSTEM_PROPERTY_TEST_VARIANT, TestCases.STANDARD.name());
		final String testCases = System.getProperty(SYSTEM_PROPERTY_TEST_CASES, testVariant);
		try {
			return TestCases.valueOf(testCases);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("illegal system property for test cases, must be one of " + Arrays.toString(TestCases.values()) + " but was <" + testCases + ">");
		}
	}
	
	private static TestTruncationPolicies getTestTruncationPolicies() {
		final String testVariant = System.getProperty(SYSTEM_PROPERTY_TEST_VARIANT, TestCases.STANDARD.name());
		try {
			return TestTruncationPolicies.valueOf(testVariant);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("illegal system property for test truncation policies, must be one of " + Arrays.toString(TestTruncationPolicies.values()) + " but was <" + testVariant + ">");
		}
	}
}
