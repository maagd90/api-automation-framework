package com.automation.client;

import com.automation.config.ConfigurationManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Base client for executing REST API requests using REST-Assured.
 *
 * <p>This class provides a thin, reusable wrapper around the REST-Assured library
 * that handles common concerns such as base URI configuration, connection/read
 * timeouts, default content-type headers, and request/response logging. All HTTP
 * methods (GET, POST, PUT, PATCH, DELETE) are exposed as simple, one-line calls.</p>
 *
 * <p>The client is initialised with the base URL sourced from
 * {@link ConfigurationManager} (no-arg constructor) or an explicit URL can be
 * supplied directly.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * RestApiClient client = new RestApiClient("https://jsonplaceholder.typicode.com");
 *
 * // Simple GET
 * Response all = client.get("/posts");
 *
 * // GET by ID
 * Response single = client.getById("/posts", 1);
 *
 * // POST with body
 * PostRequest body = PostRequest.builder().title("Hello").body("World").userId(1).build();
 * Response created = client.post("/posts", body);
 * }</pre>
 *
 * @author automation-framework
 * @version 1.0.0
 * @see GraphQLClient
 * @see ConfigurationManager
 */
public class RestApiClient {

    private static final Logger logger = LogManager.getLogger(RestApiClient.class);

    /** Shared REST-Assured request specification applied to every request. */
    private final RequestSpecification requestSpec;

    /**
     * Creates a {@code RestApiClient} using the base URL from {@link ConfigurationManager}.
     *
     * <p>Connection and read timeout values are also sourced from
     * {@link ConfigurationManager}.</p>
     */
    public RestApiClient() {
        this(ConfigurationManager.getInstance().getBaseUrl());
    }

    /**
     * Creates a {@code RestApiClient} targeting the supplied base URL.
     *
     * <p>Connection and read timeout values are sourced from {@link ConfigurationManager}.
     * Both the method and URI are logged for every outgoing request.</p>
     *
     * @param baseUrl the root URL (scheme + host [+ port]) to use for all requests
     *                (must not be {@code null} or empty)
     */
    public RestApiClient(String baseUrl) {
        ConfigurationManager config = ConfigurationManager.getInstance();
        RestAssured.baseURI = baseUrl;

        this.requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(RestAssured.config()
                        .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", config.getConnectionTimeout())
                                .setParam("http.socket.timeout", config.getReadTimeout())))
                .log(io.restassured.filter.log.LogDetail.METHOD)
                .log(io.restassured.filter.log.LogDetail.URI)
                .build();

        logger.info("RestApiClient initialized with base URL: {}", baseUrl);
    }

    /**
     * Sends an HTTP GET request to the given endpoint path.
     *
     * @param endpoint the path relative to the base URL (e.g. {@code "/posts"})
     * @return the server's {@link Response}
     */
    public Response get(String endpoint) {
        logger.info("GET {}", endpoint);
        Response response = RestAssured.given()
                .spec(requestSpec)
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
        logger.info("Response status: {}", response.getStatusCode());
        return response;
    }

    /**
     * Sends an HTTP GET request with query parameters appended to the URL.
     *
     * @param endpoint    the path relative to the base URL (e.g. {@code "/posts"})
     * @param queryParams a map of query-parameter names to values (must not be {@code null})
     * @return the server's {@link Response}
     */
    public Response get(String endpoint, Map<String, Object> queryParams) {
        logger.info("GET {} with params: {}", endpoint, queryParams);
        Response response = RestAssured.given()
                .spec(requestSpec)
                .queryParams(queryParams)
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
        logger.info("Response status: {}", response.getStatusCode());
        return response;
    }

    /**
     * Sends an HTTP GET request for the resource identified by the given numeric ID.
     *
     * <p>The final URL is constructed by appending {@code "/" + id} to {@code endpoint}.</p>
     *
     * @param endpoint the path relative to the base URL (e.g. {@code "/posts"})
     * @param id       the numeric resource identifier
     * @return the server's {@link Response}
     */
    public Response getById(String endpoint, int id) {
        logger.info("GET {}/{}", endpoint, id);
        Response response = RestAssured.given()
                .spec(requestSpec)
                .when()
                .get(endpoint + "/" + id)
                .then()
                .extract()
                .response();
        logger.info("Response status: {}", response.getStatusCode());
        return response;
    }

    /**
     * Sends an HTTP POST request with a serialisable body.
     *
     * <p>The {@code body} object is serialised to JSON using Jackson (via REST-Assured's
     * built-in object-mapping support).</p>
     *
     * @param endpoint the path relative to the base URL (e.g. {@code "/posts"})
     * @param body     the request body object to serialise and send (must not be {@code null})
     * @return the server's {@link Response}
     */
    public Response post(String endpoint, Object body) {
        logger.info("POST {}", endpoint);
        Response response = RestAssured.given()
                .spec(requestSpec)
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
        logger.info("Response status: {}", response.getStatusCode());
        return response;
    }

    /**
     * Sends an HTTP PUT request to fully replace the resource with the given ID.
     *
     * <p>The final URL is constructed by appending {@code "/" + id} to {@code endpoint}.</p>
     *
     * @param endpoint the path relative to the base URL (e.g. {@code "/posts"})
     * @param id       the numeric resource identifier
     * @param body     the replacement resource body to serialise and send (must not be {@code null})
     * @return the server's {@link Response}
     */
    public Response put(String endpoint, int id, Object body) {
        logger.info("PUT {}/{}", endpoint, id);
        Response response = RestAssured.given()
                .spec(requestSpec)
                .body(body)
                .when()
                .put(endpoint + "/" + id)
                .then()
                .extract()
                .response();
        logger.info("Response status: {}", response.getStatusCode());
        return response;
    }

    /**
     * Sends an HTTP PATCH request to partially update the resource with the given ID.
     *
     * <p>The final URL is constructed by appending {@code "/" + id} to {@code endpoint}.</p>
     *
     * @param endpoint the path relative to the base URL (e.g. {@code "/posts"})
     * @param id       the numeric resource identifier
     * @param body     the partial-update body to serialise and send (must not be {@code null})
     * @return the server's {@link Response}
     */
    public Response patch(String endpoint, int id, Object body) {
        logger.info("PATCH {}/{}", endpoint, id);
        Response response = RestAssured.given()
                .spec(requestSpec)
                .body(body)
                .when()
                .patch(endpoint + "/" + id)
                .then()
                .extract()
                .response();
        logger.info("Response status: {}", response.getStatusCode());
        return response;
    }

    /**
     * Sends an HTTP DELETE request for the resource identified by the given numeric ID.
     *
     * <p>The final URL is constructed by appending {@code "/" + id} to {@code endpoint}.</p>
     *
     * @param endpoint the path relative to the base URL (e.g. {@code "/posts"})
     * @param id       the numeric resource identifier
     * @return the server's {@link Response}
     */
    public Response delete(String endpoint, int id) {
        logger.info("DELETE {}/{}", endpoint, id);
        Response response = RestAssured.given()
                .spec(requestSpec)
                .when()
                .delete(endpoint + "/" + id)
                .then()
                .extract()
                .response();
        logger.info("Response status: {}", response.getStatusCode());
        return response;
    }

    /**
     * Sends an HTTP POST request with a body and additional custom headers.
     *
     * <p>Custom headers are merged with the default headers defined in the shared
     * request specification; if a key conflicts, the value supplied in
     * {@code headers} takes precedence.</p>
     *
     * @param endpoint the path relative to the base URL (e.g. {@code "/posts"})
     * @param body     the request body object to serialise and send (must not be {@code null})
     * @param headers  a map of additional HTTP header names to values (must not be {@code null})
     * @return the server's {@link Response}
     */
    public Response postWithHeaders(String endpoint, Object body, Map<String, String> headers) {
        logger.info("POST {} with custom headers", endpoint);
        Response response = RestAssured.given()
                .spec(requestSpec)
                .headers(headers)
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
        logger.info("Response status: {}", response.getStatusCode());
        return response;
    }
}
