package com.automation.utils;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.util.List;

/**
 * Static utility methods for asserting REST API response properties in TestNG tests.
 *
 * <p>All methods log the assertion being performed and delegate to {@link Assert} so
 * that failures produce clear, human-readable messages.
 *
 * <p>Example usage:
 * <pre>{@code
 * Response response = client.get("/posts");
 *
 * AssertionUtils.assertStatusCode(response, 200);
 * AssertionUtils.assertBodyNotEmpty(response);
 * AssertionUtils.assertResponseTime(response, 5000);
 * AssertionUtils.assertContentType(response, "application/json");
 * }</pre>
 *
 * @see ResponseValidator
 * @see org.testng.Assert
 */
public final class AssertionUtils {

    private static final Logger logger = LogManager.getLogger(AssertionUtils.class);

    /** Utility class – do not instantiate. */
    private AssertionUtils() {}

    // -------------------------------------------------------------------------
    // Core assertions
    // -------------------------------------------------------------------------

    /**
     * Asserts that the HTTP status code of {@code response} equals {@code expectedStatusCode}.
     *
     * @param response           the HTTP response to inspect
     * @param expectedStatusCode the expected HTTP status code (e.g. 200, 201, 404)
     * @throws AssertionError if the actual status code differs from {@code expectedStatusCode}
     */
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        logger.info("Asserting status code - Expected: {}, Actual: {}", expectedStatusCode, actualStatusCode);
        Assert.assertEquals(actualStatusCode, expectedStatusCode,
                "Status code mismatch. Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
    }

    /**
     * Asserts that the response body is not null and not empty.
     *
     * @param response the HTTP response to inspect
     * @throws AssertionError if the response body is null or blank
     */
    public static void assertBodyNotEmpty(Response response) {
        String body = response.getBody().asString();
        logger.info("Asserting response body is not empty");
        Assert.assertNotNull(body, "Response body should not be null");
        Assert.assertFalse(body.isBlank(), "Response body should not be empty");
    }

    /**
     * Asserts that the response time is strictly less than {@code maxTimeMs} milliseconds.
     *
     * @param response  the HTTP response to inspect
     * @param maxTimeMs the maximum acceptable response time in milliseconds
     * @throws AssertionError if the actual response time equals or exceeds {@code maxTimeMs}
     */
    public static void assertResponseTime(Response response, long maxTimeMs) {
        long responseTime = response.getTime();
        logger.info("Asserting response time below {}ms - Actual: {}ms", maxTimeMs, responseTime);
        Assert.assertTrue(responseTime < maxTimeMs,
                "Response time " + responseTime + "ms exceeds maximum " + maxTimeMs + "ms");
    }

    /**
     * Asserts that the {@code Content-Type} header of the response contains
     * {@code expectedContentType}.
     *
     * @param response            the HTTP response to inspect
     * @param expectedContentType the content-type substring to look for (e.g. {@code "application/json"})
     * @throws AssertionError if the actual Content-Type does not contain {@code expectedContentType}
     */
    public static void assertContentType(Response response, String expectedContentType) {
        String actualContentType = response.getContentType();
        logger.info("Asserting content type - Expected: {}, Actual: {}", expectedContentType, actualContentType);
        Assert.assertTrue(actualContentType.contains(expectedContentType),
                "Content-Type mismatch. Expected to contain: " + expectedContentType + ", Actual: " + actualContentType);
    }

    // -------------------------------------------------------------------------
    // JSON field assertions
    // -------------------------------------------------------------------------

    /**
     * Asserts that the JSON field at {@code fieldPath} in the response body is not {@code null}.
     *
     * @param response  the HTTP response to inspect
     * @param fieldPath the JsonPath expression (e.g. {@code "data.id"})
     * @throws AssertionError if the field value is {@code null}
     */
    public static void assertFieldNotNull(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        logger.info("Asserting field '{}' is not null", fieldPath);
        Assert.assertNotNull(value, "Field '" + fieldPath + "' should not be null");
    }

    /**
     * Asserts that the JSON field at {@code fieldPath} equals {@code expectedValue}.
     *
     * @param response      the HTTP response to inspect
     * @param fieldPath     the JsonPath expression
     * @param expectedValue the expected field value
     * @throws AssertionError if the actual value differs from {@code expectedValue}
     */
    public static void assertFieldEquals(Response response, String fieldPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(fieldPath);
        logger.info("Asserting field '{}' - Expected: {}, Actual: {}", fieldPath, expectedValue, actualValue);
        Assert.assertEquals(actualValue, expectedValue,
                "Field '" + fieldPath + "' mismatch. Expected: " + expectedValue + ", Actual: " + actualValue);
    }

    /**
     * Asserts that the JSON array at {@code fieldPath} is not {@code null} and not empty.
     *
     * @param response  the HTTP response to inspect
     * @param fieldPath the JsonPath expression pointing to an array
     * @throws AssertionError if the array is null or empty
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
     * @param response     the HTTP response to inspect
     * @param fieldPath    the JsonPath expression pointing to an array
     * @param expectedSize the expected number of elements
     * @throws AssertionError if the list is null or its size differs from {@code expectedSize}
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
     * Asserts that the response time is strictly less than {@code maxTimeMs} milliseconds.
     * Alias for {@link #assertResponseTime(Response, long)}.
     *
     * @param response  the HTTP response to inspect
     * @param maxTimeMs the maximum acceptable response time in milliseconds
     * @throws AssertionError if the actual response time equals or exceeds {@code maxTimeMs}
     */
    public static void assertResponseTimeBelow(Response response, long maxTimeMs) {
        assertResponseTime(response, maxTimeMs);
    }

    /**
     * Asserts that the integer value of the JSON field at {@code fieldPath} is greater than
     * {@code minValue}.
     *
     * @param response  the HTTP response to inspect
     * @param fieldPath the JsonPath expression
     * @param minValue  the exclusive lower bound
     * @throws AssertionError if the value is null or not greater than {@code minValue}
     */
    public static void assertFieldGreaterThan(Response response, String fieldPath, int minValue) {
        Integer value = response.jsonPath().getInt(fieldPath);
        logger.info("Asserting field '{}' > {} - Actual: {}", fieldPath, minValue, value);
        Assert.assertNotNull(value, "Field '" + fieldPath + "' should not be null");
        Assert.assertTrue(value > minValue,
                "Field '" + fieldPath + "' value " + value + " should be greater than " + minValue);
    }

    // -------------------------------------------------------------------------
    // GraphQL-specific assertions
    // -------------------------------------------------------------------------

    /**
     * Asserts that the GraphQL response body does not contain an {@code "errors"} field.
     *
     * @param response the GraphQL HTTP response to inspect
     * @throws AssertionError if the {@code "errors"} field is present
     */
    public static void assertGraphQLNoErrors(Response response) {
        Object errors = response.jsonPath().get("errors");
        logger.info("Asserting GraphQL response has no errors");
        Assert.assertNull(errors, "GraphQL response should not contain errors, but got: " + errors);
    }

    /**
     * Asserts that the GraphQL response body contains a non-null value at
     * {@code "data.<dataField>"}.
     *
     * @param response  the GraphQL HTTP response to inspect
     * @param dataField the field name under {@code "data"} to check
     * @throws AssertionError if the field is null
     */
    public static void assertGraphQLHasData(Response response, String dataField) {
        Object data = response.jsonPath().get("data." + dataField);
        logger.info("Asserting GraphQL data field '{}' is not null", dataField);
        Assert.assertNotNull(data, "GraphQL data field '" + dataField + "' should not be null");
    }
}
