package com.automation.constants;

/**
 * Constants used across the API automation framework.
 *
 * @author api-automation-framework
 * @version 1.0.0
 */
public final class ApiConstants {

    /** Base URL for JSONPlaceholder API. */
    public static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    /** Endpoint path for posts resource. */
    public static final String POSTS_ENDPOINT = "/posts";

    /** Endpoint path template for a single post by ID. */
    public static final String POST_BY_ID_ENDPOINT = "/posts/{id}";

    /** HTTP status code 200 OK. */
    public static final int STATUS_OK = 200;

    /** HTTP status code 201 Created. */
    public static final int STATUS_CREATED = 201;

    /** HTTP status code 204 No Content. */
    public static final int STATUS_NO_CONTENT = 204;

    /** HTTP status code 404 Not Found. */
    public static final int STATUS_NOT_FOUND = 404;

    /** Default connection timeout in milliseconds. */
    public static final int CONNECTION_TIMEOUT = 5000;

    /** Default read timeout in milliseconds. */
    public static final int READ_TIMEOUT = 10000;

    private ApiConstants() {
        // Utility class – prevent instantiation
    }
}
