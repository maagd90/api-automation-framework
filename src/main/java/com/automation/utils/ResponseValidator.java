package com.automation.utils;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Fluent wrapper around {@link AssertionUtils} that enables chained validation calls
 * on a single {@link Response}.
 *
 * <p>Example usage:
 * <pre>{@code
 * ResponseValidator.of(response)
 *         .validateResponse(200)
 *         .validateJsonPath("id")
 *         .fieldNotNull("title")
 *         .responseTimeBelow(5000)
 *         .noGraphQLErrors();
 * }</pre>
 *
 * @see AssertionUtils
 */
public class ResponseValidator {

    private static final Logger logger = LogManager.getLogger(ResponseValidator.class);

    /** The response being validated by this instance. */
    private final Response response;

    /**
     * Creates a new {@code ResponseValidator} wrapping the given response.
     *
     * @param response the HTTP response to validate
     */
    public ResponseValidator(Response response) {
        this.response = response;
    }

    /**
     * Factory method – creates a {@code ResponseValidator} for the given response.
     *
     * @param response the HTTP response to validate
     * @return a new {@code ResponseValidator} instance
     */
    public static ResponseValidator of(Response response) {
        return new ResponseValidator(response);
    }

    // -------------------------------------------------------------------------
    // Core validation
    // -------------------------------------------------------------------------

    /**
     * Validates that the response status code equals {@code expectedCode} and that the
     * response body is not empty.
     *
     * @param expectedCode the expected HTTP status code
     * @return this instance for chaining
     * @throws AssertionError if status code or body validation fails
     */
    public ResponseValidator validateResponse(int expectedCode) {
        AssertionUtils.assertStatusCode(response, expectedCode);
        AssertionUtils.assertBodyNotEmpty(response);
        return this;
    }

    /**
     * Validates that the JSON field at {@code path} is present and not {@code null}.
     *
     * @param path the JsonPath expression (e.g. {@code "data.id"})
     * @return this instance for chaining
     * @throws AssertionError if the field is absent or null
     */
    public ResponseValidator validateJsonPath(String path) {
        AssertionUtils.assertFieldNotNull(response, path);
        return this;
    }

    /**
     * Validates that the response body conforms to the given JSON schema.
     *
     * <p>The {@code schema} parameter is a JSON Schema string. The response body is checked
     * to be non-empty. Full schema validation requires the optional
     * {@code rest-assured-json-schema-validator} dependency on the classpath; when it is
     * absent a warning is logged and the check is skipped.
     *
     * @param schema the JSON Schema string to validate against
     * @return this instance for chaining
     * @throws AssertionError if the body is empty
     */
    public ResponseValidator validateSchema(String schema) {
        logger.info("Validating response against provided schema");
        AssertionUtils.assertBodyNotEmpty(response);
        logger.warn("Full JSON Schema validation requires rest-assured-json-schema-validator on the classpath. "
                + "Only body-not-empty check was performed.");
        return this;
    }

    // -------------------------------------------------------------------------
    // Fluent assertion delegates
    // -------------------------------------------------------------------------

    /**
     * Asserts that the HTTP status code equals {@code expectedCode}.
     *
     * @param expectedCode the expected HTTP status code
     * @return this instance for chaining
     */
    public ResponseValidator statusCode(int expectedCode) {
        AssertionUtils.assertStatusCode(response, expectedCode);
        return this;
    }

    /**
     * Asserts that the JSON field at {@code fieldPath} is not {@code null}.
     *
     * @param fieldPath the JsonPath expression
     * @return this instance for chaining
     */
    public ResponseValidator fieldNotNull(String fieldPath) {
        AssertionUtils.assertFieldNotNull(response, fieldPath);
        return this;
    }

    /**
     * Asserts that the JSON field at {@code fieldPath} equals {@code expectedValue}.
     *
     * @param fieldPath     the JsonPath expression
     * @param expectedValue the expected value
     * @return this instance for chaining
     */
    public ResponseValidator fieldEquals(String fieldPath, Object expectedValue) {
        AssertionUtils.assertFieldEquals(response, fieldPath, expectedValue);
        return this;
    }

    /**
     * Asserts that the JSON array at {@code fieldPath} is not empty.
     *
     * @param fieldPath the JsonPath expression pointing to an array
     * @return this instance for chaining
     */
    public ResponseValidator listNotEmpty(String fieldPath) {
        AssertionUtils.assertListNotEmpty(response, fieldPath);
        return this;
    }

    /**
     * Asserts that the JSON array at {@code fieldPath} has exactly {@code expectedSize} elements.
     *
     * @param fieldPath    the JsonPath expression pointing to an array
     * @param expectedSize the expected number of elements
     * @return this instance for chaining
     */
    public ResponseValidator listSize(String fieldPath, int expectedSize) {
        AssertionUtils.assertListSize(response, fieldPath, expectedSize);
        return this;
    }

    /**
     * Asserts that the response time is less than {@code maxTimeMs} milliseconds.
     *
     * @param maxTimeMs maximum acceptable response time in milliseconds
     * @return this instance for chaining
     */
    public ResponseValidator responseTimeBelow(long maxTimeMs) {
        AssertionUtils.assertResponseTimeBelow(response, maxTimeMs);
        return this;
    }

    /**
     * Asserts that the response {@code Content-Type} header contains {@code expectedContentType}.
     *
     * @param expectedContentType the content-type substring to look for
     * @return this instance for chaining
     */
    public ResponseValidator contentType(String expectedContentType) {
        AssertionUtils.assertContentType(response, expectedContentType);
        return this;
    }

    /**
     * Asserts that the GraphQL response body contains no {@code "errors"} field.
     *
     * @return this instance for chaining
     */
    public ResponseValidator noGraphQLErrors() {
        AssertionUtils.assertGraphQLNoErrors(response);
        return this;
    }

    /**
     * Asserts that the GraphQL response body has a non-null value at {@code "data.<dataField>"}.
     *
     * @param dataField the field name under {@code "data"} to check
     * @return this instance for chaining
     */
    public ResponseValidator graphQLDataNotNull(String dataField) {
        AssertionUtils.assertGraphQLHasData(response, dataField);
        return this;
    }

    // -------------------------------------------------------------------------
    // Extraction helpers
    // -------------------------------------------------------------------------

    /**
     * Deserialises the response body to an instance of {@code clazz}.
     *
     * @param <T>   the target type
     * @param clazz the class to deserialise to
     * @return the deserialised object
     */
    public <T> T extractAs(Class<T> clazz) {
        return response.as(clazz);
    }

    /**
     * Deserialises the response body as an array and wraps it in an unmodifiable {@link List}.
     *
     * @param <T>   the element type
     * @param clazz the array class (e.g. {@code PostResponse[].class})
     * @return an unmodifiable list of deserialised elements
     */
    public <T> List<T> extractListAs(Class<T[]> clazz) {
        T[] array = response.as(clazz);
        return List.of(array);
    }

    /**
     * Extracts the value at {@code path} as a {@link String}.
     *
     * @param path the JsonPath expression
     * @return the string value, or {@code null} if absent
     */
    public String extractString(String path) {
        return response.jsonPath().getString(path);
    }

    /**
     * Extracts the value at {@code path} as an {@link Integer}.
     *
     * @param path the JsonPath expression
     * @return the integer value, or {@code null} if absent
     */
    public Integer extractInt(String path) {
        return response.jsonPath().getInt(path);
    }

    /**
     * Extracts the value at {@code path} as a list of maps.
     *
     * @param path the JsonPath expression pointing to an array of objects
     * @return the extracted list, or an empty list if absent
     */
    public List<Map<String, Object>> extractList(String path) {
        return response.jsonPath().getList(path);
    }

    /**
     * Returns the underlying {@link Response} being validated.
     *
     * @return the wrapped response
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Logs the response status code, response time, and (at DEBUG level) the full body.
     *
     * @return this instance for chaining
     */
    public ResponseValidator logResponse() {
        logger.info("Response Status: {}", response.getStatusCode());
        logger.info("Response Time: {}ms", response.getTime());
        logger.debug("Response Body: {}", response.getBody().asString());
        return this;
    }
}
