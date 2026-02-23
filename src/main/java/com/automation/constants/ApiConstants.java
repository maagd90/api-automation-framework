package com.automation.constants;

public final class ApiConstants {

    private ApiConstants() {}

    // Base URLs
    public static final String JSONPLACEHOLDER_BASE_URL = "https://jsonplaceholder.typicode.com";

    // Endpoints
    public static final String POSTS_ENDPOINT = "/posts";
    public static final String USERS_ENDPOINT = "/users";
    public static final String COMMENTS_ENDPOINT = "/comments";
    public static final String ALBUMS_ENDPOINT = "/albums";
    public static final String PHOTOS_ENDPOINT = "/photos";
    public static final String TODOS_ENDPOINT = "/todos";

    // HTTP Headers
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";
    public static final String AUTHORIZATION = "Authorization";
    public static final String APPLICATION_JSON = "application/json";

    // Query Parameters
    public static final String USER_ID_PARAM = "userId";
    public static final String POST_ID_PARAM = "postId";

    // HTTP Status Codes
    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_NO_CONTENT = 204;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    // Timeouts
    public static final int DEFAULT_TIMEOUT = 10000;
    public static final int SHORT_TIMEOUT = 5000;
    public static final int LONG_TIMEOUT = 30000;

    // Test Data
    public static final int TOTAL_POSTS = 100;
    public static final int TOTAL_USERS = 10;
    public static final int MAX_POST_ID = 100;
}
