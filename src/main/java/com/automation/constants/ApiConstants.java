package com.automation.constants;

/**
 * Compile-time constants for the JSONPlaceholder REST API.
 *
 * <p>This utility class centralises all endpoint paths, HTTP header names, status codes,
 * query-parameter keys, and other values used across the REST API test suite so that
 * they can be changed in a single place.
 *
 * <p>Example usage:
 * <pre>{@code
 * RestApiClient client = new RestApiClient(ApiConstants.JSONPLACEHOLDER_BASE_URL);
 * Response response = client.get(ApiConstants.POSTS_ENDPOINT);
 * AssertionUtils.assertStatusCode(response, ApiConstants.STATUS_OK);
 * }</pre>
 *
 * @see com.automation.client.RestApiClient
 * @see com.automation.utils.AssertionUtils
 */
public final class ApiConstants {

    /** Utility class – do not instantiate. */
    private ApiConstants() {}

    // -------------------------------------------------------------------------
    // Base URLs
    // -------------------------------------------------------------------------

    /** Base URL for the JSONPlaceholder fake REST API. */
    public static final String JSONPLACEHOLDER_BASE_URL = "https://jsonplaceholder.typicode.com";

    // -------------------------------------------------------------------------
    // Endpoints
    // -------------------------------------------------------------------------

    /** Endpoint path for the Posts resource. */
    public static final String POSTS_ENDPOINT = "/posts";

    /** Endpoint path for the Users resource. */
    public static final String USERS_ENDPOINT = "/users";

    /** Endpoint path for the Comments resource. */
    public static final String COMMENTS_ENDPOINT = "/comments";

    /** Endpoint path for the Albums resource. */
    public static final String ALBUMS_ENDPOINT = "/albums";

    /** Endpoint path for the Photos resource. */
    public static final String PHOTOS_ENDPOINT = "/photos";

    /** Endpoint path for the Todos resource. */
    public static final String TODOS_ENDPOINT = "/todos";

    // -------------------------------------------------------------------------
    // HTTP Header Names
    // -------------------------------------------------------------------------

    /** {@code Content-Type} header name. */
    public static final String CONTENT_TYPE = "Content-Type";

    /** {@code Accept} header name. */
    public static final String ACCEPT = "Accept";

    /** {@code Authorization} header name. */
    public static final String AUTHORIZATION = "Authorization";

    /** MIME type for JSON content. */
    public static final String APPLICATION_JSON = "application/json";

    // -------------------------------------------------------------------------
    // Query Parameter Keys
    // -------------------------------------------------------------------------

    /** Query parameter key for filtering by user ID. */
    public static final String USER_ID_PARAM = "userId";

    /** Query parameter key for filtering by post ID. */
    public static final String POST_ID_PARAM = "postId";

    // -------------------------------------------------------------------------
    // HTTP Status Codes
    // -------------------------------------------------------------------------

    /** HTTP 200 OK. */
    public static final int STATUS_OK = 200;

    /** HTTP 201 Created. */
    public static final int STATUS_CREATED = 201;

    /** HTTP 204 No Content. */
    public static final int STATUS_NO_CONTENT = 204;

    /** HTTP 400 Bad Request. */
    public static final int STATUS_BAD_REQUEST = 400;

    /** HTTP 401 Unauthorized. */
    public static final int STATUS_UNAUTHORIZED = 401;

    /** HTTP 403 Forbidden. */
    public static final int STATUS_FORBIDDEN = 403;

    /** HTTP 404 Not Found. */
    public static final int STATUS_NOT_FOUND = 404;

    /** HTTP 500 Internal Server Error. */
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    // -------------------------------------------------------------------------
    // Timeouts (milliseconds)
    // -------------------------------------------------------------------------

    /** Default request timeout in milliseconds. */
    public static final int DEFAULT_TIMEOUT = 10000;

    /** Short request timeout in milliseconds for fast endpoints. */
    public static final int SHORT_TIMEOUT = 5000;

    /** Long request timeout in milliseconds for slow endpoints. */
    public static final int LONG_TIMEOUT = 30000;

    // -------------------------------------------------------------------------
    // Test Data Expectations
    // -------------------------------------------------------------------------

    /** Total number of posts available in the JSONPlaceholder dataset. */
    public static final int TOTAL_POSTS = 100;

    /** Total number of users available in the JSONPlaceholder dataset. */
    public static final int TOTAL_USERS = 10;

    /** Maximum post ID in the JSONPlaceholder dataset. */
    public static final int MAX_POST_ID = 100;
}
