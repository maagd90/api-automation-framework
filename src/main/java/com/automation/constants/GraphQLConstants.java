package com.automation.constants;

public final class GraphQLConstants {

    private GraphQLConstants() {}

    // SpaceX GraphQL API
    public static final String SPACEX_GRAPHQL_URL = "https://spacex-production.up.railway.app/";

    // Queries
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

    // Invalid query for error testing
    public static final String INVALID_QUERY = """
            {
              invalidField {
                nonExistentField
              }
            }
            """;

    // GraphQL response fields
    public static final String DATA_FIELD = "data";
    public static final String ERRORS_FIELD = "errors";
    public static final String MESSAGE_FIELD = "message";
}
