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

@Epic("REST API Testing")
@Feature("Users API")
@Listeners(TestListener.class)
public class UserTests {

    private static final Logger logger = LogManager.getLogger(UserTests.class);
    private RestApiClient client;

    @BeforeClass
    public void setUp() {
        client = new RestApiClient(ApiConstants.JSONPLACEHOLDER_BASE_URL);
        logger.info("UserTests setup complete");
    }

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

    @Test
    @Story("Error Handling")
    @Description("Verify 404 for non-existent user")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNonExistentUser() {
        Response response = client.getById(ApiConstants.USERS_ENDPOINT, 9999);
        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_NOT_FOUND);
    }

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
