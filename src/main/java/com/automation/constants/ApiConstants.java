package com.automation.constants;

/**
 * Compile-time constants for the REST API testing module.
 *
 * <p>This utility class centralises all string literals and numeric values used
 * across the REST API tests so that changes to an endpoint path, status code, or
 * header name only need to be made in one place.</p>
 *
 * <p>This class is not instantiable; all members are {@code public static final}.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * Response response = client.get(ApiConstants.POSTS_ENDPOINT);
 * assertStatusCode(response, ApiConstants.STATUS_OK);
 * }</pre>
 *
 * @author api-automation-framework
 * @version 1.0.0
 * @see GraphQLConstants
 */
public final class ApiConstants {

    /**
     * Private constructor – prevents instantiation of this constants class.
     */
    private ApiConstants() {}

    // -----------------------------------------------------------------------
    // Base URLs
    // -----------------------------------------------------------------------

    /** Base URL of the JSONPlaceholder fake REST API used for testing. */
    public static final String JSONPLACEHOLDER_BASE_URL = "https://jsonplaceholder.typicode.com";

    // -----------------------------------------------------------------------
    // Endpoints
    // -----------------------------------------------------------------------

    /** Path for the posts resource: {@code /posts}. */
    public static final String POSTS_ENDPOINT = "/posts";

    /** Path for the users resource: {@code /users}. */
    public static final String USERS_ENDPOINT = "/users";

    /** Path for the comments resource: {@code /comments}. */
    public static final String COMMENTS_ENDPOINT = "/comments";

    /** Path for the albums resource: {@code /albums}. */
    public static final String ALBUMS_ENDPOINT = "/albums";

    /** Path for the photos resource: {@code /photos}. */
    public static final String PHOTOS_ENDPOINT = "/photos";

    /** Path for the todos resource: {@code /todos}. */
    public static final String TODOS_ENDPOINT = "/todos";

    // -----------------------------------------------------------------------
    // HTTP Headers
    // -----------------------------------------------------------------------

    /** Standard HTTP {@code Content-Type} header name. */
    public static final String CONTENT_TYPE = "Content-Type";

    /** Standard HTTP {@code Accept} header name. */
    public static final String ACCEPT = "Accept";

    /** Standard HTTP {@code Authorization} header name. */
    public static final String AUTHORIZATION = "Authorization";

    /** MIME type for JSON payloads: {@code application/json}. */
    public static final String APPLICATION_JSON = "application/json";

    // -----------------------------------------------------------------------
    // Query Parameters
    // -----------------------------------------------------------------------

    /** Query-parameter name used to filter resources by user ID: {@code userId}. */
    public static final String USER_ID_PARAM = "userId";

    /** Query-parameter name used to filter resources by post ID: {@code postId}. */
    public static final String POST_ID_PARAM = "postId";

    // -----------------------------------------------------------------------
    // HTTP Status Codes
    // -----------------------------------------------------------------------

    /** HTTP 200 OK – the request succeeded. */
    public static final int STATUS_OK = 200;

    /** HTTP 201 Created – a new resource was successfully created. */
    public static final int STATUS_CREATED = 201;

    /** HTTP 204 No Content – the request succeeded with no response body. */
    public static final int STATUS_NO_CONTENT = 204;

    /** HTTP 400 Bad Request – the request was malformed or invalid. */
    public static final int STATUS_BAD_REQUEST = 400;

    /** HTTP 401 Unauthorized – authentication is required or has failed. */
    public static final int STATUS_UNAUTHORIZED = 401;

    /** HTTP 403 Forbidden – the caller is not authorised to access this resource. */
    public static final int STATUS_FORBIDDEN = 403;

    /** HTTP 404 Not Found – the requested resource does not exist. */
    public static final int STATUS_NOT_FOUND = 404;

    /** HTTP 500 Internal Server Error – an unexpected server-side error occurred. */
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    // -----------------------------------------------------------------------
    // Timeouts (milliseconds)
    // -----------------------------------------------------------------------

    /** Default request timeout: 10 000 ms (10 seconds). */
    public static final int DEFAULT_TIMEOUT = 10000;

    /** Short request timeout: 5 000 ms (5 seconds). */
    public static final int SHORT_TIMEOUT = 5000;

    /** Long request timeout: 30 000 ms (30 seconds). */
    public static final int LONG_TIMEOUT = 30000;

    // -----------------------------------------------------------------------
    // Test Data
    // -----------------------------------------------------------------------

    /** Total number of posts seeded in the JSONPlaceholder dataset: {@code 100}. */
    public static final int TOTAL_POSTS = 100;

    /** Total number of users seeded in the JSONPlaceholder dataset: {@code 10}. */
    public static final int TOTAL_USERS = 10;

    /** Maximum valid post ID in the JSONPlaceholder dataset: {@code 100}. */
    public static final int MAX_POST_ID = 100;
}
