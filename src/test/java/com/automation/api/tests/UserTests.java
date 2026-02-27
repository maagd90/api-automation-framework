package com.automation.api.tests;

import com.automation.client.RestApiClient;
import com.automation.constants.ApiConstants;
import com.automation.listeners.TestListener;
import com.automation.models.response.UserResponse;
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

import java.util.List;
import java.util.Map;

/**
 * Test class covering read operations and validations for the JSONPlaceholder
 * {@code /users} REST API endpoint.
 *
 * <p>All tests use {@link RestApiClient} pointed at
 * {@link ApiConstants#JSONPLACEHOLDER_BASE_URL} and validate responses with
 * {@link ResponseValidator} and {@link AssertionUtils}.</p>
 *
 * <p>The tests are organised into the following Allure stories:</p>
 * <ul>
 *   <li><b>Get All Users</b> – verifies the full collection and response time.</li>
 *   <li><b>Get User By ID</b> – verifies a single-user response and data mapping.</li>
 *   <li><b>User Response Structure</b> – verifies all mandatory fields are present.</li>
 *   <li><b>User Posts</b> – verifies that cross-resource filtering works correctly.</li>
 *   <li><b>Error Handling</b> – verifies HTTP 404 for a non-existent user.</li>
 *   <li><b>User Email Validation</b> – verifies that every user has a valid email.</li>
 * </ul>
 *
 * @author api-automation-framework
 * @version 1.0.0
 * @see RestApiClient
 * @see UserResponse
 */
@Epic("REST API Testing")
@Feature("Users API")
@Listeners(TestListener.class)
public class UserTests {

    private static final Logger logger = LogManager.getLogger(UserTests.class);
    private RestApiClient client;

    /**
     * Initialises the {@link RestApiClient} before any test in this class runs.
     *
     * <p>The client is pointed at {@link ApiConstants#JSONPLACEHOLDER_BASE_URL}.</p>
     */
    @BeforeClass
    public void setUp() {
        client = new RestApiClient(ApiConstants.JSONPLACEHOLDER_BASE_URL);
        logger.info("UserTests setup complete");
    }

    /**
     * Verifies that {@code GET /users} returns HTTP 200 with exactly
     * {@link ApiConstants#TOTAL_USERS} user records within 5 seconds.
     */
    @Test
    @Story("Get All Users")
    @Description("Verify that getting all users returns correct count")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllUsers() {
        Response response = client.get(ApiConstants.USERS_ENDPOINT);

        ResponseValidator.of(response)
                .statusCode(ApiConstants.STATUS_OK)
                .listSize("$", ApiConstants.TOTAL_USERS)
                .responseTimeBelow(5000);

        logger.info("Retrieved {} users", ApiConstants.TOTAL_USERS);
    }

    /**
     * Verifies that {@code GET /users/1} returns HTTP 200 with the correct user data
     * and that the response can be deserialised into a {@link UserResponse}.
     */
    @Test
    @Story("Get User By ID")
    @Description("Verify that getting a user by ID returns correct user data")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserById() {
        int userId = 1;
        Response response = client.getById(ApiConstants.USERS_ENDPOINT, userId);

        ResponseValidator.of(response)
                .statusCode(ApiConstants.STATUS_OK)
                .fieldEquals("id", userId)
                .fieldNotNull("name")
                .fieldNotNull("username")
                .fieldNotNull("email");

        UserResponse user = response.as(UserResponse.class);
        Assert.assertEquals(user.getId(), userId);
        Assert.assertNotNull(user.getName());
        Assert.assertNotNull(user.getEmail());
        logger.info("Retrieved user: {} ({})", user.getName(), user.getEmail());
    }

    /**
     * Verifies that the {@code GET /users/1} response contains all mandatory top-level
     * fields including the nested {@code address} and {@code company} objects,
     * and that the Content-Type is JSON.
     */
    @Test
    @Story("User Response Structure")
    @Description("Verify that user response has all required fields")
    @Severity(SeverityLevel.NORMAL)
    public void testUserResponseStructure() {
        Response response = client.getById(ApiConstants.USERS_ENDPOINT, 1);

        ResponseValidator.of(response)
                .statusCode(ApiConstants.STATUS_OK)
                .fieldNotNull("id")
                .fieldNotNull("name")
                .fieldNotNull("username")
                .fieldNotNull("email")
                .fieldNotNull("address")
                .fieldNotNull("phone")
                .fieldNotNull("website")
                .fieldNotNull("company");

        AssertionUtils.assertContentType(response, "application/json");
    }

    /**
     * Verifies that {@code GET /posts?userId=1} returns HTTP 200 with a non-empty list,
     * confirming that cross-resource filtering by user ID works correctly.
     */
    @Test
    @Story("User Posts")
    @Description("Verify that getting posts for a user returns non-empty list")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserPosts() {
        int userId = 1;
        Map<String, Object> params = Map.of(ApiConstants.USER_ID_PARAM, userId);
        Response response = client.get(ApiConstants.POSTS_ENDPOINT, params);

        ResponseValidator.of(response)
                .statusCode(ApiConstants.STATUS_OK)
                .listNotEmpty("$");

        List<Map<String, Object>> posts = response.jsonPath().getList("$");
        Assert.assertFalse(posts.isEmpty(), "User should have posts");
        logger.info("User {} has {} posts", userId, posts.size());
    }

    /**
     * Verifies that requesting a user with an ID that does not exist returns HTTP 404.
     */
    @Test
    @Story("Error Handling")
    @Description("Verify 404 for non-existent user")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNonExistentUser() {
        Response response = client.getById(ApiConstants.USERS_ENDPOINT, 9999);
        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_NOT_FOUND);
    }

    /**
     * Verifies that every user in the {@code GET /users} response has a non-null
     * email address that contains the {@code @} character.
     */
    @Test
    @Story("User Email Validation")
    @Description("Verify all users have valid email format")
    @Severity(SeverityLevel.MINOR)
    public void testUsersHaveValidEmails() {
        Response response = client.get(ApiConstants.USERS_ENDPOINT);
        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_OK);

        List<Map<String, Object>> users = response.jsonPath().getList("$");
        for (Map<String, Object> user : users) {
            String email = (String) user.get("email");
            Assert.assertNotNull(email, "User email should not be null");
            Assert.assertTrue(email.contains("@"), "Email should contain @: " + email);
            logger.debug("User {} has email: {}", user.get("id"), email);
        }
    }
}
