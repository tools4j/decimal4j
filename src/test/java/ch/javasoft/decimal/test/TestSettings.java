package ch.javasoft.decimal.test;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.truncate.TruncationPolicy;

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
		case STANDARD:
			return 10000;
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
//		final String testVariant = System.getProperty(SYSTEM_PROPERTY_TEST_EXTENT, TestCases.SMALL.name());
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
