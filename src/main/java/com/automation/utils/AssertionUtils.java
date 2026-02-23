package com.automation.utils;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import java.util.List;

public final class AssertionUtils {

    private static final Logger logger = LogManager.getLogger(AssertionUtils.class);

    private AssertionUtils() {}

    public static void assertStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        logger.info("Asserting status code - Expected: {}, Actual: {}", expectedStatusCode, actualStatusCode);
        Assert.assertEquals(actualStatusCode, expectedStatusCode,
                "Status code mismatch. Expected: " + expectedStatusCode + ", Actual: " + actualStatusCode);
    }

    public static void assertFieldNotNull(Response response, String fieldPath) {
        Object value = response.jsonPath().get(fieldPath);
        logger.info("Asserting field '{}' is not null", fieldPath);
        Assert.assertNotNull(value, "Field '" + fieldPath + "' should not be null");
    }

    public static void assertFieldEquals(Response response, String fieldPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(fieldPath);
        logger.info("Asserting field '{}' - Expected: {}, Actual: {}", fieldPath, expectedValue, actualValue);
        Assert.assertEquals(actualValue, expectedValue,
                "Field '" + fieldPath + "' mismatch. Expected: " + expectedValue + ", Actual: " + actualValue);
    }

    public static void assertListNotEmpty(Response response, String fieldPath) {
        List<?> list = response.jsonPath().getList(fieldPath);
        logger.info("Asserting list '{}' is not empty", fieldPath);
        Assert.assertNotNull(list, "List '" + fieldPath + "' should not be null");
        Assert.assertFalse(list.isEmpty(), "List '" + fieldPath + "' should not be empty");
    }

    public static void assertListSize(Response response, String fieldPath, int expectedSize) {
        List<?> list = response.jsonPath().getList(fieldPath);
        logger.info("Asserting list '{}' size - Expected: {}, Actual: {}", fieldPath, expectedSize,
                list != null ? list.size() : "null");
        Assert.assertNotNull(list, "List '" + fieldPath + "' should not be null");
        Assert.assertEquals(list.size(), expectedSize,
                "List size mismatch for '" + fieldPath + "'");
    }

    public static void assertResponseTimeBelow(Response response, long maxTimeMs) {
        long responseTime = response.getTime();
        logger.info("Asserting response time below {}ms - Actual: {}ms", maxTimeMs, responseTime);
        Assert.assertTrue(responseTime < maxTimeMs,
                "Response time " + responseTime + "ms exceeds maximum " + maxTimeMs + "ms");
    }

    public static void assertContentType(Response response, String expectedContentType) {
        String actualContentType = response.getContentType();
        logger.info("Asserting content type - Expected: {}, Actual: {}", expectedContentType, actualContentType);
        Assert.assertTrue(actualContentType.contains(expectedContentType),
                "Content-Type mismatch. Expected to contain: " + expectedContentType + ", Actual: " + actualContentType);
    }

    public static void assertFieldGreaterThan(Response response, String fieldPath, int minValue) {
        Integer value = response.jsonPath().getInt(fieldPath);
        logger.info("Asserting field '{}' > {} - Actual: {}", fieldPath, minValue, value);
        Assert.assertNotNull(value, "Field '" + fieldPath + "' should not be null");
        Assert.assertTrue(value > minValue,
                "Field '" + fieldPath + "' value " + value + " should be greater than " + minValue);
    }

    public static void assertGraphQLNoErrors(Response response) {
        Object errors = response.jsonPath().get("errors");
        logger.info("Asserting GraphQL response has no errors");
        Assert.assertNull(errors, "GraphQL response should not contain errors, but got: " + errors);
    }

    public static void assertGraphQLHasData(Response response, String dataField) {
        Object data = response.jsonPath().get("data." + dataField);
        logger.info("Asserting GraphQL data field '{}' is not null", dataField);
        Assert.assertNotNull(data, "GraphQL data field '" + dataField + "' should not be null");
    }
}
