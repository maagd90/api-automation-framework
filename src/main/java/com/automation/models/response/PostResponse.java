package com.automation.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response POJO representing a single post as returned by the JSONPlaceholder REST API.
 *
 * <p>Instances of this class are deserialised from the JSON body of responses to
 * {@code GET /posts}, {@code GET /posts/{id}}, {@code POST /posts}, and
 * {@code PUT /posts/{id}}. Unknown JSON fields are silently ignored via
 * {@link JsonIgnoreProperties#ignoreUnknown()}.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * PostResponse post = response.as(PostResponse.class);
 * Assert.assertNotNull(post.getId());
 * Assert.assertEquals(post.getTitle(), "Expected Title");
 * }</pre>
 *
 * @author api-automation-framework
 * @version 1.0.0
 * @see PostRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostResponse {

    /** Server-assigned unique identifier of the post. */
    private Integer id;

    /** Title of the post. */
    private String title;

    /** Body (main content) of the post. */
    private String body;

    /** ID of the user who owns this post. */
    private Integer userId;
}
