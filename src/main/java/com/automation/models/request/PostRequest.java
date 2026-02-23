package com.automation.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request POJO representing the body of a <em>create/update post</em> REST API call.
 *
 * <p>This model maps to the JSON payload expected by the JSONPlaceholder
 * {@code POST /posts} and {@code PUT /posts/{id}} endpoints. Fields that are
 * {@code null} are excluded from the serialised JSON (via
 * {@link JsonInclude#NON_NULL}) so that partial-update scenarios are supported
 * without sending explicit {@code null} values.</p>
 *
 * <p>Instances are normally created via the Lombok-generated builder:</p>
 * <pre>{@code
 * PostRequest request = PostRequest.builder()
 *         .title("My Title")
 *         .body("Post body content")
 *         .userId(1)
 *         .build();
 * }</pre>
 *
 * @author api-automation-framework
 * @version 1.0.0
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
     * The title of the post.
     *
     * <p>Must not be blank for a valid create/update request.</p>
     */
    private String title;

    /**
     * The body (main content) of the post.
     *
     * <p>Must not be blank for a valid create/update request.</p>
     */
    private String body;

    /**
     * The ID of the user who owns this post.
     *
     * <p>Must reference a valid user ID in the target system (1–10 for
     * JSONPlaceholder).</p>
     */
    private Integer userId;
}
