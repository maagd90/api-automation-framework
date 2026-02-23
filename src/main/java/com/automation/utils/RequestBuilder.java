package com.automation.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Fluent builder for constructing and sending REST API requests.
 *
 * <p>Provides a readable, step-by-step DSL for configuring a request before
 * dispatching it. Each setter method returns {@code this} so that calls can be chained.
 *
 * <p>Example usage:
 * <pre>{@code
 * Response response = new RequestBuilder()
 *         .withBaseUrl("https://jsonplaceholder.typicode.com")
 *         .withHeaders(Map.of("Accept", "application/json"))
 *         .withQueryParam("userId", "1")
 *         .get("/posts");
 *
 * Response created = new RequestBuilder()
 *         .withBaseUrl("https://jsonplaceholder.typicode.com")
 *         .withBody(postRequest)
 *         .post("/posts");
 * }</pre>
 *
 * @see RestApiClient
 * @see ResponseValidator
 */
public class RequestBuilder {

    private static final Logger logger = LogManager.getLogger(RequestBuilder.class);

    /** Base URL prepended to all endpoint paths. */
    private String baseUrl;

    /** HTTP headers to include in the request. */
    private final Map<String, String> headers = new HashMap<>();

    /** Query parameters to append to the URL. */
    private final Map<String, Object> queryParams = new HashMap<>();

    /** Request body object (serialised to JSON). */
    private Object body;

    /**
     * Sets the base URL for the request.
     *
     * @param url the fully-qualified base URL (e.g. {@code "https://api.example.com"})
     * @return this builder for chaining
     */
    public RequestBuilder withBaseUrl(String url) {
        this.baseUrl = url;
        return this;
    }

    /**
     * Replaces all HTTP headers with the provided map.
     *
     * @param headers a map of header name to header value
     * @return this builder for chaining
     */
    public RequestBuilder withHeaders(Map<String, String> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
        return this;
    }

    /**
     * Sets the request body object, which will be serialised to JSON.
     *
     * @param body the request body (e.g. a POJO or a {@code Map})
     * @return this builder for chaining
     */
    public RequestBuilder withBody(Object body) {
        this.body = body;
        return this;
    }

    /**
     * Adds a single query parameter.
     *
     * @param key   the query parameter name
     * @param value the query parameter value
     * @return this builder for chaining
     */
    public RequestBuilder withQueryParam(String key, Object value) {
        this.queryParams.put(key, value);
        return this;
    }

    // -------------------------------------------------------------------------
    // HTTP method dispatchers
    // -------------------------------------------------------------------------

    /**
     * Sends a GET request to {@code baseUrl + endpoint} using the configured headers and
     * query parameters.
     *
     * @param endpoint the path relative to the base URL (e.g. {@code "/posts"})
     * @return the HTTP {@link Response}
     */
    public Response get(String endpoint) {
        logger.info("GET {}{}", baseUrl, endpoint);
        RequestSpecification spec = buildSpec();
        if (!queryParams.isEmpty()) {
            spec.queryParams(queryParams);
        }
        return spec.when().get(baseUrl + endpoint).then().extract().response();
    }

    /**
     * Sends a POST request to {@code baseUrl + endpoint} with the configured body and headers.
     *
     * @param endpoint the path relative to the base URL
     * @return the HTTP {@link Response}
     */
    public Response post(String endpoint) {
        logger.info("POST {}{}", baseUrl, endpoint);
        RequestSpecification spec = buildSpec();
        if (body != null) {
            spec.body(body);
        }
        return spec.when().post(baseUrl + endpoint).then().extract().response();
    }

    /**
     * Sends a PUT request to {@code baseUrl + endpoint} with the configured body and headers.
     *
     * @param endpoint the path relative to the base URL
     * @return the HTTP {@link Response}
     */
    public Response put(String endpoint) {
        logger.info("PUT {}{}", baseUrl, endpoint);
        RequestSpecification spec = buildSpec();
        if (body != null) {
            spec.body(body);
        }
        return spec.when().put(baseUrl + endpoint).then().extract().response();
    }

    /**
     * Sends a DELETE request to {@code baseUrl + endpoint} using the configured headers.
     *
     * @param endpoint the path relative to the base URL
     * @return the HTTP {@link Response}
     */
    public Response delete(String endpoint) {
        logger.info("DELETE {}{}", baseUrl, endpoint);
        return buildSpec().when().delete(baseUrl + endpoint).then().extract().response();
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /**
     * Constructs the REST-Assured {@link RequestSpecification} from the builder state.
     *
     * @return a configured request specification
     */
    private RequestSpecification buildSpec() {
        RequestSpecification spec = RestAssured.given()
                .contentType("application/json")
                .accept("application/json");
        if (!headers.isEmpty()) {
            spec.headers(headers);
        }
        return spec;
    }
}
