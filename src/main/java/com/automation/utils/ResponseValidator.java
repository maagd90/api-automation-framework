package com.automation.utils;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class ResponseValidator {

    private static final Logger logger = LogManager.getLogger(ResponseValidator.class);
    private final Response response;

    public ResponseValidator(Response response) {
        this.response = response;
    }

    public static ResponseValidator of(Response response) {
        return new ResponseValidator(response);
    }

    public ResponseValidator statusCode(int expectedCode) {
        AssertionUtils.assertStatusCode(response, expectedCode);
        return this;
    }

    public ResponseValidator fieldNotNull(String fieldPath) {
        AssertionUtils.assertFieldNotNull(response, fieldPath);
        return this;
    }

    public ResponseValidator fieldEquals(String fieldPath, Object expectedValue) {
        AssertionUtils.assertFieldEquals(response, fieldPath, expectedValue);
        return this;
    }

    public ResponseValidator listNotEmpty(String fieldPath) {
        AssertionUtils.assertListNotEmpty(response, fieldPath);
        return this;
    }

    public ResponseValidator listSize(String fieldPath, int expectedSize) {
        AssertionUtils.assertListSize(response, fieldPath, expectedSize);
        return this;
    }

    public ResponseValidator responseTimeBelow(long maxTimeMs) {
        AssertionUtils.assertResponseTimeBelow(response, maxTimeMs);
        return this;
    }

    public ResponseValidator contentType(String expectedContentType) {
        AssertionUtils.assertContentType(response, expectedContentType);
        return this;
    }

    public ResponseValidator noGraphQLErrors() {
        AssertionUtils.assertGraphQLNoErrors(response);
        return this;
    }

    public ResponseValidator graphQLDataNotNull(String dataField) {
        AssertionUtils.assertGraphQLHasData(response, dataField);
        return this;
    }

    public <T> T extractAs(Class<T> clazz) {
        return response.as(clazz);
    }

    public <T> List<T> extractListAs(Class<T[]> clazz) {
        T[] array = response.as(clazz);
        return List.of(array);
    }

    public String extractString(String path) {
        return response.jsonPath().getString(path);
    }

    public Integer extractInt(String path) {
        return response.jsonPath().getInt(path);
    }

    public List<Map<String, Object>> extractList(String path) {
        return response.jsonPath().getList(path);
    }

    public Response getResponse() {
        return response;
    }

    public void logResponse() {
        logger.info("Response Status: {}", response.getStatusCode());
        logger.info("Response Time: {}ms", response.getTime());
        logger.debug("Response Body: {}", response.getBody().asString());
    }
}
