package com.automation.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for creating or updating a Post via the JSONPlaceholder API.
 *
 * <p>Fields with {@code null} values are excluded from the serialised JSON payload
 * (courtesy of {@link JsonInclude#NON_NULL}), which makes the same class usable for
 * both full POST/PUT requests and partial PATCH requests.
 *
 * <p>Example usage:
 * <pre>{@code
 * PostRequest post = PostRequest.builder()
 *         .userId(1)
 *         .title("My Title")
 *         .body("Post body content.")
 *         .build();
 *
 * Response response = client.post(ApiConstants.POSTS_ENDPOINT, post);
 * }</pre>
 *
 * @see com.automation.models.response.PostResponse
 * @see com.automation.utils.TestDataBuilder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRequest {

    /**
     * The identifier of the user who authors this post.
     * Must be a positive integer referencing a valid user in the system.
     */
    private Integer userId;

    /**
     * The headline / title of the post.
     * Should be a concise, non-empty string.
     */
    private String title;

    /**
     * The main text content of the post.
     */
    private String body;
}
