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
 * HTTP client for REST API interactions using REST-Assured.
 *
 * <p>Provides methods to perform standard HTTP operations (GET, POST, PUT, PATCH, DELETE)
 * against REST API endpoints. This client is pre-configured with the base URL, content type,
 * and timeout settings loaded from {@link ConfigurationManager}.
 *
 * <p>Example usage:
 * <pre>{@code
 * RestApiClient client = new RestApiClient();
 * Response response = client.get("/posts");
 * response = client.getById("/posts", 1);
 *
 * PostRequest body = PostRequest.builder().title("Hello").body("World").userId(1).build();
 * Response created = client.post("/posts", body);
 * }</pre>
 *
 * @see ConfigurationManager
 * @see io.restassured.RestAssured
 */
public class RestApiClient {

    private static final Logger logger = LogManager.getLogger(RestApiClient.class);

    /** The underlying REST-Assured request specification used for all requests. */
    private final RequestSpecification requestSpec;

    /**
     * Creates a {@code RestApiClient} using the base URL from {@link ConfigurationManager}.
     */
    public RestApiClient() {
        this(ConfigurationManager.getInstance().getBaseUrl());
    }

    /**
     * Creates a {@code RestApiClient} configured for the given base URL.
     *
     * @param baseUrl the base URL for all HTTP requests (e.g. {@code "https://api.example.com"})
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
     * Sends a GET request to the given endpoint.
     *
     * @param endpoint the path relative to the base URL (e.g. {@code "/posts"})
     * @return the HTTP {@link Response}
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
     * Sends a GET request to the given endpoint with query parameters.
     *
     * @param endpoint    the path relative to the base URL
     * @param queryParams key-value pairs to append as query parameters
     * @return the HTTP {@link Response}
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
     * Sends a GET request for a resource identified by its numeric {@code id}.
     *
     * @param endpoint the path relative to the base URL (e.g. {@code "/posts"})
     * @param id       the resource identifier appended to the endpoint
     * @return the HTTP {@link Response}
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
     * Sends a POST request with the given body to the specified endpoint.
     *
     * @param endpoint the path relative to the base URL
     * @param body     the request body object serialized to JSON
     * @return the HTTP {@link Response}
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
     * Sends a PUT request to update the resource at {@code endpoint/id}.
     *
     * @param endpoint the path relative to the base URL
     * @param id       the resource identifier
     * @param body     the replacement request body
     * @return the HTTP {@link Response}
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
     * Sends a PATCH request to partially update the resource at {@code endpoint/id}.
     *
     * @param endpoint the path relative to the base URL
     * @param id       the resource identifier
     * @param body     the partial update body
     * @return the HTTP {@link Response}
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
     * Sends a DELETE request to remove the resource at {@code endpoint/id}.
     *
     * @param endpoint the path relative to the base URL
     * @param id       the resource identifier
     * @return the HTTP {@link Response}
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
     * Sends a POST request with custom HTTP headers.
     *
     * @param endpoint the path relative to the base URL
     * @param body     the request body object
     * @param headers  additional HTTP headers to include
     * @return the HTTP {@link Response}
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
