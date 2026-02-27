package com.automation.utils;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

/**
 * Fluent builder for constructing REST-Assured {@link RequestSpecification} objects.
 *
 * <p>This builder encapsulates the incremental assembly of HTTP request parameters —
 * base URI, headers, query parameters, path parameters, body, and content/accept
 * types — and produces a fully-configured {@link RequestSpecification} via
 * {@link #build()}.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * RequestSpecification spec = new RequestBuilder()
 *         .baseUri("https://api.example.com")
 *         .header("Authorization", "Bearer token123")
 *         .queryParam("page", 1)
 *         .contentType(ContentType.JSON)
 *         .body(myRequestObject)
 *         .build();
 *
 * Response response = RestAssured.given()
 *         .spec(spec)
 *         .when()
 *         .get("/resources")
 *         .then()
 *         .extract()
 *         .response();
 * }</pre>
 *
 * @author api-automation-framework
 * @version 1.0.0
 * @see RestApiClient
 * @see ResponseValidator
 */
public class RequestBuilder {

    /** Underlying REST-Assured spec builder being populated. */
    private final RequestSpecBuilder specBuilder;

    /** Accumulated query parameters. */
    private final Map<String, Object> queryParams = new HashMap<>();

    /** Accumulated path parameters. */
    private final Map<String, Object> pathParams = new HashMap<>();

    /** Accumulated request headers. */
    private final Map<String, String> headers = new HashMap<>();

    /** The request body to be sent, if any. */
    private Object body;

    /**
     * Creates a new {@code RequestBuilder} with default JSON content-type and accept headers.
     */
    public RequestBuilder() {
        this.specBuilder = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON);
    }

    /**
     * Sets the base URI for the request.
     *
     * @param baseUri the scheme + host [+ port] portion of the target URL
     *                (e.g. {@code "https://api.example.com"}); must not be {@code null}
     * @return this builder instance for method chaining
     */
    public RequestBuilder baseUri(String baseUri) {
        specBuilder.setBaseUri(baseUri);
        return this;
    }

    /**
     * Sets the base path that is appended to the base URI for every request built
     * from this spec.
     *
     * @param basePath the path prefix (e.g. {@code "/v1"}); must not be {@code null}
     * @return this builder instance for method chaining
     */
    public RequestBuilder basePath(String basePath) {
        specBuilder.setBasePath(basePath);
        return this;
    }

    /**
     * Adds a single HTTP request header.
     *
     * @param name  the header name (e.g. {@code "Authorization"}); must not be {@code null}
     * @param value the header value; must not be {@code null}
     * @return this builder instance for method chaining
     */
    public RequestBuilder header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    /**
     * Adds all entries from the supplied map as HTTP request headers.
     *
     * @param headers a map of header name-to-value pairs (must not be {@code null})
     * @return this builder instance for method chaining
     */
    public RequestBuilder headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    /**
     * Adds a single query parameter to the request URL.
     *
     * @param name  the query-parameter name (must not be {@code null})
     * @param value the query-parameter value
     * @return this builder instance for method chaining
     */
    public RequestBuilder queryParam(String name, Object value) {
        queryParams.put(name, value);
        return this;
    }

    /**
     * Adds a single path parameter that will be substituted into the endpoint template.
     *
     * <p>For example, a path parameter {@code "id" -> 42} substituted into
     * {@code "/posts/{id}"} produces {@code "/posts/42"}.</p>
     *
     * @param name  the path-parameter placeholder name (must not be {@code null})
     * @param value the value to substitute
     * @return this builder instance for method chaining
     */
    public RequestBuilder pathParam(String name, Object value) {
        pathParams.put(name, value);
        return this;
    }

    /**
     * Sets the request body to be serialised and sent with the request.
     *
     * @param body the body object (will be serialised to JSON by REST-Assured);
     *             must not be {@code null}
     * @return this builder instance for method chaining
     */
    public RequestBuilder body(Object body) {
        this.body = body;
        return this;
    }

    /**
     * Overrides the {@code Content-Type} header for this request.
     *
     * @param contentType the content type to set (e.g. {@link ContentType#JSON})
     * @return this builder instance for method chaining
     */
    public RequestBuilder contentType(ContentType contentType) {
        specBuilder.setContentType(contentType);
        return this;
    }

    /**
     * Overrides the {@code Accept} header for this request.
     *
     * @param acceptType the accepted response content type (e.g. {@link ContentType#JSON})
     * @return this builder instance for method chaining
     */
    public RequestBuilder accept(ContentType acceptType) {
        specBuilder.setAccept(acceptType);
        return this;
    }

    /**
     * Adds a Bearer-token {@code Authorization} header to the request.
     *
     * <p>Equivalent to calling {@code header("Authorization", "Bearer " + token)}.</p>
     *
     * @param token the bearer token value (must not be {@code null})
     * @return this builder instance for method chaining
     */
    public RequestBuilder bearerAuth(String token) {
        headers.put("Authorization", "Bearer " + token);
        return this;
    }

    /**
     * Builds and returns a fully configured {@link RequestSpecification}.
     *
     * <p>All accumulated headers, query parameters, path parameters, and the body
     * are applied to the underlying {@link RequestSpecBuilder} before building.</p>
     *
     * @return a configured {@link RequestSpecification} ready for use with
     *         {@code RestAssured.given().spec(...)}
     */
    public RequestSpecification build() {
        if (!headers.isEmpty()) {
            specBuilder.addHeaders(headers);
        }
        if (!queryParams.isEmpty()) {
            specBuilder.addQueryParams(queryParams);
        }
        if (!pathParams.isEmpty()) {
            specBuilder.addPathParams(pathParams);
        }
        if (body != null) {
            specBuilder.setBody(body);
        }
        return specBuilder.build();
    }
}
