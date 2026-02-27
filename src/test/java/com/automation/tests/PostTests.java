package com.automation.tests;

import com.automation.constants.ApiConstants;
import com.automation.models.Post;
import com.automation.utils.AssertionUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Test class containing CRUD test cases for the JSONPlaceholder /posts endpoint.
 *
 * @author api-automation-framework
 * @version 1.0.0
 */
public class PostTests {

    /** Post ID used in single-resource tests. */
    private static final int TEST_POST_ID = 1;

    /** User ID used in create/update tests. */
    private static final int TEST_USER_ID = 1;

    /**
     * Configures Rest Assured base URI before any test in this class runs.
     */
    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = ApiConstants.BASE_URL;
    }

    /**
     * Test 1: GET /posts – retrieves all posts and validates the response.
     * <p>Verifies:
     * <ul>
     *   <li>Status code 200</li>
     *   <li>Response body is not empty</li>
     *   <li>At least one post is returned</li>
     *   <li>Each post has userId, id, title, and body populated</li>
     * </ul>
     */
    @Test(description = "GET all posts returns 200 and a non-empty list with required fields")
    public void testGetAllPosts() {
        Response response = RestAssured
                .given()
                    .contentType(ContentType.JSON)
                .when()
                    .get(ApiConstants.POSTS_ENDPOINT)
                .then()
                    .extract().response();

        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_OK);
        AssertionUtils.assertBodyNotEmpty(response);

        List<Post> posts = response.jsonPath().getList(".", Post.class);
        Assert.assertFalse(posts.isEmpty(), "Post list must not be empty");

        for (Post post : posts) {
            AssertionUtils.assertPositiveId(post.getId(), "id");
            AssertionUtils.assertPositiveId(post.getUserId(), "userId");
            AssertionUtils.assertFieldNotEmpty(post.getTitle(), "title");
            AssertionUtils.assertFieldNotEmpty(post.getBody(), "body");
        }
    }

    /**
     * Test 2: GET /posts/{id} – retrieves a single post and validates the response.
     * <p>Verifies:
     * <ul>
     *   <li>Status code 200</li>
     *   <li>Post ID matches the requested ID</li>
     *   <li>All required fields are populated</li>
     * </ul>
     */
    @Test(description = "GET single post returns 200 and correct post data")
    public void testGetSinglePost() {
        Response response = RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .pathParam("id", TEST_POST_ID)
                .when()
                    .get(ApiConstants.POST_BY_ID_ENDPOINT)
                .then()
                    .extract().response();

        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_OK);
        AssertionUtils.assertBodyNotEmpty(response);

        Post post = response.as(Post.class);
        AssertionUtils.assertIntEquals(post.getId(), TEST_POST_ID,
                "Post ID should match requested ID");
        AssertionUtils.assertPositiveId(post.getUserId(), "userId");
        AssertionUtils.assertFieldNotEmpty(post.getTitle(), "title");
        AssertionUtils.assertFieldNotEmpty(post.getBody(), "body");
    }

    /**
     * Test 3: POST /posts – creates a new post and validates the response.
     * <p>Verifies:
     * <ul>
     *   <li>Status code 201</li>
     *   <li>Response contains the created post with an assigned ID</li>
     *   <li>Sent fields are echoed back in the response</li>
     * </ul>
     */
    @Test(description = "POST create new post returns 201 with assigned ID and correct fields")
    public void testCreatePost() {
        Post newPost = new Post(TEST_USER_ID, "Test Title", "Test body content for the new post.");

        Response response = RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body(newPost)
                .when()
                    .post(ApiConstants.POSTS_ENDPOINT)
                .then()
                    .extract().response();

        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_CREATED);
        AssertionUtils.assertBodyNotEmpty(response);

        Post created = response.as(Post.class);
        AssertionUtils.assertPositiveId(created.getId(), "id");
        AssertionUtils.assertIntEquals(created.getUserId(), newPost.getUserId(),
                "userId should match sent value");
        AssertionUtils.assertStringEquals(created.getTitle(), newPost.getTitle(),
                "title should match sent value");
        AssertionUtils.assertStringEquals(created.getBody(), newPost.getBody(),
                "body should match sent value");
    }

    /**
     * Test 4: PUT /posts/{id} – updates an existing post and validates the response.
     * <p>Verifies:
     * <ul>
     *   <li>Status code 200</li>
     *   <li>Updated fields in the response match the sent data</li>
     * </ul>
     */
    @Test(description = "PUT update post returns 200 with updated fields")
    public void testUpdatePost() {
        Post updatedPost = new Post(TEST_USER_ID, TEST_POST_ID, "Updated Title", "Updated body content.");

        Response response = RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .pathParam("id", TEST_POST_ID)
                    .body(updatedPost)
                .when()
                    .put(ApiConstants.POST_BY_ID_ENDPOINT)
                .then()
                    .extract().response();

        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_OK);
        AssertionUtils.assertBodyNotEmpty(response);

        Post result = response.as(Post.class);
        AssertionUtils.assertIntEquals(result.getId(), TEST_POST_ID,
                "Post ID should match updated ID");
        AssertionUtils.assertStringEquals(result.getTitle(), updatedPost.getTitle(),
                "title should match updated value");
        AssertionUtils.assertStringEquals(result.getBody(), updatedPost.getBody(),
                "body should match updated value");
    }

    /**
     * Test 5: DELETE /posts/{id} – deletes a post and validates the response.
     * <p>Verifies:
     * <ul>
     *   <li>Status code 200</li>
     *   <li>Response body is an empty JSON object ({})</li>
     * </ul>
     */
    @Test(description = "DELETE post returns 200 confirming deletion")
    public void testDeletePost() {
        Response response = RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .pathParam("id", TEST_POST_ID)
                .when()
                    .delete(ApiConstants.POST_BY_ID_ENDPOINT)
                .then()
                    .extract().response();

        AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_OK);
        // JSONPlaceholder returns an empty JSON object {} on successful deletion
        String body = response.getBody().asString().trim();
        Assert.assertNotNull(body, "Response body must not be null");
        Assert.assertTrue(body.equals("{}") || body.isEmpty(),
                "Response body should be an empty JSON object '{}' but was: " + body);
    }
}
