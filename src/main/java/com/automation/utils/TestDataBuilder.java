package com.automation.utils;

import com.automation.models.request.PostRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Factory class for creating and loading test data objects used in API tests.
 *
 * <p>Provides static factory methods for constructing {@link PostRequest} instances
 * (both from explicit values and randomised) as well as helpers for loading
 * parameterised test data from JSON files on the classpath and converting it into
 * TestNG data-provider arrays.</p>
 *
 * <p>This class is not instantiable; all members are {@code public static}.</p>
 *
 * <p>Usage examples:</p>
 * <pre>{@code
 * // Build a specific post request
 * PostRequest req = TestDataBuilder.buildPostRequest("Title", "Body", 1);
 *
 * // Build a randomised post request
 * PostRequest random = TestDataBuilder.buildRandomPostRequest();
 *
 * // Load data from a JSON file and convert to TestNG data provider format
 * List<Map<String, Object>> data =
 *         TestDataBuilder.loadTestDataFromJson("testdata/test-data.json");
 * Object[][] providerData = TestDataBuilder.toTestNGDataProvider(data);
 * }</pre>
 *
 * @author api-automation-framework
 * @version 1.0.0
 * @see PostRequest
 */
public class TestDataBuilder {

    private static final Logger logger = LogManager.getLogger(TestDataBuilder.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random random = new Random();

    /**
     * Private constructor – prevents instantiation of this utility class.
     */
    private TestDataBuilder() {}

    /**
     * Creates a {@link PostRequest} with the supplied field values.
     *
     * @param title  the post title (must not be {@code null})
     * @param body   the post body content (must not be {@code null})
     * @param userId the ID of the owning user (must be a positive integer)
     * @return a new {@link PostRequest} instance with the specified values
     */
    public static PostRequest buildPostRequest(String title, String body, int userId) {
        return PostRequest.builder()
                .title(title)
                .body(body)
                .userId(userId)
                .build();
    }

    /**
     * Creates a {@link PostRequest} with a randomised user ID and timestamp-based title
     * and body.
     *
     * <p>The user ID is randomly selected from the range {@code [1, 10]}, which
     * matches the JSONPlaceholder dataset. Both the title and body embed the current
     * {@link System#currentTimeMillis()} to ensure uniqueness across test runs.</p>
     *
     * @return a new {@link PostRequest} with randomised values
     */
    public static PostRequest buildRandomPostRequest() {
        int userId = random.nextInt(10) + 1;
        return PostRequest.builder()
                .title("Test Post " + System.currentTimeMillis())
                .body("This is a test post body created at " + System.currentTimeMillis())
                .userId(userId)
                .build();
    }

    /**
     * Creates a {@link PostRequest} from explicitly supplied test-data values.
     *
     * <p>This is a convenience alias for {@link #buildPostRequest(String, String, int)}
     * intended to make data-driven test code more readable.</p>
     *
     * @param title  the post title (must not be {@code null})
     * @param body   the post body content (must not be {@code null})
     * @param userId the ID of the owning user
     * @return a new {@link PostRequest} with the specified values
     */
    public static PostRequest buildPostFromTestData(String title, String body, int userId) {
        return PostRequest.builder()
                .title(title)
                .body(body)
                .userId(userId)
                .build();
    }

    /**
     * Loads a list of test-data records from a JSON file on the classpath.
     *
     * <p>The JSON file must contain a top-level array of objects. Each object is
     * returned as a {@code Map<String, Object>} entry in the result list. If the
     * file is not found or cannot be parsed, an empty list is returned and the
     * error is logged.</p>
     *
     * @param resourcePath the classpath-relative path to the JSON file
     *                     (e.g. {@code "testdata/test-data.json"})
     * @return an immutable list of maps representing the test-data records;
     *         never {@code null}, but may be empty
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
     * Converts a list of test-data maps into a TestNG
     * {@code @DataProvider}-compatible two-dimensional array.
     *
     * <p>Each element of the returned array contains a single-element inner array
     * holding one map from the input list, so TestNG will invoke the test method
     * once per record, passing the map as the sole parameter.</p>
     *
     * @param data the list of test-data records (must not be {@code null})
     * @return a {@code Object[][]} suitable for use with {@code @DataProvider}
     */
    public static Object[][] toTestNGDataProvider(List<Map<String, Object>> data) {
        Object[][] result = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            result[i][0] = data.get(i);
        }
        return result;
    }
}
