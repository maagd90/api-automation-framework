package com.automation.api.tests;

import com.automation.client.RestApiClient;
import com.automation.constants.ApiConstants;
import com.automation.listeners.TestListener;
import com.automation.models.request.PostRequest;
import com.automation.models.response.PostResponse;
import com.automation.utils.AssertionUtils;
import com.automation.utils.ResponseValidator;
import com.automation.utils.TestDataBuilder;
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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Epic("REST API Testing")
@Feature("Posts API")
@Listeners(TestListener.class)
public class PostTests {

    private static final Logger logger = LogManager.getLogger(PostTests.class);
    private RestApiClient client;

    @BeforeClass
    public void setUp() {
        client = new RestApiClient(ApiConstants.JSONPLACEHOLDER_BASE_URL);
        logger.info("PostTests setup complete");
    }

    @Test
    @Story("Create Post")
    @Description("Verify that creating a new post returns status 201 and correct data")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePost() {
        PostRequest postRequest = TestDataBuilder.buildPostRequest(
                "Test Title", "Test Body Content", 1);

        Response response = client.post(ApiConstants.POSTS_ENDPOINT, postRequest);

        ResponseValidator.of(response)
                .statusCode(ApiConstants.STATUS_CREATED)
                .contentType("application/json")
                .fieldNotNull("id")
                .fieldEquals("title", "Test Title")
                .fieldEquals("body", "Test Body Content")
                .fieldEquals("userId", 1);

        PostResponse postResponse = response.as(PostResponse.class);
        Assert.assertNotNull(postResponse.getId(), "Created post should have an ID");
        Assert.assertEquals(postResponse.getTitle(), "Test Title");
        logger.info("Created post with ID: {}", postResponse.getId());
    }

    @Test
    @Story("Read Post")
    @Description("Verify that reading an existing post returns correct structure and data")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetPostById() {
        int postId = 1;
        Response response = client.getById(ApiConstants.POSTS_ENDPOINT, postId);

        ResponseValidator.of(response)
                .statusCode(ApiConstants.STATUS_OK)
                .fieldEquals("id", postId)
                .fieldNotNull("title")
                .fieldNotNull("body")
                .fieldNotNull("userId")
                .responseTimeBelow(5000);

        PostResponse postResponse = response.as(PostResponse.class);
        Assert.assertEquals(postResponse.getId(), postId);
        Assert.assertNotNull(postResponse.getTitle());
        Assert.assertNotNull(postResponse.getBody());
    }

    @Test
    @Story("Update Post")
    @Description("Verify that updating a post with PUT reflects the changes")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdatePost() {
        int postId = 1;
        PostRequest updateRequest = TestDataBuilder.buildPostRequest(
                "Updated Title", "Updated Body", 1);

        Response response = client.put(ApiConstants.POSTS_ENDPOINT, postId, updateRequest);

        ResponseValidator.of(response)
                .statusCode(ApiConstants.STATUS_OK)
                .fieldEquals("id", postId)
                .fieldEquals("title", "Updated Title")
                .fieldEquals("body", "Updated Body");
    }

    @Test
    @Story("Update Post")
    @Description("Verify that partially updating a post with PATCH reflects the changes")
    @Severity(SeverityLevel.NORMAL)
    public void testPatchPost() {
        int postId = 1;
        Map<String, String> partialUpdate = new HashMap<>();
        partialUpdate.put("title", "Patched Title");

        Response response = client.patch(ApiConstants.POSTS_ENDPOINT, postId, partialUpdate);

        ResponseValidator.of(response)
                .statusCode(ApiConstants.STATUS_OK)
                .fieldEquals("title", "Patched Title");
    }

    @Test
    @Story("Delete Post")
    @Description("Verify that deleting a post returns successful deletion response")
    @Severity(SeverityLevel.NORMAL)
    public void testDeletePost() {
        int postId = 1;
        Response response = client.delete(ApiConstants.POSTS_ENDPOINT, postId);

        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_OK);
        logger.info("Post {} deleted successfully", postId);
    }

    @Test
    @Story("Get All Posts")
    @Description("Verify that getting all posts returns correct count and structure")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllPosts() {
        Response response = client.get(ApiConstants.POSTS_ENDPOINT);

        ResponseValidator.of(response)
                .statusCode(ApiConstants.STATUS_OK)
                .listSize("$", ApiConstants.TOTAL_POSTS)
                .responseTimeBelow(10000);

        List<Map<String, Object>> posts = response.jsonPath().getList("$");
        Assert.assertEquals(posts.size(), ApiConstants.TOTAL_POSTS,
                "Should return " + ApiConstants.TOTAL_POSTS + " posts");
    }

    @Test
    @Story("Filter Posts")
    @Description("Verify that filtering posts by userId returns correct results")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPostsByUserId() {
        int userId = 1;
        Map<String, Object> params = new HashMap<>();
        params.put(ApiConstants.USER_ID_PARAM, userId);

        Response response = client.get(ApiConstants.POSTS_ENDPOINT, params);

        ResponseValidator.of(response)
                .statusCode(ApiConstants.STATUS_OK)
                .listNotEmpty("$");

        List<Map<String, Object>> posts = response.jsonPath().getList("$");
        for (Map<String, Object> post : posts) {
            Assert.assertEquals(post.get("userId"), userId,
                    "All posts should belong to user " + userId);
        }
        logger.info("Found {} posts for userId {}", posts.size(), userId);
    }

    @Test
    @Story("Error Handling")
    @Description("Verify that requesting non-existent post returns 404")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNonExistentPost() {
        Response response = client.getById(ApiConstants.POSTS_ENDPOINT, 99999);
        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_NOT_FOUND);
    }

    @Test(dataProvider = "postDataProvider")
    @Story("Create Post")
    @Description("Data-driven test: create posts with various datasets")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePostDataDriven(String title, String body, int userId) {
        PostRequest postRequest = TestDataBuilder.buildPostRequest(title, body, userId);
        Response response = client.post(ApiConstants.POSTS_ENDPOINT, postRequest);

        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_CREATED);
        AssertionUtils.assertFieldNotNull(response, "id");
        logger.info("Data-driven post created with title: {}", title);
    }

    @DataProvider(name = "postDataProvider")
    public Object[][] postDataProvider() {
        return new Object[][] {
                {"First Post", "First post body", 1},
                {"Second Post", "Second post body", 2},
                {"Third Post", "Third post body", 3}
        };
    }

    @Test
    @Story("Response Validation")
    @Description("Verify response time is within acceptable limits")
    @Severity(SeverityLevel.MINOR)
    public void testResponseTime() {
        Response response = client.get(ApiConstants.POSTS_ENDPOINT);
        AssertionUtils.assertResponseTimeBelow(response, 10000);
        logger.info("Response time: {}ms", response.getTime());
    }
}
