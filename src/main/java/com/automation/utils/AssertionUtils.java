package com.automation.utils;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.util.List;

/**
 * Static utility class providing reusable assertion helpers for API response validation.
 *
 * <p>All methods delegate to TestNG's {@link Assert} class after logging the assertion
 * context at the appropriate log level. On failure, {@link Assert} throws an
 * {@link AssertionError}, which TestNG catches and records as a test failure.</p>
 *
 * <p>This class is not instantiable; all members are {@code public static}.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * AssertionUtils.assertStatusCode(response, 200);
 * AssertionUtils.assertFieldNotNull(response, "id");
 * AssertionUtils.assertFieldEquals(response, "title", "My Title");
 * AssertionUtils.assertResponseTimeBelow(response, 5000);
 * }</pre>
 *
 * @author api-automation-framework
 * @version 1.0.0
 * @see ResponseValidator
 */
public final class AssertionUtils {

    private static final Logger logger = LogManager.getLogger(AssertionUtils.class);

    /**
     * Private constructor – prevents instantiation of this utility class.
     */
    private AssertionUtils() {}

    /**
     * Asserts that the HTTP status code of {@code response} equals {@code expectedStatusCode}.
     *
     * @param response           the response to inspect (must not be {@code null})
     * @param expectedStatusCode the expected HTTP status code (e.g. {@code 200}, {@code 201})
     * @throws AssertionError if the actual status code differs from {@code expectedStatusCode}
     */
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        logger.info("Asserting status code - Expected: {}, Actual: {}", expectedStatusCode, actualStatusCode);
        Assert.assertEquals(actualStatusCode, expectedStatusCode,
                "Status code mismatch. Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
    }

    /**
     * Asserts that the JSON field at {@code fieldPath} in the response body is not {@code null}.
     *
     * @param response  the response to inspect (must not be {@code null})
     * @param fieldPath a JsonPath expression identifying the field (e.g. {@code "data.id"})
     * @throws AssertionError if the field is absent or its value is {@code null}
     */
    public static void assertFieldNotNull(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        logger.info("Asserting field '{}' is not null", fieldPath);
        Assert.assertNotNull(value, "Field '" + fieldPath + "' should not be null");
    }

    /**
     * Asserts that the JSON field at {@code fieldPath} equals {@code expectedValue}.
     *
     * @param response      the response to inspect (must not be {@code null})
     * @param fieldPath     a JsonPath expression identifying the field (e.g. {@code "userId"})
     * @param expectedValue the value the field is expected to hold
     * @throws AssertionError if the actual field value does not equal {@code expectedValue}
     */
    public static void assertFieldEquals(Response response, String fieldPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(fieldPath);
        logger.info("Asserting field '{}' - Expected: {}, Actual: {}", fieldPath, expectedValue, actualValue);
        Assert.assertEquals(actualValue, expectedValue,
                "Field '" + fieldPath + "' mismatch. Expected: " + expectedValue + ", Actual: " + actualValue);
    }

    /**
     * Asserts that the JSON array at {@code fieldPath} is present and contains at least one element.
     *
     * @param response  the response to inspect (must not be {@code null})
     * @param fieldPath a JsonPath expression identifying the array (e.g. {@code "$"} for root array)
     * @throws AssertionError if the array is {@code null} or empty
     */
    public static void assertListNotEmpty(Response response, String fieldPath) {
        List<?> list = response.jsonPath().getList(fieldPath);
        logger.info("Asserting list '{}' is not empty", fieldPath);
        Assert.assertNotNull(list, "List '" + fieldPath + "' should not be null");
        Assert.assertFalse(list.isEmpty(), "List '" + fieldPath + "' should not be empty");
    }

    /**
     * Asserts that the JSON array at {@code fieldPath} has exactly {@code expectedSize} elements.
     *
     * @param response     the response to inspect (must not be {@code null})
     * @param fieldPath    a JsonPath expression identifying the array
     * @param expectedSize the expected number of elements in the array
     * @throws AssertionError if the list is {@code null} or its size differs from {@code expectedSize}
     */
    public static void assertListSize(Response response, String fieldPath, int expectedSize) {
        List<?> list = response.jsonPath().getList(fieldPath);
        logger.info("Asserting list '{}' size - Expected: {}, Actual: {}", fieldPath, expectedSize,
                list != null ? list.size() : "null");
        Assert.assertNotNull(list, "List '" + fieldPath + "' should not be null");
        Assert.assertEquals(list.size(), expectedSize,
                "List size mismatch for '" + fieldPath + "'");
    }

    /**
     * Asserts that the total response time is strictly less than {@code maxTimeMs} milliseconds.
     *
     * @param response  the response to inspect (must not be {@code null})
     * @param maxTimeMs the upper bound (exclusive) for the acceptable response time in milliseconds
     * @throws AssertionError if the actual response time is {@code >= maxTimeMs}
     */
    public static void assertResponseTimeBelow(Response response, long maxTimeMs) {
        long responseTime = response.getTime();
        logger.info("Asserting response time below {}ms - Actual: {}ms", maxTimeMs, responseTime);
        Assert.assertTrue(responseTime < maxTimeMs,
                "Response time " + responseTime + "ms exceeds maximum " + maxTimeMs + "ms");
    }

    /**
     * Asserts that the {@code Content-Type} header of the response contains
     * {@code expectedContentType} as a substring.
     *
     * @param response            the response to inspect (must not be {@code null})
     * @param expectedContentType the MIME type (or substring) to look for
     *                            (e.g. {@code "application/json"})
     * @throws AssertionError if the actual Content-Type does not contain {@code expectedContentType}
     */
    public static void assertContentType(Response response, String expectedContentType) {
        String actualContentType = response.getContentType();
        logger.info("Asserting content type - Expected: {}, Actual: {}", expectedContentType, actualContentType);
        Assert.assertTrue(actualContentType.contains(expectedContentType),
                "Content-Type mismatch. Expected to contain: " + expectedContentType + ", Actual: " + actualContentType);
    }

    /**
     * Asserts that the integer value of the JSON field at {@code fieldPath} is
     * strictly greater than {@code minValue}.
     *
     * @param response  the response to inspect (must not be {@code null})
     * @param fieldPath a JsonPath expression identifying the numeric field
     * @param minValue  the lower bound (exclusive) for the field value
     * @throws AssertionError if the field is {@code null} or its value is {@code <= minValue}
     */
    public static void assertFieldGreaterThan(Response response, String fieldPath, int minValue) {
        Integer value = response.jsonPath().getInt(fieldPath);
        logger.info("Asserting field '{}' > {} - Actual: {}", fieldPath, minValue, value);
        Assert.assertNotNull(value, "Field '" + fieldPath + "' should not be null");
        Assert.assertTrue(value > minValue,
                "Field '" + fieldPath + "' value " + value + " should be greater than " + minValue);
    }

    /**
     * Asserts that a GraphQL response body does not contain an {@code errors} field.
     *
     * <p>A non-null {@code errors} field indicates that the GraphQL server encountered
     * one or more errors while processing the request.</p>
     *
     * @param response the GraphQL response to inspect (must not be {@code null})
     * @throws AssertionError if the response contains an {@code errors} field
     */
    public static void assertGraphQLNoErrors(Response response) {
        Object errors = response.jsonPath().get("errors");
        logger.info("Asserting GraphQL response has no errors");
        Assert.assertNull(errors, "GraphQL response should not contain errors, but got: " + errors);
    }

    /**
     * Asserts that the GraphQL response body contains a non-null value at
     * {@code data.<dataField>}.
     *
     * @param response  the GraphQL response to inspect (must not be {@code null})
     * @param dataField the field name inside {@code data} to check (e.g. {@code "rockets"})
     * @throws AssertionError if {@code data.<dataField>} is absent or {@code null}
     */
    public static void assertGraphQLHasData(Response response, String dataField) {
        Object data = response.jsonPath().get("data." + dataField);
        logger.info("Asserting GraphQL data field '{}' is not null", dataField);
        Assert.assertNotNull(data, "GraphQL data field '" + dataField + "' should not be null");
    }
}
