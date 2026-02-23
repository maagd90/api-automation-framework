package com.automation.constants;

/**
 * Compile-time constants for the SpaceX GraphQL API.
 *
 * <p>Contains the endpoint URL, pre-built GraphQL query strings, and field-name constants
 * used across the GraphQL test suite.
 *
 * <p>Example usage:
 * <pre>{@code
 * GraphQLClient client = new GraphQLClient(GraphQLConstants.SPACEX_GRAPHQL_URL);
 * Response response = client.executeQuery(GraphQLConstants.ROCKETS_QUERY);
 * }</pre>
 *
 * @see com.automation.client.GraphQLClient
 * @see com.automation.graphql.tests.SpaceXTests
 */
public final class GraphQLConstants {

    /** Utility class – do not instantiate. */
    private GraphQLConstants() {}

    // -------------------------------------------------------------------------
    // Endpoint URLs
    // -------------------------------------------------------------------------

    /** Base URL for the SpaceX GraphQL API. */
    public static final String SPACEX_GRAPHQL_URL = "https://spacex-production.up.railway.app/";

    // -------------------------------------------------------------------------
    // GraphQL Queries
    // -------------------------------------------------------------------------

    /**
     * Query to retrieve all rockets with key fields.
     * Returns: id, name, description, active, country, company, first_flight, height, mass.
     */
    public static final String ROCKETS_QUERY = """
            {
              rockets {
                id
                name
                description
                active
                country
                company
                first_flight
                height {
                  meters
                }
                mass {
                  kg
                }
              }
            }
            """;

    /**
     * Query to retrieve the 5 most recent past launches.
     * Returns: id, mission_name, launch_date_utc, launch_success, rocket name.
     */
    public static final String LAUNCHES_QUERY = """
            {
              launchesPast(limit: 5) {
                id
                mission_name
                launch_date_utc
                launch_success
                rocket {
                  rocket_name
                }
              }
            }
            """;

    /**
     * Parameterised query that fetches a single rocket by its ID.
     * Requires variable: {@code id} (type {@code ID!}).
     */
    public static final String ROCKET_BY_ID_QUERY = """
            query RocketById($id: ID!) {
              rocket(id: $id) {
                id
                name
                description
                active
                country
              }
            }
            """;

    /**
     * Query to retrieve SpaceX company information.
     * Returns company name, founder, executives, headquarters and a summary.
     */
    public static final String COMPANY_INFO_QUERY = """
            {
              company {
                name
                founder
                founded
                employees
                vehicles
                launch_sites
                test_sites
                ceo
                cto
                coo
                headquarters {
                  address
                  city
                  state
                }
                summary
              }
            }
            """;

    /**
     * An intentionally invalid GraphQL query used to verify error-handling behaviour.
     * Sending this query to the server should return an {@code errors} array in the response body.
     */
    public static final String INVALID_QUERY = """
            {
              invalidField {
                nonExistentField
              }
            }
            """;

    // -------------------------------------------------------------------------
    // GraphQL Response Field Names
    // -------------------------------------------------------------------------

    /** Top-level {@code "data"} field present in a successful GraphQL response. */
    public static final String DATA_FIELD = "data";

    /** Top-level {@code "errors"} field present when the query fails. */
    public static final String ERRORS_FIELD = "errors";

    /** Field name for the human-readable error message within an error object. */
    public static final String MESSAGE_FIELD = "message";
}
