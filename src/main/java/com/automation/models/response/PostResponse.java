package com.automation.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response model representing a Post resource returned by the JSONPlaceholder API.
 *
 * <p>Unmapped JSON fields are silently ignored so that the model remains forward-compatible
 * if the API adds new properties in the future.
 *
 * <p>Example usage:
 * <pre>{@code
 * Response response = client.getById(ApiConstants.POSTS_ENDPOINT, 1);
 * PostResponse post = response.as(PostResponse.class);
 * System.out.println(post.getTitle());
 * }</pre>
 *
 * @see com.automation.models.request.PostRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostResponse {

    /**
     * The server-assigned unique identifier for this post.
     * Populated by the server on creation; present in GET/PUT/PATCH responses.
     */
    private Integer id;

    /**
     * The identifier of the user who authored this post.
     */
    private Integer userId;

    /**
     * The headline / title of the post.
     */
    private String title;

    /**
     * The main text content of the post.
     */
    private String body;
}
