package com.automation.utils;

import io.restassured.response.Response;
import org.testng.Assert;

/**
 * Utility class providing reusable assertion helpers for API test validation.
 *
 * @author api-automation-framework
 * @version 1.0.0
 */
public final class AssertionUtils {

    private AssertionUtils() {
        // Utility class – prevent instantiation
    }

    /**
     * Asserts that the HTTP response status code matches the expected value.
     *
     * @param response     the {@link Response} object to validate
     * @param expectedCode the expected HTTP status code
     */
    public static void assertStatusCode(Response response, int expectedCode) {
        Assert.assertEquals(response.getStatusCode(), expectedCode,
                "Expected status code " + expectedCode + " but got " + response.getStatusCode());
    }

    /**
     * Asserts that the response body is not null or empty.
     *
     * @param response the {@link Response} object to validate
     */
    public static void assertBodyNotEmpty(Response response) {
        String body = response.getBody().asString();
        Assert.assertNotNull(body, "Response body must not be null");
        Assert.assertFalse(body.trim().isEmpty(), "Response body must not be empty");
    }

    /**
     * Asserts that a field value in the response body is not null or empty.
     *
     * @param fieldValue the value to validate
     * @param fieldName  the name of the field (used in the assertion message)
     */
    public static void assertFieldNotEmpty(Object fieldValue, String fieldName) {
        Assert.assertNotNull(fieldValue, fieldName + " must not be null");
        if (fieldValue instanceof String) {
            Assert.assertFalse(((String) fieldValue).trim().isEmpty(),
                    fieldName + " must not be empty");
        }
    }

    /**
     * Asserts that an integer field value is greater than zero.
     *
     * @param value     the integer value to validate
     * @param fieldName the name of the field (used in the assertion message)
     */
    public static void assertPositiveId(int value, String fieldName) {
        Assert.assertTrue(value > 0, fieldName + " must be a positive integer, but was " + value);
    }

    /**
     * Asserts that two integer values are equal.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion message
     */
    public static void assertIntEquals(int actual, int expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two String values are equal.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion message
     */
    public static void assertStringEquals(String actual, String expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }
}
