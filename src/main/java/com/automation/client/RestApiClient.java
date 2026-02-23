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

public class RestApiClient {

    private static final Logger logger = LogManager.getLogger(RestApiClient.class);
    private final RequestSpecification requestSpec;

    public RestApiClient() {
        this(ConfigurationManager.getInstance().getBaseUrl());
    }

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
