package com.automation.client;

import com.automation.config.ConfigurationManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class GraphQLClient {

    private static final Logger logger = LogManager.getLogger(GraphQLClient.class);
    private final String graphqlUrl;
    private final ObjectMapper objectMapper;

    public GraphQLClient() {
        this.graphqlUrl = ConfigurationManager.getInstance().getGraphQLUrl();
        this.objectMapper = new ObjectMapper();
        logger.info("GraphQLClient initialized with URL: {}", graphqlUrl);
    }

    public GraphQLClient(String graphqlUrl) {
        this.graphqlUrl = graphqlUrl;
        this.objectMapper = new ObjectMapper();
        logger.info("GraphQLClient initialized with URL: {}", graphqlUrl);
    }

    public Response executeQuery(String query) {
        return executeQuery(query, null);
    }

    public Response executeQuery(String query, Map<String, Object> variables) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);
        if (variables != null && !variables.isEmpty()) {
            requestBody.put("variables", variables);
        }

        logger.info("Executing GraphQL query to: {}", graphqlUrl);
        logger.debug("Query: {}", query);

        return RestAssured.given()
                .baseUri(graphqlUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .extract()
                .response();
    }

    public Response executeMutation(String mutation, Map<String, Object> variables) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", mutation);
        if (variables != null && !variables.isEmpty()) {
            requestBody.put("variables", variables);
        }

        logger.info("Executing GraphQL mutation to: {}", graphqlUrl);
        logger.debug("Mutation: {}", mutation);

        return RestAssured.given()
                .baseUri(graphqlUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .extract()
                .response();
    }

    public boolean hasErrors(Response response) {
        return response.jsonPath().get("errors") != null;
    }

    public boolean hasData(Response response) {
        return response.jsonPath().get("data") != null;
    }
}
