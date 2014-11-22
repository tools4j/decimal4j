package ch.javasoft.decimal.test;

import java.util.Arrays;

/**
 * Test settings for unit tests, defines the scales to test and the size of the
 * test case sets. Can be controlled via system properties during the build.
 */
public class TestSettings {

	public static final String SYSTEM_PROPERTY_TEST_VARIANT = "testVariant";
	public static final String SYSTEM_PROPERTY_TEST_SCALES = "testScales";
	public static final String SYSTEM_PROPERTY_TEST_CASES = "testCases";

	public static TestScales getTestScales() {
		final String testVariant = System.getProperty(SYSTEM_PROPERTY_TEST_VARIANT, TestScales.STANDARD.name());
		final String testScales = System.getProperty(SYSTEM_PROPERTY_TEST_SCALES, testVariant);
		try {
			return TestScales.valueOf(testScales);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("illegal system property for test scales, must be one of " + Arrays.toString(TestScales.values()) + " but was <" + testScales + ">");
		}
	}

	public static TestCases getTestCases() {
		final String testVariant = System.getProperty(SYSTEM_PROPERTY_TEST_VARIANT, TestCases.STANDARD.name());
//		final String testVariant = System.getProperty(SYSTEM_PROPERTY_TEST_EXTENT, TestCases.SMALL.name());
		final String testCases = System.getProperty(SYSTEM_PROPERTY_TEST_CASES, testVariant);
		try {
			return TestCases.valueOf(testCases);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("illegal system property for test cases, must be one of " + Arrays.toString(TestCases.values()) + " but was <" + testCases + ">");
		}
	}
	
	public static TestTruncationPolicies getTestTruncationPolicies() {
		final String testVariant = System.getProperty(SYSTEM_PROPERTY_TEST_VARIANT, TestCases.STANDARD.name());
		try {
			return TestTruncationPolicies.valueOf(testVariant);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("illegal system property for test truncation policies, must be one of " + Arrays.toString(TestTruncationPolicies.values()) + " but was <" + testVariant + ">");
		}
	}
}
