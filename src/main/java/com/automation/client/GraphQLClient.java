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

/**
 * HTTP client for GraphQL API interactions.
 *
 * <p>Wraps REST-Assured to send GraphQL queries and mutations as HTTP POST requests
 * to a configured GraphQL endpoint. Supports parameterised queries via GraphQL variables.
 *
 * <p>Example usage:
 * <pre>{@code
 * GraphQLClient client = new GraphQLClient();
 *
 * // Simple query
 * Response response = client.executeQuery("{ rockets { id name } }");
 *
 * // Query with variables
 * Map<String, Object> vars = new HashMap<>();
 * vars.put("id", "falcon9");
 * Response response = client.executeQuery(GraphQLConstants.ROCKET_BY_ID_QUERY, vars);
 *
 * // Check for errors
 * if (client.hasErrors(response)) {
 *     System.out.println("GraphQL error returned");
 * }
 * }</pre>
 *
 * @see ConfigurationManager
 * @see GraphQLConstants
 */
public class GraphQLClient {

    private static final Logger logger = LogManager.getLogger(GraphQLClient.class);

    /** The fully-qualified GraphQL endpoint URL. */
    private final String graphqlUrl;

    /** Jackson mapper used internally for serialisation. */
    private final ObjectMapper objectMapper;

    /**
     * Creates a {@code GraphQLClient} using the GraphQL URL from {@link ConfigurationManager}.
     */
    public GraphQLClient() {
        this.graphqlUrl = ConfigurationManager.getInstance().getGraphQLUrl();
        this.objectMapper = new ObjectMapper();
        logger.info("GraphQLClient initialized with URL: {}", graphqlUrl);
    }

    /**
     * Creates a {@code GraphQLClient} configured for the given GraphQL endpoint URL.
     *
     * @param graphqlUrl the fully-qualified URL of the GraphQL endpoint
     */
    public GraphQLClient(String graphqlUrl) {
        this.graphqlUrl = graphqlUrl;
        this.objectMapper = new ObjectMapper();
        logger.info("GraphQLClient initialized with URL: {}", graphqlUrl);
    }

    /**
     * Executes a GraphQL query without variables.
     *
     * @param query the GraphQL query string
     * @return the HTTP {@link Response} containing the JSON body
     */
    public Response executeQuery(String query) {
        return executeQuery(query, null);
    }

    /**
     * Executes a GraphQL query with optional variables.
     *
     * @param query     the GraphQL query string
     * @param variables a map of variable names to values, or {@code null} if none
     * @return the HTTP {@link Response} containing the JSON body
     */
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

    /**
     * Executes a GraphQL mutation with optional variables.
     *
     * @param mutation  the GraphQL mutation string
     * @param variables a map of variable names to values, or {@code null} if none
     * @return the HTTP {@link Response} containing the JSON body
     */
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

    /**
     * Returns {@code true} when the GraphQL response body contains an {@code "errors"} field.
     *
     * @param response the HTTP response to inspect
     * @return {@code true} if errors were returned, {@code false} otherwise
     */
    public boolean hasErrors(Response response) {
        return response.jsonPath().get("errors") != null;
    }

    /**
     * Returns {@code true} when the GraphQL response body contains a {@code "data"} field.
     *
     * @param response the HTTP response to inspect
     * @return {@code true} if data is present, {@code false} otherwise
     */
    public boolean hasData(Response response) {
        return response.jsonPath().get("data") != null;
    }
}
