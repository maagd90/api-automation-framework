package com.automation.utils;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Fluent wrapper around {@link AssertionUtils} that enables chained response validation.
 *
 * <p>Create an instance via the static factory method {@link #of(Response)}, then chain
 * validation calls. Each method returns {@code this}, allowing multiple checks to be
 * expressed as a single readable statement:</p>
 *
 * <pre>{@code
 * ResponseValidator.of(response)
 *         .statusCode(200)
 *         .contentType("application/json")
 *         .fieldNotNull("id")
 *         .fieldEquals("title", "My Title")
 *         .responseTimeBelow(5000);
 * }</pre>
 *
 * <p>In addition to validation methods, several extraction helpers are provided for
 * converting the response body into typed Java objects:</p>
 *
 * <pre>{@code
 * PostResponse post = ResponseValidator.of(response)
 *         .statusCode(200)
 *         .extractAs(PostResponse.class);
 * }</pre>
 *
 * @author api-automation-framework
 * @version 1.0.0
 * @see AssertionUtils
 */
public class ResponseValidator {

    private static final Logger logger = LogManager.getLogger(ResponseValidator.class);

    /** The REST-Assured response being validated. */
    private final Response response;

    /**
     * Constructs a {@code ResponseValidator} wrapping the given response.
     *
     * <p>Prefer the static factory method {@link #of(Response)} for a more fluent style.</p>
     *
     * @param response the response to wrap (must not be {@code null})
     */
    public ResponseValidator(Response response) {
        this.response = response;
    }

    /**
     * Static factory method – creates a new {@code ResponseValidator} for the given response.
     *
     * @param response the response to validate (must not be {@code null})
     * @return a new {@code ResponseValidator} instance
     */
    public static ResponseValidator of(Response response) {
        return new ResponseValidator(response);
    }

    /**
     * Asserts that the response HTTP status code equals {@code expectedCode}.
     *
     * @param expectedCode the expected HTTP status code
     * @return this instance for method chaining
     * @throws AssertionError if the actual status code differs
     * @see AssertionUtils#assertStatusCode(Response, int)
     */
    public ResponseValidator statusCode(int expectedCode) {
        AssertionUtils.assertStatusCode(response, expectedCode);
        return this;
    }

    /**
     * Asserts that the JSON field identified by {@code fieldPath} is not {@code null}.
     *
     * @param fieldPath a JsonPath expression for the field (e.g. {@code "data.id"})
     * @return this instance for method chaining
     * @throws AssertionError if the field is absent or {@code null}
     * @see AssertionUtils#assertFieldNotNull(Response, String)
     */
    public ResponseValidator fieldNotNull(String fieldPath) {
        AssertionUtils.assertFieldNotNull(response, fieldPath);
        return this;
    }

    /**
     * Asserts that the JSON field identified by {@code fieldPath} equals {@code expectedValue}.
     *
     * @param fieldPath     a JsonPath expression for the field
     * @param expectedValue the value the field is expected to hold
     * @return this instance for method chaining
     * @throws AssertionError if the actual field value does not equal {@code expectedValue}
     * @see AssertionUtils#assertFieldEquals(Response, String, Object)
     */
    public ResponseValidator fieldEquals(String fieldPath, Object expectedValue) {
        AssertionUtils.assertFieldEquals(response, fieldPath, expectedValue);
        return this;
    }

    /**
     * Asserts that the JSON array at {@code fieldPath} is non-null and non-empty.
     *
     * @param fieldPath a JsonPath expression for the array
     * @return this instance for method chaining
     * @throws AssertionError if the list is {@code null} or empty
     * @see AssertionUtils#assertListNotEmpty(Response, String)
     */
    public ResponseValidator listNotEmpty(String fieldPath) {
        AssertionUtils.assertListNotEmpty(response, fieldPath);
        return this;
    }

    /**
     * Asserts that the JSON array at {@code fieldPath} has exactly {@code expectedSize} elements.
     *
     * @param fieldPath    a JsonPath expression for the array
     * @param expectedSize the expected element count
     * @return this instance for method chaining
     * @throws AssertionError if the list size differs from {@code expectedSize}
     * @see AssertionUtils#assertListSize(Response, String, int)
     */
    public ResponseValidator listSize(String fieldPath, int expectedSize) {
        AssertionUtils.assertListSize(response, fieldPath, expectedSize);
        return this;
    }

    /**
     * Asserts that the response time is strictly below {@code maxTimeMs} milliseconds.
     *
     * @param maxTimeMs the maximum acceptable response time in milliseconds
     * @return this instance for method chaining
     * @throws AssertionError if the actual response time is {@code >= maxTimeMs}
     * @see AssertionUtils#assertResponseTimeBelow(Response, long)
     */
    public ResponseValidator responseTimeBelow(long maxTimeMs) {
        AssertionUtils.assertResponseTimeBelow(response, maxTimeMs);
        return this;
    }

    /**
     * Asserts that the {@code Content-Type} header contains {@code expectedContentType}.
     *
     * @param expectedContentType the MIME type substring to check for
     * @return this instance for method chaining
     * @throws AssertionError if the Content-Type does not contain the expected value
     * @see AssertionUtils#assertContentType(Response, String)
     */
    public ResponseValidator contentType(String expectedContentType) {
        AssertionUtils.assertContentType(response, expectedContentType);
        return this;
    }

    /**
     * Asserts that the GraphQL response body does not contain an {@code errors} field.
     *
     * @return this instance for method chaining
     * @throws AssertionError if the response contains a GraphQL {@code errors} field
     * @see AssertionUtils#assertGraphQLNoErrors(Response)
     */
    public ResponseValidator noGraphQLErrors() {
        AssertionUtils.assertGraphQLNoErrors(response);
        return this;
    }

    /**
     * Asserts that the GraphQL response body has a non-null value at {@code data.<dataField>}.
     *
     * @param dataField the sub-field within {@code data} to check (e.g. {@code "rockets"})
     * @return this instance for method chaining
     * @throws AssertionError if {@code data.<dataField>} is absent or {@code null}
     * @see AssertionUtils#assertGraphQLHasData(Response, String)
     */
    public ResponseValidator graphQLDataNotNull(String dataField) {
        AssertionUtils.assertGraphQLHasData(response, dataField);
        return this;
    }

    /**
     * Deserialises the response body into an instance of {@code clazz}.
     *
     * @param <T>   the target type
     * @param clazz the class to deserialise into
     * @return the deserialised response body (may be {@code null} if the body is empty)
     */
    public <T> T extractAs(Class<T> clazz) {
        return response.as(clazz);
    }

    /**
     * Deserialises the response body (a JSON array) into an immutable {@link List}.
     *
     * @param <T>   the element type
     * @param clazz the array class of the element type (e.g. {@code PostResponse[].class})
     * @return an immutable list of deserialised elements
     */
    public <T> List<T> extractListAs(Class<T[]> clazz) {
        T[] array = response.as(clazz);
        return List.of(array);
    }

    /**
     * Extracts the string value at the given JsonPath expression.
     *
     * @param path a JsonPath expression (e.g. {@code "data.company.name"})
     * @return the string value, or {@code null} if the path does not match
     */
    public String extractString(String path) {
        return response.jsonPath().getString(path);
    }

    /**
     * Extracts the integer value at the given JsonPath expression.
     *
     * @param path a JsonPath expression (e.g. {@code "id"})
     * @return the integer value, or {@code null} if the path does not match
     */
    public Integer extractInt(String path) {
        return response.jsonPath().getInt(path);
    }

    /**
     * Extracts a list of maps from the given JsonPath expression.
     *
     * <p>Useful for accessing arrays of objects in the response body.</p>
     *
     * @param path a JsonPath expression pointing to a JSON array of objects
     * @return a list of maps representing each object, or {@code null} if the path does not match
     */
    public List<Map<String, Object>> extractList(String path) {
        return response.jsonPath().getList(path);
    }

    /**
     * Returns the underlying REST-Assured {@link Response} for low-level access.
     *
     * @return the wrapped response (never {@code null})
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Logs a summary of the response status code, response time, and body at the
     * appropriate log levels.
     *
     * <p>Status code and time are logged at INFO; the body is logged at DEBUG to
     * avoid flooding production log output.</p>
     */
    public void logResponse() {
        logger.info("Response Status: {}", response.getStatusCode());
        logger.info("Response Time: {}ms", response.getTime());
        logger.debug("Response Body: {}", response.getBody().asString());
    }
}
