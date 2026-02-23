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

/**
 * End-to-end tests for the JSONPlaceholder Posts REST API.
 *
 * <p>Covers the full CRUD lifecycle (create, read, update, delete) as well as
 * filtering, data-driven execution and basic error-handling scenarios.
 *
 * <p>The tests rely on {@link RestApiClient} for HTTP transport and on
 * {@link ResponseValidator} / {@link AssertionUtils} for assertions. Test execution
 * is reported to Allure via the {@link TestListener}.
 *
 * <p>Base URL: {@link ApiConstants#JSONPLACEHOLDER_BASE_URL}
 *
 * @see UserTests
 * @see RestApiClient
 * @see TestDataBuilder
 */
@Epic("REST API Testing")
@Feature("Posts API")
@Listeners(TestListener.class)
public class PostTests {

    private static final Logger logger = LogManager.getLogger(PostTests.class);

    /** Shared REST client configured for the JSONPlaceholder base URL. */
    private RestApiClient client;

    /**
     * Initialises the {@link RestApiClient} before any test in the class runs.
     */
    @BeforeClass
    public void setUp() {
        client = new RestApiClient(ApiConstants.JSONPLACEHOLDER_BASE_URL);
        logger.info("PostTests setup complete");
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    /**
     * Verifies that creating a new post via HTTP POST returns HTTP 201 and the
     * response body echoes the submitted data.
     *
     * @see ApiConstants#STATUS_CREATED
     * @see TestDataBuilder#buildPostRequest(int, String, String)
     */
    @Test
    @Story("Create Post")
    @Description("Verify that creating a new post returns status 201 and correct data")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePost() {
        PostRequest postRequest = TestDataBuilder.buildPostRequest(1, "Test Title", "Test Body Content");

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

    /**
     * Verifies that an invalid (empty) post creation request results in an error.
     * JSONPlaceholder still returns 201 for empty bodies; this test documents that behaviour.
     */
    @Test
    @Story("Create Post")
    @Description("Verify error handling for post creation with missing fields")
    @Severity(SeverityLevel.NORMAL)
    public void testInvalidPostCreation() {
        // JSONPlaceholder accepts empty bodies – assert that at minimum the request succeeds
        PostRequest emptyRequest = new PostRequest();
        Response response = client.post(ApiConstants.POSTS_ENDPOINT, emptyRequest);
        // JSONPlaceholder returns 201 even for empty bodies; document and assert that
        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_CREATED);
        logger.info("Invalid post creation returned status: {}", response.getStatusCode());
    }

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    /**
     * Verifies that fetching a post by ID via HTTP GET returns HTTP 200 and the
     * expected resource structure.
     *
     * @see ApiConstants#STATUS_OK
     */
    @Test
    @Story("Read Post")
    @Description("Verify that reading an existing post returns correct structure and data")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetPost() {
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

    /**
     * Verifies that fetching all posts returns exactly {@link ApiConstants#TOTAL_POSTS} items.
     */
    @Test
    @Story("Read Post")
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

    /**
     * Verifies that filtering posts by {@code userId} returns only posts belonging to that user.
     *
     * @see ApiConstants#USER_ID_PARAM
     */
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

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    /**
     * Verifies that a full replacement (PUT) of a post returns HTTP 200 and reflects
     * the updated title and body.
     */
    @Test
    @Story("Update Post")
    @Description("Verify that updating a post with PUT reflects the changes")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdatePost() {
        int postId = 1;
        PostRequest updateRequest = TestDataBuilder.buildPostRequest(1, "Updated Title", "Updated Body");

        Response response = client.put(ApiConstants.POSTS_ENDPOINT, postId, updateRequest);

        ResponseValidator.of(response)
                .statusCode(ApiConstants.STATUS_OK)
                .fieldEquals("id", postId)
                .fieldEquals("title", "Updated Title")
                .fieldEquals("body", "Updated Body");
    }

    /**
     * Verifies that a partial update (PATCH) of a post's title returns HTTP 200 and
     * reflects the patched value.
     */
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

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    /**
     * Verifies that deleting a post via HTTP DELETE returns HTTP 200.
     */
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

    // -------------------------------------------------------------------------
    // ERROR HANDLING
    // -------------------------------------------------------------------------

    /**
     * Verifies that requesting a non-existent post returns HTTP 404.
     */
    @Test
    @Story("Error Handling")
    @Description("Verify that requesting non-existent post returns 404")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNonExistentPost() {
        Response response = client.getById(ApiConstants.POSTS_ENDPOINT, 99999);
        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_NOT_FOUND);
    }

    // -------------------------------------------------------------------------
    // DATA-DRIVEN
    // -------------------------------------------------------------------------

    /**
     * Data-driven test that creates posts from multiple datasets.
     *
     * @param title  the post title
     * @param body   the post body
     * @param userId the authoring user identifier
     */
    @Test(dataProvider = "postDataProvider")
    @Story("Create Post")
    @Description("Data-driven test: create posts with various datasets")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePostDataDriven(String title, String body, int userId) {
        PostRequest postRequest = TestDataBuilder.buildPostRequest(userId, title, body);
        Response response = client.post(ApiConstants.POSTS_ENDPOINT, postRequest);

        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_CREATED);
        AssertionUtils.assertFieldNotNull(response, "id");
        logger.info("Data-driven post created with title: {}", title);
    }

    /**
     * Provides three datasets for {@link #testCreatePostDataDriven}.
     *
     * @return a 2D array where each row contains: title, body, userId
     */
    @DataProvider(name = "postDataProvider")
    public Object[][] postDataProvider() {
        return new Object[][] {
                {"First Post",  "First post body",  1},
                {"Second Post", "Second post body", 2},
                {"Third Post",  "Third post body",  3}
        };
    }

    // -------------------------------------------------------------------------
    // PERFORMANCE
    // -------------------------------------------------------------------------

    /**
     * Verifies that the GET all posts response time is within acceptable limits.
     */
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
