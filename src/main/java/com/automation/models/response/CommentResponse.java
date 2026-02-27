package com.automation.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response model representing a Comment resource returned by the JSONPlaceholder API.
 *
 * <p>Comments are associated with posts. Unmapped JSON fields are silently ignored so
 * that the model remains forward-compatible.
 *
 * <p>Example usage:
 * <pre>{@code
 * Map<String, Object> params = Map.of(ApiConstants.POST_ID_PARAM, 1);
 * Response response = client.get(ApiConstants.COMMENTS_ENDPOINT, params);
 * List<CommentResponse> comments =
 *         Arrays.asList(response.as(CommentResponse[].class));
 * }</pre>
 *
 * @see com.automation.constants.ApiConstants#COMMENTS_ENDPOINT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentResponse {

    /**
     * The identifier of the post this comment belongs to.
     */
    private Integer postId;

    /**
     * The server-assigned unique identifier for this comment.
     */
    private Integer id;

    /**
     * The display name / subject line of the commenter.
     */
    private String name;

    /**
     * The e-mail address of the commenter.
     */
    private String email;

    /**
     * The text body of the comment.
     */
    private String body;
}
