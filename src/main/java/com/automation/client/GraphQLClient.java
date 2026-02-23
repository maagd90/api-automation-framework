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
 * Client class for executing GraphQL queries and mutations against a GraphQL endpoint.
 *
 * <p>This class wraps REST-Assured to provide a convenient API for sending GraphQL
 * requests. It handles request serialisation, logging, and response extraction.
 * Both parameterised (variable-based) and plain queries are supported.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * GraphQLClient client = new GraphQLClient();
 * Response response = client.executeQuery("{ rockets { id name } }");
 *
 * // With variables
 * Map<String, Object> vars = Map.of("id", "falcon9");
 * Response response = client.executeQuery(ROCKET_BY_ID_QUERY, vars);
 * }</pre>
 *
 * @author automation-framework
 * @version 1.0.0
 * @see RestApiClient
 * @see ConfigurationManager
 */
public class GraphQLClient {

    private static final Logger logger = LogManager.getLogger(GraphQLClient.class);

    /** The fully-qualified URL of the GraphQL endpoint. */
    private final String graphqlUrl;

    /** Jackson mapper used for request-body serialisation. */
    private final ObjectMapper objectMapper;

    /**
     * Creates a {@code GraphQLClient} using the GraphQL URL configured in
     * {@link ConfigurationManager}.
     *
     * <p>The URL is resolved via {@code ConfigurationManager.getInstance().getGraphQLUrl()}.</p>
     */
    public GraphQLClient() {
        this.graphqlUrl = ConfigurationManager.getInstance().getGraphQLUrl();
        this.objectMapper = new ObjectMapper();
        logger.info("GraphQLClient initialized with URL: {}", graphqlUrl);
    }

    /**
     * Creates a {@code GraphQLClient} targeting the supplied GraphQL endpoint URL.
     *
     * @param graphqlUrl the fully-qualified URL of the GraphQL endpoint (must not be {@code null})
     */
    public GraphQLClient(String graphqlUrl) {
        this.graphqlUrl = graphqlUrl;
        this.objectMapper = new ObjectMapper();
        logger.info("GraphQLClient initialized with URL: {}", graphqlUrl);
    }

    /**
     * Executes a GraphQL query without variables.
     *
     * <p>Equivalent to calling {@link #executeQuery(String, Map)} with a {@code null}
     * variables map.</p>
     *
     * @param query the GraphQL query string (must not be {@code null} or empty)
     * @return the raw REST-Assured {@link Response} from the server
     * @see #executeQuery(String, Map)
     */
    public Response executeQuery(String query) {
        return executeQuery(query, null);
    }

    /**
     * Executes a GraphQL query with optional variables.
     *
     * <p>The query and variables are serialised into a JSON request body of the form:
     * <pre>{@code { "query": "...", "variables": { ... } }}</pre>
     * Variables are omitted from the body when {@code variables} is {@code null} or empty.</p>
     *
     * @param query     the GraphQL query string (must not be {@code null} or empty)
     * @param variables a map of variable name-to-value pairs; may be {@code null} or empty
     * @return the raw REST-Assured {@link Response} from the server
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
     * <p>GraphQL mutations are sent as POST requests identical in structure to queries.
     * The mutation string and variables are serialised into a JSON body.</p>
     *
     * @param mutation  the GraphQL mutation string (must not be {@code null} or empty)
     * @param variables a map of variable name-to-value pairs; may be {@code null} or empty
     * @return the raw REST-Assured {@link Response} from the server
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
     * Returns {@code true} if the GraphQL response body contains an {@code errors} field.
     *
     * <p>According to the GraphQL specification, a response may include both {@code data}
     * and {@code errors} fields simultaneously, so this check alone does not imply that
     * the request failed completely.</p>
     *
     * @param response the response received from the GraphQL server (must not be {@code null})
     * @return {@code true} if an {@code errors} array is present in the response; {@code false} otherwise
     */
    public boolean hasErrors(Response response) {
        return response.jsonPath().get("errors") != null;
    }

    /**
     * Returns {@code true} if the GraphQL response body contains a non-null {@code data} field.
     *
     * @param response the response received from the GraphQL server (must not be {@code null})
     * @return {@code true} if a {@code data} field is present in the response; {@code false} otherwise
     */
    public boolean hasData(Response response) {
        return response.jsonPath().get("data") != null;
    }
}
