package com.automation.models;

/**
 * POJO representing a Post resource from JSONPlaceholder API.
 *
 * @author api-automation-framework
 * @version 1.0.0
 */
public class Post {

    private int userId;
    private int id;
    private String title;
    private String body;

    /**
     * Default no-arg constructor required for JSON deserialization.
     */
    public Post() {
    }

    /**
     * Constructor for creating a new post (without id).
     *
     * @param userId the ID of the user who owns the post
     * @param title  the title of the post
     * @param body   the body content of the post
     */
    public Post(int userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
    }

    /**
     * Full constructor including id.
     *
     * @param userId the ID of the user who owns the post
     * @param id     the unique identifier of the post
     * @param title  the title of the post
     * @param body   the body content of the post
     */
    public Post(int userId, int id, String title, String body) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.body = body;
    }

    /**
     * Returns the user ID.
     *
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Returns the post ID.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the post ID.
     *
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the post title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the post title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the post body.
     *
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the post body.
     *
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Post{userId=" + userId + ", id=" + id
                + ", title='" + title + "', body='" + body + "'}";
    }
}
