package com.automation.utils;

import com.automation.models.request.PostRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Factory class for constructing test data objects used in API tests.
 *
 * <p>Provides static factory methods for creating request models and raw data maps,
 * as well as helpers for loading data from JSON files and converting it to TestNG
 * data-provider arrays.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Build a typed request model
 * PostRequest post = TestDataBuilder.buildPostRequest(1, "My Title", "Post body.");
 *
 * // Build a raw user data map
 * Map<String, Object> user = TestDataBuilder.buildUserData(1, "Alice", "alice@example.com");
 *
 * // Load data-driven test data from a JSON file
 * List<Map<String, Object>> data =
 *         TestDataBuilder.loadTestDataFromJson("testdata/test-data.json");
 * Object[][] testNGData = TestDataBuilder.toTestNGDataProvider(data);
 * }</pre>
 *
 * @see PostRequest
 */
public class TestDataBuilder {

    private static final Logger logger = LogManager.getLogger(TestDataBuilder.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random random = new Random();

    /** Utility class – do not instantiate. */
    private TestDataBuilder() {}

    // -------------------------------------------------------------------------
    // PostRequest builders
    // -------------------------------------------------------------------------

    /**
     * Builds a {@link PostRequest} with the given fields.
     *
     * @param userId the identifier of the authoring user (must be positive)
     * @param title  the post headline; must not be null or blank
     * @param body   the post content; must not be null
     * @return a new {@link PostRequest} instance
     */
    public static PostRequest buildPostRequest(int userId, String title, String body) {
        return PostRequest.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .build();
    }

    /**
     * Builds a {@link PostRequest} with the given fields.
     * Overload that accepts a {@code String} for {@code userId} convenience.
     *
     * @param title  the post headline
     * @param body   the post content
     * @param userId the identifier of the authoring user
     * @return a new {@link PostRequest} instance
     */
    public static PostRequest buildPostRequest(String title, String body, int userId) {
        return buildPostRequest(userId, title, body);
    }

    /**
     * Builds a {@link PostRequest} with randomly generated content.
     * Useful for "smoke" tests where the exact content is unimportant.
     *
     * @return a new {@link PostRequest} with generated title, body and a random userId (1-10)
     */
    public static PostRequest buildRandomPostRequest() {
        int userId = random.nextInt(10) + 1;
        return PostRequest.builder()
                .title("Test Post " + System.currentTimeMillis())
                .body("This is a test post body created at " + System.currentTimeMillis())
                .userId(userId)
                .build();
    }

    // -------------------------------------------------------------------------
    // User data builder
    // -------------------------------------------------------------------------

    /**
     * Builds a raw user-data map suitable for use as a request body or test expectation.
     *
     * @param id    the user identifier
     * @param name  the user's full name
     * @param email the user's e-mail address
     * @return a mutable map containing {@code id}, {@code name} and {@code email} entries
     */
    public static Map<String, Object> buildUserData(int id, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("name", name);
        user.put("email", email);
        return user;
    }

    // -------------------------------------------------------------------------
    // JSON file helpers
    // -------------------------------------------------------------------------

    /**
     * Loads test data from a JSON file on the test classpath.
     * The file must contain a JSON array of objects.
     *
     * @param resourcePath the classpath-relative path to the JSON file
     *                     (e.g. {@code "testdata/test-data.json"})
     * @return an unmodifiable list of maps representing the JSON objects,
     *         or an empty list if the file is not found or cannot be parsed
     */
    public static List<Map<String, Object>> loadTestDataFromJson(String resourcePath) {
        try (InputStream is = TestDataBuilder.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                logger.warn("Test data file not found: {}", resourcePath);
                return List.of();
            }
            return objectMapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException e) {
            logger.error("Error loading test data from {}: {}", resourcePath, e.getMessage());
            return List.of();
        }
    }

    /**
     * Converts a list of data maps into the two-dimensional array format expected by a
     * TestNG {@code @DataProvider}.
     *
     * @param data the list of data maps (each map becomes one test invocation)
     * @return a {@code Object[][]} where each row contains a single {@link Map} element
     */
    public static Object[][] toTestNGDataProvider(List<Map<String, Object>> data) {
        Object[][] result = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            result[i][0] = data.get(i);
        }
        return result;
    }
}
