package ch.javasoft.decimal.test;

import java.util.Arrays;

/**
 * Test settings for unit tests, defines the scales to test and the size of the
 * test case sets. Can be controlled via system properties during the build.
 */
public class TestSettings {

	public static final String SYSTEM_PROPERTY_TEST_EXTENT = "testExtent";
	public static final String SYSTEM_PROPERTY_TEST_SCALES = "testScales";
	public static final String SYSTEM_PROPERTY_TEST_CASES = "testCases";

	public static TestScales getTestScales() {
		final String testExtent = System.getProperty(SYSTEM_PROPERTY_TEST_EXTENT, TestScales.STANDARD.name());
		final String testScales = System.getProperty(SYSTEM_PROPERTY_TEST_SCALES, testExtent);
		try {
			return TestScales.valueOf(testScales);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("illegal system property for test scales, must be one of " + Arrays.toString(TestScales.values()) + " but was <" + testScales + ">");
		}
	}

	public static TestCases getTestCases() {
		final String testExtent = System.getProperty(SYSTEM_PROPERTY_TEST_EXTENT, TestCases.STANDARD.name());
//		final String testExtent = System.getProperty(SYSTEM_PROPERTY_TEST_EXTENT, TestCases.SMALL.name());
		final String testCases = System.getProperty(SYSTEM_PROPERTY_TEST_CASES, testExtent);
		try {
			return TestCases.valueOf(testCases);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("illegal system property for test cases, must be one of " + Arrays.toString(TestCases.values()) + " but was <" + testCases + ">");
		}
	}
	
	public static TestTruncationPolicies getTestTruncationPolicies() {
		final String testExtent = System.getProperty(SYSTEM_PROPERTY_TEST_EXTENT, TestCases.STANDARD.name());
		try {
			return TestTruncationPolicies.valueOf(testExtent);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("illegal system property for test truncation policies, must be one of " + Arrays.toString(TestTruncationPolicies.values()) + " but was <" + testExtent + ">");
		}
	}
}
