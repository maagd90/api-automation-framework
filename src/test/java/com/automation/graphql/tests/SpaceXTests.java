package com.automation.graphql.tests;

import com.automation.client.GraphQLClient;
import com.automation.constants.GraphQLConstants;
import com.automation.listeners.TestListener;
import com.automation.utils.AssertionUtils;
import com.automation.utils.ResponseValidator;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Epic("GraphQL Testing")
@Feature("SpaceX API")
@Listeners(TestListener.class)
public class SpaceXTests {

    private static final Logger logger = LogManager.getLogger(SpaceXTests.class);
    private GraphQLClient graphQLClient;

    @BeforeClass
    public void setUp() {
        graphQLClient = new GraphQLClient(GraphQLConstants.SPACEX_GRAPHQL_URL);
        logger.info("SpaceXTests setup complete");
    }

    @Test
    @Story("Query Rockets")
    @Description("Verify that querying rockets returns valid data")
    @Severity(SeverityLevel.CRITICAL)
    public void testQueryRockets() {
        Response response = graphQLClient.executeQuery(GraphQLConstants.ROCKETS_QUERY);

        ResponseValidator.of(response)
                .statusCode(200)
                .noGraphQLErrors()
                .graphQLDataNotNull("rockets");

        List<Map<String, Object>> rockets = response.jsonPath().getList("data.rockets");
        Assert.assertNotNull(rockets, "Rockets list should not be null");
        Assert.assertFalse(rockets.isEmpty(), "Rockets list should not be empty");

        for (Map<String, Object> rocket : rockets) {
            Assert.assertNotNull(rocket.get("id"), "Rocket should have an ID");
            Assert.assertNotNull(rocket.get("name"), "Rocket should have a name");
        }

        logger.info("Retrieved {} rockets", rockets.size());
    }

    @Test
    @Story("Query Launches")
    @Description("Verify that querying past launches returns valid data")
    @Severity(SeverityLevel.CRITICAL)
    public void testQueryLaunches() {
        Response response = graphQLClient.executeQuery(GraphQLConstants.LAUNCHES_QUERY);

        ResponseValidator.of(response)
                .statusCode(200)
                .noGraphQLErrors()
                .graphQLDataNotNull("launchesPast");

        List<Map<String, Object>> launches = response.jsonPath().getList("data.launchesPast");
        Assert.assertNotNull(launches, "Launches list should not be null");
        Assert.assertFalse(launches.isEmpty(), "Launches list should not be empty");

        for (Map<String, Object> launch : launches) {
            Assert.assertNotNull(launch.get("id"), "Launch should have an ID");
            Assert.assertNotNull(launch.get("mission_name"), "Launch should have a mission name");
        }

        logger.info("Retrieved {} past launches", launches.size());
    }

    @Test
    @Story("Query with Variables")
    @Description("Verify that querying rocket by ID with variables works correctly")
    @Severity(SeverityLevel.NORMAL)
    public void testQueryRocketWithVariables() {
        // First get a valid rocket ID
        Response rocketsResponse = graphQLClient.executeQuery(GraphQLConstants.ROCKETS_QUERY);
        List<Map<String, Object>> rockets = rocketsResponse.jsonPath().getList("data.rockets");
        Assert.assertFalse(rockets == null || rockets.isEmpty(), "Need rockets to test");

        String rocketId = (String) rockets.get(0).get("id");
        logger.info("Querying rocket with ID: {}", rocketId);

        Map<String, Object> variables = new HashMap<>();
        variables.put("id", rocketId);

        Response response = graphQLClient.executeQuery(GraphQLConstants.ROCKET_BY_ID_QUERY, variables);

        ResponseValidator.of(response)
                .statusCode(200)
                .noGraphQLErrors()
                .graphQLDataNotNull("rocket");

        String returnedId = response.jsonPath().getString("data.rocket.id");
        Assert.assertEquals(returnedId, rocketId, "Returned rocket ID should match requested ID");
        logger.info("Successfully retrieved rocket: {}", response.jsonPath().getString("data.rocket.name"));
    }

    @Test
    @Story("Company Info Query")
    @Description("Verify that querying company information returns valid data")
    @Severity(SeverityLevel.NORMAL)
    public void testQueryCompanyInfo() {
        Response response = graphQLClient.executeQuery(GraphQLConstants.COMPANY_INFO_QUERY);

        ResponseValidator.of(response)
                .statusCode(200)
                .noGraphQLErrors()
                .graphQLDataNotNull("company");

        String companyName = response.jsonPath().getString("data.company.name");
        Assert.assertNotNull(companyName, "Company name should not be null");
        Assert.assertFalse(companyName.isEmpty(), "Company name should not be empty");
        logger.info("Company name: {}", companyName);
    }

    @Test
    @Story("Error Handling")
    @Description("Verify that invalid GraphQL queries return appropriate errors")
    @Severity(SeverityLevel.NORMAL)
    public void testInvalidQueryReturnsErrors() {
        Response response = graphQLClient.executeQuery(GraphQLConstants.INVALID_QUERY);

        AssertionUtils.assertStatusCode(response, 400);
        boolean hasErrors = graphQLClient.hasErrors(response);
        Assert.assertTrue(hasErrors, "Invalid query should return errors");
        logger.info("Invalid query correctly returned errors");
    }

    @Test
    @Story("Response Structure")
    @Description("Verify rockets response has expected fields")
    @Severity(SeverityLevel.MINOR)
    public void testRocketResponseStructure() {
        Response response = graphQLClient.executeQuery(GraphQLConstants.ROCKETS_QUERY);

        AssertionUtils.assertStatusCode(response, 200);
        AssertionUtils.assertGraphQLNoErrors(response);

        List<Map<String, Object>> rockets = response.jsonPath().getList("data.rockets");
        Assert.assertFalse(rockets == null || rockets.isEmpty(), "Rockets list should not be empty");

        Map<String, Object> firstRocket = rockets.get(0);
        Assert.assertNotNull(firstRocket.get("id"), "Rocket should have id");
        Assert.assertNotNull(firstRocket.get("name"), "Rocket should have name");
        Assert.assertNotNull(firstRocket.get("country"), "Rocket should have country");
        Assert.assertNotNull(firstRocket.get("company"), "Rocket should have company");
    }
}
