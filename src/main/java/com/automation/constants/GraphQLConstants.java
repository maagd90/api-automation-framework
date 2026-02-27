package com.automation.constants;

/**
 * Compile-time constants for the GraphQL testing module.
 *
 * <p>This utility class centralises all GraphQL query/mutation strings, endpoint URLs,
 * and response-field names used across the GraphQL test suite. Keeping them here
 * avoids duplication and makes it easy to update a query in one place.</p>
 *
 * <p>All query strings use Java text-block syntax (Java 15+) for readability.</p>
 *
 * <p>This class is not instantiable; all members are {@code public static final}.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * GraphQLClient client = new GraphQLClient(GraphQLConstants.SPACEX_GRAPHQL_URL);
 * Response response = client.executeQuery(GraphQLConstants.ROCKETS_QUERY);
 * }</pre>
 *
 * @author api-automation-framework
 * @version 1.0.0
 * @see ApiConstants
 */
public final class GraphQLConstants {

    /**
     * Private constructor – prevents instantiation of this constants class.
     */
    private GraphQLConstants() {}

    // -----------------------------------------------------------------------
    // Endpoint URLs
    // -----------------------------------------------------------------------

    /** Base URL of the SpaceX public GraphQL API used for testing. */
    public static final String SPACEX_GRAPHQL_URL = "https://spacex-production.up.railway.app/";

    // -----------------------------------------------------------------------
    // GraphQL Queries
    // -----------------------------------------------------------------------

    /**
     * Query that retrieves a list of all SpaceX rockets with key descriptive fields.
     *
     * <p>Returned fields: {@code id}, {@code name}, {@code description}, {@code active},
     * {@code country}, {@code company}, {@code first_flight}, {@code height.meters},
     * {@code mass.kg}.</p>
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
     * Query that retrieves the five most recent past SpaceX launches.
     *
     * <p>Returned fields: {@code id}, {@code mission_name}, {@code launch_date_utc},
     * {@code launch_success}, {@code rocket.rocket_name}.</p>
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
     * Parameterised query that retrieves a single SpaceX rocket by its ID.
     *
     * <p>Expects a variable {@code $id} of GraphQL type {@code ID!}.
     * Returned fields: {@code id}, {@code name}, {@code description},
     * {@code active}, {@code country}.</p>
     *
     * <p>Example variable map: {@code Map.of("id", "falcon9")}</p>
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
     * Query that retrieves SpaceX company information.
     *
     * <p>Returned fields: {@code name}, {@code founder}, {@code founded},
     * {@code employees}, {@code vehicles}, {@code launch_sites}, {@code test_sites},
     * {@code ceo}, {@code cto}, {@code coo}, {@code headquarters} (address, city, state),
     * {@code summary}.</p>
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
     * Intentionally invalid query used to verify that the API correctly returns
     * a {@code 400 Bad Request} response with an {@code errors} payload.
     */
    public static final String INVALID_QUERY = """
            {
              invalidField {
                nonExistentField
              }
            }
            """;

    // -----------------------------------------------------------------------
    // GraphQL Response Field Names
    // -----------------------------------------------------------------------

    /** Top-level field name in a successful GraphQL response: {@code "data"}. */
    public static final String DATA_FIELD = "data";

    /** Top-level field name present when a GraphQL response contains errors: {@code "errors"}. */
    public static final String ERRORS_FIELD = "errors";

    /** Field name for the human-readable error description within an error object: {@code "message"}. */
    public static final String MESSAGE_FIELD = "message";
}
